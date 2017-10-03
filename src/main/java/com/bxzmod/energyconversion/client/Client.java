package com.bxzmod.energyconversion.client;

import com.bxzmod.energyconversion.blocks.BlockRenderLoader;
import com.bxzmod.energyconversion.blocks.blockmodel.ModelLoader;
import com.bxzmod.energyconversion.server.Common;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Client extends Common
{
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
		new ModelLoader(event);
		new BlockRenderLoader(event);

	}

	@Override
	public void init(FMLInitializationEvent event)
	{
		super.init(event);

	}

	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);

	}

}
