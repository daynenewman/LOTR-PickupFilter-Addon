package lotrpickupfilter.client;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiScreenEvent;

import java.util.List;

public class PickupFilterGuiHandler {
	private static final int FILTER_BUTTON_ID = 247801;

	@SubscribeEvent
	public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
		if (!(event.gui instanceof GuiInventory)) {
			return;
		}

		GuiContainer gui = (GuiContainer) event.gui;
		int guiLeft = getGuiLeft(gui);
		int guiTop = getGuiTop(gui);
		int buttonX = guiLeft - 58;
		if (buttonX < 0) {
			buttonX = guiLeft + getXSize(gui) + 4;
		}
		event.buttonList.add(new GuiButton(FILTER_BUTTON_ID, buttonX, guiTop + 8, 54, 20, "Filter"));
	}

	@SubscribeEvent
	public void onActionPerformed(GuiScreenEvent.ActionPerformedEvent.Pre event) {
		if (getButtonId(event.button) != FILTER_BUTTON_ID || !(event.gui instanceof GuiInventory)) {
			return;
		}

		event.setCanceled(true);
		Minecraft.getMinecraft().displayGuiScreen(new GuiPickupFilter(event.gui));
	}

	@SuppressWarnings("unused")
	private List getButtonList(GuiScreen gui) {
		return ObfuscationReflectionHelper.getPrivateValue(GuiScreen.class, gui, "buttonList", "field_146292_n");
	}

	private int getGuiLeft(GuiContainer gui) {
		return ObfuscationReflectionHelper.getPrivateValue(GuiContainer.class, gui, "guiLeft", "field_147003_i");
	}

	private int getGuiTop(GuiContainer gui) {
		return ObfuscationReflectionHelper.getPrivateValue(GuiContainer.class, gui, "guiTop", "field_147009_r");
	}

	private int getXSize(GuiContainer gui) {
		return ObfuscationReflectionHelper.getPrivateValue(GuiContainer.class, gui, "xSize", "field_146999_f");
	}

	private int getButtonId(GuiButton button) {
		return ObfuscationReflectionHelper.getPrivateValue(GuiButton.class, button, "id", "field_146127_k");
	}
}
