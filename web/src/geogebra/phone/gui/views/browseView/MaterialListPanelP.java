package geogebra.phone.gui.views.browseView;

import geogebra.html5.main.AppW;
import geogebra.web.gui.browser.MaterialListElement;
import geogebra.web.gui.browser.MaterialListPanel;
import geogebra.web.gui.browser.SearchPanel;
import geogebra.web.gui.browser.SearchPanel.SearchListener;
import geogebra.web.gui.laf.GLookAndFeel;

import com.google.gwt.user.client.Window;

public class MaterialListPanelP extends MaterialListPanel {
	private SearchPanel searchPanel;
	
	public MaterialListPanelP(final AppW app) {
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
