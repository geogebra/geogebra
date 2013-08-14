package geogebra.touch.gui;

import geogebra.common.main.App;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.html5.util.ggtapi.GeoGebraTubeAPI;
import geogebra.html5.util.ggtapi.JSONparserGGT;
import geogebra.touch.FileManagerM;
import geogebra.touch.TouchApp;
import geogebra.touch.gui.elements.ggt.FileContainer;
import geogebra.touch.gui.elements.ggt.MaterialListElement;
import geogebra.touch.gui.elements.ggt.SearchBar;
import geogebra.touch.gui.elements.ggt.SearchBar.SearchListener;
import geogebra.touch.gui.elements.ggt.VerticalMaterialPanel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * GeoGebraTube Search and Browse GUI
 * 
 */
public class BrowseGUI extends VerticalPanel {
	private SearchBar searchBar;
	private final FileManagerM fm;
	private final AppWeb app;

	// HorizontalMaterialPanel featuredMaterials;
	private VerticalMaterialPanel localFilePanel, tubeFilePanel;
	private VerticalPanel localFileContainer, tubeFileContainer;

	private List<Material> localList = new ArrayList<Material>();
	private List<Material> tubeList = new ArrayList<Material>();

	private Label headingMyProfile;
	private Label headingGeoGebraTube;

	private final static int HEADING_HEIGHT = 50;
	public final static int CONTROLS_HEIGHT = 50;

