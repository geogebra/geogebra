package geogebra.touch.gui.elements.ggt;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.gui.browser.MaterialListElement;
import geogebra.html5.gui.browser.MaterialListPanel;
import geogebra.html5.main.AppWeb;
import geogebra.touch.TouchEntryPoint;

import com.google.gwt.user.client.Window;

/**
 * The preview panel for {@link MaterialListElementT materials} of GeoGebraTube and the local device
 *
 */
public class MaterialListPanelT extends MaterialListPanel {
	
	public MaterialListPanelT(AppWeb app) {
		super(app);
		this.setSize(Window.getClientWidth() + "px", Window.getClientHeight() - TouchEntryPoint.getLookAndFeel().getHeaderHeight() - 43  + "px");
	}
	
	/**
	 * adds the given material to the list of {@link MaterialListElementT materials} and the preview-panel
	 * 
	 * @param mat
	 */
	@Override
	public void addMaterial(Material mat) {
		final MaterialListElementT preview = new MaterialListElementT(mat, this.app);
		this.materials.add(preview);
		this.add(preview);
	}


	@Override
	public void onResize() {
		this.setSize(Window.getClientWidth() + "px", "100%");
		for (MaterialListElement elem : this.materials) {
			((MaterialListElementT) elem).onResize();
		}
	}
}
