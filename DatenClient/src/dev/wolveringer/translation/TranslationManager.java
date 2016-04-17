package dev.wolveringer.translation;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ObjectUtils;

import dev.wolveringer.client.ClientWrapper;
import dev.wolveringer.client.LoadedPlayer;
import dev.wolveringer.dataserver.player.LanguageType;
import dev.wolveringer.hashmaps.CachedHashMap;

public class TranslationManager {
	public static File languageDir = new File("languages/"); 
	
	public static void setLanguageDirectory(File dir){
		languageDir = dir;
		languageDir.mkdirs();
	}
	
	private ClientWrapper handle;
	private HashMap<LanguageType, TranslationFile> translations = new HashMap<>();
	private CachedHashMap<LoadedPlayer, LanguageType> languages = new CachedHashMap<>(4, TimeUnit.HOURS);
	
	public TranslationManager(ClientWrapper handle) {
		languageDir.mkdirs();
		this.handle = handle;
		for(LanguageType t : LanguageType.values())
			translations.put(t, new TranslationFile(t, this.handle));
	}
	
	public void updateTranslations(){
		for(TranslationFile f : translations.values())
				if(f.checkUpdate().getSync())
					f.updateLanguage();
	}
	
	public String translate(String key,Object...args){
		return translate(key, LanguageType.ENGLISH, args);
	}
	public String translate(String key,LoadedPlayer player,Object...args){
		LanguageType type = languages.get(player);
		if(type == null){
			type = player.getLanguageSync();
			languages.put(player, type);
		}
		return translate(key, type, args);
	}
	public String translate(String key,LanguageType lang,Object...args){
		return formatTranslation(getTranslation(key, lang), args);
	}
	
	public void updateLanguage(LoadedPlayer player){
		languages.remove(player);
	}
	
	private String getTranslation(String key,LanguageType lang){
		key = key.toLowerCase();
		if(!translations.containsKey(lang))
			System.out.println("Cant find language: "+lang);
		String translation = null;
		if(translations.get(lang).getTranslation(key) != null)
			translation = translations.get(lang).getTranslation(key);
		else if(translations.containsKey(LanguageType.ENGLISH) && translations.get(LanguageType.ENGLISH).getTranslation(key) != null)
			translation = translations.get(LanguageType.ENGLISH).getTranslation(key);
		else{
			System.out.println("Missing translation ["+key+","+lang.getShortName()+"]");
			translation = "<missing translation "+key+">";
		}
		return translation.replaceAll("\\\\n", "\n");
	}
	private String formatTranslation(String in,Object[] args){
		for(int i = 0;i<args.length;i++)
			in = in.replaceAll("%s"+i, ObjectUtils.toString(args[i], "undefined"));
		return in;
	}

	public LanguageType getLanguage(LoadedPlayer player) {
		LanguageType type = languages.get(player);
		if(type == null){
			type = player.getLanguageSync();
			languages.put(player, type);
		}
		return type;
	}
	
	public TranslationFile getTranslationFile(LanguageType lang){
		return translations.get(lang);
	}
}
