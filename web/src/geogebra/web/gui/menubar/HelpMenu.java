package geogebra.web.gui.menubar;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.AbstractApplication;

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
	    //addItem("Version", new Command() {
		//	public void execute() {
	    //        Window.alert("GeoGebra " + GeoGebraConstants.VERSION_STRING + "\n"
	    //        		+ GeoGebraConstants.BUILD_DATE);       
        //    }
	    //});
	    
	    // Help
	    addItem(app.getMenu("Help"), new Command() {
			public void execute() {
				Window.open("http://www.geogebra.org/help/en/article/Manual:Main%20Page", "_blank","");
            }
	    });
	    
	    // Tutorials
	    addItem(app.getMenu("Tutorials"), new Command() {
			public void execute() {
				Window.open("http://www.geogebra.org/help/en/article/Tutorial:Main%20Page", "_blank","");
            }
	    });
	    
	    // GeoGebraTube
	    addItem("GeoGebraTube", new Command() {
			public void execute() {
				Window.open(GeoGebraConstants.GEOGEBRATUBE_WEBSITE, "_blank","");
            }
	    });

	    addSeparator();
	    
	    // Report Bug
	    addItem(app.getMenu("ReportBug"), new Command() {
			public void execute() {
				Window.open(GeoGebraConstants.GEOGEBRA_REPORT_BUG_WEB, "_blank","");
            }
	    });
	    
	    addSeparator();
	    
	    addItem(app.getMenu("AboutLicense"), new Command() {
			public void execute() {
				Window.open(GeoGebraConstants.GGW_ABOUT_LICENSE_URL + "&version=" +
						GeoGebraConstants.VERSION_STRING + "&date=" +
						GeoGebraConstants.BUILD_DATE, "_blank",
						"width=720,height=600,scrollbars=no,toolbar=no,location=no,directories=no,menubar=no,status=no,copyhistory=no");
            }
	    });
	    // TODO: This item has no localization entry yet.
	    //addItem("About / Team", new Command() {
		//	public void execute() {
	    //       Window.open(GeoGebraConstants.GGW_ABOUT_TEAM_URL, "_blank", "");
        //    }
	    //});
	}
	
}
