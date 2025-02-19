package dev.qther.ars_polymorphia.mixin;

import com.hollingsworth.arsnouveau.common.block.tile.CraftingLecternTile;
import net.minecraft.world.item.crafting.CraftingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.UUID;

@Mixin(value = CraftingLecternTile.class, remap = false)
public interface CraftingLecternTileAccessor {
    @Invoker
    void invokeOnCraftingMatrixChanged(UUID uuid);

    @Accessor
    void setCurrentRecipe(CraftingRecipe currentRecipe);
}
