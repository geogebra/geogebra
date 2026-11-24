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

package org.geogebra.web.full.gui.view.algebra;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.Localization;
import org.geogebra.editor.share.controller.PiecewiseController;
import org.geogebra.editor.share.controller.PiecewiseController.Bounds;
import org.geogebra.editor.share.controller.PiecewiseController.State;
import org.geogebra.editor.share.util.PiecewiseControl;
import org.geogebra.editor.web.MathFieldW;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.FlowPanel;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.EventTarget;
import elemental2.dom.FocusEvent;
import elemental2.dom.HTMLElement;
import elemental2.dom.MouseEvent;
import elemental2.dom.Node;
import jsinterop.base.Js;

/**
 * Buttons that add and remove the cases of a piecewise function while it is edited. They are
 * laid over the formula, in the space that it leaves for them.
 */
public final class PiecewiseButtons implements PiecewiseController.StateListener {
	private static final int ICON_SIZE = 16;
	private static final int BUTTON_SIZE = 24;
	private static final String[] HANDLED_EVENTS = {
		"pointerdown", "pointerup", "mousedown", "mouseup", "touchstart", "touchend", "click"
	};

	private final MathFieldW mathField;
	private final PiecewiseController controller;
	private final FlowPanel container;
	private final FlowPanel overlay = new FlowPanel();
	private final Localization localization;
	private final Runnable onFocusLeft;
	private final Map<PiecewiseControl, StandardButton> buttons = new HashMap<>();
	private boolean activating;
	private double focusedCaseRightEdge;

	/**
	 * @param mathField editor
	 * @param container panel with the canvas of the editor; the buttons are added to it
	 * @param localization localization
	 * @param onFocusLeft called when the focus moves from a button to neither the editor nor
	 * another button, like a blur of the editor
	 */
	PiecewiseButtons(
			@NonNull MathFieldW mathField,
			@NonNull FlowPanel container,
			@NonNull Localization localization,
			@NonNull Runnable onFocusLeft) {
		this.mathField = mathField;
		this.controller = mathField.getInternal().getPiecewiseController();
		this.container = container;
		this.localization = localization;
		this.onFocusLeft = onFocusLeft;
		// The buttons are positioned relative to this empty element. It is part of the content
		// next to the canvas, so the buttons scroll together with the formula.
		overlay.setStyleName("piecewiseButtons");
	}

	/**
	 * Start showing the buttons that the editor asks for.
	 */
	void attach() {
		if (overlay.getParent() != container) {
			container.insert(overlay, 0);
		}
		controller.addListener(this);
		stateChanged(controller.getState());
	}

	/**
	 * Remove all buttons and stop listening to the editor.
	 */
	void detach() {
		controller.removeListener(this);
		overlay.clear();
		overlay.removeFromParent();
		buttons.clear();
		focusedCaseRightEdge = 0;
	}

	/**
	 * @param target event target, e.g. the element that receives the focus
	 * @return whether the target is one of the buttons
	 */
	boolean isButton(@Nullable EventTarget target) {
		Node overlayNode = Js.uncheckedCast(overlay.getElement());
		return target != null && overlayNode.contains(Js.uncheckedCast(target));
	}

	@Override
	public void stateChanged(@NonNull State state) {
		Set<PiecewiseControl> shown = new HashSet<>();
		for (PiecewiseController.Button button : state.buttons()) {
			shown.add(button.control());
			update(button);
		}
		Iterator<Map.Entry<PiecewiseControl, StandardButton>> iterator =
				buttons.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<PiecewiseControl, StandardButton> entry = iterator.next();
			if (!shown.contains(entry.getKey())) {
				entry.getValue().removeFromParent();
				iterator.remove();
			}
		}
		// The cursor may have entered the structure without typing, e.g. by a click. Hovering
		// changes the buttons as well, but does not move the button of the case with the cursor.
		double rightEdge = controller.getFocusedCaseRightEdge();
		if (rightEdge > 0 && Double.compare(rightEdge, focusedCaseRightEdge) != 0) {
			mathField.scrollParentHorizontally(container);
		}
		focusedCaseRightEdge = rightEdge;
	}

	private void update(PiecewiseController.Button model) {
		StandardButton button = buttons.get(model.control());
		if (button == null) {
			button = createButton(model.control());
			buttons.put(model.control(), button);
			overlay.add(button);
		}
		Dom.toggleClass(button, "disabled", !model.enabled());
		AriaHelper.setAriaDisabled(button, !model.enabled());

		Bounds bounds = model.bounds();
		double canvasLeft =
				mathField.asWidget().getElement().getOffsetLeft() - overlay.getElement().getOffsetLeft();
		double canvasTop =
				mathField.asWidget().getElement().getOffsetTop() - overlay.getElement().getOffsetTop();
		double left = canvasLeft + mathField.toCanvasX(bounds.left());
		double centerY = canvasTop + mathField.toCanvasY(bounds.top() + bounds.height() / 2);
		if (model.control().type() == PiecewiseControl.Type.REMOVE_CASE) {
			// centered in its space
			left += (mathField.toCanvasX(bounds.width()) - BUTTON_SIZE) / 2;
		}
		button.getElement().getStyle().setLeft(left, Unit.PX);
		button.getElement().getStyle().setTop(centerY - BUTTON_SIZE / 2.0, Unit.PX);
	}

	private StandardButton createButton(PiecewiseControl control) {
		StandardButton button;
		if (control.type() == PiecewiseControl.Type.ADD_CASE) {
			button = new StandardButton(
					MaterialDesignResources.INSTANCE
							.add_black()
							.withFill(GeoGebraColorConstants.PURPLE_700.toString()),
					localization.getMenuDefault(
							PiecewiseControl.ADD_CASE_KEY, PiecewiseControl.ADD_CASE_DEFAULT),
					ICON_SIZE);
			button.addStyleName("addCase");
		} else {
			button = new StandardButton(
					GuiResourcesSimple.INSTANCE
							.close()
							.withFill(GeoGebraColorConstants.NEUTRAL_700.toString()),
					ICON_SIZE);
			button.addStyleName("removeCase");
			AriaHelper.setLabel(button, localization.getMenu("Remove"));
		}
		button.addStyleName("piecewiseButton");
		button.addStyleName(MathFieldW.OVERLAY_STYLE_NAME);

		HTMLElement element = Js.uncheckedCast(button.getElement());
		// the button handles its own input: the row behind it must not react to it as well
		for (String type : HANDLED_EVENTS) {
			element.addEventListener(type, Event::stopPropagation);
		}
		// keep the focus in the editor, so that editing goes on after using the button
		element.addEventListener("pointerdown", Event::preventDefault);
		element.addEventListener("click", event -> {
			boolean focused = DomGlobal.document.activeElement == element;
			// the button may lose the focus by being removed
			activating = true;
			controller.performAction(control);
			activating = false;
			if (focused) {
				mathField.setFocus(true);
			}
		});
		element.addEventListener("focusout", event -> {
			EventTarget next = Js.<FocusEvent>uncheckedCast(event).relatedTarget;
			if (!activating
					&& next != Js.uncheckedCast(mathField.getInputTextArea().getElement())
					&& !isButton(next)) {
				onFocusLeft.run();
			}
		});
		element.addEventListener("pointerleave", event -> {
			MouseEvent mouseEvent = Js.uncheckedCast(event);
			Element canvas = Js.uncheckedCast(mathField.asWidget().getElement());
			// the editor keeps track of the pointer again once it is back over the formula
			if (mouseEvent.relatedTarget != canvas) {
				mathField.getInternal().onHoverEnd();
			}
		});
		return button;
	}
}
