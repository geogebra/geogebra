package geogebra.web.util.keyboard;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * A button of the {@link OnScreenKeyBoard}.
 */
public class KeyBoardButton extends SimplePanel {

	private String caption;
	private String feedback;
	private Label label;

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
		this.label = new Label();
		setCaption(caption);
		this.feedback = feedback;

		this.setWidget(label);
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
		this.feedback = caption;
		int index = caption.indexOf('^');
		if (index > -1) {
			this.label.setText(caption.substring(0, index));
			Element sup = Document.get().createElement("sup");
			sup.appendChild(Document.get().createTextNode(
			        caption.substring(index + 1)));
			this.label.getElement().appendChild(sup);
		} else {
			this.label.setText(caption);
		}

	}

	/**
	 * @return the String to be sent if a click occurs
	 */
	public String getFeedback() {
		return this.feedback;
	}
}
