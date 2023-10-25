package org.geogebra.common.spreadsheet.core;

public interface CopyPasteCutTabularData {
	void copy(TabularRange range);

	void copyDeep(TabularRange range);

	void paste(TabularRange range);

	void paste(int row, int column);

	void cut(TabularRange range);
}
