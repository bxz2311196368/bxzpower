package com.bxzmod.energyconversion.blocks;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

import javax.vecmath.Matrix4f;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bxzmod.energyconversion.Info;
import com.bxzmod.energyconversion.ItemStackHandlerMe;
import com.bxzmod.energyconversion.Main;
import com.bxzmod.energyconversion.UnlistedByteArrayProperty;
import com.bxzmod.energyconversion.UnlistedPropertyBlockAvailable;
import com.bxzmod.energyconversion.compat.theoneprobe.TOPInfoProvider;
import com.bxzmod.energyconversion.compat.waila.WailaInfoProvider;
import com.bxzmod.energyconversion.creativetabs.CreativeTabsLoader;
import com.bxzmod.energyconversion.gui.GuiLoader;
import com.bxzmod.energyconversion.tileentity.PowerBlockTileEntity;
import com.google.common.collect.ImmutableList;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.PropertyFloat;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class PowerBlock extends BlockContainer implements TOPInfoProvider, WailaInfoProvider
{
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public static final UnlistedByteArrayProperty SIDE_CONFIG = new UnlistedByteArrayProperty("side_config");

	public static final UnlistedPropertyBlockAvailable NORTH = new UnlistedPropertyBlockAvailable("north");
	public static final UnlistedPropertyBlockAvailable SOUTH = new UnlistedPropertyBlockAvailable("south");
	public static final UnlistedPropertyBlockAvailable WEST = new UnlistedPropertyBlockAvailable("west");
	public static final UnlistedPropertyBlockAvailable EAST = new UnlistedPropertyBlockAvailable("east");
	public static final UnlistedPropertyBlockAvailable UP = new UnlistedPropertyBlockAvailable("up");
	public static final UnlistedPropertyBlockAvailable DOWN = new UnlistedPropertyBlockAvailable("down");

	private static final Logger LOGGER = LogManager.getLogger();

	public PowerBlock()
	{
		super(Material.GRASS);
		this.setUnlocalizedName("powerblock");
		this.setRegistryName("power_block");
		this.setHardness(2.0F);
		this.setHarvestLevel("pickaxe", 0);
		this.setCreativeTab(CreativeTabsLoader.tab);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		this.setLightLevel(1.0F);
	}

	public UnlistedPropertyBlockAvailable getProperty(EnumFacing from)
	{
		switch (from)
		{
		case UP:
			return this.UP;
		case DOWN:
			return this.DOWN;
		case NORTH:
			return this.NORTH;
		case SOUTH:
			return this.SOUTH;
		case WEST:
			return this.WEST;
		case EAST:
			return this.EAST;
		default:
			return null;
		}

	}

	// HWYLA START
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config)
	{
		PowerBlockTileEntity te = (PowerBlockTileEntity) accessor.getTileEntity();
		int e = te.getTotalEnergy();
		int rf = te.getEnergyStored(null);
		double temp = te.getTemp();
		double total = e + temp;
		DecimalFormat format = new DecimalFormat("0.00");
		currenttip.add(I18n.format("msg.total_energy") + ": " + format.format(total));
		currenttip.add("RF: " + format.format(rf + temp));
		currenttip.add("T: " + format.format(rf + temp));
		currenttip.add("EU: " + format.format((rf + temp) / 4.0));
		currenttip.add("J: " + format.format((rf + temp) * 2.5));
		if (Loader.isModLoaded("IC2"))
			currenttip.add(I18n.format("msg." + te.getType()));
		return currenttip;
	}

	// HWYLA END
	// TOP START
	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world,
			IBlockState blockState, IProbeHitData data)
	{
		PowerBlockTileEntity te;
		if (world.getTileEntity(data.getPos()) instanceof PowerBlockTileEntity)
			te = (PowerBlockTileEntity) world.getTileEntity(data.getPos());
		else
			return;
		int e = te.getTotalEnergy();
		int rf = te.getEnergyStored(null);
		double temp = te.getTemp();
		double total = e + temp;
		DecimalFormat format = new DecimalFormat("0.00");
		probeInfo.text(I18n.format("msg.total_energy") + ": " + format.format(total));
		probeInfo.text("RF: " + format.format(rf + temp));
		probeInfo.text("T: " + format.format(rf + temp));
		probeInfo.text("EU: " + format.format((rf + temp) / 4.0));
		probeInfo.text("J: " + format.format((rf + temp) * 2.5));
		if (Loader.isModLoaded("IC2"))
			probeInfo.text(I18n.format("msg." + te.getType()));
	}

	// TOP END
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		PowerBlockTileEntity te = (PowerBlockTileEntity) worldIn.getTileEntity(pos);
		te.onChunkUnload();
		/*
		 * not need ItemStackHandlerMe inv = (ItemStackHandlerMe)
		 * te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
		 * null); for (int i = inv.getSlots() - 1; i >= 0; i--) if
		 * (inv.getStackInSlot(i) != null) { Block.spawnAsEntity(worldIn, pos,
		 * inv.getStackInSlot(i)); ((IItemHandlerModifiable)
		 * inv).setStackInSlot(i, null); }
		 */
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack)
	{
		PowerBlockTileEntity te = (PowerBlockTileEntity) worldIn.getTileEntity(pos);
		if (stack.hasTagCompound())
		{
			NBTTagCompound nbt = stack.getTagCompound();
			if (nbt.hasKey("totalEnergy") && nbt.hasKey("sideType") && nbt.hasKey("Energy"))
				te.setDataByNBT(nbt);
		}
	}

	public Rotation getRotation(EnumFacing f)
	{
		switch (f)
		{
		case NORTH:
			return Rotation.NONE;
		case WEST:
			return Rotation.COUNTERCLOCKWISE_90;
		case SOUTH:
			return Rotation.CLOCKWISE_180;
		case EAST:
			return Rotation.CLOCKWISE_90;
		default:
			return Rotation.NONE;
		}
	}

	@Override
	public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player)
	{
		return true;
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te,
			ItemStack stack)
	{
		player.addStat(StatList.getBlockStats(this));
		player.addExhaustion(0.025F);
		ItemStack item = new ItemStack(Item.getItemFromBlock(this));
		item.setTagCompound(((PowerBlockTileEntity) te).getNBTFromData(new NBTTagCompound()));
		spawnAsEntity(worldIn, pos, item);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
			EntityPlayer player)
	{
		ItemStack item = new ItemStack(Item.getItemFromBlock(this));
		PowerBlockTileEntity te = (PowerBlockTileEntity) world.getTileEntity(pos);
		item.setTagCompound(te.getNBTFromData(new NBTTagCompound()));
		// LOGGER.info(item.writeToNBT(new NBTTagCompound()).toString());
		return item;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return 15;
	}

	@Override
	public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return 255;
	}

	@Override
	public boolean canDropFromExplosion(Explosion explosionIn)
	{
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new PowerBlockTileEntity();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		PowerBlockTileEntity te = (PowerBlockTileEntity) worldIn.getTileEntity(pos);
		if (playerIn.isSneaking())
		{
			te.setSideType(side);
		} else
		{
			if (!worldIn.isRemote)
			{
				int id = GuiLoader.GUI_E_C;
				playerIn.openGui(Main.instance, id, worldIn, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		return true;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		IProperty[] listedProperties = new IProperty[] { FACING };
		IUnlistedProperty[] unlistedProperties = new IUnlistedProperty[] { NORTH, SOUTH, WEST, EAST, UP, DOWN };
		return new ExtendedBlockState(this, listedProperties, unlistedProperties);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		int a;
		a = meta & 3;
		EnumFacing facing = EnumFacing.getHorizontal(a & 3);
		return this.getDefaultState().withProperty(FACING, facing);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		int facing = state.getValue(FACING).getHorizontalIndex();
		return facing;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer, ItemStack stack)
	{
		IBlockState origin = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, stack);
		return origin.withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		IExtendedBlockState s = (IExtendedBlockState) state;
		PowerBlockTileEntity te = (PowerBlockTileEntity) worldIn.getTileEntity(pos);
		boolean north = te.isSend(EnumFacing.NORTH);
		boolean south = te.isSend(EnumFacing.SOUTH);
		boolean west = te.isSend(EnumFacing.WEST);
		boolean east = te.isSend(EnumFacing.EAST);
		boolean up = te.isSend(EnumFacing.UP);
		boolean down = te.isSend(EnumFacing.DOWN);

		return s.withProperty(NORTH, north).withProperty(SOUTH, south).withProperty(WEST, west).withProperty(EAST, east)
				.withProperty(UP, up).withProperty(DOWN, down).withProperty(FACING, state.getValue(FACING));
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.MODEL;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
	{
		tooltip.add(I18n.format("tooltip.powerblock", TextFormatting.BLUE));
		tooltip.add(I18n.format("tooltip.powerblock1", TextFormatting.BLUE));
	}

}
