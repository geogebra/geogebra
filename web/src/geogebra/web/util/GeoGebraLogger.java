package geogebra.web.util;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * GeoGebraLogger implementation for the web platform
 * @author Zoltan Kovacs <zoltan@geogebra.org>

 */
public class GeoGebraLogger extends geogebra.common.util.GeoGebraLogger {

	/**
	 * Constructor
	 */
	public GeoGebraLogger() {
	}

	PopupPanel announcementPopup;
	
	@Override
	protected String getTimeInfo() {
		Date date = new Date();
		return DateTimeFormat.getFormat("HH:mm:ss.SSS").format(date);
	}

	@Override
	public void setLogFile(String logFileName) {
		log(WARN, "FILE logging is not supported in web, falling back to use CONSOLES instead");
		setLogDestination(LogDestination.CONSOLES);
	}

	@Override
	protected void print(String logEntry, Level level) {
		if (getLogDestination() == LogDestination.WEB_CONSOLE ||
				getLogDestination() == LogDestination.CONSOLES) {
			printWebConsole(logEntry);
		}
		if (getLogDestination() == LogDestination.FILE) {
			setLogDestination(LogDestination.CONSOLES);
			log(WARN, "FILE logging is not supported in desktop, falling back to use CONSOLES instead");
			print (logEntry, level);
			return;
		}
		if (getLogDestination() == LogDestination.CONSOLE ||
				getLogDestination() == LogDestination.CONSOLES) {
			// don't change this to Application.debug!!
			GWT.log(logEntry);
			return;
		}
	}

	private native void printWebConsole(String s) /*-{
		$wnd.console.log(s);
		}-*/;
	
	
	@Override
    public void showAnnouncement(String message) {
		if (announcementPopup != null) {
			return;
			// First it must be hidden.
		}
        log(INFO, "Announcement: " + message);
        hideAnnouncement();
		announcementPopup = new PopupPanel();
	    announcementPopup.ensureDebugId("announcement");
	    announcementPopup.setWidth("400px");
	    announcementPopup.setWidget(
	        new HTML(message));
        announcementPopup.setPopupPosition(400, 0);
        announcementPopup.show();
	}
	
	@Override
    public void hideAnnouncement() {
		if (announcementPopup != null) {
			announcementPopup.removeFromParent();
			announcementPopup.hide();
			announcementPopup = null;
		}
	}
}
