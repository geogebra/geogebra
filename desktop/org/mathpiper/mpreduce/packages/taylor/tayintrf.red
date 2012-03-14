module TayIntrf;

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


%*****************************************************************
%
%      The interface to the REDUCE simplificator
%
%*****************************************************************


exports simptaylor, simpTaylor!*, taylorexpand$

imports

% from the REDUCE kernel:
        !*f2q, aconc!*, denr, depends, diffsq, eqcar, kernp, lastpair,
        leq, lprim, mkquote, mksq, multsq, mvar, neq, nth, numr, over,
        prepsq, revlis, reversip, simp, simp!*, subs2, subsq, typerr,

% from the header module:
        !*tay2q, get!-degree, has!-Taylor!*, has!-TayVars,
        make!-Taylor!*, multintocoefflist, resimptaylor, TayCfPl,
        TayCfSq, TayCoeffList, TayFlags, TayMakeCoeff, TayOrig,
        TayTemplate, TayTpElOrder, TayTpElPoint,
        Taylor!-kernel!-sq!-p, taymincoeff,

% from module Tayintro:
        replace!-nth, Taylor!-error, var!-is!-nth,

% from module TayExpnd:
        taylorexpand,

% from module Tayutils:
        delete!-superfluous!-coeffs,

% from module taybasic:
        invtaylor1, quottaylor1,

% from module Tayconv:
        prepTaylor!*;


fluid '(!*backtrace !*precise !*tayinternal!* !*taylorkeeporiginal !*taylorautocombine
        frlis!* subfg!*);

global '(mul!*);

