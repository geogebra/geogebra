package geogebra.touch;


import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.MaterialFilter;
import geogebra.html5.main.AppW;
import geogebra.html5.util.ggtapi.JSONparserGGT;
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
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public boolean isAutoSavedFileAvailable() {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
    public void restoreAutoSavedFile() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void deleteAutoSavedFile() {
	    // TODO Auto-generated method stub
	    
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
	    // TODO Auto-generated method stub
	    
    }
	@Override
    public void delete(Material mat) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void saveFile(SaveCallback cb) {
	    // TODO Auto-generated method stub
	    
    }

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