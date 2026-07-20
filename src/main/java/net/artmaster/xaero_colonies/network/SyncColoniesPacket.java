package net.artmaster.xaero_colonies.network;

import net.artmaster.xaero_colonies.utils.ColonyInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public record SyncColoniesPacket(
        ResourceKey<Level> level,
        Map<Long, ColonyInfo> chunks
) implements CustomPacketPayload {

    public static final ResourceLocation TYPE_ID =
            ResourceLocation.fromNamespaceAndPath("xaero_colonies", "sync_colonies");

    public static final Type<SyncColoniesPacket> TYPE =
            new Type<>(TYPE_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncColoniesPacket> CODEC =
            StreamCodec.of(

                    // WRITE
                    (buf, pkt) -> {
                        buf.writeResourceLocation(pkt.level.location());
                        buf.writeVarInt(pkt.chunks.size());

                        for (var entry : pkt.chunks.entrySet()) {

                            buf.writeLong(entry.getKey());

                            ColonyInfo info = entry.getValue();
                            buf.writeVarInt(info.color());
                            buf.writeUtf(info.name());
                            buf.writeVarInt(info.colonyId());
                        }
                    },

                    // READ
                    buf -> {

                        ResourceKey<Level> level = ResourceKey.create(
                                Registries.DIMENSION,
                                buf.readResourceLocation()
                        );

                        int size = buf.readVarInt();
                        Map<Long, ColonyInfo> map = new HashMap<>();

                        for (int i = 0; i < size; i++) {

                            long chunk = buf.readLong();
                            int color = buf.readVarInt();
                            String name = buf.readUtf();
                            int id = buf.readVarInt();

                            map.put(chunk, new ColonyInfo(color, name, id));
                        }

                        return new SyncColoniesPacket(level, map);
                    }
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}