package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelWAbstract.EuclidianPanel;
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
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public class VoiceoverTabber {

	private Widget canvas;
	private AppW app;

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
		final SliderW range = new SliderW(0, 10);
		hide(range);
		range.getElement().addClassName("slider");
		range.addValueChangeHandler(new ValueChangeHandler<Double>() {

			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				GeoElement sel = getSelectedGeo();
				if (sel != null && sel.isGeoNumeric()) {
					((GeoNumeric) sel).setValue(event.getValue());
					((GeoNumeric) sel).updateRepaint();
					range.getElement().focus();
					updateValueText(range, sel);
				}

			}
		});
		range.getElement().setTabIndex(5000);

		if (getCanvas() != null) {
			getCanvas().addDomHandler(new FocusHandler() {
				@Override
				public void onFocus(FocusEvent event) {
					app.getAccessibilityManager().setTabOverGeos(true);
				}
			}, FocusEvent.getType());
		}

		range.addDomHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				updateSelection(range);
			}
		}, FocusEvent.getType());
		final Label simpleButton = new Label("button");
		hide(simpleButton);
		simpleButton.getElement().setAttribute("role", "button");
		simpleButton.getElement().setTabIndex(5000);
		simpleButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				app.handleSpaceKey();
			}
		});
		HTML focusTrap = makeFocusTrap(false, range, simpleButton);
		HTML focusTrapShift = makeFocusTrap(true, range, simpleButton);

		p.add(focusTrapShift);
		p.add(range);
		p.add(simpleButton);
		simpleButton.setVisible(false);
		p.add(focusTrap);
	}


	private HTML makeFocusTrap(final boolean b, final Widget range,
			final Label simpleButton) {
		HTML focusTrap = new HTML(SafeHtmlUtils.fromTrustedString(
				"<div>select next</div>"));
		hide(focusTrap);

		focusTrap.getElement().setTabIndex(5000);

		ImageWrapper.nativeon(focusTrap.getElement(), "focus",
				new ImageLoadCallback() {
					@Override
					public void onLoad() {
						app.getAccessibilityManager().setTabOverGeos(true);
						app.getGlobalKeyDispatcher().handleTab(false, b);
						if (app.getAccessibilityManager() != null && app
								.getAccessibilityManager()
								.getSpaceAction() != null) {
							simpleButton.setText(app.getAccessibilityManager()
									.getSpaceAction());
							simpleButton.setVisible(true);
							range.setVisible(false);
							simpleButton.getElement().focus();
						} else {
							simpleButton.setVisible(false);
							range.setVisible(true);
						// focusTrap.getElement().setInnerHTML("TRAPPED");
							forceFocus(range.getElement());
						}
					}
				});
		return focusTrap;
	}

	protected void updateValueText(SliderW range, GeoElement sel) {
		range.getElement().setAttribute("aria-valuetext",
				((GeoNumeric) sel).toValueString(StringTemplate.screenReader));

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

	private void hide(UIObject range) {
		range.getElement().getStyle().setOpacity(.01);
		range.getElement().getStyle().setPosition(Position.FIXED);

	}

	/**
	 * @param range
	 *            slider
	 */
	protected void updateSelection(SliderW range) {
		GeoElement sel = getSelectedGeo();
		if (sel != null && sel.isGeoNumeric()) {
			AriaHelper.setLabel(range, sel.getNameDescription());
			range.setMinimum(((GeoNumeric) sel).getIntervalMin());
			range.setMaximum(((GeoNumeric) sel).getIntervalMax());
			range.setStep(((GeoNumeric) sel).getAnimationStep());
			range.setValue(((GeoNumeric) sel).getValue());
			updateValueText(range, sel);
		}

	}

	protected GeoElement getSelectedGeo() {
		return app.getSelectionManager().getSelectedGeos().size() == 1
				? app.getSelectionManager().getSelectedGeos().get(0) : null;
	}

}
