package io.github.jason13official.danger_close;

import io.github.jason13official.danger_close.impl.common.config.ModConfigIO;
import io.github.jason13official.danger_close.impl.common.config.ServerConfig;
import io.github.jason13official.danger_close.platform.Services;
import io.github.jason13official.monolib.MonoLib;
import io.github.jason13official.monolib.impl.common.sailing.Sailing;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class DangerClose {

  public static final TagKey<Block> TORCH_BURN_DANGER = TagKey.create(Registries.BLOCK, identifier("torch_burn_danger"));
  public static final TagKey<Block> SOUL_TORCH_BURN_DANGER = TagKey.create(Registries.BLOCK, identifier("soul_torch_burn_danger"));
  public static final TagKey<Block> CAMPFIRE_BURN_DANGER = TagKey.create(Registries.BLOCK, identifier("campfire_burn_danger"));
  public static final TagKey<Block> SOUL_CAMPFIRE_BURN_DANGER = TagKey.create(Registries.BLOCK, identifier("soul_campfire_burn_danger"));
  public static final TagKey<Block> MAGMA_BURN_DANGER = TagKey.create(Registries.BLOCK, identifier("magma_burn_danger"));
  public static final TagKey<Block> STONECUTTER_DANGER = TagKey.create(Registries.BLOCK, identifier("stonecutter_danger"));
  public static boolean SOUL_FIRE_D = false;

  public static void init() {

    Sailing.register(Constants.MOD_ID, MonoLib.createFilename(Constants.MOD_ID, "26.1.2", "1.0.0"));

    ModConfigIO.getOrCreate();

    SOUL_FIRE_D = Services.PLATFORM.isModLoaded("soul_fire_d");
  }

  public static Identifier identifier(final String path) {
    return Identifier.fromNamespaceAndPath(Constants.MOD_ID, path);
  }

  public static void immolate(LivingEntity entity) {
    entity.setRemainingFireTicks(20 * 2); // for 40 ticks, or 2 seconds
  }

  public static void detect(ServerLevel level, LivingEntity living) {

    if (!ServerConfig.ENABLED.get()) {
      return;
    }
    if (level == null || living == null) {
      return;
    }

    List<LivingEntity> nearby = level.getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, living, living.getBoundingBox());

    for (LivingEntity otherLiving : nearby) {
      spreadFire(living, otherLiving);
    }

    boolean sneaking = living.isShiftKeyDown();

    Holder<Enchantment> frostWalker = living.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FROST_WALKER);
    boolean hasFrostWalker = EnchantmentHelper.getEnchantmentLevel(frostWalker, living) > 0;

    if (living.onGround()) {

      BlockPos pos = living.blockPosition();

      BlockState insideBlockState = level.getBlockState(pos);

      Stream<TagKey<Block>> insideBlockTagStream = insideBlockState.tags();
      Stream<TagKey<Block>> belowBlockTagStream = level.getBlockState(pos.below()).tags();

      List<TagKey<Block>> insideReStreamable = new ArrayList<>();
      List<TagKey<Block>> belowReStreamable = new ArrayList<>();

      insideBlockTagStream.forEach(insideReStreamable::add);
      belowBlockTagStream.forEach(belowReStreamable::add);

      detectTorches(living, insideReStreamable, belowReStreamable, hasFrostWalker);
      detectCampfires(living, insideReStreamable, belowReStreamable, hasFrostWalker);
      detectStonecutter(living, insideReStreamable, belowReStreamable, living.isShiftKeyDown());

      // only detect magma blocks if we're not inside a cauldron
      if (!(insideBlockState.getBlock() instanceof AbstractCauldronBlock)) {
        detectMagmaBlock(living, insideReStreamable, belowReStreamable, hasFrostWalker);
      }
    }
  }

  private static void detectTorches(LivingEntity living, List<TagKey<Block>> insideReStreamable, List<TagKey<Block>> belowReStreamable, boolean hasFrostWalker) {
    boolean inNormal = insideReStreamable.stream().anyMatch(Predicate.isEqual(DangerClose.TORCH_BURN_DANGER));
    boolean onNormal = belowReStreamable.stream().anyMatch(Predicate.isEqual(DangerClose.TORCH_BURN_DANGER));
    boolean inSoul = insideReStreamable.stream().anyMatch(Predicate.isEqual(DangerClose.SOUL_TORCH_BURN_DANGER));
    boolean onSoul = belowReStreamable.stream().anyMatch(Predicate.isEqual(DangerClose.SOUL_TORCH_BURN_DANGER));

    if (ServerConfig.TORCHES_BURN.get() && !hasFrostWalker && (inNormal || onNormal)) {
      immolate(living);
    } else if (ServerConfig.SOUL_TORCHES_BURN.get() && !hasFrostWalker && (inSoul || onSoul)) {
      immolate(living);
    }
  }

  private static void detectCampfires(LivingEntity living, List<TagKey<Block>> insideReStreamable, List<TagKey<Block>> belowReStreamable, boolean hasFrostWalker) {
    boolean inNormal = insideReStreamable.stream().anyMatch(Predicate.isEqual(DangerClose.CAMPFIRE_BURN_DANGER));
    boolean onNormal = belowReStreamable.stream().anyMatch(Predicate.isEqual(DangerClose.CAMPFIRE_BURN_DANGER));
    boolean inSoul = insideReStreamable.stream().anyMatch(Predicate.isEqual(DangerClose.SOUL_CAMPFIRE_BURN_DANGER));
    boolean onSoul = belowReStreamable.stream().anyMatch(Predicate.isEqual(DangerClose.SOUL_CAMPFIRE_BURN_DANGER));

    if (ServerConfig.CAMPFIRES_BURN.get() && !hasFrostWalker && (inNormal || onNormal)) {
      immolate(living);
    } else if (ServerConfig.SOUL_CAMPFIRES_BURN.get() && !hasFrostWalker && (inSoul || onSoul)) {
      immolate(living);
    }
  }

  private static void detectMagmaBlock(LivingEntity living, List<TagKey<Block>> insideReStreamable, List<TagKey<Block>> belowReStreamable, boolean hasFrostWalker) {
    boolean in = insideReStreamable.stream().anyMatch(Predicate.isEqual(DangerClose.MAGMA_BURN_DANGER));
    boolean on = belowReStreamable.stream().anyMatch(Predicate.isEqual(DangerClose.MAGMA_BURN_DANGER));

    if (ServerConfig.ENABLE_MAGMA_BLOCK_DAMAGE.get() && !hasFrostWalker && (in || on)) {
      immolate(living);
    }
  }

  private static void detectStonecutter(LivingEntity living, List<TagKey<Block>> insideReStreamable, List<TagKey<Block>> belowReStreamable, boolean isSneaking) {
    boolean in = insideReStreamable.stream().anyMatch(Predicate.isEqual(DangerClose.STONECUTTER_DANGER));
    boolean on = belowReStreamable.stream().anyMatch(Predicate.isEqual(DangerClose.STONECUTTER_DANGER));

    if (ServerConfig.STONECUTTERS_CUT.get() && !isSneaking && (in || on)) {
      immolate(living);
    }
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