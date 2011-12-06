package geogebra.web.helper;

public interface UrlFetcher {
	
	boolean isGgbFileParameterSpecified();
	String getAbsoluteGgbFileUrlFromParameter();
	String getAbsoluteGgbFileUrl(String absoluteOrRelativeUrl);
	
	void fetchGgbFileFrom(String absoluteOrRelativeUrl, FileLoadCallback callback);
	
}
