/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.dialog.image;

import org.geogebra.gwtutil.JsObject;
import org.geogebra.web.html5.Browser;
import org.gwtproject.canvas.client.Canvas;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import elemental2.core.JsArray;
import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLMediaElement;
import elemental2.dom.HTMLVideoElement;
import elemental2.dom.MediaStream;
import elemental2.dom.MediaStreamConstraints;
import elemental2.dom.MediaStreamTrack;
import elemental2.dom.MediaTrackConstraints;
import jsinterop.base.Js;

/**
 * Class for camera support.
 * @author laszlo
 *
 */
public final class WebCamAPI {
	private WebCamInputPanel webCamInputPanel;
	private MediaStream stream;
	private HTMLVideoElement videoElement;

	private boolean browserAlreadyAllowed;
	private boolean accessDenied;
	private int requestVersion;

	/**
	 *
	 * @param webCamInputPanel the container where the picture of the camera appears.
	 */
	public WebCamAPI(WebCamInputPanel webCamInputPanel) {
		this.webCamInputPanel = webCamInputPanel;
	}

	/**
	 * Starts the camera input.
	 * @param videoElem holder for the stream.
	 */
	public void start(HTMLVideoElement videoElem) {
		stop();
		videoElement = videoElem;
		browserAlreadyAllowed = false;
		accessDenied = false;
		populateMedia(requestVersion);
	}

	/**
	 * Stops the video stream.
	 */
	public void stop() {
		requestVersion++;
		stopVideo();
		if (videoElement != null) {
			videoElement.srcObject = null;
			videoElement.onloadedmetadata = null;
			videoElement = null;
		}
	}

	/**
	 * @return true if the video stream is empty
	 */
	public boolean isStreamEmpty() {
		return stream == null || !stream.active;
	}

	/**
	 * Make a screenshot of the current stream
	 * @param video the video holder element.
	 * @return the screenshot data URL, or null if a frame or canvas is unavailable
	 */
	public @Nullable String takeScreenshot(@NonNull HTMLVideoElement video) {
		if (isStreamEmpty()
				|| video != videoElement
				|| video.videoWidth <= 0
				|| video.videoHeight <= 0
				|| video.readyState < HTMLMediaElement.HAVE_CURRENT_DATA) {
			return null;
		}
		Canvas c = createCanvas();
		if (c == null) {
			return null;
		}
		c.setPixelSize(video.videoWidth, video.videoHeight);
		c.setCoordinateSpaceHeight(video.videoHeight);
		c.setCoordinateSpaceWidth(video.videoWidth);
		drawFrame(c, video);

		return c.toDataUrl("image/png");
	}

	/** Creates the browser canvas used to encode a camera frame. */
	@Nullable Canvas createCanvas() {
		return Canvas.createIfSupported();
	}

	/** Draws a native camera frame onto its capture canvas. */
	void drawFrame(@NonNull Canvas canvas, @NonNull HTMLVideoElement video) {
		Js.<CanvasRenderingContext2D>uncheckedCast(canvas.getContext2d()).drawImage(video, 0, 0);
	}

	/** Schedules the delayed permission request prompt. */
	void schedulePermissionPrompt(@NonNull Runnable callback) {
		DomGlobal.setTimeout(ignored -> callback.run(), 400);
	}

	/** Reads the name of a browser permission failure. */
	@Nullable String getErrorName(@Nullable Object error) {
		if (error == null) {
			return null;
		}
		Object name = JsObject.of(error).get("name");
		return name instanceof String text ? text : null;
	}

	private void onNotSupported() {
		webCamInputPanel.onNotSupported();
	}

	private void onRequest() {
		webCamInputPanel.onRequest();
	}

	private void onCameraSuccess(MediaStream mediaStream) {
		stream = mediaStream;
		setVideoSource(mediaStream, videoElement);
		webCamInputPanel.onCameraSuccess();
	}

	private void onCameraError(@Nullable String error) {
		webCamInputPanel.onCameraError(error == null || error.isEmpty() ? "Error" : error);
	}

	private void onLoadedMetadata(int width, int height) {
		webCamInputPanel.onLoadedMetadata(width, height);
	}

	private void populateMedia(int version) {
		if (!Browser.supportsWebcam()) {
			onNotSupported();
			return;
		}

		MediaTrackConstraints trackConstraints = MediaTrackConstraints.create();
		trackConstraints.setFacingMode("environment");
		MediaStreamConstraints constraints = MediaStreamConstraints.create();
		constraints.setVideo(trackConstraints);

		DomGlobal.window
				.navigator
				.mediaDevices
				.getUserMedia(constraints)
				.then((mediaStream) -> {
					if (version != requestVersion) {
						stopTracks(mediaStream);
						return null;
					}
					browserAlreadyAllowed = true;
					onCameraSuccess(mediaStream);
					return null;
				})
				.catch_((err) -> {
					if (version != requestVersion) {
						return null;
					}
					accessDenied = true;
					onCameraError(getErrorName(err));
					return null;
				});

		schedulePermissionPrompt(() -> {
			if (version == requestVersion && !browserAlreadyAllowed && !accessDenied) {
				onRequest();
			}
		});
	}

	private void stopVideo() {
		if (stream == null) {
			return;
		}
		stopTracks(stream);
		stream = null;
	}

	private static void stopTracks(@NonNull MediaStream mediaStream) {
		JsArray<MediaStreamTrack> tracks = mediaStream.getTracks();
		for (int index = 0; index < tracks.length; index++) {
			MediaStreamTrack track = tracks.getAt(index);
			if (track != null) {
				track.stop();
			}
		}
	}

	private void setVideoSource(MediaStream mediaStream, HTMLVideoElement video) {
		video.srcObject = mediaStream;

		video.onloadedmetadata = (e) -> {
			if (stream == mediaStream && videoElement == video && !isStreamEmpty()) {
				onLoadedMetadata(video.videoWidth, video.videoHeight);
			}
			return null;
		};
	}
}
