package com.bxzmod.energyconversion.gui;

import com.bxzmod.energyconversion.Main;
import com.bxzmod.energyconversion.gui.client.PowerBlockGuiContainer;
import com.bxzmod.energyconversion.gui.server.PowerBlockContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class GuiLoader implements IGuiHandler
{

	public static final int GUI_E_C = 1;

	public static final int DATA_E_C = 1;

	public GuiLoader(FMLInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(Main.instance, this);
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (ID)
		{
		case GUI_E_C:
			return new PowerBlockContainer(player, world.getTileEntity(new BlockPos(x, y, z)));
		default:
			return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (ID)
		{
		case GUI_E_C:
			return new PowerBlockGuiContainer(
					new PowerBlockContainer(player, world.getTileEntity(new BlockPos(x, y, z))));
		default:
			return null;
		}
	}

}
