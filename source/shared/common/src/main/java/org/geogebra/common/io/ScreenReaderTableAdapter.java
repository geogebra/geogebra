package org.geogebra.common.io;

import org.geogebra.common.main.OrdinalConverter;
import org.geogebra.common.util.lang.Language;

import com.himamis.retex.renderer.share.serialize.TableAdapter;

public class ScreenReaderTableAdapter extends TableAdapter {
	private boolean transpose;
	String type = "table";

	@Override
	public String matrixStart(int rows, int cols) {
		if (transpose) {
			return type + " with " + cols + " columns and " + rows + " rows ";
		}
		return type + " with " + rows + " rows and " + cols + " columns ";
	}

	@Override
	public String matrixEnd() {
		return " end of " + type;
	}

	@Override
	public String matrixRowOrColumnEnd() {
		return "";
	}

	@Override
	public String matrixRowStart(int row) {
		return "The " + OrdinalConverter.getOrdinalNumber(Language.English_UK, row + 1)
				+ (transpose ? " column is " : " row is ");
	}

	@Override
	public boolean shouldTransposeMatrices() {
		return transpose;
	}

	@Override
	public void setShouldTransposeMatrices(boolean transpose) {
		this.transpose = transpose;
	}

	@Override
	public String getSeparator() {
		return " ";
	}

	@Override
	public void setMatrixType(String left, String right) {
		if ("|".equals(left) && "|".equals(right)) {
			type = "determinant";
		} else if (" open parenthesis ".equals(left) && " close parenthesis ".equals(right)
			|| "(".equals(left) && ")".equals(right)) {
			type = "matrix";
		} else {
			type = "table";
		}
	}
}
