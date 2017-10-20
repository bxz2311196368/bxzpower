package com.bxzmod.energyconversion.tileentity;

import com.bxzmod.energyconversion.blocks.PowerBlock;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyTransport;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
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
	//private boolean refresh = true;
	protected int totalEnergy = 0;
	private byte[] bSideType = { 0, 0, 1, 1, 1, 1 };// 1 = send ,0 = receive;
	public static final int capacity = Integer.MAX_VALUE;
	public static final Capability<IEnergyStorage> ENERGY_HANDLER = null;

	protected EnergyStorage storage = new EnergyStorage(capacity);

	public PowerBlockTileEntity()
	{

	}

	public void setByte(byte[] b)
	{
		for (int i = 0; i < 6; i++)
			this.bSideType[i] = b[i];
	}

	public byte[] getByte()
	{
		return bSideType;
	}

	public void setSideType(EnumFacing form)
	{
		this.bSideType[form.getIndex()] = (byte) (this.bSideType[form.getIndex()] == 1 ? 0 : 1);
		IExtendedBlockState extendedBlockState = (IExtendedBlockState) this.worldObj.getBlockState(this.pos);
		extendedBlockState = extendedBlockState.withProperty(PowerBlock.SIDE_CONFIG, this.bSideType);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from)
	{

		if (CapabilityEnergy.ENERGY == capability)
		{
			return true;
		}
		if (Loader.isModLoaded("tesla"))
		{
			if (capability == TeslaCapabilities.CAPABILITY_PRODUCER)
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
			if (capability == TeslaCapabilities.CAPABILITY_PRODUCER)
			{
				return (T) this;
			}
		}
		return super.getCapability(capability, from);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
	{
		return oldState != newState;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{

		super.readFromNBT(nbt);
		this.totalEnergy = nbt.getInteger("totalEnergy");
		this.bSideType = nbt.getByteArray("sideType").clone();
		if (totalEnergy > capacity)
		{
			totalEnergy = capacity;
		}
		storage.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{

		super.writeToNBT(nbt);
		nbt = storage.writeToNBT(nbt);
		if (totalEnergy < 0)
		{
			totalEnergy = 0;
		}
		nbt.setInteger("totalEnergy", totalEnergy);
		nbt.setByteArray("sideType", this.bSideType);
		return nbt;
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
		if (bSideType[from.getIndex()] == (byte) 1)
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
			if (bSideType[i] == (byte) 0)
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
