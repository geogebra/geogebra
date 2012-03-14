% ----------------------------------------------------------------------
% $Id: gbsc.red 84 2009-02-07 07:53:22Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2003-2009 Andreas Dolzmann and Lorenz Gilch
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
   fluid '(gbsc_rcsid!* gbsc_copyright!*);
   gbsc_rcsid!* := "$Id: gbsc.red 84 2009-02-07 07:53:22Z thomas-sturm $";
   gbsc_copyright!* := "Copyright (c) 2003-2009 A. Dolzmann and L. Gilch"
>>;

module gbsc;
% Groebner bases combined structure constants.

fluid '(!*cgbverbose cgb_hashsize!*);

procedure gbsc_strconst(rt,gb,n);
   % Parametric real root counting structure constant. [rt] is a list
   % of TERM's; [gb] is a list of VDP's; [n] is an integer. Returns a
   % BETA, containing the generalized combined structure constants.
   begin scalar w,g,ul,beta; integer l;
      ul := reversip vdp_lsort gbsc_vdpsetprod(rt,n);
      beta := gbsc_betainit();
      if !*cgbverbose then <<
         l := length ul;
         ioto_tprin2t "Combined structure constants:"
      >>;
      for each u in ul do <<
         if !*cgbverbose then <<
            if remainder(l,10) = 0 then
               ioto_prin2 {"[",l,"] "};
            l := l - 1
         >>;
         if u member rt then
            for each v in rt do
               beta := gbsc_betaset(beta,u,v,if u=v then simp 1 else simp 0)
         else if (w := gbsc_hmmember(u,gb)) then <<
            %g := car w;
            g := gb_reduce(u,gb);
            for each v in rt do
               beta := gbsc_betaset(beta,u,v,
                  %negsq quotsq(gbsc_getlincombc(v,g),vdp_lbc g))
		  gbsc_getlincombc(v,g))
         >>
         else <<
            w := gbsc_goodfctr(u,rt);
            for each v in rt do
               beta := gbsc_betaset(beta,u,v,
                  gbsc_sumbeta(beta,car w,cdr w,v,rt))
         >>
      >>;
      if !*cgbverbose then
         ioto_prin2t "done";
      return beta
   end;

procedure gbsc_vdpsetprod(vdpl,n);
   % Parametric real root countig VDP set product. [vdpl] is a list of
   % VDP's. Returns a list of VDP's $v_1 v_2... v_n$ with $v_i$ in
   % $[vdpl]$.
   begin scalar prodl;
      if n = 1 then
         return vdpl;
      for each x in gbsc_vdpsetprod(vdpl,n-1) do
         for each y in vdpl do
            prodl := lto_insert(vdp_prod(x,y),prodl);
      return prodl
   end;

procedure gbsc_hmmember(u,gb);
   % Parametric real root counting head monomial member. [u] is a VDP
   % representing a monomial; [gb] is a list of VDP's. Returns [nil],
   % if there is no $f$ in [gb] with $[u]=HM(f)$ else returns a list
   % of VDP's such that $[u]=HM(g)$ for the first VDP $g$.
   begin scalar htu;
      htu := vdp_evlmon u;
      while gb and vdp_evlmon car gb neq htu do
         gb := cdr gb;
      return gb
   end;

procedure gbsc_getlincombc(b,p);
   % Parametric real root counting get linear combination coefficient.
   % [b] is a TERM an element of a basis of $K[X_1,...,X_n]/I$; [p] is
   % a VDP, an eleemnt of $K[X_1,...,X_n]/I$. Returns an SQ, the
   % coefficient of [b] in [p].
   begin scalar bt;
      b := vdp_poly b;
      p := vdp_poly p;
      bt := dip_evlmon b;
      while not null p and dip_evlmon p neq bt do
         p := dip_mred p;
      if null p then
         return simp 0;
      return bc_2sq dip_lbc p
   end;

