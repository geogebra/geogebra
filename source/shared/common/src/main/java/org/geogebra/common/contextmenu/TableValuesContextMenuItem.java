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

package org.geogebra.common.contextmenu;

import java.util.Arrays;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.geogebra.common.main.Localization;
import org.geogebra.common.util.AttributedString;

@SuppressWarnings("PMD.MissingStaticMethodInNonInstantiatableClass")
public final class TableValuesContextMenuItem implements ContextMenuItem {

	/**
	 * Table context menu item.
	 */
	public enum Item {
		Edit("Edit"),
		ClearColumn("ClearColumn"),
		RemoveColumn("RemoveColumn"),
		ShowPoints("ShowPoints"),
		HidePoints("HidePoints"),
		ImportData("ContextMenu.ImportData"),
		Regression("Regression"),
		Statistics1("AStatistics"),
		Statistics2("AStatistics"),
		Separator("");

		private final String translationKey;

		Item(String translationKey) {
			this.translationKey = translationKey;
		}

		/**
		 * Convert this to a menu item.
		 * @return this as menu item
		 */
		public TableValuesContextMenuItem toContextMenuItem() {
			return new TableValuesContextMenuItem(this, new String[]{});
		}

		/**
		 * Convert this to a menu item.
		 * @param translationPlaceholderValues translation placeholder values
		 * @return this as menu item
		 */
		public TableValuesContextMenuItem toContextMenuItem(String[] translationPlaceholderValues) {
			return new TableValuesContextMenuItem(this, translationPlaceholderValues);
		}

		/**
		 * Checks if the given {@link ContextMenuItem} is equals with this
		 * {@code TableValuesContextMenuItem} ignoring the
		 * {@link TableValuesContextMenuItem#translationPlaceholderValues}
		 *
		 * @param contextMenuItem The context menu item to compare to
		 * @return {@code true} if the given {@code ContextMenuItem} is a
		 * {@code TableValuesContextMenuItem} and their item/type are equal,
		 * {@code false} otherwise
		 */
		// TODO better name? Maybe matches/is/isItemOf/isTypeOf/isSameAs/isSameTypeAs/...
		public boolean isSameItemAs(ContextMenuItem contextMenuItem) {
			return contextMenuItem instanceof TableValuesContextMenuItem
					&& ((TableValuesContextMenuItem) contextMenuItem).getItem().equals(this);
		}
	}

	private final Item item;
	private final String[] translationPlaceholderValues;

	private TableValuesContextMenuItem(Item item, String[] translationPlaceholderValues) {
		this.item = item;
		this.translationPlaceholderValues = translationPlaceholderValues;
	}

	public @Nonnull Item getItem() {
		return item;
	}

	@Override
	public @Nonnull AttributedString getLocalizedTitle(@Nonnull Localization localization) {
		return MenuItemFormatting.parse(
				localization.getPlainArray(item.translationKey, null,
						translationPlaceholderValues)
		);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		TableValuesContextMenuItem that = (TableValuesContextMenuItem) object;
		return item == that.item && Arrays.equals(translationPlaceholderValues,
				that.translationPlaceholderValues);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(item);
		result = 31 * result + Arrays.hashCode(translationPlaceholderValues);
		return result;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(item.name());
		if (translationPlaceholderValues.length > 0) {
			stringBuilder.append(" ");
			stringBuilder.append(String.join(" ", translationPlaceholderValues));
		}
		return stringBuilder.toString();
	}
}
