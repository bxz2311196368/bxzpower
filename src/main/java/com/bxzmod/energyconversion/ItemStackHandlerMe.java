package com.bxzmod.energyconversion;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ItemStackHandlerMe extends ItemStackHandler
{

	public ItemStackHandlerMe(int i)
	{
		super(i);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		if (!StackHelper.testStack(stack))
			return stack;
		return super.insertItem(slot, stack, simulate);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		ItemStack stack = this.stacks[slot];
		if (stack != null && StackHelper.isFullStack(stack))
			return super.extractItem(slot, amount, simulate);
		return null;
	}

}
