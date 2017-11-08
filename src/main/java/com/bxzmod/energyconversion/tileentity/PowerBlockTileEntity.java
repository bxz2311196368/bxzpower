package com.bxzmod.energyconversion.tileentity;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bxzmod.energyconversion.ItemStackHandlerMe;
import com.bxzmod.energyconversion.StackHelper;
import com.bxzmod.energyconversion.blocks.PowerBlock;
import com.bxzmod.energyconversion.capability.CapabilityLoader;
import com.bxzmod.energyconversion.network.NetworkLoader;
import com.bxzmod.energyconversion.network.TileEntitySync;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyTransport;
import crazypants.enderio.power.IInternalPoweredItem;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import mekanism.api.energy.EnergizedItemManager;
import mekanism.api.energy.IEnergizedItem;
import mekanism.api.energy.IStrictEnergyAcceptor;
import mekanism.api.energy.IStrictEnergyOutputter;
import mekanism.api.energy.IStrictEnergyStorage;
import mekanism.common.capabilities.Capabilities;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

@Optional.InterfaceList(value = { @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "IC2"),
		@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = "IC2"),
		@Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaConsumer", modid = "tesla"),
		@Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaHolder", modid = "tesla"),
		@Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaProducer", modid = "tesla"),
		@Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyAcceptor", modid = "Mekanism"),
		@Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyOutputter", modid = "Mekanism"),
		@Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyStorage", modid = "Mekanism") })
