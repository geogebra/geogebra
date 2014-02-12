package geogebra.html5.gui.browser;

import geogebra.common.main.App;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.views.BooleanRenderable;
import geogebra.html5.gui.ResizeListener;
import geogebra.html5.gui.browser.BrowseHeaderPanel.SearchListener;
import geogebra.html5.main.AppWeb;
import geogebra.html5.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.html5.util.ggtapi.JSONparserGGT;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
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
	private final AppWeb app;
	private String lastQuery;
	private MaterialListElement lastSelected;

	// HorizontalMaterialPanel featuredMaterials;
	private VerticalMaterialPanel localFilePanel, tubeFilePanel;
	private FileContainer localFileContainer, tubeFileContainer;

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
		//this.fm = ((TouchApp) app).getFileManager();
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
		this.header = new BrowseHeaderPanel(app.getLocalization(),this,app.getNetworkOperation());
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
		GeoGebraTubeAPIW.getInstance(
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
						updateViewSizes();
					}
				});
	}

	public void loadFeatured() {
		this.lastQuery = null;
		GeoGebraTubeAPIW.getInstance(
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
						updateViewSizes();
					}
				});
	}

	void onSearchResults(final Response response) {
		this.tubeList = JSONparserGGT.parseResponse(response.getText());
		this.updateGUI();
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
		
			this.tubeFilePanel.setMaterials(2, this.tubeList);
			this.localFileContainer.setVisible(false);
			this.tubeFileContainer.setVisible(true);
		
	}

	@Override
	public void render(boolean b) {
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
