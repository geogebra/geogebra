/*
 * Copyright 2007 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.geogebra.web.html5.gui.util;

import com.google.gwt.user.client.ui.Image;

/**
 * A normal push button with custom styling.
 * 
 * <p>
 * <img class='gallery' src='doc-files/PushButton.png'/>
 * </p>
 * 
 * <h3>CSS Style Rules</h3>
 * <ul class="css">
 * <li>.gwt-PushButton-up/down/up-hovering/down-hovering/up-disabled/down-
 * disabled {.html-face}</li>
 * </ul>
 * 
 * <p>
 * <h3>Example</h3> {@example com.google.gwt.examples.PushButtonExample}
 * </p>
 */
public class GPushButton extends GCustomButton {

	private static final String STYLENAME_DEFAULT = "gwt-PushButton";

	{
		setStyleName(STYLENAME_DEFAULT);
	}

	/**
	 * Constructor for <code>PushButton</code>.
	 * 
	 * @param upImage
	 *            image for the default(up) face of the button
	 */
	public GPushButton(Image upImage) {
		super(upImage);
	}

	@Override
	protected void onClick() {
		setDown(false);
		super.onClick();
	}

	@Override
	protected void onClickCancel() {
		setDown(false);
	}

	@Override
	protected void onClickStart() {
		setDown(true);
	}
}
