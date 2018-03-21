package org.geogebra.web.full.gui.dialog;

import org.geogebra.web.html5.Browser;
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
	private StandardButton copyToClipboardBtn;

	/**
	 * @param app
	 *            see {@link AppW}
	 */
	public ExportImageDialog(AppW app) {
		super(app.getPanel(), app);
		setAutoHideEnabled(true);
		this.appW = app;
		initGui();
		initActions();
	}

	private void initGui() {
		mainPanel = new FlowPanel();
		contentPanel = new FlowPanel();
		contentPanel.addStyleName("expImgContent");
		if (appW.isCopyImageToClipboardAvailable()) {
			rightClickText = new Label();
			rightClickText.addStyleName("rightClickHelpText");
			contentPanel.add(rightClickText);
		}
		// create image preview
		setPreviewImage(((EuclidianViewWInterface) app.getActiveEuclidianView())
				.getExportImageDataUrl(1, true));
		contentPanel.add(previewImage);
		// panel for buttons
		downloadBtn = new StandardButton("", appW);
		if (!appW.isCopyImageToClipboardAvailable()) {
			copyToClipboardBtn = new StandardButton("", appW);
			copyToClipboardBtn.addStyleName("copyToClipBtn");
		}
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		if (!appW.isCopyImageToClipboardAvailable()) {
			buttonPanel.add(copyToClipboardBtn);
			buttonPanel.addStyleName("withCopyToClip");
		}
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
		if (!appW.isCopyImageToClipboardAvailable()) {
			copyToClipboardBtn.addFastClickHandler(this);
		}
	}

	@Override
	public void onClick(Widget source) {
		if (source == downloadBtn) {
			// DOWNLOAD AS PNG
			Browser.exportImage(previewImage.getUrl(),
					appW.getExportTitle() + ".png");
			super.hide();
		} else if (source == copyToClipboardBtn) {
			// COPY TO CLIPBOARD
		}
	}

	/**
	 * set button labels and dialog title
	 */
	public void setLabels() {
		getCaption().setText(appW.getLocalization().getMenu("exportImage")); // dialog
		// title
		if (appW.isCopyImageToClipboardAvailable()) {
			rightClickText
				.setText(appW.getLocalization().getMenu("expImgRightClickMsg"));
		} else {
			copyToClipboardBtn
					.setText(appW.getLocalization().getMenu("CopyToClipboard"));
		}
		downloadBtn.setText(appW.getLocalization().getMenu("Download")); // download
	}

	private void setPreviewImage(String imgStr) {
		if (imgStr != null && imgStr.length() > 0) {
			previewImage = new NoDragImage(imgStr);
			previewImage.addStyleName("prevImg");
		}
	}

}
