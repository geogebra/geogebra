module rsupport;   % Basic functions needed to support RLISP and REDUCE.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

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


fluid '(!*backtrace);

global '(!*comp);

symbolic procedure aconc(u,v);
   % Adds element v to the tail of u. u is destroyed in process.
   nconc(u,list v);

symbolic procedure arrayp u; get(u,'rtype) eq 'array;

symbolic procedure atsoc(u,v);
   % This definition allows for a search of a general list.
   if null v then nil
    else if eqcar(car v,u) then car v
    else atsoc(u,cdr v);

symbolic procedure copyd(new,old);
   % Copy the function definition from old id to new.
   begin scalar x;
      x := getd old;
      if null x
        then rerror('rlisp,1,list(old,"has no definition in copyd"));
      putd(new,car x,cdr x);
      return new
   end;

symbolic procedure eqcar(u,v); null atom u and car u eq v;

symbolic procedure errorset!*(u,v); errorset(u,v,!*backtrace);

symbolic procedure errorset2 u;
   begin scalar !*protfg;
      !*protfg := t;
      return errorset(u,nil,nil)
   end;

symbolic procedure flagpcar(u,v);
   null atom u and idp car u and flagp(car u,v);

symbolic procedure idlistp u;
   % True if u is a list of id's.
   null u or null atom u and idp car u and idlistp cdr u;

symbolic procedure listp u;
   % Returns T if U is a top level list.
   null u or null atom u and listp cdr u;

symbolic procedure mkprog(u,v); 'prog . (u . v);

symbolic procedure mkquote u; list('quote,u);

symbolic procedure mksetq(u,v);
   if atom u then list('setq,u,v)
    else begin scalar x;
       if (x := get(car u,'setfn)) then return apply2(x,u,v)
        else typerr(u,"assignment argument")
    end;

symbolic procedure pairvars(u,vars,mode);
   % Sets up pairings of parameters and modes.
   begin scalar x;
   a: if null u then return append(reversip!* x,vars)
       else if null idp car u or get(car u,'infix) or get(car u,'stat)
             then symerr(list("Invalid parameter:",car u),nil);
      x := (car u . mode) . x;
      u := cdr u;
      go to a
   end;

symbolic procedure prin2t u; progn(prin2 u, terpri(), u);

% The following is included for compatibility with some old code.
% Its use is discouraged.

symbolic procedure princ u; prin2 u;

symbolic procedure putc(name,type,body);
   % Defines a non-standard function, such as an smacro. Returns NAME.
   begin
      if !*comp and flagp(type,'compile) then compd(name,type,body)
       else put(name,type,body);
      return name
   end;

% flag('(putc),'eval);

symbolic procedure reversip u;
   begin scalar x,y;
    a:  if null u then return y;
        x := cdr u; y := rplacd(u,y); u := x;
        go to a
   end;

symbolic procedure smemq(u,v);
   % True if id U is a member of V at any level (excluding quoted
   % expressions).
   if atom v then u eq v
    else if car v eq 'quote then nil
    else smemq(u,car v) or smemq(u,cdr v);

symbolic procedure subsetp(u,v);
   % True if u is a subset of v.
   null u or car u member v and subsetp(cdr u,v);

symbolic procedure union(x,y);
   if null x then y
    else union(cdr x,if car x member y then y else car x . y);

symbolic procedure intersection(u,v);
   % This definition is consistent with PSL.
   if null u then nil
    else if car u member v
     then car u . intersection(cdr u,delete(car u,v))
    else intersection(cdr u,v);

symbolic procedure u>=v; null(u<v);

symbolic procedure u<=v; null(u>v);

symbolic procedure u neq v; null(u=v);

symbolic procedure setdiff(u,v);
   if null v then u
    else if null u then nil
    else setdiff(delete(car v,u),cdr v);

% symbolic smacro procedure u>=v; null(u<v);

% symbolic smacro procedure u<=v; null(u>v);

% symbolic smacro procedure u neq v; null(u=v);

% List changing alternates (may also be defined as copying functions).

symbolic procedure aconc!*(u,v); nconc(u,list v);  % append(u,list v);

symbolic procedure nconc!*(u,v); nconc(u,v);       % append(u,v);

symbolic procedure reversip!* u; reversip u;       % reverse u;

symbolic procedure rplaca!*(u,v); rplaca(u,v);     % v . cdr u;

symbolic procedure rplacd!*(u,v); rplacd(u,v);     % car u . v;

% The following functions should be provided in the compiler for
% efficient coding.

symbolic procedure lispapply(u,v);
   % I'd like to use idp in the following test, but the TPS package
   % stores code pointers on property lists which then get used here.
   if null atom u
     then rerror('rlisp,2,list("Apply called with non-id arg",u))
    else apply(u,v);

symbolic procedure lispeval u; eval u;

symbolic procedure apply1(u,v); apply(u,list v);

symbolic procedure apply2(u,v,w); apply(u,list(v,w));

symbolic procedure apply3(u,v,w,x); apply(u,list(v,w,x));

% The following function is needed by several modules. It is more
% REDUCE-specific than other functions in this module, but since it
% needs to be defined early on, it might as well go here.

symbolic procedure gettype u;
   % Returns a REDUCE-related type for the expression U.
   % It needs to be more table driven than the current definition.
   if numberp u then 'number
    else if null atom u or null u or null idp u then 'form
    else if get(u,'simpfn) then 'operator
    else if get(u,'avalue) then car get(u,'avalue)
    else if getd u then 'procedure
    else if globalp u then 'global
    else if fluidp u then 'fluid
    else if flagp(u,'parm) then 'parameter
    else get(u,'rtype);

% The following function maps reserved identifiers to internal names.
% This is needed for t and nil, and possibly others.

symbolic procedure map!-reserved!-id u;
   get(u,'map!-reserved) or u;

% The same for a list of variables

symbolic procedure map!-reserved!-ids l;
   begin scalar v;
    a: if null l then return reversip v;
       v := map!-reserved!-id car l . v;
       l := cdr l;
       go to a;
   end;    

symbolic procedure get!-print!-name u;
   idp u and get(u,'oldnam) or u;

%put('t,'map!-reserved,'t!-reserved);
%put('t!-reserved,'oldnam,'t);

%% nil will be done later, needs more modifications to the parser
%put('nil,'map!-reserved,'nil!-reserved);
% The following doesn work, as get('reserved!-nil,'oldnam) returns nil
%put('nil!-reserved,'oldnam,'nil);

endmodule;

end;
