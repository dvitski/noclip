package dev.andante.noclip.impl;

import dev.andante.noclip.api.NoClip;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * @param canClip ignored by the server
 */
public record ClippingUpdatePacket(boolean clipping, boolean canClip) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, ClippingUpdatePacket> CODEC = CustomPayload.codecOf(ClippingUpdatePacket::write, ClippingUpdatePacket::new);
    public static final Id<ClippingUpdatePacket> ID = new CustomPayload.Id<>(new Identifier(NoClip.MOD_ID, "update"));

    private ClippingUpdatePacket(PacketByteBuf buf) {
        this(buf.readBoolean(), buf.readBoolean());
    }

    private void write(PacketByteBuf buf) {
        buf.writeBoolean(this.clipping);
        buf.writeBoolean(this.canClip);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
