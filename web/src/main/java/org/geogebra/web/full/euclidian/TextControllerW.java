package org.geogebra.web.full.euclidian;

import org.geogebra.common.euclidian.TextController;
import org.geogebra.common.euclidian.draw.DrawText;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style.Unit;
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
public class TextControllerW implements TextController {
	private FlowPanel textPanel;
	MathFieldW textMathField;
	DrawText drawText;
	private AppW app;
	private EuclidianViewW view;
	private class TextListener implements MathFieldListener {

		protected TextListener() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onEnter() {
			textMathField.insertString("\n");

		}

		@Override
		public void onKeyTyped() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCursorMove() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUpKeyPressed() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDownKeyPressed() {
			// TODO Auto-generated method stub

		}

		@Override
		public String serialize(MathSequence selectionText) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void onInsertString() {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onEscape() {
			drawText.setEditMode(false);
			drawText = null;
			return false;
		}

		@Override
		public void onTab(boolean shiftDown) {
			// TODO Auto-generated method stub

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
		textMathField = new MathFieldW(null, textPanel, canvas, mfListener, false, null);
		textMathField.setPixelRatio(app.getPixelRatio());
		textMathField.setScale(app.getArticleElement().getScaleX());
	}

	@Override
	public void updateEditor(DrawText dT) {
		if (textPanel == null) {
			createGUI();
		}

		drawText = dT;
		textPanel.getElement().getStyle().setTop(drawText.getyLabel(), Unit.PX);
		textPanel.getElement().getStyle().setLeft(drawText.getxLabel(), Unit.PX);
	}

	@Override
	public GeoText createText(GeoPointND loc, boolean rw) {
		if (loc == null) {
			return null;
		}
		GeoText t = app.getKernel().getAlgebraProcessor().text("Replace me");
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
		app.getKernel().notifyRepaint();
		return t;
	}

}
