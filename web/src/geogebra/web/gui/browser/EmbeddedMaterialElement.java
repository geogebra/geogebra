package geogebra.web.gui.browser;

import geogebra.common.move.ggtapi.models.Chapter;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.MaterialType;
import geogebra.html5.main.AppW;
import geogebra.web.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.web.move.ggtapi.models.MaterialCallback;

import java.util.ArrayList;
import java.util.List;

public class EmbeddedMaterialElement extends MaterialListElement {
	
	public EmbeddedMaterialElement(final Material m, final AppW app, final boolean isLocal) {
		super(m, app, isLocal);
	}
	@Override
    public String getInsertWorksheetTitle(Material m) {
	    return m.getType() == MaterialType.book ? null : "insert_worksheet";
    }
	
	@Override
    public void onView() {
		((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI()).getItem(getMaterial().getId()+"", new MaterialCallback(){

			@Override
			        public void onLoaded(List<Material> parseResponse,
			                ArrayList<Chapter> meta) {
	            loadNative(parseResponse.get(0).toJson().toString());
	            
            }

			private native void loadNative(String data) /*-{
		if ($wnd.loadWorksheet) {
			$wnd.loadWorksheet(JSON.parse(data));
		}
	}-*/;
       });
    }

}
