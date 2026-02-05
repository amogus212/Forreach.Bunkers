package Forreach.Bunkers;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class FindBunkerCommand extends AbstractCommand {
    public FindBunkerCommand(String name, String description) {
        super(name, description);
    }

    @Nullable
    @Override
        protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
            context.sendMessage(Message.raw("Bunker position:" + Arrays.toString(ForreachBunkers.StaticConfig.get().getBunkerPosition())));
        return CompletableFuture.completedFuture(null);
    }
}
