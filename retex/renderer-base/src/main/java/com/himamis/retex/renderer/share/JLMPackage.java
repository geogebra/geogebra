/* JLMPackage.java
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

package com.himamis.retex.renderer.share;

import java.util.HashMap;
import java.util.Map;

import com.himamis.retex.renderer.share.commands.Command;

public abstract class JLMPackage {

	private static Map<String, PackagedCommand> commands;
	private static Map<String, JLMPackage> packages;

	/**
	 * Init the package
	 */
	public abstract void init();

	/**
	 * @return the package name
	 */
	public abstract String getName();

	/**
	 * @return the map between command names and command
	 */
	public abstract Map<String, Command> getCommands();

	public static void addPackage(final JLMPackage pack) {
		if (packages == null) {
			packages = new HashMap<String, JLMPackage>();
		}
		packages.put(pack.getName(), pack);
	}

	public static void usePackage(final String name)
			throws JLMPackageException {
		if (packages == null) {
			throw new JLMPackageException("Error when loading package " + name
					+ ": it doesn't exist");
		}
		final JLMPackage pack = packages.get(name);
		if (pack == null) {
			throw new JLMPackageException("Error when loading package " + name
					+ ": it doesn't exist");
		}
		JLMPackage.register(pack);
	}

	public static void register(final JLMPackage pack)
			throws JLMPackageException {
		if (commands == null) {
			commands = new HashMap<String, PackagedCommand>();
		}
		pack.init();
		final Map<String, Command> map = pack.getCommands();
		if (map != null) {
			for (final Map.Entry<String, Command> entry : map.entrySet()) {
				final String com = entry.getKey();
				if (commands.containsKey(com)) {
					throw new JLMPackageException("Error when loading package "
							+ pack.getName() + ": command \\" + com
							+ " already exists in package "
							+ commands.get(com).getPackage().getName());
				}
				commands.put(com, new PackagedCommand(entry.getValue(), pack));
			}
		}
	}

	public static AtomConsumer get(final String name) {
		if (commands != null) {
			final PackagedCommand com = commands.get(name);
			if (com != null) {
				return com.duplicate();
			}
		}
		return null;
	}

	public static boolean exec(final TeXParser tp, final String name) {
		if (commands != null) {
			final PackagedCommand com = commands.get(name);
			if (com != null) {
				final AtomConsumer cons = com.duplicate();
				if (cons.init(tp)) {
					tp.addConsumer(cons);
				}
				tp.cancelPrevPos();
				return true;
			}
		}
		return false;
	}
}
