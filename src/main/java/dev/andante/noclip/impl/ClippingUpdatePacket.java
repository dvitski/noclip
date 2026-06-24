package dev.andante.noclip.impl;

import dev.andante.noclip.api.NoClip;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * @param canClip ignored by the server
 */
public record ClippingUpdatePacket(boolean clipping, boolean canClip) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, ClippingUpdatePacket> CODEC = CustomPacketPayload.codec(ClippingUpdatePacket::write, ClippingUpdatePacket::new);
    public static final Type<ClippingUpdatePacket> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(NoClip.MOD_ID, "update"));

    private ClippingUpdatePacket(FriendlyByteBuf buf) {
        this(buf.readBoolean(), buf.readBoolean());
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeBoolean(this.clipping);
        buf.writeBoolean(this.canClip);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
