package geogebra.web.util;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
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

	/**
	 * Global object for announcements for user's interest.
	 */
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
	    // Change this to a more/less fancy thing if you want: 
	    String html = "<div id='announcement' style='background-color: blue; color: white; font-weight: bold; font-family: arial,sans-serif;'>&nbsp;"
	    		+ message + "&nbsp;</div>";
	    announcementPopup.setWidget(
	        new HTML(html));
	    announcementPopup.setPopupPosition(400, 3); // fake position first (we need to write it out)
	    announcementPopup.show();
	    int left = (Window.getClientWidth() - announcementPopup.getOffsetWidth()) / 2;
	    log(INFO, Window.getClientWidth() + "-" + announcementPopup.getOffsetWidth());
        announcementPopup.setPopupPosition(left, 3); // good position
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
