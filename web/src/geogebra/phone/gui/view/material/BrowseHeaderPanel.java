package geogebra.phone.gui.view.material;

import geogebra.html5.main.AppW;
import geogebra.phone.gui.view.HeaderPanel;
import geogebra.web.gui.browser.SearchPanel;

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
