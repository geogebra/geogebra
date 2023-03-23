/*
 * Copyright 2010 Google Inc.
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

package org.geogebra.web.full.gui.view.consprotocol;

import org.gwtproject.cell.client.AbstractCell;
import org.gwtproject.safehtml.shared.SafeHtmlBuilder;
import org.gwtproject.text.shared.SafeHtmlRenderer;
import org.gwtproject.text.shared.SimpleSafeHtmlRenderer;

/**
 * Based on NumberCell. Make sure not to use GWT's NumberFormat to avoid
 * duplicate code.
 */
public class IntegerCell extends AbstractCell<Integer> {

	/**
	 * The {@link SafeHtmlRenderer} used to render the formatted number as HTML.
	 */
	private final SafeHtmlRenderer<String> renderer;

	/**
	 * Construct a new {@link IntegerCell} using default
	 * {@link SimpleSafeHtmlRenderer}.
	 */
	public IntegerCell() {
		this.renderer = SimpleSafeHtmlRenderer.getInstance();
	}

	@Override
	public void render(Context context, Integer value, SafeHtmlBuilder sb) {
		if (value != null) {
			sb.append(renderer.render(String.valueOf(value)));
		}
	}
}
