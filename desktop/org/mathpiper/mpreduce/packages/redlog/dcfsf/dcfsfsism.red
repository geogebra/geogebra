% ----------------------------------------------------------------------
% $Id: dcfsfsism.red 595 2010-05-09 05:01:45Z thomas-sturm $
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
   fluid '(dcfsf_sism_rcsid!* dcfsf_sism_copyright!*);
   dcfsf_sism_rcsid!* :=
      "$Id: dcfsfsism.red 595 2010-05-09 05:01:45Z thomas-sturm $";
   dcfsf_sism_copyright!* := "Copyright (c) 2010 T. Sturm"
>>;

module dcfsfsism;
% Differentially closed field standard form smart simplification.
% Submodule of [dcfsf].

%DS
% <IRL> ::= (<IR>,...)
% <IR> ::= <PARA> . <DB>
% <DB> ::= (<LE>,...)
% <LE> ::= <LABEL> . <ENTRY>
% <LABEL> ::= <INTEGER>
% <ENTRY> ::= <DCFSF RELATION> . <STANDARD QUOTIENT>

procedure dcfsf_smrmknowl(knowl,v);
   % Differentially closed field smart simplification remove from
   % knowledge. [knowl] is an IRL; [v] is a variable. Returns an IRL.
   % Destructively removes all information about [v] from [knowl].
   if null knowl then
      nil
   else if v member kernels caar knowl then
      dcfsf_smrmknowl(cdr knowl,v)
   else <<
      cdr knowl := dcfsf_smrmknowl(cdr knowl,v);
      knowl
   >>;

procedure dcfsf_smcpknowl(knowl);
   % Differentially closed field smart simplification copy knowledge.
   % [knowl] is an IRL. Returns an IRL. Copies [knowl] and the
   % contained IR's and DB's.
   for each ir in knowl collect
      car ir . append(cdr ir,nil);

procedure dcfsf_smupdknowl(op,atl,knowl,n);
   % Differentially closed field smart simplification update knowledge.
   % [op] is one of [and], [or]; [atl] is a list of (simplified)
   % atomic formulas; [knowl] is a conjunctive IRL; [n] is the current
   % level. Returns an IRL. Destructively updates [knowl] wrt. the
   % [atl] information.
   begin scalar w,ir,a,h;
      if op eq 'or then
	 atl := for each at in atl collect dcfsf_negateat at;
      if !*rlsiplugtheo then
	 atl := dcfsf_plugtheo(atl,knowl);
      if atl eq 'false then
	 return 'false;
      while atl do <<
	 a := pop atl;
	 ir := dcfsf_at2ir(a,n);
	 if w := assoc(car ir,knowl) then <<
	    cdr w := dcfsf_sminsert(cadr ir,cdr w);
	    if cdr w eq 'false then <<
	       atl := nil;
	       knowl := 'false
	    >>  % else [dcfsf_sminsert] has updated [cdr w] destructively.
	 >> else if !*rlsid and (w := dcfsf_dassoc(ir,knowl)) then <<
	    % [ir] says that a derivative of some variable is either
	    % constant or not constant, and all corresponding
	    % information from [knowl] is now in [w].
	    %% ioto_tprin2t {"ir=",ir,", w=",w};
	    h := dcfsf_sminsertd(ir,w);
	    if h eq 'false then <<
	       atl := nil;
	       knowl := 'false
	    >> else if h neq 'true and knowl neq 'false then <<
	       knowl := ir . knowl;
	       for each ir in h do
 		  knowl := delqip(ir,knowl)
	    >>
	 >> else
	    knowl := ir . knowl
      >>;
      return knowl
   end;

