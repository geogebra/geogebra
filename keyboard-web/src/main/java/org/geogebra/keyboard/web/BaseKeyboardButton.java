package org.geogebra.keyboard.web;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.html5.gui.util.ClickEndHandler;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.gwtproject.dom.client.Document;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.style.shared.FontStyle;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.event.dom.client.ClickHandler;
import org.gwtproject.event.dom.client.MouseOutEvent;
import org.gwtproject.event.dom.client.MouseOutHandler;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.SimplePanel;

/**
 * A button of the {@link TabbedKeyboard}.
 */
public class BaseKeyboardButton extends SimplePanel implements MouseOutHandler {

	private String caption;
	/**
	 * the feedback that is returned when the button is clicked
	 */
	protected String feedback;
	/**
	 * the label that is displayed on the button
	 */
	protected Label label;
	private String secondaryAction;
	private final ButtonHandler buttonHandler;

	/**
	 * @param caption
	 *            text of the button
	 * @param altText
	 *            altText for the screen reader
	 * @param feedback
	 *            String to send if click occurs
	 * @param handler
	 *            {@link ClickHandler}
	 */
	public BaseKeyboardButton(String caption, String altText, String feedback,
			ButtonHandler handler) {
		this(handler);
		this.label = new Label();
		setWidget(label);
		setCaption(checkThai(caption), altText);
		this.feedback = feedback;
	}

	/**
	 * @param caption
	 *            text of the button (and altText)
	 * @param feedback
	 *            String to send if click occurs
	 * @param handler
	 *            {@link ClickHandler}
	 */
	public BaseKeyboardButton(String caption, String feedback,
			ButtonHandler handler) {
		this(caption, caption, feedback, handler);
	}

	// https://codepoints.net/search?gc=Mn
	// these Thai characters need a placeholder added to display nicely
	private static String checkThai(String str) {
		if (("\u0E31\u0E33\u0E34\u0E35\u0E36\u0E37\u0E38\u0E39\u0E3A\u0E47"
				+ "\u0E48\u0E49\u0E4A\u0E4B\u0E4C\u0E4D").contains(str)) {
			return "\u25CC" + str;
		}
		return str;
	}

	/**
	 * @param caption
	 *            text of the button and feedback (same)
	 * @param handler
	 *            {@link ClickHandler}
	 */
	public BaseKeyboardButton(String caption, ButtonHandler handler) {
		this(caption, caption, handler);
	}

	private static void addWave(Element element) {
		element.addClassName("ripple");
	}

	/**
	 * Constructor for subclass {@link FunctionalKeyboardButton}
	 * 
	 * @param handler
	 *            {@link ClickHandler}
	 */
	protected BaseKeyboardButton(final ButtonHandler handler) {
		this.buttonHandler = handler;
		ClickStartHandler.init(this, new ClickStartHandler(true, true) {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				handler.onClick(BaseKeyboardButton.this, type);
			}
		});
		// only used for preventDefault and stopPropagation
		ClickEndHandler.init(this, new ClickEndHandler(true, true) {
			@Override
			public void onClickEnd(int x, int y, PointerEventType type) {
				handler.buttonPressEnded();
			}
		});
		addDomHandler(this, MouseOutEvent.getType());
		addStyleName("KeyBoardButton");
		getElement().setAttribute("role", "button");
		addStyleName("waves-effect");
		addStyleName("waves-keyboard");
		addStyleName("btn");
		addWave(getElement());
	}

	/**
	 * @return text of the button
	 */
	public String getCaption() {
		return this.caption;
	}

	/**
	 * @param caption
	 *            text of the button
	 * @param altText
	 *            description for screen reader
	 * @param feedback1
	 *            feedback of the button (to be inserted in textfield)
	 */
	public void setCaption(String caption, String altText, String feedback1) {

		getElement().setAttribute("aria-label", altText);
		this.caption = caption;
		if (caption.length() > 5 && !caption.contains("_")) {
			this.label.addStyleName("small");
		}
		if (feedback1 != null) {
			this.feedback = feedback1;
		}
		if (caption.length() > 1 && caption.indexOf('^') > -1) {
			int index = caption.indexOf('^');
			this.label.setText(caption.substring(0, index));
			Element sup = Document.get().createElement("sup");
			sup.appendChild(Document.get().createTextNode(
					caption.substring(index + 1)));
			sup.getStyle().setFontSize(14, Unit.PX);
			sup.getStyle().setFontStyle(FontStyle.NORMAL);
			this.label.getElement().appendChild(sup);
			addStyleName("sup");
		} else if (caption.length() > 1 && caption.indexOf('_') > -1) {
			int index = caption.indexOf('_');
			this.label.setText(caption.substring(0, index));
			Element sub = Document.get().createElement("sub");
			sub.appendChild(Document.get().createTextNode(
					caption.substring(index + 1)));
			sub.getStyle().setFontSize(14, Unit.PX);
			sub.getStyle().setFontStyle(FontStyle.NORMAL);
			this.label.getElement().appendChild(sub);
			addStyleName("sub");
		} else {
			this.label.setText(caption);
		}
	}

	/**
	 * @param caption
	 *            text of the button (also used as new feedback)
	 * @param altText
	 *            description for screen reader
	 */
	public final void setCaption(String caption, String altText) {
		setCaption(caption, altText, caption);
	}

	/**
	 * @return the String to be sent if a click occurs
	 */
	public String getFeedback() {
		return this.feedback;
	}

	/**
	 * @return secondary action
	 */
	public String getSecondaryAction() {
		return secondaryAction;
	}

	/**
	 * @param actionName
	 *            secondary action name (see Action.getName()}
	 */
	public void setSecondaryAction(String actionName) {
		this.secondaryAction = actionName;
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		buttonHandler.buttonPressEnded();
	}
}
