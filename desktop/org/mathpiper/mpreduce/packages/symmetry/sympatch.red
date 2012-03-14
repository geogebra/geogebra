module sympatch;
% from rprint.red
load!_package 'rprint;

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


fluid '(!*n buffp combuff!* curmark curpos orig pretop pretoprinf rmar);

symbolic procedure rprint u;
   begin integer !*n; scalar buff,buffp,curmark,rmar,x;
      curmark := 0;
      buff := buffp := list list(0,0);
      rmar := linelength nil;
      x := get('!*semicol!*,pretop);
      !*n := 0;
      mprino1(u,list(caar x,cadar x));
    %  prin2ox ";";
      prin2ox "$"; %3.11 91 KG
      omarko curmark;
      prinos buff
   end;

% error in treatment of roots in connection
% with conjugate of complex numbers

symbolic procedure reimexpt u;
   if cadr u eq 'e
     then addsq(reimcos list('cos,reval list('times,'i,caddr u)),
                multsq(simp list('minus,'i),
                    reimsin list('sin,reval list('times,'i,caddr u))))
    else if fixp cadr u and cadr u > 0
              and eqcar(caddr u,'quotient)
              and fixp cadr caddr u
              and fixp caddr caddr u
     then mksq(u,1)
    else addsq(mkrepart u,multsq(simp 'i,mkimpart u));

put('expt,'cmpxsplitfn,'reimexpt);
put('cos,'cmpxsplitfn,'reimcos);
put('sin,'cmpxsplitfn,'reimsin);
endmodule;

% algebraic repart(pi):=pi;   % Present in 3.4.1 and later versions.
% algebraic impart(pi):=0;

% error in treatment of roots in connection
% with conjugate of complex numbers
% end;

end;
