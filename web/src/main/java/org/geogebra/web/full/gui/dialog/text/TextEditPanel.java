package org.geogebra.web.full.gui.dialog.text;

import java.util.ArrayList;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.options.model.TextOptionsModel;
import org.geogebra.common.gui.inputfield.DynamicTextElement;
import org.geogebra.common.gui.inputfield.DynamicTextProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoElementSelectionListener;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.dialog.TextEditAdvancedPanel;
import org.geogebra.web.full.gui.util.ImageResourceConverter;
import org.geogebra.web.html5.gui.util.FastClickHandler;
import org.geogebra.web.html5.gui.util.ToggleButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel to manage editing of GeoText strings.
 */
public class TextEditPanel extends VerticalPanel
		implements FastClickHandler, FocusHandler, ITextEditPanel, SetLabels {

	private AppW app;
	private DynamicTextProcessor dTProcessor;
	/** editor */
	protected GeoTextEditor editor;
	private FlowPanel toolBar;
	private TextPreviewPanelW previewer;

	/** GeoText edited by this panel */
	protected GeoText editGeo = null;

	private ToggleButton btnBold;
	private ToggleButton btnItalic;
	private ToggleButton btnSerif;
	private ToggleButton btnLatex;
	private GeoElementSelectionListener sl;
	private DisclosurePanel disclosurePanel;
	private Localization loc;
	private TextEditAdvancedPanel advancedPanel;
	private boolean mayDetectLaTeX = true;

	/**
	 * @param app - application
	 */
	public TextEditPanel(App app) {
		super();
		this.app = (AppW) app;
		loc = app.getLocalization();

		dTProcessor = new DynamicTextProcessor(app);

		editor = new GeoTextEditor(app, this);
		editor.addStyleName("textEditor");

		createToolBar();

		advancedPanel = new TextEditAdvancedPanel(app, this);
		previewer = advancedPanel.getPreviewer();

		disclosurePanel = new DisclosurePanel(
				ImageResourceConverter
						.convertToOldImageResource(GuiResources.INSTANCE.triangle_down()),
				ImageResourceConverter
						.convertToOldImageResource(GuiResources.INSTANCE.triangle_right()),
				loc.getMenu("Advanced"));
		disclosurePanel.setContent(advancedPanel);
		disclosurePanel.getContent().removeStyleName("content");
		disclosurePanel.getContent()
				.addStyleName("textEditorDisclosurePanelContent");
		disclosurePanel.getHeader()
				.addStyleName("textEditorDisclosurePanelHeader");

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

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (!visible) {
			app.setSelectionListenerMode(null);
			previewer.removePreviewGeoText();
		} else {
			editor.updateFonts();
			previewer.updateFonts();
			advancedPanel.updateGeoList();
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

	/**
	 * Replaces _ to its unicode identifier on MOW
	 * @param app - The application.
	 * @param input - The text input.
	 * @param latex - true if text is LaTeX.
	 * @return the processed text.
	 */
	public static String handleUnderscores(App app, String input, boolean latex) {
		if (app.isWhiteboardActive() && !latex) {
			return input.replace("_", "\uFF3F");
		}
		return input;
	}

	@Override
	public void onClick(Widget source) {
		if (source == btnBold || source == btnItalic) {
			int style = 0;
			if (btnBold.isSelected()) {
				style += 1;
			}
			if (btnItalic.isSelected()) {
				style += 2;
			}
			editGeo.setFontStyle(style);
			updatePreviewPanel();
		} else if (source == btnLatex) {
			editGeo.setLaTeX(btnLatex.isSelected(), false);
			// latex detection override
			mayDetectLaTeX = btnLatex.isSelected();
			updatePreviewPanel();
		} else if (source == btnSerif) {
			editGeo.setSerifFont(btnSerif.isSelected());
			updatePreviewPanel();
		}
	}

	@Override
	public void setLabels() {
		disclosurePanel.getHeaderTextAccessor()
				.setText(loc.getMenu("Advanced"));
		btnLatex.setText(loc.getMenu("LaTeXFormula"));
		btnSerif.setText(loc.getMenu("Serif"));
		if (advancedPanel != null) {
			advancedPanel.setLabels();
		}
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
	 * @param editGeo - edited text element
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
	 * @return whether latex toggle button is checked
	 */
	public boolean isLatex() {
		return btnLatex.isSelected();
	}

	private boolean isSerif() {
		return btnSerif.isSelected();
	}

	private boolean isBold() {
		return btnBold.isSelected();
	}

	private boolean isItalic() {
		return btnItalic.isSelected();
	}

	/**
	 * @return editor area
	 */
	public GeoTextEditor getTextArea() {
		return editor;
	}

	private void createToolBar() {
		btnBold = new ToggleButton(MaterialDesignResources.INSTANCE.text_bold_black());
		btnBold.addFastClickHandler(this);
		btnBold.addStyleName("btnBold");

		btnItalic = new ToggleButton(MaterialDesignResources.INSTANCE.text_italic_black());
		btnItalic.addFastClickHandler(this);
		btnItalic.addStyleName("btnItalic");

		btnSerif = new ToggleButton(loc.getMenu("Serif"));
		btnSerif.addFastClickHandler(this);
		btnSerif.addStyleName("btnSerif");

		String latexTr = loc.getMenu("LaTeXFormula");
		btnLatex = new ToggleButton(latexTr);
		btnLatex.addFastClickHandler(this);
		btnLatex.addStyleName("btnLatex");

		HorizontalPanel leftPanel = new HorizontalPanel();
		leftPanel.setSpacing(2);
		leftPanel.add(btnBold);
		leftPanel.add(btnItalic);
		leftPanel.add(btnSerif);
		leftPanel.add(btnLatex);

		toolBar = new FlowPanel();
		toolBar.getElement().getStyle().setFloat(Style.Float.LEFT);
		toolBar.getElement().getStyle().setFontSize(80, Unit.PCT);
		toolBar.add(leftPanel);
	}

	/**
	 * @param text - content as HTML
	 */
	public void setText(String text) {
		editor.setHTML(text);
	}

	/**
	 * @return Current editor content converted to a GeoText string.
	 */
	public String getText() {
		Log.debug("ggb text string: " + dTProcessor
				.buildGeoGebraString(editor.getDynamicTextList(), isLatex()));
		return dTProcessor.buildGeoGebraString(editor.getDynamicTextList(),
				isLatex());
	}

	/**
	 * Sets editor content to represent the text string of a given GeoText.
	 * @param geo - GeoText
	 */
	public void setText(GeoText geo) {
		ArrayList<DynamicTextElement> list = dTProcessor
				.buildDynamicTextList(geo);
		editor.setText(list);

		if (geo == null) {
			btnLatex.setSelected(false);
			btnSerif.setSelected(false);
			btnBold.setSelected(false);
			btnItalic.setSelected(false);
		} else {
			btnLatex.setSelected(geo.isLaTeX());
			btnSerif.setSelected(geo.isSerifFont());
			int style = geo.getFontStyle();
			btnBold.setSelected(style == GFont.BOLD
					|| style == (GFont.BOLD + GFont.ITALIC));
			btnItalic.setSelected(style == GFont.ITALIC
					|| style == (GFont.BOLD + GFont.ITALIC));
		}

		updatePreviewPanel();
	}

	/**
	 * Inserts into the editor a dynamic text element representing a given
	 * GeoElement. The element is inserted at the current caret position.
	 * @param geo - element
	 */
	@Override
	public void insertGeoElement(GeoElement geo) {
		editor.insertGeoElement(geo);
	}

	/**
	 * Inserts a text string into the editor at the current caret position.
	 * @param text - string literal
	 * @param isLatex - whether it's latex
	 */
	@Override
	public void insertTextString(String text, boolean isLatex) {
		editor.insertTextString(text, isLatex);
	}

	private int getFontStyle() {
		return TextOptionsModel.getFontStyle(isBold(), isItalic());
	}

	@Override
	public void ensureLaTeX() {
		btnLatex.setSelected(true);
		editGeo.setLaTeX(true, false);
		updatePreviewPanel();
	}

	/**
	 * Apply style to text
	 * @param t - text to be updated
	 */
	public void updateTextStyle(GeoText t) {
		t.setLaTeX(isLatex(), true);
		t.setFontStyle(getFontStyle());
		t.setSerifFont(isSerif());
		// make sure for new LaTeX texts we get nice "x"s
		if (isLatex()) {
			t.setSerifFont(true);
		}
	}

	@Override
	public void updatePreviewPanel(boolean byUser) {
		if (previewer == null) {
			return;
		}
		boolean wasLaTeX = isLatex();
		String inputValue = dTProcessor
				.buildGeoGebraString(editor.getDynamicTextList(), isLatex());
		boolean isLaTeX = previewer.updatePreviewText(editGeo,
				handleUnderscores(app, inputValue, false), isLatex(),
				byUser && mayDetectLaTeX);
		if (!wasLaTeX && isLaTeX) {
			btnLatex.setSelected(true);
			if (editGeo != null) {
				editGeo.setLaTeX(true, false);
			}
		}
	}

	/**
	 * @return advanced panel of text tool dialog
	 */
	public DisclosurePanel getDisclosurePanel() {
		return disclosurePanel;
	}
}
