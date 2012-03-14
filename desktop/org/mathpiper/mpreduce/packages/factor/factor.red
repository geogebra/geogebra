module factor;  % Header for factorizer.

% Authors: A. C. Norman and P. M. A. Moore, 1981.

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


create!-package('(factor bigmodp degsets facprim facmod facuni % factrr
                  imageset pfactor vecpoly pfacmult),
                nil);

% Other packages needed.

load!-package 'ezgcd;

for each j in get('factor,'package)
    do put(j,'compiletime,'(setq !*fastfor t));

fluid '(!*ifactor !*overview !*trallfac !*trfac factor!-level
        factor!-trace!-list posn!*);

global '(spare!*);

switch ifactor,overview,trallfac,trfac;

comment This factorizer should be used with a system dependent file
containing a setting of the variable LARGEST!-SMALL!-MODULUS. If at all
possible the integer arithmetic operations used here should be mapped
onto corresponding ones available in the underlying Lisp implementation,
and the support for modular arithmetic (perhaps based on these integer
arithmetic operations) should be reviewed.  This file provides
placeholder definitions of functions that are used on some
implementations to support block compilation, car/cdr access checks and
the like.  The front-end files on the systems that can use these
features will disable the definitions given here by use of a 'LOSE flag;

deflist('((minus!-one -1)),'newnam);   % So that it EVALs properly.

symbolic smacro procedure carcheck u; nil;

% symbolic smacro procedure irecip u; 1/u;

% symbolic smacro procedure isdomain u; domainp u;

% symbolic smacro procedure readgctime; gctime();

% symbolic smacro procedure readtime; time()-gctime();

% symbolic smacro procedure ttab n; spaces(n-posn());


%   ***** The remainder of this module used to be in FLUIDS.

% Macro definitions for functions that create and access reduce-type
% datastructures.

% smacro procedure polyzerop u; null u;

smacro procedure didntgo q; null q;

% smacro procedure depends!-on!-var(a,v);
%   (lambda !#!#a; (not domainp !#!#a) and (mvar !#!#a=v)) a;

% smacro procedure l!-numeric!-c(a,vlist); lnc a;

% Macro definitions for use in Berlekamp's algorithm.

% Smacros used in linear equation package.

% smacro procedure getm2(a,i,j);
%    % Store by rows, to ease pivoting process.
%    getv(getv(a,i),j);

% smacro procedure putm2(a,i,j,v);
%    putv(getv(a,i),j,v);

smacro procedure !*f2mod u; u;

smacro procedure !*mod2f u; u;

%%%smacro procedure adjoin!-term (p,c,r);
%%%  (lambda !#c!#; % Lambda binding prevents repeated evaluation of C.
%%%    if null !#c!# then r else (p .* !#c!#) .+ r) c;

symbolic smacro procedure get!-f!-numvec s; cadr cddr cdddr s;

% !*overshoot:=nil; % Default not to show overshoot occurring.

% reconstructing!-gcd:=nil;  % This is primarily a factorizer!

symbolic procedure ttab!* n;
<<if n>(linelength nil - spare!*) then n:=0;
  if posn!* > n then terpri!*(nil);
  while not(posn!*=n) do prin2!* '!  >>;

smacro procedure printstr l; << prin2!* l; terpri!*(nil) >>;

smacro procedure printvar v; printstr v;

smacro procedure prinvar v; prin2!* v;

% smacro procedure display!-time(str,mt);
% Displays the string str followed by time mt (millisecs).
%  << prin2 str; prin2 mt; prin2t " millisecs." >>;

% trace control package.

% smacro procedure trace!-time action; if !*timings then action;

smacro procedure new!-level(n,c); (lambda factor!-level; c) n;

symbolic procedure set!-trace!-factor(n,file);
    factor!-trace!-list:=(n . (if file=nil then nil
                               else open(mkfil file,'output))) .
                                                factor!-trace!-list;

symbolic procedure clear!-trace!-factor n;
  begin
    scalar w;
    w := assoc(n,factor!-trace!-list);
    if w then <<
       if cdr w then close cdr w;
       factor!-trace!-list:=delasc(n,factor!-trace!-list) >>;
    return nil
  end;

symbolic procedure close!-trace!-files();
 << while factor!-trace!-list
       do clear!-trace!-factor(caar factor!-trace!-list);
    nil >>;

endmodule;

end;