procedure dcfsf_plugtheo(atl,knowl);
   begin scalar !*rlsiexpla,subinfo,entry,a,natl,w;
      subinfo := for each ir in knowl join <<
	 w := dcfsf_sderlev car ir;
	 if w then <<
	    entry := cdadr ir;
	    if car entry eq 'equal then
 	       {car w . (cdr w . cdr entry)}
	 >>
      >>;
      while atl do <<
	 a := pop atl;
	 a := dcfsf_plugtheo1(a,subinfo);
	 a := rl_simplat1(a,'and);
	 if a eq 'false then <<
	    natl := 'false;
	    atl := nil
	 >> else if a neq 'true then
	    if rl_op a eq 'and then
	       natl := nconc(reverse rl_argn a,natl)
	    else
	       natl := a . natl
      >>;
      if natl eq 'false then
 	 return natl;
      return reversip natl
   end;

procedure dcfsf_plugtheo1(at,subinfo);
   begin scalar lhs;
      lhs := numr dcfsf_plugtheof(dcfsf_arg2l at,subinfo);
      return dcfsf_0mk2(dcfsf_op at,lhs)
   end;

procedure dcfsf_plugtheof(lhs,subinfo);
   begin scalar c,kq,l,r;
      if domainp lhs then
	 return !*f2q lhs;
      c := dcfsf_plugtheof(lc lhs,subinfo);
      kq := dcfsf_plugtheok(mvar lhs,subinfo);
      l := ldeg lhs;
      r := dcfsf_plugtheof(red lhs,subinfo);
      return addsq(multsq(c,exptsq(kq,l)),r)
   end;

procedure dcfsf_plugtheok(k,subinfo);
   begin scalar v,derlev,w;
      if idp k then <<
 	 v := k;
	 derlev := 0
      >> else if eqcar(k,'d) then <<
	 v := cadr k;
	 derlev := caddr k
      >> else
	 rederr {"dcfsf_plugtheok: bad kernel",k};
      w := atsoc(v,subinfo);
      if not w then
	 return !*k2q k;
      w := cdr w;
      if car w = derlev then
	 return cdr w;
      if car w < derlev then
	 return !*f2q nil;
      return !*k2q k
   end;

procedure dcfsf_dassoc(ir,knowl);
   % Differentially closed field differential assoc. [ir] is an IR,
   % [knowl] is an IRL. Returns an IRL or [nil].
   begin scalar v,w;
      if not (w := dcfsf_sderivp ir) then
	 return nil;
      v := caar w;
      return for each kir in knowl join
	 if (w := dcfsf_sderivp kir) and caar w eq v then
 	    {kir}
   end;

procedure dcfsf_sderivp(ir);
   % Differentially closed field simple derivative predicate. [ir] is an
   % IR. Returns non-[nil] iff [ir] encodes that a derivative of some
   % variable equals a constant or does not equal 0.
   (if w and ww then w . ww)
      where w = dcfsf_sderlev car ir, ww = dcfsf_sderinfo cdr ir;

procedure dcfsf_sderlev(f);
   if not domainp f and not red f and lc f = 1 then
      if idp mvar f then
	 mvar f . 0
      else if eqcar(mvar f,'d) then
	 cadr mvar f . caddr mvar f;

procedure dcfsf_sderinfo(db);
   begin scalar c,le,entry;
      c := t; while c and db do <<
	 le := car db;
      	 entry := cdr le;
      	 if car entry eq 'equal or null numr cdr entry then
	    c := nil
	 else
	    db := cdr db
      >>;
      return db
   end;

procedure dcfsf_smmkatl(op,oldknowl,newknowl,n);
   % Differentially closed field smart simplification make atomic
   % formula list. [op] is one of [and], [or]; [oldknowl] and
   % [newknowl] are IRL's; [n] is an integer. Returns a list of atomic
   % formulas.
   dcfsf_irl2atl(op,newknowl,n);

procedure dcfsf_smdbgetrel(abssq,db);
   if abssq = cddar db then
      cadar db
   else if cdr db then
      dcfsf_smdbgetrel(abssq,cdr db);

procedure dcfsf_at2ir(atf,n);
   % Differentially closed field standard form atomic formula to IR.
   % [atf] is an atomic formula; [n] is an integer. Returns the IR
   % representing [atf] on level [n].
   begin scalar op,par,abs,c;
      op := dcfsf_op atf;
      abs := par := dcfsf_arg2l atf;
      while not domainp abs do abs := red abs;
      par := addf(par,negf abs);
      c := sfto_dcontentf(par);
      par := quotf(par,c);
      abs := quotsq(!*f2q abs,!*f2q c);
      return par . {n . (op . abs)}
   end;

procedure dcfsf_irl2atl(op,irl,n);
   % Differentially closed field standard form IRL to atomic formula
   % list. [irl] is an IRL; [n] is an integer. Returns a list of
   % atomic formulas containing the level-[n] atforms encoded in IRL.
   for each ir in irl join dcfsf_ir2atl(op,ir,n);

procedure dcfsf_ir2atl(op,ir,n);
   (for each le in cdr ir join
      if car le = n then {dcfsf_entry2at(op,cdr le,a)}) where a=!*f2q car ir;

procedure dcfsf_entry2at(op,entry,parasq);
   if !*rlidentify then
      cl_identifyat dcfsf_entry2at1(op,entry,parasq)
   else
      dcfsf_entry2at1(op,entry,parasq);

procedure dcfsf_entry2at1(op,entry,parasq);
   dcfsf_0mk2(dcfsf_clnegrel(car entry,op eq 'and),
      numr addsq(parasq,cdr entry));

procedure dcfsf_sminsert(le,db);
   % Differentially closed field standard form smart simplify insert.
   % [le] is a marked entry; [db] is a database. Returns a database.
   % Destructively inserts [le] into [db].
   begin scalar a,w,scdb,oscdb;
      repeat <<
      	 w := dcfsf_sminsert1(cadr car db,cddr car db,cadr le,cddr le,car le);
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
	 w := dcfsf_sminsert1(cadr a,cddr a,cadr le,cddr le,car le);
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

procedure dcfsf_sminsert1(r1,a,r2,b,n);
   % Differentially closed field standard form smart simplify insert.
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
	 w := dcfsf_smeqtable(r1,r2);
      	 if w eq 'false then return 'false;
	 % [w eq r1]
	 return 'true
      >>;
      if minusf diff then <<
      	 w := dcfsf_smordtable(r1,r2);
	 if atom w then return w;
      	 if eqcar(w,r1) and cdr w then return 'true;
	 return n . (car w . if cdr w then a else b)
      >>;
      w := dcfsf_smordtable(r2,r1);
      if atom w then return w;
      if eqcar(w,r1) and null cdr w then return 'true;
      return n . (car w . if cdr w then b else a)
   end;

procedure dcfsf_sminsertd(newir,oldirl);
   % Returns [true], [false], [nil] or an IRL to be delqip-ed from
   % knowl, which is a superset of [oldirl]. Posssibly updates an IR in
   % [oldirl] in place.
   begin scalar db,entry; integer derlev;
      derlev := cdr dcfsf_sderlev car newir;
      db := cdr newir;
      if cdr db then
	 rederr {"dcfsf_sminsertd: new IR with multiple DB entries ",newir};
      entry := cdar db;
      if eqcar(entry,'equal) then
	 return dcfsf_sminsertd!-equal(derlev,oldirl,numr cdr entry);
      % we know eqcar(entry,'neq)
      return dcfsf_sminsertd!-neq(derlev,oldirl)
   end;

procedure dcfsf_sminsertd!-equal(derlev,oldirl,strongp);
   begin scalar c,oldir,res,db,firstentry,ostrongp; integer oderlev;
      if strongp then
	 derlev := derlev + 1;
      c := t; while not res and oldirl do <<
	 oldir := pop oldirl;
	 oderlev := cdr dcfsf_sderlev car oldir;
	 db := cdr oldir;
	 firstentry := cdr car db;
	 ostrongp := nil;
	 if eqcar(firstentry,'equal) then <<
	    if numr cdr firstentry then <<
	       ostrongp := t;
	       oderlev := oderlev + 1
	    >>;
	    if (ostrongp and oderlev > derlev)
	       or (strongp and derlev > oderlev)
	    then <<
	       res := 'false;
	       c := nil
	    >> else if (strongp and derlev = oderlev)
	       or derlev < oderlev
 	    then
	       % we know [not ostrongp] due to the regular smart
	       % simplification before
	       res := oldir . res
	    else if derlev >= oderlev then <<
	       res := 'true;
	       c := nil
	    >>
	 >> else <<
	    % all [db] entries are of type [neq], and one of them is
	    % [neq 0]
	    if oderlev >= derlev then <<
	       res := 'false;
	       c := nil
	    >>
      	 >>
      >>;
      return res
   end;

procedure dcfsf_sminsertd!-neq(derlev,oldirl);
   begin scalar c,oldir,res,db,firstentry; integer oderlev;
      c := t; while not res and oldirl do <<
	 oldir := pop oldirl;
	 oderlev := cdr dcfsf_sderlev car oldir;
	 db := cdr oldir;
	 firstentry := cdr car db;
	 if eqcar(firstentry,'equal) then <<
	    if numr cdr firstentry then
	       oderlev := oderlev + 1;
	    if derlev >= oderlev then <<
	       res := 'false;
	       c := nil
	    >>
	 >> else <<
	    % all [db] entries are of type [neq], and one of them is
	    % [neq 0]
	    if derlev = oderlev then <<
	       res := 'true;
	       c := nil
	    >>
	 >>
      >>;
      return res
   end;

procedure dcfsf_smeqtable(r1,r2);
   % Differentially closed field standard form smart simplify equal
   % absolute summands table. [r1], [r2] are relations. Returns
   % [false] or a relation $R$ such that $R(f+a,0)$ is equivalent to
   % $[r1](f+a,0) \land [r2](f+a,0)$.
      if r1 eq r2 then r1 else 'false;

procedure dcfsf_smordtable(r1,r2);
   % Differentially closed field standard form smart simplify ordered
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

endmodule;  % [dcfsfsism]

end;  % of file
