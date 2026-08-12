package net.artmaster.xaero_colonies.utils;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import xaero.map.MapProcessor;
import xaero.map.WorldMapSession;
import xaero.map.region.LayeredRegionManager;
import xaero.map.region.LeveledRegion;
import xaero.map.region.MapRegion;
import xaero.map.region.MapTileChunk;
import xaero.map.world.MapDimension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@EventBusSubscriber
public class ColonyClaimCache {

    @SubscribeEvent
    public static void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        disconnecting = true;
    }
    @SubscribeEvent
    public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        disconnecting = false;
    }

    private static final Map<ResourceKey<Level>, Map<Long, ColonyInfo>> CLAIMS = new HashMap<>();

    private static boolean disconnecting = false;

    public static void setClaims(ResourceKey<Level> level, Map<Long, ColonyInfo> chunks) {
        CLAIMS.put(level, chunks);

        if (disconnecting) {
            return;
        }

        WorldMapSession session = WorldMapSession.getCurrentSession();
        if (session != null) {
            MapProcessor processor = session.getMapProcessor();
            MapDimension dimension =
                    processor.getMapWorld().getDimension(level);
            if (dimension == null) {
                return;
            }

//            System.out.println(
//                    "Region detection complete = " +
//                            processor.getMapSaveLoad().isRegionDetectionComplete()
//            );




            for (long packed : chunks.keySet()) {
                ChunkPos pos = new ChunkPos(packed);



                LayeredRegionManager regions = dimension.getLayeredMapRegions();

//                for (LeveledRegion<?> region : regions.getUnsyncedSet()) {
//                }
//
//                for (LeveledRegion<?> region : regions.getLoadedListUnsynced()) {
//                }

                List<LeveledRegion<?>> loadedRegions =
                        new ArrayList<>(regions.getLoadedListUnsynced());

                for (LeveledRegion<?> leveledRegion : loadedRegions) {
                    if (leveledRegion instanceof MapRegion region) {
                        processor.getMapRegionHighlightsPreparer()
                                .prepare(region, false);

                        region.requestRefresh(processor, true);
                    }
                }
            }
        }

    }

//    public static void setClaims(ResourceKey<Level> level, Map<Long, ColonyInfo> chunks) {
//        CLAIMS.put(level, chunks);
//        System.out.println("set claims");
//        WorldMapSession session = WorldMapSession.getCurrentSession();
//        if (session != null) {
//            MapProcessor processor = session.getMapProcessor();
//
//            for (long packed : chunks.keySet()) {
//                ChunkPos pos = new ChunkPos(packed);
//
//                MapTileChunk tileChunk = processor.getMapChunk(processor.getCurrentCaveLayer(), pos.x, pos.z);
//                System.out.println(tileChunk);
//
//
//                if (tileChunk != null) {
//                    System.out.println("highlight preparer");
//                    var mapRegionHighlightsPreparer = processor.getMapRegionHighlightsPreparer();
//                    processor.addToRefresh(tileChunk.getInRegion(), true);
//                    mapRegionHighlightsPreparer.prepare(tileChunk.getInRegion(), false);
//                    //processor.addToRefresh(tileChunk.getInRegion(), true);
//                }
//            }
//        }
//    }

    public static boolean isClaimed(ResourceKey<Level> level, int chunkX, int chunkZ) {
        Map<Long, ColonyInfo> map = CLAIMS.get(level);
        if (map == null) return false;

        return map.containsKey(ChunkPos.asLong(chunkX, chunkZ));
    }

    public static ColonyInfo get(ResourceKey<Level> level, int chunkX, int chunkZ) {
        Map<Long, ColonyInfo> map = CLAIMS.get(level);
        if (map == null) return null;

        return map.get(ChunkPos.asLong(chunkX, chunkZ));
    }

    public static Map<ResourceKey<Level>, Map<Long, ColonyInfo>> getClaims() {
        return CLAIMS;
    }

    public static void clear() {
        CLAIMS.clear();
    }
}