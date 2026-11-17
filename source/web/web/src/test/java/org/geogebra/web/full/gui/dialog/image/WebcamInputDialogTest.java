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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.test.annotation.Issue;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.geogebra.web.test.ElementalMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

/** Tests capture preconditions and every dialog closing entry point. */
@RunWith(GgbMockitoTestRunner.class)
public class WebcamInputDialogTest {
	private static final String FRAME = "data:image/png;base64,c25hcHNob3Q=";
	private AppW app;
	private WebCamInputPanel panel;
	private WebcamInputDialog dialog;

	/**
	 * Replace only the camera panel, keeping the real dialog actions.
	 * @throws ReflectiveOperationException if the camera panel cannot be replaced
	 */
	@Before
	@SuppressWarnings("PMD.AvoidAccessibilityAlteration")
	public void setUp() throws ReflectiveOperationException {
		ElementalMocker.setupElemental();
		app = mock(AppW.class, Mockito.RETURNS_DEEP_STUBS);
		dialog = new WebcamInputDialog(app, new DialogData("Camera", "Close", "takepicture"));
		panel = mock(WebCamInputPanel.class);
		Field field = WebcamInputDialog.class.getDeclaredField("webcamInputPanel");
		field.setAccessible(true);
		field.set(dialog, panel);
	}

	/** A ready frame must be passed to the normal image insertion pipeline. */
	@Test
	public void captureShouldInsertFrameWithWebcamName() {
		when(panel.getImageDataURL()).thenReturn(FRAME);
		when(panel.isStreamEmpty()).thenReturn(false);

		dialog.onPositiveAction();

		verify(app).imageDropHappened("webcam", FRAME);
	}

	/** A missing frame must not create a construction element. */
	@Test
	public void captureWithoutFrameShouldNotInsertImage() {
		when(panel.isStreamEmpty()).thenReturn(false);

		dialog.onPositiveAction();

		verify(app, never()).imageDropHappened(any(), any());
	}

	/** A stopped camera must not attempt to read from the capture canvas. */
	@Test
	@Issue("APPS-7919")
	public void captureWithoutStreamShouldNotReadFrame() {
		when(panel.isStreamEmpty()).thenReturn(true);

		dialog.onPositiveAction();

		verify(panel, never()).getImageDataURL();
		verify(app, never()).imageDropHappened(any(), any());
	}

	/** Closing through the main action must release the camera exactly once. */
	@Test
	@Issue("APPS-7919")
	public void normalHideShouldReleaseCamera() {
		dialog.hide();

		verifyCleanup();
	}

	/** Auto-close must release the camera too. */
	@Test
	@Issue("APPS-7919")
	public void autoHideShouldReleaseCamera() {
		dialog.hide(true);

		verifyCleanup();
	}

	/** Closing while suppressing focus restoration must still release the camera. */
	@Test
	@Issue("APPS-7919")
	public void hideWithoutFocusShouldReleaseCamera() {
		dialog.hide(false, false);

		verifyCleanup();
	}

	private void verifyCleanup() {
		verify(panel).stopVideo();
		verify(app.getImageManager()).setPreventAuxImage(false);
		verify(app.getGuiManager()).setMode(EuclidianConstants.MODE_SELECT_MOW, ModeSetter.TOOLBAR);
	}
}
