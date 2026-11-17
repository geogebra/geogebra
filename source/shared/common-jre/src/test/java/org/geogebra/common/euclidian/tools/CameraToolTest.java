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

package org.geogebra.common.euclidian.tools;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CAMERA;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SELECT_MOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Set;

import org.geogebra.common.gui.GuiManager;
import org.geogebra.common.jre.headless.DialogManagerNoGui;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

/** Tests camera tool activation at the shared controller/platform boundary. */
class CameraToolTest extends BaseToolTest {

	@Override
	protected void setupClassicApp() {
		setupNotesApp();
	}

	@Test
	void toolbarShouldOpenCameraImmediatelyWithoutCreatingConstructionElements() {
		GuiManager gui = Mockito.mock(GuiManager.class, Mockito.RETURNS_DEEP_STUBS);
		getApp().setGuiManager(gui);
		GeoPoint point = add("A = (1, -2)");
		Set<GeoElement> before = getConstructionSnapshot();
		try (MockedConstruction<DialogManagerNoGui> dialogs =
				Mockito.mockConstruction(DialogManagerNoGui.class)) {
			getApp().initDialogManager(false);

			Mockito.verify(gui, Mockito.never()).loadWebcam();
			getApp().getActiveEuclidianView().setMode(MODE_CAMERA, ModeSetter.TOOLBAR);

			Mockito.verify(gui).loadWebcam();
			Mockito.verifyNoInteractions(dialogs.constructed().get(0));
			assertEquals(MODE_CAMERA, getApp().getMode());
			assertEquals(before, getConstructionSnapshot());
			moveMouseRW(1, -2);
			clickRW(1, -2);
			clickRW(3, -3);
			Mockito.verify(gui).loadWebcam();
			Mockito.verifyNoMoreInteractions(dialogs.constructed().get(0));
			assertEquals(before, getConstructionSnapshot());
			assertEquals(1, point.getInhomX(), 1E-8);
			assertEquals(-2, point.getInhomY(), 1E-8);
		}
	}

	@ParameterizedTest
	@EnumSource(value = ModeSetter.class, names = "TOOLBAR", mode = EnumSource.Mode.EXCLUDE)
	void programmaticModeChangesShouldNotRequestCamera(@NonNull ModeSetter setter) {
		GuiManager gui = Mockito.mock(GuiManager.class, Mockito.RETURNS_DEEP_STUBS);
		getApp().setGuiManager(gui);
		Set<GeoElement> before = getConstructionSnapshot();

		getApp().getActiveEuclidianView().setMode(MODE_CAMERA, setter);

		Mockito.verify(gui, Mockito.never()).loadWebcam();
		assertEquals(MODE_CAMERA, getApp().getMode());
		assertEquals(before, getConstructionSnapshot());
	}

	@Test
	void toolbarWithoutGuiShouldNotRequestCameraOrCreateObjects() {
		assertNull(getApp().getGuiManager());
		Set<GeoElement> before = getConstructionSnapshot();

		getApp().getActiveEuclidianView().setMode(MODE_CAMERA, ModeSetter.TOOLBAR);
		clickRW(1, -2);

		assertEquals(MODE_CAMERA, getApp().getMode());
		assertEquals(before, getConstructionSnapshot());
	}

	@Test
	void reselectingActiveCameraShouldNotStartAnotherRequest() {
		GuiManager gui = Mockito.mock(GuiManager.class, Mockito.RETURNS_DEEP_STUBS);
		getApp().setGuiManager(gui);

		getApp().getActiveEuclidianView().setMode(MODE_CAMERA, ModeSetter.TOOLBAR);
		getApp().getActiveEuclidianView().setMode(MODE_CAMERA, ModeSetter.TOOLBAR);

		Mockito.verify(gui).loadWebcam();
	}

	@Test
	void cameraShouldReopenAfterReturningToSelectionMode() {
		GuiManager gui = Mockito.mock(GuiManager.class, Mockito.RETURNS_DEEP_STUBS);
		getApp().setGuiManager(gui);
		Set<GeoElement> before = getConstructionSnapshot();

		getApp().getActiveEuclidianView().setMode(MODE_CAMERA, ModeSetter.TOOLBAR);
		getApp().getActiveEuclidianView().setMode(MODE_SELECT_MOW, ModeSetter.DOCK_PANEL);
		getApp().getActiveEuclidianView().setMode(MODE_CAMERA, ModeSetter.TOOLBAR);

		Mockito.verify(gui, Mockito.times(2)).loadWebcam();
		assertEquals(before, getConstructionSnapshot());
	}
}
