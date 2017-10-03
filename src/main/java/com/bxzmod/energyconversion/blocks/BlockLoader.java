package com.bxzmod.energyconversion.blocks;

import com.bxzmod.energyconversion.blocks.itemblock.PowerBlockItemBlock;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockLoader
{
	public static PowerBlock powerBlock = new PowerBlock();
	public static PowerBlockItemBlock powerBlockItem = new PowerBlockItemBlock(powerBlock);

	public BlockLoader(FMLPreInitializationEvent event)
	{
		registerBlock(powerBlock);
		registerItem(powerBlockItem, powerBlock);

	}

	private static void registerBlock(Block block)
	{
		GameRegistry.register(block);
	}

	private static void registerItem(ItemBlock item, Block block)
	{
		item.setRegistryName(block.getRegistryName());
		GameRegistry.register(item);
	}

}
