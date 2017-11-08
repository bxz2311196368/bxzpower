package com.bxzmod.energyconversion.recipes;

import com.bxzmod.energyconversion.blocks.BlockLoader;
import com.bxzmod.energyconversion.items.ItemLoader;

import ic2.api.item.IC2Items;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
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
		if (Loader.isModLoaded("IC2"))
		{
			GameRegistry.addShapelessRecipe(new ItemStack(ItemLoader.wrench),
					new ItemStack(IC2Items.getItemAPI().getItem("wrench")));
			GameRegistry.addShapelessRecipe(new ItemStack(IC2Items.getItemAPI().getItem("wrench")),
					new ItemStack(ItemLoader.wrench));
		}

	}

}
