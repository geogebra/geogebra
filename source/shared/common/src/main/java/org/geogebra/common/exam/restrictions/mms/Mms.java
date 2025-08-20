package org.geogebra.common.exam.restrictions.mms;

import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.BarChartGeoNumeric;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.kernelND.GeoElementND;

final class Mms {
    static List<Commands> FUNCTION_COMMANDS = List.of(Commands.Integral,
            Commands.IntegralSymbolic, Commands.Derivative,
            Commands.Expand, Commands.LeftSide, Commands.RightSide);

    @SuppressWarnings("PMD.SimplifyBooleanReturns")
    static boolean isOutputAllowed(@CheckForNull GeoElementND element) {
        if (element == null) {
            return false;
        }
        GeoElementND unwrapped = element.unwrapSymbolic();
        if (unwrapped instanceof BarChartGeoNumeric) {
            return false;
        }
        if (unwrapped instanceof FunctionalNVar) {
            return element instanceof GeoSymbolic
                    && isFunctionProducingCommand(element.getDefinition());
        }
        return true;
    }

    private static boolean isFunctionProducingCommand(ExpressionNode definition) {
        ExpressionValue def = definition.unwrap();
        return def instanceof Command && FUNCTION_COMMANDS.stream()
                .anyMatch(cmd -> cmd.name().equals(((Command) def).getName()));
    }
}
