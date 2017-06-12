package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasSymbolicMode;
import org.geogebra.common.util.lang.Unicode;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.web.gui.util.MyToggleButtonW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class AlgebraOutputPanel extends FlowPanel {
	FlowPanel valuePanel;
	Canvas valCanvas;

	public AlgebraOutputPanel() {

		valuePanel = new FlowPanel();
		valuePanel.addStyleName("avValue");

	}

	void addPrefixLabel(String text, boolean isLaTeX) {
		final Label label = new Label(text);
		if (!isLaTeX) {
			label.addStyleName("prefix");
		} else {
			label.addStyleName("prefixLatex");
		}
		add(label);
	}

	public void addValuePanel() {
		if (getWidgetIndex(valuePanel) == -1) {
			add(valuePanel);
		}
	}

	public void createSymbolicButton(final GeoElement geo) {
		final MyToggleButtonW btnSymbolic = new MyToggleButtonW(
				GuiResourcesSimple.INSTANCE.modeToggleSymbolic(),
				GuiResourcesSimple.INSTANCE.modeToggleNumeric());
		btnSymbolic.addStyleName("symbolicButton");
		if (getOutputPrefix(geo) == Unicode.CAS_OUTPUT_NUMERIC) {
			btnSymbolic.setSelected(true);
		}
		if (getOutputPrefix(geo) == Unicode.CAS_OUTPUT_PREFIX) {
			btnSymbolic.setSelected(false);
			btnSymbolic.addStyleName("btn-prefix");
		}
		btnSymbolic.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				btnSymbolic.setSelected(AlgebraItem.toggleSymbolic(geo));
			}
		});
		add(btnSymbolic);

	}

	private static String getOutputPrefix(GeoElement geo) {
		if (geo instanceof HasSymbolicMode
				&& !((HasSymbolicMode) geo).isSymbolicMode()) {
			return Unicode.CAS_OUTPUT_NUMERIC;
		}
		if (geo.getKernel().getLocalization().rightToLeftReadingOrder) {
			return Unicode.CAS_OUTPUT_PREFIX_RTL;
		}
		return Unicode.CAS_OUTPUT_PREFIX;
	}
}
