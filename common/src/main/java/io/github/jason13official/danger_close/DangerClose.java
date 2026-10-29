package io.github.jason13official.danger_close;

import io.github.jason13official.danger_close.impl.common.compat.Prometheus;
import io.github.jason13official.danger_close.impl.common.config.ModConfigIO;
import io.github.jason13official.danger_close.impl.common.config.ServerConfig;
import io.github.jason13official.danger_close.platform.Services;
import io.github.jason13official.monolib.MonoLib;
import io.github.jason13official.monolib.impl.common.sailing.Sailing;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class DangerClose {

  public static final TagKey<Block> TORCH_BURN_DANGER = TagKey.create(Registries.BLOCK, identifier("torch_burn_danger"));
  public static final TagKey<Block> SOUL_TORCH_BURN_DANGER = TagKey.create(Registries.BLOCK, identifier("soul_torch_burn_danger"));
  public static final TagKey<Block> CAMPFIRE_BURN_DANGER = TagKey.create(Registries.BLOCK, identifier("campfire_burn_danger"));
  public static final TagKey<Block> SOUL_CAMPFIRE_BURN_DANGER = TagKey.create(Registries.BLOCK, identifier("soul_campfire_burn_danger"));
  public static final TagKey<Block> MAGMA_BURN_DANGER = TagKey.create(Registries.BLOCK, identifier("magma_burn_danger"));
  public static final TagKey<Block> STONECUTTER_DANGER = TagKey.create(Registries.BLOCK, identifier("stonecutter_danger"));

  public static boolean PROMETHEUS_FOUND = false;

  public static void init() {

    Sailing.register(Constants.MOD_ID, MonoLib.createFilename(Constants.MOD_ID, "1.21.1", "1.0.0"));

    ModConfigIO.getOrCreate();

    PROMETHEUS_FOUND = Services.PLATFORM.isModLoaded("prometheus");
  }

  public static ResourceLocation identifier(final String path) {

    return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
  }

  public static void immolate(LivingEntity entity) {

    if (!entity.isOnFire()) entity.setRemainingFireTicks(20 * 2); // for 40 ticks, or 2 seconds
  }

  /// detection for entities runs every other tick, offset by entity id so the load is spread across both ticks
  public static boolean isTickSlot(Level level, LivingEntity living) {

    // getGameTime is usual ticks; 0, 1, 2, etc.
    // getId also counts incrementally I think
    // & 1 is the same as % 2 basically, but can't result in a -1 (which should never happen here anyway, right?) and may be faster at the lowest level?
    return ((level.getGameTime() + living.getId()) & 1) == 0;
  }

  public static void detect(ServerLevel level, LivingEntity living) {

    if (!ServerConfig.ENABLED.get()) {
      return;
    }
    if (level == null || living == null || !living.isAlive()) {
      return;
    }

    // pairs only matter if one side is burning or fiery,
    // both sides run detect, so one detect covers both
    if (living.isOnFire() || isFiery(living)) {
      List<LivingEntity> nearby = level.getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, living, living.getBoundingBox());

      for (LivingEntity otherLiving : nearby) {
        spreadFire(living, otherLiving);
      }
    }

    if (living.onGround()) {

      BlockPos pos = living.blockPosition();

      BlockState inside = level.getBlockState(pos);
      BlockState below = level.getBlockState(pos.below());

      boolean sneaking = living.isShiftKeyDown();

      if (detectTorches(living, inside, below)) return;
      if (detectCampfires(living, inside)) return;
      detectStonecutter(living, inside, below, sneaking);

      // only detect magma blocks if we're not inside a cauldron
      if (!(inside.getBlock() instanceof AbstractCauldronBlock)) {
        detectMagmaBlock(living, inside, below, sneaking);
      }
    }
  }

  private static boolean hasFrostWalker(LivingEntity living) {

    Holder<Enchantment> frostWalker = living.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FROST_WALKER);
    return EnchantmentHelper.getEnchantmentLevel(frostWalker, living) > 0;
  }

  private static void ignite(LivingEntity living, boolean soul) {

    if (soul && PROMETHEUS_FOUND) {
      Prometheus.immolateSoul(living, 2);
      return;
    }

    immolate(living);
  }

  /// @return `true` if a torch matched our tags
  private static boolean detectTorches(LivingEntity living, BlockState inside, BlockState below) {

    if (ServerConfig.TORCHES_BURN.get() && (inside.is(TORCH_BURN_DANGER) || below.is(TORCH_BURN_DANGER))) {
      if (!hasFrostWalker(living)) ignite(living, false);
      return true;
    }

    if (ServerConfig.SOUL_TORCHES_BURN.get() && (inside.is(SOUL_TORCH_BURN_DANGER) || below.is(SOUL_TORCH_BURN_DANGER))) {
      if (!hasFrostWalker(living)) ignite(living, true);
      return true;
    }

    return false;
  }

  /// @return `true` if a campfire matched our tags
  private static boolean detectCampfires(LivingEntity living, BlockState inside) {

    if (ServerConfig.CAMPFIRES_BURN.get() && inside.is(CAMPFIRE_BURN_DANGER)) {
      if (isLit(inside) && !hasFrostWalker(living)) ignite(living, false);
      return true;
    }

    if (ServerConfig.SOUL_CAMPFIRES_BURN.get() && inside.is(SOUL_CAMPFIRE_BURN_DANGER)) {
      if (isLit(inside) && !hasFrostWalker(living)) ignite(living, true);
      return true;
    }

    return false;
  }

  private static boolean isLit(BlockState state) {

    return state.hasProperty(CampfireBlock.LIT) && state.getValue(CampfireBlock.LIT);
  }

  private static void detectMagmaBlock(LivingEntity living, BlockState inside, BlockState below, boolean sneaking) {

    if (ServerConfig.ENABLE_MAGMA_BLOCK_DAMAGE.get() && !sneaking
        && (inside.is(MAGMA_BURN_DANGER) || below.is(MAGMA_BURN_DANGER))
        && !hasFrostWalker(living)) {
      immolate(living);
    }
  }

  private static void detectStonecutter(LivingEntity living, BlockState inside, BlockState below, boolean sneaking) {

    if (ServerConfig.STONECUTTERS_CUT.get() && !sneaking
        && (inside.is(STONECUTTER_DANGER) || below.is(STONECUTTER_DANGER))) {
      living.hurt(living.level().damageSources().cactus(), 4.0f);
    }
  }

  private static boolean isFiery(LivingEntity entity) {

    return (ServerConfig.ENABLE_BLAZE_DAMAGE.get() && entity instanceof Blaze)
        || (ServerConfig.ENABLE_MAGMA_CUBE_DAMAGE.get() && entity instanceof MagmaCube);
  }

  /// spread fire between burning entities or ignite based on mob type
  private static void spreadFire(LivingEntity main, LivingEntity other) {

    if (main.isOnFire() && !other.isOnFire()) {
      immolate(other);
    } else if (!main.isOnFire() && other.isOnFire()) {
      immolate(main);
    }

    if ((ServerConfig.ENABLE_BLAZE_DAMAGE.get() && other instanceof Blaze) || (ServerConfig.ENABLE_MAGMA_CUBE_DAMAGE.get() && other instanceof MagmaCube)) {
      immolate(main); // you're fiery I'm not
    } else if ((ServerConfig.ENABLE_BLAZE_DAMAGE.get() && main instanceof Blaze) || (ServerConfig.ENABLE_MAGMA_CUBE_DAMAGE.get() && main instanceof MagmaCube)) {
      immolate(other); // I'm fiery you're not
    }
  }
}
