package org.geogebra.web.full.gui.dialog.text;

import java.util.ArrayList;

import org.geogebra.common.gui.inputfield.DynamicTextElement;
import org.geogebra.common.gui.inputfield.DynamicTextElement.DynamicTextType;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.awt.GFontW;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.client.Style;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.event.dom.client.KeyUpEvent;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.FocusWidget;
import org.gwtproject.user.client.ui.PopupPanel;

import com.himamis.retex.editor.share.util.GWTKeycodes;

import elemental2.dom.DomGlobal;
import elemental2.dom.Node;
import elemental2.dom.Range;
import jsinterop.base.Js;

/**
 * Extension of RichTextArea for editing GeoText strings with dynamic references
 * to GeoElements.
 * 
 * @author G. Sturr
 * 
 */
public class GeoTextEditor extends FocusWidget {

	private static final String DYNAMIC_TEXT_CLASS = "dynamicText";
	private final AppW app;
	protected ITextEditPanel editPanel;

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
		super(DOM.createDiv());
		this.app = (AppW) app;
		this.editPanel = editPanel;
		getElement().setAttribute("contenteditable", "true");
		getElement().setAttribute("spellcheck", "false");
		getElement().setAttribute("oncontextmenu", "return false");
		getElement().setAttribute("word-wrap", "normal");

