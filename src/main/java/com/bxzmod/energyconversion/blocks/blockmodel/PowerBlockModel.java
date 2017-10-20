package com.bxzmod.energyconversion.blocks.blockmodel;

import java.util.Collection;
import java.util.Collections;

import com.bxzmod.energyconversion.Info;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

public class PowerBlockModel implements IModel
{

	@Override
	public Collection<ResourceLocation> getDependencies()
	{
		return Collections.emptySet();
	}

	@Override
	public Collection<ResourceLocation> getTextures()
	{
		return ImmutableSet.of(new ResourceLocation(Info.MODID, "blocks/power_block_in"),
				new ResourceLocation(Info.MODID, "blocks/power_block_out"),
				new ResourceLocation(Info.MODID, "blocks/power_block_face_in"),
				new ResourceLocation(Info.MODID, "blocks/power_block_face_out"));
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format,
			Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
	{
		return new PowerBlockBakedModel(state, format, bakedTextureGetter);
	}

	@Override
	public IModelState getDefaultState()
	{
		return TRSRTransformation.identity();
	}

}
