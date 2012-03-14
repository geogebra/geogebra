module mstruct;  % A tiny structure package for Standard Lisp.

% Author: Bruce A. Florman.

% Copyright (c) 1989, The RAND Corporation.  All rights reserved.

% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions are met:
%
%    * Redistributions of source code must retain the relevant copyright
%      notice, this list of conditions and the following disclaimer.
%    * Redistributions in binary form must reproduce the above copyright
%      notice, this list of conditions and the following disclaimer in the
%      documentation and/or other materials provided with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
% AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
% THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
% PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNERS OR
% CONTRIBUTORS
% BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
% CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
% SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
% INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
% CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
% ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
% POSSIBILITY OF SUCH DAMAGE.
%


comment

 DESCRIPTION

  (defstruct <structspec> [ <slotspec>... ] )

   The <structspec> may be either the name of the structure, or a list
  containing the name followed by zero or more options.  Each <slotspec>
  may be either a list containing the slot name and its default value,
  or simply the slot name, in which case the default value is NIL.

   Each option in the <structspec> may be either an option name, or a
  list containing the option name and a specified value.  If only the
  option name is given, then the default value for the given option is
  used.  If NIL is the specified value in an option, then the option is
  not used at all (in general a NIL value is the same as not having that
  option in the list at all).  If the same option appears more than once
  with different values, the last one in the <structspec> takes
  precedence.

 These are the valid options:

  PREDICATE
    Makes the zeroth element of the structure contain the structure name
    and creates a predicate macro to test if a given item is an instance
    of this structure.  The specified value is the name of the predicate
    macro.  The default value is the structure name followed by a `P'.

  CONSTRUCTOR
    By default the name of the constructor macro is `MAKE-' followed by
    the structure name.  You may provide a different constructor name
    with this option.  If there is no constructor option in the
    <structspec> the default constructor will still be generated.  The
    only way to completely suppress the generation of a constructor
    macro is to have a (CONSTRUCTOR NIL) option.

  The flag !*FASTSTRUCTS controls how the accessor macros expand.
  If it is NIL, they expand as GETVs, otherwise they expand as
  IGETVs.

 NOTE: see records.tst for a level 0 test file.

 REVISION HISTORY
  07/19/85 BAF -- File created.
  01/26/89 BAF -- Added predicate and constructor macros so that
      this code can replace the RLISP record code.
      Changed  GetR to StructFetch, and !*FAST-RECORDS
      to !*FASTSTRUCTS.  Added code to check the
      validity of the options.  Also added this file
      header.
  01/30/89 BAF -- Added CONC-NAME as a synonym for SLOT-PREFIX and
      the ExplodeId function for compatability with
      existing programs (eg. ernie).
  Wed Apr 21 14:22:18 1993 - JBM Convert to RLISP '88, remove prefix
      stuff.
  Tue May 11 09:03:20 1993 - JBM Remove tconc and fix evaluator bug.
  Mon May 17 15:36:54 1993 - JBM Add RSETF function.
  Tue May 18 11:09:07 1993 - JBM add qputv for CSL to RSETF;

flag('(defstruct), 'eval);

fluid '(!*faststructs);

switch FASTSTRUCTS;

