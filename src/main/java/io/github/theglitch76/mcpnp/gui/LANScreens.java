package io.github.theglitch76.mcpnp.gui;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;

public class LANScreens {

	public static class OpenToLanScreen extends CottonClientScreen {
		public OpenToLanScreen(GuiDescription description) {
			super(description);
		}
	}

	public static class UPnPLectureScreen extends CottonClientScreen {
		public UPnPLectureScreen(GuiDescription description) {
			super(description);
		}
	}
}
