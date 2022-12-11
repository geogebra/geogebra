/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.web.full.gui.layout.panels.AlgebraPanelInterface;
import org.geogebra.web.html5.gui.util.ClickEndHandler;
import org.geogebra.web.html5.util.sliderPanel.SliderPanelW;
import org.gwtproject.core.client.Scheduler;

/**
 * Slider item for Algebra View.
 * 
 * @author laszlo
 *
 */
public class SliderTreeItemRetex extends RadioTreeItem {

	private static final int SLIDER_EXT = 15;

	/**
	 * Slider to be shown as part of the extended Slider entries
	 */
	private SliderPanelW slider;

	/**
	 * panel to display animation related controls
	 */

	private MinMaxPanel minMaxPanel;
	private GeoNumeric num;

	/**
	 * Creates a SliderTreeItem for AV sliders
	 * 
	 * @param geo0
	 *            the existing GeoElement to display/edit
	 */
	public SliderTreeItemRetex(final GeoElement geo0) {
		super(geo0.getKernel());
		geo = geo0;
		num = (GeoNumeric) geo;

		addMarble();

		getDefinitionValuePanel().addStyleName("avPlainText");
		getElement().getStyle().setColor("black");

		content.add(getDefinitionValuePanel());

		updateFont(getDefinitionValuePanel());
		createSliderGUI();
		addControls();
		styleContentPanel();
		doUpdate();
		deferredResize();
	}

	@Override
	protected LatexTreeItemController createController() {
		return new SliderTreeItemRetexController(this);
	}

	private SliderTreeItemRetexController getSliderController() {
		return (SliderTreeItemRetexController) getController();
	}

	private void createSliderGUI() {
		if (!num.isEuclidianVisible()) {
			num.initAlgebraSlider();
		}

		if (num.getIntervalMinObject() != null
				&& num.getIntervalMaxObject() != null) {
			boolean degree = geo.isGeoAngle()
					&& kernel.degreesMode();
			slider = new SliderPanelW(num.getIntervalMin(),
					num.getIntervalMax(), app.getKernel(), degree);

			getSlider().setValue(num.getValue());

			getSlider().setStep(num.getAnimationStep());

			getSlider().getSlider().addInputHandler(() ->
					getSliderController().onValueChange(getSlider().getValue()));
			ClickEndHandler.init(getSlider(), new ClickEndHandler() {
				@Override
				public void onClickEnd(int x, int y, PointerEventType type) {
					getSliderController().storeUndoInfoIfChanged();
				}
			});

			createMinMaxPanel();

			content.addStyleName("elemText");

			main.addStyleName("avSliderElem");
			main.add(content);
			main.add(getSlider());
			main.add(minMaxPanel);
		}
	}

	/**
	 * resize slider to fit to the panel in a deferred way.
	 */
	public void deferredResize() {
		Scheduler.get().scheduleDeferred(this::resize);
	}

	/** update size */
	void resize() {
		if (getSlider() == null) {
			return;
		}

		int width = getAV().getOffsetWidth() - 2 * marblePanel.getOffsetWidth()
				+ SLIDER_EXT;
		if (controls.getAnimPanel() != null) {
			width -= AnimPanel.PLAY_BUTTON_SIZE;
		}
		slider.setWidth(width);
	}

	private void createMinMaxPanel() {
		minMaxPanel = new MinMaxPanel(this);
		minMaxPanel.setVisible(false);
	}

	@Override
	protected void styleContentPanel() {
		getSlider().setVisible(true);
		controls.showAnimPanel(true);
	}

	@Override
	protected boolean mayNeedOutput() {
		return false;
	}

	@Override
	protected void doUpdate() {
		if (num == null) {
			// called from super constructor, ignore
			return;
		}
		setNeedsUpdate(false);
		if (typeChanged()) {
			getAV().remove(geo);
			getAV().add(geo, -1, false);
			return;
		}
		marblePanel.update();
		controls.updateAnimPanel();

		double min = num.getIntervalMin();
		double max = num.getIntervalMax();
		boolean degree = geo.isGeoAngle()
				&& kernel.degreesMode();
		getSlider().setMinimum(min, degree);
		getSlider().setMaximum(max, degree);

		getSlider().setStep(num.getAnimationStep());
		getSlider().setValue(num.value);
		minMaxPanel.update();
		resize();
		if (!getSlider().isAttached()) {
			styleContentPanel();
		}
		updateTextItems();
	}

	@Override
	public void onResize() {
		super.onResize();
		deferredResize();
	}

	@Override
	public void setDraggable() {
		// slider is not draggable from AV to EV.
	}

	@Override
	public boolean isInputTreeItem() {
		return false;
	}

	/**
	 * @param visible
	 *            whether to show slider
	 */
	public void setSliderVisible(boolean visible) {
		getSlider().setVisible(visible);
	}

	@Override
	public void selectItem(boolean selected) {
		if (!app.isUnbundled()) {
			AlgebraPanelInterface dp = getAlgebraDockPanel();
			if (first && !dp.hasLongStyleBar()) {
				dp.showStyleBarPanel(!selected);
			}
		}
		super.selectItem(selected);
	}

	/**
	 * @param visible
	 *            whether to show animation panel
	 */
	public void setAnimPanelVisible(boolean visible) {
		controls.showAnimPanel(visible);
	}

	@Override
	public void setFocus(boolean b) {
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
	 * @param width
	 *            new width
	 */
	public void expandSize(int width) {
		getAV().expandWidth(width);
	}

	/**
	 * Restore last width
	 */
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
	public boolean onEditStart() {
		if (minMaxPanel.isVisible()) {
			return false;
		}
		return super.onEditStart();
	}

	@Override
	public void onStopEdit() {
		// nothing to do here
	}
}
