package com.bxzmod.energyconversion.blocks;

import java.util.List;

import com.bxzmod.energyconversion.UnlistedByteArrayProperty;
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
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
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

	public PowerBlock()
	{
		super(Material.GRASS);
		this.setUnlocalizedName("powerblock");
		this.setRegistryName("power_block");
		this.setHardness(2.0F);
		this.setHarvestLevel("pickaxe", 0);
		this.setCreativeTab(CreativeTabsLoader.tab);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));

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
		IUnlistedProperty[] unlistedProperties = new IUnlistedProperty[] { SIDE_CONFIG };
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
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		IExtendedBlockState s = (IExtendedBlockState) state;
		PowerBlockTileEntity te = (PowerBlockTileEntity) world.getTileEntity(pos);
		return s.withProperty(SIDE_CONFIG, te.getByte());
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
