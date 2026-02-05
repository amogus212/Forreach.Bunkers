package Forreach.Bunkers.generation;

import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.modules.entity.component.*;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.selection.mask.BlockFilter;
import com.hypixel.hytale.server.core.prefab.selection.mask.BlockFilter.FilterType;
import com.hypixel.hytale.server.core.prefab.selection.mask.BlockMask;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.universe.world.World;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;


public class SpawnBunker {

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public BlockSelection[] PrefabsList;
    public BlockSelection[] RoomList;
    public Vector3i[] PositionsList;
    public Vector3i[] RoomPositionsList;
    public int[][] RoomDirectionsList;
    public String[] MandatoryRoomsPaths =
            new String[] {"BunkerRooms/RoomLeadForgeSmall.prefab.json"};//Guarantees at least one room of such type per path
    PrefabStore prefabStore = PrefabStore.get();
    Random random = new Random();
    public int MainLoop(final World world, int MainX, int MainZ,int MaxRooms,boolean GenerateReturnPortal) {
        //Giving numbers between 0 and 99
        //Default seed
        //IntList SeedList = IntList.of(0,1,2,3);
        BlockMask Filter = new BlockMask(
                new BlockFilter[]{new BlockFilter(FilterType.Selection, new String[]{"Poison_source"}, true)});
        if (world == null )
        {
            LOGGER.atSevere().log("ForreachBunkers.FATAL WORLD LOAD FAILURE." +
                    "Please try to be in default world or reinstall the mod.Contact the author if the issue persists");
            return 1;
        }
        /*x/z/-x/-z
        0/0-Corridor x axis
          1/1-Corridor z axis
          2/10-Two way intersection
          3/20-Three way intersection with missing neg Z
          4/21-Three way intersection with missing neg X
          5/22-Three way intersection with missing X
          6/23-Three way intersection with missing Z
          7/3-Four way intersection
         */
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
        GeneratePrefabList(MainX,MainZ,MaxRooms,GenerateReturnPortal);
        int i = 0;
        //Placing corridors
        while (i < PrefabsList.length){
            int finalI = i;
            world.execute(() -> PrefabsList[finalI].place(ComSender,world,PositionsList[finalI],Filter));
            i++;
        }
        //x/z/-x/-z
        //Placing mandatory rooms
        List<Integer> TakenRoomIndexes = new ArrayList<>();
        for (String direction : MandatoryRoomsPaths) {
            BlockSelection Prefab = prefabStore.getAssetPrefabFromAnyPack(direction);
            int SelectedNumber = -1;
            List<Integer> RoomIndexes = new ArrayList<>();
            for (i = 0; i < RoomList.length; i++) {
                RoomIndexes.add(i);
            }Collections.shuffle(RoomIndexes,random);
            for (int k : RoomIndexes) {
                if (!TakenRoomIndexes.contains(k)) {
                    SelectedNumber = k;
                    break;
                }
            }
            if (SelectedNumber == -1){
                LOGGER.atInfo().log("ForreachBunkers.FATAL ROOM GENERATOR FAILURE" +
                        ".There is more mandatory rooms than normal rooms" +
                        ".Honestly i don't know,regenerate the bunker");
                                        break;}
            TakenRoomIndexes.add(SelectedNumber);
            RoomList[SelectedNumber] = Prefab;
        }
        for (i = 0; i < RoomList.length; i++) {
            final BlockSelection room = RoomList[i];
            final Vector3i pos = RoomPositionsList[i];
            final int[] direction = RoomDirectionsList[i];

            world.execute(() -> {
                room.place(ComSender, world, pos, Filter);
                final int x = pos.x;
                final int z = pos.z;
                if (direction[2] == 0) placeWall(world, x - 4, z, true);
                if (direction[0] == 0) placeWall(world, x + 4, z, true);
                if (direction[3] == 0) placeWall(world, x, z - 4, false);
                if (direction[1] == 0) placeWall(world, x, z + 4, false);
            });
        }
        LOGGER.atInfo().log("ForreachBunkers.Successfully set bunker rooms.Room amount:" + RoomList.length);
        return 0;
    }
    private void placeWall(World world, int x, int z, boolean isXAxis) {
        String block = "Bunker_Shielding";
        for (int h = 71; h <= 74; h++) {
            for (int offset = -1; offset <= 1; offset++) {
                if (isXAxis) {
                    world.setBlock(x, h, z + offset, block);
                } else {
                    world.setBlock(x + offset, h, z, block);
                }
            }
        }
    }

