/* {{{ License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */ //}}}

// :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:folding=explicit:collapseFolds=0:
package org.mathpiper.lisp.tokenizers;

import org.mathpiper.lisp.collections.TokenMap;
import org.mathpiper.io.MathPiperInputStream;
import org.mathpiper.builtin.BigNumber;
import org.mathpiper.lisp.*;


public class MathPiperTokenizer {

    static String symbolics = "~`!@#$^&*-=+:<>?/\\|";
    //static String unicodeVariableChars = "αβγ";
    String iToken; //Can be used as a token container.

    /// NextToken returns a string representing the next token,
    /// or an empty list.
    public String nextToken(Environment aEnvironment, int aStackTop, MathPiperInputStream aInput, TokenMap aTokenHashTable) throws Exception {
        char streamCharacter;
        int firstpos = aInput.position();

        boolean redo = true;
        while (redo) {
            redo = false;
            //REDO: //TODO FIXME
            firstpos = aInput.position();

            // End of stream: return empty string
            if (aInput.endOfStream()) {
                break;
            }

            streamCharacter = aInput.next();
            //printf("%c",c);

            //Parse brackets
            if (streamCharacter == '(') {
            } else if (streamCharacter == ')') {
            } else if (streamCharacter == '{') {
            } else if (streamCharacter == '}') {
            } else if (streamCharacter == '[') {
            } else if (streamCharacter == ']') {
            } else if (streamCharacter == ',') {
            } else if (streamCharacter == ';') {
            } else if (streamCharacter == '%') {
            } //    else if (c == '\'') {}
            else if (streamCharacter == '.' && !isDigit(aInput.peek())) {
                while (aInput.peek() == '.') {
                    aInput.next();
                }
            } // parse comments
            else if (streamCharacter == '/' && aInput.peek() == '*') {
                aInput.next(); //consume *
                while (true) {
                    while (aInput.next() != '*' && !aInput.endOfStream());
                    LispError.check(aEnvironment, aStackTop, !aInput.endOfStream(), LispError.COMMENT_TO_END_OF_FILE, "INTERNAL");
                    if (aInput.peek() == '/') {
                        aInput.next();  // consume /
                        redo = true;
                        break;
                    }
                }
                if (redo) {
                    continue;
                }
            } else if (streamCharacter == '/' && aInput.peek() == '/') {
                aInput.next(); //consume /
                while (aInput.next() != '\n' && !aInput.endOfStream());
                redo = true;
                continue;
            } // parse literal strings
            else if (streamCharacter == '\"') {
                String aResult;
                aResult = "";
                //TODO FIXME is following append char correct?
                aResult = aResult + ((char) streamCharacter);
                while (aInput.peek() != '\"') {
                    if (aInput.peek() == '\\') {
                        aInput.next();
                        LispError.check(aEnvironment, aStackTop, !aInput.endOfStream(), LispError.PARSING_INPUT, "INTERNAL");

                        /*if(! (aInput.peek() == '\"'))
                        {
                            //Leave in backslash in front of all characters except a " character.
                            aResult = aResult + "\\";
                        }*/
                    }
                    //TODO FIXME is following append char correct?
                    aResult = aResult + ((char) aInput.next());
                    LispError.check(aEnvironment, aStackTop, !aInput.endOfStream(), LispError.PARSING_INPUT, "INTERNAL");
                }
                //TODO FIXME is following append char correct?
                aResult = aResult + ((char) aInput.next()); // consume the close quote
                return (String) aTokenHashTable.lookUp(aResult);
            } //parse atoms
            else if (isAlpha(streamCharacter)) {
                while (isAlNum(aInput.peek())) {
                    aInput.next();
                }
            } else if (isSymbolic(streamCharacter)) {
                while (isSymbolic(aInput.peek())) {
                    aInput.next();
                }
            } else if (streamCharacter == '_') {
                while (aInput.peek() == '_') {
                    aInput.next();
                }
            } else if (isDigit(streamCharacter) || streamCharacter == '.') {
                while (isDigit(aInput.peek())) {
                    aInput.next();
                }
                if (aInput.peek() == '.') {
                    aInput.next();
                    while (isDigit(aInput.peek())) {
                        aInput.next();
                    }
                }
                if (BigNumber.numericSupportForMantissa()) {
                    if (aInput.peek() == 'e' || aInput.peek() == 'E') {
                        aInput.next();
                        if (aInput.peek() == '-' || aInput.peek() == '+') {
                            aInput.next();
                        }
                        while (isDigit(aInput.peek())) {
                            aInput.next();
                        }
                    }
                }
            } // Treat the char as a space.
            else {
                redo = true;
                continue;
            }
        }
        return (String) aTokenHashTable.lookUp(aInput.startPtr().substring(firstpos, aInput.position()));
    }

    public static boolean isDigit(char c) {
        return ((c >= '0' && c <= '9'));
    }

    public static boolean isAlpha(char c) {
     
       // "$", // for absolute references in the spreadsheet

        if (c >= 'a' && c <= 'z') {
            return true;
        } else if (c >= 'A' && c <= 'Z') {
            return true;
        } else if (c == '\'') {
            return true;
        } else if (c == '?') {
            return true;
        }else if (c == 0x00b7) { // middle dot (for Catalan).
            return true;
        } else if (c == 0x00b0) { // degree symbol).
            return true;
        } else if (c >= 0x00c0 && c <= 0x00d6) { //accentuated letters.
            return true;
        } else if (c >= 0x00d8 && c <= 0x01bf) {//accentuated letters.
            return true;
        } else if (c >= 0x01c4 && c <= 0x02a8) { //accentuated letters.
            return true;
        } else if (c >= 0x0391 && c <= 0x03f3) {// Greek.
            return true;
        } else if (c >= 0x0401 && c <= 0x0481) { // Cyrillic.
            return true;
        } else if (c >= 0x0490 && c <= 0x04f9) {// Cyrillic.
            return true;
        } else if (c >= 0x0531 && c <= 0x1ffc) {// a lot of signs (Arabic, accentuated, ...).
            return true;
        } else if (c >= 0x3041 && c <= 0x3357) {// Asian letters.
            return true;
        } else if (c >= 0x4e00 && c <= 0xd7a3) {// Asian letters.
            return true;
        } else if (c >= 0xf71d && c <= 0xfa2d) {// Asian letters.
            return true;
        } else if (c >= 0xfb13 && c <= 0xfdfb) {// Armenian, Hebrew, Arabic.
            return true;
        } else if (c >= 0xfe80 && c <= 0xfefc) { // Arabic.
            return true;
        } else if (c >= 0xff66 && c <= 0xff9d) {// Katakana.
            return true;
        } else if (c >= 0xffa1 && c <= 0xffdc) {// Hangul.
            return true;
        }


        return false;

    //return ( (c>='a' && c<='z') || (c>='A' && c<='Z') || (c == '\'') || unicodeVariableChars.indexOf(c) != -1);
    }

    public static boolean isAlNum(char c) {
        return (isAlpha(c) || isDigit(c));
    }

    public static boolean isSymbolic(char c) {
        return (symbolics.indexOf(c) != -1);
    }
};

