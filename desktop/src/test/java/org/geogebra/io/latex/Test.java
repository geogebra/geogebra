/**
 * This file is part of the ReTeX library - https://github.com/himamis/ReTeX
 *
 * Copyright (C) 2015 Balazs Bencze
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
 */
package org.geogebra.io.latex;

import java.awt.Dimension;

import javax.swing.JFrame;

import org.geogebra.common.io.latex.ParseException;
import org.geogebra.common.io.latex.Parser;

import com.himamis.retex.editor.desktop.MathFieldD;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.renderer.desktop.FactoryProviderDesktop;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class Test {

	static {
		FactoryProvider.INSTANCE = new FactoryProviderDesktop();
	}

	public static void main(String[] args) {
		final MathFieldD mathField = new MathFieldD();

		JFrame frame = new JFrame();
		frame.setPreferredSize(new Dimension(200, 200));
		frame.getContentPane().add(mathField);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		Parser p = new Parser(mathField.getMetaModel());
		try {
			MathFormula f = p.parse("{{1,2,3},{4,5,6}}");
			mathField.setFormula(f);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//mathField.insertString("ggbmatrix(3,3)");
		// mathField.insertString("Midpoint(<Point>, <Point>)");

	}
}
