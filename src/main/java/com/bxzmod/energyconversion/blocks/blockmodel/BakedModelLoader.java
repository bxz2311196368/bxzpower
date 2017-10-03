package com.bxzmod.energyconversion.blocks.blockmodel;

import com.bxzmod.energyconversion.Info;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

public class BakedModelLoader implements ICustomModelLoader
{
	public static final PowerBlockModel POWER_BLOCK_MODEL = new PowerBlockModel();

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{

	}

	@Override
	public boolean accepts(ResourceLocation modelLocation)
	{
		return modelLocation.getResourceDomain().equals(Info.MODID)
				&& "powerblock".equals(modelLocation.getResourcePath());
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception
	{
		return this.POWER_BLOCK_MODEL;
	}

}
