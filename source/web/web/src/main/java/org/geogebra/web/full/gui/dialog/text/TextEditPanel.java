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

package org.geogebra.web.full.gui.dialog.text;

import java.util.ArrayList;

import org.geogebra.common.gui.inputfield.DynamicTextElement;
import org.geogebra.common.gui.inputfield.DynamicTextProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoElementSelectionListener;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.dom.client.FocusEvent;
import org.gwtproject.event.dom.client.FocusHandler;
import org.gwtproject.user.client.ui.FlowPanel;

/**
 * Panel to manage editing of GeoText strings.
 */
public class TextEditPanel extends FlowPanel
		implements FocusHandler, ITextEditPanel {
	private final AppW app;
	private final DynamicTextProcessor dTProcessor;
	/** editor */
	protected GeoTextEditor editor;
	private final TextPreviewPanelW previewer;
	/** GeoText edited by this panel */
	protected GeoText editGeo = null;
	private GeoElementSelectionListener sl;

	/**
	 * @param app - application
	 */
	public TextEditPanel(App app) {
		super();
		this.app = (AppW) app;

		dTProcessor = new DynamicTextProcessor(app);

		editor = new GeoTextEditor(app, this);
		editor.addStyleName("textEditor");

		previewer = new TextPreviewPanelW(app.getKernel());
		previewer.getPanel().setStyleName("previewPanel");

		// build our panel
		setSize("100%", "100%");
		add(editor);

		registerListeners();

		// force a dummy geo to be created on first use
		setEditGeo(null);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (!visible) {
			app.setSelectionListenerMode(null);
			previewer.removePreviewGeoText();
		} else {
			editor.updateFonts();
			previewer.updateFonts();
			updatePreviewPanel();
		}
	}

	@Override
	public void onFocus(FocusEvent event) {
		app.setSelectionListenerMode(sl);
	}

	/**
	 * Updates PreviewPanel with current editor content.
	 */
	@Override
	public void updatePreviewPanel() {
		updatePreviewPanel(false);
	}

	private void registerListeners() {
		sl = (geo, addToSelection) -> {
			if (geo != editGeo) {
				editor.insertGeoElement(geo);
			}
		};

		editor.addFocusHandler(this);
	}

	/**
	 * Change edited element.
	 * @param editGeo edited text element
	 */
	public void setEditGeo(GeoText editGeo) {
		if (editGeo == null) {
			// create dummy GeoText to maintain the visual properties
			this.editGeo = new GeoText(app.getKernel().getConstruction());
		} else {
			this.editGeo = editGeo;
		}
	}

	@Override
	public GeoText getEditGeo() {
		return editGeo;
	}

	/**
	 * @return editor area
	 */
	public GeoTextEditor getTextArea() {
		return editor;
	}

	/**
	 * Sets HTML content
	 * @param text HTML content
	 */
	public void setText(String text) {
		editor.getElement().setInnerHTML(text);
	}

	/**
	 * @return text definition
	 */
	public String getText() {
		return dTProcessor.buildGeoGebraString(editor.getDynamicTextList(),
				editGeo != null && editGeo.isLaTeX());
	}

	/**
	 * Sets editor content to represent the text string of a given GeoText.
	 * @param geo GeoText
	 */
	public void setText(GeoText geo) {
		ArrayList<DynamicTextElement> list = dTProcessor
				.buildDynamicTextList(geo);
		editor.setText(list);

		updatePreviewPanel();
	}

	/**
	 * Inserts into the editor a dynamic text element representing a given
	 * GeoElement. The element is inserted at the current caret position.
	 * @param geo element
	 */
	@Override
	public void insertGeoElement(GeoElement geo) {
		editor.insertGeoElement(geo);
	}

	/**
	 * Inserts a text string into the editor at the current caret position.
	 * @param text string literal
	 * @param isLatex whether it's latex
	 */
	@Override
	public void insertTextString(String text, boolean isLatex) {
		editor.insertTextString(text, isLatex);
	}

	@Override
	public void ensureLaTeX() {
		editGeo.setLaTeX(true, false);
		updatePreviewPanel();
	}

	@Override
	public void updatePreviewPanel(boolean byUser) {
		if (previewer == null) {
			return;
		}

		String inputValue = dTProcessor
				.buildGeoGebraString(editor.getDynamicTextList(), false);
		boolean isLatex = previewer.updatePreviewText(editGeo,
				inputValue, editGeo != null && editGeo.isLaTeX(), byUser);
		if (editGeo != null && editGeo.isLaTeX() != isLatex) {
			editGeo.setLaTeX(isLatex, false);
		}
	}

	public TextPreviewPanelW getPreviewer() {
		return previewer;
	}
}
