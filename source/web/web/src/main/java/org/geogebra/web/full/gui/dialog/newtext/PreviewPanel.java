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

import org.geogebra.common.gui.dialog.handler.TextStyle;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.dialog.text.TextEditPanel;
import org.geogebra.web.full.gui.dialog.text.TextPreviewPanelW;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.user.client.ui.FlowPanel;

public class PreviewPanel extends FlowPanel {
	private final AppW appW;
	private final TextPreviewPanelW previewer;
	private ComponentCheckbox latexCheckbox;

	/**
	 * @param appW {@link AppW}
	 * @param textStyle {@link TextStyle}
	 * @param editPanel {@link TextEditPanel}
	 * @param recenterDialog center and resize dialog when preview panel is shown
	 */
	public PreviewPanel(AppW appW, TextStyle textStyle, TextEditPanel editPanel,
			Runnable recenterDialog) {
		this.appW = appW;
		this.previewer = editPanel.getPreviewer();
		createPreviewPanel(textStyle, recenterDialog);
	}

	private void createPreviewPanel(TextStyle textStyle, Runnable recenterDialog) {
		FlowPanel header = new FlowPanel();
		header.addStyleName("header closed");
		SVGResource icon = KeyboardResources.INSTANCE.keyboard_arrowRight_black()
				.withFill(GeoGebraColorConstants.NEUTRAL_700.toString());
		StandardButton previewButton = new StandardButton(icon, "Preview", 24);
		previewButton.addFastClickHandler(source -> {
			togglePreviewPanel();
			Dom.toggleClass(header, "closed", !previewer.getPanel().isVisible());
			recenterDialog.run();
		});
		header.add(previewButton);

		latexCheckbox = new ComponentCheckbox(appW.getLocalization(),
				textStyle.isLatex(), "LaTeXFormula", textStyle::setLatex);
		header.add(latexCheckbox);
		add(header);

		previewer.getPanel().setStyleName("previewPanel");
		previewer.getPanel().setVisible(false);
		add(previewer.getPanel());
	}

	private void togglePreviewPanel() {
		previewer.getPanel().setVisible(!previewer.getPanel().isVisible());
	}

	/**
	 * sets latex checkbox selected
	 */
	public void selectLatexCheckbox() {
		latexCheckbox.setSelected(true);
	}
}
