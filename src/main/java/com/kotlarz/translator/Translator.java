package com.kotlarz.translator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class Translator {
	private static String plTranslationsPath = "locale/messages_pl.properties";

	private static Map<String, Properties> translations;

	private static void init() {
		translations = new HashMap<>();
		loadLanguage(ClassLoader.getSystemResource(plTranslationsPath).getFile(), "pl");
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
			prop.load(input);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return prop;
	}

	public static String getMessage(String key) {
		return getMessage(key, Locale.getDefault());
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
