module specfn2;  % Part 2 of the Special functions package for REDUCE.
                 % The special Special functions.

% Author : Victor Adamchik, Byelorussian University Minsk, Byelorussia.

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


% Major modifications by: Winfried Neun, ZIB Berlin.


%  ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||  %
%                                                                                                                %
%     Please report bugs to Winfried Neun,                                         %
%                                            Konrad-Zuse-Zentrum                                   %
%                                               fuer Informationstechnik Berlin,   %
%                                            Heilbronner Str. 10                                     %
%                           10711 Berlin - Wilmersdorf                 %
%                                            Federal Republic of Germany                        %
%     or by email, neun@sc.ZIB-Berlin.de                                           %
%                                                                                                                %
%  ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||  %
%                                                                                                                %
%     This package provides algebraic                                    %
%     manipulations upon some special functions:                    %
%                                                                                                                %
%                   -- Generalized Hypergeometric Functions           %
%                   -- Meijer's G Function                                                  %
%              -- to be extended                                 %
%                                                                                                                %
%  ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||  %


create!-package ('(specfn2 ghyper meijerg),
                 '(contrib specfn));

load_package specfn;

%  Various help utilities and smacros for hypergeometric function
%  simplification.

symbolic smacro procedure diff1sq(u,v);
   addsq(u,negsq(v))$

symbolic smacro procedure mksqnew u;
  !*p2f(car fkern(u) .* 1) ./ 1;

symbolic smacro procedure gamsq(u);
 mksqnew('GAMMA . list(prepsq u));

symbolic smacro procedure multgamma u;
   %u -- list of SQ.
   <<for each pp in u do <<p := multsq(gamsq pp,p)>>; p>>
     where p = '(1 . 1);

symbolic smacro procedure besssq(v,u);
 mksqnew('BesselJ . list(prepsq v,prepsq u))$

symbolic smacro procedure bessmsq(v,u);
 mksqnew('BesselI . list(prepsq v,prepsq u))$

symbolic smacro procedure simppochh(v,u);
 mksqnew('Pochhammer . list(prepsq v,prepsq u))$

symbolic procedure multpochh(u,k);
 << for each pp in u do <<p := multsq (simppochh (pp,k),p)>>; p>>
                 where p = '(1 . 1);

symbolic smacro procedure psisq(v);
 mksqnew('psi . list(prepsq v))$

%symbolic smacro procedure dfpsisq(v,u);
% mksqnew('dfpsi . list(prepsq v,prepsq u))$

symbolic procedure simpfunc(u,v);
 % u -- name of the function, PF.
 % v -- argument, SQ.
 begin scalar l,v1!wq;
 v1!wq:=prepsq v; l:=('!Adamchik . v1!wq);
 u:=subf(car simp!* list(u,'!Adamchik),list l);
 return u $
end$

algebraic operator intsin,intcos,ints,intfs,intfc$

symbolic procedure subsqnew(u,v,z);
% u,v -- SQ.
% z -- PF  .
begin scalar a!1,lp,a;
  a!1:=prepsq v; lp:=((z . a!1));
  a:=quotsq(subf(car u,list lp),subf(cdr u,list lp));
  return a;
end$

symbolic procedure expdeg(x,y);
% x,y -- SQ.
% value is the x**y.
 if null car y then '(1 . 1) else
 if numberp(car y) and numberp(cdr y) then
             simpx1(prepsq x,car y,cdr y) else
 quotsq(expdeg1(car x ./ 1 ,y),expdeg1(cdr x ./ 1 ,y))$

symbolic procedure expdeg1(x,y);
% x,y -- SQ.
% value is the x**y.
simp!*(prepsq(subsqnew(subsqnew(simp!*
      '(expt a!g9 b!!g9),x,'a!g9),y,'b!!g9)))$

symbolic smacro procedure difflist(u,v);
 % u -- list of SQ.
 % v -- SQ.
 % value is (u) - v.
 for each uu in u collect addsq(uu,negsq v);

symbolic procedure listplus(u,v);
% value is (u) + v.
 difflist(u,negsq v)$

 symbolic smacro procedure addlist u;
  % u -- list of PF.
  <<for each pp in u do <<p := addsq(simp!* pp,p)>>; p>>
    where p = '(nil . 1);

 symbolic smacro procedure listsq(u);
 % u - list of PF.
 % value is list of SQ.
 for each uu in u collect simp!* uu;

symbolic smacro procedure listmin(u);
 % u - list.
 % value is (-u).
 for each uu in u collect negsq uu;

symbolic smacro procedure multlist(u);
<< for each pp in u do <<p := multsq(pp,p)>>; p>> where p = '(1 . 1);

symbolic procedure parfool u;
  % u -- SQ.
  % value is T if u = 0,-1,-2,...
  if null car u then t
    else
  if and(numberp car u,eqn(cdr u,1),lessp(car u,0)) then t
    else nil$

 symbolic procedure znak u;
 % u -- SQ.
  if numberp u then
     if u > 0 then T else NIL
               else
  if numberp car u then
     if car u > 0 then T else NIL
                   else
  if not null cdar u then T
                     else
  if numberp cdaar u then
     if cdaar u > 0 then T else NIL
                     else
     znak(cdaar u ./ 1)$

 symbolic procedure sdiff(a,b,n);
  % value is (1/b*d/db)**n(a)   .
  % a,n--SQ  b--PF              .
  if null car n then a else
  if and(numberp(car n),numberp(cdr n),eqn(cdr n,1),not lessp(car n,0))
                                        then
     multsq(invsq(simp!* b), diffsq(sdiff(a,b,diff1sq(n, '(1 . 1))),b))
     else
  rerror('specialf,130,"***** error parameter in sdiff")$

 symbolic procedure derivativesq(a,b,n);
  % a -- SQ.
  % b -- ATOM.
  % n -- order, SQ.
 if null n or null car n then a
 else derivativesq(diffsq(a,b),b,diff1sq(n,'(1 . 1)))$

 symbolic procedure addend( u,v,x);
  % u,v -- lists of SQ.
  % x -- SQ.
  cons(diff1sq(car u,x),difflist(v,diff1sq(car u,x)))$

 symbolic procedure parlistfool(u,v);
  %v -- list.
  %value is the T if u-(v)=0,-1,-2,...
  if null v then nil else
  if parfool(diff1sq(u,car v)) then t else
     parlistfool(u,cdr v)$

 symbolic procedure listparfool(u,v);
  %u -- list.
  %value is the T if (u)-v=0,-1,-2,...
  if null u then nil else
  if parfool(diff1sq(car u,v)) then t else
     listparfool(cdr u,v)$

 symbolic procedure listfool u;
  %u -- list.
  %value is the T if any two of the terms (u)
  %differ by an integer or zero.
  if null cdr u then nil else
  if parlistfool(car u,cdr u) or listparfool(cdr u,car u)
     then t else listfool(cdr u)$

 symbolic procedure listfooltwo(u,v);
  %u,v -- lists.
  %value is the T if (u)-(v)=0,-1,-2,...
  if null u then nil else
  if parlistfool(car u,v) then t else
     listfooltwo(cdr u,v)$

 symbolic smacro procedure pdifflist(u,v);
  % u -- SQ.
  % v -- list of SQ.
  %value is a list: u-(v).
 for each vv in v collect diff1sq(u,vv);

symbolic procedure redpar1(u,n);
 % value is a paire, car-part -- sublist of the length n
 %                   cdr-part --  .
 begin scalar bm;
  while u and not(n=0) do begin
    bm:=cons (car u,bm);
    u:=cdr u;
    n:=n-1;
  end;
  return cons(reverse bm,u);
end$

symbolic procedure redpar (l1,l2);
   begin scalar l3;
   while l2 do <<
         if member(car l2 , l1) then l1 := delete(car l2,l1)
                 else l3 := (car l2) . l3 ;
         l2 := cdr l2 >>;
   return list (l1,reverse l3);
   end;

algebraic operator Lommel,Heaviside;

symbolic smacro procedure heavisidesq(u);
 mksqnew('Heaviside . list(prepsq u));

symbolic smacro procedure StruveLsq(v,u);
 mksqnew('StruveL . list(prepsq v,prepsq u));

symbolic smacro procedure StruveHsq(v,u);
 mksqnew('StruveH . list(prepsq v,prepsq u));

symbolic smacro procedure neumsq(v,u);
 mksqnew('BesselY . list(prepsq v,prepsq u));

symbolic smacro procedure simppochh(v,u);
 mksqnew('Pochhammer . list(prepsq v,prepsq u));

symbolic smacro procedure psisq(v);
 mksqnew('psi . list(prepsq v));

symbolic smacro procedure dfpsisq(v,u);
 mksqnew('Polygamma . list(prepsq u,prepsq v));

symbolic smacro procedure Lommel2sq (u,v,w);
 mksqnew('Lommel2  . list(prepsq u,prepsq v,prepsq w));

symbolic smacro procedure tricomisq (u,v,w);
 mksqnew('KummerU . list(prepsq u,prepsq v,prepsq w));

symbolic smacro procedure macdsq (v,u);
 mksqnew('BesselK . list(prepsq v,prepsq u));

fluid '(v1!wq,a!g9,b!!g9);

symbolic smacro procedure sumlist u;
   % u -- list of the PF
   <<for each pp in u do <<p := addsq(simp pp,p)>>; p>>
    where p = '(nil . 1);

symbolic smacro procedure difflist(u,v);
 % u -- list of SQ.
 % v -- SQ.
 % value is (u) - v.
 for each uu in u collect addsq(uu,negsq v);

symbolic smacro procedure addlist u;
   % u -- list of PF.
   <<for each pp in u do <<p := addsq(simp!* pp,p)>>; p>>
    where p = '(nil . 1);

symbolic smacro procedure diff1sq(u,v);
  addsq(u,negsq(v));

symbolic smacro procedure listsq(u);
 % u - list of PF.
 % value is list of SQ.
 for each uu in u collect simp!* uu;

symbolic smacro procedure listmin(u);
 % u - list.
 % value is (-u).
 for each uu in u collect negsq uu;

symbolic smacro procedure multlist(u);
<< for each pp in u do <<p := multsq(pp,p)>>; p>> where p = '(1 . 1);

symbolic smacro procedure pdifflist(u,v);
  % u -- SQ.
  % v -- list of SQ.
  %value is a list: u-(v).
 for each vv in v collect diff1sq(u,vv);

symbolic smacro procedure listprepsq(u);
 for each uu in u collect prepsq uu;

endmodule;

end;


