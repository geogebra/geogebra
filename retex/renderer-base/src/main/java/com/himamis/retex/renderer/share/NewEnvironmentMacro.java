/* NewEnvironmentMacro.java
 * =========================================================================
 * This file is part of the JLaTeXMath Library - http://forge.scilab.org/jlatexmath
 *
 * Copyright (C) 2009-2018 DENIZET Calixte
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * Linking this library statically or dynamically with other modules
 * is making a combined work based on this library. Thus, the terms
 * and conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce
 * an executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under terms
 * of your choice, provided that you also meet, for each linked independent
 * module, the terms and conditions of the license of that module.
 * An independent module is a module which is not derived from or based
 * on this library. If you modify this library, you may extend this exception
 * to your version of the library, but you are not obliged to do so.
 * If you do not wish to do so, delete this exception statement from your
 * version.
 *
 */

package com.himamis.retex.renderer.share;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.himamis.retex.renderer.share.exception.ParseException;

public class NewEnvironmentMacro {

	private static final Map<String, Env> envs = new HashMap<String, Env>();

	public NewEnvironmentMacro() {
	}

	public static void clear() {
		envs.clear();
	}

	public static void addNewEnvironment(TeXParser tp, String name,
			String before, String after, int nbargs, boolean re)
			throws ParseException {
		if (envs.get(name) != null && !re) {
			throw new ParseException(tp, "Environment " + name
					+ " already exists ! Use renewenvironment instead ...");
		}
		envs.put(name, new Env(before, after, nbargs));
	}

	public static ArrayList<String> executeBeginEnv(final TeXParser tp,
			final String name) {
		final Env env = envs.get(name);
		if (env == null) {
			return null;
		}
		final int nargs = env.getNArgs();
		final ArrayList<String> args = tp.getArgsAsStrings(nargs);
		final String before = env.getBefore(tp, args);
		tp.addString(before);

		return args;
	}

	public static void executeEndEnv(final TeXParser tp, final String name,
			final ArrayList<String> args) {
		final Env env = envs.get(name);
		final String after = env.getAfter(tp, args);
		tp.addString(after,
				true /* to come back here after the string has been parsed */);
		tp.parse();
	}
}
