package org.geogebra.common.kernel.geos.inputbox;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.AsyncOperation;

import com.himamis.retex.editor.share.util.Unicode;

class InputBoxCallback implements AsyncOperation<GeoElementND> {

	private GeoInputBox inputBox;
	private int toStringMode;

	InputBoxCallback(GeoInputBox inputBox) {
		this.inputBox = inputBox;
		saveToStringMode();
	}

	private void saveToStringMode() {
		GeoElementND linkedGeo = inputBox.getLinkedGeo();
		if (linkedGeo instanceof GeoPoint) {
			toStringMode = ((GeoPoint) linkedGeo).getToStringMode();
		}
	}

	private void restoreToStringMode() {
		GeoElementND linkedGeo = inputBox.getLinkedGeo();
		if (linkedGeo instanceof GeoPoint) {
			((GeoPoint) linkedGeo).setToStringMode(toStringMode);
		}
	}

	@Override
	public void callback(GeoElementND obj) {
		restoreToStringMode();
		if (GeoPoint.isComplexNumber(inputBox.getLinkedGeo())) {
			ExpressionNode def = obj.getDefinition();
			if (def != null && def.getOperation() == Operation.PLUS && def.getRight()
					.toString(StringTemplate.defaultTemplate).equals("0" + Unicode.IMAGINARY)) {
				obj.setDefinition(def.getLeftTree());
				obj.updateRepaint();
			}
		}
		inputBox.setLinkedGeo(obj);
	}
}
