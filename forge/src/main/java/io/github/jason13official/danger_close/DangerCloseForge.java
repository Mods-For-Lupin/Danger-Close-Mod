package io.github.jason13official.danger_close;

import io.github.jason13official.danger_close.impl.common.config.ModConfigIO;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class DangerCloseForge {

  public static IEventBus EVENT_BUS;

  public DangerCloseForge(final FMLJavaModLoadingContext context) {
    EVENT_BUS = context.getModEventBus();

    EVENT_BUS.addListener((Consumer<FMLCommonSetupEvent>) event -> DangerClose.init());

    // beginning of livint entity tick
    MinecraftForge.EVENT_BUS.addListener((Consumer<LivingTickEvent>) event -> {

      Level abstractLevel = event.getEntity().level();
      LivingEntity living = event.getEntity();

      if (abstractLevel instanceof ServerLevel level &&
          DangerClose.isTickSlot(level, living)) {
        DangerClose.detect(level, living);
      }
    });

    MinecraftForge.EVENT_BUS.addListener((Consumer<AddReloadListenerEvent>) event -> {
      event.addListener(new ResourceReloadListener());
    });
  }

  @Deprecated
  @SuppressWarnings("all")
  public DangerCloseForge() {
    this(FMLJavaModLoadingContext.get());
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
