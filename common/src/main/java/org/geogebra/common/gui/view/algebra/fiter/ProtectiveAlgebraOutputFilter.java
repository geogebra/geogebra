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
 * Filters the output on the Algebra View
 */
public class ProtectiveAlgebraOutputFilter implements AlgebraOutputFilter {

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
	 * Checks whether the geo element's output is allowed.
	 * @param geoElement geo element
	 * @return True if the geo element's output can be shown, otherwise false.
	 */
	@Override
	public boolean isAllowed(GeoElementND geoElement) {
		AlgoElement parentAlgorithm = geoElement.getParentAlgorithm();
		boolean isFunctionOrEquation =
				geoElement instanceof EquationValue || geoElement instanceof GeoFunction;
		return !isFunctionOrEquation
				|| parentAlgorithm == null
				|| getAllowedCommands().contains(parentAlgorithm.getClassName());
    }

    private Collection<GetCommand> getAllowedCommands() {
        if (allowedCommands == null) {
            allowedCommands = new HashSet<>();
            allowedCommands.add(Algos.Expression);
            allowedCommands.addAll(fitCommands);
        }
        return allowedCommands;
    }
}
