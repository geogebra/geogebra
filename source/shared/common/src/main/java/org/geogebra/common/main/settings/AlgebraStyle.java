package org.geogebra.common.main.settings;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.geogebra.common.main.App;
import org.geogebra.common.main.PreviewFeature;

/**
 * This enum defines the algebra style options.
 */
public enum AlgebraStyle {

	UNDEFINED(-1, ""),
	VALUE(0, "Value"),
	DESCRIPTION(1, "Description"),
	DEFINITION(2, "Definition"),
	DEFINITION_AND_VALUE(3, "DefinitionAndValue"),
	LINEAR_NOTATION(4, "LinearNotation");

	private final int numericValue;
	private final @Nonnull String translationKey;

	/**
	 * Constructor
	 * @param numericValue The numeric value associated with this Algebra Style.
	 * @param translationKey The key used for translating this Algebra Style.
	 */
	AlgebraStyle(int numericValue, @Nonnull String translationKey) {
		this.numericValue = numericValue;
		this.translationKey = translationKey;
	}

	/**
	 * @return The numeric value associated with this Algebra Style.
	 */
	public int getNumericValue() {
		return numericValue;
	}

	/**
	 * @return The translation key associated with this Algebra Style.
	 */
	public @Nonnull String getTranslationKey() {
		return translationKey;
	}

	/**
	 * @param app Application
	 * @return The next Algebra Style that is currently available.
	 */
	public @Nonnull AlgebraStyle getNextAvailableStyle(@Nonnull App app) {
		List<AlgebraStyle> availableStyles = getAvailableValues(app);
		int currentStyle = availableStyles.indexOf(this);
		return availableStyles.get((currentStyle + 1) % availableStyles.size());
	}

	/**
	 * @param numericValue The numeric value associated with the Algebra Style.
	 * @return The enum constant that is associated with the passed numeric value or
	 * {@link AlgebraStyle#UNDEFINED} if the passed numeric value is invalid.
	 */
	public static @Nonnull AlgebraStyle fromNumericValue(int numericValue) {
		return Arrays.stream(values())
				.filter(style -> style.getNumericValue() == numericValue)
				.findAny()
				.orElse(UNDEFINED);
	}

	/**
	 * @param app App
	 * @return A list of available values that can be used within the current app.
	 */
	public static @Nonnull List<AlgebraStyle> getAvailableValues(@Nonnull App app) {
		Comparator<AlgebraStyle> definitionAndValueFirst = Comparator.comparing(
				style -> style != DEFINITION_AND_VALUE);
		return Arrays.stream(values())
				.filter(style -> style.isAvailable(app))
				.sorted(definitionAndValueFirst.thenComparing(AlgebraStyle::getNumericValue))
				.collect(Collectors.toList());
	}

	private boolean isAvailable(App app) {
		switch (this) {
		case UNDEFINED: return false;
		case DEFINITION_AND_VALUE:
			return !app.isDesktop();
		case LINEAR_NOTATION:
			return app.isHTML5Applet()
					&& PreviewFeature.isAvailable(PreviewFeature.LINEAR_NOTATION);
		default: return true;
		}
	}
}