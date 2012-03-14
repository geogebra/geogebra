% ----------------------------------------------------------------------
% $Id: clsl.red 1268 2011-08-14 12:05:46Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2010 Thomas Sturm
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
   fluid '(cl_sl_rcsid!* cl_sl_copyright!*);
   cl_sl_rcsid!* := "$Id: clsl.red 1268 2011-08-14 12:05:46Z thomas-sturm $";
   cl_sl_copyright!* := "Copyright (c) 2010 T. Sturm"
>>;

module clsl;

struct formula checked by formulap;
struct slp checked by slpp;
struct list checked by listp;
struct number checked by numberp;

procedure cl_satnum(slp);
   for each line in slp sum rl_atnum sll_rhs line;

declare cl_satnum: (slp) -> number;

procedure cl_ssimpl(slp);
   begin scalar old;
      repeat <<
      	 old := slp;
      	 slp := cl_ssimpl1 slp
      >> until eqn(length slp,length old);
      return slp
   end;

procedure cl_ssimpl1(slp);
   sl_reduce for each line in slp collect
      sll_mkx(sll_lhs line,rl_simpl(sll_rhs line,nil,-1));

declare cl_ssimpl: (slp) -> slp;

procedure cl_straightify(f);
   % [f] is a formula. Returns an SLPROG. The result is a minimal SLPROG
   % equivalent to [f].
   cl_ssimpl cl_straightify1 {sll_mk cl_simpl(f,nil,-1)};

declare cl_straightify: (formula) -> slp;

procedure cl_sstraightify(slp);
   cl_ssimpl cl_straightify1  cl_ssimpl slp;

procedure cl_straightify1(slp);
   % [slp] is an SLPROG. Returns an SLPROG, where [nil] indicates "no
   % success."
   begin scalar w,cand,candml,sal; integer n,lb;
      if !*rlverbose then
	 ioto_tprin2t "entering cl_straightify1";
      n := lto_max for each sl in slp collect cl_depth sll_rhs sl;
      lb := if !*slat then 0 else 1;
      for i := n step -1 until lb do <<
	 if !*rlverbose then
	    ioto_tprin2 {"i=",i,", "};
	 candml := cl_dcollect(slp,i);
	 sal := for each cand in candml join
	    if cdr cand >= 2 then
	       {car cand . slv_new()};
	 slp := cl_sreplace(slp,sal);
	 for each pr in sal do
	    slp := sll_mkx(cdr pr,car pr) . slp
      >>;
      if !*rlverbose then
	 ioto_tprin2t "leaving cl_straightify1";
      return slp
   end;

declare cl_straightify1: (slp) -> slp;

procedure cl_sreplace(slp,sal);
   if sal then
      for each sl in slp collect
      	 sll_mkx(sll_lhs sl,cl_replace1(sll_rhs sl,sal))
   else
      slp;

procedure cl_dcollect(slp,n);
   % Depth collect. [slp] is an SLPROG, [n] is a (positive) number.
   % Returns the list of all subformulas of depth [n] in [slp].
   lto_almerge(
      for each sl in slp collect cl_dcollect1(sll_rhs sl,n),
      function plus2);

declare cl_dcollect: (slp,number) -> list;

procedure cl_dcollect1(f,n);
   % Recursive subroutine of cl_dcollect for one single SL. [sth] is a
   % formula or an SL.
   begin scalar d,op,w,candml;
      d := cl_sldepth f;
      if eqn(d,n) then
      	 return {f . 1};
      if d < n then
	 return nil;
      op := rl_op f;
      if rl_boolp op then
	 return lto_almerge(
	    for each sub in rl_argn f collect cl_dcollect1(sub,n),
	    function plus2);
      if rl_quap op then
	 return cl_dcollect1(rl_mat f,n);
      if rl_bquap op then
	 lto_almerge(
	    {cl_dcollect1(rl_b f,n),cl_dcollect1(rl_mat f,n)},
	    function plus2);
      if (w := rl_external(rl_op f,'cl_dcollect1)) then
      	 return apply(w,{f,n});
      rederr {"something wrong in cl_dcollect1:",f}
   end;

declare cl_dcollect1: (any,number) -> list;

procedure cl_sldepth(f);
   % Wrapper for cl_depth, which modifies the depth of SLVs from 0 to
   % -1.
   if eqcar(f,'slv) then -1 else cl_depth f;

declare cl_sldepth: (any) -> number;

procedure cl_spnf(slp);
   begin scalar tslp,lst;
      tslp := for each l in slp collect
	 sll_mkx(sll_lhs l,cl_pnf sll_rhs l);
      tslp := sl_unstraightify1(tslp,function(lambda x; rl_quap rl_op x));
      tslp := reversip tslp;
      lst := car tslp;
      tslp := cdr tslp;
      lst := sll_mkx(sll_lhs lst,cl_pnf sll_rhs lst);
      tslp := lst . tslp;
      return reversip tslp
   end;

endmodule;

end;  % of file
