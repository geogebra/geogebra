% ----------------------------------------------------------------------
% $Id: qqeqe.red 81 2009-02-06 18:22:31Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2005-2009 Andreas Dolzmann and Thomas Sturm
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
   fluid '(qqe_qe_rcsid!* qqe_qe_copyright!*);
   qqe_qe_rcsid!* :=
      "$Id: qqeqe.red 81 2009-02-06 18:22:31Z thomas-sturm $";
   qqe_qe_copyright!* := "Copyright (c) 2005-2009 A. Dolzmann and T. Sturm"
>>;

module qqeqe;
% Quantifierelimination for queues. Module with algorithms
% for elimination process.



fluid '(qqe_resf!*  % list of atomic formulas not containing the 
                       % bounded variable -qvar-, which actually has
                       % to be eliminated
   qqe_qvarf!*      % list of atomic formulas containing qvar
   qqe_atf!-qequal!-ext!-c!* % list of atomic formulas of form: q == p
   qqe_atf!-qequal!-ext!-p!* % list of atomic formulas of form: Lq == p
   qqe_atf!-equal!-ext!* % list of at. formulas of form: t( ..,Lq,..) = ..
   qqe_atf!-equal!-int!* % list of atomic formulas of form: 
                         % t(...,Lq,...)=t(...,Mq,...)
   qqe_atf!-misc!-basic!* % list of atf of basic type not = or neq
   qqe_atf!-qequal!-int!*   % list of atomic formulas of form: Lq == Mq
   qqe_la!*         % left area
   qqe_ra!*         % right area
   qqe_pat!-lengths!* % list of pattern lengths
   qqe_sf!*         % formula to be quantifiereliminated
   qqe_atf!-qneq!-int!* % list of atomic formulas of form: Lq<<>>Mq
   qqe_atf!-qneq!-ext!* % list of atomic formulas of form: Lq<<>>p
   qqe_atf!-qneq!-ext!-qepsilon!* % list of atform. of form Lq <<>> qeps.
   qqe_atf!-neq!*    % list of atomic formulas of form: t(..,Lq,..) neq ..
   % qqe_atf!-neq!-int!-s!* % list of atomic formulas of form: 
                          % head(..(p)) neq head(..(p))
   qqe_var!*         % quantifier which binds qqe_var!* has to be 
                        % eliminated
   qqe_qqu!-list!*    % list of quantifiers of type queue which have to
                        % to be eliminated
   qqe_bqu!-list!*    % list of quantifiers of basic type
   % qqe_id_counter!*   % counts the number of new id's already used
   );

imports qqeqemisc.red;


procedure qqe_satlengths(minlength, maxlength, lcm, lara);
   % Queue quantifier elimination saturation lengths. Returns a list of
   % integers representing lengths which have to be considered for the
   % equivalent quantifierfree formula. [[minlength]], [[maxlength]], are
   % positive integer, [[lcm]] is nil in case there are no pattern in
   % the formula, else a positive integer, [[lara]] is a positive
   % integer denoting left area + right area.
   begin scalar list, sll;
      % if there are pattern in formula then add the corresponding
      % satlengths (lowest common multiplier of pat lengths!)
      if lcm then
         for j:=0:(lcm-1) do
            sll := append(sll, {j})
      else sll := {0};

      for j:=minlength:(max(lara,minlength)) do 
      <<
         for each x in sll do
            if null maxlength or maxlength >= j+x then
               list := lto_insertq(j + x,list);
      >>;
      
      if null lcm and qqe_atf!-qneq!-int!* then
         list := lto_insertq(lara +1,list);
      
      return qqe_quicksort list;
                        
   end;

% ------------------ Quantifierelimination ------------------------------

