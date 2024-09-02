package org.geogebra.common.kernel.parser.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;

import org.geogebra.common.plugin.Operation;

class FunctionReferences {

	private static final int MAX_ARGS = 4;

	private final List<Map<String, Operation>> functionMap = new ArrayList<>();
	private final Set<String> reservedFunctions = new HashSet<>();
	private final List<OperationSyntax> syntaxes = new ArrayList<>();

	FunctionReferences() {
		initFunctionMap();
	}

	private void initFunctionMap() {
		for (int i = 0; i <= MAX_ARGS; i++) {
			functionMap.add(new HashMap<String, Operation>());
		}
	}

	void put(int size, String name, Operation op, String arg) {
		reservedFunctions.add(name);
		if (arg != null) {
			syntaxes.add(new OperationSyntax(op, name + arg));
		}
		if (size <= MAX_ARGS && size >= 0) {
			functionMap.get(size).put(name, op);
		}
	}

	void putReserved(String name) {
		reservedFunctions.add(name);
	}

	Operation get(String name, int size) {
		if (size > MAX_ARGS || size < 0) {
			return null;
		}
		return functionMap.get(size).get(name);
	}

	boolean isReserved(String s) {
		return reservedFunctions.contains(s);
	}

	void getCompletions(String prefix, Set<String> completions,
						@CheckForNull Set<Operation> filteredOperations) {
		for (OperationSyntax operationSyntax : syntaxes) {
			if (filteredOperations != null && filteredOperations.contains(operationSyntax.operation)) {
				continue;
			}
			if (operationSyntax.syntax.startsWith(prefix)) {
				completions.add(operationSyntax.syntax);
			}
		}
	}

	private static class OperationSyntax {
		final Operation operation;
		final String syntax;

        private OperationSyntax(Operation operation, String syntax) {
            this.operation = operation;
            this.syntax = syntax;
        }
    }
}
