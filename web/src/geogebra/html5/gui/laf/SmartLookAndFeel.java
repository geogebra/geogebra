package geogebra.html5.gui.laf;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.html5.move.ggtapi.models.MaterialCallback;
import geogebra.web.gui.menubar.GeoGebraMenuW;
import geogebra.web.gui.menubar.MainMenu;
import geogebra.web.main.AppW;

import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * @author geogebra
 * Look and Feel for SMART
 *
 */
public class SmartLookAndFeel extends GLookAndFeel{
	@Override
    public GeoGebraMenuW getMenuBar(AppW app) {
		MainMenu menubar = new MainMenu(app);
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
		RootLayoutPanel.get().getElement().addClassName("AppFrameParent");
    }

	@Override
    public void open(int id, AppW app) {
		((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI()).getItem(id, new MaterialCallback(){

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
	
	@Override
    public String getType() {
	    return "smart";
    }

	@Override
    public boolean copyToClipboardSupported(){
		return false;
	}
	@Override
    public String getLoginListener() {
	    return "loginListener";
    }

	@Override
    public String getInsertWorksheetTitle() {
	    return "insert_worksheet";
    }
}
