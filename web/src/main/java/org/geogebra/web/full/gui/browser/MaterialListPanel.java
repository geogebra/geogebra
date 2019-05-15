package org.geogebra.web.full.gui.browser;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.MaterialsManager;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.MaterialRequest.Order;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.html5.gui.ResizeListener;
import org.geogebra.web.html5.gui.view.browser.MaterialListElementI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.ggtapi.models.GeoGebraTubeAPIW;
import org.geogebra.web.shared.ggtapi.models.MaterialCallback;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * contains all available materials
 */
public class MaterialListPanel extends FlowPanel
		implements ResizeListener, ShowDetailsListener {

	private final static long MIN_DIFFERNCE_TO_SCROLL = 1000;
	/** application */
	protected AppW app;
	/**
	 * last selected {@link MaterialListElement material}
	 */
	protected MaterialListElement lastSelected;
	/**
	 * list of all shown {@link MaterialListElement materials}
	 */
	protected List<MaterialListElement> materials = new ArrayList<>();
	private MaterialCallback userMaterialsCB;
	private MaterialCallback ggtMaterialsCB;
	/** last call of scroll handler */
	long lastScroll = 0;
	/** last call to setDefaultStyle */
	long lastDefaultStyle = 0;

	/**
	 * @param app
	 *            {@link AppW}
	 */
	public MaterialListPanel(final AppW app) {
		this.app = app;
		this.userMaterialsCB = getUserMaterialsCB();
		this.ggtMaterialsCB = getGgtMaterialsCB();
		if (app.getConfig().isSimpleMaterialPicker()) {
			this.setPixelSize((int) app.getWidth(), (int) app.getHeight());
		} else {
			this.setPixelSize(
					(int) app.getWidth() - GLookAndFeel.PROVIDER_PANEL_WIDTH,
					(int) app.getHeight() - GLookAndFeel.BROWSE_HEADER_HEIGHT);
		}
		this.setStyleName("materialListPanel");
		this.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				if (lastSelected != null) {
					setDefaultStyle(true);
				}
			}
		}, ClickEvent.getType());

		this.addDomHandler(new ScrollHandler() {

			@Override
			public void onScroll(final ScrollEvent event) {
				if (lastSelected != null && System.currentTimeMillis()
						- lastScroll > MIN_DIFFERNCE_TO_SCROLL) {
					setDefaultStyle(false);
				}
			}
		}, ScrollEvent.getType());

		this.addDomHandler(new TouchMoveHandler() {

			@Override
			public void onTouchMove(final TouchMoveEvent event) {
				if (lastSelected != null) {
					setDefaultStyle(false);
				}
			}
		}, TouchMoveEvent.getType());
	}

	private MaterialCallback getGgtMaterialsCB() {
		return new MaterialCallback() {
			@Override
			public void onError(final Throwable exception) {
				// FIXME implement Error Handling!
				exception.printStackTrace();
				Log.debug(exception.getMessage());
			}

			@Override
			public void onLoaded(final List<Material> response,
					ArrayList<Chapter> meta) {
				addGGTMaterials(response, meta);
			}
		};
	}

	private MaterialCallback getUserMaterialsCB() {
		return new MaterialCallback() {

			@Override
			public void onLoaded(final List<Material> parseResponse,
					ArrayList<Chapter> meta) {
				addUsersMaterials(parseResponse);

			}
		};
	}

	/**
	 * sets all {@link MaterialListElement materials} to the default style (not
	 * selected, not disabled)
	 * 
	 * @param force
	 *            boolean
	 */
	public void setDefaultStyle(boolean force) {
		if (!force
				&& System.currentTimeMillis() < this.lastDefaultStyle + 3000) {
			return;
		}
		this.lastDefaultStyle = System.currentTimeMillis();
		this.lastSelected = null;
		for (final MaterialListElement mat : this.materials) {
			mat.setDefaultStyle();
		}
	}

	/**
	 * loads featured materials and (if user is logged in) users materials
	 */
	public void loadAllMaterials() {
		clearMaterials();
		loadLocal();
		if (this.app.getLoginOperation().isLoggedIn()) {
			app.getLoginOperation().getGeoGebraTubeAPI()
					.getUsersMaterials(this.userMaterialsCB, Order.timestamp);
		} else {
			app.getLoginOperation().getGeoGebraTubeAPI()
					.getFeaturedMaterials(this.ggtMaterialsCB);
		}
	}

	/**
	 * loads users materials from ggt
	 */
	public void loadUsersMaterials() {
		app.getLoginOperation().getGeoGebraTubeAPI()
				.getUsersMaterials(this.userMaterialsCB, Order.timestamp);
	}

	/**
	 * adds the local saved materials (localStorage) of current loggedIn user
	 */
	private void loadLocal() {
		app.getFileManager().getUsersMaterials();
	}

	/**
	 * adds the new materials (matList) - GeoGebraTube only
	 * 
	 * @param matList
	 *            List of materials
	 * @param chapters
	 *            list of book chapters
	 */
	public final void addGGTMaterials(final List<Material> matList,
			final ArrayList<Chapter> chapters) {
		if (chapters == null || chapters.size() < 2) {
			for (final Material mat : matList) {
				addMaterial(mat, true, false);
			}
		} else {
			for (int i = 0; i < chapters.size(); i++) {
				addHeading(chapters.get(i).getTitle());
				int[] materialIDs = chapters.get(i).getMaterials();
				for (int j = 0; j < materialIDs.length; j++) {
					addMaterial(findMaterial(matList, materialIDs[j]), true,
							false);
				}
			}
		}
	}

	private static Material findMaterial(List<Material> matList, int id) {
		for (int i = 0; i < matList.size(); i++) {
			if (matList.get(i).getId() == id) {
				return matList.get(i);
			}
		}
		return null;
	}

	private void addHeading(String title) {
		Label chapterLabel = new Label(title);
		chapterLabel.addStyleName("ggbChapterName");
		add(chapterLabel);
	}

	/**
	 * Adds the given {@link Material materials}.
	 * 
	 * @param matList
	 *            List of materials
	 */
	public void addUsersMaterials(final List<Material> matList) {

		for (int i = matList.size() - 1; i >= 0; i--) {
			addMaterial(matList.get(i), false, false);
		}

	}

	/**
	 * adds the given material to the list of {@link MaterialListElement
	 * materials} and the preview-panel. if the {@link Material material}
	 * already exists, the {@link MaterialListElement preview} is updated
	 * otherwise a new one is created.
	 * 
	 * @param mat
	 *            {@link Material}, validated for NPE
	 * @param insertAtEnd
	 *            boolean
	 * @param isLocal
	 *            boolean
	 */
	public void addMaterial(final Material mat, final boolean insertAtEnd,
			final boolean isLocal) {
		if (mat == null) {
			return;
		}
		final MaterialListElement matElem = getMaterialListElement(mat);
		if (matElem != null) {
			int oldLocalID = matElem.getMaterial().getLocalID();
			String oldThumbnail = matElem.getMaterial().getThumbnail();
			boolean oldThumbnailIsBase64 = matElem.getMaterial()
					.thumbnailIsBase64();
			if (mat.getLocalID() == -1) {
				mat.setLocalID(oldLocalID);
				mat.setSyncStamp(Math.max(matElem.getMaterial().getSyncStamp(),
						mat.getSyncStamp()));
			}
			if (mat.getThumbnail() == null
					|| mat.getThumbnail().length() == 0) {
				if (oldThumbnailIsBase64) {
					mat.setThumbnailBase64(oldThumbnail);
				} else {
					mat.setThumbnailUrl(oldThumbnail);
				}
			}

			matElem.setMaterial(mat);
			this.remove(matElem);
			this.insert(matElem, 0);
		} else {

			addNewMaterial(mat, insertAtEnd, isLocal);
		}
	}

	/**
	 * The actual creation happens in LAF as it needs to be different for phone
	 * / tablet / web / widgets
	 * 
	 * @param mat
	 *            {@link Material}
	 * @param isLocal
	 *            boolean
	 */
	private void addNewMaterial(final Material mat,
			final boolean insertAtEnd, final boolean isLocal) {
		final MaterialListElement preview = ((GLookAndFeel) app.getLAF())
				.getMaterialElement(mat, this.app, isLocal);
		this.materials.add(preview);
		preview.setShowDetailsListener(this);

		if (insertAtEnd) {
			this.add(preview);
		} else {
			this.insert(preview, 0);
		}
	}

	/**
	 * clears the list of existing {@link MaterialListElement materials} and the
	 * {@link MaterialListPanel preview-panel}
	 */
	public void clearMaterials() {
		this.materials.clear();
		this.clear();
	}

	/**
	 * @return {@link MaterialListElement} last selected material
	 */
	public MaterialListElement getChosenMaterial() {
		return this.lastSelected;
	}

	/**
	 * sets all materials to disabled
	 */
	public void disableMaterials() {
		for (final MaterialListElement mat : this.materials) {
			mat.disableMaterial();
		}
	}

	/**
	 * @param materialElement
	 *            {@link MaterialListElement}
	 */
	public void rememberSelected(final MaterialListElementI materialElement) {
		this.lastSelected = (MaterialListElement) materialElement;
	}

	/**
	 * clears the {@link MaterialListPanel} and adds the found materials to it.
	 * if query == "" all materials (featured and users) are loaded.
	 * 
	 * @param query
	 *            String
	 */
	public void displaySearchResults(final String query) {
		clearMaterials();
		if ("".equals(query)) {
			loadAllMaterials();
			return;
		}
		searchLocal(query);
		searchGgt(query);
	}

	/**
	 * search for materials in localStorage (offline saved files)
	 * 
	 * @param query
	 *            String
	 */
	private void searchLocal(final String query) {
		this.app.getFileManager().search(query);
	}

	/**
	 * search GeoGebraTube
	 * 
	 * @param query
	 *            String
	 */
	protected void searchGgt(final String query) {
		((GeoGebraTubeAPIW) this.app.getLoginOperation().getGeoGebraTubeAPI())
				.search(query, this.ggtMaterialsCB);
	}

	/**
	 * removes the given material from the list of {@link MaterialListElement
	 * materials} and the {@link MaterialListPanel preview-panel}
	 * 
	 * @param mat
	 *            {@link Material}
	 */
	public void removeMaterial(final Material mat) {
		for (final MaterialListElement matElem : this.materials) {
			if (matElem.localMaterial
					&& mat.getTitle().equals(matElem.getMaterial().getTitle())
					|| matElem.ownMaterial
							&& matElem.getMaterial().equals(mat)) {

				this.materials.remove(matElem);
				this.remove(matElem);
				return;
			}
		}
	}

	/**
	 * 
	 */
	public void setLabels() {
		for (final MaterialListElement e : this.materials) {
			e.setLabels();
		}
	}

	@Override
	public void onResize(int appWidth, int appHeight) {
		if (app.getConfig().isSimpleMaterialPicker()) {
			this.setPixelSize(appWidth, appHeight);
		} else {
			this.setPixelSize(appWidth - GLookAndFeel.PROVIDER_PANEL_WIDTH,
					appHeight - GLookAndFeel.BROWSE_HEADER_HEIGHT);
		}
	}

	/**
	 * refreshes the preview of the {@link MaterialListElement material}
	 * 
	 * @param material
	 *            {@link Material}
	 * @param isLocal
	 *            boolean
	 */
	public void refreshMaterial(final Material material,
			final boolean isLocal) {
		addMaterial(material, false, isLocal);
	}

	private MaterialListElement getMaterialListElement(
			final Material material) {
		for (final MaterialListElement matElem : this.materials) {
			if (matElem.getMaterial().getId() > 0
					&& matElem.getMaterial().getId() == material.getId()
					|| matElem.localMaterial && MaterialsManager
							.getFileKey(matElem.getMaterial())
							.equals(MaterialsManager.getFileKey(material))) {
				return matElem;
			}
		}
		return null;
	}

	/**
	 * remove users materials from {@link MaterialListPanel}
	 */
	public void removeUsersMaterials() {
		final List<Material> delete = new ArrayList<>();
		for (final MaterialListElement elem : this.materials) {
			if (elem.ownMaterial) {
				delete.add(elem.getMaterial());
			}
		}
		for (final Material deleteElem : delete) {
			removeMaterial(deleteElem);
		}
	}

	@Override
	public void onShowDetails(FlowPanel content) {
		int elementBottom = content.getAbsoluteTop()
				+ content.getElement().getClientHeight();
		int panelBottom = this.getAbsoluteTop()
				+ this.getElement().getClientHeight();

		if (panelBottom < elementBottom) {
			lastScroll = System.currentTimeMillis();
			this.getElement().setScrollTop(this.getElement().getScrollTop()
					+ elementBottom - panelBottom);
		} else if (content.getAbsoluteTop() < this.getAbsoluteTop()) {
			lastScroll = System.currentTimeMillis();
			this.getElement().setScrollTop(this.getElement().getScrollTop()
					+ content.getAbsoluteTop() - this.getAbsoluteTop());
		}
	}

	/**
	 * @return last selected {@link MaterialListElement material}
	 */
	public MaterialListElement getLastSelected() {
		return this.lastSelected;
	}
}
