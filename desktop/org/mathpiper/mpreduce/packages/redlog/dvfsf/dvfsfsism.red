% ----------------------------------------------------------------------
% $Id: dvfsfsism.red 67 2009-02-05 18:55:15Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 1995-2009 Andreas Dolzmann and Thomas Sturm
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
   fluid '(dvfsf_sism_rcsid!* dvfsf_sism_copyright!*);
   dvfsf_sism_rcsid!* :=
      "$Id: dvfsfsism.red 67 2009-02-05 18:55:15Z thomas-sturm $";
   dvfsf_sism_copyright!* := "Copyright (c) 1995-2009 A. Dolzman and T. Sturm"
>>;

module dvfsfsism;
% Discretely valued field standard form simplify smart. Submodule of
% [dvfsf]. This module provides the black boxes [rl_smupdknowl],
% [rl_smrmknowl], [rl_smcpknowl], [rl_smmkatl], and [rl_susirmknowl]
% to [cl_simpl]. They are used with switch [rlsism] on.

procedure dvfsf_smupdknowl(op,atl,knowl,n);
   % Discretely valued field smart simplification update knowledge.
   % [op] is one of [and], [or]; [atl] is a list of (simplified)
   % atomic formulas; [knowl] is a conjunctive IRL; [n] is the current
   % level. Returns an IRL. Destructively updates [knowl] wrt. the
   % [atl] information. Accesses the switch [rlsusi].
   if !*rlsusi then
      cl_susiupdknowl(op,atl,knowl,n)
   else
      cl_smupdknowl(op,atl,knowl,n);

procedure dvfsf_smrmknowl(knowl,v);
   % Discretely valued field smart simplification remove from
   % knowledge. [knowl] is an IRL; [v] is a variable. Returns an IRL.
   % Destructively removes all information about [v] from [knowl].
   % Accesses the switch [rlsusi].
   if !*rlsusi then
      dvfsf_susirmknowl(knowl,v)
   else
      cl_smrmknowl(knowl,v);

procedure dvfsf_smcpknowl(knowl);
   % Discretely valued field smart simplification copy knowledge.
   % [knowl] is an IRL. Returns an IRL. Copies [knowl]. Accesses the
   % switch [rlsusi].
   if !*rlsusi then
      cl_susicpknowl(knowl)
   else
      cl_smcpknowl(knowl);

procedure dvfsf_smmkatl(op,knowl,newknowl,n);
   % Discretely valued field smart simplification make atomic formula
   % list. [op] is one of [and], [or]; [oldknowl] and [newknowl] are
   % IRL's; [n] is an integer. Returns a list of atomic formulas.
   % Accesses the switch [rlsusi].
   if !*rlsusi then
      cl_susimkatl(op,knowl,newknowl,n)
   else
      cl_smmkatl(op,knowl,newknowl,n);

procedure dvfsf_susirmknowl(knowl,v);
   % Discretely valued field susi remove knowledge. [knowl] is a
   % KNOWL; [v] is a variable. Returns a KNOWL. Remove all information
   % about [v] from [knowl].
   for each p in knowl join
      if v memq dvfsf_varlat car p then nil else {p};

procedure dvfsf_susibin(old,new);
   % Discretely valued field standard form susi binary smart simplification.
   % [old] and [new] are LAT's. Returns ['false] or a SUSIPRG. We
   % assume that [old] is a part of a already existence KNOWL and new
   % has to be added to this KNOWL.
   begin scalar oop,olhs,orhs,olev,nop,nlhs,nrhs,nlev,!*rlsiexpl;
      olev := cdr old;
      old := car old;
      oop := dvfsf_op old;
      olhs := dvfsf_arg2l old;
      orhs := dvfsf_arg2r old;
      nlev := cdr new;
      new := car new;
      nop := dvfsf_op new;
      nlhs := dvfsf_arg2l new;
      nrhs := dvfsf_arg2r new;
      if olhs = nlhs and orhs = nrhs then
      	 return dvfsf_susibin1(oop,nop,nlhs,nrhs,nlev);
      if (olhs = nrhs and orhs = nlhs) then
	 % [oop], [noop] cannot be [equal], [neq]
	 return dvfsf_susibin2(oop,nop,nlhs,nrhs,nlev);
      if (oop eq 'equal or oop eq 'neq) and nop neq 'equal and nop neq 'neq and
	 dvfsf_susibin!-eqlhsmatch(nlhs,nrhs,olhs)
      then
	 return dvfsf_susibin1(oop,nop,nlhs,nrhs,nlev);
      if (nop eq 'equal or nop eq 'neq) and oop neq 'equal and oop neq 'neq and
	 dvfsf_susibin!-eqlhsmatch(olhs,orhs,nlhs)
      then
	 return dvfsf_susibin1(oop,nop,olhs,orhs,nlev);
      return nil
   end;

