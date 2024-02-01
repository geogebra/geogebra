package org.geogebra.common.euclidian;

import java.util.HashMap;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.inputbox.EditorContent;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.SyntaxAdapterImpl;

import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.serializer.GeoGebraSerializer;
import com.himamis.retex.editor.share.serializer.TeXSerializer;
import com.himamis.retex.editor.share.util.JavaKeyCodes;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * MathField-capable editor for input boxes on EuclidianView.
 */
public abstract class SymbolicEditor implements MathFieldListener {

	protected final App app;
	protected final EuclidianView view;

	protected TeXSerializer texSerializer;

	private GeoInputBox geoInputBox;
	private DrawInputBox drawInputBox;
	private final GeoGebraSerializer asciiSerializer = new GeoGebraSerializer();
	private double baseline;

	protected SymbolicEditor(App app, EuclidianView view) {
		this.app = app;
		this.view = view;
		this.texSerializer = new TeXSerializer(new SyntaxAdapterImpl(app.getKernel()));
		asciiSerializer.forceRoundBrackets();
	}

	protected void applyChanges() {
		MathFormula formula = getMathFieldInternal().getFormula();
		String editedText = null;
		if (getMathFieldInternal().getInputController().getPlainTextMode()) {
			asciiSerializer.setComma(",");
		} else {
			asciiSerializer.setComma(app.getLocalization().isUsingDecimalComma() ? "." : "");
		}
		String[] entries = asciiSerializer.serializeMatrixEntries(formula);
		if (entries.length == 0) {
			editedText = asciiSerializer.serialize(formula);
		}
		geoInputBox.updateLinkedGeo(editedText,
				texSerializer.serialize(formula), entries);
	}

	protected boolean isTextMode() {
		return getGeoInputBox().getLinkedGeo() instanceof GeoText;
	}

	protected abstract MathFieldInternal getMathFieldInternal();

	/**
	 * Apply changes and hide the editor if it was attached.
	 */
	public void applyAndHide() {
		if (getDrawInputBox().isEditing()) {
			applyChanges();
			hide();
		}
	}

	/**
	 * Apply changes and hide the widget.
	 * Deferred, so that we can avoid infine recursion in update.
	 */
	public void applyAndHidDeferred() {
		if (getDrawInputBox().isEditing()) {
			app.invokeLater(this::applyAndHide);
		}
	}

	protected abstract void hide();

	/**
	 * @param point
	 *            mouse coordinates
	 * @return if editor is clicked.
	 */
	public abstract boolean isClicked(GPoint point);

	/**
	 * Attach the symbolic editor to the specified input box for editing it.
	 * @param geoInputBox GeoInputBox to edit.
	 * @param bounds place to attach the editor to.
	 * @param settings how to render text.
	 */
	public abstract void attach(GeoInputBox geoInputBox, GRectangle bounds,
			TextRendererSettings settings);

	/**
	 * @param caretLocation position of caret relative to view
	 * @param bounds input field bounds in view (in pixels)
	 */
	public void selectEntryAt(GPoint caretLocation, GRectangle2D bounds) {
		if (caretLocation != null) {
			selectEntryAt(caretLocation.x - (int) bounds.getMinX(),
					caretLocation.y - (int) bounds.getMinY());
		} else {
			selectEntryAt(0, 0);
		}
	}

	protected abstract void selectEntryAt(int x, int y);

	protected void setInputBox(GeoInputBox geoInputBox) {
		this.geoInputBox = geoInputBox;
		this.drawInputBox = (DrawInputBox) view.getDrawableFor(geoInputBox);
	}

	@Override
	public void onEnter() {
		String oldLabel = getGeoInputBox().getLabelSimple();
		applyChanges();
		GeoElement input = getGeoInputBox().getKernel().lookupLabel(oldLabel);
		if (input == getGeoInputBox()) {
			resetChanges();
		} else {
			DrawableND drawable = view.getDrawableFor(input);
			if (drawable instanceof DrawInputBox) {
				showRedefinedBox((DrawInputBox) drawable);
			}
		}
	}

