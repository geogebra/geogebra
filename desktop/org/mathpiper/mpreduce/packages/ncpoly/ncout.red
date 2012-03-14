module nout; % Output of noncom polynomials.

% Author: H. Melenk, ZIB Berlin, J. Apel, University of Leipzig

% Copyright: Konrad-Zuse-Zentrum Berlin, 1994

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


symbolic procedure nc_compact u;
 % write a polynomial in factored form.
 begin scalar vl,t1,t2,y,r,d,w;
  vl := intersection(kord!*,for each y in ncpi!-names!* collect car y);
  for each x in vl do
    <<y:=gensym();t1:=(x.y).t1;t2:=(y.x).t2>>;
  w:=simp u where !*factor=nil,!*factors=nil,!*exp=t;
  d:=denr w;
  r:=nc_compactr(numr w,reverse vl,t1,t2);
  return mk!*sq (r./d)end;

symbolic procedure nc_compactr(u,vl,t1,t2);
  begin scalar x,xn,y,q,w,r,s;
    integer n,m;
  x:=car vl; vl := cdr vl;
  w:=nc_compactd u;
  n:=-1;
loop:if null w then goto done;
  n:=n+1;
  xn:=if n=0 then 1 else x .** n .* 1 .+ nil;
  q:=nc_compactx(w,x,xn);
  w:=cdr q;q:=car q;
  if q then
  begin scalar !*factor,!*exp;
     if null vl or null cdr vl or 2>
       <<m:=0;for each y in vl do if smember(y,q) then m:=m+1;m>>
     then
      <<q:='plus.for each s in q collect prepf sublis(t1,s);
        !*factor:=t;
        q:=reorder sublis(t2,numr simp reval1(q,nil))>>
     else
      <<s:=nil; for each f in q do s:=addf(s,f);
        q:=nc_compactr(s,vl,t1,t2)>>;
     r:=addf(multf(q,xn),r)end;
  goto loop;
done:return r end;

symbolic operator nc_compact;

symbolic procedure nc_compactd u;
  % convert standard form into list (=sum) of monomials.
    if domainp u then {u} else
    append(for each s in nc_compactd lc u collect lpow u .* s .+nil,
           red u and nc_compactd red u);

symbolic procedure nc_compactx(u,x,xn);
  % Extract sum of terms which contain multiples of power xn. Divide xn out.
 begin scalar yes,no,w;
   for each r in u do
    if xn=1 and not smember(x,r) then yes:=r.yes
       else
    if (w:=quotf(r,xn)) and not smember(x,w) then yes:=w.yes else no:=r.no;
   return yes.no end;

endmodule;;end;