procedure dvfsf_susibin!-eqlhsmatch(lhs,rhs,eqlhs);
   % Discretely valued field standard form super simplifier binary
   % smart simplification equal left hand side match. Check if
   % $[lhs]-[rhs]=[eqlhs]$.
   begin scalar w;
      w := dvfsf_simplat1(dvfsf_0mk2('equal,addf(lhs,negf rhs)),nil);
      if not rl_tvalp w then
	 return dvfsf_arg2l w = eqlhs;
      return nil
   end;

procedure dvfsf_susibin1(rold,rnew,lhs,rhs,nlev);
   if rold eq rnew then
      '((delete . t))
   else if rold eq 'neq then
      if rnew eq 'equal then
	 'false
      else if rnew eq 'sdiv or rnew eq 'nassoc then
	 '((delete . nil))
      else
	 nil
   else if rold eq 'sdiv then
      if rnew eq 'neq or rnew eq 'div or rnew eq 'nassoc then
	 '((delete . t))
      else  % [rnew memq '(assoc equal)]
	 'false
   else if rold eq 'div then
      if rnew eq 'sdiv or rnew eq 'assoc or rnew eq 'equal then
	 '((delete . nil))
      else if rnew eq 'nassoc then
	 {'(delete . nil),'(delete . t),
	    'add . (dvfsf_mk2('sdiv,lhs,rhs) . nlev)}
      else
	 nil
   else if rold eq 'assoc then
      if rnew eq 'sdiv or rnew eq 'nassoc then
	 'false
      else if rnew eq 'div then
	 '((delete . t))
      else if rnew eq 'equal then
	 '((delete . nil))
      else  % [rnew eq 'neq]
	 nil
   else if rold eq 'equal then
      if rnew eq 'neq or rnew eq 'sdiv or rnew eq 'nassoc then
	 'false
      else  % [rnew memq '(div, assoc)]
	 '((delete . t))
   else if rold eq 'nassoc then
      if rnew eq 'sdiv then
	 '((delete . nil))
      else if rnew eq 'assoc or rnew eq 'equal then
	 'false
      else if rnew eq 'div then
	 {'(delete . nil),'(delete . t),
	    'add . (dvfsf_mk2('sdiv,lhs,rhs) . nlev)}
      else  % [rnew eq 'neq]
	 '((delete . t))
   else
      rederr {"BUG IN dvfsf_susibin1(",rold,",",rnew,")"};

procedure dvfsf_susibin2(rold,rnew,nlhs,nrhs,nlev);
   % Smart simplification with crossed sides. Assumed to be called
   % with valuation relations only, and also not with two of the
   % symmetric relations [assoc], [nassoc].
   if rold eq 'div then
      if rnew eq 'sdiv then
	 'false
      else if rnew eq 'div then
	 {'(delete . nil),'(delete . t),
	    'add . (dvfsf_simplat1(dvfsf_mk2('assoc,nlhs,nrhs),nil) . nlev)}
      else if rnew eq 'assoc then
	 '((delete . nil))
      else if rnew eq 'nassoc then
	 {'(delete . nil),'(delete . t),
	    'add . (dvfsf_mk2('sdiv,nrhs,nlhs) . nlev)}
      else
	 nil
   else if rold eq 'sdiv then
      if rnew eq 'div or rnew eq 'sdiv or rnew eq 'assoc then
	 'false
      else if rnew eq 'nassoc then
	 '((delete . t))
      else
	 nil
   else if rold eq 'nassoc then
      if rnew eq 'sdiv then
	 '((delete . nil))
      else if rnew eq 'div then
	 {'(delete . nil),'(delete . t),
	    'add . (dvfsf_mk2('sdiv,nlhs,nrhs) . nlev)}
      else
	 nil
   else if rold eq 'assoc then
      if rnew eq 'sdiv then
	 'false
      else if rnew eq 'div then
	 '((delete . t))
      else
	 nil
   else
      nil;

procedure dvfsf_susipost(atl,knowl);
   % Discretely valued field standad form susi post simplification. [atl] is a
   % list of atomic formulas. [knowl] is a KNOWL. Returns a list
   % $\lambda$ of atomic formulas, such that
   % $\bigwedge[knowl]\land\bigwedge\lambda$ is equivalent to
   % $\bigwedge[knowl]\land\bigwedge[atl]$
   atl;

procedure dvfsf_susitf(at,knowl);
   % Discretely valued field standard form susi transform. [at] is an
   % atomic formula, [knowl] is a knowledge. Returns an atomic formula
   % $\alpha$ such that $\alpha\land\bigwedge[knowl]$ is equivalent to
   % $[at]\land\bigwedge[knowl]$. $\alpha$ has possibly a more
   % convenient relation than [at].
   at;

endmodule;  % [dvfsfsism]

end;  % of file
