% ----------------------------------------------------------------------
% $Id: acfsfsism.red 67 2009-02-05 18:55:15Z thomas-sturm $
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
   fluid '(acfsf_sism_rcsid!* acfsf_sism_copyright!*);
   acfsf_sism_rcsid!* :=
      "$Id: acfsfsism.red 67 2009-02-05 18:55:15Z thomas-sturm $";
   acfsf_sism_copyright!* := "Copyright (c) 1995-2009 A. Dolzmann and T. Sturm"
>>;

module acfsfsism;
% Algebraically closed field standard form smart simplification.
% Submodule of [acfsf].

%DS
% <IRL> ::= (<IR>,...)
% <IR> ::= <PARA> . <DB>
% <DB> ::= (<LE>,...)
% <LE> ::= <LABEL> . <ENTRY>
% <LABEL> ::= <INTEGER>
% <ENTRY> ::= <ACFSF RELATION> . <STANDARD QUOTIENT>

procedure acfsf_smrmknowl(knowl,v);
   % Algebraically closed field smart simplification remove from
   % knowledge. [knowl] is an IRL; [v] is a variable. Returns an IRL.
   % Destructively removes all information about [v] from [knowl].
   if null knowl then
      nil
   else if v member kernels caar knowl then
      acfsf_smrmknowl(cdr knowl,v)
   else <<
      cdr knowl := acfsf_smrmknowl(cdr knowl,v);
      knowl
   >>;

procedure acfsf_smcpknowl(knowl);
   % Algebraically closed field smart simplification copy knowledge.
   % [knowl] is an IRL. Returns an IRL. Copies [knowl] and the
   % contained IR's and DB's.
   for each ir in knowl collect
      car ir . append(cdr ir,nil);

procedure acfsf_smupdknowl(op,atl,knowl,n);
   % Algebraically closed field smart simplification update knowledge.
   % [op] is one of [and], [or]; [atl] is a list of (simplified)
   % atomic formulas; [knowl] is a conjunctive IRL; [n] is the current
   % level. Returns an IRL. Destructively updates [knowl] wrt. the
   % [atl] information.
   begin scalar w,ir,a;
      while atl do <<
	 a := if op eq 'and then car atl else acfsf_negateat car atl;
	 atl := cdr atl;
	 ir := acfsf_at2ir(a,n);
	 if w := assoc(car ir,knowl) then <<
	    cdr w := acfsf_sminsert(cadr ir,cdr w);
	    if cdr w eq 'false then <<
	       atl := nil;
	       knowl := 'false
	    >>  % else [acfsf_sminsert] has updated [cdr w] destructively.
	 >> else
	    knowl := ir . knowl
      >>;
      return knowl
   end;

procedure acfsf_smmkatl(op,oldknowl,newknowl,n);
   % Algebraically closed field smart simplification make atomic
   % formula list. [op] is one of [and], [or]; [oldknowl] and
   % [newknowl] are IRL's; [n] is an integer. Returns a list of atomic
   % formulas.
   acfsf_irl2atl(op,newknowl,n);

procedure acfsf_smdbgetrel(abssq,db);
   if abssq = cddar db then
      cadar db
   else if cdr db then
      acfsf_smdbgetrel(abssq,cdr db);

procedure acfsf_at2ir(atf,n);
   % Algebraically closed field standard form atomic formula to IR.
   % [atf] is an atomic formula; [n] is an integer. Returns the IR
   % representing [atf] on level [n].
   begin scalar op,par,abs,c;
      op := acfsf_op atf;
      abs := par := acfsf_arg2l atf;
      while not domainp abs do abs := red abs;
      par := addf(par,negf abs);
      c := sfto_dcontentf(par);
      par := quotf(par,c);
      abs := quotsq(!*f2q abs,!*f2q c);
      return par . {n . (op . abs)}
   end;

procedure acfsf_irl2atl(op,irl,n);
   % Algebraically closed field standard form IRL to atomic formula
   % list. [irl] is an IRL; [n] is an integer. Returns a list of
   % atomic formulas containing the level-[n] atforms encoded in IRL.
   for each ir in irl join acfsf_ir2atl(op,ir,n);

procedure acfsf_ir2atl(op,ir,n);
   (for each le in cdr ir join
      if car le = n then {acfsf_entry2at(op,cdr le,a)}) where a=!*f2q car ir;

