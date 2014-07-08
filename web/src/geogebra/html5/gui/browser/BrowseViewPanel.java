package geogebra.html5.gui.browser;

import geogebra.common.main.App;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.gui.ResizeListener;
import geogebra.html5.gui.browser.SearchPanel.SearchListener;
import geogebra.html5.main.AppWeb;
import geogebra.html5.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.html5.move.ggtapi.models.MaterialCallback;

import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * 
 * The container for the {@link SearchPanel} and the {@link MaterialListPanel}
 */
public class BrowseViewPanel extends FlowPanel implements ResizeListener {
	
	protected final int SEARCH_PANEL_HEIGHT = 40;
	
	protected AppWeb app;
	
	protected String lastQuery;
	/**
	 * last selected {@link MaterialListElement material}
	 */
	protected MaterialListElement lastSelected;
	/**
	 * a panel with available {@link MaterialListElement materials}
	 */
	protected MaterialListPanel filePanel;
	/** */
	protected FlowPanel container;
	
	protected SearchPanel searchPanel;
	
	public BrowseViewPanel(AppWeb app) {
		this.setStyleName("contentPanel");
		//FIXME do this with LAF
		this.setPixelSize(Window.getClientWidth()-70, Window.getClientHeight()-61);
		this.app = app;
		
		this.container = new FlowPanel();
		this.add(this.container);
		
		addSearchPanel();
		addFilePanel();
	}

	private void addSearchPanel() {
		this.searchPanel = new SearchPanel(app);
		this.searchPanel.addSearchListener(new SearchListener() {
          @Override
			public void onSearch(final String query) {
				BrowseViewPanel.this.displaySearchResults(query);
			}
		});
		this.container.add(this.searchPanel);
	}

	protected void addFilePanel() {
		this.filePanel = new MaterialListPanel(this.app);
		this.container.add(this.filePanel);
	}

	public void loadFeatured() {
		this.lastQuery = null;
		MaterialCallback rc = new MaterialCallback() {
			@Override
			public void onError(final Throwable exception) {
				// FIXME implement Error Handling!
				exception.printStackTrace();
				App.debug(exception.getMessage());
			}

			@Override
			public void onLoaded(final List<Material> response) {
				onSearchResults(response);
			}
		};
		GeoGebraTubeAPIW api = (GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI();
		
		if(app.getLoginOperation().isLoggedIn()){
			api.getUsersMaterials(app.getLoginOperation().getModel().getUserId(), rc);
		}else{
			api.getFeaturedMaterials(rc);
		}
	}

	public void onSearchResults(final List<Material> response) {
		this.filePanel.setMaterials(response);		
	}
	
	public MaterialListElement getChosenMaterial() {
		return this.lastSelected;
	}

	public void unselectMaterials() {
		if (this.lastSelected != null) {
			this.lastSelected.markUnSelected();
		}
	}

	public void rememberSelected(final MaterialListElement materialElement) {
		this.lastSelected = materialElement;
	}
	
	public void displaySearchResults(final String query) {
		this.lastQuery = query;
		((GeoGebraTubeAPIW) this.app.getLoginOperation().getGeoGebraTubeAPI()).search(
				query, new MaterialCallback() {
					@Override
					public void onError(final Throwable exception) {
						// FIXME implement Error Handling!
						exception.printStackTrace();
						App.debug(exception.getMessage());
					}

					@Override
					public void onLoaded(final List<Material> response) {
						onSearchResults(response);
					}
				});
	}
	
	@Override
	public void onResize() {
		//FIXME do this with LAF
		this.setPixelSize(Window.getClientWidth()-70, Window.getClientHeight()-61);
		this.filePanel.onResize();
	}

	public void setLabels() {
		this.filePanel.setLabels();
	}
}
