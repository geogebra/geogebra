module vecanlys;

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


%author: Eberhard Schruefer;

symbolic procedure basis u;
   cofram(for each j in u collect cdr j,nil);

rlistat '(basis);

symbolic procedure simpgrad u;
   simp!*('d . u);

put('grad,'simpfn,'simpgrad);

symbolic procedure simpcurl u;
   simp!* list('hodge,'d . u);

put('curl,'simpfn,'simpcurl);

symbolic procedure simpdiv u;
   simp!* list('hodge,list('d,'hodge . u));

put('div,'simpfn,'simpdiv);

newtok '((!. !* !.) crossprod);
infix crossprod;

symbolic procedure simpcrossprod u;
   simp!* list('hodge,'wedge . u);

put('crossprod,'simpfn,'simpcrossprod);

symbolic procedure simpdotprod u;
   simp!* list('hodge,list('wedge,car u,list('hodge,cadr u)));

put('cons,'simpfn,'simpdotprod);

symbolic procedure hodge3dpri u;
   %converts the form notation to vector notation for output;
   if caar u eq 'd then
        if eqcar(cadar u,'hodge) then maprin('div . cdadar u)
         else maprin('curl . cdar u)
    else if caar u eq 'wedge then
              if eqcar(cadar u,'hodge) then
                     inprint('cons,0,cdadar u)
               else inprint('crossprod,0,cdar u);

endmodule;

end;
