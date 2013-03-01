/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mathpiper.mpreduce;

import geogebra.common.main.App;

import java.io.IOException;
import java.math.BigInteger;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import org.mathpiper.mpreduce.datatypes.Cons;
import org.mathpiper.mpreduce.datatypes.LispEqualHash;
import org.mathpiper.mpreduce.datatypes.LispHash;
import org.mathpiper.mpreduce.datatypes.LispString;
import org.mathpiper.mpreduce.datatypes.LispVector;
import org.mathpiper.mpreduce.exceptions.EOFException;
import org.mathpiper.mpreduce.exceptions.ResourceException;
import org.mathpiper.mpreduce.functions.builtin.Fns;
import org.mathpiper.mpreduce.functions.functionwithenvironment.ByteOpt;
import org.mathpiper.mpreduce.functions.functionwithenvironment.Bytecode;
import org.mathpiper.mpreduce.functions.functionwithenvironment.FnWithEnv;
import org.mathpiper.mpreduce.functions.lisp.AutoLoad;
import org.mathpiper.mpreduce.functions.lisp.CallAs;
import org.mathpiper.mpreduce.functions.lisp.Interpreted;
import org.mathpiper.mpreduce.functions.lisp.LispFunction;
import org.mathpiper.mpreduce.functions.lisp.Macro;
import org.mathpiper.mpreduce.functions.lisp.Undefined;
import org.mathpiper.mpreduce.io.Fasl;
import org.mathpiper.mpreduce.io.streams.LispStream;
import org.mathpiper.mpreduce.numbers.LispFloat;
import org.mathpiper.mpreduce.numbers.LispInteger;
import org.mathpiper.mpreduce.special.SpecialFunction;
import org.mathpiper.mpreduce.symbols.Gensym;
import org.mathpiper.mpreduce.symbols.Symbol;

import com.google.gwt.core.client.Scheduler.RepeatingCommand;

public class LispReader implements RepeatingCommand {

    private static LispReader lispReader = null;
    static int istacklimit;
    static int[] istack;
    public static int sharedIndex;
    public static Stack stack;
    static int sharedSize;
    static LispObject[] shared;
    // I choose my initial oblist size so that REDUCE can run without need
    // for re-hashing at all often. The size must also be a prime, and 15013
    // seems to fit the bill.
    public static int oblistSize = 15013;
    public static int oblistCount = 0;
    public static Symbol[] oblist = new Symbol[oblistSize];
    public static LispVector obvector = new LispVector(oblist);
    public static Symbol[] chars = new Symbol[128];  // to speed up READCH
    public static LispObject[] spine = new LispObject[17]; // for PRESERVE
    static int inputType;
    public static HashSet objects;
    public static HashMap repeatedObjects;
    static final int S_VECTOR = 0;      // + number of items to come
    static final int S_START = -1;
    static final int S_CDR = -2;
    static final int S_HASHKEY = -3;
    static final int S_HASHVAL = -4;
    static final int S_SYMVAL = -5;
    static final int S_SYMPLIST = -6;
    static final int S_SYMFN = -7;
    static final int S_SYMSPECIAL = -8;
    static final int S_AUTONAME = -9;
    static final int S_AUTODATA = -10;
    static final int S_INTERP_BODY = -11;
    static final int S_MACRO_BODY = -12;
    static final int S_CALLAS_BODY = -13;
    static final int S_CADR = -100;  // +0 to +15 offsets from this used

    private LispReader() {
        super();
    }

    public static LispReader getInstance() {
        if (lispReader == null) {
            lispReader = new LispReader();
        }

        return lispReader;
    }
    private int state = S_START;
    private int sp = 0;
    private LispObject w = null;
    private boolean setLabel = false;
    private int index;

    public void readObjectReset() {
        state = S_START;
        sp = 0;
        w = null;
        setLabel = false;

    }

    public LispObject readObject() throws IOException, ResourceException {
        readObjectReset();

        while (readObjectIncrement() == true) {
        }
        return w;
    }

