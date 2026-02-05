package Forreach.Bunkers;

import Forreach.Bunkers.generation.SpawnBunker;
import Forreach.Bunkers.generation.SpawnBunkerEnters;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.modules.entity.component.*;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.npc.INonPlayerCharacter;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import it.unimi.dsi.fastutil.Pair;

import java.util.Random;


public class PlayerJoinedEvent {
    public static void onPlayerReady(PlayerReadyEvent event) {
       new PlayerJoinedEvent().TryToGenerateBunker(event);
        event.getPlayer().sendMessage(Message.raw("Something went wrong if you see this message.Check the logs"));
    }
    private void TryToGenerateBunker(PlayerReadyEvent event){
        if (!ForreachBunkers.StaticConfig.get().getBunkerGenerated()){
            Random random = new Random();
            int[] xGrid= {random.nextInt(-1000,1001),
                          random.nextInt(-1000,1001),
                    random.nextInt(-1000,1001),
                    random.nextInt(-1000,1001),
                    random.nextInt(-1000,1001)};
            int[] zGrid= {random.nextInt(-1000,1001),
                    random.nextInt(-1000,1001),
                    random.nextInt(-1000,1001),
                    random.nextInt(-1000,1001),
                    random.nextInt(-1000,1001)};
            //event.getPlayer().sendMessage(Message.raw("h:"+ Arrays.toString(xGrid) +"/" + Arrays.toString(zGrid)));
            for (int i = 0; i < 4; i++) {
                World world = event.getPlayer().getWorld();
                assert world != null;
                if (new SpawnBunker().MainLoop(world, xGrid[i], zGrid[i],25,false) != 0) {
                    event.getPlayer().sendMessage(Message.raw("Something went wrong during generation of bunker.Check the logs and/or contact the author"));
                }
                if (new SpawnBunkerEnters().MainLoop(world, xGrid[i], zGrid[i]) != 0) {
                    event.getPlayer().sendMessage(Message.raw("Something went wrong during generation of bunker enters.Check the logs and/or contact the author"));
                }
                Vector3d position = new Vector3d(xGrid[i],72, zGrid[i]);
                // Define the initial rotation (facing direction) for the NPC
                Vector3f rotation = new Vector3f(0, 0, 0);
                // Use the NPCPlugin helper to spawn the NPC.
                Pair<Ref<EntityStore>, INonPlayerCharacter> result = NPCPlugin.get().spawnNPC(event.getPlayer().getWorld().getEntityStore().getStore(), "MonsterHumanoid", null,
                        position, rotation);
            }

            ForreachBunkers.StaticConfig.get().setBunkerGenerated(true);
            ForreachBunkers.StaticConfig.get().setBunkerPosition(new int[] {xGrid[0],zGrid[0]});
            ForreachBunkers.StaticConfig.save();
        }
    }

}
