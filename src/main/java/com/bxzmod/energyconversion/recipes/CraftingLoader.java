package com.bxzmod.energyconversion.recipes;

import com.bxzmod.energyconversion.blocks.BlockLoader;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CraftingLoader
{
	public CraftingLoader(FMLInitializationEvent event)
	{
		registerRecipe();
	}

	private static void registerRecipe()
	{
		GameRegistry.addShapedRecipe(new ItemStack(BlockLoader.powerBlockItem), new Object[] { "###", "#*#", "###", '#',
				Items.REDSTONE, '*', Item.getItemFromBlock(Blocks.COBBLESTONE) });

	}

}
