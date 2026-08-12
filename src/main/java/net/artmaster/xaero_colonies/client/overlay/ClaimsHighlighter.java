package net.artmaster.xaero_colonies.client.overlay;

import net.artmaster.xaero_colonies.utils.ColonyClaimCache;
import net.artmaster.xaero_colonies.utils.ColonyInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import xaero.map.MapProcessor;
import xaero.map.highlight.ChunkHighlighter;
import xaero.map.highlight.DimensionHighlighterHandler;
import xaero.map.highlight.MapRegionHighlightsPreparer;
import xaero.map.region.MapRegion;

import java.util.List;
import java.util.Map;

public class ClaimsHighlighter extends ChunkHighlighter {

    public ClaimsHighlighter() {
        super(false);
    }


    @Override
    public int calculateRegionHash(ResourceKey<Level> key, int regionX, int regionZ) {


        Map<Long, ColonyInfo> claims = ColonyClaimCache.getClaims().get(key);
        if (claims == null)
            return 0;

        int startX = regionX << 5;
        int startZ = regionZ << 5;

        int hash = 1;

        for (int dx = 0; dx < 32; dx++) {
            for (int dz = 0; dz < 32; dz++) {
                long pos = ChunkPos.asLong(startX + dx, startZ + dz);
                ColonyInfo info = claims.get(pos);

                if (info != null) {
                    hash = 31 * hash + Long.hashCode(pos);
                    hash = 31 * hash + info.colonyId();
                    hash = 31 * hash + info.color();
                }
            }
        }

        return hash;
    }


    @Override
    public boolean regionHasHighlights(ResourceKey<Level> key, int regionX, int regionZ) {


        Map<Long, ColonyInfo> claims = ColonyClaimCache.getClaims().get(key);
        if (claims == null || claims.isEmpty())
            return false;

        int startX = regionX << 5;
        int startZ = regionZ << 5;

        for (int dx = 0; dx < 32; dx++) {
            for (int dz = 0; dz < 32; dz++) {
                if (claims.containsKey(ChunkPos.asLong(startX + dx, startZ + dz))) {
                    return true;
                }
            }
        }

        return false;
    }


    @Override
    public boolean chunkIsHighlit(ResourceKey<Level> key, int x, int z) {
        return ColonyClaimCache.isClaimed(key, x, z);
    }

    @Override
    public void addMinimapBlockHighlightTooltips(List<Component> list, ResourceKey<Level> resourceKey, int i, int i1, int i2) {

    }


    @Override
    protected int[] getColors(ResourceKey<Level> key, int x, int z) {


        if (!ColonyClaimCache.isClaimed(key, x, z))
            return null;

        ColonyInfo info = ColonyClaimCache.get(key, x, z);
        if (info == null) return null;

        int rgb = info.color() & 0xFFFFFF;

        int packed =
                ((rgb & 0xFF) << 24) |       
                        ((rgb >> 8 & 0xFF) << 16) |
                        ((rgb >> 16 & 0xFF) << 8);

        int fillOpacity = 120;   // 0-255
        int borderOpacity = 220;

        int fill = (packed & 0xFFFFFF00) | fillOpacity;
        int edge = (packed & 0xFFFFFF00) | borderOpacity;

        this.resultStore[0] = fill;
        this.resultStore[1] = ColonyClaimCache.isClaimed(key, x, z - 1) ? fill : edge;
        this.resultStore[2] = ColonyClaimCache.isClaimed(key, x + 1, z) ? fill : edge;
        this.resultStore[3] = ColonyClaimCache.isClaimed(key, x, z + 1) ? fill : edge;
        this.resultStore[4] = ColonyClaimCache.isClaimed(key, x - 1, z) ? fill : edge;

        return this.resultStore;
    }

    @Override
    public Component getChunkHighlightSubtleTooltip(ResourceKey<Level> key, int x, int z) {

        ColonyInfo info = ColonyClaimCache.get(key, x, z);
        if (info == null)
            return Component.empty();

        return Component.literal(info.name());
    }

    @Override
    public Component getChunkHighlightBluntTooltip(ResourceKey<Level> resourceKey, int i, int i1) {
        return null;
    }
}