    public boolean readObjectIncrement() throws IOException, ResourceException {
        // Reloading an image uses an explicit stack to manage the recusion that
        // it needs. It controls this stack using a finite-state control. The states
        // are identified here as constants S_xxx.

        for (;;) {
            if (sp >= istacklimit - 2) // grow integer stack if needbe.
            {
                int[] newistack = new int[2 * istacklimit];
                for (index = 0; index < istacklimit; index++) {
                    newistack[index] = istack[index];
                }
                istack = newistack;
                istacklimit = 2 * istacklimit;
            }
            // At the start of the loop here I will read another object. I "continue"
            // if the object can not be completed all at once, having adjusted my
            // state and the stack suitably.
            int opcode = Jlisp.idump.read();
            if (opcode == -1) {
                throw new IOException("End of file");
            }
            int operand = 0;
            if (opcode < LispObject.X_BREAK1) {
                operand = opcode & 0x3f;
                opcode &= ~0x3f;
            } else if (opcode < LispObject.X_BREAK2) {
                operand = opcode & 0x0f;
                opcode &= ~0x0f;
            } else if (opcode < LispObject.X_BREAK3) {
                // The first class of opcodes have a selector in their bottom two bits,
                // and that indicates whether they are followed by 1, 2, 3 or 4 bytes
                // of operand.
                switch (opcode & 3) {
                    case 0:
                        operand = Jlisp.idump.read();
                        break;
                    case 1:
                        operand = Jlisp.idump.read();
                        operand = (operand << 8) | Jlisp.idump.read();
                        break;
                    case 2:
                        operand = Jlisp.idump.read();
                        operand = (operand << 8) | Jlisp.idump.read();
                        operand = (operand << 8) | Jlisp.idump.read();
                        break;
                    case 3:
                        operand = Jlisp.idump.read();
                        operand = (operand << 8) | Jlisp.idump.read();
                        operand = (operand << 8) | Jlisp.idump.read();
                        operand = (operand << 8) | Jlisp.idump.read();
                        break;
                }
                opcode &= ~3;
            }
            // Other cases do not have an (explicit) operand.
            switch (opcode) {
                case LispObject.X_REFn:
                    if (operand >= 48) {
                        operand = sharedIndex - (operand + 1 - 48);
                    }
                case LispObject.X_REF:       // refer to an item that has already been read
                    w = shared[operand];
                    break;
                case LispObject.X_REFBACK:
                    w = shared[sharedIndex - operand];
                    break;
                case LispObject.X_RECENT:
                    Fasl.recentn++;
                    w = Fasl.recent[Jlisp.idump.read()];
                    if (setLabel) {
                        shared[sharedIndex++] = w;
                        setLabel = false;
                    }
                    break;
                case LispObject.X_RECENT1:
                    Fasl.recentn++;
                    w = Fasl.recent[Jlisp.idump.read() + 256];
                    if (setLabel) {
                        shared[sharedIndex++] = w;
                        setLabel = false;
                    }
                    break;
                case LispObject.X_OBLIST:
                    w = obvector;
                    break;
                case LispObject.X_INT:       // a LispInteger
                case LispObject.X_INTn: {
                    byte[] data = new byte[operand];
                    for (index = 0; index < operand; index++) {
                        data[index] = (byte) Jlisp.idump.read();
                    }
                    w = LispInteger.valueOf(new BigInteger(data));
                }
                break;
                case LispObject.X_FIXNUM:
                    // Slighly curious encoding of signed numbers so that the variable-length
                    // packing in the image file works well.
                    if ((operand & 1) == 0) {
                        operand = (operand >>> 1);
                    } else if (operand == 1) {
                        operand = 0x80000000;
                    } else {
                        operand = -(operand >>> 1);
                    }
                    w = LispInteger.valueOf(operand);
                    break;
                case LispObject.X_STR:
                case LispObject.X_STRn: {
                    byte[] data = new byte[operand];
                    for (index = 0; index < operand; index++) {
                        data[index] = (byte) Jlisp.idump.read();
                    }
                    w = new LispString(new String(data));
                    LispString.stringCount++;
                }
                break;
                case LispObject.X_GENSYM:
                case LispObject.X_GENSYMn: {
                    byte[] data = new byte[operand];
                    for (index = 0; index < operand; index++) {
                        data[index] = (byte) Jlisp.idump.read();
                    }
                    int sequence = Jlisp.idump.read();
                    sequence = sequence | (Jlisp.idump.read() << 8);
                    sequence = sequence | (Jlisp.idump.read() << 16);
                    sequence = sequence | (Jlisp.idump.read() << 24);
                    Gensym ws = new Gensym(new String(data));
                    ws.myNumber = sequence;
                    if (sequence != -1) {
                        ws.pname = ws.nameBase + sequence;
                    }
                    Symbol.symbolCount++;
                    if (setLabel) {
                        shared[sharedIndex++] = ws;
                        setLabel = false;
                    }
                    if (!Environment.descendSymbols) {
                        ws.car/*value*/ = Environment.lit[Lit.undefined];
                        ws.cdr/*plist*/ = Environment.nil;
                        if (ws.pname != null) {
                            ws.fn = new Undefined(ws.pname);
                        } else {
                            ws.fn = new Undefined(ws.nameBase);
                        }
                        ws.special = null;
                        w = ws;
                        break;
                    }
                    stack.push(ws);
                    istack[sp++] = state;
                    state = S_SYMFN;
                    continue;
                }
                case LispObject.X_SYM:
                    opcode = LispObject.X_SYMn; // drop through
                case LispObject.X_SYMn:
                case LispObject.X_UNDEF:
                case LispObject.X_UNDEFn: {
                    byte[] data = new byte[operand];
                    for (index = 0; index < operand; index++) {
                        data[index] = (byte) Jlisp.idump.read();
                    }
                    if (Environment.descendSymbols) {
                        Symbol ws = new Symbol();
                        Symbol.symbolCount++;
                        ws.pname = new String(data);
                        stack.push(ws);
                        istack[sp++] = state;
                        if (opcode == LispObject.X_SYMn) {
                            state = S_SYMFN;
                        } else {
                            ws.fn = new Undefined(ws.pname);
                            state = S_SYMSPECIAL;
                        }
                        if (setLabel) {
                            shared[sharedIndex++] = ws;
                            setLabel = false;
                        }
                        continue;
                    } else {
                        w = Symbol.intern(new String(data));
                        Fasl.recent[Fasl.recentp++ & 0x1ff] = w;
                        break;
                    }
                }
                case LispObject.X_VEC:
                    w = new LispVector(operand);
                    if (setLabel) {
                        shared[sharedIndex++] = w;
                        setLabel = false;
                    }
                    if (operand == 0) {
                        break;  // vector with 0 elements
                    }
                    stack.push(w);
                    istack[sp++] = state;
                    state = S_VECTOR + operand;
                    continue;
                case LispObject.X_HASH:
                    w = new LispHash(new HashMap(), 0);
                    stack.push(w);
                    istack[sp++] = state;
                    state = S_HASHKEY;
                    if (setLabel) {
                        shared[sharedIndex++] = w;
                        setLabel = false;
                    }
                    continue;
                case LispObject.X_HASH2:
                    w = new LispHash(new LispEqualHash(), 2);
                    stack.push(w);
                    istack[sp++] = state;
                    state = S_HASHKEY;
                    if (setLabel) {
                        shared[sharedIndex++] = w;
                        setLabel = false;
                    }
                    continue;
                case LispObject.X_ENDHASH:
                    w = null;          // marker for end of hash table entries
                    break;
                case LispObject.X_UNDEF1: {
                    byte[] data = new byte[operand];
                    for (index = 0; index < operand; index++) {
                        data[index] = (byte) Jlisp.idump.read();
                    }
                    w = new Undefined(new String(data));
                }
                break;
                case LispObject.X_MACRO: {
                    Macro wm = new Macro();
                    if (setLabel) {
                        shared[sharedIndex++] = wm;
                        setLabel = false;
                    }
                    stack.push(wm);
                    istack[sp++] = state;
                    state = S_MACRO_BODY;
                }
                continue;
                case LispObject.X_AUTOLOAD: {
                    AutoLoad wa = new AutoLoad(null, null);
                    if (setLabel) {
                        shared[sharedIndex++] = wa;
                        setLabel = false;
                    }
                    stack.push(wa);
                    istack[sp++] = state;
                    state = S_AUTONAME;
                    continue;
                }
                case LispObject.X_INTERP: {
                    Interpreted wi = new Interpreted();
                    if (setLabel) {
                        shared[sharedIndex++] = wi;
                        setLabel = false;
                    }
                    stack.push(wi);
                    istack[sp++] = state;
                    state = S_INTERP_BODY;
                    continue;
                }
                case LispObject.X_CALLAS: {
                    CallAs wi = new CallAs(Jlisp.idump.read());
                    if (setLabel) {
                        shared[sharedIndex++] = wi;
                        setLabel = false;
                    }
                    stack.push(wi);
                    istack[sp++] = state;
                    state = S_CALLAS_BODY;
                    continue;
                }
                case LispObject.X_BPS: {
                    byte[] data;
                    int nargs = 0;
                    int n1 = Jlisp.idump.read(), n2 = 0, n3 = 0;
                    if ((n1 & 0x80) != 0) {
                        n1 &= 0x7f;
                        n2 = Jlisp.idump.read();
                        if ((n2 & 0x80) != 0) {
                            n2 &= 0x7f;
                            n3 = Jlisp.idump.read();
                        }
                    }
                    nargs = n1 + (n2 << 7) + (n3 << 14);
                    if (operand == 0) {
                        data = null;
                    } else {
                        data = new byte[operand];
                        for (index = 0; index < operand; index++) {
                            data[index] = (byte) Jlisp.idump.read();
                        }
                    }
                    FnWithEnv ws;
                    if (nargs > 0xff) {
                        ws = new ByteOpt(nargs);
                    } else {
                        ws = new Bytecode();
                        ws.nargs = nargs;
                    }
                    ws.bytecodes = data;
                    // the X_BPS format is curious in that it should ALWAYS be followed
                    // by an X_VEC. So I look for that here. I think I should also note that
                    // I have a fragment of design here that is not fully worked through.
                    // My Bytecoded is a sub-class of FnWithEnv - a general class for functions
                    // that want a vector of LispObjects kept with them. But at present
                    // Bytecode is the only sub-class that exists and the only one that this
                    // rea-loading code can ever re-create.  So I expect to have to do more
                    // work when or if I add more, for instance for code that has been reduced
                    // to real Jaba bytecodes rather than my Jlisp-specific ones.
                    opcode = Jlisp.idump.read();
                    if (opcode < LispObject.X_VEC || opcode > LispObject.X_VEC + 3) {
                        throw new IOException("Corrupted image file");
                    }
                    switch (opcode & 3) {
                        case 0:
                            operand = Jlisp.idump.read();
                            break;
                        case 1:
                            operand = Jlisp.idump.read();
                            operand = (operand << 8) | Jlisp.idump.read();
                            break;
                        case 2:
                            operand = Jlisp.idump.read();
                            operand = (operand << 8) | Jlisp.idump.read();
                            operand = (operand << 8) | Jlisp.idump.read();
                            break;
                        case 3:
                            operand = Jlisp.idump.read();
                            operand = (operand << 8) | Jlisp.idump.read();
                            operand = (operand << 8) | Jlisp.idump.read();
                            operand = (operand << 8) | Jlisp.idump.read();
                            break;
                    }
                    ws.env = new LispObject[operand];
                    if (operand == 0) {
                        w = ws;
                        break;
                    }
                    stack.push(ws);
                    istack[sp++] = state;
                    state = S_VECTOR + operand;
                    continue;
                }
                case LispObject.X_LIST:
                    w = Environment.nil;
                    if (operand == 0) {
                        break;
                    }
                    for (index = 0; index < operand; index++) {
                        w = new Cons(Environment.nil, w);
                    }
                    //Cons.consCount += operand;
                    if (setLabel) {
                        shared[sharedIndex++] = w;
                        setLabel = false;
                    }
                    stack.push(w);
                    istack[sp++] = state;
                    state = S_CADR + operand;
                    continue;
                case LispObject.X_LISTX:
                    w = new Cons(Environment.nil, Environment.nil); {
                    LispObject w1 = w;
                    for (index = 0; index < operand; index++) {
                        w = new Cons(Environment.nil, w);
                    }
                    //Cons.consCount += operand+1;
                    if (setLabel) {
                        shared[sharedIndex++] = w;
                        setLabel = false;
                    }
                    stack.push(w);
                    istack[sp++] = state;
                    state = S_CADR + operand + 1;
                    stack.push(w1);
                    // I will fill in the very tail and then drop back to
                    // the case used with X_LIST
                    istack[sp++] = state;
                    state = S_CDR;
                    continue;
                }
                case LispObject.X_NULL:
                    w = null;
                    break;
                case LispObject.X_DOUBLE: {
                    long v = Jlisp.idump.read();
                    for (index = 0; index < 7; index++) {
                        v = (v << 8) | Jlisp.idump.read();
                    }
                    w = new LispFloat(Fns.longBitsToDouble(v));
                }
                break;
                case LispObject.X_SPID:
                    w = new Spid(Jlisp.idump.read());
                    break;
                case LispObject.X_DEFINMOD: // This case is ONLY expected to be present in FASL modules, and it is a
                // prefix indicating what to do with some subsequent stuff.
                {
                    int n0 = Jlisp.idump.read(), n1 = 0, n2 = 0;
                    if ((n0 & 0x80) != 0) {
                        n0 &= 0x7f;
                        n1 = Jlisp.idump.read();
                        if ((n1 & 0x80) != 0) {
                            n1 &= 0x7f;
                            n2 = Jlisp.idump.read();
                        }
                    }
                    n0 = n0 + (n1 << 7) + (n2 << 14);
                    // That has read in a 22-bit number. Actually only 18 bits are really needed
                    // in the CSL byte-compiler model so I have some spare capacity. I offset
                    // values by 1 so I can represent "-1" too.
                    w = new Spid(Spid.DEFINMOD, n0 - 1);
                }
                break;
                case LispObject.X_STREAM:
                    w = Environment.nil;       // new LispStream();
                    break;
                case LispObject.X_FNAME:
                    operand = Jlisp.idump.read(); {
                    byte[] data = new byte[operand];
                    for (index = 0; index < operand; index++) {
                        data[index] = (byte) Jlisp.idump.read();
                    }
                    String s = new String(data);
                    w = (LispObject) Jlisp.builtinFunctions.get(s);
                    if (w == null) {
                    	if (Jlisp.lispErr != null)
                    		Jlisp.lispErr.println(s + " not found");
                    	else
                    		App.error("lispErr is null, " + s + " not found");
                    }
                }
                break;
                case LispObject.X_SPECFN:
                    operand = Jlisp.idump.read(); {
                    byte[] data = new byte[operand];
                    for (index = 0; index < operand; index++) {
                        data[index] = (byte) Jlisp.idump.read();
                    }
                    String s = new String(data);
                    w = (LispObject) Jlisp.builtinSpecials.get(s);
                    if (w == null) {
                        Jlisp.lispErr.println(s + " not found");
                    }
                }
                break;
                case LispObject.X_STORE:
                    setLabel = true;
                    continue;
                default:
                    throw new IOException("Bad byte in image file");
            }
            // For objects that were read all in one gulp I arrive here and must
            // impose sharing.
            if (setLabel) {
                shared[sharedIndex++] = w;
                setLabel = false;
            }
            // Now I have read in an object (it is in w) so I need to consider what to
            // do with it! It may be that processing this object will complete another
            // whose actions had been stacked, so I have a loop here which unwinds
            // the stack. If I "break" that will take me back to where the next item
            // gets read.
            for (;;) {
                LispObject y = (LispObject) stack.peek();
                if (state > S_VECTOR) {
                    if (y instanceof LispVector) {
                        ((LispVector) y).vec[--state - S_VECTOR] = w;
                    } else if (y instanceof FnWithEnv) {
                        ((FnWithEnv) y).env[--state - S_VECTOR] = w;
                    } else {
                        throw new IOException("Corrupt image file");
                    }
                    if (state == S_VECTOR) // now completed?
                    {
                        if (y instanceof LispVector) {
                            stack.pop();
                            w = y;
                            state = istack[--sp];
                            continue;
                        } else if (y instanceof FnWithEnv) {
                            stack.pop();
                            w = y;
                            state = istack[--sp];
                            continue;
                        }
                    } else {
                        break;
                    }
                } else {
                    switch (state) {
                        case S_START:
                            return false;
                        case S_CADR + 16:
                            y = y.cdr;
                        case S_CADR + 15:
                            y = y.cdr;
                        case S_CADR + 14:
                            y = y.cdr;
                        case S_CADR + 13:
                            y = y.cdr;
                        case S_CADR + 12:
                            y = y.cdr;
                        case S_CADR + 11:
                            y = y.cdr;
                        case S_CADR + 10:
                            y = y.cdr;
                        case S_CADR + 9:
                            y = y.cdr;
                        case S_CADR + 8:
                            y = y.cdr;
                        case S_CADR + 7:
                            y = y.cdr;
                        case S_CADR + 6:
                            y = y.cdr;
                        case S_CADR + 5:
                            y = y.cdr;
                        case S_CADR + 4:
                            y = y.cdr;
                        case S_CADR + 3:
                            y = y.cdr;
                        case S_CADR + 2:
                            y = y.cdr;
                            y.car = w;
                            state--;
                            break;
                        case S_CADR + 1:
                            y.car = w;
                            w = (LispObject) stack.pop();
                            state = istack[--sp];
                            continue;
                        case S_CDR: {
                            Cons wc = (Cons) stack.pop();
                            wc.cdr = w;
                            state = istack[--sp];  // will be S_CADR+nn
                        }
                        break;
                        case S_HASHKEY:
                            if (w == null) // hash table now complete
                            {
                                w = (LispObject) stack.pop();
                                state = istack[--sp];
                                continue;
                            }
                            stack.push(w);
                            state = S_HASHVAL;
                            break;
                        case S_HASHVAL: {
                            LispObject k = (LispObject) stack.pop();
                            LispHash h = (LispHash) stack.peek();
                            h.hash.put(k, w);
                        }
                        state = S_HASHKEY;
                        break;
                        case S_SYMFN: {
                            Symbol ws = (Symbol) stack.peek();
                            ws.fn = (LispFunction) w;
                            state = S_SYMSPECIAL;
                            break;
                        }
                        case S_SYMSPECIAL: {
                            Symbol ws = (Symbol) stack.peek();
                            ws.special = (SpecialFunction) w;
                            state = S_SYMPLIST;
                            break;
                        }
                        case S_SYMPLIST: {
                            Symbol ws = (Symbol) stack.peek();
                            ws.cdr/*plist*/ = w;
                            state = S_SYMVAL;
                            break;
                        }
                        case S_SYMVAL: {
                            Symbol ws = (Symbol) stack.pop();
                            ws.car/*value*/ = w;
                            w = ws;
                            state = istack[--sp];
                            continue;
                        }
                        case S_AUTONAME: {
                            AutoLoad wa = (AutoLoad) stack.peek();
                            wa.name = (Symbol) w;
                            state = S_AUTODATA;
                            break;
                        }
                        case S_AUTODATA: {
                            AutoLoad wa = (AutoLoad) stack.pop();
                            wa.data = w;
                            w = wa;
                            state = istack[--sp];
                            continue;
                        }
                        case S_INTERP_BODY: {
                            Interpreted wa = (Interpreted) stack.pop();
                            wa.body = w;
                            w = wa;
                            state = istack[--sp];
                            continue;
                        }
                        case S_MACRO_BODY: {
                            Macro wa = (Macro) stack.pop();
                            wa.body = w;
                            w = wa;
                            state = istack[--sp];
                            continue;
                        }
                        case S_CALLAS_BODY: {
                            CallAs wa = (CallAs) stack.pop();
                            wa.body = w;
                            w = wa;
                            state = istack[--sp];
                            continue;
                        }
                        default:
                            Jlisp.lispIO.println("Unknown state");
                            throw new IOException("Malformed image file (bad state)");
                    }
                }
                break;    // so "break" in the switch corresponds to
                // requesting a SHIFT, while "continue" is a REDUCE.
            }//end for.

            break;
        }//end for.

        return true;
    }//end method.
    //===================================================================================================================================
    // read a single parenthesised expression.
    // Supports  'xx as a short-hand for (quote xx)
    // which is what most Lisps do.
    // Formal syntax:
    //    read => SYMBOL | NUMBER | STRING
    //         => ' read
    //         => ` read
    //         => , read
    //         => ,@ read
    //         => ( tail
    //    tail => )
    //         => . read )
    //         => read readtail
    static LispStream readIn;

