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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.test.annotation.Issue;
import org.geogebra.web.test.ElementalMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.gwtproject.canvas.client.Canvas;
import org.jspecify.annotations.NonNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;
import org.mockito.ArgumentCaptor;

import com.google.gwtmockito.impl.StubGenerator;

import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLMediaElement;
import elemental2.dom.HTMLVideoElement;
import elemental2.dom.MediaDevices;
import elemental2.dom.MediaStream;
import elemental2.dom.MediaStreamConstraints;
import elemental2.dom.MediaStreamTrack;
import elemental2.dom.MediaTrackConstraints;
import elemental2.promise.IThenable;
import elemental2.promise.Promise;

/** Tests webcam lifecycle without requesting a physical camera. */
@RunWith(WebCamAPITest.CameraTestRunner.class)
public class WebCamAPITest {
	private WebCamInputPanel panel;
	private WebCamAPI camera;
	private final List<Runnable> permissionPrompts = new ArrayList<>();
	private IThenable.ThenOnFulfilledCallbackFn<MediaStream, Object> success;
	private Promise.CatchOnRejectedCallbackFn<Object> failure;

	/** Provides JVM substitutes for native constraint factories. */
	public static class CameraTestRunner extends GgbMockitoTestRunner {
		/**
		 * @param testClass test class
		 * @throws InitializationError if the test class is invalid
		 */
		public CameraTestRunner(@NonNull Class<?> testClass) throws InitializationError {
			super(testClass);
			StubGenerator.replaceMethodWithMock(
					MediaTrackConstraints.class, "create", MediaTrackConstraints.class);
			StubGenerator.replaceMethodWithMock(
					MediaStreamConstraints.class, "create", MediaStreamConstraints.class);
		}
	}

	/** Set up mocked browser globals and a real camera controller. */
	@Before
	public void setUp() throws ReflectiveOperationException {
		ElementalMocker.setupElemental();
		// Native browser constants have no values in the JVM DOM substitutes.
		HTMLMediaElement.class.getField("HAVE_CURRENT_DATA").setInt(null, 2);
		DomGlobal.window.navigator = DomGlobal.navigator;
		panel = mock(WebCamInputPanel.class);
		camera = spy(new WebCamAPI(panel));
		doAnswer(invocation -> {
					permissionPrompts.add(invocation.getArgumentAt(0, Runnable.class));
					return null;
				})
				.when(camera)
				.schedulePermissionPrompt(any(Runnable.class));
	}

	/** Unsupported browsers must report the failure without creating a stream. */
	@Test
	public void unsupportedBrowserShouldReportFailure() {
		camera.start(new HTMLVideoElement());

		verify(panel).onNotSupported();
		assertTrue(camera.isStreamEmpty());
	}

	/** Stopping before a request has produced a stream must be harmless. */
	@Test
	public void stoppingWithoutStreamShouldBeSafe() {
		camera.stop();
		camera.stop();

		assertTrue(camera.isStreamEmpty());
	}

	/** A granted stream must attach to the video and report intrinsic dimensions. */
	@Test
	public void successfulRequestShouldAttachStreamAndForwardMetadata() {
		prepareRequest();
		HTMLVideoElement video = new HTMLVideoElement();
		MediaStream stream = stream(mock(MediaStreamTrack.class));
		camera.start(video);

		success.onInvoke(stream);

		assertSame(stream, video.srcObject);
		assertFalse(camera.isStreamEmpty());
		verify(panel).onCameraSuccess();
		video.videoWidth = 1280;
		video.videoHeight = 720;
		video.onloadedmetadata.onInvoke(null);
		verify(panel).onLoadedMetadata(1280, 720);
	}

	/** Closing must release every track and detach the video. */
	@Test
	@Issue("APPS-7919")
	public void stoppingShouldReleaseAllTracksAndDetachVideo() {
		prepareRequest();
		MediaStreamTrack first = mock(MediaStreamTrack.class);
		MediaStreamTrack second = mock(MediaStreamTrack.class);
		HTMLVideoElement video = new HTMLVideoElement();
		camera.start(video);
		success.onInvoke(stream(first, second));

		camera.stop();
		camera.stop();

		verify(first).stop();
		verify(second).stop();
		assertTrue(camera.isStreamEmpty());
		assertNull(video.srcObject);
		assertNull(video.onloadedmetadata);
	}

