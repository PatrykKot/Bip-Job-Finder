package com.kotlarz.translator;

public enum Language {
	POLISH("pl");

	private String lang;

	private Language(String lang) {
		this.lang = lang;
	}

	public String getLang() {
		return lang;
	}

	public String getFilePath() {
		return "locale/messages_" + lang + ".properties";
	}

}
