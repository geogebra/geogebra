package geogebra.touch.gui.elements;

import java.util.ArrayList;

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

	TextBox textBox;
	Panel underline;
	ArrayList<InputField> box = new ArrayList<InputField>();
	private Label nameLabel;

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
	public InputField(String caption, boolean useUnderline) {
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
			public void onFocus(FocusEvent event) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					@Override
					public void execute() {
						InputField.this.textBox.setFocus(true);
						InputField.this.textBox.setCursorPos(InputField.this.textBox.getText().length());
						if (InputField.this.underline != null) {
							InputField.this.underline
									.removeStyleName("inactive");
							InputField.this.underline.addStyleName("active");
						}
						InputField.this.textBox.removeStyleName("inactive");
						InputField.this.textBox.addStyleName("active");
					}
				});
			}
		});

		this.textBox.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				InputField.this.textBox.setFocus(false);
				if (InputField.this.underline != null) {
					InputField.this.underline.removeStyleName("active");
					InputField.this.underline.addStyleName("inactive");
				}
				InputField.this.textBox.removeStyleName("active");
				InputField.this.textBox.addStyleName("inactive");
			}
		});

		this.textBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				InputField.this.textBox.setFocus(true);

				for (final InputField t : InputField.this.box) {
					t.setFocus(false);
				}
			}
		});
	}

	public void addErrorBox(HorizontalPanel errorBox) {
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

	public void addKeyDownHandler(KeyDownHandler keyDownHandler) {
		this.textBox.addKeyDownHandler(keyDownHandler);
	}

	public int getCursorPos() {
		return this.textBox.getCursorPos();
	}

	public String getText() {
		return this.textBox.getText();
	}

	public void setCursorPos(int i) {
		this.textBox.setCursorPos(i);
	}

	public void setFocus(boolean b) {
		this.textBox.setFocus(b);
	}

	public void setText(String string) {
		this.textBox.setText(string);
	}

	public void setTextBoxToLoseFocus(InputField[] text) {
		this.box = new ArrayList<InputField>();

		for (final InputField t : text) {
			if (!this.box.contains(t) && !t.equals(this)) {
				this.box.add(t);
			}
		}
	}
}
