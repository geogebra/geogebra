/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.web.cas.latex;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.util.sliderPanel.SliderPanelW;
import org.geogebra.web.web.gui.view.algebra.MinMaxPanel;
import org.geogebra.web.web.gui.view.algebra.RadioTreeItemController;
import org.geogebra.web.web.gui.view.algebra.SliderTreeItemInterface;
import org.geogebra.web.web.gui.view.algebra.SliderTreeItemMQController;

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
	private FlowPanel sliderPanel = null;

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
		content.removeStyleName("mathQuillEditor");
		setNum((GeoNumeric) geo);

		addControls();

		createSliderGUI();
		deferredResize();
	}

	@Override
	protected RadioTreeItemController createController() {
		return new SliderTreeItemMQController(this);
	}

	private SliderTreeItemMQController getSliderController() {
		return (SliderTreeItemMQController) getController();
	}

	private void createSliderGUI() {
		content.addStyleName("noPadding");
		if (!getNum().isEuclidianVisible()) {
			getNum().initAlgebraSlider();
		}

		if (getNum().getIntervalMinObject() != null
				&& getNum().getIntervalMaxObject() != null) {
			boolean degree = geo.isGeoAngle()
					&& kernel.getAngleUnit() == Kernel.ANGLE_DEGREE;
			slider = new SliderPanelW(getNum().getIntervalMin(),
					getNum().getIntervalMax(), app.getKernel(), degree);
			updateColor();

			slider.setValue(getNum().getValue());

			slider.setStep(getNum().getAnimationStep());

			slider.addValueChangeHandler(getSliderController());


			setSliderPanel(new FlowPanel());
			getSliderPanel().add(slider);

			createMinMaxPanel();

			createSliderContent();
			styleContentPanel();

			addAVEXWidget(content);

			sliderContent.add(LayoutUtilW.panelRow(getSliderPanel(), getMinMaxPanel()));
			main.add(sliderContent);
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
		setMinMaxPanel(new MinMaxPanel(this));
		getMinMaxPanel().setVisible(false);
	}


	@Override
	protected void styleContentPanel() {

		sliderContent.removeStyleName("elemPanel");
		sliderContent.addStyleName("avItemContent");

		getSliderPanel().setVisible(true);

		controls.showAnimPanel();
	}

	private void updateColor() {
		slider.updateColor(geo.getAlgebraColor());
	}

	@Override
	protected void doUpdate() {
		setNeedsUpdate(false);
		marblePanel.update();
		controls.updateAnimPanel();

		slider.setScale(app.getArticleElement().getScaleX());
		double min = getNum().getIntervalMin();
		double max = getNum().getIntervalMax();
		boolean degree = geo.isGeoAngle()
				&& kernel.getAngleUnit() == Kernel.ANGLE_DEGREE;
		slider.setMinimum(min, degree);
		slider.setMaximum(max, degree);

		slider.setStep(getNum().getAnimationStep());
		slider.setValue(getNum().value);
		getMinMaxPanel().update();

		if (!slider.isAttached()) {
			getSliderPanel().add(slider);
			styleContentPanel();
		}
		updateTextItems();
		updateColor();
	}


	void addAVEXWidget(Widget w) {
		if (getSliderPanel() == null) {
			return;
		}
		getSliderPanel().remove(slider);
		sliderContent.add(w);
		getSliderPanel().add(slider);
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
	public static SliderTreeItemMQ as(TreeItem item) {
		return (SliderTreeItemMQ) item;
	}

	@Override
	public boolean isInputTreeItem() {
		return false;
	}


	public void setSliderVisible(boolean visible) {
		getSliderPanel().setVisible(visible);
	}


	public void setAnimPanelVisible(boolean visible) {
		controls.showAnimPanel(visible);
	}

	@Override
	public boolean isSliderItem() {
		return true;
	}

	public MinMaxPanel getMinMaxPanel() {
		return minMaxPanel;
	}

	public void setMinMaxPanel(MinMaxPanel minMaxPanel) {
		this.minMaxPanel = minMaxPanel;
	}

	public FlowPanel getSliderPanel() {
		return sliderPanel;
	}

	public void setSliderPanel(FlowPanel sliderPanel) {
		this.sliderPanel = sliderPanel;
	}

	public GeoNumeric getNum() {
		return num;
	}

	public void setNum(GeoNumeric num) {
		this.num = num;
	}

	public SliderPanelW getSlider() {
		return slider;
	}

}