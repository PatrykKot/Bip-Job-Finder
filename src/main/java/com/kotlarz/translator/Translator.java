package com.kotlarz.translator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Translator {
	private static Logger log = Logger.getLogger(Translator.class);
	
	private static Map<String, Properties> translations;

	private static void init() {
		translations = new HashMap<>();
		Language[] languages = Language.values();

		for (Language lang : languages) {
			loadLanguage(lang.getFilePath(), lang.getLang());
		}
	}

	private static void loadLanguage(String filePath, String lang) {
		Properties props = loadProperties(filePath);
		translations.put(lang, props);
	}

	private static Properties loadProperties(String path) {
		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream(path);
			prop.load(new InputStreamReader(input, "UTF-8"));
		} catch (Exception ex) {
			log.error("Problem z plikiem tłumaczeń", ex);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					log.error("Problem z zamknięciem pliku", e);
				}
			}
		}

		return prop;
	}

	public static String getMessage(String key) {
		return getMessage(key, new Locale("pl"));
	}

	public static String getMessage(String key, Locale locale) {
		if (translations == null)
			init();

		Properties props = translations.get(locale.getLanguage());
		if (props == null || !props.containsKey(key))
			return key;

		return props.getProperty(key);
	}
}
