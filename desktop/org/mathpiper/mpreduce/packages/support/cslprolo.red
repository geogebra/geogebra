% module cslprolo;   % CSL dependent code for REDUCE.

% Author: Anthony C. Hearn.

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


% This file defines functions, variables and declarations needed to
% make REDUCE and the underlying CSL system compatible, and which need
% to be input before the system independent REDUCE source is loaded.

% Support for package creation.

symbolic procedure create!-package(u,v);
   % Make module list u into a package with path v.
   % Second argument is no longer used.
   if null idp car u then typerr(car u,"package name")
    else progn(
% If building the bootstrap version report the name of each package.
           (if member('cold!-start, lispsystem!*) then progn(
              terpri(), princ "+++ Creating a package: ", print car u)),
           put(car u,'package,u),
           car u);

% create!-package('(cslprolo),nil);

symbolic procedure evload l;
% This is coded out as an explicit loop because it is processed rather
% early in the bootstrap sequence, and the nicer syntax I might prefer
% to use may not be stable...
  begin
top: if null l then return nil;
     load!-module car l;
     l := cdr l;
     go to top
  end;

%
% Well you might wonder... this is in cslprolo.red AND in cslrend.red.
% The reason is that it is needed early if rlisp is to be patchable. It
% is needed AFTER rlisp.red has been loaded because the first time in
% the bootstrap-build that RLISP is loaded no attention is given to LOSE
% properties (this is a REAL misery) so the definition must be put in
% place on top of the incorrect one builty by RLISP the first time
% around.
%

remflag('(copyd), 'lose);

symbolic procedure copyd(new,old);
% Copy the function definition from old id to new.
   begin scalar x;
      x := getd old;
% If loading with !*savedef = '!*savedef then the actual definitions
% do not get loaded, but the source forms do...
      if null x then progn(
        if not (!*savedef = '!*savedef)
          then rerror('rlisp,1,list(old,"has no definition in copyd")) )
      else progn(putd(new,car x,cdr x),
                 if flagp(old, 'lose) then flag(list new, 'lose) );
% The transfer of the saved definition is needed if the REDUCE "patch"
% mechanism is to work fully properly.
      if (x := get(old, '!*savedef)) then put(new, '!*savedef, x);
      return new
   end;

flag('(copyd), 'lose);

% The following are built into CSL and so any definition found within
% the REDUCE sources should be viewed as "portability" but should be ignored.

if memq('vsl, lispsystem!*) then
   flag('(atsoc copy eqcar gcdn geq lastpair leq mkquote neq reversip
       rplacw iplus itimes iplus2 itimes2 iadd1 isub1 iminus iminusp
       idifference iquotient iremainder ilessp igreaterp ileq igeq
       izerop ionep apply1 apply2 apply3
% modular!-difference
% modular!-minus modular!-number modular!-plus modular!-quotient
% modular!-reciprocal modular!-times modular!-expt set!-small!-modulus
% acos acosd acosh acot acotd acoth acsc acscd acsch asec asecd
% asech asin asind asinh atan atand atan2 atan2d atanh cbrt
       cos
% cosd cosh cot cotd coth csc cscd csch
       exp expt
% hypot ln
       log
% logb log10 sec secd sech
       sin
% sind sinh
       sqrt
% tan tand tanh
       fix
       ceiling floor round clrhash puthash gethash remhash
% princ!-upcase princ!-downcase
       union intersection
% safe!-fp!-plus safe!-fp!-times safe!-fp!-quot
       threevectorp
       sort
% stable!-sort stable!-sortip
       lengthc prin2 princ),'lose)
else
   flag('(atsoc copy eqcar gcdn geq lastpair leq mkquote neq reversip
       rplacw iplus itimes iplus2 itimes2 iadd1 isub1 iminus iminusp
       idifference iquotient iremainder ilessp igreaterp ileq igeq
       izerop ionep apply1 apply2 apply3 modular!-difference
       modular!-minus modular!-number modular!-plus modular!-quotient
       modular!-reciprocal modular!-times modular!-expt set!-small!-modulus
       acos acosd acosh acot acotd acoth acsc acscd acsch asec asecd
       asech asin asind asinh atan atand atan2 atan2d atanh cbrt cos
       cosd cosh cot cotd coth csc cscd csch exp expt hypot ln log
       logb log10 sec secd sech sin sind sinh sqrt tan tand tanh fix
       ceiling floor round clrhash puthash gethash remhash
       princ!-upcase princ!-downcase union intersection
       safe!-fp!-plus safe!-fp!-times safe!-fp!-quot threevectorp
       sort stable!-sort stable!-sortip lengthc prin2 princ),'lose);

% substq has only been built into CSL since July 2011 so I will be cautious
% here for when this file is used with a legacy version of CSL.

if getd 'substq then flag('(substq), 'lose);

!*argnochk := t;

% endmodule;

end;
