package geogebra.web.gui.menubar;

import geogebra.common.move.views.BooleanRenderable;
import geogebra.html5.gui.util.AppResources;
import geogebra.html5.main.AppW;
import geogebra.html5.main.GgbAPIW;
import geogebra.html5.main.StringHandler;
import geogebra.web.css.GuiResources;
import geogebra.web.gui.dialog.DialogManagerW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;
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
	Anchor downloadButton;
	
	/**
	 * @param app application
	 */
	public FileMenuW(final AppW app, Runnable onFileOpen) {
	    super(true);
	    this.app = app;
	    this.onFileOpen = onFileOpen;
	    this.downloadButton = new Anchor();
		this.downloadButton.setStyleName("downloadButton");
		this.downloadButton.getElement().setAttribute("download", "geogebra.ggb");
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
					app.openSearch();
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
	    
	    addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(), app.getMenu("Export"), true), true, new Command() {
			
			@Override
			public void execute() {
				openFilePicker();
			}
		});
	    
	    /*addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(), app.getMenu("Export"), true),
		        true, new ExportMenuW(app));*/
	    
	    app.getNetworkOperation().getView().add(this);
	    
	    if (!app.getNetworkOperation().getOnline()) {
	    	render(false);    	
	    }
	}
	
	void openFilePicker() {
		JavaScriptObject callback = getDownloadCallback(this.downloadButton.getElement());
		((GgbAPIW) this.app.getGgbApi()).getGGB(true, callback);
    }
	
	private native JavaScriptObject getDownloadCallback(Element downloadButton) /*-{
		var _this = this;
		return function(ggbZip) {
			var URL = $wnd.URL || $wnd.webkitURL;
			var ggburl = URL.createObjectURL(ggbZip);
			downloadButton.setAttribute("href", ggburl);
			if ($wnd.navigator.msSaveBlob) {
				$wnd.navigator.msSaveBlob(ggbZip, "geogebra.ggb");
			} else {
				downloadButton.click();
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
	

}