		Dom.addEventListener(getElement(), "cut", e -> handleCut());
		Dom.addEventListener(getElement(), "paste", e -> handlePaste());
		createEditPopup();
		registerHandlers();
		updateFonts();
	}

	private void registerHandlers() {

		addDomHandler(event -> editPanel.updatePreviewPanel(true), KeyUpEvent.getType());
		editBox.addKeyUpHandler(event -> {

			int keyCode = event.getNativeKeyCode();

			switch (keyCode) {
			case GWTKeycodes.KEY_ESCAPE:
			case GWTKeycodes.KEY_ENTER:
				showEditPopup(false);
				break;

			default:
				editPanel.updatePreviewPanel(true);
			}
		});

		addDomHandler(event -> {

			showEditPopup(false);

			Element target = Element
					.as(event.getNativeEvent().getEventTarget());

			if (DYNAMIC_TEXT_CLASS
					.equalsIgnoreCase(target.getClassName())) {
				editBox.setText(target.getAttribute("value"));
				editBox.setTarget(target);
				showEditPopup(true);
			}
		}, ClickEvent.getType());
	}

	/**
	 * Update editor font.
	 */
	public void updateFonts() {
		int fontSize = app.getSettings().getFontSettings().getAppFontSize();
		getElement().getStyle().setFontSize(fontSize, Style.Unit.PX);
	}

	/**
	 * 
	 * @return content as HTML without formatting
	 */
	public String getUnformattedContent() {
		return getUnformattedContent(Js.uncheckedCast(getElement()));
	}

	private String getUnformattedContent(Node e) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < e.childNodes.length; i++) {
			Node c = e.childNodes.getAt(i);
			if (c.childNodes.length > 0) {
				sb.append(getUnformattedContent(c));
			} else {
				if (c.nodeType == Node.ELEMENT_NODE) {
					Element el = Js.uncheckedCast(c);
					if (el.getClassName()
							.contains(GeoTextEditor.DYNAMIC_TEXT_CLASS)) {
						sb.append(el.getString());
						continue;
					}
				}
				String nodeValue = c.nodeValue;
				if (nodeValue != null) {
					if (c.nodeType == Node.TEXT_NODE) {
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
		Scheduler.get().scheduleDeferred(() -> {
			getElement().setInnerHTML(getUnformattedContent());
			updateFonts();
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
	public void insertElement(Node elem) {
		getElement().focus();
		Range range = DomGlobal.document.getSelection().getRangeAt(0);
		range.deleteContents();
		range.insertNode(elem);
		range.collapse(false);
		editPanel.updatePreviewPanel();
	}

	private Node createValueElement(String value) {
		Element elem = DOM.createElement("input");
		elem.setClassName(DYNAMIC_TEXT_CLASS);
		elem.setPropertyString("type", "button");
		elem.setPropertyString("value", value);
		elem.getStyle().setFontSize(app.getSettings().getFontSettings().getAppFontSize(),
				Style.Unit.PX);
		return Js.uncheckedCast(elem);
	}

	private Node createTextElement(String text) {
		return DomGlobal.document.createTextNode(text);
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

	/**
	 * Update dialog with text elements.
	 * 
	 * @param dynamicList
	 *            list of text elements
	 */
	public void setText(ArrayList<DynamicTextElement> dynamicList) {
		getElement().setInnerHTML("");
		Node lineElement = Js.uncheckedCast(getElement());
		for (DynamicTextElement dt : dynamicList) {
			if (dt.type == DynamicTextType.STATIC) {
				String[] lineSplit = dt.text.split("\n");
				lineElement.appendChild(createTextElement(lineSplit[0]));
				for (int i = 1; i < lineSplit.length; i++) {
					lineElement = DomGlobal.document.createElement("div");
					getElement().appendChild(Js.uncheckedCast(lineElement));
					lineElement.appendChild(createTextElement(lineSplit[i]));
				}
			} else {
				lineElement.appendChild(createValueElement(dt.text));
			}
		}
	}

	/**
	 * Parses the RichTextArea HTML to create a list of DynamicTextElements
	 * 
	 * @return list of DynamicTextElements represented by current editor text
	 */
	public ArrayList<DynamicTextElement> getDynamicTextList() {
		ArrayList<DynamicTextElement> list = new ArrayList<>();
		getDynamicTextListRecursive(list, Js.uncheckedCast(getElement()));
		return list;
	}

	/**
	 * Parses a node into a list of DynamicTextElements
	 * 
	 * @param list
	 *            parsed dynamic texts
	 * @param node
	 *            HTML node
	 */
	public void getDynamicTextListRecursive(
			ArrayList<DynamicTextElement> list, Node node) {
		if (node == null) {
			return;
		}

		for (int i = 0; i < node.childNodes.length; i++) {
			Node child = node.childNodes.getAt(i);
			if (child.nodeType == Node.TEXT_NODE) {
				processTextNode(child, list);
			} else if (child.nodeType == Node.ELEMENT_NODE) {
				processElement(child, list);
			}
		}
	}

	private void processTextNode(Node child, ArrayList<DynamicTextElement> list) {
		if (child.nodeValue != null) {
			list.add(new DynamicTextElement(child.nodeValue,
					DynamicTextType.STATIC));
		}
	}

	private void processElement(Node child, ArrayList<DynamicTextElement> list) {
		Element childEl = Js.uncheckedCast(child);
		String tagName = childEl.getTagName();

		// convert input element to dynamic text string
		if (DYNAMIC_TEXT_CLASS
				.equals(childEl.getClassName())) {
			list.add(new DynamicTextElement(
					childEl.getPropertyString("value"),
					DynamicTextType.VALUE));

			// convert DIV or P (browser dependent) to newline
		} else if ("div".equalsIgnoreCase(tagName)
				|| "p".equalsIgnoreCase(tagName)) {

			list.add(new DynamicTextElement("\n",
					DynamicTextType.STATIC));

			// parse the inner HTML of this element
			getDynamicTextListRecursive(list, child);
		} else if ("span".equalsIgnoreCase(tagName)) {
			getDynamicTextListRecursive(list, child);
		}
	}

	// ======================================================
	// Editor Popup
	// ======================================================

	protected void showEditPopup(boolean isVisible) {
		if (isVisible) {
			textEditPopup
					.setPopupPositionAndShow((offsetWidth, offsetHeight) -> {

						int left = getAbsoluteLeft() + getOffsetWidth() / 2
								- offsetWidth / 2;
						int top = getAbsoluteTop() + getOffsetHeight() / 2
								- offsetHeight / 2;

						textEditPopup.setPopupPosition(left, top);
						Scheduler.get().scheduleDeferred(
								() -> editBox.setFocus(true));
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
			editBox.addValueChangeHandler(event -> textEditPopup.hide());

		}
	}

	public String getText() {
		return getElement().getInnerText();
	}
}
