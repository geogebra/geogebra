package geogebra.touch.gui.elements;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class InputArea extends VerticalPanel {

	private final TextArea textArea;
	private Panel underline;
	private Label nameLabel;

	/**
	 * equal to AndroidTextBox(null)
	 */
	public InputArea() {
		this(null, true);
	}

	/**
	 * AndroidTextBox(null) is equal to AndroidTextBox()
	 * 
	 * @param caption
	 *            caption of the TextField (will NOT(!) be translated)
	 */
	public InputArea(final String caption, final boolean useUnderline) {
		if (caption != null) {
			this.nameLabel = new Label(caption);
			this.add(this.nameLabel);
		}

		this.textArea = new TextArea();
		this.textArea.addStyleName("textBox");
		this.textArea.getElement().setAttribute("autocorrect", "off");
		this.textArea.getElement().setAttribute("autocapitalize", "off");
		this.textArea.addStyleName("inactive");
		this.add(this.textArea);

		if (useUnderline) {
			this.underline = new LayoutPanel();
			this.underline.setStyleName("inputUnderline");
			this.underline.addStyleName("inactive");
			this.add(this.underline);
		}
		this.setStyleName("inputField");

		this.textArea.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(final FocusEvent event) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					@Override
					public void execute() {
						onFocusTextBox();
					}
				});
			}
		});

		this.textArea.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(final BlurEvent event) {
				onBlurTextBox();
			}
		});

		this.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				onClickTextBox();
			}
		}, ClickEvent.getType());
	}

	protected void onFocusTextBox() {
		this.textArea.setFocus(true);
		if (this.underline != null) {
			this.underline.removeStyleName("inactive");
			this.underline.addStyleName("active");
		}
		this.textArea.removeStyleName("inactive");
		this.textArea.addStyleName("active");
	}

	protected void onBlurTextBox() {
		this.textArea.setFocus(false);
		if (this.underline != null) {
			this.underline.removeStyleName("active");
			this.underline.addStyleName("inactive");
		}
		this.textArea.removeStyleName("active");
		this.textArea.addStyleName("inactive");
	}

	protected void onClickTextBox() {
		this.textArea.setFocus(true);
	}

	public void addErrorBox(final HorizontalPanel errorBox) {
		this.clear();
		this.add(errorBox);
		if (this.nameLabel != null) {
			this.add(this.nameLabel);
		}
		this.add(this.textArea);
		if (this.underline != null) {
			this.add(this.underline);
		}
	}

	public void addKeyDownHandler(final KeyDownHandler keyDownHandler) {
		this.textArea.addKeyDownHandler(keyDownHandler);
	}

	public int getCursorPos() {
		return this.textArea.getCursorPos();
	}

	public String getText() {
		return this.textArea.getText().trim();
	}

	public void setCursorPos(final int i) {
		this.textArea.setCursorPos(i);
	}

	public void setFocus(final boolean b) {
		this.textArea.setFocus(b);
	}

	public void setText(final String string) {
		this.textArea.setText(string);
	}

	public void setLabelText(final String name) {
		this.nameLabel.setText(name);
	}

	public void setInactive() {
		onBlurTextBox();
	}
}
