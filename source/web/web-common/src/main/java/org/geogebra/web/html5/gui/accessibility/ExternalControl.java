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

package org.geogebra.web.html5.gui.accessibility;

import static elemental2.dom.DomGlobal.document;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.FocusableComponent;
import org.geogebra.web.html5.util.GlobalHandlerRegistry;

import elemental2.dom.Element;
import elemental2.dom.KeyboardEvent;
import jsinterop.base.Js;

public class ExternalControl implements FocusableComponent {
	private final ArrayList<Element> controls = new ArrayList<>();
	private final List<String> selectors;
	private final AccessibilityManagerW manager;
	private final GlobalHandlerRegistry registry;

	/**
	 * @param selectors selectors for individual controls
	 * @param manager accessibility manager
	 * @param registry handler registry
	 */
	public ExternalControl(List<String> selectors, AccessibilityManagerW manager,
			GlobalHandlerRegistry registry) {
		this.selectors = selectors;
		this.manager = manager;
		this.registry = registry;
	}

	@Override
	public boolean focusIfVisible(boolean reverse) {
		initControls();
		if (!controls.isEmpty()) {
			controls.get(0).focus();
			return true;
		}
		return false;
	}

	@Override
	public boolean hasFocus() {
		if (document.activeElement == null) {
			return false;
		}
		return selectors.stream().anyMatch(document.activeElement::matches);
	}

	@Override
	public boolean focusNext() {
		initControls();
		int index = controls.indexOf(document.activeElement);
		if (index + 1 < controls.size()) {
			controls.get(index + 1).focus();
			return true;
		}
		return false;
	}

	@Override
	public boolean focusPrevious() {
		initControls();
		int index = controls.indexOf(document.activeElement);
		if (index > 0) {
			controls.get(index - 1).focus();
			return true;
		}
		return false;
	}

	private void initControls() {
		if (!controls.isEmpty()) {
			return;
		}
		for (String selector: selectors) {
			Element control = document.querySelector(selector);
			registry.addEventListener(control, "keydown", evt -> {
				KeyboardEvent kbd = Js.uncheckedCast(evt);
				if ("Tab".equals(kbd.code)) {
					evt.preventDefault();
					if (kbd.shiftKey) {
						manager.focusPrevious();
					} else {
						manager.focusNext();
					}
				}
			});
			controls.add(control);
		}
	}

	@Override
	public AccessibilityGroup getAccessibilityGroup() {
		return AccessibilityGroup.EXTERNAL;
	}

	@Override
	public AccessibilityGroup.ViewControlId getViewControlId() {
		return null;
	}
}
