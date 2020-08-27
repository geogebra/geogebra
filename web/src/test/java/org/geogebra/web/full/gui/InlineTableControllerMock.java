package org.geogebra.web.full.gui;

import org.geogebra.common.euclidian.inline.InlineTableController;
import org.mockito.Mockito;

public final class InlineTableControllerMock {

	/**
	 * @param editMode whether the table edit mode is on
	 * @return table controller mock
	 */
	public static InlineTableController get(boolean editMode) {
		InlineTableController mock = Mockito.mock(InlineTableController.class);
		Mockito.when(mock.isInEditMode()).thenReturn(editMode);
		return mock;
	}
}
