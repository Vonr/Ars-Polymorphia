package dev.qther.ars_polymorphia.mixin;

import com.hollingsworth.arsnouveau.client.container.StorageTerminalMenu;
import com.hollingsworth.arsnouveau.common.block.tile.StorageLecternTile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = StorageTerminalMenu.class, remap = false)
public interface StorageTerminalMenuAccessor {
    @Accessor
    StorageLecternTile getTe();
}
