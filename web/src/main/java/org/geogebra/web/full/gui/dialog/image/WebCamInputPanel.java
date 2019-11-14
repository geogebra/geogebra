package org.geogebra.web.full.gui.dialog.image;

import org.geogebra.common.GeoGebraConstants.Platform;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.webcam.WebCamAPI;
import org.geogebra.web.html5.webcam.WebCamInterface;
import org.geogebra.web.html5.webcam.WebcamDialogInterface;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Panel for HTML5 webcam input
 */
public class WebCamInputPanel extends VerticalPanel implements WebCamInterface {

	private SimplePanel inputWidget;
	private Element video;
	private Element errorPanel;
	private int canvasWidth = 640;
	private int canvasHeight = 480; // overwritten by real
									// dimensions
	private AppW app;
	private WebcamDialogInterface webcamDialog;
	private WebcamPermissionDialog permissionDialog;
	private static final VideoTemplate TEMPLATE = GWT.create(VideoTemplate.class);
	private WebCamAPI webCam;

	/**
	 * @param app
	 *            application
	 *
	 * @param webcamDialog
	 *            webcam dialog
	 */
	public WebCamInputPanel(AppW app, WebcamDialogInterface webcamDialog) {
		this.app = app;
		webCam = new WebCamAPI(this);
		this.webcamDialog = webcamDialog;
		initGUI();
	}

	private void initGUI() {
		inputWidget = new SimplePanel();
		resetVideo();
		add(inputWidget);
	}

	public void stopVideo() {
		webCam.stop();
	}

	@Override
	public void onCameraSuccess(JavaScriptObject bs) {
		hidePermissionDialog();
		webcamDialog.onCameraSuccess();
	}

	/**
	 * @return screenshot as data URL (png)
	 */
	public String getImageDataURL() {
		if (video == null) {
			return null;
		}
		String capture = webCam.takeScreenshot(video.getFirstChildElement());
		if (!app.isWhiteboardActive()) {
			webCam.stop();
		}
		return capture;
	}

	/**
	 * Starts recording
	 */
	public void startVideo() {
		webCam.stop();
		inputWidget.getElement().removeAllChildren();
		resetVideo();
	}

	private void resetVideo() {
		Localization loc = app.getLocalization();
		String message;
		if (app.getPlatform() == Platform.WEB) {
			message = "";
		} else if (Browser.isFirefox()) {
			message = loc.getMenu("Webcam.Firefox");
		} else if (Browser.isEdge()) {
			message = loc.getMenu("Webcam.Edge");
		} else {
			message = loc.getMenu("Webcam.Chrome");
		}

		errorPanel = DOM.createSpan();
		video = DOM.createSpan();
		errorPanel.setInnerSafeHtml(TEMPLATE.error(getStyle(), message));
		inputWidget.getElement().appendChild(video);
		inputWidget.getElement().appendChild(errorPanel);
		video.setInnerSafeHtml(TEMPLATE.video(getStyle(),
				loc.getMenu("Webcam.Problem")));
		webCam.start(video, errorPanel);
	}

	private String getStyle() {
		if (app.isWhiteboardActive()) {
			return "mowCameraInputPanel";
		}
		return "webcamInputPanel";
	}

	/**
	 * @return true if the video stream is empty
	 */
	public boolean isStreamEmpty() {
		return webCam.isStreamEmpty();
	}

	private void showAndResizeInputDialog() {
		if (webcamDialog != null) {
			webcamDialog.showAndResize();
		}
	}

	private void hidePermissionDialog() {
		if (permissionDialog != null) {
			permissionDialog.hide();
		}
	}

	private void showPermissionDialog(WebcamPermissionDialog.DialogType dialogType) {
		hidePermissionDialog();
		permissionDialog = new WebcamPermissionDialog(app, dialogType);
		permissionDialog.center();
		permissionDialog.show();
	}

	private void showRequestDialog() {
		showPermissionDialog(WebcamPermissionDialog.DialogType.PERMISSION_REQUEST);
	}

	private void showPermissionDeniedDialog() {
		showPermissionDialog(WebcamPermissionDialog.DialogType.PERMISSION_DENIED);
	}

	private void showErrorDialog() {
		showPermissionDialog(WebcamPermissionDialog.DialogType.ERROR);
	}

	private void showNotSupportedDialog() {
		showPermissionDialog(WebcamPermissionDialog.DialogType.NOT_SUPPORTED);
	}

	/**
	 * @return width of video
	 */
	public int getVideoWidth() {
		return canvasWidth;
	}

	/**
	 * @return height of video
	 */
	public int getVideoHeight() {
		return canvasHeight;
	}

	@Override
	public void onLoadedMetadata(int width, int height) {
		canvasWidth = width;
		canvasHeight = height;
		showAndResizeInputDialog();
	}

	@Override
	public void onCameraError(String errName) {
		webcamDialog.onCameraError();
		if ("PermissionDeniedError".equals(errName)
				|| "NotAllowedError".equals(errName)
				|| (Browser.isElectron() && Browser.isMacOS())
				&& "TrackStartError".equals(errName)) {
			showPermissionDeniedDialog();
		} else if ("NotFoundError".equals(errName)
				|| "DevicesNotFoundError".equals(errName)
				|| "TrackStartError".equals(errName)
				|| "NotReadableError".equals(errName)
				|| "SourceUnavailableError".equals(errName)
				|| "Error".equals(errName)) {
			showErrorDialog();
			// permission denied by user
		}
		Log.debug("Error from WebCam: " + errName);
	}

	@Override
	public void onRequest() {
		showRequestDialog();
	}

	@Override
	public void onNotSupported() {
		showNotSupportedDialog();
	}
	
	public interface VideoTemplate extends SafeHtmlTemplates {
		@SafeHtmlTemplates.Template("<video autoplay class=\"{0}\"><br>\r\n"
				+ "  {1}</video>")
		SafeHtml video(String style, String err);

		@SafeHtmlTemplates.Template("<span class=\"{0}\">{1}</span>")
		SafeHtml error(String style, String message);
	}
}
