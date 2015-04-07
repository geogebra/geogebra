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

/**
 * This enum defines the drop down list position of the
 * {@link org.gwt.advanced.client.ui.widget.ComboBox} widget.<p/>
 * The component uses it define how to display it. By default {@link #AUTO} is applied.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 2.0.2
 */
public enum DropDownPosition {
    /**
     * The list will be displayed above the box or under it according to many conditions detected
     * automatically. Usually it's shown under the text area and button if there is space enough.
     */
    AUTO,
    /** The list is always displayed above the box */
    ABOVE,
    /** The list is always displayed under the box */
    UNDER
}