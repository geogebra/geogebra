package org.geogebra.common.spreadsheet.core;

/**
 * Creates renderers for custom data types stored in {@link TabularData}
 */
public interface CellRendererFactory {
	public CellRenderer getRenderer(Object data);
}
