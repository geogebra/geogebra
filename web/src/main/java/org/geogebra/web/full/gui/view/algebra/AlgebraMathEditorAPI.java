package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.io.EditorStateDescription;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.main.MathEditorAPI;

import com.himamis.retex.editor.web.MathFieldW;
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
