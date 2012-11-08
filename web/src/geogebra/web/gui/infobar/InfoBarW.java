package geogebra.web.gui.infobar;

import geogebra.common.main.App;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Put a global announcement on the display. 
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 */

public class InfoBarW extends geogebra.common.gui.infobar.InfoBar {

	/**
	 * Global object for announcements for user's interest.
	 */
	PopupPanel info;
	
	/**
	 * Constructor
	 * @param app the used application (or window), not used at the moment
	 */
	public InfoBarW(App app) {
		info = null;
	}
	
    @Override
    public void show(String message) {
		App.info("ANNOUNCEMENT: " + message);
        hide();
		info = new PopupPanel();
	    info.ensureDebugId("announcement");
	    // Change this to a more/less fancy thing if you want: 
	    String html = "<div id='announcement' style='background-color: blue; color: white; font-weight: bold; font-family: arial,sans-serif;'>&nbsp;"
	    		+ message + "&nbsp;</div>";
	    info.setWidget(
	        new HTML(html));
	    info.setPopupPosition(10, 10); // fake position first (we need to write it out), but also for applets
	    info.show();
	    if (App.isFullAppGui()) {
	    	// For full GUI the text should be centered (ant put on the top):
	    	int left = (Window.getClientWidth() - info.getOffsetWidth()) / 2;
	    	info.setPopupPosition(left, 3); // good position
	    }
        info.show();
	}
	
    @Override
    public void hide() {
		if (info != null) {
			info.removeFromParent();
			info.hide();
			info = null;
		}
	}
}
