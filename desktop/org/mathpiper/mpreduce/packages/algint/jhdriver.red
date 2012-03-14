module jhdriver;

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


fluid '(!*algint
    !*backtrace
    !*coates
    !*noacn
    !*tra
    !*trmin
    !*structure
        basic!-listofallsqrts
        basic!-listofnewsqrts
        gaussiani
        intvar
        listofallsqrts
        listofnewsqrts
        previousbasis
        sqrt!-intvar
        sqrtflag
        sqrts!-in!-integrand
        sqrts!-mod!-prime
        taylorasslist
        varlist
        zlist);

global '(tryharder);

switch algint,coates,noacn,tra,trmin;

exports algebraiccase,doalggeom,coates!-multiple;

!*algint := t;   % Assume algebraic integration wanted if this module
                 % is loaded.

symbolic procedure operateon(reslist,x);
begin
  scalar u,v,answer,save;
  scalar sqrts!-mod!-prime;
  u:=zmodule(reslist);
  v:=answer:=nil ./ 1;
  while u and not atom v do <<
    v:=findfunction cdar u;
    if not atom v then <<
      if !*tra or !*trmin then <<
    printc "Extension logarithm is ";
        printsq v >>;
      save:=tryharder;
      tryharder:=x;
      v:= combine!-logs(caar u, simplogsq v);
      tryharder:=save;
      answer:=!*addsq(answer,v);
      u:=cdr u >> >>;
  if atom v
    then return v
    else return answer
  end;


symbolic procedure findfunction divisor;
begin
  scalar v,places,mults,ans,dof1k;
  scalar previousbasis;
  % ***time-hack-2 :::
    % A hack for decreasing the amount of work done in COATES.
  divisor:=for each u in divisor collect
         correct!-mults u;
  if !*coates
    then go to nohack;
  v:=precoates(divisor,intvar,nil);
  if not atom v
    then return v;
nohack:
  for each u in divisor do <<
    places:=(car u).places;
    mults :=(cdr u).mults >>;
  v:=coates(places,mults,intvar);
  if not atom v
    then return v;
  dof1k:=differentials!-1 getsqrtsfromplaces places;
  if null dof1k
    then interr "Must be able to integrate over curves of genus 0";
  if not mazurp(places,dof1k)
    then go to general;
  ans:='provably!-impossible;
  for i:=2:12 do
    if (i neq 11) and
       not atom (ans:=coates!-multiple(places,mults,i))
    then i:=12;   % leave the loop - we have an answer.
  return ans;
general:
  v:=findmaninparm places;
  if null v
     then return algebraic!-divisor(divisor,dof1k);
  if not maninp(divisor,v,dof1k)
    then return 'provably!-impossible;
  v:=1;
loop:
  v:=iadd1 v;
  if not atom (ans:=coates!-multiple(places,mults,v))
    then return ans;
  go to loop
  end;


