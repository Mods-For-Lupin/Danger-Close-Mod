package io.github.jason13official.danger_close.impl.common.config;

import io.github.jason13official.monolib.api.common.config.ConfigGetterSetter.Commented;

public class ServerConfig {

  private static boolean enabled = true;

  private static boolean torchesBurn = false;
  private static boolean soulTorchesBurn = false;
  private static boolean campfiresBurn = true;
  private static boolean soulCampfiresBurn = true;
  private static boolean stonecuttersCut = true;
  private static boolean enableBlazeDamage = true;
  private static boolean enableMagmaCubeDamage = false;
  private static boolean enableMagmaBlockDamage = true;

  public static Commented<Boolean> ENABLED = new Commented<>("enabled", () -> enabled, value -> enabled = value, "Whether the mod functions.");

  public static Commented<Boolean> TORCHES_BURN = new Commented<>("torches_burn", () -> torchesBurn, value -> torchesBurn = value, "Torches ignite entities?");
  public static Commented<Boolean> SOUL_TORCHES_BURN = new Commented<>("soul_torches_burn", () -> soulTorchesBurn, value -> soulTorchesBurn = value, "Soul Torches ignite entities?");
  public static Commented<Boolean> CAMPFIRES_BURN = new Commented<>("campfires_burn", () -> campfiresBurn, value -> campfiresBurn = value, "Campfires ignite entities?");
  public static Commented<Boolean> SOUL_CAMPFIRES_BURN = new Commented<>("soul_campfires_burn", () -> soulCampfiresBurn, value -> soulCampfiresBurn = value, "Soul Campfires ignite entities?");
  public static Commented<Boolean> STONECUTTERS_CUT = new Commented<>("stonecutters_cut", () -> stonecuttersCut, value -> stonecuttersCut = value, "Stonecutters hurt entities?");
  public static Commented<Boolean> ENABLE_BLAZE_DAMAGE = new Commented<>("enable_blaze_damage", () -> enableBlazeDamage, value -> enableBlazeDamage = value, "Being close to a Blaze ignites entities?");
  public static Commented<Boolean> ENABLE_MAGMA_CUBE_DAMAGE = new Commented<>("enable_magma_cube_damage", () -> enableMagmaCubeDamage, value -> enableMagmaCubeDamage = value, "Being hurt by a Magma Cube ignites entities?");
  public static Commented<Boolean> ENABLE_MAGMA_BLOCK_DAMAGE = new Commented<>("enable_magma_block_damage", () -> enableMagmaBlockDamage, value -> enableMagmaBlockDamage = value, "Magma Blocks ignite entities?");
}
