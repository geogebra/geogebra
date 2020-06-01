package org.geogebra.web.full.euclidian.inline;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.inline.InlineTableController;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.richtext.impl.Carota;
import org.geogebra.web.richtext.impl.CarotaTable;
import org.geogebra.web.richtext.impl.CarotaUtil;
import org.geogebra.web.richtext.impl.EditorCallback;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;

import elemental2.core.Global;

public class InlineTableControllerW implements InlineTableController {

	private static final int CELL_HEIGHT = 36;
	private static final int CELL_WIDTH = 100;

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
		initTable(parent);
	}

	@Override
	public void updateContent() {
		if (table.getContent() != null && !table.getContent().isEmpty()) {
			tableImpl.load(Global.JSON.parse(table.getContent()));
		}
	}

	@Override
	public void update() {
		if (style != null && table.getLocation() != null) {
			GPoint2D location = table.getLocation();

			setLocation(view.toScreenCoordX(location.x),
					view.toScreenCoordY(location.y));

			setWidth(2 * CELL_WIDTH + 3);
			setHeight(2 * CELL_HEIGHT + 3);

			setAngle(table.getAngle());
		}
	}

	@Override
	public void draw(GGraphics2D g2, GAffineTransform transform) {
		if (!"visible".equals(style.getVisibility())) {
			g2.saveTransform();
			g2.transform(transform);
			tableImpl.draw(((GGraphics2DW) g2).getContext());
			g2.restoreTransform();
		}
	}

	@Override
	public void toForeground(int x, int y) {
		if (style != null) {
			style.setVisibility(Style.Visibility.VISIBLE);
			tableImpl.startEditing(x, y);
		}
	}

	@Override
	public void toBackground() {
		if (style != null) {
			style.setVisibility(Style.Visibility.HIDDEN);
			tableImpl.stopEditing();
			tableImpl.removeSelection();
		}
	}

	@Override
	public void format(String key, Object val) {
		//CarotaRange selection = editor.selectedRange();
		//CarotaRange range = selection.getStart() == selection.getEnd() ? editor.documentRange()
		//		: selection;
		//range.setFormatting(key, val);
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
	public void removeFromDom() {
		if (tableElement != null) {
			tableElement.removeFromParent();
		}
	}

	private void initTable(Element parent) {
		tableElement = DOM.createDiv();
		tableElement.addClassName("mowWidget");
		parent.appendChild(tableElement);

		EditorCallback callback = () -> {
			table.setContent(Global.JSON.stringify(tableImpl.save()));
			view.getApplication().storeUndoInfo();
		};

		style = tableElement.getStyle();
		style.setProperty("transformOrigin", "0 0");
		tableImpl = Carota.get().getTable().create(tableElement);
		tableImpl.init(2, 2);

		updateContent();
		tableImpl.contentChanged(callback);

		update();
	}
}
