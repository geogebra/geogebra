module groext; % author: Herbert Melenk, ZIB Berlin.
               % version 3: removal of the return value 'superfluous' and
               %  switching to 'groebnerf'.
               % version 4: extending ALL bases, which do not reduce the
               %  polynomial to zero; 'groext11' has now a list for any
               %  new polynmial with a '1', if the polynomial is not reduced
               %  to zero by the basis; otherwise it has a '0'.
               % version 5: determine the subcases by Groebner base
               %  computaions.

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


create!-package('(groext),'(contrib groebner));

load!-package 'groebner;put('groext,'psopfn,'groexteval);

fluid'(groext11);groext11:='(list);share groext11;

symbolic procedure groexteval u;
begin scalar gg,ll,v;
 !*groebopt:=nil;
 if not(2=length u) then
  rerror(groext,1,"groext: illegal number of parameters.");
 gg:=reval car u;
 if not eqcar(gg,'list) then
  rerror(groext,2,"groext: first parameter must be a list of lists.");
 gg:=cdr gg;ll:=reval cadr u;
 if not eqcar(ll,'list) then
  rerror(groext,3,"groext: second parameter must be a list.");
 ll:=for each lll in cdr ll collect reval{'num,lll};
 v:=groext1(gg,ll);
 return if null u then 'empty else if v=t then car u else 'list.v end;

symbolic procedure groext1(gg,ll);
begin scalar a,aa,b,bb,c,ii,l;
 l:=length ll;
 gg:=for each ggg in gg collect ggg.for each gggg in ggg collect gggg;
 groext11:=nil;
 for each lll in ll do
 <<c:='list.for each ggg in gg collect
  <<a:=preduceeval{lll,car ggg};
   if a=0 then 0 else<<cdr ggg:=nconc(cdr ggg,{a});1>> >>;
  groext11:=c.groext11>>;
 groext11:='list.reversip groext11;
 for each ggg in gg do ii:=nconc(groext3 cdr ggg,ii);
 if null ii then return nil;
% for each iii in ii do if null groext2(iii,ii) then jj:=iii.jj
%  else ii:=deletip(iii,ii);
 a:=ii;
aa:if null a then go to cc;aa:=car a;a:=cdr a;b:=ii;
bb:if null b then go to aa;bb:=car b;b:=cdr b;
 if groext2(aa,bb)then<<ii:=delete(bb,ii);a:=delete(bb,a)>>;go to bb;
cc:return reversip ii end;

symbolic procedure groext2(a,b);
% Test, if the Groebner basis 'a' describes a subproblem of one of
% the Groebner basis 'b'; return 't' then. Otherwise return 'nil'.
if a eq b then nil else
begin scalar !*groebfac;
 !*groebfac:=t;return if b=cadr groebner1(append(b,cdr a),nil,nil)then t
    else nil end;

fluid'(!*groebfac);

symbolic procedure groext3 a;
% Simulate "Groebner a;".
begin scalar b,!*groebfac;!*groebfac:=t;b:=groebner1(a,nil,nil);
 return if b='(list(list 1))then nil else cdr b end;

endmodule;;end;
