module trialdiv;  % Trial division routines.

% Authors: Mary Ann Moore and Arthur C. Norman.

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


fluid '(!*trint intvar loglist tanlist);

exports countz,findsqrts,findtrialdivs,trialdiv,simp,mksp;

imports !*multf,printsf,quotf;

symbolic procedure countz dl;
% DL is a list of S.F.s;
    begin         scalar s,n,rl;
loop2:  if null dl then return arrangelistz rl;
        n:=1;
loop1:  n:=n+1;
        s:=car dl;
        dl:=cdr dl;
        if not null dl and (s eq car dl) then
            go to loop1
        else rl:=(s.n).rl;
        go to loop2
    end;

symbolic procedure arrangelistz d;
    begin         scalar n,s,rl,r;
        n:=1;
        if null d then return rl;
loopd:  if (cdar d)=n then s:=(caar d).s
        else r:=(car d).r;
        d:=cdr d;
        if not null d then go to loopd;
        d:=r;
        rl:=s.rl;
        s:=nil;
        r:=nil;
        n:=n+1;
        if not null d then go to loopd;
        return reversip rl
    end;

symbolic procedure findtrialdivs zl;
   % zl is list of kernels found in integrand. result is a list
   % giving things to be treated specially in the integration
   % namely, exps and tans.
   % Result is list of form ((a . b) ...)
   % with a a kernel and car a=expt or tan
   % and b a standard form for either expt or (1+tan**2).
   begin scalar dlists1,args1;
      for each z in zl do
         if exportan z
           then <<if car z eq 'tan
                    then <<args1 := (mksp(z,2) .* 1) .+ 1;
                           tanlist := (args1 ./ 1) . tanlist>>
                   else args1 := !*kk2f z;  % z is not unique here.
                  dlists1 := (z . args1) . dlists1>>;
      return dlists1
   end;

symbolic procedure exportan dl;
    if atom dl then nil
     else begin
    % extract exp or tan fns from the z-list.
        if eq(car dl,'tan) then return t;
   nxt: if not eq(car dl,'expt) then return nil;
        dl:=cadr dl;
%       if atom dl then return t;
%       if atom dl or constant_exprp dl then return t;
        if atom dl or not smember(intvar,dl) then return t;
% Make sure we find nested exponentials?
        go to nxt
    end;

symbolic procedure findsqrts z;
    begin  scalar r;
        while not null z do <<
            if eqcar(car z,'sqrt) then r:=(car z) . r;
            z:=cdr z >>;
        return r
    end;

symbolic procedure trialdiv(x,dl);
    begin         scalar qlist,q;
    while not null dl do
        if not null(q:=quotf(x,cdar dl)) then <<
            if (caaar dl='tan) and not eqcar(qlist,cdar dl) then
                loglist:=('iden . simp cadr caar dl) . loglist;
                         %tan fiddle!
            qlist:=(cdar dl).qlist;
            x:=q >>
        else dl:=cdr dl;
    return qlist.x
    end;

endmodule;

end;
