package org.geogebra.web.full.gui.openfileview;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.Feature;
import org.geogebra.common.main.OpenFileListener;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.Provider;
import org.geogebra.common.move.ggtapi.models.MaterialRequest.Order;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.MyHeaderPanel;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.main.BrowserDevice.FileOpenButton;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.gui.view.browser.BrowseViewI;
import org.geogebra.web.html5.gui.view.browser.MaterialListElementI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.ggtapi.models.MaterialCallback;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
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
	private MaterialCallbackI ggtMaterialsCB;
	private MaterialCallbackI userMaterialsCB;
	private FlowPanel imagePanel;

	private boolean materialListEmpty = true;
	private Order order = Order.timestamp;
	private static Order[] map = new Order[] { Order.title, Order.created,
			Order.timestamp };

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
		this.userMaterialsCB = getUserMaterialsCB();
		this.ggtMaterialsCB = getGgtMaterialsCB();
		initHeader();
		initContentPanel();
		initButtonPanel();
		initSortDropdown();
		initMaterialPanel();
	}

	/**
	 * adds content if available, notification otherwise
	 */
	protected void addContent() {
		if (materialListEmpty) {
			showEmptyListNotification();
			setExtendedButtonStyle();
			imagePanel.add(buttonPanel);
		} else {
			contentPanel.add(buttonPanel);
			contentPanel.add(sortDropDown);
			contentPanel.add(materialPanel);
		}
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

			@Override
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
		newFileBtn.addStyleName("buttonMargin16");
		newFileBtn.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				newFile();
			}
		});
		openFileBtn.setImageAndText(
				MaterialDesignResources.INSTANCE.mow_pdf_open_folder()
						.getSafeUri().asString(),
				localize("mow.openFile"));

		buttonPanel.add(openFileBtn);
		buttonPanel.add(newFileBtn);
	}

	private void initSortDropdown() {
		sortDropDown = new ListBox();
		sortDropDown.setMultipleSelect(false);
		sortDropDown.addItem(localize("SortBy"));
		sortDropDown.getElement().getFirstChildElement()
				.setAttribute("disabled", "disabled");
		for (int i = 0; i < map.length; i++) {
			sortDropDown.addItem(localize(labelFor(map[i])));
		}
		sortDropDown.setSelectedIndex(3);
		sortDropDown.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				updateOrder();
			}
		});
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

		order = map[sortDropDown.getSelectedIndex() - 1];
		loadAllMaterials();
	}

	private void initMaterialPanel() {
		materialPanel = new FlowPanel();
		materialPanel.addStyleName("materialPanel");
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
		Log.printStacktrace("OpenFileView.openFile");
		if (app.getLAF().supportsLocalSave()) {
			app.getFileManager().setFileProvider(Provider.LOCAL);
		}
		app.openFile(fileToHandle, callback);
		close();
	}

	private void showEmptyListNotification() {
		imagePanel = new FlowPanel();
		imagePanel.setStyleName("emptyMaterialListInfo");
		Image image = new NoDragImage(
				MaterialDesignResources.INSTANCE.mow_lightbulb(), 112, 112);
		// init texts
		Label caption = new Label(localize("emptyMaterialList.caption.mow"));
		caption.setStyleName("caption");
		Label info = new Label(localize("emptyMaterialList.info.mow"));
		info.setStyleName("info");
		// build panel
		imagePanel.add(image);
		imagePanel.add(caption);
		imagePanel.add(info);
		// add panel to content panel
		contentPanel.add(imagePanel);
	}

	private void setExtendedButtonStyle() {
		newFileBtn.setStyleName("extendedFAB");
		newFileBtn.addStyleName("FABteal");
		newFileBtn.addStyleName("buttonMargin24");
		openFileBtn.setStyleName("extendedFAB");
		openFileBtn.addStyleName("FABwhite");
		buttonPanel.addStyleName("center");
		contentPanel.addStyleName("empty");
	}

	@Override
	public AppW getApp() {
		return app;
	}

	@Override
	public void resizeTo(int width, int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setMaterialsDefaultStyle() {
		// TODO Auto-generated method stub
	}

	@Override
	public void loadAllMaterials() {
		clearMaterials();
		if (this.app.getLoginOperation().isLoggedIn()) {
			app.getLoginOperation().getGeoGebraTubeAPI()
					.getUsersOwnMaterials(this.userMaterialsCB,
							order);
		} else {
			app.getLoginOperation().getGeoGebraTubeAPI()
					.getFeaturedMaterials(this.ggtMaterialsCB);
		}
	}

	@Override
	public void clearMaterials() {
		materialPanel.clear();
	}

	@Override
	public void disableMaterials() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSearchResults(List<Material> response,
			ArrayList<Chapter> chapters) {
		// TODO Auto-generated method stub
	}

	@Override
	public void displaySearchResults(String query) {
		// TODO Auto-generated method stub
	}

	@Override
	public void refreshMaterial(Material material, boolean isLocal) {
		// TODO Auto-generated method stub
	}

	@Override
	public void rememberSelected(MaterialListElementI materialElement) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setLabels() {
		headerCaption.setText(
				app.getLocalization().getMenu("mow.openFileViewTitle"));
	}

	@Override
	public void addMaterial(Material material) {
		for (int i = 0; i < materialPanel.getWidgetCount(); i++) {
			Widget wgt = materialPanel.getWidget(i);
			if (wgt instanceof MaterialCard
					&& isBefore(material, ((MaterialCard) wgt).getMaterial())) {
				materialPanel.insert(new MaterialCard(material, app), i);
				return;
			}
		}
		materialPanel.add(new MaterialCard(material, app));
	}

	private boolean isBefore(Material material, Material material2) {
		switch (order) {
		case title:
			return material.getTitle().compareTo(material2.getTitle()) <= 0;
		case created:
			return material.getDateCreated() > material2.getDateCreated();
		case timestamp:
			return material.getTimestamp() > material2.getTimestamp();
		default:
			return false;
		}

	}

	@Override
	public void removeMaterial(Material material) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onOpenFile() {
		// TODO
		return false;
	}

	private MaterialCallback getUserMaterialsCB() {
		return new MaterialCallback() {

			@Override
			public void onLoaded(final List<Material> parseResponse,
					ArrayList<Chapter> meta) {
				addUsersMaterials(parseResponse);
				addContent();
			}
		};
	}

	/**
	 * Adds the given {@link Material materials}.
	 * 
	 * @param matList
	 *            List of materials
	 */
	public void addUsersMaterials(final List<Material> matList) {
		if (matList.size() > 0) {
			materialListEmpty = false;
		}
		for (int i = 0; i < matList.size(); i++) {
			addMaterial(matList.get(i));
		}
	}

	private MaterialCallback getGgtMaterialsCB() {
		return new MaterialCallback() {
			@Override
			public void onError(final Throwable exception) {
				exception.printStackTrace();
				Log.debug(exception.getMessage());
			}

			@Override
			public void onLoaded(final List<Material> response,
					ArrayList<Chapter> meta) {
				addGGTMaterials(response, meta);
				addContent();
			}
		};
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
		if (matList.size() > 0) {
			materialListEmpty = false;
		}
		if (chapters == null || chapters.size() < 2) {
			for (final Material mat : matList) {
				addMaterial(mat);
			}
		}
	}
}