    public LispObject read() throws Exception {
        LispObject r;
        r = Environment.lit[Lit.std_input].car/*value*/;
        if (r instanceof LispStream) {
            readIn = (LispStream) r;
        } else {
            throw new EOFException();
        }
        if (!readIn.inputValid) {
            inputType = readIn.nextToken();
            readIn.inputValid = true;
        }
        switch (inputType) {
            case LispStream.TT_EOF:
                throw new EOFException();
            case LispStream.TT_WORD:
                readIn.inputValid = false;
                return readIn.value;
            //case LispStream.TT_NUMBER:
            //readIn.inputValid = false;
            //return readIn.value;
            //case '\"':  // String
            //r = new LispString(readIn.sval);
            //readIn.inputValid = false;
            //return r;
            case '\'':
                readIn.inputValid = false;
                r = read();
                return new Cons(Environment.lit[Lit.quote], new Cons(r, Environment.nil));
            case '`':
                readIn.inputValid = false;
                r = read();
                return expandBackquote(r);
            case ',':
                readIn.inputValid = false;
                r = read();
                return new Cons(Environment.lit[Lit.comma], new Cons(r, Environment.nil));
            case 0x10000:  // ",@"
                readIn.inputValid = false;
                r = read();
                return new Cons(Environment.lit[Lit.commaAt], new Cons(r, Environment.nil));
            case '(':
                readIn.inputValid = false;
                return readTail();
            case ')':
            case '.':
                readIn.inputValid = false;
                return Environment.nil;
            default:
                if (inputType < 128) {
                    r = chars[inputType];
                } else {
                    r = Symbol.intern(String.valueOf((char) inputType));
                }
                readIn.inputValid = false;
                return r;
        }
    }

