package org.geogebra.web.full.gui.dialog.options;

import java.util.ArrayList;

import javax.annotation.CheckForNull;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.gui.dialog.options.model.TextOptionsModel;
import org.geogebra.common.gui.dialog.options.model.TextOptionsModel.ITextOptionsListener;
import org.geogebra.common.gui.inputfield.DynamicTextElement;
import org.geogebra.common.kernel.InlineTextFormatter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.GeoElementSelectionListener;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.full.gui.dialog.TextEditAdvancedPanel;
import org.geogebra.web.full.gui.dialog.text.GeoTextEditor;
import org.geogebra.web.full.gui.dialog.text.ITextEditPanel;
import org.geogebra.web.full.gui.dialog.text.TextPreviewPanelW;
import org.geogebra.web.full.gui.properties.OptionPanel;
import org.geogebra.web.full.gui.properties.PropertiesViewW;
import org.geogebra.web.html5.gui.util.ToggleButton;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.ListBox;

import com.himamis.retex.editor.share.util.Unicode;

class TextOptionsPanelW extends OptionPanel implements ITextOptionsListener,
		ITextEditPanel, GeoElementSelectionListener {

	TextOptionsModel model;

	private Label decimalLabel;
	ListBox lbFont;
	ListBox lbSize;
	ListBox lbDecimalPlaces;
	ToggleButton btnBold;
	ToggleButton btnItalic;
	@CheckForNull ToggleButton btnUnderline;
	private ToggleButton btnLatex;

	private FlowPanel secondLine;

	private FlowPanel editorPanel;
	private FlowPanel btnPanel;
	private StandardButton btnOk;
	private StandardButton btnCancel;

	GeoTextEditor editor;
	private TextEditAdvancedPanel advancedPanel;
	private Localization loc;

	private AppW app;

	private boolean mayDetectLaTeX = true;

	private final InlineTextFormatter inlineFormatter;

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
			boolean isSerif = lbFont.getSelectedIndex() == 1;
			saveEditorChanges();
			model.applyFont(isSerif);
		});
		lbSize = new ListBox();

		lbSize.addChangeHandler(event -> {
			int selectedIndex = lbSize.getSelectedIndex();
			saveEditorChanges();
			boolean isCustom = selectedIndex == 7;
			if (isCustom) {
				String currentSize = Math
						.round(model.getTextPropertiesAt(0)
								.getFontSizeMultiplier() * 100)
						+ "%";

				DialogData data = new DialogData("EnterPercentage", "Cancel", "OK");
				ComponentDialog dialog = new ComponentDialog(appw, data, false, true);
				ComponentInputField inputTextField = new ComponentInputField(app, "", "", "",
						currentSize, -1, "");
				dialog.addDialogContent(inputTextField);
				dialog.setOnPositiveAction(() ->
						model.applyFontSizeFromString(inputTextField.getText()));
				dialog.show();
			} else {
				model.applyFontSizeFromIndex(selectedIndex);
				double size = GeoText
						.getRelativeFontSize(selectedIndex)
						* app.getActiveEuclidianView().getFontSize();
				inlineFormat("size", size);
			}
			updatePreviewPanel();
		});

		btnItalic = new ToggleButton(MaterialDesignResources.INSTANCE.text_italic_black());
		btnItalic.addStyleName("btnItalic");

		btnBold = new ToggleButton(MaterialDesignResources.INSTANCE.text_bold_black());
		btnBold.addStyleName("btnBold");

		if (app.isWhiteboardActive()) {
			btnUnderline = new ToggleButton(
					MaterialDesignResources.INSTANCE.text_underline_black());
			btnUnderline.addStyleName("btnUnderline");
		}

		btnLatex = new ToggleButton("LaTeX");

		addStyleClickListener("bold", GFont.BOLD, btnBold);
		addStyleClickListener("italic", GFont.ITALIC, btnItalic);
		if (btnUnderline != null) {
			addStyleClickListener("underline", GFont.UNDERLINE, btnUnderline);
		}

		btnLatex.addFastClickHandler(event -> {
			boolean latex = isLatex();
			saveEditorChanges();
			model.setLaTeX(latex, true);
			// manual override -> ignore autodetect
			mayDetectLaTeX = latex;

			updatePreviewPanel();
		});
		btnLatex.addStyleName("btnLatex");

		// decimal places
		lbDecimalPlaces = new ListBox();
		for (String item : loc.getRoundingMenu()) {
			lbDecimalPlaces.addItem(item);
		}

		lbDecimalPlaces.addChangeHandler(event -> {
			int selectedIndex = lbDecimalPlaces.getSelectedIndex();
			saveEditorChanges();
			model.applyDecimalPlaces(selectedIndex);
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
		btnOk = new StandardButton("");
		btnPanel.add(btnOk);
		btnOk.addFastClickHandler(event -> {
			saveEditorChanges();
			((PropertiesViewW) appw.getGuiManager().getPropertiesView())
					.getOptionPanel(OptionType.OBJECTS, 1);
		});

		btnCancel = new StandardButton("");
		btnPanel.add(btnCancel);
		btnOk.addStyleName("textButton");
		btnCancel.addStyleName("textButton");
		btnCancel.addFastClickHandler(event -> {
			model.cancelEditGeo();
			model.updateProperties();
			updatePreviewPanel(false);
		});

		mainPanel.add(btnPanel);
		setWidget(mainPanel);
	}

	private void addStyleClickListener(final String propertyName, final int mask,
			final ToggleButton toggle) {
		toggle.addFastClickHandler(event -> {
			boolean selected = toggle.isSelected();
			saveEditorChanges();
			model.applyFontStyle(mask, selected);
			inlineFormat(propertyName, selected);
			updatePreviewPanel();
		});
	}

	private void saveEditorChanges() {
		if (model.isTextEditable()) {
			model.applyEditedGeo(editor.getDynamicTextList(), isLatex(),
					app.getDefaultErrorHandler());
		}
	}

	protected void inlineFormat(String key, Object val) {
		inlineFormatter.formatInlineText(model.getGeosAsList(), key, val);
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
			btnLatex.setSelected(((GeoText) geos[0]).isLaTeX());
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
		btnBold.setTitle(loc.getPlainTooltip("stylebar.Bold"));
		btnItalic.setTitle(loc.getPlainTooltip("stylebar.Italic"));

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
		return btnLatex.isSelected();
	}

	@Override
	public void selectFontStyle(int style) {
		btnBold.setSelected((style & GFont.BOLD) != 0);
		btnItalic.setSelected((style & GFont.ITALIC) != 0);
		if (btnUnderline != null) {
			btnUnderline.setSelected((style & GFont.UNDERLINE) != 0);
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
			btnLatex.setSelected(true);
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
		btnLatex.setSelected(true);
		model.getEditGeo().setLaTeX(true, false);
		updatePreviewPanel();
	}
}