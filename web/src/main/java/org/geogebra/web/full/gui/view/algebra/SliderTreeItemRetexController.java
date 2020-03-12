package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.event.ZeroOffset;
import org.geogebra.web.html5.gui.util.CancelEventTimer;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;

/**
 * Controller for slider items in AV that use RETEX editor.
 *
 * @author laszlo
 *
 */
public class SliderTreeItemRetexController extends LatexTreeItemController
		implements ValueChangeHandler<Double> {

	private SliderTreeItemRetex slider;

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
		if (slider.sliderPanel == null) {
			evt.stopPropagation();
			return;
		}
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		PointerEvent wrappedEvent = PointerEvent.wrapEvent(evt,
				ZeroOffset.INSTANCE);
		onPointerMove(wrappedEvent);
	}

	@Override
	public void onDoubleClick(DoubleClickEvent evt) {
		evt.stopPropagation();

		if ((isWidgetHit(slider.controls.getAnimPanel(), evt)
				|| (slider.getMinMax() != null
						&& slider.getMinMax().isVisible())
				|| isMarbleHit(evt))) {
			return;
		}
		super.onDoubleClick(evt);
	}

	@Override
	protected boolean handleAVItem(int x, int y, boolean rightClick) {

		slider.setForceControls(true);
		slider.expandSize(slider.getWidthForEdit());

		boolean minHit = slider.sliderPanel != null
				&& isWidgetHit(slider.getSlider().getWidget(0), x, y);
		boolean maxHit = slider.sliderPanel != null
				&& isWidgetHit(slider.getSlider().getWidget(2), x, y);
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

		if (slider.sliderPanel != null && slider.sliderPanel.isVisible()
				&& !rightClick) {

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

		return (x > left && x < right && y > top && y < bottom);
	}

	/**
	 * @return true if another SliderTreeItem's min/max panel is showing.
	 */
	boolean isAnotherMinMaxOpen() {
		return (MinMaxPanel.getOpenedPanel() != null
				&& MinMaxPanel.getOpenedPanel() != slider.getMinMax());
	}

	private boolean isClickedOutMinMax(int x, int y) {
		return (MinMaxPanel.getOpenedPanel() == slider.getMinMax()
				&& !isWidgetHit(slider.getMinMax(), x, y));
	}

	@Override
	public void onValueChange(ValueChangeEvent<Double> event) {
		slider.expandSize(slider.getWidthForEdit());
		if (isEditing()) {
			stopEdit();
		}

		slider.getNum().setValue(event.getValue());
		slider.geo.updateCascade();

		if (!slider.geo.isAnimating()) {
			if (isAnotherMinMaxOpen()) {
				MinMaxPanel.closeMinMaxPanel();
			}

			slider.selectItem(true);
			updateSelection(false, false);
		}
		// updates other views (e.g. Euclidian)
		getApp().getKernel().notifyRepaint();
	}

}
