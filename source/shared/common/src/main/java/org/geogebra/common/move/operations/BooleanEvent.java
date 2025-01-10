package org.geogebra.common.move.operations;

import org.geogebra.common.move.events.GenericEvent;
import org.geogebra.common.move.views.BooleanRenderable;

public class BooleanEvent implements GenericEvent<BooleanRenderable> {
	private final boolean value;

	public BooleanEvent(boolean value) {
		this.value = value;
	}

	@Override
	public void fire(BooleanRenderable view) {
		view.render(value);
	}
}
