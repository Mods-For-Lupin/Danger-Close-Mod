package io.github.jason13official.danger_close;

import java.util.function.Consumer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class DangerCloseClientNeoForge {

  public DangerCloseClientNeoForge(final IEventBus modEventBus) {

    modEventBus.addListener((Consumer<FMLClientSetupEvent>) event -> DangerCloseClient.init());
  }
}
