package org.mathpiper.mpreduce.functions.builtin;

//
// This file is part of the Jlisp implementation of Standard Lisp
// Copyright \u00a9 (C) Codemist Ltd, 1998-2011.
//


// Fns3.java

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
// Each built-in function is created wrapped in a class
// that is derived from BuiltinFunction.





import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.mathpiper.mpreduce.Environment;
import org.mathpiper.mpreduce.functions.lisp.AutoLoad;
import org.mathpiper.mpreduce.functions.functionwithenvironment.ByteOpt;
import org.mathpiper.mpreduce.functions.functionwithenvironment.Bytecode;
import org.mathpiper.mpreduce.functions.lisp.CallAs;
import org.mathpiper.mpreduce.datatypes.Cons;
import org.mathpiper.mpreduce.exceptions.EOFException;
import org.mathpiper.mpreduce.io.Fasl;
import org.mathpiper.mpreduce.functions.functionwithenvironment.FnWithEnv;
import org.mathpiper.mpreduce.functions.lisp.Interpreted;
import org.mathpiper.mpreduce.Jlisp;
import org.mathpiper.mpreduce.io.streams.LispDigester;
import org.mathpiper.mpreduce.datatypes.LispEqualHash;
import org.mathpiper.mpreduce.functions.lisp.LispFunction;
import org.mathpiper.mpreduce.datatypes.LispHash;
import org.mathpiper.mpreduce.numbers.LispInteger;
import org.mathpiper.mpreduce.numbers.LispNumber;
import org.mathpiper.mpreduce.LispObject;
import org.mathpiper.mpreduce.LispReader;
import org.mathpiper.mpreduce.io.streams.LispOutputStream;
import org.mathpiper.mpreduce.numbers.LispSmallInteger;
import org.mathpiper.mpreduce.io.streams.LispStream;
import org.mathpiper.mpreduce.datatypes.LispString;
import org.mathpiper.mpreduce.datatypes.LispVector;
import org.mathpiper.mpreduce.Lit;
import org.mathpiper.mpreduce.functions.lisp.Macro;
import org.mathpiper.mpreduce.exceptions.ProgEvent;
import org.mathpiper.mpreduce.exceptions.ResourceException;
import org.mathpiper.mpreduce.special.Specfn;
import org.mathpiper.mpreduce.symbols.Symbol;
import org.mathpiper.mpreduce.functions.lisp.TracedFunction;
import org.mathpiper.mpreduce.functions.lisp.Undefined;

public class Fns3
{
    public Object [][] builtins =
    {
        {"liter",                       new LiterFn()},
        {"load-module",                 new Load_moduleFn()},
        {"lposn",                       new LposnFn()},
        {"macro-function",              new Macro_functionFn()},
        {"macroexpand",                 new MacroexpandFn()},
        {"macroexpand-1",               new Macroexpand_1Fn()},
        {"make-bps",                    new Make_bpsFn()},
        {"make-function-stream",        new Make_function_streamFn()},
        {"make-global",                 new Make_globalFn()},
        {"make-native",                 new Make_nativeFn()},
        {"make-random-state",           new Make_random_stateFn()},
        {"make-simple-string",          new Make_simple_stringFn()},
        {"make-special",                new Make_specialFn()},
        {"map",                         new MapFn()},
        {"mapc",                        new MapcFn()},
        {"mapcan",                      new MapcanFn()},
        {"mapcar",                      new MapcarFn()},
        {"mapcon",                      new MapconFn()},
        {"maphash",                     new MaphashFn()},
        {"maplist",                     new MaplistFn()},
        {"mapstore",                    new MapstoreFn()},
        {"md5",                         new Md5Fn()},
        {"md60",                        new Md60Fn()},
        {"member",                      new MemberFn()},
        {"member**",                    new MemberStarStarFn()},
        {"memq",                        new MemqFn()},
        {"mkevect",                     new MkevectFn()},
        {"mkfvect32",                   new Mkfvect32Fn()},
        {"mkfvect64",                   new Mkfvect64Fn()},
        {"mkhash",                      new MkhashFn()},
        {"mkquote",                     new MkquoteFn()},
        {"mkvect",                      new MkvectFn()},
        {"mkvect16",                    new Mkvect16Fn()},
        {"mkvect32",                    new Mkvect32Fn()},
        {"mkvect8",                     new Mkvect8Fn()},
        {"mkxvect",                     new MkxvectFn()},
        {"modulep",                     new ModulepFn()},
        {"native-address",              new Native_addressFn()},
        {"native-getv",                 new Native_getvFn()},
        {"native-putv",                 new Native_putvFn()},
        {"native-type",                 new Native_typeFn()},
        {"nconc",                       new NconcFn()},
        {"ncons",                       new NconsFn()},
        {"neq",                         new NeqFn()},
        {"noisy-setq",                  new Noisy_setqFn()},
        {"not",                         new NotFn()},
        {"null",                        new NullFn()},
        {"oblist",                      new OblistFn()},
        {"oem-supervisor",              new Oem_supervisorFn()},
        {"open",                        new OpenFn()},
        {"internal-open",               new InternalOpenFn()},
        {"open-library",                new Open_libraryFn()},
        {"open-url",                    new Open_urlFn()},
        {"orderp",                      new OrderpFn()},
        {"ordp",                        new OrderpFn()}, // synonym
        {"output-library",              new Output_libraryFn()},
        {"pagelength",                  new PagelengthFn()},
        {"pair",                        new PairFn()},
        {"pairp",                       new PairpFn()},
        {"peekch",                      new PeekchFn()},
        {"pipe-open",                   new Pipe_openFn()},
        {"plist",                       new PlistFn()},
        {"posn",                        new PosnFn()},
        {"preserve",                    new PreserveFn()},
        {"restart-csl",                 new RestartFn()},
        {"saveobject",                  new SaveObjectFn()},
        {"restoreobject",               new RestoreObjectFn()},
        {"prin",                        new PrinFn()},
        {"prin1",                       new Prin1Fn()},
        {"prin2",                       new Prin2Fn()},
        {"prin2a",                      new Prin2aFn()},
        {"prinbinary",                  new PrinbinaryFn()},
        {"princ",                       new PrincFn()},
        {"princ-downcase",              new Princ_downcaseFn()},
        {"princ-upcase",                new Princ_upcaseFn()},
        {"prinhex",                     new PrinhexFn()},
        {"prinoctal",                   new PrinoctalFn()},
        {"print",                       new PrintFn()},
        {"printc",                      new PrintcFn()},
        {"printprompt",                 new PrintpromptFn()},
        {"prog1",                       new Prog1Fn()},
        {"prog2",                       new Prog2Fn()},
        {"progn",                       new PrognFn()},
        {"put",                         new PutFn()},
        {"puthash",                     new PuthashFn()},
        {"putv",                        new PutvFn()},
        {"putv-char",                   new Putv_charFn()},
        {"putv16",                      new Putv16Fn()},
        {"putv32",                      new Putv32Fn()},
        {"putv8",                       new Putv8Fn()},
        {"qcaar",                       new QcaarFn()},
        {"qcadr",                       new QcadrFn()},
        {"qcar",                        new QcarFn()},
        {"qcdar",                       new QcdarFn()},
        {"qcddr",                       new QcddrFn()},
        {"qcdr",                        new QcdrFn()},
        {"qgetv",                       new QgetvFn()},
        {"qputv",                       new QputvFn()},
        {"rassoc",                      new RassocFn()},
        {"rdf",                         new RdfFn()},
        {"rds",                         new RdsFn()},
        {"read",                        new ReadFn()},
        {"readch",                      new ReadchFn()},
        {"readline",                    new ReadlineFn()},
        {"reclaim",                     new ReclaimFn()},
        {"remd",                        new RemdFn()},
        {"remflag",                     new RemflagFn()},
        {"remhash",                     new RemhashFn()},
        {"remob",                       new RemobFn()},
        {"remprop",                     new RempropFn()},
        {"rename-file",                 new Rename_fileFn()},
        {"representation",              new RepresentationFn()},
        {"return",                      new ReturnFn()},
        {"reverse",                     new ReverseFn()},
        {"reversip",                    new ReversipFn()},
        {"reversip2",                   new ReversipFn()},
        {"nreverse",                    new ReversipFn()},
        {"rplaca",                      new RplacaFn()},
        {"rplacd",                      new RplacdFn()},
        {"rplacw",                      new RplacwFn()},
        {"rseek",                       new RseekFn()},
        {"rtell",                       new RtellFn()},
        {"sample",                      new SampleFn()},
        {"sassoc",                      new SassocFn()},
        {"schar",                       new ScharFn()},
        {"seprp",                       new SeprpFn()},
        {"set",                         new SetFn()},
        {"set-autoload",                new Set_autoloadFn()},
        {"set-help-file",               new Set_help_fileFn()},
        {"set-print-precision",         new Set_print_precisionFn()},
        {"setprintprecision",           new Set_print_precisionFn()},
        {"getprintprecision",           new Get_print_precisionFn()},
        {"setpchar",                    new SetpcharFn()},
        {"simple-string-p",             new Simple_string_pFn()},
        {"simple-vector-p",             new Simple_vector_pFn()},
        {"smemq",                       new SmemqFn()},
        {"spaces",                      new SpacesFn()},
        {"special-char",                new Special_charFn()},
        {"special-form-p",              new Special_form_pFn()},
        {"spool",                       new SpoolFn()},
        {"start-module",                new Start_moduleFn()},
        {"stop",                        new StopFn()},
        {"streamp",                     new StreampFn()},
        {"stringp",                     new StringpFn()},
        {"stub1",                       new Stub1Fn()},
        {"stub2",                       new Stub2Fn()},
        {"subla",                       new SublaFn()},
        {"sublis",                      new SublisFn()},
        {"subst",                       new SubstFn()},
        {"substq",                      new SubstqFn()},
        {"sxhash",                      new SxhashFn()},
     // equalhash is NOT really sorted out yet since it ought not to
     // descend through vectors.
        {"equalhash",                   new SxhashFn()},        
        {"symbol-argcount",             new Symbol_argcountFn()},
        {"symbol-env",                  new Symbol_envFn()},
        {"symbol-fastgets",             new Symbol_fastgetsFn()},
        {"symbol-fn-cell",              new Symbol_fn_cellFn()},
        {"symbol-function",             new Symbol_functionFn()},
        {"symbol-make-fastget",         new Symbol_make_fastgetFn()},
        {"symbol-name",                 new Symbol_nameFn()},
        {"symbol-protect",              new Symbol_protectFn()},
        {"symbol-set-definition",       new Symbol_set_definitionFn()},
        {"symbol-set-env",              new Symbol_set_envFn()},
        {"symbol-set-native",           new Symbol_set_nativeFn()},
        {"symbol-value",                new Symbol_valueFn()},
        {"symbolp",                     new SymbolpFn()},
        {"symerr",                      new SymerrFn()},
        {"system",                      new SystemFn()},
        {"tagbody",                     new TagbodyFn()},
        {"terpri",                      new TerpriFn()},
        {"threevectorp",                new ThreevectorpFn()},
        {"throw",                       new ThrowFn()},
        {"time",                        new TimeFn()},
        {"tmpnam",                      new TmpnamFn()},
        {"trace",                       new TraceFn()},
        {"traceset",                    new TracesetFn()},
        {"traceset1",                   new Traceset1Fn()},
        {"ttab",                        new TtabFn()},
        {"tyo",                         new TyoFn()},
        {"undouble-execute",            new Undouble_executeFn()},
        {"unfluid",                     new UnfluidFn()},
        {"unglobal",                    new UnglobalFn()},
        {"union",                       new UnionFn()},
        {"unmake-global",               new Unmake_globalFn()},
        {"unmake-special",              new Unmake_specialFn()},
        {"unreadch",                    new UnreadchFn()},
        {"untrace",                     new UntraceFn()},
        {"untraceset",                  new UntracesetFn()},
        {"untraceset1",                 new Untraceset1Fn()},
        {"unwind-protect",              new Unwind_protectFn()},
        {"upbv",                        new UpbvFn()},
        {"user-homedir-pathname",       new User_homedir_pathnameFn()},
        {"vectorp",                     new VectorpFn()},
        {"verbos",                      new VerbosFn()},
        {"where-was-that",              new Where_was_thatFn()},
        {"window-heading",              new Window_headingFn()},
        {"startup-banner",              new Startup_bannerFn()},
        {"writable-libraryp",           new Writable_librarypFn()},
        {"write-help-module",           new Write_help_moduleFn()},
        {"write-module",                new Write_moduleFn()},
        {"wrs",                         new WrsFn()},
        {"xassoc",                      new XassocFn()},
        {"xcons",                       new XconsFn()},
        {"xdifference",                 new XdifferenceFn()},
        {"xtab",                        new XtabFn()},
        {"~tyi",                        new TyiFn()}
    };



class LiterFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (!(arg1 instanceof Symbol)) return Environment.nil;
        Symbol s = (Symbol)arg1;
        s.completeName();
        char ch = s.pname.charAt(0);
        if (Character.isLetter(ch)) return Jlisp.lispTrue;
        else return Environment.nil;
    }
}


