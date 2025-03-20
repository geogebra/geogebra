package org.geogebra.web.html5.gui.accessibility;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.draw.CanvasDrawable;
import org.geogebra.common.euclidian.draw.dropdown.DrawDropDownList;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.ListItem;
import org.geogebra.web.html5.gui.util.UnorderedList;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.event.dom.client.FocusEvent;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.Button;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

public class AccessibleDropDown implements AccessibleWidget {
	private final GeoList list;
	private final AppW app;
	private final Button button;
	private final UnorderedList options;
	private final Label label;
	private final AccessibilityView view;

	/**
	 * @param geo list
	 * @param app application
	 * @param view view
	 * @param widgetFactory widget factory
	 */
	public AccessibleDropDown(GeoList geo, AppW app, AccessibilityView view,
			BaseWidgetFactory widgetFactory) {
		this.list = geo;
		this.app = app;
		this.view = view;

		button = widgetFactory.newButton();
		button.getElement().setAttribute("aria-haspopup", "listbox");
		options = new UnorderedList();
		options.getElement().setAttribute("role", "listbox");
		options.getElement().setTabIndex(-1);
		label = new Label();
		label.setVisible(false); // hide; only used via aria-labelledby
		String labelId = DOM.createUniqueId();
		label.getElement().setId(labelId);
		String buttonId = DOM.createUniqueId();
		button.getElement().setId(buttonId);
		button.addStyleName("accessibleInput");
		button.getElement().setAttribute("aria-labelledby", labelId + " " + buttonId);

		options.setVisible(false);
		button.addDomHandler(e -> {
			toggle();
			if (options.isVisible()) {
				options.getElement().focus();
				setHoverIndex(list.getSelectedIndex());
			} else {
				button.getElement().focus();
			}
		}, ClickEvent.getType());
		update();
	}

	private void toggle() {
		boolean visible = !options.isVisible();
		view.closeAllDropdowns();
		options.setVisible(visible);
		app.getActiveEuclidianView().closeAllDropDowns();
		app.getSelectionManager().setSelectedGeos(Collections.singletonList(list));
		DrawableND drawable = app.getActiveEuclidianView().getDrawableFor(list);
		if (drawable instanceof DrawDropDownList) {
			((DrawDropDownList) drawable).setOptionsVisible(visible);
			app.getActiveEuclidianView().repaintView();
		}
	}

	private void setHoverIndex(int i) {
		DrawableND drawable = app.getActiveEuclidianView().getDrawableFor(list);
		if (drawable instanceof DrawDropDownList) {
			((DrawDropDownList) drawable).setHoverIndex(i);
		}
	}

	@Override
	public List<? extends Widget> getWidgets() {
		return Arrays.asList(label, button, options);
	}

	@Override
	public void update() {
		updatePosition(list, button, app);
		ScreenReaderBuilder sb = new ScreenReaderBuilder(app.getLocalization());
		list.addAuralCaption(sb);
		label.setText(sb.toString());
		updateText();
		options.clear();
		for (int i = 0; i < list.size(); i++) {
			ListItem option = new ListItem();
			String optionId = DOM.createUniqueId();
			option.getElement().setId(optionId);
			if (i == list.getSelectedIndex()) {
				options.getElement().setAttribute("aria-activedescendant", optionId);
			}
			option.setText(list.getItemDisplayString(i, StringTemplate.screenReaderAscii));
			option.getElement().setAttribute("role", "option");
			final int idx = i;
			option.addDomHandler(e -> {
						list.setSelectedIndexUpdate(idx);
						updateText();
						button.getElement().focus();
						toggle();
					}, ClickEvent.getType());
			option.addDomHandler(e -> setHoverIndex(idx), FocusEvent.getType());
			options.add(option);
		}
	}

	/**
	 * Even though double tap normally taps the focused element, if there is something above
	 * the other element will be clicked instead. Position inputs like drawables to avoid this.
	 * @param geo construction element
	 * @param button DOM element
	 * @param app app
	 */
	static void updatePosition(GeoElement geo, Widget button, AppW app) {
		DrawableND drawable = app.getActiveEuclidianView().getDrawableFor(geo);
		if (drawable instanceof CanvasDrawable) {
			GRectangle bounds = ((CanvasDrawable) drawable).getBounds();
			if (bounds != null) {
				button.getElement().getStyle().setTop(bounds.getMinY(), Unit.PX);
				button.getElement().getStyle().setLeft(bounds.getMinX(), Unit.PX);
			}
		}
	}

	private void updateText() {
		button.setText(list.getSelectedItemDisplayString(StringTemplate.screenReaderAscii));
	}

	@Override
	public void setFocus(boolean focus) {
		if (focus) {
			button.getElement().focus();
		}
	}

	@Override
	public boolean isCompatible(GeoElement geo) {
		return geo instanceof GeoList && ((GeoList) geo).drawAsComboBox();
	}

	public void close() {
		options.setVisible(false);
	}
}
