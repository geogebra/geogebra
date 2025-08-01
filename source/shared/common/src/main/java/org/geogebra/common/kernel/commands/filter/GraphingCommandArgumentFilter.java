package org.geogebra.common.kernel.commands.filter;

import static org.geogebra.common.kernel.commands.Commands.Function;
import static org.geogebra.common.kernel.commands.Commands.Length;
import static org.geogebra.common.kernel.commands.Commands.Line;

import java.util.Map;
import java.util.Set;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.syntax.Syntax;

public final class GraphingCommandArgumentFilter implements CommandArgumentFilter {
    private final Map<Commands, Set<Syntax>> allowedSyntaxesForRestrictedCommands = Map.of(
            Line, Set.of(
                Syntax.of(Line, GeoElement::isGeoPoint,
                        GraphingCommandArgumentFilter::isNotALineOrFunction)),
            Length, Set.of(
                Syntax.of(Length, GeoElement::isGeoText),
                Syntax.of(Length, GeoElement::isGeoList)),
            Function, Set.of(
                Syntax.of(Function, GeoElement::isGeoList)));

    private static boolean isNotALineOrFunction(GeoElement element) {
        return !element.isGeoLine() && !element.isGeoFunction();
    }

    @Override
    public void checkAllowed(Command command, CommandProcessor commandProcessor) {
        Syntax.checkRestrictedSyntaxes(
                allowedSyntaxesForRestrictedCommands, command, commandProcessor);
    }
}
