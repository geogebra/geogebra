/*
 * Copyright © 2019 The GWT Project Authors
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
package org.gwtproject.regexp.client;

import org.gwtproject.regexp.shared.MatchResult;

import elemental2.core.JsArray;

public class NativeMatchResult implements MatchResult {

	private JsArray<String> array;

	NativeMatchResult(JsArray<String> results) {
		array = results;
	}

	@Override
	public String getGroup(int index) {
		return array.getAt(index);
	}

	@Override
	public int getGroupCount() {
		return array.length;
	}

	@Override
	public int getIndex() {
		return array.index;
	}

	@Override
	public String getInput() {
		return array.input;
	}
}