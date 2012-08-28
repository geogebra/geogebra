module csl;  % Support for fast floating point arithmetic in CSL.

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


imports ash, ash1, logand, msd;

exports msd!:;

fluid '(!!nbfpd);

remflag ('(fl2bf msd!: fix2 rndpwr timbf),'lose);

symbolic smacro procedure fix2 u; fix u;

symbolic smacro procedure lshift(m,d); ash(m,d);

symbolic smacro procedure ashift(m,d); ash1(m,d);

symbolic smacro procedure land(a,b); logand(a,b);

symbolic smacro procedure msd!: u; msd u;

symbolic smacro procedure make!:ibf (mt, ep);
  '!:rd!: . (mt . ep);

fluid '(!:bprec!:);

symbolic smacro procedure rndpwr j;
  begin
    scalar !#w;   % I use an odd name here to avoid clashes (smacro)
%   !#w := mt!: j;
    !#w := cadr j;
    if !#w = 0 then return make!:ibf(0, 0);
    !#w := inorm(!#w, !:bprec!:);
%   return make!:ibf(car !#w, cdr !#w + ep!: j)
    return make!:ibf(car !#w, cdr !#w + cddr j)
  end;

% This is introduced as a privately-named function and an associated
% smacro to avoid unwanted interactions between 3 versions of this
% function: the one here, the version of this code compiled into C, and
% the original version in arith.red.  Note thus that CSL_normbf is not
% flagged as 'lose here (but it will be when a version compiled into
% C exists), and the standard version of normbf will still get compiled
% in arith.red, but all references to it will get turned into calls
% to CSL_normbf.  The SMACRO does not need a 'lose flag either.

symbolic procedure CSL_normbf x;
   begin
      scalar mt,s;
      integer ep;
% Note I write out mt!: and ep!: here because the smacros for them are
% not yet available.
      if (mt := cadr x)=0 then return '(!:rd!: 0 . 0);
      if mt<0 then <<mt := -mt; s := t>>;
      ep := lsd mt;
      mt := lshift(mt, -ep);
      if s then mt := -mt;
      ep := ep + cddr x;
      return make!:ibf(mt,ep)
   end;

!#if (not (memq 'vsl lispsystem!*))

symbolic smacro procedure normbf x; CSL_normbf x;

symbolic procedure CSL_timbf(u, v);
  begin
     scalar m;
%    m := mt!: u * mt!: v;
     m := cadr u * cadr v;
     if m = 0 then return '(!:rd!: 0 . 0);
     m := inorm(m, !:bprec!:);
%    return make!:ibf(car m, cdr m + ep!: u + ep!: v)
     return make!:ibf(car m, cdr m + cddr u + cddr v)
  end;

symbolic smacro procedure timbf(u, v); CSL_timbf(u, v);

!#endif

symbolic procedure fl2bf x;
  begin scalar u;
    u := frexp x;
    x := cdr u; % mantissa between 0.5 and 1
    u := car u; % exponent
    x := fix(x*2**!!nbfpd);
    return normbf make!:ibf(x,u-!!nbfpd)
  end;

!#if (memq 'vsl lispsystem!*)
flag ('(fl2bf msd!: fix2), 'lose);
!#else
flag ('(fl2bf msd!: fix2 rndpwr timbf), 'lose);
!#endif

set!-print!-precision 6;

% The following definition is appropriate for MSDOS, and the value of
% !!maxbflexp should be OK for all IEEE systems. BEWARE if you have a
% computer with non-IEEE arithmetic, and worry a bit about !!flexperr
% (which is hardly ever used anyway...).
% I put this here to avoid having arith.red do a loop that is terminated
% by a floating point exception, since as of Nov 1994 CSL built using
% Watcom C 10.0a can not recover from such errors more than (about) ten
% times in any one run - this avoids that during system building.

global '(!!flexperr !!!~xx !!maxbflexp);

remflag('(find!!maxbflexp), 'lose);

symbolic procedure find!!maxbflexp();
  << !!flexperr := t;
     !!!~xx := expt(2.0, 1023);
     !!maxbflexp := 1022 >>;

flag('(find!!maxbflexp), 'lose);

remflag('(copyd), 'lose);

symbolic procedure copyd(new,old);
% Copy the function definition from old id to new.
   begin scalar x;
      x := getd old;
% If loading with !*savedef = '!*savedef then the actual definitions
% do not get loaded, but the source forms do...
      if null x then <<
        if not (!*savedef = '!*savedef)
          then rerror('rlisp,1,list(old,"has no definition in copyd"))>>
      else << putd(new,car x,cdr x);
              if flagp(old, 'lose) then flag(list new, 'lose) >>;
% The transfer of the saved definition is needed if the REDUCE "patch"
% mechanism is to work fully properly.
      if (x := get(old, '!*savedef)) then put(new, '!*savedef, x);
      return new
   end;

flag('(copyd), 'lose);

smacro procedure int2id x; compress list('!!, x);
smacro procedure id2int x; car explode2n x;

smacro procedure bothtimes x; eval!-when((compile load eval), x);
smacro procedure compiletime x; eval!-when((compile eval), x);
smacro procedure loadtime x; eval!-when((load eval), x);

smacro procedure csl x; x;
smacro procedure psl x; nil;

symbolic macro procedure printf u;
  list('printf1, cadr u, 'list . cddr u);

symbolic procedure printf1(fmt, args);
% this is the inner works of print formatting.
% the special sequences that can occur in format strings are
%       %b    do that many spaces
%       %c    next arg is a numeric character code. display character
%       %d    print an integer (actually just the same as %w)
% *     %f    do a terpri() unless posn()=0
%       %l    prin2 items from given list, blank separated
% *     %n    do a terpri()
%       %o    print in octal
%       %p    print using prin1
%       %t    do a ttab to move to given column
%       %w    use prin2
%       %x    print in hexadecimal
% *     %%    print a '%' character (items marked * do not use an arg).
% All except those marked with "*" use an argument.
  begin
    scalar a, c;
    fmt := explode2 fmt;
    while fmt do <<
      c := car fmt;
      fmt := cdr fmt;
      if c = '!% then <<
         c := car fmt;
         fmt := cdr fmt;
         if c = '!f then << if not zerop posn() then terpri() >>
         else if c = '!n then terpri()
         else if c = '!% then prin2 c
         else <<
            a := car args;
            args := cdr args;
            if c = '!b then spaces a
            else if c = '!c then tyo a
            else if c = '!l then <<
               if not atom a then <<
                  prin2 car a;
                  for each w in cdr a do << prin2 " "; prin2 w >> >> >>
            else if c = '!o then prinoctal a
            else if c = '!p then prin1 a
            else if c = '!t then ttab a
            else if c = '!w or c = '!d or c = '!s then prin2 a
            else if c = '!x then prinhex a
            else rerror('cslrend,1,list(c,"bad format character")) >> >>
      else prin2 c >>
  end;

% The format options with bldmsg are intended to match those used
% with printf. If I had make!-string!-output!-stream() available in
% Standard Lisp mode it would let me use one copy of this code and things
% would thus be tidier!

symbolic macro procedure bldmsg u;
  list('bldmsg1, cadr u, 'list . cddr u);

symbolic procedure bldstring r;
% Could possibly be (list!-to!-string nreverse r) ???
  begin
    scalar w;
    w := '(!");
    while r do <<
       w := car r . w;
       if car r eq '!" then w := '!" . w;
       r := cdr r >>;
    return compress ('!" . w)
  end;

symbolic procedure bldcolumn(s, n);
  if null s or eqcar(s, !$eol!$) then n
  else bldcolumn(cdr s, n+1);

symbolic procedure bldmsg1(fmt, args);
  begin
    scalar a, c, r;
    fmt := explode2 fmt;
    while fmt do <<
      c := car fmt;
      fmt := cdr fmt;
      if c = '!% then <<
         c := car fmt;
         fmt := cdr fmt;
         if c = '!f then <<
             if not zerop bldcolumn(r, 0) then r := !$eol!$ . r >>
         else if c = '!n then r := !$eol!$ . r
         else if c = '!% then r := c . r
         else <<
            a := car args;
            args := cdr args;
            if c = '!b then for i := 1:a do r := '!  . r
            else if c = '!c then r := a . r
            else if c = '!l then <<
               if not atom a then <<
                  r := append(reverse explode2 car a, r);
                  for each w in cdr a do <<
                     r := '!  . r;
                     r := append(reverse explode2 w, r) >> >> >>
            else if c = '!o then r := append(reverse explodeoctal a, r)
            else if c = '!p then r := append(reverse explode a, r)
            else if c = '!t then while bldcolumn(r, 0)<a do r := '!  . r
            else if c = '!w or c = '!d or c = '!s
             then r := append(reverse explode2 a, r)
            else if c = '!x then r := append(reverse explodehex a, r)
            else rerror('cslrend,1,list(c,"bad format character")) >> >>
      else r := c . r >>;
    return bldstring r
  end;

put('gc, 'simpfg, '((t (verbos t)) (nil (verbos nil))));

switch gc;

endmodule;

end;
