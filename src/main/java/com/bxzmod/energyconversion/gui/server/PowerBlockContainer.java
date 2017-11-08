package com.bxzmod.energyconversion.gui.server;

import com.bxzmod.energyconversion.ItemStackHandlerMe;
import com.bxzmod.energyconversion.StackHelper;
import com.bxzmod.energyconversion.gui.GuiLoader;
import com.bxzmod.energyconversion.tileentity.PowerBlockTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class PowerBlockContainer extends Container
{
	private ItemStackHandlerMe items = new ItemStackHandlerMe(9);
	private PowerBlockTileEntity te;
	private int totalEnergy = 0;

	public PowerBlockContainer(EntityPlayer player, TileEntity tileEntity)
	{
		super();
		this.te = (PowerBlockTileEntity) tileEntity;
		this.items = (ItemStackHandlerMe) te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		for (int i = 0; i < 9; ++i)
		{
			this.addSlotToContainer(new SlotItemHandler(items, i, 8 + i * 18, 20)
			{
				@Override
				public boolean isItemValid(ItemStack stack)
				{
					return stack != null && StackHelper.testStack(stack);
				}

				@Override
				public int getItemStackLimit(ItemStack stack)
				{
					return 1;
				}
			});
		}

		for (int i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 50 + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i)
		{
			this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 109));
		}

	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		Slot slot = inventorySlots.get(index);

		if (slot == null || !slot.getHasStack())
		{
			return null;
		}

		ItemStack newStack = slot.getStack(), oldStack = newStack.copy();

		boolean isMerged = false;

		if (index >= 0 && index < 9)
		{
			isMerged = mergeItemStack(newStack, 9, 45, true);
		} else if (index >= 9 && index < 36)
		{
			isMerged = newStack.stackSize <= 1 && mergeItemStack(newStack, 0, 9, false)
					|| mergeItemStack(newStack, 36, 45, false);
		} else if (index >= 36 && index < 45)
		{
			isMerged = newStack.stackSize <= 1 && mergeItemStack(newStack, 0, 9, false)
					|| mergeItemStack(newStack, 9, 36, false);
		}

		if (!isMerged)
		{
			return null;
		}

		if (newStack.stackSize == 0)
		{
			slot.putStack(null);
		} else
		{
			slot.onSlotChanged();
		}

		slot.onPickupFromSlot(playerIn, newStack);

		return oldStack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{

		return true;
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		totalEnergy = te.getTotalEnergy();

		for (IContainerListener i : this.listeners)
		{
			i.sendProgressBarUpdate(this, GuiLoader.DATA_E_C, totalEnergy);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data)
	{
		super.updateProgressBar(id, data);
		switch (id)
		{
		case GuiLoader.DATA_E_C:
			this.totalEnergy = data;
			break;
		default:
			break;
		}
	}

	public int getTotalEnergy()
	{
		return totalEnergy;
	}

}
