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

import java.util.List;

import org.geogebra.common.gui.dialog.ButtonDialogModel;
import org.geogebra.common.jre.headless.DialogManagerNoGui;
import org.jspecify.annotations.NonNull;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

/** Shared dialog-boundary helpers for widget creation and inspector tools. */
abstract class BaseDialogToolTest extends BaseToolTest {

	protected void enableUndo() {
		getApp().setUndoActive(true);
		getApp().storeUndoInfo();
	}

	protected @NonNull MockedConstruction<DialogManagerNoGui> mockDialogs() {
		MockedConstruction<DialogManagerNoGui> dialogs =
				Mockito.mockConstruction(DialogManagerNoGui.class);
		getApp().initDialogManager(false);
		return dialogs;
	}

	protected @NonNull DialogManagerNoGui dialog(
			@NonNull MockedConstruction<DialogManagerNoGui> dialogs) {
		assertEquals(1, dialogs.constructed().size());
		return dialogs.constructed().get(0);
	}

	protected void verifyNoWidgetCreationDialog(
			@NonNull MockedConstruction<DialogManagerNoGui> dialogs) {
		Mockito.verify(dialog(dialogs), Mockito.never())
				.showButtonCreationDialog(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyBoolean());
	}

	protected void captureModels(
			@NonNull DialogManagerNoGui dialog, @NonNull List<ButtonDialogModel> models) {
		Mockito.when(dialog.showButtonCreationDialog(
						Mockito.anyInt(), Mockito.anyInt(), Mockito.anyBoolean()))
				.thenAnswer(invocation -> {
					models.add(new ButtonDialogModel(
							getApp(),
							invocation.getArgument(0),
							invocation.getArgument(1),
							invocation.getArgument(2)));
					return true;
				});
	}
}
