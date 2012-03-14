module forstat4;   % Definition of REDUCE 4 FOR loops.

% Author: Anthony C. Hearn.

% Copyright (c) 1995 RAND.  All rights reserved.

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


put('for,'n_formfn,'n_formfor);

flag('(go),'non_form);

symbolic procedure top_type u;
   % U is a list of formed expressions.  Result is top type of elements.
   begin scalar v,w;
      v := type car u;
  a:  u := cdr u;
      if null u then return v;
      w := type car u;
      if xtype1(w,v) then go to a
       else if xtype1(v,w) then <<v := w; go to a>>
       else rederr "ugh"
   end;

symbolic procedure n_formfor(u,vars);
   begin scalar action,body,endval,incr,initval,result,testexp,var,x;
         scalar incrtype;
      var := cadr u;
      incr := caddr u;
      incr := list(car incr,cadr incr,caddr incr);
      incrtype := top_type for each j in incr collect n_form1(j,vars);
      action := cadddr u;
      body :=  car cddddr u;
      initval := car incr;
      endval := caddr incr;
      incr := cadr incr;
      x := list('difference,endval,var);
      if incr neq 1 then x := list('times,incr,x);
      x := list('lessp,x,0);
      testexp := x;
      result := gensym();
      x :=
         sublis(list('body2 .
                       list(get(action,'bin),body,result),
               'body3 .
                   body,
               'body . body,
               'initval . initval,
               'nillist . nil,
               'result . result,
               'incrtype . incrtype,
               'initresult . get(action,'initval),
               'resultlist . result,
               'testexp . testexp,
               'updfn . 'plus,
               'updval . incr,
               'var . var),
          if action eq 'do
            then '(rblock ((var . incrtype))
                  (setq var initval)
              lab (cond (testexp (return nil)))
                  body
                  (setq var (updfn var updval))
                  (go lab))
           else if action eq 'collect
            then '(rblock ((var . incrtype) (result . generic)
                          (endptr . generic))
                  (setq var initval)
                  (cond (testexp (return nillist)))
                  (setq result (setq endptr (cons body nil)))
                looplabel
                  (setq var (updfn var updval))
                  (cond (testexp (return resultlist)))
                  (rplacd endptr (cons body nil))
                  (setq endptr (cdr endptr))
                  (go looplabel))
           else if action eq 'conc
            then '(rblock ((var . incrtype) (result . generic)
                          (endptr . generic))
                  (setq var initval)
               startover
                  (cond (testexp (return nillist)))
                  (setq result body)
                  (setq endptr (lastpair resultlist))
                  (setq var (updfn var updval))
                  (cond ((atom endptr) (go startover)))
                looplabel
                  (cond (testexp (return result)))
                  (rplacd endptr body3)
                  (setq endptr (lastpair endptr))
                  (setq var (updfn var updval))
                  (go looplabel))
           else '(rblock ((var . incrtype) (result . generic))
                 (setq var initval)
                 (setq result initresult)
              lab1
                 (cond (testexp (return result)))
                 (setq result body2)
                 (setq var (updfn var updval))
                 (go lab1)));
      return n_form1(x,vars)
   end;

endmodule;

end;
