package com.bxzmod.energyconversion.items;

import java.util.List;

import com.bxzmod.energyconversion.creativetabs.CreativeTabsLoader;
import com.bxzmod.energyconversion.tileentity.PowerBlockTileEntity;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Wrench extends Item
{

	public Wrench()
	{
		this.setRegistryName("wrench");
		this.setUnlocalizedName("wrench");
		this.setMaxStackSize(1);
		this.setMaxDamage(0);
		this.setCreativeTab(CreativeTabsLoader.tab);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		TileEntity te1 = worldIn.getTileEntity(pos);
		PowerBlockTileEntity te;
		if (te1 instanceof PowerBlockTileEntity)
		{
			if (!Loader.isModLoaded("IC2"))
			{
				if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
					playerIn.addChatMessage(new TextComponentString(I18n.format("msg.error")));
				return EnumActionResult.PASS;
			}
			te = (PowerBlockTileEntity) te1;
			te.setType(te.getType() + 1);
			if (te.getType() > 5)
				te.setType(1);
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
				playerIn.addChatMessage(new TextComponentString(I18n.format("msg." + te.getType())));
		}
		return EnumActionResult.PASS;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
	{
		tooltip.add(I18n.format("tooltip.wrench", TextFormatting.BLUE));
	}

}
