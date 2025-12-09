/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.gwtproject.event.dom.client.DoubleClickEvent;
import org.gwtproject.event.dom.client.MouseDownEvent;
import org.gwtproject.event.dom.client.MouseEvent;
import org.gwtproject.event.dom.client.MouseMoveEvent;
import org.gwtproject.timer.client.Timer;
import org.gwtproject.user.client.ui.Widget;

/**
 * Controller for slider items in AV that use RETEX editor.
 *
 * @author laszlo
 *
 */
public class SliderTreeItemRetexController extends LatexTreeItemController {

	private final SliderTreeItemRetex slider;
	private boolean hasUnsavedChanges;
	private Timer debounce;
	private Double targetValue;

	/**
	 * @param item
	 *            parent item
	 */
	public SliderTreeItemRetexController(SliderTreeItemRetex item) {
		super(item);
		slider = item;
	}

	@Override
	protected void onPointerUp(AbstractEvent event) {
		selectionCtrl.setSelectHandled(false);
		if (slider.getMinMax().isVisible()) {
			return;
		}
		super.onPointerUp(event);
	}

	@Override
	public void onMouseMove(MouseMoveEvent evt) {
		CancelEventTimer.cancelMouseEvent();
	}

	@Override
	public void onDoubleClick(DoubleClickEvent evt) {
		evt.stopPropagation();

		if (isWidgetHit(slider.controls.getAnimPanel(), evt)
				|| (slider.getMinMax() != null
						&& slider.getMinMax().isVisible())
				|| checkMarbleHit(evt)) {
			return;
		}
		super.onDoubleClick(evt);
	}

	@Override
	protected boolean handleAVItem(int x, int y, boolean rightClick) {
		slider.expandSize(slider.getWidthForEdit());

		boolean minHit = isWidgetHit(slider.getSlider().getWidget(0), x, y);
		boolean maxHit = isWidgetHit(slider.getSlider().getWidget(2), x, y);
		// Min max panel should be closed
		if (isAnotherMinMaxOpen() || isClickedOutMinMax(x, y)) {
			MinMaxPanel.closeMinMaxPanel(!(minHit || maxHit));
		}

		if (isAnotherMinMaxOpen()) {
			slider.selectItem(false);

		}

		if (slider.getMinMax() != null && slider.getMinMax().isVisible()) {
			slider.selectItem(true);
			return false;
		}

		if (slider.isVisible() && !rightClick) {

			if (minHit || maxHit) {
				handleMinMaxHit(minHit);
				return true;
			}
		}

		if (!selectionCtrl.isSelectHandled()) {
			slider.selectItem(true);
		}

		return false;

	}

	private void handleMinMaxHit(boolean minHit) {
		stopEdit();
		slider.getMinMax().show();
		if (minHit) {
			slider.getMinMax().setMinFocus();
		} else {
			slider.getMinMax().setMaxFocus();
		}
		getApp().getKernel().notifyRepaint();

	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		if (handleAVItem(event)) {
			event.stopPropagation();
			return;
		}
		super.onMouseDown(event);
	}

	@Override
	protected boolean canEditStart(MouseEvent<?> event) {

		return super.canEditStart(event)
				&& isWidgetHit(item.getDefinitionValuePanel(), event);
	}

	private static boolean isWidgetHit(Widget w, int x, int y) {
		if (w == null) {
			return false;
		}
		int left = w.getAbsoluteLeft();
		int top = w.getAbsoluteTop();
		int right = left + w.getOffsetWidth();
		int bottom = top + w.getOffsetHeight();

		return x > left && x < right && y > top && y < bottom;
	}

	/**
	 * @return true if another SliderTreeItem's min/max panel is showing.
	 */
	boolean isAnotherMinMaxOpen() {
		return !MinMaxPanel.isOpenedPanel(null)
				&& !MinMaxPanel.isOpenedPanel(slider.getMinMax());
	}

	private boolean isClickedOutMinMax(int x, int y) {
		return MinMaxPanel.isOpenedPanel(slider.getMinMax())
				&& !isWidgetHit(slider.getMinMax(), x, y);
	}

	/**
	 * Handle slider drag
	 * @param val slider value
	 */
	public void onValueChange(double val) {
		if (debounce != null && debounce.isRunning()) {
			targetValue = val;
			return;
		}
		targetValue = Double.NaN;
		getDebounceTimer().schedule(30);
		slider.expandSize(slider.getWidthForEdit());
		if (isEditing()) {
			stopEdit();
		}

		GeoNumeric numeric = slider.getNum();
		numeric.setValue(val);
		numeric.updateCascade();
		hasUnsavedChanges = true;
		if (!numeric.isAnimating()) {
			if (isAnotherMinMaxOpen()) {
				MinMaxPanel.closeMinMaxPanel();
			}

			slider.selectItem(true);
			updateSelection(false, false);
		}
		// updates other views (e.g. Euclidian)
		getApp().getKernel().notifyRepaint();
	}

	private Timer getDebounceTimer() {
		if (debounce == null) {
			debounce = new Timer() {
				@Override
				public void run() {
					if (!Double.isNaN(targetValue)) {
						onValueChange(targetValue);
					}
				}
			};
		}
		return debounce;
	}

	/**
	 * If the slider moved since last undo point, store changes.
	 */
	public void storeUndoInfoIfChanged() {
		if (hasUnsavedChanges) {
			app.storeUndoInfo();
			hasUnsavedChanges = false;
		}
	}
}
