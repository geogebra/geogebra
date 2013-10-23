package geogebra.html5.gui.inputfield;

import geogebra.common.gui.inputfield.DynamicTextElement;
import geogebra.common.gui.inputfield.DynamicTextElement.DynamicTextType;
import geogebra.common.gui.inputfield.DynamicTextProcessor;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.App;
import geogebra.common.main.GWTKeycodes;
import geogebra.html5.awt.GFontW;
import geogebra.web.main.AppW;

import java.util.ArrayList;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * Panel for editing GeoText strings.
 * 
 * @author G. Sturr
 * 
 */
public class TextEditPanel extends VerticalPanel {

	AppW app;

	DynamicTextProcessor dTProcessor;
	final CssColor colorBlack = CssColor.make("black");

	RichTextAreaEditor rta;
	Formatter formatter;
	HorizontalPanel toolBar;

	GeoText editGeo = null;;

	private Button openButton;
	ToggleButton btInsertGeo;
	private ListBox geoListBox;
	GeoListPopup geoListPopup;

	protected PopupPanel textEditPopup;
	protected MyEditBox editBox;

	/**
	 * @param app
	 */
	public TextEditPanel(AppW app) {
		super();
		this.app = app;
		dTProcessor = new DynamicTextProcessor(app);

		rta = new RichTextAreaEditor((GFontW) app.getPlainFontCommon());

		formatter = rta.getFormatter();
		rta.setSize("350px", "8em");
		rta.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		rta.getElement().getStyle().setBorderColor("lightgray");
		rta.getElement().getStyle().setBorderWidth(1, Unit.PX);

		rta.addValueChangeHandler(new ValueChangeHandler() {
			public void onValueChange(ValueChangeEvent event) {
				App.debug("value changed:");
			}
		});

		createToolBar();
		this.add(rta);
		this.add(toolBar);

		createEditPopup();

		rta.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Element target = Element.as(event.getNativeEvent()
				        .getEventTarget());

				textEditPopup.setVisible(false);

				if ("input".equalsIgnoreCase(target.getTagName())) {

					String value = DOM.getElementAttribute(
					        (com.google.gwt.user.client.Element) target,
					        "value");
					App.debug(value);

					editBox.setText(value);
					editBox.setTarget(target);
					showEditPopup();

				}
			}
		});

	}

	protected void showEditPopup() {

		textEditPopup
		        .setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			        public void setPosition(int offsetWidth, int offsetHeight) {

				        int left = (rta.getAbsoluteLeft()
				                + rta.getOffsetWidth() / 2 - offsetWidth / 2);
				        int top = (rta.getAbsoluteTop() + rta.getOffsetHeight()
				                / 2 - offsetHeight / 2);

				        textEditPopup.setPopupPosition(left, top);
				        Scheduler.get().scheduleDeferred(
				                new Scheduler.ScheduledCommand() {
					                public void execute() {
						                editBox.setFocus(true);
					                }
				                });
			        }
		        });
	}

	protected void createEditPopup() {
		if (textEditPopup == null) {
			editBox = new MyEditBox();
			editBox.setWidth("9em");
			textEditPopup = new PopupPanel();
			textEditPopup.add(editBox);
			textEditPopup.setAutoHideEnabled(true);
			editBox.addValueChangeHandler(new ValueChangeHandler<String>() {

				public void onValueChange(ValueChangeEvent<String> event) {
					textEditPopup.setVisible(false);

				}
			});

		}
	}

	public void setEditGeo(GeoText editGeo) {
		this.editGeo = editGeo;
	}

	public void setText(String text) {
		rta.setHTML(text);
	}

	public void insertText(String text) {
		formatter.insertHTML(text);
	}

	public String getText() {
		App.debug("ggb text string: "
		        + dTProcessor.buildGeoGebraString(getDList(), isLatex()));
		return dTProcessor.buildGeoGebraString(getDList(), isLatex());
	}

	private boolean isLatex() {
		// TODO Auto-generated method stub
		return false;
	}

	private String getHTML() {
		return rta.getHTML();
	}

	public RichTextAreaEditor getTextArea() {
		return rta;
	}

	private ArrayList<DynamicTextElement> getDList() {
		ArrayList<DynamicTextElement> list = new ArrayList<DynamicTextElement>();

		// parse the RichTextArea html to create a list of DynamicTextElements
		for (int i = 0; i < rta.getBody().getChildCount(); i++) {
			Node node = rta.getBody().getChild(i);

			if (node.getNodeType() == Node.TEXT_NODE) {
				if (node.getNodeValue() == null) {
					// App.debug("null node value found");
					continue;
				}

				list.add(new DynamicTextElement(node.getNodeValue(),
				        DynamicTextType.STATIC));
				// App.debug("to string:" + node.getNodeValue());

			} else if (node.getNodeType() == Node.ELEMENT_NODE) {
				list.add(new DynamicTextElement(((Element) node)
				        .getPropertyString("value"), DynamicTextType.VALUE));
				// App.debug("node value:"
				// + ((Element) node).getPropertyString("value"));
			}
		}

		return list;
	}

	private void createToolBar() {
		toolBar = new HorizontalPanel();

		buildInsertGeoButton();
		toolBar.add(btInsertGeo);

		Button testBtn = new Button("test");
		testBtn.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				formatter.insertImage(createTextImageURL("sample text"));

			}
		});
		// toolBar.add(testBtn);
	}

	/**
	 * Builds GeoElement insertion button.
	 */
	private void buildInsertGeoButton() {

		btInsertGeo = new ToggleButton("Objects");
		geoListPopup = new GeoListPopup(app, this, btInsertGeo);
		btInsertGeo.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (btInsertGeo.isDown()) {
					geoListPopup.updateGeoList();
					geoListPopup.showRelativeTo(btInsertGeo);
				} else {
					geoListPopup.hide();
				}
			}
		});

	}

	public void insertGeoElement(GeoElement geo) {
		if (geo == null)
			return;
		insertDynamicTextBox(geo.getLabel(StringTemplate.defaultTemplate));
	}

	public void insertDynamicTextBox(String text) {

		String dummyURL = createTextImageURL("dummy");
		formatter.insertImage(dummyURL);

		for (int i = 0; i < rta.getBody().getChildCount(); i++) {
			Node node = rta.getBody().getChild(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) node;
				String nodeSRC = elem.getPropertyString("src");
				App.debug("node src:" + nodeSRC);

				if (elem.getPropertyString("src").equals(dummyURL)) {
					/*
					 * App.debug("found it!"); elem.setPropertyString("src",
					 * createTextImageURL(text)); elem.setPropertyString("alt",
					 * text); //elem.setPropertyString("style", style);
					 * elem.getStyle().setBorderStyle(BorderStyle.SOLID);
					 * elem.getStyle().setBorderWidth(2, Unit.PX);
					 * elem.getStyle().setBorderColor("lightgray");
					 * elem.getStyle().setCursor(Cursor.POINTER);
					 * elem.getStyle().setMarginTop(0, Unit.PX);
					 * elem.getStyle().setMarginBottom(-2, Unit.PX);
					 * elem.getStyle().setMarginLeft(4, Unit.PX);
					 * elem.getStyle().setMarginRight(4, Unit.PX);
					 */

					rta.getBody().replaceChild(createValueElement(text), node);

					break;
				}

			}
		}

		// formatter.insertHTML(getDynamicTextHTML(text));
		// App.debug("HTML: " + rta.getHTML());
	}

	private Element createValueElement(String value) {

		Element elem = rta.getDocument().createElement("input");
		elem.getStyle().clearBackgroundImage();
		elem.getStyle().clearBackgroundColor();
		elem.getStyle().clearTextDecoration();
		elem.getStyle().clearOpacity();

		elem.setPropertyString("type", "button");
		elem.setPropertyString("value", value);
		elem.getStyle().setBorderStyle(BorderStyle.SOLID);
		elem.getStyle().setBorderWidth(2, Unit.PX);
		elem.getStyle().setBorderColor("lightgray");
		elem.getStyle().setCursor(Cursor.POINTER);
		elem.getStyle().setFontSize(app.getFontSize(), Unit.PT);
		elem.getStyle().setBackgroundColor("wheat");

		return elem;

	}

	private Element createTextElement(String text) {

		Element elem = rta.getDocument().createTextNode(text).cast();
		return elem;

	}

	public String getDynamicTextHTML(String text) {
		// String fcn = "onclick = edit(this) ";
		String url = createTextImageURL(text);
		String style = " style = \"border: 2px solid lightgray; cursor: pointer; ";
		style += "margin: 0px 4px -4px 4px;\"";
		String alt = " alt = \"" + text + "\"";
		String src = " src = \"" + url + "\"";

		String html = "<img " + src + alt + style + " />";

		// String html = "<input type = \"button\" value = \"" + text + "\"" +
		// fcn
		// + " /> &nbsp;";

		return html;
	}

	public GeoText getEditGeo() {
		return editGeo;
	}

	public void setGeoListButton(boolean down) {
		this.btInsertGeo.setDown(down);
	}

	/**
	 * Builds and sets editor content to correspond with the text string of a
	 * GeoText
	 * 
	 * @param geo
	 *            GeoText
	 */
	public void setText(GeoText geo) {

		rta.setHTML("");

		ArrayList<DynamicTextElement> list = dTProcessor
		        .buildDynamicTextList(geo);

		for (DynamicTextElement dt : list) {
			if (dt.type == DynamicTextType.STATIC) {
				rta.getBody().appendChild(createTextElement(dt.text));
			} else {
				rta.getBody().appendChild(createValueElement(dt.text));
			}
		}

	}

	/**
	 * Builds and sets editor content to correspond with the text string of a
	 * GeoText
	 * 
	 * @param geo
	 *            GeoText
	 */
	public void setText2(GeoText geo) {

		String htmlString = "";
		ArrayList<DynamicTextElement> list = dTProcessor
		        .buildDynamicTextList(geo);

		for (DynamicTextElement dt : list) {
			if (dt.type == DynamicTextType.STATIC) {
				htmlString += dt.text;
			} else {
				htmlString += getDynamicTextHTML(dt.text);
			}
		}

		rta.setHTML(htmlString);
	}

	String createTextImageURL(String str) {

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

	private class MyEditBox extends TextBox implements KeyUpHandler{

		Element target;

		public MyEditBox() {
			super();

			// TODO: either use AutoCompleteTextField or a style name
			GFontW font = (GFontW) app.getPlainFontCommon();
			String fontSize = font.getFontSize();
			String fontFamily = font.getFontFamily();

			getStyleElement().setAttribute(
			        "style",
			        "font-family:" + fontFamily + "; font-size:" + fontSize
			                + "pt");
			getStyleElement().setAttribute("spellcheck", "false");
			getStyleElement().setAttribute("oncontextmenu", "return false");

			addValueChangeHandler(new ValueChangeHandler<String>() {

				public void onValueChange(ValueChangeEvent<String> event) {
					//updateTarget();
				}
			});
			
			addKeyUpHandler(this);
		}

		protected void updateTarget(){
			if (target != null) {
				target.setPropertyString("src",
				        createTextImageURL(getText()));
				target.setPropertyString("value", getText());
			}
		}
		
		
		public void setTarget(Element target) {
			this.target = target;
		}

		public void onKeyUp(KeyUpEvent e) {
			int keyCode = e.getNativeKeyCode();

			switch (keyCode) {
			case GWTKeycodes.KEY_ESCAPE:
				textEditPopup.hide();
				break;

			case GWTKeycodes.KEY_ENTER:
				updateTarget();
				textEditPopup.hide();
				break;
				
			default:
				updateTarget();
			}
		}

	}

}