public class PowerBlockTileEntity extends TileEntity
		implements ITickable, IEnergyReceiver, IEnergyProvider, IStrictEnergyStorage, IStrictEnergyOutputter,
		IStrictEnergyAcceptor, ITeslaConsumer, ITeslaProducer, ITeslaHolder, IEnergySink, IEnergySource
{
	private int type = 1;
	protected int totalEnergy = 0;
	public static final int capacity = Integer.MAX_VALUE;
	double temp = .0d;
	public static Capability<IEnergyStorage> ENERGY_HANDLER = null;

	public static Capability<IStrictEnergyStorage> ENERGY_STORAGE_CAPABILITY = null;
	public static Capability<IStrictEnergyAcceptor> ENERGY_ACCEPTOR_CAPABILITY = null;
	public static Capability<IStrictEnergyOutputter> ENERGY_OUTPUTTER_CAPABILITY = null;

	// public static final Capability<IElectricItem> IC2_ElECTRIC_ITEM = null;

	boolean[] sideType = new boolean[6];

	boolean addedToEnet = false;
	boolean ic2Load = false;
	boolean teslaLoad = false;
	boolean mekanismLoad = false;

	private static final Logger LOGGER = LogManager.getLogger();

	protected EnergyStorage storage = new EnergyStorage(capacity);

	MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

	ItemStackHandlerMe inventory = new ItemStackHandlerMe(9);

	public PowerBlockTileEntity()
	{
		ic2Load = Loader.isModLoaded("IC2");
		if (Loader.isModLoaded("Mekanism"))
		{
			mekanismLoad = Loader.isModLoaded("Mekanism");
			ENERGY_ACCEPTOR_CAPABILITY = Capabilities.ENERGY_ACCEPTOR_CAPABILITY;
			ENERGY_OUTPUTTER_CAPABILITY = Capabilities.ENERGY_OUTPUTTER_CAPABILITY;
			ENERGY_STORAGE_CAPABILITY = Capabilities.ENERGY_STORAGE_CAPABILITY;
		}
		teslaLoad = Loader.isModLoaded("tesla");
		ENERGY_HANDLER = CapabilityEnergy.ENERGY;
	}

	public int getTotalEnergy()
	{
		return totalEnergy;
	}

	public void setTotalEnergy(int totalEnergy)
	{
		this.totalEnergy = totalEnergy;
	}

	public double getTemp()
	{
		return temp;
	}

	public void setTemp(double temp)
	{
		this.temp = temp;
	}

	public boolean isSend(EnumFacing form)
	{
		return this.sideType[form.getIndex()];
	}

	public void setSideType(EnumFacing form)
	{
		this.sideType[form.getIndex()] = !this.sideType[form.getIndex()];
		this.worldObj.markBlockRangeForRenderUpdate(pos, pos);
		this.markDirty();
		if (FMLCommonHandler.instance().getEffectiveSide().isServer() && ic2Load)
			this.refreshEuNet();
	}

	// SYNC
	private void sendUpdates()
	{
		if (server.getTickCounter() % 20 == 0)
		{
			TileEntitySync message = new TileEntitySync();
			int dim = worldObj.provider.getDimension();
			message.nbt = this.writeToNBT(new NBTTagCompound()).copy();
			message.nbt.setInteger("world", dim);
			NetworkLoader.instance.sendToAllAround(message,
					new NetworkRegistry.TargetPoint(dim, pos.getX(), pos.getY(), pos.getZ(), 32));
		}
		markDirty();
	}

	public void chargeItem()
	{
		for (int i = 0; i < 9; i++)
		{
			ItemStack stack = inventory.getStackInSlot(i);
			double lose = 0.0;
			if (stack != null)
			{
				if (ic2Load)
					lose += -ElectricItem.manager.charge(stack, totalEnergy / 4.0, 14, true, false) * 4.0;
				lose += -EnergizedItemManager.charge(stack, totalEnergy * 2.5) / 2.5;
				if (stack.getItem() instanceof IEnergyContainerItem)
					lose += -((IEnergyContainerItem) stack.getItem()).receiveEnergy(stack, totalEnergy, false);
				if (teslaLoad)
					if (stack.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null))
						lose += -stack.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null).givePower(totalEnergy,
								false);
				if (stack.hasCapability(CapabilityEnergy.ENERGY, null))
					lose += -stack.getCapability(CapabilityEnergy.ENERGY, null).receiveEnergy(totalEnergy, false);
				totalEnergy += (new Double(lose).intValue());
				storage.modifyEnergyStored((new Double(lose).intValue()));
			}
		}
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		return this.writeToNBT(new NBTTagCompound());
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag)
	{
		this.readFromNBT(tag);
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		return new SPacketUpdateTileEntity(this.pos, this.getBlockMetadata(), this.writeToNBT(new NBTTagCompound()));
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isServer())
			net.sendPacket(pkt);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			BlockPos pos = pkt.getPos();
			if (pos == this.pos)
				this.readFromNBT(pkt.getNbtCompound());
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from)
	{
		if (teslaLoad)
		{
			if (capability == TeslaCapabilities.CAPABILITY_CONSUMER
					|| capability == TeslaCapabilities.CAPABILITY_PRODUCER
					|| capability == TeslaCapabilities.CAPABILITY_HOLDER)
				return true;
		}
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability))
		{
			return true;
		}
		if (mekanismLoad)
		{
			if (capability == ENERGY_STORAGE_CAPABILITY || capability == ENERGY_OUTPUTTER_CAPABILITY
					|| capability == ENERGY_ACCEPTOR_CAPABILITY)
			{
				return true;
			}
		}
		if (ENERGY_HANDLER.equals(capability))
		{
			return true;
		}
		return super.hasCapability(capability, from);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from)
	{
		if (Loader.isModLoaded("tesla"))
		{
			if (capability == TeslaCapabilities.CAPABILITY_CONSUMER
					|| capability == TeslaCapabilities.CAPABILITY_PRODUCER
					|| capability == TeslaCapabilities.CAPABILITY_HOLDER)
			{
				return (T) this;
			}
		}
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability))
		{
			@SuppressWarnings("unchecked")
			T result = (T) inventory;
			return result;
		}
		if (mekanismLoad)
		{
			if (capability == ENERGY_STORAGE_CAPABILITY || capability == ENERGY_OUTPUTTER_CAPABILITY
					|| capability == ENERGY_ACCEPTOR_CAPABILITY)
			{
				return (T) this;
			}
		}
		if (ENERGY_HANDLER.equals(capability))
		{
			if (from != null)
				return (T) new ForgeEnergy(!sideType[from.getIndex()], sideType[from.getIndex()]);
			else
				return (T) new ForgeEnergy(true, false);
		}
		return super.getCapability(capability, from);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
	{
		return oldState.getBlock() != newState.getBlock();
	}

	public void setDataByNBT(NBTTagCompound nbt)
	{
		if (!nbt.hasKey("sideType"))
			return;
		this.inventory.deserializeNBT(nbt.getCompoundTag("inventory"));
		NBTTagList list = new NBTTagList();
		this.totalEnergy = nbt.getInteger("totalEnergy");
		this.temp = nbt.getDouble("temp");
		this.type = nbt.getInteger("eu_out");
		list = (NBTTagList) nbt.getTag("sideType");
		for (int i = 0; i < 6; i++)
			this.sideType[i] = ((NBTTagCompound) list.get(i)).getBoolean("side" + i);
		if (totalEnergy > capacity)
		{
			totalEnergy = capacity;
		}
		storage.readFromNBT(nbt);
	}

	public NBTTagCompound getNBTFromData(NBTTagCompound nbt)
	{
		nbt.setTag("inventory", this.inventory.serializeNBT());
		nbt = storage.writeToNBT(nbt);
		if (totalEnergy < 0)
		{
			totalEnergy = 0;
		}
		nbt.setInteger("totalEnergy", totalEnergy);
		nbt.setInteger("eu_out", type);
		nbt.setDouble("temp", temp);
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < 6; i++)
		{
			NBTTagCompound a = new NBTTagCompound();
			a.setBoolean("side" + i, this.sideType[i]);
			list.appendTag(a);
		}
		nbt.setTag("sideType", list);
		return nbt;
	}

	@Override
	public void onChunkUnload()
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isServer() && ic2Load)
			this.euNetUnload();
	}

	@Override
	public void onLoad()
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isServer() && ic2Load)
			this.euNetLoad();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		/*
		 * if (needUpdate) { TileEntitySync message = new TileEntitySync();
		 * message.nbt = nbt.copy(); message.nbt.setInteger("world",
		 * Minecraft.getMinecraft().theWorld.provider.getDimension());
		 * message.nbt.setString("player",
		 * Minecraft.getMinecraft().thePlayer.getName());
		 * NetworkLoader.instance.sendToServer(message); }
		 */
		this.setDataByNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		return this.getNBTFromData(nbt);
	}

	// COFH START
	/* IEnergyConnection */
	@Override
	public boolean canConnectEnergy(EnumFacing from)
	{
		return true;
	}

	/* IEnergyReceiver */
	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate)
	{
		if (sideType[from.getIndex()])
			return 0;
		int get = storage.receiveEnergy(maxReceive, simulate);
		if (!simulate)
			this.totalEnergy = storage.getEnergyStored();
		return get;
	}

	/* IEnergyProvider */
	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate)
	{

		int lose = storage.extractEnergy(maxExtract, simulate);
		if (!simulate)
			this.totalEnergy = storage.getEnergyStored();
		return lose;
	}

	/* IEnergyHandler */
	@Override
	public int getEnergyStored(EnumFacing from)
	{

		return storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from)
	{

		return storage.getMaxEnergyStored();
	}

	// COFH END
	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	// ENERGY SEND
	@Override
	public void update()
	{
		if (!worldObj.isRemote)
		{
			if (!this.addedToEnet && this.ic2Load)
				this.euNetLoad();
			this.transferEnergy();
			chargeItem();
			sendUpdates();
		}
	}

	protected void transferEnergy()
	{
		for (int i = 0; i < 6 && storage.getEnergyStored() > 0; i++)
		{
			if (!sideType[i])
				continue;
			double lose = -this.insertEnergyIntoAdjacentEnergyReceiver(this, EnumFacing.VALUES[i],
					Math.min(storage.getMaxExtract(), storage.getEnergyStored()), false);
			double t = lose + temp + totalEnergy;
			temp = t - Math.floor(t);
			double t1 = t - temp - totalEnergy;
			this.totalEnergy += (new Double(t1)).intValue();
			storage.modifyEnergyStored((new Double(t1)).intValue());
		}
	}

	public static double insertEnergyIntoAdjacentEnergyReceiver(TileEntity tile, EnumFacing side, int energy,
			boolean simulate)
	{

		TileEntity handler = getAdjacentTileEntity(tile.getWorld(), tile.getPos(), side);

		if (handler instanceof IEnergyReceiver)
		{
			return ((IEnergyReceiver) handler).receiveEnergy(side.getOpposite(), energy, simulate);
		} else if (handler != null && handler instanceof IStrictEnergyAcceptor)
		{
			return ((IStrictEnergyAcceptor) handler).acceptEnergy(side.getOpposite(), energy, simulate) / 2.5;
		} else if (Loader.isModLoaded("tesla") && handler != null && handler instanceof ITeslaConsumer)
		{
			return ((ITeslaConsumer) handler).givePower(energy, simulate);
		} else if (Loader.isModLoaded("Mekanism") && handler != null
				&& handler.hasCapability(ENERGY_ACCEPTOR_CAPABILITY, side.getOpposite()))
		{
			return handler.getCapability(ENERGY_ACCEPTOR_CAPABILITY, side.getOpposite())
					.acceptEnergy(side.getOpposite(), energy, simulate) / 2.5;
		} else if (handler != null && handler.hasCapability(ENERGY_HANDLER, side.getOpposite()))
		{
			return handler.getCapability(ENERGY_HANDLER, side.getOpposite()).receiveEnergy(energy, simulate);
		}
		return 0;
	}

	public static TileEntity getAdjacentTileEntity(World world, BlockPos pos, EnumFacing dir)
	{

		pos = pos.offset(dir);
		return world == null || !world.isBlockLoaded(pos) ? null : world.getTileEntity(pos);
	}

	// TESLA START
	// no side to control
	@Override
	public long givePower(long Tesla, boolean simulated)
	{

		final long acceptedTesla = Math.min(this.capacity - this.totalEnergy, Math.min(this.capacity, Tesla));

		if (!simulated)
		{
			this.totalEnergy += (new Long(acceptedTesla)).intValue();
			storage.modifyEnergyStored((new Long(acceptedTesla)).intValue());
		}

		return acceptedTesla;
	}

	@Override
	public long takePower(long Tesla, boolean simulated)
	{

		final long removedPower = Math.min(this.totalEnergy, Math.min(this.capacity, Tesla));

		if (!simulated)
		{
			this.totalEnergy -= (new Long(removedPower)).intValue();
			storage.modifyEnergyStored(-((new Long(removedPower)).intValue()));
		}

		return removedPower;
	}

	@Override
	public long getStoredPower()
	{

		return this.totalEnergy;
	}

	@Override
	public long getCapacity()
	{

		return this.capacity;
	}

	// TESLA END
	// MEk START
	@Override
	public double acceptEnergy(EnumFacing side, double amount, boolean simulate)
	{
		double energyReceived = Math.min(capacity * 2.5 - totalEnergy * 2.5, Math.min(capacity * 2.5, amount));
		if (sideType[side.getIndex()])
			return 0;
		if (!simulate)
		{
			double t = totalEnergy + temp + energyReceived / 2.5;
			if (t > this.capacity)
			{
				totalEnergy = this.capacity;
				storage.setEnergyStored(capacity);
				temp = 0;
				return t - totalEnergy - temp;
			} else
			{
				temp = t - Math.floor(t);
				double t1 = t - temp - totalEnergy;
				totalEnergy += (new Double(t1)).intValue();
				storage.modifyEnergyStored(-((new Double(t1)).intValue()));
			}
		}
		return energyReceived;
	}

	@Override
	public boolean canReceiveEnergy(EnumFacing side)
	{
		if (sideType[side.getIndex()])
			return false;
		else
			return true;
	}

	@Override
	public double pullEnergy(EnumFacing side, double amount, boolean simulate)
	{
		double energyExtracted = Math.min(totalEnergy * 2.5, Math.min(capacity * 2.5, amount));

		if (!simulate)
		{
			double t = totalEnergy + temp - energyExtracted / 2.5;
			temp = t - Math.floor(t);
			double t1 = t - temp - totalEnergy;
			totalEnergy += (new Double(t1)).intValue();
			storage.modifyEnergyStored(-((new Double(t1)).intValue()));
		}
		return energyExtracted;
	}

	@Override
	public boolean canOutputEnergy(EnumFacing side)
	{
		if (sideType[side.getIndex()])
			return true;
		else
			return false;
	}

	@Override
	public double getEnergy()
	{
		return (this.temp + this.totalEnergy) * 2.5;
	}

	@Override
	public void setEnergy(double energy)
	{
		double t = energy / 2.5;
		this.temp = t - Math.floor(t);
		this.totalEnergy = (new Double(t)).intValue();
		storage.setEnergyStored(totalEnergy);
	}

	@Override
	public double getMaxEnergy()
	{
		return this.capacity * 2.5;
	}

	// MEK END
	// IC2 START
	@Optional.Method(modid = "IC2")
	public void euNetLoad()
	{
		MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
		this.addedToEnet = true;
	}

	@Optional.Method(modid = "IC2")
	public void euNetUnload()
	{
		MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
		this.addedToEnet = false;
	}

	@Optional.Method(modid = "IC2")
	private void refreshEuNet()
	{
		euNetUnload();
		euNetLoad();
	}

	@Override
	public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side)
	{
		if (sideType[side.getIndex()])
			return false;
		else
			return true;
	}

	@Override
	public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing side)
	{
		if (sideType[side.getIndex()])
			return true;
		else
			return false;
	}

	@Override
	public double getOfferedEnergy()
	{
		return Math.min(this.getEUOutputByTire(type), this.totalEnergy / 4);
	}

	@Override
	public void drawEnergy(double amount)
	{
		double t = totalEnergy + temp - amount * 4;
		temp = t - Math.floor(t);
		double t1 = t - temp - totalEnergy;
		totalEnergy += (new Double(t1).intValue());
		storage.modifyEnergyStored((new Double(t1).intValue()));
	}

	@Override
	public int getSourceTier()
	{
		return this.type;
	}

	@Override
	public double getDemandedEnergy()
	{
		return this.capacity - this.totalEnergy;

	}

	@Override
	public int getSinkTier()
	{
		return 14;
	}

	@Override
	public double injectEnergy(EnumFacing directionFrom, double amount, double voltage)
	{
		if (sideType[directionFrom.getOpposite().getIndex()])
			return amount;
		double energyReceived = Math.min(capacity / 4 - totalEnergy / 4, Math.min(capacity / 4, amount));
		double t = totalEnergy + temp + energyReceived * 4;
		double t1;
		if (t > this.capacity)
		{
			totalEnergy = this.capacity;
			storage.setEnergyStored(capacity);
			temp = 0;
			t1 = t - capacity;
			return amount - t1 / 4;
		} else
		{
			temp = t - Math.floor(t);
			t1 = t - temp - totalEnergy;
			;
			totalEnergy += (new Double(t1)).intValue();
			storage.modifyEnergyStored(((new Double(t1)).intValue()));
		}
		return 0;
	}

	public int getEUOutputByTire(int tire)
	{
		switch (tire)
		{
		case 1:
			return 32;
		case 2:
			return 128;
		case 3:
			return 512;
		case 4:
			return 2048;
		case 5:
			return 8192;
		default:
			return 32;
		}
	}

	// IC2 END
	public class ForgeEnergy implements IEnergyStorage
	{
		private boolean rec;
		private boolean send;

		public ForgeEnergy(boolean rec, boolean send)
		{
			super();
			this.rec = rec;
			this.send = send;
		}

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate)
		{
			if (!canReceive())
				return 0;

			int energyReceived = Math.min(capacity - totalEnergy, Math.min(capacity, maxReceive));
			if (!simulate)
				totalEnergy += energyReceived;
			return energyReceived;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate)
		{
			if (!canExtract())
				return 0;

			int energyExtracted = Math.min(totalEnergy, Math.min(totalEnergy, maxExtract));
			if (!simulate)
				totalEnergy -= energyExtracted;
			return energyExtracted;
		}

		@Override
		public int getEnergyStored()
		{

			return totalEnergy;
		}

		@Override
		public int getMaxEnergyStored()
		{

			return capacity;
		}

		@Override
		public boolean canExtract()
		{

			return send;
		}

		@Override
		public boolean canReceive()
		{

			return rec;
		}

	}
}
