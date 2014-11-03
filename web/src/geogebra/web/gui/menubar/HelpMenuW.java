package geogebra.web.gui.menubar;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.App;
import geogebra.html5.main.AppW;
import geogebra.web.css.GuiResources;
import geogebra.web.gui.images.AppResources;

import com.google.gwt.user.client.Window;

public class HelpMenuW extends GMenuBar {
	
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
	 // Tutorials
	    addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),
	    		app.getMenu("Tutorials"), true),true,new MenuCommand((AppW) app) {
			
	    	@Override
	    	public void doExecute() {
		        app.getGuiManager().openHelp(App.WIKI_TUTORIAL);
            }
	    });
	    
	    // Help
	    addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_help().getSafeUri().asString(),
	    		app.getMenu("Manual"), true),true,new MenuCommand((AppW) app) {
			
	    	@Override
	    	public void doExecute() {
		        app.getGuiManager().openHelp(App.WIKI_MANUAL);
				
            }
	    });
	    
	    addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),
	    		app.getMenu("GeoGebraForum"), true), true, new MenuCommand((AppW) app) {
			
	    	@Override
	    	public void doExecute() {
		        app.getGuiManager().openHelp(GeoGebraConstants.FORUM_URL);
				
            }
	    });

	    addSeparator();
	    
	    // Report Bug
	    addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),
	    		app.getMenu("ReportBug"), true),true,new MenuCommand((AppW) app) {
			
	    	@Override
	    	public void doExecute() {
		        Window.open(GeoGebraConstants.GEOGEBRA_REPORT_BUG_WEB + "&lang="+app.getLocalization().getLanguage(), "_blank","");
            }
	    });
	    
	    addSeparator();

	    addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_help_about().getSafeUri().asString(),
	    		app.getMenu("AboutLicense"), true),true,new MenuCommand((AppW) app) {
	    	
			@Override
			public void doExecute() {
		        Window.open(GeoGebraConstants.GGW_ABOUT_LICENSE_URL +
						"&version=" + GeoGebraConstants.VERSION_STRING + 
						"&date=" + GeoGebraConstants.BUILD_DATE,
						"_blank",
						"width=720,height=600,scrollbars=yes,toolbar=no,location=no,directories=no,menubar=no,status=no,copyhistory=no");
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
