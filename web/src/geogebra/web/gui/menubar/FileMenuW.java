package geogebra.web.gui.menubar;

import geogebra.common.move.views.BooleanRenderable;
import geogebra.html5.css.GuiResources;
import geogebra.html5.main.StringHandler;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Web implementation of FileMenu
 */
public class FileMenuW extends GMenuBar implements BooleanRenderable {
	
	/** Application */
	AppW app;
	private MenuItem uploadToGGT;
	Runnable onFileOpen;
	Runnable newConstruction;
	
	/**
	 * @param app application
	 */
	public FileMenuW(AppW app, Runnable onFileOpen) {
	    super(true);
	    this.app = app;
	    this.onFileOpen = onFileOpen;
	    this.newConstruction = new Runnable() {
			
			@Override
			public void run() {
				runNew();
			}
		};
	    addStyleName("GeoGebraMenuBar");
	    initActions();
		update();
	}

	private void update() {
	    // TODO Auto-generated method stub
	    
    }
	
	native void nativeShare(String base64, String title)/*-{
		if($wnd.android){
			$wnd.android.share(base64,title);

		}
	}-*/;
	
	private native boolean nativeShareSupported()/*-{
		if($wnd.android && $wnd.android.share){
			return true;
		}
		return false;
	}-*/;
	
	private void runNew() {
		app.setWaitCursor();
		app.fileNew();
		app.setDefaultCursor();
	}
	
	private void initActions() {

		// this is enabled always
		addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_file_new().getSafeUri().asString(),app.getMenu("New"), true),true,new Command() {

			public void execute() {
				((DialogManagerW) app.getDialogManager()).getSaveUnsavedDialog().setCallback(newConstruction);
				((DialogManagerW) app.getDialogManager()).getSaveUnsavedDialog().showIfNeeded();
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
	    		if(!nativeShareSupported()){
	    		app.uploadToGeoGebraTube();
	    		}else{
	    			app.getGgbApi().getBase64(true, new StringHandler(){

						@Override
                        public void handle(String s) {
							String title = app.getKernel().getConstruction().getTitle();
	                        nativeShare(s, "".equals(title) ? "construction" : title);
                        }});
	    		
	    		}
	    			
	    	}
	    });
	    
	    addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(), app.getMenu("Export"), true),
		        true, new ExportMenuW(app));
	    
	    app.getNetworkOperation().getView().add(this);
	    
	    if (!app.getNetworkOperation().getOnline()) {
	    	render(false);    	
	    }
	    
		

	}

	/**
	 * @param online wether the application is online
	 * renders a the online - offline state of the FileMenu
	 */
	public void render(boolean online) {
	    uploadToGGT.setEnabled(online);
	    if (!online) {
	    	uploadToGGT.setTitle(app.getMenu("YouAreOffline"));
		} else {
			uploadToGGT.setTitle("");
		}
    }
	

}
