package org.geogebra.common.contextmenu;

import java.util.Arrays;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.geogebra.common.main.Localization;

public final class TableValuesContextMenuItem implements ContextMenuItem {
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

		public TableValuesContextMenuItem toContextMenuItem() {
			return new TableValuesContextMenuItem(this);
		}

		public TableValuesContextMenuItem toContextMenuItem(String[] translationPlaceholderValues) {
			return new TableValuesContextMenuItem(this, translationPlaceholderValues);
		}
	}

	public final Item item;
	private final String[] translationPlaceholderValues;

	private TableValuesContextMenuItem(Item item) {
		this.item = item;
		this.translationPlaceholderValues = new String[]{};
	}

	private TableValuesContextMenuItem(Item item, String[] translationPlaceholderValues) {
		this.item = item;
		this.translationPlaceholderValues = translationPlaceholderValues;
	}

	@Nonnull
	@Override
	public AttributedString getLocalizedTitle(@Nonnull Localization localization) {
		return AttributedString.parseString(
				localization.getPlainArray(item.translationKey, null,
						translationPlaceholderValues)
		);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TableValuesContextMenuItem that = (TableValuesContextMenuItem) o;
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
