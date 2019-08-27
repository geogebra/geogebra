package org.geogebra.web.html5.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.TextController;
import org.geogebra.common.euclidian.draw.DrawText;
import org.geogebra.common.kernel.ModeSetter;
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
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * Handling text editor in Euclidian getView().
 *
 * @author laszlo
 *
 */
public class TextControllerW
		implements TextController, FocusHandler, BlurHandler, KeyDownHandler {
	private MowTextEditor editor;
	private AppW app;

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
	}

	private EuclidianViewW getView() {
		return (EuclidianViewW) (app.getActiveEuclidianView());
	}

	private void createGUI() {
		editor = new MowTextEditor();
		AbsolutePanel evPanel = getView().getAbsolutePanel();
		evPanel.add(editor);
		editor.addKeyDownHandler(this);
		editor.addFocusHandler(this);
		editor.addBlurHandler(this);
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		updateBoundingBox();
	}

	@Override
	public void onBlur(BlurEvent event) {
		stopEditing();
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
		if (loc == null) {
			return null;
		}
		GeoText t = app.getKernel().getAlgebraProcessor().text("");
		app.getSelectionManager().addSelectedGeo(t);
		t.setEuclidianVisible(true);
		t.setAbsoluteScreenLocActive(false);
		// always use RW coords, ignore rw argument
		Coords coords = loc.getInhomCoordsInD3();
		t.setRealWorldLoc(getView().toRealWorldCoordX(coords.getX()),
				getView().toRealWorldCoordY(coords.getY()));
		t.setAbsoluteScreenLocActive(false);
		t.setLabel(null);
		edit(t, true);
		app.getKernel().notifyRepaint();
		app.setMode(EuclidianConstants.MODE_SELECT_MOW, ModeSetter.DOCK_PANEL);
		return t;
	}

	@Override
	public void edit(GeoText geo) {
		edit(geo, false);
	}

	private DrawText getDrawText(GeoText geo) {
		return (DrawText) getView().getDrawableFor(geo);
	}

	private void edit(GeoText geo, boolean create) {
		if (geo.isLaTeX() || !geo.isIndependent()) {
			return;
		}
		geo.setEditMode();
		this.text = geo;
		text.update();

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
			if (create) {
				updateBoundingBox();
			}
		}
		editor.show();
		editor.requestFocus();
		getView().repaint();
	}

	/**
	 * Update bounding box and repaint.
	 */
	void doUpdateBoundingBox() {
		DrawText d = getDrawText(text);
		if (d != null) {
			d.adjustBoundingBoxToText(getEditorBounds());
			getView().setBoundingBox(d.getBoundingBox());
		}
		getView().repaint();
	}

	private void updateBoundingBox() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				doUpdateBoundingBox();
			}
		});
	}

	@Override
	public GRectangle getEditorBounds() {
		if (editor == null) {
			return null;
		}
		return editor.getBounds(getView().getAbsoluteTop());
	}

	@Override
	public String wrapText(String txt, DrawText d) {
		String editText = txt.replace("&nbsp;", " ");
		String[] rows = editText.split("\n");
		ArrayList<String> wrappedRows = new ArrayList<>();
		for (int i = 0; i < rows.length; i++) {
			wrappedRows.addAll(wrapRow(rows[i], d));
		}
		return StringUtil.join("\n", wrappedRows);
	}

	private static int getCurrentWidth(DrawText d) {
		if (d == null || d.getBounds() == null) {
			return DrawText.MIN_EDITOR_WIDTH;
		}
		return (int) d.getBounds().getWidth();
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
	public ArrayList<String> wrapRow(String row, DrawText drawText) {
		String[] words = row.split(" ");
		int rowLength = getCurrentWidth(drawText) - 2 * EuclidianStatic.EDITOR_MARGIN;
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
	public void onFocus(FocusEvent event) {
		//do nothing
	}

	@Override
	public void handleTextPressed() {
		if (!isTextHandlingMode()) {
			return;
		}
		lastText = getHit();
	}

	@Override
	public boolean handleTextReleased(boolean drag, int x, int y) {
		if (!isTextHandlingMode()) {
			return false;
		}

		if (lastText != null) {
			if (drag) {
				lastText.setReadyToEdit();
				return true;
			}

			if (app.getMode() == EuclidianConstants.MODE_MEDIA_TEXT) {
				lastText.setEditMode();
				edit(lastText, true);
			} else {
				lastText.processEditMode();
				if (lastText.isEditMode()) {
					edit(lastText);
					editor.moveCursor(x, y);
				}
			}
			lastText = null;
			return true;
		}
		return false;
	}

	private boolean isTextHandlingMode() {
		int mode = app.getMode();
		return (mode == EuclidianConstants.MODE_MEDIA_TEXT || mode == EuclidianConstants.MODE_SELECT
				|| mode == EuclidianConstants.MODE_SELECT_MOW);
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
	public boolean isEditing() {
		return lastText != null && lastText.isEditMode();
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
}