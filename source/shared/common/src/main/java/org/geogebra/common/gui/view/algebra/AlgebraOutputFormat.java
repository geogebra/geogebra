package org.geogebra.common.gui.view.algebra;

import static org.geogebra.common.gui.view.algebra.AlgebraOutputOperator.APPROXIMATELY_EQUALS;
import static org.geogebra.common.gui.view.algebra.AlgebraOutputOperator.EQUALS;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.geogebra.common.kernel.arithmetic.Fractions;
import org.geogebra.common.kernel.cas.AlgoSolve;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasSymbolicMode;
import org.geogebra.common.util.SymbolicUtil;

/**
 * Possible output formats for {@code GeoElement}s shown in the Algebra view.
 * These correspond to the different states of the (Algebra view) output format toggle button.
 * This class also contains utility methods to get the possible output formats for a {@code GeoElement},
 * and methods to cycle through them.
 */
public enum AlgebraOutputFormat {
    FRACTION, EXACT, APPROXIMATION, ENGINEERING;

    /**
     * Retrieves all the possible output formats for the given {code GeoElement}.
     * @param geoElement the {@code GeoElement} for which to retrieve the possible formats
     * @param enableEngineeringFormat whether the engineering notation should be included if possible
     * @return the list of possible output formats
     * @apiNote This method is public only for testing, for integration other methods should be sufficient.
     */
    @Nonnull
    public static List<AlgebraOutputFormat> getPossibleFormats(@Nonnull GeoElement geoElement,
            boolean enableEngineeringFormat) {
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

        return possibleFormats;
    }

    /**
     * Retrieves the next output format from the sequence of possible formats for the given {@code GeoElement}.
     * @param geoElement the {@code GeoElement} for which to retrieve the next format
     * @param enableEngineeringFormat whether the engineering notation should be included if possible
     * @return the next format in the sequence or {@code null} if switching between formats is not available.
     * @apiNote This method can be used to decide whether an entry in the algebra view has a toggle button, and if so, which one to display.
     */
    @Nullable
    public static AlgebraOutputFormat getNextFormat(@Nonnull GeoElement geoElement,
            boolean enableEngineeringFormat) {
        AlgebraOutputFormat activeFormat = getActiveFormat(geoElement);
        List<AlgebraOutputFormat> possibleFormats =
                getPossibleFormats(geoElement, enableEngineeringFormat);
        int currentIndex = possibleFormats.indexOf(activeFormat);
        if (currentIndex == -1) {
            return null;
        }
        int nextIndex = (currentIndex + 1) % possibleFormats.size();
        return possibleFormats.get(nextIndex);
    }

    /**
     * Switches the output format of the given {@code GeoElement} to the next format in the sequence.
     * @param geoElement the {@code GeoElement} for which to switch the output format
     * @param enableEngineeringFormat whether the engineering notation should be included if possible
     * @apiNote This method can be used directly as a toggle button action in the algebra view entry.
     */
    public static void switchToNextFormat(@Nonnull GeoElement geoElement,
            boolean enableEngineeringFormat) {
        AlgebraOutputFormat activeFormat = getActiveFormat(geoElement);
        AlgebraOutputFormat nextFormat = getNextFormat(geoElement, enableEngineeringFormat);
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
    @Nonnull
    public static AlgebraOutputOperator getOutputOperator(@Nonnull GeoElement geoElement) {
        return (!AlgebraItem.isSymbolicDiffers(geoElement)
                || isCASOutputTypeSymbolic(geoElement)
                || Fractions.isExactFraction(geoElement.unwrapSymbolic(), geoElement.getKernel())
        ) ? EQUALS : APPROXIMATELY_EQUALS;
    }

    private static AlgebraOutputFormat getActiveFormat(GeoElement geoElement) {
        if (SymbolicUtil.isEngineeringNotationMode(geoElement)) {
            return ENGINEERING;
        }

        if (hasDifferentSymbolicAndNumericFormat(geoElement)) {
            return SymbolicUtil.isSymbolicMode(geoElement)
                    ? (AlgebraItem.evaluatesToFraction(geoElement) ? FRACTION : EXACT)
                    : APPROXIMATION;
        }

        if (!isCASOutputTypeSymbolic(geoElement) && hasFractionalFormat(geoElement)) {
            return APPROXIMATION;
        }

        return EXACT;
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
        return !(geoElement instanceof HasSymbolicMode)
                || ((HasSymbolicMode) geoElement).isSymbolicMode()
                || (geoElement.getParentAlgorithm() instanceof AlgoSolve
                && geoElement.getParentAlgorithm().getClassName() != Commands.NSolve);
    }
}
