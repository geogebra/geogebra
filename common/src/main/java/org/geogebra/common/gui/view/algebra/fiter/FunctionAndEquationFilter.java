package org.geogebra.common.gui.view.algebra.fiter;

import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoElementND;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Filters the value of functions and equations that are not safe to show to the user.
 */
public class FunctionAndEquationFilter {

    private List<Commands> fitCommands =
            Arrays.asList(
                    Commands.FitExp,
                    Commands.Fit,
                    Commands.FitGrowth,
                    Commands.FitImplicit,
                    Commands.FitLine,
                    Commands.FitLineX,
                    Commands.FitLineY,
                    Commands.FitLog,
                    Commands.FitLogistic,
                    Commands.FitPoly,
                    Commands.FitPow,
                    Commands.FitSin);

    private Set<GetCommand> allowedCommands;

    /**
     * Recursively checks the algo parents to determine whether the element's value is safe to show
     * to the user.
     * @param element The GeoElementND for which we check whether it's allowed to show its value
     * @return True if it's allowed to show the element's value, otherwise false.
     */
    public boolean isAllowed(GeoElementND element) {
        if (!isFunctionOrEquation(element)) {
            return true;
        }
        return isAllowedFunctionOrEquation(element);
    }

    private static boolean isFunctionOrEquation(GeoElementND element) {
        return element instanceof EquationValue || element instanceof GeoFunction;
    }

    private boolean isAllowedFunctionOrEquation(GeoElementND element) {
        boolean hasEquation = element instanceof EquationValue;
        return hasEquation
                && isParentAlgorithmAllowedFor(element)
                && areParentAlgoInputsAllowedFor(element);
    }

    private boolean areParentAlgoInputsAllowedFor(GeoElementND element) {
        AlgoElement algoElement = element.getParentAlgorithm();

        if (algoElement != null) {
            GeoElementND[] inputGeos = algoElement.getInput();
            if (inputGeos != null) {
                for (GeoElementND inputGeo : inputGeos) {
                    boolean isAllowed =
                            isParentAlgorithmAllowedFor(element)
                                    && areParentAlgoInputsAllowedFor(inputGeo);
                    if (!isAllowed) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean isParentAlgorithmAllowedFor(GeoElementND geoElement) {
        AlgoElement parentAlgorithm = geoElement.getParentAlgorithm();
        return parentAlgorithm == null
                || getAllowedCommands().contains(parentAlgorithm.getClassName());
    }

    private Collection<GetCommand> getAllowedCommands() {
        if (allowedCommands == null) {
            allowedCommands = new HashSet<>();
            allowedCommands.add(Algos.Expression);
            allowedCommands.add(Commands.Point);
            allowedCommands.addAll(fitCommands);
        }
        return allowedCommands;
    }
}
