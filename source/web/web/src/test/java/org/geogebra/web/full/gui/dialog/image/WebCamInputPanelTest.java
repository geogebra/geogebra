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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.geogebra.test.annotation.Issue;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.webcam.WebcamDialogInterface;
import org.geogebra.web.test.ElementalMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.jspecify.annotations.NonNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import elemental2.dom.HTMLVideoElement;

/** Tests camera metadata and browser error routing without requesting camera access. */
@RunWith(GgbMockitoTestRunner.class)
public class WebCamInputPanelTest {
	private AppW app;
	private WebcamDialogInterface dialog;
	private WebCamInputPanel panel;

	/** Set up the classic dialog error boundary and a real camera panel. */
	@Before
	public void setUp() {
		ElementalMocker.setupElemental();
		app = mock(AppW.class, Mockito.RETURNS_DEEP_STUBS);
		when(app.getVendorSettings().getMenuLocalizationKey(anyString()))
				.thenAnswer(invocation -> invocation.getArgumentAt(0, String.class));
		dialog = mock(WebcamDialogInterface.class);
		panel = new WebCamInputPanel(app, dialog);
	}

	/** No frame is available until the video element has been created. */
	@Test
	public void newPanelShouldHaveNoCapture() {
		assertNull(panel.getImageDataURL());
		assertTrue(panel.isStreamEmpty());
		assertEquals(640, panel.getVideoWidth());
		assertEquals(480, panel.getVideoHeight());
		panel.stopVideo();
	}

	/** Metadata dimensions must update the capture dialog. */
	@Test
	public void metadataShouldResizeDialogToVideoDimensions() {
		panel.onLoadedMetadata(1920, 1080);

		assertEquals(1920, panel.getVideoWidth());
		assertEquals(1080, panel.getVideoHeight());
		verify(dialog).showAndResize();
	}

	/** Successful permission must notify the owning dialog. */
	@Test
	public void successfulPermissionShouldNotifyDialog() {
		panel.onCameraSuccess();

		verify(dialog).onCameraSuccess();
	}

	/** Both permission-denied names must use the permission-specific message. */
	@Test
	public void permissionErrorsShouldUsePermissionMessage() {
		for (String error : new String[] {"PermissionDeniedError", "NotAllowedError"}) {
			reset(dialog);
			panel.onCameraError(error);
			verify(dialog).onCameraError("Webcam.Denied.Caption", "Webcam.Denied.Message");
			verifyNoMoreInteractions(dialog);
		}
	}

	/** Hardware and interrupted-stream errors must use the general camera message. */
	@Test
	public void deviceErrorsShouldUseCameraProblemMessage() {
		for (String error : new String[] {
			"TrackStartError",
			"NotFoundError",
			"DevicesNotFoundError",
			"NotReadableError",
			"SourceUnavailableError",
			"AbortError",
			"Error"
		}) {
			reset(dialog);
			panel.onCameraError(error);
			verify(dialog).onCameraError("Webcam.Problem", "Webcam.Problem.Message");
			verifyNoMoreInteractions(dialog);
		}
	}

	/** A pending permission request must report the request prompt. */
	@Test
	public void pendingPermissionShouldShowRequestMessage() {
		panel.onRequest();

		verify(dialog).onCameraError("Webcam.Request", "Webcam.Request.Message");
	}

	/** Unsupported browsers must use the unsupported-camera message. */
	@Test
	public void unsupportedCameraShouldUseNotSupportedMessage() {
		panel.onNotSupported();

		verify(dialog).onCameraError("Webcam.Notsupported.Caption", "Webcam.Notsupported.Message");
	}
	/** Closing the panel must hide a pending permission prompt and release its reference. */
	@Test
	@Issue("APPS-7919")
	@SuppressWarnings("PMD.AvoidAccessibilityAlteration")
	public void stoppingShouldHidePendingPermissionPromptOnlyOnce()
			throws ReflectiveOperationException {
		WebcamPermissionDialog permission = mock(WebcamPermissionDialog.class);
		Field field = WebCamInputPanel.class.getDeclaredField("permissionDialog");
		field.setAccessible(true);
		field.set(panel, permission);

		panel.stopVideo();
		panel.stopVideo();

		verify(permission).hide();
		assertNull(field.get(panel));
	}

	/** Missing frames must keep the classic stream alive until capture succeeds. */
	@Test
	@Issue("APPS-7919")
	public void missingFrameShouldKeepStreamAliveForRetry() throws ReflectiveOperationException {
		WebCamAPI camera = replaceCamera();
		when(camera.takeScreenshot(Mockito.any())).thenReturn(null, "decoded-frame");

		assertNull(panel.getImageDataURL());

		verify(camera, never()).stop();
		assertEquals("decoded-frame", panel.getImageDataURL());
		verify(camera).stop();
	}

	/** Notes allows multiple captures from the same active stream. */
	@Test
	public void notesCaptureShouldKeepStreamAlive() throws ReflectiveOperationException {
		when(app.isWhiteboardActive()).thenReturn(true);
		WebCamAPI camera = replaceCamera();
		when(camera.takeScreenshot(Mockito.any())).thenReturn("decoded-frame");

		assertEquals("decoded-frame", panel.getImageDataURL());
		assertEquals("decoded-frame", panel.getImageDataURL());

		verify(camera, never()).stop();
	}

	@SuppressWarnings("PMD.AvoidAccessibilityAlteration")
	private @NonNull WebCamAPI replaceCamera() throws ReflectiveOperationException {
		WebCamAPI camera = mock(WebCamAPI.class);
		Field field = WebCamInputPanel.class.getDeclaredField("webCam");
		field.setAccessible(true);
		field.set(panel, camera);
		Field video = WebCamInputPanel.class.getDeclaredField("video");
		video.setAccessible(true);
		video.set(panel, new HTMLVideoElement());
		return camera;
	}
}
