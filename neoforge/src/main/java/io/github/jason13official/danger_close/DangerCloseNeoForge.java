package io.github.jason13official.danger_close;

import io.github.jason13official.danger_close.impl.common.config.ModConfigIO;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@Mod(Constants.MOD_ID)
public class DangerCloseNeoForge {

  public static IEventBus EVENT_BUS;

  public DangerCloseNeoForge(final IEventBus modEventBus) {

    EVENT_BUS = modEventBus;

    EVENT_BUS.addListener((Consumer<FMLCommonSetupEvent>) event -> DangerClose.init());

    NeoForge.EVENT_BUS.addListener((Consumer<EntityTickEvent.Pre>) event -> {

      Level abstractLevel = event.getEntity().level();

      if (abstractLevel instanceof ServerLevel level && level.getGameTime() % 2 == 0 && event.getEntity() instanceof LivingEntity living) {
        DangerClose.detect(level, living);
      }
    });

    NeoForge.EVENT_BUS.addListener((Consumer<AddReloadListenerEvent>) event -> {
      event.addListener(new ResourceReloadListener());
    });
  }

  public static class ResourceReloadListener extends SimplePreparableReloadListener<Void> {

    @Override
    public String getName() {
      return DangerClose.identifier(Constants.MOD_ID).toString();
    }

    @Override
    protected void apply(Void unused, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
      // ModConfig.load(Services.PLATFORM.getConfigDirectory());
      ModConfigIO.getOrCreate();
    }

    @Override
    protected Void prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
      return null;
    }
  }
}