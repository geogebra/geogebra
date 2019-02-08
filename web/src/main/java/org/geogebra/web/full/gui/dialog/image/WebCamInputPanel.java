package org.geogebra.web.full.gui.dialog.image;

import org.geogebra.common.GeoGebraConstants.Versions;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Panel for HTML5 webcam input
 */
public class WebCamInputPanel extends VerticalPanel {
	
	private SimplePanel inputWidget;
	private Element video;
	private JavaScriptObject stream;
	private int canvasWidth = 640;
	private int canvasHeight = 480; // overwritten by real
														// dimensions
	private AppW app;
	private static final int MAX_CANVAS_WIDTH = 640;
	private WebcamInputDialog webcamDialog;
	private WebcamPermissionDialog permissionDialog;

	/**
	 * @param app
	 *            application
	 */
	public WebCamInputPanel(AppW app) {
	    this.app = app;
	    initGUI();
    }

	/**
	 * @param app
	 *            application
	 * @param webcamDialog
	 *            webcam dialog
	 */
	public WebCamInputPanel(AppW app, WebcamInputDialog webcamDialog) {
		this(app);
		this.webcamDialog = webcamDialog;
	}

	private void initGUI() {		
		inputWidget = new SimplePanel();
		resetVideo();
		add(inputWidget);
	}

	private native Element populate(Element el, String message,
			String errorMessage) /*-{
									
									el.style.position = "relative";
									var dependentStyle = this.@org.geogebra.web.full.gui.dialog.image.WebCamInputPanel::getStyle()();
									
									var ihtml = "<span class=" + dependentStyle + "><br><br>" + message
									+ "</span>\n";
									ihtml += "<video autoplay class=" + dependentStyle + "><br><br>"
									+ errorMessage + "</video>";
									
									el.innerHTML = ihtml;
									var video = el.lastChild;
									
									$wnd.navigator.getMedia = ($wnd.navigator.getUserMedia
									|| $wnd.navigator.webkitGetUserMedia
									|| $wnd.navigator.mozGetUserMedia || $wnd.navigator.msGetUserMedia);
									
									$wnd.URL = $wnd.URL || $wnd.webkitURL || $wnd.msURL || $wnd.mozURL
									|| $wnd.oURL || null;
									var that = this;
									
									if ($wnd.navigator.getMedia) {
									try {
									var browserAlreadyAllowed = false;
									var accessDenied = false;
									$wnd.navigator
									.getMedia(
									{
									video : true
									},
									function(bs) {
									browserAlreadyAllowed = true;
									that.@org.geogebra.web.full.gui.dialog.image.WebCamInputPanel::hidePermissionDialog()();
									if ($wnd.URL && $wnd.URL.createObjectURL) {
										video.src = $wnd.URL
												.createObjectURL(bs);
										el.firstChild.style.display = "none";
										video.onloadedmetadata = function(e) {
											that.@org.geogebra.web.full.gui.dialog.image.WebCamInputPanel::canvasWidth = video.videoWidth;
											that.@org.geogebra.web.full.gui.dialog.image.WebCamInputPanel::canvasHeight = video.videoHeight;
											that.@org.geogebra.web.full.gui.dialog.image.WebCamInputPanel::showInputDialog()();
											that.@org.geogebra.web.full.gui.dialog.image.WebCamInputPanel::resize()();
										};
									} else {
										video.src = bs;
										el.firstChild.style.display = "none";
									}
									that.@org.geogebra.web.full.gui.dialog.image.WebCamInputPanel::stream = bs;
									},
									function(err) {
									accessDenied = true;
									// camera not found or not working
									if (err.name == "NotFoundError"
											|| err.name == "DevicesNotFoundError"
											|| err.name == "TrackStartError"
											|| err.name == "NotReadableError"
											|| err.name == "SourceUnavailableError"
											|| err.name == "Error") {
										that.@org.geogebra.web.full.gui.dialog.image.WebCamInputPanel::showErrorDialog()();
										// permission denied by user
									} else if (err.name == "PermissionDeniedError"
											|| err.name == "NotAllowedError") {
										that.@org.geogebra.web.full.gui.dialog.image.WebCamInputPanel::showPermissionDeniedDialog()();
									}
									@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("Error from WebCam: " + err.name);
									});
									function accessRequest() {
									if (!browserAlreadyAllowed && !accessDenied) {
									that.@org.geogebra.web.full.gui.dialog.image.WebCamInputPanel::showRequestDialog()();
									}
									}
									setTimeout(accessRequest, 400);
									
									return video;
									} catch (e) {
									el.firstChild.innerHTML = "<br><br>" + errorMessage;
									that.@org.geogebra.web.full.gui.dialog.image.WebCamInputPanel::showErrorDialog()();
									return null;
									}
									} else {
									el.firstChild.innerHTML = "<br><br>" + errorMessage;
									that.@org.geogebra.web.full.gui.dialog.image.WebCamInputPanel::showNotSupportedDialog()();
									}
									return null;
									}-*/;