    private void GeneratePrefabList(int x, int z,int MaxRooms,boolean GenerateReturnPortal) {
        int[][] KeyGrid = {
                //x/z/-x/-z
                {1, 0, 1, 0}, // 0 - Corridor X
                {0, 1, 0, 1}, // 1 - Corridor Z
                //Two way
                {0, 0, 1, 1}, // 2
                {1, 1, 0, 0}, // 3/
                {1, 0, 0, 1}, // 4/
                {0, 1, 1, 0}, // 5
                //T-Intersections
                {0, 1, 1, 1}, // 6
                {1, 0, 1, 1}, // 7
                {1, 1, 0, 1}, // 8
                {1, 1, 1, 0}, // 9
                //4-way
                {1, 1, 1, 1}  // 10
        };

        List<BlockSelection> PrefabList = new ArrayList<>();
        List<BlockSelection> RoomsList = new ArrayList<>();
        List<Vector3i> PositionList = new ArrayList<>();
        List<int[]> RoomDirectionList = new ArrayList<>();
        List<Vector3i> RoomsPositionList = new ArrayList<>();
        Set<String> occupiedPositions = new HashSet<>();

        Map<String, Integer> boardLayout = new HashMap<>(); // IndexInKeyMap for Corridor, -1 for Room

        int VectorsLeft = random.nextInt(15, MaxRooms);
        int PreviousDirection = -1;

        //0:+X, 1:+Z, 2:-X, 3:-Z
        int[] dx = {9, 0, -9, 0};
        int[] dz = {0, 9, 0, -9};

        while (VectorsLeft > 0) {
            List<Integer> possibleDirections = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
            Collections.shuffle(possibleDirections, random);
            int nextDir = -1;
            int nextX = x, nextZ = z;

            for (int dir : possibleDirections) {
                int testX = x + dx[dir];
                int testZ = z + dz[dir];
                if (!occupiedPositions.contains(testX + "," + testZ)) {
                    nextX = testX; nextZ = testZ; nextDir = dir;
                    break;
                }
            }

            int[] req = {0, 0, 0, 0};
            if (PreviousDirection >= 0) req[Math.floorMod(PreviousDirection + 2, 4)] = 1;
            if (nextDir != -1) req[nextDir] = 1;

            List<Integer> valid = new ArrayList<>();
            for (int i = 0; i < KeyGrid.length; i++) {
                boolean fits = true;
                for (int j = 0; j < 4; j++) {
                    if (req[j] == 1 && KeyGrid[i][j] == 0) { fits = false; break; } // must have
                    if (req[j] == 2 && KeyGrid[i][j] == 1) { fits = false; break; } // must NOT have
                }
                if (fits) valid.add(i);
            }
            int sel = valid.get(random.nextInt(valid.size()));
            boardLayout.put(x + "," + z, sel);
            occupiedPositions.add(x + "," + z);
            PrefabList.add(FindBunkerRoom(sel,new int[]{0,0,0,0}));
            PositionList.add(new Vector3i(x, 71, z));
            //> [num] - Chance of NOT getting a room
                int[] chosenDoors = KeyGrid[sel];
                for (int side = 0; side < 4; side++) {
                    if (chosenDoors[side] == 1) {
                        int rx = x + dx[side], rz = z + dz[side];
                        boolean isPath = (rx == nextX && rz == nextZ) ||
                                (PreviousDirection >= 0 && side == Math.floorMod(PreviousDirection + 2, 4));

                        if (!isPath && !occupiedPositions.contains(rx + "," + rz)) {
                            RoomsList.add(FindBunkerRoom(20,new int[]{0,0,0,0}));
                            RoomsPositionList.add(new Vector3i(rx, 71, rz));
                            occupiedPositions.add(rx + "," + rz);
                            boardLayout.put(rx + "," + rz, -1);
                        }
                    }
                }

            if (nextDir == -1) break;
            x = nextX; z = nextZ; PreviousDirection = nextDir; VectorsLeft--;
        }

        //Setting rooms
        int i = -1;

        for (Vector3i roomPos : RoomsPositionList) {
            i += 1;
            //Checking which doors should be opened
            int[] openFaces = new int[4];
            for (int d = 0; d < 4; d++) {
                String neighborKey = (roomPos.x + dx[d]) + "," + (roomPos.z + dz[d]);

                if (boardLayout.containsKey(neighborKey)) {
                    int neighborVal = boardLayout.get(neighborKey);
                    if (neighborVal != -1) {
                        //does corridor have an open door for me
                        if (KeyGrid[neighborVal][Math.floorMod(d + 2, 4)] == 1) {
                            openFaces[d] = 1;
                        }
                    }else{
                        //If it's a room,open the door
                        openFaces[d] = 1;
                    }
                }
            }
            RoomDirectionList.add(openFaces);
            BlockSelection room = FindBunkerRoom(30,openFaces);
            LOGGER.atInfo().log((room != null) + "/" + Arrays.toString(openFaces));
            if (room != null){
            RoomsList.set(i,room);}
        }
        List<BlockSelection> NecPrefabList = ListPrefabsInDirectory("NecessaryRooms");
        List<BlockSelection> AdditionalRooms = ListPrefabsInDirectory("AdditionallRooms");
        Set<Integer> usedIndices = new HashSet<>();
        i = -1;
        if (GenerateReturnPortal && AdditionalRooms != null){
            RoomsList.set(0,AdditionalRooms.get(0));
            usedIndices.add(0);
        }
        for (BlockSelection prefab : NecPrefabList){
            i ++;
            int randomIndex;
            do {
                randomIndex = random.nextInt(RoomsList.toArray().length);
            } while (usedIndices.contains(randomIndex));
            RoomsList.set(randomIndex,prefab);
            usedIndices.add(randomIndex);
        }

        this.RoomDirectionsList = RoomDirectionList.toArray(new int[0][]);
        this.RoomList = RoomsList.toArray(new BlockSelection[0]);
        this.PositionsList = PositionList.toArray(new Vector3i[0]);
        this.PrefabsList = PrefabList.toArray(new BlockSelection[0]);
        this.RoomPositionsList = RoomsPositionList.toArray(new Vector3i[0]);
    }

