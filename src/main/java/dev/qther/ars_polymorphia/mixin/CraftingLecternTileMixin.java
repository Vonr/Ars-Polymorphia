package dev.qther.ars_polymorphia.mixin;

import com.hollingsworth.arsnouveau.common.block.tile.CraftingLecternTile;
import com.hollingsworth.arsnouveau.common.block.tile.TransientCustomContainer;
import com.illusivesoulworks.polymorph.api.common.base.IRecipePair;
import com.illusivesoulworks.polymorph.client.RecipesWidget;
import com.illusivesoulworks.polymorph.common.util.RecipePair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mixin(CraftingLecternTile.class)
public abstract class CraftingLecternTileMixin {
    @Shadow public abstract TransientCustomContainer getCraftingInv(UUID uuid);

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

        var maybeWidget = RecipesWidget.get();
        if (maybeWidget.isEmpty()) {
            return;
        }

        var recipes = serverLevel.getRecipeManager().getRecipesFor(RecipeType.CRAFTING, this.getCraftingInv(uuid).asCraftInput(), serverLevel);

        Set<IRecipePair> set = new HashSet<>();

        for (var recipe : recipes) {
            set.add(new RecipePair(recipe.id(), recipe.value().getResultItem(level.registryAccess())));
        }

        var widget = maybeWidget.get();
        widget.setRecipesList(set, null);
    }
}
