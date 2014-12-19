package geogebra.touch;


import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.MaterialFilter;
import geogebra.html5.main.AppW;
import geogebra.html5.util.ggtapi.JSONparserGGT;
import geogebra.web.gui.browser.BrowseGUI;
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
	
	public void saveLoggedOut(AppW app){
		((DialogManagerW) app.getDialogManager()).showSaveDialog();
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
    public void openMaterial(final Material mat) {
		getBase64(mat.getTitle(), new NativeSaveCallback(){

			@Override
            public void onSuccess(String fileID) {
	            mat.setBase64(fileID);
	            doOpen(mat);
            }

			@Override
            public void onFailure() {
	            // TODO Auto-generated method stub
	            
            }});
    }

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
    public void upload(final Material mat){
		
		getBase64(mat.getTitle(), new NativeSaveCallback(){

			@Override
            public void onSuccess(String fileID) {
	            mat.setBase64(fileID);
	            doUpload(mat);
            }

			@Override
            public void onFailure() {
	            // TODO Auto-generated method stub
	            
            }});
		
	}
	void doOpen(Material mat){
		super.openMaterial(mat);
	}
	
	void doUpload(Material mat){
		super.upload(mat);
	}
	
	private native void getBase64(String title, NativeSaveCallback nsc)/*-{
		if($wnd.android && $wnd.android.getBase64){
			$wnd.android.getBase64(title,function(jsString){
	    		nsc.@geogebra.touch.NativeSaveCallback::onSuccess(Ljava/lang/String;)(jsString);
	    	});
		}
	}-*/;
	
	






	@Override
    public void saveFile(String base64,final SaveCallback cb) {
	 
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
    public native void uploadUsersMaterials() /*-{
		var that = this;
	    if($wnd.android && $wnd.android.getFiles){
	    	$wnd.android.getFiles(function(jsString){
	    		that.@geogebra.touch.WinFileManager::uploadMaterials(Ljava/lang/String;)(jsString);
	    	});
	    }
	    
    }-*/;

	private void uploadMaterials(String jsString){
		JSONArray jv = JSONParser.parseLenient(jsString).isArray();
		for(int i = 0; i < jv.size(); i++){
			final Material mat = JSONparserGGT.toMaterial( jv.get(i).isObject());
			if ("".equals(mat.getAuthor()) || mat.getAuthor().equals(getApp().getLoginOperation().getUserName())) {
				if (mat.getId() == 0) {
					upload(mat);
				} else {
					sync(mat);
				}
			}
			
		}
		
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







	@Override
    public void setTubeID(int localID, int id) {
	    // TODO Auto-generated method stub
	    
    }
	
	
	
	
}