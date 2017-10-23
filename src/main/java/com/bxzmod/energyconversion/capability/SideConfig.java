package com.bxzmod.energyconversion.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class SideConfig
{
	public static class Storage implements Capability.IStorage<ISideConfig>
	{

		@Override
		public NBTBase writeNBT(Capability<ISideConfig> capability, ISideConfig instance, EnumFacing side)
		{

			return instance.serializeNBT();
		}

		@Override
		public void readNBT(Capability<ISideConfig> capability, ISideConfig instance, EnumFacing side, NBTBase nbt)
		{
			instance.deserializeNBT((NBTTagCompound) nbt);

		}

	}

	public static class Implementation implements ISideConfig
	{
		protected boolean[] side = new boolean[6];

		@Override
		public NBTTagCompound serializeNBT()
		{
			NBTTagCompound nbt = new NBTTagCompound();
			NBTTagList list = new NBTTagList();
			for(int i = 0; i <6; i++)
			{
				NBTTagCompound a = new NBTTagCompound();
				a.setBoolean("side" + i, this.side[i]);
				list.appendTag(a);
			}
			nbt.setTag("sideType", list);
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt)
		{
			NBTTagList list = nbt.getTagList("sideType", 0);
			for (int i = 0; i < 6; i++)
				this.side[i] = ((NBTTagCompound) list.get(i)).getBoolean("side" + i);
		}

		@Override
		public void setSide(boolean[] b)
		{
			for(int i = 0; i < 6; i++)
				this.side[i] = b[i];

		}

		@Override
		public boolean[] getSide()
		{
			return this.side;
		}

	}
}