    LispObject readTail() throws Exception {
        LispObject r;
        if (!readIn.inputValid) {
            inputType = readIn.nextToken();
            readIn.inputValid = true;
        }
        switch (inputType) {
            case '.':
                readIn.inputValid = false;
                r = read();
                if (!readIn.inputValid) {
                    inputType = readIn.nextToken();
                    readIn.inputValid = true;
                }
                if (inputType == ')') {
                    readIn.inputValid = false;
                }
                return r;
            case LispStream.TT_EOF:
                throw new EOFException();
            case ')':
                readIn.inputValid = false;
                return Environment.nil;
            default:
                r = read();
                return new Cons(r, readTail());
        }
    }

    LispObject expandBackquote(LispObject a) throws ResourceException {
        if (a == Environment.nil) {
            return a;
        } else if (a.atom) {
            return new Cons(Environment.lit[Lit.quote], new Cons(a, Environment.nil));
        }
        LispObject aa = a;
        if (aa.car == Environment.lit[Lit.comma]) {
            return aa.cdr.car;
        }
        if (!aa.car.atom) {
            LispObject aaa = aa.car;
            if (aaa.car == Environment.lit[Lit.commaAt]) {
                LispObject v = aaa.cdr.car;
                LispObject t = expandBackquote(aa.cdr);
                return new Cons(Environment.lit[Lit.append],
                        new Cons(v, new Cons(t, Environment.nil)));
            }
        }
        return new Cons(Environment.lit[Lit.cons],
                new Cons(expandBackquote(aa.car),
                new Cons(expandBackquote(aa.cdr), Environment.nil)));
    }