	/**
	 * Show this for the new drawbale after redefine; overriden to be async in desktop
	 * @param drawable input box drawable
	 */
	protected void showRedefinedBox(DrawInputBox drawable) {
		drawable.setWidgetVisible(true);
	}

	protected void resetChanges() {
		boolean textMode = isTextMode();
		String text = getGeoInputBox().getTextForEditor();
		getMathFieldInternal().setAllowAbs(
				!(getGeoInputBox().getLinkedGeo() instanceof GeoPointND));
		getMathFieldInternal().setPlainTextMode(textMode);
		if (textMode) {
			getMathFieldInternal().setPlainText(text);
		} else {
			getMathFieldInternal().parse(text);
		}
		setProtection();
	}

	public abstract void repaintBox(GGraphics2D g2);

	public GeoInputBox getGeoInputBox() {
		return geoInputBox;
	}

	public DrawInputBox getDrawInputBox() {
		return drawInputBox;
	}

	protected void addDegree(String key, MathFieldInternal mf) {
		if (geoInputBox.getLinkedGeo().isGeoAngle() && key != null && isSimpleNumber(mf)
				&& key.matches("[0-9]")) {
			mf.insertString(Unicode.DEGREE_STRING);
			mf.onKeyPressed(new KeyEvent(JavaKeyCodes.VK_LEFT));
		}
	}

	private boolean isSimpleNumber(MathFieldInternal mf) {
		String text = mf.getText();
		try {
			Double.parseDouble(text);
			return true;
		} catch (RuntimeException e) {
			// not a number
		}
		return false;
	}

	public void removeListeners() {
		// web only
	}

	protected double computeTop(int height) {
		return MyMath.clamp(baseline - height / 2d, 0, view.getHeight() - height);
	}

	protected void setBaseline(double baseline) {
		this.baseline = baseline;
	}

	protected void setProtection() {
		if (getGeoInputBox().isListEditor()) {
			getMathFieldInternal().getFormula().getRootComponent().setKeepCommas();
		} else if (getGeoInputBox().getLinkedGeo().hasSpecialEditor()) {
			getMathFieldInternal().getFormula().getRootComponent().setProtected();
			getMathFieldInternal().setLockedCaretPath();
		}
	}

	/**
	 * get editor state
	 * @return input as flat string
	 */
	public String getEditorState() {
		MathFormula formula = getMathFieldInternal().getFormula();
		String editedText = null;
		String[] entries = asciiSerializer.serializeMatrixEntries(formula);
		if (entries.length == 0) {
			editedText = asciiSerializer.serialize(formula);
		}
		return editedText;
	}

	/**
	 * @return current content in GGB syntax, using ? for empty matrix entries
	 */
	public EditorContent getEditorStateWithQuestionMarks() {
		MathFormula formula = getMathFieldInternal().getFormula();
		GeoGebraSerializer geoGebraSerializer = new GeoGebraSerializer();
		geoGebraSerializer.setShowPlaceholderAsQuestionmark(true);
		String content;
		if (getGeoInputBox().getLinkedGeo() instanceof GeoVectorND) {
			String[] entries = geoGebraSerializer.serializeMatrixEntries(formula);
			content = "(" + StringUtil.join(",", entries) + ")";
		} else {
			content = geoGebraSerializer.serialize(formula);
		}
		return new EditorContent(content, null, new String[0], 0);
	}

	/**
	 * serialize to latex
	 * @param input - input text
	 * @return input serialized to latex
	 */
	public String getLatexInput(String input) {
		getMathFieldInternal().parse(input);
		MathFormula formula = getMathFieldInternal().getFormula();
		return texSerializer.serialize(formula);
	}

	protected void dispatchKeyTypeEvent(String key) {
		Event event = new Event(EventType.EDITOR_KEY_TYPED, getGeoInputBox());
		HashMap<String, Object> jsonArgument = new HashMap<>();
		jsonArgument.put("key", key == null ? "" : key);
		jsonArgument.put("label", getGeoInputBox() != null
				? getGeoInputBox().getLabelSimple() : "");
		event.setJsonArgument(jsonArgument);
		app.dispatchEvent(event);
	}

}
