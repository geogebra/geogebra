package org.geogebra.web.html5.gui.inputfield;

import java.util.ArrayList;

import org.geogebra.common.gui.inputfield.DynamicTextElement;
import org.geogebra.common.gui.inputfield.DynamicTextElement.DynamicTextType;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.awt.GFontW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.himamis.retex.editor.share.util.GWTKeycodes;

/**
 * Extension of RichTextArea for editing GeoText strings with dynamic references
 * to GeoElements.
 * 
 * @author G. Sturr
 * 
 */
public class GeoTextEditor extends RichTextArea {

	private static final String DYNAMIC_TEXT_CLASS = "dynamicText";
	private AppW app;
	boolean initialized = false;
	protected ArrayList<DynamicTextElement> dynamicList = null;
	protected GFontW font;
	protected ITextEditPanel editPanel;
	protected Formatter formatter;

	protected PopupPanel textEditPopup;
	protected EditorTextField editBox;

	/**************************************
	 * Constructor
	 * 
	 * @param app
	 *            application
	 * @param editPanel
	 *            editor panel
	 */
	public GeoTextEditor(App app, ITextEditPanel editPanel) {

		this.app = (AppW) app;
		this.editPanel = editPanel;

		formatter = getFormatter();

		// styles and handlers must be set after the editor has been initialized
		addInitializeHandler(new InitializeHandler() {
			@Override
			public void onInitialize(InitializeEvent event) {
				initialized = true;
				updateFonts();

				// set style properties that cannot be done from stylesheet
				getBody().setAttribute("spellcheck", "false");
				getBody().setAttribute("oncontextmenu", "return false");
				getBody().setAttribute("word-wrap", "normal");

				addCutHandler(getBody());
				addPasteHandler(getBody());
				if (dynamicList != null) {
					setDynamicText();
				}
			}
		});

		createEditPopup();
		registerHandlers();
	}

