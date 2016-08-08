/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.web.cas.latex;

import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.event.ZeroOffset;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.util.sliderPanel.SliderPanelW;
import org.geogebra.web.web.gui.view.algebra.MinMaxPanel;
import org.geogebra.web.web.gui.view.algebra.SliderTreeItemInterface;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * Slider item for Algebra View.
 * 
 * @author laszlo
 *
 */
public class SliderTreeItemMQ extends MathQuillTreeItem
		implements SliderTreeItemInterface {
	interface CancelListener {
		void cancel();
	}
	private static final int SLIDER_EXT = 15;
	private static final int DEFAULT_SLIDER_WIDTH = 100;

	/**
	 * Slider to be shown as part of the extended Slider entries
	 */
	private SliderPanelW slider;

	/**
	 * panel to correctly display an extended slider entry
	 */
	FlowPanel sliderPanel = null;

	/**
	 * panel to display animation related controls
	 */

	private ScheduledCommand resizeCmd = new ScheduledCommand() {


		public void execute() {
			resize();
		}
	};
	private MinMaxPanel minMaxPanel;
	private GeoNumeric num;

	/**
	 * Creates a SliderTreeItem for AV sliders
	 * 
	 * @param geo0
	 *            the existing GeoElement to display/edit
	 */
	public SliderTreeItemMQ(final GeoElement geo0) {
		super(geo0);
		num = (GeoNumeric) geo;
		createAnimPanel();
		createSliderGUI();
		addDomHandlers(main);
		deferredResize();
	}

	private void createSliderGUI() {
		ihtml.addStyleName("noPadding");
		if (!num.isEuclidianVisible()) {
			num.initAlgebraSlider();
		}

		if (num.getIntervalMinObject() != null
				&& num.getIntervalMaxObject() != null) {
			boolean degree = geo.isGeoAngle()
					&& kernel.getAngleUnit() == Kernel.ANGLE_DEGREE;
			slider = new SliderPanelW(num.getIntervalMin(),
					num.getIntervalMax(), app.getKernel(), degree);
			updateColor();

			slider.setValue(num.getValue());

			slider.setStep(num.getAnimationStep());

			slider.addValueChangeHandler(new ValueChangeHandler<Double>() {
				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					num.setValue(event.getValue());
					geo.updateCascade();


					if (!geo.isAnimating()) {
						if (isAnotherMinMaxOpen()) {
							MinMaxPanel.closeMinMaxPanel();
						}

						selectItem(true);
						updateSelection(false, false);
					}
					// updates other views (e.g. Euclidian)
					kernel.notifyRepaint();
				}
			});


			sliderPanel = new FlowPanel();
			sliderPanel.add(slider);

			createMinMaxPanel();

			createContentPanel();
			styleContentPanel();

			addAVEXWidget(ihtml);

			contentPanel.add(LayoutUtilW.panelRow(sliderPanel, minMaxPanel));
			main.add(contentPanel);
		}

	}

	/**
	 * resize slider to fit to the panel in a deferred way.
	 */
	public void deferredResize() {
		if (slider == null) {
			return;
		}
		Scheduler.get().scheduleDeferred(resizeCmd);
	}

	private void resize() {
		if (slider == null) {
			return;
		}

		int width = getAV().getOffsetWidth() - 2 * marblePanel.getOffsetWidth()
				+ SLIDER_EXT;
		slider.setWidth(width < DEFAULT_SLIDER_WIDTH ? DEFAULT_SLIDER_WIDTH
				: width);
	}

	private void createMinMaxPanel() {
		minMaxPanel = new MinMaxPanel(this);
		minMaxPanel.setVisible(false);
	}

	/**
	 * @return true if another SliderTreeItem's min/max panel is showing.
	 */
	boolean isAnotherMinMaxOpen() {
		return (MinMaxPanel.openedMinMaxPanel != null
				&& MinMaxPanel.openedMinMaxPanel != minMaxPanel);
	}

	private boolean isClickedOutMinMax(int x, int y) {
		return (MinMaxPanel.openedMinMaxPanel == minMaxPanel
				&& !isWidgetHit(minMaxPanel, x, y));
	}

	@Override
	protected void styleContentPanel() {

		contentPanel.removeStyleName("elemPanel");
		contentPanel.addStyleName("avItemContent");

		sliderPanel.setVisible(true);

		if (animPanel != null) {
			animPanel.setVisible(true);
		}
	}

	private void updateColor() {
		slider.updateColor(geo.getAlgebraColor());
	}

	@Override
	protected void doUpdate() {
		setNeedsUpdate(false);
		marblePanel.update();
		animPanel.update();

		slider.setScale(app.getArticleElement().getScaleX());
		double min = num.getIntervalMin();
		double max = num.getIntervalMax();
		boolean degree = geo.isGeoAngle()
				&& kernel.getAngleUnit() == Kernel.ANGLE_DEGREE;
		slider.setMinimum(min, degree);
		slider.setMaximum(max, degree);

		slider.setStep(num.getAnimationStep());
		slider.setValue(num.value);
		minMaxPanel.update();

		if (!slider.isAttached()) {
			sliderPanel.add(slider);
			styleContentPanel();
		}

		updateColor();
	}

	@Override
	protected void onPointerUp(AbstractEvent event) {
		selectionCtrl.setSelectHandled(false);
		if (minMaxPanel.isVisible()) {
			return;
		}
		super.onPointerUp(event);
	}

	@Override
	public void onMouseMove(MouseMoveEvent evt) {
		if (sliderPanel == null) {
			evt.stopPropagation();
			return;
		}
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		PointerEvent wrappedEvent = PointerEvent.wrapEvent(evt,
				ZeroOffset.instance);
		onPointerMove(wrappedEvent);
	}

	@Override
	public void onDoubleClick(DoubleClickEvent evt) {
		evt.stopPropagation();

		if ((isWidgetHit(animPanel, evt)
				|| (minMaxPanel != null && minMaxPanel.isVisible())
				|| isWidgetHit(marblePanel, evt)
		)) {
			return;
		}
		super.onDoubleClick(evt);
	}

	static boolean isWidgetHit(Widget w, MouseEvent<?> evt) {
		return isWidgetHit(w, evt.getClientX(), evt.getClientY());

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

	@Override
	protected boolean handleAVItem(int x, int y, boolean rightClick) {

		boolean minHit = sliderPanel != null
				&& isWidgetHit(slider.getWidget(0), x, y);
		boolean maxHit = sliderPanel != null
				&& isWidgetHit(slider.getWidget(2), x, y);
		// Min max panel should be closed
		if (isAnotherMinMaxOpen() || isClickedOutMinMax(x, y)) {
			MinMaxPanel.closeMinMaxPanel(!(minHit || maxHit));
		}

		if (isAnotherMinMaxOpen()) {
			selectItem(false);

		}

		if (minMaxPanel != null && minMaxPanel.isVisible()) {
			selectItem(true);
			return false;
		}

		if (sliderPanel != null && sliderPanel.isVisible() && !rightClick) {

			if (minHit || maxHit) {
				minMaxPanel.show();
				if (minHit) {
					minMaxPanel.setMinFocus();
				} else if (maxHit) {
					minMaxPanel.setMaxFocus();
				}

				return true;
			}
		}

		if (!selectionCtrl.isSelectHandled()) {
			selectItem(true);
		}

		return false;

	}

	void addAVEXWidget(Widget w) {
		if (sliderPanel == null) {
			return;
		}
		sliderPanel.remove(slider);
		contentPanel.add(w);
		sliderPanel.add(slider);
	}



	@Override
	public void onResize() {
		deferredResize();
		super.onResize();
	}



	/**
	 * Sets the currently open min/max panel of AV.
	 */
	public void setOpenedMinMaxPanel(MinMaxPanel panel) {
		MinMaxPanel.openedMinMaxPanel = panel;
	}

	@Override
	public void setDraggable() {
		// slider is not draggable from AV to EV.
	}

	/**
	 * cast method with no 'instanceof' check.
	 * 
	 * @param item
	 *            TreeItem to be casted
	 * @return Casted item to SliderTreeItem
	 */
	public static SliderTreeItemMQ as(TreeItem item) {
		return (SliderTreeItemMQ) item;
	}

	@Override
	public boolean isInputTreeItem() {
		return false;
	}


	public void setSliderVisible(boolean visible) {
		sliderPanel.setVisible(visible);
	}


	public void setAnimPanelVisible(boolean visible) {
		if (animPanel == null) {
			return;
		}
		animPanel.setVisible(visible);
	}


}