class Load_moduleFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return Fasl.loadModule(arg1);
    }
}

class LposnFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Macro_functionFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (!(arg1 instanceof Symbol)) return Environment.nil;
        LispFunction fn = ((Symbol)arg1).fn;
        if (fn instanceof Macro)
        {   return ((Macro)fn).body;
        }
        else return Environment.nil;
    }
}

class MacroexpandFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return op2(arg1, null);
    }
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        for (;;)
        {   if (arg1.atom) return arg1;
            if (!(arg1.car instanceof Symbol)) return arg1;
            Symbol f = (Symbol)arg1.car;
            LispFunction fn = f.fn;
            if (!(fn instanceof Macro)) return arg1;
// At last - here I have a macro that I can expand
            arg1 = fn.op1(arg1);
        }
    }
}

class Macroexpand_1Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return op2(arg1, null);
    }
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        if (arg1.atom) return arg1;
        if (!(arg1.car instanceof Symbol)) return arg1;
        Symbol f = (Symbol)arg1.car;
        LispFunction fn = f.fn;
        if (!(fn instanceof Macro)) return arg1;
// At last - here I have a macro that I can expand
        return fn.op1(arg1);
    }
}

class Make_bpsFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        int n = ((LispSmallInteger)arg1).value;
        return new Bytecode(n);
    }
}

class Make_function_streamFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Make_globalFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws ResourceException
    {
        Symbol s = (Symbol)arg1;
        Fns.put(s, Jlisp.lit[Lit.global], Jlisp.lispTrue);
        if (s.car/*value*/ == Jlisp.lit[Lit.undefined]) s.car/*value*/ = Environment.nil;
        return Environment.nil; 
    }
}

class Make_nativeFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Make_random_stateFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Make_simple_stringFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        int n = ((LispSmallInteger)arg1).value;
        char [] c = new char[n];
        for (int i=0; i<n; i++) c[i] = (char)0;
        return new LispString(new String(c));
    }
}

class Make_specialFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws ResourceException
    {
        Symbol s = (Symbol)arg1;
        Fns.put(s, Jlisp.lit[Lit.special], Jlisp.lispTrue);
        if (s.car/*value*/ == Jlisp.lit[Lit.undefined]) s.car/*value*/ = Environment.nil;
        return Environment.nil; 
    }
}

class MapFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class MapcFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class MapcanFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class MapcarFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class MapconFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class MaphashFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class MaplistFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class MapstoreFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        Jlisp.println();
        Jlisp.println("*** MAPSTORE ***");
        return Environment.nil;
    }
}

class Md5Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        LispStream f = new LispDigester();
        LispObject save = Jlisp.lit[Lit.std_output].car/*value*/;
        try
        {   Jlisp.lit[Lit.std_output].car/*value*/ = f;
            arg1.print(LispObject.noLineBreak+LispObject.printEscape);
        }
        finally
        {   Jlisp.lit[Lit.std_output].car/*value*/ = save;
        }
        byte [] res = f.md.digest();
        return LispInteger.valueOf(new BigInteger(res));
    }
}

class Md60Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        LispStream f = new LispDigester();
        LispObject save = Jlisp.lit[Lit.std_output].car/*value*/;
        try
        {   Jlisp.lit[Lit.std_output].car/*value*/ = f;
            arg1.print(LispObject.noLineBreak+LispObject.printEscape);
        }
        finally
        {   Jlisp.lit[Lit.std_output].car/*value*/ = save;
        }
        byte [] res = f.md.digest();
        return LispInteger.valueOf(new BigInteger(res).shiftRight(68));
    }
}


class MemberFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2)
    {
        while (!arg2.atom)
        {   if (arg1.lispequals(arg2.car)) return arg2;
            arg2 = arg2.cdr;
        }
        return Environment.nil;
    }
}

class MemberStarStarFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2)
    {
        while (!arg2.atom)
        {   if (arg1.lispequals(arg2.car)) return arg2;
            arg2 = arg2.cdr;
        }
        return Environment.nil;
    }
}

class MemqFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        while (!arg2.atom)
        {   if (arg1 instanceof LispNumber)                 // @@@
            {   if (arg1.lispequals(arg2.car)) return arg2; // @@@
            }                                               // @@@
            else if (arg1 == arg2.car) return arg2;
            arg2 = arg2.cdr;
        }
        return Environment.nil;
    }
}

class MkevectFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Mkfvect32Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Mkfvect64Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class MkhashFn extends BuiltinFunction
{
  // (MKHASH size flavour growth-ratio)
  //    size is initial table size
  //    flavour: 0  EQ
  //             1  EQL
  //             2  EQUAL
  //             3  EQUALS
  //             4  EQUALP
  //    ratio:   amount to expand by as table gets full
  //
  // In this Java version I will ignore the first and third args,
  // and only support EQ and EQUAL tables!  Note that an EQ table
  // will generally re-hash itself if serialized...

    public LispObject opn(LispObject [] args) throws Exception
    {
        if (args.length != 3)
            return error("mkhash called with " + args.length +
                "args when 3 expected");
        int n = ((LispSmallInteger)args[1]).value;
        HashMap h;
        if (n == 0) h = new HashMap();
        else h = new LispEqualHash();
        return new LispHash(h, n);
    }
}

class MkquoteFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return new Cons(Jlisp.lit[Lit.quote],
            new Cons(arg1, Environment.nil));
    }
}

class MkvectFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1)
    {
        int n = ((LispSmallInteger)arg1).value;
        return new LispVector(n+1); // Hah - index values from 0 to n
    }
}

class Mkvect16Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Mkvect32Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Mkvect8Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class MkxvectFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class ModulepFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        String s;
        if (arg1 instanceof Symbol)
        {   ((Symbol)arg1).completeName();
            s = ((Symbol)arg1).pname;
        }
        else if (arg1 instanceof LispString) s = ((LispString)arg1).string;
        else return error("illegal arg to modulep", arg1);
        s = s + ".fasl";
        for (int i=0; i<Jlisp.imageCount; i++)
        {   arg1 = Jlisp.images[i].modulep(s);
            if (arg1 != Environment.nil) return arg1;
        }
        return Environment.nil;
    }
}

class Native_addressFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Native_getvFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Native_putvFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Native_typeFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class NconcFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        if (arg1.atom) return arg2;
        LispObject r = arg1;
        LispObject prev = null;
        while (!arg1.atom)
        {   prev = arg1;
            arg1 = prev.cdr;
        }
        prev.cdr = arg2;
        return r;
    }
}

class NconsFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws ResourceException
    {
        return new Cons(arg1, Environment.nil);
    }
}

class NeqFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2)
    {
        if (arg1 == arg2) return Environment.nil;
        return arg1.lispequals(arg2) ? Environment.nil :
            Jlisp.lispTrue;
    }
}

class Noisy_setqFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class NotFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1)
    {
        return arg1 == Environment.nil ?
               Jlisp.lispTrue :
               Environment.nil;
    }
}

class NullFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1)
    {
        return arg1 == Environment.nil ?
               Jlisp.lispTrue :
               Environment.nil;
    }
}

class OblistFn extends BuiltinFunction
{
    public LispObject op0() throws ResourceException
    {
// Note that this implementation pushes out the object list with
// items in a randomish order. CSL sorted it which was nice - to do that
// here I would have to implement a sorting function, and as present that
// does not seem my highest priority.
        LispObject r = Environment.nil;
        for (int i=0; i<LispReader.oblistSize; i++)
        {   Symbol w = LispReader.oblist[i];
            if (w != null)
            {   if (w.car/*value*/ != Jlisp.lit[Lit.undefined] ||
                    w.cdr/*plist*/ != Environment.nil ||
                    w.special != null ||
                    !(w.fn instanceof Undefined))
                    r = new Cons(w, r);
            }
        }
        return r;
    }
}

class Oem_supervisorFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class OpenFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        if (!(arg1 instanceof LispString)) 
            return error("argument 1 to open must be a string");
        String name = ((LispString)arg1).string;
        if (arg2 == Jlisp.lit[Lit.input])
        {   LispObject r = Environment.nil;
            try
            {   r = new LispStream(
                    name,
                    new BufferedReader(
                        new FileReader(LispStream.nameConvert(name))),
                    false, true);
            }
            catch (FileNotFoundException e)
            {   return error("File " + name + " not found");
            }
            return r;
        }
        else if (arg2 == Jlisp.lit[Lit.output]) 
        {   LispObject r = Environment.nil;
            try
            {   r = new LispOutputStream(name);
            }
            catch (IOException e)
            {   return error("File " + name + " can not be opened for output");
            }
            return r;
        }
        else if (arg2 == Jlisp.lit[Lit.append]) 
        {   LispObject r = Environment.nil;
            try
            {   r = new LispOutputStream(name, true);
            }
            catch (IOException e)
            {   return error("File " + name + " can not be opened for output");
            }
            return r;
        }
        else return error(
            "argument 2 to open should be input, output or append");
    }
}


// The system-coded primitive function ~OPEN opens a file, and takes a second
// argument that shows what options are wanted. See extracts from the CSL
// file "print.c" (included just below this comment) for an explanation 
// of the bits.
//
// This stuff is here so I can be almost ridiculously compatible with CSL
// since that makes it easier to share files with that world...
//
//(de open (a b)
//   (cond
//     ((eq b 'input) (!~open a (plus 1 64)))     % if-does-not-exist error
//     ((eq b 'output) (!~open a (plus 2 20 32))) % if-does-not-exist create,
//                                                % if-exists new-version
//     ((eq b 'append) (!~open a (plus 2 8 32)))  % if-exists append
//     (t (error "bad direction ~A in open" b))))
//
//(de binopen (a b)
//   (cond
//     ((eq b 'input) (!~open a (plus 1 64 128)))
//     ((eq b 'output) (!~open a (plus 2 20 32 128)))
//     ((eq b 'append) (!~open a (plus 2 8 32 128)))
//     (t (error "bad direction ~A in binopen" b))))
//
//(de pipe!-open (c d)
//   (cond
//     ((eq d 'input) (!~open c (plus 1 256)))
//     ((eq d 'output) (!~open c (plus 2 256)))
//     (t (error "bad direction ~A in pipe-open" d))))
//


//
///*
// * The Common Lisp keywords for OPEN are a horrid mess. I arrange to decode
// * the syntax of the keywords in a Lisp-coded wrapper function, and in that
// * code I will also fill in default values for any that needs same. I then
// * pack all the information into a single integer, which has several
// * sub-fields
// *
// * x x xx xxx 00   direction PROBE
// * x x xx xxx 01             INPUT
// * x x xx xxx 10             OUTPUT
// * x x xx xxx 11             IO
// *
// * x x xx 000 xx   if-exists NIL
// * x x xx 001 xx             overwrite
// * x x xx 010 xx             append
// * x x xx 011 xx             rename
// * x x xx 100 xx             error
// * x x xx 101 xx             (new-version)
// * x x xx 110 xx             (supersede)
// * x x xx 111 xx             (rename-and-delete)
// *
// * x x 00 xxx xx   if-does-not-exist NIL
// * x x 01 xxx xx                     create
// * x x 10 xxx xx                     error
// *
// * x 0 xx xxx xx   regular text file
// * x 1 xx xxx xx   open for binary access
// *
// * 0 x xx xxx xx   regular file
// * 1 x xx xxx xx   open as a pipe
// */
//
//#define DIRECTION_MASK               0x3
//#define DIRECTION_PROBE              0x0
//#define DIRECTION_INPUT              0x1
//#define DIRECTION_OUTPUT             0x2
//#define DIRECTION_IO                 0x3
//#define IF_EXISTS_MASK               0x1c
//#define IF_EXISTS_NIL                0x00
//#define IF_EXISTS_OVERWRITE          0x04
//#define IF_EXISTS_APPEND             0x08
//#define IF_EXISTS_RENAME             0x0c
//#define IF_EXISTS_ERROR              0x10
//#define IF_EXISTS_NEW_VERSION        0x14
//#define IF_EXISTS_SUPERSEDE          0x18
//#define IF_EXISTS_RENAME_AND_DELETE  0x1c
//#define IF_MISSING_MASK              0x60
//#define IF_MISSING_NIL               0x00
//#define IF_MISSING_CREATE            0x20
//#define IF_MISSING_ERROR             0x40
//#define OPEN_BINARY                  0x80
//#define OPEN_PIPE                    0x100

class InternalOpenFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        if (!(arg1 instanceof LispString)) 
            return error("argument 1 to ~open must be a string");
        String name = ((LispString)arg1).string;
        int bits = ((LispSmallInteger)arg2).value;
        if ((bits & 0x100) != 0) return openPipe(name, bits);
        String localName = LispStream.nameConvert(name);
        File f = new File(localName);
        boolean x = f.exists();
        LispObject r;
        switch (bits & 3)
        {
    case 0: // probe
            if (x) return Jlisp.lispTrue;
            else return Environment.nil;
    case 1: // read
            if (!x)
            {   switch (bits & 0x60)
                {
            case 0x00: return Environment.nil;
            case 0x40: return Jlisp.error("File does not exist: " + name);
            default:   return Jlisp.error("File open mode unknown " +
                              Integer.toHexString(bits));
                }
            }
            r = Environment.nil;
            try
            {   r = new LispStream(
                    name,
                    new BufferedReader(
                        new FileReader(f)),
                    false, true);
            }
            catch (FileNotFoundException e) // should not happen!
            {   return error("File " + name + " not found");
            }
            return r;
    case 2: // write
            r = Environment.nil;
            try
            {   if (x)
                {   switch (bits & 0x1c)
                    {
                case 0x00: return Environment.nil;
                case 0x14: // new version: treat as overwrite...
                case 0x04: return new LispOutputStream(f);
// the "append" option seems to have to be opened based on a String not a File
                case 0x08: return new LispOutputStream(localName, true);
                case 0x10: return error("File already exists: " + name);
                default:   return error("Unsupported file open mode: " +
                                        Integer.toHexString(bits));
                    }
                }
                else r = new LispOutputStream(f);
            }
            catch (IOException e)
            {   return Environment.nil;
            }
            return r;
    case 3: // input and output
            return error("simultaneous input+output mode files not supported");
        }
        return Environment.nil;
    }

    public LispObject openPipe(String name, int bits) throws Exception
    {
        return error("pipes not supported by Java, it seems?");
    }

}

class Open_libraryFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Open_urlFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class OrderpFn extends BuiltinFunction
{
//  symbolic procedure ordp(u,v);
//     if null u then null v
//      else if null v then t
//      else if vectorp u then if vectorp v then ordpv(u,v) else atom v
//      else if atom u
//       then if atom v
//              then if numberp u then numberp v and not u<v
//                    else if idp v then orderp(u,v)
//                    else numberp v
//             else nil
//      else if atom v then t
//      else if car u=car v then ordp(cdr u,cdr v)
//      else if flagp(car u,'noncom)
//       then if flagp(car v,'noncom) then ordp(car u,car v) else t
//      else if flagp(car v,'noncom) then nil
//      else ordp(car u,car v);
//

    public LispObject op2(LispObject u, LispObject v) throws Exception
    {   if (ordp(u,v)) return Jlisp.lispTrue;
        else return Environment.nil;
    }

    boolean ordp(LispObject u, LispObject v) throws Exception
    {
        if (u == Environment.nil) return (v == Environment.nil);
        else if (v == Environment.nil) return true;
        else if (u instanceof LispVector)
        {  if (v instanceof LispVector) 
               return ordv((LispVector)u, (LispVector)v);
           else return v.atom;
        }
        else if (u.atom)
        {   if (v.atom)
            {   if (u instanceof LispNumber)
                {   if (!(v instanceof LispNumber)) return false;
                    return (Fns.lessp(u, v) == Environment.nil);
                }
                else if (v instanceof Symbol)
                {   if (!(u instanceof Symbol)) return false;
                    ((Symbol)u).completeName();
                    ((Symbol)v).completeName();
                    return ((Symbol)u).pname.compareTo(
                        ((Symbol)v).pname) <= 0;
                }
                else return (v instanceof LispNumber);
            }
            else return false;
        }
        else if (v.atom) return true;
        LispObject cu = u, cv = v;
        LispObject caru = cu.car, carv = cv.car;
        if (caru.lispequals(carv))
            return ordp(cu.cdr, cv.cdr);
        else if (Fns.get(caru, Jlisp.lit[Lit.noncom]) !=
                 Environment.nil)
        {   if (Fns.get(carv, Jlisp.lit[Lit.noncom]) !=
                Environment.nil)
                return ordp(caru, carv);
            else return true;
        }
        else if (Fns.get(carv, Jlisp.lit[Lit.noncom]) !=
                 Environment.nil)
            return false;
        else return ordp(caru, carv);
    }

    boolean ordv(LispVector u, LispVector v)
    {
        return false;
    }
}

class Output_libraryFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class PagelengthFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class PairFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        if (!arg1.atom)
        {   if (!arg2.atom)
            {    return new Cons(
                     new Cons(arg1.car, arg2.car),
                     op2(arg1.cdr, arg2.cdr));
            }
            else return error("arg2 to pair is too short");
        }
        else if (!arg2.atom)
            return error("arg2 to pair is too long");
        else return Environment.nil;
    }
}

class PairpFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {   return arg1.atom ? Environment.nil :
               Jlisp.lispTrue;
    }
}

class PeekchFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Pipe_openFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class PlistFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1)
    {
        return ((Symbol)arg1).cdr/*plist*/;
    }
}

class PosnFn extends BuiltinFunction
{
    public LispObject op0() throws Exception
    {
        int n = ((LispStream)
            Jlisp.lit[Lit.std_output].car/*value*/).column;
        return LispInteger.valueOf(n);
    }
}

class RestartFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        Jlisp.backtrace = false;
        throw new ProgEvent(ProgEvent.RESTART, arg1, "restart");
    }
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        Jlisp.backtrace = false;
        throw new ProgEvent(ProgEvent.RESTART, arg1, arg2, "restart");
    }
}

// (preserve [restartfn [initmsg [continuep]]])
//                 dumps all state to a file specifed
//                 as "-o xxx.img" on the initial command-line.
//                 if continuep is non-nil reload the dumped image.

class PreserveFn extends BuiltinFunction
{
    public LispObject op0() throws Exception
    { 
    	return op3(Jlisp.nil, Jlisp.nil, Jlisp.nil); 
    }

    public LispObject op1(LispObject arg1) throws Exception
    {
    	return op3(arg1, Jlisp.nil, Jlisp.nil);
    }

	public LispObject op2(LispObject arg1, LispObject arg2)
			throws Exception {
		return op3(arg1, arg2, Jlisp.nil);
	}

	public LispObject opn(LispObject[] args) throws Exception {
		if (args.length != 3)
			return error("preserve called with " + args.length
					+ "args when 1 to 3 expected");
		return op3(args[0], args[1], args[2]);
	}

	LispObject op3(LispObject arg1, LispObject arg2, LispObject arg3)
			throws Exception {
// Following the tradition from CSL when the user calls PRESERVE the
// system stops. This makes more sense than one might have thought since
// in the process of unwinding (via the ProgEvent you see here) all fluid
// variables are put back to their top level values. If I checkpointed
// the system more directly various local bindings might be captured, and
// I think that would be undesirable. Aha but of arg3 is non-nil the
// system should restart using the new image it has just made...
        if (Jlisp.outputImagePos < 0)
            return Jlisp.error("No output image available");
        Jlisp.backtrace = false;
		throw new ProgEvent(
				(arg3 == Jlisp.nil ? ProgEvent.PRESERVE :
									 ProgEvent.PRESERVERESTART),
				new Cons(arg1, arg2),
				"preserve");
    }
}

