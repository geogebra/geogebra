package geogebra.touch;


import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.MaterialFilter;
import geogebra.html5.main.AppW;
import geogebra.html5.main.StringHandler;
import geogebra.html5.util.ggtapi.JSONparserGGT;
import geogebra.web.gui.browser.BrowseGUI;
import geogebra.web.gui.browser.SignInButton;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.main.FileManager;
import geogebra.web.util.SaveCallback;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;



public class WinFileManager extends FileManager {
	private static final String META_PREFIX = "meta_";
	private static final String GGB_DIR = "GeoGebra";
	private static final String META_DIR = "meta";
	private static final String FILE_EXT = ".ggb";

	
	public WinFileManager(final AppW app) {
		super(app);
	}

	
	




	@Override
    public void autoSave() {
	    // not in touch either
	    
    }

	@Override
    public boolean isAutoSavedFileAvailable() {
	    // not in touch either
	    return false;
    }

	@Override
    public void restoreAutoSavedFile() {
	    // not in touch either
	    
    }

	@Override
    public void deleteAutoSavedFile() {
	    // not in touch either
	    
    }

	@Override
    public boolean save(AppW app) {
		if (!app.isOffline() && !app.getLoginOperation().isLoggedIn()) {
			app.getGuiManager().listenToLogin();
			((SignInButton) app.getLAF().getSignInButton(app)).login();
		} else {
			((DialogManagerW) app.getDialogManager()).showSaveDialog();
		}
		return true;
    }
	@Override
    public void rename(String newTitle, Material mat) {
	    renameNative(mat.getTitle(), newTitle);
	    
    }
	private native void renameNative(String oldTitle, String newTitle) /*-{
		if($wnd.android && $wnd.android.renameFile){
			$wnd.android.renameFile(oldTitle, newTitle);
		}
	}-*/;
	
	@Override
    public void openMaterial(final Material material) {
		openMaterialNative(material.getTitle());
    }

	private native void openMaterialNative(String title) /*-{
		if($wnd.android && $wnd.android.openFile){
			$wnd.android.openFile(title);
		}
    }-*/;







	@Override
    public void delete(Material mat) {
		deleteNative(mat.getTitle());
		removeFile(mat);
		((BrowseGUI) getApp().getGuiManager().getBrowseGUI()).setMaterialsDefaultStyle();
		
	    
    }

	private native void deleteNative(String title) /*-{
		if($wnd.android && $wnd.android.deleteFile){
			$wnd.android.deleteFile(title);
		}
    }-*/;







	@Override
    public void saveFile(final SaveCallback cb) {
	    getApp().getGgbApi().getBase64(true, new StringHandler(){

			@Override
            public void handle(String base64) {
				final Material mat = WinFileManager.this.createMaterial("");
	            String meta = mat.toJson().toString();
	            WinFileManager.this.doSave(base64, getApp().getLocalID(), getApp().getKernel().getConstruction().getTitle(), meta,
	            		new NativeSaveCallback(){

							@Override
                            public void onSuccess(String fileID) {
								getApp().setLocalID(Integer.parseInt(fileID));
	                            cb.onSaved(mat, true);
	                            
                            }

							@Override
                            public void onFailure() {
	                            cb.onError();
	                            
                            }});
	            
            }});
	    
    }

	protected native void doSave(String base64, int id, String title, String meta, NativeSaveCallback nsc) /*-{
	    var that = this;
	    if($wnd.android && $wnd.android.saveFile){
	    	$wnd.android.saveFile(base64, id, title, meta,function(jsString){
	    		nsc.@geogebra.touch.NativeSaveCallback::onSuccess(Ljava/lang/String;)(jsString);
	    	},function(jsString){
	    		nsc.@geogebra.touch.NativeSaveCallback::onFailure()();
	    	});
	    }
	    
    }-*/;







	@Override
    public void uploadUsersMaterials() {
	    // TODO Auto-generated method stub
	    
    }



	private void addMaterials(String jsString){
		JSONArray jv = JSONParser.parseLenient(jsString).isArray();
		for(int i = 0; i < jv.size(); i++){
			this.addMaterial(JSONparserGGT.toMaterial( jv.get(i).isObject()));
		}
		
	}



	@Override
    protected native void getFiles(MaterialFilter materialFilter) /*-{
    	var that = this;
	    if($wnd.android && $wnd.android.getFiles){
	    	$wnd.android.getFiles(function(jsString){
	    		that.@geogebra.touch.WinFileManager::addMaterials(Ljava/lang/String;)(jsString);
	    	});
	    }
	    
    }-*/;
	
	
	
	
}