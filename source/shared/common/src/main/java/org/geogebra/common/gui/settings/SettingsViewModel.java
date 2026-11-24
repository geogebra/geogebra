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

package org.geogebra.common.gui.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.gui.colorpicker.ColorHistory;
import org.geogebra.common.gui.colorpicker.ColorPickerModel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.ownership.SuiteScope;
import org.geogebra.common.properties.ObjectSettingsSession;
import org.geogebra.common.properties.PropertyViewFactory;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.impl.undo.UndoSavingPropertyObserver;
import org.geogebra.common.spreadsheet.core.Spreadsheet;
import org.geogebra.common.states.MutableState;
import org.geogebra.common.states.State;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.google.j2objc.annotations.Weak;

/**
 * View model of the settings sheet. Holds a navigation stack of {@link SettingsPage}s, the last
 * page being the visible one; the sheet is hidden when the stack is empty.
 * <p>
 * Pages hold references to properties and elements, so {@link #close()} must be called on app
 * teardown and when a new construction is loaded.
 */
public final class SettingsViewModel {

	@Weak
	private final App app;

	private final MutableState<@NonNull List<SettingsPage>> navigationStack =
			new MutableState<>(Collections.emptyList());
	private State.@Nullable Subscription propertyTreeSubscription;

	/**
	 * @param app the app (held weakly)
	 */
	public SettingsViewModel(@NonNull App app) {
		this.app = app;
	}

	/** @return the navigation stack, bottom first; empty when the sheet is hidden */
	public @NonNull State<@NonNull List<SettingsPage>> getNavigationStack() {
		return navigationStack;
	}

	/**
	 * Shows the app settings as the only page.
	 * @param openedFromBurgerMenu whether opened from the burger menu (selects initial tab)
	 */
	public void showAppSettings(boolean openedFromBurgerMenu) {
		replaceStack(List.of(new PropertiesPage(PropertyViewFactory.propertyViewOfAppSettings(
				app, app.appScope.propertiesRegistry, openedFromBurgerMenu))));
	}

	/**
	 * Shows the object settings for the given selection as the only page. Does nothing if the
	 * selection is empty.
	 * @param selection the selected elements
	 */
	public void showObjectSettings(@NonNull List<GeoElement> selection) {
		if (selection.isEmpty()) {
			return;
		}
		ObjectSettingsSession session = new ObjectSettingsSession(app, selection);
		replaceStack(List.of(new PropertiesPage(session)));
		// the color picker edits a property of the previous tree, so close it on replacement
		propertyTreeSubscription = session.getTree().subscribe(tree -> {
			if (navigationStack.get().size() > 1) {
				navigateBack();
			}
		});
	}

	/**
	 * Opens the color picker on top of the current page, replacing an open color picker.
	 * @param property the color property
	 */
	public void openColorPicker(@NonNull ColorProperty property) {
		List<SettingsPage> newStack = new ArrayList<>(navigationStack.get());
		if (!newStack.isEmpty() && newStack.get(newStack.size() - 1) instanceof ColorPickerPage top) {
			newStack.remove(newStack.size() - 1);
			top.dispose();
		}
		newStack.add(new ColorPickerPage(createColorPickerModel(property), !newStack.isEmpty()));
		navigationStack.set(newStack);
	}

	/**
	 * Opens the color picker for the background color of the selected spreadsheet cells as
	 * the only page.
	 */
	public void openSpreadsheetCellBackgroundColorPicker() {
		Spreadsheet<GeoElement> spreadsheet = app.getSpreadsheet();
		if (spreadsheet == null) {
			return;
		}
		ColorProperty property = spreadsheet.createCellBackgroundColorProperty(app.getLocalization());
		property.addValueObserver(new UndoSavingPropertyObserver(app.getUndoManager()));
		replaceStack(List.of(new ColorPickerPage(createColorPickerModel(property), false)));
	}

	/**
	 * Opens the color picker for the text color of the selected spreadsheet cells as
	 * the only page.
	 */
	public void openSpreadsheetCellTextColorPicker() {
		Spreadsheet<GeoElement> spreadsheet = app.getSpreadsheet();
		if (spreadsheet == null) {
			return;
		}
		ColorProperty property = spreadsheet.createCellTextColorProperty(app.getLocalization());
		property.addValueObserver(new UndoSavingPropertyObserver(app.getUndoManager()));
		replaceStack(List.of(new ColorPickerPage(createColorPickerModel(property), false)));
	}

	/** Leaves the top page; hides the sheet if it was the only page. */
	public void navigateBack() {
		List<SettingsPage> stack = navigationStack.get();
		if (stack.isEmpty()) {
			return;
		}
		if (stack.size() == 1) {
			close();
			return;
		}
		stack.get(stack.size() - 1).dispose();
		navigationStack.set(stack.subList(0, stack.size() - 1));
	}

	/** Hides the sheet, disposing all pages. */
	public void close() {
		replaceStack(Collections.emptyList());
	}

	private void replaceStack(List<SettingsPage> newStack) {
		if (propertyTreeSubscription != null) {
			propertyTreeSubscription.cancel();
			propertyTreeSubscription = null;
		}
		List<SettingsPage> old = navigationStack.get();
		for (int i = old.size() - 1; i >= 0; i--) {
			old.get(i).dispose();
		}
		navigationStack.set(newStack);
	}

	private @NonNull ColorPickerModel createColorPickerModel(@NonNull ColorProperty property) {
		return new ColorPickerModel(property, getColorHistory());
	}

	private @NonNull ColorHistory getColorHistory() {
		SuiteScope suiteScope = GlobalScope.getSuiteScope(app);
		assert suiteScope != null;
		return suiteScope.colorHistory;
	}
}