class SaveObjectFn extends BuiltinFunction
{

    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        String name = ((LispString)arg1).string;
        GZIPOutputStream dump = null;
        try
        {   dump = new GZIPOutputStream(
                       new BufferedOutputStream(
                           new FileOutputStream(name),
                           32768));
            LispReader.dumpTree(arg2, dump);
        }
        catch (IOException e)
        {   Jlisp.errprintln("IO error on dump file: " + e.getMessage());
        }
        finally
        {   if (dump != null) dump.close();
        }
        return Environment.nil;
    }
}

class RestoreObjectFn extends BuiltinFunction
{

    public LispObject op1(LispObject arg1) throws Exception
    {
        return op2(arg1, LispInteger.valueOf(1));
    }
    
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        String name = ((LispString)arg1).string;
// read item number n from the file concerned. Used to debug!
        int n = ((LispSmallInteger)arg2).value;
        LispObject r = Environment.nil;
        Jlisp.idump = null;
        try
        {   GZIPInputStream dump = 
                new GZIPInputStream(
                    new BufferedInputStream(
                        new FileInputStream(name),
                        32768));
            Jlisp.idump = dump;
            LispReader.preRestore();
            Jlisp.descendSymbols = false;
            for (int i=0; i<n; i++)
                r = LispReader.readObject();
        }
        catch (IOException e)
        {   Jlisp.errprintln("IO error on dump file: " + e.getMessage());
        }
        finally
        {   if (Jlisp.idump != null) Jlisp.idump.close();
            LispReader.postRestore();
        }
        if (r == null) return new LispString("<null>");
        else return r;
    }
}

class PrinFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws ResourceException
    {
        arg1.print(LispObject.printEscape);
        return arg1;
    }
}

class Prin1Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws ResourceException
    {
        arg1.print(LispObject.printEscape);
        return arg1;
    }
}

class Prin2Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws ResourceException
    {
        arg1.print(0);
        return arg1;
    }
}

class Prin2aFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws ResourceException
    {
        arg1.print(LispObject.noLineBreak);
        return arg1;
    }
}

class PrinbinaryFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws ResourceException
    {
        arg1.print(LispObject.printBinary);
        return arg1;
    }
}

class PrincFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws ResourceException
    {
        arg1.print();
        return arg1;
    }
}

class Princ_downcaseFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws ResourceException
    {
        arg1.print(LispObject.printLower);
        return arg1;
    }
}

class Princ_upcaseFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws ResourceException
    {
        arg1.print(LispObject.printUpper);
        return arg1;
    }
}

class PrinhexFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws ResourceException
    {
        arg1.print(LispObject.printHex);
        return arg1;
    }
}

class PrinoctalFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws ResourceException
    {
        arg1.print(LispObject.printOctal);
        return arg1;
    }
}

class PrintFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws ResourceException
    {
        arg1.print(LispObject.printEscape);
        Jlisp.println();
        return arg1;
    }
}

class PrintcFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws ResourceException
    {
        arg1.print();
        Jlisp.println();
        return arg1;
    }
}

class PrintpromptFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Prog1Fn extends BuiltinFunction
{
    public LispObject op0()
    {
        return Environment.nil;
    }
    public LispObject op1(LispObject arg1)
    {
        return arg1;
    }
    public LispObject op2(LispObject arg1, LispObject arg2)
    {
        return arg1;
    }
    public LispObject opn(LispObject [] args)
    {
        return args[0];
    }
}

class Prog2Fn extends BuiltinFunction
{
    public LispObject op0()
    {
         return Environment.nil;
    }
    public LispObject op1(LispObject arg1)
    {
         return Environment.nil;
    }
    public LispObject op2(LispObject arg1, LispObject arg2)
    {
        return arg2;
    }
    public LispObject opn(LispObject [] args)
    {
        return args[1];
    }
}

class PrognFn extends BuiltinFunction
{
    public LispObject op0()
    {
         return Environment.nil;
    }

    public LispObject op1(LispObject arg1)
    {
        return arg1;
    }
    public LispObject op2(LispObject arg1, LispObject arg2)
    {
        return arg2;
    }
    public LispObject opn(LispObject [] args)
    {
        return args[args.length-1];
    }

}

class PutFn extends BuiltinFunction
{
    public LispObject opn(LispObject [] args) throws Exception
    {
        if (args.length != 3)
            return error("put called with " + args.length +
                "args when 3 expected");
        return Fns.put((Symbol)args[0], args[1], args[2]);
    }
}

class PuthashFn extends BuiltinFunction
{
    public LispObject op2(LispObject key, LispObject value)
    {
        ((LispHash)Jlisp.lit[Lit.hashtab]).hash.put(key, value);
        return value;
    }
    public LispObject opn(LispObject [] args) throws Exception
    {
        if (args.length != 3)
            return error("puthash called with " + args.length +
                "args when 2 or 3 expected");
        LispObject key = args[0];
        LispHash h = (LispHash)args[1];
        LispObject value = args[2];
        h.hash.put(key, value);
        return value;
    }
}

class PutvFn extends BuiltinFunction
{
    public LispObject opn(LispObject [] args) throws Exception
    {
        if (args.length != 3)
            return error("putv called with " + args.length +
                "args when 3 expected");
        LispVector v = (LispVector)args[0];
        LispSmallInteger n = (LispSmallInteger)args[1];
        int i = n.value;
        v.vec[i] = args[2];
        return args[2];
    }

}

class Putv_charFn extends BuiltinFunction
{
    public LispObject opn(LispObject [] args) throws Exception
    {
        if (args.length != 3)
            return error("putv-char called with " + args.length +
                "args when 3 expected");
        String v = ((LispString)args[0]).string;
        LispSmallInteger n = (LispSmallInteger)args[1];
        int i = n.value;
        char [] v1 = v.toCharArray();
        v1[i] = (char)(((LispSmallInteger)args[2]).value);
        ((LispString)args[0]).string = new String(v1);
        return args[2];
    }
}

class Putv16Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Putv32Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Putv8Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class QcaarFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1)
    {
        return arg1.car.car;
    }
}

class QcadrFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return arg1.cdr.car;
    }
}

class QcarFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return arg1.car;
    }
}

class QcdarFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return arg1.car.cdr;
    }
}

class QcddrFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return arg1.cdr.cdr;
    }
}

class QcdrFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return arg1.cdr;
    }
}

class QgetvFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2)
    {
        LispVector v = (LispVector)arg1;
        return v.vec[((LispSmallInteger)arg2).value];
    }
}

class QputvFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class RassocFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class RdfFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (!(arg1 instanceof LispString))
            return error("argument for rdf should be a string");
        String name = ((LispString)arg1).string;
        LispObject save = Jlisp.lit[Lit.std_input].car/*value*/;
        try
        {   Jlisp.lit[Lit.std_input].car/*value*/ =
                new LispStream(
                    name,
                    new BufferedReader(
                        new FileReader(LispStream.nameConvert(name))),
                    false, true);
            try
            {   Jlisp.println();
                // here I really want the simple READ-EVAL-PRINT
                // without any messing with any restart function.
                Jlisp.restarting = false; // just to be ultra-careful!
                Jlisp.readEvalPrintLoop(true);
            }
            finally
            {   ((LispStream)Jlisp.lit[Lit.std_input].car/*value*/).close();
            }
        }
        catch (FileNotFoundException e)
        {   return error("Unable to read from \"" +
                         name + "\"");
        }
        finally
        {   Jlisp.lit[Lit.std_input].car/*value*/ = save;
            Jlisp.println("+++ end of reading " + name);
        }
        return Environment.nil;
    }
}

class RdsFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1)
    {
// The issue of what to select if the user says (rds nil) is a bit horrid
// here in terms of how it should react with the user also re-setting
// or re-binding !*std-input!* and the other related variables. Here I
// do something that probably works well enough for REDUCE...
        if (arg1 == Environment.nil) arg1 = Jlisp.lit[Lit.terminal_io].car/*value*/;
        LispObject prev = Jlisp.lit[Lit.std_input].car/*value*/;
        Jlisp.lit[Lit.std_input].car/*value*/ = (LispStream)arg1;
        return prev;
    }
}

