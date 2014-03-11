package geogebra.html5.gui.laf;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.move.ggtapi.models.GeoGebraTubeAPI;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.html5.move.ggtapi.models.MaterialCallback;
import geogebra.web.gui.menubar.GeoGebraMenuW;
import geogebra.web.gui.menubar.GeoGebraMenubarSMART;
import geogebra.web.main.AppW;

import java.util.List;

import com.google.gwt.user.client.Window;

/**
 * @author geogebra
 * Look and Feel for SMART
 *
 */
public class SmartLookAndFeel extends GLookAndFeel{
	@Override
    public GeoGebraMenuW getMenuBar(AppW app) {
		GeoGebraMenubarSMART menubar = new GeoGebraMenubarSMART(app);
		Window.addResizeHandler(menubar);
		return menubar;
    }
	
	@Override
    public boolean undoRedoSupported() {
	    return false;
    }
	
	@Override
    public boolean isSmart() {
		return true;
	}
	
	public void setCloseMessage(final App appl) {
		//no message on smart board
	}

	@Override
    public void setCloseMessage(Localization loc) {
	    //no close message for SMART
    }

	@Override
    public void open(int id) {
	    GeoGebraTubeAPIW.getInstance(GeoGebraTubeAPI.url).getItem(id, new MaterialCallback(){

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
