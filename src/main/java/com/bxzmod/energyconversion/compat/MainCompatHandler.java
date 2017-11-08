package com.bxzmod.energyconversion.compat;

import com.bxzmod.energyconversion.compat.theoneprobe.TOPCompatibility;
import com.bxzmod.energyconversion.compat.waila.WailaCompatibility;

import net.minecraftforge.fml.common.Loader;

public class MainCompatHandler
{
	public static void registerWaila()
	{
		if (Loader.isModLoaded("Waila"))
		{
			WailaCompatibility.register();
		}
	}

	public static void registerTOP()
	{
		if (Loader.isModLoaded("theoneprobe"))
		{
			TOPCompatibility.register();
		}
	}

}
