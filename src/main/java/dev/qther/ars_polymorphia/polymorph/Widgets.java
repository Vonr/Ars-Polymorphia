package dev.qther.ars_polymorphia.polymorph;

import com.hollingsworth.arsnouveau.client.container.CraftingTerminalScreen;
import com.illusivesoulworks.polymorph.api.client.PolymorphWidgets;
import dev.qther.ars_polymorphia.mixin.CraftingTerminalMenuAccessor;

public class Widgets {
    public static void register() {
        PolymorphWidgets.getInstance().registerWidget(screen -> {
            if (screen instanceof CraftingTerminalScreen term) {
                return new CraftingTerminalWidget(term, ((CraftingTerminalMenuAccessor) term.getMenu()).getCraftingResultSlot());
            }

            return null;
        });
    }
}
