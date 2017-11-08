package com.bxzmod.energyconversion;

import cofh.api.energy.IEnergyContainerItem;
import crazypants.enderio.power.IInternalPoweredItem;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import mekanism.api.energy.IEnergizedItem;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.Loader;

public class StackHelper
{
	public static boolean testStack(ItemStack stack)
	{
		boolean isEnergyItem = false;
		if (Loader.isModLoaded("IC2"))
		{
			Capability<IElectricItem> ic2 = null;
			if (stack.hasCapability(ic2, null) || stack.getItem() instanceof IElectricItem)
				isEnergyItem = true;
		}
		if (stack.getItem() instanceof IEnergizedItem)
			isEnergyItem = true;
		if (stack.getItem() instanceof IEnergyContainerItem)
			isEnergyItem = true;
		if (Loader.isModLoaded("tesla"))
			if (stack.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null))
				isEnergyItem = true;
		if (stack.hasCapability(CapabilityEnergy.ENERGY, null))
			isEnergyItem = true;
		return isEnergyItem;
	}

	public static boolean isFullStack(ItemStack stack)
	{
		boolean isEnergyFull = false;
		if (stack.getItem() instanceof IEnergizedItem)
			if (((IEnergizedItem) stack.getItem()).getEnergy(stack) == ((IEnergizedItem) stack.getItem())
					.getMaxEnergy(stack))
				isEnergyFull = true;
		if (stack.getItem() instanceof IEnergyContainerItem)
			if (((IEnergyContainerItem) stack.getItem())
					.getEnergyStored(stack) == ((IEnergyContainerItem) stack.getItem()).getMaxEnergyStored(stack))
				isEnergyFull = true;
		if (Loader.isModLoaded("IC2"))
		{
			int tier = 14;
			if (ElectricItem.manager.charge(stack, 1.0d, tier, true, true) == 0.0d)
				isEnergyFull = true;
			if (stack.getItemDamage() == 0 && stack.getMaxDamage() == 27)
				isEnergyFull = true;
		}
		if (Loader.isModLoaded("tesla"))
			if (stack.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null))
				if (stack.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null).givePower(1, true) == 0)
					isEnergyFull = true;
		if (stack.hasCapability(CapabilityEnergy.ENERGY, null))
			if (stack.getCapability(CapabilityEnergy.ENERGY, null).receiveEnergy(1, true) == 0)
				isEnergyFull = true;
		return isEnergyFull;
	}
}
