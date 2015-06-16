package org.geogebra.web.web.gui.util;


public class PopupBlockAvoider {

	private String showURLinBrowserPageUrl = null;

	public PopupBlockAvoider(){
		showURLinBrowserWaiterFixedDelay();
	}
	public native void showURLinBrowserWaiterFixedDelay() /*-{
		this.@org.geogebra.web.web.gui.util.PopupBlockAvoider::showURLinBrowserPageUrl = null;
		var that = this;
		var timer = {};
		function intervalTask() {
			if (that.@org.geogebra.web.web.gui.util.PopupBlockAvoider::showURLinBrowserPageUrl != null) {
				$wnd
						.open(
								that.@org.geogebra.web.web.gui.util.PopupBlockAvoider::showURLinBrowserPageUrl,
								"_blank");
				if (timer.tout) {
					$wnd.clearInterval(timer.tout);
				}
			}
		}

		timer.tout = $wnd.setInterval(intervalTask, 700);
	}-*/;

	public void openURL(String pageUrl) {
		showURLinBrowserPageUrl = pageUrl;
	}
}
