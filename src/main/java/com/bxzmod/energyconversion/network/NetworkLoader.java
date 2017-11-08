package com.bxzmod.energyconversion.network;

import com.bxzmod.energyconversion.Info;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkLoader
{
	public static SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(Info.MODID);

	private static int nextID = 0;

	public NetworkLoader(FMLPreInitializationEvent event)
	{
		registerMessage(TileEntitySync.ToClientHandler.class, TileEntitySync.class, Side.CLIENT);
		registerMessage(TileEntitySync.ToServerHandler.class, TileEntitySync.class, Side.SERVER);

	}

	private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(
			Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side)
	{
		instance.registerMessage(messageHandler, requestMessageType, nextID++, side);
	}
}
