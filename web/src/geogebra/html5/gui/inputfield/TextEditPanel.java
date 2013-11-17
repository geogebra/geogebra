package geogebra.html5.gui.inputfield;

import geogebra.common.awt.GFont;
import geogebra.common.gui.inputfield.DynamicTextElement;
import geogebra.common.gui.inputfield.DynamicTextProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.App;
import geogebra.common.util.Unicode;
import geogebra.html5.awt.GFontW;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import java.util.ArrayList;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
public class TextEditPanel extends VerticalPanel implements ClickHandler {

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
	private ToggleButton btnBold;
	private ToggleButton btnItalic;
	private ToggleButton btnSerif;

	/*****************************************************
	 * @param app
	 */
	public TextEditPanel(AppW app) {
		super();
		this.app = app;

		dTProcessor = new DynamicTextProcessor(app);

		editor = new GeoTextEditor(app, this);
		editor.setSize("350px", "120px");
		// TODO: styling done in stylesheet
		editor.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		editor.getElement().getStyle().setBorderColor("lightgray");
		editor.getElement().getStyle().setBorderWidth(1, Unit.PX);

		// TODO: this panel is slow loading. Make this happen on demand ...
		// after disclosure opens
		TextEditInsertPanel insertPanel = new TextEditInsertPanel(app, this);
		previewer = insertPanel.getPreviewer();

		
		createToolBar();

		//VerticalPanel v = new VerticalPanel();
		//v.add(toolBar);
		//v.add(insertPanel);
		
		DisclosurePanel d = new DisclosurePanel("Options");
		d.setContent(insertPanel);
		d.getContent().getElement().getStyle().setMargin(0, Unit.PX);
		d.getContent().getElement().getStyle().setPadding(0, Unit.PX);
		d.getContent().getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		d.getHeader().getElement().getStyle().setFontSize(80, Unit.PCT);

		setSize("100%", "100%");
		add(toolBar);
		add(editor);
		add(d);
		
		// force a dummy geo on first use
		setEditGeo(null);

	}

	// ======================================================
	// Event Handlers
	// ======================================================

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (!visible) {
			previewer.removePreviewGeoText();
		}
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

		btnLatex = new ToggleButton("Latex");
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
		//toolBar.add(rightPanel);

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

	// to be removed ----------------------------------------------------------

	final CssColor colorBlack = CssColor.make("black");

	protected String createTextImageURL(String str) {

		Canvas canvas = Canvas.createIfSupported();
		Context2d context = canvas.getContext2d();

		String fontSize = ((GFontW) app.getPlainFontCommon()).getFontSize();
		String fontString = fontSize + "pt sans-serif";
		// App.debug(fontString);

		context.setFont(fontString);
		TextMetrics fm = context.measureText(str);
		int w = (int) fm.getWidth() + 4;
		int h = Integer.parseInt(fontSize) + 8; // TODO: better height calc
		canvas.setWidth(w + "px");
		canvas.setHeight(h + "px");
		canvas.setCoordinateSpaceWidth(w);
		canvas.setCoordinateSpaceHeight(h);

		context.setFont(fontString);
		context.setTextBaseline(TextBaseline.TOP);
		context.setFillStyle(colorBlack);
		context.fillText(str, 2, 1);

		return canvas.toDataUrl();
	}

}
