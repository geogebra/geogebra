% ----------------------------------------------------------------------
% $Id: ibalpqsat.red 1820 2012-11-08 08:42:24Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2007-2009 Andreas Dolzmann and Thomas Sturm
% ---------------------------------------------------------------------
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
   fluid '(ibalp_qsat_rcsid!* ibalp_qsat_copyright!*);
   ibalp_qsat_rcsid!* :=
      "$Id: ibalpqsat.red 1820 2012-11-08 08:42:24Z thomas-sturm $";
   ibalp_qsat_copyright!* := "Copyright (c) 2007-2009 A. Dolzmann and T. Sturm"
>>;

module ibalpqsat;

fluid '(ibalp_qsatoptions!* !*rlverbose);

fluid '(donel!*, numcdcl!*, numlocs!*);

procedure my_mkvect(n);
   <<
      ioto_tprin2t {"entering mkvect n=",n};
      for i := 0:n collect (i . nil)
   >>;

procedure my_putv(v,n,a);
   begin scalar w;
      ioto_tprin2t {"entering putv n=",n," size=",length v};
      w := assoc(n,v);
      cdr w := a;
      return v
   end;

procedure my_getv(v,n);
   <<
      ioto_tprin2t {"entering getv n=",n," size=",length v};
      cdr assoc(n,v)
   >>;

procedure my_mkvect(n);
   <<
      ioto_tprin2t {"entering mkvect n=",n};
      mkvect n
   >>;

procedure my_putv(v,n,a);
   begin scalar w;
      ioto_tprin2t {"entering putv n=",n," size=",upbv v};
      putv(v,n,a);
      ioto_tprin2t {"leaving putv n=",n," size=",upbv v};
   end;

procedure my_getv(v,n);
   <<
      ioto_tprin2t {"entering getv n=",n," size=",upbv v};
      getv(v,n)
   >>;

