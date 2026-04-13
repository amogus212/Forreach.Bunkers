package Forreach.Bunkers;

import Forreach.Bunkers.generation.SpawnBunker;
import Forreach.Bunkers.generation.SpawnBunkerEnters;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.modules.entity.component.*;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.npc.NPCPlugin;
import java.util.Random;


public class PlayerJoinedEvent {
    public static void onPlayerReady(PlayerReadyEvent event) {
       new PlayerJoinedEvent().TryToGenerateBunker(event);
       new PlayerJoinedEvent().TryToGenerateBunkerInPortalWorld(event);
        //event.getPlayer().sendMessage(Message.raw("Something went wrong if you see this message.World name:" + event.getPlayer().getWorld().getName()));
    }

    //TODO:Redo it later
    //For now its storing 1 uid where it will not generate bunker if any player enters in the same one
    private void TryToGenerateBunkerInPortalWorld(PlayerReadyEvent event){
        assert event.getPlayer().getWorld() != null;
        if (event.getPlayer().getWorld().getName().equals("default")){return;}
        //Ignore if the instance is not a toxic field
        if (!event.getPlayer().getWorld().getName().split("-")[1].equals("ToxicField") ){return;}
        //Checking if the UID matches the one in buffer
        if (!event.getPlayer().getWorld().getName().split("-")[2].equals(ForreachBunkers.StaticConfig.get().getInstanceUID())){
            World world = event.getPlayer().getWorld();
            if (new SpawnBunker().MainLoop(world, 10, 10,40) != 0) {
                event.getPlayer().sendMessage(Message.raw("Something went wrong during generation of bunker.Check the logs and/or contact the author"));
            }
            if (new SpawnBunkerEnters().MainLoop(world, 10, 10) != 0) {
                event.getPlayer().sendMessage(Message.raw("Something went wrong during generation of bunker enters.Check the logs and/or contact the author"));
            }
            Vector3d position = new Vector3d(0,72, 0);
            // Define the initial rotation (facing direction) for the NPC
            Vector3f rotation = new Vector3f(0, 0, 0);
            // Use the NPCPlugin helper to spawn the NPC.
            NPCPlugin.get().spawnNPC(event.getPlayer().getWorld().getEntityStore().getStore(), "MonsterHumanoid", null,
                    position, rotation);

            ForreachBunkers.StaticConfig.get().setInstanceUID(event.getPlayer().getWorld().getName().split("-")[2]);
            ForreachBunkers.StaticConfig.save();
        }
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
                if (new SpawnBunker().MainLoop(world, xGrid[i], zGrid[i],25) != 0) {
                    event.getPlayer().sendMessage(Message.raw("Something went wrong during generation of bunker.Check the logs and/or contact the author"));
                }
                if (new SpawnBunkerEnters().MainLoop(world, xGrid[i], zGrid[i]) != 0) {
                    event.getPlayer().sendMessage(Message.raw("Something went wrong during generation of bunker enters.Check the logs and/or contact the author"));
                }
                Vector3d position = new Vector3d(xGrid[i],72, zGrid[i]);
                // Define the initial rotation (facing direction) for the NPC
                Vector3f rotation = new Vector3f(0, 0, 0);
                // Use the NPCPlugin helper to spawn the NPC.
                NPCPlugin.get().spawnNPC(event.getPlayer().getWorld().getEntityStore().getStore(), "MonsterHumanoid", null,
                        position, rotation);
            }

            ForreachBunkers.StaticConfig.get().setBunkerGenerated(true);
            ForreachBunkers.StaticConfig.get().setBunkerPosition(new int[] {xGrid[0],zGrid[0]});
            ForreachBunkers.StaticConfig.save();
        }
    }

}
