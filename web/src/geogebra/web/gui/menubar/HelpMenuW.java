package geogebra.web.gui.menubar;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.App;
import geogebra.web.gui.images.AppResources;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;

public class HelpMenuW extends MenuBar {
	
	private final App app;
	
	public HelpMenuW(final App app) {

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
	    		app.getMenu("Help"), true),true,new Command() {
			public void execute() {
				app.getGuiManager().openHelp(App.WIKI_MANUAL);
				
            }
	    });
	    
	    // Tutorials
	    addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),
	    		app.getMenu("Tutorials"), true),true,new Command() {
			public void execute() {
				app.getGuiManager().openHelp(App.WIKI_TUTORIAL);
            }
	    });
	    
	    // GeoGebraTube (no translation, deliberate)
	    addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.GeoGebraTube().getSafeUri().asString(),
	    		"GeoGebraTube", true),true,new Command() {
			public void execute() {
				Window.open(GeoGebraConstants.GEOGEBRATUBE_WEBSITE + "?lang="+app.getLocalization().getLanguage(), "_blank","");
            }
	    });

	    addSeparator();
	    
	    // Report Bug
	    addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),
	    		app.getMenu("ReportBug"), true),true,new Command() {
			public void execute() {
				Window.open(GeoGebraConstants.GEOGEBRA_REPORT_BUG_WEB + "&lang="+app.getLocalization().getLanguage(), "_blank","");
            }
	    });
	    
	    addSeparator();

	    addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.info().getSafeUri().asString(),
	    		app.getMenu("AboutLicense"), true),true,new Command() {
			public void execute() {
				Window.open(GeoGebraConstants.GGW_ABOUT_LICENSE_URL +
						"&version=" + GeoGebraConstants.VERSION_STRING + 
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
