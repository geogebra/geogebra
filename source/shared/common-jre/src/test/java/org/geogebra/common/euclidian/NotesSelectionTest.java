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

package org.geogebra.common.euclidian;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.settings.config.AppConfigNotes;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class NotesSelectionTest extends BaseEuclidianControllerTest {

	@Test
	public void selectionRectangleShouldSelectPartOfStroke() {
		setMode(EuclidianConstants.MODE_PEN);
		dragStart(100, 100);
		dragEnd(200, 100);
		setMode(EuclidianConstants.MODE_SELECT_MOW);
		dragStart(150, 50);
		dragEnd(250, 150);
		assertSelected(lookup("stroke1"));
	}

	@Test
	public void selectionRectangleShouldSelectPartOfMoreStrokes() {
		setMode(EuclidianConstants.MODE_PEN);
		dragStart(100, 100);
		dragEnd(200, 100);
		setMode(EuclidianConstants.MODE_SELECT_MOW);
		setMode(EuclidianConstants.MODE_PEN);
		dragStart(220, 100);
		dragEnd(300, 100);
		setMode(EuclidianConstants.MODE_SELECT_MOW);
		dragStart(150, 50);
		dragEnd(250, 150);
		assertSelected(lookup("stroke1"), lookup("stroke2"));
	}

	@Test
	@Issue("MOW-1744")
	public void tabToNextGeoShouldHideBoundingBox() {
		getApp().setConfig(new AppConfigNotes());
		setMode(EuclidianConstants.MODE_SHAPE_RECTANGLE);
		dragStart(50, 50);
		dragEnd(150, 150);
		assertNotNull(getApp().getActiveEuclidianView().getBoundingBox());
		getApp().getSelectionManager().selectNextGeo();
		assertNull(getApp().getActiveEuclidianView().getBoundingBox());
	}

	private void assertSelected(GeoElement... geos) {
		assertArrayEquals(getApp().getSelectionManager().getSelectedGeos().toArray(),
				geos);
	}
}