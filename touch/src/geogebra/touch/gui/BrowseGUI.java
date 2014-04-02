package geogebra.touch.gui;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.views.BooleanRenderable;
import geogebra.html5.gui.ResizeListener;
import geogebra.html5.main.AppWeb;
import geogebra.html5.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.html5.move.ggtapi.models.MaterialCallback;
import geogebra.touch.FileManagerT;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.elements.ggt.FileContainer;
import geogebra.touch.gui.elements.ggt.MaterialListElement;
import geogebra.touch.gui.elements.ggt.VerticalMaterialPanel;
import geogebra.touch.gui.elements.header.BrowseHeaderPanel;
import geogebra.touch.gui.elements.header.BrowseHeaderPanel.SearchListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * GeoGebraTube Search and Browse GUI
 * 
 */
public class BrowseGUI extends HeaderPanel implements BooleanRenderable {

	private final List<ResizeListener> resizeListeners = new ArrayList<ResizeListener>();
	private BrowseHeaderPanel header;
	private final FileManagerT fm;
	final AppWeb app;
	private String lastQuery;
	private MaterialListElement lastSelected;

	// HorizontalMaterialPanel featuredMaterials;
	private VerticalMaterialPanel localFilePanel, tubeFilePanel;
	private FileContainer localFileContainer, tubeFileContainer;

	private final Map<String, Material> localList = new HashMap<String, Material>();
	private List<Material> tubeList = new ArrayList<Material>();

	public final static int HEADING_HEIGHT = 50;
	public final static int CONTROLS_HEIGHT = 50;

	/**
	 * Sets the viewport and other settings, creates a link element at the end
	 * of the head, appends the css file and initializes the GUI elements.
	 * 
	 * @param app
	 */
	public BrowseGUI(final AppWeb app) {
		this.setStyleName("browsegui");
		this.fm = ((TouchApp) app).getFileManager();
		this.app = app;
		this.app.getNetworkOperation().getView().add(this);
		addHeader();
		addContent();

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(final ResizeEvent event) {
				BrowseGUI.this.updateViewSizes();
			}
		});

	}

	void updateViewSizes() {
		for (final ResizeListener res : this.resizeListeners) {
			res.onResize();
		}
	}

	private void addHeader() {
		this.header = TouchEntryPoint.getLookAndFeel().buildBrowseHeader(this);
		this.header.addSearchListener(new SearchListener() {
			@Override
			public void onSearch(final String query) {
				BrowseGUI.this.displaySearchResults(query);
			}
		});
	}

	private void addContent() {
		initLocalFilePanel();
		initTubeFilePanel();

		final HorizontalPanel fileList = new HorizontalPanel();
		fileList.add(this.localFileContainer);
		fileList.add(this.tubeFileContainer);
		this.setContentWidget(fileList);
	}

	private void initTubeFilePanel() {
		this.tubeFilePanel = new VerticalMaterialPanel(this.app);
		this.tubeFileContainer = new FileContainer("GeoGebraTube",
				this.tubeFilePanel);
		this.tubeFileContainer.setVisible(false);
		this.tubeFileContainer.setStyleName("tubeFilePanel");
		this.addResizeListener(this.tubeFileContainer);
		this.addResizeListener(this.tubeFilePanel);
	}

	private void initLocalFilePanel() {
		this.localFilePanel = new VerticalMaterialPanel(this.app);
		this.localFileContainer = new FileContainer(
				this.app.getMenu("MyProfile"), this.localFilePanel);
		this.localFileContainer.setVisible(false);
		this.localFileContainer.addStyleName("localFilePanel");
		this.addResizeListener(this.localFileContainer);
		this.addResizeListener(this.localFilePanel);
	}

	void displaySearchResults(final String query) {
		this.lastQuery = query;
		this.localList.clear();
		this.fm.search(query);
		((GeoGebraTubeAPIW) BrowseGUI.this.app.getLoginOperation()
				.getGeoGebraTubeAPI()).search(query, new MaterialCallback() {
			@Override
			public void onError(final Throwable exception) {
				// FIXME implement Error Handling!
				BrowseGUI.this.updateGUI();
				exception.printStackTrace();
			}

			@Override
			public void onLoaded(final List<Material> response) {
				onSearchResults(response);
				updateGUI();
			}
		});
	}

	public void addToLocalList(final Material mat) {
		final String fileName = mat.getURL();
		this.localList.put(fileName, mat);
		updateGUI();
	}

	public void removeFromLocalList(final Material mat) {
		this.localList.remove(mat.getURL());
		updateGUI();
	}

	public void loadFeatured() {
		this.lastQuery = null;
		this.fm.getAllFiles();
		// doesn't work (2.4.14)
		((GeoGebraTubeAPIW) BrowseGUI.this.app.getLoginOperation()
				.getGeoGebraTubeAPI())
				.getFeaturedMaterials(new MaterialCallback() {
					@Override
					public void onError(final Throwable exception) {
						BrowseGUI.this.updateGUI();
					}

					@Override
					public void onLoaded(final List<Material> response) {
						onSearchResults(response);
					}
				});
	}

	void onSearchResults(final List<Material> response) {
		this.tubeList = response;
		updateGUI();
	}

	public void addResizeListener(final ResizeListener rl) {
		this.resizeListeners.add(rl);
	}

	public void setLabels() {
		this.header.setLabels();
		this.tubeFilePanel.setLabels();
		this.localFilePanel.setLabels();
		this.localFileContainer.setHeading(this.app.getMenu("MyProfile"));
	}

	void updateGUI() {
		if (this.tubeList.isEmpty()) {
			this.localFilePanel.setMaterials(2, new ArrayList<Material>(
					this.localList.values()));
			this.tubeFileContainer.setVisible(false);
			this.localFileContainer.setVisible(true);
		} else if (this.localList.isEmpty()) {
			this.tubeFilePanel.setMaterials(2, this.tubeList);
			this.localFileContainer.setVisible(false);
			this.tubeFileContainer.setVisible(true);
		} else {
			this.localFilePanel.setMaterials(1, new ArrayList<Material>(
					this.localList.values()));
			this.tubeFilePanel.setMaterials(1, this.tubeList);
			this.tubeFileContainer.setVisible(true);
			this.localFileContainer.setVisible(true);
		}
		updateViewSizes();
	}

	@Override
	public void render(final boolean b) {
		if (!b) {
			this.tubeList.clear();
			updateGUI();
		} else if (this.lastQuery != null) {
			this.displaySearchResults(this.lastQuery);
		} else {
			this.loadFeatured();
		}
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
}