    public void preRestore() throws IOException {
        sharedIndex = 0;
        sharedSize = Jlisp.idump.read();
        sharedSize = (sharedSize << 8) + Jlisp.idump.read();
        sharedSize = (sharedSize << 8) + Jlisp.idump.read();
        shared = new LispObject[sharedSize];
        istacklimit = 500;
        istack = new int[istacklimit];
        stack = new Stack();
        stack.push(new Cons()); // to make "peek()" valid even when empty
    }

    public void postRestore() {
        istack = null;
        stack = null;
        shared = null;
    }
    private int loopIndex = 1;
    private int i = 0;

    boolean incrementalRestore() throws IOException, ResourceException {

        boolean returnValue = true;
        
        switch (loopIndex) {
            case 1:
                Environment.descendSymbols = true;
                // First I will read and display the banner...
                // I would like to be able to update JUST this banner in a heap image. To
                // support that I will (sometime!) change my heap format to put the
                // banner as an initial chunk of bytes in the PDS outside the compressed
                // data that represents the main heap image. One natural place to put it
                // will be as part of the directory entry for the initial image, and another
                // would be at the very start of the whole image file.
                int n;

                n = Jlisp.idump.read();
                n = (n << 8) + Jlisp.idump.read();
                n = (n << 8) + Jlisp.idump.read();
                if (n != 0) {
                    byte[] b = new byte[n];
                    for (i = 0; i < n; i++) {
                        b[i] = (byte) Jlisp.idump.read();
                    }
                    Jlisp.lispIO.println(new String(b));
                    Jlisp.lispIO.flush();
                }

                Environment.nil = (Symbol) readObject();

                Environment.lispTrue = (Symbol) readObject();
                App.debug(loopIndex);
                loopIndex++;

                break;

            case 2:
                readObjectReset();
                App.debug(loopIndex);
                loopIndex++;
                break;
            case 3:
                if (i < Lit.names.length) {
                    if (readObjectIncrement() == true) {
                        break;
                    } else {
                        Environment.lit[i] = w;

                        /*
                        System.out.println("literal " + i + " restored");
                        if (Jlisp.lit[i] instanceof Symbol) {
                            System.out.println("= " + ((Symbol) Jlisp.lit[i]).pname);
                        }
                         */
                        
                        i++;
                    }
                } else {
                	App.debug(loopIndex);
                    loopIndex++;
                }

                break;

            case 4:

                for (i = 0; i < oblistSize; i++) {
                    oblist[i] = null;
                }
                oblistCount = 0;



                // When restoring a heap image my oblist handling can be fairly
                // simple: I should NEVER get any attempt to insert an item that is already
                // there and I start with an empty table so there are no deleted
                // items to worry about.

                //System.out.println("termination of oblist found : " + oblistCount);
                App.debug(loopIndex);
                loopIndex++;

                break;

            default:
                returnValue = false;
                break;
        }//end switch;

        return returnValue;

    }//end method

