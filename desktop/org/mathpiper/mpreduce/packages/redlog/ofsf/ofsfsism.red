% ----------------------------------------------------------------------
% $Id: ofsfsism.red 1813 2012-11-02 13:14:39Z thomas-sturm $
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
   fluid '(ofsf_sism_rcsid!* ofsf_sism_copyright!*);
   ofsf_sism_rcsid!* :=
      "$Id: ofsfsism.red 1813 2012-11-02 13:14:39Z thomas-sturm $";
   ofsf_sism_copyright!* := "Copyright (c) 1995-2009 A. Dolzmann and T. Sturm"
>>;

module ofsfsism;
% Ordered field standard form smart simplification. Submodule of [ofsf].

procedure ofsf_smwupdknowl(op,atl,knowl,n);
   % Ordered field standard form smart simplification wrapper update
   % knowledge.
   if !*rlsusi then
      cl_susiupdknowl(op,atl,knowl,n)
   else
      ofsf_smupdknowl(op,atl,knowl,n);

procedure ofsf_smwrmknowl(knowl,v);
   if !*rlsusi then
      ofsf_susirmknowl(knowl,v)
   else
      ofsf_smrmknowl(knowl,v);

procedure ofsf_smwcpknowl(knowl);
   if !*rlsusi then
      cl_susicpknowl(knowl)
   else
      ofsf_smcpknowl(knowl);

procedure ofsf_smwmkatl(op,knowl,newknowl,n);
   if !*rlsusi then
      cl_susimkatl(op,knowl,newknowl,n)
   else
      ofsf_smmkatl(op,knowl,newknowl,n);

% The black boxes are rl_smsimpl!-impl and rl_smsimpl!-equiv1 are set
% correctly for both the regular smart simplifier and for susi.

%DS
% <irl> ::= (<ir>,...)
% <ir> ::= <para> . <db>
% <db> ::= (<le>,...)
% <le> ::= <label> . <entry>
% <label> ::= <integer>
% <entry> ::= <of relation> . <standard quotient>

procedure ofsf_smrmknowl(knowl,v);
   % Ordered field standard form remove from knowledge. [knowl] is an
   % IRL; [v] is a variable. Returns an IRL. Destructively removes any
   % information about [v] from [knowl].
   if null knowl then
      nil
   else if v member kernels caar knowl then
      ofsf_smrmknowl(cdr knowl,v)
   else <<
      cdr knowl := ofsf_smrmknowl(cdr knowl,v);
      knowl
   >>;

procedure ofsf_smcpknowl(knowl);
   for each ir in knowl collect
      car ir . append(cdr ir,nil);

procedure ofsf_smupdknowl(op,atl,knowl,n);
   % Ordered field standard form update knowledge. [op] is one of
   % [and], [or]; [atl] is a list of (simplified) atomic formulas;
   % [knowl] is a conjunctive IRL; [n] is the current level. Returns
   % an IRL. Destructively updates [knowl] wrt. the [atl] information.
   begin scalar w,ir,a;
      while atl do <<
	 a := if op eq 'and then car atl else ofsf_negateat car atl;
	 atl := cdr atl;
	 ir := ofsf_at2ir(a,n);
	 if w := assoc(car ir,knowl) then <<
	    cdr w := ofsf_sminsert(cadr ir,cdr w);
	    if cdr w eq 'false then <<
	       atl := nil;
	       knowl := 'false
	    >>  % else [ofsf_sminsert] has updated [cdr w] destructively.
	 >> else
	    knowl := ir . knowl
      >>;
      return knowl
   end;

