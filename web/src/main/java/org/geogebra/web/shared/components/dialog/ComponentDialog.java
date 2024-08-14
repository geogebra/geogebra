package org.geogebra.web.shared.components.dialog;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Persistable;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.dom.client.EventTarget;
import org.gwtproject.event.dom.client.KeyCodes;
import org.gwtproject.user.client.Event;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.RequiresResize;

import jsinterop.base.Js;

/**
 * Base dialog material design component
 */
public class ComponentDialog extends GPopupPanel implements RequiresResize, Persistable, SetLabels {
	private FlowPanel dialogContent;
	private Runnable positiveAction;
	private Runnable negativeAction;
	private StandardButton posButton;
	private StandardButton negButton;
	private boolean preventHide = false;
	private final DialogData dialogData;
	private Label title;

	/**
	 * base dialog constructor
	 * @param app - see {@link AppW}
	 * @param dialogData - contains trans keys for title and buttons
	 * @param autoHide - if the dialog should be closed on click outside
	 * @param hasScrim - background should be greyed out
	 */
	public ComponentDialog(AppW app, DialogData dialogData, boolean autoHide,
			boolean hasScrim) {
		super(autoHide, app.getAppletFrame(), app);
		this.dialogData = dialogData;
		setGlassEnabled(hasScrim);
		this.setStyleName("dialogComponent");
		buildDialog();
		app.addWindowResizeListener(this::onResize);
	}

	private void  buildDialog() {
		FlowPanel dialogMainPanel = new FlowPanel();
		dialogMainPanel.addStyleName("dialogMainPanel");

		addTitleOfDialog(dialogMainPanel, dialogData.getTitleTransKey(),
				dialogData.getSubTitleHTML());
		createEmptyDialogContent(dialogMainPanel);
		if (dialogData.getNegativeBtnTransKey() != null
				|| dialogData.getPositiveBtnTransKey() != null) {
			addButtonsOfDialog(dialogMainPanel, dialogData);
		}

		this.add(dialogMainPanel);
	}

	private void addTitleOfDialog(FlowPanel dialogMainPanel, String titleTransKey,
			String subTitleHTML) {
		if (titleTransKey == null) {
			return;
		}

		title = BaseWidgetFactory.INSTANCE.newPrimaryText(
				getApplication().getLocalization().getMenu(titleTransKey), "dialogTitle");
		dialogMainPanel.add(title);

		if (subTitleHTML != null) {
			addStyleName("withSubtitle");
			Label subTitle = BaseWidgetFactory.INSTANCE.newSecondaryText(
					"", "dialogSubTitle");
			subTitle.getElement().setInnerHTML(subTitleHTML);
			dialogMainPanel.add(subTitle);
		}
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
				.getMenu(negTransKey));
		negButton.setStyleName("dialogTextButton");

		negButton.addClickHandler(((AppW) app).getGlobalHandlers(), source -> onNegativeAction());
		dialogButtonPanel.add(negButton);
	}

	private void addPositiveButton(FlowPanel dialogButtonPanel, String posTransKey) {
		if (posTransKey == null) {
			return;
		}

		posButton = new StandardButton(app.getLocalization()
				.getMenu(posTransKey));
		posButton.setStyleName("dialogContainedButton");

		posButton.addClickHandler(((AppW) app).getGlobalHandlers(), source -> onPositiveAction());
		dialogButtonPanel.add(posButton);
	}

	/**
	 * @param posLabel new label for positive button
	 * @param negLabel new label for negative button
	 */
	public void updateBtnLabels(String posLabel, String negLabel) {
		posButton.setLabel(app.getLocalization().getMenu(posLabel));
		negButton.setLabel(app.getLocalization().getMenu(negLabel));
	}

	public void setPosBtnDisabled(boolean disabled) {
		setBtnDisabled(posButton, disabled);
	}

	public void setNegBtnDisabled(boolean disabled) {
		setBtnDisabled(negButton, disabled);
	}

	private void setBtnDisabled(StandardButton btn, boolean disabled) {
		Dom.toggleClass(btn, "disabled", disabled);
	}

	public void setPreventHide(boolean preventHide) {
		this.preventHide = preventHide;
	}

	/**
	 * fills the dialog with content
	 * @param content - content of the dialog
	 */
	public void addDialogContent(IsWidget content) {
		dialogContent.add(content);
	}

	/**
	 * clears dialog content and fills with this widget
	 * @param content - content of the dialog
	 */
	public void setDialogContent(IsWidget content) {
		dialogContent.clear();
		dialogContent.add(content);
	}

	/**
	 * runs the negative action and hides the dialog
	 */
	private void onNegativeAction() {
		if (negButton != null
			&& negButton.getStyleName().contains("disabled")) {
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
		if (posButton != null
			&& posButton.getStyleName().contains("disabled")) {
			return;
		}
		if (positiveAction != null) {
			positiveAction.run();
		}
		if (!preventHide) {
			hide();
		}
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
		// make sure that the dialog content loaded before decide if should be scrollable
		Scheduler.get().scheduleDeferred(() -> {
			super.show();
			super.centerAndResize(((AppW) app).getAppletFrame().getKeyboardHeight());
		});
	}

	public void showDirectly() {
		super.show();
	}

	@Override
	public void onResize() {
		if (isShowing()) {
			super.centerAndResize(((AppW) app).getAppletFrame().getKeyboardHeight());
		}
	}

	private boolean isEnter(int key) {
		return key == KeyCodes.KEY_ENTER;
	}

	@Override
	protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
		if (!isVisible()) {
			return; // onPreviewNativeEvent is global: ignore for hidden dialogs
		}
		Event nativeEvent = Event.as(event.getNativeEvent());
		if (Event.ONKEYPRESS == event.getTypeInt() && isEnter(nativeEvent.getCharCode())
				&& !isContentEditable(nativeEvent.getEventTarget())
				&& !isTextarea(nativeEvent.getEventTarget())) {
			onPositiveAction();
		} else if (event.getTypeInt() == Event.ONKEYUP
				&& nativeEvent.getKeyCode() == KeyCodes.KEY_ESCAPE) {
			nativeEvent.stopPropagation();
			onEscape();
		}
	}

	private boolean isContentEditable(EventTarget eventTarget) {
		elemental2.dom.Element el = Js.uncheckedCast(eventTarget);
		return el.hasAttribute("contenteditable");
	}

	private boolean isTextarea(EventTarget eventTarget) {
		elemental2.dom.Element el = Js.uncheckedCast(eventTarget);
		return "TEXTAREA".equals(el.tagName);
	}

	protected void onEscape() {
		hide();
	}

	@Override
	public void setLabels() {
		title.setText(app.getLocalization().getMenu(dialogData.getTitleTransKey()));
		updateBtnLabels(dialogData.getPositiveBtnTransKey(), dialogData.getNegativeBtnTransKey());
	}
}