    public void afterIncrementalRestore() throws Exception {
        LispObject w;

        if (Jlisp.idump.read() == 0) {
            Fns.prompt = null;
        } else {
            w = readObject();
            Fns.prompt = ((LispString) w).string;
        }

        w = readObject();
        try {
            Gensym.gensymCounter = w.intValue();
        } catch (Exception ee) {
            Gensym.gensymCounter = 0;
        }

        w = readObject();
        try {
            Environment.modulus = w.intValue();
        } catch (Exception ee) {
            Environment.modulus = 1;
        }
        Environment.bigModulus = BigInteger.valueOf(Environment.modulus);

        w = readObject();
        try {
            Environment.printprec = w.intValue();
        } catch (Exception ee) {
            Environment.printprec = 14;
        }


        postRestore();
    }//end method.

    private boolean readObjects() throws Exception {
        Symbol s;
        if ((s = (Symbol) readObject()) != null) {
            s.completeName();
            String name = s.pname;

            //Uncomment the following line of code to print the contents of the heap.
            //if (name.length() > 1) { System.out.println("restore symbol <" + name + "> length " + name.length()); }

            int inc = name.hashCode();
            //System.out.println("raw hash = " + Integer.toHexString(inc));
            // I want my hash addresses and the increment to be positive...
            // and Java tells me what the hash algorithm for strings is. What I do here
            // ensures that strings that differ only in their final character get placed
            // some multiple of 169 apart (is not quite adjacant).
            int hash = ((169 * inc) & 0x7fffffff) % oblistSize;
            inc = 1 + ((inc & 0x7fffffff) % (oblistSize - 1)); // never zero
            //System.out.println("first probe = " + hash + " " + inc);
            while (oblist[hash] != null) {
                if (oblist[hash].pname.equals(name)) {
                    System.out.println("Two symbols called <" + name + "> " + Integer.toHexString(name.charAt(0)));
                }
                hash += inc;
                if (hash >= oblistSize) {
                    hash -= oblistSize;
                }
                //System.out.println("next probe = " + hash);
            }
            //System.out.println("Put <" + name + "> at " + hash + " " + inc);
            oblist[hash] = s;
            oblistCount++;
            // I will permit the hash table loading to reach 0.75, but then I take action
            if (4 * oblistCount > 3 * oblistSize) {
                reHashOblist();
            }

            return true;
        }//end if.
        else {
            afterIncrementalRestore();

            return false;
        }
    }//end method.

