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
			if (inputBox.symbolicMode && ((GeoNumeric) linkedGeo).isSymbolicMode()
					&& !((GeoNumeric) linkedGeo).isSimple()) {
				linkedGeoText = toLaTex();
			} else if (linkedGeo.isDefined() && linkedGeo.isIndependent()) {
				linkedGeoText = linkedGeo.toValueString(inputBox.tpl);
			} else {
				linkedGeoText = linkedGeo.getRedefineString(true, true);
			}
		} else if (inputBox.isSymbolicMode()) {
			if (linkedGeo.isGeoVector() && !linkedGeo.isIndependent()) {
				linkedGeoText = linkedGeo.getRedefineString(true, true);
			} else {
				linkedGeoText = toLaTex();
			}
		} else {
			linkedGeoText = linkedGeo.getRedefineString(true, true);
		}

		if ("?".equals(linkedGeoText)) {
			return "";
		}

		return linkedGeoText;
	}

	private String toLaTex() {
		boolean flatEditableList = !hasEditableMatrix() && linkedGeo.isGeoList();

		if (inputBox.hasSymbolicFunction() || flatEditableList) {
			return linkedGeo.getRedefineString(true, true,
					getStringtemplateForLaTeX());
		} else if (linkedGeo instanceof GeoVectorND) {
			return ((GeoVectorND) linkedGeo).toLaTeXStringAsColumnVector(StringTemplate.latexTemplate);
		}
		return linkedGeo.toLaTeXString(true, StringTemplate.latexTemplate);
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
