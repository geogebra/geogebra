package org.geogebra.web.full.gui.layout.panels;

import org.apache.commons.math3.util.Cloner;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelWAbstract.EuclidianPanel;
import org.geogebra.web.html5.euclidian.ReaderWidget;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ImageLoadCallback;
import org.geogebra.web.html5.util.ImageWrapper;
import org.geogebra.web.html5.util.sliderPanel.SliderW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class VoiceoverTabber {

	private Widget canvas;
	private AppW app;
	private double[] oldVal;

	/**
	 * @param app
	 *            app
	 * @param canvas
	 *            canvas
	 */
	public VoiceoverTabber(AppW app, Widget canvas) {
		this.app = app;
		this.canvas = canvas;
	}

	private Widget getCanvas() {
		return canvas;
	}

	/**
	 * Add dummy divs for handling focus change with swipe in VoiceOver.
	 * 
	 * @param p
	 *            euclidian panel
	 */
	public void add(final EuclidianPanel p) {
		if (getCanvas() != null) {
			getCanvas().addDomHandler(new FocusHandler() {
				@Override
				public void onFocus(FocusEvent event) {
					app.getAccessibilityManager().setTabOverGeos(true);
				}
			}, FocusEvent.getType());
		}
		final SliderW range = makeSlider(0);
		final SliderW rangeY = makeSlider(1);
		final SliderW rangeZ = makeSlider(2);
		final Label simpleButton = new Label("button");
		hide(simpleButton);
		simpleButton.getElement().setAttribute("role", "button");
		simpleButton.getElement().setTabIndex(5000);
		simpleButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Log.error("click");
				app.handleSpaceKey();
			}
		});
		HTML focusTrap = makeFocusTrap(false, range, rangeY, rangeZ,
				simpleButton);
		HTML focusTrapShift = makeFocusTrap(true, range, rangeY, rangeZ,
				simpleButton);

		p.add(focusTrapShift);
		p.add(range);
		p.add(rangeY);
		p.add(rangeZ);
		p.add(simpleButton);
		simpleButton.setVisible(false);
		p.add(focusTrap);
	}

	private void hide(Widget range) {
		range.getElement().getStyle().setOpacity(.01);
		range.getElement().getStyle().setPosition(Position.FIXED);
	}

	private SliderW makeSlider(final int index) {
		final SliderW range = new SliderW(0, 10);
		ReaderWidget.offscreen(range);
		range.getElement().addClassName("slider");
		range.addValueChangeHandler(new ValueChangeHandler<Double>() {

			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				GeoElement sel = app.getAccessibilityManager().getSelectedGeo();
				if (sel != null && sel.isGeoNumeric()) {
					((GeoNumeric) sel).setValue(event.getValue());
					((GeoNumeric) sel).updateRepaint();
					range.getElement().focus();
					updateValueText(range, (GeoNumeric) sel);
				}
				if (sel != null && sel.isGeoPoint()) {
					double step = range.getValue() - oldVal[index];
					app.getGlobalKeyDispatcher().handleArrowKeyMovement(
							app.getSelectionManager().getSelectedGeos(),
							index == 0 ? step : 0, index == 1 ? step : 0,
							index == 2 ? step : 0);
					oldVal[index] += step;
					updateValueText(range, range.getValue());
				}

			}
		});
		range.getElement().setTabIndex(5000);
		range.setVisible(false);

		range.addDomHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				updateSelection(range, index);
			}
		}, FocusEvent.getType());
		return range;
	}

	private HTML makeFocusTrap(final boolean backward, final SliderW range,
			final SliderW rangeY, final SliderW rangeZ,
			final Label simpleButton) {
		HTML focusTrap = new HTML(SafeHtmlUtils.fromTrustedString(
				"<div>select " + (backward ? "previous" : "next") + "</div>"));
		ReaderWidget.offscreen(focusTrap);

		focusTrap.getElement().setTabIndex(5000);

		ImageWrapper.nativeon(focusTrap.getElement(), "focus",
				new ImageLoadCallback() {
					@Override
					public void onLoad() {
						app.getAccessibilityManager().setTabOverGeos(true);
						app.getGlobalKeyDispatcher().handleTab(false, backward);
						if (app.getAccessibilityManager()
								.getSpaceAction() != null) {
							simpleButton.setText(app.getAccessibilityManager()
									.getSpaceAction());
							simpleButton.setVisible(true);
							range.setVisible(false);
							rangeY.setVisible(false);
							rangeZ.setVisible(false);
							forceFocus(simpleButton.getElement());
						} else {
							GeoElement sel = app.getAccessibilityManager()
									.getSelectedGeo();
							int dim = sel instanceof GeoPointND ? 2 : 1;
							if (sel != null && sel.isGeoElement3D()) {
								dim = 3;
							}
							simpleButton.setVisible(false);
							updateSelection(range, 0);
							range.setVisible(true);
							rangeY.setVisible(dim > 1);
							updateSelection(rangeY, 1);
							rangeZ.setVisible(dim > 2);
							updateSelection(rangeZ, 2);
							SliderW nextRange = range;
							if (backward && dim == 2) {
								nextRange = rangeY;
							}
							if (backward && dim == 3) {
								nextRange = rangeZ;
							}
							forceFocus(nextRange.getElement());

						}

					}
				});
		return focusTrap;
	}

	/**
	 * @param range
	 *            slider
	 * @param sel
	 *            selected number
	 */
	protected void updateValueText(SliderW range, GeoNumeric sel) {
		range.getElement().setAttribute("aria-valuetext",
				sel.toValueString(StringTemplate.screenReader));
	}

	protected void updateValueText(SliderW range, double sel) {
		range.getElement().setAttribute("aria-valuetext",
				app.getKernel().format(sel, StringTemplate.screenReader));
	}

	protected void forceFocus(final Element element) {
		// element.focus();
		app.invokeLater(new Runnable() {

			@Override
			public void run() {
				element.focus();
			}

		});
	}

	/**
	 * @param range
	 *            slider
	 */
	protected void updateSelection(SliderW range, int index) {
		GeoElement sel = app.getAccessibilityManager().getSelectedGeo();
		if (sel == null) {
			return;
		}
		if (sel.isGeoNumeric()) {
			range.setMinimum(((GeoNumeric) sel).getIntervalMin());
			range.setMaximum(((GeoNumeric) sel).getIntervalMax());
			range.setStep(((GeoNumeric) sel).getAnimationStep());
			range.setValue(((GeoNumeric) sel).getValue());
			updateValueText(range, (GeoNumeric) sel);
		}
		if (sel.isGeoPoint()) {
			String[] labels = { "x coordinate of", "y coordinate of",
					"z coordinate of" };
			AriaHelper.setLabel(range,
					labels[index] + sel.getNameDescription());
			range.setMinimum(app.getActiveEuclidianView().getXmin());
			range.setMaximum(app.getActiveEuclidianView().getXmax());
			range.setStep(sel.getAnimationStep());
			double coord = ((GeoPointND) sel).getInhomCoords().get(index + 1);
			this.oldVal = Cloner
					.clone(((GeoPointND) sel).getInhomCoords().get());
			range.setValue(coord);
			updateValueText(range, coord);
		} else {
			AriaHelper.setLabel(range, sel.getNameDescription());
		}

	}



}
