package org.geogebra.web.full.euclidian.inline;

import static com.google.gwt.dom.client.Style.Visibility.HIDDEN;
import static com.google.gwt.dom.client.Style.Visibility.VISIBLE;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.inline.InlineTableController;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.kernel.geos.properties.BorderType;
import org.geogebra.common.kernel.geos.properties.HorizontalAlignment;
import org.geogebra.common.kernel.geos.properties.VerticalAlignment;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.euclidian.FontLoader;
import org.geogebra.web.html5.util.CopyPasteW;
import org.geogebra.web.richtext.EditorChangeListener;
import org.geogebra.web.richtext.impl.Carota;
import org.geogebra.web.richtext.impl.CarotaTable;
import org.geogebra.web.richtext.impl.CarotaUtil;
import org.geogebra.web.richtext.impl.EventThrottle;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;

import elemental2.core.Global;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

public class InlineTableControllerW implements InlineTableController {

	private GeoInlineTable table;
	private final EuclidianView view;

	private Element tableElement;
	private Style style;

	private CarotaTable tableImpl;

	/**
	 *
	 * @param view view
	 * @param table editable table
	 */
	public InlineTableControllerW(GeoInlineTable table, EuclidianView view, Element parent) {
		this.table = table;
		this.view = view;
		CarotaUtil.ensureInitialized(view.getFontSize());
		if (view.getApplication().isMebis()) {
			CarotaUtil.setSelectionColor(GColor.MOW_SELECTION_COLOR.toString());
		}
		initTable(parent);
		if (table.getContent() != null) {
			checkFonts();
		}
	}

	private void checkFonts() {
		try {
			JSONObject tableData = new JSONObject(table.getContent());
			JSONArray tableContent = tableData.getJSONArray("content");
			for (int i = 0; i < tableContent.length(); i++) {
				JSONArray row = tableContent.getJSONArray(i);
				for (int j = 0; j < row.length(); j++) {
					JSONObject cell = row.getJSONObject(j);
					if (cell.has("content")) {
						InlineTextControllerW
								.checkFonts(cell.getJSONArray("content"), getCallback());
					}
				}
			}
		} catch (JSONException | RuntimeException e) {
			Log.debug("cannot parse fonts");
		}
	}

	@Override
	public void updateContent() {
		if (table.getContent() != null && !table.getContent().isEmpty()) {
			tableImpl.load(Global.JSON.parse(table.getContent()));
			updateSizes();
		}
	}

	@Override
	public void setBackgroundColor(GColor backgroundColor) {
		tableImpl.setCellProperty("bgcolor",
				backgroundColor == null ? null : backgroundColor.toString());
	}

	@Override
	public String urlByCoordinate(int x, int y) {
		return tableImpl.urlByCoordinate(x, y);
	}

	@Override
	public void update() {
		if (style != null && table.getLocation() != null) {
			GPoint2D location = table.getLocation();

			setLocation(view.toScreenCoordX(location.x),
					view.toScreenCoordY(location.y));

			setWidth(table.getWidth());
			setHeight(table.getHeight());

			setAngle(table.getAngle());
		}
	}

	@Override
	public boolean isInEditMode() {
		return VISIBLE.getCssName().equals(style.getVisibility());
	}

	@Override
	public boolean isSingleCellSelection() {
		return tableImpl.getSelection() != null && Js.isFalsy(tableImpl.getSelection().row1);
	}

	@Override
	public boolean hasSelection() {
		return tableImpl.getSelection() != null;
	}

	@Override
	public void draw(GGraphics2D g2, GAffineTransform transform) {
		if (!isInEditMode()) {
			g2.saveTransform();
			g2.transform(transform);
			tableImpl.draw(((GGraphics2DW) g2).getContext());
			g2.restoreTransform();
		}
	}

	@Override
	public void toForeground(int x, int y) {
		if (style != null) {
			style.setVisibility(VISIBLE);
			tableImpl.startEditing(x, y);
		}
	}

	@Override
	public void toBackground() {
		if (style != null) {
			style.setVisibility(HIDDEN);
			tableImpl.stopEditing();
			tableImpl.removeSelection();
		}
	}

	@Override
	public void format(String key, Object val) {
		tableImpl.setFormatting(key, val);
		table.setContent(getContent());
		table.updateVisualStyleRepaint(GProperty.COMBINED);
		if ("font".equals(key)) {
			FontLoader.loadFont(String.valueOf(val), getCallback());
		}
	}

	@Override
	public <T> T getFormat(String key, T fallback) {
		return tableImpl.getFormatting(key, fallback);
	}

	@Override
	public String getHyperLinkURL() {
		return tableImpl.getFormatting("url", "");
	}

	@Override
	public void setHyperlinkUrl(String url) {
		tableImpl.setHyperlinkUrl(url);
	}

	@Override
	public String getHyperlinkRangeText() {
		return tableImpl.hyperlinkRange().plainText();
	}

	@Override
	public void insertHyperlink(String url, String text) {
		tableImpl.insertHyperlink(url, text);
	}

	@Override
	public String getListStyle() {
		return tableImpl.getListStyle();
	}

	@Override
	public void switchListTo(String listType) {
		tableImpl.switchListTo(listType);
	}

	@Override
	public boolean copySelection() {
		String text = tableImpl.selectedRange().plainText();
		return CopyPasteW.writeToExternalClipboardIfNonempty(text);
	}

	@Override
	public void setSelectionText(String text) {
		tableImpl.insert(text);
	}

	@Override
	public void insertRowAbove() {
		tableImpl.insertRowAbove();
		updateSizes();
	}

