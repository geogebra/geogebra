package org.geogebra.common.spreadsheet.core;

public interface CopyPasteCutTabularData {
	void copy(int columnFrom, int rowFrom, int columnTo, int rowTo);

	void cut(int columnFrom, int rowFrom, int columnTo, int rowTo);
}
