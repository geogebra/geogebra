package org.geogebra.web.full.gui.browser;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.OpenFileListener;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.Material.Provider;
import org.geogebra.common.move.views.BooleanRenderable;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.MyHeaderPanel;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.html5.gui.ResizeListener;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.view.browser.BrowseViewI;
import org.geogebra.web.html5.gui.view.browser.MaterialListElementI;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.ggtapi.LoginOperationW;
import org.gwtproject.event.dom.client.TouchMoveEvent;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.HorizontalPanel;
import org.gwtproject.user.client.ui.Widget;

import elemental2.dom.File;

/**
 * GeoGebraTube Search and Browse GUI
 * 
 */
public class BrowseGUI extends MyHeaderPanel implements BooleanRenderable,
        EventRenderable, OpenFileListener, BrowseViewI {
	/** children that need resizing */
	protected final List<ResizeListener> resizeListeners = new ArrayList<>();
	private BrowseHeaderPanel header;
	/** pane with materials */
	protected MaterialListPanel materialListPanel;
	/** container */
	protected HorizontalPanel container;

	private FlowPanel providerPanel;
	private StandardButton locationTube;
	private StandardButton locationDrive;
	private Widget locationLocal;
	/** application */
	protected final AppW app;

	/**
	 * @param app
	 *            application
	 * @param fileButton
	 *            button to open loacl files
	 */
	public BrowseGUI(final AppW app, Widget fileButton) {
		this.setStyleName("browsegui");
		this.locationLocal = fileButton;
		this.app = app;
		this.app.getNetworkOperation().getView().add(this);
		if (this.app.getLoginOperation() == null) {
			this.app.initSignInEventFlow(new LoginOperationW(app));
		}
		this.app.getLoginOperation().getView().add(this);
		this.container = new HorizontalPanel();
		if (app.getConfig().isSimpleMaterialPicker()) {
			this.container.setPixelSize((int) app.getWidth(),
					(int) app.getHeight());
		} else {
			this.container.setPixelSize((int) app.getWidth(),
					(int) app.getHeight() - GLookAndFeel.BROWSE_HEADER_HEIGHT);
		}
		this.container.setStyleName("content");

		initMaterialListPanel();

		if (!app.getConfig().isSimpleMaterialPicker()) {
			addHeader();
		}
		addContent();

		app.registerOpenFileListener(this);

		this.addBitlessDomHandler(event -> {
			// prevent zooming
			if (event.getTouches().length() > 1) {
				event.preventDefault();
				event.stopPropagation();
			}
		}, TouchMoveEvent.getType());
		if (app.getGoogleDriveOperation() != null) {
			app.getGoogleDriveOperation().initGoogleDriveApi();
		}
	}

	/**
	 * Initialize material panel
	 */
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
		locationTube.addFastClickHandler(source -> {
			app.getFileManager().setFileProvider(Provider.TUBE);
			loadAllMaterials(0);
		});

		setAvailableProviders();
	}

	private void addDriveButton() {

		if (locationDrive == null) {
			locationDrive = new StandardButton(
					BrowseResources.INSTANCE.location_drive());
			locationDrive.addFastClickHandler(source -> {
				if (app.getGoogleDriveOperation() != null) {
					app.getFileManager().setFileProvider(Provider.GOOGLE);
					app.getGoogleDriveOperation()
							.requestPicker();
				}
			});
		}
		this.providerPanel.add(this.locationDrive);
	}

	/**
	 * Add panels to this
	 */
	protected void addContent() {
		initMaterialListPanel();
		this.container.add(this.materialListPanel);

		if (!app.getConfig().isSimpleMaterialPicker()) {
			initProviders();
			this.providerPanel.setStyleName("providers");
			this.container.add(providerPanel);
		}

		this.setContentWidget(this.container);
	}

	@Override
	public void loadAllMaterials(int offset) {
		if (header != null) {
			this.header.clearSearchPanel();
		}
		this.materialListPanel.loadAllMaterials();
	}

	@Override
	public void onSearchResults(List<Material> response,
	        ArrayList<Chapter> chapters) {
		this.materialListPanel.addGGTMaterials(response, chapters);
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

	/**
	 * @param rl
	 *            resize lsitener
	 */
	protected void addResizeListener(final ResizeListener rl) {
		this.resizeListeners.add(rl);
	}

	@Override
	public void setLabels() {
		if (header != null) {
			this.header.setLabels();
		}
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

	@Override
	public void openFile(final File fileToHandle) {
		showLoading();
		closeAndSave(success -> app.openFile(fileToHandle));
	}

	/**
	 * deletes all files from the {@link MaterialListPanel}
	 */
	@Override
	public void clearMaterials() {
		this.materialListPanel.clearMaterials();
	}

	@Override
	public boolean onOpenFile() {
		// For GoogleDrive files getLastSelected may be null
		if (getLastSelected() != null) {
			final Material material = getLastSelected().getMaterial();
			app.setSyncStamp(Math.max(material.getModified(),
			        material.getSyncStamp()));
			if (getLastSelected().localMaterial) {
				String key = material.getTitle();
				app.getKernel()
				        .getConstruction()
				        .setTitle(
				                key.substring(key.indexOf("#",
				                        key.indexOf("#") + 1) + 1));
				if (material.getType() != MaterialType.ggt) {
				app.updateMaterialURL(material);
				}
				app.setLocalID(material.getLocalID());
			} else if (!getLastSelected().localMaterial
			        && getLastSelected().ownMaterial) {
				app.getKernel().getConstruction().setTitle(material.getTitle());
				app.updateMaterialURL(material);
			} else {
				app.setTubeId(null);
				app.updateMaterialURL(0, material.getSharingKeyOrId(),
						material.getTitle());
			}
		} else {
			app.updateMaterialURL(0, null, null);
			app.setTubeId(null);
		}
		setMaterialsDefaultStyle();
		app.setCloseBrowserCallback(null);
		close();
		ToolTipManagerW.sharedInstance().hideTooltip();
		return false;
	}

	private MaterialListElement getLastSelected() {
		return this.materialListPanel.lastSelected;
	}

	@Override
	public void displaySearchResults(final String query) {
		this.materialListPanel.displaySearchResults(query);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			resizeTo((int) app.getWidth(), (int) app.getHeight());
		}
	}

	@Override
	public void resizeTo(int width, int height) {
		if (app.getConfig().isSimpleMaterialPicker()) {
			this.container.setPixelSize(width, height);
		} else {
			this.container.setPixelSize(width,
					height - GLookAndFeel.BROWSE_HEADER_HEIGHT);
		}
		for (final ResizeListener res : this.resizeListeners) {
			res.onResize(width, height);
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
		if (user != null && user.hasGoogleDrive()
		        && app.getLAF().supportsGoogleDrive()) {
			this.addDriveButton();
		} else if (user != null) {
			Log.debug(user.getIdentifier());
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
				this.materialListPanel.loadUsersMaterials();
			} else if (event instanceof LogOutEvent) {
				this.materialListPanel.removeUsersMaterials();
			}
		}
	}

	@Override
	public void render(final boolean online) {
		if (online) {
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

	/**
	 * Show "Loading..." message
	 */
	public void showLoading() {
		if (!app.isUnbundled()) {
			ToolTipManagerW.sharedInstance().showBottomMessage(
				app.getLocalization().getMenu("Loading"), app);
		}
	}

	@Override
	public void closeAndSave(AsyncOperation<Boolean> callback) {
		close();
		app.checkSaved(callback);
	}
}
