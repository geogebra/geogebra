package org.geogebra.web.html5.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianStatic;
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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class TextControllerW
		implements TextController, KeyDownHandler, BlurHandler {
	private MowTextEditor editor;
	private AppW app;

	/** GeoText to edit */
	private GeoText text;

	/**
	 * Constructor.
	 *
	 * @param app
	 *            the application.
	 */
	public TextControllerW(AppW app) {
		this.app = app;
	}

	private EuclidianViewW getView() {
		return (EuclidianViewW) (app.getActiveEuclidianView());
	}

	private void createGUI() {
		editor = new MowTextEditor();
		AbsolutePanel evPanel = getView().getAbsolutePanel();
		evPanel.add(editor);
		editor.addKeyDownHandler(this);
		editor.addBlurHandler(this);
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		updateBoundingBox();
	}

	@Override
	public void onBlur(BlurEvent event) {
		if (!app.getSelectionManager().containsSelectedGeo(text)) {
			stopEditing();
		}
	}

	@Override
	public void stopEditing() {
		if (editor == null || !editor.isVisible()) {
			return;
		}
		editor.hide();
		String content = editor.getText();
		if (!StringUtil.empty(content)) {
			text.cancelEditMode();
			text.setTextString(content);
			text.update();
			text.updateRepaint();
		} else {
			text.remove();
		}
		text = null;
	}

	private void updateEditor(GFont font, int x, int y, int width, int height) {
		if (editor == null) {
			createGUI();
		}
		editor.setFont(font);
		editor.setLineHeight(font.getSize() * 1.5);
		editor.setColor(text.getObjectColor());
		editor.setText(text.getText().getTextString());
		editor.setPosition(x, y);
		editor.setWidth(width);
		editor.setHeight(height);
	}

	@Override
	public void resizeEditor(int width, int height) {
		editor.setWidth(width);
		editor.setHeight(height);
	}

	@Override
	public GeoText createText(GeoPointND loc) {
		GeoText t = app.getKernel().getAlgebraProcessor().text("");
		t.setEuclidianVisible(true);
		t.setAbsoluteScreenLocActive(false);
		// always use RW coords, ignore rw argument
		Coords coords = loc.getInhomCoordsInD3();
		t.setRealWorldLoc(getView().toRealWorldCoordX(coords.getX()),
				getView().toRealWorldCoordY(coords.getY()));
		t.setLabel(null);
		edit(t);
		return t;
	}

	@Override
	public void edit(final GeoText geo) {
		if (geo.isLaTeX() || !geo.isIndependent()) {
			return;
		}

		this.text = geo;
		text.update();
		text.setEditMode();

		updatePosition(geo);
		editor.show();
		editor.requestFocus();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				DrawText d = getDrawText(text);
				d.adjustBoundingBoxToText(getEditorBounds());

				getView().getEuclidianController().selectAndShowBoundingBox(geo);

				getView().repaint();
				editor.requestFocus();
			}
		});
	}

	@Override
	public boolean handleTextPressed(GeoText text, int x, int y, boolean dragged) {
		if (text != null && this.text == text && !dragged) {
			edit(text);
			moveCursor(x, y);
			return true;
		} else {
			stopEditing();
			this.text = text;
			return false;
		}
	}

	private void moveCursor(final int x, final int y) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				editor.requestFocus();
				editor.moveCursor(x, y + getView().getAbsoluteTop());
			}
		});
	}

	private DrawText getDrawText(GeoText geo) {
		return (DrawText) getView().getDrawableFor(geo);
	}

	private void updatePosition(GeoText geo) {
		DrawText d = getDrawText(geo);
		if (d != null) {
			int x = d.xLabel - EuclidianStatic.EDITOR_MARGIN;
			int y = d.yLabel - d.getFontSize()
					- EuclidianStatic.EDITOR_MARGIN;
			int width = (int) d.getBounds().getWidth()
					- 2 * EuclidianStatic.EDITOR_MARGIN;
			int height = (int) d.getBounds().getHeight()
					- 2 * EuclidianStatic.EDITOR_MARGIN;
			updateEditor(d.getTextFont(), x, y, width, height);
		}
	}

	private void updateBoundingBox() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				doUpdateBoundingBox();
			}
		});
	}

	/**
	 * Update bounding box immediately
	 */
	private void doUpdateBoundingBox() {
		DrawText d = getDrawText(text);
		if (d != null) {
			d.adjustBoundingBoxToText(getEditorBounds());
			getView().setBoundingBox(d.getBoundingBox());
		}
		getView().repaint();
	}

	@Override
	public GRectangle getEditorBounds() {
		if (editor == null) {
			return null;
		}
		return editor.getBounds(getView().getAbsoluteTop());
	}

	@Override
	public String wrapText(String txt, DrawText d, GRectangle bounds) {
		String editText = txt.replace("&nbsp;", " ");
		String[] rows = editText.split("\n");
		ArrayList<String> wrappedRows = new ArrayList<>();
		for (int i = 0; i < rows.length; i++) {
			wrappedRows.addAll(wrapRow(rows[i], d, bounds));
		}
		return StringUtil.join("\n", wrappedRows);
	}

	private static int getCurrentWidth(GRectangle bounds) {
		if (bounds == null) {
			return DrawText.MIN_EDITOR_WIDTH;
		}
		return (int) bounds.getWidth();
	}

	/**
	 * Wraps a row.
	 *
	 * @param row
	 *            row to wrap.
	 * @param drawText
	 *            drawable
	 * @return list of short rows
	 */
	public ArrayList<String> wrapRow(String row, DrawText drawText, GRectangle bounds) {
		String[] words = row.split(" ");
		int rowLength = getCurrentWidth(bounds) - 2 * EuclidianStatic.EDITOR_MARGIN;
		int i = 0;
		String currRow, tempRow = "";
		ArrayList<String> wrappedRow = new ArrayList<>();
		GeoText txt = (GeoText) drawText.getGeoElement();
		GFont font = app.getFontCanDisplay(txt.getTextString(),
				txt.isSerifFont(), txt.getFontStyle(), drawText.getFontSize());
		for (i = 0; i < words.length; i++) {
			currRow = tempRow;
			if (i > 0) {
				tempRow = tempRow.concat(" ");
			}
			tempRow = tempRow.concat(words[i]);
			int currLength = getLength(tempRow, font);

			if (currLength > rowLength) {
				if ("".equals(currRow)) {
					tempRow = wrapWord(tempRow, wrappedRow, rowLength, font);
				} else {
					wrappedRow.add(currRow);
					tempRow = words[i];
					if (getLength(tempRow, font) > rowLength) {
						tempRow = wrapWord(tempRow, wrappedRow, rowLength,
								font);
					}
				}
			}
		}

		wrappedRow.add(tempRow);
		return wrappedRow;
	}

	private int getLength(String txt, GFont font) {
		GFontRenderContextW fontRenderContext = (GFontRenderContextW) getView().getGraphicsForPen()
				.getFontRenderContext();
		return fontRenderContext.measureText(txt,
				((GFontW) font).getFullFontString());
	}

	private String wrapWord(String word, ArrayList<String> wrappedRow,
			int rowLength, GFont font) {
		if (word.length() < 1) {
			return "";
		}
		String currWord = "";
		char nextChar = word.charAt(0);
		for (int i = 1; i < word.length(); i++) {
			currWord += nextChar;
			nextChar = word.charAt(i);
			if (getLength(currWord + nextChar, font) > rowLength) {
				wrappedRow.add(currWord);
				currWord = "";
			}
		}
		return currWord + nextChar;
	}

	@Override
	public GeoText getHit() {
		Hits ret = new Hits();
		if (getView().getHits().containsGeoText(ret)) {
			return (GeoText) (ret.get(0));
		}
		return null;
	}

	@Override
	public void reset() {
		if (editor == null) {
			return;
		}
		stopEditing();
		editor.removeFromParent();
		editor = null;
	}

	/**
	 * Update position of editor and bounding box.
	 */
	public void updateEditorPosition() {
		if (text != null && text.isEditMode()) {
			updatePosition(text);
			doUpdateBoundingBox();
		}
	}
}