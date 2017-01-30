package org.geogebra.web.tablet;

import org.geogebra.common.main.Feature;
import org.geogebra.common.move.ggtapi.models.JSONParserGGT;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.MaterialFilter;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.touch.FileManagerT;
import org.geogebra.web.web.main.FileManager;

public class TabletFileManager extends FileManagerT {

	public TabletFileManager(AppW tabletApp) {
		super(tabletApp);
		if (app.has(Feature.TABLET_WITHOUT_CORDOVA)){
			exportJavascriptMethods();
		}
	}
	
	@Override
	protected void getFiles(final MaterialFilter filter) {
		if (app.has(Feature.TABLET_WITHOUT_CORDOVA)){
			getFilesFilter = filter;
			getFilesNative();
		}else{
			super.getFiles(filter);
		}
	}
	
	private MaterialFilter getFilesFilter = null;
	
	private native void getFilesNative() /*-{
		if ($wnd.android) {
			$wnd.android.getMetaDatas();
		}
	}-*/;
	
	private native void exportJavascriptMethods() /*-{
	var that = this;
		$wnd.tabletFileManager_catchMetaDatas = $entry(function(data) {			
			that.@org.geogebra.web.tablet.TabletFileManager::catchMetaDatas(Ljava/lang/String;)(data);
		});
	}-*/;
	
	/**
	 * this method is called through js (see exportJavascriptMethods())
	 */
	public void catchMetaDatas(String data) {

		JSONTokener tokener = new JSONTokener(data);
		try {
			JSONArray arr = new JSONArray(tokener);
			for (int i = 0; i < arr.length(); i+=2){
				String name = (String) arr.get(i);
				JSONObject metaDatas = (JSONObject) arr.get(i+1);
				Material mat = JSONParserGGT.prototype.toMaterial(metaDatas);
				
                if (mat == null) {
	                mat = new Material(
	                        0,
	                        MaterialType.ggb);
	                mat.setTitle(getTitleFromKey(name));
                }

                mat.setLocalID(FileManager.getIDFromKey(name));

                if (getFilesFilter.check(mat)) {
	                addMaterial(mat);
                }
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
