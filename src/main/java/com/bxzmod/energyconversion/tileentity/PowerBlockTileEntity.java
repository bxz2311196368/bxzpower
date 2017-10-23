package com.bxzmod.energyconversion.tileentity;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bxzmod.energyconversion.blocks.PowerBlock;
import com.bxzmod.energyconversion.capability.CapabilityLoader;
import com.bxzmod.energyconversion.network.NetworkLoader;
import com.bxzmod.energyconversion.network.TileEntitySync;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyTransport;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;

public class PowerBlockTileEntity extends TileEntity implements ITickable, IEnergyReceiver, IEnergyProvider
{
	private int type = 0;
	protected int totalEnergy = 0;
	public static final int capacity = Integer.MAX_VALUE;
	public static final Capability<IEnergyStorage> ENERGY_HANDLER = null;
	boolean needUpdate;
	
	boolean[] sideType = new boolean[6];
	
	private static final Logger LOGGER = LogManager.getLogger();

	protected EnergyStorage storage = new EnergyStorage(capacity);

	public PowerBlockTileEntity()
	{
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			needUpdate = true;
		else
			needUpdate = false;
	}
	
	public void markHasUpdated()
	{
		this.needUpdate = false;
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
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from)
	{

		if (CapabilityEnergy.ENERGY == capability)
		{
			return true;
		}
		if (CapabilityLoader.SIDE_CONFIG.equals(capability))
			return true;
		if (Loader.isModLoaded("tesla"))
		{
			if (capability == TeslaCapabilities.CAPABILITY_HOLDER)
				return true;
		}
		return super.hasCapability(capability, from);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from)
	{

		if (CapabilityEnergy.ENERGY == capability)
		{
			return (T) this;
		}
		if (Loader.isModLoaded("tesla"))
		{
			if (capability == TeslaCapabilities.CAPABILITY_HOLDER)
			{
				return (T) this;
			}
		}
		if (CapabilityLoader.SIDE_CONFIG.equals(capability))
        {
            return (T) this;
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
		if(!nbt.hasKey("sideType"))
			return;
		NBTTagList list = new NBTTagList();
		this.totalEnergy = nbt.getInteger("totalEnergy");
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
		nbt = storage.writeToNBT(nbt);
		if (totalEnergy < 0)
		{
			totalEnergy = 0;
		}
		nbt.setInteger("totalEnergy", totalEnergy);
		NBTTagList list = new NBTTagList();
		for(int i = 0; i <6; i++)
		{
			NBTTagCompound a = new NBTTagCompound();
			a.setBoolean("side" + i, this.sideType[i]);
			list.appendTag(a);
		}
		nbt.setTag("sideType", list);
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		if(needUpdate)
		{
			TileEntitySync message = new TileEntitySync();
			message.nbt = nbt.copy();
			message.nbt.setInteger("world", Minecraft.getMinecraft().theWorld.provider.getDimension());
			message.nbt.setString("player", Minecraft.getMinecraft().thePlayer.getName());
			NetworkLoader.instance.sendToServer(message);
		}
		this.setDataByNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		return this.getNBTFromData(nbt);
	}

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

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	@Override
	public void update()
	{
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
			return;
		}
		this.transferEnergy();

	}

	protected void transferEnergy()
	{
		for (int i = 0; i < 6 && storage.getEnergyStored() > 0; i++)
		{
			if (!sideType[i])
				continue;
			int lose = -this.insertEnergyIntoAdjacentEnergyReceiver(this, EnumFacing.VALUES[i],
					Math.min(storage.getMaxExtract(), storage.getEnergyStored()), false);
			storage.modifyEnergyStored(lose);
			this.totalEnergy += lose;
		}
	}

	public static int insertEnergyIntoAdjacentEnergyReceiver(TileEntity tile, EnumFacing side, int energy,
			boolean simulate)
	{

		TileEntity handler = getAdjacentTileEntity(tile.getWorld(), tile.getPos(), side);

		if (handler instanceof IEnergyReceiver)
		{
			return ((IEnergyReceiver) handler).receiveEnergy(side.getOpposite(), energy, simulate);
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
}