symbolic procedure correct!-mults u;
begin
  scalar multip;
  multip:=cdr u;
  for each v in car u do
    if (lsubs v eq intvar) and
        eqcar(rsubs v,'expt)
      then multip:=multip * (caddr rsubs v);
    return (car u).multip
  end;

symbolic procedure algebraiccase(expression,zlist,varlist);
begin
  scalar rischpart,deriv,w,firstterm;
  scalar sqrtflag,!*structure;  % set !*structure to NIL, else
                                % sqrt(z)^2 isn't simplified
  sqrtflag:=t;
  sqrtsave(listofallsqrts,listofnewsqrts,list(intvar . intvar));
  rischpart:= errorset!*(list('doalggeom,mkquote expression),
                         !*backtrace);
  newplace list (intvar.intvar);
  if atom rischpart
    then <<
      if !*tra then printc "Inner integration failed";
      deriv:=nil ./ 1;
      % assume no answer.
      rischpart:=deriv >>
    else
      if atom car rischpart
        then <<
      if !*tra or !*trmin then
        printc "The 'logarithmic part' is not elementary";
          return (nil ./ 1) . expression >>
      else <<
        rischpart:=car rischpart;
    deriv:=!*diffsq(rischpart,intvar) where sqrtflag=nil;
    % deriv := squashsqrt deriv;
    % Should no longer be necessary.
    if !*tra or !*trmin then <<
      printc "Inner working yields";
          printsq rischpart;
      printc "with derivative";
          printsq deriv >> >>;
  deriv:=!*addsq(expression,negsq deriv);
  if null numr deriv
    then return rischpart . (nil ./ 1); % no algebraic part.
  if null involvesq(deriv,intvar)
    then return !*addsq(rischpart,
                !*multsq(deriv,((mksp(intvar,1) .* 1) .+ nil) ./ 1))
          . (nil ./ 1);
        % if the difference is merely a constant.
  varlist:=getvariables deriv;
  zlist:=findzvars(varlist,list intvar,intvar,nil);
  varlist:=setdiff(varlist,zlist);
  firstterm:=simp!* car zlist; % this may crop up.
  w:=sqrt2top !*multsq(deriv,invsq !*diffsq(firstterm,intvar));
  if null involvesq(w,intvar)
    then return !*addsq(rischpart,!*multsq(w,firstterm)) . (nil ./ 1);
  if !*noacn then interr "Testing only logarithmic code";
  deriv:=transcendentalcase(deriv,intvar,nil,zlist,varlist);
  return !*addsq(car deriv, rischpart) . cdr deriv
  end;

symbolic procedure doalggeom(differential);
begin
  scalar reslist,place,placelist,
     savetaylorasslist,sqrts!-in!-integrand,
     taylorasslist;
  placelist:=findpoles(differential,intvar);
  reslist:=nil;
  sqrts!-in!-integrand:=sqrtsinsq (differential,intvar);
  while placelist do <<
    place:=car placelist;
    placelist:=cdr placelist;
    savetaylorasslist:=taylorasslist;
    place:=find!-residue(differential,intvar,place);
    if place
      then reslist:=append(place,reslist)
      else taylorasslist:=savetaylorasslist >>;
  if reslist
    then go to serious;
  if !*tra or !*trmin
    then printc "No residues => no logs";
  return nil ./ 1;
serious:
  placelist:=operateon(reslist,intvar);
  if placelist eq 'failed
    then interr "Divisor operations failed";
  return placelist
  end;


symbolic procedure algebraic!-divisor(divisor,dof1k);
if length dof1k = 1
  then lutz!-nagell(divisor)
  else bound!-torsion(divisor,dof1k);


symbolic procedure coates!-multiple(places,mults,v);
begin
  scalar ans;
  if not atom (ans:=coates(places,
                           for each u in mults collect v*u,
                           intvar))
    then <<
      if !*tra or !*trmin then <<
    princ "Divisor has order ";
        printc v >>;
      return !*kk2q list('nthroot,mk!*sq ans,v) >>
    else return ans
  end;


symbolic procedure mazurp(places,dof1k);
   % Checks to ensure we have an elliptic curve over the rationals.
begin
%  scalar sqrt2,sqrt4,v;
%  sqrt2:=0;
%    % Number of SQRTs of things of degree 1 or 2;
%  sqrt4:=0;
%    % " " " 3 or 4;
%  for each u in getsqrtsfromplaces places do <<
%    v:=!*q2f simp u;
%    if sqrtsinsq(v,intvar)
%      then return nil;
%      % Cannot use nested SQRTs;
%    v:=car stt(v,intvar);
%    if v < 3
%      then if sqrt4>0
%        then return nil
%        else if sqrt2>1
%          then return nil
%          else sqrt2:=iadd1 sqrt2
%      else if v < 5
%        then if sqrt2>0 or sqrt4>0
%          then return nil
%          else sqrt4:=1
%        else return nil >>;
  scalar answer;
  if length dof1k neq 1
    then return nil;
    % Genus = # linearly independent differentials of 1st kind;
    % We know know that it is of genus = 1.
  answer:=t;
  while answer and places do
    if sqrtsintree(basicplace car places,nil,nil)
      then answer:= nil
      else places:=cdr places;
  if null answer then return nil;
  if !*tra then
    <<prin2 "*** We can apply Mazur's bound on the torsion of";
      prin2t "elliptic curves over the rationals">>;
  return t
  end;

endmodule;

end;
