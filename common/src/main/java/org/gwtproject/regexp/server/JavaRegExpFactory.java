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

import org.gwtproject.regexp.shared.RegExp;
import org.gwtproject.regexp.shared.RegExpFactory;

/** RegExp factory to create Java implementation */
public class JavaRegExpFactory extends RegExpFactory {

	@Override
	public RegExp compile(String pattern, String flags) {
		return JavaRegExp.compile(pattern, flags);
	}

	@Override
	public RegExp compile(String pattern) {
		return JavaRegExp.compile(pattern);
	}

	@Override
	public String quote(String input) {
		return JavaRegExp.quote(input);
	}
}
