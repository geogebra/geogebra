package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.inputbar.AlgebraInputW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.event.dom.client.KeyDownEvent;
import org.gwtproject.event.dom.client.KeyDownHandler;
import org.gwtproject.user.client.ui.Widget;

import com.himamis.retex.editor.share.util.GWTKeycodes;

public class LinearNotationTreeItem extends RadioTreeItem implements KeyDownHandler {

	private AutoCompleteTextFieldW textField;

	/**
	 * Minimal constructor
	 * @param kernel {@link Kernel}
	 * @param av {@link AlgebraViewW}
	 */
	public LinearNotationTreeItem(Kernel kernel, AlgebraViewW av) {
		super(kernel, av);
		ensureTextField();
	}

	public LinearNotationTreeItem(GeoElement geo) {
		super(geo);
	}

	private void createTextField() {
		textField = new AutoCompleteTextFieldW(app);
		textField.setAutoComplete(false);
		textField.setAutoCloseParentheses(false);
		setText(getTextForEditing(false, StringTemplate.linearNotation));
		textField.getTextField().addKeyDownHandler(this);
		textField.addFocusHandler(ignore -> getAV().startEditItem(geo));
		textField.addBlurHandler(controller);
	}

	private void ensureTextField() {
		if (textField == null) {
			createTextField();
		}
	}

	@Override
	public void doUpdate() {
		ensureTextField();
		setNeedsUpdate(false);
		if (typeChanged()) {
			updateTreeItemAfterTypeChanged();
			return;
		}

		if (hasMarblePanel()) {
			marblePanel.update();
		}

		buildItemContent();
	}

	private void buildItemContent() {
		if (getController().isEditing() || geo == null) {
			return;
		}
		content.clear();
		if (shouldBuildItemWithTwoRows()) {
			buildItemWithTwoRows();
		} else {
			buildItemWithSingleRow();
		}
	}

	private void buildItemWithTwoRows() {
		createDVPanels();
		content.add(textField);
		if (updateOutputValuePanel()) {
			outputPanel.addValuePanel();
			content.add(outputPanel);
		}
	}

	private void buildItemWithSingleRow() {
		content.add(textField);
	}

	@Override
	public RadioTreeItem initInput() {
		insertHelpToggle();
		new FocusableWidget(AccessibilityGroup.ALGEBRA_ITEM, null, content) {
			@Override
			public void focus(Widget widget) {
				setFocus(true);
			}

			@Override
			public boolean hasFocus() {
				return textField.hasFocus();
			}
		}.attachTo(app);
		getHelpToggle().setIndex(1);
		inputControl.addInputControls();

		content.clear();
		content.addStyleName("elemText");
		buildItemWithSingleRow();
		setItemWidth(getAV().getFullWidth());
		if (!content.isAttached()) {
			main.add(content);
		}
		return this;
	}

	@Override
	public boolean enterEditMode(boolean substituteNumbers) {
		boolean success = super.enterEditMode(substituteNumbers);
		content.removeStyleName("scrollableTextBox");
		return success;
	}

	@Override
	public boolean onEditStart() {
		updatePreview();
		adjustStyleBar();
		setFocus(true);
		return true;
	}

	@Override
	public void onStopEdit() {
		cancelEditing();
	}

	@Override
	public void cancelEditing() {
		updateIcons(false);
		getController().setEditing(false);
		setFocus(false);

		showStyleBarIfNeeded();
		kernel.notifyUpdatePreviewFromInputBar(null);
		getAV().cancelEditItem();
		app.getActiveEuclidianView().requestFocus();
	}

	@Override
	public void setFocus(boolean focus) {
		ensureTextField();
		if (focus) {
			if (app.isUnbundled()) {
				app.hideMenu();
			}
			if (isInputTreeItem()) {
				MinMaxPanel.closeMinMaxPanel();
				getAV().restoreWidth(true);
			}
		} else if (isInputTreeItem()) {
			setItemWidth(getAV().getFullWidth());
		}
		if (geo == null) {
			initInput();
		}
		setFocusedStyle(focus);
		textField.setFocus(focus);
	}

	@Override
	public String getText() {
		return textField.getText();
	}

	@Override
	protected String getTextForEditing(boolean substituteNumbers, StringTemplate tpl) {
		if (geo == null) {
			return textField.getText();
		}
		return super.getTextForEditing(substituteNumbers, tpl);
	}

	@Override
	public void setText(String text) {
		textField.setText(text);
	}

	@Override
	public String getCommand() {
		return "";
	}

	@Override
	public boolean isLinearNotationItem() {
		return true;
	}

	@Override
	public void setItemWidth(int width) {
		textField.setWidth(width - 130);
		super.setItemWidth(width);
	}

	@Override
	protected void addDummyLabel() {
		// Not needed here
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		getController().setEditing(true);
		if (event.getNativeKeyCode() == GWTKeycodes.KEY_ENTER) {
			getController().onEnter(true);
			event.preventDefault();
			return;
		} else if (event.getNativeKeyCode() == GWTKeycodes.KEY_TAB) {
			getController().onTab(event.getNativeEvent().getShiftKey());
			event.preventDefault();
			return;
		}
		Scheduler.get().scheduleDeferred(this::updatePreview);
	}

	@Override
	protected void updatePreview() {
		kernel.getInputPreviewHelper().updatePreviewFromInputBar(
				textField.getText(), AlgebraInputW.getWarningHandler(this, app));
	}

	@Override
	public RadioTreeItem copy() {
		return new LinearNotationTreeItem(geo);
	}

	@Override
	public void insertString(String text) {
		textField.insertString(text);
	}
}
