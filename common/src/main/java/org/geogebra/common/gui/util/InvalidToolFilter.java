package org.geogebra.common.gui.util;

import org.geogebra.common.gui.toolcategorization.ToolCollectionFilter;
import org.geogebra.common.main.App;

/**
 * Filters modes that are invalid based on app.
 *
 * Deprecated, use {@link App#getAvailableTools()} instead.
 */
@Deprecated
public class InvalidToolFilter implements ToolCollectionFilter {

	private App app;

	/**
	 * Constructs an InvalidToolFilter.
	 *
	 * @param app app
	 */
	public InvalidToolFilter(App app) {
		this.app = app;
	}

	@Override
	public boolean isIncluded(int tool) {
		return app.isModeValid(tool);
	}
}
