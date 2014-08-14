package geogebra.html5.util.keyboard;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class KeyBoardButton extends SimplePanel {

	/**
	 * only use if the ClickEvents have to be redirected from the label
	 */
	private Label content;

	private String feedBack;

	public KeyBoardButton(String caption) {
		this(caption, caption, false);
	}

	public KeyBoardButton(String caption, String feedBack, boolean largeButton) {
		Label l = new Label(caption);
		if (largeButton) {
			content = l;
			content.addStyleName("KeyBoardButton_label");
			this.addStyleName("KeyBoardButton_simple");
		} else {
			if (Character.isDigit(caption.charAt(0))) {
				addStyleName("KeyBoardButton_Number");
			} else {
				addStyleName("KeyBoardButton");
			}
		}
		this.add(l);

		this.feedBack = feedBack == null ? caption : feedBack;
	}

	public String getText() {
		return feedBack;
	}
}
