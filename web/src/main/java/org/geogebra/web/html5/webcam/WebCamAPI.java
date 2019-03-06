package org.geogebra.web.html5.webcam;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

/**
 * Class for camera support.
 * @author laszlo
 *
 */
public class WebCamAPI implements WebCamInterface {
	private static final int MAX_CANVAS_WIDTH = 640;
	private static final int MAX_CANVAS_HEIGHT = (int) Math.round(0.75 * MAX_CANVAS_WIDTH);

	private WebCamInterface dialog;
	private JavaScriptObject stream;
	private Element videoElement;
	private Element errorElement;

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
	 * @param errorElem holder the errors, if any.
	 */
	public void start(Element videoElem, Element errorElem) {
		videoElement = videoElem;
		errorElement = errorElem;
		checkLegacyAPI();
		populateMedia(videoElem, errorElem);
	}

	/**
	 *
	 * @return true if web camera is supported.
	 */
	public native static boolean isSupported() /*-{
		return $wnd.navigator.mediaDevices != undefined ||
		   	(navigator.webkitGetUserMedia || navigator.mozGetUserMedia);
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
	public String takeScreenshot(Element video) {
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
	public void onCameraSuccess(JavaScriptObject mediaStream) {
		stream = mediaStream;
		setVideoSource(mediaStream, videoElement, errorElement);
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

	private native void drawVideoElement(JavaScriptObject ctx, Element img) /*-{
		ctx.drawImage(img, 0, 0);
	}-*/;

	private native void checkLegacyAPI() /*-{
		if ($wnd.navigator.mediaDevices === undefined) {
  			$wnd.navigator.mediaDevices = {};
  		}

		if ($wnd.navigator.mediaDevices.getUserMedia === undefined) {
  			$wnd.navigator.mediaDevices.getUserMedia = function(constraints) {
    			var getUserMedia = navigator.webkitGetUserMedia || navigator.mozGetUserMedia;
    			if (!getUserMedia) {
      				return Promise.reject(new Error('getUserMedia is not implemented in this browser'));
    			}
    			return new Promise(function(resolve, reject) {
      			getUserMedia.call(navigator, constraints, resolve, reject);
    		});
  			}
		}
	}-*/;

	private native void populateMedia(Element elem, Element errorElem) /*-{
	var constraints = { video: {facingMode: 'environment'} };
	var that = this;
	var browserAlreadyAllowed = false;
	var accessDenied = false;

	$wnd.navigator.mediaDevices.getUserMedia(constraints)
		.then(function(mediaStream) {
			browserAlreadyAllowed = true;
			that.@org.geogebra.web.html5.webcam.WebCamAPI::
			onCameraSuccess(Lcom/google/gwt/core/client/JavaScriptObject;)(mediaStream);
		}) // .catch workaround https://github.com/gwtproject/gwt/issues/9490
		['catch'](function(err) {
			accessDenied = true;
			that.@org.geogebra.web.html5.webcam.WebCamAPI::
			onCameraError(Ljava/lang/String;)(err.name);
		});

		function accessRequest() {
				if (!browserAlreadyAllowed && !accessDenied) {
					that.@org.geogebra.web.html5.webcam.WebCamAPI::onRequest()();
				}
		}
		setTimeout(accessRequest, 400);
	}-*/;

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

	private native void setVideoSource(JavaScriptObject mediaStream, Element el, Element errorElem) /*-{
		$wnd.URL = $wnd.URL || $wnd.webkitURL || $wnd.msURL || $wnd.mozURL
				|| $wnd.oURL || null;
		var video = el.firstChild;
		if ($wnd.URL && $wnd.URL.createObjectURL) {
			try {
				video.srcObject = mediaStream
			} catch (error) {
				video.src = $wnd.URL
					.createObjectURL(mediaStream);
			}
			errorElem.style.display = "none";
			var that = this;
			video.onloadedmetadata = function(e) {
				that.@org.geogebra.web.html5.webcam.WebCamAPI::onLoadedMetadata(II)(video.videoWidth,
					video.videoHeight);
			};
		} else {
			video.src = bs;
			errorElem.style.display = "none";
		}
	}-*/;
}
