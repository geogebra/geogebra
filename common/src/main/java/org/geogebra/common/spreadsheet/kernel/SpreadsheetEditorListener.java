package org.geogebra.common.spreadsheet.kernel;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;

import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.util.Unicode;

public class SpreadsheetEditorListener implements MathFieldListener {

	final MathFieldInternal mathField;
	final Kernel kernel;
	private final GPoint coords;

	public SpreadsheetEditorListener(MathFieldInternal mathField, Kernel kernel, GPoint coords) {
		this.mathField = mathField;
		this.kernel = kernel;
		this.coords = coords;
	}

	@Override
	public void onEnter() {
		String cmd = GeoElementSpreadsheet.getSpreadsheetCellName(coords.x, coords.y)
				+ Unicode.ASSIGN_STRING + mathField.getText();
		kernel.getAlgebraProcessor().processAlgebraCommand(
				cmd, true);
	}

	@Override
	public void onKeyTyped(String key) {
		// not needed
	}

	@Override
	public boolean onArrowKeyPressed(int keyCode) {
		return false;
	}

	@Override
	public void onInsertString() {
		// not needed
	}

	@Override
	public boolean onEscape() {
		return false;
	}

	@Override
	public boolean onTab(boolean shiftDown) {
		return false;
	}
}
