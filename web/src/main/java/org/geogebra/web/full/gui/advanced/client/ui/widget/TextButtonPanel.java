/*
 * Copyright 2008-2013 Sergey Skladchikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.geogebra.web.full.gui.advanced.client.ui.widget;

import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.advanced.client.ui.AdvancedWidget;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.FormLabel.HasInputElement;
import org.geogebra.web.html5.gui.view.button.MyToggleButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * This is a basic class for all text boxs with a button.
 * 
 * @see ComboBox
 * 
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @param <T>
 *            value type
 * @since 1.2.0
 */
public abstract class TextButtonPanel<T> extends SimplePanel
		implements AdvancedWidget, HasValue<T>,
		HasInputElement {
	/** widget layout */
	private FlexTable layout;
	/** a selected value box */
	private AutoCompleteTextFieldW selectedValue;
	/** a choice button */
	private MyToggleButton choiceButton;
	/** a choice button image */
	private Image choiceButtonImage;
	/** this flag means whether it's possible to enter a custom text */
	private boolean customTextAllowed;

	/** choice button visibility flag */
	private boolean choiceButtonVisible;
	/** widget width */
	private String width;
	/** widget height */
	private String height;

	private EnterAction enterAction = EnterAction.OPEN_DROP_DOWN;
	private final AppW app;

	/** action to be performed on Enter key press in the text field */
	public enum EnterAction {
		/** open */
		OPEN_DROP_DOWN,
		/** ignore */
		DO_NOTHING;
	}

	/**
	 * @param app
	 *            application
	 */
	protected TextButtonPanel(AppW app) {
		this.app = app;
		getLayout().setWidget(0, 0, getSelectedValue());
		setChoiceButtonVisible(true);
		setCustomTextAllowed(false);
		setStyleName("advanced-TextButtonPanel");
		setWidget(getLayout());
		addComponentListeners();
	}

	/**
	 * Getter for property 'customTextAllowed'.
	 *
	 * @return Value for property 'customTextAllowed'.
	 */
	public boolean isCustomTextAllowed() {
		return customTextAllowed;
	}

	/**
	 * Setter for property 'customTextAllowed'.
	 *
	 * @param customTextAllowed
	 *            Value to set for property 'customTextAllowed'.
	 */
	public void setCustomTextAllowed(boolean customTextAllowed) {
		this.customTextAllowed = customTextAllowed;
		prepareSelectedValue();
	}

	/**
	 * Setter for property 'choiceButtonImage'.
	 *
	 * @param choiceButtonImage
	 *            Value to set for property 'choiceButtonImage'.
	 */
	public void setChoiceButtonImage(Image choiceButtonImage) {
		this.choiceButtonImage = choiceButtonImage;
	}

	/**
	 * Getter for property 'choiceButtonVisible'.
	 *
	 * @return Value for property 'choiceButtonVisible'.
	 */
	public boolean isChoiceButtonVisible() {
		return choiceButtonVisible;
	}

	/**
	 * Setter for property 'choiceButtonVisible'.
	 *
	 * @param choiceButtonVisible
	 *            Value to set for property 'choiceButtonVisible'.
	 */
	public void setChoiceButtonVisible(boolean choiceButtonVisible) {
		if (!choiceButtonVisible && isChoiceButtonVisible()) {
			getLayout().removeCell(0, 1);
		} else if (choiceButtonVisible && !isChoiceButtonVisible()) {
			getLayout().setWidget(0, 1, getChoiceButton());
			prepareChoiceButton();
		}
		this.choiceButtonVisible = choiceButtonVisible;
	}

	/** {@inheritDoc} */
	@Override
	public void setWidth(String width) {
		super.setWidth(width);
		this.width = width;
		prepareSelectedValue();
	}

	/** {@inheritDoc} */
	@Override
	public void setHeight(String height) {
		super.setHeight(height);
		this.height = height;
		prepareSelectedValue();
	}

	/**
	 * This method gets a maximum length of the text box.
	 * <p/>
	 * It makes sence if you allow custom values entering.
	 * <p/>
	 * See also {@link #isCustomTextAllowed()} and
	 * {@link #setCustomTextAllowed(boolean)}.
	 *
	 * @return a maximum length of the text box.
	 */
	public int getMaxLength() {
		return 100; // getSelectedValue().getMaxLength();
	}

	/**
	 * This method sets a maximum length of the text box.
	 * <p/>
	 * It makes sence if you allow custom values entering.
	 * <p/>
	 * See also {@link #isCustomTextAllowed()} and
	 * {@link #setCustomTextAllowed(boolean)}.
	 *
	 * @param length
	 *            is a maximum length of the text box.
	 */
	public void setMaxLength(int length) {
		// getSelectedValue().setMaxLength(length);
	}

	/**
	 * Enables or disables the controls inside the panel.
	 *
	 * @param enabled
	 *            is a flag that means whether the controls must be enabled.
	 */
	public void setEnabled(boolean enabled) {
		getSelectedValue().setEditable(enabled);
		getChoiceButton().setEnabled(enabled);
	}

	/**
	 * Cleans all the data displayed in the widget.
	 */
	public void cleanSelection() {
		getSelectedValue().setText("");
	}

	/**
	 * This method adds component listeners.
	 */
	protected abstract void addComponentListeners();

	/**
	 * Prepares the selected value box for displaying.
	 */
	protected void prepareSelectedValue() {
		AutoCompleteTextFieldW selValue = getSelectedValue();
		selValue.setEditable(!isCustomTextAllowed());
		selValue.setStyleName("selected-value");

		if (getHeight() != null) {
			getLayout().setHeight("100%");
			getLayout().getCellFormatter().setHeight(0, 0, "100%");
			getSelectedValue().setHeight("100%");
		}

		if (getWidth() != null) {
			getLayout().setWidth("100%");
			getLayout().getCellFormatter().setWidth(0, 0, "100%");
			getSelectedValue().setWidth("100%");
		}
	}

	/**
	 * Prepares the drop down button for displaying.
	 */
	protected void prepareChoiceButton() {
		MyToggleButton dropDownButton = getChoiceButton();
		if (app.isUnbundledOrWhiteboard()) {
			dropDownButton.setUpfaceDownfaceImg(
					MaterialDesignResources.INSTANCE.arrow_drop_down(),
					MaterialDesignResources.INSTANCE.arrow_drop_up());
		} else {
			dropDownButton.getUpFace().setImage(getChoiceButtonImage());
			dropDownButton.getDownFace().setImage(getChoiceButtonImage());
		}
		dropDownButton.setStyleName("choice-button");
	}

	/**
	 * Getter for property 'layout'.
	 *
	 * @return Value for property 'layout'.
	 */
	protected FlexTable getLayout() {
		if (layout == null) {
			layout = new FlexTable();
			layout.setCellPadding(0);
			layout.setCellSpacing(0);
			layout.getFlexCellFormatter().setWidth(0, 0, "100%");
		}
		return layout;
	}

	/**
	 * @param tf
	 *            textfield
	 */
	public void setTextField(AutoCompleteTextFieldW tf) {
		selectedValue = tf;
	}

	/**
	 * Getter for property 'selectedValue'.
	 *
	 * @return Value for property 'selectedValue'.
	 */
	protected AutoCompleteTextFieldW getSelectedValue() {
		if (selectedValue == null) {
			selectedValue = new AutoCompleteTextFieldW(6, getApp());
			selectedValue.requestToShowSymbolButton();
			selectedValue.setAutoComplete(false);
		}
		return selectedValue;
	}

	/**
	 * Getter for property 'choiceButton'.
	 *
	 * @return Value for property 'choiceButton'.
	 */
	protected MyToggleButton getChoiceButton() {
		if (choiceButton == null) {
			choiceButton = new MyToggleButton(app);
		}
		return choiceButton;
	}

	/**
	 * Getter for property 'choiceButtonImage'.
	 *
	 * @return Value for property 'choiceButtonImage'.
	 */
	private Image getChoiceButtonImage() {
		if (choiceButtonImage == null) {
			choiceButtonImage = new Image(
					GuiResources.INSTANCE.little_triangle_down());
		}
		return choiceButtonImage;
	}

	/**
	 * Getter for property 'width'.
	 *
	 * @return Value for property 'width'.
	 */
	private String getWidth() {
		return width;
	}

	/**
	 * Getter for property 'height'.
	 *
	 * @return Value for property 'height'.
	 */
	private String getHeight() {
		return height;
	}

	/**
	 * @return action on enter
	 */
	public ComboBox.EnterAction getEnterAction() {
		return enterAction;
	}

	/**
	 * @param enterAction
	 *            action on enter
	 */
	public void setEnterAction(ComboBox.EnterAction enterAction) {
		this.enterAction = enterAction;
	}

	/**
	 * @return application
	 */
	public AppW getApp() {
		return app;
	}

	@Override
	public Element getInputElement() {
		return getSelectedValue().getTextField().getElement();
	}
}
