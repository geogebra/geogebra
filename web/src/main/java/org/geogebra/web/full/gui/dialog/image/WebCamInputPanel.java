package org.geogebra.web.full.gui.dialog.image;

import org.geogebra.common.GeoGebraConstants.Versions;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.canvas.client.Canvas;
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
	private static final int MAX_CANVAS_HEIGHT = (int) Math.round(0.75 * MAX_CANVAS_WIDTH);
	private WebcamDialogInterface webcamDialog;
	private WebcamPermissionDialog permissionDialog;

	/**
	 * @param app
	 *            application
	 * @param webcamDialog
	 *            webcam dialog
	 */
	public WebCamInputPanel(AppW app, WebcamDialogInterface webcamDialog) {
		this.app = app;
		this.webcamDialog = webcamDialog;
		initGUI();
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
										try {
											video.srcObject = bs
										} catch (error) {
											video.src = $wnd.URL
												.createObjectURL(bs);
										}
										el.firstChild.style.display = "none";
										video.onloadedmetadata = function(e) {
											that.@org.geogebra.web.full.gui.dialog.image.WebCamInputPanel::onLoadedMetadata(II)(video.videoWidth,
												video.videoHeight);
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
	
	private String makeAShot(Element video) {
		Canvas c = Canvas.createIfSupported();
		int w = 0;
		int h = 0;
		try {
			w = Integer.parseInt(video.getAttribute("width"));
			h = Integer.parseInt(video.getAttribute("height"));
		} catch (NumberFormatException e) {
			// w, h = 0
		} finally {
			int width = Math.max(w, MAX_CANVAS_WIDTH);
			int height = h != 0 ? Math.round(width * h /w ) : MAX_CANVAS_HEIGHT;
			c.setPixelSize(width, height);
			c.setCoordinateSpaceHeight(height);
			c.setCoordinateSpaceWidth(width);
			drawVideoElement(c.getContext2d(), video);
			
		}
		return c.toDataUrl("image/png");
		
	}
	
	public native void drawVideoElement(JavaScriptObject ctx, Element img) /*-{
		ctx.drawImage(img, 0, 0);
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
		String capture = makeAShot(video);
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
		video = populate(inputWidget.getElement(), message, loc.getMenu("Webcam.Problem"));
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

	private void resize() {
		webcamDialog.resize();
	}
	
	private void onLoadedMetadata(int width, int height) {
		canvasWidth = width;
		canvasHeight = height;
		showInputDialog();
		resize();
	
	}
}