	/** Permission granted after closing must immediately release the unused stream. */
	@Test
	@Issue("APPS-7919")
	public void permissionGrantedAfterStopShouldNotReactivateCamera() {
		prepareRequest();
		HTMLVideoElement video = new HTMLVideoElement();
		MediaStreamTrack track = mock(MediaStreamTrack.class);
		camera.start(video);
		camera.stop();

		success.onInvoke(stream(track));

		verify(track).stop();
		verify(panel, never()).onCameraSuccess();
		assertNull(video.srcObject);
		assertTrue(camera.isStreamEmpty());
	}

	/** An older pending request must not replace the stream of the latest request. */
	@Test
	@Issue("APPS-7919")
	public void restartingShouldIgnoreOlderPendingRequest() {
		prepareRequest();
		camera.start(new HTMLVideoElement());
		IThenable.ThenOnFulfilledCallbackFn<MediaStream, Object> oldSuccess = success;
		HTMLVideoElement currentVideo = new HTMLVideoElement();
		camera.start(currentVideo);
		MediaStream current = stream(mock(MediaStreamTrack.class));
		success.onInvoke(current);
		MediaStreamTrack stale = mock(MediaStreamTrack.class);

		oldSuccess.onInvoke(stream(stale));

		verify(stale).stop();
		assertSame(current, currentVideo.srcObject);
		verify(panel).onCameraSuccess();
	}

	/** Restarting must stop and detach an existing stream before requesting another. */
	@Test
	@Issue("APPS-7919")
	public void restartingShouldReleasePreviouslyActiveStream() {
		prepareRequest();
		HTMLVideoElement oldVideo = new HTMLVideoElement();
		MediaStreamTrack oldTrack = mock(MediaStreamTrack.class);
		camera.start(oldVideo);
		success.onInvoke(stream(oldTrack));

		camera.start(new HTMLVideoElement());

		verify(oldTrack).stop();
		assertNull(oldVideo.srcObject);
		assertTrue(camera.isStreamEmpty());
	}

	/** Even a stream with no tracks must be detached safely. */
	@Test
	@Issue("APPS-7919")
	public void stoppingTracklessStreamShouldDetachVideo() {
		prepareRequest();
		HTMLVideoElement video = new HTMLVideoElement();
		camera.start(video);
		success.onInvoke(stream());

		camera.stop();

		assertNull(video.srcObject);
		assertTrue(camera.isStreamEmpty());
	}

	/** A queued metadata event must not update a dialog after stopping the camera. */
	@Test
	@Issue("APPS-7919")
	public void metadataAfterStopShouldNotUpdateDialog() {
		prepareRequest();
		HTMLVideoElement video = new HTMLVideoElement();
		camera.start(video);
		success.onInvoke(stream(mock(MediaStreamTrack.class)));
		HTMLMediaElement.OnloadedmetadataFn metadata = video.onloadedmetadata;
		camera.stop();

		metadata.onInvoke(null);

		verify(panel, never()).onLoadedMetadata(anyInt(), anyInt());
	}

	/** A rejected old permission request must not show errors after closing. */
	@Test
	@Issue("APPS-7919")
	public void rejectedRequestAfterStopShouldNotReportError() {
		prepareRequest();
		camera.start(new HTMLVideoElement());
		camera.stop();

		failure.onInvoke(new Object());

		verify(panel, never()).onCameraError(any());
		assertTrue(camera.isStreamEmpty());
	}

	/** A still-pending browser request must show its delayed permission prompt. */
	@Test
	public void pendingPermissionShouldRequestPromptAfterDelay() {
		prepareRequest();
		camera.start(new HTMLVideoElement());
		assertEquals(1, permissionPrompts.size());

		permissionPrompts.get(0).run();

		verify(panel).onRequest();
	}

	/** Closing must invalidate the queued permission prompt. */
	@Test
	@Issue("APPS-7919")
	public void permissionPromptAfterStopShouldBeIgnored() {
		prepareRequest();
		camera.start(new HTMLVideoElement());
		camera.stop();

		permissionPrompts.get(0).run();

		verify(panel, never()).onRequest();
	}

	/** Once a stream is granted, the permission timer must not show a waiting prompt. */
	@Test
	public void grantedPermissionShouldSuppressWaitingPrompt() {
		prepareRequest();
		camera.start(new HTMLVideoElement());
		success.onInvoke(stream(mock(MediaStreamTrack.class)));

		permissionPrompts.get(0).run();

		verify(panel, never()).onRequest();
	}

