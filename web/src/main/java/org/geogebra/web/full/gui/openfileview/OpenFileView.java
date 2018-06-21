package org.geogebra.web.full.gui.openfileview;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.Feature;
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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * View for browsing materials
 * 
 * @author Alicia
 *
 */
public class OpenFileView extends MyHeaderPanel implements BrowseViewI {
	/**
	 * application
	 */
	protected AppW app;
	// header
	private FlowPanel headerPanel;
	private StandardButton backBtn;
	private Label headerCaption;

	// content panel
	private VerticalPanel contentPanel;
	// button panel
	private FlowPanel buttonPanel;
	private StandardButton newFileBtn;
	private FileOpenButton openFileBtn;

	/**
	 * @param app
	 *            application
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
				app.getLocalization().getMenu("mow.openFileViewTitle"));
		headerCaption.setStyleName("headerCaption");
		headerPanel.add(headerCaption);

		this.setHeaderWidget(headerPanel);
	}

	private void initContentPanel() {
		contentPanel = new VerticalPanel();
		contentPanel.setStyleName("fileViewContentPanel");
		this.setContentWidget(contentPanel);
	}

	private void initButtonPanel() {
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("fileViewButtonPanel");

		newFileBtn = new StandardButton(
				MaterialDesignResources.INSTANCE.add_black(),
				app.getLocalization().getMenu("mow.newFile"), 18, app);
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
				app.getLocalization().getMenu("mow.openFile"));
		openFileBtn.addStyleName("buttonMargin");
		buttonPanel.add(openFileBtn);

		contentPanel.add(buttonPanel);
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
}