procedure ibalp_qsat!-initoptions();
   % Initialise the options. Sets default values for all options.
   ibalp_qsat!-setoptionl({'zmom,5,1,1.2,200});

procedure ibalp_qsat!-getoptionl();
   % Get the option list. Returns the list with the options of the SAT
   % solver.
   begin scalar tlist;
      tlist := lto_catsoc('clause_del,ibalp_qsatoptions!*) . nil;
      tlist := lto_catsoc('res_inc,ibalp_qsatoptions!*) . tlist;
      tlist := lto_catsoc('first_val,ibalp_qsatoptions!*) . tlist;
      tlist := lto_catsoc('res_start,ibalp_qsatoptions!*) . tlist;
      tlist := lto_catsoc('heuristic,ibalp_qsatoptions!*) . tlist;
      return tlist
   end;

procedure ibalp_qsat!-setoptionl(optionl);
   % Set options. [optionl] is a list of options. It must be [nil] or
   % have five elements, indicating the branching heuristc, the
   % restart value, the first value to be set, the increase factor for
   % restarts, the bound for clause deletion. Returns the new list of
   % options.
   begin scalar temp;
      if null optionl then
	 return ibalp_qsat!-getoptionl();
      temp := car optionl;
      ibalp_qsatoptions!* := ('heuristic . temp) . nil;
      temp := cadr optionl;
      ibalp_qsatoptions!* := ('res_start . temp) . ibalp_qsatoptions!*;
      temp := caddr optionl;
      ibalp_qsatoptions!* := ('first_val . temp) . ibalp_qsatoptions!*;
      temp := cadddr optionl;
      ibalp_qsatoptions!* := ('res_inc . temp) . ibalp_qsatoptions!*;
      temp := car cddddr optionl;
      ibalp_qsatoptions!* := ('clause_del . temp) . ibalp_qsatoptions!*;
      return ibalp_qsat!-getoptionl();
   end;

procedure ibalp_qsat!-getoption(opt);
   % Get option. [opt] is one of the options heuristic, res_start,
   % first_val, res_inc or clause_del. Returns the corresponding
   % option.
   lto_catsoc(opt,ibalp_qsatoptions!*);

!#if t

smacro procedure ibalp_var!-new(id);
   % Create a new variable. [id] is the identifier of the
   % variable. Returns a list with the following components: l[0] is
   % the identifier; l[1] is the value of the variable; l[2] is the
   % list of positive occurences of the variable; l[3] is the list of
   % negative occurences; l[4] is the number of currently false
   % clauses where the variable has a positive occurence; l[5] is the
   % number of currently false clauses where the variable has a
   % negative occurence; l[6] is the level at which the variable was
   % set; l[7] is the reason for the variable; l[8] is the number of
   % positive occurences in new added conflict-clauses; l[9] is the
   % number of positive occurences in new added conflict-clauses;
   % l[10] is the list of watched clauses; l[11] is the f-Calc;
   % l[12] is the quantifier of this variable; l[13] is the
   % quantification level of the variable; l[14] is the flipped-flag.
   {id,nil,nil,nil,0,0,-1,nil,0,0,nil,0,nil,0,nil};

smacro procedure ibalp_var!-setval(var,val);
   % Set the value of a variable. [var] is a variable; [val] is the
   % value [0], [1] or [nil];
   cadr var := val;

smacro procedure ibalp_var!-setposocc(var,posocc);
   % Add a clause to the list of clauses where the variable has a
   % positive occurence. [var] is a variable; [negocc] the clause.
   caddr var := posocc . caddr var;

smacro procedure ibalp_var!-setnegocc(var,negocc);
   % Add a clause to the list of clauses where the variable has a
   % negative occurence. [var] is a variable; [negocc] the clause.
   cadddr var := negocc . cadddr var;

smacro procedure ibalp_var!-setposoccabs(var,posocc);
   % Add a clause to the list of clauses where the variable has a
   % positive occurence. [var] is a variable; [negocc] the clause.
   caddr var := posocc;

smacro procedure ibalp_var!-setnegoccabs(var,negocc);
   % Add a clause to the list of clauses where the variable has a
   % negative occurence. [var] is a variable; [negocc] the clause.
   cadddr var := negocc;

smacro procedure ibalp_var!-setnumpos(var,numpos);
   % Get the number of currently false clauses where the variable has
   % a positive occurence and is not set. [var] is a variable;
   % [numpos] the number of occurences.
   car cddddr var := numpos;

smacro procedure ibalp_var!-setnumneg(var,numneg);
   % Set the number of currently false clauses where the variable has
   % a negative occurence and is not set. [var] is a variable;
   % [numneg] the number of occurences.
   cadr cddddr var := numneg;

smacro procedure ibalp_var!-setlev(var,lev);
   % Set the level at which the variable was set. [var] is a variable;
   % [lev] is the number of the level;
   caddr cddddr var := lev;

smacro procedure ibalp_var!-setreas(var,reas);
   % Set the reason why the variable was set. [var] is a variable;
   % [reas] is the clause which became unit and forced the set or nil
   % if it was a decision.
   cadddr cddddr var := reas;

smacro procedure ibalp_var!-setposcc(var,num);
   % Set the number of positive occurences in added
   % conflict-clauses. [var] is a variable; [num] is the number of
   % occurences.
   car cddddr cddddr var := num;

smacro procedure ibalp_var!-setnegcc(var,num);
   % Set the number of negative occurences in added
   % conflict-clauses. [var] is a variable; [num] is the number of
   % occurences.
   cadr cddddr cddddr var := num;

smacro procedure ibalp_var!-setwc(var,wc);
   % Set the watched-clauses of a variable . [var] is a variable; [wc]
   % is a watched clause.
   caddr cddddr cddddr var := wc . caddr cddddr cddddr var;

smacro procedure ibalp_var!-setmom(var,mom);
   % Set the MOM-value for this variable. [var] is a variable; [mom]
   % is the MOM-value.
   cadddr cddddr cddddr var := mom;

smacro procedure ibalp_var!-setquant(var,quant);
   % Set the quantifier for this variable. [var] is a variable;
   % [quant] is [nil] if it is an unquantified variable, [ex] for an
   % existential quantified variable and [all] for an universal
   % quantified variable.
   car cddddr cddddr cddddr var := quant;

smacro procedure ibalp_var!-setqlevel(var,qlevel);
   % Set the quantifier-level for this variable. [var] is a variable;
   % [qlevel] the quantifier level.
   cadr cddddr cddddr cddddr var := qlevel;

smacro procedure ibalp_var!-setflip(var,flip);
   % Set the flip-level for this variable. [var] is a variable;
   % [flip] is the flipstatus.
   caddr cddddr cddddr cddddr var := flip;

smacro procedure ibalp_var!-getid(var);
   % Get the identifier of a variable. [var] is variable. Returns the
   % identifier.
   car var;

smacro procedure ibalp_var!-getval(var);
   % Get the current value of a variable. [var] is a variable. Returns
   % [1] if the variable is set to true, [0] if set to false and [nil]
   % if the variable is not set.
   cadr var;

smacro procedure ibalp_var!-getposocc(var);
   % Get the list of all clauses where the variable has a positive
   % occurence. [var] is a variable. Returns the list of clauses.
   caddr var;

smacro procedure ibalp_var!-getnegocc(var);
   % Get the list of all clauses where the variable has a negative
   % occurence. [var] is a variable. Returns the list of clauses.
   cadddr var;

smacro procedure ibalp_var!-getnumpos(var);
   % Get the number of currently false clauses where the variable has
   % a positive occurence and is not set. [var] is a variable. Returns
   % the number of clauses.
   car cddddr var;

smacro procedure ibalp_var!-getnumneg(var);
   % Get the number of currently false clauses where the variable has
   % a negative occurence and is not set. [var] is a variable. Returns
   % the number of clauses.
   cadr cddddr var;

smacro procedure ibalp_var!-getlev(var);
   % Get the level at which the variable was set. [var] is a
   % variable. Returns the level.
   caddr cddddr var;

smacro procedure ibalp_var!-getreas(var);
   % Get the reason why the variable was set. [var] is a variable.
   % Returns the clause which became unit and forced the set or [nil]
   % if a decision was the reason.
   cadddr cddddr var;

smacro procedure ibalp_var!-getposcc(var);
   % Get the number of positive occurences in added
   % conflict-clauses. [var] is a variable.  Returns the number of
   % positive occurences in conflict-clauses.
   car cddddr cddddr var;

smacro procedure ibalp_var!-getnegcc(var);
   % Get the number of negative occurences in added
   % conflict-clauses. [var] is a variable.  Returns the number of
   % negative occurences in conflict-clauses.
   cadr cddddr cddddr var;

smacro procedure ibalp_var!-getwc(var);
   % Get the watched-clauses of a variable . [var] is a variable.
   caddr cddddr cddddr var;

smacro procedure ibalp_var!-delwc(var,wc);
   % Delete a single watched-clauses of this variable . [var] is a
   % variable; [wc] is a clause.
   caddr cddddr cddddr var := delq(wc,caddr cddddr cddddr var);

smacro procedure ibalp_var!-delallwc(var);
   % Delete all watched-clauses of this variable . [var] is a
   % variable.
   caddr cddddr cddddr var := nil;

smacro procedure ibalp_var!-getmom(var);
   % Get the MOM-value for this variable. [var] is a variable.
   cadddr cddddr cddddr var;

smacro procedure ibalp_var!-getquant(var);
   % Get the quantifier for this variable. [var] is a variable;
   % Return [nil] if it is an unquantified variable, [ex] for an
   % existential quantified variable and [all] for an universal
   % quantified variable.
   car cddddr cddddr cddddr var;

smacro procedure ibalp_var!-isex(var);
   % Returns if a variable is existential quantified. [var] is a
   % variable. Returns [t] iff the var is existential quantified.
   car cddddr cddddr cddddr var eq 'ex;

smacro procedure ibalp_var!-isuni(var);
   % Returns if a variable is universal quantified. [var] is a
   % variable. Returns [t] iff the var is universal quantified.
   car cddddr cddddr cddddr var eq 'all;

smacro procedure ibalp_var!-getqlevel(var);
   % Get the quantifier-level for this variable. [var] is a variable;
   % Returns the quantifier level.
   cadr cddddr cddddr cddddr var;

smacro procedure ibalp_var!-getflip(var);
   % Get the flipstatus for this variable. [var] is a variable;
   % Returns [nil] if the variable is no decision variable, 0 if the
   % variable i unflipped and 1 if flipped.
   caddr cddddr cddddr cddddr var;

smacro procedure ibalp_clause!-new();
   % Create a new clause. Returns a list with the following
   % components: l[0] is a list of the positive literals of the
   % clause; l[1] is a list of the negative literals of the clause;
   % l[2] is the number of currently unset positive variables in the
   % clause; l[3] is the number of currently unset negative variables
   % in the clause; l[4] is the variable turning this clause to true
   % or [nil] if the clause is false; l[5] is a counter for new-added
   % clauses.
   {nil,nil,0,0,nil,nil,nil};

smacro procedure ibalp_clause!-setsat(clause,sat);
   % Set the variable turning a clause to true. [clause] is a clause;
   % [sat] is the variable turning to true
   car cddddr clause := sat . car cddddr clause;

smacro procedure ibalp_clause!-delallsat(clause);
   % Set the variable turning a clause to true. [clause] is a clause;
   % [sat] is the variable turning to true
   car cddddr clause := nil;

smacro procedure ibalp_clause!-setposlit(clause,var);
   % Add a variable to the list of positive literals of a
   % clause. [clause] is a clause; [var] is a variable.
   car clause := var . car clause;

smacro procedure ibalp_clause!-setneglit(clause,var);
   % Add a variable to the list of negative literals of a
   % clause. [clause] is a clause; [var] is a variable.
   cadr clause := var . cadr clause;

smacro procedure ibalp_clause!-setposlitabs(clause,var);
   % Add a variable to the list of positive literals of a
   % clause. [clause] is a clause; [var] is a variable.
   car clause := var;

smacro procedure ibalp_clause!-setneglitabs(clause,var);
   % Add a variable to the list of negative literals of a
   % clause. [clause] is a clause; [var] is a variable.
   cadr clause := var;

smacro procedure ibalp_clause!-setactpos(clause,actpos);
   % Set the number of positive literals that are currently
   % unset. [clause] is a clause; [actpos] is the number of currently
   % unset literals.
   caddr clause := actpos;

smacro procedure ibalp_clause!-setactneg(clause,actneg);
   % Set the number of negative literals that are currently
   % unset. [clause] is a clause; [actneg] is the number of currently
   % unset literals.
   cadddr clause := actneg;

smacro procedure ibalp_clause!-setcount(clause,count);
   % Set the current count for new-added clauses. [clause] is a
   % clause; [count] is the count.
   cadr cddddr clause := count;

smacro procedure ibalp_clause!-setwl(clause,wl);
   % Add a watched literal for this clause. [clause] is a
   % clause; [wl] is a variable.
   caddr cddddr clause := wl . caddr cddddr clause;

smacro procedure ibalp_clause!-delallwl(clause);
   % Delete the watched literals for this clause. [clause] is a
   % clause.
   caddr cddddr clause := nil;

smacro procedure ibalp_clause!-delwl(clause,wl);
   % Delete a single watched literal from this clause. [clause] is a
   % clause; [wl] is a variable.
   caddr cddddr clause := delq(wl,caddr cddddr clause);

smacro procedure ibalp_clause!-getposlit(clause);
   % Get a list of all positive literals of a clause. [clause] is a
   % clause. Returns the list of variables. [nil] if the clause has no
   % positive literals.
   car clause;

smacro procedure ibalp_clause!-getneglit(clause);
   % Get a list of all negative literals of a clause. [clause] is a
   % clause. Returns the list of variables. [nil] if the clause has no
   % negative literals.
   cadr clause;

smacro procedure ibalp_clause!-getactpos(clause);
   % Get the number of positive literals that are currently unset in a
   % clause. [clause] is a clause. Returns the number of literals
   caddr clause;

smacro procedure ibalp_clause!-getactneg(clause);
   % Get the number of negative literals that are currently unset in a
   % clause. [clause] is a clause. Returns the number of literals.
   cadddr clause;

smacro procedure ibalp_clause!-getsat(clause);
   % Get the variable turning a clause to true. [clause] is a
   % clause. Returns the variable or [nil] if the clause is false.
   car cddddr clause;

smacro procedure ibalp_clause!-delsat(clause,sat);
   % Delete a variable turning a clause to true. [clause] is a clause;
   % [sat] is a variable.
   car cddddr clause := delq(sat,car cddddr clause);

smacro procedure ibalp_clause!-getcount(clause);
   % Get the current count for new-added clauses. [clause] is a
   % clause. Return the count.
   cadr cddddr clause;

smacro procedure ibalp_clause!-getwl(clause);
   % Get the watched literals for this clause. [clause] is a
   % clause. Return the watched literal.
   caddr cddddr clause;

!#else

smacro procedure ibalp_var!-new(id);
   % Create a new variable. [id] is the identifier of the
   % variable. Returns a list with the following components: l[0] is
   % the identifier; l[1] is the value of the variable; l[2] is the
   % list of positive occurences of the variable; l[3] is the list of
   % negative occurences; l[4] is the number of currently false
   % clauses where the variable has a positive occurence; l[5] is the
   % number of currently false clauses where the variable has a
   % negative occurence; l[6] is the level at which the variable was
   % set; l[7] is the reason for the variable; l[8] is the number of
   % positive occurences in new added conflict-clauses; l[9] is the
   % number of positive occurences in new added conflict-clauses;
   % l[10] is the list of watched clauses; l[11] is the MOM-Calc;
   % l[12] is the quantifier of this variable; l[13] is the
   % quantification level of the variable; l[14] is the flipped-flag.
   begin scalar v;
      v := mkvect(14);
      putv(v,0,id);
      putv(v,4,0);
      putv(v,5,0);
      putv(v,6,-1);
      putv(v,8,0);
      putv(v,9,0);
      putv(v,11,0);
      putv(v,13,0);
      return v
   end;

smacro procedure ibalp_var!-setval(var,val);
   % Set the value of a variable. [var] is a variable; [val] is the
   % value [0], [1] or [nil];
   putv(var,1,val);

smacro procedure ibalp_var!-setposocc(var,posocc);
   % Add a clause to the list of clauses where the variable has a
   % positive occurence. [var] is a variable; [negocc] the clause.
   putv(var,2,posocc . getv(var,2));

smacro procedure ibalp_var!-setnegocc(var,negocc);
   % Add a clause to the list of clauses where the variable has a
   % negative occurence. [var] is a variable; [negocc] the clause.
   putv(var,3,negocc . getv(var,3));

smacro procedure ibalp_var!-setposoccabs(var,posocc);
   % Add a clause to the list of clauses where the variable has a
   % positive occurence. [var] is a variable; [negocc] the clause.
   putv(var,2,posocc);

smacro procedure ibalp_var!-setnegoccabs(var,negocc);
   % Add a clause to the list of clauses where the variable has a
   % negative occurence. [var] is a variable; [negocc] the clause.
   putv(var,3,negocc);

smacro procedure ibalp_var!-setnumpos(var,numpos);
   % Get the number of currently false clauses where the variable has
   % a positive occurence and is not set. [var] is a variable;
   % [numpos] the number of occurences.
   putv(var,4,numpos);

smacro procedure ibalp_var!-setnumneg(var,numneg);
   % Set the number of currently false clauses where the variable has
   % a negative occurence and is not set. [var] is a variable;
   % [numneg] the number of occurences.
   putv(var,5,numneg);

smacro procedure ibalp_var!-setlev(var,lev);
   % Set the level at which the variable was set. [var] is a variable;
   % [lev] is the number of the level;
   putv(var,6,lev);

smacro procedure ibalp_var!-setreas(var,reas);
   % Set the reason why the variable was set. [var] is a variable;
   % [reas] is the clause which became unit and forced the set or nil
   % if it was a decision.
   putv(var,7,reas);

smacro procedure ibalp_var!-setposcc(var,num);
   % Set the number of positive occurences in added
   % conflict-clauses. [var] is a variable; [num] is the number of
   % occurences.
   putv(var,8,num);

smacro procedure ibalp_var!-setnegcc(var,num);
   % Set the number of negative occurences in added
   % conflict-clauses. [var] is a variable; [num] is the number of
   % occurences.
   putv(var,9,num);

smacro procedure ibalp_var!-setwc(var,wc);
   % Set the watched-clauses of a variable . [var] is a variable; [wc]
   % is a watched clause.
   putv(var,10,wc . getv(var,10));

smacro procedure ibalp_var!-setmom(var,mom);
   % Set the MOM-value for this variable. [var] is a variable; [mom]
   % is the MOM-value.
   putv(var,11,mom);

smacro procedure ibalp_var!-setquant(var,quant);
   % Set the quantifier for this variable. [var] is a variable;
   % [quant] is [nil] if it is an unquantified variable, [ex] for an
   % existential quantified variable and [all] for an universal
   % quantified variable.
   putv(var,12,quant);

smacro procedure ibalp_var!-setqlevel(var,qlevel);
   % Set the quantifier-level for this variable. [var] is a variable;
   % [qlevel] the quantifier level.
   putv(var,13,qlevel);

smacro procedure ibalp_var!-setflip(var,flip);
   % Set the flip-level for this variable. [var] is a variable;
   % [flip] is the flipstatus.
   putv(var,14,flip);

smacro procedure ibalp_var!-getid(var);
   % Get the identifier of a variable. [var] is variable. Returns the
   % identifier.
   getv(var,0);

smacro procedure ibalp_var!-getval(var);
   % Get the current value of a variable. [var] is a variable. Returns
   % [1] if the variable is set to true, [0] if set to false and [nil]
   % if the variable is not set.
   getv(var,1);

smacro procedure ibalp_var!-getposocc(var);
   % Get the list of all clauses where the variable has a positive
   % occurence. [var] is a variable. Returns the list of clauses.
   getv(var,2);

smacro procedure ibalp_var!-getnegocc(var);
   % Get the list of all clauses where the variable has a negative
   % occurence. [var] is a variable. Returns the list of clauses.
   getv(var,3);

smacro procedure ibalp_var!-getnumpos(var);
   % Get the number of currently false clauses where the variable has
   % a positive occurence and is not set. [var] is a variable. Returns
   % the number of clauses.
   getv(var,4);

smacro procedure ibalp_var!-getnumneg(var);
   % Get the number of currently false clauses where the variable has
   % a negative occurence and is not set. [var] is a variable. Returns
   % the number of clauses.
   getv(var,5);

smacro procedure ibalp_var!-getlev(var);
   % Get the level at which the variable was set. [var] is a
   % variable. Returns the level.
   getv(var,6);

smacro procedure ibalp_var!-getreas(var);
   % Get the reason why the variable was set. [var] is a variable.
   % Returns the clause which became unit and forced the set or [nil]
   % if a decision was the reason.
   getv(var,7);

smacro procedure ibalp_var!-getposcc(var);
   % Get the number of positive occurences in added
   % conflict-clauses. [var] is a variable.  Returns the number of
   % positive occurences in conflict-clauses.
   getv(var,8);

smacro procedure ibalp_var!-getnegcc(var);
   % Get the number of negative occurences in added
   % conflict-clauses. [var] is a variable.  Returns the number of
   % negative occurences in conflict-clauses.
   getv(var,9);

smacro procedure ibalp_var!-getwc(var);
   % Get the watched-clauses of a variable . [var] is a variable.
   getv(var,10);

smacro procedure ibalp_var!-delwc(var,wc);
   % Delete a single watched-clauses of this variable . [var] is a
   % variable; [wc] is a clause.
   putv(var,10,delq(wc,getv(var,10)));

smacro procedure ibalp_var!-delallwc(var);
   % Delete all watched-clauses of this variable . [var] is a
   % variable.
   putv(var,10,nil);

smacro procedure ibalp_var!-getmom(var);
   % Get the MOM-value for this variable. [var] is a variable.
   getv(var,11);

smacro procedure ibalp_var!-getquant(var);
   % Get the quantifier for this variable. [var] is a variable;
   % Return [nil] if it is an unquantified variable, [ex] for an
   % existential quantified variable and [all] for an universal
   % quantified variable.
   getv(var,12);

smacro procedure ibalp_var!-isex(var);
   % Returns if a variable is existential quantified. [var] is a
   % variable. Returns [t] iff the var is existential quantified.
   getv(var,12) eq 'ex;

smacro procedure ibalp_var!-isuni(var);
   % Returns if a variable is universal quantified. [var] is a
   % variable. Returns [t] iff the var is universal quantified.
   getv(var,12) eq 'all;

smacro procedure ibalp_var!-getqlevel(var);
   % Get the quantifier-level for this variable. [var] is a variable;
   % Returns the quantifier level.
   getv(var,13);

smacro procedure ibalp_var!-getflip(var);
   % Get the flipstatus for this variable. [var] is a variable;
   % Returns [nil] if the variable is no decision variable, 0 if the
   % variable i unflipped and 1 if flipped.
   getv(var,14);

smacro procedure ibalp_clause!-new();
   % Create a new clause. Returns a list with the following
   % components: l[0] is a list of the positive literals of the
   % clause; l[1] is a list of the negative literals of the clause;
   % l[2] is the number of currently unset positive variables in the
   % clause; l[3] is the number of currently unset negative variables
   % in the clause; l[4] is the variable turning this clause to true
   % or [nil] if the clause is false; l[5] is a counter for new-added
   % clauses.
   begin scalar v;
      v := mkvect(6);
      putv(v,2,0);
      putv(v,3,0);
      return v
   end;

smacro procedure ibalp_clause!-setsat(clause,sat);
   % Set the variable turning a clause to true. [clause] is a clause;
   % [sat] is the variable turning to true
   putv(clause,4,sat . getv(clause,4));

smacro procedure ibalp_clause!-delallsat(clause);
   % Set the variable turning a clause to true. [clause] is a clause;
   % [sat] is the variable turning to true
   putv(clause,4,nil);

smacro procedure ibalp_clause!-setposlit(clause,var);
   % Add a variable to the list of positive literals of a
   % clause. [clause] is a clause; [var] is a variable.
   putv(clause,0,var . getv(clause,0));

smacro procedure ibalp_clause!-setneglit(clause,var);
   % Add a variable to the list of negative literals of a
   % clause. [clause] is a clause; [var] is a variable.
   putv(clause,1,var . getv(clause,1));

smacro procedure ibalp_clause!-setposlitabs(clause,var);
   % Add a variable to the list of positive literals of a
   % clause. [clause] is a clause; [var] is a variable.
   putv(clause,0,var);

smacro procedure ibalp_clause!-setneglitabs(clause,var);
   % Add a variable to the list of negative literals of a
   % clause. [clause] is a clause; [var] is a variable.
   putv(clause,1,var);

smacro procedure ibalp_clause!-setactpos(clause,actpos);
   % Set the number of positive literals that are currently
   % unset. [clause] is a clause; [actpos] is the number of currently
   % unset literals.
   putv(clause,2,actpos);

smacro procedure ibalp_clause!-setactneg(clause,actneg);
   % Set the number of negative literals that are currently
   % unset. [clause] is a clause; [actneg] is the number of currently
   % unset literals.
   putv(clause,3,actneg);

smacro procedure ibalp_clause!-setcount(clause,count);
   % Set the current count for new-added clauses. [clause] is a
   % clause; [count] is the count.
   putv(clause,5,count);

smacro procedure ibalp_clause!-setwl(clause,wl);
   % Add a watched literal for this clause. [clause] is a
   % clause; [wl] is a variable.
   putv(clause,6,wl . getv(clause,6));

smacro procedure ibalp_clause!-delallwl(clause);
   % Delete the watched literals for this clause. [clause] is a
   % clause.
   putv(clause,6,nil);

smacro procedure ibalp_clause!-delwl(clause,wl);
   % Delete a single watched literal from this clause. [clause] is a
   % clause; [wl] is a variable.
   putv(clause,6,delq(wl,getv(clause,6)));

smacro procedure ibalp_clause!-getposlit(clause);
   % Get a list of all positive literals of a clause. [clause] is a
   % clause. Returns the list of variables. [nil] if the clause has no
   % positive literals.
   getv(clause,0);

smacro procedure ibalp_clause!-getneglit(clause);
   % Get a list of all negative literals of a clause. [clause] is a
   % clause. Returns the list of variables. [nil] if the clause has no
   % negative literals.
   getv(clause,1);

smacro procedure ibalp_clause!-getactpos(clause);
   % Get the number of positive literals that are currently unset in a
   % clause. [clause] is a clause. Returns the number of literals
   getv(clause,2);

smacro procedure ibalp_clause!-getactneg(clause);
   % Get the number of negative literals that are currently unset in a
   % clause. [clause] is a clause. Returns the number of literals.
   getv(clause,3);

smacro procedure ibalp_clause!-getsat(clause);
   % Get the variable turning a clause to true. [clause] is a
   % clause. Returns the variable or [nil] if the clause is false.
   getv(clause,4);

smacro procedure ibalp_clause!-delsat(clause,sat);
   % Delete a variable turning a clause to true. [clause] is a clause;
   % [sat] is a variable.
   putv(clause,4,delq(sat,getv(clause,4)));

smacro procedure ibalp_clause!-getcount(clause);
   % Get the current count for new-added clauses. [clause] is a
   % clause. Return the count.
   getv(clause,5);

smacro procedure ibalp_clause!-getwl(clause);
   % Get the watched literals for this clause. [clause] is a
   % clause. Return the watched literal.
   getv(clause,6);

!#endif

procedure ibalp_printclause(clause);
   % Helper function to print a clause.
   begin scalar poslit,neglit,sat;
      for each v in ibalp_clause!-getposlit clause do
	 poslit := ibalp_var!-getid v . poslit;
      for each v in ibalp_clause!-getneglit clause do
      	 neglit := ibalp_var!-getid v . neglit;
      for each v in ibalp_clause!-getsat clause do
      	 sat := v . sat;
      ioto_tprin2t {"Clause ",poslit," ",neglit," ","SAT: ",sat}
   end;

procedure ibalp_printclauses(clausel);
   % Helper function to print all clauses.
   for each c in clausel do
      ibalp_printclause c;

procedure ibalp_printvaral(varal);
   % Helper function to print the list of variables.
   for each v in varal do
      ioto_tprin2t {ibalp_var!-getid cdr v, " ", ibalp_var!-getval cdr v, " ", ibalp_var!-getquant cdr v};

procedure ibalp_qsat!-dimacs(input);
   % The main entry point for solving a given .cnf or .qdimacs
   % file. [input] is the filename. Returns [true] or [false].
   begin scalar pair,clausel,varal;
      if null ibalp_qsatoptions!* then ibalp_qsat!-initoptions();
      pair := ibalp_qsat!-readdimacs2(input);
      clausel := cadr pair;
      varal := cddr pair;
      return if car pair then
	 car ibalp_qsat!-cdcl(clausel,varal,nil,t)
      else
      	 ibalp_start!-sat(clausel,varal)
   end;

procedure ibalp_qsat!-readdimacs(input);
   % Read a .cnf or .qdimacs file and conert it to Lisp
   % Prefix. [input] is the filename. Returns the corresponding
   % formula in Lisp Prefix.
   begin scalar pair,clausel,varal;
      pair := ibalp_qsat!-readdimacs2(input);
      clausel := cadr pair;
      varal := cddr pair;
      return ibalp_convcnf(clausel,varal,car pair)
   end;

procedure ibalp_qsat(f);
   % The main entry point for the QSAT function. [f] is a formula in
   % lisp prefix. Returns true or false in SAT and Q-SAT or a formula
   % in DNF in PQ-SAT.
   begin scalar pair,clausel,varal,readform,qsat,pqsat;
      if null ibalp_qsatoptions!* then ibalp_qsat!-initoptions();
      qsat := cl_bvarl f;
      pqsat := cl_fvarl f;
      readform := if qsat then cl_matrix (cl_pnf f) else f;
      if not (ibalp_iscnf readform) then <<
	 %readform := ibalp_get3cnf(readform);
      	 if !*rlverbose then
      	    ioto_tprin2t "Formula was not in CNF. Using QE";
	 return cl_qe(f,nil)
      >>;
      pair := ibalp_readform readform;
      clausel := car pair;
      varal := cdr pair;
      if null clausel then
	 return 'true;
      if ibalp_emptyclausep car clausel then
	 return 'false;
      if qsat and null pqsat then
       	 return ibalp_start!-qsat(clausel,varal,f)
      else if qsat and pqsat then
      	 return ibalp_start!-pqsat(clausel,varal,f,pqsat)
      else
      	 return ibalp_start!-sat(clausel,varal)
   end;

procedure ibalp_start!-sat(clausel,varal);
   % Start SAT solving. [clausel] is the list of clauses; [varal] is
   % the A-List of variables. Returns [true] if there is a satisfying
   % assignment, [nil] else.
   begin scalar resstart,firstval,inc,heur;
      if !*rlverbose then
      	 ioto_tprin2t {"Starting SAT Algorithm"};
      for each v in varal do
	 ibalp_var!-setmom(cdr v,ibalp_calcmom cdr v);
      resstart := ibalp_qsat!-getoption('res_start);
      firstval := ibalp_qsat!-getoption('first_val);
      inc := ibalp_qsat!-getoption('res_inc);
      heur := ibalp_qsat!-getoption('heuristic);
      return ibalp_cdcl(clausel,varal,resstart,firstval,1,inc,heur)
   end;

procedure ibalp_start!-qsat(clausel,varal,f);
      % Start Q-SAT solving. [clausel] is the list of clauses, [varal]
      % is the A-List of variables; [f] is the original
      % formula. Returns [true] if the formula is true, [nil] else.
      begin scalar varal,pair;
	 if !*rlverbose then
	    ioto_tprin2t {"Starting QSAT Algorithm"};
   	 pair := ibalp_readquantal(cl_pnf f,varal);
	 varal := cdr pair;
	 if eqn(car pair,1) and ibalp_var!-isex cdar varal then
	    return ibalp_start!-sat(clausel,varal)
	 else
   	    return car ibalp_qsat!-cdcl(clausel,varal,nil,t)
      end;

procedure ibalp_start!-pqsat(clausel,varal,f,pqsat);
      % Start parametric Q-SAT solving. [clausel] is the list of
      % clauses; [varal] is the A-List of variables; [f] is the
      % original formula; [pqsat] is the list of free
      % variables. Returns a condition to the free variables in DNF or
      % true or false.
   begin scalar pair,psat;
      if !*rlverbose then
	 ioto_tprin2t {"Starting PQSAT Algorithm with ", length pqsat, " free variables..."};
      pair := ibalp_readquantal(cl_pnf f,varal);
      varal := cdr pair;
      pair := ibalp_splitvars(pqsat,varal);
      varal := car pair;
      pqsat := cdr pair;
      psat := ibalp_psatp varal;
      if !*rlverbose and psat then
	 ioto_tprin2t {"**PSAT Problem"};
      donel!* := nil;
      numcdcl!* := 0;
      numlocs!* := 0;
      %if length pqsat / length varal > 2/3 then
      if nil then
	 return cl_qe(f,nil)
      else <<
      	 varal := cdr ibalp_readquantal(cl_pnf f,varal);
      	 pair := ibalp_qsat!-par(pqsat,clausel,varal,nil,psat);
	 if !*rlverbose then <<
	    ioto_tprin2t {"Runs of CDCL: ", numcdcl!*};
	    ioto_tprin2t {"Local Search Successes: ", numlocs!*};
	 >>;
      	 return ibalp_exres2(car pair,pqsat)
      >>
   end;

procedure ibalp_cdcl(clausel,varal,c,setval,rescount,inc,heur);
   % Conflict Driven Clause Learning Procedure. [clausel] is the list
   % of clauses; [varal] is the A-List of variables; [c] is the number
   % of conflict clauses for a restart; [setval] is the value a chosen
   % variable should be set to; [rescount] is a counter for restarts;
   % [inc] is the increase factor for restarts; [heur] is the used
   % heuristic. Returns [true] if there is a satisfying assignment,
   % [false] else.
   begin scalar res,fin,pair,ec,lv,upl; integer level,count;
      pair := ibalp_preprocess(clausel,varal);
      clausel := car pair;
      varal := cdr pair;
      if null clausel then return {'true};
      upl := ibalp_initwl clausel;
      while null fin do <<
	 ec := ibalp_cec clausel;
	 if null ec then <<
	    upl := ibalp_getupl clausel;
	    pair := ibalp_unitprop(upl,clausel,level);
	    ec := car pair;
	    lv := cdr pair;
	 >>;
	 if ec then <<
	    if eqn(level,0) then <<
	       fin := t;
	       res := {'false}
	    >> else <<
	       ibalp_recalcv varal;
	       count := count + 1;
	       ibalp_dimcount clausel;
	       pair := ibalp_analconf(ec,level,lv,clausel,varal);
	       level := car pair;
	       clausel := cdr pair;
	       pair := ibalp_dosimpl(clausel,varal);
	       clausel := car pair;
	       varal := cdr pair
	    >>
	 >> else <<
	    if ibalp_istotal varal or ibalp_csat clausel then <<
	       fin := t;
	       res := {'true}
	    >> else <<
	       pair := ibalp_getvar(varal,clausel,heur);
	       level := level + 1;
	       if heur = 'activity then setval := cdr pair;
	       ibalp_var!-set(car pair,setval,level,nil);
	       if count > c then <<
	     	  res := ibalp_restart(clausel,varal,c,
		     rescount,setval,inc,heur);
	       	  fin := t
	       >>
	    >>
	 >>
      >>;
      return res
   end;

procedure ibalp_preprocess(clausel,varal);
   % Pre-processing of the formula. [clausel] is the list of clauses;
   % [varal] is the A-List of variables. Retruns a pair of the new
   % clauses and the new variables.
   begin scalar pair; integer count;
      for each v in varal do <<
	 if eqn(ibalp_var!-getnumpos cdr v,0) then <<
	    count := count + 1;
	    pair := ibalp_simplify(cdr v,0,nil,clausel,varal);
	    clausel := car pair;
	    varal := cdr pair
	 >> else if eqn(ibalp_var!-getnumneg cdr v,0) then <<
	    count := count + 1;
	    pair := ibalp_simplify(cdr v,1,nil,clausel,varal);
	    clausel := car pair;
	    varal := cdr pair
      	 >>
      >>;
      if !*rlverbose then
      	 ioto_tprin2t {"deleted variables in pre-processing ",count};
      return (clausel . varal)
   end;

procedure ibalp_getvar(varal,clausel,heur);
   % Get a variable corresponding to a branching heuristic. [clausel]
   % is the list of clauses; [varal] is the A-List of variables;
   % [heur] is the branching heuristic. Returns a pair of variable and
   % value it should be assigned to.
   if heur = 'zmom then
      ibalp_getvar!-zmom(varal,clausel)
   else if heur = 'activity then
      ibalp_getmacvext varal
   else ibalp_getvar!-dlcs varal;

procedure ibalp_restart(clausel,varal,c,rescount,setval,inc,heur);
   % Restart the CDCL algorithm. [clausel] is the list of clauses;
   % [varal] is the A-List of variables; [c] is the number of conflict
   % clauses for a restart; [setval] is the value a chosen variable
   % should be set to; [rescount] is a counter for restarts; [inc] is
   % the increase factor for restarts; [heur] is the used
   % heuristic. Returns [true] if there is a satisfying assignment for
   % the formula, [nil] else.
   <<
      if !*rlverbose then
	 ioto_tprin2t {"restart ",rescount};
      ibalp_dav(varal,clausel);
      if c > ibalp_qsat!-getoption('clause_del) then
	 clausel := ibalp_killcount clausel;
      ibalp_cdcl(clausel,varal,c*inc,1-setval,rescount+1,inc,heur)
   >>;

procedure ibalp_analconf(ec,level,lv,clausel,varal);
   % Analyse conflict. [ec] is the empty clause; [level] is the
   % current level; [lv] is the last assigned variable; [clausel] is
   % the list of clauses; [varal] is the A-List of variables. Returns
   % a pair of the new level and the new list of clauses.
   begin scalar pair,newlev,cc,p,val;
      cc := ibalp_calccc!-fuip(ec,level,lv);
      pair := ibalp_calccvar(cc,level);
      p := car pair;
      newlev := cdr pair;
      val := ibalp_var!-getval p;
      clausel := cc . clausel;
      ibalp_tvb(varal,newlev);
      ibalp_renewwl clausel;
      ibalp_var!-set(p,1-val,newlev,nil);
      return (newlev . clausel)
   end;

procedure ibalp_dosimpl(clausel,varal);
   % Perform Simplifications. [clausel] is the list of clauses;
   % [varal] is the A-List of variables. Return a pair of the new
   % clauses and the new variables.
   begin scalar h,pair;
      while h := ibalp_hassimple clausel do <<
	 pair := ibalp_simplify(nil,nil,h,clausel,varal);
	 clausel := car pair;
	 varal := cdr pair
      >>;
      return (clausel . varal)
   end;

procedure ibalp_simplify(dvar,dval,clause,clausel,varal);
   % Simplification. Delete needles literals. [dvar] is a variable;
   % [dval] its value; [clause] is a clause; [clausel] is the list of
   % clauses; [varal] is the A-List of variables. Returns a pair of
   % the new clauses and the new variables.
   begin scalar var,val;
      if null dvar then <<
      	 if ibalp_lenisone ibalp_clause!-getposlit clause then <<
	    var := car ibalp_clause!-getposlit clause;
	    val := 1
      	 >>
      	 else <<
	    var := car ibalp_clause!-getneglit clause;
	    val := 0
      	 >>;
      	 if ibalp_var!-getval var then
	    ibalp_var!-unset(var,ibalp_var!-getval var);
	 ibalp_var!-set(var,val,0,nil);
      >> else <<
	 var := dvar;
	 val := dval
      >>;
      if eqn(val,1) then <<
	 for each clause in ibalp_var!-getposocc var do
	    clausel := ibalp_delclause(clause,clausel);
	 for each clause in ibalp_var!-getnegocc var do
	    ibalp_dellit(var,clause,nil);
      >> else <<
	 for each clause in ibalp_var!-getnegocc var do
	    clausel := ibalp_delclause(clause,clausel);
	 for each clause in ibalp_var!-getposocc var do
	    ibalp_dellit(var,clause,t);
      >>;
      varal := delq(atsoc(ibalp_var!-getid var,varal),varal);
      return (clausel . varal)
   end;

procedure ibalp_lenisone(l);
   l and null cdr l;

procedure ibalp_commonlenisone(l1,l2);
   % l1 and l2 are lists, which are not both empty.
   null l1 and ibalp_lenisone l2 or null l2 and ibalp_lenisone l1;

procedure ibalp_hassimple(clausel);
   % Check if a clause list has some literals to simplify. [clausel]
   % is the list of clauses. Returns a clause to simplfy or [nil].
   begin scalar ret,tl;
      tl := clausel;
      while tl and null ret do <<
	 if ibalp_commonlenisone(
	    ibalp_clause!-getposlit car tl,ibalp_clause!-getneglit car tl)
	 then
	    ret := car tl;
	 tl := cdr tl
      >>;
      return ret
   end;

procedure ibalp_getupl(clausel);
   % Get initial set for Unit Propagation. [clausel] is the list of
   % clauses. Returns a list of unit clauses.
   begin scalar upl;
      for each c in clausel do
	 if null ibalp_clause!-getsat c and
	 eqn(ibalp_clause!-getactpos c + ibalp_clause!-getactneg c,1) then
	    upl := c . upl;
      return upl
   end;

procedure ibalp_unitprop(clist,clausel,level);
   % Unitpropagation. [clist] is a list of clauses with unit
   % variables; [clausel] ist the list of clauses; [level] is the
   % level the reduction is made; [setvar] is the last variable
   % set. Returns a Pair. The first entry is an empty clause if one is
   % derived the second the variable set at last.
   begin scalar tl,clause,actpos,actneg,var,ec,upl,w;
      w := tl := clist;
      while tl and null ec do <<
	 clause := car tl;
	 if null ibalp_clause!-getsat clause then <<
	    actpos := ibalp_clause!-getactpos clause;
	    actneg := ibalp_clause!-getactneg clause;
	    % Since clause is unit, we know that actpos is 1 and
	    % actneg is 0 or vice versa.
	    if actpos #= 1 then <<
	       var := car ibalp_clause!-getwl clause;
	       if null ibalp_var!-getval var then <<
	       	  upl := ibalp_var!-set(var,1,level,clause);
		  nconc(w,upl);
		  w := upl or w
	       >>
	    >> else <<
	       var := car ibalp_clause!-getwl clause;
	       if null ibalp_var!-getval var then <<
	       	  upl := ibalp_var!-set(var,0,level,clause);
		  nconc(w,upl);
		  w := upl or w
	       >>
	    >>
	 >>;
	 tl := cdr tl;
	 ec := ibalp_cec clausel
      >>;
      return (ec . var)
   end;

procedure ibalp_initwl(clausel);
   % Initialize the watched literals. [clausel] is the list of
   % clauses. Returns a list of unit clauses.
   begin scalar count,upl,tl;
      for each c in clausel do <<
	 count := 0;
	 tl := ibalp_clause!-getposlit c;
	 while not eqn(count,2) and tl do <<
	    ibalp_clause!-setwl(c,car tl);
	    ibalp_var!-setwc(car tl,c);
	    count := count + 1;
	    tl := cdr tl
	 >>;
	 tl := ibalp_clause!-getneglit c;
	 while not eqn(count,2) and tl do <<
	    ibalp_clause!-setwl(c,car tl);
	    ibalp_var!-setwc(car tl,c);
	    count := count + 1;
	    tl := cdr tl
	 >>;
	 if count < 2 then upl := c . upl
      >>;
      return upl
   end;

procedure ibalp_renewwl(clausel);
   % Renew watched literals. [clausel] is the list of clauses;
   begin scalar wl;
      for each c in clausel do <<
	 if null ibalp_clause!-getsat c then <<
      	    if eqn(length ibalp_clause!-getwl c,1) and
	       length ibalp_clause!-getposlit c +
	       length ibalp_clause!-getneglit c > 1 then <<
	    	  wl := ibalp_getnewwl c;
	    	  if wl then <<
	       	     ibalp_clause!-setwl(c,wl);
	       	     ibalp_var!-setwc(wl,c)
	    	  >>;
	       >> else
	 	  if null ibalp_clause!-getwl c  and
	       length ibalp_clause!-getposlit c +
	       length ibalp_clause!-getneglit c > 1 then <<
		  wl := ibalp_getnewwl c;
	       	  if wl then <<
	       	     ibalp_clause!-setwl(c,wl);
	       	     ibalp_var!-setwc(wl,c)
	       	  >>;
	       	  wl := ibalp_getnewwl c;
	       	  if wl then <<
	       	     ibalp_clause!-setwl(c,wl);
	       	     ibalp_var!-setwc(wl,c)
	       	  >>
	       >>
	 >>
      >>
   end;

procedure ibalp_resolve(newclause,clause1,clause2,cv);
   % Resolve two clauses to one. [newclause] is the new clause;
   % [clause1] is the first clause to resolve; [clause2] is the second
   % clause to resolve; [cv] is the conflict variable within the two
   % clauses.
   <<
      for each v in ibalp_clause!-getposlit clause1 do
      	 if null (v eq cv) and
	 null memq(v,ibalp_clause!-getposlit newclause) then <<
	    ibalp_clause!-setposlit(newclause,v);
	    ibalp_var!-setposocc(v,newclause);
	    ibalp_var!-setnumpos(v,ibalp_var!-getnumpos v + 1)
	 >>;
      for each v in ibalp_clause!-getposlit clause2 do
      	 if null (v eq cv) and
	 null memq(v,ibalp_clause!-getposlit newclause) then <<
	    ibalp_clause!-setposlit(newclause,v);
	    ibalp_var!-setposocc(v,newclause);
	    ibalp_var!-setnumpos(v,ibalp_var!-getnumpos v + 1)
	 >>;
      for each v in ibalp_clause!-getneglit clause1 do
	 if null (v eq cv) and
	 null memq(v,ibalp_clause!-getneglit newclause) then <<
	    ibalp_clause!-setneglit(newclause,v);
	    ibalp_var!-setnegocc(v,newclause);
	    ibalp_var!-setnumneg(v,ibalp_var!-getnumneg v + 1)
	 >>;
      for each v in ibalp_clause!-getneglit clause2 do
	 if null (v eq cv) and
	 null memq(v,ibalp_clause!-getneglit newclause) then <<
	    ibalp_clause!-setneglit(newclause,v);
	    ibalp_var!-setnegocc(v,newclause);
	    ibalp_var!-setnumneg(v,ibalp_var!-getnumneg v + 1)
	 >>;
   >>;

procedure ibalp_dav(varal,clausel);
   % Delete all assignments to variables. [varal] is the A-List of
   % variables; [clausel] is the list of clauses.
   <<
      for each v in varal do <<
	 if ibalp_var!-getval cdr v then <<
	    ibalp_var!-unset(cdr v,ibalp_var!-getval cdr v);
	    ibalp_var!-setflip(cdr v,nil)
	 >>
      >>;
      for each v in varal do <<
	 ibalp_var!-delallwc cdr v
      >>;
      for each c in clausel do
	 ibalp_clause!-delallwl c
   >>;

procedure ibalp_calccc!-fuip(ec,level,lv);
   % Calculate conflict clause after Strategy: First UIP. [ec] is the
   % empty clause to start the calculation with; [level] is the
   % conflict level; [lv] is the last variable set. Returns the new
   % learnt clause.
   begin scalar newclause,tv,reas;
      newclause := ibalp_clause!-new();
      ibalp_resolve(newclause,ec,ibalp_var!-getreas lv,lv);
      while tv := ibalp_countgetlev(newclause,level) do <<
	 if eqn(ibalp_var!-getval tv,0) then
	    ibalp_dellit(tv,newclause,t)
	 else
	    ibalp_dellit(tv,newclause,nil);
	 reas := ibalp_var!-getreas tv;
	 if ibalp_clause!-getcount reas then
	    ibalp_clause!-setcount(reas,ibalp_clause!-getcount reas + 1);
	 ibalp_resolve(newclause,newclause,reas,tv);
      >>;
      for each v in ibalp_clause!-getposlit newclause do
	 ibalp_var!-setposcc(v,ibalp_var!-getposcc v + 1);
      for each v in ibalp_clause!-getneglit newclause do
	 ibalp_var!-setnegcc(v,ibalp_var!-getnegcc v + 1);
      ibalp_clause!-setcount(newclause,1);
      return newclause
   end;

procedure ibalp_countgetlev(clause,level);
   % Count variables at a certain level and return a variable at this
   % level if there are more than one. [clause] is a clause; [level]
   % is the level. Returns a
   % variable or [nil]
   begin scalar temp,tv,ret;
      tv := ibalp_clause!-getposlit clause;
      while tv and null ret do <<
	 temp := car tv;
	 if ibalp_var!-getlev temp = level and
	    ibalp_var!-getreas temp then
	       ret := temp;
	 tv := cdr tv;
      >>;
      tv := ibalp_clause!-getneglit clause;
      while tv and null ret do <<
	 temp := car tv;
	 if ibalp_var!-getlev temp = level and
 	    ibalp_var!-getreas temp then
	       ret := temp;
	 tv := cdr tv;
      >>;
      return ret
   end;

procedure ibalp_dellit(lit,clause,posneg);
   % Delete a literal from a clause. [lit] is the literal to delete;
   % [clause] is the clause; [posneg] is [t] if it is a true literal,
   % [nil] else;
   if posneg then <<
      ibalp_var!-setposoccabs(lit,delq(clause,ibalp_var!-getposocc lit));
      ibalp_clause!-setposlitabs(
	 clause,delq(lit,ibalp_clause!-getposlit clause))
   >> else <<
      ibalp_var!-setnegoccabs(lit,delq(clause,ibalp_var!-getnegocc lit));
      ibalp_clause!-setneglitabs(
	 clause,delq(lit,ibalp_clause!-getneglit clause))
   >>;

procedure ibalp_dimcount(clausel);
   % Decrease the counter of newly added clauses. [clausel] is the
   % list of clauses.
   begin scalar doit,tc,c;
      doit := t;
      tc := clausel;
      while doit do <<
	 c := car tc;
	 if null ibalp_clause!-getcount c then
	    doit := nil
	 else
	    ibalp_clause!-setcount(c,ibalp_clause!-getcount c - 0.05);
	 tc := cdr tc
      >>
   end;

procedure ibalp_killcount(clausel);
   % Delete clauses with a count < 1. [clausel] is the list of
   % clauses. Return the new list of clauses.
   begin scalar doit,tc,c;
      doit := t;
      tc := clausel;
      while doit do <<
	 c := car tc;
	 if null ibalp_clause!-getcount c then doit := nil else <<
	    tc := cdr tc;
	    if ibalp_clause!-getcount c < 1 then
	       clausel := ibalp_delclause(c,clausel);
	 >>
      >>;
      return clausel
   end;

procedure ibalp_delclause(c,clausel);
   % Delete a clause. [c] is the clause to delete; [clausel] is the
   % list of clauses.
   <<
      for each v in ibalp_clause!-getposlit c do <<
	 ibalp_var!-setposoccabs(v,delq(c,ibalp_var!-getposocc v));
	 if ibalp_clause!-getcount c then
	    ibalp_var!-setposcc(v,ibalp_var!-getposcc v - 1);
	 if null ibalp_clause!-getsat c then
	    ibalp_var!-setnumpos(v,ibalp_var!-getnumpos v - 1);
	 ibalp_var!-setmom(v,ibalp_calcmom v)
      >>;
      for each v in ibalp_clause!-getneglit c do <<
	 ibalp_var!-setnegoccabs(v,delq(c,ibalp_var!-getnegocc v));
	 if ibalp_clause!-getcount c then
	    ibalp_var!-setnegcc(v,ibalp_var!-getnegcc v - 1);
	 if null ibalp_clause!-getsat c then
	    ibalp_var!-setnumneg(v,ibalp_var!-getnumneg v - 1);
	 ibalp_var!-setmom(v,ibalp_calcmom v)
      >>;
      for each v in ibalp_clause!-getwl c do <<
	 ibalp_var!-delwc(v,c);
      >>;
      clausel := delq(c,clausel);
      clausel
   >>;

procedure ibalp_getmacvext(varal);
   % Get most active variable. [varal] is the list of
   % variables. Returns a pair of the most active variable and its
   % value.
   begin scalar tv,tm,val;
      tv := ibalp_cv varal;
      if ibalp_var!-getposcc tv > ibalp_var!-getnegcc tv then <<
	 tm := ibalp_var!-getposcc tv;
 	 val := 1
      >> else <<
	 tm := ibalp_var!-getnegcc tv;
	 val := 0
      >>;
      for each v in varal do
	 if null ibalp_var!-getval cdr v then <<
	    if ibalp_var!-getposcc cdr v > tm then <<
	       tv := cdr v;
	       val := 1;
	       tm := ibalp_var!-getposcc tv
	    >>;
	    if ibalp_var!-getnegcc cdr v > tm then <<
	       tv := cdr v;
	       val := 0;
	       tm := ibalp_var!-getnegcc tv
	    >>
      	 >>;
      return (tv . val)
   end;

procedure ibalp_recalcv(varal);
   % Recalc variables activity value. [varal] is the A-List of
   % variables.
   for each v in varal do <<
      ibalp_var!-setposcc(cdr v,ibalp_var!-getposcc cdr v - 0.05);
      ibalp_var!-setnegcc(cdr v,ibalp_var!-getnegcc cdr v - 0.05)
   >>;

procedure ibalp_calccvar(cc,level);
   % Calclate the only conflict variable set at the conflict
   % level. [cc] is the conflict clause; [varal] is the A-List of
   % variables; [level] is the conflict level. Returns a Pair. The
   % first entry is the conflict variable and the second is the
   % highest level of all other variables.
   begin scalar v,rv; integer lev;
      for each v in ibalp_clause!-getposlit cc do <<
	 if eqn(ibalp_var!-getlev v,level) then
	    rv := v
	 else
	    if ibalp_var!-getlev v > lev then lev := ibalp_var!-getLev v
      >>;
      for each v in ibalp_clause!-getneglit cc do <<
	 if eqn(ibalp_var!-getlev v,level) then
	    rv := v
	 else
	    if ibalp_var!-getlev v > lev then lev := ibalp_var!-getLev v
      >>;
      return (rv . lev)
   end;

procedure ibalp_tvb(varal,level);
   % Take back all variable assignments down to a certain
   % level. [varal] is the A-List of variables; [level] is the level.
   for each v in varal do
      if ibalp_var!-getlev cdr v >= level then
	 ibalp_var!-unset(cdr v,ibalp_var!-getval cdr v);

procedure ibalp_istotal(varal);
   % Checks if an assignment is total. [varal] is a A-List of
   % variables. Returns [t] if the assigenment is total [nil] else.
   null varal or (ibalp_var!-getval cdar varal and
      ibalp_istotal cdr varal);

procedure ibalp_getvar!-zmom(varal,clausel);
   % Get a variable following the ZMOM (maximum occurrences in minimal
   % clauses ) strategy. [varal] is a A-List of variables. Returns a
   % Pair. The first entry is the chosen variable, the second entry
   % is the value the variable should be set to.
   begin scalar minc,tv,tmax,h,val;
      minc := ibalp_minclnr clausel;
      tmax := -1;
      for each v in varal do <<
	 if null ibalp_var!-getval cdr v and
	    (h := ibalp_var!-getmom cdr v) > tmax then
	       if ibalp_isinminclause(cdr v,minc) then <<
	       	  tv := cdr v;
	       	  tmax := h
	       >>
      >>;
      val := if ibalp_var!-getposcc tv > ibalp_var!-getnegcc tv then 1 else 0;
      return (tv . val)
   end;

procedure ibalp_isinminclause(var,minc);
   % Check if a variable is in a clause of minmal size. [var] is a
   % variable; [minc] is the size of a minimal clause. Returns [t] if
   % the variable is in a clause of minimal size, [nil] else.
   begin scalar tv,ret;
      tv := ibalp_var!-getposocc var;
      while tv and null ret do <<
	 if null ibalp_clause!-getsat car tv and
	    eqn(ibalp_clause!-getactneg car tv +
	    ibalp_clause!-getactpos car tv,minc) then
	       ret := t;
	 tv := cdr tv;
      >>;
      tv := ibalp_var!-getnegocc var;
      while tv and null ret do <<
	 if null ibalp_clause!-getsat car tv and
	    eqn(ibalp_clause!-getactneg car tv +
	    ibalp_clause!-getactpos car tv,minc) then
	       ret := t;
	 tv := cdr tv;
      >>;
      return ret
   end;

procedure ibalp_getvar!-dlcs(varal);
   % Get a variable following the DLCS (dynamic largest combined sum)
   % strategy. [varal] is a A-List of variables. Returns a Pair. The
   % first entry is the chosen variable, the second entry is the value
   % the variable should be set to.
   begin scalar tv,max,val;
      tv := ibalp_cv varal;
      max := ibalp_var!-getnumneg tv + ibalp_var!-getnumpos tv;
      for each var in varal do
	 if null ibalp_var!-getval cdr var then
	    if ibalp_var!-getnumneg cdr var +
	 ibalp_var!-getnumpos cdr var > max then <<
	    tv := cdr var;
	    max := ibalp_var!-getnumneg cdr var +
	       ibalp_var!-getnumpos cdr var
	 >>;
      val := if ibalp_var!-getnumpos tv > ibalp_var!-getnumneg tv then
	 1
      else
 	 0;
      return (tv . val)
   end;

procedure ibalp_minclnr(clausel);
   % Get the size of a minimal clause. [clausel] is the list of
   % clauses. Returns the size of a minimum clause.
   begin scalar min;
      %hack
      min := 100000;
      for each c in clausel do
	 if null ibalp_clause!-getsat c then
	    if ibalp_clause!-getactpos c +
	 ibalp_clause!-getactneg c < min then
	    min := ibalp_clause!-getactpos c +
	 ibalp_clause!-getactneg c;
      return min
   end;

procedure ibalp_calcmom(var);
   % Calculate the zmom value of a variable. [var] is a
   % variable. Returns the mom value.
   (ibalp_var!-getnumpos var + ibalp_var!-getnumneg var)*32 +
      (ibalp_var!-getnumpos var * ibalp_var!-getnumneg var);

procedure ibalp_cec(clausel);
   % Check empty clauses. [clausel] is the list of clauses. Returns
   % the first empty clause if there is one (a clause which is false
   % but has also no unset variables), else [nil].
   if null clausel then
      nil
   else if ibalp_emptyclausep car clausel then
      car clausel
   else
      ibalp_cec cdr clausel;

procedure ibalp_emptyclausep(clause);
   null ibalp_clause!-getsat clause and
      eqn(ibalp_clause!-getactpos clause,0) and
      eqn(ibalp_clause!-getactneg clause,0);

procedure ibalp_csat(clausel);
   % Check SAT. [clausel] is the list of clauses. Returns [t] if all
   % the clauses are true, else [nil].
   null clausel or (ibalp_clause!-getsat car clausel
      and ibalp_csat cdr clausel);

procedure ibalp_cv(varal);
   % Choose a variable. [varal] is the A-List of variables. Returns a
   % unset variable.
   if null ibalp_var!-getval cdar varal then
      cdar varal
   else
      ibalp_cv cdr varal;

procedure ibalp_var!-set(var,val,level,reas);
   % Set a variable. [var] is the variable; [val] is value to be set;
   % [level] is the level the variable is set; [reas] is the reason
   % why the variable is set. Sets the given variable from [nil] to
   % [val] and updates all needed data structures. Returns a pair of
   % new unit clauses.
   begin scalar id,sc,upl;
      ibalp_var!-setval(var,val);
      ibalp_var!-setlev(var,level);
      ibalp_var!-setreas(var,reas);
      id := ibalp_var!-getid var;
      sc := if eqn(val,0) then
	 ibalp_var!-getnegocc var
      else
	 ibalp_var!-getposocc var;
      ibalp_var!-satlist(sc,id);
      sc := if eqn(val,1) then
	 ibalp_var!-getnegocc var
      else
	 ibalp_var!-getposocc var;
      ibalp_var!-unsatlist(sc,val);
      upl := ibalp_var!-wclist var;
      ibalp_var!-setmom(var,ibalp_calcmom var);
      return upl
   end;

procedure ibalp_var!-satlist(sc,id);
   % Perform changes on the list of satisfied clauses. [sc] is the
   % list of satisfied clauses; [id] is the identifier of the
   % variable.
   for each clause in sc do <<
      if null ibalp_clause!-getsat clause then <<
	 for each v in ibalp_clause!-getposlit clause do <<
	    ibalp_var!-setnumpos(v,ibalp_var!-getnumpos v - 1);
	    ibalp_var!-setmom(v,ibalp_calcmom v)
	 >>;
	 for each v in ibalp_clause!-getneglit clause do <<
	    ibalp_var!-setnumneg(v, ibalp_var!-getnumneg v - 1);
	    ibalp_var!-setmom(v,ibalp_calcmom v)
	 >>;
	 for each v in ibalp_clause!-getwl clause do <<
	    ibalp_var!-delwc(v,clause)
	 >>;
	 ibalp_clause!-delallwl clause;
      >>;
      ibalp_clause!-setsat(clause,id)
   >>;

procedure ibalp_var!-unsatlist(sc,val);
   % Perform changes on the list of unsatisfied clauses. [sc] is the
   % list of unsatisfied clauses; [val] is the value of the
   % variable.
   for each clause in sc do
      if eqn(val,1) then
	 ibalp_clause!-setactneg(clause,
	    ibalp_clause!-getactneg clause - 1)
      else
	 ibalp_clause!-setactpos(clause,
	    ibalp_clause!-getactpos clause - 1);

procedure ibalp_var!-wclist(var);
   % Perform changes on the list of watched clauses. [var] is the
   % variable. Returns the list of unit clauses.
   begin scalar newwl,upl;
      for each c in ibalp_var!-getwc var do
      	 if null ibalp_clause!-getsat c then <<
	    ibalp_clause!-delwl(c,var);
	    ibalp_var!-delwc(var,c);
	    newwl := ibalp_getnewwl c;
	    if null newwl then
	       upl := c . upl
	    else <<
	       ibalp_clause!-setwl(c,newwl);
	       ibalp_var!-setwc(newwl,c)
	    >>
      	 >>;
      return upl
   end;

procedure ibalp_var!-setq(var,val,level,reas);
   % Set a variable (QSAT). [var] is the variable; [val] is value to
   % be set; [varal] is the list of variables; [level] is the level
   % the variable is set; [reas] is the reason why the variable is
   % set. Sets the given variable from [nil] to [val] and updates all
   % needed data structures. Returns a pair of new unit clauses and
   % new conflict clauses.
   begin scalar clause,id,sc,upl,h,ec;
      ibalp_var!-setval(var,val);
      ibalp_var!-setlev(var,level);
      ibalp_var!-setreas(var,reas);
      id := ibalp_var!-getid var;
      sc := if eqn(val,0) then
	 ibalp_var!-getnegocc var
      else
	 ibalp_var!-getposocc var;
      ibalp_var!-satlistq(sc,id);
      sc := if eqn(val,1) then
	 ibalp_var!-getnegocc var
      else
	 ibalp_var!-getposocc var;
      for each clause in sc do <<
	 if eqn(val,1) then
	    ibalp_clause!-setactneg(clause,
	       ibalp_clause!-getactneg clause - 1)
	 else
	    ibalp_clause!-setactpos(clause,
	       ibalp_clause!-getactpos clause - 1);
	 if h := ibalp_qsat!-isunit clause then upl := (h . clause) . upl;
	 if ibalp_qsat!-isec clause then ec := clause
      >>;
      ibalp_var!-setmom(var,ibalp_calcmom var);
      return (upl . ec)
   end;

procedure ibalp_var!-satlistq(sc,id);
   % Perform changes on the list of satisfied clauses. [sc] is the
   % list of satisfied clauses; [id] is the identifier of the
   % variable.
   for each clause in sc do <<
      if null ibalp_clause!-getsat clause then <<
	 for each v in ibalp_clause!-getposlit clause do <<
	    ibalp_var!-setnumpos(v,ibalp_var!-getnumpos v - 1);
	    ibalp_var!-setmom(v,ibalp_calcmom v)
	 >>;
	 for each v in ibalp_clause!-getneglit clause do <<
	    ibalp_var!-setnumneg(v, ibalp_var!-getnumneg v - 1);
	    ibalp_var!-setmom(v,ibalp_calcmom v)
	 >>;
      >>;
      ibalp_clause!-setsat(clause,id)
   >>;

procedure ibalp_var!-unset(var,val);
   % Unset a variable. [var] is the variable; [val] is value to be
   % unset. Sets the given variable from [val] to [nil] and updates
   % all needed data structures.
   begin scalar clause,id,sc;
      ibalp_var!-setval(var,nil);
      ibalp_var!-setlev(var,-1);
      ibalp_var!-setreas(var,nil);
      id := ibalp_var!-getid var;
      sc := if eqn(val,1) then
	 ibalp_var!-getnegocc var
      else
	 ibalp_var!-getposocc var;
      for each clause in sc do <<
	 if eqn(val,1) then
	    ibalp_clause!-setactneg(clause,
	       ibalp_clause!-getactneg clause +1)
	 else
	    ibalp_clause!-setactpos(clause,
	       ibalp_clause!-getactpos clause +1)
      >>;
      sc := if eqn(val,0) then
	 ibalp_var!-getnegocc var
      else
	 ibalp_var!-getposocc var;
      ibalp_unvar!-unsatlist(sc,id);
      ibalp_var!-setmom(var,ibalp_calcmom var)
   end;

procedure ibalp_unvar!-unsatlist(sc,id);
   % Perform changes on the list of unsatisfied clauses. [sc] is the
   % list of unsatisfied clauses; [id] is the identifier of the
   % variable.
   begin scalar newwl;
      for each clause in sc do <<
	 ibalp_clause!-delsat(clause,id);
	 if null ibalp_clause!-getsat clause then <<
      	    for each v in ibalp_clause!-getposlit clause do <<
	       ibalp_var!-setnumpos(v, ibalp_var!-getnumpos v + 1);
	       ibalp_var!-setmom(v,ibalp_calcmom v)
	    >>;
	    for each v in ibalp_clause!-getneglit clause do <<
	       ibalp_var!-setnumneg(v, ibalp_var!-getnumneg v + 1);
	       ibalp_var!-setmom(v,ibalp_calcmom v)
	    >>;
	    for each v in ibalp_clause!-getwl clause do <<
	       ibalp_var!-delwc(v,clause)
	    >>;
	    ibalp_clause!-delallwl clause;
	    newwl := ibalp_getnewwl clause;
	    ibalp_clause!-setwl(clause,newwl);
	    ibalp_var!-setwc(newwl,clause);
	    newwl := ibalp_getnewwl clause;
	    if newwl then <<
	       ibalp_clause!-setwl(clause,newwl);
	       ibalp_var!-setwc(newwl,clause)
	    >>
	 >>
      >>;
   end;

procedure ibalp_var!-unsetq(var,val);
   % Unset a variable (QSAT). [var] is the variable; [val] is value to
   % be unset; Sets the given variable from [val] to [nil] and updates
   % all needed data structures.
   begin scalar clause,v,id,sc;
      ibalp_var!-setval(var,nil);
      ibalp_var!-setlev(var,-1);
      ibalp_var!-setreas(var,nil);
      id := ibalp_var!-getid var;
      sc := if eqn(val,1) then
	 ibalp_var!-getnegocc var
      else
	 ibalp_var!-getposocc var;
      ibalp_unvar!-unsatlistq(sc,val);
      sc := if eqn(val,0) then
	 ibalp_var!-getnegocc var
      else
	 ibalp_var!-getposocc var;
      for each clause in sc do <<
	 ibalp_clause!-delsat(clause,id);
	 if null ibalp_clause!-getsat clause then <<
      	    for each v in ibalp_clause!-getposlit clause do <<
	       ibalp_var!-setnumpos(v, ibalp_var!-getnumpos v + 1);
	       ibalp_var!-setmom(v,ibalp_calcmom v)
	    >>;
	    for each v in ibalp_clause!-getneglit(clause) do <<
	       ibalp_var!-setnumneg(v, ibalp_var!-getnumneg v + 1);
	       ibalp_var!-setmom(v,ibalp_calcmom v)
	    >>;
	 >>
      >>;
      ibalp_var!-setmom(var,ibalp_calcmom var)
   end;

procedure ibalp_unvar!-unsatlistq(sc,val);
   % Perform changes on the list of unsatisfied clauses. [sc] is the
   % list of unsatisfied clauses; [val] is the value of the
   % variable.
   for each clause in sc do <<
      if eqn(val,1) then
	 ibalp_clause!-setactneg(clause,
	    ibalp_clause!-getactneg clause +1)
      else
	 ibalp_clause!-setactpos(clause,
	    ibalp_clause!-getactpos clause +1)
   >>;

procedure ibalp_getnewwl(clause);
   % Get a new watched literal for a clause. [clause] is a clause;
   % Returns a new watched literal or [nil].
   begin scalar tl,wl;
      tl := ibalp_clause!-getposlit clause;
      while tl and null wl do <<
	 if null ibalp_var!-getval car tl and
	    null memq(car tl,ibalp_clause!-getwl clause)
	 then
	    wl := car tl;
	 tl := cdr tl
      >>;
      tl := ibalp_clause!-getneglit clause;
      while tl and null wl do <<
	 if null ibalp_var!-getval car tl and
	    null memq(car tl,ibalp_clause!-getwl clause)
	 then
	    wl := car tl;
	 tl := cdr tl
      >>;
      return wl
   end;

procedure ibalp_iscnf(f);
   ibalp_clausep f or (rl_op f eq 'and and ibalp_clauselp rl_argn f);

procedure ibalp_clauselp(l);
   null l or (ibalp_clausep car l and ibalp_clauselp cdr l);

procedure ibalp_clausep(s);
   ibalp_litp s or (rl_op s eq 'or and ibalp_litlp rl_argn s);

procedure ibalp_litlp(l);
   null l or (ibalp_litp car l and ibalp_litlp cdr l);

procedure ibalp_litp(s);
   ibalp_atomp s or (rl_op s eq 'not and ibalp_atomp rl_arg1 s);

procedure ibalp_atomp(s);
   % We consider true and false to be atomic formulas at this point.
   rl_tvalp s or (rl_op s eq 'equal and idp ibalp_arg2l s and numberp ibalp_arg2r s);

procedure ibalp_readform(f);
   % Read a formula in cnf. [f] is a formula in cnf in lisp
   % prefix. Returns a pair: [clausel] is the list of clauses; [varal]
   % is the A-List of variables.
   begin scalar pair,clausel,varal,clause,argn,x,c; integer count;
      f := cl_mkstrict(f,'and);
      argn := rl_argn f;
      c := t; while c and argn do <<
	 x := car argn;
	 argn := cdr argn;
	 pair := ibalp_readclause(x,varal);
	 clause := car pair;
	 varal := cdr pair;
	 if clause neq 'true then <<
	    if ibalp_emptyclausep clause then
	       c := nil
	    else
	       (if ibalp_clmember(clause,clausel) or ibalp_redclause clause then <<
	       	  ibalp_undoclause clause;
	       	  count := count + 1
	       >> else
	       	  clausel := car pair . clausel)
	 >>
      >>;
      if null c then <<
      if !*rlverbose then
      	 ioto_tprin2t {"Detected empty clause"};
	 return {clause} . nil
      >>;
      if null clausel then <<
      	 if !*rlverbose then
      	    ioto_tprin2t {"Tautology detected"};
	 return nil . nil
      >>;
      if !*rlverbose then
      	 ioto_tprin2t {"Deleted redundant clauses: ",count};
      return (clausel . varal)
   end;

procedure ibalp_clmember(x,l);
   l and (ibalp_cequal(x,car l) or ibalp_clmember(x,cdr l));

procedure ibalp_cequal(c1,c2);
   begin scalar poslitl1,neglitl1,poslitl2,neglitl2;
      poslitl1 := for each v in ibalp_clause!-getposlit c1 collect
	 ibalp_var!-getid v;
      poslitl2 := for each v in ibalp_clause!-getposlit c2 collect
	 ibalp_var!-getid v;
      if not lto_setequalq(poslitl1,poslitl2) then
	 return nil;
      neglitl1 := for each v in ibalp_clause!-getneglit c1 collect
	 ibalp_var!-getid v;
      neglitl2 := for each v in ibalp_clause!-getneglit c2 collect
	 ibalp_var!-getid v;
      return lto_setequalq(neglitl1,neglitl2)
   end;

procedure ibalp_undoclause(clause);
   % Undo a clause if it redundant. [clause] is a clause.
   <<
      for each v in ibalp_clause!-getposlit clause do <<
	 ibalp_var!-setposoccabs(v,delq(clause,ibalp_var!-getposocc v));
	 ibalp_var!-setnumpos(v,ibalp_var!-getnumpos v - 1);
	 ibalp_var!-setposcc(v,ibalp_var!-getposcc v - 1)
      >>;
      for each v in ibalp_clause!-getneglit clause do <<
	 ibalp_var!-setnegoccabs(v,delq(clause,ibalp_var!-getnegocc v));
	 ibalp_var!-setnumneg(v,ibalp_var!-getnumneg v - 1);
	 ibalp_var!-setnegcc(v,ibalp_var!-getnegcc v - 1)
      >>
   >>;

procedure ibalp_redclause(clause);
   % Checks if a new clause is redundant. [clause] is a
   % clause. Returns [t] if the clause is redundant, [nil] else.
   begin scalar tv,ret;
      tv := ibalp_clause!-getposlit clause;
     while tv and null ret do <<
	 if ibalp_vmember(car tv,ibalp_clause!-getneglit clause) then
	    ret := t;
	 tv := cdr tv
      >>;
      return ret
   end;

procedure ibalp_vmember(v,vl);
   vl and (ibalp_vequal(v,car vl) or ibalp_vmember(v,cdr vl));

procedure ibalp_vequal(v1,v2);
   ibalp_var!-getid v1 eq ibalp_var!-getid v2;

procedure ibalp_readclause(c,varal);
   % Read a clause. [c] is a clause in lisp prefix; [varal] is the
   % A-List of variables. Returns a pair: [clause] is the created
   % datastructure for this clause; [varal] is the updated list of
   % variables.
   begin scalar x,id,val,clause,nc,posids,negids,cnt;
      nc := rl_argn c;
      clause := ibalp_clause!-new();
      cnt := t; while cnt and nc do <<
	 x := car nc;
	 if x eq 'true then
	    cnt := nil
	 else <<
	    nc := cdr nc;
	    if x neq 'false then <<
	       if rl_op x eq 'not then <<
	       	  id := ibalp_arg2l rl_arg1 x;
	       	  val := 1 #- ibalp_arg2r rl_arg1 x
	       >> else <<
	       	  id := ibalp_arg2l x;
	       	  val := ibalp_arg2r x
	       >>;
	       if val #= 1 then <<
	       	  if not memq(id,posids) then <<
	       	     ibalp_clause!-setactpos(clause,
		     	ibalp_clause!-getactpos clause + 1);
	       	     posids := id . posids;
	       	     varal := ibalp_process!-var(clause,varal,id,1)
	       	  >>
	       >> else <<
	       	  if not memq(id,negids) then <<
	       	     ibalp_clause!-setactneg(clause,
		     	ibalp_clause!-getactneg clause + 1);
	       	     negids := id . negids;
	       	     varal := ibalp_process!-var(clause,varal,id,0)
	       	  >>
	       >>
	    >>
      	 >>
      >>;
      if not cnt then
	 return 'true . varal;
      return (clause . varal)
   end;

procedure ibalp_qsat!-readdimacs2(file);
   % Read a .cnf or .qdimacs file. [file] is the filename. Returns a
   % pair of the clauses and variables of this file.
   begin scalar ch,tok,doit,numvars,numclauses,varal,clausel,pair,qsat;
      ch := open(file, 'input);
      rds ch;
      tok := read();
      if not (tok eq 'p or tok eq 'c) then <<
	 rederr "Invalid input format";
	 rds nil;
      	 close(ch);
	 return {'false}
      >>;
      if tok eq 'c then doit := t;
      while doit do <<
      	 tok := read();
	 if tok eq 'p then doit := nil
      >>;
      tok := read();
      if not (tok eq 'cnf) then rederr "Invalid input format";
      numvars := read();
      numclauses := read();
      if !*rlverbose then
      	 ioto_tprin2t {"Reading ",numvars," variables and ",
	    numclauses," clauses"};
      tok := read();
      if tok eq 'e or tok eq 'a then <<
	 qsat := t;
	 if !*rlverbose then
      	    ioto_tprin2t "Q-SAT: Reading quantifiers";
	 pair := ibalp_readquant!-cnf(tok);
	 tok := car pair;
	 varal := cdr pair
      >>;
      pair := ibalp_readclause!-cnf(numclauses,varal,tok);
      clausel := car pair;
      varal := cdr pair;
      rds nil;
      close(ch);
      return (qsat . (clausel . varal))
   end;

procedure ibalp_readquant!-cnf(tok);
   % Read quantifier list from a .qdimacs file. [tok] is the last read
   % token. Returns a pair of the last read token and the new
   % variables.
   begin scalar varal,quant,level,doit,qswitch,var;
      if tok eq 'e then quant := 'ex else quant := 'all;
      level := 1;
      doit := t;
      while doit or qswitch do <<
	 tok := read();
	 if eqn(tok,0) then <<
	    doit := nil;
	    tok := read();
	    if tok eq 'a or tok eq 'e then <<
	       qswitch := t;
	       quant := if tok eq 'a then 'all else 'ex;
	       level := level + 1
	    >> else
	       qswitch := nil;
	 >> else <<
	    var := ibalp_var!-new(tok);
	    ibalp_var!-setqlevel(var,level);
	    ibalp_var!-setquant(var,quant);
	    varal := (tok . var) . varal;
	 >>
      >>;
      varal := reverse varal;
      return (tok . varal)
   end;

procedure ibalp_readclause!-cnf(numclauses,varal,lt);
   % Reads the clauses of a .cnf or .qdimacs file. [numclauses] is the
   % number of clauses; [varal] is the A-List of variables; [lt] is
   % the last read token. Returns a pair of clauses / variables.
   begin scalar doit,poslit,neglit,clause,tok,clausel,first; integer count;
      first := t;
      for i := 1 : numclauses do <<
      	 doit := t;
	 poslit := nil;
	 neglit := nil;
	 clause := ibalp_clause!-new();
      	 while doit do <<
	    if first then <<
	       tok := lt;
	       first := nil
	    >>
	    else
      	       tok := read();
      	    if tok = 0 then
	       doit := nil
	    else
	       if tok < 0 and null memq(-tok,neglit) then <<
		  ibalp_clause!-setactneg(clause,
		     ibalp_clause!-getactneg clause + 1);
		  varal := ibalp_process!-var(clause,varal,-tok,0)
	       >> else if tok > 0 and null memq(tok,poslit) then <<
		  ibalp_clause!-setactpos(clause,
		     ibalp_clause!-getactpos clause + 1);
		  varal := ibalp_process!-var(clause,varal,tok,1)
	       >>
      	 >>;
	 if ibalp_clmember(clause,clausel) or ibalp_redclause clause then <<
	    ibalp_undoclause clause;
	    count := count + 1
	 >> else
	    clausel := clause . clausel;
      >>;
      if !*rlverbose then
      	 ioto_tprin2t {"Deleted Redundant Clauses: ",count};
      return (clausel . varal)
   end;

procedure ibalp_process!-var(clause,varal,id,val);
   % Process a variable of the input. [clause] is a clause; [varal] is
   % the A-List of variables; [id] is a number; [val] is the value of
   % the variable. Returns the new A-List of variables.
   begin scalar h,var;
      id := intern compress('!! . explode id);
      if h := atsoc(id,varal) then
	 var := cdr h
      else <<
	 var := ibalp_var!-new(id);
	 varal := (id . var) . varal
      >>;
      if eqn(val,1) then <<
	 ibalp_var!-setposocc(var,clause);
	 ibalp_var!-setnumpos(var,ibalp_var!-getnumpos var + 1);
	 ibalp_var!-setposcc(var,ibalp_var!-getposcc var + 1);
	 ibalp_clause!-setposlit(clause,var)
      >>
      else <<
	 ibalp_var!-setnegocc(var,clause);
	 ibalp_var!-setnumneg(var,ibalp_var!-getnumneg var + 1);
	 ibalp_var!-setnegcc(var,ibalp_var!-getnegcc var + 1);
	 ibalp_clause!-setneglit(clause,var)
      >>;
      return varal
   end;

procedure ibalp_get3cnf(f);
   % Generate set of polynomials 3CNF. [f] is a formula. [trthval] is
   % 0 or 1. Returns a list of polynomials by transforming [f] into a
   % into a conjunctive clausal form, containing max 3 variables per
   % clause.
   begin scalar newf,newform;
      newf := f;
      newf := ibalp_pset3knfnf newf;
      newf := ibalp_pset3knf2(newf,nil);
      newf := if rl_op newf eq 'and then
            rl_mkn('and,for each j in rl_argn newf join
               ibalp_pset3knf3(j,nil))
         else
            rl_smkn('and,ibalp_pset3knf3(newf,nil));
      newform := for each c in rl_argn newf join
	 if rl_op c = 'equal or rl_op c = 'not then
	    {c}
	 else
	    rl_argn ibalp_cnf c;
      newform := 'and . newform;
      return newform
   end;

procedure ibalp_convcnf(clausel,varal,qsat);
   % Converts a list of clauses and variables into Lisp
   % Prefix. [clausel] is the list of variables; [varal] is the A-List
   % of variables; [qsat] indicates if it is a Q-SAT problem or
   % not. Returns the corresponding formula in lisp-prefix.
   begin scalar formula,tempcl,id,newvaral,rvaral;
      for each v in varal do <<
	 id := ibalp_var!-mkid ibalp_var!-getid cdr v;
	 newvaral := (ibalp_var!-getid cdr v . id) . newvaral
      >>;
      for each clause in clausel do <<
	 tempcl := nil;
	 for each v in ibalp_clause!-getposlit clause do <<
	    id := cdr atsoc(ibalp_var!-getid v,newvaral);
	    tempcl := {'equal,id,1} . tempcl;
	 >>;
	 for each v in ibalp_clause!-getneglit clause do <<
	    id := cdr atsoc(ibalp_var!-getid v,newvaral);
	    tempcl := {'equal,id,0} . tempcl
	 >>;
	 if length tempcl > 1 then
	    tempcl := 'or . tempcl
	 else
	    tempcl := {'equal, cadar tempcl,caddar tempcl};
	 formula := tempcl . formula;
      >>;
      if length formula > 1 then
      	 formula := 'and . formula;
      if qsat then <<
	 rvaral := reverse varal;
	 for each v in rvaral do <<
	    id := cdr atsoc(ibalp_var!-getid v,newvaral);
	    if ibalp_var!-isex cdr v then
	       formula := {'ex, id, formula}
	    else
	       formula := {'all, id, formula}
	 >>
      >>;
      return formula
   end;

procedure ibalp_var!-mkid(tok);
   % Turn a number into a identifier. [tok] is a number. Returns an
   % identifier.
   intern compress ('v . 'a . 'r . explode tok);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% QSAT %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

procedure ibalp_qsat!-cdcl(clausel,varal,origupl,qsat);
   % Main procedure for the conflictdriven clausel-learning QSAT
   % algorithm. [clausel] is the list of clauses; [varal] is the
   % A-List of variables; [origupl] is a set of initial unit clauses;
   % [qsat] indicates if it is a Q-SAT problem or a parametric
   % one. Return [true] if the formula is true, [false] else.
   begin scalar fin,break,res,level,pair,ec,lv,upl;
      pair := ibalp_qsat!-preprocess(clausel,varal,origupl,qsat);
      if car pair then return car pair;
      clausel := cadr pair;
      varal := cddr pair;
      level := 1;
      while null fin do <<
	 break := nil;
	 pair := ibalp_qsat!-cv(clausel,varal,level);
	 if cdr pair and eqn(level,1) then <<
	    res := {'false};
	    break := t;
	    fin := t
	 >> else
	    upl := car pair;
	 while null break do <<
	    pair := ibalp_qsat!-cdclup(upl,level);
	    ec := car pair;
	    lv := cdr pair;
	    if ec then <<
	       pair := ibalp_qsat!-analconf(ec,lv,level,clausel,varal);
	       if cddr pair < 0 then <<
		  res := {'false};
		  break := t;
		  fin := t
	       >> else <<
		  clausel := car pair;
		  level := cddr pair;
		  upl := car ibalp_qsat!-btcase(level,
		     cadr pair,varal,car clausel,t);
		  ec := nil
	       >>
	    >> else if ibalp_qsat!-csat clausel then <<
	       pair := ibalp_qsat!-analsatNAIV varal;
	       if cdr pair <= 0 then <<
		  res := {'true};
		  break := t;
		  fin := t
	       >> else <<
		  level := cdr pair;
		  upl := car ibalp_qsat!-btcase(level,car pair,varal,nil,nil);
	       >>
	    >> else <<
	       break := t;
	       level := level + 1
	    >>
	 >>
      >>;
      return (res . (clausel . varal))
   end;

procedure ibalp_qsat!-preprocess(clausel,varal,origupl,qsat);
   % Perform pre-processing on the formula. [clausel] is the list of
   % clauses; [varal] is the A-List of variables; [origupl] is the set
   % of initial unit clauses; [qsat] indicates if it is a Q-SAT or a
   % parametric problem. Return a pair of clauses/variables and a
   % possible return value (Sudden death).
   begin scalar pair,res;
      pair := ibalp_qsat!-cdclup(origupl,-1);
      if car pair then res := ({'false} . (clausel . varal));
      if qsat then <<
      	 pair := ibalp_qsat!-doSimpl(clausel,varal);
      	 clausel := car pair;
      	 varal := cdr pair
      >>;
      if ibalp_qsat!-csat clausel then res := ({'true} . (clausel . varal));
      if null clausel then res := ({'true} . (clausel . varal));
      if ibalp_qsat!-abort clausel then res := ({'false} . (clausel . varal));
      return (res . (clausel . varal))
   end;

procedure ibalp_qsat!-doSimpl(clausel,varal);
   % Do simplifications on the formula. [clausel] is the list of
   % clauses; [varal] is the A-List of variables. Return a pair of
   % clauses and variables.
   begin scalar h,pair; integer count;
      while h := ibalp_hassimple clausel do <<
      	 pair := ibalp_simplify(nil,nil,h,clausel,varal);
      	 clausel := car pair;
      	 varal := cdr pair
      >>;
      for each v in varal do <<
	 if ibalp_var!-isex cdr v and
	    eqn(ibalp_var!-getnumpos cdr v,0) then <<
	       count := count + 1;
	       ibalp_var!-setq(cdr v,0,0,nil);
	       pair := ibalp_simplify(cdr v,0,nil,clausel,varal);
	       clausel := car pair;
	       varal := cdr pair;
	    >> else if ibalp_var!-isex cdr v and
	    eqn(ibalp_var!-getnumneg cdr v,0) then <<
	       count := count + 1;
	       ibalp_var!-setq(cdr v,1,0,nil);
	       pair := ibalp_simplify(cdr v,1,nil,clausel,varal);
	       clausel := car pair;
	       varal := cdr pair;
      	    >>
      >>;
      if !*rlverbose then
      	 ioto_tprin2t {"Deleted variables in pre-processing: ",count};
      return (clausel . varal)
   end;

procedure ibalp_qsat!-cv(clausel,varal,level);
   % Choose and set a variable. [clausel] is the list of clauses;
   % [varal] is the A-List of variables; [level] is the current
   % level. Returns the new list of unit clauses.
   begin scalar cv,temp;
      cv := ibalp_qsat!-mom(varal,clausel);
      temp := ibalp_var!-setq(cv,1,level,nil);
      if cdr temp then <<
	 ibalp_var!-unsetq(cv,1);
	 temp := ibalp_var!-setq(cv,0,level,nil)
      >>;
      ibalp_var!-setflip(cv,0);
      return temp
   end;

procedure ibalp_qsat!-btcase(blevel,bvar,varal,cc,val);
   % Subprocedure for the backtrack case. [blevel] is the backtracking
   % level; [bvar] is the backtrack variable; [varal] is the A-List of
   % variables; [cc] is the new learnt clause; [val] indicates if it
   % is a conflict-driven or a SAT-driven backtracking. Returns the
   % list of new unit clauses.
   begin scalar tval,temp;
      tval := ibalp_var!-getval bvar;
      ibalp_qsat!-backtrack(blevel,varal,val);
      temp := ibalp_var!-setq(bvar,1-tval,blevel,cc);
      ibalp_var!-setflip(bvar,1);
      return temp
   end;


procedure ibalp_qsat!-analconf(ec,lv,level,clausel,varal);
   % Clausel Learning backtracking. [ec] is the conflict clause; [lv]
   % is the variable last set; [level] is the current level; [clausel]
   % is the list of clauses; [varal] is the A-List of
   % variables. Returns a pair. The first entry is the new list of
   % clauses. The second entry is a pair of conflict variable and the
   % conflict-level.
   begin scalar cl,cc,cv;
      if eqn(level,0) then return (clausel . (nil . -1));
      cc := ibalp_qsat!-calccc(varal,ec,lv);
      if null cc then
	 return (clausel . (nil . -1))
      else <<
	 cv := ibalp_qsat!-calccvar cc;
	 cl := ibalp_qsat!-getbtlevel(cc,level);
	 clausel := cc . clausel;
      	 return (clausel . (cv . cl))
      >>
   end;

procedure ibalp_qsat!-mom(varal,clausel);
   % Get a variable following the original MOM-strategy. [varal] is
   % the A-List of variables; [clausel] is the list of
   % clauses. Returns a variable.
   begin scalar min,tv,h,qlevel,tmom;
      min := ibalp_minclnr clausel;
      qlevel := ibalp_qsat!-qlevel varal;
      tmom := -1;
      for each v in varal do
	 if eqn(ibalp_var!-getqlevel cdr v,qlevel)
	    and null ibalp_var!-getval cdr v
	       and ibalp_var!-getquant cdr v then
	    	  if (h := ibalp_qsat!-calcmom(cdr v,min)) > tmom then <<
	       	     tmom := h;
	       	     tv := cdr v
	    	  >>;
      return tv
   end;

procedure ibalp_qsat!-calcmom(var,min);
   % Calculate the mom value of a variable. [var] is a variable; [min]
   % is the size of minimal clause. Returns the mom value.
   begin integer minpos,minneg;
      for each clause in ibalp_var!-getposocc var do
	 if null clause and eqn(ibalp_clause!-getactpos clause +
	    ibalp_clause!-getactneg clause,min) then
	       minpos := minpos + 1;
      for each clause in ibalp_var!-getnegocc var do
	 if null clause and eqn(ibalp_clause!-getactpos clause +
	    ibalp_clause!-getactneg clause,min) then
	       minneg := minneg + 1;
      return (minpos + minneg)*64 + (minpos * minneg)
   end;

procedure ibalp_qsat!-qlevel(varal);
   % Return the current quantification level. [varal] is the A-List of
   % variables. Returns the current quantification level.
   if null ibalp_var!-getval cdar varal then
      ibalp_var!-getqlevel cdar varal
   else
      ibalp_qsat!-qlevel cdr varal;

procedure ibalp_qsat!-hassimple(clausel);
   % Check if a clause list has some literals to simplify. [clausel]
   % is the list of clauses. Returns a clause to simplfy or [nil].
   begin scalar ret,tl,tv;
      tl := clausel;
      while tl and null ret do <<
      	 if eqn(length ibalp_clause!-getposlit car tl +
      	    length ibalp_clause!-getneglit car tl,1) then <<
	       tv := if null ibalp_clause!-getposlit car tl then
		  car ibalp_clause!-getneglit car tl
	       else
	 	  car ibalp_clause!-getposlit car tl;
	       if ibalp_var!-isex tv and ibalp_var!-getreas tv then
		  ret := car tl
	       if ibalp_var!-isex tv and ibalp_var!-getreas tv then
		  ret := car tl;
	    >>;
	 tl := cdr tl;
      >>;
      return ret
   end;

procedure ibalp_qsat!-abort(clausel);
   % Checks for contradictions after simplification. [clausel] is the
   % list of clauses. Return [t] if there is a contradiction, [nil]
   % else.
   if null clausel then nil else
      if null ibalp_clause!-getposlit car clausel
	 and null ibalp_clause!-getneglit car clausel then t
      else
	 ibalp_qsat!-abort cdr clausel;

procedure ibalp_qsat!-calccvar(clause);
   % Calculate the conflict variable of a new learnt clause. [clause]
   % is the new learnt clause. Returns the conflict variable.
   begin scalar tl,tv,cv,level;
      level := -1;
      tl := ibalp_clause!-getposlit clause;
      while tl do <<
	 tv := car tl;
	 if ibalp_var!-isex tv and ibalp_var!-getlev tv > level then <<
	    level := ibalp_var!-getlev tv;
	    cv := tv
	 >>;
	 tl := cdr tl
      >>;
      tl := ibalp_clause!-getneglit clause;
      while tl do <<
	 tv := car tl;
	 if ibalp_var!-isex tv and ibalp_var!-getlev tv > level then <<
	    level := ibalp_var!-getlev tv;
	    cv := tv
	 >>;
	 tl := cdr tl
      >>;
      return cv
   end;

procedure ibalp_qsat!-getbtlevel(clause,oldlev);
   % Calculate the backtrack level after a conflict case. [clause] is
   % the new learnt clause; [oldlev] is the old level; Returns the
   % backtrack level.
   begin scalar tl,tv,level,tlevel;
      level := -1;
      tl := ibalp_clause!-getposlit clause;
      tlevel := ibalp_var!-getlev ibalp_qsat!-calccvar clause;
      while tl do <<
	 tv := car tl;
	 if ibalp_var!-isex tv and
	    ibalp_var!-getlev tv > level and ibalp_var!-getlev tv < tlevel then
	    level := ibalp_var!-getlev tv;
	 tl := cdr tl
      >>;
      tl := ibalp_clause!-getneglit clause;
      while tl do <<
	 tv := car tl;
	 if ibalp_var!-isex tv and
	    ibalp_var!-getlev tv > level and ibalp_var!-getlev tv < tlevel then
	    level := ibalp_var!-getlev tv;
	 tl := cdr tl
      >>;
      return if eqn(level,-1) then oldlev - 1 else level
   end;

procedure ibalp_qsat!-calccc(varal,ec,lv);
   % Calculate conflict clause after Strategy: First UIP. [varal] is a
   % A-List of variables; [ec] is the empty clause to start the
   % calculation with [lv] is the last variable set. Returns the new
   % generated clause or nil if there is a sudden death.
   begin scalar newclause,tv,reas,doit,res;
      newclause := ibalp_clause!-new();
      res := t;
      ibalp_resolve(newclause,ec,ibalp_var!-getreas lv,lv);
      doit := ibalp_qsat!-doresolve(newclause,varal);
      if cdr doit then return nil;
      while car doit and res do <<
	 tv := ibalp_qsat!-getresvar newclause;
	 if eqn(ibalp_var!-getval tv,0) then
	    ibalp_dellit(tv,newclause,t)
	 else
	    ibalp_dellit(tv,newclause,nil);
	 reas := ibalp_var!-getreas tv;
	 if ibalp_clausetest(reas,newclause) then res := nil;
	 if not (null ibalp_clause!-getcount reas) then
	    ibalp_clause!-setcount(reas,ibalp_clause!-getcount reas + 1);
	 ibalp_resolve(newclause,newclause,reas,tv);
	 doit := ibalp_qsat!-doresolve(newclause,varal);
	 if cdr doit then res := nil
      >>;
      ibalp_clause!-setcount(newclause,1);
      return if res then newclause else nil;
   end;

procedure ibalp_clausetest(clause1,clause2);
   % Tests if two clauses have the same literal. [clause1] is a
   % clause; [clause2] is a clause. Returns [t] or [nil].
   ibalp_clause!-getposlit clause1 equal ibalp_clause!-getposlit clause2
      and ibalp_clause!-getneglit clause1 equal
	 ibalp_clause!-getneglit clause2;

procedure ibalp_qsat!-doresolve(newclause,varal);
   % Test the stopping criterion for resolving. [newclause] is a
   % clause; [varal] is the A-List of variables. Returns a pair. The
   % first entry is the result of the test, the second is a flag for a
   % sudden death.
   begin scalar hl,cl,hv,decv,ac1,ac2;
      hl := -2;
      for each v in ibalp_clause!-getposlit newclause do <<
	 if ibalp_var!-isex v then <<
	    ac1 := t;
	    if ibalp_var!-getlev v > hl then <<
	       hl := ibalp_var!-getlev v;
	       hv := v;
	       cl := 1
	    >> else if eqn(ibalp_var!-getlev v,hl) then
 	       cl := cl + 1;
 	    if ibalp_var!-getlev v > 0 then ac2 := t
	 >>
      >>;
      for each v in ibalp_clause!-getneglit newclause do <<
	 if ibalp_var!-isex v then <<
	    ac1 := t;
	    if ibalp_var!-getlev v > hl then <<
	       hl := ibalp_var!-getlev v;
	       hv := v;
	       cl := 1
	    >> else if eqn(ibalp_var!-getlev v,hl) then
 	       cl := cl + 1;
 	    if ibalp_var!-getlev v > 0 then ac2 := t
	 >>
      >>;
      if null ac1 or null ac2 then return (nil . t);
      if cl > 1 then return (t . nil);
      decv := ibalp_qsat!-searchdec(hl,varal);
      if not (ibalp_var!-isex decv) then return (t . nil);
      return ibalp_qsat!-unicheck(newclause,hv)
   end;

procedure ibalp_qsat!-searchdec(level,varal);
   % Search a decision variable at a certain level. [level] is the
   % level; [varal] is the A-List of variables.
   if null varal then nil else
      if eqn(ibalp_var!-getlev cdar varal,level) and
      null ibalp_var!-getreas cdar varal then
	 cdar varal
      else
	 ibalp_qsat!-searchdec(level,cdr varal);

procedure ibalp_qsat!-unicheck(clause,var);
   % Checks the third condition of the stopping criterion. [clause] is
   % a clause; [var] is a single variable. Returns a pair. The first
   % entry indicates the result of the check.
   begin scalar tl,res,tv,ql,dl;
      ql := ibalp_var!-getqlevel var;
      dl := ibalp_var!-getlev var;
      tl := ibalp_clause!-getposlit clause;
      while tl and null res do <<
	 tv := car tl;
	 if ibalp_var!-isuni tv and
	    ibalp_var!-getqlevel tv < ql then
	       if not (eqn(ibalp_var!-getval tv,0) and
		  ibalp_var!-getlev tv < dl) then
		     res := t;
	 tl := cdr tl;
      >>;
      tl := ibalp_clause!-getneglit clause;
      while tl and null res do <<
	 tv := car tl;
	 if ibalp_var!-isuni tv and
	    ibalp_var!-getqlevel tv < ql then
	       if not (eqn(ibalp_var!-getval tv,0) and
		  ibalp_var!-getlev tv < dl) then
		     res := t;
	 tl := cdr tl;
      >>;
      return (res . nil);
   end;

procedure ibalp_qsat!-getresvar(clause);
   % Get the variable for the next resolve. [clause] is a
   % clause. Returns a variable.
   begin scalar tl,tv,res,lev;
      tl := ibalp_clause!-getposlit clause;
      lev := -2;
      while tl do <<
      	 tv := car tl;
	 if ibalp_var!-getreas tv and ibalp_var!-getlev tv > lev then <<
	    res := tv;
	    lev := ibalp_var!-getlev tv
	 >>;
	 tl := cdr tl;
      >>;
      tl := ibalp_clause!-getneglit clause;
      while tl do <<
      	 tv := car tl;
	 if ibalp_var!-getreas tv and ibalp_var!-getlev tv > lev then <<
	    res := tv;
	    lev := ibalp_var!-getlev tv
	 >>;
	 tl := cdr tl;
      >>;
      return res
   end;

procedure ibalp_qsat!-analsatNAIV(varal);
   % Naive SAT-driven backtracking. [varal] is the A-List of
   % variables. Returns a pair of branching variable and the
   % branch-level.
   begin scalar cv,cl;
      cl := -1;
      for each v in varal do <<
	 if ibalp_var!-isuni cdr v and eqn(ibalp_var!-getflip cdr v,0) then
	    if ibalp_var!-getlev cdr v > cl then <<
	       cl := ibalp_var!-getlev cdr v;
	       cv := cdr v
	    >>
      >>;
      return (cv . cl)
   end;

procedure ibalp_qsat!-backtrack(level,varal,val);
   % Backtrack to a certain level. [level] is the backtrack level;
   % [varal] is the A-List of variables; [val] indicates if it is a
   % Conflict-driven or a SAT-driven backtracking.
   if val then <<
      for each v in varal do
      	 if ibalp_var!-getlev cdr v > level then <<
	    ibalp_var!-unsetq(cdr v,ibalp_var!-getval cdr v);
	    ibalp_var!-setflip(cdr v,nil)
      	 >>
   >> else <<
      for each v in varal do
	 if ibalp_var!-getlev cdr v >= level then <<
	    ibalp_var!-unsetq(cdr v,ibalp_var!-getval cdr v);
	    ibalp_var!-setflip(cdr v,nil)
      	 >>
   >>;

procedure ibalp_qsat!-cdclup(clist,level);
   % Unitpropagation. [clist] is a list of clauses with unit
   % variables; [level] is the level the reduction is made; Returns a
   % Pair. The first entry is an empty clause if one is derived the
   % second the variable set at last.
   begin scalar tl,tv,lv,ec,upl,temp;
      tl := clist;
      while tl and null ec do <<
	 tv := car tl;
	 if null ibalp_clause!-getsat cdr tv then <<
	    temp := ibalp_var!-setq(caar tv,cdar tv,level,cdr tv);
	    upl := car temp;
	    nconc(tl,upl)
	 >>;
	 tl := cdr tl;
	 lv := caar tv;
	 ec := cdr temp;
      >>;
      return (ec. lv)
   end;

procedure ibalp_qsat!-isunit(clause);
   % Check if a clause is a unit clause. [clause] is a clause. Returns
   % the unit variable and its assignment of a unit clause or [nil] if
   % the clause is not unit.
   begin scalar tl,tv,min,te; integer ce;
      if ibalp_clause!-getsat clause then return nil;
      %dirty hack
      min := 10000;
      tl := ibalp_clause!-getposlit clause;
      while tl and ce < 2 do <<
	 tv := car tl;
	 if ibalp_var!-isex tv and null ibalp_var!-getval tv then <<
	    ce := ce + 1;
	    te := (tv . 1)
	 >>;
	 if ibalp_var!-isuni tv and null ibalp_var!-getval tv and
 	    ibalp_var!-getqlevel tv < min then min := ibalp_var!-getqlevel tv;
	 tl := cdr tl
      >>;
      tl := ibalp_clause!-getneglit clause;
      while tl and ce < 2 do <<
	 tv := car tl;
	 if ibalp_var!-isex tv and null ibalp_var!-getval tv then <<
	    ce := ce + 1;
	    te := (tv . 0)
	 >>;
	 if ibalp_var!-isuni tv and null ibalp_var!-getval tv and
 	    ibalp_var!-getqlevel tv < min then min := ibalp_var!-getqlevel tv;
	 tl := cdr tl
      >>;
      return if eqn(ce,1) and ibalp_var!-getqlevel car te < min then
	 te
      else
	 nil
   end;

procedure ibalp_qsat!-isec(clause);
   % Check if a clause is an empty clause. [clausel] is the list of
   % clauses. Returns [t] if the clause is a empty clause, [nil] else.
   begin scalar ec,tl,tv;
      if ibalp_clause!-getsat clause then return nil;
      ec := t;
      tl := ibalp_clause!-getposlit clause;
      while ec and tl do <<
	 tv := car tl;
	 if ibalp_var!-isex tv and
 	    not eqn(ibalp_var!-getval tv,0) then ec := nil;
	 if ibalp_var!-isuni tv and
	    eqn(ibalp_var!-getval tv,1) then ec := nil;
	 if null ibalp_var!-getquant tv and null ibalp_var!-getval tv then
	    ec := nil;
	 tl := cdr tl
      >>;
      tl := ibalp_clause!-getneglit clause;
      while ec and tl do <<
	 tv := car tl;
	 if ibalp_var!-isex tv and
 	    not eqn(ibalp_var!-getval tv,1) then ec := nil;
	 if ibalp_var!-isuni tv and
	    eqn(ibalp_var!-getval tv,0) then ec := nil;
	 if null ibalp_var!-getquant tv and null ibalp_var!-getval tv then
	    ec := nil;
	 tl := cdr tl
      >>;
      return ec
   end;

procedure ibalp_qsat!-csat(clausel);
   % Check if the formula is satisfied. [clausel] is the List of
   % clauses. Returns [t] if all clauses are satisfied, [nil] else.
   ibalp_csat clausel;

procedure ibalp_readquantal(formula,varal);
   % Read prenex quantifiers of a formula. [formula] is a formula in
   % LISP-Prefix, [varal] is the A-List of variables. Reads the
   % quantifiers and annotates each quantified variable with its
   % quantifier and its quantification level. Returns a pair of the
   % highest quantification level and the A-List of the new sorted
   % variables.
   begin scalar hl,tl;
      tl := ibalp_readquantal2(formula,varal,rl_op formula,1,nil);
      hl := ibalp_var!-getqlevel cdar tl;
      for each v in varal do
	 if null ibalp_var!-getquant cdr v then
	    tl := v . tl;
      tl := reverse tl;
      return (hl . tl)
   end;

procedure ibalp_readquantal2(formula,varal,quant,level,newvaral);
   % Helper function for reading prenex quantifiers of a
   % formula. [formula] is a formula in LISP-Prefix, [varal] is the
   % A-List of variables, [quant] is the current quantifier, [level]
   % is the current quantification level, [varal] is the new A-list of
   % variables. Returns a A-List of the new sorted variables.
   if rl_quap rl_op formula then <<
      if not (rl_op formula eq quant) then level := level + 1;
      if atsoc(rl_var formula,varal) then <<
      	 ibalp_var!-setquant(cdr atsoc(rl_var formula,varal),rl_op formula);
      	 ibalp_var!-setqlevel(cdr atsoc(rl_var formula,varal),level);
      	 newvaral := (ibalp_var!-getid cdr atsoc(rl_var formula,varal) .
	    cdr atsoc(rl_var formula,varal)) . newvaral
      >>;
      ibalp_readquantal2(rl_mat formula,varal,rl_op formula,level,newvaral)
   >> else
      newvaral;

%%%%%%%%%%%%%%%%%%%%%%%%%% Parametric QSAT %%%%%%%%%%%%%%%%%%%%%%%%%%

procedure ibalp_qsat!-par(fvl,clausel,varal,result,psat);
   % The main procedure for parametric Q-SAT. [fvl] is the list of
   % currently free variables; [clausel] is the list of clauses; [varal] is the
   % A-List of variables; [result] is the current list of
   % results; [pqsat] ist the list of free variables. Returns a pair of the
   % result and the clauses/variables.
   begin scalar tv,res,pair,ec,upl,pair2,ec2;
      tv := ibalp_getfree!-dlcs fvl;
      if null tv then <<
	 if (not member(ibalp_qsat!-calcbin fvl,donel!*)) then <<
	    upl := ibalp_qsat!-getupl clausel;
	    res := ibalp_qsat!-cdcl(clausel,varal,upl,nil);
	    numcdcl!* := numcdcl!* + 1;
	    donel!* := ibalp_qsat!-calcbin fvl . donel!*;
	    if car res = {'true} then <<
	       result := (ibalp_exres fvl) . result;
	       if psat then
	       	  result := ibalp_qsat!-localsearch(clausel,varal,length fvl,
		     fvl,result);
	    >>;
	    return (result . (cadr res . cddr res));
	 >> else
	    return (result . (clausel . varal));
      >> else <<
	 ec := ibalp_var!-setq(tv,1,-42,nil);
	 if null cdr ec then <<
	    pair := ibalp_qsat!-par(fvl,clausel,varal,result,psat);
    	    result := car pair;
    	    clausel := cadr pair;
    	    varal := cddr pair;
    	    ibalp_qsat!-dav varal;
	 >>;
	 ibalp_var!-unsetq(tv,1);
	 ec := ibalp_var!-setq(tv,0,-42,nil);
	 if null cdr ec then <<
	    pair := ibalp_qsat!-par(fvl,clausel,varal,result,psat);
	    result := car pair;
	    clausel := cadr pair;
	    varal := cddr pair;
	    ibalp_qsat!-dav varal;
	 >>;
	 ibalp_var!-unsetq(tv,0);
	 return (result . (clausel . varal))
      >>
   end;

procedure ibalp_qsat!-localsearch(clausel,varal,radius,fvl,result);
   % Performs a local search with a given radius. [clausel] is the list of
   % clauses; [varal] is the A-List of variables; [radius] is the radius for
   % the local search; [fvl] is the list of free variables; [result] ist the
   % current result. Returns the new result.
   begin scalar v,oldl,varl;
      varl := ibalp_qsat!-getlocvars!-last(fvl,radius);
      for each v in varl do <<
	 oldl := ibalp_var!-getval v . oldl;
	 ibalp_var!-unsetq(v,ibalp_var!-getval v);
      >>;
      result := ibalp_qsat!-localsearchrec(clausel,varal,varl,fvl,result);
      for i := 1:length varl do <<
	 v := nth(varl,i);
	 if eqn(nth(oldl,(length oldl) - i + 1),0) then
	    ibalp_var!-setq(v,0,-42,nil)
	 else
	    ibalp_var!-setq(v,1,-42,nil)
      >>;
      return result
   end;

procedure ibalp_qsat!-getlocvars!-last(fvl,number);
   % Get the [number] last free variables for local search.
   begin scalar l,varl;
      l := length fvl;
      for i := l-number+1:l do
	    varl := nth(fvl,i) . varl;
      return varl
   end;

procedure ibalp_qsat!-getlocvars!-rand(fvl,number);
   % Get [number] random free variables for local search.
   begin scalar v,r,varl;
      while not eqn(length varl,number) do <<
	 r := random length fvl;
	 v := nth(fvl,r+1);
	 if (not memq(v,varl)) then
	    varl := v . varl
      >>;
      return varl
   end;

procedure ibalp_qsat!-localsearchrec(clausel,varal,selvars,fvl,result);
   % Recursive helper function of the local search.
   begin scalar tv,res,pair,ec,upl,pair2;
      tv := ibalp_getfree selvars;
      if null tv then <<
	 if ibalp_csat clausel and
	    not member(ibalp_qsat!-calcbin fvl,donel!*) then <<
	    donel!* := ibalp_qsat!-calcbin fvl . donel!*;
	    numlocs!* := numlocs!* + 1;
	    result := (ibalp_exres fvl) . result;
	 >>
      >> else <<
	 ec := ibalp_var!-setq(tv,1,-42,nil);
	 if null cdr ec then
	    result := ibalp_qsat!-localsearchrec(clausel,varal,selvars,
	       fvl,result);
	 ibalp_var!-unsetq(tv,1);
	 ec := ibalp_var!-setq(tv,0,-42,nil);
	 if null cdr ec then
	    result := ibalp_qsat!-localsearchrec(clausel,varal,selvars,
	       fvl,result);
	 ibalp_var!-unsetq(tv,0)
      >>;
      return result
   end;

procedure ibalp_qsat!-calcbin(fvl);
   % Calculate a binary representation of the current assignment to the free
   % variables in [fvl].
   if null fvl then 0 else
      ibalp_var!-getval car fvl * 2^(length fvl -1) +
      ibalp_qsat!-calcbin(cdr fvl);

procedure ibalp_printvars(fvl);
   % Helper function to print the list of variables.
   for each v in fvl do
      ioto_tprin2t {ibalp_var!-getid v, " ", ibalp_var!-getval v};

procedure ibalp_qsat!-getupl(clausel);
   % Get a initial set of unit clauses. [clausel] is the list of
   % clauses. Return a list of unit clauses.
   begin scalar upl,h;
      for each clause in clausel do
	 if (h := ibalp_qsat!-isunit clause) then
	    upl := (h . clause) . upl;
      return upl
   end;

procedure ibalp_exres2(resultl,fvl);
   % Expand the result. [resultl] is the list of results; [fvl] is the
   % list of free variables. Returns the complete result in lisp
   % prefix.
   begin scalar l,tl;
      l := length fvl;
      if eqn(length resultl,0) then return {'false};
      if eqn(length resultl,1) then return car resultl;
      if eqn(length resultl,2^l) then return {'true};
      for each res in resultl do <<
	 tl := res . tl
      >>;
      tl := 'or . tl;
      return tl
   end;

procedure ibalp_exres(vl);
   % Expand result. Expand a single result into Lisp Prefix. [vl] is a
   % list of variables. Return the expanded result.
   begin scalar tl,var,res;
      for each v in vl do <<
	 var := {'equal,ibalp_var!-getid v,ibalp_var!-getval v};
	 tl := var . tl;
      >>;
      if length tl > 1 then <<
	 for each v in tl do
	    res := v . res;
	 res := 'and . res
      >> else
	 res := car tl;
      return res
   end;

procedure ibalp_getfree(list);
   % Get an unassigned variable. [list] is the list of free
   % varibles. Returns a varialbe or [nil] if there is no unassigned.
   if null list then nil else
      if null ibalp_var!-getval car list then
	 car list
      else
	 ibalp_getfree cdr list;

procedure ibalp_getfree!-dlcs(list);
   % Get an unassigned variable. [list] is the list of free
   % varibles (following the DLCS strategy). Returns a varialbe
   % or [nil] if there is no unassigned.
   begin scalar tv,max;
      tv := ibalp_getfree list;
      if null tv then return nil;
      max := ibalp_var!-getnumneg tv + ibalp_var!-getnumpos tv;
      for each var in list do
	 if null ibalp_var!-getval var then
	    if ibalp_var!-getnumneg var +
	 ibalp_var!-getnumpos var > max then <<
	    tv := var;
	    max := ibalp_var!-getnumneg var + ibalp_var!-getnumpos var
	 >>;
      return tv;
   end;

procedure ibalp_psatp(varal);
   % Returns whether a problem is PSAT.
   %if null varal then
   %   t
   %else
   %   ibalp_var!-isex cdar varal and ibalp_psatp cdr varal;
   begin scalar ret;
      ret := t;
      for each v in varal do <<
	 if ibalp_var!-isuni cdr v then
	     ret := nil;
      >>;
      return ret;
   end;

procedure ibalp_splitvars(pqsat,varal);
   % Split variables. [pqsat] is the list of free variables; [varal]
   % is the A-List of variables. Deletes all the free variables from
   % the variable list and returns the two lists of bound and free
   % variables.
   begin scalar fvl,tv;
      for each v in pqsat do <<
	 tv := cdr atsoc(v,varal);
	 fvl := tv . fvl;
	 varal := delete((v . tv),varal)
      >>;
      return (varal . fvl)
   end;

procedure ibalp_qsat!-dav(varal);
   % Delete all assignments to variables. [varal] is the A-List of
   % variables; [clausel] is the list of clauses.
   for each v in varal do <<
      if ibalp_var!-getval cdr v then
	 ibalp_var!-unsetq(cdr v,ibalp_var!-getval cdr v);
      ibalp_var!-setflip(cdr v,nil)
   >>;

endmodule;  % ibalpqsat

end;  % of file
