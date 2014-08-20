package geogebra.tablet;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.gui.browser.MaterialListElement;
import geogebra.html5.main.AppWeb;

public class TabletMaterialElement extends MaterialListElement {
	
	public TabletMaterialElement(final Material m, final AppWeb app) {
		super(m,app);
	}
	
	@Override
    public void onOpen() {
		loadNative(getMaterial().getId(), app.getLoginOperation().getModel().getLoginToken());
	}
	
	private native void loadNative(int id, String token) /*-{
    	if($wnd.android){
    		$wnd.android.open(id, token);
    	}
	}-*/;

}
