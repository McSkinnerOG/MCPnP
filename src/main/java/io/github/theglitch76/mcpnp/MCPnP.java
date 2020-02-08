package io.github.theglitch76.mcpnp;

import net.fabricmc.api.ModInitializer;

import com.dosse.upnp.UPnP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

public class MCPnP implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
	    // TODO: Automatically disable UPnP option on unsupported routers
    }

	/**
	 * Opens to LAN
	 * @param port
	 * @param useUPnP
	 * @param gameMode
	 * @param allowCheats
	 * @return true if the server was opened successfully, false otherwise
	 */
    public static boolean openToLan(int port, boolean useUPnP, GameMode gameMode, boolean allowCheats) {
    	// TODO initalize UPnP on the client thread some time while the world is loading
        MinecraftClient client = MinecraftClient.getInstance();
        boolean success = client.getServer().openToLan(gameMode, allowCheats, port);
        if(success && useUPnP) {
	        if (!UPnP.isUPnPAvailable()) {
		        client.player.addChatMessage(new TranslatableText("chat.mcpnp.failed", port, "UPnP service unavailable!"), false);
		        return false;
	        }
	        if (UPnP.isMappedTCP(port)) {
		        client.player.addChatMessage(new TranslatableText("chat.mcpnp.failed", port, "Port is forwarded by another service!"), false);
		        return false;
	        }
	        MCPnP.LOGGER.info("Opening port {} via UPnP", port);

	        if (!UPnP.openPortTCP(port)) {
		        client.player.addChatMessage(new TranslatableText("chat.mcpnp.failed", port, "An unknown error has occurred!"), false);
		        return false;
	        }
	        client.player.addChatMessage(new TranslatableText("chat.mcpnp.success", port), false);
        }

    	return success;
    }

}
