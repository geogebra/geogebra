% ----------------------------------------------------------------------
% $Id: talpsiat.red 81 2009-02-06 18:22:31Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2004-2009 Andreas Dolzmann and Thomas Sturm
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
   fluid '(talp_siat_rcsid!* talp_siat_copyright!*);
   talp_siat_rcsid!* :=
      "$Id: talpsiat.red 81 2009-02-06 18:22:31Z thomas-sturm $";
   talp_siat_copyright!* := "Copyright (c) 2004-2009 A. Dolzmann and T. Sturm"
>>;

module talpsiat;
% Term algebra Lisp prefix simplify for atomic formulas. Submodule of
% [talp].

procedure talp_simplat1(at,sop);
   % Term algebra Lisp prefix simplify atomic formulas. [at] is an
   % atomic formula; [sop] is the boolean operator [at] occurs with or
   % [nil] (this operator currently has no effect on the result).
   % Returns a simplified atomic formula. Calls talp_simplat1 to
   % simplify [at] and transforms the result into lisp prefix form if
   % necessary (t -> 'true, nil -> 'false).
   begin scalar result;
      at := talp_simpat at;
      result := talp_simplat2(talp_op at,talp_arg2l at,talp_arg2r at);
      if result then
	 if result eq t then
	    return 'true
	 else return result
      else
	 return 'false
   end;

