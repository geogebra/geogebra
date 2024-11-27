package org.geogebra.common.kernel.geos.inputbox;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.arithmetic3D.MyVec3DNode;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.AsyncOperation;

import com.himamis.retex.editor.share.util.Unicode;

class InputBoxCallback implements AsyncOperation<GeoElementND> {

	private final GeoInputBox inputBox;
	private final boolean hasSpecialEditor;
	private int toStringMode;

	InputBoxCallback(GeoInputBox inputBox) {
		this.inputBox = inputBox;
		this.hasSpecialEditor = inputBox.getLinkedGeo().hasSpecialEditor();
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
		GeoElementND linkedGeo = inputBox.getLinkedGeo();
		if (GeoPoint.isComplexNumber(linkedGeo)) {
			ExpressionNode def = obj.getDefinition();
			if (def != null && def.getOperation() == Operation.PLUS && def.getRight()
					.toString(StringTemplate.defaultTemplate).equals("0" + Unicode.IMAGINARY)) {
				obj.setDefinition(def.getLeftTree());
				obj.updateRepaint();
			}
		}
		if (hasSpecialEditor && linkedGeo instanceof GeoPointND) {
			ExpressionNode def = linkedGeo.getDefinition();
			if (def != null && !(def.unwrap() instanceof MyVecNDNode)) {
				MyVecNDNode wrappedDef = asVecNode(def, linkedGeo);
				obj.setDefinition(wrappedDef.wrap());
				obj.updateRepaint();
			}
		}
		inputBox.setLinkedGeo(obj);
	}

	private MyVecNDNode asVecNode(ExpressionNode def, GeoElementND linkedGeo) {
		MyDouble zero = new MyDouble(def.getKernel(), 0);
		return linkedGeo.isGeoElement3D()
				? new MyVec3DNode(def.getKernel(), def, zero, zero)
				: new MyVecNode(def.getKernel(), def, zero);
	}
}
