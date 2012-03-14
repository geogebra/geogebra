
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

install getd(u:variable):generic -> getd1;

install get(u:variable,v:variable):generic -> get;

install prop(u:variable):list -> prop1;

install put(u:variable,v:variable,w:generic):generic -> put;

install lterm(u:poly,v:kernel):poly -> !*lterm;

install sub(u:list,v:generic):generic -> sub!*;

install operator(u:list):noval -> operator1;


% Hooks needed for support of REDUCE 3 operators.

symbolic procedure lterm1(u,v); !*q2f simp lterm(prepf u,v);

symbolic procedure prop1 u; for each j in prop u collect
   if idp j then mkobject(j,'variable)    % Must be a flag.
    else mkobject('list . pair2list j,'generic);

symbolic procedure pair2list u;
   if null u then nil
    else if atom u then list u
    else car u . pair2list cdr u;

symbolic procedure getd1 u; 'list . pair2list getd u;

symbolic procedure operator1 u;
   <<for each j in u do if car j eq 'variable
        then <<flag(list cadr j,'opr); put(cadr j,'simpfn,'simpiden)>>
       else typerr(cadr j,"variable");
     nil>>;

rlistat '(operator);

% !%reduce4();    % This must be final statement!!!!

end;
