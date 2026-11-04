package dev.qther.ars_polymorphia.mixin;

import com.hollingsworth.arsnouveau.common.block.tile.CraftingLecternTile;
import com.hollingsworth.arsnouveau.common.block.tile.TransientCustomContainer;
import com.illusivesoulworks.polymorph.api.PolymorphApi;
import com.illusivesoulworks.polymorph.api.common.base.IRecipePair;
import com.illusivesoulworks.polymorph.common.network.server.SPacketPlayerRecipeSync;
import com.illusivesoulworks.polymorph.common.network.server.SPacketRecipesList;
import com.illusivesoulworks.polymorph.common.util.RecipePair;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Optional;
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
        }

        Optional<ResourceLocation> selectedId = Optional.empty();
        if (selected != null) {
            selectedId = Optional.of(selected.id());
        }
        PacketDistributor.sendToPlayer(player, new SPacketRecipesList(Optional.of(pairs), selectedId));
        PacketDistributor.sendToPlayer(player, new SPacketPlayerRecipeSync(Optional.of(pairs), selectedId));
    }

    @Unique
    private static <I extends RecipeInput, T extends Recipe<I>>
    Optional<RecipeHolder<T>> arsPolymorphia$getRecipe(RecipeManager instance, RecipeType<T> recipeType, I input, Level level, Operation<Optional<RecipeHolder<T>>> original, @Nullable Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            var selected = PolymorphApi.getInstance()
                    .getPlayerRecipeData(serverPlayer)
                    .getSelectedRecipe();

            if (selected != null && selected.value().getType() == recipeType) {
                @SuppressWarnings("unchecked")
                RecipeHolder<T> selectedRecipe = (RecipeHolder<T>) selected;

                if (selectedRecipe.value().matches(input, level)) {
                    return Optional.of(selectedRecipe);
                }
            }
        }

        return original.call(instance, recipeType, input, level);
    }

    @WrapOperation(method = "craft", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> craft(RecipeManager instance, RecipeType<T> recipeType, I input, Level level, Operation<Optional<RecipeHolder<T>>> original, ServerPlayer thePlayer, @Nullable String tab) {
        return arsPolymorphia$getRecipe(instance, recipeType, input, level, original, thePlayer);
    }
    @WrapOperation(method = "updateCraftResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> updateCraftResult(RecipeManager instance, RecipeType<T> recipeType, I input, Level level, Operation<Optional<RecipeHolder<T>>> original, UUID uuid) {
        return arsPolymorphia$getRecipe(instance, recipeType, input, level, original, level.getPlayerByUUID(uuid));
    }
}
