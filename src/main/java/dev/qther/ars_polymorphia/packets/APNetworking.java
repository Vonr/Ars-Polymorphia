package dev.qther.ars_polymorphia.packets;

import com.hollingsworth.arsnouveau.common.network.AbstractPacket;
import dev.qther.ars_polymorphia.packets.serverbound.PacketResetCraftingResult;
import dev.qther.ars_polymorphia.packets.serverbound.PacketSetRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class APNetworking {
    public static PayloadRegistrar INSTANCE;

    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar reg = event.registrar("1");

        reg.playToServer(PacketResetCraftingResult.TYPE, PacketResetCraftingResult.CODEC, APNetworking::handle);
        reg.playToClient(PacketSetRecipe.TYPE, PacketSetRecipe.CODEC, APNetworking::handle);

        INSTANCE = reg;
    }

    private static <T extends AbstractPacket> void handle(T message, IPayloadContext ctx) {
        if (ctx.flow().getReceptionSide() == LogicalSide.SERVER) {
            handleServer(message, ctx);
        } else {
            // Separate class to avoid loading client code on server.
            ClientMessageHandler.handleClient(message, ctx);
        }
    }

    private static <T extends AbstractPacket> void handleServer(T message, IPayloadContext ctx) {
        MinecraftServer server = ctx.player().getServer();
        message.onServerReceived(server, (ServerPlayer) ctx.player());
    }

    private static class ClientMessageHandler {
        public static <T extends AbstractPacket> void handleClient(T message, IPayloadContext ctx) {
            Minecraft minecraft = Minecraft.getInstance();
            message.onClientReceived(minecraft, minecraft.player);
        }
    }
}
