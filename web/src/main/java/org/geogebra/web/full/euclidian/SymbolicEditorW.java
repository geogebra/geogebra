package org.geogebra.web.full.euclidian;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.SymbolicEditor;
import org.geogebra.common.euclidian.TextRendererSettings;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.components.MathFieldEditor;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.euclidian.HasMathKeyboardListener;
import org.geogebra.web.html5.gui.accessibility.AccessibleInputBox;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;
import org.gwtproject.animation.client.AnimationScheduler;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.event.dom.client.BlurEvent;
import org.gwtproject.event.dom.client.BlurHandler;
import org.gwtproject.event.dom.client.ChangeEvent;
import org.gwtproject.event.dom.client.ChangeHandler;

import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.renderer.share.TeXFont;

/**
 * MathField-capable editor for EV, Web implementation.
 *
 * @author Laszlo
 */
public class SymbolicEditorW extends SymbolicEditor implements HasMathKeyboardListener,
		BlurHandler, ChangeHandler {

	private static final int EDITOR_PADDING = 2;
	private GRectangle bounds;
	private final MathFieldEditor editor;
	private final SymbolicEditorDecorator decorator;

	/**
	 * Constructor
	 * @param app The application.
	 * @param settings font size/padding/margin settings
	 */
	public SymbolicEditorW(App app, EuclidianViewW view, TextRendererSettings settings) {
		super(app, view);
		editor = new MathFieldEditor(app, this);
		editor.addBlurHandler(this);
		editor.getMathField().setChangeListener(this);
		editor.setTextRendererSettings(settings);
		decorator = new SymbolicEditorDecorator(editor, settings.getFixMargin());
	}

	@Override
	public void attach(GeoInputBox geoInputBox, GRectangle bounds, TextRendererSettings settings) {
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
		editor.setTextRendererSettings(settings);
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
	public void removeListeners() {
		editor.removeListeners();
	}

	@Override
	public MathKeyboardListener getKeyboardListener() {
		return editor.getKeyboardListener();
	}

	@Override
	protected void resetChanges() {
		getDrawInputBox().setEditing(true);

		decorator.update(bounds, getGeoInputBox(), app.getFontSize());
		setBaseline(bounds.getY() + bounds.getHeight() / 2d);

		colorEditor();
		editor.setVisible(true);

		super.resetChanges();

		editor.setLabel(getGeoInputBox().getAuralText());
		if (getGeoInputBox().hasError()) {
			editor.setErrorText(AccessibleInputBox.getErrorText(app.getLocalization()));
		} else {
			editor.setErrorText(null);
		}

		Scheduler.get().scheduleDeferred(editor::requestFocus);
	}

	private void colorEditor() {
		GColor borderCol = getDrawInputBox().getBorderColor();
		if (borderCol != null && !getDrawInputBox().hasError()) {
			editor.getStyle().setBorderColor(borderCol.toString());
			return;
		}
		if (getDrawInputBox().hasError()) {
			editor.getStyle().clearBackgroundColor();
			editor.getStyle().clearBorderColor();
			return;
		}
		editor.getStyle().clearBorderColor();
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
	protected void hide() {
		((AppWFull) app).resetInputBox();
		getDrawInputBox().setEditing(false);
		editor.setVisible(false);
		AnimationScheduler.get()
				.requestAnimationFrame(timestamp -> ((EuclidianViewW) view).doRepaint2());
	}

	@Override
	public void onKeyTyped(String key) {
		updateTop();
		addDegree(key, editor.getMathField().getInternal());
		getDrawInputBox().update();
		editor.scrollCursorVisibleHorizontally();
		editor.updateAriaLabel();
		dispatchKeyTypeEvent(key);
	}

	@Override
	public void onEnter() {
		super.onEnter();
		editor.updateAriaLabel();
	}

	@Override
	public boolean onEscape() {
		applyAndHide();
		return true;
	}

	@Override
	public boolean onTab(boolean shiftDown) {
		applyAndHide();
		return ((GlobalKeyDispatcherW) app.getGlobalKeyDispatcher()).handleTab(shiftDown);
	}

	@Override
	public void onBlur(BlurEvent event) {
		applyAndHide();
	}

	@Override
	public void onChange(ChangeEvent event) {
		updateTop();
	}

	private void updateTop() {
		if (editor.asWidget().getOffsetHeight() > 0) {
			decorator.setTop(computeTop(editor.asWidget().getOffsetHeight()));
		}
	}

	@Override
	public boolean onArrowKeyPressed(int keyCode) {
		editor.scrollCursorVisibleHorizontally();
		return false;
	}

	@Override
	public void selectEntryAt(int x, int y) {
		editor.selectEntryAt(x - EDITOR_PADDING, y - EDITOR_PADDING);
	}
}
