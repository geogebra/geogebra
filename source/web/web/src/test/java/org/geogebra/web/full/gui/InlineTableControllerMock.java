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

package org.geogebra.web.full.gui;

import org.geogebra.common.euclidian.inline.InlineTableController;
import org.mockito.Mockito;

public final class InlineTableControllerMock {

	/**
	 * @return table controller mock in non-editing mode
	 */
	public static InlineTableController get() {
		return Mockito.mock(InlineTableController.class);
	}

	/**
	 * @return controller in editing mode, with one or multiple cells selected
	 */
	public static InlineTableController getWithSelection(boolean single) {
		InlineTableController mock = get();
		Mockito.when(mock.isInEditMode()).thenReturn(true);
		Mockito.when(mock.hasSelection()).thenReturn(true);
		Mockito.when(mock.isSingleCellSelection()).thenReturn(single);
		return mock;
	}
}
