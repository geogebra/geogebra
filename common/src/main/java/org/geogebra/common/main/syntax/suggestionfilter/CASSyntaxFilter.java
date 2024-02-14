package org.geogebra.common.main.syntax.suggestionfilter;

import org.geogebra.common.kernel.commands.Commands;

public class CASSyntaxFilter implements SyntaxFilter {
    private LineSelector lineSelector = new LineSelector();

    @Override
    public String getFilteredSyntax(String commandName, String syntax) {
        String[] syntaxArray = syntax.split("\n");
        if (Commands.Distance.name().equals(commandName)) {
            return lineSelector.select(syntaxArray, 0, 1);
        }
        return syntax;
    }
}
