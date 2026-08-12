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

package org.geogebra.web.full.gui.view.algebra;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.geogebra.common.gui.view.algebra.GeoSelectionCallback;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class EvaluateInputTest {

	@Test
	public void nullEvaluationShouldRemovePreviewOutput() {
		RadioTreeItem item = mock(RadioTreeItem.class);
		RadioTreeItemController controller = mock(RadioTreeItemController.class);
		AppWFull app = mock(AppWFull.class);
		Kernel kernel = mock(Kernel.class);
		when(item.getApplication()).thenReturn(app);
		when(app.getKernel()).thenReturn(kernel);

		EvaluateInput evaluateInput = new EvaluateInput(item, controller,
				mock(GeoSelectionCallback.class));
		evaluateInput.evaluationCallback(false).callback(null);

		verify(item).removeOutput();
	}
}
