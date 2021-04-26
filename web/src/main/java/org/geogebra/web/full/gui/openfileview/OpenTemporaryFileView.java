package org.geogebra.web.full.gui.openfileview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.geogebra.common.main.OpenFileListener;
import org.geogebra.common.main.exam.TempStorage;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.MaterialRequest.Order;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.MyHeaderPanel;
import org.geogebra.web.html5.gui.view.browser.BrowseViewI;
import org.geogebra.web.html5.gui.view.browser.MaterialListElementI;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.ListBox;

import elemental2.dom.File;

/**
 * View for browsing materials
 */
public class OpenTemporaryFileView implements
		BrowseViewI, OpenFileListener {

	private final TempStorage tempStorage;
	private final FileViewCommon common;

	private static final Order[] map = new Order[] { Order.title, Order.created,
			Order.timestamp };
	private final AppW app;

	/**
	 * @param app
	 *            application
	 *
	 */
	public OpenTemporaryFileView(AppW app) {
		this.app = app;
		app.registerOpenFileListener(this);
		common = new FileViewCommon(app, "Open");
		tempStorage = app.getExam().getTempStorage();
	}

	public MyHeaderPanel getPanel() {
		return common;
	}

	private Collection<Material> getMaterials() {
		return tempStorage.collectTempMaterials();
	}

	private void initSortDropdown() {
		// dropdown
		ListBox sortDropDown = new ListBox();
		sortDropDown.setMultipleSelect(false);
		sortDropDown.addItem(common.localize("SortBy"));
		sortDropDown.getElement().getFirstChildElement()
				.setAttribute("disabled", "disabled");

		for (Order value : map) {
			sortDropDown.addItem(common.localize(labelFor(value)));
		}
		sortDropDown.setSelectedIndex(3);
		sortDropDown.addChangeHandler(event -> updateOrder());
	}

	private static String labelFor(Order order2) {
		switch (order2) {
		case created:
			return "sort_date_created";
		case timestamp:
			return "sort_last_modified";
		default:
		case title:
			return "sort_title";
		}
	}

	/**
	 * Reload materials sorted by another property.
	 */
	protected void updateOrder() {
		loadAllMaterials();
	}

	@Override
	public void openFile(final File fileToHandle) {
	}

	@Override
	public void setMaterialsDefaultStyle() {
		// not used
	}

	@Override
	public void loadAllMaterials() {
		clearMaterials();
		if (getMaterials().isEmpty()) {
			common.showEmptyListNotification();
		} else {
			common.addContent();
			addTemporaryMaterials();
		}
	}

	@Override
	public void clearMaterials() {
		common.clearMaterials();
	}

	private void addTemporaryMaterials() {
		common.clearPanels();
		for (Material material : getMaterials()) {
			addMaterial(material);
		}
		common.addMaterialPanel();
	}

	@Override
	public void disableMaterials() {
		// not used
	}

	@Override
	public void onSearchResults(List<Material> response,
			ArrayList<Chapter> chapters) {
		// not used
	}

	@Override
	public void close() {

	}

	@Override
	public void displaySearchResults(String query) {
		// not used
	}

	@Override
	public void refreshMaterial(Material material, boolean isLocal) {
		// not used
	}

	@Override
	public void rememberSelected(MaterialListElementI materialElement) {
		// not used
	}

	@Override
	public void setLabels() {
		common.setLabels();
	}

	@Override
	public void addMaterial(Material material) {
		common.addMaterialCard(new TemporaryCard(material, app));
	}

	@Override
	public void removeMaterial(Material material) {
		// not used
	}

	@Override
	public boolean onOpenFile() {
		setConstructionTitleAsMaterial();
		return false;
	}

	private void setConstructionTitleAsMaterial() {
		Material activeMaterial = app.getActiveMaterial();
		if (activeMaterial != null) {
			app.getKernel().getConstruction().setTitle(
					activeMaterial.getTitle());
		}
	}

	@Override
	public void closeAndSave(AsyncOperation<Boolean> callback) {
		close();
		app.checkSaved(callback);
	}
}