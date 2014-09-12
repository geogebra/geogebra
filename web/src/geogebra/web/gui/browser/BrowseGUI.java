package geogebra.web.gui.browser;

import geogebra.common.main.App;
import geogebra.common.main.OpenFileListener;
import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.ggtapi.events.LogOutEvent;
import geogebra.common.move.ggtapi.events.LoginEvent;
import geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.views.BooleanRenderable;
import geogebra.common.move.views.EventRenderable;
import geogebra.html5.gui.BrowseGuiI;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.ResizeListener;
import geogebra.html5.gui.StandardButton;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.main.AppW;
import geogebra.web.gui.MyHeaderPanel;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.laf.GLookAndFeel;
import geogebra.web.move.ggtapi.models.MaterialCallback;
import geogebra.web.move.ggtapi.operations.LoginOperationW;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * GeoGebraTube Search and Browse GUI
 * 
 */
public class BrowseGUI extends MyHeaderPanel implements BooleanRenderable, EventRenderable, OpenFileListener, BrowseGuiI {
  
	protected final List<ResizeListener> resizeListeners = new ArrayList<ResizeListener>();
	private BrowseHeaderPanel header;
	protected MaterialListPanel materialListPanel;
	protected HorizontalPanel container;
	
	private FlowPanel providerPanel;
	private StandardButton locationTube;
	private StandardButton locationDrive;
	private StandardButton locationSkyDrive;
	protected final AppW app;

	
	public class MyButton extends FlowPanel {
		public MyButton(final BrowseGUI bg) {
			super();
			this.setStyleName("button");
			final Image icon = new Image(BrowseResources.INSTANCE.location_local());
			final Element span = DOM.createElement("span");
			span.setAttribute(
			        "style",
			        "position: absolute; width: 50px; height: 50px; padding: 10px; top: 0px; left: 0px; overflow: hidden;");
			span.setInnerHTML("<img src=\"" + icon.getUrl() + "\"/>");
			final Element input = DOM.createElement("input");
			input.setAttribute("type", "file");
			input.setAttribute(
			        "style",
			        "width: 500px; height: 60px; font-size: 56px;"
			                + "opacity: 0; position: absolute; right: 0px; top: 0px; cursor: pointer;");
			span.appendChild(input);
	
			DOM.insertChild(getElement(), span, 0);
			addGgbChangeHandler(input, bg);
		}
	
		public native void addGgbChangeHandler(Element el, BrowseGUI bg) /*-{
			var dialog = this;
			//		el.setAttribute("accept", "application/vnd.geogebra.file, application/vnd.geogebra.tool");
			el.onchange = function(event) {
				var files = this.files;
				if (files.length) {
					var fileToHandle = files[0];
					bg.@geogebra.web.gui.browser.BrowseGUI::openFileAsGgb(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(fileToHandle);
				}
	
			};
		}-*/;
	}
	
