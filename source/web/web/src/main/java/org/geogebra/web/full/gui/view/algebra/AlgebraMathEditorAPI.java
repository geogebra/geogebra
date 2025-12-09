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

package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.io.EditorStateDescription;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.editor.web.MathFieldW;
import org.geogebra.web.html5.main.MathEditorAPI;

import com.himamis.retex.renderer.share.CursorBox;

/**
 * Editor API for algebra view
 */
public class AlgebraMathEditorAPI implements MathEditorAPI {

	private AlgebraViewW algebraView;

	/**
	 * @param algebraView
	 *            algebra view
	 */
	public AlgebraMathEditorAPI(AlgebraViewW algebraView) {
		this.algebraView = algebraView;
	}

	@Override
	public void setState(String text, GeoElement geo) {
		RadioTreeItem algebraItem;
		if (geo != null) {
			algebraItem = algebraView.getNode(geo);
		} else {
			algebraItem = algebraView.getInputTreeItem();
		}
		
		EditorStateDescription editorJsonHandler = EditorStateDescription
				.fromJSON(text);
		if (editorJsonHandler != null) {
			algebraItem.prepareEdit(editorJsonHandler.getContent());
			if (algebraItem.getMathField() != null) {
				CursorBox.setBlink(true);
				algebraItem.getMathField()
						.setCaretPath(editorJsonHandler.getCaretPath());
				algebraItem.getMathField().keepFocus();
				algebraItem.getMathField().repaintWeb();
			}
		}
	}

	@Override
	public String getState() {
		RadioTreeItem algebraItem = algebraView.getActiveTreeItem();
		MathFieldW mathField = algebraItem.getMathField();
		if (mathField != null) {
			return new EditorStateDescription(mathField.getText(),
					mathField.getCaretPath()).asJSON();
		}
		return "";
	}

}
