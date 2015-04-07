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

package org.geogebra.web.web.gui.advanced.client.ui.widget;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * This is a panel that appears when one of components like to lock the screen.
 *
 * @author <a href="mailto:sskladchikov@gmail.com">Sergey Skladchikov</a>
 * @since 1.0.0
 */
public class LockingPanel extends PopupPanel {
    /**
     * Creates an instance of this class.
     */
    public LockingPanel() {
        super(false, false);
    }

    /**
     * Shows the panel.
     */
    public void lock() {
        setStyleName("advanced-LockingPanel");
        setPopupPosition(0, 0);
        setWidth("100%");
        setHeight("100%");
        setPixelSize(Window.getClientWidth(), Window.getClientHeight());

        show();
    }

    /**
     * Hides the panel.
     */
    public void unlock() {
        hide();
    }
}