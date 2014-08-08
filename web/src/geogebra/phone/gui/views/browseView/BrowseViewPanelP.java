package geogebra.phone.gui.views.browseView;

import geogebra.html5.gui.browser.BrowseViewPanel;
import geogebra.html5.gui.browser.SearchPanel;
import geogebra.html5.gui.browser.SearchPanel.SearchListener;
import geogebra.html5.gui.laf.GLookAndFeel;
import geogebra.html5.main.AppWeb;

import com.google.gwt.user.client.Window;

public class BrowseViewPanelP extends BrowseViewPanel {
	private SearchPanel searchPanel;
	
	public BrowseViewPanelP(AppWeb app) {
	    super(app);
		this.setPixelSize(Window.getClientWidth(), Window.getClientHeight() - GLookAndFeel.PHONE_HEADER_HEIGHT);
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
	
	@Override
	public void onResize() {
		this.setPixelSize(Window.getClientWidth(), Window.getClientHeight() - GLookAndFeel.PHONE_HEADER_HEIGHT);
		this.filePanel.onResize();
	}
	
}
