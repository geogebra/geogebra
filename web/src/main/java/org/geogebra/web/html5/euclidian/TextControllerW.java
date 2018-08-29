package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.TextController;
import org.geogebra.common.euclidian.draw.DrawText;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.web.MathFieldW;

/**
 * Handling text editor in Euclidian View.
 *
 * @author laszlo
 *
 */
public class TextControllerW implements TextController, BlurHandler {
	private FlowPanel textPanel;
	MathFieldW mf;
	private AppW app;
	private EuclidianViewW view;

	/** GeoText to edit */
	GeoText text;
	private class TextListener implements MathFieldListener {
		protected TextListener() {
			// nothing to do
		}

		@Override
		public void onEnter() {
			mf.insertString("\n");

		}

		@Override
		public void onKeyTyped() {
			text.setTextString(mf.getText());
			text.updateRepaint(false);
		}


		@Override
		public void onCursorMove() {
			// not used.

		}

		@Override
		public void onUpKeyPressed() {
			// not used.
		}

		@Override
		public void onDownKeyPressed() {
			// not used.
		}

		@Override
		public String serialize(MathSequence selectionText) {
			return null;
		}

		@Override
		public void onInsertString() {
			// not used.
		}

		@Override
		public boolean onEscape() {
			mf.blur();
			return false;
		}

		@Override
		public void onTab(boolean shiftDown) {
			// not used.
		}

	}

	/**
	 * Constructor.
	 *
	 * @param app
	 *            the application.
	 */
	public TextControllerW(AppW app) {
		this.app = app;
		this.view = (EuclidianViewW) (app.getActiveEuclidianView());
	}

	private void createGUI() {
		textPanel = new FlowPanel();
		textPanel.addStyleName("textEditorPanel");
		AbsolutePanel evPanel = view.getAbsolutePanel();
		evPanel.add(textPanel);
		Canvas canvas = Canvas.createIfSupported();
		TextListener mfListener = new TextListener();
		textPanel.add(canvas);
		mf = new MathFieldW(null, textPanel, canvas, mfListener, false, null);
		mf.setPixelRatio(app.getPixelRatio());
		mf.setScale(app.getArticleElement().getScaleX());
		mf.setLineBreakEnabled(true);
		mf.setOnBlur(this);
	}

	private void updateEditor(int x, int y) {
		if (textPanel == null) {
			createGUI();
		}

		textPanel.removeStyleName("hidden");
		mf.requestViewFocus();
		mf.setText(text.getText().getTextString(), true);
		textPanel.getElement().getStyle().setLeft(x, Unit.PX);
		textPanel.getElement().getStyle().setTop(y, Unit.PX);
	}

	@Override
	public GeoText createText(GeoPointND loc, boolean rw) {
		if (loc == null) {
			return null;
		}
		GeoText t = app.getKernel().getAlgebraProcessor().text("");
		t.setEditMode(true);
		t.setEuclidianVisible(true);
		t.setAbsoluteScreenLocActive(false);
		if (rw) {
			Coords coords = loc.getInhomCoordsInD3();
			t.setRealWorldLoc(view.toRealWorldCoordX(coords.getX()),
					view.toRealWorldCoordY(coords.getY()));
			t.setAbsoluteScreenLocActive(false);
		} else {
			Coords coords = loc.getInhomCoordsInD3();
			t.setAbsoluteScreenLoc((int) coords.getX(), (int) coords.getY());
			t.setAbsoluteScreenLocActive(true);

		}

		t.setLabel(null);
		edit(t);
		app.getKernel().notifyRepaint();
		return t;
	}

	@Override
	public void onBlur(BlurEvent event) {
		textPanel.addStyleName("hidden");
		text.setEditMode(false);
		text.setTextString(mf.getText());
		text.update();
	}

	@Override
	public void edit(GeoText geo) {
		geo.setEditMode(true);
		this.text = geo;

		DrawText d = (DrawText) view.getDrawableFor(geo);
		if (d != null) {
			int x = d.xLabel - 3;
			int y = d.yLabel - view.getFontSize() - 3;
			updateEditor(x, y);
			view.setBoundingBox(d.getBoundingBox());
		}
		view.repaintView();
	}
}