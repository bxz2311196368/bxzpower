package com.bxzmod.energyconversion.blocks;

import java.util.List;
import java.util.Random;

import com.bxzmod.energyconversion.UnlistedByteArrayProperty;
import com.bxzmod.energyconversion.UnlistedPropertyBlockAvailable;
import com.bxzmod.energyconversion.creativetabs.CreativeTabsLoader;
import com.bxzmod.energyconversion.tileentity.PowerBlockTileEntity;
import com.google.common.collect.ImmutableList;

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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.PropertyFloat;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PowerBlock extends BlockContainer
{
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public static final UnlistedByteArrayProperty SIDE_CONFIG = new UnlistedByteArrayProperty("side_config");
	
	public static final UnlistedPropertyBlockAvailable NORTH = new UnlistedPropertyBlockAvailable("north");
    public static final UnlistedPropertyBlockAvailable SOUTH = new UnlistedPropertyBlockAvailable("south");
    public static final UnlistedPropertyBlockAvailable WEST = new UnlistedPropertyBlockAvailable("west");
    public static final UnlistedPropertyBlockAvailable EAST = new UnlistedPropertyBlockAvailable("east");
    public static final UnlistedPropertyBlockAvailable UP = new UnlistedPropertyBlockAvailable("up");
    public static final UnlistedPropertyBlockAvailable DOWN = new UnlistedPropertyBlockAvailable("down");

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
		switch(from)
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
		default :
			return null;
		}
			
	}
/*
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
		ItemStack item = new ItemStack(Item.getItemFromBlock(this));
		PowerBlockTileEntity te = (PowerBlockTileEntity) world.getTileEntity(pos);
		item.setTagCompound(te.getNBTFromData(new NBTTagCompound()));
		ret.add(item);
		return ret;
	}
*/
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack)
	{
		PowerBlockTileEntity te = (PowerBlockTileEntity) worldIn.getTileEntity(pos);
		if(stack.hasTagCompound())
		{
			NBTTagCompound nbt = stack.getTagCompound();
			if(nbt.hasKey("totalEnergy") && nbt.hasKey("sideType") && nbt.hasKey("Energy"))
				te.setDataByNBT(nbt);
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
		item.setTagCompound(((PowerBlockTileEntity)te).getNBTFromData(new NBTTagCompound()));
		spawnAsEntity(worldIn, pos, item);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
			EntityPlayer player)
	{
		ItemStack item = new ItemStack(Item.getItemFromBlock(this));
		PowerBlockTileEntity te = (PowerBlockTileEntity) world.getTileEntity(pos);
		item.setTagCompound(te.getNBTFromData(new NBTTagCompound()));
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
			if (!Loader.isModLoaded("IC2"))
			{
				if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
					playerIn.addChatMessage(new TextComponentString(I18n.format("msg.error")));
			} else
			{
				if (playerIn.getHeldItemMainhand() == null)
				{
					te.setType(te.getType() + 1);
					if (te.getType() > 5)
						te.setType(1);
					if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
						playerIn.addChatMessage(new TextComponentString(I18n.format("msg." + te.getType())));
				}
			}
		} else
		{
			te.setSideType(side);
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
	}

}
