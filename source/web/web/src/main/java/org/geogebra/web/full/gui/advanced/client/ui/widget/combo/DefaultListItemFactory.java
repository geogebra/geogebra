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

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.gwtproject.user.client.ui.HTML;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

/**
 * This factory tries to detect what should be returned by value type.
 * <p>
 * If the value is instance of <code>String</code>, <code>Number</code> or
 * <code>Date</code> it returns this value wrapped in appropriate widget.
 * Returns <code>null</code> otherwise.
 * <p>
 * If you want to use more complex objects you should develop your own factory.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.2.0
 */
public class DefaultListItemFactory implements ListItemFactory {
	/**
	 * See class docs.
	 *
	 * @param value
	 *            is a value to be adopted.
	 * @return a widget to be inserted into the list.
	 */
	@Override
	public Widget createWidget(Object value) {
		if (value == null) {
			return new Label();
		} else if (value instanceof String || value instanceof Number) {
			return new Label(String.valueOf(value));
		} else if (value instanceof GeoElement) {
			return new HTML(((GeoElement) value).getColoredLabel());
		} else {
			return null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public String convert(Object value) {
		if (value == null) {
			return "";
		} else if (value instanceof String || value instanceof Number) {
			return String.valueOf(value);
		} else if (value instanceof GeoElement) {
			return ((GeoElement) value).getLabel(StringTemplate.editTemplate);
		}
		/*
		 * else if (value instanceof Date) return new
		 * DatePicker((Date)value).getTextualDate(); else if (value instanceof
		 * IconItem) return ((IconItem)value).getLabel(); else
		 */
		return "";
	}
}