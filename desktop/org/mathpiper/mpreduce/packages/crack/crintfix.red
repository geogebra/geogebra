%********************************************************************
module intfix$  % Further fixes to the integration package.
%********************************************************************
%  Routines to extend the REDUCE integrator or to fix problems
%  Author: Francis Wright
%
%  $Id$
%

if lisp !*comp then apply1('load!-package, 'int)$

fluid '(!*depend !*nolnr !*failhard)$

% die folgende Aenderung verhindert das Erzeugen von int* ...

remd('simpint!*)$

symbolic procedure simpint!* u$
   begin scalar x$
      return if (x := opmtch('int . u)) then simp x
              else simpiden('int . u)
% statt       else simpiden('int!* . u)
   end$

% ein Patch fuer das REDUCE 3.5 EZGCD

%symbolic procedure simpexpt u$
%   % We suppress reordering during exponent evaluation, otherwise
%   % internal parts (as in e^(a*b)) can have wrong order.
%   begin scalar expon;
%      expon := simpexpon carx(cdr u,'expt) where kord!*=nil;
%      expon := resimp expon;  % We still need right order. <--- change.
%      return simpexpt1(car u,expon,nil)
%   end$

% Zum Integrieren

% put('int, 'simpfn, 'SimpIntPatch)$

%algebraic <<
% % fuer reelle Rechnungen:
% let {abs(~r)**(~n) => r**n when (fixp(n) and evenp(n))}$
% let {
% int(1/~x^(~n),~x) => -x/(x^n*(n-1)) when numberp n,
%     ~x^(~m/~n)*~x => x**((m+n)/n) when (numberp n and numberp m),
%     int(~z/~y,~x) => log(y) when z = df(y,x)}$
%
% if sin(!%x)**2+cos(!%x)**2 neq 1 then
% let {sin(~x)**2 => 1-cos(x)**2}$
%
% if cosh(!%x)**2 neq (sinh(!%x)**2 + 1) then
% let {cosh(~x)**2 => (sinh(x)**2 + 1)}$
%
% if sin(!%x)*tan(!%x/2)+cos(!%x) neq 1 then
% let {tan(~x/2) => (1-cos(x))/sin(x)}$
%
% if sin(!%x)*cot(!%x/2)-cos(!%x) neq 1 then
% let {cot(~x/2) => (1+cos(x))/sin(x)}$
%
% if sqrt(!%x**2-!%y**2)-sqrt(!%x-!%y)*sqrt(!%x+!%y) neq 0 then
% let {sqrt(~x)*sqrt(~y) => sqrt(x*y)}
%>>$

endmodule$


module dfint$

% Patch to improve differentiation, mainly of integrals.
% This version specifically for use by the crack package.

% Francis J. Wright <F.J.Wright@QMW.ac.uk>, 27 December 1997

fluid '(!*fjwflag)$  !*fjwflag := t$

switch allowdfint, dfint$               % dfint OFF by default
deflist('((dfint ((t (rmsubs))))
   (allowdfint ((t (progn (put 'int 'dfform 'dfform_int) (rmsubs)))
                (nil (remprop 'int 'dfform))))), 'simpfg)$
   % There is no code to reverse the df-int commutation,
   % so no reason to call rmsubs when the switch is turned off.

!*allowdfint := t$                      % allowdfint ON by default
put('int, 'dfform, 'dfform_int)$

% The switch allowdfint ALLOWS differentiation under the integral sign
% provided the result simplies, and should normally be on.

% The switch dfint FORCES differentiation under the integral sign,
% PROVIDED ALLOWDFINT IS ALSO ON, and should normally be turned on
% only when required.


symbolic procedure diffp(u,v);
   % U is a standard power, V a kernel.
   % Value is the standard quotient derivative of U wrt V.
   begin scalar n,w,x,y,z; integer m;
        n := cdr u;     % integer power.
        u := car u;     % main variable.
        if u eq v and (w := 1 ./ 1) then go to e
         else if atom u then go to f
         %else if (x := assoc(u,dsubl!*)) and (x := atsoc(v,cdr x))
%               and (w := cdr x) then go to e   % deriv known.
             % DSUBL!* not used for now.
         else if (not atom car u and (w:= difff(u,v)))
                  or (car u eq '!*sq and (w:= diffsq(cadr u,v)))
          then go to c  % extended kernel found.
         else if x := get(car u,'dfform) then return apply3(x,u,v,n)
         else if x:= get(car u,dfn_prop u) then nil
         else if car u eq 'plus and (w := diffsq(simp u,v))
          then go to c
         else go to h;  % unknown derivative.
        y := x;
        z := cdr u;
    a:  w := diffsq(simp car z,v) . w;
        if caar w and null car y then go to h;  % unknown deriv.
        y := cdr y;
        z := cdr z;
        if z and y then go to a
         else if z or y then go to h;  % arguments do not match.
        y := reverse w;
        z := cdr u;
        w := nil ./ 1;
    b:  % computation of kernel derivative.
        if caar y
          then w := addsq(multsq(car y,simp subla(pair(caar x,z),
                                                   cdar x)),
                          w);
        x := cdr x;
        y := cdr y;
        if y then go to b;
    c:  % save calculated deriv in case it is used again.
        % if x := atsoc(u,dsubl!*) then go to d
        %  else x := u . nil;
        % dsubl!* := x . dsubl!*;
  % d:   rplacd(x,xadd(v . w,cdr x,t));
    e:  % allowance for power.
        % first check to see if kernel has weight.
        if (x := atsoc(u,wtl!*))
          then w := multpq('k!* .** (-cdr x),w);
        m := n-1;
        % Evaluation is far more efficient if results are rationalized.
        return rationalizesq if n=1 then w
                else if flagp(dmode!*,'convert)
                     and null(n := int!-equiv!-chk
                                           apply1(get(dmode!*,'i2d),n))
                 then nil ./ 1
                else multsq(!*t2q((u .** m) .* n),w);
    f:  % Check for possible unused substitution rule.
        if not depends(u,v)
           and (not (x:= atsoc(u,powlis!*))
                 or not depends(cadddr x,v))
           and null !*depend
          then return nil ./ 1;
        w := list('df,u,v);
        w := if x := opmtch w then simp x else mksq(w,1);
        go to e;
    h:  % Final check for possible kernel deriv.
        if car u eq 'df                 % multiple derivative
          then if depends(cadr u,v)
% FJW - my version of above test was simply as follows.  Surely, inner
% derivative will already have simplied to 0 unless v depends on A!
                        and not(cadr u eq v)
                        % (df (df v A) v) ==> 0
%%            and not(cadr u eq v and not depends(v,caddr u))
%%             % (df (df v A) v) ==> 0 unless v depends on A.
                 then
          <<if !*fjwflag and eqcar(cadr u, 'int) then
              % (df (df (int F x) A) v) ==> (df (df (int F x) v) A) ?
              % Commute the derivatives to differentiate the integral?
              if caddr cadr u eq v then
                 % Evaluating (df u v) where u = (df (int F v) A)
                 % Just return (df F A) - derivative absorbed
                 << w := 'df . cadr cadr u . cddr u;  go to j >>
              else if !*allowdfint and
                 % Evaluating (df u v) where u = (df (int F x) A)
                 % (If dfint is also on then this will not arise!)
                 % Commute only if the result simplifies:
                 not_df_p(w := diffsq(simp!* cadr cadr u, v))
              then <<
                 % Generally must re-evaluate the integral (carefully!)
% FJW.  Bug fix!
                 % w := aeval{'int, mk!*sq w, caddr cadr u} . cddr u;
                 w := 'df . reval{'int, mk!*sq w, caddr cadr u} . cddr u;
                 go to j >>;  % derivative absorbed
           if (x := find_sub_df(w:= cadr u . derad(v,cddr u),
                                           get('df,'kvalue)))
                          then <<w := simp car x;
                                 for each el in cdr x do
                                    for i := 1:cdr el do
                                        w := diffsq(w,car el);
                                 go to e>>
                       else w := 'df . w
                >>
                else if null !*depend then return nil ./ 1
                else w := {'df,u,v}
         else w := {'df,u,v};
   j:   if (x := opmtch w) then w := simp x
         else if not depends(u,v) and null !*depend then return nil ./ 1
         else w := mksq(w,1);
      go to e
   end$


% Author: Francis J. Wright <F.J.Wright@QMW.ac.uk>
% Last revised: 27 December 1997

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


symbolic procedure dfform_int(u, v, n);
   % Simplify a SINGLE derivative of an integral.
   % u = '(int y x) [as main variable of SQ form]
   % v = kernel
   % n = integer power
   % Return SQ form of df(u**n, v) = n*u**(n-1)*df(u, v)
   % This routine is called by diffp via the hook
   % "if x := get(car u,'dfform) then return apply3(x,u,v,n)".
   % It does not necessarily need to use this hook, but it needs to be
   % called as an alternative to diffp so that the linearity of
   % differentiation has already been applied.
   begin scalar result, x, y;
      y := simp!* cadr u;  % SQ form integrand
      x := caddr u;  % kernel
      result :=
      if v eq x then y
         % df(int(y,x), x) -> y       replacing the let rule in INT.RED
      else if not !*intflag!* and       % not in the integrator
         % If used in the integrator it can cause infinite loops,
         % e.g. in df(int(int(f,x),y),x) and df(int(int(f,x),y),y)
         !*allowdfint and               % must be on for dfint to work
            << y := diffsq(y, v);  !*dfint or not_df_p y >>
               % it has simplified
      then simp{'int, mk!*sq y, x}  % MUST re-simplify it!!!
         % i.e. differentiate under the integral sign
         % df(int(y, x), v) -> int(df(y, v), x).
         % (Perhaps I should use prepsq - kernels are normally true prefix?)
      else !*kk2q{'df, u, v};  % remain unchanged
      if not(n eq 1) then
         result := multsq( (((u .** (n-1)) .* n) .+ nil) ./ 1, result);
      return result
   end$

symbolic procedure not_df_p y;
   % True if the SQ form y is not a df kernel.
   not(denr y eq 1 and
       not domainp (y := numr y) and eqcar(mvar y, 'df))$

endmodule$


module intdf$

% Patch to simpint1 in src/int/trans/driver.red to provide better
% simplification of integrals of derivatives.  (I think -- hope --
% this is the right place to hook this patch into the integrator!)
% This patch was motivated by the needs of crack.

% F.J.Wright@Maths.QMW.ac.uk, 31 December 1997

%% load_package int$
%apply1('load!-package, 'int)$           % not at compile time!

switch PartialIntDf$                    % off by default
deflist('((PartialIntDf ((t (rmsubs))))), 'simpfg)$

% If the switch PartialIntDf is turned on then integration by parts is
% performed if the result simplifies in the sense that it integrates a
% symbolic derivative and does not introduce new symbolic derivatives.
% However, because the initial integral contains an unevaluated
% derivative then the result must still contain an unevaluated
% integral.

symbolic procedure simpint1 u;
   % Varstack* rebound, since FORMLNR use can create recursive
   % evaluations.  (E.g., with int(cos(x)/x**2,x)).
   begin scalar !*keepsqrts,v,varstack!*;
      u := 'int . prepsq car u . cdr u;
      if (v := formlnr u) neq u
        then if !*nolnr
               then <<v := simp subst('int!*,'int,v);
                      return remakesf numr v ./ remakesf denr v>>
              else <<!*nolnr := nil . !*nolnr;
                     v:=errorset!*(list('simp,mkquote v),!*backtrace);
                     if pairp v then v := car v else v := simp u;
                     !*nolnr := cdr !*nolnr;
                     return v>>;
      % FJW: At this point linearity has been applied.
      return if (v := opmtch u) then simp v
         % FJW: Check for a directly integrable derivative:
      else if (v := NestedIntDf(cadr u, caddr u)) then mksq(v,1)
      else if !*failhard then rerror(int,4,"FAILHARD switch set")
         % FJW: Integrate by parts if the result simplifies:
      else if !*PartialIntDf and
         (v := PartialIntDf(cadr u, caddr u)) then mksq(v,1)
      else mksq(u,1)
   end$

symbolic procedure NestedIntDf(y, x);
   %% int( ... df(f,A,x,B) ..., x) -> ... df(f,A,B) ...
   %% Find a df(f,A,x,B) among possibly nested int's and df's within
   %% the integrand y in int(y,x), and return the whole structure y
   %% but with the derivative integrated; otherwise return nil.
   %% [A,B are arbitrary sequences of kernels.]
   not atom y and
   begin scalar car_y, nested;
      return
         if (car_y := car y) eq 'df and memq(x, cddr y) then
            %% int( df(f, A, x, B), x ) -> df(f, A, B)
            'df . cadr y . delete(x, cddr y)
               %% use delete for portability!
               %% deleq is defined in CSL, delq in PSL -- oops!
         else if memq(car_y, '(df int)) and
            (nested := NestedIntDf(cadr y, x)) then
            %% int( df(int(df(f, A, x, B), c), C), x ) ->
            %%      df(int(df(f, A, B), c), C)
            %% int( int(df(f, A, x, B), c), x ) ->
            %%      int(df(f, A, B), c)
            car_y . nested . cddr y
   end$

symbolic procedure PartialIntDf(y, x);
   %% int(u(x)*df(v(x),x), x) -> u(x)*v(x) - int(df(u(x),x)*v(x), x)
   %% Integrate by parts if the resulting integral simplifies [to
   %% avoid infinite loops], which means that df(u(x),x) may not
   %% contain any unevaluated derivatives; otherwise return nil.
   not atom y and
   begin scalar denlist, facs, df, u, v;
      if car y eq 'quotient then <<
         denlist := cddr y;
         % y := numerator:
         if atom(y := cadr y) then return % no derivative
      >>;
      % y := list of factors:
      if car y eq 'times then y := cdr y
      else if denlist then y := y . nil
      else return;
      % Find an integrable derivative among the factors:
      facs := y;
      while facs and not
         (eqcar(df := car facs, 'df) and memq(x, cddr df)) do
            facs := cdr facs;
      if null facs then return;         % no integrable derivative
      % Construct u(x) and v(x) [v(x) may still be a derivative]:
      u := delete(df, y);               % list of factors
      u := if null u then 1 else if cdr u then 'times . u else car u;
      if denlist then u := 'quotient . u . denlist;
      v := cadr df;                     % kernel being differentiated
      if (df := delete(x, cddr df)) then v := 'df . v . df;
      % Check that df(u(x),x) simplifies:
      if smemq('df, df := reval {'df,u,x}) then return;
      return reval {'difference,
         {'times,u,v}, {'int, {'times, df, v}, x}}
   end$

endmodule$

end$


