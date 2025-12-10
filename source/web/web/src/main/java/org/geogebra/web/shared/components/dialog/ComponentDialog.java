/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.shared.components.dialog;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.accessibility.HasFocus;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Persistable;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.client.EventTarget;
import org.gwtproject.event.dom.client.KeyCodes;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.Event;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.RequiresResize;
import org.gwtproject.user.client.ui.Widget;

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
	private final List<Widget> widgetList = new ArrayList<>();
	private int focusIndex = 0;

	/**
	 * base dialog constructor
	 * @param app see {@link AppW}
	 * @param dialogData contains trans keys for title and buttons
	 * @param autoHide if the dialog should be closed on click outside
	 * @param hasScrim background should be greyed out
	 */
	public ComponentDialog(AppW app, DialogData dialogData, boolean autoHide,
			boolean hasScrim) {
		super(autoHide, app.getAppletFrame(), app);
		this.dialogData = dialogData;
		setGlassEnabled(hasScrim);
		this.setStyleName("dialogComponent");
		buildDialog();
		app.addWindowResizeListener(this);
		setAccessibilityProperties(hasScrim);
		sinkEvents(Event.ONKEYDOWN);
	}

	private void buildDialog() {
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
		negButton.addStyleName("keyboardFocus");

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
		posButton.addStyleName("keyboardFocus");

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

	/**
	 * Enables or disables the positive button.
	 * @param disabled whether to disable
	 */
	public void setPosBtnDisabled(boolean disabled) {
		setBtnDisabled(posButton, disabled);
	}

	private void setBtnDisabled(StandardButton btn, boolean disabled) {
		Dom.toggleClass(btn, "disabled", disabled);
	}

	public void setPreventHide(boolean preventHide) {
		this.preventHide = preventHide;
	}

	/**
	 * fills the dialog with content
	 * @param content content of the dialog
	 */
	public void addDialogContent(IsWidget content) {
		dialogContent.add(content);
		content.asWidget().addStyleName("keyboardFocus");
		if (!content.asWidget().getElement().hasAttribute("tabindex")) {
			content.asWidget().getElement().setTabIndex(0);
		}
		widgetList.add(content.asWidget());
	}

	/**
	 * fills the dialog with the given list of widgets
	 * @param widgets widget list of the dialog
	 */
	public void addDialogContent(IsWidget... widgets) {
		for (IsWidget widget : widgets) {
			addDialogContent(widget);
		}
	}

	/**
	 * clears dialog content and fills with this widget
	 * @param content content of the dialog
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
	 * @param posAction what should happen on positive button hit
	 */
	public void setOnPositiveAction(Runnable posAction) {
		positiveAction = posAction;
	}

	/**
	 * set negative action
	 * @param negAction what should happen on negative button hit
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
			widgetList.add(negButton);
			widgetList.add(posButton);
			widgetList.get(0).getElement().focus();
		});
	}

	/**
	 * Show without centering.
	 */
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
			EventTarget target = nativeEvent.getEventTarget();
			if (Element.is(target)
					&& Element.as(target).getClassName().contains("dialogTextButton")) {
				onEscape();
			} else {
				onPositiveAction();
			}
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

	private void setAccessibilityProperties(boolean isModal) {
		AriaHelper.setRole(this, "dialog");
		AriaHelper.setModal(this, isModal);
		AriaHelper.setTitle(this, app.getLocalization().getMenu(dialogData.getTitleTransKey()));
	}

	@Override
	public void onBrowserEvent(Event event) {
		if (DOM.eventGetType(event) == Event.ONKEYDOWN
				&& event.getKeyCode() == KeyCodes.KEY_TAB) {
			handleTab();
			event.stopPropagation();
			event.preventDefault();
		}
	}

	private void handleTab() {
		do {
			focusIndex = focusIndex >= widgetList.size() - 1 ? 0 : focusIndex + 1;
		} while (widgetList.get(focusIndex).getElement().getTabIndex() < 0);
		if (widgetList.get(focusIndex) instanceof HasFocus) {
			((HasFocus) widgetList.get(focusIndex)).focus();
		} else {
			widgetList.get(focusIndex).getElement().focus();
		}
	}
}