class ReadFn extends BuiltinFunction
{
    public LispObject op0() throws Exception
    {
        LispObject w = Jlisp.lit[Lit.eof];
        try
        {   w = LispReader.read();
        }
        catch (EOFException e)
        {   return Jlisp.lit[Lit.eof];
        }
        catch (IOException e)
        {   Jlisp.errprintln("Reader error: " + e.getMessage());
        }
        return w;
    }
}

class ReadchFn extends BuiltinFunction
{
    public LispObject op0() throws Exception
    {
        try
        {   int ch;
            do
            {   ch = ((LispStream)Jlisp.lit[Lit.std_input].car/*value*/
                     ).readChar();
            } while (ch == '\r');          // wary of Windows (& DOS)
            if (ch < 0) return Jlisp.lit[Lit.eof];
            else if (ch < 128) return LispReader.chars[ch];
            else return Symbol.intern(String.valueOf((char)ch));
        }
        catch (IOException e)
        {   return error("IO error detected in readch");
        }
    }
}

class ReadlineFn extends BuiltinFunction
{
    public LispObject op0() throws Exception
    {
        StringBuffer s = new StringBuffer();
        LispObject sr = Jlisp.lit[Lit.raise].car/*value*/;
        LispObject sl = Jlisp.lit[Lit.lower].car/*value*/;
        Jlisp.lit[Lit.raise].car/*value*/ = Environment.nil;
        Jlisp.lit[Lit.lower].car/*value*/ = Environment.nil;
        try
        {   int c;
            boolean any = false;
            LispStream r = (LispStream)Jlisp.lit[Lit.std_input].car/*value*/;
            while ((c = r.readChar()) != '\n' &&
                   c != -1)
            {   if (c != '\r') 
                {   s.append((char)c);
                    any = true;
                }
            }
            if (c == -1 && !any) return Jlisp.lit[Lit.eof];
            else return new LispString(new String(s));
        }
        catch (IOException e)
        {   return error("IO error detected in readline");
        }
        finally
        {   Jlisp.lit[Lit.raise].car/*value*/ = sr;
            Jlisp.lit[Lit.lower].car/*value*/ = sl;
        }
    }
    public LispObject op1(LispObject a1) throws Exception
    {
        StringBuffer s = new StringBuffer();
        LispObject sr = Jlisp.lit[Lit.raise].car/*value*/;
        LispObject sl = Jlisp.lit[Lit.lower].car/*value*/;
        Jlisp.lit[Lit.raise].car/*value*/ = Environment.nil;
        Jlisp.lit[Lit.lower].car/*value*/ = Environment.nil;
        try
        {   int c;
            boolean any = false;
            LispStream r = (LispStream)a1;
            while ((c = r.readChar()) != '\n' &&
                   c != -1)
            {   if (c != '\r')
                {   s.append((char)c);
                    any = true;
                }
            }
            if (c == -1 && !any) return Jlisp.lit[Lit.eof];
            else return new LispString(new String(s));
        }
        catch (IOException e)
        {   return error("IO error detected in readline");
        }
        finally
        {   Jlisp.lit[Lit.raise].car/*value*/ = sr;
            Jlisp.lit[Lit.lower].car/*value*/ = sl;
        }
    }
}

class ReclaimFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class RemdFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        Symbol a = (Symbol)arg1;
        a.completeName();
        a.fn = new Undefined(a.pname);
        return a;
    }
}

class RemflagFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2)
    {
        while (!arg1.atom)
        {   LispObject p = arg1;
            Symbol s = (Symbol)p.car;
            arg1 = p.cdr;
            Fns.remprop(s, arg2);
        }
        return Environment.nil;
    }
}

class RemhashFn extends BuiltinFunction
{
    public LispObject op1(LispObject key)
    {
        LispObject r = (LispObject)
            ((LispHash)Jlisp.lit[Lit.hashtab]).hash.remove(key);
        if (r == null) r = Environment.nil;
        return r;
    }
    public LispObject op2(LispObject key, LispObject table)
    {
        LispHash h = (LispHash)table;
        LispObject r = (LispObject)h.hash.remove(key);
        if (r == null) r = Environment.nil;
        return r;
    }
    public LispObject opn(LispObject [] args) throws Exception
    {
        if (args.length != 3)
            return error("remhash called with " + args.length +
                "args when 1 to 3 expected");
        LispObject key = args[0];
        LispHash h = (LispHash)args[1];
        LispObject defaultValue = args[2];
        LispObject r = (LispObject)h.hash.remove(key);
        if (r == null) r = defaultValue;
        return r;
    }
}

class RemobFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1 instanceof Symbol) Symbol.remob((Symbol)arg1);
        return arg1;
    }
}

class RempropFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        if (!(arg1 instanceof Symbol)) return Environment.nil;
        else return Fns.remprop((Symbol)arg1, arg2);
    }
}

class Rename_fileFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        String s;
        if (arg1 instanceof Symbol)
        {   ((Symbol)arg1).completeName();
            s = ((Symbol)arg1).pname;
        }
        else if (arg1 instanceof LispString) s = ((LispString)arg1).string;
        else return Environment.nil;
        String s1;
        if (arg2 instanceof Symbol)
        {   ((Symbol)arg1).completeName();
            s1 = ((Symbol)arg2).pname;
        }
        else if (arg2 instanceof LispString) s1 = ((LispString)arg2).string;
        else return Environment.nil;
        return LispStream.fileRename(s, s1);
    }
}

class RepresentationFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class ReturnFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws ProgEvent
    {
        Specfn.progEvent = Specfn.RETURN;
        Specfn.progData = arg1;
        return arg1;
    }
}

class ReverseFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws ResourceException
    {
        LispObject r = Environment.nil;
        while (!arg1.atom)
        {   LispObject a = arg1;
            r = new Cons(a.car, r);
            arg1 = a.cdr;
        }
        return r;
    }
}

class ReversipFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1)
    {
        LispObject r = Environment.nil;
        while (!arg1.atom)
        {   LispObject a = arg1;
            arg1 = a.cdr;
            a.cdr = r;
            r = a;
        }
        return r;
    }
    public LispObject op2(LispObject arg1, LispObject arg2)
    {
        LispObject r = arg2;
        while (!arg1.atom)
        {   LispObject a = arg1;
            arg1 = a.cdr;
            a.cdr = r;
            r = a;
        }
        return r;
    }
}

class RplacaFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        if (arg1.atom) return error("bad arg to rplaca");
        arg1.car = arg2;
        return arg1;
    }
}

class RplacdFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        if (arg1.atom) return error("bad arg to rplacd");
        arg1.cdr = arg2;
        return arg1;
    }
}

class RplacwFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        if (arg1.atom || arg2.atom) return error("bad arg to rplacw");
        arg1.car = arg2.car;
        arg1.cdr = arg2.cdr;
        return arg1;
    }
}

class RseekFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class RtellFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class SampleFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class SassocFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class ScharFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2)
    {
        int n = ((LispSmallInteger)arg2).value;
        String s = ((LispString)arg1).string;
        char ch = s.charAt(n);
        if (ch < 128) return LispReader.chars[ch];
        else return Symbol.intern(String.valueOf((char)ch));
    }
}

class SeprpFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        // blank end-of-line tab form-fee carriage-return
        if (arg1 == Jlisp.lit[Lit.space] ||
            arg1 == Jlisp.lit[Lit.newline] ||
            arg1 == Jlisp.lit[Lit.tab] ||
            arg1 == Jlisp.lit[Lit.formFeed] ||
            arg1 == Jlisp.lit[Lit.cr])
            return Jlisp.lispTrue;
        else return Environment.nil;
    }
}

class SetFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2)
    {
        ((Symbol)arg1).car/*value*/ = arg2;
        return arg2;
    }
}

class Set_autoloadFn extends BuiltinFunction
{
    public LispObject op2(LispObject name, LispObject data) throws Exception
    {
        Symbol f = (Symbol)name;
        if (data.atom)
            data = new Cons(data, Environment.nil);
        f.fn = new AutoLoad(f, data);
        return name;
    }
}

class Set_help_fileFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Set_print_precisionFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        int n = Jlisp.printprec;
        Jlisp.printprec = ((LispSmallInteger)arg1).value;
        return LispInteger.valueOf(n);
    }
}

