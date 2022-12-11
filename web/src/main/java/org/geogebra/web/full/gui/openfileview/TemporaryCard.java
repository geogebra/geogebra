package org.geogebra.web.full.gui.openfileview;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.full.gui.CardInfoPanel;
import org.geogebra.web.full.gui.browser.MaterialCardController;
import org.geogebra.web.full.gui.util.ExamSaveDialog;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.user.client.ui.FlowPanel;

/**
 * Material card
 */
public class TemporaryCard extends FlowPanel {
	private final AppW app;
	private final MaterialCardController controller;

	/**
	 * @param m
	 *            material
	 * @param app
	 *            see {@link AppW}
	 */
	public TemporaryCard(final Material m, final AppW app) {
		this.app = app;
		controller = new MaterialCardController(app);
		controller.setMaterial(m);
		initGui();
		this.addDomHandler(event -> checkUnsavedAndOpen(), ClickEvent.getType());
	}

	private void checkUnsavedAndOpen() {
		if (app.isSaved()) {
			openMaterial();
		} else {
			app.getGuiManager().getBrowseView().close();
			new ExamSaveDialog(app, this::openMaterial).show();
		}
	}

	/**
	 * Open this material.
	 */
	private void openMaterial() {
		Material material = controller.getMaterial();
		app.getGgbApi().setBase64(material.getBase64());
		controller.onOpenFile();
	}

	private void initGui() {
		this.setStyleName("materialCard");
		Material material = controller.getMaterial();
		MaterialImagePanel imgPanel = new MaterialImagePanel(material);
		CardInfoPanel infoPanel = new CardInfoPanel(material.getTitle(), "");
		add(imgPanel);
		add(infoPanel);
	}
}