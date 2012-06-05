package geogebra.web.gui.menubar;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.images.AppResources;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;

public class HelpMenu extends MenuBar {
	
	private AbstractApplication app;
	
	public HelpMenu(AbstractApplication app) {

		super(true);
	    this.app = app;
	    addStyleName("GeoGebraMenuBar");
	    // TODO: This item has no localization entry yet.
	    addItem("Version", new Command() {
			public void execute() {
	            Window.alert("GeoGebra " + GeoGebraConstants.VERSION_STRING + "\n"
	            		+ GeoGebraConstants.BUILD_DATE);       
            }
	    });
	    // TODO: This item has no localization entry yet.
	    addItem("About / License", new Command() {
			public void execute() {
				Window.open(GeoGebraConstants.GGW_ABOUT_LICENSE_URL, "_blank",
						"width=720,height=600,scrollbars=no,toolbar=no,location=no,directories=no,menubar=no,status=no,copyhistory=no");
            }
	    });
	    addItem(app.getMenu("About") + " / " + app.getMenu("License"), new Command() {
			public void execute() {
	            Window.open(GeoGebraConstants.GGW_ABOUT_TEAM_URL, "_blank", "");
            }
	    });
	}
	
}
