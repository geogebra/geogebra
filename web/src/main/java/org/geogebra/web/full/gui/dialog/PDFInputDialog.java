package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author csilla
 *
 */
public class PDFInputDialog extends DialogBoxW implements FastClickHandler {
	/**
	 * see {@link App}
	 */
	protected AppW appW;
	private FlowPanel mainPanel;
	private FlowPanel pdfContainerPanel;
	private FlowPanel buttonPanel;
	private StandardButton insertBtn;
	private StandardButton cancelBtn;

	/**
	 * @param app
	 *            see {@link App}
	 */
	public PDFInputDialog(AppW app) {
		super(app.getPanel(), app);
		this.appW = app;
		initGui();
		initActions();
	}

	private void initGui() {
		mainPanel = new FlowPanel();
		pdfContainerPanel = new FlowPanel();
		pdfContainerPanel.setStyleName("pdfContainer");
		// panel for buttons
		insertBtn = new StandardButton("", appW);
		insertBtn.addStyleName("insertBtn");
		insertBtn.setEnabled(false);
		cancelBtn = new StandardButton("", app);
		cancelBtn.addStyleName("cancelBtn");
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		buttonPanel.add(cancelBtn);
		buttonPanel.add(insertBtn);
		// add panels
		add(mainPanel);
		mainPanel.add(pdfContainerPanel);
		mainPanel.add(buttonPanel);
		// style
		addStyleName("GeoGebraPopup");
		addStyleName("pdfDialog");
		setGlassEnabled(true);
		setLabels();
	}

	private void initActions() {
		getCaption().setText(appW.getLocalization().getMenu("pdfDialogTitle"));
		insertBtn.addFastClickHandler(this);
		cancelBtn.addFastClickHandler(this);
	}

	/**
	 * set button labels
	 */
	public void setLabels() {
		insertBtn.setText(appW.getLocalization().getMenu("Insert")); // insert
		cancelBtn.setText(appW.getLocalization().getMenu("Cancel")); // cancel
	}

	public void onClick(Widget source) {
		if (source == cancelBtn) {
			hide();
		} else if (source == insertBtn) {
			// TODO process pdf
		}
	}

	@Override
	public void hide() {
		appW.getGuiManager().setMode(EuclidianConstants.MODE_MOVE,
				ModeSetter.TOOLBAR);
		super.hide();
	}
}