comment The following statement forces all expressions to be
        re-simplified if the switch `taylorautocombine' is set to on,
        unfortunately, this is not always sufficient;

put ('taylorautocombine, 'simpfg, '((t (rmsubs))));


comment Interface to the fkern mechanism that makes kernels unique and 
        stores them in the klist property;

symbolic procedure tayfkern u;
  begin scalar x,y;
    if !*tayinternal!* then return u;
    % rest of code borrowed from fkern
    y := get('taylor!*,'klist);
    x := assoc(u,y);
    if null x
      then <<x := list(u,nil);
             y := ordad(x,y);
             kprops!* := union('(taylor!*),kprops!*);
             put('taylor!*,'klist,y)>>;
    return x
  end;

put('taylor!*, 'fkernfn, 'tayfkern);


symbolic procedure simptaylor u;
  %
  % (PrefixForm) -> s.q.
  %
  % This procedure is called directly by the simplifier.
  % Its argument list must be of the form
  %     (exp, [var, var0, deg, ...]),
  % where [...] indicate one or more occurences.
  % This means that exp is to be expanded w.r.t var about var0
  % up to degree deg.
  % var may also be a list of variables, which means that the
  % the expansion takes place in a homogeneous way.
  % If var0 is the special atom infinity var is replaced by 1/var
  % and the result expanded about 0.
  %
  % This procedure returns the input if it cannot expand the expression.
  %
  if remainder(length u,3) neq 1
    then Taylor!-error('wrong!-no!-args,'taylor)
   else if null subfg!* then mksq('taylor . u,1)
   else begin scalar !*precise,arglist,degree,f,ll,result,var,var0;
     %
     % Allow automatic combination of Taylor kernels.
     %
     if !*taylorautocombine and not ('taysimpsq memq mul!*)
       then mul!* := aconc!*(mul!*,'taysimpsq);
     %
     % First of all, call the simplifier on exp (i.e. car u),
     %
     f := simp!* car u;
     u := revlis cdr u; % reval instead of simp!* to handle lists
     arglist := u;
     %
     % then scan the rest of the argument list.
     %
     while not null arglist do
       << var := car arglist;
          var := if eqcar(var,'list) then cdr var else {var};
          % In principle one should use !*a2k
          % but typerr (maprin) does not correctly print the atom nil
          for each el in var collect begin
            el := simp!* el;
            if kernp el then return mvar numr el
             else typerr(prepsq el,'kernel)
            end;
          var0 := cadr arglist;
          degree := caddr arglist;
          if not fixp degree
            then typerr(degree,"order of Taylor expansion");
          arglist := cdddr arglist;
          ll := {var,var0,degree,degree + 1} . ll>>;
     %
     % Now ll is a Taylor template, i.e. of the form
     %  ((var_1 var0_1 deg1 next_1) (var_2 var0_2 deg_2 next_2) ...)
     %
     result := taylorexpand(f,reversip ll);
     %
     % If the result does not contain a Taylor kernel, return the input.
     %
     return if has!-Taylor!* result then result
             else mksq('taylor . prepsq f . u,1)
   end;

put('taylor,'simpfn,'simptaylor)$


%symbolic procedure taylorexpand (f, ll);
%  %
%  % f is a s.q. that is expanded according to the list ll
%  %  which has the form
%  %  ((var_1 var0_1 deg1) (var_2 var0_2 deg_2) ...)
%  %
%  begin scalar result;
%    result := f;
%    for each el in ll do
%      %
%      % taylor1 is the function that does the real work
%      %
%      result := !*tay2q taylor1 (result, car el, cadr el, caddr el);
%      if !*taylorkeeporiginal then set!-TayOrig (mvar numr result, f);
%      return result
%  end$


symbolic procedure taylor1 (f, varlis, var0, n);
  %
  % Taylor expands s.q. f w.r.t. varlis about var0 up to degree n.
  % var is a list of kernels, which means that the expansion
  % takes place in a homogeneous way if there is more than one
  % kernel.
  % If var0 is the special atom infinity all kernels in varlis are
  % replaced by 1/kernel.  The result is then expanded about 0.
  %
  taylor1sq (if var0 eq 'infinity
               then subsq (f,
                           for each krnl in varlis collect
                             (krnl . list ('quotient, 1, krnl)))
              else f,
             varlis, var0, n)$

symbolic procedure taylor1sq (f, varlis, var0, n);
  %
  % f is a standard quotient, value is a Taylor kernel.
  %
  % First check if it is a Taylor kernel
  %
  if Taylor!-kernel!-sq!-p f
    then if has!-TayVars(mvar numr f,varlis)
           %
           % special case: f has already been expanded w.r.t. var.
           %
           then taylorsamevar (mvar numr f, varlis, var0, n)
          else begin scalar y, z;
            f := mvar numr f;
            %
            % taylor2 returns a list of the form
            %  ((deg1 . coeff1) (deg2 . coeff2) ... )
            % apply this to every coefficient in f.
            % car cc is the degree list of this coefficient,
            % cdr cc the coefficient itself.
            % Finally collect the new pairs
            %  (degreelist . coefficient)
            %
            z :=
              for each cc in TayCoeffList f join
                for each cc2 in taylor2 (TayCfSq cc, varlis, var0, n)
                  collect
                    TayMakeCoeff (append (TayCfPl cc, TayCfPl cc2),
                                  TayCfSq cc2);
            %
            % Append the new list to the Taylor template and return.
            %
            y := append(TayTemplate f,list {varlis,var0,n,n+1});
            return make!-Taylor!* (z, y, TayOrig f, TayFlags f)
            end
   %
   % Last possible case: f is not a Taylor expression.
   % Expand it.
   %
   else make!-Taylor!* (
                 taylor2 (f, varlis, var0, n),
                 list {varlis,var0,n,n+1},
                 if !*taylorkeeporiginal then f else nil,
                 nil)$

symbolic procedure taylor2 (f, varlis, var0, n);
  begin scalar result,oldklist;
    oldklist := get('Taylor!*,'klist);
    result := errorset (list ('taylor2e,
                               mkquote f,
                               mkquote varlis,
                               mkquote var0,
                               mkquote n),
                        nil, !*backtrace);
    put('Taylor!*,'klist,oldklist);
    if atom result
      then Taylor!-error ('expansion, "(possible singularity!)")
     else return car result
  end$

symbolic procedure taylor2e (f, varlis, var0, n);
  %
  % taylor2e expands s.q. f w.r.t. varlis about var0 up to degree n.
  % var is a list of kernels, which means that the expansion takes
  % place in a homogeneous way if there is more than one kernel.
  % The case that var0 is the special atom infinity has to be taken
  % care of by the calling function(s).
  % Expansion is carried out separately for numerator and
  % denominator.  This approach has the advantage of not producing
  % complicated s.q.'s which usually appear if an s.q. is
  % differentiated.  The procedure is (roughly) as follows:
  %  if the denominator of f is free of var
  %   then expand the numerator and divide,
  %  else if the numerator is free of var expand the denominator,
  %   take the reciprocal of the Taylor series and multiply,
  %  else expand both and divide the two series.
  % This fails if there are nontrivial dependencies, e.g.,
  %  if a variable is declared to depend on a kernel in varlis.
  % It is of course necessary that the denominator yields a unit
  %  in the ring of Taylor series. If not, an error will be
  %  signalled by invtaylor or quottaylor, resp.
  %
  if cdr varlis then taylor2hom (f, varlis, var0, n)
   else if denr f = 1 then taylor2f (numr f, car varlis, var0, n, t)
   else if not depends (denr f, car varlis)
    then multintocoefflist (taylor2f (numr f, car varlis, var0, n, t),
                            1 ./ denr f)
   else if numr f = 1
    then delete!-superfluous!-coeffs
           (invtaylor1 ({varlis,var0,n,n+1},
                        taylor2f (denr f, car varlis, var0, n, nil)),
            1, n)
   else if not depends (numr f, car varlis)
    then multintocoefflist
           (invtaylor1 ({varlis,var0,n,n+1},
                        taylor2f (denr f, car varlis, var0, n, nil)),
            !*f2q numr f)
   else begin scalar denom; integer n1;
     denom := taylor2f (denr f, car varlis, var0, n, nil);
     n1 := n + taymincoeff denom;
     return
       delete!-superfluous!-coeffs
         (quottaylor1 ({varlis,var0,n1,n1+1},
                       taylor2f (numr f, car varlis, var0, n1, t),
                       denom),
          1, n)
  end$

symbolic procedure taylor2f (f, var, var0, n, flg);
  %
  % taylor2f is the procedure that does the actual expansion
  % of the s.f. f.
  % It returns a list of the form
  %  ((deglis1 . coeff1) (deglis2 . coeff2) ... )
  % For the use of the parameter `flg' see below.
  %
  begin scalar x, y, z; integer k;
    %
    % Calculate once what is needed several times.
    % var0 eq 'infinity is a special case that has already been taken
    % care of by the calling functions by replacing var by 1/var.
    %
    if var0 eq 'infinity then var0 := 0;
    x := list (var . var0);
    y := simp list ('difference, var, var0);
    %
    % The following is a first attempt to treat expressions
    % that have poles at the expansion point.
    % Currently nothing more than factorizable poles, i.e.
    % factors in the denominator are handled.
    % We use the following trick to calculate enough terms: If the
    % first l coefficients of the Taylor series are zero we replace n
    % by n + 2l.  This is necessary since we separately expand
    % numerator and denominator of an expression.  If the expansion of
    % both parts starts with, say, the quadratic term we have to
    % expand them up to order (n+2) to get the correct result up to
    % order n. However, if the numerator starts with a constant term
    % instead, we have to expand up to order (n+4).  It is important,
    % however, to drop the additional coefficients as soon as they are
    % no longer needed.  The parameter `flg' is used here to control
    % this behaviour.  The calling function must pass the value `t' if
    % it wants to inhibit the calculation of additional coefficients.
    % This is currently the case if the s.q. f has a denominator that
    % may contain the expansion variable.  Otherwise `flg' is used to
    % remember if we already encountered a non-zero coefficient.
    %
    f := !*f2q f;
    z := subs2 subsq (f, x);
    if null numr z and not flg then n := n + 1 else flg := t;
    y := list TayMakeCoeff ((list list 0), z);
    k := 1;
    while k <= n and not null numr f do
      << f := multsq (diffsq (f, var), 1 ./ k);
                                             % k is always > 0!
         % subs2 added to simplify expressions involving roots
         z := subs2 subsq (f, x);
         if null numr z and not flg then n := n + 2 else flg := t;
         if not null numr z then y := TayMakeCoeff(list list k, z) . y;
         k := k + 1 >>;
    return reversip y
  end;

symbolic procedure taylor2hom (f, varlis, var0, n);
  %
  % Homogeneous expansion of s.q. f wrt the variables in varlis,
  % i.e. the sum of the degrees of the kernels is varlis is <= n
  %
  if null cdr varlis then taylor2e (f, list car varlis, var0, n)
   else for each u in taylor2e (f, list car varlis, var0, n) join
     for each v in taylor2hom (cdr u,
                               cdr varlis,
                               var0,
                               n - get!-degree TayCfPl car u)
           collect list (car TayCfPl car u . TayCfPl car v) . cdr v$

symbolic procedure taylorsamevar (u, varlis, var0, n);
  begin scalar tp; integer mdeg, pos;
    if cdr varlis
      then Taylor!-error ('not!-implemented,
                          "(homogeneous expansion in TAYLORSAMEVAR)");
    tp := TayTemplate u;
    pos := car var!-is!-nth (tp, car varlis);
    tp := nth (tp, pos);
    if TayTpElPoint tp neq var0
      then return taylor1 (if not null TayOrig u then TayOrig u
                            else simp!* prepTaylor!* u,
                           varlis, var0, n);
    mdeg := TayTpElOrder tp;
    if n=mdeg then return u
     else if n > mdeg
      %
      % further expansion required
      %
      then << lprim "Cannot expand further... truncated.";
              return u >> ;
    return make!-Taylor!* (
        for each cc in TayCoeffList u join
          if nth (nth (TayCfPl cc, pos), 1) > n
            then nil
           else list cc,
        replace!-nth(TayTemplate u,pos,
                      {varlis,TayTpElPoint tp,n,n+1}),
        TayOrig u, TayFlags u)
  end$


comment The `FULL' flag causes the whole term (including the
        Taylor!* symbol) to be passed to SIMPTAYLOR!* ;

symbolic procedure simpTaylor!* u;
  if TayCoefflist u memq frlis!* or eqcar(TayCoefflist u,'!~)
    then !*tay2q u
   else <<
     %
     % Allow automatic combination of Taylor kernels.
     %
     if !*taylorautocombine and not ('taysimpsq memq mul!*)
       then mul!* := aconc!* (mul!*, 'taysimpsq);
     !*tay2q resimptaylor u >>$

flag ('(Taylor!*), 'full)$

put ('Taylor!*, 'simpfn, 'simpTaylor!*)$

comment The following is necessary to properly process Taylor kernels
        in substitutions;

flag ('(Taylor!*), 'simp0fn);

endmodule;

end;
