package org.geogebra.common.main.syntax;

public interface CommandSyntax {

	String getCommandSyntax(String key, int dim);

	String getCommandSyntaxCAS(String key);
}
