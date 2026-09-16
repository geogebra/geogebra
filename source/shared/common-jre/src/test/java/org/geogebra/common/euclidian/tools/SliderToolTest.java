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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.jre.headless.DialogManagerNoGui;
import org.geogebra.test.TestEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

class SliderToolTest extends BaseToolTest {
	@BeforeEach
	void setMode() {
		setMode(EuclidianConstants.MODE_SLIDER);
	}

	@Test
	void sliderTool() {
		try (MockedConstruction<DialogManagerNoGui> dialogs =
				Mockito.mockConstruction(DialogManagerNoGui.class)) {
			getApp().initDialogManager(false);
			click(100, 100);

			assertEquals(1, dialogs.constructed().size());
			Mockito.verify(dialogs.constructed().get(0)).showSliderCreationDialog(100, 100);
		}
	}

	@Test
	void sliderToolPreviewDoesNotShowDialog() {
		try (MockedConstruction<DialogManagerNoGui> dialogs =
				Mockito.mockConstruction(DialogManagerNoGui.class)) {
			getApp().initDialogManager(false);
			ec.wrapMouseMoved(new TestEvent(100, 100));

			assertEquals(1, dialogs.constructed().size());
			Mockito.verify(dialogs.constructed().get(0), Mockito.never())
					.showSliderCreationDialog(Mockito.anyInt(), Mockito.anyInt());
		}
	}

	@Test
	void sliderToolWithoutMouseLocationDoesNotShowDialog() {
		try (MockedConstruction<DialogManagerNoGui> dialogs =
				Mockito.mockConstruction(DialogManagerNoGui.class)) {
			getApp().initDialogManager(false);
			ec.processMode(new Hits(), false, false);

			assertEquals(1, dialogs.constructed().size());
			Mockito.verify(dialogs.constructed().get(0), Mockito.never())
					.showSliderCreationDialog(Mockito.anyInt(), Mockito.anyInt());
		}
	}

	@Test
	void sliderToolWithoutDialogManagerDoesNotCreateObject() {
		assertNull(getApp().getDialogManager());
		click(100, 100);

		checkContent();
	}
}
