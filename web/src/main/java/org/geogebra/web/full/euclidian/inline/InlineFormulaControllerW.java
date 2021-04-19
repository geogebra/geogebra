package org.geogebra.web.full.euclidian.inline;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.draw.DrawFormula;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.euclidian.inline.InlineFormulaController;
import org.geogebra.common.kernel.geos.GeoFormula;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.components.MathFieldEditor;
import org.geogebra.web.html5.gui.util.ClickEndHandler;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.timer.client.Timer;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Panel;
import com.himamis.retex.editor.share.event.MathFieldListener;

public class InlineFormulaControllerW implements InlineFormulaController {

	private final GeoFormula formula;
	private final MathFieldEditor mathFieldEditor;

	private final AbsolutePanel widget;
	private final Style style;

	private final Timer saveTimer = new Timer() {
		@Override
		public void run() {
			formula.setContent(getText());
			formula.getKernel().storeUndoInfo();
		}
	};

	/**
	 * Controller (communicates with MathFieldEditor) for the inline formula editor
	 * @param formula GeoFormula to be edited
	 * @param app the application
	 * @param parent parent panel (generally, the euclidian view)
	 */
	public InlineFormulaControllerW(GeoFormula formula, AppW app, Panel parent) {
		this.formula = formula;
		this.mathFieldEditor = new MathFieldEditor(app, new FormulaMathFieldListener());
		if (formula.getContent() != null) {
			mathFieldEditor.setText(formula.getContent());
		}

		this.widget = new AbsolutePanel();
		ClickEndHandler.init(widget, new ClickEndHandler(true, true) {
			@Override
			public void onClickEnd(int x, int y, PointerEventType type) {
				mathFieldEditor.focus();
			}
		});
		widget.setVisible(false);
		widget.addStyleName("mowWidget");
		parent.add(widget);

		this.style = widget.getElement().getStyle();
		style.setPosition(Style.Position.ABSOLUTE);
		style.setProperty("transformOrigin", "0px 0px");
		style.setPaddingLeft(DrawFormula.PADDING, Style.Unit.PX);

		mathFieldEditor.attach(widget);
		mathFieldEditor.getMathField().setFixMargin(DrawFormula.PADDING);
		mathFieldEditor.setUseKeyboardButton(false);
		mathFieldEditor.getMathField().setBackgroundCssColor("transparent");
	}

	@Override
	public void setLocation(int x, int y) {
		style.setLeft(x, Style.Unit.PX);
		style.setTop(y, Style.Unit.PX);
	}

	@Override
	public void setWidth(int width) {
		style.setWidth(width, Style.Unit.PX);
	}

	@Override
	public void setHeight(int height) {
		style.setHeight(height, Style.Unit.PX);
	}

	@Override
	public void setAngle(double angle) {
		style.setProperty("transform", "rotate(" + angle + "rad)");
	}

	@Override
	public void toForeground(int x, int y) {
		if (formula.getContent() != null) {
			mathFieldEditor.setText(formula.getContent());
		}
		widget.setVisible(true);
		mathFieldEditor.requestFocus();
		mathFieldEditor.getMathField().getInternal().onPointerUp(x, y);
	}

	@Override
	public void toBackground() {
		if (widget.isVisible() && !mathFieldEditor.getMathField()
				.getText().equals(formula.getContent())) {
			saveTimer.cancel();
			saveTimer.run();
		}
		if (widget.isVisible()) {
			formula.updateRepaint();
			widget.setVisible(false);
		}
		mathFieldEditor.setKeyboardVisibility(false);
	}

	@Override
	public void updateContent(String content) {
		if (content != null) {
			mathFieldEditor.setText(content);
		}
	}

	@Override
	public void setColor(GColor objectColor) {
		mathFieldEditor.getMathField().setForegroundCssColor(StringUtil.toHtmlColor(objectColor));
		mathFieldEditor.getMathField().repaintWeb();
	}

	@Override
	public void setFontSize(int fontSize) {
		// +3 coming from DrawEquation.createIcon.... :((
		// FIXME in DrawEquation cleanup
		mathFieldEditor.setFontSize(fontSize + 3);
	}

	@Override
	public boolean isInForeground() {
		return widget.isVisible();
	}

	@Override
	public void discard() {
		mathFieldEditor.setKeyboardVisibility(false);
		widget.removeFromParent();
	}

	@Override
	public String getText() {
		return mathFieldEditor.getMathField().getText();
	}

	private class FormulaMathFieldListener implements MathFieldListener {

		@Override
		public void onEnter() {
			// do nothing
		}

		@Override
		public void onKeyTyped(String key) {
			Scheduler.get().scheduleDeferred(() -> {
				if (saveTimer.isRunning()) {
					saveTimer.cancel();
				}

				saveTimer.schedule(500);

				int width = mathFieldEditor.getMathField().asWidget().getOffsetWidth()
						- DrawFormula.PADDING;
				int height = mathFieldEditor.getMathField().asWidget().getOffsetHeight();

				formula.setSize(Math.max(formula.getWidth(), width),
						Math.max(formula.getHeight(), height));

				formula.setMinWidth(width);
				formula.setMinHeight(height);

				formula.updateRepaint();
			});
		}

		@Override
		public void onCursorMove() {
			// do nothing
		}

		@Override
		public void onUpKeyPressed() {
			// do nothing
		}

		@Override
		public void onDownKeyPressed() {
			// do nothing
		}

		@Override
		public void onInsertString() {
			// do nothing
		}

		@Override
		public boolean onEscape() {
			return false;
		}

		@Override
		public void onTab(boolean shiftDown) {
			// do nothing
		}
	}
}
