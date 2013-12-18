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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class InputField extends VerticalPanel {

	private final TextBox textBox;
	private Panel underline;
	private Label nameLabel;
	boolean active = true;

	/**
	 * equal to AndroidTextBox(null)
	 */
	public InputField() {
		this(null, true);
	}

	/**
	 * AndroidTextBox(null) is equal to AndroidTextBox()
	 * 
	 * @param caption
	 *            caption of the TextField (will NOT(!) be translated)
	 */
	public InputField(final String caption, final boolean useUnderline) {
		if (caption != null) {
			this.nameLabel = new Label(caption);
			this.add(this.nameLabel);
		}

		this.textBox = new TextBox();
		this.textBox.addStyleName("textBox");
		this.textBox.getElement().setAttribute("autocorrect", "off");
		this.textBox.getElement().setAttribute("autocapitalize", "off");
		this.textBox.addStyleName("inactive");
		this.add(this.textBox);

		if (useUnderline) {
			this.underline = new LayoutPanel();
			this.underline.setStyleName("inputUnderline");
			this.underline.addStyleName("inactive");
			this.add(this.underline);
		}
		this.setStyleName("inputField");

		this.textBox.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(final FocusEvent event) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					@Override
					public void execute() {
						InputField.this.active = true;
						onFocusTextBox();
					}
				});
			}
		});

		this.textBox.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(final BlurEvent event) {
				InputField.this.active = false;
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
		this.textBox.setFocus(true);
		if (this.underline != null) {
			this.underline.removeStyleName("inactive");
			this.underline.addStyleName("active");
		}
		this.textBox.removeStyleName("inactive");
		this.textBox.addStyleName("active");
	}

	protected void onBlurTextBox() {
		this.textBox.setFocus(false);
		if (this.underline != null) {
			this.underline.removeStyleName("active");
			this.underline.addStyleName("inactive");
		}
		this.textBox.removeStyleName("active");
		this.textBox.addStyleName("inactive");
	}

	protected void onClickTextBox() {
		this.textBox.setFocus(true);
	}

	public void addErrorBox(final HorizontalPanel errorBox) {
		this.clear();
		this.add(errorBox);
		if (this.nameLabel != null) {
			this.add(this.nameLabel);
		}
		this.add(this.textBox);
		if (this.underline != null) {
			this.add(this.underline);
		}
	}

	public void addKeyDownHandler(final KeyDownHandler keyDownHandler) {
		this.textBox.addKeyDownHandler(keyDownHandler);
	}

	public int getCursorPos() {
		return this.textBox.getCursorPos();
	}

	public String getText() {
		return this.textBox.getText().trim();
	}

	public void setCursorPos(final int i) {
		this.textBox.setCursorPos(i);
	}

	public void setFocus(final boolean b) {
		this.textBox.setFocus(b);
	}

	public void setText(final String string) {
		this.textBox.setText(string);
	}

	public void setLabelText(final String name) {
		this.nameLabel.setText(name);
	}

	public void setInactive() {
		onBlurTextBox();
	}

	public boolean isActive() {
		return this.active;
	}
}
