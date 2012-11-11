% ----------------------------------------------------------------------
% $Id: mri.red 1797 2012-10-30 07:10:58Z thomas-sturm $
% ----------------------------------------------------------------------
% (c) 2008-2011 Thomas Sturm
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
   fluid '(mri_rcsid!* mri_copyright!*);
   mri_rcsid!* := "$Id: mri.red 1797 2012-10-30 07:10:58Z thomas-sturm $";
   mri_copyright!* := "(c) 2008-2011 T. Sturm"
>>;

module mri;
% Mixed real-integer quantifier elimination.

create!-package('(mri mriqe),nil);

loadtime load!-package 'rltools;
loadtime load!-package 'cl;
load!-package 'pasf;
load!-package 'ofsf;

fluid '(!*msg !*rlverbose);

switch rlmrivb,rlmrivbio,rlsimplfloor,rlmrivb2;;

on1 'rlmrivb;
off1 'rlmrivb2;
on1 'rlsimplfloor;

flag('(mri),'rl_package);
flag('(mri_chsimpat),'full);
flag('(mri_simpat),'full);
flag('(equal neq leq geq lessp greaterp),'spaced);

put('mri,'rl_cswitches,'(
   (rlsism . nil)));

% Parameters
put('mri,'rl_params,'(
   (rl_simplat1!* . mri_simplat1)
   (rl_op!* . mri_op)
   (rl_negateat!* . mri_negateat)
   (rl_tordp!* . ordp)
   (rl_simplb!* . mri_simplb)
   (rl_bsatp!* . mri_bsatp)
   (rl_ordatp!* . mri_ordatp)
   (rl_subat!* . mri_subat)
   (rl_subalchk!* . null)
   (rl_eqnrhskernels!* . mri_eqnrhskernels)
   (rl_bnfsimpl!* . cl_bnfsimpl)
   (rl_smsimpl!-impl!* . cl_smsimpl!-impl)
   (rl_smsimpl!-equiv1!* . cl_smsimpl!-equiv1)
   (rl_susibin!* . mri_susibin)
   (rl_susipost!* . mri_susipost)
   (rl_susitf!* . mri_susitf)));

% Services
put('mri,'rl_services,'(
   (rl_simpl!* . cl_simpl)
   (rl_nnf!* . cl_nnf)
   (rl_atnum!* . cl_atnum)
   (rl_varlat!* . mri_varlat)
   (rl_atl!* . cl_atl)
   (rl_subfof!* . cl_subfof)
   (rl_expand!* . mri_expand)
   (rl_qe!* . mri_qe)));

