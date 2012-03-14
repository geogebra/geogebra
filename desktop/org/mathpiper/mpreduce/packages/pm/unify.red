module unify;   % Main part of unify code.

% Author: Kevin McIsaac.
% Changes by Rainer M. Schoepf 1991.

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


% The switch semantic, default on, controls use of semantic matching.

fluid '(!*semantic substitution);

switch semantic;

!*semantic := t;

symbolic procedure amatch(r,p,suchl,pmstack);
   if atom r then unify(nil,mval list r,list p,suchl, pmstack)
    else if not(atom p or (car r neq car p)) then
            unify(car r,mval  cdr r, cdr p, suchl, pmstack)
    else if suchp r then amatch(cadr r, p, caddr r . suchl, pmstack)
    else if !*semantic then resume(list('equal,r,p).suchl, pmstack);

symbolic procedure suspend(op,r,p,suchl, pmstack);
   % Process the interrupting operator.
   amatch(car r, car p,suchl,list(op.cdr r,op.cdr p ). pmstack);

symbolic procedure resume(suchl,pmstack);
   % Resume interrupted operator.
   if pmstack then amatch(caar pmstack,cadar pmstack,suchl,cdr pmstack)
    else if chk(suchl) eq t then bsubs substitution;

symbolic procedure unify(op,r,p,suchl,pmstack);
   if null r and null p then resume(suchl,pmstack) % Bottom of arg list.
    else if null(r) then
        <<prin2("UNIFY:pattern over-run for function ");print(op);nil>>
    else if null(p) and not (ident(op ) or mgenp(car r)) then
%       <<prin2("UNIFY:rule over-run for function ");print(op);NIL>>
        nil
    else
      begin scalar mmatch, st, arg, symm, comb, identity,
             mcontract, acontract, expand; integer i, upb;
         if pm!:free(car r) then  suchl := genp(car r).suchl;
         initarg(p);
         while (not(mmatch) and (arg := nextarg(p))) do
            begin
               if not atom(car r)
                 then mmatch := suspend(op,r,arg,suchl, pmstack)
               else if (pm!:free(car r)) then
               begin
                  bind(car r, car arg);
                     if (st := chk suchl) then
                        mmatch := unify(op,mval cdr r,cdr arg,st,
                                        pmstack);
                  unbind(car r);
               end
               else if meq(car r, car arg)
                then mmatch := unify(op,mval cdr r,cdr arg,suchl,
                                     pmstack)
            end;
         return mmatch
       end;

endmodule;

end;
