module dmode; % Functions for defining and using poly domain modes.

% Author: Anthony C. Hearn.
% Modifications by: Stanley L. Kameny.

% Copyright (c) 1992 RAND.  All rights reserved.

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


Comment
   *** Description of Definition Requirements for Domain arithmetics ***

Syntactically, such elements have the following form:

<domain element>:=NIL|integer|<structured domain element>

<structured domain element> ::=
        (<domain identifier>.<domain structure>),

where NIL represents the domain element zero.

To introduce a new domain, we need to define:

1) A conversion function from integer to the given mode, stored under
   the attribute I2D.

2) A conversion function from new mode to or from every other mode.

3) Particular instances of the binary operations +,- and * for this
   mode.

4) Particular instances of ZEROP, ONEP and MINUSP for this mode.
   Although ONEP could be defined in terms of ZEROP, we believe it is
   more efficient to have both functions (though this has not been
   thoroughly tested).

5) If domain is a field, a quotient must be defined.  If domain is a
   ring, a gcd and divide must be defined, and also a quotient
   function which returns NIL if the division fails.

6) A printing function for this mode that can print the object in a
   linear form. The printing function is associated with the attribute
   PRIFN.  This printing function should enclose the printed expression
   in parentheses if its top level operator has a precedence greater
   than +.

7) A function to convert structure to an appropriate prefix form.

8) A reading function for this mode.

9) A DNAME property for the tag, and a TAG property for the DNAME

10) Optionally, an exponentiation function. If this is not provided,
    repeated squaring is used (cf !:expt in dmodeop.red)

To facilitate this, all such modes should be listed in the global
variable DOMAINLIST!*.

The following rules should also be followed when introducing new
domains:

Some modes, such as modular arithmetic, require that integers be
converted to domain elements when input or addition or multiplication
of such objects occurs.  Such modes should be flagged "convert".

A domain which holds mutable internal state should be flagged
"resimplify" (no Reduce domains are currently so flagged) which means
that attempts to simplify domain elements will actually do so, rather
than just thinking "domain elements are always simplified".

In ALL cases it is assumed that any domain element that tests true to
the zero test can be converted into an explicit 0 (represented by NIL),
and any that tests true to the onep test can be converted into an
explicit 1.  If the domain allows for the conversion of other elements
into equivalent integers, a function under the optional attribute
INTEQUIVFN may also be defined to effect this conversion.

The result of an arithmetic (as opposed to a boolean) operation on
structured domain elements with the same tag must be another structured
domain element with the same tag.  In particular, a domain zero must be
returned as a tagged zero in that domain.

In some cases, it is possible to map functions on domain elements to
domain elements.  To provide for this capability in the complete
system, one can give such functions the domain tag as an indicator.
The results of this evaluation must be a tagged domain element (or an
integer?), but not necessarily an element from the same domain, or the
evaluation should abort with an error.  The error number associated
with this should be in the range 100-150;

fluid '(!*complex dmode!* gdmode!*);

global '(domainlist!*);

symbolic procedure initdmode u;
   % Checks that U is a valid domain mode, and sets up appropriate
   % interfaces to the system.
   begin
      dmodechk u;
      put(u,'simpfg,list(list(t,list('setdmode,mkquote u,t)),
                         list(nil,list('setdmode,mkquote u,nil))))
   end;

% switch complex!-rational,complex!-rounded;

symbolic procedure setdmode(u,bool);
   % Sets polynomial domain mode.  If bool is NIL, integers are used,
   % or in the case of complex, set to the lower domain.
   % Otherwise mode is set to u, or derived from it.
   begin scalar x;
      if (x := get(u,'dname)) then u := x;  % Allow a tag as argument.
      if u eq 'complex!-rational then
        <<if (x := dmode!*) then x := get(x,'dname);
          onoff('complex,bool); onoff('rational,bool);
          return x>>
        else if u eq 'complex!-rounded then
          <<if (x := dmode!*) then x := get(x,'dname);
            onoff('complex,bool); onoff('rounded,bool);
            return x>>;
      if null get(u,'tag)
        then rerror(poly,5,
                   list("Domain mode error:",u,"is not a domain mode"));
      if x := get(u,'package!-name) then load!-package x;
      return if u eq 'complex or !*complex then setcmpxmode(u,bool)
              else setdmode1(u,bool)
   end;

symbolic procedure setdmode1(u,bool);
   begin scalar x,y,z;
      x := get(u,'tag);
      y := dmode!*;
      if null bool
        then return if null y then nil
                     else if u eq (y := get(y,'dname))
                      then <<rmsubs(); gdmode!* := dmode!* := nil; y>>
                     else offmoderr(u,y)
        else <<if u memq '(rounded complex!-rounded) then !!mfefix();
               if x eq y then return x>>;
      % Now make sure there are no other domain switches left on.
      if not (z := get(x,'realtype)) then z := x;
      for each j in domainlist!* do
         if j neq '!:gi!: and not(j eq z)
           then set(intern compress
                       append(explode '!*,explode get(j,'dname)),
                    nil);
      rmsubs();
      y := get(y,'dname);
      if y then lprim list("Domain mode",y,"changed to",u);
      gdmode!* := dmode!* := x;
      return y
   end;

symbolic procedure offmoderr(u,y);
   lpriw("***",list("Failed attempt to turn off",u,"when",y,"is on"));

symbolic procedure dmodechk u;
   % Checks to see if U has complete specification for a domain mode.
   begin scalar z;
      if not(z := get(u,'tag))
        then rerror(poly,6,list("Domain mode error:","No tag for",u))
       else if not(get(z,'dname) eq u)
        then rerror(poly,7,list("Domain mode error:",
                                 "Inconsistent or missing DNAME for",z))
       else if not(z memq domainlist!*)
        then rerror(poly,8,list("Domain mode error:",
                                 z,"not on domain list"));
      u := z;
      for each x in domainlist!*
        do if u=x then nil
            else <<if not get(u,x) then put(u,x,mkdmoderr(u,x));
                   if not get(x,u) then put(x,u,mkdmoderr(x,u))>>;
%            then rederr list("Domain mode error:",
%                          "No conversion defined between",U,"and",X);
      z := '(plus difference times quotient i2d prepfn prifn
             minusp onep zerop);
      if not flagp(u,'field) then z := 'divide . 'gcd . z;
      for each x in z do if not get(u,x)
             then rerror(poly,9,list("Domain mode error:",
                                      x,"is not defined for",u))
   end;

symbolic procedure dmoderr(u,v);
   rerror(poly,10,list("Conversion between",get(u,'dname),
                       "and",get(v,'dname),"not defined"));

symbolic procedure mkdmoderr(u,v);
   list('lambda,'(!*x!*),list('dmoderr,mkquote u,mkquote v));

endmodule;

end;
