module exaux;

% Author: Eberhard Schruefer;

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


fluid '(!*nat);

global '(coord!* basisforml!* keepl!*);

symbolic procedure boundindp(u,v);
   if null u then t else member(car u,v) and boundindp(cdr u,v);

symbolic procedure memblp(u,v);
   if null u then nil
    else if atom u then member(u,v)
    else memblp(car u,v) or memblp(cdr u,v);

symbolic procedure displayframe;
   begin scalar x,scoord;
     terpri!* t;
     scoord := coord!*;
     coord!* := nil;
     for each j in basisforml!* do
       <<x := assoc(j,keepl!*);
         maprin car x;
         prin2!* " = ";
         maprin reval cdr x;
         terpri!* t>>;
%was     varpri(reval cdr x,list mkquote car x,t)>>;
     if !*nat then terpri!* t;
     coord!* := scoord
   end;

put('displayframe,'stat,'endstat);

%symbolic procedure form!*coeff u;
%begin scalar x,inds; %integer n;
 %inds:=cdr u;
 %n:=length inds;
 %x:=simp!* car u;
 %y:=dstrsdf numr x;


 %put('fcoeff,'simpfn,'form!*coeff);


endmodule;

end;
