package geogebra.html5.gui.inputfield;

import geogebra.common.awt.GFont;
import geogebra.common.gui.inputfield.DynamicTextElement;
import geogebra.common.gui.inputfield.DynamicTextProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.App;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.common.main.Localization;
import geogebra.common.util.Unicode;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import java.util.ArrayList;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * Panel to manage editing of GeoText strings.
 * 
 * @author G. Sturr
 * 
 */
public class TextEditPanel extends VerticalPanel implements ClickHandler,
        FocusHandler, ITextEditPanel {

	protected AppW app;
	protected DynamicTextProcessor dTProcessor;
	protected GeoTextEditor editor;
	protected FlowPanel toolBar;
	protected TextPreviewPanelW previewer;

	protected ToggleButton btnInsert;
	private ToggleButton btnLatex;

	private boolean isSerif, isBold, isItalic;

	/** GeoText edited by this panel */
	protected GeoText editGeo = null;

	private ToggleButton btnBold, btnItalic, btnSerif;
	private GeoElementSelectionListener sl;
	private DisclosurePanel disclosurePanel;
	private Localization loc;
	private TextEditAdvancedPanel advancedPanel;

	/*****************************************************
	 * @param app
	 */
	public TextEditPanel(AppW app) {
		super();
		this.app = app;
		loc = app.getLocalization();

		dTProcessor = new DynamicTextProcessor(app);

		editor = new GeoTextEditor(app, this);
		editor.addStyleName("textEditor");
		
		createToolBar();
		
		// TODO: advancedPanel is slow loading. Make this happen on demand.
		advancedPanel = new TextEditAdvancedPanel(app, this);
		previewer = advancedPanel.getPreviewer();
		
		disclosurePanel = new DisclosurePanel(loc.getMenu("Advanced"));
		disclosurePanel.setContent(advancedPanel);
		disclosurePanel. getContent().removeStyleName("content");
		disclosurePanel.getContent().addStyleName(
		        "textEditorDisclosurePanelContent");
		disclosurePanel.getHeader().addStyleName(
		        "textEditorDisclosurePanelHeader");

		// build our panel
		setSize("100%", "100%");
		add(toolBar);
		add(editor);
		add(disclosurePanel);

		registerListeners();
		setLabels();

		// force a dummy geo to be created on first use
		setEditGeo(null);

	}

	// ======================================================
	// Event Handlers
	// ======================================================

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (!visible) {
			app.setSelectionListenerMode(null);
			previewer.removePreviewGeoText();
		} else {
			editor.updateFonts();
			previewer.updateFonts();
		}
	}

	public void onFocus(FocusEvent event) {
		app.setSelectionListenerMode(sl);
	}

	/**
	 * Updates PreviewPanel with current editor content.
	 */
	public void updatePreviewPanel() {

		if (previewer == null) {
			return;
		}

		previewer.updatePreviewText(editGeo, dTProcessor.buildGeoGebraString(
		        editor.getDynamicTextList(), isLatex()), isLatex());
	}

	public void onClick(ClickEvent event) {
		Object source = event.getSource();

		if (source == btnBold || source == btnItalic) {
			int style = 0;
			if (btnBold.getValue() == true)
				style += 1;
			if (btnItalic.getValue() == true)
				style += 2;
			editGeo.setFontStyle(style);
			updatePreviewPanel();

		} else if (source == btnLatex) {
			editGeo.setLaTeX(btnLatex.getValue(), false);
			updatePreviewPanel();

		} else if (source == btnSerif) {
			editGeo.setSerifFont(btnSerif.getValue());
			updatePreviewPanel();

		} else if (source == btnInsert) {
			if (btnInsert.isDown()) {
				// open a symbol table
			} else {
				// close a symbol table
			}
		}

	}

	public void setLabels() {

		disclosurePanel.getHeaderTextAccessor()
		        .setText(loc.getMenu("Advanced"));
		btnLatex.setText(loc.getPlain("LaTeXFormula"));
		btnSerif.setText("Serif");
		if (advancedPanel != null) {
			advancedPanel.setLabels();
		}

	}

	private void registerListeners() {

		sl = new GeoElementSelectionListener() {
			public void geoElementSelected(GeoElement geo,
			        boolean addToSelection) {
				if (geo != editGeo) {
					editor.insertGeoElement(geo);
				}
			}
		};

		editor.addFocusHandler(this);
	}

	// ======================================================
	// Getters/Setters
	// ======================================================

	public void setEditGeo(GeoText editGeo) {
		if (editGeo == null) {
			// create dummy GeoText to maintain the visual properties
			editGeo = new GeoText(app.getKernel().getConstruction());
		}
		this.editGeo = editGeo;
	}

	public GeoText getEditGeo() {
		return editGeo;
	}

	public boolean isLatex() {
		return btnLatex.getValue();
	}

	public boolean isSerif() {
		return btnSerif.getValue();
	}

	public boolean isBold() {
		return btnBold.getValue();
	}

	public boolean isItalic() {
		return btnItalic.getValue();
	}

	public GeoTextEditor getTextArea() {
		return editor;
	}

	// ======================================================
	// ToolBar
	// ======================================================

	private void createToolBar() {

		int iconHeight = 18;

		btnInsert = new ToggleButton(Unicode.alpha + "");
		btnInsert.addClickHandler(this);

		btnBold = new ToggleButton(new Image(AppResources.INSTANCE
		        .format_text_bold().getSafeUri().asString()));
		btnBold.addClickHandler(this);

		btnItalic = new ToggleButton(new Image(AppResources.INSTANCE
		        .format_text_italic().getSafeUri().asString()));
		btnItalic.addClickHandler(this);

		btnSerif = new ToggleButton("Serif");
		btnSerif.addClickHandler(this);

		btnLatex = new ToggleButton("LaTeX");
		btnLatex.addClickHandler(this);

		// TODO: put styles in css stylesheet
		btnBold.getElement().getStyle().setHeight(18, Unit.PX);
		btnItalic.getElement().getStyle().setHeight(18, Unit.PX);
		btnSerif.getElement().getStyle().setHeight(18, Unit.PX);
		btnLatex.getElement().getStyle().setHeight(18, Unit.PX);
		btnInsert.getElement().getStyle().setHeight(18, Unit.PX);

		HorizontalPanel leftPanel = new HorizontalPanel();
		leftPanel.setSpacing(2);
		// leftPanel.getElement().getStyle().setFloat(Style.Float.LEFT);
		leftPanel.add(btnBold);
		leftPanel.add(btnItalic);
		leftPanel.add(btnSerif);
		leftPanel.add(btnLatex);

		FlowPanel rightPanel = new FlowPanel();
		rightPanel.getElement().getStyle().setFloat(Style.Float.RIGHT);
		rightPanel.add(btnInsert);

		toolBar = new FlowPanel();
		// toolBar.setSize("100%", "20px");
		toolBar.getElement().getStyle().setFloat(Style.Float.LEFT);
		toolBar.getElement().getStyle().setFontSize(80, Unit.PCT);
		toolBar.add(leftPanel);
		// toolBar.add(rightPanel);

	}

	// ======================================================
	// Text Handlers
	// ======================================================

	public void setText(String text) {
		editor.setHTML(text);
	}

	/**
	 * @return Current editor content converted to a GeoText string.
	 */
	public String getText() {
		App.debug("ggb text string: "
		        + dTProcessor.buildGeoGebraString(editor.getDynamicTextList(),
		                isLatex()));
		return dTProcessor.buildGeoGebraString(editor.getDynamicTextList(),
		        isLatex());
	}

	/**
	 * Sets editor content to represent the text string of a given GeoText.
	 * 
	 * @param geo
	 *            GeoText
	 */
	public void setText(GeoText geo) {

		ArrayList<DynamicTextElement> list = dTProcessor
		        .buildDynamicTextList(geo);

		editor.setText(list);

		if (geo == null) {
			btnLatex.setValue(false);
			btnSerif.setValue(false);
			btnBold.setValue(false);
			btnItalic.setValue(false);

		} else {

			btnLatex.setValue(geo.isLaTeX());
			btnSerif.setValue(geo.isSerifFont());
			int style = geo.getFontStyle();
			btnBold.setValue(style == GFont.BOLD
			        || style == (GFont.BOLD + GFont.ITALIC));
			btnItalic.setValue(style == GFont.ITALIC
			        || style == (GFont.BOLD + GFont.ITALIC));
		}

		updatePreviewPanel();

	}

	/**
	 * Inserts into the editor a dynamic text element representing a given
	 * GeoElement. The element is inserted at the current caret position.
	 * 
	 * @param geo
	 */
	public void insertGeoElement(GeoElement geo) {
		editor.insertGeoElement(geo);

	}

	/**
	 * Inserts a text string into the editor at the current caret position.
	 * 
	 * @param text
	 * @param isLatex
	 */
	public void insertTextString(String text, boolean isLatex) {
		editor.insertTextString(text, isLatex);
	}
	
	public void updateFonts(){
		editor.updateFonts();
	}

}
