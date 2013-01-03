package geogebra.gui;

import geogebra.common.gui.inputfield.AltKeys;
import geogebra.common.main.App;
import geogebra.common.main.JavaKeyCodes;
import geogebra.common.main.KeyCodes;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;
import geogebra.main.AppD;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Locale;

import javax.swing.text.JTextComponent;

/*
 * Michael Borcherds
 * 
 * Implements KeyListener
 * adds support for alt-codes (and alt-shift-) for special characters
 * (ctrl on MacOS)
 */

public class GeoGebraKeys implements KeyListener {

	private static StringBuilder altCodes = new StringBuilder();

	private boolean altPressed;
	
	private AppD app;

	public GeoGebraKeys(AppD app) {
		this.app = app;
	}
	
	public void keyPressed(KeyEvent e) {  
		// swallow eg ctrl-a ctrl-b ctrl-p on Mac
		if (AppD.MAC_OS && e.isControlDown())
			e.consume();
		//Application.debug("keyPressed");
	}

	public void keyTyped(KeyEvent e) {
		
		//Application.debug("keyTyped"+e.getKeyChar());
		if (AppD.isAltDown(e)) {
			if (!altPressed) {
				altCodes.setLength(0);
				//Application.debug("alt pressed");
			}
			altPressed = true;
		} else {
			if (altCodes.length() > 0) {
				
				// intercept wrong character and replace with correct Alt-code
				char insertStr = (char) Integer.parseInt(altCodes.toString());
				JTextComponent comp = (JTextComponent) e.getComponent();
				int pos = comp.getCaretPosition();
				String oldText = comp.getText();
				StringBuilder sb = new StringBuilder();
				sb.append(oldText.substring(0, pos));
				sb.append(insertStr);
				sb.append(oldText.substring(pos));            
				comp.setText(sb.toString());

				comp.setCaretPosition(pos + 1);
				e.consume();
				
			}
			
			altPressed = false;
			altCodes.setLength(0);
		}
		
		

		// we don't want to trap AltGr
		// as it is used eg for entering {[}] is some locales
		// NB e.isAltGraphDown() doesn't work
		if (e.isAltDown() && e.isControlDown())
			return;

		
		

	}   

