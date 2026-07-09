package net.mat0u5.commandsafety;

import net.mat0u5.commandsafety.config.ConfigManager;
import net.mat0u5.commandsafety.validator.CommandAnalyzer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

	public static final String MOD_ID = "commandsafety";
	public static final String MOD_VERSION = "1.1.0";
	public static final String MOD_FRIENDLY_NAME = "Command Safety";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static ConfigManager config;

	public static void onInitialize() {
		LOGGER.info("Initializing {}", MOD_ID);
		LOGGER.info("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
		config = new ConfigManager("./config", Main.MOD_ID+".properties");
		CommandAnalyzer.loadConfig();
	}
}
