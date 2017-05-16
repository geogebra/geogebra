/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.util.sliderPanel.SliderPanelW;
import org.geogebra.web.web.gui.layout.panels.AlgebraDockPanelW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * Slider item for Algebra View.
 * 
 * @author laszlo
 *
 */
public class SliderTreeItemRetex extends LatexTreeItem
		implements SliderTreeItemInterface {

	private static final int SLIDER_EXT = 15;


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


		@Override
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
	public SliderTreeItemRetex(final GeoElement geo0) {
		super(geo0);
		num = (GeoNumeric) geo;
		createSliderGUI();
		addControls();
		deferredResize();
	}

	@Override
	protected RadioTreeItemController createController() {
		return new SliderTreeItemRetexController(this);
	}

	private SliderTreeItemRetexController getSliderController() {
		return (SliderTreeItemRetexController) getController();
	}
	private void createSliderGUI() {
		content.addStyleName("noPadding");
		if (!num.isEuclidianVisible()) {
			num.initAlgebraSlider();
		}

		if (num.getIntervalMinObject() != null
				&& num.getIntervalMaxObject() != null) {
			boolean degree = geo.isGeoAngle()
					&& kernel.getAngleUnit() == Kernel.ANGLE_DEGREE;
			setSlider(new SliderPanelW(num.getIntervalMin(),
					num.getIntervalMax(), app.getKernel(), degree));
			updateColor();

			getSlider().setValue(num.getValue());

			getSlider().setStep(num.getAnimationStep());

			getSlider().addValueChangeHandler(getSliderController());


			sliderPanel = new FlowPanel();
			sliderPanel.add(getSlider());

			createMinMaxPanel();

			createSliderContent();
			styleContentPanel();

			addAVEXWidget(content);

			sliderContent.add(LayoutUtilW.panelRow(sliderPanel, minMaxPanel));
			main.add(sliderContent);
		}

	}


	/**
	 * resize slider to fit to the panel in a deferred way.
	 */
	@Override
	public void deferredResize() {
		if (getSlider() == null) {
			return;
		}
		Scheduler.get().scheduleDeferred(resizeCmd);
	}

	/** update size */
	void resize() {
		if (getSlider() == null) {
			return;
		}

		int width = getAV().getOffsetWidth() - 2 * marblePanel.getOffsetWidth()
				+ SLIDER_EXT;
		if (app.has(Feature.AV_PLAY_ONLY) && controls.getAnimPanel() != null) {
			width -= controls.getAnimPanel().getPlayButton().getOffsetWidth();
		}
		slider.setWidth(width);
	
	}

	private void createMinMaxPanel() {
		minMaxPanel = new MinMaxPanel(this);
		minMaxPanel.setVisible(false);
	}


	@Override
	protected void styleContentPanel() {

		sliderContent.removeStyleName("elemPanel");
		sliderContent.addStyleName("avItemContent");
		content.addStyleName("avSliderValue");

		sliderPanel.setVisible(true);
		controls.showAnimPanel(true);

	}

	private void updateColor() {
		getSlider().updateColor(geo.getAlgebraColor());
	}

	@Override
	protected void doUpdate() {
		setNeedsUpdate(false);
		marblePanel.update();
		controls.updateAnimPanel();

		getSlider().setScale(app.getArticleElement().getScaleX());
		double min = num.getIntervalMin();
		double max = num.getIntervalMax();
		boolean degree = geo.isGeoAngle()
				&& kernel.getAngleUnit() == Kernel.ANGLE_DEGREE;
		getSlider().setMinimum(min, degree);
		getSlider().setMaximum(max, degree);

		getSlider().setStep(num.getAnimationStep());
		getSlider().setValue(num.value);
		minMaxPanel.update();

		if (!getSlider().isAttached()) {
			sliderPanel.add(getSlider());
			styleContentPanel();
		}
		updateTextItems();
		updateColor();
	}

	//
	// static boolean isWidgetHit(Widget w, MouseEvent<?> evt) {
	// return isWidgetHit(w, evt.getClientX(), evt.getClientY());
	//
	// }


	@Override
	protected void addAVEXWidget(Widget w) {
		if (sliderPanel == null) {
			return;
		}
		sliderPanel.remove(getSlider());
		sliderContent.add(w);
		sliderPanel.add(getSlider());
	}



	@Override
	public void onResize() {
		deferredResize();
		super.onResize();
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
	public static SliderTreeItemRetex as(TreeItem item) {
		return (SliderTreeItemRetex) item;
	}

	@Override
	public boolean isInputTreeItem() {
		return false;
	}

	/**
	 * 
	 * @param geo
	 *            geo element
	 * @return if geo matches to SliderTreeItem.
	 */
	public static boolean match(GeoElement geo) {
		return geo instanceof GeoNumeric
				&& ((GeoNumeric) geo).isShowingExtendedAV() && geo.isSimple()
				&& MyDouble.isFinite(((GeoNumeric) geo).value);
	}

	@Override
	public void setSliderVisible(boolean visible) {
		sliderPanel.setVisible(visible);
	}


	@Override
	public void selectItem(boolean selected) {
		AlgebraDockPanelW dp = getAlgebraDockPanel();
		if (first && !dp.hasLongStyleBar()) {
			dp.showStyleBarPanel(!selected);
		}
		super.selectItem(selected);

	}

	@Override
	public void setAnimPanelVisible(boolean visible) {
		controls.showAnimPanel(visible);
	}

	@Override
	public void setFocus(boolean b, boolean sv) {
		// ignore
	}

	@Override
	public boolean isSliderItem() {
		return true;
	}

	/**
	 * @return slider
	 */
	public SliderPanelW getSlider() {
		return slider;
	}

	/**
	 * @param slider
	 *            slider
	 */
	public void setSlider(SliderPanelW slider) {
		this.slider = slider;
	}

	@Override
	public void expandSize(int width) {
		if (getAV().getOffsetWidth() < width) {
			getAV().expandWidth(width);
		}
	}

	@Override
	public void restoreSize() {
		getAV().restoreWidth(false);
	}

	/**
	 * @return number
	 */
	protected GeoNumeric getNum() {
		return num;
	}

	/**
	 * @return min/max panel
	 */
	public MinMaxPanel getMinMax() {
		return this.minMaxPanel;
	}

	@Override
	protected int getWidthForEdit() {
		return MinMaxPanel.MINMAX_MIN_WIDHT;

	}

	@Override
	public boolean onEditStart(boolean substituteNumbers) {
		if (minMaxPanel.isVisible()) {
			return false;
		}
		return super.onEditStart(substituteNumbers);
	}

}