macro procedure defstruct u;
begin integer indx;
      scalar options,slot_forms,name,predicate,constructor,functions;
  options := get_defstruct_options cadr u;
  if cdr u
    then slot_forms := for each slot in cddr u
                          collect if idp slot then {slot,nil} else slot;
  name := car options;
  predicate := atsoc('predicate,cdr options);
  if predicate then predicate := cdr predicate;
  constructor := atsoc('constructor,cdr options);
  if constructor then constructor := cdr constructor;
  functions := NIL;
  if constructor then
   functions := build_defstruct_constructor_macro(name,
                                                  constructor,
                                                  slot_forms,
                                                  predicate)
                   . functions;
  if predicate then
     functions :=
       build_defstruct_predicate_function(name, predicate) . functions;
  indx := if predicate then 1 else 0;
  for each slot in slot_forms do
     <<functions :=
            build_defstruct_accessor_macro(car slot, indx) . functions;
       indx := indx + 1>>;
  functions :=  mkquote name . functions;
  return 'progn  . reverse functions
end;

expr procedure get_defstruct_options u;
begin scalar name, options, predicate, constructor;
  if pairp u then << name := car u; options := cdr u >>
     else << name := u; options := nil >>;
  if  not idp name then error(0, {"bad defstruct name:", name});
  for each entry in options
      do if  entry eq 'predicate then
            predicate := intern compress append(explode name, '(p))
         else if eqcar(entry, 'predicate) then predicate := cadr entry
         else if entry eq 'constructor then
               constructor := intern compress append('(m a k e !! !-),
                                                    explode name)
         else if eqcar(entry,'constructor)
          then constructor := cadr entry
         else error(0, {"bad defstruct option:", entry});
  if null constructor then
    constructor := intern compress append('(m a k e !! !-),
                                          explode name);
  return {name, 'predicate . predicate, 'constructor . constructor}
end;

expr procedure explodeid x;
% EXPLODEID(X) - Explode whatever x is and make sure the result can
% be compressed back into an id no matter what it is.
if idp x then explode x
  else for each elt in explode2 x join {'!!, elt};

expr procedure build_defstruct_constructor_macro
   (name,macro_name,slot_forms,has_predicate);
begin scalar dflts;
  dflts := for each x in slot_forms collect
     {'cons, mkquote car x, cadr x};
   % I deal with the name field by inserting it as an extra slot, with
   % slot-name made by a gensym so that the user will not get to
   % override the default value ever.  As coded here if the default
   % value of a slot depends on a variable called !$!$!$ then scope
   % issues will lead to silly results being generated.
  if has_predicate
    then dflts := {'cons, '(gensym), mkquote name} . dflts;
  return {'putd,
           mkquote macro_name,
           ''macro,
           mkquote {'lambda, '(!$!$!$),
                      {'list, ''defstructvector,
                         {'mklist, {'defstruct_constructor,
                            '(cdr !$!$!$),
                            'list . dflts}}}}}
   end;

symbolic procedure mklist x; 'list . x;

expr procedure defstruct_constructor(u, dflts);
  for each d in dflts collect find_struct_key(car d, u, cdr d);

expr procedure find_struct_key(key, u, dflt);
  if null u then mkquote dflt
  else if car u eq key then
    if null cdr u then nil else cadr u
  else find_struct_key(key, cddr u, dflt);

expr procedure defstructvector l;
% DEFSTRUCTVECTOR(L) - Create a vector and store the list L into it.
% This is a portable substitute for PSL's list2vector.
begin integer i; scalar v;
  v := mkvect sub1 length l;
  i := 0;
  for each vl in l do <<putv(v,i,vl); i := i+1>>;
  return v
end;

expr procedure build_defstruct_predicate_function(name, fnname);
% BUILD_DEFSTRUCT_PREDICATE_FUNCTION(NAME, FNNAME) - Builds a defstruct
% predicate to return as a function.
{'de, fnname, '(x),
  {'and, '(vectorp x), {'eq, mkquote name, '(igetv x 0)}}};

expr procedure build_defstruct_accessor_macro(slot_name,indx);
{'dm, slot_name, '(u), {'list, '(quote structfetch), '(cadr u), indx}};

macro procedure structfetch u;
  if !*faststructs  then 'igetv . cdr u else 'getv . cdr u;


%-----------------------------------------------------------------------
%                         SETF for RLISP88
%-----------------------------------------------------------------------

macro procedure rsetf u; expandrsetf(cadr u, caddr u);

expr procedure expandrsetf(lhs, rhs);
 if atom lhs then {'setq, lhs, rhs}
    else if eqcar(lhs, '!&VARIABLE_FETCH) then
        '!&VARIABLE_STORE . append(cdr lhs, {rhs})
    else if get(car lhs, 'ASSIGN_OP) then
         get(car lhs, 'ASSIGN_OP) . append(cdr lhs, {rhs})
    else if getd car lhs and eqcar(getd car lhs, 'macro) then
        expandrsetf(apply(cdr getd car lhs, {lhs}), rhs)
    else error(0, {lhs, "bad RSETF form"});

deflist('((getv putv) (igetv putv) (car rplaca) (cdr rplacd)),
        'ASSIGN_OP);

% This is CSL specific but shouldn't hurt anybody.
put('qgetv, 'ASSIGN_OP, 'qputv);

endmodule;

end;
