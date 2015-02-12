package geogebra.web.gui.browser;

import geogebra.common.main.App;
import geogebra.common.main.OpenFileListener;
import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.ggtapi.events.LogOutEvent;
import geogebra.common.move.ggtapi.events.LoginEvent;
import geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.Provider;
import geogebra.common.move.ggtapi.models.SyncEvent;
import geogebra.common.move.views.BooleanRenderable;
import geogebra.common.move.views.EventRenderable;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.ResizeListener;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.gui.view.browser.BrowseViewI;
import geogebra.html5.gui.view.browser.MaterialListElementI;
import geogebra.html5.main.AppW;
import geogebra.web.gui.MyHeaderPanel;
import geogebra.web.gui.laf.GLookAndFeel;
import geogebra.web.gui.util.StandardButton;
import geogebra.web.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.web.move.ggtapi.models.SyncCallback;
import geogebra.web.move.ggtapi.operations.LoginOperationW;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * GeoGebraTube Search and Browse GUI
 * 
 */
public class BrowseGUI extends MyHeaderPanel implements BooleanRenderable,
        EventRenderable, OpenFileListener, BrowseViewI {

	protected final List<ResizeListener> resizeListeners = new ArrayList<ResizeListener>();
	private BrowseHeaderPanel header;
	protected MaterialListPanel materialListPanel;
	protected HorizontalPanel container;

	private FlowPanel providerPanel;
	private StandardButton locationTube;
	private StandardButton locationDrive;
	private StandardButton locationSkyDrive;
	private MyButton locationLocal;
	protected final AppW app;

	public class MyButton extends FlowPanel {
		public MyButton(final BrowseGUI bg) {
			super();
			this.setStyleName("button");
			final Image icon = new Image(
			        BrowseResources.INSTANCE.location_local());
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
		if (this.app.getLoginOperation() == null) {
			this.app.initSignInEventFlow(new LoginOperationW(app), false);
		}
		this.app.getLoginOperation().getView().add(this);
		this.container = new HorizontalPanel();
		this.container.setPixelSize(Window.getClientWidth(),
		        Window.getClientHeight() - GLookAndFeel.BROWSE_HEADER_HEIGHT);
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

		this.addDomHandler(new TouchMoveHandler() {
			@Override
			public void onTouchMove(TouchMoveEvent event) {
				// prevent zooming
				if (event.getTouches().length() > 1) {
					event.preventDefault();
					event.stopPropagation();
				}
			}
		}, TouchMoveEvent.getType());
		if (app.getGoogleDriveOperation() != null) {
			app.getGoogleDriveOperation().initGoogleDriveApi();
		}
		if (app.getLoginOperation().isLoggedIn()) {
			sync();
		}
	}

	private void sync() {
		if (!app.getFileManager().isSyncing()) {
		((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI()).sync(
		        app, 0, new SyncCallback() {

			        @Override
			        public void onSync(ArrayList<SyncEvent> events) {
				        App.debug("Start sync upload");
				        app.getFileManager().uploadUsersMaterials(events);

			        }
		        });
		}

	}

	protected void initMaterialListPanel() {
		this.materialListPanel = new MaterialListPanel(app);
		this.addResizeListener(this.materialListPanel);
	}

	private void addHeader() {
		this.header = new BrowseHeaderPanel(app, this,
		        app.getNetworkOperation());
		this.setHeaderWidget(this.header);
		this.addResizeListener(this.header);
	}

	/**
	 * Initialize the providers panel, needs to be done after google / MS login
	 */
	void initProviders() {
		this.providerPanel = new FlowPanel();

		locationTube = new StandardButton(
		        BrowseResources.INSTANCE.location_tube());
		locationTube.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				app.getFileManager().setFileProvider(Provider.TUBE);
				loadAllMaterials();
			}
		});
		this.locationLocal = new MyButton(this);// StandardButton(AppResources.INSTANCE.folder());
		// TODO: Only visible if user is logged in with google Account
		final GeoGebraTubeUser user = this.app.getLoginOperation().getModel()
		        .getLoggedInUser();

		setAvailableProviders();
	}

	private void addDriveButton(GeoGebraTubeUser user) {

		if (locationDrive == null) {
			locationDrive = new StandardButton(
			        BrowseResources.INSTANCE.location_drive());
			locationDrive.addFastClickHandler(new FastClickHandler() {

				@Override
				public void onClick(Widget source) {
					if (BrowseGUI.this.app.getGoogleDriveOperation() != null) {
						app.getFileManager().setFileProvider(Provider.GOOGLE);
						BrowseGUI.this.app.getGoogleDriveOperation()
						        .requestPicker();
					}
				}
			});
		}
		this.providerPanel.add(this.locationDrive);

	}

	private void addOneDriveButton(GeoGebraTubeUser user) {

		if (this.locationSkyDrive == null) {
			this.locationSkyDrive = new StandardButton(
			        BrowseResources.INSTANCE.location_skydrive());
			this.locationSkyDrive.addFastClickHandler(new FastClickHandler() {

				@Override
				public void onClick(Widget source) {
					if (BrowseGUI.this.app.getGoogleDriveOperation() != null) {
						app.getFileManager().setFileProvider(Provider.ONE);
						// TODO open skydrive picker
					}
				}
			});
		}
		this.providerPanel.add(this.locationSkyDrive);

	}

	protected void addContent() {
		initMaterialListPanel();
		this.container.add(this.materialListPanel);

		initProviders();
		this.providerPanel.setStyleName("providers");
		this.container.add(providerPanel);

		this.setContentWidget(this.container);
	}

	@Override
	public void loadAllMaterials() {
		this.header.clearSearchPanel();
		this.materialListPanel.loadAllMaterials();
	}

	@Override
	public void onSearchResults(final List<Material> response) {
		this.materialListPanel.addGGTMaterials(response);
	}

	/**
	 * adds a local material
	 * 
	 * @param mat
	 *            {@link Material}
	 */
	@Override
	public void addMaterial(final Material mat) {
		this.materialListPanel.addMaterial(mat, false, true);
	}

	@Override
	public void removeMaterial(final Material mat) {
		this.materialListPanel.removeMaterial(mat);
	}

	protected void addResizeListener(final ResizeListener rl) {
		this.resizeListeners.add(rl);
	}

	@Override
	public void setLabels() {
		this.header.setLabels();
		this.materialListPanel.setLabels();
	}

	@Override
	public void disableMaterials() {
		this.materialListPanel.disableMaterials();
	}

	@Override
	public void setMaterialsDefaultStyle() {
		this.materialListPanel.setDefaultStyle(true);
	}

	@Override
	public void rememberSelected(final MaterialListElementI materialElement) {
		this.materialListPanel.rememberSelected(materialElement);
	}

	// public void setFrame(final GeoGebraAppFrame frame) {
	// super.setFrame(frame);
	// }

	public void openFileAsGgb(final JavaScriptObject fileToHandle,
	        final JavaScriptObject callback) {
		app.openFileAsGgb(fileToHandle, callback);
	}

	/**
	 * deletes all files from the {@link MaterialListPanel}
	 */
	@Override
	public void clearMaterials() {
		this.materialListPanel.clearMaterials();
	}

	@Override
	public void onOpenFile() {
		// For GoogleDrive files getLastSelected may be null
		if (getLastSelected() != null) {
			final Material material = getLastSelected().getMaterial();
			app.setSyncStamp(Math.max(material.getModified(),
			        material.getSyncStamp()));
			if (getLastSelected().isLocal) {
				String key = material.getTitle();
				app.getKernel()
				        .getConstruction()
				        .setTitle(
				                key.substring(key.indexOf("#",
				                        key.indexOf("#") + 1) + 1));
				app.setTubeId(material.getId());
				app.setLocalID(material.getLocalID());
			} else if (!getLastSelected().isLocal
			        && getLastSelected().isOwnMaterial) {
				app.getKernel().getConstruction().setTitle(material.getTitle());
				app.setTubeId(material.getId());
			} else {
				app.resetUniqueId();
				app.setTubeId(0);
			}
		} else {
			app.setTubeId(0);
			app.resetUniqueId(); // TODO
		}
		setMaterialsDefaultStyle();
		app.setCloseBrowserCallback(null);
		close();
		ToolTipManagerW.sharedInstance().hideBottomInfoToolTip();
	}

	private MaterialListElement getLastSelected() {
		return this.materialListPanel.lastSelected;
	}

	@Override
	public void displaySearchResults(final String query) {
		this.materialListPanel.displaySearchResults(query);
	}

	protected void updateViewSizes() {
		this.container.setPixelSize((int) app.getWidth(), (int) app.getHeight()
		        - GLookAndFeel.BROWSE_HEADER_HEIGHT);
		for (final ResizeListener res : this.resizeListeners) {
			res.onResize();
		}
	}

	@Override
	public void refreshMaterial(final Material material, final boolean isLocal) {
		this.materialListPanel.refreshMaterial(material, isLocal);
	}

	private void setAvailableProviders() {
		if (this.providerPanel == null) {
			return;
		}
		this.providerPanel.clear();
		this.providerPanel.add(locationTube);
		this.providerPanel.add(locationLocal);

		final GeoGebraTubeUser user = this.app.getLoginOperation().getModel()
		        .getLoggedInUser();
		if (user != null && user.hasGoogleDrive() && !app.getLAF().isSmart()) {
			this.addDriveButton(user);
		} else if (user != null) {
			App.debug(user.getIdentifier());
		}
		if (user != null && user.hasOneDrive()) {
			this.addOneDriveButton(user);
		}
		// Set Tube as the active on
		locationTube.addStyleName("selected");
	}

	@Override
	public void renderEvent(final BaseEvent event) {
		if (event instanceof LoginEvent || event instanceof LogOutEvent) {
			setAvailableProviders();
			if (event instanceof LoginEvent
			        && ((LoginEvent) event).isSuccessful()) {
				sync();
				this.materialListPanel.loadUsersMaterials();
			} else if (event instanceof LogOutEvent) {
				this.materialListPanel.removeUsersMaterials();
			}
		}
	}

	@Override
	public void render(final boolean online) {
		if (online) {
			if (app.getLoginOperation().isLoggedIn()) {
				sync();
			}
			this.materialListPanel.loadAllMaterials();
		} else {
			this.clearMaterials();
			this.app.getFileManager().getUsersMaterials();
		}
	}

	@Override
	public AppW getApp() {
		return app;
	}
}
