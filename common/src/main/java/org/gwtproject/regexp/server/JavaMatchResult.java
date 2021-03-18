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

package org.gwtproject.regexp.server;

import java.util.ArrayList;
import java.util.List;

import org.gwtproject.regexp.shared.MatchResult;

/** Pure Java implementation of a regular expression match result. */
public final class JavaMatchResult implements MatchResult {

	private final List<String> groups;
	private final int index;
	private final String input;

	JavaMatchResult(int index, String input, List<String> groups) {
		this.index = index;
		this.input = input;
		this.groups = new ArrayList<>(groups);
	}

	@Override
	public String getGroup(int index) {
		return groups.get(index);
	}

	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public String getInput() {
		return input;
	}
}