put('mri,'simpfnname,'mri_simpfn);
put('mri,'rl_prepat,'mri_prepat);
put('mri,'rl_resimpat,'mri_resimpat);
put('mri,'rl_lengthat,'mri_lengthat);
put('mri,'rl_prepterm,'prepf);
put('mri,'rl_simpterm,'mri_simpterm);

put('equal,'mri_simpfn,'mri_chsimpat);

put('neq,'mri_simpfn,'mri_chsimpat);

put('leq,'mri_simpfn,'mri_chsimpat);

put('geq,'mri_simpfn,'mri_chsimpat);

put('lessp,'mri_simpfn,'mri_chsimpat);

put('greaterp,'mri_simpfn,'mri_chsimpat);

put('cong,'mri_simpfn,'mri_simpat);

put('ncong,'mri_simpfn,'mri_simpat);

put('floor,'prifn,'mri_prifloor);

put('mrireal,'stat,'rlis);

put('mriclear,'stat,'rlis);

procedure mri_prifloor(u);
   if null !*nat then
      'failed
   else <<
      prin2!* "[";
      maprin cadr u;
      prin2!* "]"
   >>;

procedure mrireal(varl);
   <<
      for each v in varl do put(v,'mri_type,'real);
      rmsubs()
   >>;

procedure mri_putreal(v);
   <<
      put(v,'mri_type,'real);
      rmsubs()
   >>;

procedure mri_realvarp(id);
   idp id and get(id,'mri_type) eq 'real;

procedure mriclear(varl);
   <<
      for each v in varl do remprop(v,'mri_type);
      rmsubs()
   >>;

procedure mri_simpat(u);
   begin scalar w;
      w := pasf_simpat u;
      return mri_pasf2mriat(w,nil)
   end;

procedure mri_chsimpat(u);
   begin scalar w;
      w := pasf_chsimpat u;
      return mri_pasf2mri(w,nil)
   end;

procedure mri_resimpat(u);
   mri_pasf2mriat(pasf_resimpat mri_2pasfat u,nil);

procedure mri_pasf2mri(f,type);
   cl_apply2ats1(f,function mri_pasf2mriat,{type});

procedure mri_2pasf(f);
   cl_apply2ats(f,function mri_2pasfat);

procedure mri_pasf2mriat(at,type);
   mri_0mk2(pasf_op at,pasf_arg2l at,type);

procedure mri_ofsf2mri(f,type);
   cl_apply2ats1(f,function mri_ofsf2mriat,{type});

procedure mri_ofsf2mriat(at,type);
   mri_0mk2(ofsf_op at,ofsf_arg2l at,type);

procedure mri_prepat(u);
   pasf_prepat mri_2pasfat u;

procedure mri_2pasfat(at);
   pasf_0mk2(mri_op at,mri_arg2l at);

procedure mri_2ofsfat(at);
   ofsf_0mk2(mri_op at,mri_arg2l at);

procedure mri_op(atf);
   % Mixed-real-integer operator. [atf] is an atomic formula
   % $r(t_1,t_2)$ or $r(t_1,t_2,m)$. Returns $r$ or in case of a
   % congruence the pair $(r . m)$.
   car atf;

procedure mri_opn(atf);
   (if atom w then w else car w) where w=mri_op atf;

procedure mri_m(atf);
   % Mixed-real-integer modulus operator. [atf] is an atomic formula
   % $t_1 \equiv_m t_2$. Returns $m$.
   cdadr atf;

procedure mri_type(atf);
   % Mixed-real-integer type. [atf] is an atomic formula. Returns the
   % type of atf, which is ['real], ['int], or [nil].
   cdddr atf and cadddr atf;

procedure mri_arg2l(atf);
   % Mixed-real-integer left hand side argument. [atf] is an atomic
   % formula $r(t_1,t_2)$. Returns $t_1$.
   cadr atf;

procedure mri_mkop(op,m);
   % Mixed-real-integer make operator. [op] is an operator; [m] is an
   % optional modulus. Returns $op$ if the operator is not 'cong or
   % 'ncong and $([op] . [m])$ otherwise.
   op . m;

procedure mri_0mk2(op,lhs,type);
   % Mixed-real-integer make zero right hand atomic formula. [op] is
   % an operator; [lhs] is a term; [type] is one of ['real], ['int],
   % ['nil]. Returns the atomic formula $[op]([lhs],0)$.
   {op,lhs,nil,type};

procedure mri_atfp(f);
   % Mixed-real-integer atomic formula predicate. [f] is a formula.
   % Returns non-[nil] iff [f] has a legal relation name.
   mri_opn f memq '(equal neq leq geq lessp greaterp cong ncong);

procedure mri_congp(atf);
   % Mixed-real-integer congruence predicate. [atf] is an atomic
   % formula. Returns non-[nil] iff the operator is 'cong or 'ncong.
   mri_opn atf memq '(cong ncong);

procedure mri_simplat1(at,sop);
   begin scalar type,w;
      if !*rlsimplfloor then
	 at := mri_0mk2(mri_op at,mri_simplfloor mri_arg2l at,mri_type at);
      type := mri_type at or mri_dettype at;
      if type eq 'int then
	 return mri_pasf2mri(pasf_simplat1(mri_2pasfat at,sop),'int);
      if not mri_congp at then
	 return mri_ofsf2mri(ofsf_simplat1(mri_2ofsfat at,sop),'real);
      return mri_0mk2(mri_op at,mri_arg2l at,type)
   end;

procedure mri_simplfloor(lhs);
   if not mri_floorkernelp lhs then
      lhs
   else
      mri_simplfloor1 lhs;

procedure mri_simplfloor1(lhs);
   begin scalar l,r,w;
      if domainp lhs then
	 return lhs;
      l := mri_simplfloor lc lhs;
      r := mri_simplfloor red lhs;
      w := mri_irsplit mvar lhs;
      return addf(multf(l,exptf(addf(car w,cdr w),ldeg lhs)),r)
   end;

procedure mri_irsplit(k);
   begin scalar w;
      if not eqcar(k,'floor) then
      	 return !*k2f k . nil;
      w := mri_irsplit1 mri_simplfloor numr simp cadr k;
      return car w . if cdr w then !*k2f !*a2k {'floor,prepf cdr w}
   end;

procedure mri_irsplit1(k);
   begin scalar l,r,v; integer d;
      if domainp k then
      	 return k . nil;
      r := mri_irsplit1 red k;
      d := ldeg k;
      v := exptf(!*k2f mvar k,d);
      if mri_realvarp mvar v then
	 return car r . addf(multf(lc k,v),cdr r);
      l := mri_irsplit1 lc k;
      return addf(multf(car l,v),car r) . addf(multf(cdr l,v),cdr r)
   end;

procedure mri_floorkernelp(f);
   mri_floorp kernels f;

procedure mri_floorp(l);
   l and (eqcar(car l,'floor) or mri_floorp cdr l);

procedure mri_dettype(at);
   begin scalar varl,v,c,foundreal,foundint;
      varl := kernels mri_arg2l at;
      if null varl then
	 return 'int;
      c := t; while c and varl do <<
	 v := car varl;
	 varl := cdr varl;
	 if mri_realvarp v then
	    if foundint then
 	       c := foundint := foundreal := nil
	    else
	       foundreal := 'real
	 else
	    if foundreal then
 	       c := foundint := foundreal := nil
	    else
	       foundint := 'int
      >>;
      return foundint or foundreal
   end;

procedure mri_negateat(at);
   mri_pasf2mriat(pasf_negateat mri_2pasfat at,mri_type at);

procedure mri_varlat(at);
   pasf_varlat mri_2pasfat at;

procedure mri_ordatp(at1,at2);
   pasf_ordatp(mri_2pasfat at1,mri_2pasfat at2);

procedure mri_simplb(f,v);
   pasf_simplb(f,v);

procedure mri_bsatp(f,var);
   f eq 'true;

procedure mri_subat(al,at);
   mri_0mk2(mri_op at,numr subf(mri_arg2l at,al),nil);

procedure mri_eqnrhskernels(x);
   nconc(kernels numr w,kernels denr w) where w=simp cdr x;

procedure mri_expand(f);
   begin scalar !*rlverbose, w;
      rl_set '(mri_pasf) where !*msg=nil;  % Hack! TS
      w := if mri_bqp f then mri_pasf2mri(pasf_expand mri_2pasf f,nil) else f;
      rl_set '(mri) where !*msg=nil;
      return w
   end;

procedure mri_bqp(f);
   smemq('bex,f) or smemq('ball,f);

endmodule;

end;