	/**
	 * Sets the viewport and other settings, creates a link element at the end
	 * of the head, appends the css file and initializes the GUI elements.
	 */
	/**
	 * 
	 */
	public BrowseGUI(final AppWeb app) {
		this.setStyleName("tubesearchgui");
		this.fm = ((TouchApp) app).getFileManager();
		this.app = app;

		addSearchBar();
		addContent();

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(final ResizeEvent event) {
				BrowseGUI.this.onResize();
			}
		});
	}

	private void addSearchBar() {
		this.searchBar = new SearchBar(this.app.getLocalization(), this);
		this.searchBar.addSearchListener(new SearchListener() {
			@Override
			public void onSearch(final String query) {
				BrowseGUI.this.displaySearchResults(query);
			}
		});
		this.add(this.searchBar);
	}

	private void addContent() {
		initLocalFilePanel();
		initTubeFilePanel();

		final HorizontalPanel fileList = new HorizontalPanel();
		fileList.add(this.localFileContainer);
		fileList.add(this.tubeFileContainer);
		this.add(fileList);

		this.loadFeatured();
	}

	private void initTubeFilePanel() {
		this.headingGeoGebraTube = new Label();
		this.headingGeoGebraTube.setStyleName("filePanelTitle");
		this.headingGeoGebraTube.setText("GeoGebraTube");

		this.tubeFilePanel = new VerticalMaterialPanel(this.app);
		this.tubeFilePanel.setStyleName("filePanel");
		this.tubeFileContainer = new FileContainer("tubeFilePanel",
				this.headingGeoGebraTube, this.tubeFilePanel);
	}

	private void initLocalFilePanel() {
		this.headingMyProfile = new Label();
		this.headingMyProfile.setStyleName("filePanelTitle");

		this.localFilePanel = new VerticalMaterialPanel(this.app);
		this.localFilePanel.setStyleName("filePanel");
		this.localFileContainer = new FileContainer("localFilePanel",
				this.headingMyProfile, this.localFilePanel);
	}

	protected void displaySearchResults(final String query) {
		this.localList = this.fm.search(query);
		GeoGebraTubeAPI.getInstance(
				geogebra.common.move.ggtapi.models.GeoGebraTubeAPI.url).search(
				query, new RequestCallback() {
					@Override
					public void onError(final Request request,
							final Throwable exception) {
						// FIXME implement Error Handling!
						BrowseGUI.this.updateGUI();
						exception.printStackTrace();
					}

					@Override
					public void onResponseReceived(final Request request,
							final Response response) {
						App.debug(response.getText());
						onSearchResults(response);
					}
				});
	}

	public MaterialListElement getChosenMaterial() {
		return this.tubeFilePanel.getChosenMaterial();
	}

	public void loadFeatured() {
		this.localList = this.fm.getAllFiles();
		GeoGebraTubeAPI.getInstance(
				geogebra.common.move.ggtapi.models.GeoGebraTubeAPI.url)
				.getFeaturedMaterials(new RequestCallback() {
					@Override
					public void onError(final Request request,
							final Throwable exception) {
						BrowseGUI.this.updateGUI();
					}

					@Override
					public void onResponseReceived(final Request request,
							final Response response) {
						onSearchResults(response);
					}
				});
	}

	protected void onSearchResults(Response response) {
		this.tubeList = JSONparserGGT.parseResponse(response.getText());
		this.updateGUI();
	}

	public void onResize() {
		this.localFilePanel.updateWidth();
		this.tubeFilePanel.updateWidth();

		this.localFileContainer.setHeight(Window.getClientHeight()
				- this.searchBar.getOffsetHeight() + "px");
		this.tubeFileContainer.setHeight(Window.getClientHeight()
				- BrowseGUI.this.searchBar.getOffsetHeight() + "px");
		this.localFilePanel.setHeight(Window.getClientHeight()
				- this.searchBar.getOffsetHeight() - HEADING_HEIGHT
				- CONTROLS_HEIGHT + "px");
		this.tubeFilePanel.setHeight(Window.getClientHeight()
				- this.searchBar.getOffsetHeight() - HEADING_HEIGHT
				- CONTROLS_HEIGHT + "px");

		// Scheduler.get().scheduleDeferred(new ScheduledCommand() {
		//
		// @Override
		// public void execute() {
		// BrowseGUI.this.localFileContainer.setHeight(Window
		// .getClientHeight()
		// - BrowseGUI.this.searchBar.getOffsetHeight() + "px");
		// BrowseGUI.this.tubeFileContainer.setHeight(Window
		// .getClientHeight()
		// - BrowseGUI.this.searchBar.getOffsetHeight() + "px");
		// BrowseGUI.this.localFilePanel.setHeight(Window
		// .getClientHeight()
		// - BrowseGUI.this.searchBar.getOffsetHeight()
		// - HEADING_HEIGHT - CONTROLS_HEIGHT + "px");
		// BrowseGUI.this.tubeFilePanel.setHeight(Window.getClientHeight()
		// - BrowseGUI.this.searchBar.getOffsetHeight()
		// - HEADING_HEIGHT - CONTROLS_HEIGHT + "px");
		// }
		// });
	}

	/**
	 * Loads the featured materials
	 */
	public void reloadLocalFiles() {
		this.localList = this.fm.getAllFiles();
		this.updateGUI();
	}

	public void setLabels() {
		this.searchBar.setLabels();
		this.tubeFilePanel.setLabels();
		this.localFilePanel.setLabels();
		this.headingMyProfile.setText(this.app.getMenu("MyProfile"));
	}

	protected void updateGUI() {
		if (this.tubeList.isEmpty()) {
			this.localFilePanel.setMaterials(2, this.localList);
			this.tubeFileContainer.setVisible(false);
			this.localFileContainer.setVisible(true);
		} else if (this.localList.isEmpty()) {
			this.tubeFilePanel.setMaterials(2, this.tubeList);
			this.localFileContainer.setVisible(false);
			this.tubeFileContainer.setVisible(true);
		} else {
			this.localFilePanel.setMaterials(1, this.localList);
			this.tubeFilePanel.setMaterials(1, this.tubeList);
			this.tubeFileContainer.setVisible(true);
			this.localFileContainer.setVisible(true);
		}
	}

	public void updateNextPrevButtons() {
		if (!this.localList.isEmpty()) {
			((FileContainer) this.localFileContainer).updateNextPrevButtons();
		}
		if (!this.tubeList.isEmpty()) {
			((FileContainer) this.tubeFileContainer).updateNextPrevButtons();
		}
	}
}
