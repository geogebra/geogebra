package org.geogebra.common.contextmenu;

import java.util.Arrays;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.geogebra.common.main.Localization;

public final class TableValuesContextMenuItem implements ContextMenuItem {
	public enum Type {
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

		Type(String translationKey) {
			this.translationKey = translationKey;
		}

		public TableValuesContextMenuItem toContextMenuItem() {
			return new TableValuesContextMenuItem(this);
		}

		public TableValuesContextMenuItem toContextMenuItem(String[] translationPlaceholderValues) {
			return new TableValuesContextMenuItem(this, translationPlaceholderValues);
		}
	}

	public final Type type;
	private final String[] translationPlaceholderValues;

	private TableValuesContextMenuItem(Type type) {
		this.type = type;
		this.translationPlaceholderValues = new String[]{};
	}

	private TableValuesContextMenuItem(Type type, String[] translationPlaceholderValues) {
		this.type = type;
		this.translationPlaceholderValues = translationPlaceholderValues;
	}

	@Nonnull
	@Override
	public String getLocalizedTitle(@Nonnull Localization localization) {
		return localization.getPlainArray(type.translationKey, null, translationPlaceholderValues);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TableValuesContextMenuItem that = (TableValuesContextMenuItem) o;
		return type == that.type && Arrays.equals(translationPlaceholderValues,
				that.translationPlaceholderValues);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(type);
		result = 31 * result + Arrays.hashCode(translationPlaceholderValues);
		return result;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(type.name());
		if (translationPlaceholderValues.length > 0) {
			stringBuilder.append(" ");
			stringBuilder.append(String.join(" ", translationPlaceholderValues));
		}
		return stringBuilder.toString();
	}
}
