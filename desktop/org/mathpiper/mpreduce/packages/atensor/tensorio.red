%======================================================
%       Name:           tio.red - tensor user interface
%       Author:         A.Kryukov (kryukov@npi.msu.su)
%       Copyright:      (C), 1993i-1995, A.Kryukov
%       Version:        1.35
%       Release:        Apr., 17, 1995
%------------------------------------------------------
%       Modified:       Apr., 17, 1995   tsym2
%                       Apr., 24, 1996   tclear0
%======================================================

module tensorio$

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


%=====================================================
%       blist::=((th . pv_list) ...)
%       pv_list::= (pv1 pv2 ...)
%=====================================================

smacro procedure tname th$ car th$
smacro procedure ilist th$ cadr th$
smacro procedure dlist th$ cddr th$
smacro procedure mkth(tn,il,dl)$ list tn . il . id$
smacro procedure mkth0(tn,il,dl)$  tn . il . dl$

smacro procedure thead ten$ car ten$
smacro procedure pvect ten$ cdr ten$
smacro procedure mkten0(th,pv)$ th . pv$
smacro procedure mkten(th,pv)$ '!:tensor . list(th . pv)$

symbolic procedure bassoc(th,bl)$
  if null bl then nil
  else if th_match(th,caar bl) then bl
  else bassoc(th,cdr bl)$

global '(!*basis,tensors!*)$

remprop('tensor,'stat)$
remprop('tsym,'stat)$
remprop('tclear,'stat)$

symbolic procedure tensor u$
    for each x in u do
      if null(x memq tensors!*) then <<
        put(x,'!:tensor,99)$            % undefine rank
        put(x,'simpfn,'t_simp)$
        flag(list x,'full)$
        tensors!* := x . tensors!*$
      >>
      else write "+++ ",x," is already declared as tensor."$

symbolic procedure tclear u$
  tclear0(if car u eq 'all then tensors!* else u)$

symbolic procedure tclear0 u$
  for each x in u do
    if x memq tensors!* then
      begin scalar bs,bs1$
        tensors!* := delete(x,tensors!*)$
        remprop(x,'!:tensor)$
        remflag(x,'full)$
        bs:=!*basis$
        while bs do <<
          if null(x memq caaar bs) then bs1:=car bs . bs1$
          bs:=cdr bs$
        >>$
        !*basis:=reversip bs1$
      end
    else << write "+++ ",x," is not a tensor."$ terpri() >>$

symbolic procedure tsym u$
  % u is a list of symmetry identities.
  % return nil.
  % Out side eff.: add identities to basis list in !*basis.
  begin scalar b$
    b:=!*basis$
    !*basis:=nil$
    !*basis:=tsym1(u,b)$
  end$

symbolic procedure tsym1(u,b)$
  % u is a list of symmetry identities.
  % b is a basis list (returned value).
  % return new basis list.
  if null u then b
  else tsym1(cdr u,tsym2(cdr numr simp!* car u,b,nil))$

symbolic procedure tsym2(tt,b,b1)$
  % tt is a tensor identity
  % b is old basis
  % b1 is new basis (returned value)
  if cdr tt then rederr list('tsym2,"*** Invalid identity:",tt)
  else if null b
    then (caar tt . tsym4(gperm length cadaar tt,car tt,nil))
         . reversip b1
  else if th_match0(caar tt,caar b)
         then (caar b . tsym4(gperm length cadaar tt,car tt,cdar b))
              . append(cdr b,b1)
  else tsym2(tt,cdr b,car b . b1)$

symbolic procedure tsym4(ps,x,b0)$
 if null ps then b0
 else tsym4(cdr ps,x
           ,insert_pv(pv_renorm sieve_pv(pv_applp(cdr x,car ps),b0),b0)
           )$

put('tensor,'stat,'rlis)$
put('tsym,'stat,'rlis)$
put('tclear,'stat,'rlis)$

symbolic procedure kbasis x$
  for each z in x do basis1 z$

global '(!*dummypri)$
switch dummypri$

symbolic procedure basis1 x$
  begin scalar b$
    if idp x then x:=list x;
    if atom x or null get(car x,'!:tensor)
      then rederr list('basis1,"*** Invalid as tensor:",x);
    b:=!*basis$
    while b do <<
      if tnequal(x,caaar b)
        then << for each z in cdar b do
                  t_pri1('!:tensor . list(caar b . z),t)$
                write length cdar b$ terpri()$
             >>$
      b:=cdr b$
    >>$
  end$

symbolic procedure tnequal(tn1,tn2)$
  if atom tn1 then tn1 eq tn2
  else (lambda x$ if x neq tn2 then tnequal(cdr tn1,x)
                  else nil) delete(car tn1,tn2)$

put('kbasis,'stat,'rlis)$

endmodule;

end;
