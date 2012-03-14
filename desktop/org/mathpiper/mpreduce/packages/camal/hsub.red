module hsub;

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


%% Harmonic substitution: the CAMAL HSUB operation, as well as other
%% substitutions.

fluid '(!*trharm);
switch trham;

symbolic procedure hsub1(x,u,v,A,n);
%% Substitute v+A for u in x to order n
begin scalar ans, c, tmp, fs!:zero!-generated;
%%    fs!:zero!-generated := 0;
    ans := fs!:subang(x, u, v);
%    c := ensure!-fourier A;
    c := car A;
    if c then c := cdr c;
    A := c;
    if !*trham
      then << print "A"; if null A then print 0 else fs!:prin A >>;
    for i:=1:n do <<
        if !*trham then << print "i="; print i >>;
        x := hdiff(x, u);
        if !*trham then << prin2!* "df(x,u,i)="; fs!:prin x; terpri!* t;
                prin2!* "A^i ="; fs!:prin c; terpri!* t >>;
        c := fs!:times(cdr !*sq2fourier (1 ./ i), c);
        if !*trham
          then << prin2!* "A^i/fact(i) ="; fs!:prin c; terpri!* t>>;
        tmp := fs!:times(fs!:subang(x, u, v), c);
        if !*trham then <<
            prin2!* "f'(0)*A^i/fact i = "; fs!:prin tmp; terpri!* t>>;
        ans := fs!:plus(ans, tmp);
        if !*trham
          then << prin2!* "partial sum ="; fs!:prin ans; terpri!* t>>;
        if not(i=n) then c := fs!:times(c,A);
    >>;
    return ans
end;

symbolic procedure fs!:subang(x, u, v);
if null x then nil
else begin scalar vv, n;
    vv := mkvect 7;
    n := getv!.unsafe(fs!:angle x, u);
    for i:=0:7 do if i = u then putv!.unsafe(vv, i, n*getv!.unsafe(v,i))
        else putv!.unsafe(vv, i,
                getv!.unsafe(fs!:angle x,i) + n*getv!.unsafe(v,i));
    return fs!:plus(fs!:subang(fs!:next x, u, v),
                    make!-term(fs!:fn x, vv, fs!:coeff x));
end;

symbolic procedure fs!:sub(x,u);
if null x then nil else
begin scalar ans;
    ans := aeval prepsq fs!:coeff x;
    if not fixp ans then ans := subsq(cadr ans, u)
    else ans := fs!:coeff x;
    if eqcar(numr ans, '!:fs!:) then ans := cdar ans
        else ans := cdr !*sq2fourier ans;
    ans := fs!:times(make!-term(fs!:fn x, fs!:angle x, 1 ./ 1), ans);
    return fs!:plus(fs!:sub(fs!:next x, u), ans);
end;

symbolic procedure simphsub uu;
begin scalar x, u, v, vv, A, n, dmode!*;
    dmode!* := '!:fs!:;
    if (length uu = 5) then <<
        x := car uu; uu := cdr uu;
        u := car uu; uu := cdr uu;
        v := car uu; uu := cdr uu;
        A := car uu; uu := cdr uu;
        n := car uu
    >>
    else if (length uu = 3) then <<
        x := car uu; uu := cdr uu;
        u := car uu; uu := cdr uu;
        v := car uu; uu := cdr uu;
        if not harmonicp u then <<
            A := ( ((get('fourier, 'tag) .
                         fs!:sub(cdar simp x, list(u . v))) ./ 1)
            )  where wtl!*=delasc(u,wtl!*);
            return A;
        >>;
        A := 0;
        n := 0
    >>;
    if not harmonicp u then
        rerror(fourier, 7, "Not an angle in HSUB");
    x := cdar simp x;
    if not angle!-expression!-p v then
        rerror(fourier, 8, "Not an angle expression in HSUB");
    vv := mkvect 7;
    for i:=0:7 do putv!.unsafe(vv,i,0);
    compile!-angle!-expression(v, vv);
    A := simp!* A;
    n := simp!* n;
    if null car n then n := 0 ./ 1
    else if not(fixp car n and cdr n = 1)  then
        rerror(fourier, 9, "Non integer expansion in HSUB");
    n := car n;
    return (get('fourier, 'tag) .
           hsub1(x,get(u,'fourier!-angle),vv,A,n)) ./ 1;
end;

put('hsub, 'simpfn, 'simphsub);

endmodule;

end;