procedure talp_simplat2(op,lhs,rhs);
   % Term algebra Lisp prefix simplify atomic formulas subroutine. [op]
   % is one of ['equal], ['neq]; [lhs], [rhs] are the terms (in Lisp
   % prefix form) to be compared / simplified. Returns an equivalent
   % simplified atomic formula respectively a conjunction / disjunction
   % of atomic formulae, true is represented by t, false by nil.
   % ['inv]-term on right hand side
   if talp_invp rhs then
      talp_simplatrinv(op,lhs,rhs)
   % left term constant or variable
   else if atom lhs then
      talp_simplatat(op,lhs,rhs)
   % ['inv]-term on left hand side
   else if talp_invp lhs then
      talp_simplatlinv(op,lhs,rhs)
   % left term function
   else
      talp_simplatfn(op,lhs,rhs);

procedure talp_simplatrinv(op,lhs,rhs);
   % Term algebra Lisp prefix simplify atomic formulae with
   % ['inv]-term on the right hand side. [op] is one of ['equal],
   % ['neq]; [lhs], [rhs] are the terms (in Lisp prefix form) to be
   % compared / simplified. Returns an equivalent simplified atomic
   % formula respectively a conjunction / disjunction of atomic
   % formulae, true is represented by t, false by nil.
   <<
      rhs := talp_simplt rhs;
      if talp_invp rhs then
	 if talp_invp lhs then <<
	    lhs := talp_simplt lhs;
	    if talp_invp lhs then
	       % matching ['inv]s
%	       if talp_invf lhs eq talp_invf rhs and
%	       talp_invn lhs eq talp_invn rhs then
%		  talp_simplat2(op,talp_invarg lhs,talp_invarg rhs)
	       if talp_eqtp(lhs,rhs) then 
		  if op eq 'equal then t else nil
	       % ['inv] mismatch
	       else
		  talp_mk2(op,lhs,rhs)
	    % lhs no inv-term after simplification
	    else
	       talp_simplat2(op,lhs,rhs)
	 >>
	 % lhs no inv-term
	 else
	    talp_mk2(op,talp_simplt lhs,rhs)
      % rhs no inv-term after simplification
      else
	 talp_simplat2(op,lhs,rhs)
   >>;

procedure talp_simplatlinv(op,lhs,rhs);
   % Term algebra Lisp prefix simplify atomic formulae with
   % ['inv]-term on the left hand side. [op] is one of ['equal],
   % ['neq]; [lhs], [rhs] are the terms (in Lisp prefix form) to be
   % compared / simplified. Returns an equivalent simplified atomic
   % formula respectively a conjunction / disjunction of atomic
   % formulae, true is represented by t, false by nil. Cases of
   % ['inv]-terms on the right hand side are handled by
   % [talp_simplatrinv].
   <<
      lhs := talp_simplt lhs;
      if talp_invp lhs then
	 talp_mk2(op,lhs,rhs)
      else
	 talp_simplat2(op,lhs,talp_simplt rhs)
   >>;

procedure talp_simplatfn(op,lhs,rhs);
   % Term algebra Lisp prefix simplify atomic formulae with a function
   % on the left hand side. [op] is one of ['equal], ['neq]; [lhs],
   % [rhs] are the terms (in Lisp prefix form) to be compared /
   % simplified. Returns an equivalent simplified atomic formula
   % respectively a conjunction / disjunction of atomic formulae, true
   % is represented by t, false by nil.
   begin scalar result,list;
      if atom rhs then
	 if not atsoc(rhs,talp_getl()) then
	    if talp_telp(rhs,lhs) then
	       return op neq 'equal
	    else
	       return talp_mk2(op,talp_simplt lhs,rhs)
	 else
	    return op neq 'equal
      else <<
	 result := talp_fop lhs eq talp_fop rhs;
	 lhs := talp_fargl lhs;
	 rhs := talp_fargl rhs;
	 % check arguments
	 while lhs and rhs and result do <<
	    result := talp_simplat2('equal,car lhs,car rhs);
	    if result and result neq t then
	       if op eq 'equal then
		  list := result . list
	       else list := ('neq . cdr result) . list;
	    lhs := cdr lhs;
	    rhs := cdr rhs;
	 >>;
	 if result and list then
	    % joining more than one equality/inequality with and/or
	    if cdr list then
	       if op eq 'equal then
		  return 'and . list
	       else
		  return 'or . list
	    % result single equality/inequality
	    else
	       return car list
         % result t/nil
	 else if op eq 'equal then
	    return result
	 else
	    return not result
      >>
   end;

procedure talp_simplatat(op,lhs,rhs);
   % Term algebra Lisp prefix simplify atomic formulae with an atom
   % (i.e. variable or constant) on the formula's left hand side.
   % [op] is one of ['equal], ['neq]; [lhs], [rhs] are the terms (in
   % Lisp prefix form) to be compared / simplified. Returns an
   % equivalent simplified atomic formula respectively a conjunction
   % /disjunction of atomic formulae, true is represented by t, false
   % by nil.
   if lhs eq rhs then
      op eq 'equal
   % left term variable
   else if not atsoc(lhs,talp_getl()) then
      if atom rhs then
	 talp_mk2(op,lhs,rhs)
      else if talp_telp(lhs,rhs) then
	 op neq 'equal
      else
	 talp_mk2(op,lhs,rhs)
   %left term constant
   else if atom rhs then
      % right term variable
      if not atsoc(rhs,talp_getl()) then
	 talp_mk2(op,lhs,talp_simplt rhs)
      % right term constant
      else
	 op neq 'equal
   % right term function
   else
      op neq 'equal;

procedure talp_simplt(term);
   % Term algebra Lisp prefix simplify term subroutine . [term] is a
   % term. Returns an equivalent term to [term] by calling
   % [talp_simplt1].
   talp_simplt1(term,nil);

procedure talp_simplt1(term,stack);
   % Term algebra Lisp prefix simplify term subroutine . [term] is a
   % term, [stack] contains the ['inv]s to be simplified after
   % evaluating their arguments (initially nil). Returns a simplified
   % term equivalent to [term].
   begin scalar arg;
      if talp_invp term then <<
	 arg := talp_invarg term;
	 % variable as argument - no more simplifications applicable
	 if atom arg and not atsoc(arg,talp_getl()) then <<
	    if stack then
	       while stack do <<
		  term := talp_mkinv(stack_top stack,term);
		  stack := stack_pop stack
	       >>;
	    return term
	 >>
	 % ['inv]-term's argument ['inv]-term - push outer ['inv] on to stack
	 else if talp_invp arg then
	    return talp_simplt1(arg,stack_push(talp_op term,stack))
	 % ['inv]-term's argument function - return nth argument
	 else if eqcar(arg,talp_invf term) then
	    return talp_simplt1(nth(talp_fargl arg,talp_invn term),stack)
         % ['inv] function symbol and ['inv] argument's function symbol
	 % mismatch - return whole argument
	 else return talp_simplt1(arg,stack)
      >>
      % simplify outer ['inv]s: pop ['inv]s from stack
      else <<
	 if pairp term then
	    term := talp_fop term . for each arg in talp_fargl term collect
	       talp_simplt1(arg,nil);
         if stack then
	    return talp_simplt1(talp_mkinv(stack_top stack,term),
	                         stack_pop stack)
      	 % stack empty
      	 else
	    if talp_invp term then
	       % Remove superfluous parenthesis from ['inv]-term
	       return car term
      	    else
	       return term
      >>
   end;

procedure talp_telp(var,term);
   % Term algebra Lisp prefix term element predicate. [var] is a
   % variable, [term] is a term. Returns t if [var] matches with
   % [term] or is included in [term] but not within the scope of an
   % ['inv]-term.
   begin scalar flag;
      if atom term then
	 return var eq term
      else if not talp_invp term then <<
	 term := talp_fargl term;
	 while not flag and term do <<
	    flag := talp_telp(var,car term);
	    term := cdr term
	 >>;
	 return flag
      >>
   end;

procedure stack_new();
   % New stack. Returns the empty stack.
   nil;

procedure stack_push(x,s);
   % Push. [x] is any, [s] is a stack. Returns a stack. Pushes [x]
   % onto the top of s.
   x . s;

procedure stack_top(s);
   % Top. [s] is a stack. Returns any. Returns the top element of
   % [s]. [s] is not modified.
   car s;

procedure stack_pop(s);
   % Pop. [s] is a stack. Returns a stack. Removes the top element
   % from [s], if present.
   cdr s;

procedure stack_print(s);
   % Print stack. [s] is a stack. Returns nil. Prints [s] to the
   % screen.
   for each x in s do
      prin2t x;

endmodule;  % [talpsiat]

end;  % of file
