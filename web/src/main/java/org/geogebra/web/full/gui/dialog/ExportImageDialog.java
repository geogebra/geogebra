package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ImageLoadCallback;
import org.geogebra.web.html5.util.ImageWrapper;
import org.geogebra.web.shared.DialogBoxW;

import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
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
	private Frame iframePDF;
	private String base64Url;
	private FlowPanel buttonPanel;
	private StandardButton downloadBtn;
	private StandardButton copyToClipboardBtn;

	/**
	 * @param app
	 *            see {@link AppW}
	 * @param base64Image
	 *            optional image
	 */
	public ExportImageDialog(AppW app, String base64Image) {
		super(app.getPanel(), app);
		setAutoHideEnabled(true);
		this.appW = app;
		if (base64Image != null) {
			setPreviewImage(base64Image);
		} else {
			setPreviewImage(getExportDataURL(app));
		}
		initGui();
		initActions();
	}

	/**
	 * @param app
	 *            app
	 * @return data URL of high resolution PNG export
	 */
	public static String getExportDataURL(AppW app) {
		// aim for a reasonable size export
		double width = 3000;
		// with scale 1 unit : 1 cm
		double scaleCM = 1;

		EuclidianView ev = app.getActiveEuclidianView();
		double viewWidth = ev.getExportWidth();
		double xScale = ev.getXscale();

		// logic copied from CmdExportImage
		double widthRW = viewWidth / xScale;
		double dpcm = width / (widthRW * scaleCM);
		int dpi = (int) (dpcm * 2.54);
		double pixelWidth = Math.round(dpcm * widthRW * scaleCM);
		double exportScale = pixelWidth / viewWidth;
		boolean transparent = false;
		boolean greyscale = false;

		return StringUtil.pngMarker
				+ app.getGgbApi().getPNGBase64(exportScale, transparent, dpi,
						false, greyscale);
	}

	private void initGui() {
		mainPanel = new FlowPanel();
		contentPanel = new FlowPanel();
		contentPanel.addStyleName("expImgContent");
		if (!appW.isCopyImageToClipboardAvailable()) {
			rightClickText = new Label();
			rightClickText.addStyleName("rightClickHelpText");
			contentPanel.add(rightClickText);
		}
		if (previewImage != null) {
			contentPanel.add(previewImage);
		} else if (iframePDF != null) {
			contentPanel.add(iframePDF);
		}
		// panel for buttons
		downloadBtn = new StandardButton("", appW);
		if (!appW.isUnbundled() && !appW.isWhiteboardActive()) {
			downloadBtn.addStyleName("gwt-Button");
			downloadBtn.addStyleName("downloadBtn");
		}
		if (appW.isCopyImageToClipboardAvailable()) {
			copyToClipboardBtn = new StandardButton("", appW);
			copyToClipboardBtn.addStyleName("copyToClipBtn");
			if (!appW.isUnbundled()) {
				copyToClipboardBtn.addStyleName("gwt-Button");
			}
		}
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		if (copyToClipboardBtn != null) {
			buttonPanel.add(copyToClipboardBtn);
			buttonPanel.addStyleName("withCopyToClip");
			if (!appW.isUnbundled() && !appW.isWhiteboardActive()) {
				buttonPanel.addStyleName("classic");
			}
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
		center();
	}

	private void initActions() {
		downloadBtn.addFastClickHandler(this);
		if (copyToClipboardBtn != null) {
			copyToClipboardBtn.addFastClickHandler(this);
		}
	}

	@Override
	public void onClick(Widget source) {
		if (source == downloadBtn) {
			// DOWNLOAD AS PNG/SVG/PDF
			Browser.exportImage(base64Url,
					appW.getExportTitle() + getExtension(base64Url));
			super.hide();
		} else if (source == copyToClipboardBtn) {
			app.copyGraphicsViewToClipboard();
		}
	}

	private static String getExtension(String url) {
		if (url.startsWith(StringUtil.svgMarker)) {
			return ".svg";
		} else if (url.startsWith(StringUtil.pdfMarker)) {
			return ".pdf";
		} else {
			return ".png";
		}
	}

	/**
	 * set button labels and dialog title
	 */
	public void setLabels() {
		getCaption().setText(appW.getLocalization().getMenu("exportImage")); // dialog
		// no right click message for:
		// PDF
		// iOS
		// Android
		if (rightClickText != null && previewImage != null && !Browser.isMobile()) {
			rightClickText.setText(
					appW.getLocalization().getMenu("expImgRightClickMsg"));
		}
		if (copyToClipboardBtn != null) {
			copyToClipboardBtn
					.setText(appW.getLocalization().getMenu("CopyToClipboard"));
		}
		downloadBtn.setText(appW.getLocalization().getMenu("Download")); // download
	}

	private void setPreviewImage(String imgStr) {
		if (imgStr != null && imgStr.length() > 0) {

			base64Url = imgStr;

			if (imgStr.startsWith(StringUtil.pdfMarker)) {

				iframePDF = new Frame(imgStr);

				IFrameElement iframe = iframePDF.getElement().cast();

				Style style = iframe.getStyle();
				style.setHeight(600, Style.Unit.PX);
				style.setWidth(600, Style.Unit.PX);

				iframe.setFrameBorder(0);
				iframe.setTabIndex(-1);
				iframe.setSrc(imgStr);

				ImageWrapper.nativeon(iframe, "load", new ImageLoadCallback() {

					@Override
					public void onLoad() {
						center();
					}
				});

			} else {

				previewImage = new NoDragImage(imgStr);
				previewImage.addStyleName("prevImg");
				Browser.setAllowContextMenu(previewImage.getElement(), true);
				ImageElement img = previewImage.getElement().cast();
				ImageWrapper.nativeon(img, "load", new ImageLoadCallback() {

					@Override
					public void onLoad() {
						center();
					}
				});
			}
		}
	}

}
