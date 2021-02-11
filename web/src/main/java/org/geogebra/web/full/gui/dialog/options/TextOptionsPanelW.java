package org.geogebra.web.full.gui.dialog.options;

import java.util.ArrayList;

import javax.annotation.CheckForNull;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.gui.dialog.options.model.TextOptionsModel;
import org.geogebra.common.gui.dialog.options.model.TextOptionsModel.ITextOptionsListener;
import org.geogebra.common.gui.inputfield.DynamicTextElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.GeoElementSelectionListener;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.dialog.TextEditAdvancedPanel;
import org.geogebra.web.full.gui.dialog.TextPreviewPanelW;
import org.geogebra.web.full.gui.properties.OptionPanel;
import org.geogebra.web.full.gui.properties.PropertiesViewW;
import org.geogebra.web.full.gui.util.InlineTextFormatter;
import org.geogebra.web.full.gui.util.MyToggleButtonW;
import org.geogebra.web.html5.gui.inputfield.GeoTextEditor;
import org.geogebra.web.html5.gui.inputfield.ITextEditPanel;
import org.geogebra.web.html5.gui.util.GToggleButton;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.himamis.retex.editor.share.util.Unicode;

class TextOptionsPanelW extends OptionPanel implements ITextOptionsListener,
		ITextEditPanel, GeoElementSelectionListener {

	TextOptionsModel model;

	private Label decimalLabel;
	ListBox lbFont;
	ListBox lbSize;
	ListBox lbDecimalPlaces;
	MyToggleButtonW btnBold;
	MyToggleButtonW btnItalic;
	@CheckForNull MyToggleButtonW btnUnderline;
	private GToggleButton btnLatex;

	private FlowPanel secondLine;

	private FlowPanel editorPanel;
	private FlowPanel btnPanel;
	private Button btnOk;
	private Button btnCancel;

	GeoTextEditor editor;
	private TextEditAdvancedPanel advancedPanel;
	private Localization loc;

	private AppW app;

	private boolean mayDetectLaTeX = true;

	private InlineTextFormatter inlineFormatter;

	public TextOptionsPanelW(TextOptionsModel model, AppW app) {
		createGUI(model, app);
		inlineFormatter = new InlineTextFormatter();
	}

	public void createGUI(TextOptionsModel model0, final AppW appw) {
		loc = appw.getLocalization();
		this.app = appw;
		model = model0;
		model.setListener(this);
		setModel(model);
		editor = null;
		editor = new GeoTextEditor(appw, this);
		editor.setStyleName("objectPropertiesTextEditor");
		lbFont = new ListBox();
		for (String item : model.getFonts()) {
			lbFont.addItem(item);
		}

		lbFont.addChangeHandler(event -> {
			model.setEditGeoText(editor.getText());
			model.applyFont(lbFont.getSelectedIndex() == 1);
		});
		lbSize = new ListBox();

		lbSize.addChangeHandler(event -> {
			model.setEditGeoText(editor.getText());
			boolean isCustom = (lbSize.getSelectedIndex() == 7);
			if (isCustom) {
				String currentSize = Math
						.round(model.getTextPropertiesAt(0)
								.getFontSizeMultiplier() * 100)
						+ "%";

				AsyncOperation<String[]> customSizeHandler =
						dialogResult -> model.applyFontSizeFromString(dialogResult[1]);
				appw.getGuiManager()
						.getOptionPane()
						.showInputDialog(
								appw
										.getLocalization()
										.getMenu("EnterPercentage"),
								currentSize, null,
								customSizeHandler);

			} else {
				model.applyFontSizeFromIndex(lbSize.getSelectedIndex());
				double size = GeoText
						.getRelativeFontSize(lbSize.getSelectedIndex())
						* app.getActiveEuclidianView().getFontSize();
				inlineFormat("size", size);
			}
			updatePreviewPanel();
		});

		btnItalic = new MyToggleButtonW(
				new ImageResourcePrototype(
						null, MaterialDesignResources.INSTANCE
								.text_italic_black().getSafeUri(),
						0, 0, 24, 24, false, false));
		btnItalic.addStyleName("btnItalic");

		btnBold = new MyToggleButtonW(
				new ImageResourcePrototype(
						null, MaterialDesignResources.INSTANCE
								.text_bold_black().getSafeUri(),
						0, 0, 24, 24, false, false));
		btnBold.addStyleName("btnBold");

		if (app.isWhiteboardActive()) {
			btnUnderline = new MyToggleButtonW(new NoDragImage(
					MaterialDesignResources.INSTANCE.text_underline_black(), 24));
			btnUnderline.addStyleName("btnUnderline");
		}

		btnLatex = new MyToggleButtonW("LaTeX");

		addStyleClickListener("bold", GFont.BOLD, btnBold);
		addStyleClickListener("italic", GFont.ITALIC, btnItalic);
		if (btnUnderline != null) {
			addStyleClickListener("underline", GFont.UNDERLINE, btnUnderline);
		}

		btnLatex.addClickHandler(event -> {
			model.setLaTeX(isLatex(), true);
			// manual override -> ignore autodetect
			mayDetectLaTeX = isLatex();

			updatePreviewPanel();
		});
		btnLatex.addStyleName("btnLatex");

		// decimal places
		lbDecimalPlaces = new ListBox();
		for (String item : loc.getRoundingMenu()) {
			lbDecimalPlaces.addItem(item);
		}

		lbDecimalPlaces.addChangeHandler(event -> {
			model.setEditGeoText(editor.getText());
			model.applyDecimalPlaces(lbDecimalPlaces.getSelectedIndex());
			updatePreviewPanel();
		});

		// font, size
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.setStyleName("textPropertiesTab");
		FlowPanel firstLine = new FlowPanel();
		firstLine.setStyleName("textOptionsToolBar");
		firstLine.add(lbFont);
		firstLine.add(lbSize);
		firstLine.add(btnBold);
		firstLine.add(btnItalic);
		if (btnUnderline != null) {
			firstLine.add(btnUnderline);
		}
		firstLine.add(btnLatex);

		// bold, italic
		secondLine = new FlowPanel();
		secondLine.setStyleName("optionsPanel");
		decimalLabel = new Label();
		secondLine.add(decimalLabel);
		secondLine.add(lbDecimalPlaces);

		mainPanel.add(firstLine);
		mainPanel.add(secondLine);

		editorPanel = new FlowPanel();
		editorPanel.setStyleName("optionsInput");
		editorPanel.add(editor);
		advancedPanel = new TextEditAdvancedPanel(appw, this);
		editorPanel.add(advancedPanel);
		mainPanel.add(editorPanel);

		btnPanel = new FlowPanel();
		btnPanel.setStyleName("optionsPanel");
		btnOk = new Button();
		btnPanel.add(btnOk);
		btnOk.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				model.applyEditedGeo(editor.getDynamicTextList(), isLatex(),
						isSerif(), appw.getDefaultErrorHandler());
				((PropertiesViewW) appw.getGuiManager().getPropertiesView())
						.getOptionPanel(OptionType.OBJECTS, 1);
			}
		});

		btnCancel = new Button();
		btnPanel.add(btnCancel);
		btnOk.addStyleName("okBtn");
		btnCancel.addStyleName("cancelBtn");
		btnCancel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				model.cancelEditGeo();
			}
		});

		mainPanel.add(btnPanel);
		setWidget(mainPanel);
	}

	private void addStyleClickListener(final String propertyName, final int mask,
									   final MyToggleButtonW toggle) {
		toggle.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				model.setEditGeoText(editor.getText());
				model.applyFontStyle(mask, toggle.getValue());
				inlineFormat(propertyName, toggle.getValue());
				updatePreviewPanel();
			}
		});
	}

	protected void inlineFormat(String key, Object val) {
		inlineFormatter.formatInlineText(model.getGeosAsList(), key, val);
	}

	protected boolean isSerif() {
		return lbFont.getSelectedIndex() == 1;
	}

	/**
	 * The editor must be recreated each time the options panel is re-attached
	 * to the DOM
	 */
	@Override
	public void reinitEditor() {

		int index = editorPanel.getWidgetIndex(editor);
		editorPanel.remove(editor);

		editor = new GeoTextEditor(this.app, this);
		editor.setStyleName("objectPropertiesTextEditor");
		editorPanel.insert(editor, index);
	}

	@Override
	public OptionPanel updatePanel(Object[] geos) {

		getModel().setGeos(geos);

		if (!getModel().checkGeos()) {
			model.cancelEditGeo();
			return null;
		}
		if (geos.length > 0 && geos[0] instanceof GeoText) {
			btnLatex.setValue(((GeoText) geos[0]).isLaTeX());
		}
		getModel().updateProperties();
		setLabels();
		advancedPanel.updateGeoList();
		if (model.isTextEditable()) {
			updatePreviewPanel();
			editor.updateFonts();
		}

		return this;
	}

	@Override
	public void setLabels() {
		rebuildSizeListBox();

		decimalLabel.setText(loc.getMenu("Rounding") + ":");

		btnLatex.setText(loc.getMenu("LaTeXFormula"));
		btnBold.setToolTipText(loc.getPlainTooltip("stylebar.Bold"));
		btnItalic.setToolTipText(loc.getPlainTooltip("stylebar.Italic"));

		if (advancedPanel != null) {
			advancedPanel.setLabels();
		}
		btnOk.setText(loc.getMenu("OK"));
		btnCancel.setText(loc.getMenu("Cancel"));
	}

	private void rebuildSizeListBox() {
		String[] fontSizes = loc.getFontSizeStrings();

		int selectedIndex = lbSize.getSelectedIndex();
		lbSize.clear();

		for (String fontSize : fontSizes) {
			lbSize.addItem(fontSize);
		}
		if (model.hasGeos() && !(model.getGeoAt(0) instanceof GeoInlineText)) {
			lbSize.addItem(loc.getMenu("Custom") + Unicode.ELLIPSIS);
		}

		lbSize.setSelectedIndex(selectedIndex);
	}

	@Override
	public void selectSize(int index) {
		lbSize.setSelectedIndex(index);
	}

	@Override
	public void selectFont(int index) {
		lbFont.setSelectedIndex(index);
	}

	@Override
	public void selectDecimalPlaces(int index) {
		lbDecimalPlaces.setSelectedIndex(index);
	}

	@Override
	public void updateWidgetVisibility() {
		secondLine.setVisible(model.hasRounding());
		editorPanel.setVisible(model.isTextEditable());
		lbFont.setVisible(model.hasGeos());
		btnBold.setVisible(model.hasFontStyle());
		btnItalic.setVisible(model.hasFontStyle());
		btnLatex.setVisible(model.isTextEditable());
		btnPanel.setVisible(model.isTextEditable());
	}

	boolean isLatex() {
		return btnLatex.getValue();
	}

	@Override
	public void selectFontStyle(int style) {
		btnBold.setValue((style & GFont.BOLD) != 0);
		btnItalic.setValue((style & GFont.ITALIC) != 0);
		if (btnUnderline != null) {
			btnUnderline.setValue((style & GFont.UNDERLINE) != 0);
		}
	}

	@Override
	public void updatePreviewPanel() {
		updatePreviewPanel(false);
	}
	
	@Override
	public void updatePreviewPanel(boolean byUser) {
		if (!model.isTextEditable()) {
			return;
		}
		TextPreviewPanelW previewer = advancedPanel.getPreviewer();
		previewer.updateFonts();
		boolean wasLaTeX = isLatex();
		boolean isLaTeX = previewer
				.updatePreviewText(model.getEditGeo(), model.getGeoGebraString(
						editor.getDynamicTextList(), isLatex()), isLatex(),
						byUser && mayDetectLaTeX);
		if (!wasLaTeX && isLaTeX) {
			btnLatex.setValue(true);
			if (model.getEditGeo() != null) {
				model.getEditGeo().setLaTeX(true, false);
			}
		}
	}

	@Override
	public void setEditorText(ArrayList<DynamicTextElement> list) {
		editor.setText(list);
	}

	@Override
	public void insertGeoElement(GeoElement geo) {
		editor.insertGeoElement(geo);
	}

	@Override
	public void insertTextString(String text, boolean isLatex) {
		editor.insertTextString(text, isLatex);

	}

	@Override
	public GeoText getEditGeo() {
		return model.getEditGeo();
	}

	@Override
	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		model.cancelEditGeo();

	}

	@Override
	public void ensureLaTeX() {
		btnLatex.setValue(true);
		model.getEditGeo().setLaTeX(true, false);
		updatePreviewPanel();
	}

}