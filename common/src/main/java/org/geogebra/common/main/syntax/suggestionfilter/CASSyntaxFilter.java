package org.geogebra.common.main.syntax.suggestionfilter;

import org.geogebra.common.kernel.commands.Commands;

public class CASSyntaxFilter implements SyntaxFilter {

    @Override
    public String getFilteredSyntax(String internalCommandName, String syntax) {
        if (Commands.Distance.name().equals(internalCommandName)) {
            return LineSelector.select(syntax, 0, 1);
        }
        return syntax;
    }
}
