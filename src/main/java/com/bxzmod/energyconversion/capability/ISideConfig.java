package com.bxzmod.energyconversion.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface ISideConfig extends INBTSerializable<NBTTagCompound>
{
	public void setSide(boolean[] b);

	public boolean[] getSide();
}
