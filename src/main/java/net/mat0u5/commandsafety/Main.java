package net.mat0u5.commandsafety;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.mat0u5.commandsafety.config.ConfigManager;
import net.mat0u5.commandsafety.validator.CommandAnalyzer;
import net.mat0u5.commandsafety.validator.ConfirmationCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
	public static final String MOD_ID = "commandsafety";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static ConfigManager config;

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Command Safety.");
		CommandRegistrationCallback.EVENT.register(ConfirmationCommand::register);
		config = new ConfigManager("./config", Main.MOD_ID+".properties");
		CommandAnalyzer.loadConfig();
	}
}