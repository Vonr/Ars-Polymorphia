package dev.qther.ars_polymorphia;

import dev.qther.ars_polymorphia.packets.APNetworking;
import dev.qther.ars_polymorphia.polymorph.Widgets;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(ArsPolymorphia.MODID)
public class ArsPolymorphia {
    public static final String MODID = "ars_polymorphia";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public ArsPolymorphia(IEventBus modEventBus, ModContainer modContainer) {
        if (FMLEnvironment.dist.isClient()) {
            Widgets.register();
        }

        modEventBus.addListener(APNetworking::register);
    }

    public static ResourceLocation prefix(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