procedure qqe_qe(u);
   % Queue quantifier elimination entry function. [u] is a formula 
   % for which a existential quantifier binding [qvar] has to be
   % eliminated. The function initiates a scan of [u] to decide,
   % what further steps have to be done.
   begin scalar f;
   
      f := rl_pnf(u);
      f := cadr qqe_get!-quantifier!-sequenz(f);
      
      f := qqe_la2lth f;
      qqe_elimb!* := t;
      if null qqe_qqu!-list!* then <<
         if qqe_elimb!* then <<
            % return qqe_qe!-basic u
            f := qqe_simpl!-dnf rl_dnf qqe_qe!-basic 
               qqe_iterate!-quantifier(qqe_bqu!-list!*,f);
            qqe_qeexit();
            return f;
         >>
         else << qqe_qeexit(); return u;>>;
      >>;
      
      for each q in qqe_qqu!-list!* do
      <<
         if not atom f then <<
            if car q eq 'ex then f := cadr qqe_get!-quantifier!-sequenz f 
            else <<
               f := rl_dnf cadr qqe_get!-quantifier!-sequenz {'not, f};
               f := qqe_make!-harmless f;
               %pause;
            >>;
            
            f := qqe_la2lth f;
            
            f := rl_dnf f;
            
            if car q eq 'ex then f := rl_pnf qqe_qe!-dnf(f,cadr q) 
            else f := rl_pnf {'not,qqe_qe!-dnf(f,cadr q)};
      
         >>;
      >>;
      
      f := rl_pnf rl_simpl(f,nil,-1);
      if not atom f then <<
         << if x then f := cadr x >>
            where x=qqe_get!-quantifier!-sequenz f;
         f := qqe_simpl!-dnf rl_dnf f;
         f := qqe_iterate!-quantifier(qqe_bqu!-list!*,f);
      >>;
      qqe_qeexit();
      return if qqe_elimb!* then qqe_qe!-basic f else f;
            
   end;

procedure qqe_qeexit();
   % QQE exit. Exit function. Free memory.
   begin
      qqe_resf!* := nil;
      qqe_qvarf!* := nil;    
      qqe_atf!-qequal!-ext!-c!* := nil;
      qqe_atf!-qequal!-ext!-p!* := nil;
      qqe_atf!-equal!-ext!* := nil;
      qqe_atf!-equal!-int!* := nil;
      qqe_atf!-misc!-basic!* := nil;
      qqe_atf!-qequal!-int!* := nil;  
      qqe_la!* := nil;    
      qqe_ra!* := nil;
      qqe_pat!-lengths!* := nil;
      qqe_sf!* := nil;       
      qqe_atf!-qneq!-int!* := nil;
      qqe_atf!-qneq!-ext!* := nil;
      qqe_atf!-qneq!-ext!-qepsilon!* := nil;
      qqe_atf!-neq!* := nil;  
      qqe_var!* := nil;   
      qqe_qqu!-list!* := nil;
      qqe_bqu!-list!* := nil;
   end;

procedure qqe_qe!-clause!-init(u);
   % initialize fluids
   begin
      qqe_resf!* := nil; 
      qqe_qvarf!* := nil;
      qqe_atf!-qequal!-ext!-c!* := nil;
      qqe_atf!-qequal!-ext!-p!* := nil;
      qqe_atf!-equal!-ext!* := nil;
      qqe_atf!-equal!-int!* := nil; 
      qqe_atf!-misc!-basic!* := nil; 
      qqe_atf!-qequal!-int!* := nil; 
      qqe_la!* := nil;
      qqe_ra!* := nil;
      qqe_pat!-lengths!* := nil; 
      qqe_sf!* := nil;
      qqe_atf!-qneq!-int!* := nil;
      qqe_atf!-qneq!-ext!* := nil;
      qqe_atf!-qneq!-ext!-qepsilon!* := nil;
      qqe_atf!-neq!* := nil;
      %% qqe_atf!-neq!-int!-s!* := nil;
      qqe_var!* := nil;
      % qqe_qqu!-list!* := nil;
      % qqe_bqu!-list!* := nil;

      % return  qqe_length!-graph!-clause u;
      return qqe_clause!-update!-lengths(u,t);
   end;

procedure qqe_qe!-dnf(u,q);
   % Queue quantifier elimination for disjunctive normal form.
   % [u] is dnf. Quantifier of variable [q] is has to be eliminated. 
   begin scalar f, temp;
      if u memq '(true false) then return u
      else if car u eq 'or then
      <<
         f := {'or};
         if cddr u then 
         <<
            for each x in cdr u do 
            <<
               temp := qqe_qe!-clause(x,q);
               f := append(f, {temp});  
            >>;
         >>
         else 
            f := qqe_qe!-clause(cadr u,q);
      >>
      else f := qqe_qe!-clause(u,q);
      
      return f;
   end;

procedure qqe_qe!-clause(u,q);
   % Queue quantifier elimination for clause of dnf.  [u] is a
   % conjunctive clause. Quantifier of variable [q] is has to be
   % eliminated.
   begin scalar f, list; 
      list := qqe_qe!-clause!-init(u);
      if list eq 'false then u := 'false
      else if list then <<
         if null qqe_harmless!-formula!-test!-clause1(u, list) then <<
            qqe_length!-graph!-delete list;
            qqe_qeexit();
            rederr("input formula is not harmless");
         >>;
      >>;

      if not atom u then u := qqe_simpl!-clause(u);

      if idp u then <<
         if list neq 'false then qqe_length!-graph!-delete(list);
         return u;
      >>;

      f := u;

      % initialization of fluids
      qqe_var!* := q;
      qqe_sf!* := f;

      qqe_sort!-atf(qqe_sf!*, qqe_var!*);
      
      if null qqe_qvarf!* then <<
         qqe_length!-graph!-delete(list);
         return f;
      >>;

      % calculation of left and right area
      qqe_la!* := qqe_la();
      qqe_ra!* := qqe_ra();
  
      % decision how to proceed according to structure of input formula
      if qqe_atf!-qequal!-ext!-c!* or qqe_atf!-qequal!-ext!-p!* 
      then <<
	 f := qqe_qe!-dna();
	 if qqe_resf!* then
            f := append({'and,f},qqe_resf!*);
      >>
      else <<
         if qqe_atf!-qneq!-ext!* then f:= qqe_ndna!-qneq!-ext()
         else f := qqe_ndna(nil,nil ,nil, qqe_la!* + qqe_ra!*); 
         if qqe_resf!* then
            f := append({'and,f},qqe_resf!*);
      >>;
  
      f := rl_simpl(f,nil,-1);
     
      qqe_length!-graph!-delete(list);
      
      return f;      
   end;

procedure qqe_qe!-dna();
   % Queue quantifier elimination determined neutral area. Initiates
   % quantifier elimination for the case that the neutral area is
   % fully determined.
   if qqe_atf!-qequal!-ext!-c!* then qqe_qe!-dna!-fd()
   else qqe_qe!-dna!-nfd();
   
procedure qqe_qe!-dna!-fd();
   % Queue quantifier elimination determined neutral area with [qqe_sf!*]
   % is fully determined, e.g. a atomic formula of the form: q==p exists.
   qqe_simpl!-clause subst(qqe_arg2r car qqe_atf!-qequal!-ext!-c!*,
      qqe_var!*, qqe_sf!*);
  
procedure qqe_qe!-dna!-nfd();
   % Queue quantifier elimination determined neutral area with [qqe_sf!*]
   % is not fully determined with a formula of the form q == p.
   begin scalar l,r,j, phi, rho, phi_e, k, idlist, minlength, maxlength;
      % init of variables   
      minlength := get(qqe_var!*,'minlength);
      if null minlength then minlength := 0;
      maxlength := get(qqe_var!*,'maxlength);
      if qqe_debug!* then 
	 prin2t{"minlength=",minlength," maxlength=", maxlength};
      j := qqe_atf!-qequal!-ext!-min!-prefix();
      l := qqe_atf!-qequal!-ext!-p!-min!-lefts(minlength, cadr j);
      r := qqe_atf!-qequal!-ext!-p!-min!-rights(minlength,cadr j);
      k := car l + car r;
      if maxlength then k := min(maxlength, k);
      idlist := qqe_new!-ids!-x(k, qqe_sf!*);

      % subformula generation
      % - rho
      if minlength + 1 < k then
         rho := qqe_atf!-dna!-nfd!-rho(idlist, minlength, k)
       else rho := 'false; 
      % - phi
      phi := qqe_simpl!-clause subst(
         qqe_atf!-dna!-nfd!-psi!-subst(l,r,j, idlist), qqe_var!*, qqe_sf!*);
      % - phi_e
      if minlength = 0 then
         phi_e := qqe_simpl!-clause subst('qepsilon, qqe_var!*, qqe_sf!*)
      else phi_e := 'false; 
      if qqe_debug!* then <<prin2t{phi,rho,phi_e};pause;>>;

      return qqe_qe!-basic qqe_iterate!-quantifier!-ex(k, idlist, 
        qqe_la2lth {'or, rho, phi, phi_e});
   end;


procedure qqe_atf!-dna!-nfd!-rho(idlist, minlength, kup);
   begin scalar f, ff, idlistx;
      for j:=max(minlength,1):kup do 
      <<
         idlistx := qqe_list!-take!-n(idlist,kup,j);
         f := qqe_subst(j,idlistx);         
         ff := f . ff;
      >>;
      
      if null cdr ff then return car ff
      else return 'or . ff;
   end;

procedure qqe_atf!-dna!-nfd!-rho!-subst(list,k);
   % obsolete - but eventually needed later
   begin scalar x, f;
      x := list;
      f := 'qepsilon;

      for j:=1:k do
      <<
         f := {'radd, car x, f};
         x := cdr x;
      >>;
      return f;
   end;

procedure qqe_atf!-dna!-nfd!-psi!-subst(l,r,j, idlist);
   % QQE atomic formula determined neutral area not fully determined
   % psi substitution. [l], [r], [j] are natural
   % numbers. [idlist] is a list of identifiers.
   begin scalar x, f;
      
      x := idlist;
      f := qqe_arg2r cadr j;

      if cadr l neq cadr j then
      <<
         for jj:=0:
            (qqe_prefix!-lefts qqe_arg2l cadr j - 
               qqe_prefix!-lefts qqe_arg2l cadr l - 1)
               do f := {'ladd, {'lhead, 
                  qqe_iterate!-rtail(jj, qqe_arg2r cadr l)}, f};
      >>;

      if car l neq 0 then
      <<
         for j:=1:car l do
         <<
            f := {'ladd, car x, f};
            x := cdr x;
         >>;
      >>;
      
      if cadr r neq cadr j then
      <<
         for jj:=0:
            (qqe_prefix!-rights qqe_arg2l cadr j - 
               qqe_prefix!-rights qqe_arg2l cadr r - 1)
                  do f:= {'radd,{'rhead, 
                     qqe_iterate!-ltail(jj, qqe_arg2r cadr r)}, f};
      >>;
      
      if car r neq 0 then
      <<
         for jj:=1:car r do
         <<
            f := {'radd, car x, f};
            x := cdr x;
         >>;
      >>;
      return f;
   end;

procedure qqe_ndna(minlength, maxlength,lcm, lara);
   % Queue quantifier elimination not determined neutral area.
   % [minlength], [maxlength], [lcm] are natural numbers, or nil.
   begin scalar satlengths,f, new_ids, new_idsx;
      
      if null minlength then minlength := get(qqe_var!*,'minlength);
      if null maxlength then maxlength := get(qqe_var!*,'maxlength);
      if null minlength then minlength := 0;

      if null qqe_pat!-lengths!* and qqe_atf!-qequal!-int!* then
      <<
         qqe_pat!-lengths!* := qqe_pat!-lengths(lara);
         if null lcm then
            lcm := qqe_lcm!-list qqe_pat!-lengths!*;
      >>;

      satlengths := qqe_satlengths(minlength, maxlength, lcm, lara);

      if qqe_debug!* then 
         prin2t{"satlengths=",satlengths, "for ", qqe_var!*};

      new_ids := qqe_new!-ids!-x(car satlengths, qqe_sf!*);
      
      f := nil;
      for each x in satlengths do
      << %!!!
         new_idsx := qqe_list!-take!-n(new_ids,car satlengths,x);
         f := qqe_qe!-basic(qqe_iterate!-quantifier!-ex(x,new_idsx,
            qqe_subst(x, new_idsx))) . f;
      >>;
      if cdr satlengths then f := 'or . f else f := car f;
      return f;
   end;

procedure qqe_ndna!-qneq!-ext!-phi2!-true();
   if qqe_atf!-qequal!-ext!-c!* or qqe_atf!-qequal!-ext!-p!* or
      qqe_atf!-equal!-ext!* or qqe_atf!-equal!-int!* or
      qqe_atf!-misc!-basic!* or qqe_atf!-qequal!-int!* or
      qqe_atf!-qneq!-int!* or qqe_atf!-neq!* 
   then nil
   else 'true;
  
procedure qqe_ndna!-qneq!-ext();
   % QQE not determined neutral area for the case that
   % [qqe_atf!-qneq!-ext!*] is not nil.
   begin scalar phi1, phi2, lcm, lara;
      
      lara := qqe_la!* + qqe_ra!*;
      if qqe_atf!-qequal!-int!* then
      <<
         qqe_pat!-lengths!* := qqe_pat!-lengths(lara);
         lcm := qqe_lcm!-list qqe_pat!-lengths!*;
      >>
      else lcm := 1;
      
      phi1 := rl_simpl(qqe_ndna(nil,nil, lcm, lara),nil,-1);
      if qqe_debug!* then pause;
      phi2 := qqe_ndna!-qneq!-ext!-phi2!-true(); 
   
      qqe_atf!-qneq!-ext!* := nil;
      qqe_atf!-qneq!-ext!-qepsilon!* := nil;
      if null phi2 then phi2 := rl_simpl(qqe_ndna(lara + lcm, 
         lara + 2 * lcm - 1, lcm, lara + lcm),nil,-1);
      if qqe_debug!* then pause;
      return {'or, phi1, phi2};
   end;
      
endmodule;  % [qqeqe]

end;  % of file
