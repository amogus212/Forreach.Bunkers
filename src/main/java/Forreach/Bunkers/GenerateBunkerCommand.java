package Forreach.Bunkers;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import Forreach.Bunkers.generation.SpawnBunker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class GenerateBunkerCommand extends AbstractCommand {
    public GenerateBunkerCommand(String name, String description) {
        super(name, description);
    }

    @Nullable
    @Override
        protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
            context.sendMessage(Message.raw("Generating bunker" ));
            Ref<EntityStore> playerRef = context.senderAsPlayerRef();
            Store<EntityStore> store = playerRef.getStore();
            World currentWorld = store.getExternalData().getWorld();

            int output = new SpawnBunker().MainLoop(currentWorld,0,0,25);
            if (output == 0) context.sendMessage(Message.raw("Success" ));
            if (output == 1) context.sendMessage(Message.raw("World load failure.Check the logs for more details" ));
            if (output == 2) context.sendMessage(Message.raw("Prefab load failure.Check the logs for more details" ));
        return CompletableFuture.completedFuture(null);
    }
}
