package io.github.theglitch76.mcpnp.mixin;

import io.github.theglitch76.mcpnp.gui.LANScreens;
import io.github.theglitch76.mcpnp.gui.OpenToLanGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

@Mixin(GameMenuScreen.class)
public abstract class MixinGameMenuScreen extends Screen {
	protected MixinGameMenuScreen(Text title) {
		super(title);
	}

	@Redirect(method = "initWidgets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/GameMenuScreen;addButton(Lnet/minecraft/client/gui/widget/AbstractButtonWidget;)Lnet/minecraft/client/gui/widget/AbstractButtonWidget;"))
	private <T extends AbstractButtonWidget> T redirectAddButton(GameMenuScreen screen, T button) {
		if(button.getMessage().equals(I18n.translate("menu.shareToLan"))) {
			return (T) this.addButton(new ButtonWidget(this.width / 2 + 4, this.height / 4 + 96 + -16, 98, 20, I18n.translate("menu.shareToLan"), (buttonWidgetx) -> {
				this.minecraft.openScreen(new LANScreens.OpenToLanScreen(new OpenToLanGui(this)));
			}));
		} else return this.addButton(button);
	}
}
