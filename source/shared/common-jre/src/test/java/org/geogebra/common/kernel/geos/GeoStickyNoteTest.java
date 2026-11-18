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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.io.XmlTestUtil;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.UndoRedoTester;
import org.geogebra.test.annotation.Issue;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Tests persistence of sticky-note font sizing modes. */
@Issue("MOW-1944")
class GeoStickyNoteTest extends BaseAppTestSetup {

	private static final String CONTENT = "[{\"text\":\"Hi\",\"size\":20}]";

	@BeforeEach
	void setup() {
		setupNotesApp();
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void fontSizeModeAndTextSurviveSaveLoad(boolean automatic) {
		GeoStickyNote note = createNote(automatic);
		String xml = getApp().getXML();
		assertTrue(xml.contains("<autoFontSize val=\"" + automatic + "\"/>"));
		assertFalse(xml.contains("<tag key=\"autoFontSize\""));
		XmlTestUtil.checkCurrentXML(getApp());

		getApp().setXML(xml, true);

		GeoStickyNote loaded = lookupNote();
		assertEquals(automatic, loaded.isAutoFontSize());
		assertEquals(note.getContent(), loaded.getContent());
		assertEquals(240, loaded.getContentWidth());
		assertEquals(240, loaded.getContentHeight());
	}

	@Test
	void undoRedoRestoresFontSizeMode() {
		UndoRedoTester undoRedo = new UndoRedoTester(getApp());
		undoRedo.setupUndoRedo();
		GeoStickyNote note = createNote(true);
		getApp().storeUndoInfo();
		note.setAutoFontSize(false);
		getApp().storeUndoInfo();

		GeoStickyNote undone = undoRedo.getAfterUndo("note");
		assertTrue(undone.isAutoFontSize());
		GeoStickyNote redone = undoRedo.getAfterRedo("note");
		assertFalse(redone.isAutoFontSize());
		assertEquals(CONTENT, redone.getContent());
	}

	private @NonNull GeoStickyNote createNote(boolean automatic) {
		GeoStickyNote note = new GeoStickyNote(getConstruction(), new GPoint2D(0, 0));
		note.setLabel("note");
		note.setContent(CONTENT);
		note.setAutoFontSize(automatic);
		note.setSize(240, 240);
		return note;
	}

	private @NonNull GeoStickyNote lookupNote() {
		return (GeoStickyNote) getApp().getKernel().lookupLabel("note");
	}
}
