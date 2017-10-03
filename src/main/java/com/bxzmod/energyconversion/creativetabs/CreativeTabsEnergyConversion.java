package com.bxzmod.energyconversion.creativetabs;

import com.bxzmod.energyconversion.blocks.BlockLoader;

import net.minecraft.item.Item;

public class CreativeTabsEnergyConversion extends net.minecraft.creativetab.CreativeTabs
{

	public CreativeTabsEnergyConversion(String label)
	{
		super(label);

	}

	@Override
	public Item getTabIconItem()
	{

		return BlockLoader.powerBlockItem;
	}

}
