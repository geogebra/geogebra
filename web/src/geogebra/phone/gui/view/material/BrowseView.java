package geogebra.phone.gui.view.material;

import geogebra.common.main.OpenFileListener;
import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.ggtapi.events.LogOutEvent;
import geogebra.common.move.ggtapi.events.LoginEvent;
import geogebra.common.move.ggtapi.models.Chapter;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.views.BooleanRenderable;
import geogebra.common.move.views.EventRenderable;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.gui.view.browser.BrowseViewI;
import geogebra.html5.gui.view.browser.MaterialListElementI;
import geogebra.html5.main.AppW;
import geogebra.phone.Phone;
import geogebra.phone.gui.view.AbstractView;
import geogebra.phone.gui.view.HeaderPanel;
import geogebra.phone.gui.view.StyleBar;
import geogebra.phone.gui.view.ViewPanel;
import geogebra.web.css.GuiResources;
import geogebra.web.gui.browser.MaterialListElement;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.resources.client.ImageResource;

/**
 * Contains the {@link BrowseViewPanel}
 * 
 * @see AbstractView
 */
public class BrowseView extends AbstractView implements BrowseViewI,
        BooleanRenderable, EventRenderable, OpenFileListener {

	private BrowseViewPanel browseViewPanel;

	/**
	 * @param app
	 *            {@link AppW}
	 */
	public BrowseView(AppW app) {
		super(app);
		this.browseViewPanel = new BrowseViewPanel(app);
		this.browseViewPanel.getMaterialPanel().loadAllMaterials();
		app.registerOpenFileListener(this);
	}

	@Override
	protected ViewPanel createViewPanel() {
		return this.browseViewPanel;
	}

	@Override
	protected HeaderPanel createHeaderPanel() {
		return new BrowseHeaderPanel(app);
	}

	@Override
	protected ImageResource createViewIcon() {
		return GuiResources.INSTANCE.browseView();
	}
	
	@Override
	public StyleBar createStyleBar() {
		// no stylebar for BrowseView
		return null;
	}

	@Override
	public void setMaterialsDefaultStyle() {
		this.browseViewPanel.getMaterialPanel().setDefaultStyle(true);
	}

	@Override
	public void loadAllMaterials() {
		this.browseViewPanel.getMaterialPanel().loadAllMaterials();
	}

	@Override
	public void clearMaterials() {
		this.browseViewPanel.getMaterialPanel().clearMaterials();
	}

	@Override
	public void disableMaterials() {
		this.browseViewPanel.getMaterialPanel().disableMaterials();
	}

	@Override
	public void onSearchResults(List<Material> response,
	        ArrayList<Chapter> chapters) {
		this.browseViewPanel.getMaterialPanel().addGGTMaterials(response,
		        chapters);
	}

	@Override
	public void close() {
		Phone.showEuclidianView();
	}

	@Override
	public void displaySearchResults(String query) {
		this.browseViewPanel.getMaterialPanel().displaySearchResults(query);
	}

	@Override
	public void refreshMaterial(Material material, boolean isLocal) {
		this.browseViewPanel.getMaterialPanel().refreshMaterial(material,
		        isLocal);
	}

	@Override
	public void rememberSelected(MaterialListElementI materialElement) {
		this.browseViewPanel.getMaterialPanel().rememberSelected(
		        materialElement);
	}

	@Override
	public void setLabels() {
		this.browseViewPanel.getMaterialPanel().setLabels();
	}

	@Override
	public void onOpenFile() {
		// For GoogleDrive files getLastSelected may be null
		if (getLastSelected() != null) {
			final Material material = getLastSelected().getMaterial();
			app.setSyncStamp(material.getSyncStamp());
			if (getLastSelected().isLocal()) {
				String key = material.getTitle();
				app.getKernel()
				        .getConstruction()
				        .setTitle(
				                key.substring(key.indexOf("#",
				                        key.indexOf("#") + 1) + 1));
				app.resetUniqueId();
			} else if (!getLastSelected().isLocal()
			        && getLastSelected().isOwn()) {
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
		close();
		ToolTipManagerW.sharedInstance().hideBottomInfoToolTip();
	}

	private MaterialListElement getLastSelected() {
		return this.browseViewPanel.getMaterialPanel().getLastSelected();
	}

	@Override
	public void renderEvent(final BaseEvent event) {
		if (event instanceof LoginEvent || event instanceof LogOutEvent) {
			if (event instanceof LoginEvent
			        && ((LoginEvent) event).isSuccessful()) {
				this.browseViewPanel.getMaterialPanel().loadUsersMaterials();
			} else if (event instanceof LogOutEvent) {
				this.browseViewPanel.getMaterialPanel().removeUsersMaterials();
			}
		}
	}

	@Override
	public void render(final boolean online) {
		if (online) {
			this.browseViewPanel.getMaterialPanel().loadAllMaterials();
		} else {
			this.clearMaterials();
			this.app.getFileManager().getUsersMaterials();
		}
	}

	@Override
	public void addMaterial(Material material) {
		this.browseViewPanel.getMaterialPanel().addMaterial(material, false,
		        true);
	}

	@Override
	public void removeMaterial(Material material) {
		this.browseViewPanel.getMaterialPanel().removeMaterial(material);
	}
}
