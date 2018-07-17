package org.geogebra.keyboard.base.model;

import org.geogebra.keyboard.base.model.impl.factory.ButtonFactory;

/**
 * This class can create a {@link KeyboardModel}.
 */
public interface KeyboardModelFactory {

	KeyboardModel createKeyboardModel(ButtonFactory buttonFactory);
}
