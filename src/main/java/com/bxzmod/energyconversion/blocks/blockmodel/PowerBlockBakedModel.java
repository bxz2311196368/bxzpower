package com.bxzmod.energyconversion.blocks.blockmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.bxzmod.energyconversion.Info;
import com.bxzmod.energyconversion.blocks.PowerBlock;
import com.google.common.base.Function;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.property.IExtendedBlockState;

public class PowerBlockBakedModel implements IBakedModel
{
	public static final ModelResourceLocation POWER_BLOCK_BAKED_MODEL = new ModelResourceLocation(
			Info.MODID + ":powerblock");

	private TextureAtlasSprite sprite[] = new TextureAtlasSprite[4];
	private VertexFormat format;

	public PowerBlockBakedModel(IModelState state, VertexFormat format,
			Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
	{
		this.format = format;
		sprite[0] = bakedTextureGetter.apply(new ResourceLocation(Info.MODID, "blocks/power_block_in"));
		sprite[1] = bakedTextureGetter.apply(new ResourceLocation(Info.MODID, "blocks/power_block_out"));
		sprite[2] = bakedTextureGetter.apply(new ResourceLocation(Info.MODID, "blocks/power_block_face_in"));
		sprite[3] = bakedTextureGetter.apply(new ResourceLocation(Info.MODID, "blocks/power_block_face_out"));
		
	}

	private BakedQuad setQuad(EnumFacing side, int i)
	{
		BakedQuad quads;
		switch (side)
		{
		case UP:
			quads = createQuad(new Vec3d(0, 1, 0), new Vec3d(0, 1, 1), new Vec3d(1, 1, 1), new Vec3d(1, 1, 0), i);
			break;
		case DOWN:
			quads = createQuad(new Vec3d(1, 0, 1), new Vec3d(0, 0, 1), new Vec3d(0, 0, 0), new Vec3d(1, 0, 0), i);
			break;
		case NORTH:
			quads = createQuad(new Vec3d(1, 1, 0), new Vec3d(1, 0, 0), new Vec3d(0, 0, 0), new Vec3d(0, 1, 0), i+2);
			break;
		case SOUTH:
			quads = createQuad(new Vec3d(0, 1, 1), new Vec3d(0, 0, 1), new Vec3d(1, 0, 1), new Vec3d(1, 1, 1), i);
			break;
		case WEST:
			quads = createQuad(new Vec3d(0, 1, 0), new Vec3d(0, 0, 0), new Vec3d(0, 0, 1), new Vec3d(0, 1, 1), i);
			break;
		case EAST:
			quads = createQuad(new Vec3d(1, 1, 1), new Vec3d(1, 0, 1), new Vec3d(1, 0, 0), new Vec3d(1, 1, 0), i);
			break;
		default:
			return null;
		}
		return quads;
	}

	private BakedQuad createQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, int num)
	{
		Vec3d normal = v1.subtract(v2).crossProduct(v3.subtract(v2));
		UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
		builder.setTexture(sprite[num]);
		putVertex(builder, normal, v1.xCoord, v1.yCoord, v1.zCoord, 0, 0, num);
		putVertex(builder, normal, v2.xCoord, v2.yCoord, v2.zCoord, 0, 16, num);
		putVertex(builder, normal, v3.xCoord, v3.yCoord, v3.zCoord, 16, 16, num);
		putVertex(builder, normal, v4.xCoord, v4.yCoord, v4.zCoord, 16, 0, num);
		return builder.build();
	}

	private void putVertex(UnpackedBakedQuad.Builder builder, Vec3d normal, double x, double y, double z, float u,
			float v, int num)
	{
		for (int e = 0; e < format.getElementCount(); e++)
		{
			switch (format.getElement(e).getUsage())
			{
			case POSITION:
				builder.put(e, (float) x, (float) y, (float) z, 1.0f);
				break;
			case COLOR:
				builder.put(e, 1.0f, 1.0f, 1.0f, 1.0f);
				break;
			case UV:
				if (format.getElement(e).getIndex() == 0)
				{
					u = sprite[num].getInterpolatedU(u);
					v = sprite[num].getInterpolatedV(v);
					builder.put(e, u, v, 0f, 1f);
					break;
				}
			case NORMAL:
				builder.put(e, (float) normal.xCoord, (float) normal.yCoord, (float) normal.zCoord, 0f);
				break;
			default:
				builder.put(e);
				break;
			}
		}
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
	{
		IExtendedBlockState extendedBlockState;
		List<BakedQuad> quads = new ArrayList<>();

		if (state == null)
		{
			for (EnumFacing f : EnumFacing.values())
				quads.add(this.setQuad(f, 0));
			return quads;
		} else
			extendedBlockState = (IExtendedBlockState) state;
		if (side == null)
		{
			return Collections.emptyList();
		}
		byte[] a = extendedBlockState.getValue(PowerBlock.SIDE_CONFIG);
		quads.add(this.setQuad(side, a[side.getIndex()]));
		return quads;
	}
	
	public static boolean isArray(Object obj) {
        return (obj != null && obj.getClass().isArray());
    }

	@Override
	public boolean isAmbientOcclusion()
	{
		return false;
	}

	@Override
	public boolean isGui3d()
	{
		return false;
	}

	@Override
	public boolean isBuiltInRenderer()
	{
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture()
	{
		return sprite[0];
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms()
	{
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public ItemOverrideList getOverrides()
	{
		return null;
	}
	
}