	/** A failed request must report the browser error instead of showing a waiting prompt. */
	@Test
	public void rejectedPermissionShouldReportErrorAndSuppressWaitingPrompt() {
		prepareRequest();
		camera.start(new HTMLVideoElement());
		Object error = new Object();
		doReturn("NotAllowedError").when(camera).getErrorName(error);

		failure.onInvoke(error);
		permissionPrompts.get(0).run();

		verify(panel).onCameraError("NotAllowedError");
		verify(panel, never()).onRequest();
	}

	/** A new request must wait for permission independently of a previously granted request. */
	@Test
	public void restartingAfterSuccessShouldAllowNewPermissionPrompt() {
		prepareRequest();
		camera.start(new HTMLVideoElement());
		success.onInvoke(stream(mock(MediaStreamTrack.class)));
		camera.start(new HTMLVideoElement());

		permissionPrompts.get(0).run();
		permissionPrompts.get(1).run();

		verify(panel).onRequest();
	}

	/** A new request must not inherit a previous permission denial. */
	@Test
	public void restartingAfterFailureShouldAllowNewPermissionPrompt() {
		prepareRequest();
		camera.start(new HTMLVideoElement());
		Object error = new Object();
		doReturn("NotAllowedError").when(camera).getErrorName(error);
		failure.onInvoke(error);
		camera.start(new HTMLVideoElement());

		permissionPrompts.get(0).run();
		permissionPrompts.get(1).run();

		verify(panel).onRequest();
	}

	/** Capture must preserve the complete intrinsic frame, independent of HTML attributes. */
	@Test
	@Issue("APPS-7919")
	public void screenshotShouldPreserveIntrinsicVideoDimensions() {
		prepareRequest();
		for (int[] dimensions : new int[][] {{1280, 720}, {480, 640}, {320, 240}}) {
			HTMLVideoElement video = activeVideo(dimensions[0], dimensions[1]);
			Canvas canvas = mock(Canvas.class);
			doReturn(canvas).when(camera).createCanvas();
			doNothing().when(camera).drawFrame(canvas, video);
			when(canvas.toDataUrl("image/png")).thenReturn("captured-frame");

			assertEquals("captured-frame", camera.takeScreenshot(video));

			verify(canvas).setCoordinateSpaceWidth(dimensions[0]);
			verify(canvas).setCoordinateSpaceHeight(dimensions[1]);
			verify(camera).drawFrame(canvas, video);
		}
	}

	/** Capture must wait until both intrinsic dimensions are available. */
	@Test
	@Issue("APPS-7919")
	public void screenshotWithoutMetadataShouldNotCreateCanvas() {
		prepareRequest();
		for (int[] dimensions : new int[][] {{0, 0}, {640, 0}, {0, 480}}) {
			HTMLVideoElement video = activeVideo(dimensions[0], dimensions[1]);

			assertNull(camera.takeScreenshot(video));
		}

		verify(camera, never()).createCanvas();
	}

	/** Closing the camera must prevent capture from a video retaining old dimensions. */
	@Test
	@Issue("APPS-7919")
	public void screenshotAfterStopShouldNotReadOldFrame() {
		prepareRequest();
		HTMLVideoElement video = activeVideo(1280, 720);
		camera.stop();

		assertNull(camera.takeScreenshot(video));

		verify(camera, never()).createCanvas();
	}

	/** A video from another request must not be captured by the active camera session. */
	@Test
	@Issue("APPS-7919")
	public void screenshotOfDifferentVideoShouldBeRejected() {
		prepareRequest();
		activeVideo(1280, 720);
		HTMLVideoElement other = new HTMLVideoElement();
		other.videoWidth = 1280;
		other.videoHeight = 720;
		other.readyState = 2;

		assertNull(camera.takeScreenshot(other));

		verify(camera, never()).createCanvas();
	}

	/** Unsupported canvas creation must leave capture unsuccessful without throwing. */
	@Test
	@Issue("APPS-7919")
	public void unavailableCanvasShouldNotCrashCapture() {
		prepareRequest();
		HTMLVideoElement video = activeVideo(1280, 720);
		doReturn(null).when(camera).createCanvas();

		assertNull(camera.takeScreenshot(video));

		verify(camera, never()).drawFrame(any(), any());
	}

	/** Video metadata alone does not guarantee that a decoded frame is available. */
	@Test
	@Issue("APPS-7919")
	public void screenshotShouldWaitForDecodedFrame() {
		prepareRequest();
		for (int state : new int[] {0, 1}) {
			HTMLVideoElement video = activeVideo(1280, 720);
			video.readyState = state;

			assertNull(camera.takeScreenshot(video));
		}

		verify(camera, never()).createCanvas();
	}

