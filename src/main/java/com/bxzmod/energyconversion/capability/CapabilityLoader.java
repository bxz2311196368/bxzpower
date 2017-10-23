package com.bxzmod.energyconversion.capability;

import java.util.concurrent.Callable;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CapabilityLoader 
{
	@CapabilityInject(ISideConfig.class)
    public static Capability<ISideConfig> SIDE_CONFIG = null;

	public CapabilityLoader(FMLPreInitializationEvent event) 
	{
		CapabilityManager.INSTANCE.register(ISideConfig.class, new SideConfig.Storage(),
				new Callable<SideConfig.Implementation>() {
					@Override
					public SideConfig.Implementation call() throws Exception
					{
						return new SideConfig.Implementation();
					}
				});
	}

}
