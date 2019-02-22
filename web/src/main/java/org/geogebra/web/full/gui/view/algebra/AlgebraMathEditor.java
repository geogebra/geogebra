package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.geogebra.web.html5.main.MathEditor;

import com.himamis.retex.editor.web.MathFieldW;

public class AlgebraMathEditor implements MathEditor {

	private RadioTreeItem algebraItem;

	public AlgebraMathEditor(RadioTreeItem radioTreeItem) {
		this.algebraItem = radioTreeItem;
	}

	public void setState(String text) {
		MathFieldW mathField = algebraItem.getMathField();
		if (mathField != null) {
			try {
				JSONObject jso = new JSONObject(new JSONTokener(text));
				algebraItem.prepareEdit(jso.getString("content"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public String getState() {
		MathFieldW mathField = algebraItem.getMathField();
		if (mathField != null) {
			try {
				JSONObject jso = new JSONObject();
				jso.put("content", mathField.getText());
				return jso.toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}

}
