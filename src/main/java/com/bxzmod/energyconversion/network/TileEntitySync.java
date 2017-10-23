package com.bxzmod.energyconversion.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bxzmod.energyconversion.tileentity.PowerBlockTileEntity;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class TileEntitySync implements IMessage
{
	public static final Logger LOGGER = LogManager.getLogger();
	public NBTTagCompound nbt;

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		ByteBufUtils.writeTag(buf, nbt);
	}
	
	public static class ToClientHandler implements IMessageHandler<TileEntitySync, IMessage>
	{

		@Override
		public IMessage onMessage(TileEntitySync message, MessageContext ctx)
		{
			if (ctx.side == Side.CLIENT)
			{
				//LOGGER.info(message.nbt.toString());
				final NBTTagCompound nbt = (NBTTagCompound) message.nbt;
                Minecraft.getMinecraft().addScheduledTask(new Runnable()
                {
					@Override
					public void run()
					{
						BlockPos pos = new BlockPos(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
						World world = Minecraft.getMinecraft().theWorld;
						PowerBlockTileEntity te;
						if (world.provider.getDimension() == nbt.getInteger("world"))
						{
							te = (PowerBlockTileEntity) world.getTileEntity(pos);
							te.markHasUpdated();
							te.readFromNBT(nbt);
							world.markBlockRangeForRenderUpdate(pos, pos);
						}

					}
                	
                });
				
			}
			return null;
		}
		
	}
	public static class ToServerHandler implements IMessageHandler<TileEntitySync, IMessage>
	{

		@Override
		public IMessage onMessage(TileEntitySync message, MessageContext ctx)
		{
			final MinecraftServer Server = FMLCommonHandler.instance().getMinecraftServerInstance();
			
			if (ctx.side == Side.SERVER)
			{
				final NBTTagCompound nbt = (NBTTagCompound) message.nbt;
                Server.addScheduledTask(new Runnable()
                {
					@Override
					public void run()
					{
						BlockPos pos = new BlockPos(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
						int dim = nbt.getInteger("world");
						WorldServer world = DimensionManager.getWorld(dim);
						PowerBlockTileEntity te;
						te = (PowerBlockTileEntity) world.getTileEntity(pos);
						NBTTagCompound side = new NBTTagCompound();
						te.writeToNBT(side);
						side.setInteger("world", world.provider.getDimension());
						TileEntitySync message1 = new TileEntitySync();
						message1.nbt = side.copy();
						EntityPlayerMP player = Server.getPlayerList().getPlayerByUsername(nbt.getString("player"));
						NetworkLoader.instance.sendTo(message1, player);
					}
                	
                });
			}
			return null;
		}
		
	}
}
