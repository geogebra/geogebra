package org.geogebra.web.html5.webcam;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.JavaScriptObject;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLVideoElement;
import elemental2.dom.MediaStream;
import elemental2.dom.MediaStreamConstraints;
import elemental2.dom.MediaTrackConstraints;
import jsinterop.base.Js;

/**
 * Class for camera support.
 * @author laszlo
 *
 */
public class WebCamAPI implements WebCamInterface {
	private static final int MAX_CANVAS_WIDTH = 640;
	private static final int MAX_CANVAS_HEIGHT = (int) Math.round(0.75 * MAX_CANVAS_WIDTH);
	
	private WebCamInterface dialog;
	private MediaStream stream;
	private HTMLVideoElement videoElement;

	private boolean browserAlreadyAllowed;
	private boolean accessDenied;

	/**
	 *
	 * @param dialog the container where the picture of the camera appears.
	 */
	public WebCamAPI(WebCamInterface dialog) {
		this.dialog = dialog;
	}

	/**
	 * Starts the camera input.
	 * @param videoElem holder for the stream.
	 */
	public void start(HTMLVideoElement videoElem) {
		videoElement = videoElem;
		createPolyfillIfNeeded();
		populateMedia();
	}

	/**
	 *
	 * @return true if web camera is supported.
	 */
	public native static boolean isSupported() /*-{
		return $wnd.navigator.mediaDevices != undefined
				|| (navigator.webkitGetUserMedia || navigator.mozGetUserMedia);
	}-*/;

	/**
	 * Stops the video stream.
	 */
	public void stop() {
		stopVideo();
	}

	/**
	 * @return true if the video stream is empty
	 */
	public boolean isStreamEmpty() {
		return stream == null;
	}

	/**
	 * Make a screenshot of the current stream
	 * @param video the video holder element.
	 * @return the screenshot data URL.
	 */
	public String takeScreenshot(HTMLVideoElement video) {
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
			int height = h != 0 ? (int) Math.round(width * h / ((double) w))
					: MAX_CANVAS_HEIGHT;
			c.setPixelSize(width, height);
			c.setCoordinateSpaceHeight(height);
			c.setCoordinateSpaceWidth(width);
			drawVideoElement(c.getContext2d(), video);

		}
		return c.toDataUrl("image/png");

	}

	@Override
	public void onRequest() {
		dialog.onRequest();
	}

	@Override
	public void onCameraSuccess(MediaStream mediaStream) {
		stream = mediaStream;
		setVideoSource(mediaStream, videoElement);
		dialog.onCameraSuccess(mediaStream);
	}

	@Override
	public void onCameraError(String error) {
		dialog.onCameraError(error);
	}

	@Override
	public void onLoadedMetadata(int width, int height) {
		dialog.onLoadedMetadata(width, height);
	}

	private native void drawVideoElement(JavaScriptObject ctx, HTMLVideoElement img) /*-{
		ctx.drawImage(img, 0, 0);
	}-*/;

	private native void createPolyfillIfNeeded() /*-{
		var mediaDevices = $wnd.navigator.mediaDevices;
		if (mediaDevices === undefined) {
			mediaDevices = {};
		}

		if (mediaDevices.getUserMedia === undefined) {
			mediaDevices.getUserMedia = function(constraints) {
				var getUserMedia = navigator.webkitGetUserMedia
						|| navigator.mozGetUserMedia;
				if (!getUserMedia) {
					return Promise.reject(new Error(
							'getUserMedia is not implemented in this browser'));
				}
				return new Promise(function(resolve, reject) {
					getUserMedia.call(navigator, constraints, resolve, reject);
				});
			}
		}
	}-*/;

	private void populateMedia() {
		if (DomGlobal.window.navigator.mediaDevices == null) {
			onNotSupported();
			return;
		}

		MediaTrackConstraints trackConstraints = MediaTrackConstraints.create();
		trackConstraints.setFacingMode("environment");
		MediaStreamConstraints constraints = MediaStreamConstraints.create();
		constraints.setVideo(trackConstraints);

		DomGlobal.window.navigator.mediaDevices.getUserMedia(constraints)
			.then((mediaStream) -> {
				browserAlreadyAllowed = true;
				onCameraSuccess(mediaStream);
				return null;
			}).catch_(( err) -> {
				accessDenied = true;
				onCameraError((String) Js.asPropertyMap(err).get("name"));
				return null;
			});

		DomGlobal.setTimeout((x) -> {
			if (!browserAlreadyAllowed && !accessDenied) {
				onRequest();
			}
		}, 400);
	}

	private native void stopVideo() /*-{
		var stream = this.@org.geogebra.web.html5.webcam.WebCamAPI::stream;
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

	private native void setVideoSource(MediaStream mediaStream, HTMLVideoElement el) /*-{
		$wnd.URL = $wnd.URL || $wnd.webkitURL || $wnd.msURL || $wnd.mozURL
				|| $wnd.oURL || null;
		var video = el.firstChild;
		if ($wnd.URL && $wnd.URL.createObjectURL) {
			try {
				video.srcObject = mediaStream
			} catch (error) {
				video.src = $wnd.URL.createObjectURL(mediaStream);
			}
			errorElem.style.display = "none";
			var that = this;
			video.onloadedmetadata = function(e) {
				that
						.@org.geogebra.web.html5.webcam.WebCamAPI::onLoadedMetadata(
								II)(video.videoWidth, video.videoHeight);
			};
		} else {
			video.src = bs;
			errorElem.style.display = "none";
		}
	}-*/;

	@Override
	public void onNotSupported() {
		dialog.onNotSupported();
	}
}
