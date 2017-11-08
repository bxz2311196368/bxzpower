package com.bxzmod.energyconversion.compat.theoneprobe;

import javax.annotation.Nullable;

import com.bxzmod.energyconversion.Info;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class TOPCompatibility
{
	private static boolean registered;

	public static void register()
	{
		if (registered)
			return;
		registered = true;
		FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe",
				"com.bxzmod.energyconversion.compat.theoneprobe.TOPCompatibility$GetTheOneProbe");
	}

	public static class GetTheOneProbe implements com.google.common.base.Function<ITheOneProbe, Void>
	{

		public static ITheOneProbe probe;

		@Nullable
		@Override
		public Void apply(ITheOneProbe theOneProbe)
		{
			probe = theOneProbe;
			probe.registerProvider(new IProbeInfoProvider()
			{
				@Override
				public String getID()
				{
					return Info.MODID + ":default";
				}

				@Override
				public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world,
						IBlockState blockState, IProbeHitData data)
				{
					if (blockState.getBlock() instanceof TOPInfoProvider)
					{
						TOPInfoProvider provider = (TOPInfoProvider) blockState.getBlock();
						provider.addProbeInfo(mode, probeInfo, player, world, blockState, data);
					}

				}
			});
			return null;
		}
	}
}
