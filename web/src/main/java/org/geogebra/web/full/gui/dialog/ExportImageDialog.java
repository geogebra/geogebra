package org.geogebra.web.full.gui.dialog;

import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author csilla
 *
 */
public class ExportImageDialog extends DialogBoxW implements FastClickHandler {
	private AppW appW;
	private FlowPanel mainPanel;
	private FlowPanel contentPanel;
	private Label rightClickText;
	private NoDragImage previewImage;
	private FlowPanel buttonPanel;
	private StandardButton downloadBtn;

	/**
	 * @param app
	 *            see {@link AppW}
	 */
	public ExportImageDialog(AppW app) {
		super(app.getPanel(), app);
		this.appW = app;
		initGui();
		initActions();
	}

	private void initGui() {
		mainPanel = new FlowPanel();
		contentPanel = new FlowPanel();
		contentPanel.addStyleName("expImgContent");
		rightClickText = new Label();
		rightClickText.addStyleName("rightClickHelpText");
		contentPanel.add(rightClickText);
		// create image preview
		setPreviewImage(((EuclidianViewWInterface) app.getActiveEuclidianView())
				.getExportImageDataUrl(1, true));
		contentPanel.add(previewImage);
		// panel for buttons
		downloadBtn = new StandardButton("", appW);
		// downloadBtn.addStyleName("insertBtn");
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		buttonPanel.add(downloadBtn);
		// add panels
		add(mainPanel);
		mainPanel.add(contentPanel);
		mainPanel.add(buttonPanel);
		// style
		addStyleName("GeoGebraPopup");
		addStyleName("exportImgDialog");
		setGlassEnabled(true);
		setLabels();
	}

	private void initActions() {
		downloadBtn.addFastClickHandler(this);
	}

	@Override
	public void onClick(Widget source) {
		if (source == downloadBtn) {
			// TO DO DOWNLOAD AS PNG
			super.hide();
		}
	}

	/**
	 * set button labels and dialog title
	 */
	public void setLabels() {
		getCaption().setText(appW.getLocalization().getMenu("exportImage")); // dialog
		// title
		rightClickText
				.setText(appW.getLocalization().getMenu("expImgRightClickMsg"));
		downloadBtn.setText(appW.getLocalization().getMenu("Download")); // download
	}

	private void setPreviewImage(String imgStr) {
		if (imgStr != null && imgStr.length() > 0) {
			previewImage = new NoDragImage(imgStr);
			previewImage.addStyleName("prevImg");
		}
	}

}
