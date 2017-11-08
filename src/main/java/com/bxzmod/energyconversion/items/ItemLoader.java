package com.bxzmod.energyconversion.items;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemLoader
{
	public static Item wrench = new Wrench();

	public ItemLoader(FMLPreInitializationEvent event)
	{
		register(wrench);
	}

	private static void register(Item item)
	{
		GameRegistry.register(item);
	}
}