class Get_print_precisionFn extends BuiltinFunction
{
    public LispObject op0() throws Exception
    {
        return LispInteger.valueOf(Jlisp.printprec);
    }
}

class SetpcharFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        String old = Fns.prompt;
        if (old == null) old = "";     // just in case!
        if (arg1 instanceof LispString)
            Fns.prompt = ((LispString)arg1).string;
        else if (arg1 instanceof Symbol)
        {   ((Symbol)arg1).completeName();
            Fns.prompt = ((Symbol)arg1).pname;
        }
        else Fns.prompt = null;  // use system default
        return new LispString(old);
    }
}

class Simple_string_pFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1)
    {
        if (arg1 instanceof LispString) return Jlisp.lispTrue;
        else return Environment.nil;
    }
}

class Simple_vector_pFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1)
    {
        if (arg1 instanceof LispVector) return Jlisp.lispTrue;
        else return Environment.nil;
    }
}

class SmemqFn extends BuiltinFunction
{

    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        while (!arg2.atom)
        {   LispObject a = arg2;
            if (a.car == Jlisp.lit[Lit.quote]) return Environment.nil;
            else if (op2(arg1, a.car) != Environment.nil)
                return Jlisp.lispTrue;
            else arg2 = a.cdr;
        }
        if (arg1 == arg2) return Jlisp.lispTrue;
        else return Environment.nil;
    }
}

class SpacesFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws ResourceException
    {
        int n = ((LispSmallInteger)arg1).value;
        for (int i=0; i<n; i++)
            Jlisp.print(" ");
        return Environment.nil;
    }
}

class Special_charFn extends BuiltinFunction
{

    public LispObject op1(LispObject arg1) throws Exception
    {
        LispSmallInteger a = (LispSmallInteger)arg1;
        int n = a.value;
        LispObject [] t = Jlisp.lit;
        switch (n)
        {
    case 0:  return t[Lit.space];
    case 1:  return t[Lit.newline];
    case 2:  return t[Lit.backspace];
    case 3:  return t[Lit.tab];
    // case 4: vertical tab
    case 5:  return t[Lit.formFeed];
    case 6:  return t[Lit.cr];
    case 7:  return t[Lit.rubout];
    case 8:  return t[Lit.eof];
    // case 9: ctrl-G
    case 10: return t[Lit.escape];
    default: return Environment.nil;
        }
    }
}

class Special_form_pFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {   return (arg1 instanceof Symbol &&
                ((Symbol)arg1).special != null) ? 
               Jlisp.lispTrue :
               Environment.nil;
    }
}

class SpoolFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Start_moduleFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return Fasl.startModule(arg1);
    }
}

// (stop) exist from this Lisp.

class StopFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        Jlisp.println();
        Jlisp.backtrace = false;
        throw new ProgEvent(ProgEvent.STOP, arg1, "STOP function called");
    }
}

class StreampFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1)
    {
        return arg1 instanceof LispStream ?
               Jlisp.lispTrue : 
               Environment.nil;
    }
}

class StringpFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1)
    {
        return arg1 instanceof LispString ? Jlisp.lispTrue :
               Environment.nil;
    }
}

class Stub1Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1)
    {
        return Environment.nil;
    }
}

class Stub2Fn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2)
    {
        return Environment.nil;
    }
}

class SublaFn extends BuiltinFunction
{
    public LispObject op2(LispObject u, LispObject v) throws Exception
    {
        if (u == Environment.nil ||
            v == Environment.nil) return v;
        else if (v.atom)
        {   while (!u.atom)
            {   LispObject cu = u;
                u = cu.cdr;
                if (cu.car.atom) continue;
                LispObject ccu = cu.car;
                if (v instanceof LispNumber)                   // @@@
                {   if (v.lispequals(ccu.car)) return ccu.car; // @@@
                }                                              // @@@
                else if (ccu.car == v) return ccu.cdr;
            }
            return v;
        }
        LispObject cv = v;
        LispObject y = new Cons(
            op2(u, cv.car),
            op2(u, cv.cdr));
        if (y.lispequals(v)) return v;
        else return y; 
    }
}

class SublisFn extends BuiltinFunction
{
    public LispObject op2(LispObject al, LispObject x) throws Exception
    {
        LispObject a = al;
        while (!a.atom)
        {   LispObject c = a;
            a = c.cdr;
            if (c.car.atom) continue;
            LispObject cc = c.car;
            if (cc.car.lispequals(x)) return cc.cdr;
        }
        if (x.atom) return x;
        LispObject cx = x;
        LispObject aa = op2(al, cx.car);
        LispObject bb = op2(al, cx.cdr);
        if (aa == cx.car && bb == cx.cdr) return x;
        else return new Cons(aa, bb);
    }
}

class SubstFn extends BuiltinFunction
{
    public LispObject opn(LispObject [] args) throws Exception
    {
        if (args.length != 3)
            return error("subst called with " + args.length +
                "args when 1 to 3 expected");
        return subst(args[0], args[1], args[2]);
    }

    LispObject subst(LispObject a, LispObject b, LispObject c) throws ResourceException
    {
        if (b.lispequals(c)) return a;
        if (c.atom) return c;
        LispObject cc = c;
        LispObject aa = subst(a, b, cc.car);
        LispObject bb = subst(a, b, cc.cdr);
        if (aa == cc.car && bb == cc.cdr) return c;
        else return new Cons(aa, bb);
    }
}

class SubstqFn extends BuiltinFunction
{
    public LispObject opn(LispObject [] args) throws Exception
    {
        if (args.length != 3)
            return error("substq called with " + args.length +
                "args when 1 to 3 expected");
        return substq(args[0], args[1], args[2]);
    }

    LispObject substq(LispObject a, LispObject b, LispObject c) throws ResourceException
    {
        if (b instanceof LispNumber)
        {   if (b.lispequals(c)) return a;
        }
        else if (b == c) return a;
        if (c.atom) return c;
        LispObject cc = c;
        LispObject aa = substq(a, b, cc.car);
        LispObject bb = substq(a, b, cc.cdr);
        if (aa == cc.car && bb == cc.cdr) return c;
        else return new Cons(aa, bb);
    }
}

class SxhashFn extends BuiltinFunction
{ // use md60 here...
    public LispObject op1(LispObject arg1) throws Exception
    {
        LispStream f = new LispDigester();
        LispObject save = Jlisp.lit[Lit.std_output].car/*value*/;
        try
        {   Jlisp.lit[Lit.std_output].car/*value*/ = f;
            arg1.print(LispObject.noLineBreak+LispObject.printEscape);
        }
        finally
        {   Jlisp.lit[Lit.std_output].car/*value*/ = save;
        }
        byte [] res = f.md.digest();
        return LispInteger.valueOf(new BigInteger(res).shiftRight(68));
    }
}

class Symbol_argcountFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Symbol_envFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1)
    {
        if (!(arg1 instanceof Symbol)) return Environment.nil;
        LispFunction f = ((Symbol)arg1).fn;
        if (f instanceof FnWithEnv)
            return new LispVector(((FnWithEnv)f).env);
        else return Environment.nil;
    }
}

class Symbol_fastgetsFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Symbol_fn_cellFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        LispFunction f = ((Symbol)arg1).fn;
        if (f instanceof Undefined) return Environment.nil;
        else return f;
    }
}

class Symbol_functionFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return ((Symbol)arg1).fn;
    }
}

class Symbol_make_fastgetFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1)
    {
        return Environment.nil;
    }
    public LispObject op2(LispObject arg1, LispObject arg2)
    {
        return Environment.nil;
    }
}

class Symbol_nameFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        ((Symbol)arg1).completeName();
        return new LispString(((Symbol)arg1).pname);
    }
}

