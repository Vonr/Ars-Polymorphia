package dev.qther.ars_polymorphia.polymorph;

import com.hollingsworth.arsnouveau.client.container.AbstractStorageTerminalScreen;
import com.illusivesoulworks.polymorph.api.client.widgets.PlayerRecipesWidget;
import com.mojang.datafixers.util.Pair;
import dev.qther.ars_polymorphia.ArsPolymorphia;
import dev.qther.ars_polymorphia.packets.serverbound.PacketResetCraftingResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.network.PacketDistributor;

public class CraftingTerminalWidget extends PlayerRecipesWidget {
    public static final WidgetSprites OUTPUT = new WidgetSprites(ArsPolymorphia.prefix("output_button"), ArsPolymorphia.prefix("output_button_highlighted"));
    public static final WidgetSprites CURRENT_OUTPUT = new WidgetSprites(ArsPolymorphia.prefix("current_output"), ArsPolymorphia.prefix("current_output_highlighted"));
    public static final WidgetSprites SELECTOR = new WidgetSprites(ArsPolymorphia.prefix("selector_button"), ArsPolymorphia.prefix("selector_button_highlighted"));

    protected final AbstractContainerMenu menu;

    public CraftingTerminalWidget(AbstractStorageTerminalScreen screen, Slot outputSlot) {
        super(screen, outputSlot);
        menu = screen.getMenu();
    }

    @SuppressWarnings("resource")
    @Override
    public void selectRecipe(ResourceLocation id) {
        super.selectRecipe(id);
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        var recipe = player.level().getRecipeManager().byKey(id);
        if (recipe.isEmpty()) {
            return;
        }

        PacketDistributor.sendToServer(PacketResetCraftingResult.INSTANCE);
    }

    @Override
    public WidgetSprites getSelectorSprites() {
        return SELECTOR;
    }

    @Override
    public Pair<WidgetSprites, WidgetSprites> getOutputSprites() {
        return Pair.of(OUTPUT, CURRENT_OUTPUT);
    }

    @Override
    public int getYPos() {
        return super.getYPos() - 2;
    }
}