procedure acfsf_entry2at(op,entry,parasq);
   if !*rlidentify then
      cl_identifyat acfsf_entry2at1(op,entry,parasq)
   else
      acfsf_entry2at1(op,entry,parasq);

procedure acfsf_entry2at1(op,entry,parasq);
   acfsf_0mk2(acfsf_clnegrel(car entry,op eq 'and),
      numr addsq(parasq,cdr entry));

procedure acfsf_sminsert(le,db);
   % Algebraically closed field standard form smart simplify insert.
   % [le] is a marked entry; [db] is a database. Returns a database.
   % Destructively inserts [le] into [db].
   begin scalar a,w,scdb,oscdb;
      repeat <<
      	 w := acfsf_sminsert1(cadr car db,cddr car db,cadr le,cddr le,car le);
      	 if w and not idp w then <<  % identifiers [false] and [true] possible.
	    db := cdr db;
	    le := w
      	 >>
      >> until null w or idp w or null db;
      if w eq 'false then return 'false;
      if w eq 'true then return db;
      if null db then return {le};
      oscdb := db;
      scdb := cdr db;
      while scdb do <<
	 a := car scdb;
	 scdb := cdr scdb;
	 w := acfsf_sminsert1(cadr a,cddr a,cadr le,cddr le,car le);
	 if w eq 'true then <<
	    scdb := nil;
	    a := 'true
	 >> else if w eq 'false then <<
	    scdb := nil;
	    a := 'false
	 >> else if w then <<
	    cdr oscdb := scdb;
	    le := w
	 >> else
	    oscdb := cdr oscdb
      >>;
      if a eq 'false then return 'false;
      if a eq 'true then return db;
      return le . db
   end;

procedure acfsf_sminsert1(r1,a,r2,b,n);
   % Algebraically closed field standard form smart simplify insert.
   % [r1], [r2] are relations, [a], [b] are absolute summands in SQ
   % representation; [n] is the current level. Returns [nil], [false],
   % [true], or a marked entry. Simplification of $\alpha=[r2](f+b,0)$
   % under the condition $\gamma=[r1](f+a,0)$ is considered: [nil]
   % means there is no simplification posssible; [true] means that
   % $\gamma$ implies $\alpha$; [false] means that $\alpha$
   % contradicts $\gamma$; the atomic formula encoded by a resulting
   % marked entry wrt. $f$ is equivalent to $\alpha$ under $\gamma$.
   begin scalar w,diff,n;
      diff := numr subtrsq(a,b);
      if null diff then <<
	 w := acfsf_smeqtable(r1,r2);
      	 if w eq 'false then return 'false;
	 % [w eq r1]
	 return 'true
      >>;
      if minusf diff then <<
      	 w := acfsf_smordtable(r1,r2);
	 if atom w then return w;
      	 if eqcar(w,r1) and cdr w then return 'true;
	 return n . (car w . if cdr w then a else b)
      >>;
      w := acfsf_smordtable(r2,r1);
      if atom w then return w;
      if eqcar(w,r1) and null cdr w then return 'true;
      return n . (car w . if cdr w then b else a)
   end;

procedure acfsf_smeqtable(r1,r2);
   % Algebraically closed field standard form smart simplify equal
   % absolute summands table. [r1], [r2] are relations. Returns
   % [false] or a relation $R$ such that $R(f+a,0)$ is equivalent to
   % $[r1](f+a,0) \land [r2](f+a,0)$.
      if r1 eq r2 then r1 else 'false;

procedure acfsf_smordtable(r1,r2);
   % Algebraically closed field standard form smart simplify ordered
   % absolute summands table. [r1], [r2] are relations. Returns [nil],
   % which means that no simplification is possible, [false] or a pair
   % $R . s$ where $R$ is a relation and $s$ is one of [T], [nil]. For
   % absolute summands $a<b$ we have $[r1](f+a,0) \land [r2](f+b,0)$
   % equivalent to $R(f+a,0)$ in case $[s]=[T]$ or to $R(f+b,0)$ in
   % case $[s]=[nil]$.
   if r1 eq 'equal and r2 eq 'equal then
      'false
   else if r1 neq r2 then
      'equal . (r1 eq 'equal);

endmodule;  % [acfsfsism]

end;  % of file
