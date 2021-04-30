package org.geogebra.web.full.euclidian;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.SymbolicEditor;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.euclidian.draw.LaTeXTextRenderer;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.components.MathFieldEditor;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.euclidian.HasMathKeyboardListener;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.renderer.share.TeXFont;

/**
 * MathField-capable editor for EV, Web implementation.
 *
 * @author Laszlo
 */
public class SymbolicEditorW extends SymbolicEditor implements HasMathKeyboardListener,
		BlurHandler, ChangeHandler {

	private GRectangle bounds;
	private MathFieldEditor editor;
	private final SymbolicEditorDecorator decorator;

	/**
	 * Constructor
	 *
	 * @param app
	 *            The application.
	 */
	public SymbolicEditorW(App app, EuclidianViewW view) {
		super(app, view);
		editor = new MathFieldEditor(app, this);
		editor.addBlurHandler(this);
		editor.getMathField().setChangeListener(this);
		editor.getMathField().setFixMargin(LaTeXTextRenderer.MARGIN);
		editor.getMathField().setMinHeight(DrawInputBox.SYMBOLIC_MIN_HEIGHT);
		int baseFontSize = app.getSettings()
				.getFontSettings().getAppFontSize() + 3;

		decorator = new SymbolicEditorDecorator(editor, baseFontSize);
	}

	@Override
	public void attach(GeoInputBox geoInputBox, GRectangle bounds) {
		if (getDrawInputBox() != null && getDrawInputBox().getGeoElement() != geoInputBox) {
			getDrawInputBox().setEditing(false);
		}
		setInputBox(geoInputBox);

		this.bounds = bounds;
		// add to DOM, but hidden => getHeight works, but widget is not shown in wrong position
		editor.setVisible(false);
		editor.getMathField().setPixelRatio(((AppW) app).getPixelRatio());
		editor.setFontType(geoInputBox.isSerifContent() ? TeXFont.SERIF
				:  TeXFont.SANSSERIF);
		editor.attach(((EuclidianViewW) view).getAbsolutePanel());
		((AppWFull) app).setInputBoxType(geoInputBox.getInputBoxType());
		((AppWFull) app).setInputBoxFunctionVars(geoInputBox.getFunctionVars());
		// update size and show
		resetChanges();
	}

	@Override
	public void repaintBox(GGraphics2D g2) {
		// only in desktop
	}

	@Override
	public MathKeyboardListener getKeyboardListener() {
		return editor.getKeyboardListener();
	}

	@Override
	protected void resetChanges() {
		getDrawInputBox().setEditing(true);

		decorator.update(bounds, getGeoInputBox());
		editor.setVisible(true);
		editor.setText(getGeoInputBox().getTextForEditor());
		editor.setLabel(getGeoInputBox().getAuralText());
		editor.setErrorStyle(getGeoInputBox().hasError());
		if (getGeoInputBox().getLinkedGeo().hasSpecialEditor()) {
			getMathFieldInternal().getFormula().getRootComponent().setProtected();
			getMathFieldInternal().setLockedCaretPath();
		}

		Scheduler.get().scheduleDeferred(() -> editor.requestFocus());
	}

	@Override
	public boolean isClicked(GPoint point) {
		return getDrawInputBox().isEditing() && bounds.contains(point.getX(), point.getY());
	}

	@Override
	protected MathFieldInternal getMathFieldInternal() {
		return editor.getMathField().getInternal();
	}

	@Override
	public void hide() {
		if (!getDrawInputBox().isEditing()) {
			return;
		}

		((AppWFull) app).resetInputBox();
		applyChanges();
		getDrawInputBox().setEditing(false);
		editor.setVisible(false);

		AnimationScheduler.get()
				.requestAnimationFrame(timestamp -> ((EuclidianViewW) view).doRepaint2());
	}

	@Override
	public void onKeyTyped(String key) {
		decorator.update();
		getGeoInputBox().update();
		editor.scrollHorizontally();
		editor.updateAriaLabel();
	}

	@Override
	public boolean onEscape() {
		resetChanges();
		return true;
	}

	@Override
	public void onTab(boolean shiftDown) {
		applyChanges();
		hide();
		((GlobalKeyDispatcherW) app.getGlobalKeyDispatcher()).handleTab(shiftDown);
	}

	@Override
	public void onBlur(BlurEvent event) {
		hide();
	}

	@Override
	public void onChange(ChangeEvent event) {
		decorator.update();
	}
}
