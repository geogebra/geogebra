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

package org.geogebra.common.properties.impl.objects;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.undo.UndoActionObserver;
import org.geogebra.common.main.undo.UndoActionType;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.scientific.LabelController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LabelStylePropertyTest {
	AppCommon app;
	GeoElementND element;
	LabelStyleProperty property;

	@BeforeEach
	public void setup() throws NotApplicablePropertyException {
		app = AppCommonFactory.create3D();
		element = app.getKernel().getAlgebraProcessor()
				.processAlgebraCommand("(1,2)", false)[0];
		property = new LabelStyleProperty(app.getLocalization(),
				app.getKernel(), element.toGeoElement());
	}

	@Test
	public void notApplicableForText() {
		GeoElement text = (GeoElement) app.getKernel().getAlgebraProcessor()
				.processAlgebraCommand("\"text\"", false)[0];
		text.setEuclidianVisible(true);
		assertThrows(NotApplicablePropertyException.class,
				() -> new LabelStyleProperty(app.getLocalization(), app.getKernel(),
						text));
	}

	@Test
	public void testGetValue() {
		assertEquals(List.of(true, false), property.getValue());
		element.setLabelVisible(false);
		element.setLabelMode(GeoElementND.LABEL_NAME_VALUE);
		assertEquals(List.of(false, false), property.getValue());
		element.setLabelVisible(true);
		assertEquals(List.of(true, true), property.getValue());
		element.setLabelMode(GeoElementND.LABEL_CAPTION_VALUE);
		assertEquals(List.of(true, true), property.getValue());
		element.setLabelMode(GeoElementND.LABEL_VALUE);
		assertEquals(List.of(false, true), property.getValue());
	}

	@Test
	public void testSetValue() {
		property.setValue(List.of(false, false));
		assertFalse(element.isLabelVisible(), "Label should be hidden");
		property.setValue(List.of(true, true));
		assertTrue(element.isLabelVisible(), "Label should be shown");
		assertEquals(GeoElementND.LABEL_NAME_VALUE, element.getLabelMode());
		property.setValue(List.of(false, true));
		assertEquals(GeoElementND.LABEL_VALUE, element.getLabelMode());
	}

	@Test
	public void testSetValueWithCaption() {
		element.setCaption("The Caption");
		property.setValue(List.of(true, true));
		assertTrue(element.isLabelVisible(), "Label should be shown");
		assertEquals(GeoElementND.LABEL_CAPTION_VALUE, element.getLabelMode());
		property.setValue(List.of(true, false));
		assertEquals(GeoElementND.LABEL_CAPTION, element.getLabelMode());
		property.setValue(List.of(false, true));
		assertEquals(GeoElementND.LABEL_VALUE, element.getLabelMode());
	}

	@Test
	public void testSetValueWithHiddenAlgebraLabel() {
		new LabelController().hideLabel(element);
		assertEquals(List.of(false, false), property.getValue());
		property.setValue(List.of(true, true));
		assertEquals("A", element.getLabelSimple());
		assertEquals(GeoElementND.LABEL_NAME_VALUE, element.getLabelMode());
	}

	@Test
	public void testSetValueWithHiddenAlgebraLabelAndUndo() {
		element.getApp().getKernel().setUndoActive(true);
		element.getApp().getKernel().initUndoInfo();
		new LabelController().hideLabel(element);
		assertEquals(List.of(false, false), property.getValue());
		property.addValueObserver(new UndoActionObserver(List.of(element.toGeoElement()),
				UndoActionType.STYLE));
		assertEquals(LabelManager.HIDDEN_PREFIX, element.getLabelSimple());

		property.setValue(List.of(true, true));
		assertEquals("A", element.getLabelSimple());
		assertTrue(element.isAlgebraLabelVisible());
		assertEquals(GeoElementND.LABEL_NAME_VALUE, element.getLabelMode());

		element.getApp().getKernel().getConstruction().getUndoManager().undo();
		assertFalse(element.isAlgebraLabelVisible());
		assertEquals(LabelManager.HIDDEN_PREFIX, element.getLabelSimple());
		assertEquals(GeoElementND.LABEL_NAME, element.getLabelMode());

		element.getApp().getKernel().getConstruction().getUndoManager().redo();
		assertTrue(element.isAlgebraLabelVisible());
		assertEquals(GeoElementND.LABEL_NAME_VALUE, element.getLabelMode());
	}

}
