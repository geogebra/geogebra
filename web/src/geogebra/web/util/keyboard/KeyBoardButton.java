package geogebra.web.util.keyboard;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class KeyBoardButton extends SimplePanel {

	/**
	 * only use if the ClickEvents have to be redirected from the label
	 */
	private Label content;

	private String feedBack;

	private Label label;

	public KeyBoardButton(String caption) {
		this(caption, caption, false);
	}

	public KeyBoardButton(String caption, String feedBack, boolean largeButton) {
		label = new Label(caption);
		if (largeButton) {
			content = label;
			content.addStyleName("KeyBoardButton_label");
			this.addStyleName("KeyBoardButton_simple");
		} else {
			if (caption.length() > 0 && Character.isDigit(caption.charAt(0))) {
				addStyleName("KeyBoardButton_Number");
			} else {
				addStyleName("KeyBoardButton");
			}
		}
		this.add(label);

		this.feedBack = feedBack == null ? caption : feedBack;
	}

	public String getText() {
		return feedBack;
	}

	public String getCaption() {
		return label.getText();
	}

	public void setCaption(String caption, boolean resetFeedBack) {
		label.setText(caption);
		if (resetFeedBack) {
			feedBack = caption;
		}
	}
}
