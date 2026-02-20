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

package org.geogebra.common.gui.view.algebra;

import static org.geogebra.common.gui.view.algebra.AlgebraOutputOperator.APPROXIMATELY_EQUALS;
import static org.geogebra.common.gui.view.algebra.AlgebraOutputOperator.EQUALS;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.Fractions;
import org.geogebra.common.kernel.cas.AlgoSolve;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasSymbolicMode;
import org.geogebra.common.main.ScreenReaderSupport;
import org.geogebra.common.util.SymbolicUtil;

/**
 * Possible output formats for {@code GeoElement}s shown in the Algebra view.
 * These correspond to the different states of the (Algebra view) output format toggle button.
 * This class also contains utility methods to get the possible output formats for a {@code GeoElement},
 * and methods to cycle through them.
 */
public enum AlgebraOutputFormat implements ScreenReaderSupport {
    FRACTION("FormatFraction"),
	EXACT("FormatExact"),
	APPROXIMATION("FormatApproximate"),
	ENGINEERING("FormatEngineering");

	private final String screenReaderLabel;

	AlgebraOutputFormat(String screenReaderLabel) {
		this.screenReaderLabel = screenReaderLabel;
	}

	@Override
	public String getScreenReaderLabel() {
		return screenReaderLabel;
	}

	/**
	 * Retrieves all the possible output formats for the given {@code GeoElement}.
	 * @param geoElement the {@code GeoElement} for which to retrieve the possible formats
	 * @param enableEngineeringFormat whether the engineering notation should be included if possible
	 * @param algebraOutputFormatFilters filters to be applied
	 * @return the list of possible output formats
	 * @apiNote This method is public only for testing, for integration other methods should be sufficient.
	 */
	public static @Nonnull List<AlgebraOutputFormat> getPossibleFormats(@Nonnull GeoElement geoElement,
            boolean enableEngineeringFormat,
            @Nonnull Set<AlgebraOutputFormatFilter> algebraOutputFormatFilters) {
        ArrayList<AlgebraOutputFormat> possibleFormats = new ArrayList<>();

        boolean hasEngineeringFormat = geoElement instanceof HasSymbolicMode
                && ((HasSymbolicMode) geoElement).supportsEngineeringNotation();

        if (hasDifferentSymbolicAndNumericFormat(geoElement)) {
            possibleFormats.add(hasFractionalFormat(geoElement) ? FRACTION : EXACT);
            possibleFormats.add(APPROXIMATION);
        }
        if (hasEngineeringFormat && enableEngineeringFormat) {
            if (!hasDifferentSymbolicAndNumericFormat(geoElement)) {
                possibleFormats.add(EXACT);
            }
            possibleFormats.add(ENGINEERING);
        }

        return possibleFormats.stream().filter(format ->
                algebraOutputFormatFilters.stream().allMatch(filter ->
                        filter.isAllowed(geoElement, format)))
                .collect(Collectors.toList());
    }

	/**
	 * Retrieves the next output format from the sequence of possible formats for the given {@code GeoElement}.
	 * @param geoElement the {@code GeoElement} for which to retrieve the next format
	 * @param enableEngineeringFormat whether the engineering notation should be included if possible
	 * @param algebraOutputFormatFilters filters to be applied to the possible list of formats
	 * @return the next format in the sequence or {@code null} if switching between formats is not available.
	 * @apiNote This method can be used to decide whether an entry in the algebra view has a toggle button, and if so, which one to display.
	 */
	public static @CheckForNull AlgebraOutputFormat getNextFormat(@Nonnull GeoElement geoElement,
            boolean enableEngineeringFormat,
            @Nonnull Set<AlgebraOutputFormatFilter> algebraOutputFormatFilters) {
        AlgebraOutputFormat activeFormat = getActiveFormat(geoElement);
        List<AlgebraOutputFormat> possibleFormats =
                getPossibleFormats(geoElement, enableEngineeringFormat, algebraOutputFormatFilters);
		// If there are no possible formats, then switching should not be available.
        if (possibleFormats.isEmpty()) {
            return null;
        }
        // If the active format is the only possible format, switching should not be available.
        int currentIndex = possibleFormats.indexOf(activeFormat);
        if (possibleFormats.size() == 1 && currentIndex == 0) {
            return null;
        }
        // If the active format is disabled, then we return the first possible format.
        if (currentIndex == -1) {
            return possibleFormats.get(0);
        }
        // Select the next one from the list of possible formats.
        int nextIndex = (currentIndex + 1) % possibleFormats.size();
        return possibleFormats.get(nextIndex);
    }

