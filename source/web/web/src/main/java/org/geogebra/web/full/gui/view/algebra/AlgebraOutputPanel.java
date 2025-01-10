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
import org.geogebra.web.html5.gui.view.button.TriStateToggleButton;
import org.geogebra.web.html5.main.DrawEquationW;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.HTML;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

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

	public Canvas getValCanvas() {
		return this.valCanvas;
	}

	public FlowPanel getValuePanel() {
		return this.valuePanel;
	}

	/**
	 * @param isLaTeX whether output is LaTeX
	 */
	void addApproximateValuePrefix(boolean isLaTeX) {
		final Label label = new Label(Unicode.CAS_OUTPUT_NUMERIC + "");
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
	void addEqualSignPrefix() {
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
	 * @param geo geoElement
	 * @return the symbolic button or the engineering notation button
	 */
	public static ToggleButton createToggleButton(FlowPanel parent,
			final GeoElement geo) {
		ToggleButton toggleButton = newToggleButton(geo);
		updateOutputPanelButton(toggleButton, parent, geo);
		return toggleButton;
	}

	/**
	 * @param parent Parent panel
	 * @param geo GeoElement
	 * @return The Tri-State toggle button (symbolic, engineering mode)
	 */
	public static TriStateToggleButton createTriStateToggleButton(FlowPanel parent,
			final GeoElement geo) {
		TriStateToggleButton button = newEngineeringButton(geo);
		updateOutputPanelButton(button, parent, geo);
		return button;
	}

	/**
	 * Updates the buttons icons and sets the correct icon
	 * @param button {@link ToggleButton} OR {@link TriStateToggleButton}
	 * @param parent Parent panel
	 * @param geo GeoElement
	 */
	public static void updateOutputPanelButton(Widget button, FlowPanel parent, GeoElement geo) {
		if (button instanceof ToggleButton) {
			updateToggleButtonIcons((ToggleButton) button, geo);
			selectIconForToggleButton((ToggleButton) button, geo);
		} else if (button instanceof TriStateToggleButton) {
			updateTriStateToggleButtonIcons(geo, (TriStateToggleButton) button);
			selectIconForTriStateToggleButton((TriStateToggleButton) button, geo);
		}
		button.addStyleName("symbolicButton");
		parent.add(button);
	}

	private static void updateToggleButtonIcons(ToggleButton toggleButton, GeoElement geo) {
		if (AlgebraItem.shouldShowEngineeringNotationOutputButton(geo)) {
			toggleButton.updateIcons(MaterialDesignResources.INSTANCE.engineering_notation_white(),
					MaterialDesignResources.INSTANCE.modeToggleSymbolic());
		} else if (AlgebraItem.evaluatesToFraction(geo)) {
			toggleButton.updateIcons(MaterialDesignResources.INSTANCE.fraction_white(),
					MaterialDesignResources.INSTANCE.modeToggleSymbolic());
			Dom.toggleClass(toggleButton, "show-fraction", !toggleButton.isSelected());
		} else {
			toggleButton.updateIcons(MaterialDesignResources.INSTANCE.equal_sign_white(),
					MaterialDesignResources.INSTANCE.modeToggleSymbolic());
		}
	}

	private static void updateTriStateToggleButtonIcons(GeoElement geo,
			TriStateToggleButton button) {
		if (AlgebraItem.evaluatesToFraction(geo)) {
			button.updateIcons(MaterialDesignResources.INSTANCE.fraction_white(),
					MaterialDesignResources.INSTANCE.engineering_notation_white(),
					MaterialDesignResources.INSTANCE.modeToggleSymbolic());
			Dom.toggleClass(button, "show-fraction", button.getIndex() == 0);
		} else {
			button.updateIcons(MaterialDesignResources.INSTANCE.equal_sign_white(),
					MaterialDesignResources.INSTANCE.engineering_notation_white(),
					MaterialDesignResources.INSTANCE.modeToggleSymbolic());
		}
	}

	private static ToggleButton newToggleButton(GeoElement geo) {
		final ToggleButton button = new ToggleButton();
		if (AlgebraItem.shouldShowSymbolicOutputButton(geo)) {
			return newSymbolicToggleButton(geo, button);
		} else {
			return newEngineeringToggleButton(geo, button);
		}
	}

	private static ToggleButton newSymbolicToggleButton(GeoElement geo, ToggleButton button) {
		ClickStartHandler.init(button, new ClickStartHandler(true, true) {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				boolean symbolic = SymbolicUtil.toggleSymbolic(geo);
				button.setSelected(symbolic);
				Dom.toggleClass(button, "show-fraction", !symbolic);
			}
		});
		button.setText("symbolicToggleButton");
		return button;
	}

	private static ToggleButton newEngineeringToggleButton(GeoElement geo, ToggleButton button) {
		ClickStartHandler.init(button, new ClickStartHandler(true, true) {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				boolean engineeringMode = SymbolicUtil.toggleEngineeringNotation(geo);
				button.setSelected(engineeringMode);
			}
		});
		button.setText("engineeringToggleButton");
		return button;
	}

	private static TriStateToggleButton newEngineeringButton(GeoElement geo) {
		final TriStateToggleButton button = new TriStateToggleButton();

		ClickStartHandler.init(button, new ClickStartHandler(true, true) {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				boolean symbolic = SymbolicUtil.isSymbolicMode(geo);
				if (button.getIndex() != 0) {
					SymbolicUtil.toggleEngineeringNotation(geo);
				}
				if (button.getIndex() != 2) {
					symbolic = SymbolicUtil.toggleSymbolic(geo);
				}
				button.selectNext();
				Dom.toggleClass(button, "show-fraction", !symbolic);
			}
		});
		return button;
	}

	private static void selectIconForToggleButton(ToggleButton toggleButton, GeoElement geo) {
		if (shouldSelectToggleButton(toggleButton, geo)) {
			toggleButton.setSelected(true);
			toggleButton.addStyleName("btn-prefix");
		} else {
			toggleButton.setSelected(false);
		}
	}

	private static boolean shouldSelectToggleButton(ToggleButton toggleButton, GeoElement geo) {
		return (toggleButton.getText().equals("symbolicToggleButton")
				&& AlgebraItem.getCASOutputType(geo) == AlgebraItem.CASOutputType.NUMERIC)
				|| (toggleButton.getText().equals("engineeringToggleButton")
				&& SymbolicUtil.isEngineeringNotationMode(geo));
	}

	private static void selectIconForTriStateToggleButton(TriStateToggleButton button,
			GeoElement geo) {
		if (SymbolicUtil.isEngineeringNotationMode(geo)) {
			button.select(2);
		} else if (AlgebraItem.getCASOutputType(geo) == AlgebraItem.CASOutputType.NUMERIC) {
			button.select(0);
		} else {
			button.select(1);
		}
	}

	/**
	 * @param parent Parent Panel
	 * @return The symbolic button if it exists, null otherwise
	 */
	public static Widget getSymbolicButtonIfExists(FlowPanel parent) {
		for (int i = 0; i < parent.getWidgetCount(); i++) {
			if (parent.getWidget(i).getStyleName().contains("symbolicButton")) {
				return parent.getWidget(i);
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
		if (AlgebraItem.shouldShowEqualSignPrefix(geo1)) {
			addEqualSignPrefix();
		} else {
			addApproximateValuePrefix(latex);
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

	/*
	private String getSymbolicPrefix(Kernel kernel) {
		return kernel.getLocalization().rightToLeftReadingOrder
				? Unicode.CAS_OUTPUT_PREFIX_RTL + ""
				: Unicode.CAS_OUTPUT_PREFIX + "";
	}
	*/

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
