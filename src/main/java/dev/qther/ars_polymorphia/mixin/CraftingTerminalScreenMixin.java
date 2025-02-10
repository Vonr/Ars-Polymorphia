package dev.qther.ars_polymorphia.mixin;

import com.hollingsworth.arsnouveau.client.container.AbstractStorageTerminalScreen;
import com.hollingsworth.arsnouveau.client.container.CraftingTerminalMenu;
import com.hollingsworth.arsnouveau.client.container.CraftingTerminalScreen;
import com.illusivesoulworks.polymorph.client.RecipesWidget;
import dev.qther.ars_polymorphia.packets.serverbound.PacketResetCraftingResult;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingTerminalScreen.class)
public abstract class CraftingTerminalScreenMixin extends AbstractStorageTerminalScreen<CraftingTerminalMenu> {
    public CraftingTerminalScreenMixin(CraftingTerminalMenu screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        RecipesWidget.create(this);
        PacketDistributor.sendToServer(new PacketResetCraftingResult());
    }
}
