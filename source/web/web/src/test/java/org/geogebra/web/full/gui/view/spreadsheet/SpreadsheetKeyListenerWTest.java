/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.view.spreadsheet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.geogebra.editor.share.util.GWTKeycodes;
import org.geogebra.web.full.gui.util.AdvancedFocusPanel;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.gwtproject.event.dom.client.KeyDownEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwtmockito.WithClassesToStub;

@RunWith(GgbMockitoTestRunner.class)
@WithClassesToStub(AdvancedFocusPanel.class)
public class SpreadsheetKeyListenerWTest {

	private SpreadsheetKeyListenerW listener;

	@Before
	public void setup() {
		AppWFull app = AppMocker.mockApplet(new AppletParameters("classic"));
		SpreadsheetViewW view = new SpreadsheetViewW(app);
		view.setDefaultSelection();
		listener = spy(view.getSpreadsheetListener());
	}

	@Test
	public void testLetterOrDigitIsNotTypedForMetaKeyOnWindows() {
		KeyDownEvent keyDownEvent = createKeyDownEvent(GWTKeycodes.KEY_WIN_KEY_LEFT_META);
		when(keyDownEvent.isMetaKeyDown()).thenReturn(true);
		verifyNoLetterOrDigitTyped(keyDownEvent);
	}

	@Test
	public void testLetterOrDigitIsNotTypedForEscapeKey() {
		KeyDownEvent keyDownEvent = createKeyDownEvent(GWTKeycodes.KEY_ESCAPE);
		verifyNoLetterOrDigitTyped(keyDownEvent);
	}

	@Test
	public void testLetterOrDigitIsNotTypedForUpKey() {
		KeyDownEvent keyDownEvent = createKeyDownEvent(GWTKeycodes.KEY_UP);
		verifyNoLetterOrDigitTyped(keyDownEvent);
	}

	@Test
	public void testLetterOrDigitIsNotTypedForDownKey() {
		KeyDownEvent keyDownEvent = createKeyDownEvent(GWTKeycodes.KEY_DOWN);
		verifyNoLetterOrDigitTyped(keyDownEvent);
	}

	@Test
	public void testLetterOrDigitIsNotTypedForLeftKey() {
		KeyDownEvent keyDownEvent = createKeyDownEvent(GWTKeycodes.KEY_LEFT);
		verifyNoLetterOrDigitTyped(keyDownEvent);
	}

	@Test
	public void testLetterOrDigitIsNotTypedForRightKey() {
		KeyDownEvent keyDownEvent = createKeyDownEvent(GWTKeycodes.KEY_RIGHT);
		verifyNoLetterOrDigitTyped(keyDownEvent);
	}

	@Test
	public void testLetterOrDigitIsNotTypedForHomeKey() {
		KeyDownEvent keyDownEvent = createKeyDownEvent(GWTKeycodes.KEY_HOME);
		verifyNoLetterOrDigitTyped(keyDownEvent);
	}

	@Test
	public void testLetterOrDigitIsNotTypedForEndKey() {
		KeyDownEvent keyDownEvent = createKeyDownEvent(GWTKeycodes.KEY_END);
		verifyNoLetterOrDigitTyped(keyDownEvent);
	}

	@Test
	public void testLetterOrDigitIsNotTypedForCtrlKey() {
		KeyDownEvent keyDownEvent = createKeyDownEvent(GWTKeycodes.KEY_CTRL);
		verifyNoLetterOrDigitTyped(keyDownEvent);
	}

	@Test
	public void testLetterOrDigitIsNotTypedForShiftKey() {
		KeyDownEvent keyDownEvent = createKeyDownEvent(GWTKeycodes.KEY_SHIFT);
		verifyNoLetterOrDigitTyped(keyDownEvent);
	}

	@Test
	public void testLetterOrDigitIsNotTypedForAltKey() {
		KeyDownEvent keyDownEvent = createKeyDownEvent(GWTKeycodes.KEY_ALT);
		verifyNoLetterOrDigitTyped(keyDownEvent);
	}

	@Test
	public void testLetterOrDigitIsNotTypedForDeleteKey() {
		KeyDownEvent keyDownEvent = createKeyDownEvent(GWTKeycodes.KEY_DELETE);
		verifyNoLetterOrDigitTyped(keyDownEvent);
	}

	@Test
	public void testLetterOrDigitIsNotTypedForBackspaceKey() {
		KeyDownEvent keyDownEvent = createKeyDownEvent(GWTKeycodes.KEY_BACKSPACE);
		verifyNoLetterOrDigitTyped(keyDownEvent);
	}

	@Test
	public void testLetterOrDigitIsNotTypedForEnterKey() {
		KeyDownEvent keyDownEvent = createKeyDownEvent(GWTKeycodes.KEY_ENTER);
		verifyNoLetterOrDigitTyped(keyDownEvent);
	}

	@Test
	public void testLetterOrDigitIsNotTypedForPageupKey() {
		KeyDownEvent keyDownEvent = createKeyDownEvent(GWTKeycodes.KEY_PAGEUP);
		verifyNoLetterOrDigitTyped(keyDownEvent);
	}

	@Test
	public void testLetterOrDigitIsNotTypedForPagedownKey() {
		KeyDownEvent keyDownEvent = createKeyDownEvent(GWTKeycodes.KEY_PAGEDOWN);
		verifyNoLetterOrDigitTyped(keyDownEvent);
	}

	@Test
	public void testLetterOrDigitIsNotTypedForTabKey() {
		KeyDownEvent keyDownEvent = createKeyDownEvent(GWTKeycodes.KEY_TAB);
		verifyNoLetterOrDigitTyped(keyDownEvent);
	}

	@Test
	public void testLetterOrDigitIsNotTypedWhenSelectingAllCells() {
		KeyDownEvent keyDownEvent = createKeyDownEvent(GWTKeycodes.KEY_A);
		when(keyDownEvent.isControlKeyDown()).thenReturn(true);
		verifyNoLetterOrDigitTyped(keyDownEvent);
	}

	@Test
	public void testLetterOrDigitIsNotTypedWhenUndoing1() {
		KeyDownEvent keyDownEvent = createKeyDownEvent(GWTKeycodes.KEY_Z);
		when(keyDownEvent.isControlKeyDown()).thenReturn(true);
		verifyNoLetterOrDigitTyped(keyDownEvent);
	}

	@Test
	public void testLetterOrDigitIsNotTypedWhenUndoing2() {
		KeyDownEvent keyDownEvent = createKeyDownEvent(GWTKeycodes.KEY_Y);
		when(keyDownEvent.isControlKeyDown()).thenReturn(true);
		verifyNoLetterOrDigitTyped(keyDownEvent);
	}

	private KeyDownEvent createKeyDownEvent(int keyCode) {
		KeyDownEvent keyDownEvent = mock(KeyDownEvent.class);
		when(keyDownEvent.getNativeKeyCode()).thenReturn(keyCode);
		return keyDownEvent;
	}

	private void verifyNoLetterOrDigitTyped(KeyDownEvent keyDownEvent) {
		listener.onKeyDown(keyDownEvent);
		verify(listener, never()).letterOrDigitTyped();
	}
}
