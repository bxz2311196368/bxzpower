package com.bxzmod.energyconversion.blocks;

import com.bxzmod.energyconversion.Info;
import com.bxzmod.energyconversion.blocks.blockmodel.PowerBlockBakedModel;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRenderLoader
{
	@SideOnly(Side.CLIENT)
	public BlockRenderLoader(FMLPreInitializationEvent event)
	{
		// registerRender(BlockLoader.removeEnchantment,
		// BlockLoader.removeEnchantmentBlock);
		registerStateMapper(BlockLoader.powerBlock, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState)
			{
				return PowerBlockBakedModel.POWER_BLOCK_BAKED_MODEL;
			}
		});

		registerRenderBlockState(BlockLoader.powerBlock, BlockLoader.powerBlockItem, 0, "powerblock");

	}

	@SideOnly(Side.CLIENT)
	private static void registerRender(Block block, ItemBlock item)
	{
		ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(block.getRegistryName(),
				"inventory");
		final int DEFAULT_ITEM_SUBTYPE = 0;
		ModelLoader.setCustomModelResourceLocation(item, DEFAULT_ITEM_SUBTYPE, itemModelResourceLocation);
	}

	@SideOnly(Side.CLIENT)
	private static void registerRenderBlockState(Block block, ItemBlock item, int meta, String name)
	{
		ResourceLocation location = new ResourceLocation(Info.MODID, name);
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(location, "inventory"));
		// ModelLoader.registerItemVariants(item, location);
	}

	@SideOnly(Side.CLIENT)
	private static void registerStateMapper(Block block, IStateMapper mapper)
	{
		ModelLoader.setCustomStateMapper(block, mapper);
	}

}
