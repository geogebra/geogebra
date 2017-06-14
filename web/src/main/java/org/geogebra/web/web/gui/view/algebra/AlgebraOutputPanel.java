package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.IndexHTMLBuilder;
import org.geogebra.common.util.lang.Unicode;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.main.DrawEquationW;
import org.geogebra.web.web.gui.util.MyToggleButtonW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

public class AlgebraOutputPanel extends FlowPanel {
	private FlowPanel valuePanel;
	private Canvas valCanvas;

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

	public static void createSymbolicButton(FlowPanel parent,
			final GeoElement geo, boolean swap) {
		MyToggleButtonW btnSymbolic = null;
		for (int i = 0; i < parent.getWidgetCount(); i++) {
			if(parent.getWidget(i).getStyleName().contains("symbolicButton")){
				btnSymbolic = (MyToggleButtonW) parent.getWidget(i);
			}
		}
		if (btnSymbolic == null) {
			btnSymbolic = new MyToggleButtonW(
				GuiResourcesSimple.INSTANCE.modeToggleSymbolic(),
				GuiResourcesSimple.INSTANCE.modeToggleNumeric());
			final MyToggleButtonW btn = btnSymbolic;
			btnSymbolic.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					btn.setSelected(AlgebraItem.toggleSymbolic(geo));
				}
			});
		}
		btnSymbolic.addStyleName("symbolicButton");
		if (AlgebraItem.getOutputPrefix(geo) == Unicode.CAS_OUTPUT_NUMERIC) {
			btnSymbolic.setSelected(!swap);
		}
		if (AlgebraItem.getOutputPrefix(geo) != Unicode.CAS_OUTPUT_NUMERIC) {
			btnSymbolic.setSelected(swap);
			btnSymbolic.addStyleName("btn-prefix");
		}

		parent.add(btnSymbolic);

	}



	boolean updateValuePanel(GeoElement geo1, String text,
			boolean latex, int fontSize) {
		if (geo1 == null || !geo1.needToShowBothRowsInAV()) {
			return false;
		}
		Kernel kernel = geo1.getKernel();
		clear();
		if (AlgebraItem.isSymbolicDiffers(geo1)) {
			if (!kernel.getApplication().has(Feature.AV_ITEM_DESIGN)) {
				createSymbolicButton(this, geo1, false);
			} else {
				addPrefixLabel(AlgebraItem.getOutputPrefix(geo1), latex);
			}
		} else {
			addPrefixLabel(AlgebraItem.getSymbolicPrefix(kernel),
					latex);
		}

		valuePanel.clear();

		if (latex 
				&& (geo1.isLaTeXDrawableGeo()
						|| AlgebraItem.isGeoFraction(geo1))) {
			valCanvas = DrawEquationW.paintOnCanvas(geo1, text, valCanvas,
					fontSize);
			valCanvas.addStyleName("canvasVal");
			valuePanel.clear();
			valuePanel.add(valCanvas);
		} else {
			IndexHTMLBuilder sb = new IndexHTMLBuilder(false);
			geo1.getAlgebraDescriptionTextOrHTMLDefault(sb);
			valuePanel.add(new HTML(sb.toString()));
		}

		return true;
	}

	public void showLaTeXPreview(String text, GeoElementND previewGeo,
			int fontSize) {
		// LaTeX
		valCanvas = DrawEquationW.paintOnCanvas(previewGeo, text, valCanvas,
				fontSize);
		valCanvas.addStyleName("canvasVal");
		valuePanel.clear();
		valuePanel.add(valCanvas);

	}

	public void reset() {
		valuePanel.clear();
		clear();
	}
}