class Symbol_protectFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Symbol_set_definitionFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        Symbol a1 = (Symbol)arg1;
        if (!arg2.atom)
        {   LispObject a2 = arg2;
            if (a2.car == Jlisp.lit[Lit.lambda])
            {   a1.fn = new Interpreted(a2.cdr);
                return arg1;
            }
            else if (a2.car instanceof LispInteger)
            {   int nargs = a2.car.intValue();
                int nopts = nargs >> 8;
                int flagbits = nopts >> 8;
                int ntail = flagbits >> 2;
                nargs &= 0xff;
                nopts &= 0xff;
                flagbits &= 0x03;
// The next few cases are where a function is defined as a direct call
// to another, possibly discarding a few final args. Eg
//                          (de f (a b) (g a))
                if (ntail != 0)
                {   a1.fn = new CallAs(nargs, a2.cdr.cdr, ntail-1);
                    return arg1;
                }
                a2 = a2.cdr;
                if (a2.atom) return Environment.nil;
                Bytecode b = (Bytecode)a2.car;
                LispVector v = (LispVector)a2.cdr;
                if (flagbits != 0 || nopts != 0)
                {
// What is happening here is a MESS inherited from CSL.
//   nopts = number of optional args wanted
//   flagbits & 1   "hard case": pass Spid.noarg not nil for missing opts
//   flagbits & 2    &rest arg present
                    b = new ByteOpt(b.bytecodes, v.vec, 
                                    nargs, nopts, flagbits);
                }
                else 
                {   b.env = v.vec;
                    b.nargs = nargs;
                }
                a1.fn = b;
                return arg1;
            }
            // Otherwise drop through and moan
        }
        else if (arg2 instanceof Symbol)
        {   Symbol a2 = (Symbol)arg2;
            a1.fn = a2.fn;
            return arg1;
        }
        else if (arg2 instanceof LispFunction)
        {   a1.fn = (LispFunction)arg2;
            return arg1;
        }
        // Unrecognised cases follow - just print a message
        Jlisp.println();
        arg1.print(LispObject.printEscape);
        Jlisp.print(" => ");
        arg2.print();
        Jlisp.println();
        return Environment.nil;
    }
}

class Symbol_set_envFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        if (!(arg1 instanceof Symbol)) return Environment.nil;
        LispFunction f = ((Symbol)arg1).fn;
        if (f instanceof FnWithEnv) 
            ((FnWithEnv)f).env = ((LispVector)arg2).vec;
        else return Environment.nil; // quiet in case it fails?
        return arg2;
    }
}

class Symbol_set_nativeFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Symbol_valueFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1)
    {
        return ((Symbol)arg1).car/*value*/;
    }
}

class SymbolpFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {   return arg1 instanceof Symbol ? Jlisp.lispTrue :
               Environment.nil;
    }
}

class SymerrFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class SystemFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        try
        {   Runtime r = Runtime.getRuntime();
            r.exec(((LispString)arg1).string);
        }
        catch (IOException e)
        {   return Environment.nil;
        }
        catch (SecurityException e)
        {   return Environment.nil;
        }
        return Jlisp.lispTrue;
    }
}

class TagbodyFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class TerpriFn extends BuiltinFunction
{
    public LispObject op0() throws ResourceException
    {
        Jlisp.println();
        return Environment.nil;
    }
}

class ThreevectorpFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1 instanceof LispVector &&
            ((LispVector)arg1).vec.length == 3) return Jlisp.lispTrue;
        else return Environment.nil;
    }
}

class ThrowFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class TimeFn extends BuiltinFunction
{
    public LispObject op0() throws Exception
    {
        return LispInteger.valueOf(System.currentTimeMillis());
    }
}

class TmpnamFn extends BuiltinFunction
{
	public LispObject op0() throws Exception
    {
// Not really satisfactory - but I hope that nobody uses this!
        return new LispString("tempfile.tmp");
    }
    public LispObject op1(LispObject arg1) throws Exception
    {   String s;
        if (arg1 instanceof Symbol)
        {   ((Symbol)arg1).completeName();
            s = ((Symbol)arg1).pname;
        }
        else if (arg1 instanceof LispString) s = ((LispString)arg1).string;
        else s = "tmp";
        return new LispString("tempfile." + s);
    }
}

class TraceFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        while (!arg1.atom)
        {   Symbol n = (Symbol)arg1.car;
            if (!(n.fn instanceof TracedFunction))
                n.fn = new TracedFunction(n, n.fn);
            arg1 = arg1.cdr;
        }
        return Environment.nil;
    }
}

class TracesetFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Traceset1Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class TtabFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws ResourceException
    {
        int n = ((LispSmallInteger)arg1).value;
        LispStream f = (LispStream)Jlisp.lit[Lit.std_output].car/*value*/;
        while (f.column < n) f.print(" ");
        return Environment.nil;
    }
}

class TyoFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Undouble_executeFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class UnfluidFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class UnglobalFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class UnionFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        while (!arg1.atom)
        {   LispObject a2 = arg2;
            while (!a2.atom)
            {   if (a2.car.lispequals(arg1.car)) break;
                a2 = a2.cdr;
            }
            if (a2.atom)
                arg2 = new Cons(arg1.car, arg2);
            arg1 = arg1.cdr;
        }
        return arg2; 
    }
}

class Unmake_globalFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        Fns.remprop((Symbol)arg1, Jlisp.lit[Lit.global]);
        return Environment.nil;
    }
}

class Unmake_specialFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        Fns.remprop((Symbol)arg1, Jlisp.lit[Lit.special]);
        return Environment.nil;
    }
}

class UnreadchFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class UntraceFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        while (!arg1.atom)
        {   Symbol n = (Symbol)arg1.car;
            if (n.fn instanceof TracedFunction)
                n.fn = ((TracedFunction)n.fn).fn;
            arg1 = arg1.cdr;
        }
        return Environment.nil;
    }
}

class UntracesetFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Untraceset1Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Unwind_protectFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class UpbvFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        int n;
        if (arg1 instanceof LispString)
            n = ((LispString)arg1).string.length();
        else if (arg1 instanceof LispVector)
            n = ((LispVector)arg1).vec.length;
        else return Environment.nil;
        return LispInteger.valueOf(n-1);
    }
}

class User_homedir_pathnameFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class VectorpFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1 instanceof LispVector) return Jlisp.lispTrue;
        else return Environment.nil;
    }
}

class VerbosFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        int old = Jlisp.verbosFlag;
        if (arg1 instanceof LispInteger)
            Jlisp.verbosFlag = arg1.intValue();
        else if (arg1 == Environment.nil) Jlisp.verbosFlag = 0;
        else Jlisp.verbosFlag = 3;
        return LispInteger.valueOf(old);
    }
}

class Where_was_thatFn extends BuiltinFunction
{
    public LispObject op0() throws Exception
    {
        return new Cons(
            new LispString("Unknown file"),
            new Cons(LispInteger.valueOf(-1), Environment.nil));
    }
}

class Window_headingFn extends BuiltinFunction
{
    public LispObject op1(LispObject a) throws Exception
    {
        String s;
        if (a instanceof Symbol)
        {   ((Symbol)a).completeName();
            s = ((Symbol)a).pname;
        }
        else if (a instanceof LispString) s = ((LispString)a).string;
        else return Environment.nil;
// Note that I just dump this to output with no regard for Lisp output
// streams, buffering etc!
        if (Jlisp.standAlone) System.out.println(s);
        else
        {
            // in CWin case put string arg on window title-bar @@@@
        }
        return Environment.nil;
    }
}

class Startup_bannerFn extends BuiltinFunction
{
    public LispObject op1(LispObject a) throws Exception
    {
        // reset message displayed when Jlisp starts up @@@@
        // compressed heap images make this harder. I need to worry!
        return Environment.nil;
    }
}

class Writable_librarypFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Write_help_moduleFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Write_moduleFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (Fasl.writer == null)
            return error("no FASL file active in write-module");
        Fasl.faslWrite(arg1);
        return Environment.nil;
    }
}

class WrsFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1)
    {
// see comments for Rds.
        if (arg1 == Environment.nil) arg1 = Jlisp.lit[Lit.terminal_io].car/*value*/;
        LispObject prev = Jlisp.lit[Lit.std_output].car/*value*/;
        Jlisp.lit[Lit.std_output].car/*value*/ = (LispStream)arg1;
        return prev;
    }
}

class XassocFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class XconsFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws ResourceException
    {
        return new Cons(arg2, arg1);
    }
}

class XdifferenceFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class XtabFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws ResourceException
    {
        int n = ((LispSmallInteger)arg1).value;
        LispStream f = (LispStream)Jlisp.lit[Lit.std_output].car/*value*/;
        for (int i=0; i<n; i++) f.print(" ");
        return Environment.nil;
    }
}

class TyiFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}


}

// end of Fns3.java

