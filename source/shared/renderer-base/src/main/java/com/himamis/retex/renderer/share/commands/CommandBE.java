/* CommandBE.java
 * =========================================================================
 * This file is part of the JLaTeXMath Library - http://forge.scilab.org/jlatexmath
 *
 * Copyright (C) 2018 DENIZET Calixte
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

package com.himamis.retex.renderer.share.commands;

import java.util.ArrayList;

import com.himamis.retex.renderer.share.Commands;
import com.himamis.retex.renderer.share.Env;
import com.himamis.retex.renderer.share.NewEnvironmentMacro;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.exception.ParseException;

public class CommandBE {

	public static class Begin extends Command {

		@Override
		public boolean init(TeXParser tp) {
			final String name = tp.getArgAsString();
			final Command com = (Command) Commands.get("begin@" + name);
			if (com == null) {
				final ArrayList<String> args = NewEnvironmentMacro
						.executeBeginEnv(tp, name);
				if (args != null) {
					final Env.Begin beg = new Env.Begin(name, args);
					tp.addConsumer(beg);
					return false;
				}
				throw new ParseException(tp,
						"Environment " + name + " doesn't exist");
			}
			if (com.init(tp)) {
				tp.addConsumer(com);
			}

			return false;
		}

	}

	public static class End extends Command {

		@Override
		public boolean init(TeXParser tp) {
			tp.close();
			final String name = tp.getArgAsString();
			final Command com = (Command) Commands.get("end@" + name);
			if (com == null) {
				// don't pop here since we must add the end stuff
				final Env.Begin beg = tp.getBegin();
				if (beg != null) {
					if (name.equals(beg.getName())) {
						NewEnvironmentMacro.executeEndEnv(tp, name,
								beg.getArgs());
						tp.closeConsumer(beg.getBase());
						return false;
					}
					tp.pop();
					throw new ParseException(tp,
							"Mismatching environments: \\begin{" + beg.getName()
									+ "} and \\end{" + name + "}");
				}

				throw new ParseException(tp,
						"No matching \\begin{" + name + "}");
			}
			com.init(tp);
			return false;
		}

	}
}
