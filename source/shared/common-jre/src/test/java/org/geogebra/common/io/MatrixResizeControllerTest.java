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

package org.geogebra.common.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.editor.share.controller.MatrixResizeController;
import org.geogebra.editor.share.editor.EditorFeatures;
import org.geogebra.editor.share.serializer.GeoGebraSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class MatrixResizeControllerTest {
	private EditorChecker checker;
	private MathFieldCommon mathField;
	private MatrixResizeController matrixResizeController;
	private final AppCommon app = AppCommonFactory.create();

	@BeforeAll
	public static void prepare() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderCommon());
		}
	}

	@BeforeEach
	public void setUp() {
		checker = new EditorChecker(app);
		mathField = checker.getMathField();
		matrixResizeController = mathField.getInternal().getMatrixResizeController();
	}

	@Test
	public void testState() {
		checker.insert("{{1,2,3}, {4,5,6}}").left(1);
		MatrixResizeController.State state = matrixResizeController.getState();

		MatrixResizeController.PopupState popupState = state.popupState();
		assertNotNull(popupState);
		MatrixResizeController.ControlState controlState = popupState.controlState();
		assertEquals("2", controlState.rows());
		assertEquals("3", controlState.columns());
		assertTrue(controlState.isRemoveRowEnabled());
		assertTrue(controlState.isAddRowEnabled());
		assertTrue(controlState.isRemoveColumnEnabled());
		assertTrue(controlState.isAddColumnEnabled());

		checker.insert("{{1,2,3}, {4,5,6}}").right(1);
		assertNull(matrixResizeController.getState().popupState());
	}

	@Test
	public void testMatrixDimensionControls() {
		checker.insert("{{1,2,3}, {4,5,6}}").left(1);
		assertDimensions(2, 3);

		matrixResizeController.addRow();
		assertDimensions(3, 3);
		assertEditorContents("{{1,2,3},{4,5,6},{,,}}");

		matrixResizeController.addColumn();
		assertEditorContents("{{1,2,3,},{4,5,6,},{,,,}}");
		assertDimensions(3, 4);

		matrixResizeController.removeRow();
		assertEditorContents("{{1,2,3,},{4,5,6,}}");
		assertDimensions(2, 4);

		matrixResizeController.removeColumn();
		assertEditorContents("{{1,2,3},{4,5,6}}");
		assertDimensions(2, 3);

		matrixResizeController.removeColumn();
		assertEditorContents("{{1,2},{4,5}}");
		assertDimensions(2, 2);

		matrixResizeController.removeRow();
		assertEditorContents("{{1,2}}");
		assertDimensions(1, 2);
	}
	
	@Test
	public void testVectorDimensionControls() {
		checker.insert("$vector(1,2)").left(1);
		assertDimensions(2, 1);

		MatrixResizeController.State state = matrixResizeController.getState();
		assertTrue(state.containsMatrix());
		assertNotNull(state.popupState());
		assertFalse(state.popupState().controlState().isAddColumnEnabled());
		assertFalse(state.popupState().controlState().isRemoveColumnEnabled());
		assertTrue(state.popupState().controlState().isAddRowEnabled());
		assertFalse(state.popupState().controlState().isRemoveRowEnabled());

		matrixResizeController.addRow();
		assertEditorContents("$vector(1,2,)");
		state = matrixResizeController.getState();
		assertFalse(state.popupState().controlState().isAddRowEnabled());
		assertTrue(state.popupState().controlState().isRemoveRowEnabled());
		matrixResizeController.removeRow();
		assertThrows(IllegalStateException.class, matrixResizeController::removeRow);
		assertEditorContents("$vector(1,2)");
	}

	private void assertEditorContents(String ascii) {
		assertEquals(ascii, GeoGebraSerializer
				.serialize(mathField.getInternal().getEditorState().getRootNode(),
						(EditorFeatures) null));
	}

	private void assertDimensions(int rows, int columns) {
		assertNotNull(matrixResizeController.getState().popupState());
		assertEquals(String.valueOf(rows),
				matrixResizeController.getState().popupState().controlState().rows());
		assertEquals(String.valueOf(columns),
				matrixResizeController.getState().popupState().controlState().columns());
	}
}
