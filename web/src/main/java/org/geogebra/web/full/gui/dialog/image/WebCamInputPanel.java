package org.geogebra.web.full.gui.dialog.image;

import org.geogebra.common.util.debug.Log;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.full.gui.laf.BundleLookAndFeel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.webcam.WebcamDialogInterface;
import org.geogebra.web.shared.components.dialog.DialogData;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLVideoElement;
import jsinterop.base.Js;

/**
 * Panel for HTML5 webcam input
 */
public class WebCamInputPanel extends FlowPanel {
	private final boolean isElectronMac;
	private SimplePanel inputWidget;
	private HTMLVideoElement video;
	private int canvasWidth = 640;
	private int canvasHeight = 480; // overwritten by real
									// dimensions
	private final AppW app;
	private final WebcamDialogInterface webcamDialog;
	private WebcamPermissionDialog permissionDialog;
	private final WebCamAPI webCam;

	/**
	 * @param app
	 *            application
	 *
	 * @param webcamDialog
	 *            webcam dialog
	 */
	public WebCamInputPanel(AppW app, WebcamDialogInterface webcamDialog) {
		this.app = app;
		this.isElectronMac = (app.getLAF() instanceof BundleLookAndFeel) && NavigatorUtil.isMacOS();
		webCam = new WebCamAPI(this);
		this.webcamDialog = webcamDialog;
		initGUI();
	}

	private void initGUI() {
		inputWidget = new SimplePanel();
		add(inputWidget);
	}

	/**
	 * stop camera
	 */
	public void stopVideo() {
		webCam.stop();
	}

	/**
	 * on camera success
	 */
	public void onCameraSuccess() {
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
		String capture = webCam.takeScreenshot(video);
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
		video = (HTMLVideoElement) DomGlobal.document.createElement("video");
		video.setAttribute("autoplay", "");
		video.className = "webcamInputPanel";

		inputWidget.getElement().appendChild(Js.uncheckedCast(video));
		webCam.start(video);
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

	private void showPermissionDialog(DialogData data, String msgTranskey) {
		hidePermissionDialog();
		permissionDialog = new WebcamPermissionDialog(app, data, msgTranskey);
		permissionDialog.center();
		permissionDialog.show();
	}

	private String getPermissionDeniedTitleKey() {
		return isElectronMac && !app.isMebis() ? "permission.camera.denied"
				: "Webcam.Denied.Caption";
	}

	private String getPermissionDeniedMessageKey() {
		return isElectronMac && !app.isMebis() ? "permission.request"
				: "Webcam.Denied.Message";
	}

	private void showRequestDialog() {
		DialogData data = new DialogData(app.getVendorSettings()
				.getMenuLocalizationKey("Webcam.Request"),
				null, null);
		showPermissionDialog(data, app.getVendorSettings()
				.getMenuLocalizationKey("Webcam.Request.Message"));
	}

	private void showPermissionDeniedDialog() {
		if (!app.isWhiteboardActive()) {
			webcamDialog.onCameraError(getPermissionDeniedTitleKey(),
					getPermissionDeniedMessageKey());
		} else {
			DialogData data = new DialogData(getPermissionDeniedTitleKey(),
					null, "OK");
			showPermissionDialog(data, getPermissionDeniedMessageKey());
		}
	}

	private void showErrorDialog() {
		if (!app.isWhiteboardActive()) {
			webcamDialog.onCameraError("Webcam.Problem",
					"Webcam.Problem.Message");
		} else {
			DialogData data = new DialogData("Webcam.Problem",
					null, "OK");
			showPermissionDialog(data, app.getVendorSettings()
					.getMenuLocalizationKey("Webcam.Problem.Message"));
		}
	}

	private void showNotSupportedDialog() {
		DialogData data = new DialogData("Webcam.Notsupported.Caption",
				null, "OK");
		showPermissionDialog(data, "Webcam.Notsupported.Message");
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

	/**
	 * load size and adjust dialog
	 * @param width - width
	 * @param height - height
	 */
	public void onLoadedMetadata(int width, int height) {
		canvasWidth = width;
		canvasHeight = height;
		showAndResizeInputDialog();
	}

	/**
	 * on camera error
	 * @param errName - error
	 */
	public void onCameraError(String errName) {
		if ("PermissionDeniedError".equals(errName)
				|| "NotAllowedError".equals(errName)
				|| isElectronMac
				&& "TrackStartError".equals(errName)) {
			// permission denied by user
			showPermissionDeniedDialog();
		} else if ("NotFoundError".equals(errName)
				|| "DevicesNotFoundError".equals(errName)
				|| "TrackStartError".equals(errName)
				|| "NotReadableError".equals(errName)
				|| "SourceUnavailableError".equals(errName)
				|| "Error".equals(errName)) {
			showErrorDialog();
		}
		Log.debug("Error from WebCam: " + errName);
	}

	/**
	 * on camera request
	 */
	public void onRequest() {
		if (app.isWhiteboardActive()) {
			showRequestDialog();
		} else {
			webcamDialog.onCameraError("Webcam.Request",
					"Webcam.Request.Message");
		}
	}

	/**
	 * camera not supported
	 */
	public void onNotSupported() {
		if (app.isWhiteboardActive()) {
			showNotSupportedDialog();
		} else {
			webcamDialog.onCameraError("Webcam.Notsupported.Caption",
					"Webcam.Notsupported.Message");
		}
	}
}