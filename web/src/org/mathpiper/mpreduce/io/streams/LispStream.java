package org.mathpiper.mpreduce.io.streams;

//
// This file is part of the Jlisp implementation of Standard Lisp
// Copyright \u00a9 (C) Codemist Ltd, 1998-2011.
//

/**************************************************************************
 * Copyright (C) 1998-2011, Codemist Ltd.                A C Norman       *
 *                            also contributions from Vijay Chauhan, 2002 *
 *                                                                        *
 * Redistribution and use in source and binary forms, with or without     *
 * modification, are permitted provided that the following conditions are *
 * met:                                                                   *
 *                                                                        *
 *     * Redistributions of source code must retain the relevant          *
 *       copyright notice, this list of conditions and the following      *
 *       disclaimer.                                                      *
 *     * Redistributions in binary form must reproduce the above          *
 *       copyright notice, this list of conditions and the following      *
 *       disclaimer in the documentation and/or other materials provided  *
 *       with the distribution.                                           *
 *                                                                        *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS    *
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT      *
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS      *
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE         *
 * COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,   *
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,   *
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS  *
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND *
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR  *
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF     *
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH   *
 * DAMAGE.                                                                *
 *************************************************************************/

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

import org.mathpiper.mpreduce.Environment;
import org.mathpiper.mpreduce.Jlisp;
import org.mathpiper.mpreduce.LispObject;
import org.mathpiper.mpreduce.LispReader;
import org.mathpiper.mpreduce.Lit;
import org.mathpiper.mpreduce.datatypes.LispString;
import org.mathpiper.mpreduce.exceptions.ResourceException;
import org.mathpiper.mpreduce.functions.builtin.Fns;
import org.mathpiper.mpreduce.numbers.LispFloat;
import org.mathpiper.mpreduce.numbers.LispInteger;
import org.mathpiper.mpreduce.symbols.Symbol;

public class LispStream extends LispObject
{
    String name;

    public LispStream(String name)
    {
        this.name = name;
    }

// A jollity here is that a LispStream can be both for input and
// ouput. So I defined the fields & methods used for each here even though
// they only get activated in sub-classes.

    public int lineLength = 80;  // for linelength() and wrapping
    public int column;           // for posn(), lengthc()

// Making end-of-line behave in a properly platform-specific manner is
// something I had just expected to happen for me as part of the Unicode
// conversion fun, but it seems not to. So I do it by steam here. Ugh!

    public static String eol = "\n"; //System.getProperty("line.separator");

// Various classes derived from this generic LispStream will direct
// output to one or the other of the following destinations. I include
// all the options here to start with but will consider moving some down
// the class hierarchy later on when I have things more complete!

    public LispObject exploded;  // for explode() and friends
    public StringBuffer sb;      // for explodeToString()
    public MessageDigest md;     // for md5 checksumming
    public LispStream wr;            // for ordinary printing!

    public void print(String s) throws ResourceException
    { // attempting to print to (eg) an input stream has no effect at all
    }

    public void println(String s) throws ResourceException
    {
    }

    public void write(int i) throws IOException
    {
        sb.append((char)i);
    }

    public void write(byte[] b, int off, int len) throws IOException
    {
        if(b != null)
        {
            for(int index = off; index < b.length; index++)
            {
                write(b[index]);
            }
        }
    }

    public void println() throws ResourceException
    {
        print("\n");
    }

    public void flush()
    {
    }

    public void close()
    {
        if (reader != null)
        {   try
            {   reader.close();
            }
            catch (IOException e)
            {}
        }
    }

// Next I include data fields and methods associated with reading from
// this stream. The code here will be to read from a file, or at least
// something packaged up via a Reader. But a sub-class will be able to
// cause input to be taken from a list (for compress()).

