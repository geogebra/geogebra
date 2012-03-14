% ----------------------------------------------------------------------
% $Id: acfsfsiat.red 67 2009-02-05 18:55:15Z thomas-sturm $
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
   fluid '(acfsf_siat_rcsid!* acfsf_siat_copyright!*);
   acfsf_siat_rcsid!* :=
      "$Id: acfsfsiat.red 67 2009-02-05 18:55:15Z thomas-sturm $";
   acfsf_siat_copyright!* := "Copyright (c) 1995-2009 A. Dolzmann and T. Sturm"
>>;

module acfsfsiat;
% Algebraically closed field standard form simplification for atomic
% formulas. Submodule of [acfsf]. This submodule provides the service
% [rl_simplat1] to [cl_simpl].

procedure acfsf_simplat1(f,sop);
   % Algebraically closed field standard form simplify atomic formula.
   % [f] is an atomic formula; [sop] is the complex formula operator
   % [f] occurs with or [nil]. Accesses switches [rlsiatadv],
   % [rlsifac], [rlsiexpl], and [rlsiexpla]. Returns a quantifier-free
   % formula that is a simplified equivalent of [f].
   begin scalar rel,lhs;
      rel := acfsf_op f;
      if not (rel memq '(equal neq)) then
 	 return nil;
      lhs := acfsf_arg2l f;
      if domainp lhs then
 	 return if acfsf_evalatp(rel,lhs) then 'true else 'false;
      lhs := quotf(lhs,sfto_dcontentf lhs);
      if minusf lhs then
    	 lhs := negf lhs;
      if null !*rlsiatadv then return acfsf_0mk2(rel,lhs);
      if rel eq 'equal then return acfsf_simplequal(lhs,sop);
      if rel eq 'neq then return acfsf_simplneq(lhs,sop)
   end;

procedure acfsf_simplequal(lhs,sop);
   % Algebraically closed field standard form simplify [equal]-atomic
   % formula. [lhs] is a term. Returns a quantifier-free formula.
   begin scalar w,ff,ww;
      ff := sfto_sqfpartf lhs;
      if !*rlsifac and (!*rlsiexpla or !*rlsiexpl and sop = 'or) then
	 return acfsf_facequal ff;
      return acfsf_0mk2('equal,ff)
   end;

procedure acfsf_facequal(f);
   % Left hand side factorization [equal] case.
   rl_smkn('or,for each x in cdr fctrf f collect acfsf_0mk2('equal,car x));

procedure acfsf_simplneq(lhs,sop);
   % Algebraically closed field standard form simplify [neq]-atomic
   % formula. [lhs] is a term. Returns a quantifier-free formula.
   begin scalar w,ff,ww;
      ff := sfto_sqfpartf lhs;
      if !*rlsifac and (!*rlsiexpla or !*rlsiexpl and sop = 'and) then
	 return acfsf_facneq ff;
      return acfsf_0mk2('neq,ff)
   end;

procedure acfsf_facneq(f);
   % Left hand side factorization [neq] case.
   rl_smkn('and,for each x in cdr fctrf f collect acfsf_0mk2('neq,car x));

procedure acfsf_evalatp(rel,lhs);
   % Algebraically closed field standard form evaluate atomic formula.
   % [rel] is a relation; [lhs] is a domain element. Returns a truth
   % value equivalent to $[rel]([lhs],0)$.
   if rel eq 'equal then null lhs
   else if rel eq 'neq then not null lhs
   else rederr {"acfsf_evalatp: unknown operator ",rel};

endmodule;  % [acfsfsiat]

end;  % of file
