package com.bxzmod.energyconversion.gui.client;

import com.bxzmod.energyconversion.Info;
import com.bxzmod.energyconversion.gui.server.PowerBlockContainer;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class PowerBlockGuiContainer extends GuiContainer
{
	private static final String TEXTURE_PATH = Info.MODID + ":" + "textures/gui/container/PowerBlock.png";
	private static final ResourceLocation TEXTURE = new ResourceLocation(TEXTURE_PATH);

	PowerBlockContainer inventory;

	public PowerBlockGuiContainer(Container inventorySlotsIn)
	{
		super(inventorySlotsIn);
		this.xSize = 176;
		this.ySize = 133;
		inventory = (PowerBlockContainer) inventorySlotsIn;

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F);

		this.mc.getTextureManager().bindTexture(TEXTURE);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;

		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String rf = I18n.format("msg.total_energy") + ": " + inventory.getTotalEnergy();
		this.fontRendererObj.drawString(rf, 6, 40, 0x404040);
	}

}