    /**
     * Switches the output format of the given {@code GeoElement} to the next format in the sequence.
     * @param geoElement the {@code GeoElement} for which to switch the output format
     * @param enableEngineeringFormat whether the engineering notation should be included if possible
     * @param algebraOutputFormatFilters filters to be applied to the possible list of formats
     * @apiNote This method can be used directly as a toggle button action in the algebra view entry.
     */
    public static void switchToNextFormat(@Nonnull GeoElement geoElement,
            boolean enableEngineeringFormat,
            @Nonnull Set<AlgebraOutputFormatFilter> algebraOutputFormatFilters) {
        AlgebraOutputFormat activeFormat = getActiveFormat(geoElement);
        AlgebraOutputFormat nextFormat =
                getNextFormat(geoElement, enableEngineeringFormat, algebraOutputFormatFilters);
        if (nextFormat == null) {
            return;
        }
        if (isSymbolicFormat(activeFormat, geoElement)
                != isSymbolicFormat(nextFormat, geoElement)) {
            SymbolicUtil.toggleSymbolic(geoElement);
        }
        if (activeFormat == ENGINEERING || nextFormat == ENGINEERING) {
            SymbolicUtil.toggleEngineeringNotation(geoElement);
        }
    }

	/**
	 * Retrieves the operator to use for the given {@code GeoElement} in the output row of the algebra view entry.
	 * @param geoElement the {@code GeoElement} in the algebra view entry
	 * @return the symbol to display in the output row of the algebra view
	 */
	public static @Nonnull AlgebraOutputOperator getOutputOperator(@Nonnull GeoElement geoElement) {
        return (!AlgebraItem.isSymbolicDiffers(geoElement)
                || isCASOutputTypeSymbolic(geoElement)
                || Fractions.isExactFraction(geoElement.unwrapSymbolic(), geoElement.getKernel())
        ) ? EQUALS : APPROXIMATELY_EQUALS;
    }

	/**
	 * Retrieves the current output format for the given {@code GeoElement}.
	 * @param geoElement the {@code GeoElement} for which to retrieve the current format
	 * @return the current format of {@code GeoElement}
	 */
	public static @Nonnull AlgebraOutputFormat getActiveFormat(@Nonnull GeoElement geoElement) {
        if (SymbolicUtil.isEngineeringNotationMode(geoElement)) {
            return ENGINEERING;
        }

        if (hasDifferentSymbolicAndNumericFormat(geoElement)) {
            return isCASOutputTypeSymbolic(geoElement)
                    ? (hasFractionalFormat(geoElement) ? FRACTION : EXACT)
                    : APPROXIMATION;
        }

        if (!isCASOutputTypeSymbolic(geoElement) && hasFractionalFormat(geoElement)) {
            return APPROXIMATION;
        }

        return EXACT;
    }

    /**
     * Switches the output format of the given {@code GeoElement}
     * if the current format is disabled (filtered), to the first possible (unfiltered) format.
     * @param geoElement the {@code GeoElement} for which to switch the output format if necessary
     * @param enableEngineeringFormat whether the engineering notation should be included if possible
     * @param algebraOutputFormatFilters filters to be applied to the possible list of formats
     */
    public static void switchFromDisabledFormat(@Nonnull GeoElement geoElement,
            boolean enableEngineeringFormat,
            @Nonnull Set<AlgebraOutputFormatFilter> algebraOutputFormatFilters) {
        AlgebraOutputFormat currentFormat = getActiveFormat(geoElement);
        if (algebraOutputFormatFilters.stream().anyMatch(filter ->
                !filter.isAllowed(geoElement, currentFormat))) {
            switchToNextFormat(geoElement, enableEngineeringFormat, algebraOutputFormatFilters);
        }
    }

    private static boolean isSymbolicFormat(AlgebraOutputFormat format,
            GeoElement geoElement) {
        switch (format) {
        case APPROXIMATION:
        case ENGINEERING: return false;
        case EXACT: return hasDifferentSymbolicAndNumericFormat(geoElement);
        default: return true;
        }
    }

    private static boolean hasDifferentSymbolicAndNumericFormat(GeoElement geoElement) {
        return AlgebraItem.hasDefinitionAndValueMode(geoElement)
                && AlgebraItem.isSymbolicDiffers(geoElement);
    }

    private static boolean hasFractionalFormat(GeoElement geoElement) {
        return AlgebraItem.evaluatesToFraction(geoElement)
                && !AlgebraItem.isRationalizableFraction(geoElement);
    }

    private static boolean isCASOutputTypeSymbolic(GeoElement geoElement) {
        return geoElement.getParentAlgorithm() instanceof AlgoSolve
					? isSymbolicSolve(geoElement.getParentAlgorithm().getClassName())
					: isSymbolicGeneral(geoElement);
    }

	private static boolean isSymbolicGeneral(GeoElement geoElement) {
		return !(geoElement instanceof HasSymbolicMode)
				|| ((HasSymbolicMode) geoElement).isSymbolicMode();
	}

	private static boolean isSymbolicSolve(GetCommand className) {
		return className != Commands.NSolve && className != Commands.NSolutions;
	}
}
