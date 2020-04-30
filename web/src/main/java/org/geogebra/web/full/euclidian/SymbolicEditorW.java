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
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.euclidian.HasMathKeyboardListener;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.himamis.retex.editor.share.editor.MathFieldInternal;

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
		editor.getMathField().setMinHeight(DrawInputBox.MIN_HEIGHT);
		int baseFontSize = app.getSettings()
				.getFontSettings().getAppFontSize() + 3;

		decorator = new SymbolicEditorDecorator(editor, baseFontSize);
	}

	@Override
	public void attach(GeoInputBox geoInputBox, GRectangle bounds) {
		super.attach(geoInputBox, bounds);

		this.bounds = bounds;
		resetChanges();
		editor.attach(((EuclidianViewW) view).getAbsolutePanel());
	}

	@Override
	public void repaintBox(GGraphics2D g2) {
		// only in desktop
	}

	@Override
	public MathKeyboardListener getKeyboardListener() {
		return editor.getKeyboardListener();
	}

	protected void resetChanges() {
		getDrawInputBox().setEditing(true);
		editor.setVisible(true);
		decorator.update(bounds, getGeoInputBox());

		editor.setText(getGeoInputBox().getTextForEditor());
		editor.setLabel(getGeoInputBox().getAuralText());
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				editor.requestFocus();
			}
		});
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

		applyChanges();
		getDrawInputBox().setEditing(false);
		editor.setVisible(false);

		AnimationScheduler.get()
				.requestAnimationFrame(new AnimationScheduler.AnimationCallback() {
			@Override
			public void execute(double timestamp) {
				((EuclidianViewW) view).doRepaint2();
			}
		});
	}

	@Override
	public void onKeyTyped() {
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
		app.getGlobalKeyDispatcher().handleTab(false, shiftDown);
		app.getSelectionManager().nextFromInputBox();
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
