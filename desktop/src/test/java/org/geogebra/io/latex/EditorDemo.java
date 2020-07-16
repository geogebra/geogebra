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

import com.himamis.retex.editor.desktop.MathFieldD;
import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.io.latex.ParseException;
import com.himamis.retex.editor.share.io.latex.Parser;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.serializer.GeoGebraSerializer;
import com.himamis.retex.editor.share.serializer.TeXSerializer;
import com.himamis.retex.editor.share.util.JavaKeyCodes;
import com.himamis.retex.renderer.desktop.FactoryProviderDesktop;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class EditorDemo {

	static {
		FactoryProvider.setInstance(new FactoryProviderDesktop());
	}

	/**
	 * @param args
	 *            command line args
	 */
	public static void main(String[] args) {
		final MathFieldD mathField = new MathFieldD();
		mathField.setFieldListener(new MathFieldListener() {

			@Override
			public void onEnter() {
				System.out.println(mathField.getFormula().getRootComponent());
				System.out.println(GeoGebraSerializer
						.serialize(mathField.getFormula().getRootComponent()));
				System.out.println(TeXSerializer
						.serialize(mathField.getFormula().getRootComponent()));
				System.out.println(mathField.getCurrentWord());
			}

			@Override
			public void onKeyTyped() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onCursorMove() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDownKeyPressed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onUpKeyPressed() {
				// TODO Auto-generated method stub

			}

			@Override
			public String serialize(MathSequence selectionText) {
				return GeoGebraSerializer.serialize(selectionText);
			}

			@Override
			public void onInsertString() {
				// nothing to do
			}

			@Override
			public boolean onEscape() {
				return false;
			}

			@Override
			public void onTab(boolean shiftDown) {
				// nothing to do
			}
		});
		JFrame frame = new JFrame();
		frame.setPreferredSize(new Dimension(200, 200));
		frame.getContentPane().add(mathField);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		Parser p = new Parser(mathField.getMetaModel());
		try {
			MathFormula f = p.parse("f'(x)/2");
			mathField.setFormula(f);
			// insertString(mathField, "Plane[<7>,<7>]");
			for (int i = 0; i < 1; i++) {
				mathField.getInternal()
						.onKeyPressed(new KeyEvent(JavaKeyCodes.VK_LEFT, 0));
			}
			mathField.getInternal().update();
			/*
			 * mathField.getInternal() .onKeyPressed(new
			 * KeyEvent(KeyEvent.VK_LEFT, 0));
			 * mathField.getInternal().getInputController().removeCharacters(
			 * mathField.getInternal().getEditorState(), 1, 0);
			 * mathField.getInternal().update();
			 * System.out.println(mathField.getCurrentWord());
			 */
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// mathField.insertString("ggbmatrix(3,3)");
		// mathField.insertString("Midpoint(<Point>, <Point>)");

	}

}
