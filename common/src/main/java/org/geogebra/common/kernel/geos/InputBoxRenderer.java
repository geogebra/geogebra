package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.StringTemplate;
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

	private boolean isTextUndefined(String text) {
		return "?".equals(text);
	}

	private String getTextForSymbolic() {
		boolean flatEditableList = !hasEditableMatrix() && linkedGeo.isGeoList();

		if (inputBox.hasSymbolicFunction() || flatEditableList) {
			return getLaTeXRedefineString();
		} else if (hasVector()) {
			return getVectorString((GeoVectorND) linkedGeo);
		}

		return toLaTex();
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
		return linkedGeo.toLaTeXString(true, StringTemplate.latexTemplate);
	}

	private boolean hasVector() {
		return linkedGeo instanceof GeoVectorND;
	}

	private String getVectorString(GeoVectorND vector) {
		return vector.isColumnEditabe()
				? vector.toLaTeXStringAsColumnVector(StringTemplate.latexTemplate)
				: getLaTeXRedefineString();
	}

	private String getLaTeXRedefineString() {
		return linkedGeo.getRedefineString(true, true,
				getStringTemplateForLaTeX());
	}

	private StringTemplate getStringTemplateForLaTeX() {
		if (stringTemplateForLaTeX == null) {
			stringTemplateForLaTeX = StringTemplate.latexTemplate.makeStrTemplateForEditing();
		}
		return stringTemplateForLaTeX;
	}

	private boolean hasEditableMatrix() {
		if (!linkedGeo.isGeoList()) {
			return false;
		}

		return ((GeoList) linkedGeo).isEditableMatrix();
	}
}
