package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

class InputBoxRenderer {
	private GeoElementND linkedGeo;
	private StringTemplate stringTemplateForLaTeX;
	private GeoInputBox inputBox;

	InputBoxRenderer(GeoInputBox inputBox) {
		this.inputBox = inputBox;
		this.linkedGeo = inputBox.getLinkedGeo();
	}

	String getText() {
		if (linkedGeo.isGeoText()) {
			return ((GeoText) linkedGeo).getTextString();
		}

		String linkedGeoText;

		if (linkedGeo.isGeoNumeric()) {
			linkedGeoText = getTextForNumeric();
		} else if (inputBox.isSymbolicMode()) {
			linkedGeoText = getTextForSymbolic();
		} else {
			linkedGeoText = linkedGeo.getRedefineString(true, true);
		}

		if (isTextUndefined(linkedGeoText)) {
			return "";
		}

		return linkedGeoText;
	}

	private String getTextForSymbolic() {
		return toLaTex();
	}

	private boolean isTextUndefined(String text) {
		return "?".equals(text);
	}


	private String getTextForNumeric() {
		if (inputBox.symbolicMode && ((GeoNumeric) linkedGeo).isSymbolicMode()
				&& !((GeoNumeric) linkedGeo).isSimple()) {
			return toLaTex();
		} else if (linkedGeo.isDefined() && linkedGeo.isIndependent()) {
			return linkedGeo.toValueString(inputBox.tpl);
		}

		return linkedGeo.getRedefineString(true, true);
	}

	private String toLaTex() {
		boolean flatEditableList = !hasEditableMatrix() && linkedGeo.isGeoList();

		if (inputBox.hasSymbolicFunction() || flatEditableList) {
			return linkedGeo.getRedefineString(true, true,
					getStringtemplateForLaTeX());
		} else if (hasEditableVector()) {
			return ((GeoVectorND) linkedGeo).toLaTeXStringAsColumnVector(StringTemplate.latexTemplate);
		} else if (linkedGeo instanceof GeoVectorND) {
			return linkedGeo.getRedefineString(true, true,
					getStringtemplateForLaTeX());
		}
		return linkedGeo.toLaTeXString(true, StringTemplate.latexTemplate);
	}

	private boolean hasEditableVector() {
		if (!(linkedGeo instanceof GeoVectorND)) {
			return false;
		}

		return ((GeoVectorND)linkedGeo).isColumnEditabe();
	}

	private boolean isMyVecNDNode(ExpressionValue value) {
		return value instanceof  MyVecNDNode;
	}

	private boolean hasEditableMatrix() {
		if (!linkedGeo.isGeoList()) {
			return false;
		}

		return ((GeoList) linkedGeo).isEditableMatrix();
	}

	private StringTemplate getStringtemplateForLaTeX() {
		if (stringTemplateForLaTeX == null) {
			stringTemplateForLaTeX = StringTemplate.latexTemplate.makeStrTemplateForEditing();
		}
		return stringTemplateForLaTeX;
	}

}
