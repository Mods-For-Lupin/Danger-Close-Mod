package io.github.jason13official.danger_close;

import io.github.jason13official.danger_close.impl.common.config.ModConfigIO;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public class DangerCloseFabric implements ModInitializer {

  @Override
  public void onInitialize() {

    DangerClose.init();

    ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(DangerClose.identifier(Constants.MOD_ID), new ResourceReloadListener());
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