	/** A device-disconnected stream must not expose a retained frame for capture. */
	@Test
	@Issue("APPS-7919")
	public void endedStreamShouldNotAllowCapture() {
		prepareRequest();
		HTMLVideoElement video = activeVideo(1280, 720);
		MediaStream stream = video.srcObject;
		stream.active = false;

		assertTrue(camera.isStreamEmpty());
		assertNull(camera.takeScreenshot(video));

		verify(camera, never()).createCanvas();
	}

	/** Nameless browser errors must report a general camera failure. */
	@Test
	@Issue("APPS-7919")
	public void namelessPermissionFailureShouldReportGeneralError() {
		prepareRequest();
		for (String name : new String[] {null, ""}) {
			reset(panel);
			camera.start(new HTMLVideoElement());
			Object error = new Object();
			doReturn(name).when(camera).getErrorName(error);

			failure.onInvoke(error);

			verify(panel).onCameraError("Error");
		}
	}

	/** A rejection without an error object must still report a general failure. */
	@Test
	@Issue("APPS-7919")
	public void nullPermissionFailureShouldReportGeneralError() {
		prepareRequest();
		camera.start(new HTMLVideoElement());

		failure.onInvoke(null);

		verify(panel).onCameraError("Error");
	}

	/** Camera requests must select a video track with the environment-facing preference. */
	@Test
	public void requestShouldUseEnvironmentFacingVideoConstraints() {
		MediaDevices devices = prepareRequest();

		camera.start(new HTMLVideoElement());

		ArgumentCaptor<MediaStreamConstraints> constraints =
				ArgumentCaptor.forClass(MediaStreamConstraints.class);
		verify(devices).getUserMedia(constraints.capture());
		ArgumentCaptor<MediaTrackConstraints> track =
				ArgumentCaptor.forClass(MediaTrackConstraints.class);
		verify(constraints.getValue()).setVideo(track.capture());
		verify(track.getValue()).setFacingMode("environment");
		org.mockito.Mockito.verifyNoMoreInteractions(constraints.getValue());
	}

	/** Metadata queued before a device disconnect must not reopen its camera dialog. */
	@Test
	@Issue("APPS-7919")
	public void metadataFromEndedStreamShouldNotUpdateDialog() {
		prepareRequest();
		HTMLVideoElement video = activeVideo(1280, 720);
		video.srcObject.active = false;

		video.onloadedmetadata.onInvoke(null);

		verify(panel, never()).onLoadedMetadata(anyInt(), anyInt());
	}

	private @NonNull HTMLVideoElement activeVideo(int width, int height) {
		HTMLVideoElement video = new HTMLVideoElement();
		camera.start(video);
		success.onInvoke(stream(mock(MediaStreamTrack.class)));
		video.videoWidth = width;
		video.videoHeight = height;
		video.readyState = 2;
		return video;
	}

	@SuppressWarnings("unchecked")
	private @NonNull MediaDevices prepareRequest() {
		MediaDevices devices = mock(MediaDevices.class);
		DomGlobal.navigator.mediaDevices = devices;
		Promise<MediaStream> request = mock(Promise.class);
		when(devices.getUserMedia(any(MediaStreamConstraints.class))).thenReturn(request);
		when(request.then(any(IThenable.ThenOnFulfilledCallbackFn.class))).thenAnswer(invocation -> {
			success = invocation.getArgumentAt(0, IThenable.ThenOnFulfilledCallbackFn.class);
			return request;
		});
		when(request.catch_(any())).thenAnswer(invocation -> {
			failure = invocation.getArgumentAt(0, Promise.CatchOnRejectedCallbackFn.class);
			return request;
		});
		return devices;
	}

	@SuppressWarnings("unchecked")
	private @NonNull MediaStream stream(@NonNull MediaStreamTrack... tracks) {
		MediaStream stream = mock(MediaStream.class);
		stream.active = tracks.length > 0;
		JsArray<MediaStreamTrack> array = mock(JsArray.class);
		array.length = tracks.length;
		for (int i = 0; i < tracks.length; i++) {
			when(array.getAt(i)).thenReturn(tracks[i]);
		}
		when(stream.getVideoTracks()).thenReturn(array);
		when(stream.getTracks()).thenReturn(array);
		return stream;
	}
}
