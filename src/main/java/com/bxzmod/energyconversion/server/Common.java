package com.bxzmod.energyconversion.server;

import com.bxzmod.energyconversion.blocks.BlockLoader;
import com.bxzmod.energyconversion.capability.CapabilityLoader;
import com.bxzmod.energyconversion.compat.MainCompatHandler;
import com.bxzmod.energyconversion.creativetabs.CreativeTabsLoader;
import com.bxzmod.energyconversion.gui.GuiLoader;
import com.bxzmod.energyconversion.items.ItemLoader;
import com.bxzmod.energyconversion.network.NetworkLoader;
import com.bxzmod.energyconversion.recipes.CraftingLoader;
import com.bxzmod.energyconversion.tileentity.TileEntityLoader;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Common
{

	public void preInit(FMLPreInitializationEvent event)
	{
		new CapabilityLoader(event);
		new CreativeTabsLoader(event);
		new BlockLoader(event);
		new ItemLoader(event);
		new TileEntityLoader(event);
		new NetworkLoader(event);
		MainCompatHandler.registerWaila();
		MainCompatHandler.registerTOP();
	}

	public void init(FMLInitializationEvent event)
	{
		new CraftingLoader(event);
		new GuiLoader(event);

	}

	public void postInit(FMLPostInitializationEvent event)
	{

	}

}
