package net.artmaster.xaero_colonies.utils;

import com.minecolonies.api.MinecoloniesAPIProxy;
import com.minecolonies.core.colony.Colony;
import net.artmaster.xaero_colonies.network.Network;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;
import java.util.Map;
import net.artmaster.xaero_colonies.ModMain;
import xaero.map.WorldMapSession;
import xaero.map.region.MapRegion;

public class ColonyTools {



    public static void updateColonyCash(ServerPlayer player, ServerLevel level) {
            Map<Long, ColonyInfo> chunks = new HashMap<>();

            MinecoloniesAPIProxy.getInstance()
                    .getColonyManager()
                    .getAllColonies()
                    .forEach(colony -> {
                        if (colony.getWorld().equals(level)) {

                            int color = colony.getTeamColonyColor().getColor() & 0xFFFFFF;
                            String name = colony.getName();
                            int id = colony.getID();


                            ColonyInfo info = new ColonyInfo(color, name, id);
                            Colony colonyImpl = (Colony) colony;


                            colonyImpl.getClaimData().keySet().forEach(packed -> {
                                ChunkPos pos = new ChunkPos(packed);
                                chunks.put(
                                        ChunkPos.asLong(pos.x, pos.z),
                                        info
                                );
                            });
//                            colony.getLoadedChunks().forEach(packed -> {
//                                ChunkPos pos = new ChunkPos(packed);
//                                chunks.put(
//                                        ChunkPos.asLong(pos.x, pos.z),
//                                        info
//                                );
//                            });
                            //System.out.println("sent colony data, color: "+color);
                        }
                    });



        ServerScheduler.schedule(10, () -> {
            Network.syncColonies(player, chunks);
        });


        }
}