procedure gbsc_goodfctr(u,rt);
   % Parametric real root counting good factorization. [u] is a VDP
   % representing a term; [rt] is a list of VDP's representing terms,
   % too. Write $[u]=u'X_i$ such that $u'$ is not in [rt]. Returns a
   % pair $(u' . X_i ) with $u'$ and $X_i$ are VDP's.
   begin scalar htu,fctr,cand,candt,n,i;
      htu := vdp_evlmon u;
      n := length htu;
      i := 1;
      while i <= n do <<
         candt := for each x in htu collect x;  % TODO: Muesste nach EV.
         if nth(candt,i) > 0 then <<
            nth(candt,i) := nth(candt,i) - 1;  % TODO; Muesste nach EV.
            cand := vdp_fmon(simp 1,candt);
            if not (cand member rt) then <<
               fctr := cand . vdp_fmon(simp 1,gbsc_mkvar(i,n));  % TODO Abbruch
               i := n + 1
            >>
         >>;
         i := i + 1
      >>;
      if i neq n + 2 then rederr {"bug in gbsc_goodfctr"};
      return fctr
   end;

procedure gbsc_mkvar(i,n);  % TODO nach EV.
   % Parametric real root counting make variable. [i] and [n] are
   % integers, such that [i] is between 1 and [n]. Returns an EV,
   % representing $X_1$ in the polynomial ring $K[X_1,...,X_n]$.
   begin scalar m;
      for j := 1:i-1 do
         m := 0 . m;
      m := 1 . m;
      for j := i+1:n do
         m := 0 . m;
      return reversip m
   end;

procedure gbsc_sumbeta(beta,up,xi,v,rt);
   % Parametric real root counting sum beta. [beta] is a BETA; [up],
   % [xi], and [a] are VDP's; [rt] is a list of VDP's. Returns a SQ,
   % the sum $sum_{w\in [rt], w<[up]}
   % \beta_{[up]w}}beta_{(w[xi])[v]}$.
   begin scalar res,betaupline;
      res := simp 0;
      betaupline := gbsc_betagetline(beta,up);
      for each w in rt do
         if ev_compless!?(vdp_evlmon w,vdp_evlmon up) then
            res := addsq(res,multsq(gbsc_betalineget(betaupline,w),
               gbsc_betaget(beta,vdp_prod(w,xi),v)));
      return res
   end;


% endmodule;

% module prrcbeta;

% Parametric real root counting beta. Implements an efficient data structue for
% storing generalized combined structure constants.

%DS BETA
% BETA represents a $m\times n% matrix indexed by TERM's. We organize
% BETA as an hashtable for all lines of beta. Each hash table entry is
% an alist mapping the line index to a matrix line. The matrix lines
% are simply organized as ALISTS, mapping the column index to the
% entry. All entries are SQ's. Note that in our case $m$ is
% $|RT(I)|^3$ and $n$ is $|RT(I)|$, and therefore we have in general
% $m>>n$.

procedure gbsc_betainit();
   % Parametric real root counting beta init. [m], [n] are INTEGERS;
   % Returns an empty BETA $\beta$.
   mkvect(cgb_hashsize!* - 1);

procedure gbsc_betaset(beta,u,v,sc);
   % Parametric real root counting beta set. [beta] is a BETA; [u] and
   % [v] are VDP's; [sc] is a SQ. Returns a BETA, the updated and
   % inplace modiefied [beta]. Stores the generalized combined
   % structure constant [sc] of [u] and [v] in [beta]. It is forbidden
   % to overwrite an existing entry in [beta].
   begin scalar w,i,slot;
      i := gbsc_hashfunction u;
      slot := getv(beta,i);
      if null slot then <<
         putv(beta,i,{u . {v . sc}});
         return beta
      >>;
      w := assoc(u,slot);
      if null w then <<
         putv(beta,i,(u . {v . sc}) . slot);
         return beta
      >>;
      if not assoc(v,cdr w) then
	 cdr w := (v . sc) . cdr w
      else
         rederr "bug in gbsc_betaset (gbsc_strconst)";
      return beta
   end;

procedure gbsc_hashfunction(term);
   % Parametric real root counting hash functions. [term] is a TERM.
   % Returns an integer between 0 and [cgb_hashsize!*].
   begin integer w;
      for each x in vdp_evlmon term do
         w := 10*w + x;  % TODO: remainder
      return remainder(w,cgb_hashsize!*)
   end;

procedure gbsc_betagetline(beta,u);
   % Parametric real root counting beta getline. [beta] is a BETA; [u]
   % is a VDP. Returns the line of [beta] which is indexed by [u].
   begin scalar w;
      w := assoc(u,getv(beta,gbsc_hashfunction u));
      if null w then rederr "bug in gbsc_betagetline";
      return cdr w
   end;

procedure gbsc_betalineget(betaline,v);
   % Parametric real root counting beta line get. [betaline] is a line
   % of a BETA; [v] is a VDP. Returns a SQ, the entry of betaline
   % indexed by [v].
   begin scalar w;
      w := atsoc(v,betaline);
      if null w then rederr "bug in gbsc_betalineget";
      return cdr w
   end;

procedure gbsc_betaget(beta,u,v);
   % Parametric real root counting betaget. [beta] is a BETA; [u] and
   % [v] are VDP's. Returns a SQ the entry of [beta] indexed by [u]
   % and [v].
   begin scalar w;
      w := assoc(u,getv(beta,gbsc_hashfunction u));
      if null w then rederr "bug in gbsc_betaget (1)";
      w := atsoc(v,cdr w);
      if null w then rederr "bug in gbsc_betaget (2)";
      return cdr w
   end;

endmodule;  [gbsc]

end; % of file
