module loops;  % Looping forms other than the FOR statement.

% Author: Anthony C. Hearn

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

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


fluid '(!*blockp);

global '(cursym!*);


% ***** REPEAT STATEMENT *****

symbolic procedure repeatstat;
  begin scalar !*blockp,body,bool;
        if flagp('until,'delim) then bool := t
         else flag('(until),'delim);
        body:= xread t;
        if not bool then remflag('(until),'delim);
        if not(cursym!* eq 'until) then symerr('repeat,t);
        return list('repeat,body,xread t);
   end;

symbolic macro procedure repeat u;
   begin scalar body,bool,lab;
        body := cadr u; bool := caddr u;
        lab := gensym();
        return mkprog(nil,list(lab,body,
                list('cond,list(list('not,bool),list('go,lab)))))
   end;

put('repeat,'stat,'repeatstat);

flag('(repeat),'nochange);

symbolic procedure formrepeat(u,vars,mode);
   begin scalar !*!*a2sfn;
      !*!*a2sfn := 'aeval!*;
      return list('repeat,formc(cadr u,vars,mode),
                  formbool(caddr u,vars,mode))
   end;

put('repeat,'formfn,'formrepeat);


% ***** WHILE STATEMENT *****

symbolic procedure whilstat;
   begin scalar !*blockp,bool,bool2;
        if flagp('do,'delim) then bool2 := t else flag('(do),'delim);
        bool := xread t;
        if not bool2 then remflag('(do),'delim);
        if not(cursym!* eq 'do) then symerr('while,t);
        return list('while,bool,xread t)
   end;

symbolic macro procedure while u;
   begin scalar body,bool,lab;
        bool := cadr u; body := caddr u;
        lab := gensym();
        return mkprog(nil,list(lab,list('cond,list(list('not,bool),
                list('return,nil))),body,list('go,lab)))
   end;

put('while,'stat,'whilstat);

flag('(while),'nochange);

symbolic procedure formwhile(u,vars,mode);
   begin scalar !*!*a2sfn;
      !*!*a2sfn := 'aeval!*;
      return list('while,formbool(cadr u,vars,mode),
                  formc(caddr u,vars,mode))
   end;

put('while,'formfn,'formwhile);

endmodule;

end;



