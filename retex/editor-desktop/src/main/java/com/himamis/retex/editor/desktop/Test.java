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
package com.himamis.retex.editor.desktop;

import java.awt.Dimension;
import java.text.Normalizer;

import javax.swing.JFrame;

import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.model.Korean;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.renderer.desktop.FactoryProviderDesktop;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class Test  {

	static {
		FactoryProvider.setInstance(new FactoryProviderDesktop());
	}

	public static void main(String[] args) {
		final MathFieldD mathField = new MathFieldD();
		
		MathFieldInternal mathFieldInternal = mathField.getInternal();
		//InputController inputController = mathFieldInternal
		//		.getInputController();
		//EditorState editorState = mathFieldInternal.getEditorState();
		
		mathFieldInternal.setFieldListener(new MathFieldListener() {

			@Override
			public void onEnter() {
				// TODO Auto-generated method stub
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
				return selectionText + "";
			}

			@Override
			public void onInsertString() {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onEscape() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onTab(boolean shiftDown) {
				// TODO Auto-generated method stub
				
			}
		});

		JFrame frame = new JFrame();
		frame.setPreferredSize(new Dimension(200, 200));
		frame.getContentPane().add(mathField);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		// mathField.insertString("ggbmatrix(3,3)");

		// these two should both end up inserting a single char \uB458
		//mathField.insertString("\u3137\u315c\u3139 ");
		//mathField.insertString("\u1103\u116e\u11af ");
		// sector command (short format)
		//mathField.insertString("\ubd80\ucc44\uaf34 ");
		// sector command (long format)
		//mathField.insertString("\u1107\u116E\u110E\u1162\u1101\u1169\u11AF ");

		// sector command (compatibility format)
		//mathField.insertString(
		//		" compat \u3142\u315c\u314a\u3150\u3132\u3157\u3139 ");

		// from
		// https://stackoverflow.com/questions/40941528/get-last-character-of-korean-word-in-java
		String testString = "\uD56D\uC131\uC740 \uD56D\uC0C1 \uD63C\uC790 \uC788\uB294 \uAC83\uC774 \uC544\uB2C8\uB77C, \uB450 \uAC1C \uC774\uC0C1\uC758";
		// mathField.insertString(testString);
		
		//mathField.insertString("\u3145\u3145\u1161\u11BC and \u110A\u1161\u11BC should both be \uC30D");
		
		//mathField.insertString("\u314E\u314F\u3145\u3145   \u314E\u314F\u3145\u3145\u314F   \u314E\u314F\u3145\u3145\u314F\u3147      ");
		//mathField.insertString("   \u3131\u314F\u3142\u3145\u3145\u314F\u3134= should give \uAC12\uC0B0 (not \uAC11\uC2FC");
		
		//mathField.insertString("\u3131\u314F\u3142\u3145\u3147\u3161\u3134  ");//\u3147\u3161\u3134");
		
		mathField.insertString("\uBDC1 cannot be represented: \u3142\u315C\u3154\u3139\u3131");
		
		//testString="\uC30D";
		testString="\uB113";

		String flat = Korean.flattenKorean(testString);
		String unflat = Korean.unflattenKorean(flat).toString();

		System.err.println("original " + testString);
		System.err.println("unflat   " + unflat);
		System.err.println("flat     " + flat);
		System.err.println("Normalizer.normalize(flat, Normalizer.Form.NFKC) " + Normalizer.normalize(flat, Normalizer.Form.NFKC));
		System.err.println("Normalizer.normalize(unflat, Normalizer.Form.NFD) " + Normalizer.normalize(unflat, Normalizer.Form.NFD));
		
		// inputController.bkspCharacter(editorState);
		// inputController.bkspCharacter(editorState);
		// mathFieldInternal.update();

		// MathContainer.toHexString(c)

		// mathField.insertString("Midpoint(<Point>, <Point>)");
		
		

	}


}
