package org.geogebra.web.html5.webcam;

import org.geogebra.web.html5.Browser;

import com.google.gwt.canvas.client.Canvas;

import elemental2.dom.CanvasRenderingContext2D;
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
public class WebCamAPI {
	private static final int MAX_CANVAS_WIDTH = 640;
	private static final int MAX_CANVAS_HEIGHT = (int) Math.round(0.75 * MAX_CANVAS_WIDTH);
	
	private WebCamPanelInterface dialog;
	private MediaStream stream;
	private HTMLVideoElement videoElement;

	private boolean browserAlreadyAllowed;
	private boolean accessDenied;

	/**
	 *
	 * @param dialog the container where the picture of the camera appears.
	 */
	public WebCamAPI(WebCamPanelInterface dialog) {
		this.dialog = dialog;
	}

	/**
	 * Starts the camera input.
	 * @param videoElem holder for the stream.
	 */
	public void start(HTMLVideoElement videoElem) {
		videoElement = videoElem;
		populateMedia();
	}

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
			Js.<CanvasRenderingContext2D>uncheckedCast(c.getContext2d()).drawImage(video, 0, 0);
		}

		return c.toDataUrl("image/png");
	}

	private void onNotSupported() {
		dialog.onNotSupported();
	}

	private void onRequest() {
		dialog.onRequest();
	}

	private void onCameraSuccess(MediaStream mediaStream) {
		stream = mediaStream;
		setVideoSource(mediaStream, videoElement);
		dialog.onCameraSuccess();
	}

	private void onCameraError(String error) {
		dialog.onCameraError(error);
	}

	private void onLoadedMetadata(int width, int height) {
		dialog.onLoadedMetadata(width, height);
	}

	private void populateMedia() {
		if (!Browser.supportsWebcam()) {
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
			}).catch_((err) -> {
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

	private void stopVideo() {
		if (stream == null) {
			return;
		}
		stream.getVideoTracks().getAt(0).stop();
		stream = null;
	}

	private void setVideoSource(MediaStream mediaStream, HTMLVideoElement video) {
		video.srcObject = mediaStream;

		video.onloadedmetadata = (e) -> {
			onLoadedMetadata(video.videoWidth, video.videoHeight);
			return null;
		};
	}
}
