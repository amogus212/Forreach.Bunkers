package Forreach.Bunkers;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;

import javax.annotation.Nonnull;

//Main thread
public class ForreachBunkers extends JavaPlugin {

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public static Config<BunkerConfig> StaticConfig;
    public static ForreachBunkers StaticPlugin;
    private ComponentType exampleBlockComponentType;

    public ForreachBunkers(@Nonnull JavaPluginInit init) {
        super(init);
        StaticConfig = this.withConfig("BunkerConfigFile", BunkerConfig.CODEC);
        StaticPlugin = this;
    }
    @Override
    protected void setup() {

        StaticConfig.save(); // Ensures the config file is created if it doesn't exist
        this.exampleBlockComponentType = this.getChunkStoreRegistry().registerComponent(BunkerSpawnBlock.class, "BunkerSpawnBlock", BunkerSpawnBlock.CODEC);
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, PlayerJoinedEvent::onPlayerReady);
        this.getCommandRegistry().registerCommand(new GenerateBunkerEntersCommand("GenerateEnters", "Creating enter points for bunkers"));
        this.getCommandRegistry().registerCommand(new GenerateBunkerCommand("GenerateBunker", "Creating bunker interior"));
        this.getCommandRegistry().registerCommand(new FindBunkerCommand("FindBunker", "Finding the position of one of the bunkers"));
        LOGGER.atInfo().log("Loaded ToxicFields.java");
    }
    @Override
    protected void start() {
        this.getChunkStoreRegistry().registerSystem(new Initializer());
        this.getChunkStoreRegistry().registerSystem(new TickSystem());
    }

    public ComponentType GetBunkerSpawnBlockComponentType() {
        return this.exampleBlockComponentType;
    }
}