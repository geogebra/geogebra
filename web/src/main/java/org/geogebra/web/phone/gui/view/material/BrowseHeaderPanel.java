package org.geogebra.web.phone.gui.view.material;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.phone.gui.view.HeaderPanel;
import org.geogebra.web.web.gui.browser.SearchPanel;

import com.google.gwt.user.client.ui.SimplePanel;

public class BrowseHeaderPanel extends SimplePanel implements HeaderPanel {
	
	private SearchPanel searchPanel;
	
	public BrowseHeaderPanel(AppW app) {
		searchPanel = new SearchPanel(app);
		add(searchPanel);
		/*this.searchPanel.addSearchListener(new SearchListener() {
	          @Override
				public void onSearch(final String query) {
	        	  MaterialListPanelP.this.displaySearchResults(query);
				}
			});*/
		// ADD SEARCH Listener
	}

}
