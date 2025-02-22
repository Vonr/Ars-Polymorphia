package dev.qther.ars_polymorphia.mixin;

import com.hollingsworth.arsnouveau.common.block.tile.CraftingLecternTile;
import com.hollingsworth.arsnouveau.common.block.tile.TransientCustomContainer;
import com.illusivesoulworks.polymorph.api.PolymorphApi;
import com.illusivesoulworks.polymorph.api.common.base.IRecipePair;
import com.illusivesoulworks.polymorph.common.network.server.SPacketPlayerRecipeSync;
import com.illusivesoulworks.polymorph.common.network.server.SPacketRecipesList;
import com.illusivesoulworks.polymorph.common.util.RecipePair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Mixin(CraftingLecternTile.class)
public abstract class CraftingLecternTileMixin {
    @Shadow public abstract TransientCustomContainer getCraftingInv(UUID uuid);

    @Shadow private CraftingRecipe currentRecipe;

    @SuppressWarnings("resource")
    @Inject(method = "onCraftingMatrixChanged", at = @At("HEAD"))
    public void onCraftMatrixChanged(UUID uuid, CallbackInfo ci) {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }

        var player = server.getPlayerList().getPlayer(uuid);
        if (player == null) {
            return;
        }

        var level = player.level();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        var playerData = PolymorphApi.getInstance().getPlayerRecipeData(player);
        var selected = playerData.getSelectedRecipe();

        var recipes = serverLevel.getRecipeManager().getRecipesFor(RecipeType.CRAFTING, this.getCraftingInv(uuid).asCraftInput(), serverLevel);

        HashSet<IRecipePair> pairs = new HashSet<>();

        for (var recipe : recipes) {
            var result = recipe.value().getResultItem(level.registryAccess());
            if (result.isEmpty()) {
                continue;
            }
            pairs.add(new RecipePair(recipe.id(), result));
            if (selected != null && recipe.id().equals(selected.id())) {
                this.currentRecipe = recipe.value();
            }
        }

        Optional<ResourceLocation> selectedId = Optional.empty();
        if (selected != null) {
            selectedId = Optional.of(selected.id());
        }
        PacketDistributor.sendToPlayer(player, new SPacketRecipesList(Optional.of(pairs), selectedId));
        PacketDistributor.sendToPlayer(player, new SPacketPlayerRecipeSync(Optional.of(pairs), selectedId));
    }
}