    public boolean inputValid;
    public LispObject inputData;
    String stringData;
    public InputStream reader = null;


// This will behave somewhat like the regular Java StreamTokenizer class
// except that the rules for processing will be as expected for
// my Lisp purposes. This will include making integers readable
// as big values not just longs, distinction between integer and floating
// point input, and acceptance of markers that flag the following character
// as "letter-like". So when I have read a token the variable ttype will get
// set to show what sort of thing is there and value will hold extended
// data.

    public static final int TT_EOF    = -1;
    public static final int TT_NUMBER = -2;
    public static final int TT_WORD   = -3;
    public static final int TT_STRING = -4;
    int ttype;

    public LispObject value; // symbol, number, whatever

    public int nextChar, prevChar = '\n';
    StringBuffer s = new StringBuffer();
    public boolean needsPrompt;
    public boolean escaped;

    public boolean allowOctal;

    static BigInteger [] digits = // to avoid repeated re-construction
    {   BigInteger.valueOf(0),
        BigInteger.valueOf(1),
        BigInteger.valueOf(2),
        BigInteger.valueOf(3),
        BigInteger.valueOf(4),
        BigInteger.valueOf(5),
        BigInteger.valueOf(6),
        BigInteger.valueOf(7),
        BigInteger.valueOf(8),
        BigInteger.valueOf(9),
        BigInteger.valueOf(10),
        BigInteger.valueOf(11),
        BigInteger.valueOf(12),
        BigInteger.valueOf(13),
        BigInteger.valueOf(14),
        BigInteger.valueOf(15),
        BigInteger.valueOf(16)
    };

// To get an input-capable stream I can either create it directly using
// this constructor, or I can add input capability to an existing
// (output) stream using setReader().

    public LispStream(String name, InputStream reader, boolean np, boolean allowOctal)
    {
        this.name = name;
        setReader(name, reader, np, allowOctal);
    }

    public void setReader(String name, InputStream reader, boolean np, boolean allowOctal)
    {
        this.name = name;
        this.reader = reader;
        this.needsPrompt = np;
        escaped = false;
        this.allowOctal = allowOctal;
        nextChar = -2;
    }

// Parsing of Lisp tokens uses a finite state machine. The next
// collection of values represent its states.

    static final int init   =  0;
    static final int zero   =  1;
    static final int binary =  2;
    static final int octal  =  3;
    static final int dec    =  4;
    static final int hex    =  5;
    static final int dot    =  6;
    static final int e      =  7;
    static final int e1     =  8;
    static final int e2     =  9;
    static final int e3     = 10;
    static final int esc    = 11;
    static final int sym    = 12;
    static final int signed = 13;

    void prompt() throws ResourceException
    {
        if (!needsPrompt) return;
// here I want to print to the standard output regardless of any stream
// selections anybody has tried to make!
        if (Fns.prompt != null) Jlisp.lispIO.print(Fns.prompt);
        Jlisp.lispIO.flush();
    }
    
    public int read() throws Exception
    {
        if (reader == null) return -1;
        int c;
        try
        {   c = reader.read();
            if (c == -1) return c;
        }
        catch (IOException e)
        {   return -1;
        }
        if (Jlisp.lit[Lit.starecho].car/*value*/ != Environment.nil)
        {   LispStream o = (LispStream)Jlisp.lit[Lit.std_output].car/*value*/;
            o.print(String.valueOf((char)c));
        }
        return c;
    }

    public void getNext() throws Exception
    {
        if (prevChar == '\n') prompt();
        int c = read();
        if (c >= 0 && !escaped)
        {   if (((Symbol)Jlisp.lit[Lit.lower]).car/*value*/ !=
                Environment.nil) c = (int)Character.toLowerCase((char)c);
            else if (((Symbol)Jlisp.lit[Lit.raise]).car/*value*/ !=
                Environment.nil) c = (int)Character.toUpperCase((char)c);
        }
        prevChar = nextChar = c;
    }
    
    public int readChar() throws Exception // gets one character
    {
        if (nextChar == -2) getNext();
        int c = nextChar;
        if (c == 26) c = -1;  // map ^Z onto EOF
        if (nextChar != -1) nextChar = -2;
        return c;
    }
    
