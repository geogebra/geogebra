% ----------------------------------------------------------------------
% $Id: clresolv.red 392 2009-07-28 07:05:52Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2006-2009 Andreas Dolzmann and Thomas Sturm
% ----------------------------------------------------------------------
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

lisp <<
   fluid '(cl_resolv_rcsid!* cl_resolv_copyright!*);
   cl_resolv_rcsid!* :=
      "$Id: clresolv.red 392 2009-07-28 07:05:52Z thomas-sturm $";
   cl_resolv_copyright!* := "(c) 2006-2009 A. Dolzmann and T. Sturm"
>>;

module clresolve;
% Resolve extended functions in Lisp prefix.

%DS
% <RRV> ::= (...,<RCASE>,...)
% <RCASE> ::= (<TERM>,<GUARD>,<QL>)

procedure rc_term(rc);
   car rc;

procedure rc_guard(rc);
   cadr rc;

procedure rc_ql(rc);
   caddr rc;

procedure rc_mk(term,guard,ql);
   {term,guard,ql};

procedure cl_resolve(f);
   cl_apply2ats(f,'cl_resolveat);

procedure cl_resolveat(atf);
   % Resolve atomic formula. [atf] is an atomic formula. Return a
   % formula. Result is equivalent to [atf] but does not contain any
   % atomic formulas for which the black box [rl_rxffn] yields a
   % function for resolving. Caveat: The OFSF implementation of
   % [rl_posresolve] relies on that [cl_simpl] is not called with any
   % non-resolved formula here.
   begin scalar w;
      w := rl_simp cl_resolve1 rl_prepfof atf;
      return if !*rlresi then cl_simpl(w,nil,-1) else w
   end;

procedure cl_resolve1(lpf);
   % lpf is an atomic formula in lisp prefix representation. returns a
   % lisp prefix formula without extended functions.
   begin scalar op,lhs,rrv;
      op := car lpf;
      lhs := cadr lpf;
      rrv := cl_resolve2 lhs;
      if cdr rrv then
      	 return 'or . for each rc in rrv collect
	    cl_transrc(op,rc);
      return cl_transrc(op,car rrv)
   end;

procedure cl_transrc(op,rc);
   begin scalar w;
      w := cl_transrc1(op,rc);
      for each p in reverse rc_ql rc do
	 w := {car p,cdr p,w};
      return w
   end;

procedure cl_transrc1(op,rc);
   if op eq 'equal or op eq 'neq then
      {'and,rc_guard rc,{op,rc_term rc,0}}
   else
      {'and,rc_guard rc,{op,rc_term rc,0}};

procedure cl_resolve2(lpf);
   % lpf is a lisp prefix form of a term including extended functions.
   % Returns a RRV.
   begin scalar op,fn,cprodl;
      if atom lpf then
	 return {rc_mk(lpf,'true,nil)};
      op := car lpf;
      cprodl := cl_cartprod for each arg in cdr lpf collect cl_resolve2 arg;
      fn := rl_rxffn op;
      if not fn then
	 return cl_resolve!-simple(op,cprodl);
      return cl_resolve!-xfn(op,fn,cprodl)
   end;

procedure cl_cartprod(rrvl);
   begin scalar w;
      if null cdr rrvl then
	 return for each rc in car rrvl collect
	    {{rc_term rc},{rc_guard rc},{rc_ql rc}};
      w := cl_cartprod cdr rrvl;
      return for each rc in car rrvl join
	 for each x in w collect
	    {rc_term rc . car x,rc_guard rc . cadr x,rc_ql rc . caddr x}
   end;

procedure cl_resolve!-simple(op,cprodl);
   for each x in cprodl collect
      rc_mk(
	 op . car x,
	 'and . cadr x,
	 lto_appendn(caddr x));
	 
procedure cl_resolve!-xfn(op,fn,cprodl);
   for each x in cprodl join
      apply(fn,op . x);
      
procedure cl_rxffn!-max(op,argl,condl,qll);
   begin scalar rel;
      rel := if op eq 'max then 'geq else 'leq;
      return for each x on argl collect
	 rc_mk(car x,cl_maxcond(rel,argl,x,condl),lto_appendn qll);
   end;

procedure cl_maxcond(rel,argl,restargl,condl);
   begin scalar w;
      w := for each y on argl join
	 if not(y eq restargl) then
	    {{rel,{'difference,car restargl,car y},0}};
      return 'and . nconc(w,condl)
   end;

procedure cl_rxffn!-abs(op,argl,condl,qll);
   if cdr argl then
      rederr {"cl_rxffn!-abs: length(argl)=",length argl}
   else
      {rc_mk(
	 car argl,
	 'and . {'geq,car argl,0} . condl,
	 lto_appendn qll),
       rc_mk(
	 {'minus,car argl},
	 'and . {'leq,car argl,0} . condl,
	 lto_appendn qll)};

procedure cl_rxffn!-sign(op,argl,condl,qll);
   if cdr argl then
      rederr {"cl_rxffn!-abs: length(argl)=",length argl}
   else
      {rc_mk(
	 1,
	 'and . {'greaterp,car argl,0} . condl,
	 lto_appendn qll),
       rc_mk(
	 -1,
	 'and . {'lessp,car argl,0} . condl,
	 lto_appendn qll),
       rc_mk(
	 0,
	 'and . {'equal,car argl,0} . condl,
	 lto_appendn qll)};

procedure cl_rxffn!-sqrt(op,argl,condl,qll);
   begin scalar w;
      w := intern gensym();
      return {rc_mk(
	 w,
	 'and . {'equal,{'difference,{'expt,w,2},car argl},0}
 	    . {'geq,w,0} . condl,
      	 ('ex . w) . lto_appendn qll)}
   end;

endmodule;  % clresolve

end;  % of file
