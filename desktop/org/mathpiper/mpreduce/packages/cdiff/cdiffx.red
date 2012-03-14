module 'cdiffx;

% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions
% are met:
%
%    * Redistributions of source code must retain the relevant
%      copyright notice, this list of conditions and the following
%      disclaimer.
%    * Redistributions in binary form must reproduce the above
%      copyright notice, this list of conditions and the following
%      disclaimer in the documentation and/or other materials provided
%      with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
% "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
% LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
% A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
% OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
% SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
% LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
% DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
% THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
% (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
% OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
%
% % *****************************************************************
%
% Authors: P. Gragert, P.H.M. Kersten, G.H.M. Roelofs, G.F. Post
% University of Twente (Enschede, The Netherlands)
%
% Version and Date:  Version 1.0, 1992.
%
% Maintainer: Raffaele Vitolo
% Dipartimento di Matematica, Universita' del Salento (Lecce, Italy)
% email: raffaele.vitolo@unisalento.it
% web: http://poincare.unisalento.it/vitolo
% ===============================================================

% The material in her ewas originally in four separate files - these
% have been consolidated.

% akf
define sj=solve_jacobi_identities_of,
   god=generators_of_degree,
   cod=commutators_of_degree,
   uso=unsolved_identities_of,
   ng=new_generators,
   ls=linear_solve_and_assign,
   oc=operator_coeff,
   ra=relation_analysis;

define es=integrate_equation,
   seq=integrate_equations,
   xes=integrate_exceptional_equation,
   pr=show_equation,
   preq=show_equations,
   te=equations_used(),
   pte=put_equations_used,
   fu=functions_used,
   pfu=put_functions_used;


% losop

lisp operator losop;
lisp procedure losop(m,n,koplist,coplist);
   begin
      koplist:=if atom koplist then list(koplist) else koplist;
      for i:=m:n do begin algebraic write i; elim(i,koplist,coplist) end;
      algebraic write "Totaal ",totaal(koplist),
    	 " coefficienten opgelost";
   end$

lisp operator totaal;
lisp procedure totaal oplist;
   for each el in oplist sum length get(el,'kvalue)$

lisp operator oplosmogelijkheid;
lisp procedure oplosmogelijkheid(m,n,l);
   'list . for i:=m:n conc
      if solvable_kernels(list('num,list('equ,i)),
	 reval 'allowed_opr,reval 'forbidden_opr) neq '(list)
      then if algebraic(length num equ i) <= l then
     	 list list('list,i,algebraic num length(equ i)) else nil$

algebraic procedure propl l;
   for each el in l do write first el,"  ",equ first el$

lisp operator elim;
lisp procedure elim(i,koplist,coplist);
   begin scalar lijst;
      lijst:=cdr solvable_kernels(list('equ,i),koplist,coplist);
      if lijst then
   	 %verandering paul
	 if (lijst:=ordlist lijst) then
   	    %einde verandering paul
      	    <<
               linear_solve_and_assign(list('equ,i),car lijst);
               setk(list('equ,i),0); lijst:=car lijst;
               terpri();varpri(list('list,i,lijst),nil,t);
      	    >>;
   end$

lisp procedure ordlist u;
   ordlist1(u,nil)$

lisp procedure ordlist1(u,v);
   if null u then v
   else ordlist1(cdr u,ordlist2(car u,v))$

lisp procedure ordlist2(x,v);
   if ordp(x,car v) then x . v
   else car v . ordlist2(x,cdr v)$

lisp operator schoonop;
lisp procedure schoonop i;
   begin scalar ol;
      ol:=cdr multi_coeff(list('equ,i),cdr reval 'vars);
      if length ol=1 then return i;
      if car ol neq 0 then equ(put_equations_used(equations_used()+1)):=car ol;
      for each el in cdr ol do equ(put_equations_used(equations_used()+1)):=caddr el;
      equ(i):=0;
      return equations_used();
   end$

procedure hl(m,n,l);
   for i:=m:n do if length num equ i<=l and equ i neq 0
   then elim(i,allowed_opr,forbidden_opr) else if remainder(i,10)=0 then write i$

procedure h i;elim(i,allowed_opr,forbidden_opr)$

lisp operator clean;
lisp procedure clean i;
   begin scalar ol;
      ol:=cdr operator_coeff(list('equ,i),'ext);
      if car ol neq 0 then equ(pte(te+1)):=car ol;
      for each el in cdr ol do
    	 equ(pte(te+1)):=caddr el;
      equ(i):=0;
      return te;
   end$

procedure prl(m,n,l);
   for i:=m:n do if length equ i<=l and equ i neq 0 then write i,"  ",equ i$


% heho

lisp procedure mkpartitions(m,q,min,max,partitie,partitielist);
   if q=1 then
      if m>=min then reverse(m . partitie) . partitielist
      else partitielist
   else
      if min>max then partitielist
      else mkpartitions(m-min,q-1,min,(m-min)/(q-1),min . partitie,
	 mkpartitions(m,q,min+1,max,partitie,partitielist))$

lisp procedure partities(m,q,min);
   mkpartitions(m,q,min,m/q,nil,nil)$

lisp procedure mkallpartitions m;
   for i:=m step -1 until 1 conc partities(m,i,1)$

lisp operator mkvarlist;
lisp procedure mkvarlist(m,q);
   'list . processpartitielist(partities(m,q,0),nil)$

lisp procedure processpartitielist(partitielist,varlist);
   if null partitielist then varlist
   else processpartitielist(cdr partitielist,
      processpartitie(car partitielist,0,nil,nil . nil,varlist))$

lisp procedure processpartitie(partitie,oldi,oldilist,var,varlist);
   if null partitie then if null car var then ('times . cdr var) . varlist else
      ('times . ('ext . reverse ordn car var) . cdr var) . varlist
   else if car partitie=0 then processpartitie(cdr partitie,oldi,oldilist,var,varlist)
   else if car partitie=oldi then
      processcarpartitie(oldi,oldilist,cdr partitie,var,varlist)
   else processcarpartitie(car partitie,
      cdr nth(cdadr get('graadlijst,'avalue),car partitie),
      cdr partitie,var,varlist)$

lisp procedure processcarpartitie(i,ilist,restpartitie,var,varlist);
   if null ilist then varlist
   else if evenp i then
      processcarpartitie(i,cdr ilist,restpartitie,var,
   	 processpartitie(restpartitie,i,ilist,car var . car ilist . cdr var,varlist))
   else
      processcarpartitie(i,cdr ilist,restpartitie,var,
   	 processpartitie(restpartitie,i,cdr ilist,(car ilist . car var) . cdr var,varlist))$


% polynom

lisp procedure mkpartitions1(m,q,min,max,partitie,partitielist);
   if q=1 then
      if m>=min then reverse(m . partitie) . partitielist
      else partitielist
   else
      if min>max then partitielist
      else mkpartitions1(m-min,q-1,min,(m-min)/(q-1),min . partitie,
	 mkpartitions1(m,q,min+1,max,partitie,partitielist))$

lisp procedure partities1(m,q,min);
   mkpartitions1(m,q,min,m/q,nil,nil)$

lisp procedure mkallpartitions1 m;
   for i:=m step -1 until 1 conc partities1(m,i,1)$

lisp operator mkvarlist1;
lisp procedure mkvarlist1(m,q);
   'list . processpartitie1list1(partities1(m,q,0),nil)$

lisp procedure processpartitie1list1(partitielist,varlist);
   if null partitielist then varlist
   else processpartitie1list1(cdr partitielist,
      processpartitie1(car partitielist,0,nil,nil . nil,varlist))$

lisp procedure processpartitie1(partitie,oldi,oldilist,var,varlist);
   if null partitie then if null car var then ('times . cdr var) . varlist else
      ('times . ('ext . reverse ordn car var) . cdr var) . varlist
   else if car partitie=0 then processpartitie1(cdr partitie,oldi,oldilist,var,varlist)
   else if car partitie=oldi then
      processcarpartitie1(oldi,oldilist,cdr partitie,var,varlist)
   else processcarpartitie1(car partitie,
      cdr nth(cdadr get('graadlijst,'avalue),car partitie),
      cdr partitie,var,varlist)$

lisp procedure processcarpartitie1(i,ilist,restpartitie,var,varlist);
   if null ilist then varlist
   else
      processcarpartitie1(i,cdr ilist,restpartitie,var,
   	 processpartitie1(restpartitie,i,ilist,car var . car ilist . cdr var,varlist))$

endmodule;

end;