	@Override
	public void insertRowBelow() {
		tableImpl.insertRowBelow();
		updateSizes();
	}

	@Override
	public void insertColumnLeft() {
		tableImpl.insertColumnLeft();
		updateSizes();
	}

	@Override
	public void insertColumnRight() {
		tableImpl.insertColumnRight();
		updateSizes();
	}

	@Override
	public void removeRow() {
		tableImpl.removeRow();
		updateSizes();
	}

	@Override
	public void removeColumn() {
		tableImpl.removeColumn();
		updateSizes();
	}

	@Override
	public void setBorderThickness(int borderThickness) {
		tableImpl.setBorderThickness(borderThickness);
		table.updateRepaint();
	}

	@Override
	public int getBorderThickness() {
		return tableImpl.getBorderThickness();
	}

	@Override
	public void setBorderStyle(BorderType borderType) {
		tableImpl.setBorderStyle(borderType.toString());
		table.updateRepaint();
	}

	@Override
	public BorderType getBorderStyle() {
		return BorderType.fromString(tableImpl.getBorderStyle());
	}

	@Override
	public void setWrapping(String setting) {
		tableImpl.setCellProperty("wrapping", setting);
		table.updateRepaint();
	}

	@Override
	public String getWrapping() {
		return tableImpl.getCellProperty("wrapping");
	}

	@Override
	public void setRotation(String setting) {
		tableImpl.setCellProperty("rotation", setting);
		table.updateRepaint();
	}

	@Override
	public String getRotation() {
		return tableImpl.getCellProperty("rotation");
	}

	@Override
	public VerticalAlignment getVerticalAlignment() {
		return VerticalAlignment.fromString(tableImpl.getCellProperty("valign"));
	}

	@Override
	public void setVerticalAlignment(VerticalAlignment alignment) {
		tableImpl.setCellProperty("valign", alignment.toString());
		table.updateRepaint();
	}

	@Override
	public HorizontalAlignment getHorizontalAlignment() {
		return HorizontalAlignment.fromString(tableImpl.getCellProperty("halign"));
	}

	@Override
	public void setHorizontalAlignment(HorizontalAlignment alignment) {
		tableImpl.setCellProperty("halign", alignment.toString(), null);
		table.updateRepaint();
	}

	@Override
	public void setHeading(GColor color, boolean isRow) {
		JsPropertyMap<Object> range = JsPropertyMap.of();
		range.set("col0", 0);
		range.set("row0", 0);

		if (isRow) {
			range.set("col1", tableImpl.getCols());
			range.set("row1", 1);
		} else {
			range.set("col1", 1);
			range.set("row1", tableImpl.getRows());
		}

		tableImpl.setCellProperty("bgcolor", color.toString(), range);
		tableImpl.setBorderThickness(3, range);
		table.updateRepaint();
	}

	@Override
	public void saveContent() {
		table.setContent(getContent());
	}

	@Override
	public void setLocation(int x, int y) {
		style.setLeft(x, Style.Unit.PX);
		style.setTop(y, Style.Unit.PX);
	}

	@Override
	public void setWidth(double width) {
		style.setWidth(width, Style.Unit.PX);
		tableImpl.setWidth(width);
	}

	@Override
	public void setHeight(double height) {
		style.setHeight(height, Style.Unit.PX);
		tableImpl.setHeight(height);
	}

	@Override
	public void setAngle(double angle) {
		style.setProperty("transform", "rotate(" + angle + "rad)");
	}

	@Override
	public void removeFromDom() {
		if (tableElement != null) {
			tableElement.removeFromParent();
		}
	}

	private void updateSizes() {
		table.setSize(tableImpl.getTotalWidth(), tableImpl.getTotalHeight());
		table.setMinWidth(tableImpl.getMinWidth());
		table.setMinHeight(tableImpl.getMinHeight());
		saveContent();
		table.updateRepaint();
	}

	private String getContent() {
		return Global.JSON.stringify(tableImpl.save());
	}

	private void initTable(Element parent) {
		tableElement = DOM.createDiv();
		tableElement.addClassName("mowWidget");
		parent.appendChild(tableElement);

		style = tableElement.getStyle();
		style.setProperty("transformOrigin", "0 0");
		style.setVisibility(HIDDEN);
		tableImpl = Carota.get().getTable().create(tableElement);
		tableImpl.init(2, 2);

		updateContent();
		new EventThrottle(tableImpl).setListener(new EditorChangeListener() {
			@Override
			public void onContentChanged(String content) {
				if (tableImpl.getTotalWidth() < 1 || tableImpl.getTotalHeight() < 1) {
					table.remove();
					view.getApplication().storeUndoInfo();
				} else {
					onEditorChanged(content);
				}
			}

			@Override
			public void onInput() {
				// not needed
			}

			@Override
			public void onSelectionChanged() {
				table.getKernel().notifyUpdateVisualStyle(table, GProperty.TEXT_SELECTION);
			}
		});
		tableImpl.sizeChanged(() -> changeContent(getContent()));
		update();
	}

	private void onEditorChanged(String content) {
		if (changeContent(content)) {
			view.getApplication().storeUndoInfo();
		}
	}

	private boolean changeContent(String content) {
		if (content.equals(table.getContent())) {
			return false;
		}

		table.setContent(content);
		table.notifyUpdate();
		return true;
	}

	private Runnable getCallback() {
		return () -> {
			tableImpl.stopEditing();
			tableImpl.reload();
			tableImpl.repaint();
			table.getKernel().notifyRepaint();
		};
	}
}
