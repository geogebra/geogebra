package geogebra.html5.gui.browser;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.MaterialType;
import geogebra.html5.main.AppWeb;
import geogebra.html5.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.html5.move.ggtapi.models.MaterialCallback;

import java.util.List;

public class EmbeddedMaterialElement extends MaterialListElement {
	
	public EmbeddedMaterialElement(final Material m, final AppWeb app) {
		super(m,app);
	}
	@Override
    public String getInsertWorksheetTitle(Material m) {
	    return m.getType() == MaterialType.book ? null : "insert_worksheet";
    }
	
	@Override
    public void onOpen() {
		((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI()).getItem(getMaterial().getId(), new MaterialCallback(){

			@Override
            public void onLoaded(List<Material> parseResponse) {
	            loadNative(parseResponse.get(0).toJson().toString());
	            
            }

			private native void loadNative(String data) /*-{
	            if($wnd.loadWorksheet){
	            	$wnd.loadWorksheet(JSON.parse(data));
	            }
            }-*/;
       });
    }

}
