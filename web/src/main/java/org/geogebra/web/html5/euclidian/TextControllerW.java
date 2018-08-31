package org.geogebra.web.html5.euclidian;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.TextController;
import org.geogebra.common.euclidian.draw.DrawText;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * Handling text editor in Euclidian View.
 *
 * @author laszlo
 *
 */
public class TextControllerW implements TextController, BlurHandler, KeyDownHandler {
	private MowTextEditor editor;
	private AppW app;
	private EuclidianViewW view;

	/** GeoText to edit */
	GeoText text;

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
		editor = new MowTextEditor();
		AbsolutePanel evPanel = view.getAbsolutePanel();
		evPanel.add(editor);
		editor.addKeyDownHandler(this);
		editor.addBlurHandler(this);

	}

	private void updateEditor(int x, int y) {
		if (editor == null) {
			createGUI();
		}
		editor.setText(text.getText().getTextString());
		editor.setPosition(x, y);
	}

	@Override
	public GeoText createText(GeoPointND loc, boolean rw) {
		if (loc == null) {
			return null;
		}
		GeoText t = app.getKernel().getAlgebraProcessor().text("");
		app.getSelectionManager().addSelectedGeo(t);
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
		edit(t, true);
		app.getKernel().notifyRepaint();
		return t;
	}

	@Override
	public void onBlur(BlurEvent event) {
		editor.hide();
		text.setEditMode(false);
		text.setTextString(editor.getText());
		text.update();
		text.updateRepaint();
	}

	@Override
	public void edit(GeoText geo) {
		edit(geo, false);
	}

	private void edit(GeoText geo, boolean create) {
		if (geo.isEditMode()) {
			return;
		}
		geo.setEditMode(true);
		this.text = geo;
		text.update();

		DrawText d = (DrawText) view.getDrawableFor(geo);
		if (d != null) {
			int x = d.xLabel - 3;
			int y = d.yLabel - view.getFontSize() - 3;
			updateEditor(x, y);
			if (create) {
				view.setBoundingBox(d.getBoundingBox());
			}
		}
		editor.show();
		editor.requestFocus();
		view.repaint();
	}

	@Override
	public GRectangle getEditorBounds() {
		if (editor == null) {
			return null;
		}
		return editor.getBounds();
	}

	@Override
	public void onKeyDown(KeyDownEvent arg0) {
		text.updateRepaint();
	}

	@Override
	public void setEditorFont(GFont font) {
		if (editor == null) {
			return;
		}
		editor.setFont(font);
	}

	@Override
	public void setEditorColor(GColor color) {
		if (editor == null) {
			return;
		}
		editor.setColor(color);
	}
}