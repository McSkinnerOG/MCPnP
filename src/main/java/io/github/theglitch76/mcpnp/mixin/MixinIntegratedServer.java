package io.github.theglitch76.mcpnp.mixin;

import com.dosse.upnp.UPnP;
import io.github.theglitch76.mcpnp.MCPnP;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegratedServer.class)
public class MixinIntegratedServer {

    @Final
    @Shadow
    private MinecraftClient client;

    @Shadow
    private int lanPort;

    @Inject(at = @At("HEAD"), method = "openToLan")
    public void beforeOpenToLan(GameMode gameMode, boolean cheatsAllowed, int port, CallbackInfoReturnable<Boolean> cir) {

    }

    @Inject(at = @At("HEAD"), method = "stop")
    public void beforeStop(boolean bl, CallbackInfo ci) {
    	if(lanPort == -1) {
    		return;
	    }
        MCPnP.LOGGER.info("Closing UPnP port " + lanPort);
        if (!UPnP.closePortTCP(lanPort)) {
            MCPnP.LOGGER.warn("Failed to close port " + lanPort + "! Was it opened in the first place?");
        }
    }
}
