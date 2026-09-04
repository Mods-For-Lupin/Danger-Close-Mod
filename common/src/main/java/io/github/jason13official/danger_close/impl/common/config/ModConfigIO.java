package io.github.jason13official.danger_close.impl.common.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import io.github.jason13official.danger_close.platform.Services;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfigIO {

  public static void getOrCreate() {

    Path configDir = Services.PLATFORM.getConfigDirectory();
    File configDirectory = new File(configDir.toUri());

    if (!configDirectory.isDirectory() && !configDirectory.mkdirs()) {

      return;
    }

    Path configFilepath = configDir.resolve("danger_closer-server.toml");
    File configFile = new File(configFilepath.toUri());

    Config.setInsertionOrderPreserved(true);
    try (CommentedFileConfig config = CommentedFileConfig.builder(configFile).build()) {

      if (Files.exists(configFilepath)) {
        config.load();
      }

      // get from config

      ServerConfig.ENABLED.set(config.getOrElse(ServerConfig.ENABLED.key(), ServerConfig.ENABLED.get()));
      ServerConfig.TORCHES_BURN.set(config.getOrElse(ServerConfig.TORCHES_BURN.key(), ServerConfig.TORCHES_BURN.get()));
      ServerConfig.SOUL_TORCHES_BURN.set(config.getOrElse(ServerConfig.SOUL_TORCHES_BURN.key(), ServerConfig.SOUL_TORCHES_BURN.get()));
      ServerConfig.CAMPFIRES_BURN.set(config.getOrElse(ServerConfig.CAMPFIRES_BURN.key(), ServerConfig.CAMPFIRES_BURN.get()));
      ServerConfig.SOUL_CAMPFIRES_BURN.set(config.getOrElse(ServerConfig.SOUL_CAMPFIRES_BURN.key(), ServerConfig.SOUL_CAMPFIRES_BURN.get()));
      ServerConfig.STONECUTTERS_CUT.set(config.getOrElse(ServerConfig.STONECUTTERS_CUT.key(), ServerConfig.STONECUTTERS_CUT.get()));
      ServerConfig.ENABLE_BLAZE_DAMAGE.set(config.getOrElse(ServerConfig.ENABLE_BLAZE_DAMAGE.key(), ServerConfig.ENABLE_BLAZE_DAMAGE.get()));
      ServerConfig.ENABLE_MAGMA_CUBE_DAMAGE.set(config.getOrElse(ServerConfig.ENABLE_MAGMA_CUBE_DAMAGE.key(), ServerConfig.ENABLE_MAGMA_CUBE_DAMAGE.get()));
      ServerConfig.ENABLE_MAGMA_BLOCK_DAMAGE.set(config.getOrElse(ServerConfig.ENABLE_MAGMA_BLOCK_DAMAGE.key(), ServerConfig.ENABLE_MAGMA_BLOCK_DAMAGE.get()));

      // set to config

      config.setComment(ServerConfig.ENABLED.key(), ServerConfig.ENABLED.comment());
      config.setComment(ServerConfig.TORCHES_BURN.key(), ServerConfig.TORCHES_BURN.comment());
      config.setComment(ServerConfig.SOUL_TORCHES_BURN.key(), ServerConfig.SOUL_TORCHES_BURN.comment());
      config.setComment(ServerConfig.CAMPFIRES_BURN.key(), ServerConfig.CAMPFIRES_BURN.comment());
      config.setComment(ServerConfig.SOUL_CAMPFIRES_BURN.key(), ServerConfig.SOUL_CAMPFIRES_BURN.comment());
      config.setComment(ServerConfig.STONECUTTERS_CUT.key(), ServerConfig.STONECUTTERS_CUT.comment());
      config.setComment(ServerConfig.ENABLE_BLAZE_DAMAGE.key(), ServerConfig.ENABLE_BLAZE_DAMAGE.comment());
      config.setComment(ServerConfig.ENABLE_MAGMA_CUBE_DAMAGE.key(), ServerConfig.ENABLE_MAGMA_CUBE_DAMAGE.comment());
      config.setComment(ServerConfig.ENABLE_MAGMA_BLOCK_DAMAGE.key(), ServerConfig.ENABLE_MAGMA_BLOCK_DAMAGE.comment());

      config.set(ServerConfig.ENABLED.key(), ServerConfig.ENABLED.get());
      config.set(ServerConfig.TORCHES_BURN.key(), ServerConfig.TORCHES_BURN.get());
      config.set(ServerConfig.SOUL_TORCHES_BURN.key(), ServerConfig.SOUL_TORCHES_BURN.get());
      config.set(ServerConfig.CAMPFIRES_BURN.key(), ServerConfig.CAMPFIRES_BURN.get());
      config.set(ServerConfig.SOUL_CAMPFIRES_BURN.key(), ServerConfig.SOUL_CAMPFIRES_BURN.get());
      config.set(ServerConfig.STONECUTTERS_CUT.key(), ServerConfig.STONECUTTERS_CUT.get());
      config.set(ServerConfig.ENABLE_BLAZE_DAMAGE.key(), ServerConfig.ENABLE_BLAZE_DAMAGE.get());
      config.set(ServerConfig.ENABLE_MAGMA_CUBE_DAMAGE.key(), ServerConfig.ENABLE_MAGMA_CUBE_DAMAGE.get());
      config.set(ServerConfig.ENABLE_MAGMA_BLOCK_DAMAGE.key(), ServerConfig.ENABLE_MAGMA_BLOCK_DAMAGE.get());

      config.save();
    } catch (Exception e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }
}
