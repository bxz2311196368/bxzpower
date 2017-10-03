package com.bxzmod.energyconversion.blocks.blockmodel;

import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ModelLoader
{

	public ModelLoader(FMLPreInitializationEvent event)
	{
		ModelLoaderRegistry.registerLoader(new BakedModelLoader());
	}

}
