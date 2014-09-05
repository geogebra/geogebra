package geogebra.phone.gui.views.browseView;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.MaterialType;
import geogebra.common.util.StringUtil;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.main.AppW;
import geogebra.phone.AppP;
import geogebra.phone.Phone;
import geogebra.phone.gui.views.ViewsContainer.View;
import geogebra.web.gui.browser.MaterialListElement;
import geogebra.web.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.web.move.ggtapi.models.MaterialCallback;

import java.util.List;

public class MaterialListElementP extends MaterialListElement {

	public MaterialListElementP(final Material m, final AppW app, final boolean isLocal) {
		super(m, app, isLocal);
	}

	@Override
	protected void closeBrowseView() {
		Phone.getGUI().scrollTo(View.Graphics);
	}

	@Override
	protected void markSelected() {
		this.state = State.Selected;
		Phone.getGUI().getMaterialListPanel().disableMaterials();
		Phone.getGUI().getMaterialListPanel().rememberSelected(this);

		this.addStyleName("selected");
	}
	
	@Override
	protected void onEdit() {
		Phone.getGUI().getMaterialListPanel().disableMaterials();		
		if (!isLocal) {
			if(material.getType() == MaterialType.book){
				((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI()).getBookItems(material.getId(), new MaterialCallback(){

					@Override
					public void onLoaded(final List<Material> response) {
						Phone.getGUI().getMaterialListPanel().clearMaterials();
						Phone.getGUI().getMaterialListPanel().onSearchResults(response);
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
			((AppP) this.app).getFileManager().openMaterial(this.material);
		}
		closeBrowseView();
	}
	
	@Override
	protected void remove() {
		Phone.getGUI().getMaterialListPanel().removeMaterial(this.material);
		Phone.getGUI().getMaterialListPanel().setDefaultStyle();
		ToolTipManagerW.sharedInstance().showBottomMessage("<html>" + StringUtil.toHTMLString("deleted") + "</html>", true);
	}
	

}
