package org.geogebra.web.full.gui.layout.panels;

import org.apache.commons.math3.util.Cloner;
import org.geogebra.common.gui.AccessibilityManagerNoGui;
import org.geogebra.common.gui.SliderInput;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelWAbstract.EuclidianPanel;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ImageLoadCallback;
import org.geogebra.web.html5.util.ImageWrapper;
import org.geogebra.web.html5.util.sliderPanel.SliderW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Set of hidden widgets for navigating the construction with Voiceover (iOS)
 * 
 * @author Zbynek
 */
public class VirtualTabber implements ClickHandler {

	private AppW app;
	private double[] oldVal;
	private HTML focusTrap;
	private HTML focusTrapShift;
	private SliderW[] ranges;
	private Label hiddenButton;
	private HandlerRegistration focusHandler;

	/**
	 * @param app
	 *            app
	 */
	public VirtualTabber(AppW app) {
		this.app = app;
		this.ranges = new SliderW[3];
		buildUI();
	}

	private void buildUI() {
		for (int i = 0; i < ranges.length; i++) {
			ranges[i] = makeSlider(i);
		}
		hiddenButton = new Label("button");
		hideButton(hiddenButton);
		setRole(hiddenButton, "button");
		hiddenButton.getElement().setTabIndex(5000);
		hiddenButton.addClickHandler(this);
		focusTrap = makeFocusTrap(false);
		focusTrapShift = makeFocusTrap(true);
		focusTrapShift.setVisible(false);
	}

	@Override
	public void onClick(ClickEvent event) {
		app.handleSpaceKey();
		updateButtonAction();
	}

	/**
	 * Add dummy divs for handling focus change with swipe in VoiceOver.
	 * 
	 * @param p
	 *            euclidian panel
	 * @param canvas
	 *            canvas
	 */
	public void add(final EuclidianPanel p, final Canvas canvas) {
		final AppW app1 = app;
		if (focusHandler != null) {
			focusHandler.removeHandler();
		}
		if (canvas != null) {
			focusHandler = canvas.addDomHandler(new FocusHandler() {
				@Override
				public void onFocus(FocusEvent event) {
					app1.getAccessibilityManager().setTabOverGeos(true);
				}
			}, FocusEvent.getType());
		}
		p.add(focusTrapShift);
		for (SliderW r : ranges) {
			p.add(r);
		}
		p.add(hiddenButton);
		hiddenButton.setVisible(false);
		p.add(focusTrap);
	}

	private static void setRole(Label simpleButton, String string) {
		simpleButton.getElement().setAttribute("role", string);
	}

	/** For buttons we need to make sure click handler still works */
	private static void hideButton(Widget range) {
		range.getElement().getStyle().setOpacity(.01);
		range.getElement().getStyle().setPosition(Position.FIXED);
	}

	/**
	 * For sliders we want more restrictive hide method than
	 * {@link #hideButton(Widget)}
	 */
	private static void hide(Widget ui) {
		Style style = ui.getElement().getStyle();
		style.setOpacity(.01);
		style.setPosition(Position.FIXED);
		style.setWidth(1, Unit.PX);
		style.setHeight(1, Unit.PX);
		style.setOverflow(Overflow.HIDDEN);
	}

