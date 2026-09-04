package io.github.jason13official.danger_close.impl.common.compat;

import it.crystalnest.prometheus.api.FireManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class Prometheus {

  public static void immolate(Entity entity, int seconds) {

    setOnTypedFire(entity, seconds, FireManager.DEFAULT_FIRE_TYPE);
  }

  public static void immolateSoul(Entity entity, int seconds) {

    setOnTypedFire(entity, seconds, FireManager.SOUL_FIRE_TYPE);
  }

  private static void setOnTypedFire(Entity entity, int seconds, ResourceLocation fireType) {

    FireManager.setOnFire(entity, seconds, fireType);
  }
}
