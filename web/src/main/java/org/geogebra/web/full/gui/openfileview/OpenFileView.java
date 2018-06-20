package org.geogebra.web.full.gui.openfileview;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.MyHeaderPanel;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.gui.view.browser.BrowseViewI;
import org.geogebra.web.html5.gui.view.browser.MaterialListElementI;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * View for browsing materials
 * 
 * @author Alicia
 *
 */
public class OpenFileView extends MyHeaderPanel implements BrowseViewI {
	private AppW app;
	private FlowPanel headerPanel;
	private StandardButton backBtn;
	private Label headerCaption;

	/**
	 * @param app
	 *            application
	 */
	public OpenFileView(AppW app) {
		this.app = app;
		initGUI();
	}

	private void initGUI() {
		this.setStyleName("openFileView");
		initHeader();
	}

	private void initHeader() {
		headerPanel = new FlowPanel();
		headerPanel.addStyleName("openFileViewHeader");

		backBtn = new StandardButton(
				MaterialDesignResources.INSTANCE.mow_back_arrow(),
				null, 24,
				app);
		backBtn.addStyleName("headerBackButton");
		backBtn.addFastClickHandler(new FastClickHandler() {

			public void onClick(Widget source) {
				close();
			}
		});
		headerPanel.add(backBtn);

		headerCaption = new Label(
				app.getLocalization().getMenu("mow.openFileViewTitle"));
		headerCaption.addStyleName("headerCaption");
		headerPanel.add(headerCaption);

		this.add(headerPanel);
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
		// TODO Auto-generated method stub

	}

	public void addMaterial(Material material) {
		// TODO Auto-generated method stub

	}

	public void removeMaterial(Material material) {
		// TODO Auto-generated method stub

	}
}
