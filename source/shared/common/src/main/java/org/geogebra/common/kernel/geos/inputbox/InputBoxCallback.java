/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
import org.geogebra.editor.share.util.Unicode;

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
			((GeoPoint) linkedGeo).setMode(toStringMode);
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
