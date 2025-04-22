package org.geogebra.web.editor;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.keyboard.web.TabbedKeyboard;

import com.himamis.retex.editor.share.serializer.SolverSerializer;
import com.himamis.retex.editor.share.serializer.TeXSerializer;
import com.himamis.retex.editor.web.MathFieldW;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.serialize.TeXAtomSerializer;

import elemental2.core.Function;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

@JsType
public class EditorApi {
	private final MathFieldW mathField;
	private final TabbedKeyboard kb;
	private final EditorListener listener;

	/**
	 * @param mathField math input field
	 * @param kb keyboard
	 * @param listener editor input listener
	 */
	@JsIgnore
	public EditorApi(MathFieldW mathField, TabbedKeyboard kb,
			EditorListener listener) {
		this.mathField = mathField;
		this.kb = kb;
		this.listener = listener;
	}

	/**
	 * Remove from DOM
	 */
	public void remove() {
		mathField.asWidget().removeFromParent();
		kb.removeFromParent();
	}

	/**
	 * @return state, contains formula in LaTeX, AsciiMath and solver syntax
	 */
	public Object getEditorState() {
		JsPropertyMap<Object> jsObject = JsPropertyMap.of();
		jsObject.set("latex", new TeXSerializer().serialize(mathField.getFormula()));
		jsObject.set("solver", new SolverSerializer().serialize(mathField.getFormula()));
		jsObject.set("content", mathField.getText());
		return jsObject;
	}

	/**
	 * Converts LaTeX input to editor syntax and loads into editor
	 * @param formula input as LaTeX
	 */
	public void evalLaTeX(String formula) {
		TeXFormula tf = new TeXFormula(formula);
		mathField.parse(new TeXAtomSerializer(null).serialize(tf.root));
	}

	/**
	 * Load state into editor
	 * @param state JS object {content: "foo"}
	 */
	public void setEditorState(Object state) {
		JsPropertyMap<String> json = Js.uncheckedCast(state);
		mathField.parse(json.get("content"));
	}

	/**
	 * @param options {type: string, transparent: boolean}
	 * @param callback called with {svg: base64 encoded SVG,
	 *             baseline: relative baseline position} or error
	 */
	public void exportImage(JsPropertyMap<String> options,
			MathFieldExporter.ImageConsumer callback) {
		new MathFieldExporter(mathField).export(options.get("type"),
				Js.isTruthy(options.get("transparent")), callback);
	}

	/**
	 * Register client listener.
	 * @param fn client listener
	 */
	public void registerClientListener(Function fn) {
		listener.registerClientListener(fn);
	}

	/**
	 * open keyboard
	 */
	public void openKeyboard() {
		kb.setVisible(true);
		listener.notifyKeyboardVisibilityChange(true);
	}

	/**
	 * close keyboard
	 */
	public void closeKeyboard() {
		kb.setVisible(false);
		listener.notifyKeyboardVisibilityChange(false);
	}

	public String getVersion() {
		return GeoGebraConstants.VERSION_STRING;
	}

}