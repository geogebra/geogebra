module simpsqrt;   % Simplify square roots.

% Authors: Mary Ann Moore and Arthur C. Norman.
% Heavily modified by J.H. Davenport for algebraic functions.

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


fluid '(!*galois !*pvar !*tra !*trint basic!-listofallsqrts
        gaussiani basic!-listofnewsqrts intvar knowntobeindep
        listofallsqrts listofnewsqrts sqrtflag sqrtlist
        sqrt!-places!-alist varlist zlist);

% This module should be rewritten in terms of the REDUCE function
% SIMPSQRT.

% remd 'simpsqrt;

exports proper!-simpsqrt,simpsqrti,simpsqrtsq,simpsqrt2,sqrtsave,
        newplace,actualsimpsqrt,formsqrt;

symbolic procedure proper!-simpsqrt(exprn);
   simpsqrti carx(exprn,'proper!-simpsqrt);


symbolic procedure simpsqrti sq;
begin
   scalar u;
   if atom sq
     then if numberp sq
       then return (simpsqrt2 sq) ./ 1
       else if (u:=get(sq,'avalue))
         then return simpsqrti cadr u
           % BEWARE!!! This is VERY system dependent.
         else return simpsqrt2((mksp(sq,1) .* 1) .+ nil) ./ 1;
           % If it doesn't have an AVALUE then it is itself.
   if car sq eq 'times
     then return mapply(function multsq,
                        for each j in cdr sq collect simpsqrti j);
   if car sq eq 'quotient
     then return multsq(simpsqrti cadr sq,
                        invsq simpsqrti caddr sq);
   if car sq eq 'expt and numberp caddr sq
     then if evenp caddr sq
       then return simpexpt list(cadr sq,caddr sq / 2)
       else return simpexpt
                     list(mk!*sq simpsqrti cadr sq,caddr sq);
  if car sq = '!*sq
    then return simpsqrtsq cadr sq;
  return simpsqrtsq tidysqrt simp!* sq
  end;


symbolic procedure simpsqrtsq sq;
(simpsqrt2 numr sq) ./ (simpsqrt2 denr sq);


symbolic procedure simpsqrt2 sf;
if minusf sf
  then if sf iequal -1
    then gaussiani
    else begin
      scalar u;
      u:=negf sf;
      if numberp u
        then return multf(gaussiani,simpsqrt3 u);
      % we cannot negate general expressions for the following reason:
%       (%%%thesis remark%%%)
%       sqrt(x*x-1) under x->1/x gives sqrt(1-x*x)/x=i*sqrt(x*x-1)/x
%                 under x->1/x gives x*i*sqrt(-1+1/x*x)=i**2*sqrt(x*x-1)
%       hence an abysmal catastrophe.
      return simpsqrt3 sf
      end
  else simpsqrt3 sf;


symbolic procedure simpsqrt3 sf;
begin
  scalar u;
  u:=assoc(sf,listofallsqrts);
  if u
    then return cdr u;
  % now see if 'knowntobeindep'can help.
  u:=atsoc(listofnewsqrts,knowntobeindep);
  if null u
    then go to no;
  u:=assoc(sf,cdr u);
  if u
    then <<
      listofallsqrts:=u.listofallsqrts;
      return cdr u >>;
no:
  u:=actualsimpsqrt sf;
  listofallsqrts:=(sf.u).listofallsqrts;
  return u
  end;


symbolic procedure sqrtsave(u,v,place);
begin
  scalar a;
  %u is new value of listofallsqrts, v of new.
  a:=assoc(place,sqrt!-places!-alist);
  if null a
    then sqrt!-places!-alist:=(place.(listofnewsqrts.listofallsqrts))
           .sqrt!-places!-alist
    else rplacd(a,listofnewsqrts.listofallsqrts);
      listofnewsqrts:=v;
      % throw away things we are not going to need in future.
      if not !*galois
        then listofallsqrts:=u;
        % we cannot guarantee the validity of our calculations.
      if listofallsqrts eq u
        then return nil;
      v:=listofallsqrts;
      while not (cdr v eq u) do
        v:=cdr v;
      rplacd(v,nil);
      % listofallsqrts is all those added since routine was entered.
      v:=atsoc(listofnewsqrts,knowntobeindep);
      if null v
        then knowntobeindep:=(listofnewsqrts.listofallsqrts)
                              . knowntobeindep
        else rplacd(v,union(cdr v,listofallsqrts));
      listofallsqrts:=u;
      return nil
  end;


symbolic procedure newplace(u);
% Says to restart algebraic bases at a new place u.
begin
  scalar v;
  v:=assoc(u,sqrt!-places!-alist);
  if null v
    then <<
      listofallsqrts:=basic!-listofallsqrts;
      listofnewsqrts:=basic!-listofnewsqrts >>
    else <<
      v:=cdr v;
      listofnewsqrts:=car v;
      listofallsqrts:=cdr v >>;
  return if v then v
              else listofnewsqrts.listofallsqrts
  end;

symbolic procedure mknewsqrt u;
   % U is prefix form.
   begin scalar v,w;
     if not !*galois then go to new;
       % no checking required.
     v:=addf(!*p2f mksp(!*pvar,2),negf !*q2f tidysqrt simp u);
     w:=errorset!*(list('afactor,mkquote v,mkquote !*pvar),t);
     if atom w then go to new
       else w:=car w; % The actual result of afactor.
     if cdr w then go to notnew;
   new:
     w := mksqrt reval u;  % Note that u need not be a canonical
                           % structure here.
     listofnewsqrts:=w . listofnewsqrts;
     return !*kk2f w;
   notnew:
     w:=car w;
     v:=stt(w,!*pvar);
     if car v neq 1 then errach list("Error in mknewsqrt: ",v);
     w:=addf(w,multf(cdr v,(mksp(!*pvar,car v) .* -1) .+nil));
     v:=sqrt2top(w ./ cdr v);
     w:=quotf(numr v,denr v);
     if null w
       % We now test to see if the quotient failure is spurious, e.g.,
       % as in int(-2x/(sqrt(2x^2+1)-2x^2+1),x); It's not clear this is
       % the right place to check though.  More information is
       % available from the earlier int-sqrt step.
       then begin scalar oldprop;
               oldprop := get('sqrt,'simpfn);
               put('sqrt,'simpfn,'simpsqrt);
               v := simp prepsq v;
               put('sqrt,'simpfn,oldprop);
               if denr v = 1 then w := numr v
            end;
     if null w then errach list("Division failure in mknewsqrt",u);
     return w
  end;

symbolic procedure actualsimpsqrt(sf);
if sf iequal -1
  then gaussiani
  else actualsqrtinner(sf,listofnewsqrts);


symbolic procedure actualsqrtinner(sf,l);
   if sf =1 then 1
    else if null l
       or domainp sf or ldeg sf=1
   % Patch by A.C. Norman to prevent recursion errors.
     then actualsimpsqrt2 sf
    else begin scalar z;
       if numberp sf and (z := list('sqrt,sf)) member l
         then return !*kk2f z;
       z := argof car l;
       if z member l then z := !*kk2f car l else z := !*q2f simp z;
       if z = -1 then return actualsqrtinner(sf,cdr l);
       z:=quotf(sf,z);
       if null z then return actualsqrtinner(sf,cdr l)
        else return !*multf(!*kk2f car l,actualsimpsqrt z)
     end;


symbolic procedure actualsimpsqrt2(sf);
 if atom sf
   then if null sf
     then nil
     else if numberp sf
       then if sf < 0
         then multf(gaussiani,actualsimpsqrt2(- sf))
           %Above 2 lines inserted JHD 4 Sept 80;
           % test case: SQRT(B*X**2-C)/SQRT(X);
         else begin
           scalar n;
           n:=int!-sqrt sf;
           % Changed for conformity with DEC20 LISP JHD July 1982;
           if not fixp n
             then return mknewsqrt sf
             else return n
           end
     else mknewsqrt(sf)
   else begin
     scalar form;
     form:=comfac sf;
     if car form
       then return multf((if null cdr sf and (car sf = form)
                            then formsqrt(form .+ nil)
                            else simpsqrt2(form .+ nil)),
                            %The above 2 lines changed by JHD;
                            %(following suggestions of Morrison);
                            %to conform to Standard LISP 4 Sept 80;
                         simpsqrt2 quotf(sf,form .+ nil));
     % we have killed common powers.
     form:=cdr form;
     if form neq 1
       then return multf(simpsqrt2 form,
                          simpsqrt2 quotf(sf,form));
     % remove a common factor from the sf.
     return formsqrt sf
     end;


symbolic procedure int!-sqrt n;
   % Return sqrt of n if same is exact, or something non-numeric
   % otherwise.
    if not numberp n then 'nonnumeric
    else if n<0 then 'negative
    else if floatp n then sqrt n
    else if n<2 then n
    else int!-nr(n,(n+1)/2);


symbolic procedure int!-nr(n,root);
% root is an overestimate here. nr moves downwards to root;
 begin
    scalar w;
    w:=root*root;
    if n=w then return root;
    w:=(root+n/root)/2;
    if w>=root then return !*q2f simpsqrt list n;
    return int!-nr(n,w)
 end;


 symbolic procedure formsqrt(sf);
 if (null red sf)
   then if (lc sf iequal 1) and (ldeg sf iequal 1)
     then mknewsqrt mvar sf
     else multf(if evenp ldeg sf
                  then !*p2f mksp(mvar sf,ldeg sf / 2)
                  else exptf(mknewsqrt mvar sf,ldeg sf),simpsqrt2 lc sf)
   else begin
     scalar varlist,zlist,sqrtlist,sqrtflag;
     scalar v,l,n,w;
     % This returns a list, the i-th member of which is
%      a list of the factors of multiplicity i (as s.f's);
     v:=jsqfree(sf,if intvar and involvesf(sf,intvar) then intvar
                     else findatom mvar sf);
                     % intvar is the best thing to do square-free
                     % decompositions with respect to, but anything
                     % else will do if intvar is not set.
     if null cdr v and null cdar v then return mknewsqrt prepf sf;
       % The JSQFREE did nothing.
     l:=nil;
     n:=0;
     while v do <<
       n:=n+1;
       w:=car v;
       while w do <<
         l:=list('expt,mk!*sq !*f2q car w,n) . l;
         w:=cdr w >>;
       v:=cdr v >>;
     if null cdr l
       then l:=car l
       else l:='times.l;
       % makes L into a valid prefix form;
     return !*q2f simpsqrti l
     end;


symbolic procedure findatom pf;
if atom pf
  then pf
  else findatom argof pf;


endmodule;

end;
