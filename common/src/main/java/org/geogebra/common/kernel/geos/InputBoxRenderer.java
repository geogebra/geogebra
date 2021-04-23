package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

import com.himamis.retex.editor.share.util.Unicode;

class InputBoxRenderer {
	private GeoElementND linkedGeo;
	private StringTemplate stringTemplateForLaTeX;
	private GeoInputBox inputBox;
	public String tempUserEvalInput;

	InputBoxRenderer(GeoInputBox inputBox) {
		this.inputBox = inputBox;
		this.linkedGeo = inputBox.getLinkedGeo();
	}

	String getText() {
		if (tempUserEvalInput != null) {
			return tempUserEvalInput;
		}
		if (linkedGeo.isGeoText()) {
			return ((GeoText) linkedGeo).getTextStringSafe().replace("\n", GeoText.NEW_LINE);
		}

		String linkedGeoText;

		if (linkedGeo.isGeoNumeric()) {
			linkedGeoText = getTextForNumeric((GeoNumeric) linkedGeo);
		} else if (inputBox.isSymbolicMode()) {
			linkedGeoText = getTextForSymbolic();
		} else if (isRestrictedPoint()) {
			linkedGeoText = linkedGeo.toValueString(StringTemplate.editTemplate);
		} else {
			linkedGeoText = linkedGeo.getRedefineString(true, true);
		}

		linkedGeoText = linkedGeoText.replace(Unicode.IMAGINARY, 'i');

		if (isTextUndefined(linkedGeoText)) {
			return "";
		}

		return linkedGeoText;
	}

	private boolean isTextUndefined(String text) {
		return "?".equals(text);
	}

	private String getTextForSymbolic() {
		boolean flatEditableList = linkedGeo.isGeoList()
				&& !((GeoList) linkedGeo).hasSpecialEditor();
		boolean isComplexFunction = linkedGeo.isGeoSurfaceCartesian()
				&& linkedGeo.getDefinition() != null;
		if (isRestrictedPoint()) {
			return linkedGeo.toValueString(StringTemplate.latexTemplate);
		} else if (inputBox.hasSymbolicFunction() || flatEditableList || isComplexFunction) {
			return getLaTeXRedefineString();
		} else if (hasVector()) {
			return getVectorRenderString((GeoVectorND) linkedGeo);
		}

		return toLaTex();
	}

	private boolean isRestrictedPoint() {
		return linkedGeo.isPointInRegion() || linkedGeo.isPointOnPath();
	}

	private String getTextForNumeric(GeoNumeric numeric) {
		if (inputBox.symbolicMode && !numeric.isSimple()) {
			return toLaTex();
		} else if (numeric.isDefined() && numeric.isIndependent()) {
			return numeric.toValueString(inputBox.tpl);
		}

		return numeric.getRedefineString(true, true);
	}

	private String toLaTex() {
		return linkedGeo.toLaTeXString(true, StringTemplate.latexTemplate);
	}

	private boolean hasVector() {
		return linkedGeo instanceof GeoVectorND;
	}

	private String getVectorRenderString(GeoVectorND vector) {
		return vector.hasSpecialEditor()
				? vector.toLaTeXString(true, StringTemplate.latexTemplate)
				: getLaTeXRedefineString();
	}

	private String getLaTeXRedefineString() {
		return linkedGeo.getRedefineString(true, true,
				getStringTemplateForLaTeX());
	}

	private StringTemplate getStringTemplateForLaTeX() {
		if (stringTemplateForLaTeX == null) {
			stringTemplateForLaTeX = StringTemplate.latexTemplate;
		}
		return stringTemplateForLaTeX;
	}

	void setLinkedGeo(GeoElementND linkedGeo) {
		this.linkedGeo = linkedGeo;
	}

}
