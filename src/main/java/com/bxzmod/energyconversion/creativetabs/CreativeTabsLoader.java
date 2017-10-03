package com.bxzmod.energyconversion.creativetabs;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CreativeTabsLoader
{
	public static CreativeTabsEnergyConversion tab;

	public CreativeTabsLoader(FMLPreInitializationEvent event)
	{
		tab = new CreativeTabsEnergyConversion("energyconversion");
	}

}
