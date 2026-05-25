package lotrpickupfilter.client;

import lotrpickupfilter.common.PickupFilterData;
import lotrpickupfilter.common.network.PacketPickupFilterUpdate;
import lotrpickupfilter.common.network.PickupFilterNetwork;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GuiPickupFilter extends GuiScreen {
	private static final int ADD_HELD = 0;
	private static final int REMOVE = 1;
	private static final int CLEAR = 2;
	private static final int DONE = 3;
	private static final int ADD_SEARCH = 4;
	private final GuiScreen parent;
	private int selectedIndex = -1;
	private int selectedSearchIndex = -1;
	private int scroll;
	private int searchScroll;
	private GuiTextField searchField;
	private List<String> searchResults = new ArrayList<String>();
	private String lastSearch = "";

	public GuiPickupFilter(GuiScreen parent) {
		this.parent = parent;
	}

	@Override
	public void initGui() {
		buttonList.clear();
		int center = width / 2;
		searchField = new GuiTextField(fontRendererObj, center + 8, 58, 168, 18);
		searchField.setMaxStringLength(64);
		searchField.setText(lastSearch);
		buttonList.add(new GuiButton(ADD_HELD, center - 170, height - 32, 74, 20, "Add Held"));
		buttonList.add(new GuiButton(ADD_SEARCH, center - 92, height - 32, 74, 20, "Add Result"));
		buttonList.add(new GuiButton(REMOVE, center - 14, height - 32, 64, 20, "Remove"));
		buttonList.add(new GuiButton(CLEAR, center + 54, height - 32, 54, 20, "Clear"));
		buttonList.add(new GuiButton(DONE, center + 112, height - 32, 58, 20, "Done"));
		updateSearchResults();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, "Pickup Filter", width / 2, 18, 0xFFFFFF);
		drawCenteredString(fontRendererObj, "Items in the filter list will stay on the ground.", width / 2, 32, 0xA0A0A0);

		List<String> keys = getKeys();
		int listLeft = width / 2 - 196;
		int listTop = 84;
		int rowHeight = 20;
		int visibleRows = Math.max(1, (height - 132) / rowHeight);

		fontRendererObj.drawString("Filter List", listLeft, 70, 0xFFFFFF);
		drawRect(listLeft - 4, listTop - 4, listLeft + 184, listTop + visibleRows * rowHeight + 4, 0xAA000000);
		for (int row = 0; row < visibleRows; row++) {
			int index = row + scroll;
			if (index >= keys.size()) {
				break;
			}

			int y = listTop + row * rowHeight;
			String key = keys.get(index);
			ItemStack stack = PickupFilterData.stackFromKey(key);
			if (index == selectedIndex) {
				drawRect(listLeft - 2, y - 1, listLeft + 182, y + 19, 0x66FFFFFF);
			}
			drawStackRow(stack, key, listLeft, y, 160);
		}

		int searchLeft = width / 2 + 8;
		fontRendererObj.drawString("Search Items", searchLeft, 48, 0xFFFFFF);
		searchField.drawTextBox();
		drawRect(searchLeft - 4, listTop - 4, searchLeft + 184, listTop + visibleRows * rowHeight + 4, 0xAA000000);
		for (int row = 0; row < visibleRows; row++) {
			int index = row + searchScroll;
			if (index >= searchResults.size()) {
				break;
			}

			int y = listTop + row * rowHeight;
			String key = searchResults.get(index);
			ItemStack stack = PickupFilterData.stackFromKey(key);
			if (index == selectedSearchIndex) {
				drawRect(searchLeft - 2, y - 1, searchLeft + 182, y + 19, 0x665588FF);
			}
			drawStackRow(stack, key, searchLeft, y, 160);
		}

		updateButtons();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		searchField.mouseClicked(mouseX, mouseY, mouseButton);
		int listLeft = width / 2 - 196;
		int searchLeft = width / 2 + 8;
		int listTop = 84;
		int rowHeight = 20;
		int visibleRows = Math.max(1, (height - 132) / rowHeight);
		if (mouseX >= listLeft - 4 && mouseX <= listLeft + 184 && mouseY >= listTop - 4 && mouseY <= listTop + visibleRows * rowHeight + 4) {
			int index = scroll + (mouseY - listTop) / rowHeight;
			if (index >= 0 && index < getKeys().size()) {
				selectedIndex = index;
			}
		} else if (mouseX >= searchLeft - 4 && mouseX <= searchLeft + 184 && mouseY >= listTop - 4 && mouseY <= listTop + visibleRows * rowHeight + 4) {
			int index = searchScroll + (mouseY - listTop) / rowHeight;
			if (index >= 0 && index < searchResults.size()) {
				selectedSearchIndex = index;
			}
		}
	}

	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		int wheel = org.lwjgl.input.Mouse.getEventDWheel();
		if (wheel != 0) {
			int mouseX = org.lwjgl.input.Mouse.getEventX() * width / mc.displayWidth;
			int mouseY = height - org.lwjgl.input.Mouse.getEventY() * height / mc.displayHeight - 1;
			if (mouseX >= width / 2 + 4) {
				searchScroll = clampScroll(searchScroll + (wheel < 0 ? 1 : -1), searchResults.size());
			} else {
				scroll = clampScroll(scroll + (wheel < 0 ? 1 : -1), getKeys().size());
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		int buttonId = getButtonId(button);
		if (buttonId == ADD_HELD) {
			ItemStack held = getHeldFilterStack();
			if (held != null) {
				PickupFilterClientData.filter.add(PickupFilterData.getKey(held));
				sync();
			}
		} else if (buttonId == ADD_SEARCH) {
			if (selectedSearchIndex >= 0 && selectedSearchIndex < searchResults.size()) {
				PickupFilterClientData.filter.add(searchResults.get(selectedSearchIndex));
				sync();
			}
		} else if (buttonId == REMOVE) {
			List<String> keys = getKeys();
			if (selectedIndex >= 0 && selectedIndex < keys.size()) {
				PickupFilterClientData.filter.remove(keys.get(selectedIndex));
				selectedIndex = -1;
				sync();
			}
		} else if (buttonId == CLEAR) {
			PickupFilterClientData.filter.clear();
			selectedIndex = -1;
			scroll = 0;
			sync();
		} else if (buttonId == DONE) {
			mc.displayGuiScreen(parent);
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		if (searchField.textboxKeyTyped(typedChar, keyCode)) {
			updateSearchResults();
		} else if (keyCode == 1) {
			mc.displayGuiScreen(parent);
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		searchField.updateCursorCounter();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	private void updateButtons() {
		for (Object obj : buttonList) {
			GuiButton button = (GuiButton) obj;
			int buttonId = getButtonId(button);
			if (buttonId == ADD_HELD) {
				button.enabled = getHeldFilterStack() != null;
			} else if (buttonId == ADD_SEARCH) {
				button.enabled = selectedSearchIndex >= 0 && selectedSearchIndex < searchResults.size();
			} else if (buttonId == REMOVE) {
				button.enabled = selectedIndex >= 0 && selectedIndex < getKeys().size();
			} else if (buttonId == CLEAR) {
				button.enabled = !PickupFilterClientData.filter.isEmpty();
			}
		}
	}

	private List<String> getKeys() {
		return new ArrayList<String>(PickupFilterClientData.filter);
	}

	private void sync() {
		PickupFilterNetwork.network.sendToServer(new PacketPickupFilterUpdate(PickupFilterClientData.filter));
	}

	private ItemStack getHeldFilterStack() {
		ItemStack cursorStack = mc.thePlayer.inventory.getItemStack();
		if (cursorStack != null) {
			return cursorStack;
		}
		return mc.thePlayer.getCurrentEquippedItem();
	}

	private void updateSearchResults() {
		lastSearch = searchField.getText();
		searchResults = findItems(lastSearch);
		selectedSearchIndex = -1;
		searchScroll = 0;
	}

	private List<String> findItems(String query) {
		List<String> results = new ArrayList<String>();
		if (query == null || query.trim().length() < 2) {
			return results;
		}

		String needle = query.trim().toLowerCase();
		Set<String> seen = new LinkedHashSet<String>();
		for (Object obj : Item.itemRegistry) {
			if (!(obj instanceof Item)) {
				continue;
			}

			Item item = (Item) obj;
			List subItems = new ArrayList();
			try {
				item.getSubItems(item, CreativeTabs.tabAllSearch, subItems);
			} catch (Throwable ignored) {
			}
			if (subItems.isEmpty()) {
				subItems.add(new ItemStack(item));
			}

			for (Object stackObj : subItems) {
				if (!(stackObj instanceof ItemStack)) {
					continue;
				}
				ItemStack stack = (ItemStack) stackObj;
				String key = PickupFilterData.getKey(stack);
				String registryName = Item.itemRegistry.getNameForObject(stack.getItem());
				String displayName = stack.getDisplayName();
				if (matchesSearch(needle, registryName, displayName) && seen.add(key)) {
					results.add(key);
					if (results.size() >= 80) {
						return results;
					}
				}
			}
		}
		return results;
	}

	private boolean matchesSearch(String needle, String registryName, String displayName) {
		return (registryName != null && registryName.toLowerCase().contains(needle)) || (displayName != null && displayName.toLowerCase().contains(needle));
	}

	private int clampScroll(int value, int size) {
		int maxScroll = Math.max(0, size - Math.max(1, (height - 132) / 20));
		if (value < 0) {
			return 0;
		}
		if (value > maxScroll) {
			return maxScroll;
		}
		return value;
	}

	private void drawStackRow(ItemStack stack, String fallbackText, int x, int y, int textWidth) {
		if (stack != null) {
			RenderHelper.enableGUIStandardItemLighting();
			itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.getTextureManager(), stack, x, y + 1);
			RenderHelper.disableStandardItemLighting();
			fontRendererObj.drawString(fontRendererObj.trimStringToWidth(stack.getDisplayName(), textWidth), x + 24, y + 6, 0xFFFFFF);
		} else {
			fontRendererObj.drawString(fontRendererObj.trimStringToWidth(fallbackText, textWidth), x + 24, y + 6, 0xFF8080);
		}
	}

	private int getButtonId(GuiButton button) {
		return ObfuscationReflectionHelper.getPrivateValue(GuiButton.class, button, "id", "field_146127_k");
	}
}
