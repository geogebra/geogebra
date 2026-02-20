/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.view.algebra;

import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.gui.view.algebra.AlgebraOutputFormat;
import org.geogebra.common.gui.view.algebra.AlgebraOutputFormatFilter;
import org.geogebra.common.gui.view.algebra.AlgebraOutputOperator;
import org.geogebra.common.kernel.geos.DescriptionMode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.util.IndexHTMLBuilder;
import org.geogebra.editor.share.util.Unicode;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.util.ToggleButton;
import org.geogebra.web.html5.main.DrawEquationW;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.HTML;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.Label;

/**
 * Output part of AV item
 */
public class AlgebraOutputPanel extends FlowPanel {
	private final @Nonnull FlowPanel valuePanel;
	private @CheckForNull Canvas valCanvas;
	private @CheckForNull Label valueLabel;

	/**
	 * Create new output panel
	 */
	public AlgebraOutputPanel() {
		valuePanel = new FlowPanel();
		valuePanel.addStyleName("avValue");
		valuePanel.getElement().setTabIndex(0);
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
	 * @param geo GeoElement
	 * @param engineeringNotation if engineering notation is enabled
	 * @return The multi-state toggle button (symbolic, engineering mode)
	 */
	public static AlgebraOutputFormatButton createOutputFormatButton(
			GeoElement geo, boolean engineeringNotation,
			Set<AlgebraOutputFormatFilter> algebraOutputFormatFilters) {
		final AlgebraOutputFormatButton button = new AlgebraOutputFormatButton();

		ClickStartHandler.init(button, new ClickStartHandler(true, true) {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				applyNextFormat(geo, engineeringNotation, algebraOutputFormatFilters, button);
			}
		});
		button.addKeyActivateHandler(() -> applyNextFormat(geo,
				engineeringNotation, algebraOutputFormatFilters, button));
		return button;
	}

	private static void applyNextFormat(GeoElement geo, boolean engineeringNotation,
			Set<AlgebraOutputFormatFilter> algebraOutputFormatFilters,
			AlgebraOutputFormatButton button) {
		AlgebraOutputFormat nextFormat = AlgebraOutputFormat.getNextFormat(
				geo, engineeringNotation, algebraOutputFormatFilters);
		AlgebraOutputFormat.switchToNextFormat(
				geo, engineeringNotation, algebraOutputFormatFilters);
		if (nextFormat != null) {
			button.select(nextFormat);
		}
	}

	/**
	 * Updates the buttons icons and sets the correct icon
	 * @param button {@link ToggleButton} OR {@link AlgebraOutputFormatButton}
	 * @param parent Parent panel
	 * @param geo GeoElement
	 */
	public static void updateOutputPanelButton(AlgebraOutputFormatButton button, FlowPanel parent,
			GeoElement geo, boolean engineering,
			Set<AlgebraOutputFormatFilter> algebraOutputFormatFilters) {
		AlgebraOutputFormat nextFormat = AlgebraOutputFormat.getNextFormat(
				geo, engineering, algebraOutputFormatFilters);
		if (nextFormat != null) {
			button.select(nextFormat);
			AriaHelper.setLabel(button,
					geo.getApp().getLocalization().getMenu(nextFormat.getScreenReaderLabel()));
		}
		parent.add(button);
	}

	/**
	 * @param parent Parent Panel
	 * @return The symbolic button if it exists, null otherwise
	 */
	public static AlgebraOutputFormatButton getSymbolicButtonIfExists(FlowPanel parent) {
		for (int i = 0; i < parent.getWidgetCount(); i++) {
			if (parent.getWidget(i) instanceof AlgebraOutputFormatButton) {
				return (AlgebraOutputFormatButton) parent.getWidget(i);
			}
		}
		return null;
	}

	static void removeSymbolicButton(FlowPanel parent) {
		for (int i = 0; i < parent.getWidgetCount(); i++) {
			if (parent.getWidget(i) instanceof AlgebraOutputFormatButton) {
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
		if (AlgebraOutputFormat.getOutputOperator(geo1) == AlgebraOutputOperator.EQUALS) {
			addEqualSignPrefix();
		} else {
			addApproximateValuePrefix(latex);
		}

		valuePanel.clear();

		if (latex
				&& (geo1.isLaTeXDrawableGeo()
				|| AlgebraItem.evaluatesToFraction(geo1)
				|| AlgebraItem.isRationalizableFraction(geo1)
				|| AlgebraItem.isGeoSurd(geo1))) {
			showLaTeXValue(text, geo1, fontSize);
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
	 *            LaTeX value string
	 * @param geo
	 *            construction element
	 * @param fontSize
	 *            size in pixels
	 */
	public void showLaTeXValue(String text, GeoElementND geo,
			int fontSize) {
		// LaTeX
		Canvas canvas = DrawEquationW.paintOnCanvas(geo, text, valCanvas,
				fontSize);
		canvas.addStyleName("canvasVal");
		valuePanel.clear();
		valuePanel.add(canvas);
		valCanvas = canvas;
	}

	/**
	 * Show plain text preview.
	 * @param text text in linear notation
	 */
	public void showPlainTextPreview(String text) {
		valuePanel.clear();
		if (valueLabel == null) {
			valueLabel = new Label(text);
		} else {
			valueLabel.setText(text);
		}
		valuePanel.add(valueLabel);
	}

	/**
	 * Clear the panel and its children
	 */
	public void reset() {
		valuePanel.clear();
		clear();
	}
}