    public boolean execute() {
        boolean continueFlag = false;

        try {
            continueFlag = readObjects();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return continueFlag;
        }
    }

    static boolean isPrime(int n) {
        // the input must be odd and fairly large here... so the case of even
        // numbers is not important, as is the status of the number 1.
        for (int f = 3; f * f <= n; f += 2) {
            if (n % f == 0) {
                return false;
            }
        }
        return true;
    }

    public static void reHashOblist() {
        int n = ((3 * oblistSize) / 2) | 1;
        while (!isPrime(n)) {
            n += 2;
        }
        Symbol[] v = new Symbol[n];
        for (int i = 0; i < n; i++) {
            v[i] = null;
        }
        for (int i = 0; i < oblistSize; i++) {
            Symbol s = oblist[i];
            if (s == null) {
                continue;
            }
            int inc = s.pname.hashCode();
            int hash = ((169 * inc) & 0x7fffffff) % n;
            inc = 1 + ((inc & 0x7fffffff) % (n - 1)); // never zero
            while (v[hash] != null) {
                if (v[hash].pname.equals(s.pname)) {
                    System.out.println("Two symbols called <" + s.pname + "> "
                            + Integer.toHexString(s.pname.charAt(0)));
                }
                hash += inc;
                if (hash >= n) {
                    hash -= n;
                }
            }
            //System.out.println("Relocate <" + s.pname + "> at " + hash + " " + inc);
            v[hash] = s;
        }
        oblist = v;
        oblistSize = n;
        obvector.vec = v;
    }

    public void scanObject(LispObject a) {
        if (a == null) {
            return;
        }
        stack.push(a);
        try // keep going until the stack empties.
        {
            for (;;) {
                LispObject w = (LispObject) stack.pop();
                w.scan();
            }
        } catch (EmptyStackException e) {
        }
    }
}//End class.