    public int nextToken() throws Exception
    {
        int i;
        for (;;)
        {   if (nextChar == -2) getNext();
            if (nextChar == -1 || nextChar == 26) 
            {   nextChar = -1;
                return TT_EOF;
            }
            if (((char)nextChar) == ' ' || ((char)nextChar) == '\n' || ((char)nextChar) == '\t' || ((char)nextChar) == '\r' )
            {   nextChar = -2;
                continue;
            }
            if (nextChar == '%')
            {   while (nextChar != '\n' &&
                       nextChar != '\r' &&
                       //nextChar != '\p' &&
                       nextChar != -1)
                {   getNext();
                }
                if (nextChar == -1 || nextChar == 26)
                {   nextChar = -1;
                    return TT_EOF;
                }
                continue;
            }
            else if (nextChar == '\"')
            {   s.setLength(0);
                for (;;)
                {   if (prevChar == '\n') prompt(); // no case fold
                    prevChar = nextChar = read();
                    if (nextChar == -1 || nextChar == 26) break;
                    if (nextChar == '\"')
                    {   prevChar = nextChar = read(); // no possibly prompt here
                        if (nextChar != '\"') break;
                    }
                    s.append((char)nextChar);
                }
                value = new LispString(s.toString());
                return TT_WORD;
            }
            else if (Character.isLetterOrDigit((char)nextChar) ||
                     nextChar == '_' ||
                     nextChar == '+' || nextChar == '-' || // for numbers
// It seems that maybe at this level Reduce wants almost any character to
// be allowed to start a symbol. And specifically the mathmlom package depends
// on some of these via its use of compress.
                     nextChar == '&' || nextChar == '$' || nextChar == '*' ||
                     nextChar == '/' || nextChar == '^' || nextChar == '?' ||
                     nextChar == '<' || nextChar == '>' || nextChar == ':' ||
                     nextChar == ';' || nextChar == '#' || nextChar == '\\' ||
                     nextChar == '!')
            {
// Numbers are in one of the following forms:
//       0dddddddddd                octal (digits 0 to 7)
//       0Bddddddddd                binary (digits 0 or 1)
//       0Xddddddddd                hex   (digits 0 to f)
//       [+/-]ddddddddddd           decimal
//       [+/-]dddd.[ddd][E[+|-]ddd] floating point with "."
//       [+/-]ddddE[+|-]ddd         floating point with "E"
// and if a sequence of characters starts off as a number if it
// breaks with one of the above forms before a "." then I just
// keep reading ahead and accept a symbol. If I have seen a "."
// or E followed by + or - (but not just E on its own) and the format
// deviates from that of a floating point number then I stop reading
// at the character that was odd. This 0xen is a symbol despite 0xe
// making it start looking like a hex value. Similarly 100e99L will
// be a symbol because the L can not form part of a float. But the
// very similar-seeming 1.00E99L will be treated as two tokens, ie
// 1.00E99 followed by L. And equally 1e-7x is two tokens, one for
// the floating point value 1.0e-7 and a second for the "x". Escaped
// characters (following "!") can never be parts of numbers and so
// fit somewhere in the above discussion. Eg 0x12!34 is a symbol and 
// 1.23!e4 is a float (1.23) followed by a symbol (!e4).
// 
// I parse using a finite state machine so that at the end of a
// token I can tell it I have a number or a symbol (and what sort
// of number too). The code here may indicate that I would quite
// like to have GOTO statements in my language for implementing
// some styles of code! Equally one feels that this mess might better
// be implemented using transition tables. But if I did that I would
// need more refined classification of every character read.
                s.setLength(0);
                int state = init;
                for (;;getNext())
                {   switch (state)
                    {
                case init:
                        if (nextChar == '!')
                        {   state = esc;  // NB do not buffer the "!"
                            escaped = true;
                            continue;
                        }
                        else if (nextChar == '0' && allowOctal)
                        {   state = zero;
                            s.append('0');
                            continue;
                        }
                        else if (Character.digit((char)nextChar, 10) >= 0)
                        {   state = dec;
                            s.append((char)nextChar);
                            continue;
                        }
                        else if (nextChar == '+' || nextChar == '-')
                        {   state = signed;
                            s.append((char)nextChar);
                            continue;
                        }
                        else
// In the init state I know that the character is alphanumeric.
                        {   state = sym;
                            s.append((char)nextChar);
                            continue;
                        }
                case signed:
                        if (Character.digit((char)nextChar, 10) >= 0)
                        {   state = dec;
                            s.append((char)nextChar);
                            continue;
                        }
                        else
                        {   value = Symbol.intern(s.toString());
                            return TT_WORD;
                        }
                case zero:
                        if (nextChar == 'B' || nextChar == 'b')
                        {   state = binary;
                            s.append((char)nextChar);
                            continue;
                        }
                        else if (nextChar == 'X' || nextChar == 'x')
                        {   state = hex;
                            s.append((char)nextChar);
                            continue;
                        }
                        else if (Character.digit((char)nextChar, 8) >= 0)
                        {   state = octal;
                            s.append((char)nextChar);
                            continue;
                        }
                        else if (nextChar == '.')
                        {   state = dot;
                            s.append('.');
                            continue;
                        }
                        else if (nextChar == 'e' || nextChar == 'E')
                        {   state = e2;
                            s.append((char)nextChar);
                            continue;
                        }
                        else if (Character.isLetterOrDigit((char)nextChar) ||
                                 nextChar == '_')
                        {   state = sym;
                            s.append((char)nextChar);
                            continue;
                        }
                        else if (nextChar == '!')
                        {   state = esc;
                            escaped = true;
                            continue;
                        }
                        else break;
                case binary:
                        if (Character.digit((char)nextChar, 2) >= 0)
                        {   s.append((char)nextChar);
                            continue;
                        }
                        else if (Character.isLetterOrDigit((char)nextChar) ||
                                 nextChar == '_')
                        {   state = sym;
                            s.append((char)nextChar);
                            continue;
                        }
                        else if (nextChar == '!')
                        {   state = esc;
                            escaped = true;
                            continue;
                        }
                        else break;
                case octal:
                        if (Character.digit((char)nextChar, 8) >= 0)
                        {   s.append((char)nextChar);
                            continue;
                        }
                        else if (Character.isLetterOrDigit((char)nextChar) ||
                                 nextChar == '_')
                        {   state = sym;
                            s.append((char)nextChar);
                            continue;
                        }
                        else if (nextChar == '!')
                        {   state = esc;
                            escaped = true;
                            continue;
                        }
                        else break;
                case hex:
                        if (Character.digit((char)nextChar, 16) >= 0)
                        {   s.append((char)nextChar);
                            continue;
                        }
                        else if (Character.isLetterOrDigit((char)nextChar) ||
                                 nextChar == '_')
                        {   state = sym;
                            s.append((char)nextChar);
                            continue;
                        }
                        else if (nextChar == '!')
                        {   state = esc;
                            escaped = true;
                            continue;
                        }
                        else break;
                case dec:
                        if (Character.digit((char)nextChar, 10) >= 0)
                        {   s.append((char)nextChar);
                            continue;
                        }
                        else if (nextChar == '.')
                        {   state = dot;
                            s.append('.');
                            continue;
                        }
                        else if (nextChar == 'e' || nextChar == 'E')
                        {   state = e2;
                            s.append((char)nextChar);
                            continue;
                        }
                        else if (Character.isLetterOrDigit((char)nextChar) ||
                                 nextChar == '_')
                        {   state = sym;
                            s.append((char)nextChar);
                            continue;
                        }
                        else if (nextChar == '!')
                        {   state = esc;
                            escaped = true;
                            continue;
                        }
                        else break;
                case dot:
                        if (nextChar == 'e' || nextChar == 'E')
                        {   state = e;
                            s.append((char)nextChar);
                            continue;
                        }
                        else if (Character.digit((char)nextChar, 10) >= 0)
                        {   s.append((char)nextChar);
                            continue;
                        }
                        else break;
                case e:
                        if (Character.digit((char)nextChar, 10) >= 0 ||
                            nextChar == '+' || nextChar == '-')
                        {   state = e1;
                            s.append((char)nextChar);
                            continue;
                        }
                        else break;
                case e1:
                        if (Character.digit((char)nextChar, 10) >= 0)
                        {   s.append((char)nextChar);
                            continue;
                        }
                        else break;
                case e2:
                        if (Character.digit((char)nextChar, 10) >= 0)
                        {   state = e3;
                            s.append((char)nextChar);
                            continue;
                        }
                        else if (nextChar == '+' || nextChar == '-')
                        {   state = e1;
                            s.append((char)nextChar);
                            continue;
                        }
                        else if (Character.isLetterOrDigit((char)nextChar) ||
                                 nextChar == '_')
                        {   state = sym;
                            s.append((char)nextChar);
                            continue;
                        }
                        else if (nextChar == '!')
                        {   state = esc;
                            escaped = true;
                            continue;
                        }
                        else break;
                case e3:
                        if (Character.digit((char)nextChar, 10) >= 0)
                        {   s.append((char)nextChar);
                            continue;
                        }
                        else if (Character.isLetterOrDigit((char)nextChar) ||
                                 nextChar == '_')
                        {   state = sym;
                            s.append((char)nextChar);
                            continue;
                        }
                        else if (nextChar == '!')
                        {   state = esc;
                            escaped = true;
                            continue;
                        }
                        else break;
                case esc:
                        state = sym;
                        escaped = false;
                        s.append((char)nextChar);
                        continue;
                case sym:
                        if (Character.isLetterOrDigit((char)nextChar) ||
                            nextChar == '_')
                        {   s.append((char)nextChar);
                            continue;
                        }
                        else if (nextChar == '!')
                        {   state = esc;
                            escaped = true;
                            continue;
                        }
                        else break;
                default:
                        break; // should never happen!
                    }
                    break;
                }
                BigInteger r;
                switch (state)
                {
            case dec:
                    r = new BigInteger(s.toString());
                    value = LispInteger.valueOf(r);
                    break;
            case binary:
// If I really expected LOTS of people to read in large binary, octal
// or hex numbers I ought to read around 32-bits at a time using int or
// long arithmetic and only fall back to bignumber work after that. However
// I do NOT expect these to be read in very often so I will not bother!
                    BigInteger two = digits[2];
                    r = BigInteger.valueOf(0);
                    for (i=2; i<s.length(); i++)
                    {   r = r.multiply(two);
                        r = r.add(digits[Character.digit(s.charAt(i), 2)]);
                    }
                    value = LispInteger.valueOf(r);
                    break;
            case zero:
            case octal:
                    BigInteger eight = digits[8];
                    r = BigInteger.valueOf(0);
                    for (i=1; i<s.length(); i++)
                    {   r = r.multiply(eight);
                        r = r.add(digits[Character.digit(s.charAt(i), 8)]);
                    }
                    value = LispInteger.valueOf(r);
                    break;
            case hex:
                    BigInteger sixteen = digits[16];
                    r = BigInteger.valueOf(0);
                    for (i=2; i<s.length(); i++)
                    {   r = r.multiply(sixteen);
                        r = r.add(digits[Character.digit(s.charAt(i), 16)]);
                    }
                    value = LispInteger.valueOf(r);
                    break;
            case dot:
            case e:
            case e1:
            case e2:
            case e3:
                    Double d = Double.valueOf(s.toString());
                    value = new LispFloat(d.doubleValue());
                    break;
            case sym:
                    value = Symbol.intern(s.toString());
                    break;
            default:
                    value = Environment.nil; // should never happen
                } 
                return TT_WORD;
            }
            else if (nextChar == '.' || nextChar == '(' ||
                     nextChar == ')' || nextChar == '\'' ||
                     nextChar == '`')
            {   int r = nextChar;
                nextChar = -2;
                return r;
            }
            else if (nextChar == ',')
            {   getNext();
                if (nextChar == '@')
                {   nextChar = -2;
                    return 0x10000; // special value for ",@"
                }
                else return ',';
            }
            else
            {   if (nextChar < 128) value = LispReader.chars[nextChar];
                else value = Symbol.intern(String.valueOf((char)nextChar));
                nextChar = -2;
                return TT_WORD;
            }
        }
    } 

