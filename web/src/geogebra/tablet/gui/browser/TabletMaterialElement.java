package geogebra.tablet.gui.browser;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.MaterialType;
import geogebra.html5.main.AppWeb;
import geogebra.html5.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.html5.move.ggtapi.models.MaterialCallback;
import geogebra.tablet.gui.TabletGuiManager;
import geogebra.touch.gui.browser.MaterialListElementT;
import geogebra.touch.main.AppT;

import java.util.List;

public class TabletMaterialElement extends MaterialListElementT {
	
	public TabletMaterialElement(final Material m, final AppWeb app) {
		super(m,app);
	}

	@Override
	protected void onEdit() {
		((TabletGuiManager) app.getGuiManager()).getBrowseGUI().setMaterialsDefaultStyle();		
		if (!isLocalFile()) {
			if(material.getType() == MaterialType.book){
				((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI()).getBookItems(material.getId(), new MaterialCallback(){

					@Override
					public void onLoaded(final List<Material> response) {
						((TabletGuiManager) app.getGuiManager()).getBrowseGUI().clearMaterials();
						((TabletGuiManager) app.getGuiManager()).getBrowseGUI().onSearchResults(response);
					}
				});
				return;
			}
			((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI()).getItem(material.getId(), new MaterialCallback(){

				@Override
				public void onLoaded(final List<Material> parseResponse) {
					app.getGgbApi().setBase64(parseResponse.get(0).getBase64());
				}
			});
		} else {
			((AppT) this.app).getFileManager().openMaterial(this.material, this.app);
		}
		closeBrowseView();
	}
	
	@Override
    public void onView() {
		((TabletGuiManager) app.getGuiManager()).getBrowseGUI().setMaterialsDefaultStyle();
		if (!isLocalFile()) {
			loadNative(getMaterial().getId(), app.getLoginOperation().getModel().getLoginToken());
		}
		
	}
	
	private native void loadNative(int id, String token) /*-{
    	if($wnd.android){
    		$wnd.android.open(id, token);
    	}
	}-*/;
}
