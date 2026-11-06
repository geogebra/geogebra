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

package org.geogebra.common.kernel.geos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.geos.properties.VerticalAlignment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GeoStickyNoteTest extends BaseUnitTest {
	private GeoStickyNote stickyNote;

	@BeforeEach
	void setupTest() {
		stickyNote = new GeoStickyNote(getConstruction(), null);
	}

	@Test
	void stickyNoteCorrectDefaults() {
		assertEquals(GColor.STICKY_NOTE_YELLOW, stickyNote.getBackgroundColor());
		assertEquals(VerticalAlignment.MIDDLE, stickyNote.getVerticalAlignment());
		assertTrue(stickyNote.getContent().contains("\"align\":\"center\""));
		assertEquals(160, stickyNote.getWidth());
		assertEquals(160, stickyNote.getHeight());
	}
}
