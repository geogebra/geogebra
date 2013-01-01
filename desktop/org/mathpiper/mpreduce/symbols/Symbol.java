package org.mathpiper.mpreduce.symbols;

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


// Class to represent Lisp symbols

import org.mathpiper.mpreduce.Environment;
import org.mathpiper.mpreduce.Jlisp;
import org.mathpiper.mpreduce.LispObject;
import org.mathpiper.mpreduce.LispReader;
import org.mathpiper.mpreduce.Lit;
import org.mathpiper.mpreduce.exceptions.ResourceException;
import org.mathpiper.mpreduce.functions.lisp.LispFunction;
import org.mathpiper.mpreduce.functions.lisp.Undefined;
import org.mathpiper.mpreduce.io.Fasl;
import org.mathpiper.mpreduce.special.SpecialFunction;

public class Symbol extends LispObject
{

    public static int symbolCount = 0;

// ALL LispObjects have car and cdr fields, which can contain other
// LispObjects. So I might as well use those for the fields I would otherwise
// need here that are of that type, ie value and plist. Ugh!

//  LispObject value;        // shallow-binding         use .car instead!
//  LispObject plist;        // property list           use .cdr instead!

    public String pname;            // print name
    int cacheFlags;          // used with cacheString to speed up..
    String cacheString;      // .. printing when escape chare may be needed
    public LispFunction fn;         // function (if any)
    public SpecialFunction special; // special fn (if any)


    public void completeName()      // needed to that gensyms can have delayed names
    {
    }

// intern() looks up a Java String and find the Lisp
// symbol with that name. It creates it if needbe. This version
// always sets the (pre-defined) function call of this symbol. It is
// only used from cold-start code. So at that stage nothing will ever have
 // been subject to "remob" and so I do not need to fuss about tombstones.

    public static Symbol intern(String name,
                         LispFunction fn, 
                         SpecialFunction special)
    {
        Symbol p;
        int inc = name.hashCode();
        int hash = ((169*inc) & 0x7fffffff) % LispReader.oblistSize;
        inc = 1 + (inc & 0x7fffffff) % (LispReader.oblistSize-1);
        for (;;)
        {   p = LispReader.oblist[hash];
            if (p == null) break;    // symbol is not in oblist
            if (p.pname.equals(name)) 
            {   if (fn != null) p.fn = fn;
                if (special != null) p.special = special;
                return p;
            }
            hash += inc;
            if (hash >= LispReader.oblistSize) hash -= LispReader.oblistSize;
        }
// not found on object-list, so create it.
        p = new Symbol();
        p.pname = name;
        p.cacheFlags = -1;
        p.car/*value*/ = Jlisp.lit[Lit.undefined];
        p.cdr/*plist*/ = Environment.nil;
        LispReader.oblist[hash] = p;
        if (fn != null) p.fn = fn;
        else p.fn = new Undefined(name);
        p.special = special;
        LispReader.oblistCount++;
        if (4*LispReader.oblistCount > 3*LispReader.oblistSize) LispReader.reHashOblist();
        return p;
    }

    public static void remob(Symbol p)
    {
        p.completeName();
        String name = p.pname;
        int inc = name.hashCode();
        int hash = ((169*inc) & 0x7fffffff) % LispReader.oblistSize;
        inc = 1 + (inc & 0x7fffffff) % (LispReader.oblistSize-1);
        for (;;)
        {   LispObject p1 = LispReader.oblist[hash];
            if (p1 == null) return;    // symbol is not in oblist
            if (p1 == p) break;
            hash += inc;
            if (hash >= LispReader.oblistSize) hash -= LispReader.oblistSize;
        }
// Although the undefined value goes into the hash table at the start
// I use that value there as a tombstone marker.
        LispReader.oblist[hash] = (Symbol)Jlisp.lit[Lit.undefined];
    }       

// now the version of intern() for normal use
    public static Symbol intern(String name)
    {
        Symbol p;
        int inc = name.hashCode();
        int hash = ((169*inc) & 0x7fffffff) % LispReader.oblistSize;
        int tomb = -1;
        inc = 1 + (inc & 0x7fffffff) % (LispReader.oblistSize-1);
        for (;;)
        {   p = LispReader.oblist[hash];
        	if (p == Jlisp.lit[Lit.undefined]) tomb = hash;
            if (p == null) break;    // symbol is not in oblist
            if (p.pname.equals(name)) return p;
            hash += inc;
            if (hash >= LispReader.oblistSize) hash -= LispReader.oblistSize;
        }
        if (tomb != -1) hash = tomb;
// not found on object-list, so create it.
        p = new Symbol();
        p.pname = name;
        p.cacheFlags = -1;
        p.car/*value*/ = Jlisp.lit[Lit.undefined];
        p.cdr/*plist*/ = Environment.nil;
        LispReader.oblist[hash] = p;
        p.fn = new Undefined(name);
        p.special = null;
        LispReader.oblistCount++;
        if (4*LispReader.oblistCount > 3*LispReader.oblistSize) LispReader.reHashOblist();
        return p;
    }

    public LispObject eval()
    {   return car/*value*/;
    }

    static StringBuffer cache = new StringBuffer();