    public BlockSelection FindBunkerRoom(int RoomID,int[] keys){
        // Load from different locations (relative to respective base paths)
        //Use Prefabs with y+1
        BlockSelection BunkerCorridorXPrefab = prefabStore.getAssetPrefabFromAnyPack("Corridors/BunkerCorridor.prefab.json");
        BlockSelection BunkerCorridorZPrefab = prefabStore.getAssetPrefabFromAnyPack("Corridors/BunkerCorridorZAxis.prefab.json");

        BlockSelection BunkerTwoIntersectionPrefabNegXNegZ = prefabStore.getAssetPrefabFromAnyPack("Intersections/TwoWayInter.prefab.json");
        BlockSelection BunkerTwoIntersectionPrefabXZ = prefabStore.getAssetPrefabFromAnyPack("Intersections/TwoWayInter1.prefab.json");
        BlockSelection BunkerTwoIntersectionPrefabXNegZ = prefabStore.getAssetPrefabFromAnyPack("Intersections/TwoWayInter2.prefab.json");
        BlockSelection BunkerTwoIntersectionPrefabNegXZ = prefabStore.getAssetPrefabFromAnyPack("Intersections/TwoWayInter3.prefab.json");

        BlockSelection BunkerThreeIntersectionPrefabNegX = prefabStore.getAssetPrefabFromAnyPack("Intersections/ThreeWayInter.prefab.json");
        BlockSelection BunkerThreeIntersectionPrefabNegZ = prefabStore.getAssetPrefabFromAnyPack("Intersections/ThreeWayInter1.prefab.json");
        BlockSelection BunkerThreeIntersectionPrefabX = prefabStore.getAssetPrefabFromAnyPack("Intersections/ThreeWayInter2.prefab.json");
        BlockSelection BunkerThreeIntersectionPrefabZ = prefabStore.getAssetPrefabFromAnyPack("Intersections/ThreeWayInter3.prefab.json");

        BlockSelection BunkerFourIntersectionPrefab = prefabStore.getAssetPrefabFromAnyPack("Intersections/FourWayInter.prefab.json");

        //Default/Fallback room
        BlockSelection BunkerRoomSmallEmptyPrefab = prefabStore.getAssetPrefabFromAnyPack("Rooms/RoomEmpty.prefab.json");





        List<BlockSelection> PrefabList = ListPrefabsInDirectory("Rooms");
        AssetPack pack = AssetModule.get().getAssetPack("Forreach:ForreachBunkers");
        Path prefabsPath = prefabStore.getAssetPrefabsPathForPack(pack);
        List<BlockSelection> RoomsWithKeysPrefabList = ListPrefabsInDirectory("RoomsWithKeys");

        int[][] RoomKeys = new int[0][0];
        try (var lines = Files.lines(prefabsPath.resolve("Keys.txt"))) {
            RoomKeys = lines
                    .filter(line -> !line.trim().isEmpty())
                    .map(line -> Arrays.stream(line.trim().split("\\s+"))
                            .mapToInt(Integer::parseInt)
                            .toArray())
                    .toArray(int[][]::new);
        }catch (IOException e) {
            LOGGER.atSevere().withCause(e).log("ForreachBunkers.FATAL ROOM PREFAB LOAD FAILURE" +
                    ".Failed to find or list the key array reinstall the mod.Contact the author if the issue persists");
        }


        if (BunkerCorridorXPrefab == null || BunkerTwoIntersectionPrefabXZ == null || BunkerTwoIntersectionPrefabXNegZ == null || BunkerTwoIntersectionPrefabNegXZ == null || BunkerTwoIntersectionPrefabNegXNegZ == null
                || BunkerThreeIntersectionPrefabNegX == null || BunkerThreeIntersectionPrefabNegZ == null|| BunkerThreeIntersectionPrefabX == null|| BunkerThreeIntersectionPrefabZ == null
                || BunkerFourIntersectionPrefab == null)
        {
            LOGGER.atSevere().log("ForreachBunkers.FATAL PREFAB LOAD FAILURE." +
                    "Please check the prefab files in the mod or reinstall the mod.Contact the author if the issue persists");
            return null;
        }




        BlockSelection PlacedPrefab = BunkerCorridorXPrefab;
        int Returning = switch (RoomID) {
            case (0) -> 0;
            case (1) -> {
                PlacedPrefab=BunkerCorridorZPrefab;
                yield 1;
            }
            case (2) -> {
                PlacedPrefab=BunkerTwoIntersectionPrefabNegXNegZ;
                yield 10;
            }
            case (3) -> {
                PlacedPrefab=BunkerTwoIntersectionPrefabXZ;
                yield 11;
            }
            case (4) -> {
                PlacedPrefab=BunkerTwoIntersectionPrefabXNegZ;
                yield 12;
            }
            case (5) -> {
                PlacedPrefab=BunkerTwoIntersectionPrefabNegXZ;
                yield 13;
            }
            case (6) -> {
                PlacedPrefab=BunkerThreeIntersectionPrefabX;
                yield 20;
            }
            case (7) -> {
                PlacedPrefab=BunkerThreeIntersectionPrefabZ;
                yield 21;
            }
            case (8) -> {
                PlacedPrefab=BunkerThreeIntersectionPrefabNegX;
                yield 22;
            }
            case (9) -> {
                PlacedPrefab= BunkerThreeIntersectionPrefabNegZ;
                yield 23;
            }
            case (10) -> {
                PlacedPrefab=BunkerFourIntersectionPrefab;
                yield 3;
            }
            //Random room
            case (20) -> {
               PlacedPrefab= PrefabList.get(random.nextInt(PrefabList.toArray().length));
                yield 3;
            }
            //Empty
            case (21) -> {
                PlacedPrefab=BunkerRoomSmallEmptyPrefab;
                yield 3;
            }
            //3# - RandomWithKeys
            case (30) -> {
                List<Integer> valid = new ArrayList<>();
                for (int i = 0; i < RoomKeys.length; i++) {
                    boolean fits = true;
                    for (int j = 0; j < 4; j++) {
                        if (RoomKeys[i][j] == 1 && keys[j] == 0) { fits = false; break; }
                        if (RoomKeys[i][j] == 2 && keys[j] == 1) { fits = false; break; }
                    }
                    if (fits) {valid.add(i);}
                }
                if (!valid.isEmpty()){
                    int randomIndex = valid.get(random.nextInt(valid.size()));
                    PlacedPrefab = RoomsWithKeysPrefabList.get(randomIndex);}else{
                    PlacedPrefab=null;
                }
                yield 3;
            }
            default -> {
                LOGGER.atSevere().log("ForreachBunkers.INTERNAL ERROR.Room_ID invalid,please check file integrity or downgrade.Contact the author if the issue persists");
                yield 0;
            }

        };
        return PlacedPrefab;
    }
    public List<BlockSelection> ListPrefabsInDirectory(String directoryName){
        List<BlockSelection> PrefabList = new ArrayList<>();
        List<Path> fileList;
        AssetPack pack = AssetModule.get().getAssetPack("Forreach:ForreachBunkers");
        Path prefabsPath = prefabStore.getAssetPrefabsPathForPack(pack);
        try (Stream<Path> stream = Files.list(prefabsPath.resolve(directoryName))) {
            fileList = stream
                    .toList();
        }
        catch (IOException e) {
            LOGGER.atSevere().log("ForreachBunkers.FATAL ROOM PREFAB LOAD FAILURE" +
                    ".Failed to find or list the directory.Contact the author or reinstall the mod");
            return null;
        }
        for (Path paths : fileList){
            PrefabList.add(prefabStore.getPrefab(paths));
        }
        return PrefabList;
    }
}
