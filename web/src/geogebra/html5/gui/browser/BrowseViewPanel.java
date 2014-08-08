package geogebra.html5.gui.browser;

import geogebra.common.main.App;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.gui.ResizeListener;
import geogebra.html5.gui.laf.GLookAndFeel;
import geogebra.html5.main.AppWeb;
import geogebra.html5.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.html5.move.ggtapi.models.MaterialCallback;
import geogebra.web.main.AppW;

import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * 
 * The container for the {@link SearchPanel} and the {@link MaterialListPanel}
 */
public class BrowseViewPanel extends FlowPanel implements ResizeListener {
	
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

	private SearchPanel searchPanel;

	
	public BrowseViewPanel(final AppWeb app) {
		this.setStyleName("browseViewPanel");
		this.setPixelSize(Window.getClientWidth() - GLookAndFeel.PROVIDER_PANEL_WIDTH, Window.getClientHeight() - GLookAndFeel.BROWSE_HEADER_HEIGHT);
		this.app = app;
		
		this.container = new FlowPanel();
		this.add(this.container);
		
		addContent();
	}

	protected void addContent() {
		addFilePanel();
	}

	protected void addFilePanel() {
		this.filePanel = new MaterialListPanel(this.app);
		this.container.add(this.filePanel);
	}

	public void loadFeatured() {
		this.lastQuery = null;
		filePanel.clearMaterials();
		//local files
		if (((AppW) app).getFileManager() != null) {
			((AppW) app).getFileManager().getAllFiles();
		}

		final MaterialCallback rc = new MaterialCallback() {
			@Override
			public void onError(final Throwable exception) {
				// FIXME implement Error Handling!
				exception.printStackTrace();
				App.debug("API error"+exception.getMessage());
			}

			@Override
			public void onLoaded(final List<Material> response) {
				App.debug("API success: " + response.size());
				onSearchResults(response);
			}
		};
		final GeoGebraTubeAPIW api = (GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI();
		
		//load userMaterials first
		if(app.getLoginOperation().isLoggedIn()){
			api.getUsersMaterials(app.getLoginOperation().getModel().getUserId(), rc);
		}
//		else{
			api.getFeaturedMaterials(rc);
//		}	
	}

	public void onSearchResults(final List<Material> response) {
		this.filePanel.setMaterials(response);		
	}
	
	public void addMaterial(final Material mat) {
		this.filePanel.addMaterial(mat);
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
		clearMaterials();
		//search local
		if (((AppW) this.app).getFileManager() != null) {
			((AppW) this.app).getFileManager().search(query);
		}
		
		//search GeoGebraTube
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

	public void removeMaterial(final Material mat) {
	    this.filePanel.removeMaterial(mat);
    }

	public void clearMaterials() {
	   this.filePanel.clearMaterials(); 
    }
	
	public void setLabels() {
		this.filePanel.setLabels();
	}

	@Override
	public void onResize() {
		this.setPixelSize(Window.getClientWidth() - GLookAndFeel.PROVIDER_PANEL_WIDTH, Window.getClientHeight() - GLookAndFeel.BROWSE_HEADER_HEIGHT);
		this.filePanel.onResize();
	}
}
