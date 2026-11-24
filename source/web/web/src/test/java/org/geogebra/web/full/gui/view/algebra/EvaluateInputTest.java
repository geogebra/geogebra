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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.geogebra.common.gui.view.algebra.GeoSelectionCallback;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class EvaluateInputTest {

	@Test
	public void nullEvaluationShouldRemovePreviewOutput() {
		AlgebraViewW algebraView = mock(AlgebraViewW.class);
		AppWFull app = AppMocker.mockApplet(new AppletParameters("suite"));
		RadioTreeItem item = spy(new LaTeXTreeItem(app.getKernel(), algebraView));
		RadioTreeItemController controller = mock(RadioTreeItemController.class);
		EvaluateInput evaluateInput =
				new EvaluateInput(item, controller, mock(GeoSelectionCallback.class));
		evaluateInput.evaluationCallback(false).callback(null);

		verify(item).removeOutput();
	}
}
