package org.geogebra.common.spreadsheet.core;

import javax.annotation.CheckForNull;

import org.geogebra.common.spreadsheet.rendering.SelfRenderable;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;

/**
 * Creates renderers for custom data types stored in {@link TabularData}
 */
public interface CellRenderableFactory {

	/**
	 * May return one of these types that are rendered by spreadsheet
	 * * String
	 * * Boolean (render as checkbox) - TODO
	 * * TeXIcon
	 * * button - TODO
	 * @param data cell content
	 * @return renderable representation of the cell data
	 */
	@CheckForNull SelfRenderable getRenderable(@CheckForNull Object data,
			@CheckForNull SpreadsheetStyling fontStyle, int row, int column);
}
