package com.bxzmod.energyconversion;

import com.bxzmod.energyconversion.server.Common;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Info.MODID, name = Info.MODNAME, dependencies = Info.dependencies, version = Info.VERSION, acceptedMinecraftVersions = Info.acceptedMinecraftVersions)
public class Main
{
	@SidedProxy(clientSide = "com.bxzmod.energyconversion.client.Client", serverSide = "com.bxzmod.energyconversion.server.Common")
	public static Common proxy;

	@Instance(Info.MODID)
	public static Main instance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit(event);
	}

}