    public void tidyup(LispObject a)
    {
        value = a;
        inputData = a;
        exploded = a;
    }
    
    public void iprint() throws ResourceException
    {
        String s = "#Stream<" + name + ">";
        if ((currentFlags & noLineBreak) == 0 &&
            currentOutput.column + s.length() > currentOutput.lineLength)
            currentOutput.println();
        currentOutput.print(s);
    }

    public void blankprint() throws ResourceException
    {
        String s = "#Stream<" + name + ">";
        if ((currentFlags & noLineBreak) == 0 &&
            currentOutput.column + s.length() >= currentOutput.lineLength)
            currentOutput.println();
        else currentOutput.print(" ");
        currentOutput.print(s);
    }

    public LispObject eval()
    {
        return this;
    }

// I put various things related to file manipulation here as a convenient
// sort of place for them to go.


// nameConvert is here to perform any adjustments on file-names that may
// be called for. The first thing that I do is to take any initial
// prefix of the form $xxx/... and expand out the $xxx as the value
// of a lisp variable called $xxx. If the variable concerned does not have
// as string as its value I put in "." as the expansion, and hope that that
// refers to the current directory.
    public static String nameConvert(String a)
    {
        if (a.charAt(0) != '$') return a;
        int n = a.indexOf('/');
        if (n < 0) n = a.indexOf('\\');
        String prefix, tail;
        if (n < 0) 
        {   prefix = a.substring(1);
            tail = "";
        }
        else 
        {   prefix = a.substring(1, n);
            tail = a.substring(n);
        }
        LispObject x = Symbol.intern("$" + prefix).car/*value*/;
        if (x instanceof LispString) prefix = ((LispString)x).string;
        else if ((x = Symbol.intern("@" + prefix).car/*value*/) 
                instanceof LispString) prefix = ((LispString)x).string;
        else prefix = ".";
        return prefix + tail;
    }


