package io.github.theglitch76.mcpnp.mixin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import io.github.theglitch76.mcpnp.MCPnP;
import io.github.theglitch76.mcpnp.gui.widget.PortWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.GameMode;

@Mixin(OpenToLanScreen.class)
public class MixinOpenToLanScreen extends Screen {


    @Shadow
    private boolean allowCommands;
    @Unique
    private PortWidget portWidget;
    @Unique
    private Properties mcpnpProperties;
    @Unique
    private File mcpnpPropertiesFile;

    @Unique
    private int port = -1;

    @Shadow
    private String gameMode;

    public MixinOpenToLanScreen(Text title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "<init>")
    private void createMcpnpProperties(Screen screen, CallbackInfo ci) {
    	// TODO use Paths
	    // this.client hasn't been initialized yet
	    MinecraftClient client = MinecraftClient.getInstance();
        File worldDir = client.getServer().getSavePath(WorldSavePath.ROOT).normalize().toFile();

        mcpnpPropertiesFile = new File(worldDir.getPath() + "/mcpnp.properties");
        this.mcpnpProperties = new Properties();
        PlayerManager manager = client.getServer().getPlayerManager();

        try {
            if (!mcpnpPropertiesFile.exists()) {
                mcpnpPropertiesFile.createNewFile();
                mcpnpProperties.setProperty("port", Integer.toString(NetworkUtils.findLocalPort()));
                mcpnpProperties.setProperty("enable_cheats", Boolean.toString(manager.areCheatsAllowed()));
                mcpnpProperties.setProperty("gamemode", client.getServer().getSaveProperties().getGameMode().getName());
            } else {
                mcpnpProperties.load(new FileInputStream(mcpnpPropertiesFile));
            }

            //Finally, initalize all of our properties.
            this.port = Integer.parseInt(this.mcpnpProperties.getProperty("port"));

            String gameMode = this.mcpnpProperties.getProperty("gamemode");
            //If the gamemode is NOT survival, creative, adventure, or spectator assume survival
            if (!(gameMode.equals("survival") || gameMode.equals("creative") || gameMode.equals("adventure") || gameMode.equals("spectator"))) {
                this.gameMode = "survival";
            } else {
                this.gameMode = gameMode;
            }
            //TODO invalid property parses to "false" instead of throwing an exception
            this.allowCommands = Boolean.parseBoolean(this.mcpnpProperties.getProperty("enable_cheats"));

         //Nothing we can do about this error, log and fail gracefully.
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        //Just generate a new port and log error.
        } catch (NumberFormatException ex) {
            MCPnP.LOGGER.warn("Unable to parse port. %s", ex);
            mcpnpProperties.setProperty("port", Integer.toString(NetworkUtils.findLocalPort()));
            this.port = Integer.parseInt(mcpnpProperties.getProperty("port"));
        }
    }

    @Inject(at = @At("RETURN"), method = "init")
    public void addPortSelection(CallbackInfo ci) {
        portWidget = this.addButton(new PortWidget(this.textRenderer, this.width / 2 - 155, 125, 150, 20, port, new TranslatableText("mcpnp.port")));

        this.buttons.remove(0);
        this.children.remove(0);
        this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, new TranslatableText("lanServer.start"), (buttonWidget_1) -> {

            int port = Integer.parseInt(portWidget.getText());
            if(port < 1024 /*system reserve*/ || port > 65536) {
                return; //TODO add warning for port out of range
            }
            //Update any changed settings
            if(port != this.port) {
                mcpnpProperties.setProperty("port", Integer.toString(port));
            }
            if(!this.gameMode.equals(mcpnpProperties.get("gamemode"))) {
                mcpnpProperties.setProperty("gamemode", this.gameMode);
            }
            if(this.allowCommands != Boolean.parseBoolean(mcpnpProperties.getProperty("enable_cheats"))) {
                mcpnpProperties.setProperty("enable_cheats", Boolean.toString(this.allowCommands));
            }
            //Close the screen
            this.client.openScreen(null);
            //Output text
            TranslatableText text_2;
            if (this.client.getServer().openToLan(GameMode.byName(this.gameMode), this.allowCommands, port)) {
                text_2 = new TranslatableText("commands.publish.started", port);
            } else {
                text_2 = new TranslatableText("commands.publish.failed");
            }
            this.client.inGameHud.getChatHud().addMessage(text_2);
            try {
                this.mcpnpProperties.store(new FileOutputStream(mcpnpPropertiesFile), "LAN world settings");
            } catch (IOException ex) {
                MCPnP.LOGGER.warn("Unable to store properties!");
            }

        }));
    }


}