procedure ofsf_smmkatl(op,oldknowl,newknowl,n);
   % Ordered field standard form make atomic formula list. [op] is one
   % of [and], [or]; [oldknowl] and [newknowl] are IRL's; [n] is an
   % integer. Returns a list of atomic formulas. Depends on switch
   % [rlsipw].
   if op eq 'and then
      ofsf_smmkatl!-and(oldknowl,newknowl,n)
   else  % [op eq 'or]
      ofsf_smmkatl!-or(oldknowl,newknowl,n);

procedure ofsf_smmkatl!-and(oldknowl,newknowl,n);
   begin scalar w;
      if not !*rlsipw and !*rlsipo then
	 return ofsf_irl2atl('and,newknowl,n);
      return for each ir in newknowl join <<
	 w := atsoc(car ir,oldknowl);
	 if null w then ofsf_ir2atl('and,ir,n) else ofsf_smmkatl!-and1(w,ir,n)
      >>;
   end;

procedure ofsf_smmkatl!-and1(oir,nir,n);
   begin scalar w,parasq;
      parasq := !*f2q car nir;
      return for each le in cdr nir join
      if car le = n then <<
	 if cadr le memq '(lessp greaterp) and
 	    (w := ofsf_smmkat!-and2(cdr oir,cdr le,parasq))
 	 then
	    {w}
	 else
	    {ofsf_entry2at('and,cdr le,parasq)}
      >>
   end;

procedure ofsf_smmkat!-and2(odb,ne,parasq);
   % Ordered field standard form smart simplify make atomic formula.
   % [odb] is a DB; [ne] is an entry with its relation being one of
   % [lessp], [greaterp]; [parasq] is a numerical SQ. Returns an
   % atomic formula.
   begin scalar w;
      w := ofsf_smdbgetrel(cdr ne,odb);
      if w eq 'neq then
	 (if !*rlsipw then <<
      	    if car ne eq 'lessp then
 	       return ofsf_entry2at('and,'leq . cdr ne,parasq);
	    % We know [car ne eq 'greaterp].
	    return ofsf_entry2at('and,'geq . cdr ne,parasq)
      	 >>)
      else if w memq '(leq geq) then
	 if not !*rlsipo then
 	    return ofsf_entry2at('and,'neq . cdr ne,parasq)
   end;

procedure ofsf_smmkatl!-or(oldknowl,newknowl,n);
   begin scalar w;
      return for each ir in newknowl join <<
	 w := atsoc(car ir,oldknowl);
	 if null w then ofsf_ir2atl('or,ir,n) else ofsf_smmkatl!-or1(w,ir,n)
      >>;
   end;

procedure ofsf_smmkatl!-or1(oir,nir,n);
   begin scalar w,parasq;
      parasq := !*f2q car nir;
      return for each le in cdr nir join
      if car le = n then <<
	 if cadr le memq '(lessp greaterp equal) and
 	    (w := ofsf_smmkat!-or2(cdr oir,cdr le,parasq))
 	 then
	    {w}
	 else
	    {ofsf_entry2at('or,cdr le,parasq)}
      >>
   end;

procedure ofsf_smmkat!-or2(odb,ne,parasq);
   begin scalar w;
      w := ofsf_smdbgetrel(cdr ne,odb);
      if w eq 'neq then
	 (if not !*rlsipw then <<
      	    if car ne eq 'lessp then
 	       return ofsf_entry2at('or,'leq . cdr ne,parasq);
	    % We know [car ne eq 'greaterp]!
	    return ofsf_entry2at('or,'geq . cdr ne,parasq)
      	 >>)
      else if w memq '(leq geq) then <<
	 if car ne memq '(lessp greaterp) then
	    return ofsf_entry2at('or,'neq . cdr ne,parasq);
      	 % We know [car ne eq 'equal].
	 if !*rlsipo then
	    return ofsf_entry2at('or,ofsf_anegrel w . cdr ne,parasq)
      >>
   end;

procedure ofsf_smdbgetrel(abssq,db);
   if abssq = cddar db then
      cadar db
   else if cdr db then
      ofsf_smdbgetrel(abssq,cdr db);

procedure ofsf_at2ir(atf,n);
   % Ordered field standard form atomic formula to IR. [atf] is an
   % atomic formula; [n] is an integer. Returns the IR representing
   % [atf] on level [n].
   begin scalar op,par,abs,c;
      op := ofsf_op atf;
      abs := par := ofsf_arg2l atf;
      while not domainp abs do abs := red abs;
      par := addf(par,negf abs);
      c := sfto_dcontentf(par);
      par := quotf(par,c);
      abs := quotsq(!*f2q abs,!*f2q c);
      return par . {n . (op . abs)}
   end;

procedure ofsf_irl2atl(op,irl,n);
   % Ordered field standard form IRL to atomic formula list. [irl] is
   % an IRL; [n] is an integer. Returns a list of atomic formulas
   % containing the level-[n] atforms encoded in IRL.
   for each ir in irl join ofsf_ir2atl(op,ir,n);

procedure ofsf_ir2atl(op,ir,n);
   (for each le in cdr ir join
      if car le = n then {ofsf_entry2at(op,cdr le,a)}) where a=!*f2q car ir;

procedure ofsf_entry2at(op,entry,parasq);
   if !*rlidentify then
      cl_identifyat ofsf_entry2at1(op,entry,parasq)
   else
      ofsf_entry2at1(op,entry,parasq);

procedure ofsf_entry2at1(op,entry,parasq);
   ofsf_0mk2(ofsf_clnegrel(car entry,op eq 'and),numr addsq(parasq,cdr entry));

procedure ofsf_sminsert(le,db);
   % Ordered field standard form smart simplify insert. [le] is a
   % marked entry; [db] is a database. Returns a database.
   % Destructively inserts [le] into [db].
   begin scalar a,w,scdb,oscdb;
      repeat <<
      	 w := ofsf_sminsert1(cadr car db,cddr car db,cadr le,cddr le,car le);
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
	 w := ofsf_sminsert1(cadr a,cddr a,cadr le,cddr le,car le);
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

procedure ofsf_sminsert1(r1,a,r2,b,n);
   % Ordered field standard form smart simplify insert. [r1], [r2] are
   % relations, [a], [b] are absolute summands in SQ representation;
   % [n] is the current level. Returns [nil], [false], [true], or a
   % marked entry. Simplification of $\alpha=[r2](f+b,0)$ under the
   % condition $\gamma=[r1](f+a,0)$ is considered: [nil] means there
   % is no simplification posssible; [true] means that $\gamma$
   % implies $\alpha$; [false] means that $\alpha$ contradicts
   % $\gamma$; the atomic formula encoded by a resulting marked entry
   % wrt. $f$ is equivalent to $\alpha$ under $\gamma$.
   begin scalar w,diff,n;
      diff := numr subtrsq(a,b);
      if null diff then <<
	 w := ofsf_smeqtable(r1,r2);
      	 if w eq 'false then return 'false;
	 if r1 eq w then return 'true;
	 return n . (w . a)
      >>;
      if minusf diff then <<
      	 w := ofsf_smordtable(r1,r2);
	 if atom w then return w;
      	 if eqcar(w,r1) and cdr w then return 'true;
	 return n . (car w . if cdr w then a else b)
      >>;
      w := ofsf_smordtable(r2,r1);
      if atom w then return w;
      if eqcar(w,r1) and null cdr w then return 'true;
      return n . (car w . if cdr w then b else a)
   end;

procedure ofsf_smeqtable(r1,r2);
   % Ordered field standard form smart simplify equal absolute
   % summands table. [r1], [r2] are relations. Returns [false] or a
   % relation $R$ such that $R(f+a,0)$ is equivalent to $[r1](f+a,0)
   % \land [r2](f+a,0)$.
   begin scalar al;
      al := '((equal . ((equal . equal) (neq . false) (geq . equal)
           (leq . equal) (greaterp . false) (lessp . false)))
        (neq . ((neq . neq) (geq . greaterp) (leq . lessp)
           (greaterp . greaterp) (lessp . lessp)))
        (geq . ((geq . geq) (leq . equal) (greaterp . greaterp)
           (lessp . false)))
        (leq . ((leq . leq) (greaterp . false) (lessp . lessp)))
        (greaterp . ((greaterp . greaterp) (lessp . false)))
        (lessp . ((lessp . lessp))));
      return cdr (atsoc(r2,atsoc(r1,al)) or atsoc(r1,atsoc(r2,al)))
   end;

procedure ofsf_smordtable(r1,r2);
   % Ordered field standard form smart simplify ordered absolute
   % summands table. [r1], [r2] are relations. Returns [nil], which
   % means that no simplification is possible, [false] or a pair $R .
   % s$ where $R$ is a relation and $s$ is one of [T], [nil]. For
   % absolute summands $a<b$ we have $[r1](f+a,0) \land [r2](f+b,0)$
   % equivalent to $R(f+a,0)$ in case $[s]=[T]$ or to $R(f+b,0)$ in
   % case $[s]=[nil]$.
   begin scalar al;
      al := '((equal . ((equal . false) (neq . (equal . T)) (geq . (equal .T))
            (leq . false) (greaterp . (equal . T)) (lessp . false)))
         (neq . ((equal . (equal . nil)) (neq . nil) (geq . nil)
            (leq . (leq . nil)) (greaterp . nil) (lessp . (lessp . nil))))
         (geq . ((equal . false) (neq . (geq . T)) (geq . (geq . T))
            (leq . false) (greaterp . (geq . T)) (lessp . false)))
         (leq . ((equal . (equal . nil)) (neq . nil) (geq . nil)
            (leq . (leq . nil)) (greaterp . nil) (lessp . (lessp . nil))))
         (greaterp . ((equal . false) (neq . (greaterp . T))
            (geq . (greaterp . T)) (leq . false) (greaterp . (greaterp . T))
            (lessp . false)))
         (lessp . ((equal . (equal . nil)) (neq . nil) (geq . nil)
            (leq . (leq . nil)) (greaterp . nil) (lessp . (lessp . nil)))));
      return cdr atsoc(r2,atsoc(r1,al))
   end;

% Orderd field standard form part of susi.

procedure ofsf_susirmknowl(knowl,v);
   % Ordered field susi remove knowledge. [knowl] is a KNOWL; [v] is a
   % variable. Returns a KNOWL. Remove all information about [v] from
   % [knowl].
   for each p in knowl join
      if v memq ofsf_varlat car p then nil else {p};

procedure ofsf_susibin(old,new);
   % Orderd field standard form susi binary smart simplification.
   % [old] and [new] are LAT's. Returns ['false] or a SUSIPRG. We
   % assume that [old] is a part of a already existence KNOWL and new
   % has to be added to this KNOWL.
   begin scalar w,x;
      if !*rlsusiadd then <<
      	 w := ofsf_susibinad(old,new);
      	 if w eq 'false then
	    return 'false
      >>;
      if !*rlsusimult then <<
      	 x := ofsf_susibinmult(old,new);
      	 if x eq 'false then
	    return 'false;
	 w := nconc(w,x)
      >>;
      return w
   end;

procedure ofsf_susibinmult(old,new);
   % Ordered field standard forms susi binary smart simplification
   % multiplicative case. [old] and [new] are LAT's. Returns ['false]
   % or a SUSIPRG. We assume that [old] is a part of a already
   % existence KNOWL and new has to be added to this KNOWL.
   begin scalar w,ot,nt,orel,nrel,olevel,nlevel;
      ot := ofsf_arg2l car old;
      nt := ofsf_arg2l car new;
      orel := ofsf_op car old;
      nrel := ofsf_op car new;
      olevel := cdr old;
      nlevel := cdr new;
      w := quotf(ot,nt);
      if w = 1 then  % [ot] = [nt]
	 return nil;
      if w then  % [nt] | [ot]
	 return ofsf_susibinmult1(orel,nrel,ot,nt,w,olevel,nlevel,T);
      w := quotf(nt,ot);
      if w then
	 return ofsf_susibinmult1(nrel,orel,nt,ot,w,nlevel,olevel,nil);
      return nil
   end;

procedure ofsf_susibinmult1(pr,fr,prod,af,cf,plevel,flevel,flg);
   % Ordered field standard form susi binary smart simplification
   % multiplicative part subroutine. [pr] is the relation of the
   % product; [fr] is the realtion of the factor; [prod] is the
   % product; [af] and [cf] are factors of [prod]; [flg] is boolean.
   % If [flg] is [nil] then the factor is contained in the theory,
   % otherwise the product is contained in the theory.
   begin scalar w;
      w := ofsf_susibinmulttab(fr,pr);
      if not w then return nil;
      w := cdr w;
      if not w then return nil;
      if atom w then <<
	 if w eq 'false then
	    return 'false;
      	 if w eq 'ign1 then  % The factor can be ignored
	    return  { ('ignore . flg) };
	 if w eq 'ign2 then  % The product can be ignored
	    return { ('ignore . not flg) }
      >>;
      if ofsf_wop fr then
      	 return { '(ignore . T), '(ignore . nil),
	    ('add . (ofsf_0mk2(car w,af) . cl_susiminlevel(plevel,flevel))),
	    ('add . (ofsf_0mk2(cdr w,cf) . plevel))}
      else % The factor is necessary
	 return { ('ignore . not flg),
	    ('add . (ofsf_0mk2(cdr w,cf) . plevel))}
   end;

procedure ofsf_wop(rel);
   rel memq '(leq geq);

procedure ofsf_susibinmulttab(u,uv);
   begin scalar al;
      al := '(
	 (equal . ( (equal . ign2) (leq . ign2) (geq . ign2)
	            (neq . false) (greaterp . false) (lessp . false)))
         (leq   . ( (equal . nil) (leq . nil) (geq . nil) (neq . (lessp . neq))
                    (greaterp . (lessp . lessp)) (lessp . (lessp . greaterp))))
         (geq .   ( (equal . nil) (leq . nil) (geq . nil)
                    (neq . (greaterp . neq)) (greaterp . (greaterp . greaterp))
                    (lessp . (greaterp . lessp))))
         (neq .   ( (equal . (neq . equal)) (leq . nil) (geq . nil)
                    (neq . (neq . neq)) (greaterp . ign1) (lessp . ign1)))
         (lessp . ( (equal . (lessp . equal)) (leq . (lessp . geq))
                    (geq . (lessp . leq)) (neq . (lessp . neq))
                    (lessp . (lessp . greaterp)) (greaterp . (lessp . lessp))))
         (greaterp . ( (equal . (greaterp . equal)) (leq . (greaterp . leq))
                    (geq . (greaterp . geq)) (neq . (greaterp . neq))
                    (lessp . (greaterp . lessp))
                    (greaterp . (greaterp . greaterp)))));
      return atsoc(uv,atsoc(u,al))
   end;

procedure ofsf_susibinad(old,new);
   begin scalar od,nd,level;
      level := cl_susiminlevel(cdr old,cdr new);
      old := car old;
      new := car new;
      if ofsf_arg2l old = ofsf_arg2l new then
	 return ofsf_susibineq(ofsf_arg2l old,ofsf_op old,ofsf_op new,level);
      od := ofsf_susidec(ofsf_arg2l old);
      nd := ofsf_susidec(ofsf_arg2l new);
      if car od = car nd then
	 return ofsf_susibinord(ofsf_op old,ofsf_arg2l old,cdr od,
	    ofsf_op new,ofsf_arg2l new,cdr nd,level);
      return nil;
   end;

procedure ofsf_susibineq(u,oop,nop,level);
   begin scalar w;
      w := ofsf_smeqtable(oop,nop);
      if w eq 'false then
	 return 'false
      else if w eq oop then
	 return '((delete . T))
      else if w eq nop then
	 return {'(delete . nil)}
      else
	 return {'(delete . nil), '(delete . T),
	    'add . (ofsf_0mk2(w,u) . level)};
   end;

procedure ofsf_susidec(u);
   % Decompose. [u] is a SF. Returns a pair $(p . a)$, where $p$ is a
   % SF, and $a$ is SQ. $p$ is the parametric part of [u] and $a$ is
   % the absolut part of [u].
   begin scalar par,absv,c;
      absv := u;
      while not domainp absv do absv := red absv;
      par := addf(u,negf absv);
      c := sfto_dcontentf(par);
      par := quotf(par,c);
      absv := quotsq(!*f2q absv,!*f2q c);
      return par . absv;
   end;

procedure ofsf_susibinord(orel,ot,oabs,nrel,nt,nabs,level);
   begin scalar w,diff;
      diff := numr subtrsq(oabs,nabs);
      if minusf diff then <<
      	 w := ofsf_smordtable(orel,nrel);
	 if atom w then return w;
      	 if eqcar(w,orel) and cdr w then return '((ignore . T));
	 if cdr w then
	    return {'(ignore . nil),
	       'add . (ofsf_0mk2(car w,ot) . level)}
	 else
	    return {'(ignore . nil)}
      >>;
      w := ofsf_smordtable(nrel,orel);
      if atom w then return w;
      if eqcar(w,orel) and null cdr w then return '((ignore . T));
      if cdr w then
      	 return {'(ignore . nil)}
      else
	 return {'(ignore . nil),
	    'add . (ofsf_0mk2(car w,ot) . level)}
   end;

procedure ofsf_susipost(atl,knowl);
   % Ordered field standad form susi post simplification. [atl] is a
   % list of atomic formulas. [knowl] is a KNOWL. Returns a list
   % $\lambda$ of atomic formulas, such that
   % $\bigwedge[knowl]\land\bigwedge\lambda$ is equivalent to
   % $\bigwedge[knowl]\and\bigwedge[atl]$
   if !*rlsusigs then
      ofsf_susigs(atl,knowl)
   else atl;

procedure ofsf_susigs(atl,knowl);
   % Ordered field standard form susi Groebner simplification. [atl]
   % is a list of atomic formulas; [knowl] is a KNOWL. Returns a list
   % of atomic formulas. The conjunction over [atl] is simplified wrt.
   % the theory [knowl] with the Groebner simplifier.
   begin scalar w,theo,!*rlsiexpla,!*rlsiexpl;
      atl := for each at in atl collect rl_negateat(at);
      w := rl_smkn('or,atl);
      theo := for each x in knowl collect car x;
      w := ofsf_gssimplify0(w,theo);
      if w eq 'inctheo then
	 return 'inctheo;
      if rl_tvalp w then
	 return cl_flip w;
      if cl_atfp w then
	 return {rl_negateat w};
      w := for each at in rl_argn w collect rl_negateat(at);
      return w
   end;

procedure ofsf_susitf(at,knowl);
   % Orderd field standard form susi transform. [at] is an atomic
   % formula, [knowl] is a knowledge. Returns an atomic formula
   % $\alpha$ such that $\alpha\land\bigwedge[knowl]$ is equivalent to
   % $[at]\land\bigwedge[knowl]$. $\alpha$ has possibly a more
   % convenient relation than [at].
   begin scalar r,s;
      r := ofsf_op at;
      s := ofsf_arg2l at;
      if (r eq 'geq and assoc(ofsf_0mk2('leq,s),knowl)) or
	 (r eq 'leq and assoc(ofsf_0mk2('geq,s),knowl))
      then
	 return ofsf_0mk2('equal,s);
      if not (r eq 'lessp or r eq 'greaterp) then
	 return at;
      if !*rlsipw and assoc(ofsf_0mk2('neq,s),knowl) then
	 return ofsf_0mk2(ofsf_canegrel('leq,r eq 'lessp),s);
      if !*rlsipo then
	 return at;
      if (r eq 'lessp and assoc(ofsf_0mk2('leq,s),knowl)) or
	 (r eq 'greaterp and assoc(ofsf_0mk2('geq,s),knowl))
      then
	 return ofsf_0mk2('neq,s);
      return at
   end;

endmodule;  % [ofsfsism]

end;  % of file
