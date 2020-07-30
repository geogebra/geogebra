package org.geogebra.web.shared.components;

import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.Persistable;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

/**
 * Base dialog material design component
 */
public class ComponentDialog extends GPopupPanel implements Persistable, ResizeHandler {
	private Label title;
	private FlowPanel dialogContent;
	private Runnable positiveAction;
	private Runnable negativeAction;
	private StandardButton posButton;
	private StandardButton negButton;

	/**
	 * base dialog constructor
	 * @param app - see {@link AppW}
	 * @param dialogData - contains trans keys for title and buttons
	 * @param autoHide - if the dialog should be closed on click outside
	 * @param hasScrim - background should be greyed out
	 */
	public ComponentDialog(AppW app, DialogData dialogData, boolean autoHide,
						   boolean hasScrim) {
		super(autoHide, app.getPanel(), app);
		setGlassEnabled(hasScrim);
		this.setStyleName("dialogComponent");
		buildDialog(dialogData);
		Window.addResizeHandler(this);
	}

	private void  buildDialog(DialogData dialogData) {
		FlowPanel dialogMainPanel = new FlowPanel();
		dialogMainPanel.addStyleName("dialogMainPanel");

		addTitleOfDialog(dialogMainPanel, dialogData.getTitleTransKey());
		createEmptyDialogContent(dialogMainPanel);
		if (dialogData.getNegativeBtnTransKey() != null
				|| dialogData.getPositiveBtnTransKey() != null) {
			addButtonsOfDialog(dialogMainPanel, dialogData);
		}

		this.add(dialogMainPanel);
	}

	private void addTitleOfDialog(FlowPanel dialogMainPanel, String titleTransKey) {
		if (titleTransKey == null) {
			return;
		}

		title = new Label(getApplication().getLocalization().getMenu(titleTransKey));
		title.setStyleName("dialogTitle");
		dialogMainPanel.add(title);
	}

	private void createEmptyDialogContent(FlowPanel dialogMainPanel) {
		dialogContent = new FlowPanel();
		dialogContent.addStyleName("dialogContent");
		dialogMainPanel.add(dialogContent);
	}

	private void addButtonsOfDialog(FlowPanel dialogMainPanel, DialogData dialogData) {
		FlowPanel dialogButtonPanel = new FlowPanel();
		dialogButtonPanel.setStyleName("dialogBtnPanel");

		addNegativeButton(dialogButtonPanel, dialogData.getNegativeBtnTransKey());
		addPositiveButton(dialogButtonPanel, dialogData.getPositiveBtnTransKey());

		dialogMainPanel.add(dialogButtonPanel);
	}

	private void addNegativeButton(FlowPanel dialogButtonPanel, String negTransKey) {
		if (negTransKey == null) {
			return;
		}

		negButton = new StandardButton(app.getLocalization()
				.getMenu(negTransKey), getApplication());
		negButton.setStyleName("dialogTextButton");

		negButton.addFastClickHandler(source -> onNegativeAction());
		dialogButtonPanel.add(negButton);
	}

	private void addPositiveButton(FlowPanel dialogButtonPanel, String posTransKey) {
		if (posTransKey == null) {
			return;
		}

		posButton = new StandardButton(app.getLocalization()
				.getMenu(posTransKey), getApplication());
		posButton.setStyleName("dialogContainedButton");

		posButton.addFastClickHandler(source -> onPositiveAction());
		dialogButtonPanel.add(posButton);
	}

	public void setPosBtnDisabled(boolean disabled) {
		setBtnDisabled(posButton, disabled);
	}

	public void setNedBtnDisabled(boolean disabled) {
		setBtnDisabled(negButton, disabled);
	}

	private void setBtnDisabled(StandardButton btn, boolean disabled) {
		Dom.toggleClass(btn, "disabled", disabled);
	}

	/**
	 * fills the dialog with content
	 * @param content - content of the dialog
	 */
	public void addDialogContent(IsWidget content) {
		dialogContent.add(content);
	}

	/**
	 * runs the negative action and hides the dialog
	 */
	private void onNegativeAction() {
		if (negButton.getStyleName().contains("disabled")) {
			return;
		}
		if (negativeAction != null) {
			negativeAction.run();
		}
		hide();
	}

	/**
	 * runs the positive action and hides the dialog
	 */
	public void onPositiveAction() {
		if (posButton.getStyleName().contains("disabled")) {
			return;
		}
		if (positiveAction != null) {
			positiveAction.run();
		}
		hide();
	}

	/**
	 * set positive action
	 * @param posAction - what should happen on positive button hit
	 */
	public void setOnPositiveAction(Runnable posAction) {
		positiveAction = posAction;
	}

	/**
	 * set negative action
	 * @param negAction - what should happen on negative button hit
	 */
	public void setOnNegativeAction(Runnable negAction) {
		negativeAction = negAction;
	}

	@Override
	public void show() {
		super.show();
		super.center();
	}

	@Override
	public void onResize(ResizeEvent resizeEvent) {
		if (isShowing()) {
			super.center();
		}
	}

	private boolean isEnter(int key) {
		return key == KeyCodes.KEY_ENTER;
	}

	@Override
	protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
		Event nativeEvent = Event.as(event.getNativeEvent());
		if (Event.ONKEYPRESS == event.getTypeInt() && isEnter(nativeEvent.getCharCode())) {
			onPositiveAction();
		} else if (event.getTypeInt() == Event.ONKEYUP
				&& event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
			hide();
		}
	}
}
