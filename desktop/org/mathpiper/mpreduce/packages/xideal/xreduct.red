module xreduct;

% Normal form algorithms

% Author: David Hartley

% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions are met:
%
%    * Redistributions of source code must retain the relevant copyright
%      notice, this list of conditions and the following disclaimer.
%    * Redistributions in binary form must reproduce the above copyright
%      notice, this list of conditions and the following disclaimer in the
%      documentation and/or other materials provided with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
% AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
% THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
% PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNERS OR
% CONTRIBUTORS
% BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
% CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
% SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
% INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
% CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
% ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
% POSSIBILITY OF SUCH DAMAGE.
%



fluid '(!*trxmod !*trxideal xtruncate!*);


infix xmod;
precedence xmod,freeof;
put('xmod,'rtypefn,'getrtypecar);
put('xmod,'listfn,'xmodlist);
put('xmod,'simpfn,'simpxmod);


symbolic procedure simpxmod u;
   % u:{prefix,prefix} -> simpxmod:sq
   begin scalar x;
   if length u neq 2 then
      rerror(xideal,0,"Wrong number of arguments to xmod");
   x := getrlist aeval cadr u;
   return !*pf2sq repartit xreduce(xpartitop car u,
                                   for each g in x join
                                      if g := xpartitop g then {g});
   end;


symbolic procedure xmodlist(u,v);
   % u:{prefix,prefix},v:bool -> xmodlist:prefix
   begin scalar x;
   if length u neq 2 then
      rerror(xideal,0,"Wrong number of arguments to xmod");
   x := getrlist aeval cadr u;
   u := foreach f in getrlist aeval car u collect xpartitop f;
   x := for each f in x join
           if f := xpartitop f then {f};
   return makelist foreach f in u join
      if f := xreduce(f,x) then {!*q2a1(!*pf2sq repartit f,v)};
   end;


infix xmodideal;
precedence xmodideal,freeof;
put('xmodideal,'rtypefn,'getrtypecar);
put('xmodideal,'listfn,'xmodideallist);
put('xmodideal,'simpfn,'simpxmodideal);


symbolic procedure simpxmodideal u;
   % u:{prefix,prefix} -> simpxmodideal:sq
   begin scalar x;
   if length u neq 2 then
      rerror(xideal,0,"Wrong number of arguments to xmodideal");
   x := getrlist aeval cadr u;
   u := xpartitop car u;
   xtruncate!* := xmaxdegree u;
   x := for each f in x join if f := xpartitop f then {f};
   foreach f in x do if not xhomogeneous f then xtruncate!* := nil;
   x := xidealpf x where !*trxmod = nil; % is this desirable?
   return !*pf2sq repartit xreduce(u,x);
   end;


symbolic procedure xmodideallist(u,v);
   % u:{prefix,prefix},v:bool -> xmodideallist:prefix
   begin scalar x;
   if length u neq 2 then
      rerror(xideal,0,"Wrong number of arguments to xmodideal");
   x := getrlist aeval cadr u;
   u := foreach f in getrlist aeval car u collect xpartitop f;
   xtruncate!* := eval('max . foreach f in u collect xmaxdegree f);
   x := for each f in x join if f := xpartitop f then {f};
   foreach f in x do if not xhomogeneous f then xtruncate!* := nil;
   x := xidealpf x where !*trxmod = nil; % is this desirable?
   return makelist foreach f in u join
      if f := xreduce(f,x) then {!*q2a1(!*pf2sq repartit f,v)};
   end;


put('xauto,'rtypefn,'quotelist);
put('xauto,'listfn,'xautolist);

symbolic procedure xautolist(u,v);
   % u:{prefix},v:bool -> xautolist:prefix
   begin scalar x;
   if length u neq 1 then
      rerror(xideal,0,"Wrong number of arguments to xauto");
   u := foreach f in getrlist aeval car u collect xpartitop f;
   return makelist foreach f in xautoreduce u join
      {!*q2a1(!*pf2sq repartit f,v)};
   end;


symbolic procedure xreduce(f,p);
   % f:pf, p:list of pf -> xreduce:pf
   % returns left normal form of f wrt p
   % l contains reduction chain (not used at present).
   begin scalar g,l;
   l := nil . nil;
   if !*trxmod then
    <<writepri(mkquote preppf f,'nil);
      writepri(" =",'last)>>;
   g := xreduce1(f,p,l);
   if !*trxmod then
    <<writepri("   ",'first);
      writepri(mkquote preppf g,'last)>>;
   return g;
   end;


symbolic procedure xreduce1(f,p,l);
   % f:pf, p:list of pf, l:list of {pf,pf} -> xreduce1:pf
   % Returns left normal form of f wrt p. Chain of reducing poly's and
   % cofactors stored in l as side-effect.
   if (f := weak_xreduce1(f,p,l)) then lt f .+ xreduce1(red f,p,l);


symbolic procedure weak_xreduce(f,p);
   % f:pf, p:list of pf, result:pf
   % Returns weak left normal form of f wrt p (i.e. lpow f is
   % irreducible).
   begin scalar g,l;
   l := nil . nil;
   if !*trxmod then
    <<writepri(mkquote preppf f,'nil);
      writepri(" =",'last)>>;
   g := weak_xreduce1(f,p,l);
   if !*trxmod then
    <<writepri("   ",'first);
      writepri(mkquote preppf g,'last)>>;
   return g;
   end;


symbolic procedure weak_xreduce1(f,p,l);
   % f:pf, p:list of pf, l:list of {pf,pf} -> weak_xreduce1:pf
   % Returns weak left normal form of f wrt p (i.e. lpow f is
   % irreducible).
   % Chain of reducing poly's and cofactors stored in l as side-effect.
   begin scalar q,g,h,c,r;
   q := p;
   while f and q do
     begin
     g := car q; q := cdr q;
     if (r := xdiv(xval g,xval f)) then
       begin
       r := !*k2pf mknwedge r;
       h := wedgepf(r,g); % NB: left multiplication here
       c := quotsq(lc f,lc h);
       f := subs2pf addpf(f,multpfsq(h,negsq c));
       if !*trxmod then l := nconc(l,{{multpfsq(r,c),g}});
       if !*trxmod then
        <<writepri("   ",'first);
                 writepri(mkquote
             {'wedge,preppf multpfsq(r,c),preppf g},nil);
                 writepri(" +",'last);>>;
       q := p;
       end;
     end;
   return f;
   end;


symbolic procedure xautoreduce F;
   % F:list of pf -> weak_xautoreduce:list of pf
   % returns autoreduced form of F,
   % sorted in increasing order of leading terms
   xautoreduce1 weak_xautoreduce F;


symbolic procedure xautoreduce1 G;
   % G:list of pf -> xautoreduce1:list of pf
   % G is weakly autoreduced, result is autoreduced and sorted
   begin scalar H;
   H := reversip sort(G,'pfordp); % otherwise need to reduce wrt H too.
   G := {};
   while H do
     begin scalar k;
     k := car H; H := cdr H;
     k := xreduce(k,G);
     if k then G := k . G;
     end;
   return reversip G;
   end;


symbolic procedure weak_xautoreduce F;
   % F:list of pf -> weak_xautoreduce:list of pf
   % returns weakly autoreduced form of F
   weak_xautoreduce1(F,{});


symbolic procedure weak_xautoreduce1(F,G);
   % F,G:list of pf -> weak_xautoreduce1:list of pf
   % G is (weakly) autoreduced, F may be reducible wrt G.
   begin
   while F do
      begin scalar k;
      k := car F; F := cdr F;
      if k := weak_xreduce(k,G) then
        begin
        k := xnormalise k;
        foreach h in G do
           if xdiv(xval k,xval h) then
            <<F := h . F;
              G := delete(h,G)>>;
        G := append(G,{k});
        end;
      end;
   return G;
   end;


% symbolic procedure print_reduction_chain(f,l,g);
%    % f,g:pf, l:list of {pf,pf} -> print_reduction_chain:nil
%    begin
%    writepri(mkquote preppf f,'nil);
%    writepri(" =",'last);
%    foreach pr in cdr l do
%      <<writepri("   ",'first);
%        writepri(mkquote preppf car pr,nil);
%        writepri(mkquote '(wedge " " " "),'nil);
%        writepri("(",'nil);
%        writepri(mkquote preppf cadr pr,nil);
%        writepri(") +",'last);>>;
%    writepri("   ",'first);
%    writepri(mkquote preppf g,'last);
%    end;

endmodule;

end;
