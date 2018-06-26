package org.geogebra.web.full.gui.openfileview;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.Feature;
import org.geogebra.common.main.OpenFileListener;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.Provider;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.MyHeaderPanel;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.main.BrowserDevice.FileOpenButton;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.gui.view.browser.BrowseViewI;
import org.geogebra.web.html5.gui.view.browser.MaterialListElementI;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * View for browsing materials
 * 
 * @author Alicia
 *
 */
public class OpenFileView extends MyHeaderPanel
		implements BrowseViewI, OpenFileListener {
	/**
	 * application
	 */
	protected AppW app;
	// header
	private FlowPanel headerPanel;
	private StandardButton backBtn;
	private Label headerCaption;

	// content panel
	private FlowPanel contentPanel;
	// button panel
	private FlowPanel buttonPanel;
	private StandardButton newFileBtn;
	private FileOpenButton openFileBtn;

	// dropdown
	private ListBox sortDropDown;

	// material panel
	private FlowPanel materialPanel;

	/**
	 * @param app
	 *            application
	 * @param openFileButton
	 *            button to open file picker
	 */
	public OpenFileView(AppW app, FileOpenButton openFileButton) {
		this.app = app;
		this.openFileBtn = openFileButton;
		initGUI();
	}

	private void initGUI() {
		this.setStyleName("openFileView");
		initHeader();
		initContentPanel();
		initButtonPanel();
		initSortDropdown();
		initMaterialPanel();
	}

	private void initHeader() {
		headerPanel = new FlowPanel();
		headerPanel.setStyleName("openFileViewHeader");

		backBtn = new StandardButton(
				MaterialDesignResources.INSTANCE.mow_back_arrow(),
				null, 24,
				app);
		backBtn.setStyleName("headerBackButton");
		backBtn.addFastClickHandler(new FastClickHandler() {

			public void onClick(Widget source) {
				close();
			}
		});
		headerPanel.add(backBtn);

		headerCaption = new Label(
				localize("mow.openFileViewTitle"));
		headerCaption.setStyleName("headerCaption");
		headerPanel.add(headerCaption);

		this.setHeaderWidget(headerPanel);
	}

	private void initContentPanel() {
		contentPanel = new FlowPanel();
		contentPanel.setStyleName("fileViewContentPanel");
		this.setContentWidget(contentPanel);
	}

	private void initButtonPanel() {
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("fileViewButtonPanel");

		newFileBtn = new StandardButton(
				MaterialDesignResources.INSTANCE.add_black(),
				localize("mow.newFile"), 18, app);
		newFileBtn.setStyleName("containedButton");
		newFileBtn.addFastClickHandler(new FastClickHandler() {

			public void onClick(Widget source) {
				newFile();
			}
		});
		buttonPanel.add(newFileBtn);

		openFileBtn.setImageAndText(
				MaterialDesignResources.INSTANCE.mow_pdf_open_folder()
						.getSafeUri().asString(),
				localize("mow.openFile"));
		openFileBtn.addStyleName("buttonMargin");
		buttonPanel.add(openFileBtn);

		contentPanel.add(buttonPanel);
	}

	private void initSortDropdown() {
		sortDropDown = new ListBox();
		sortDropDown.setMultipleSelect(false);
		sortDropDown.addItem(localize("SortBy"));
		sortDropDown.getElement().getFirstChildElement()
				.setAttribute("disabled", "disabled");
		sortDropDown.addItem(localize("sort_author")); // index 1
		sortDropDown.addItem(localize("sort_title")); // index 2
		sortDropDown.addItem(localize("sort_date_created")); // index 3
		sortDropDown.addItem(localize("sort_last_modified")); // index 4
		sortDropDown.setSelectedIndex(4);
		sortDropDown.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				// TODO sort material cards according to selected sort mode
			}

		});
		contentPanel.add(sortDropDown);
	}

	private void initMaterialPanel() {
		materialPanel = new FlowPanel();
		materialPanel.addStyleName("materialPanel");
		contentPanel.add(materialPanel);
	}

	private String localize(String id) {
		return app.getLocalization().getMenu(id);
	}

	/**
	 * start a new file
	 */
	protected void newFile() {
		Runnable newConstruction = new Runnable() {

			@Override
			public void run() {
				app.setWaitCursor();
				app.fileNew();
				app.setDefaultCursor();

				if (!app.isUnbundledOrWhiteboard()) {
					app.showPerspectivesPopup();
				}
				if (app.has(Feature.MOW_MULTI_PAGE)
						&& app.getPageController() != null) {
					app.getPageController().resetPageControl();
				}
			}
		};
		((DialogManagerW) getApp().getDialogManager()).getSaveDialog()
				.showIfNeeded(newConstruction);
		close();
	}

	/**
	 * @param fileToHandle
	 *            JS file object
	 * @param callback
	 *            callback after file is open
	 */
	public void openFile(final JavaScriptObject fileToHandle,
			final JavaScriptObject callback) {
		if (app.getLAF().supportsLocalSave()) {
			app.getFileManager().setFileProvider(Provider.LOCAL);
		}
		app.openFile(fileToHandle, callback);
		close();
	}

	@Override
	public AppW getApp() {
		return app;
	}

	@Override
	public void resizeTo(int width, int height) {
		// TODO Auto-generated method stub

	}

	public void setMaterialsDefaultStyle() {
		// TODO Auto-generated method stub

	}

	public void loadAllMaterials() {
		// TODO Auto-generated method stub

	}

	public void clearMaterials() {
		// TODO Auto-generated method stub

	}

	public void disableMaterials() {
		// TODO Auto-generated method stub

	}

	public void onSearchResults(List<Material> response,
			ArrayList<Chapter> chapters) {
		// TODO Auto-generated method stub

	}

	public void displaySearchResults(String query) {
		// TODO Auto-generated method stub

	}

	public void refreshMaterial(Material material, boolean isLocal) {
		// TODO Auto-generated method stub

	}

	public void rememberSelected(MaterialListElementI materialElement) {
		// TODO Auto-generated method stub

	}

	public void setLabels() {
		headerCaption.setText(
				app.getLocalization().getMenu("mow.openFileViewTitle"));
	}

	public void addMaterial(Material material) {
		// TODO Auto-generated method stub

	}

	public void removeMaterial(Material material) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onOpenFile() {
		// TODO
		return false;
	}
}
