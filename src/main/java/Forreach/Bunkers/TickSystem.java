package Forreach.Bunkers;

import Forreach.Bunkers.generation.SpawnBunker;
import Forreach.Bunkers.generation.SpawnBunkerEnters;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.npc.NPCPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TickSystem extends EntityTickingSystem {
    public void tick(float dt, int index, @Nonnull ArchetypeChunk archetypeChunk, @Nonnull Store store, @Nonnull CommandBuffer commandBuffer) {
        BlockSection blocks = (BlockSection) archetypeChunk.getComponent(index, BlockSection.getComponentType());
        assert blocks != null;
        if (blocks.getTickingBlocksCountCopy() != 0) {
            ChunkSection section = (ChunkSection) archetypeChunk.getComponent(index, ChunkSection.getComponentType());
            assert section != null;
            BlockComponentChunk blockComponentChunk = (BlockComponentChunk) commandBuffer.getComponent(section.getChunkColumnReference(), BlockComponentChunk.getComponentType());
            assert blockComponentChunk != null;
            blocks.forEachTicking(blockComponentChunk, commandBuffer, section.getY(), (blockComponentChunk1, commandBuffer1, localX, localY, localZ, blockId) -> {
                Ref<ChunkStore> blockRef = blockComponentChunk.getEntityReference(ChunkUtil.indexBlockInColumn(localX, localY, localZ));
                if (blockRef == null) {
                    return BlockTickStrategy.IGNORED;
                } else {
                    BunkerSpawnBlock exampleBlock = (BunkerSpawnBlock) commandBuffer1.getComponent(blockRef, BunkerSpawnBlock.getComponentType());
                    if (exampleBlock != null) {
                        WorldChunk worldChunk = (WorldChunk) commandBuffer.getComponent(section.getChunkColumnReference(), WorldChunk.getComponentType());
                        World world = worldChunk.getWorld();
                        int globalX = localX + (worldChunk.getX() * 32);
                        int globalZ = localZ + (worldChunk.getZ() * 32);
                        // Put code here :3
                        new SpawnBunker().MainLoop(world,globalX,globalZ,25);
                        new SpawnBunkerEnters().MainLoop(world,globalX,globalZ);

                        Vector3d position = new Vector3d(globalX,72,localZ);
                        Vector3f rotation = new Vector3f(0, 0, 0);
                        NPCPlugin.get().spawnNPC(world.getEntityStore().getStore(), "MonsterHumanoid", null,
                                position, rotation);

                        return BlockTickStrategy.CONTINUE;
                    } else {
                        return BlockTickStrategy.IGNORED;
                    }
                }
            });
        }
    }
    @Nullable
    public Query getQuery() {
        return Query.and(BlockSection.getComponentType(), ChunkSection.getComponentType());
    }
}
