package geogebra.phone.gui.views.browseView;

import geogebra.html5.gui.browser.BrowseViewPanel;
import geogebra.html5.gui.browser.SearchPanel;
import geogebra.html5.gui.browser.SearchPanel.SearchListener;
import geogebra.html5.main.AppWeb;

public class BrowseViewPanelP extends BrowseViewPanel {
	private final int SEARCH_PANEL_HEIGHT = 40;
	private SearchPanel searchPanel;
	
	public BrowseViewPanelP(AppWeb app) {
	    super(app);
    }

	private void addSearchPanel() {
		this.searchPanel = new SearchPanel(app);
		this.searchPanel.addSearchListener(new SearchListener() {
          @Override
			public void onSearch(final String query) {
				BrowseViewPanelP.this.displaySearchResults(query);
			}
		});
		this.container.add(this.searchPanel);
	}
	
	@Override
	protected void addContent() {
		addSearchPanel();
		addFilePanel();
	}
	
	@Override
	protected void addFilePanel() {
		this.filePanel = new MaterialListPanelP(this.app);
		this.container.add(this.filePanel);
	}
}
