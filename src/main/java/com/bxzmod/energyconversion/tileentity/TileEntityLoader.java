package com.bxzmod.energyconversion.tileentity;

import com.bxzmod.energyconversion.Info;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileEntityLoader
{

	public TileEntityLoader(FMLPreInitializationEvent event)
	{
		registerTileEntity(PowerBlockTileEntity.class, "PowerBlock");

	}

	public void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id)
	{
		GameRegistry.registerTileEntity(tileEntityClass, Info.MODID + ":" + id);
	}

}
