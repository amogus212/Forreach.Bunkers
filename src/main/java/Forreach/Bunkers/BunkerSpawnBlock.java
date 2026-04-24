package Forreach.Bunkers;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

import javax.annotation.Nullable;

public class BunkerSpawnBlock implements Component<ChunkStore> {
    public static final BuilderCodec CODEC = BuilderCodec.builder(BunkerSpawnBlock.class, BunkerSpawnBlock::new).build();
    public BunkerSpawnBlock() { }
    public static ComponentType getComponentType() {
        return ForreachBunkers.StaticPlugin.GetBunkerSpawnBlockComponentType();
    }
    @Nullable
    public Component<ChunkStore> clone() {
        return new BunkerSpawnBlock();
    }
}