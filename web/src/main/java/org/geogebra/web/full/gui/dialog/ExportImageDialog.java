package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.ClipboardUtil;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.dom.client.IFrameElement;
import org.gwtproject.dom.client.Style;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Frame;
import org.gwtproject.user.client.ui.Label;

import elemental2.dom.HTMLIFrameElement;
import elemental2.dom.HTMLImageElement;
import jsinterop.base.Js;

/**
 * @author csilla
 *
 */
public class ExportImageDialog extends ComponentDialog {
	private FlowPanel contentPanel;
	private NoDragImage previewImage;
	private Frame iframePDF;
	private String base64Url;

	/**
	 * @param app
	 *            see {@link AppW}
	 * @param data
	 *            dialog translation keys
	 * @param base64Image
	 *            optional image
	 */
	public ExportImageDialog(AppW app, DialogData data, String base64Image) {
		super(app, data, true, true);
		addStyleName("exportImgDialog");
		if (base64Image != null) {
			setPreviewImage(base64Image);
		} else {
			setPreviewImage(getExportDataURL(app));
		}
		buildContent();

		setOnPositiveAction(() -> {
			Browser.exportImage(base64Url,
					app.getExportTitle() + getExtension(base64Url));
		});
		setOnNegativeAction(app::copyGraphicsViewToClipboard);
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

	private void buildContent() {
		contentPanel = new FlowPanel();
		contentPanel.addStyleName("expImgContent");
		if (!ClipboardUtil.isCopyImageToClipboardAvailable()) {
			Label rightClickText = new Label(app.getLocalization().getMenu("expImgRightClickMsg"));
			rightClickText.addStyleName("rightClickHelpText");
			contentPanel.add(rightClickText);
		}
		if (previewImage != null) {
			contentPanel.add(previewImage);
		} else if (iframePDF != null) {
			contentPanel.add(iframePDF);
		}
		addDialogContent(contentPanel);
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

				Js.<HTMLIFrameElement>uncheckedCast(iframe)
						.addEventListener("load", (event) -> center());
			} else {
				previewImage = new NoDragImage(imgStr);
				previewImage.addStyleName("prevImg");
				Browser.setAllowContextMenu(previewImage.getElement(), true);
				Js.<HTMLImageElement>uncheckedCast(previewImage.getElement())
						.addEventListener("load", (event) -> center());
			}
		}
	}
}