    String toPrint()
    {
        completeName();
        if ((currentFlags & (printEscape | printLower | printUpper)) == 0)
            return pname;
        else if (currentFlags == cacheFlags) return cacheString;
        cache.setLength(0);
        String p = pname;
        if (p.length() == 0) return p;
        cacheFlags = currentFlags;
        if ((currentFlags & printLower) != 0) p = p.toLowerCase();
        else if ((currentFlags & printUpper) != 0) p = p.toUpperCase();
        char c = p.charAt(0);
        if ((currentFlags & printEscape) != 0)
        {   if (Character.isLetter(c))
            {   if (((Symbol)Jlisp.lit[Lit.lower]).car/*value*/ !=
                    Environment.nil)
                {   if (Character.isUpperCase(c))
                        cache.append((char)'!');
                }
                else if (((Symbol)Jlisp.lit[Lit.raise]).car/*value*/ !=
                    Environment.nil)
                {   if (Character.isLowerCase(c))
                        cache.append((char)'!');
                }
                cache.append((char)c);
            }
//          else if ((int)c < 32)
//          {   cache.append("\\x" + Integer.toHexString((int)c));
//          }
            else
            {   cache.append((char)'!');
                cache.append((char)c);
            }
        }
        else cache.append((char)c);
        for (int i=1; i<p.length(); i++)
        {   c = p.charAt(i);
            if ((currentFlags & printEscape) != 0)
            {   if (Character.isLetterOrDigit(c) || c == '_')
                {   if (((Symbol)Jlisp.lit[Lit.lower]).car/*value*/ !=
                        Environment.nil)
                    {   if (Character.isUpperCase(c))
                            cache.append((char)'!');
                    }
                    else if (((Symbol)Jlisp.lit[Lit.raise]).car/*value*/ !=
                        Environment.nil)
                    {   if (Character.isLowerCase(c))
                            cache.append((char)'!');
                    }
                    cache.append((char)c);
                } 
//              else if ((int)c < 32)
//              {   cache.append("\\x" + Integer.toHexString((int)c));
//              }
                else
                {   cache.append((char)'!');
                    cache.append((char)c);
                }
            }
            else cache.append((char)c);
        }
        cacheString = cache.toString();
        return cacheString;
    }
    public void iprint() throws ResourceException
    {
        String s = toPrint();
        if ((currentFlags & noLineBreak) == 0 &&
            currentOutput.column + s.length() > currentOutput.lineLength)
            currentOutput.println();
        currentOutput.print(s);
    }

    public void blankprint() throws ResourceException
    {
        String s = toPrint();
        if ((currentFlags & noLineBreak) == 0 &&
            currentOutput.column + s.length() >= currentOutput.lineLength)
            currentOutput.println();
        else currentOutput.print(" ");
        currentOutput.print(s);
    }

    public int lisphashCode()
    {
        completeName();
        return 139*pname.hashCode() ^ 0x12345678; 
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
        else 
        {   LispReader.objects.add(this);
            if (Jlisp.descendSymbols)
            {   if (car/*value*/ != null) LispReader.stack.push(car/*value*/);
                if (cdr/*plist*/ != null) LispReader.stack.push(cdr/*plist*/);
                if (fn != null) LispReader.stack.push(fn);
                if (special != null) LispReader.stack.push(special);
            }
        }
    }
    
    public void dump() throws Exception
    {
        Object w = LispReader.repeatedObjects.get(this);
        if (w != null &&
            w instanceof Integer)
        {   if (Jlisp.specialNil &&
                this == Environment.nil) 
                // important enough for a special-case, but must not use
                // that while dumping itself!
                Jlisp.odump.write(X_LIST+0);
            else putSharedRef(w); // processed before
        }
        else
        {   if (w != null) // will be used again sometime
            {   LispReader.repeatedObjects.put(
                    this,
                    new Integer(LispReader.sharedIndex++));
                Jlisp.odump.write(X_STORE);
            }
// Now this is the first time I see this symbol while writing a dump
// file. For a symbol I emit
//    SYM n c0 c1 ... cn  // the name
//    special fn plist value
            if (!Jlisp.descendSymbols) // ie for FASL not PRESERVE
            {
// The search here is a crude linear search through the most recent
// 512 symbols mentioned. I could use a hash table but the code overhead
// makes this really simple look attractive as a start.
                 for (int i=0; i<512; i++)
                 {   if (Fasl.recent[i] == this)
                     {    int op = X_RECENT;
                          if (i >= 256)
                          {   op = X_RECENT1;
                              i -= 256;
                          }
                          Jlisp.odump.write(op);
                          Jlisp.odump.write(i);
                          Fasl.recentn++;
                          return;
                     }
                 }
            }
            byte [] rep = pname.getBytes("UTF8");
            int length = rep.length;
// Because I expect it to be a common case I have a special opcode
// for a function that does not have a function associated with it.
// I also have to take special care with uninterned symbols, and since
// I do not have unlimited space in the opcode map I do not manage to
// optimise these two cases at the same time.
            boolean undefined = false;
// NB gensyms are handled as a derived class so do not need attention here
            if (fn instanceof Undefined &&
                LispReader.repeatedObjects.get(fn) == null)
            {   putPrefix2(length, X_UNDEFn, X_UNDEF);
                undefined = true;
            }
            else putPrefix2(length, X_SYMn, X_SYM);
            for (int i=0; i<length; i++)
                Jlisp.odump.write(rep[i]);
            if (Jlisp.descendSymbols)        
            {   LispReader.stack.push(car/*value*/);
                LispReader.stack.push(cdr/*plist*/);
                LispReader.stack.push(special);
// If the symbol had a non-trivial function-definition then that will
// be dumped straight after the symbol-header/pname info. Otherwise I
// skip and start with the "special-function" info which will usually be
// empty.
                if (!undefined) LispReader.stack.push(fn);
            }
            else Fasl.recent[Fasl.recentp++ & 0x1ff] = this;
        }
    }
        
}

// End of Symbol.java

