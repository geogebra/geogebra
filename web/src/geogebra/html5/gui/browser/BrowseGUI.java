package geogebra.html5.gui.browser;

import geogebra.common.main.App;
import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.ggtapi.events.LogOutEvent;
import geogebra.common.move.ggtapi.events.LoginEvent;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.MaterialType;
import geogebra.common.move.ggtapi.models.Material.Provider;
import geogebra.common.move.views.BooleanRenderable;
import geogebra.common.move.views.EventRenderable;
import geogebra.html5.gui.MyHeaderPanel;
import geogebra.html5.gui.ResizeListener;
import geogebra.html5.main.AppWeb;
import geogebra.html5.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.html5.move.ggtapi.models.MaterialCallback;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.move.googledrive.operations.GoogleDriveFileHandler;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;

/**
 * GeoGebraTube Search and Browse GUI
 * 
 */
public class BrowseGUI extends MyHeaderPanel implements BooleanRenderable, GoogleDriveFileHandler, EventRenderable {
  
	private final List<ResizeListener> resizeListeners = new ArrayList<ResizeListener>();
	private BrowseHeaderPanel header;
	private final AppWeb app;
	private String lastQuery;
	private MaterialListElement lastSelected;

	// HorizontalMaterialPanel featuredMaterials;
	private VerticalMaterialPanel tubeFilePanel;
	private FileContainer tubeFileContainer;

	private List<Material> tubeList = new ArrayList<Material>();
	private List<Material> googleList = new ArrayList<Material>();
	private List<Material> oneList = new ArrayList<Material>();
	private Provider provider = Provider.TUBE;

	public final static int HEADING_HEIGHT = 61;

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
		this.app.getLoginOperation().getView().add(this);
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
		this.header = new BrowseHeaderPanel(app, this,app.getNetworkOperation());
		
		this.setHeaderWidget(this.header);
		this.addResizeListener(this.header);
	}

	private void addContent() {
		initTubeFilePanel();
		this.setContentWidget(this.tubeFileContainer);
	}

	private void initTubeFilePanel() {
		this.tubeFilePanel = new VerticalMaterialPanel(this.app, this);
		this.tubeFileContainer = new FileContainer("GeoGebraTube",
				this.tubeFilePanel, this, app);
		this.tubeFileContainer.setVisible(false);
		this.tubeFileContainer.setStyleName("tubeFilePanel");
		this.addResizeListener(this.tubeFileContainer);
		this.addResizeListener(this.tubeFilePanel);
	}

	void displaySearchResults(final String query) {
		this.lastQuery = query;
		((GeoGebraTubeAPIW) this.app.getLoginOperation().getGeoGebraTubeAPI()).search(
				query, new MaterialCallback() {
					@Override
					public void onError(final Throwable exception) {
						// FIXME implement Error Handling!
						BrowseGUI.this.updateGUI();
						exception.printStackTrace();
						App.debug(exception.getMessage());
					}

					@Override
					public void onLoaded(final List<Material> response) {
						onSearchResults(response);
						updateViewSizes();
					}
				});
	}

	public void loadFeatured() {
		this.lastQuery = null;
		MaterialCallback rc = new MaterialCallback() {
			@Override
			public void onError(
					final Throwable exception) {
				BrowseGUI.this.updateGUI();
			}

			@Override
			public void onLoaded(final List<Material> response) {
				onSearchResults(response);
				updateViewSizes();
			}
		};
		GeoGebraTubeAPIW api = (GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI();
		
		if(app.getLoginOperation().isLoggedIn()){
			api.getUsersMaterials(app.getLoginOperation().getModel().getUserId(), rc);
		}else{
			api.getFeaturedMaterials(rc);
		}
	}

	void onSearchResults(final List<Material> response) {
		this.tubeList = response;
		this.updateGUI();
	}

	public void addResizeListener(final ResizeListener rl) {
		this.resizeListeners.add(rl);
	}

	public void setLabels() {
		this.header.setLabels();
		this.tubeFilePanel.setLabels();
	}

	void updateGUI() {
			switch(this.provider){
			case TUBE:
				this.tubeFilePanel.setMaterials(2, this.tubeList);
				break;
			case GOOGLE:
				this.tubeFilePanel.setMaterials(2, this.googleList);
				break;
			case ONE:
				this.tubeFilePanel.setMaterials(2, this.oneList);
				break;
			}
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

	

	public void setFrame(GeoGebraAppFrame frame) {
		this.loadFeatured();
	    super.setFrame(frame);
	    
    }
	
	public void openFileAsGgb(JavaScriptObject fileToHandle,
	        JavaScriptObject callback){
		app.openFileAsGgb(fileToHandle, callback);		
		close();
	}

	@Override
    public void show(String title, String author, String date,
            String url, String description, String googleID, String thumbnail) {
		Material m = new Material(-1, MaterialType.ggb);
		m.setTitle(title);
		m.setURL(url);
		m.setAuthor(author);
		m.setDescription(description);
		m.setGoogleID(googleID);
		m.setTimestamp(Long.parseLong(date)/1000);
		m.setThumbnail(thumbnail);
	    this.googleList.add(m);
	    
    }

	@Override
    public void done() {
	    this.updateGUI();
	    
    }

	@Override
    public void clearMaterials() {
	    this.googleList.clear();
    }
	
	public void setProvider(Provider provider){
		this.provider = provider;
	}

	@Override
    public void renderEvent(BaseEvent event) {
	    if(event instanceof LoginEvent || event instanceof LogOutEvent){
	    	loadFeatured();
	    }
    }
}

