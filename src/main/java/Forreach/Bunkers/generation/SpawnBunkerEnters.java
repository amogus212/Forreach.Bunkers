package Forreach.Bunkers.generation;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.selection.mask.BlockFilter;
import com.hypixel.hytale.server.core.prefab.selection.mask.BlockFilter.FilterType;
import com.hypixel.hytale.server.core.prefab.selection.mask.BlockMask;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.universe.world.World;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.UUID;


public class SpawnBunkerEnters {

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    PrefabStore prefabStore = PrefabStore.get();
    public int MainLoop(final World world,int x,int z) {
        if (world == null )
        {
            LOGGER.atSevere().log("ForreachBunkers.FATAL WORLD LOAD FAILURE." +
                    "Please try to be in default world or reinstall the mod.Contact the author if the issue persists");
            return 1;
        }
        BlockMask Filter = new BlockMask(
                new BlockFilter[]{new BlockFilter(FilterType.Selection, new String[]{"Poison_source"}, true)});

        // Load from different locations (relative to respective base paths)
        BlockSelection BunkerEnterTop = prefabStore.getAssetPrefabFromAnyPack("BunkerEnterParts/BunkerEnterTop.prefab.json");
        BlockSelection BunkerEnterMiddle = prefabStore.getAssetPrefabFromAnyPack("BunkerEnterParts/BunkerEnterMiddle.prefab.json");

        if (BunkerEnterTop == null || BunkerEnterMiddle == null)
        {
            LOGGER.atSevere().log("ForreachBunkers.FATAL PREFAB LOAD FAILURE." +
                    "Please check the prefab files in the mod or reinstall the mod.Contact the author if the issue persists");
            return 2;
        }


        boolean Completed = false;
        int i = 250;
        CommandSender ComSender = new CommandSender() {

            @Override

            public String getDisplayName() {

                return null;

            }



            @Override

            public UUID getUuid() {

                return null;

            }



            @Override

            public boolean hasPermission(@NonNullDecl String s) {

                return false;

            }



            @Override

            public boolean hasPermission(@NonNullDecl String s, boolean b) {

                return false;

            }



            @Override

            public void sendMessage(@NonNullDecl Message message) {



            }

        };
        //Finds the highest possible spot
        while (i > 60) {
            int blockId = world.getBlock(x, i, z);

            if (blockId != 0) {
                final int topY = i;

                world.execute(() -> BunkerEnterTop.place(ComSender, world, new Vector3i(x, topY, z), Filter));

                for (int currentY = topY - 1; currentY > 73; currentY--) {
                    final int tempY = currentY;
                    world.execute(() -> BunkerEnterMiddle.place(ComSender, world, new Vector3i(x, tempY, z), Filter));
                }
                Completed = true;
                break;
            }
            i--;
        }
        if (Completed){
            LOGGER.atInfo().log("ForreachBunkers.Successfully set the bunker.");
            return 0;//Successful
        }else{
            LOGGER.atWarning().log("ForreachBunkers.Failed to find a point to set ");
            return 3;//Failed to find point to place
        }
    }
}
