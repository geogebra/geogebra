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
