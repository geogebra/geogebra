package org.mathpiper.mpreduce;

//
// This file is part of the Jlisp implementation of Standard Lisp
// Copyright \u00a9 (C) Codemist Ltd, 1998-2000.
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

public class Lit
{
    static String [] names =
    {
// The first few items in the "literals" table are not really literals at
// all in that they are values that can be changed at least when a heap
// image is being dumped. But putting them here provides me with a
// simple scheme that lets me save them in image files.

        "nil",             // restart function
	"nil",             // banner
	"nil",             // system-wide hash-table
	"nil",             // birthday
        "nil",             // SPARE

// The ones below here are all constant values	
        "*undefined-value*",
	"lambda",
	"quote",
	",",               // used in back-quote input syntax
	",@",
	"cons",
	"append",
	"special",
	"global",
	"expr",
	"subr",
	"macro",
	"fexpr",
	"input",
	"output",
	"noncom",          // special treatment or ORDERP for Reduce. Ugh!
	"<eof>",           // various special characters
	" ",
	"\n",
	"\b",
	"\t",
	"\f",
	"\r",
	"\u007f",          // rubout/delete
	"\u001b",          // ESCAPE
	"lispsystem*",
	"*raise",
	"*lower",
	"*comp",
        "compile",
	"common-lisp-mode",
        "*echo",
        "&optional",
        "&rest",
        "*savedef",
        "*package*",
        "*terminal-io*",
        "*standard-output*",
        "*standard-input*",
        "*error-output*",
        "*trace-output*",
        "*debug-io*",
        "*query-io*",
        "*redefmsg",
        "*resources*",

        "++spare2++",
        "++spare1++"
    };
    
// The names listed here MUST be in the same order as entries in the
// above table.

    public static final int restart    = 0;
    public static final int banner     = 1;
    public static final int hashtab    = 2;
    public static final int birthday   = 3;
    public static final int spareV     = 4;
 
    public static final int undefined  = 5;
    public static final int lambda     = 6;
    public static final int quote      = 7;
    public static final int comma      = 8;
    public static final int commaAt    = 9;
    public static final int cons       = 10;
    public static final int append     = 11;
    public static final int special    = 12;
    public static final int global     = 13;
    public static final int expr       = 14;
    public static final int subr       = 15;
    public static final int macro      = 16;
    public static final int fexpr      = 17;
    public static final int input      = 18;
    public static final int output     = 19;
    public static final int noncom     = 20;
    public static final int eof        = 21;
    public static final int space      = 22;
    public static final int newline    = 23;
    public static final int backspace  = 24;
    public static final int tab        = 25;
    public static final int formFeed   = 26;
    public static final int cr         = 27;
    public static final int rubout     = 28;
    public static final int escape     = 29;
    public static final int lispsystem = 30;
    public static final int raise      = 31;
    public static final int lower      = 32;
    public static final int starcomp   = 33;
    public static final int compile    = 34;
    public static final int commonLisp = 35;
    public static final int starecho   = 36;
    public static final int optional   = 37;
    public static final int rest       = 38;
    public static final int savedef    = 39;
    public static final int starpackage= 40;
    public static final int terminal_io= 41;
    public static final int std_output = 42;
    public static final int std_input  = 43;
    public static final int err_output = 44;
    public static final int tr_output  = 45;
    public static final int debug_io   = 46;
    public static final int query_io   = 47;
    public static final int redefmsg   = 48;
    public static final int resources  = 49;
    public static final int spare2     = 50;
    public static final int spare1     = 51;
}

// end of Lit.java


