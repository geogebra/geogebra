package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.kernel.geos.DescriptionMode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.util.IndexHTMLBuilder;
import org.geogebra.common.util.SymbolicUtil;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.util.ToggleButton;
import org.geogebra.web.html5.main.DrawEquationW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Output part of AV item
 */
public class AlgebraOutputPanel extends FlowPanel {
	private final FlowPanel valuePanel;
	private Canvas valCanvas;

	/**
	 * Create new output panel
	 */
	public AlgebraOutputPanel() {
		valuePanel = new FlowPanel();
		valuePanel.addStyleName("avValue");
	}

	/**
	 * @param text
	 *            prefix
	 * @param isLaTeX
	 *            whether output is LaTeX
	 */
	void addPrefixLabel(String text, boolean isLaTeX) {
		final Label label = new Label(text);
		if (!isLaTeX) {
			label.addStyleName("prefix");
		} else {
			label.addStyleName("prefixLatex");
		}
		add(label);
	}

	/**
	 * add arrow prefix for av output
	 */
	void addArrowPrefix() {
		final Image arrow = new NoDragImage(
				MaterialDesignResources.INSTANCE.equal_sign_black(), 24);
		arrow.setStyleName("arrowOutputImg");
		add(arrow);
	}

	/**
	 * Add value panel to DOM
	 */
	public void addValuePanel() {
		if (getWidgetIndex(valuePanel) == -1) {
			add(valuePanel);
		}
	}

	/**
	 * @param parent parent panel
	 * @param geo geoelement
	 */
	public static void createSymbolicButton(FlowPanel parent,
			final GeoElement geo) {

		ToggleButton btnSymbolic = getSymbolicButtonIfExists(parent);

		if (btnSymbolic == null) {
			btnSymbolic = newSymbolicButton(geo);
		}

		updateSymbolicIcons(geo, btnSymbolic);

		btnSymbolic.addStyleName("symbolicButton");

		if ((Unicode.CAS_OUTPUT_NUMERIC + "")
				.equals(AlgebraItem.getOutputPrefix(geo))) {
			btnSymbolic.setSelected(false);
		} else {
			btnSymbolic.setSelected(true);
			btnSymbolic.addStyleName("btn-prefix");
		}

		parent.add(btnSymbolic);
	}

	private static void updateSymbolicIcons(GeoElement geo, ToggleButton btnSymbolic) {
		if (geo.getDefinition().isFraction()) {
			btnSymbolic.updateIcons(MaterialDesignResources.INSTANCE.fraction_white(),
					MaterialDesignResources.INSTANCE.modeToggleSymbolic());
			Dom.toggleClass(btnSymbolic, "show-fraction", !btnSymbolic.isSelected());
		} else {
			btnSymbolic.updateIcons(MaterialDesignResources.INSTANCE.equal_sign_white(),
					MaterialDesignResources.INSTANCE.modeToggleSymbolic());
		}
	}

	private static ToggleButton newSymbolicButton(GeoElement geo) {
		final ToggleButton btn = new ToggleButton();

		ClickStartHandler.init(btn, new ClickStartHandler(true, true) {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				boolean symbolic = SymbolicUtil.toggleSymbolic(geo);
				btn.setSelected(symbolic);
				Dom.toggleClass(btn, "show-fraction", !symbolic);
			}
		});
		return btn;
	}

	private static ToggleButton getSymbolicButtonIfExists(FlowPanel parent) {
		for (int i = 0; i < parent.getWidgetCount(); i++) {
			if (parent.getWidget(i).getStyleName().contains("symbolicButton")) {
				return (ToggleButton) parent.getWidget(i);
			}
		}
		return null;
	}

	static void removeSymbolicButton(FlowPanel parent) {
		for (int i = 0; i < parent.getWidgetCount(); i++) {
			if (parent.getWidget(i).getStyleName().contains("symbolicButton")) {
				parent.getWidget(i).removeFromParent();
			}
		}
	}

	/**
	 * @param geo1 geoelement
	 * @param text text content
	 * @param latex whether the text is LaTeX
	 * @param fontSize size in pixels
	 * @return whether update was successful (AV has value panel)
	 */
	boolean updateValuePanel(GeoElement geo1, String text,
			boolean latex, int fontSize) {
		if (geo1 == null || geo1
				.getDescriptionMode() != DescriptionMode.DEFINITION_VALUE) {
			return false;
		}
		clear();
		if (AlgebraItem.shouldShowSymbolicOutputButton(geo1)) {
			if (AlgebraItem.getOutputPrefix(geo1)
					.startsWith(Unicode.CAS_OUTPUT_NUMERIC + "")) {
				addPrefixLabel(AlgebraItem.getOutputPrefix(geo1), latex);
			} else {
				addArrowPrefix();
			}
		} else {
			addArrowPrefix();
		}

		valuePanel.clear();

		if (latex 
				&& (geo1.isLaTeXDrawableGeo()
						|| AlgebraItem.isGeoFraction(geo1)
						|| AlgebraItem.isGeoSurd(geo1))) {
			valCanvas = DrawEquationW.paintOnCanvas(geo1, text, valCanvas,
					fontSize);
			valCanvas.addStyleName("canvasVal");
			valuePanel.clear();
			valuePanel.add(valCanvas);
		} else {
			HTML html = new HTML();
			IndexHTMLBuilder sb = new DOMIndexHTMLBuilder(html,
					geo1.getKernel().getApplication());
			if (AlgebraItem.needsPacking(geo1)) {
				geo1.getAlgebraDescriptionTextOrHTMLDefault(sb);
			} else {
				geo1.getAlgebraDescriptionTextOrHTMLRHS(sb);
			}
			valuePanel.add(html);
		}

		return true;
	}

	/**
	 * @param text
	 *            preview text
	 * @param previewGeo
	 *            preview geo
	 * @param fontSize
	 *            size in pixels
	 */
	public void showLaTeXPreview(String text, GeoElementND previewGeo,
			int fontSize) {
		// LaTeX
		valCanvas = DrawEquationW.paintOnCanvas(previewGeo, text, valCanvas,
				fontSize);
		valCanvas.addStyleName("canvasVal");
		valuePanel.clear();
		valuePanel.add(valCanvas);

	}

	/**
	 * Clear the panel and its children
	 */
	public void reset() {
		valuePanel.clear();
		clear();
	}
}
