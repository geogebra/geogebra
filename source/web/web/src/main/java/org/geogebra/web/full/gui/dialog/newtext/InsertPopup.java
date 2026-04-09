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

package org.geogebra.web.full.gui.dialog.newtext;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.geogebra.common.gui.util.TableSymbols;
import org.geogebra.common.gui.util.TableSymbolsLaTeX;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.web.full.gui.dialog.text.TextEditPanel;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DrawEquationW;
import org.geogebra.web.shared.components.tab.ComponentTab;
import org.geogebra.web.shared.components.tab.TabData;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

public class InsertPopup extends GPopupPanel {
	private final TextEditPanel textEditPanel;
	private final Map<String, GeoElement> geosMap = new HashMap<>();
	private final Runnable previewUpdater;

	/**
	 * Creates a popup with geos, symbols and latex
	 * @param appW {@link AppW}
	 * @param textEditPanel {@link TextEditPanel}
	 * @param previewUpdater updates preview
	 */
	public InsertPopup(AppW appW, TextEditPanel textEditPanel, Runnable previewUpdater) {
		super(appW.getAppletFrame(), appW);
		this.textEditPanel = textEditPanel;
		this.previewUpdater = previewUpdater;
		addStyleName("insertPopup");
		buildPopupContent();
	}

	private void buildPopupContent() {
		TabData objectsTab = new TabData("Objects", createObjectsPanel());
		TabData symbolsTab = new TabData("Symbols", createSymbolsPanel());
		TabData latexTab = new TabData("TextDialog.LaTeX", createLatexPanel());
		ComponentTab tab = new ComponentTab((AppW) app, null, objectsTab, symbolsTab, latexTab);
		add(tab);
	}

	private FlowPanel createObjectsPanel() {
		FlowPanel holder = new FlowPanel();
		FlowPanel objectsPanel = new FlowPanel();
		objectsPanel.addStyleName("tabPanel objects");

		TreeSet<GeoElement> geos = app.getKernel().getConstruction()
				.getGeoSetLabelOrder();
		geosMap.put(app.getLocalization().getMenu("EmptyBox"), null);
		for (GeoElement geo : geos) {
			if (geo.isLabelSet() && !geo.equals(textEditPanel.getEditGeo())) {
				geosMap.put(geo.getNameDescription(), geo);
			}
		}

		addGroup(geosMap.keySet().toArray(new String[0]), objectsPanel, false, false, true);
		holder.add(objectsPanel);
		return holder;
	}

	private FlowPanel createSymbolsPanel() {
		FlowPanel holder = new FlowPanel();
		FlowPanel symbolsPanel = new FlowPanel();
		symbolsPanel.addStyleName("tabPanel");
		fillSymbolsContent(symbolsPanel);
		holder.add(symbolsPanel);
		return holder;
	}

	private void fillSymbolsContent(FlowPanel symbolsPanel) {
		addGroup(TableSymbols.basicSymbols(app.getLocalization(), TableSymbols.basicSymbolsMap(
				app.getLocalization())), symbolsPanel, false, true, false);
		addGroup(TableSymbols.OPERATORS, symbolsPanel, false, true, false);
		addGroup(TableSymbols.greekLettersPlusVariants(), symbolsPanel,  false, true, false);
		addGroup(TableSymbols.ANALYSIS, symbolsPanel, false, true, false);
		addGroup(TableSymbols.SETS, symbolsPanel, false, true, false);
		addGroup(TableSymbols.LOGICAL, symbolsPanel, false, true, false);
		addGroup(TableSymbols.SUB_SUPERSCRIPTS, symbolsPanel, false, true, false);
		addGroup(TableSymbols.BASIC_ARROWS, symbolsPanel, false, true, false);
		addGroup(TableSymbols.OTHER_ARROWS, symbolsPanel, false, true, false);
		addGroup(TableSymbols.GEOMETRIC_SHAPES, symbolsPanel, false, true, false);
		addGroup(TableSymbols.GAMES_MUSIC, symbolsPanel, false, true, false);
		addGroup(TableSymbols.CURRENCY, symbolsPanel, false, true, false);
		addGroup(TableSymbols.HAND_POINTERS, symbolsPanel, false, false, false);
	}

	private FlowPanel createLatexPanel() {
		FlowPanel latexPanel = new FlowPanel();
		latexPanel.addStyleName("tabPanel");

		addGroup(TableSymbolsLaTeX.roots_fractions, latexPanel, true, true, false);
		addGroup(TableSymbolsLaTeX.sums, latexPanel, true, true, false);
		addGroup(TableSymbolsLaTeX.accents, latexPanel,  true, true, false);
		addGroup(TableSymbolsLaTeX.accentsExtended, latexPanel, true, true, false);
		addGroup(TableSymbolsLaTeX.brackets, latexPanel, true, true, false);
		addGroup(TableSymbolsLaTeX.borders, latexPanel, true, false, false);

		FlowPanel holder = new FlowPanel();
		holder.add(latexPanel);
		return holder;
	}

	private void addGroup(String[] symbols, FlowPanel parent, boolean isLatex,
			boolean addSeparator, boolean insertGeo) {
		parent.add(getFilledPanel(symbols, isLatex, insertGeo));
		if (addSeparator) {
			parent.add(BaseWidgetFactory.INSTANCE.newDivider(false));
		}
	}

	private FlowPanel getFilledPanel(String[] symbols, boolean isLatex, boolean insertGeo) {
		FlowPanel panel = new FlowPanel();
		panel.addStyleName("group");
		for (String symbol : symbols) {
			Widget widget;
			if (isLatex) {
				widget = Canvas.createIfSupported();
				((DrawEquationW) app.getDrawEquation()).paintOnCleanCanvas(symbol, (Canvas) widget,
						16, GeoGebraColorConstants.NEUTRAL_900, false);
			} else {
				widget = BaseWidgetFactory.INSTANCE.newPrimaryText(symbol);
			}
			Dom.addEventListener(widget.getElement(), "click", event -> {
				if (insertGeo) {
					textEditPanel.insertGeoElement(geosMap.get(symbol));
				} else {
					textEditPanel.insertTextString(symbol, isLatex);
					if (isLatex) {
						textEditPanel.ensureLaTeX();
					}
				}
				hide();
				previewUpdater.run();
			});
			panel.add(widget);
		}
		return panel;
	}
}
