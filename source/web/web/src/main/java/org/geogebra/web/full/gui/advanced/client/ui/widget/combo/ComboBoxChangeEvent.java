/*
 * Copyright 2008-2013 Sergey Skladchikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.geogebra.web.full.gui.advanced.client.ui.widget.combo;

import org.gwtproject.event.dom.client.ChangeEvent;

/**
 * This event extends the standard <code>ChangeEvent</code> class to provide
 * more information about how the event was produced.
 * <p>
 * It's fired by the
 * {@link org.geogebra.web.full.gui.advanced.client.ui.widget.ComboBox} and
 * subclasses.
 * 
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 2.0.1
 */
public class ComboBoxChangeEvent extends ChangeEvent {
	/** selected row number */
	private int row;
	/** an input device originally initiated the event */
	private ChangeEventInputDevice inputDevice;

	/**
	 * Creates an instance of this class and initialized internal variables.
	 *
	 * @param row
	 *            is a row number.
	 * @param inputDevice
	 *            is an original input device initiated the event.
	 */
	public ComboBoxChangeEvent(int row, ChangeEventInputDevice inputDevice) {
		this.row = row;
		this.inputDevice = inputDevice;
	}

	/**
	 * @return row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * @return keyboard or mouse
	 */
	public ChangeEventInputDevice getInputDevice() {
		return inputDevice;
	}

	/**
	 * This enum describes possible devices that can initiate the event.
	 */
	public enum ChangeEventInputDevice {
		/** keyboard device (or virtual keyboard) */
		KEYBOARD,
		/** mouse device (or touch screen) */
		MOUSE
	}
}