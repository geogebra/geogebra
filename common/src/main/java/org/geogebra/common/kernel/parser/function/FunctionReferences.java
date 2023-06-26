package org.geogebra.common.kernel.parser.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.geogebra.common.plugin.Operation;

class FunctionReferences {

	private static final int MAX_ARGS = 4;

	private final List<Map<String, Operation>> functionMap = new ArrayList<>();
	private final Set<String> reservedFunctions = new HashSet<>();
	private final TreeSet<String> syntaxes = new TreeSet<>();

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
			syntaxes.add(name + arg);
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

	void getCompletions(String prefix, Set<String> completions) {
		for (String candidate : syntaxes.tailSet(prefix)) {
			if (!candidate.startsWith(prefix)) {
				break;
			}
			completions.add(candidate);
		}
	}
}
