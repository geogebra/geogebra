package geogebra.touch.gui.views;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.gui.browser.BrowseViewPanel;
import geogebra.html5.main.AppWeb;
import geogebra.html5.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.html5.move.ggtapi.models.MaterialCallback;
import geogebra.touch.FileManagerT;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.elements.ggt.MaterialListPanelT;

import java.util.List;

import com.google.gwt.user.client.Window;

public class BrowseViewPanelT extends BrowseViewPanel {
	
	private final FileManagerT fm;

	public BrowseViewPanelT(AppWeb app) {
		super(app);
		this.fm = ((TouchApp) app).getFileManager();
		this.setStyleName("browsegui");
		this.addStyleName("browseViewPanel");
	}


	@Override
	protected void addFilePanel() {
		this.filePanel = new MaterialListPanelT(this.app);
		this.container.add(this.filePanel);
	}

	public void addToLocalList(final Material mat) {
//		this.localFilePanel.addMaterial(mat);
	}

	public void removeFromLocalList(final Material mat) {
//		this.localFilePanel.removeMaterial(mat);
	}
	
	public void loadFeatured() {
		this.lastQuery = null;
		this.fm.getAllFiles();
		// doesn't work (2.4.14)
		((GeoGebraTubeAPIW) BrowseViewPanelT.this.app.getLoginOperation()
				.getGeoGebraTubeAPI())
				.getFeaturedMaterials(new MaterialCallback() {
					@Override
					public void onError(final Throwable exception) {
					}

					@Override
					public void onLoaded(final List<Material> response) {
						onSearchResults(response);
					}
				});
	}

	public void displaySearchResults(final String query) {
		this.lastQuery = query;
//		this.localFilePanel.clearList();
		this.fm.search(query);
		((GeoGebraTubeAPIW) BrowseViewPanelT.this.app.getLoginOperation()
				.getGeoGebraTubeAPI()).search(query, new MaterialCallback() {
			@Override
			public void onError(final Throwable exception) {
				// FIXME implement Error Handling!
				exception.printStackTrace();
			}

			@Override
			public void onLoaded(final List<Material> response) {
				onSearchResults(response);
			}
		});
	}
	
	@Override
	public void render(final boolean b) {
		if (!b) {
			this.filePanel.clearList();
		} else if (this.lastQuery != null) {
			this.displaySearchResults(this.lastQuery);
		} else {
			this.loadFeatured();
		}
	}

	@Override
	public void onResize() {
		this.setPixelSize(Window.getClientWidth(), TouchEntryPoint.getLookAndFeel().getCanvasHeight());
		this.container.setPixelSize(Window.getClientWidth(), TouchEntryPoint.getLookAndFeel().getCanvasHeight());
		this.searchPanel.setPixelSize(Window.getClientWidth(), this.SEARCH_PANEL_HEIGHT);
		this.filePanel.onResize();
//		this.localFilePanel.onResize();
	}

//	
//	public void setLabels() {
//		this.tubeFilePanel.setLabels();
//		this.localFilePanel.setLabels();
//		this.localFileContainer.setHeading(this.app.getMenu("MyProfile"));
//	}
}
