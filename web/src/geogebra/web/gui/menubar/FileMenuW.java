package geogebra.web.gui.menubar;

import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.events.StayLoggedOutEvent;
import geogebra.common.move.ggtapi.events.LoginEvent;
import geogebra.common.move.views.BooleanRenderable;
import geogebra.common.move.views.EventRenderable;
import geogebra.html5.main.AppW;
import geogebra.html5.main.StringHandler;
import geogebra.web.css.GuiResources;
import geogebra.web.gui.browser.SignInButton;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.gui.images.AppResources;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Web implementation of FileMenu
 */
public class FileMenuW extends GMenuBar implements BooleanRenderable, EventRenderable {
	
	/** Application */
	AppW app;
	private MenuItem uploadToGGT;
	Runnable onFileOpen;
	Runnable newConstruction;
	boolean uploadWaiting;
	
	/**
	 * @param app application
	 * @param onFileOpen 
	 */
	public FileMenuW(final AppW app, Runnable onFileOpen) {
	    super(true);
	    this.app = app;
	    this.onFileOpen = onFileOpen;
	    this.newConstruction = new Runnable() {
			
			@Override
			public void run() {
				app.setWaitCursor();
				app.fileNew();
				app.setDefaultCursor();
				app.showStartScreen();
			}
		};
	    addStyleName("GeoGebraMenuBar");
	    initActions();
		update();
		app.getLoginOperation().getView().add(this);
	}

	private void update() {
	    // TODO Auto-generated method stub
	    
    }
	
	native void nativeShare(String base64, String title)/*-{
		if($wnd.android){
			$wnd.android.share(base64,title);

		}
	}-*/;
	
	native boolean nativeShareSupported()/*-{
		if($wnd.android && $wnd.android.share){
			return true;
		}
		return false;
	}-*/;

	private void initActions() {

		// this is enabled always
		addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_file_new().getSafeUri().asString(),app.getMenu("New"), true),true,new Command() {

			public void execute() {
				((DialogManagerW) app.getDialogManager()).getSaveUnsavedDialog().setAfterSavedCallback(newConstruction);
				((DialogManagerW) app.getDialogManager()).getSaveUnsavedDialog().showIfNeeded();
			}
		});

		// open menu is always visible in menu
		
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_file_open().getSafeUri().asString(), app.getPlain("Open"), true),true,new Command() {
    		
				public void execute() {
					app.openSearch();
					if(FileMenuW.this.onFileOpen!=null){
						FileMenuW.this.onFileOpen.run();
					}
				}
			});	
		
		
		if(app.getLAF().undoRedoSupported()) {
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_file_save().getSafeUri().asString(), app.getMenu("Save"), true),true,new Command() {
		
				public void execute() {
					if (!app.getNetworkOperation().isOnline() && !app.getLoginOperation().isLoggedIn()) {
						openFilePicker();
					} else if (!app.getLoginOperation().isLoggedIn()) {
						uploadWaiting = true;
						((SignInButton)app.getLAF().getSignInButton(app)).login();
					} else {
						app.getGuiManager().save();
					}
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
	    
	    addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(), app.getMenu("Export"), true), true, new Command() {
			
			@Override
			public void execute() {
				openFilePicker();
			}
		});
	    
	    /*addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(), app.getMenu("Export"), true),
		        true, new ExportMenuW(app));*/
	    
	    app.getNetworkOperation().getView().add(this);
	    
	    if (!app.getNetworkOperation().isOnline()) {
	    	render(false);    	
	    }
	}
	
	void openFilePicker() {
		String title = "".equals(app.getKernel().getConstruction().getTitle()) ? "geogebra.ggb":
			(app.getKernel().getConstruction().getTitle() + ".ggb");
		JavaScriptObject callback = getDownloadCallback(title);
		this.app.getGgbApi().getGGB(true, callback);
    }
	
	private native JavaScriptObject getDownloadCallback(String title) /*-{
		var _this = this;
		return function(ggbZip) {
			var URL = $wnd.URL || $wnd.webkitURL;
			var ggburl = URL.createObjectURL(ggbZip);

			if ($wnd.navigator.msSaveBlob) {
				//works for chrome and internet explorer
				$wnd.navigator.msSaveBlob(ggbZip, title);
			} else {
				//works for firefox
				var a = document.createElement("a");
    			document.body.appendChild(a);
		    	a.style = "display: none";
		        a.href = ggburl;
		        a.download = title;
		        a.click();
//		        window.URL.revokeObjectURL(url);
			} 
		}
	}-*/;

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
	
	@Override
	public void renderEvent(final BaseEvent event) {
		if(this.uploadWaiting && event instanceof LoginEvent && ((LoginEvent)event).isSuccessful()){
			this.uploadWaiting = false;
			app.getGuiManager().save();
		} else if(this.uploadWaiting && event instanceof StayLoggedOutEvent){
			this.uploadWaiting = false;
			openFilePicker();
		}
	}
	

}
