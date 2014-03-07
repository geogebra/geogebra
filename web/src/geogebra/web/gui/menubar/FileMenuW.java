package geogebra.web.gui.menubar;

import geogebra.common.move.views.BooleanRenderable;
import geogebra.html5.css.GuiResources;
import geogebra.html5.gui.browser.BrowseGUI;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Web implementation of FileMenu
 */
public class FileMenuW extends MenuBar {
	
	/** Application */
	AppW app;
	private OpenMenuW openMenu;
	private MenuItem uploadToGGT;
	Runnable onFileOpen;
	
	/**
	 * @param app application
	 */
	public FileMenuW(AppW app, boolean useOpenScreen, Runnable onFileOpen) {
	    super(true);
	    this.app = app;
	    this.onFileOpen = onFileOpen;
	    addStyleName("GeoGebraMenuBar");
	    initActions(useOpenScreen);
		update();
	}

	private void update() {
	    // TODO Auto-generated method stub
	    
    }

	private void initActions(boolean useOpenScreen) {

		// this is enabled always
		addItem(GeoGebraMenubarW.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_file_new().getSafeUri().asString(),app.getMenu("New"), true),true,new Command() {

			public void execute() {
				app.setWaitCursor();
				app.fileNew();
				app.setDefaultCursor();
			}
		});

		// open menu is always visible in menu
		openMenu = new OpenMenuW(app);
		if(useOpenScreen) {
			addItem(GeoGebraMenubarW.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_file_open().getSafeUri().asString(), app.getMenu("Open"), true),true,new Command() {
    		
				public void execute() {
					BrowseGUI bg = new BrowseGUI(app);
					app.showBrowser(bg);
					if(FileMenuW.this.onFileOpen!=null){
						FileMenuW.this.onFileOpen.run();
					}
				}
			});	
		} else{
			addItem(GeoGebraMenubarW.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_file_open().getSafeUri().asString(), app.getPlain("Open"), true),true, openMenu);
		}
		
		if(!app.getLAF().isSmart()) {
			addItem(GeoGebraMenubarW.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_file_save().getSafeUri().asString(), app.getMenu("SaveAs"), true),true,new Command() {
		
				public void execute() {
					app.getGuiManager().save();
				}
			});			
		}

		// this is enabled always
	    uploadToGGT = addItem(GeoGebraMenubarW.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_file_share().getSafeUri().asString(),app.getMenu("Share"), true),true,new Command() {
	    	public void execute() {
	    		app.uploadToGeoGebraTube();
	    	}
	    });
	    
	    app.getNetworkOperation().getView().add(new BooleanRenderable() {
			
			public void render(boolean b) {
				renderNetworkOperation(b);
			}
		});
	    
	    if (!app.getNetworkOperation().getOnline()) {
	    	renderNetworkOperation(false);    	
	    }
	    
		

	}

	/**
	 * @param online wether the application is online
	 * renders a the online - offline state of the FileMenu
	 */
	void renderNetworkOperation(boolean online) {
	    uploadToGGT.setEnabled(online);
	    if (!online) {
	    	uploadToGGT.setTitle(app.getMenu("YouAreOffline"));
		} else {
			uploadToGGT.setTitle("");
		}
    }

	/**
	 * @return Open submenu
	 */
	public OpenMenuW getOpenMenu() {
	   return openMenu;
    }
	
	

}
