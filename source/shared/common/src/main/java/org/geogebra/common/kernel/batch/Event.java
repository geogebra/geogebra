package org.geogebra.common.kernel.batch;

import java.util.Arrays;

class Event {

	private String name;
	private Object[] parameters;

	Event(String name, Object[] parameters) {
		this.name = name;
		this.parameters = parameters;
	}

	String getName() {
		return name;
	}

	Object[] getParameters() {
		return parameters;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Event event = (Event) o;

		if (!name.equals(event.name)) {
			return false;
		}
		return Arrays.equals(parameters, event.parameters);
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + Arrays.hashCode(parameters);
		return result;
	}
}
