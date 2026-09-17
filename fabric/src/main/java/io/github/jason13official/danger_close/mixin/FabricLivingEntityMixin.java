package io.github.jason13official.danger_close.mixin;

import io.github.jason13official.danger_close.DangerClose;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class FabricLivingEntityMixin {

  // @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", shift = Shift.AFTER), method = "rideTick")
  @Inject(at = @At(value = "HEAD"), method = "tick")
  private void danger_close$rideTick(CallbackInfo ci) {

    LivingEntity self = (LivingEntity) (Object) this;

    Level abstractLevel = self.level();

    if (abstractLevel instanceof ServerLevel level &&
        level.getGameTime() % 2 == 0) {
      DangerClose.detect(level, self);
    }
  }
}
