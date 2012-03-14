module compopr;   % Operators on Complex Expressions.

% Author: Eberhard Schruefer.
% Modifications by:  Francis Wright.

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


fluid '(!*exp !*factor kord!*);

put('repart,'simpfn,'simprepart);

symbolic procedure simprepart u;
   repartsq simp!* car u where !*factor = nil;

symbolic procedure repartsq u;
   multsq(addsq(multsq(repartnum,repartden),
                multsq(impartnum,impartden)),
          invsq addsq(multsq(repartden,repartden),
                      multsq(impartden,impartden)))
   where repartnum = car reimnum, impartnum = cdr reimnum,
         repartden = car reimden, impartden = cdr reimden
   where reimnum = splitcomplex numr u,
         reimden = splitcomplex denr u;

put('impart,'simpfn,'simpimpart);

symbolic procedure simpimpart u;
   impartsq simp!* car u where !*factor = nil;

symbolic procedure impartsq u;
   multsq(addsq(multsq(impartnum,repartden),
                negsq multsq(repartnum,impartden)),
          invsq addsq(multsq(repartden,repartden),
                      multsq(impartden,impartden)))
   where repartnum = car reimnum, impartnum = cdr reimnum,
         repartden = car reimden, impartden = cdr reimden
   where reimnum = splitcomplex numr u,
         reimden = splitcomplex denr u;

put('conj,'simpfn,'simpconj);

symbolic procedure simpconj u;
   conjsq simp!* car u;

symbolic procedure conjsq u;
  (if null numr w then u else addsq(repartsq u,negsq multsq(simp 'i,w)))
   where w=impartsq u;

smacro procedure idomainp; get('i,'idvalfn);
  % Tests if 'i' is transformed to a domain structure.

symbolic procedure splitcomplex u;
   (begin scalar v;
      v := if idomainp() then expand!-imrepart u
            else <<if smemq('i,u) then
                   <<setkorder('i . kord!*); u:=reorder u>>;
                   subs2 expand!-imrepart u>>;
      return take!-realpart v . take!-impart v
    end) where kord!* = kord!*,!*exp = t;

symbolic procedure expand!-imrepart u;
   if domainp u then u ./ 1
    else addsq(multsq(expand!-imrepartpow lpow u,
                      expand!-imrepart lc u),
               expand!-imrepart red u);

symbolic procedure expand!-imrepartpow u;
   % This needs to treat kernels that are standard forms smarter.
   % At the moment, we expand to get the required result.
   begin scalar !*exp,cmpxsplitfn;
     !*exp := t;
     cmpxsplitfn := null idp car u and
                    get(car car u,'cmpxsplitfn);
     return
       exptsq(if null cmpxsplitfn
                 then if car u eq 'i then !*k2q 'i
                       else addsq(mkrepart car u,
                                  multsq(simp 'i,
                                         mkimpart car u))
               else apply1(cmpxsplitfn,car u),cdr u)
    end;

symbolic procedure mkrepart u;
   if realvaluedp u then !*k2q u
    else if sfp u then repartsq(u ./ 1)
    else mksq(list('repart, u),1);

symbolic procedure mkimpart u;
   if realvaluedp u then nil ./ 1
    else if sfp u then impartsq(u ./ 1)
    else mksq(list('impart, u),1);

symbolic procedure take!-realpart u;
   repartf numr u ./ denr u;

symbolic procedure repartf u;
   % We can't check for null dmode!* as there may still be complex
   % domain elements in the expression (e.g., e^repart x).
  (if domainp u
      then if atom u then u
            else if get(car u,'cmpxfn)
            % We now know u is of form (<tag> <re> . <im>).
              then int!-equiv!-chk(car u . cadr u .
                        cadr apply1(get(car u,'i2d),0))
        % Otherwise we assume it is real
       else u
    else if mvar u eq 'i then repartf red u
%    else if null dmode!* then addf(!*t2f lt u,repartf red u)
    else addf(multpf(lpow u,repartf lc u),repartf red u))
       where u = reorder u where kord!* = 'i . kord!*;

symbolic procedure take!-impart u;
   impartf numr u ./ denr u;

symbolic procedure impartf u;
   % We can't check for null dmode!* as there may still be complex
   % domain elements in the expression.
  (if domainp u
     then if atom u then nil
           else if get(car u,'cmpxfn)
            % We now know u is of form (<tag> <re> . <im>).
             then int!-equiv!-chk(car u . cddr u .
                                  cadr apply1(get(car u,'i2d),0))
        % Otherwise we assume it is real
       else nil
    else if mvar u eq 'i then addf(lc u,impartf red u)
%   else if null dmode!* then impartf red u
    else addf(multpf(lpow u,impartf lc u),impartf red u))
       where u = reorder u where kord!* = 'i . kord!*;

% The following code attempts to improve the way that the complex
% operators CONJ, REPART and IMPART handle values that are implicitly
% real, namely composed "reality-preserving" functions of explicitly
% real numbers, implicitly real symbolic constants and variables that
% the user has declared using the REALVALUED command defined below.

% All arithmetic operations, direct trig functions and the exponential
% function are "reality-preserving", but inverse trig functions and the
% logarithm are "reality-preserving" only for real arguments in a
% restricted range.  This relates to piecewise-defined functions!  This
% code is believed to make the right decision about implicit reality in
% straightforward cases, and otherwise errs on the side of caution and
% makes no assumption at all, as does the standard REDUCE 3.4 code.  It
% performs only very limited numerical evaluation, which should be very
% fast.  It never performs any approximate numerical evaluation, or any
% sophisticated analysis, both of which would be much slower and/or
% complicated.  The current strategy is believed to represent a
% reasonable compromise, and will normally give the user what they
% expect without undue overhead.

rlistat '(realvalued notrealvalued);   % Make user operators.

symbolic procedure realvalued u;
   % Command to allow the user to declare functions or variables to be
   % implicitly real valued.
   <<rmsubs();  % So that an expression can be re-evaluated.
     for each v in u do
        if not idp v then typerr(v,"id")
         else flag(list v,'realvalued)>>;

symbolic procedure notrealvalued u;
   % Undo realvalued declaration.
   % Cannot recover "complexity", so no need for rmsubs().
   for each v in u do
      if not idp v then typerr(v,"id")
       else remflag(list v, 'realvalued);

flag('(realvaluedp),'boolean); % Make realvaluedp available in
                               % algebraic mode.

symbolic procedure realvaluedp u;
   % True if the true prefix kernel form u is explicitly or implicitly
   % real-valued.
   if atom u then numberp u or flagp(u, 'realvalued)
   else begin scalar caru; % cnd
     return
      flagp((caru := car u), 'alwaysrealvalued)
         % real-valued for all possible argument values
      or (flagp(caru, 'realvalued) and realvaluedlist cdr u)
         % real-valued function if arguments are real-valued,
         % an important common special case of condrealvalued.
%%      or ((cnd := get(caru, 'condrealvalued)) and apply(cnd, cdr u))
         % real-valued function if arguments satisfy conditions
         % that depend on the function
      or caru eq '!:rd!:;  % rounded number - least likely?
   end;

symbolic procedure realvaluedlist u;
   % True if every element of the list u of true prefix kernel forms
   % is real-valued.
   realvaluedp car u and (null cdr u or realvaluedlist cdr u);

% Define the real valued properties
% ---------------------------------

% Only operators that can remain symbolic need be considered,
% e.g. NOT nextprime, num, den, deg, det.

% A very small number of functions are real-valued for ALL arguments:

flag('(repart impart abs ceiling floor fix round max min),
     'alwaysrealvalued);

% Symbolic constants:

flag('(pi e infinity),'realvalued);

% Some functions are real-valued if all their arguments are
% real-valued, without further constraints:

% Arithmetic operators:

flag('(plus minus times quotient), 'realvalued);

% Elementary transcendental functions, etc:

flag('(exp cbrt hypot sin cos tan csc sec cot sind cosd tand cscd secd
       cotd sinh cosh tanh csch sech coth atan atand atan2 atan2d acot
       acotd asinh acsch factorial),
     'realvalued);

% Additional such variables and functions can be declared by the user
% with the REALVALUED command defined above.


put('sin,'cmpxsplitfn,'reimsin);

symbolic procedure reimsin u;
   addsq(multsq(simp list('sin,rearg),
                simp list('cosh,imarg)),
         multsq(simp 'i,
                multsq(simp list('cos,rearg),
                       simp list('sinh,imarg))))
   where rearg = prepsq simprepart cdr u,
         imarg = prepsq simpimpart cdr u;

put('cos,'cmpxsplitfn,'reimcos);

symbolic procedure reimcos u;
   addsq(multsq(simp list('cos,rearg),
                simp list('cosh,imarg)),
         multsq(simp 'i,negsq
                multsq(simp list('sin,rearg),
                       simp list('sinh,imarg))))
   where rearg = prepsq simprepart cdr u,
         imarg = prepsq simpimpart cdr u;

put('expt,'cmpxsplitfn,'reimexpt);

symbolic procedure reimexpt u;
   if cadr u eq 'e
     then addsq(reimcos list('cos,reval list('times,'i,caddr u)),
                multsq(simp list('minus,'i),
                    reimsin list('sin,reval list('times,'i,caddr u))))
    else if fixp cadr u and cadr u > 0
              and eqcar(caddr u,'quotient)
              and fixp cadr caddr u
              and fixp caddr caddr u
     then mksq(u,1)
    else addsq(mkrepart u,multsq(simp 'i,mkimpart u));

endmodule;

end;
