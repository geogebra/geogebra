package geogebra.tablet.gui.browser;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppW;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.browser.MaterialListElement;

public class TabletMaterialElement extends MaterialListElement {
	
	public TabletMaterialElement(final Material m, final AppW app, final boolean isLocal) {
		super(m, app, isLocal);
	}
	
	@Override
    public void onView() {
		((GuiManagerW) app.getGuiManager()).getBrowseGUI().setMaterialsDefaultStyle();
		if (!isLocal) {
			loadNative(getMaterial().getId(), app.getLoginOperation().getModel().getLoginToken());
		}
		
	}
	
	private native void loadNative(int id, String token) /*-{
    	if($wnd.android){
    		$wnd.android.open(id, token);
    	}
	}-*/;
}
