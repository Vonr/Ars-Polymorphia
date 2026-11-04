package dev.qther.ars_polymorphia;

import dev.qther.ars_polymorphia.packets.APNetworking;
import dev.qther.ars_polymorphia.polymorph.Widgets;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Mod(ArsPolymorphia.MODID)
public class ArsPolymorphia {
    public static final String MODID = "ars_polymorphia";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public ArsPolymorphia(IEventBus modEventBus, ModContainer modContainer) {
        if (FMLEnvironment.dist.isClient()) {
            Widgets.register();
        }

        modEventBus.addListener(APNetworking::register);

        if (Boolean.getBoolean("devenv")) {
            NeoForge.EVENT_BUS.addListener((OnDatapackSyncEvent event) -> {
                var mgr = event.getPlayerList().getServer().getRecipeManager();
                mgr.replaceRecipes(List.of(
                        new RecipeHolder<>(prefix("test1"), new ShapelessRecipe("ars_polymorphia.test", CraftingBookCategory.MISC, Items.OAK_BOAT.getDefaultInstance(), NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.BARRIER)))),
                        new RecipeHolder<>(prefix("test2"), new ShapelessRecipe("ars_polymorphia.test", CraftingBookCategory.MISC, Items.DARK_OAK_DOOR.getDefaultInstance(), NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.BARRIER))))
                ));
            });
        }
    }

    public static ResourceLocation prefix(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
