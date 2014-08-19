package geogebra.web.gui.menubar;

import geogebra.common.move.views.BooleanRenderable;
import geogebra.html5.css.GuiResources;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Web implementation of FileMenu
 */
public class FileMenuW extends GMenuBar {
	
	/** Application */
	AppW app;
	private MenuItem uploadToGGT;
	Runnable onFileOpen;
	
	/**
	 * @param app application
	 */
	public FileMenuW(AppW app, Runnable onFileOpen) {
	    super(true);
	    this.app = app;
	    this.onFileOpen = onFileOpen;
	    addStyleName("GeoGebraMenuBar");
	    initActions();
		update();
	}

	private void update() {
	    // TODO Auto-generated method stub
	    
    }
	
	private native boolean nativeShare()/*-{
		if($wnd.android){
			$wnd.android.share("a","b");
			return true;
		}else{
			return false;
		}
	}-*/;
	
	private void initActions() {

		// this is enabled always
		addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_file_new().getSafeUri().asString(),app.getMenu("New"), true),true,new Command() {

			public void execute() {
				app.setWaitCursor();
				app.fileNew();
				app.setDefaultCursor();
			}
		});

		// open menu is always visible in menu
		
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_file_open().getSafeUri().asString(), app.getPlain("Open"), true),true,new Command() {
    		
				public void execute() {
					app.showBrowser(((GuiManagerW) app.getGuiManager()).getBrowseGUI());
					if(FileMenuW.this.onFileOpen!=null){
						FileMenuW.this.onFileOpen.run();
					}
				}
			});	
		
		
		if(app.getLAF().undoRedoSupported()) {
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_file_save().getSafeUri().asString(), app.getMenu("Save"), true),true,new Command() {
		
				public void execute() {
					app.getGuiManager().save();
				}
			});			
		}

		// this is enabled always
	    uploadToGGT = addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_file_share().getSafeUri().asString(),app.getMenu("Share"), true),true,new Command() {
	    	public void execute() {
	    		if(!nativeShare()){
	    		app.uploadToGeoGebraTube();
	    		}
	    	}
	    });
	    
	    addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(), app.getMenu("Export"), true),
		        true, new ExportMenuW(app));
	    
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
	

}
