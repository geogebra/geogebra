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

package org.geogebra.common.gui.compositefocus;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;

/**
 * Base class for managing focus traversal within a composite UI element.
 *
 * <p>A composite focus represents a single focusable component that contains
 * multiple focusable parts, navigable using dedicated traversal shortcuts.
 * This class manages selection state and delegates focus handling to the parts.</p>
 *
 * @param <T> the type of focusable parts managed by this composite
 */
public abstract class AbstractFocusableComposite<T extends FocusablePart>
		implements FocusableComposite {
	protected final List<T> parts = new ArrayList<>();
	protected int selectedIndex = -1;
	private String selectedKey = null;

	public AbstractFocusableComposite(Object o) {

	}

	/**
	 * @return whether any part of this composite currently has focus
	 */
	@Override
	public final boolean hasFocus() {
		return selectedIndex != -1 && !parts.isEmpty();
	}

	/**
	 * Moves focus to the next part within the composite.
	 *
	 * @return true if focus was moved; false if no parts are available
	 */
	@Override
	public final boolean focusNext() {
		if (parts.isEmpty()) {
			return false;
		}
		blurCurrent();
		selectedIndex = (selectedIndex + 1) % parts.size();
		focusCurrent();
		return true;
	}

	/**
	 * Moves focus to the previous part within the composite.
	 *
	 * @return true if focus was moved; false if no parts are available
	 */
	@Override
	public final boolean focusPrevious() {
		if (parts.isEmpty()) {
			return false;
		}
		blurCurrent();
		selectedIndex = (selectedIndex - 1 + parts.size()) % parts.size();
		focusCurrent();
		return true;
	}

	/**
	 * Adds a focusable part to this composite.
	 *
	 * @param part the part to add
	 */
	public final void addPart(T part) {
		parts.add(part);
	}

	/**
	 * Removes all parts from this composite and clears focus state.
	 */
	public final void clearParts() {
		parts.clear();
		resetSelection();
	}

	protected void resetSelection() {
		selectedIndex = -1;
		selectedKey = null;
	}

	/**
	 * Applies focus to the currently selected part and announces it for accessibility.
	 */
	protected void focusCurrent() {
		T part = getSelectedPart();
		if (part == null) {
			return;
		}

		readDebug(part.getAccessibleLabel());
		focus(part);
	}

	protected void readDebug(String text) {
		// no debug by default
	}

	protected void focus(T part) {
		part.focus();
		selectedKey = part.getFocusKey();
	}

	/**
	 * @return the currently selected part, or {@code null} if none is selected
	 */
	protected final @CheckForNull T getSelectedPart() {
		return hasSelectedPart() ? parts.get(selectedIndex) : null;
	}

	private boolean hasSelectedPart() {
		return selectedIndex >= 0 && selectedIndex < parts.size();
	}

	/**
	 * Removes focus from the currently selected part, if any.
	 */
	protected void blurCurrent() {
		T part = getSelectedPart();
		if (part == null) {
			return;
		}

		part.blur();
	}

	/**
	 * @return the focus key of the currently selected part, or {@code null}
	 *         if no part is selected
	 */
	public final @CheckForNull String getSelectedKey() {
		return selectedKey;
	}

	/**
	 * Restores selection to the part identified by the given focus key.
	 *
	 * <p>If a matching part is found, it becomes the selected part.
	 * If the key is {@code null} or no matching part exists, the selection
	 * is cleared.</p>
	 *
	 * @param key focus key identifying the part to select
	 */
	public void restoreSelection(@CheckForNull String key) {
		if (key == null) {
			return;
		}
		for (int i = 0; i < parts.size(); i++) {
			T part = parts.get(i);
			if (key.equals(part.getFocusKey())) {
				selectedIndex = i;
				focusCurrent();
				return;
			}
		}
		selectedIndex = -1;
	}

	@Override
	public final boolean focusFirst() {
		if (parts.isEmpty()) {
			return false;
		}

		onGainFocus();

		blurCurrent();
		selectedIndex = 0;
		focusCurrent();
		return true;
	}

	protected abstract void onGainFocus();

	@Override
	public final boolean focusLast() {
		if (parts.isEmpty()) {
			return false;
		}
		onGainFocus();
		blurCurrent();
		selectedIndex = parts.size() - 1;
		focusCurrent();
		return true;
	}

	@Override
	public void blur() {
		blurCurrent();
		resetSelection();
	}

	@Override
	public final boolean handlesEnterKeyForSelectedPart() {
		T part = getSelectedPart();
		return part != null && part.handlesEnterKey();
	}
}