	private native String shotcapture(Element video1) /*-{
		var canvas = $doc.createElement("canvas");
		canvas.width = Math
				.max(video1.videoWidth || 0,
						@org.geogebra.web.full.gui.dialog.image.WebCamInputPanel::MAX_CANVAS_WIDTH);
		canvas.height = video1.videoHeight ? Math.round(canvas.width
				* video1.videoHeight / video1.videoWidth)
				: 0.75 * @org.geogebra.web.full.gui.dialog.image.WebCamInputPanel::MAX_CANVAS_WIDTH;
		var ctx = canvas.getContext('2d');
		ctx.drawImage(video1, 0, 0);
		this.@org.geogebra.web.full.gui.dialog.image.WebCamInputPanel::canvasWidth = canvas.width;
		this.@org.geogebra.web.full.gui.dialog.image.WebCamInputPanel::canvasHeight = canvas.height;
		return canvas.toDataURL('image/png');
	}-*/;

	/**
	 * Stop recording
	 */
	public native void stopVideo() /*-{
		var stream = this.@org.geogebra.web.full.gui.dialog.image.WebCamInputPanel::stream;
		if (stream == null) {
			return;
		}
		if (stream.stop) {
			stream.stop();
		} else {
			stream.getVideoTracks()[0].stop();
		}
		stream = null;
	}-*/;

	/**
	 * @return screenshot as data URL (png)
	 */
	public String getImageDataURL() {
		if (video == null) {
			return null;
		}
		String capture = shotcapture(video);
		if (!app.isWhiteboardActive()) {
			stopVideo();
		}
		return capture;
	}

	/**
	 * Starts recording
	 */
	public void startVideo() {
		stopVideo();
		inputWidget.getElement().removeAllChildren();
		resetVideo();
    }

	private void resetVideo() {
		Localization loc = app.getLocalization();
		String message;
		if (app.getVersion() == Versions.WEB_FOR_DESKTOP) {
			message = "";
		} else if (Browser.isFirefox()) {
			message = loc.getMenu("Webcam.Firefox"); 
		} else if (Browser.isEdge()) {
			message = loc.getMenu("Webcam.Edge");
		} else {
			message = loc.getMenu("Webcam.Chrome");
		}
		video = populate(inputWidget.getElement(), message,
				loc.getMenu("Webcam.Problem"));
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
		return stream == null;
	}

	private void showInputDialog() {
		if (webcamDialog != null) {
			webcamDialog.center();
			webcamDialog.show();
		}
	}

	private void hidePermissionDialog() {
		if (!app.isWhiteboardActive()) {
			return;
		}
		if (permissionDialog != null) {
			permissionDialog.hide();
		}
	}

	private void showPermissionDialog(
			WebcamPermissionDialog.DialogType dialogType) {
		if (!app.isWhiteboardActive()) {
			return;
		}
		hidePermissionDialog();
		permissionDialog = new WebcamPermissionDialog(app, dialogType);
		permissionDialog.center();
		permissionDialog.show();
	}

	private void showRequestDialog() {
		showPermissionDialog(
				WebcamPermissionDialog.DialogType.PERMISSION_REQUEST);
	}

	private void showPermissionDeniedDialog() {
		showPermissionDialog(
				WebcamPermissionDialog.DialogType.PERMISSION_DENIED);
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

	private void resize() {
		if (!app.isWhiteboardActive()) {
			return;
		}
		webcamDialog.resize();
	}
}