	public void keyReleased(KeyEvent e) {  
		
		// when decimal comma typed on numeric keypad on eg German keyboard, replace with .
		if (e.getKeyCode() == KeyEvent.VK_SEPARATOR && e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
			App.warn("replacing decimal , with decimal .");
			JTextComponent comp = (JTextComponent) e.getComponent();
			int pos = comp.getCaretPosition();
			String oldText = comp.getText();
			StringBuilder sb = new StringBuilder();
			
			// pos - 1 to remove ","
			sb.append(oldText.substring(0, pos - 1));
			
			sb.append(".");
			sb.append(oldText.substring(pos));            
			comp.setText(sb.toString());

			comp.setCaretPosition(pos);
			e.consume();
			
		}

		
		//Application.debug("keyReleased");
		// ctrl pressed on Mac
		// or alt on Windows
		boolean modifierKeyPressed = AppD.isAltDown(e);

		if (modifierKeyPressed) {

			String insertStr = "";


			// works nicely for alt or ctrl pressed (Windows/Mac)  
			String keyString = KeyEvent.getKeyText(e.getKeyCode()).toLowerCase(Locale.US);
			
			//Application.debug(KeyEvent.getKeyText(e.getKeyCode()).toLowerCase().charAt(0)+"");
			//Application.debug(e+"");
			//Application.debug(keyString);
			
			// support for alt codes
			if (e.isAltDown() && e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
				char c = e.getKeyChar();
				
				// make sure it's not eg alt-*
				if (c >= '0' && c <= '9')
					altCodes.append(e.getKeyChar());
				//Application.debug("alt:"+altCodes);
			}
			
			boolean numpad = e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD;
			
			// Numeric keypad numbers eg NumPad-8, NumPad *
			if (!e.isAltDown() && numpad)
				keyString = e.getKeyChar() + "";
			
			App.debug("Key pressed "+StringUtil.toHexString(e.getKeyChar())+" "+keyString);
			
			// workaround for different Java versions!!
			if (keyString.equals("minus"))
				keyString = "-";
			else if (keyString.equals("plus"))
				keyString = "+";
			else if (keyString.equals("comma"))
				keyString = ",";
			else if (keyString.equals("period"))
				keyString = ".";
			else if (keyString.equals("equals"))
				keyString = "=";
			else if (keyString.length() > 1) App.debug("Unknown keyString: "+keyString);
			
			switch (e.getKeyChar()) {
			// workaround for shifted characters:
			// (different in different locales)
			case  '+': 
			case  '*': 
			case  '=': 
			case  '-': 
			case  '>': 
			case  '<': 
				// Italian keyboard, keyString="unknown keycode: 0x0" for these two
				// French keyboard, keyString= eg "2" (so we need to leave it for Alt-2 to work)
			case  Unicode.eGrave: 
			case  Unicode.eAcute: 
				if (keyString.length() > 1) {
					keyString = e.getKeyChar()+"";
				}
			}
				
			
			// don't want to act on eg "Shift"
			if (keyString.length() == 1) {
				
				if (e.isShiftDown()) {
					insertStr = AltKeys.LookupUpper.get(Character.toUpperCase(keyString.charAt(0)));
				} else {
					// toUpperCase() here deliberate!
					insertStr = AltKeys.LookupLower.get(Character.toUpperCase(keyString.charAt(0)));
				}
				
				if (insertStr == null) {
					insertStr = "";
				}
				
				/*
				switch (keyString.charAt(0)) {

				case '*' :
					if (italian && !numpad) insertStr = e.isShiftDown() ? "}" : "]"; // Italian keyboard			
					else insertStr = ExpressionNodeConstants.strVECTORPRODUCT; // alt-* -> vector product
					break;
				case '+' :
					if (italian) insertStr = e.isShiftDown() ? "}" : "]"; // Italian keyboard			
					else insertStr = "\u00b1"; // alt-+ -> plusOrMinus
					break;
				case Unicode.eGrave :
					AbstractApplication.debug("matched eGrave "+e.isShiftDown());
					insertStr = e.isShiftDown() ? "{" : "["; // for Italian keyboard
					break;					
				case Unicode.eAcute : // Shift+eGrave on Italian keyboard
					AbstractApplication.debug("matched eAcute "+e.isShiftDown());
					insertStr = e.isShiftDown() ? "{" : "["; // for Italian keyboard
					break;					
				case '=' :
					insertStr = "\u2260"; // alt-= -> notEqualTo
					break;
				case '-' :
					//insertStr = "\u2213"; // alt-- -> minusOrPlus
					// changed for ggb40
					insertStr = "\u207b"; // alt-- -> superscript -
					break;
				case ',' : 
				case '<' : 
					insertStr = "\u2264"; // alt-< -> lessThanOrEqual
					break;
				case '.' : 
				case '>' : 
					insertStr = "\u2265"; // alt-> -> greaterThanOrEqual
					break;
				case 'a' :
					if (e.isShiftDown())
						insertStr = "\u0391"; // alt-A -> unicode alpha (upper case)
					else
						insertStr = "\u03b1"; // alt-a -> unicode alpha
					break;
				case 'b' :
					if (e.isShiftDown())
						insertStr = "\u0392"; // alt-B -> unicode beta (upper case)
					else
						insertStr = "\u03b2"; // alt-b -> unicode beta
					break;
				case 'd' :
					if (e.isShiftDown())
						insertStr = "\u0394"; // alt-D -> unicode delta (upper case)
					else
						insertStr = "\u03b4"; // alt-d -> unicode delta
					break;
				case 'e' :
					insertStr = Unicode.EULER_STRING; // alt-e -> unicode e
					break;
				case 'f' :
					if (e.isShiftDown())
						insertStr = "\u03a6"; // alt-F -> unicode phi (upper case)
					else
						insertStr = "\u03d5"; // alt-f -> unicode phi
					break;
				case 'g' :
					if (e.isShiftDown())
						insertStr = "\u0393"; // alt-G -> unicode gamma (upper case)
					else
						insertStr = "\u03b3"; // alt-g -> unicode gamma
					break;
				case 'i' :
					insertStr = Unicode.IMAGINARY; // alt-i -> sqrt(-1) symbol
					break;
				case 'l' :
					if (e.isShiftDown())
						insertStr = "\u039b"; // alt-L -> unicode lambda (upper case)
					else
						insertStr = "\u03bb"; // alt-l -> unicode lambda
					break;
				case 'm' :
					if (e.isShiftDown())
						insertStr = "\u039c"; // alt-M -> unicode my (upper case)
					else
						insertStr = "\u03bc"; // alt-m -> unicode my
					break;
				case 'o' :
					insertStr = "\u00b0"; // alt-o -> unicode degree sign
					break;
				case 'p' :
					if (e.isShiftDown())
						insertStr = "\u03a0"; // alt-P -> unicode pi (upper case)
					else
						insertStr = "\u03c0"; // alt-p -> unicode pi
					break;
				case 's' :
					if (e.isShiftDown())
						insertStr = "\u03a3"; // alt-S -> unicode sigma (upper case)
					else
						insertStr = "\u03c3"; // alt-s -> unicode sigma
					break;
				case 't' :
					if (e.isShiftDown())
						insertStr = "\u0398"; // alt-T -> unicode theta (upper case)
					else
						insertStr = "\u03b8"; // alt-t -> unicode theta
					break;
				case 'u' :
					insertStr = Unicode.Infinity+""; // alt-u -> infinity
					break;
				case 'w' :
					if (e.isShiftDown())
						insertStr = "\u03a9"; // alt-W -> unicode omega (upper case)
					else
						insertStr = "\u03c9"; // alt-w -> unicode omega
					break;
				case '0' :
					insertStr = "\u2070"; // alt-0 -> unicode superscript 0
					break;
				case '1' :
					insertStr = "\u00b9"; // alt-1 -> unicode superscript 1
					break;
				case '2' :
					insertStr = "\u00b2"; // alt-2 -> unicode superscript 2
					break;
				case '3' :
					insertStr = "\u00b3"; // alt-3 -> unicode superscript 3
					break;
				case '4' :
					insertStr = "\u2074"; // alt-4 -> unicode superscript 4
					break;
				case '5' :
					insertStr = "\u2075"; // alt-5 -> unicode superscript 5
					break;
				case '6' :
					insertStr = "\u2076"; // alt-6 -> unicode superscript 6
					break;
				case '7' :
					insertStr = "\u2077"; // alt-7 -> unicode superscript 7
					break;
//	care needed
//On pc-keyboard:    7   8   9    0
//AltGraph
//or Ctrl Alt        {   [   ]    }
//
//On Apple:          7   8   9    0
//Alt                    [   ]
//Ctrl Alt               {   }
// 
 				case '8' :
 
					insertStr = "\u2078"; // alt-8 -> unicode superscript 8
					break;
				case '9' :
					insertStr = "\u2079"; // alt-9 -> unicode superscript 9
					break;
				default:
					AbstractApplication.debug("no key matched "+keyString+" "+StringUtil.toHexString(keyString));
				} //*/
			}

			// insert into the text component
			if (!insertStr.equals("")) {
				JTextComponent comp = (JTextComponent) e.getComponent();
				int pos = comp.getCaretPosition();

				// if we have a DynamicTextInputPane then using setText to
				// insert will destroy any dynamic objects, so use its
				// insertString method instead.
				if (comp instanceof DynamicTextInputPane) {
					((DynamicTextInputPane) comp).insertString(pos, insertStr,
							null);
				} else
				// all other cases use setText
				{
					String oldText = comp.getText();
					StringBuilder sb = new StringBuilder();
					sb.append(oldText.substring(0, pos));
					sb.append(insertStr);
					sb.append(oldText.substring(pos));
					comp.setText(sb.toString());
					comp.setCaretPosition(pos + insertStr.length());
				}

				e.consume();
			}
		}
	}
}
