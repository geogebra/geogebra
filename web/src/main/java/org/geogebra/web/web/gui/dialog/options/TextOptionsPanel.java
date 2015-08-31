package org.geogebra.web.web.gui.dialog.options;

import java.util.ArrayList;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.gui.dialog.options.model.TextOptionsModel;
import org.geogebra.common.gui.dialog.options.model.TextOptionsModel.ITextOptionsListener;
import org.geogebra.common.gui.inputfield.DynamicTextElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.GeoElementSelectionListener;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.gui.inputfield.GeoTextEditor;
import org.geogebra.web.html5.gui.inputfield.ITextEditPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.dialog.TextEditAdvancedPanel;
import org.geogebra.web.web.gui.dialog.TextPreviewPanelW;
import org.geogebra.web.web.gui.properties.OptionPanel;
import org.geogebra.web.web.gui.properties.PropertiesViewW;
import org.geogebra.web.web.gui.util.MyToggleButton2;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ToggleButton;

class TextOptionsPanel extends OptionPanel implements ITextOptionsListener,
		ITextEditPanel, GeoElementSelectionListener {
	/**
		 * 
		 */

	TextOptionsModel model;

	private Label decimalLabel;
	ListBox lbFont;
	ListBox lbSize;
	ListBox lbDecimalPlaces;
	MyToggleButton2 btnBold;
	MyToggleButton2 btnItalic;
	private ToggleButton btnLatex;

	private FlowPanel secondLine;

	private FlowPanel editorPanel;
	private FlowPanel btnPanel;
	private Button btnOk;
	private Button btnCancel;

	private boolean secondLineVisible = false;
	GeoTextEditor editor;
	private TextEditAdvancedPanel advancedPanel;
	private TextPreviewPanelW previewer;
	private Localization loc;

	private AppW app;

	public TextOptionsPanel(TextOptionsModel model, AppW app) {
		createGUI(model, app);
	}

	public void createGUI(TextOptionsModel model0, final AppW app) {
		loc = app.getLocalization();
		this.app = app;
		model = model0;
		model.setListener(this);
		setModel(model);
		editor = null;
		editor = new GeoTextEditor(app, this);
		editor.setStyleName("objectPropertiesTextEditor");
		lbFont = new ListBox();
		for (String item : model.getFonts()) {
			lbFont.addItem(item);
		}

		lbFont.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				model.setEditGeoText(editor.getText());
				model.applyFont(lbFont.getSelectedIndex() == 1);
			}
		});
		lbSize = new ListBox();
		for (String item : model.getFonts()) {
			lbSize.addItem(item);
		}
		lbSize.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				model.setEditGeoText(editor.getText());
				boolean isCustom = (lbSize.getSelectedIndex() == 7);
				if (isCustom) {
					String currentSize = Math
							.round(model.getTextPropertiesAt(0)
									.getFontSizeMultiplier() * 100)
							+ "%";

					app.getGuiManager()
							.getOptionPane()
							.showInputDialog(
									app,
									app
							.getLocalization().getPlain("EnterPercentage"),
							currentSize, null, new AsyncOperation() {

								@Override
								public void callback(Object obj) {
									String[] dialogResult = (String[]) obj;
									model.applyFontSizeFromString(dialogResult[1]);
								}
							});

				} else {
					model.applyFontSizeFromIndex(lbSize.getSelectedIndex());
				}
				updatePreview();
			}
		});

		// font size
		// TODO require font phrases F.S.
		// toggle buttons for bold and italic
		btnBold = new MyToggleButton2(app.getMenu("Bold.Short"));
		btnBold.addStyleName("btnBold");

		btnItalic = new MyToggleButton2(app.getMenu("Italic.Short"));
		btnItalic.addStyleName("btnItalic");

		btnBold.setToolTipText(loc.getPlainTooltip("stylebar.Bold"));
		btnItalic.setToolTipText(loc.getPlainTooltip("stylebar.Italic"));

		btnLatex = new MyToggleButton2("LaTeX");

		// hack
		// btnLatex.getElement().getStyle().setWidth(100, Unit.PX);

		ClickHandler styleClick = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				model.setEditGeoText(editor.getText());
				model.applyFontStyle(btnBold.getValue(), btnItalic.getValue());
				updatePreview();
			}
		};

		btnBold.addClickHandler(styleClick);
		btnItalic.addClickHandler(styleClick);

		btnLatex.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				model.setLaTeX(isLatex(), true);
				updatePreview();
			}
		});
		btnLatex.addStyleName("btnLatex");

		// decimal places
		lbDecimalPlaces = new ListBox();
		for (String item : loc.getRoundingMenu()) {
			lbDecimalPlaces.addItem(item);
		}

		lbDecimalPlaces.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				model.setEditGeoText(editor.getText());
				model.applyDecimalPlaces(lbDecimalPlaces.getSelectedIndex());
				updatePreview();
			}
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
		firstLine.add(btnLatex);

		// bold, italic
		secondLine = new FlowPanel();
		secondLine.setStyleName("optionsPanel");
		decimalLabel = new Label();
		secondLine.add(decimalLabel);
		secondLine.add(lbDecimalPlaces);

		mainPanel.add(firstLine);
		mainPanel.add(secondLine);
		secondLineVisible = true;

		editorPanel = new FlowPanel();
		editorPanel.setStyleName("optionsInput");
		editorPanel.add(editor);
		advancedPanel = new TextEditAdvancedPanel(app, this);
		editorPanel.add(advancedPanel);
		mainPanel.add(editorPanel);

		previewer = advancedPanel.getPreviewer();

		btnPanel = new FlowPanel();
		btnPanel.setStyleName("optionsPanel");
		btnOk = new Button();
		btnPanel.add(btnOk);
		btnOk.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				model.applyEditedGeo(editor.getDynamicTextList(), isLatex(),
						isSerif());
				((PropertiesViewW) app.getGuiManager().getPropertiesView())
						.getOptionPanel(OptionType.OBJECTS, 1);
			}
		});

		btnCancel = new Button();
		btnPanel.add(btnCancel);
		btnCancel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				model.cancelEditGeo();
			}
		});

		mainPanel.add(btnPanel);
		setWidget(mainPanel);
	}

	protected boolean isSerif() {
		return lbFont.getSelectedIndex() == 1;
	}

	/**
	 * The editor must be recreated each time the options panel is re-attached
	 * to the DOM
	 */
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

		getModel().updateProperties();
		setLabels();
		advancedPanel.updateGeoList();
		if (getModel().hasPreview()) {
			updatePreview();
			editor.updateFonts();
		}

		return this;

	}

	@Override
	public void setLabels() {
		String[] fontSizes = loc.getFontSizeStrings();

		int selectedIndex = lbSize.getSelectedIndex();
		lbSize.clear();

		for (int i = 0; i < fontSizes.length; ++i) {
			lbSize.addItem(fontSizes[i]);
		}

		lbSize.addItem(loc.getMenu("Custom") + "...");

		lbSize.setSelectedIndex(selectedIndex);

		decimalLabel.setText(loc.getMenu("Rounding") + ":");

		btnBold.setText(loc.getMenu("Bold.Short"));
		btnItalic.setText(loc.getMenu("Italic.Short"));

		btnLatex.setText(loc.getPlain("LaTeXFormula"));
		btnBold.setToolTipText(loc.getPlainTooltip("stylebar.Bold"));
		btnItalic.setToolTipText(loc.getPlainTooltip("stylebar.Italic"));

		if (advancedPanel != null) {
			advancedPanel.setLabels();
		}
		btnOk.setText(loc.getMenu("OK"));
		btnCancel.setText(loc.getMenu("Cancel"));
	}

	@Override
	public void setWidgetsVisible(boolean showFontDetails, boolean isButton) {
		// hide most options for Textfields
		lbFont.setVisible(showFontDetails);
		btnBold.setVisible(showFontDetails);
		btnItalic.setVisible(showFontDetails);
		secondLine.setVisible(showFontDetails);
		secondLineVisible = showFontDetails;

		if (isButton) {
			secondLine.setVisible(!showFontDetails);
			secondLineVisible = !showFontDetails;
		}
	}

	@Override
	public void setFontSizeVisibleOnly() {
		lbSize.setVisible(true);
		lbFont.setVisible(false);
		btnBold.setVisible(false);
		btnItalic.setVisible(false);
		secondLine.setVisible(false);
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
	public void setSecondLineVisible(boolean noDecimals) {
		if (noDecimals) {

			if (secondLineVisible) {
				secondLineVisible = false;
			}
		} else {
			if (!secondLineVisible) {
				secondLineVisible = true;
			}

			secondLine.setVisible(secondLineVisible);
		}

		editorPanel.setVisible(model.isTextEditable());
		lbFont.setVisible(model.isTextEditable());
		btnLatex.setVisible(model.isTextEditable());
		btnPanel.setVisible(model.isTextEditable());

	}

	@Override
	public void updatePreview() {
		updatePreviewPanel();
	}

	boolean isLatex() {
		return btnLatex.getValue();
	}

	@Override
	public void selectFontStyle(int style) {

		btnBold.setValue(style == GFont.BOLD
				|| style == (GFont.BOLD + GFont.ITALIC));
		btnItalic.setValue(style == GFont.ITALIC
				|| style == (GFont.BOLD + GFont.ITALIC));

	}

	@Override
	public void updatePreviewPanel() {
		if (previewer == null) {
			return;
		}
		previewer.updateFonts();
		previewer
				.updatePreviewText(model.getEditGeo(), model.getGeoGebraString(
						editor.getDynamicTextList(), isLatex()), isLatex());
	}

	@Override
	public void setEditorText(ArrayList<DynamicTextElement> list) {

		editor.setText(list);

	}

	@Override
	public void setEditorText(String text) {

		editor.setText(text);

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

}