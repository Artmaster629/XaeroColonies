package net.artmaster.xaero_colonies.utils;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class ColonyClaimCache {

    private static final Map<ResourceKey<Level>, Map<Long, ColonyInfo>> CLAIMS = new HashMap<>();

    public static void setClaims(ResourceKey<Level> level, Map<Long, ColonyInfo> chunks) {
        CLAIMS.put(level, chunks);
    }

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