    public static LispObject fileDate(String s)
    {
        /*try
        {   File f = new File(nameConvert(s));
            long n = f.lastModified();
            if (n == 0) return Environment.nil;
            s = new Date(n).toString();
            return new LispString(s);
        }
        catch (Exception e)
        {   return Environment.nil;
        }*/
        
        return new LispString("Operation is not supported.");
    }
    
    public static LispObject fileDelete(String s)
    {
        /*try
        {   File f = new File(nameConvert(s));
            f.delete();
            return Jlisp.lispTrue;
        }
        catch (Exception e)
        {   return Environment.nil;
        }*/

        return Environment.nil; //Operation is not supported.
    }
    
    public static LispObject fileRename(String s, String s1)
    {
        /*try
        {   File f = new File(nameConvert(s));
            File f1 = new File(nameConvert(s1));
            f.renameTo(f1);
            return Jlisp.lispTrue;
        }
        catch (Exception e)
        {   return Environment.nil;
        }*/

        return Environment.nil; //Operation not supported.
    }
    
    public void scan()
    {
        if (LispReader.objects.contains(this)) // seen before?
	{   if (!LispReader.repeatedObjects.containsKey(this))
	    {   LispReader.repeatedObjects.put(
	            this,
	            Environment.nil); // value is junk at this stage
	    }
	}
	else LispReader.objects.add(this);
    }
    


}

// end of LispStream.java
