package com.himamis.retex.renderer.share.serialize;

public class TableAdapter {

	private boolean determinant;

	/**
	 * Mark start of a table.
	 * @param rows number of rows
	 * @param cols number of columns
	 * @return mark
	 */
	public String matrixStart(int rows, int cols) {
		return determinant ? "Determinant({" : "{";
	}

	/**
	 * @return mark for end of matrix
	 */
	public String matrixEnd() {
		return determinant ? "})" : "}";
	}

	/**
	 * @return mark for row/column start
	 */
	public String matrixRowStart(int row) {
		return "{";
	}

	/**
	 * @return mark for row/column end
	 */
	public String matrixRowOrColumnEnd() {
		return "}";
	}

	/**
	 * @return whether matrices should be read by columns
	 */
	public boolean shouldTransposeMatrices() {
		return false;
	}

	/**
	 * @param transposeMatrices whether matrices should be read by columns
	 */
	public void setShouldTransposeMatrices(boolean transposeMatrices) {
		// not needed
	}

	public String getSeparator() {
		return ",";
	}

	/**
	 * @param left left bracket
	 * @param right right bracket
	 */
	public void setMatrixType(String left, String right) {
		this.determinant = "|".equals(left) && "|".equals(right);
	}
}
