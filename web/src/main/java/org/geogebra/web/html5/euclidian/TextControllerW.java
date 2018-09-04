package org.geogebra.web.html5.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.TextController;
import org.geogebra.common.euclidian.draw.DrawText;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.awt.GFontRenderContextW;
import org.geogebra.web.html5.awt.GFontW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * Handling text editor in Euclidian View.
 *
 * @author laszlo
 *
 */
public class TextControllerW implements TextController, FocusHandler, BlurHandler, KeyDownHandler {
	private MowTextEditor editor;
	private AppW app;
	private EuclidianViewW view;

	/** GeoText to edit */
	GeoText text;
	private GeoText lastText;

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
		editor.addFocusHandler(this);
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
		view.setBoundingBox(null);
		text.cancelEditMode();
		text.setTextString(editor.getText());
		text.update();
		text.updateRepaint();
	}

	@Override
	public void edit(GeoText geo) {
		edit(geo, false);
	}

	private void edit(GeoText geo, boolean create) {
		// if (geo.isEditMode()) {
		// return;
		// }
		geo.setEditMode();
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
	public String wrapText(String txt) {
		String editText = txt.replace("&nbsp;", " ");
		String[] rows = editText.split("\n");
		ArrayList<String> wrappedRows = new ArrayList<>();
		for (int i = 0; i < rows.length; i++) {
			wrappedRows.addAll(wrapRow(rows[i]));
		}
		return StringUtil.join("\n", wrappedRows);
	}

	private int getCurrentWidth() {
		DrawText d = (DrawText) view.getDrawableFor(text);
		if (d == null || d.getBounds() == null) {
			return DrawText.getMinEditorWidth();
		}
		return (int) d.getBounds().getWidth();
	}

	/**
	 * Wraps a row.
	 *
	 * @param row
	 *            row to wrap.
	 */
	public ArrayList<String> wrapRow(String row) {
		String[] words = row.split(" ");
		int rowLength = getCurrentWidth();
		int i = 0;
		String currRow, tempRow = "";
		ArrayList<String> wrappedRow = new ArrayList<>();

		for (i = 0; i < words.length; i++) {
			currRow = tempRow;
			if (i > 0) {
				tempRow = tempRow.concat(" ");
			}
			tempRow = tempRow.concat(words[i]);
			int currLength = getLength(tempRow);

			if (currLength > rowLength) {
				if ("".equals(currRow)) {
					tempRow = wrapWord(tempRow, wrappedRow, rowLength);
				} else {
					wrappedRow.add(currRow);
					tempRow = words[i];
					if (getLength(tempRow) > rowLength) {
						tempRow = wrapWord(tempRow, wrappedRow, rowLength);
					}
				}
			}
		}

		wrappedRow.add(tempRow);
		return wrappedRow;
	}

	private int getLength(String txt) {
		GFont textFont = view.getApplication().getPlainFontCommon().deriveFont(GFont.PLAIN,
				view.getFontSize());
		GFontRenderContextW fontRenderContext = (GFontRenderContextW) view.getGraphicsForPen()
				.getFontRenderContext();
		return fontRenderContext.measureText(txt, ((GFontW) textFont).getFullFontString());
	}

	private String wrapWord(String word, ArrayList<String> wrappedRow, int rowLength) {
		String currWord = "";
		char nextChar = word.charAt(0);
		for (int i = 0; i < word.length(); i++) {
			currWord += nextChar;
			nextChar = word.charAt(i);
			if (getLength(currWord + nextChar) > rowLength) {
				wrappedRow.add(currWord);
				currWord = "";
			}
		}
		return currWord + nextChar;
	}

	@Override
	public void setEditorColor(GColor color) {
		if (editor == null) {
			return;
		}
		editor.setColor(color);
	}

	@Override
	public void onFocus(FocusEvent event) {
		DrawText d = (DrawText) view.getDrawableFor(text);
		if (d != null) {
			d.adjustBoundingBoxToText();
		}
	}

	@Override
	public void handleTextPressed() {
		lastText = getHit();

		if (lastText != null) {
			lastText.processEditMode();
		}
	}

	@Override
	public boolean handleTextReleased() {
		if (lastText != null && lastText.isEditMode()) {
			edit(lastText);
			lastText = null;
			return true;
		}
		return false;

	}

	@Override
	public GeoText getHit() {
		Hits ret = new Hits();
		if (view.getHits().containsGeoText(ret)) {
			return (GeoText) (ret.get(0));
		}
		return null;
	}

}