package dev.qther.ars_polymorphia.packets.serverbound;

import com.hollingsworth.arsnouveau.client.container.CraftingTerminalMenu;
import com.hollingsworth.arsnouveau.common.block.tile.TransientCustomContainer;
import com.hollingsworth.arsnouveau.common.network.AbstractPacket;
import com.illusivesoulworks.polymorph.api.PolymorphApi;
import dev.qther.ars_polymorphia.ArsPolymorphia;
import dev.qther.ars_polymorphia.mixin.CraftingLecternTileAccessor;
import dev.qther.ars_polymorphia.mixin.CraftingTerminalMenuAccessor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeType;

public class PacketResetCraftingResult extends AbstractPacket {
    public static final PacketResetCraftingResult INSTANCE = new PacketResetCraftingResult();

    public static final Type<PacketResetCraftingResult> TYPE = new Type<>(ArsPolymorphia.prefix("reset_crafting_result"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketResetCraftingResult> CODEC = StreamCodec.unit(PacketResetCraftingResult.INSTANCE);

    private PacketResetCraftingResult() {
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {
        if (player == null || !(player.containerMenu instanceof CraftingTerminalMenu menu)) {
            return;
        }

        var lectern = ((CraftingTerminalMenuAccessor) menu).getTe();
        if (lectern == null) {
            return;
        }

        var polymorph = PolymorphApi.getInstance();
        TransientCustomContainer craftMatrix = lectern.getCraftingInv(player.getUUID());
        var recipe = polymorph.getRecipeManager().getPlayerRecipe(
                menu,
                RecipeType.CRAFTING,
                craftMatrix.asCraftInput(),
                player.level(),
                player
        );

        if (recipe.isEmpty()) {
            return;
        }

        polymorph.getPlayerRecipeData(player).selectRecipe(recipe.get());

        ((CraftingLecternTileAccessor) lectern).invokeOnCraftingMatrixChanged(player.getUUID());

        menu.broadcastChanges();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
