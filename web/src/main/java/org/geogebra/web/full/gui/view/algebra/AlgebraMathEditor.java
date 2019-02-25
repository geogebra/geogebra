package org.geogebra.web.full.gui.view.algebra;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.MathEditor;

import com.himamis.retex.editor.web.MathFieldW;
import com.himamis.retex.renderer.share.CursorBox;

/**
 * Editor API for algebra view
 */
public class AlgebraMathEditor implements MathEditor {

	private AlgebraViewW algebraView;

	/**
	 * @param algebraView
	 *            algebra view
	 */
	public AlgebraMathEditor(AlgebraViewW algebraView) {
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
		
		try {
			JSONObject jso = new JSONObject(new JSONTokener(text));
			algebraItem.prepareEdit(jso.getString("content"));

			JSONArray caretPathJson = jso.getJSONArray("caret");
			ArrayList<Integer> caretPath = new ArrayList<>();
			for (int index = 0; index < caretPathJson.length(); index++) {
				caretPath.add(caretPathJson.getInt(index));
			}
			if (algebraItem.getMathField() != null) {
				CursorBox.blink = true;
				Log.debug(StringUtil.join(",", caretPath));
				algebraItem.getMathField().setCaretPath(caretPath);
				algebraItem.getMathField().repaintWeb();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getState() {
		RadioTreeItem algebraItem = algebraView.getActiveTreeItem();
		MathFieldW mathField = algebraItem.getMathField();
		if (mathField != null) {
			try {
				JSONObject jso = new JSONObject();
				jso.put("content", mathField.getText());
				ArrayList<Integer> caretPath = mathField.getCaretPath();
				JSONArray caretPathJson = new JSONArray();
				for (Integer pathElement : caretPath) {
					caretPathJson.put(pathElement);
				}
				jso.put("caret", caretPathJson);
				return jso.toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}

}
