module findres;

% Author: James H. Davenport.

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


fluid '(!*gcd
        !*tra
        !*trmin
        basic!-listofallsqrts
        basic!-listofnewsqrts
        intvar
        listofallsqrts
        listofnewsqrts
        nestedsqrts
        sqrt!-intvar
        taylorvariable);


exports find!-residue,findpoles;
imports sqrt2top,jfactor,prepsq,printplace,simpdf,involvesf,simp;
imports stt,interr,mksp,negf,multf,addf,let2,substitutesq,subs2q,quotf;
imports printsq,clear,taylorform,taylorevaluate,involvesf,!*multsq;
imports sqrtsave,sqrtsinsq,sqrtsign;

symbolic procedure find!-residue(simpdl,x,place);
  % evaluates residue of simpdl*dx at place given by x=place(y).
begin
  scalar deriv,nsd,poss,slist;
  listofallsqrts:=basic!-listofallsqrts;
  listofnewsqrts:=basic!-listofnewsqrts;
  deriv:=simpdf(list(place,x));
  if involvesf(numr deriv,intvar)
    then return residues!-at!-new!-point(simpdl,x,place);
  if eqcar(place,'quotient) and (cadr place iequal 1)
    then goto place!-correct;
  place:=simp list('difference,intvar,place);
  if involvesq(place,intvar)
    then interr "Place wrongly formatted";
  place:=list('plus,intvar,prepsq place);
place!-correct:
  if car place eq 'plus and caddr place = 0
    then place:=list(x.x)
    else place:=list(x.place);
  % the substitution required.
  nsd:=substitutesq(simpdl,place);
  deriv:=!*multsq(nsd,deriv);
  % differential is deriv * dy, where x=place(y).
  if !*tra then <<
    printc "Differential after first substitution is ";
    printsq deriv >>;
  while involvesq(deriv,sqrt!-intvar)
    do <<
      sqrtsave(basic!-listofallsqrts,basic!-listofnewsqrts,place);
      nsd:=list(list(x,'expt,x,2));
      deriv:=!*multsq(substitutesq(deriv,nsd),!*kk2q x);
      % derivative of x**2 is 2x, but there's a jacobian of 2 to
      % consider.
      place:=nconc(place,nsd) >>;
  % require coeff x**-1 in deriv.
  nestedsqrts:=nil;
  slist:=sqrtsinsq(deriv,x);
  if !*tra and nestedsqrts
    then printc "We have nested square roots";
  slist:=sqrtsign(slist,intvar);
  % The reversip is to ensure that the simpler sqrts are at
  % the front of the list.
  % Slist is a list of all combinations of signs of sqrts.
  taylorvariable:=x;
  for each branch in slist do <<
    nsd:=taylorevaluate(taylorform substitutesq(deriv,branch),-1);
    if numr nsd
      then poss:=(append(place,branch).sqrt2top nsd).poss >>;
  poss:=reversip poss;
  if null poss
    then go to finished;
  % poss is a list of all possible residues at this place.
  if !*tra
    then <<
      princ "Residues at ";
      printplace place;
      printc " are ";
      mapc(poss, function (lambda u; <<
                       printplace car u;
                       printsq cdr u >>)) >>;
finished:
  sqrtsave(basic!-listofallsqrts,basic!-listofnewsqrts,place);
  return poss
  end;


symbolic procedure residues!-at!-new!-point(func,x,place);
begin
  scalar place2,tempvar,topterm,a,b,xx;
  if !*tra then <<
    printc "Find residues at all roots of";
    superprint place >>;
  place2:=numr simp place;
  topterm:=stt(place2,x);
  if car topterm = 0
    then interr "Why are we here?";
  tempvar:=gensym();
  place2:=addf(place2,
               multf(!*p2f mksp(x,car topterm),negf cdr topterm));
  % The remainder of PLACE2.
  let2(list('expt,tempvar,car topterm),
       subst(tempvar,x,prepsq(place2 ./ cdr topterm)),
       nil,t);
  place2:=list list(x,'plus,x,tempvar);
  !*gcd:=nil;
    % No unnecessary work: only factors of X worry us.
  func:=subs2q substitutesq(func,place2);
  !*gcd:=t;
  xx:=!*k2f x;
  while (a:=quotf(numr func,xx)) and (b:=quotf(denr func,xx))
    do func:=a ./ b;
  if !*tra then <<
    printc "which gives rise to ";
    printsq func >>;
  if null a
    then b:=quotf(denr func,xx);
    % because B goes back to the last time round that WHILE loop.
  if b then go to hard;
  if !*tra then printc "There were no residues";
  clear tempvar;
  return nil;
  % *** thesis remark ***
%   This test for having an X in the denominator only works
%   because we are at a new place, and hence (remark of Trager)
%   if we have a residue at one place over this point, we must have one
%   at them all, since the places are indistinguishable;
hard:
  taylorvariable:=x;
  func:=taylorevaluate(taylorform func,-1);
  printsq func;
  interr "so far"
  end;


symbolic procedure findpoles(simpdl,x);
begin
  scalar simpdl2,poles;
  % finds possible poles of simpdl * dx.
  simpdl2:=sqrt2top simpdl;
  poles:=jfactor(denr simpdl2,x);
  poles := for each j in poles collect prepsq j;
  % what about the place at infinity.
  poles:=list('quotient,1,x).poles;
  if !*tra or !*trmin
    then <<
      printc "Places at which poles could occur ";
      for each u in poles do
        printplace list(intvar.u) >>;
  return poles
  end;

endmodule;

end;
