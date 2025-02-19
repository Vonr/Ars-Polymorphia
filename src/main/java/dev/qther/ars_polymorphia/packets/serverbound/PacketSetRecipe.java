package dev.qther.ars_polymorphia.packets.serverbound;

import com.hollingsworth.arsnouveau.client.container.CraftingTerminalMenu;
import com.hollingsworth.arsnouveau.common.network.AbstractPacket;
import com.illusivesoulworks.polymorph.api.common.base.IRecipePair;
import com.illusivesoulworks.polymorph.client.RecipesWidget;
import com.illusivesoulworks.polymorph.common.util.RecipePair;
import dev.qther.ars_polymorphia.ArsPolymorphia;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.List;

public class PacketSetRecipe extends AbstractPacket {
    public static final Type<PacketSetRecipe> TYPE = new Type<>(ArsPolymorphia.prefix("set_recipe"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSetRecipe> CODEC = StreamCodec.composite(
            RecipePair.STREAM_CODEC.apply(ByteBufCodecs.list()),
            (i) -> i.recipes,
            PacketSetRecipe::new
    );

    public List<IRecipePair> recipes;

    public PacketSetRecipe(List<IRecipePair> recipes) {
        this.recipes = recipes;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if (player == null) {
            return;
        }

        if (player.containerMenu instanceof CraftingTerminalMenu menu) {
            var maybeWidget = RecipesWidget.get();
            if (maybeWidget.isEmpty()) {
                return;
            }

            var widget = maybeWidget.get();
            widget.setRecipesList(new HashSet<>(this.recipes), null);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
