package com.bxzmod.energyconversion.server;

import com.bxzmod.energyconversion.blocks.BlockLoader;
import com.bxzmod.energyconversion.creativetabs.CreativeTabsLoader;
import com.bxzmod.energyconversion.recipes.CraftingLoader;
import com.bxzmod.energyconversion.tileentity.TileEntityLoader;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Common
{

	public void preInit(FMLPreInitializationEvent event)
	{
		new CreativeTabsLoader(event);
		new BlockLoader(event);
		new TileEntityLoader(event);

	}

	public void init(FMLInitializationEvent event)
	{
		new CraftingLoader(event);

	}

	public void postInit(FMLPostInitializationEvent event)
	{

	}

}
