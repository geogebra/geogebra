package geogebra.web.helper;

import geogebra.web.main.Application;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window.Location;


public class UrlFetcherImpl implements UrlFetcher {
	
	private final RequestTemplate requestTemplate;
	private final String ggbFileParam;
	private final String proxyParam;
	private final String proxyLocation;

	public UrlFetcherImpl(RequestTemplate requestTemplate,
			String ggbFileParam,
			String proxyParam,
			String proxyLocation) {
		this.requestTemplate = requestTemplate;
		this.ggbFileParam = ggbFileParam;
		this.proxyParam = proxyParam;
		this.proxyLocation = proxyLocation;
	}
	
	// Public Methods
	
	public boolean isGgbFileParameterSpecified() {
		return !(getGgbFileUrlFromParameter() == null ? "" : getGgbFileUrlFromParameter()).isEmpty();
	}
	
	
	public String getAbsoluteGgbFileUrlFromParameter() {
		return makeAbsolute(getGgbFileUrlFromParameter());
	}
	
	
	public String getAbsoluteGgbFileUrl(String absoluteOrRelativeUrl) {
		return makeAbsolute(absoluteOrRelativeUrl);
	}
	
	
	public void fetchGgbFileFrom(String absoluteUrl, FileLoadCallback callback) {
		String proxiedUrl = makeProxyFetchUrlIfNecessary(absoluteUrl);
		requestTemplate.fetchBinary(proxiedUrl, callback);
	}
	
	// Private Methods
	private String getGgbFileUrlFromParameter() {
		return Location.getParameter(ggbFileParam);
	}
	
	private String makeAbsolute(String absoluteOrRelativeUrl) {
		String url = absoluteOrRelativeUrl.trim().toLowerCase();
		if (url.startsWith("http://") || url.startsWith("https://")) {
			return url;
		}
		if (url.startsWith("www")) {
			return "http://" + url;
		}
		return GWT.getHostPageBaseURL() + url;
	}
	
	private String makeProxyFetchUrlIfNecessary(String url) {
		String hostPageBase = GWT.getHostPageBaseURL();
		if (url.contains(hostPageBase)) {
			return url;
		}
		//return hostPageBase + proxyLocation + "?" + proxyParam + "=" + url
		return hostPageBase+url;
	}

}
