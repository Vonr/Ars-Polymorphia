package dev.qther.ars_polymorphia.mixin;

import com.hollingsworth.arsnouveau.client.container.CraftingTerminalMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = CraftingTerminalMenu.class, remap = false)
public interface CraftingTerminalMenuAccessor {
    @Accessor
    CraftingTerminalMenu.ActiveResultSlot getCraftingResultSlot();
}