	/**
	 * 
	 * @param app
	 */
	public BrowseGUI(final AppW app) {
		this.setStyleName("browsegui");
		
		this.app = app;
		this.app.getNetworkOperation().getView().add(this);
		if(this.app.getLoginOperation() == null){
			this.app.initSignInEventFlow(new LoginOperationW(app));
		}
		this.app.getLoginOperation().getView().add(this);
		
		this.container = new HorizontalPanel();
		this.container.setPixelSize(Window.getClientWidth(), Window.getClientHeight() - GLookAndFeel.BROWSE_HEADER_HEIGHT);
		this.container.setStyleName("content");
		
		initMaterialListPanel();
		
		addHeader();
		addContent();

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(final ResizeEvent event) {
				BrowseGUI.this.updateViewSizes();
			}
		});
		
		app.registerOpenFileListener(this);
	}

	protected void initMaterialListPanel() {
		this.materialListPanel = new MaterialListPanel(app);
		this.addResizeListener(this.materialListPanel);
    }

	private void addHeader() {
		this.header = new BrowseHeaderPanel(app, this, app.getNetworkOperation());
		this.setHeaderWidget(this.header);
		this.addResizeListener(this.header);
	}

	/**
	 * Initialize the providers panel, needs to be done after google / MS login
	 */
	void initProviders() {
		this.providerPanel = new FlowPanel();
		this.providerPanel.setHeight(Window.getClientHeight() - GLookAndFeel.BROWSE_HEADER_HEIGHT + "px");
		providerPanel.clear();
		locationTube = new StandardButton(
		        BrowseResources.INSTANCE.location_tube());
		locationTube.addFastClickHandler(new FastClickHandler() {
			
			@Override
			public void onClick() {
				loadAllMaterials();
			}
		});
		providerPanel.add(locationTube);

		final MyButton locationLocal = new MyButton(this);// StandardButton(AppResources.INSTANCE.folder());
		providerPanel.add(locationLocal);
		// TODO: Only visible if user is logged in with google Account
		final GeoGebraTubeUser user = this.app.getLoginOperation().getModel()
		        .getLoggedInUser();
		if (user != null && user.hasGoogleDrive() && !app.getLAF().isSmart()) {
			locationDrive = new StandardButton(
			        BrowseResources.INSTANCE.location_drive());
			providerPanel.add(locationDrive);
			locationDrive.addFastClickHandler(new FastClickHandler() {

				@Override
				public void onClick() {
					if(BrowseGUI.this.app.getGoogleDriveOperation()!=null){
					 BrowseGUI.this.app.getGoogleDriveOperation().requestPicker();
					}
				}
			});
		} else if (user != null) {
			App.debug(user.getIdentifier());
		}
		
		// TODO: Only visible if user is logged in with google Account
		if (user != null && user.hasOneDrive()) {
			locationSkyDrive = new StandardButton(
			        BrowseResources.INSTANCE.location_skydrive());
			this.providerPanel.add(locationSkyDrive);
		}

		// Set Tube as the active on
		locationTube.addStyleName("selected");
	}
	
	protected void addContent() {		
		this.materialListPanel = new MaterialListPanel(app);
		this.addResizeListener(this.materialListPanel);
		this.container.add(this.materialListPanel);
		
		initProviders();
		this.providerPanel.setStyleName("providers");
		this.container.add(providerPanel);
		
		this.setContentWidget(this.container);
	}

	public void loadAllMaterials() {	
		this.header.clearSearchPanel();
		this.materialListPanel.loadAllMaterials();
	}
	
	public void onSearchResults(final List<Material> response) {		
		this.materialListPanel.addGGTMaterials(response);
	}
	
	/**
	 * adds a local material
	 * @param mat {@link Material}
	 */
	public void addMaterial(final Material mat) {
		this.materialListPanel.addMaterial(mat, false, true);
	}
	
	public void removeMaterial(final Material mat) {
		this.materialListPanel.removeMaterial(mat);
	}
	
	public void addResizeListener(final ResizeListener rl) {
		this.resizeListeners.add(rl);
	}

	public void setLabels() {
		this.header.setLabels();
		this.materialListPanel.setLabels();
	}


	public MaterialListElement getChosenMaterial() {
		return this.materialListPanel.getChosenMaterial();
	}

	public void disableMaterials() {
	    this.materialListPanel.disableMaterials();
    }
	
	public void setMaterialsDefaultStyle() {
		this.materialListPanel.setDefaultStyle();
	}

	public void rememberSelected(final MaterialListElement materialElement) {
		this.materialListPanel.rememberSelected(materialElement);
	}

	public void setFrame(final GeoGebraAppFrame frame) {
	    super.setFrame(frame);	    
    }
	
	public void openFileAsGgb(final JavaScriptObject fileToHandle,
	        final JavaScriptObject callback){
		app.openFileAsGgb(fileToHandle, callback);		
	}

    /**
     * deletes all files from the {@link MaterialListPanel}
     */
	public void clearMaterials() {
	    this.materialListPanel.clearMaterials();
    }

	@Override
    public void onOpenFile() {
		//For local / GoogleDrive files getLastSelected may be null
		if(getLastSelected() != null){
			final Material material = getLastSelected().getMaterial();
			app.getKernel().getConstruction().setTitle(material.getTitle());
			app.setSyncStamp(material.getSyncStamp());
			if (getLastSelected().isOwnMaterial) {
				app.setUniqueId(material.getId()+"");
			} else {
				app.resetUniqueId();
			}
		}else{
			app.resetUniqueId(); //TODO
		}
	    setMaterialsDefaultStyle();
	    this.close();
	    ToolTipManagerW.sharedInstance().hideBottomInfoToolTip();
    }
	
	private MaterialListElement getLastSelected() {
		return this.materialListPanel.lastSelected;
	}

	public void displaySearchResults(final String query) {
	    this.materialListPanel.displaySearchResults(query);
    }
	
	protected void updateViewSizes() {
		this.container.setPixelSize(Window.getClientWidth(), Window.getClientHeight() - GLookAndFeel.BROWSE_HEADER_HEIGHT);
		this.providerPanel.setHeight(Window.getClientHeight() - GLookAndFeel.BROWSE_HEADER_HEIGHT + "px");
		for (final ResizeListener res : this.resizeListeners) {
			res.onResize();
		}
	}

	public void refreshMaterial(final Material material, final boolean isLocal) {
	    this.materialListPanel.refreshMaterial(material, isLocal);
    }
	

	@Override
    public void renderEvent(final BaseEvent event) {
	    if(event instanceof LoginEvent || event instanceof LogOutEvent){
	    	initProviders();
	    	if (event instanceof LoginEvent && ((LoginEvent)event).isSuccessful()) {
	    		this.materialListPanel.loadUsersMaterials(new MaterialCallback() {
					
					@Override
					public void onLoaded(final List<Material> parseResponse) {
						uploadLocals();
					}
				});
	    	} else if (event instanceof LogOutEvent) {
	    		this.materialListPanel.removeUsersMaterials();
	    	}
	    }
    }
	
	@Override
    public void render(final boolean online) {
	    if (online) {
	    	this.materialListPanel.loadAllMaterials(new MaterialCallback() {
				
				@Override
				public void onLoaded(final List<Material> parseResponse) {
					uploadLocals();
				}
			});
	    } else {
		    this.clearMaterials();
		    this.app.getFileManager().getUsersMaterials();
	    }
    }
	
	void uploadLocals() {
		if (this.app.getLoginOperation().isLoggedIn()) {
			this.app.getFileManager().uploadUsersMaterials();
		}
    }

}

