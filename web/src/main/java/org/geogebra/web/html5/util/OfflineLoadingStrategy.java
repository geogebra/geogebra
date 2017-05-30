package org.geogebra.web.html5.util;

import com.google.gwt.core.client.impl.LoadingStrategyBase;
import com.google.gwt.core.client.impl.ScriptTagLoadingStrategy.ScriptTagDownloadStrategy;
import com.google.gwt.core.client.impl.XhrLoadingStrategy.XhrDownloadStrategy;
import com.google.gwt.user.client.Window.Location;

/**
 * Download strategy that works with appcache / service worker over HTTP and
 * still supports file protocol (via script tag)
 *
 */
public class OfflineLoadingStrategy extends LoadingStrategyBase {

	/**
	 * @param downloadStrategy
	 *            internal download strategy
	 */
	public OfflineLoadingStrategy(DownloadStrategy downloadStrategy) {
		super(downloadStrategy);
	}

	private static DownloadStrategy makeDownloadStrategy() {
		return Location.getProtocol().startsWith("http")
				? new XhrDownloadStrategy() {
					@Override
					public void tryDownload(final RequestData request) {
						setAsyncCallback(request.getFragment(), request);
						super.tryDownload(request);
					}
				} : new ScriptTagDownloadStrategy();

	}

	/**
	 * @param fragment
	 *            fragment number
	 * @param request
	 *            request
	 */
	static native void setAsyncCallback(int fragment,
			RequestData request) /*-{
    __gwtModuleFunction['runAsyncCallback' + fragment] = $entry(function(code, instance) {
      @com.google.gwt.core.client.impl.ScriptTagLoadingStrategy::asyncCallback(Lcom/google/gwt/core/client/impl/LoadingStrategyBase$RequestData;Ljava/lang/String;)(
        request, code);
    });
  }-*/;

	/**
	 * Create the strategy
	 */
	public OfflineLoadingStrategy() {
		super(makeDownloadStrategy());
	}
}
