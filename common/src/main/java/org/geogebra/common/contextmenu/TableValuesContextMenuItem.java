package org.geogebra.common.contextmenu;

import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.apache.commons.math3.util.Pair;
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
	public AttributedString getLocalizedTitle(@Nonnull Localization localization) {
		return createSubscriptAttributedString(
				localization.getPlainArray(type.translationKey, null,
						translationPlaceholderValues)
		);
	}

	private static AttributedString createSubscriptAttributedString(String text) {
		StringBuilder stringBuilder = new StringBuilder(text);
		List<Pair<Integer, Integer>> subscriptRanges = new ArrayList<>();

		// Special character sequence representing a subscript
		Matcher matcher = Pattern.compile("_\\{(.+)\\}").matcher(text);

		int offset = 0;
		while (matcher.find()) {
			// Start and end index of special subsequence relative to the final string
			int start = matcher.start() - offset;
			int end = matcher.end() - offset;

			// Value contained in the special subsequence
			String subscript = matcher.group(1);

			// Replace special character sequence with the value it contained
			stringBuilder.replace(start, end, subscript);

			// Save the range of the special subscript value
			subscriptRanges.add(Pair.create(start, start + subscript.length()));

			// Update the offset according to the change in the string length
			offset += end - start - subscript.length() + 1;
		}

		AttributedString attributedString = new AttributedString(stringBuilder.toString());
		for (Pair<Integer, Integer> subscriptRange : subscriptRanges) {
			attributedString.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB,
					subscriptRange.getFirst(), subscriptRange.getSecond());
		}

		return attributedString;
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
