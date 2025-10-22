package org.geogebra.keyboard.base.model;

import org.geogebra.keyboard.base.model.impl.factory.ButtonFactory;

/**
 * This class can create a {@link KeyboardModel}.
 */
@FunctionalInterface
public interface KeyboardModelFactory {

	/**
	 * Creates model for one keyboard tab.
	 * @param buttonFactory button factory
	 * @return keyboard model
	 */
	KeyboardModel createKeyboardModel(ButtonFactory buttonFactory);
}
