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

package org.geogebra.web.web.gui.advanced.client.ui.widget.combo;

import com.google.gwt.user.client.ui.Widget;

/**
 * This is a list item factory that helps to produce items in the
 * {@link org.gwt.advanced.client.ui.widget.ListPopupPanel}.
 * 
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.2.0
 */
public interface ListItemFactory {
    /**
     * This method creates a new widget that should be inserted into the list.
     *
     * @param value is a value to be used to construct the widget.
     * @return a widget instance (can be equal to <code>null</code>).
     */
    Widget createWidget(Object value);

    /**
     * This method should convert the value to the text to be displayed in the selection text box.
     *
     * @param value is a value to be converted.
     * @return textual representation of the value.
     */
    String convert(Object value);
}