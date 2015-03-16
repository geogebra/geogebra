package geogebra.web.util.keyboard;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * A button of the {@link OnScreenKeyBoard}.
 */
public class KeyBoardButton extends SimplePanel {

	private String caption;
	private String feedback;

	/**
	 * @param caption
	 *            text of the button
	 * @param feedback
	 *            String to send if click occurs
	 * @param handler
	 *            {@link ClickHandler}
	 */
	public KeyBoardButton(String caption, String feedback, ClickHandler handler) {
		this(handler);
		this.caption = caption;
		this.feedback = feedback;
		this.getElement().setInnerHTML(
		        "<div class=\"gwt-Label\">" + caption + "</div>");
	}

	/**
	 * Constructor for subclass {@link KeyBoardButtonFunctional}
	 * 
	 * @param handler
	 *            {@link ClickHandler}
	 */
	protected KeyBoardButton(ClickHandler handler) {
		addDomHandler(handler, ClickEvent.getType());
		addStyleName("KeyBoardButton");
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
	 */
	public void setCaption(String caption) {
		this.caption = caption;
		this.getElement().setInnerHTML(
		        "<div class=\"gwt-Label\">" + caption + "</div>");
	}

	/**
	 * @return the String to be sent if a click occurs
	 */
	public String getFeedback() {
		return this.feedback;
	}
}
