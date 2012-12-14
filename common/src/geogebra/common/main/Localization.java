package geogebra.common.main;

public interface Localization {
	String getPlain(String key);
	String getPlain(String key,String p1);
	String getPlain(String key,String p1,String p2);
	String getPlain(String key,String p1,String p2,String p3);
	String getPlain(String key,String p1,String p2, String p3,String p4);
	String getPlain(String key,String p1,String p2, String p3,String p4, String p5);
	String getPlainLabel(String key);	
	String getMenu(String key);
	String getError(String key);
	String getCommand(String key);
	String translationFix(String s);
	String getOrdinalNumber(int n);
	boolean isReverseNameDescriptionLanguage();
	boolean isRightToLeftReadingOrder();
	boolean languageIs(String lang);
	boolean isUsingLocalizedLabels();
	
}
