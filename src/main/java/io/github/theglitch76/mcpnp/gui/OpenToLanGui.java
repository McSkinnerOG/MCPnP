package io.github.theglitch76.mcpnp.gui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.client.modmenu.WKirbSprite;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.WToggleButton;
import io.github.theglitch76.mcpnp.MCPnP;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

public class OpenToLanGui extends LightweightGuiDescription {
	private final MinecraftClient minecraft = MinecraftClient.getInstance();
	private final MusicLoop discLoop = new MusicLoop(SoundEvents.MUSIC_DISC_WAIT);
	private WGridPanel root;

	public OpenToLanGui(Screen parent) {
		root = new WGridPanel();
		setRootPanel(root);
		root.setSize(306, 198);

		WLabel label = new WLabel(new LiteralText("Open To LAN"), 0xFFFFFF);
		root.add(label, 0, 0, 2, 1);

		easterWidget();

		// Configuration

		WTextField portWidget = new WTextField() {
			@Override
			public void onCharTyped(char ch) {
				if(Character.isDigit(ch) || ch==GLFW.GLFW_KEY_DELETE || ch==GLFW.GLFW_KEY_BACKSPACE || ch==GLFW.GLFW_KEY_LEFT
								|| ch==GLFW.GLFW_KEY_RIGHT || Screen.isSelectAll(ch) || Screen.isPaste(ch) || Screen.isCopy(ch)) {
					super.onCharTyped(ch);
				}
			}

			@Override
			public void onKeyPressed(int ch, int key, int modifiers) {
				if(Character.isDigit(ch) || ch==GLFW.GLFW_KEY_DELETE || ch==GLFW.GLFW_KEY_BACKSPACE || ch==GLFW.GLFW_KEY_LEFT
								|| ch==GLFW.GLFW_KEY_RIGHT || Screen.isSelectAll(ch) || Screen.isPaste(ch) || Screen.isCopy(ch)) {
					super.onKeyPressed(ch, key, modifiers);
				}
			}
		};

		portWidget.setMaxLength(5);
		root.add(portWidget, 0, 2, 3, 1);

		WToggleButton openViaUpnp = new WToggleButton(new TranslatableText("gui.mcpnp.useupnp"));
		root.add(openViaUpnp, 5, 1);

		WButton open = new WButton(new TranslatableText("lanServer.start"));
		open.setOnClick(() -> {
			minecraft.openScreen(null);
			int portNum;
			if(portWidget.getText().equals("")) {
					portNum = NetworkUtils.findLocalPort();
					minecraft.player.addChatMessage(new TranslatableText("chat.mcpnp.invalidport", portNum), true);
			} else {
				portNum = Integer.parseInt(portWidget.getText());
			}
			boolean success = MCPnP.openToLan(portNum, openViaUpnp.getToggle(), GameMode.CREATIVE, false);
			if(success) {
				minecraft.player.addChatMessage(new TranslatableText("commands.publish.started", portNum), false);
			} else {
				new TranslatableText("commands.publish.failed");
			}
		});
		root.add(open, 0, 9, 6, 1);

		WButton cancel = new WButton(new TranslatableText("gui.cancel"));
		cancel.setOnClick(() -> minecraft.openScreen(parent));
		root.add(cancel, 7, 9, 6, 1);

		root.validate(this);
	}

	private void easterWidget() {
		WKirbSprite kirb = new WKirbSprite();
		WSprite icon = new WSprite(new Identifier("minecraft:textures/item/redstone.png")) {
			private boolean isDiamond = false;
			@Override
			public void onClick(int x, int y, int button) {
				if (isDiamond) {
					this.setImage(new Identifier("minecraft:textures/item/redstone.png"));
					minecraft.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_SNARE, 1.25F, 0.75F);
					minecraft.getSoundManager().stop(discLoop);
					root.remove(kirb);

				} else {
					this.setImage(new Identifier("minecraft:textures/item/diamond.png"));
					minecraft.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_CHIME, 1.25F, 1);
					minecraft.getSoundManager().play(discLoop);
					root.add(kirb, 0, 9);
				}
				isDiamond = !isDiamond;
			}
		};

		root.add(icon, 16, 10, 1, 1);
	}

	public static class MusicLoop extends MovingSoundInstance {
		protected MusicLoop(SoundEvent soundEvent) {
			super(soundEvent, SoundCategory.AMBIENT);
			this.repeat = false;
			this.repeatDelay = 0;
			this.volume = 1.0F;
			this.field_18935 = true;
			this.looping = true;
		}

		@Override
		public void tick() {
			// NO-OP
		}
	}
}