	private void registerHandlers() {

		addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				editPanel.updatePreviewPanel(true);
			}
		});

		editBox.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {

				int keyCode = event.getNativeKeyCode();

				switch (keyCode) {
				case GWTKeycodes.KEY_ESCAPE:
					showEditPopup(false);
					break;

				case GWTKeycodes.KEY_ENTER:
					showEditPopup(false);
					break;

				default:
					editPanel.updatePreviewPanel(true);
				}
			}
		});

		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				showEditPopup(false);

				Element target = Element
						.as(event.getNativeEvent().getEventTarget());

				if (DYNAMIC_TEXT_CLASS
						.equalsIgnoreCase(target.getClassName())) {
					editBox.setText(target.getAttribute("value"));
					editBox.setTarget(target);
					showEditPopup(true);
				}
			}
		});

	}

	/**
	 * Update editor font.
	 */
	public void updateFonts() {
		if (!initialized) {
			return;
		}

		font = (GFontW) app.getPlainFontCommon();
		String fontSize = app.getFontSize() + "";
		String fontFamily = font.getFontFamily();

		// note: formatter cannot be used here because pixel font-size is not
		// supported

		getBody().setAttribute("style",
				"font-family:" + fontFamily + "; font-size:" + fontSize + "px");
	}

	private Document getDocument() {
		if (!initialized) {
			return null;
		}
		if (IFrameElement.as(getElement()) == null) {
			return null;
		}
		return IFrameElement.as(getElement()).getContentDocument();
	}

	/**
	 * @return body of iframe
	 */
	protected BodyElement getBody() {
		if (getDocument() == null) {
			return null;
		}
		return getDocument().getBody();
	}

	// workaround for ff bug that prevents disabling RichTextEditor
	@Override
	public void onBrowserEvent(final Event event) {
		if (isEnabled()) {
			super.onBrowserEvent(event);
		}
	}

	/**
	 * @param elem
	 *            target element
	 */
	public native void addCutHandler(Element elem) /*-{
		var temp = this;
		elem.oncut = function(e) {
			temp.@org.geogebra.web.html5.gui.inputfield.GeoTextEditor::handleCut()();
		}
	}-*/;

	/**
	 * @param elem
	 *            target element
	 */
	public native void addPasteHandler(Element elem) /*-{
		var temp = this;
		elem.onpaste = function(e) {
			temp.@org.geogebra.web.html5.gui.inputfield.GeoTextEditor::handlePaste()();
		}
	}-*/;

	/**
	 * 
	 * @return content as HTML without formatting
	 */
	public String getUnformattedContent() {
		return getUnformattedContent(getBody());
	}

	private String getUnformattedContent(Node e) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < e.getChildCount(); i++) {
			Node c = e.getChild(i);
			if (c.getChildCount() > 0) {
				sb.append(getUnformattedContent(c));
			} else {
				if (c instanceof Element) {
					Element el = (Element) c;
					if (((Element) c).getClassName()
							.contains(GeoTextEditor.DYNAMIC_TEXT_CLASS)) {
						sb.append(el.getString());
						continue;
					}
				}
				String nodeValue = c.getNodeValue();
				if (nodeValue != null) {
					if (c.getNodeType() == Node.TEXT_NODE) {
						sb.append(nodeValue);
					} else {
						sb.append("<div>");
						sb.append(nodeValue);
						sb.append("</div>");
					}

				}
			}
		}

		return sb.toString();
	}

	/**
	 * Handle paste event.
	 */
	public void handlePaste() {
		// setDynamicText();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				getBody().setInnerHTML(getUnformattedContent());
				updateFonts();
			}
		});
		editPanel.updatePreviewPanel(true);
		Log.debug("Paste! ");
	}

	/**
	 * Handle cut to clipboard
	 */
	public void handleCut() {
		editPanel.updatePreviewPanel();
	}

	/**
	 * Inserts an HTML element at the current cursor position and updates the
	 * editor.
	 * 
	 * Note: The insertHTML method is not supported in IE11 so it can't be used
	 * here. Instead, insertImage (browser safe) is used to insert a dummy image
	 * element which is then replaced with the actual element to be inserted.
	 * 
	 * @param elem
	 *            input element
	 */
	public void insertElement(Element elem) {
		String dummyURL = dummyImageURL();
		formatter.insertImage(dummyURL);

		Node node = findImageNodeRecursive(getBody(), dummyURL);
		if (node != null) {
			node.getParentElement().replaceChild(elem, node);
			editPanel.updatePreviewPanel();
		}
	}

	private Node findImageNodeRecursive(Node node, String dummyURL) {

		for (int i = 0; i < node.getChildCount(); i++) {

			Node child = node.getChild(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {

				// image node?
				String source = ((Element) child).getPropertyString("src");
				if (source != null && source.equals(dummyURL)) {
					return child;

				} else if (node.hasChildNodes()) {
					// recursive search of child nodes
					Node targetNode = findImageNodeRecursive(child, dummyURL);
					if (targetNode != null) {
						return targetNode;
					}
				}
			}
		}
		return null;
	}

	private static String dummyImageURL() {

		Canvas canvas = Canvas.createIfSupported();
		canvas.setWidth("1px");
		canvas.setHeight("1px");
		canvas.setCoordinateSpaceWidth(1);
		canvas.setCoordinateSpaceHeight(1);
		return canvas.toDataUrl();
	}

	private Element createValueElement(String value) {

		Element elem = getDocument().createElement("input");
		elem.setClassName(DYNAMIC_TEXT_CLASS);
		elem.setPropertyString("type", "button");
		elem.setPropertyString("value", value);

		// set style
		// TODO: get this to work from the css file
		elem.getStyle().clearBackgroundImage();
		elem.getStyle().clearBackgroundColor();
		elem.getStyle().clearTextDecoration();
		elem.getStyle().clearOpacity();
		elem.getStyle().setBorderStyle(BorderStyle.SOLID);
		elem.getStyle().setBorderWidth(2, Unit.PX);
		elem.getStyle().setBorderColor("lightgray");
		elem.getStyle().setCursor(Cursor.POINTER);
		elem.getStyle().setFontSize(font.getSize(), Unit.PX);
		elem.getStyle().setBackgroundColor("wheat");
		elem.getStyle().setMarginLeft(1, Unit.PX);
		elem.getStyle().setMarginRight(1, Unit.PX);

		return elem;

	}

	private Element createTextElement(String text) {
		Element elem = getDocument().createTextNode(text).cast();
		return elem;
	}

	/**
	 * Add textfield for given element.
	 * 
	 * @param geo
	 *            element
	 */
	public void insertGeoElement(GeoElement geo) {

		String text = ""; // gives empty box if geo is null

		if (geo != null) {
			text = geo.getLabel(StringTemplate.defaultTemplate);
		}
		insertElement(createValueElement(text));
	}

	/**
	 * Insert static text into the editor
	 * 
	 * @param str0
	 *            static text
	 * @param isLatex
	 *            whether it's latex
	 */
	public void insertTextString(String str0, boolean isLatex) {

		boolean convertGreekLetters = !app.getLocalization().getLanguage()
				.equals("gr");

		if (str0 != null) {
			String str = str0;
			if (isLatex) {
				str = StringUtil.toLaTeXString(str, convertGreekLetters);
			}
			insertElement(createTextElement(str));
		}
	}

	protected void setDynamicText() {
		setHTML("");
		Element lineElement = getBody();
		for (DynamicTextElement dt : dynamicList) {
			if (dt.type == DynamicTextType.STATIC) {
				String[] lineSplit = dt.text.split("\n");
				lineElement.appendChild(createTextElement(lineSplit[0]));
				for (int i = 1; i < lineSplit.length; i++) {
					lineElement = DOM.createDiv();
					getBody().appendChild(lineElement);
					lineElement.appendChild(createTextElement(lineSplit[i]));
				}
			} else {
				lineElement.appendChild(createValueElement(dt.text));
			}
		}
	}

	/**
	 * Update dialog with text elements.
	 * 
	 * @param list
	 *            list of text elements
	 */
	public void setText(ArrayList<DynamicTextElement> list) {
		dynamicList = list;
		if (initialized) {
			setDynamicText();
		}
	}

	/**
	 * Parses the RichTextArea HTML to create a list of DynamicTextElements
	 * 
	 * @return list of DynamicTextElements represented by current editor text
	 */
	public ArrayList<DynamicTextElement> getDynamicTextList() {
		if (!initialized && dynamicList != null) {
			return dynamicList;
		}
		ArrayList<DynamicTextElement> list = new ArrayList<>();
		getDynamicTextListRecursive(list, getBody());
		return list;
	}

	/**
	 * Parses a node into a list of DynamicTextElements
	 * 
	 * @param list
	 *            parsed dynamic texts
	 * @param node
	 *            HTML node
	 * @return updated dynamic text
	 */
	public ArrayList<DynamicTextElement> getDynamicTextListRecursive(
			ArrayList<DynamicTextElement> list, Node node) {
		if (node == null) {
			return list;
		}

		for (int i = 0; i < node.getChildCount(); i++) {
			Node child = node.getChild(i);

			if (child.getNodeType() == Node.TEXT_NODE) {
				if (child.getNodeValue() != null) {
					list.add(new DynamicTextElement(child.getNodeValue(),
							DynamicTextType.STATIC));
				}

			} else if (child.getNodeType() == Node.ELEMENT_NODE) {

				String tagName = ((Element) child).getTagName();

				// convert input element to dynamic text string
				if (DYNAMIC_TEXT_CLASS
						.equals(((Element) child).getClassName())) {
					list.add(new DynamicTextElement(
							((Element) child).getPropertyString("value"),
							DynamicTextType.VALUE));

					// convert DIV or P (browser dependent) to newline
				} else if ("div".equalsIgnoreCase(tagName)
						|| "p".equalsIgnoreCase(tagName)) {

					list.add(new DynamicTextElement("\n",
							DynamicTextType.STATIC));

					// parse the inner HTML of this element
					getDynamicTextListRecursive(list, child);
				}

			}
		}

		return list;
	}

	// ======================================================
	// Editor Popup
	// ======================================================

	protected void showEditPopup(boolean isVisible) {
		if (isVisible) {
			textEditPopup
					.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
						@Override
						public void setPosition(int offsetWidth,
								int offsetHeight) {

							int left = (getAbsoluteLeft() + getOffsetWidth() / 2
									- offsetWidth / 2);
							int top = (getAbsoluteTop() + getOffsetHeight() / 2
									- offsetHeight / 2);

							textEditPopup.setPopupPosition(left, top);
							Scheduler.get().scheduleDeferred(
									new Scheduler.ScheduledCommand() {
										@Override
										public void execute() {
											editBox.setFocus(true);
										}
									});
						}
					});
			textEditPopup.getElement().getStyle().setZIndex(1000);
		} else {
			textEditPopup.hide();
		}

	}

	protected void createEditPopup() {
		if (textEditPopup == null) {
			textEditPopup = new PopupPanel();
			editBox = new EditorTextField();

			// TODO handle formatting with css style
			editBox.setWidth("9em");
			editBox.setFont((GFontW) app.getPlainFontCommon());
			textEditPopup.add(editBox);

			textEditPopup.setAutoHideEnabled(true);
			editBox.addValueChangeHandler(new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					textEditPopup.hide();
				}
			});

		}
	}

}
