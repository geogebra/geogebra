package geogebra.web.gui.menubar;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.App;
import geogebra.web.gui.images.AppResources;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;

public class HelpMenuW extends MenuBar {
	
	private App app;
	
	public HelpMenuW(App app) {

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
	    addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.help().getSafeUri().asString(),
	    		app.getMenu("Help")),true,new Command() {
			public void execute() {
				Window.open("http://www.geogebra.org/help/en/article/Manual:Main%20Page", "_blank","");
            }
	    });
	    
	    // Tutorials
	    addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),
	    		app.getMenu("Tutorials")),true,new Command() {
			public void execute() {
				Window.open("http://www.geogebra.org/help/en/article/Tutorial:Main%20Page", "_blank","");
            }
	    });
	    
	    // GeoGebraTube (no translation, deliberate)
	    addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.GeoGebraTube().getSafeUri().asString(),
	    		"GeoGebraTube"),true,new Command() {
			public void execute() {
				Window.open(GeoGebraConstants.GEOGEBRATUBE_WEBSITE, "_blank","");
            }
	    });

	    addSeparator();
	    
	    // Report Bug
	    addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),
	    		app.getMenu("ReportBug")),true,new Command() {
			public void execute() {
				Window.open(GeoGebraConstants.GEOGEBRA_REPORT_BUG_WEB, "_blank","");
            }
	    });
	    
	    addSeparator();

	    addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.info().getSafeUri().asString(),
	    		app.getMenu("AboutLicense")),true,new Command() {
			public void execute() {
				Window.open(GeoGebraConstants.GGW_ABOUT_LICENSE_URL +
						/* "&version=" + GeoGebraConstants.VERSION_STRING + */ 
						"&date=" + GeoGebraConstants.BUILD_DATE,
						"_blank",
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