	private SliderW makeSlider(final int index) {
		final SliderW range = new SliderW(0, 10);
		hide(range);
		range.getElement().addClassName("slider");
		range.addValueChangeHandler(new ValueChangeHandler<Double>() {

			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				onSliderChange(range, index, event.getValue());

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

	/**
	 * @param range
	 *            slider
	 * @param index
	 *            slider index
	 * @param val
	 *            slider value
	 */
	protected void onSliderChange(SliderW range, int index, double val) {
		GeoElement sel = AccessibilityManagerNoGui.getSelectedGeo(app);
		if (sel != null && sel.isGeoNumeric()) {
			((GeoNumeric) sel).setValue(val);
			((GeoNumeric) sel).updateRepaint();
			range.getElement().focus();
			updateValueText(range, (GeoNumeric) sel);
			return;
		}
		double step = range.getValue() - oldVal[index];
		String unit = "";
		oldVal[index] += step;
		if (sel != null && sel.isGeoPoint()) {
			app.getGlobalKeyDispatcher().handleArrowKeyMovement(
					app.getSelectionManager().getSelectedGeos(),
					index == 0 ? step : 0, index == 1 ? step : 0,
					index == 2 ? step : 0, 1);
		} else {
			app.getAccessibilityManager().sliderChange(step);
			unit = "degrees";
		}
		updateValueText(range, range.getValue(), unit);
	}

	private HTML makeFocusTrap(final boolean backward) {
		HTML focusTrapN = new HTML(SafeHtmlUtils.fromTrustedString(
				"<div>select " + (backward ? "previous" : "next") + "</div>"));
		hide(focusTrapN);

		focusTrapN.getElement().setTabIndex(5000);

		ImageWrapper.nativeon(focusTrapN.getElement(), "focus",
				new ImageLoadCallback() {
					@Override
					public void onLoad() {
						onFocusTrap(backward);
					}
				});
		ImageWrapper.nativeon(focusTrapN.getElement(), "click",
				new ImageLoadCallback() {
					@Override
					public void onLoad() {
						onFocusTrap(backward);
					}
				});
		return focusTrapN;
	}

	/**
	 * @param backward
	 *            whether backward selection was triggered
	 */
	protected void onFocusTrap(boolean backward) {
		focusTrapShift.setVisible(true);
		app.getAccessibilityManager().setTabOverGeos(true);
		app.getGlobalKeyDispatcher().handleTab(false, backward);
		if (app.getAccessibilityManager().getSpaceAction() != null) {
			updateButtonAction();
		} else if (app.getAccessibilityManager().getSliderAction() != null) {
			SliderInput slider = app.getAccessibilityManager()
					.getSliderAction();
			updateRange(ranges[0], slider);
			ranges[0].setVisible(true);
			AriaHelper.setLabel(ranges[0], slider.getDescription());
			hiddenButton.setVisible(false);
			for (int i = 1; i < ranges.length; i++) {
				ranges[i].setVisible(false);
			}
			forceFocus(ranges[0].getElement());
		} else {
			GeoElement sel = AccessibilityManagerNoGui.getSelectedGeo(app);
			int dim = 0;
			if (sel instanceof GeoNumeric) {
				dim = 1;
			} else if (sel != null && sel.isGeoElement3D()) {
				dim = 3;
			} else if (sel instanceof GeoPointND) {
				dim = 2;
			}
			hiddenButton.setVisible(false);
			for (int i = 0; i < ranges.length; i++) {
				updateSelection(ranges[i], i);
				ranges[i].setVisible(dim > i);
			}

			if (dim > 0) {
				SliderW nextRange = ranges[backward ? dim - 1 : 0];
				forceFocus(nextRange.getElement());
			} else if (sel != null) {
				setRole(hiddenButton, "");
				hiddenButton
						.setText(sel.getAuralText(new ScreenReaderBuilder()));
				hiddenButton.setVisible(true);
				forceFocus(hiddenButton.getElement());
			} else {
				if (backward) {
					focusTrapShift.setVisible(false);
				}
			}
		}
	}

	private void updateButtonAction() {
		hiddenButton.setText(app.getAccessibilityManager().getSpaceAction());
		setRole(hiddenButton, "button");
		hiddenButton.setVisible(true);
		for (SliderW r : ranges) {
			r.setVisible(false);
		}
		forceFocus(hiddenButton.getElement());
	}

	private void updateRange(SliderW range, SliderInput slider) {
		range.setMinimum(slider.getMin());
		range.setMaximum(slider.getMax());
		range.setStep(1);
		double val = getInitialValue(slider);
		range.setValue(val);
		oldVal[0] = val;
		updateValueText(range, val, "degrees");
	}

	private double getInitialValue(SliderInput slider) {
		if (slider == SliderInput.ROTATE_Z) {
			return app.getEuclidianView3D().getAngleA();
		}
		if (slider == SliderInput.TILT) {
			return app.getEuclidianView3D().getAngleB();
		}
		return 0;
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

	private void updateValueText(SliderW range, double sel, String unit) {
		range.getElement().setAttribute("aria-valuetext",
				app.getKernel().format(sel, StringTemplate.screenReader) + " "
						+ unit);
	}

	/**
	 * Deferred focus for element
	 * 
	 * @param element
	 *            element
	 */
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
	 * @param index
	 *            slider index
	 */
	protected void updateSelection(SliderW range, int index) {
		GeoElement sel = AccessibilityManagerNoGui.getSelectedGeo(app);
		if (sel == null) {
			if (app.getAccessibilityManager().getSliderAction() != null) {
				updateRange(range,
						app.getAccessibilityManager().getSliderAction());
			}
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
			range.setMinimum(
					Math.floor(app.getActiveEuclidianView().getXmin()));
			range.setMaximum(Math.ceil(app.getActiveEuclidianView().getXmax()));
			range.setStep(sel.getAnimationStep());
			double coord = ((GeoPointND) sel).getInhomCoords().get(index + 1);
			this.oldVal = Cloner
					.clone(((GeoPointND) sel).getInhomCoords().get());
			range.setValue(coord);
			updateValueText(range, coord, "");
		} else {
			AriaHelper.setLabel(range, sel.getNameDescription());
		}
	}

}
