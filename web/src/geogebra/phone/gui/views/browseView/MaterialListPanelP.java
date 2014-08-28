package geogebra.phone.gui.views.browseView;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.gui.browser.MaterialListElement;
import geogebra.html5.gui.browser.MaterialListPanel;
import geogebra.html5.gui.browser.SearchPanel;
import geogebra.html5.gui.browser.SearchPanel.SearchListener;
import geogebra.html5.gui.laf.GLookAndFeel;
import geogebra.html5.main.AppWeb;
import geogebra.touch.gui.browser.MaterialListPanelT;

import com.google.gwt.user.client.Window;

public class MaterialListPanelP extends MaterialListPanelT {
	private SearchPanel searchPanel;
	
	public MaterialListPanelP(final AppWeb app) {
	    super(app);
		this.setPixelSize(Window.getClientWidth(), Window.getClientHeight() - GLookAndFeel.PHONE_HEADER_HEIGHT);
		addSearchPanel();
    }

	private void addSearchPanel() {
		this.searchPanel = new SearchPanel(app);
		this.searchPanel.addSearchListener(new SearchListener() {
          @Override
			public void onSearch(final String query) {
        	  MaterialListPanelP.this.displaySearchResults(query);
			}
		});
		this.add(this.searchPanel);
	}
	
	@Override
	public void addMaterial(final Material mat) {
		final MaterialListElement preview = new MaterialListElementP(mat, this.app);
		this.materials.add(preview);
		this.insert(preview, 0);
	}

	
	@Override
	public void onResize() {
		this.setPixelSize(Window.getClientWidth(), Window.getClientHeight() - GLookAndFeel.PHONE_HEADER_HEIGHT);
		for (final MaterialListElement elem : this.materials) {
			elem.onResize();
		}
	}
	
	/**
	 * clears the list of existing {@link MaterialListElement materials} and the {@link MaterialListPanel preview-panel}
	 */
	@Override
	public void clearMaterials() {
		super.clearMaterials();
		this.add(this.searchPanel);
	}
}
