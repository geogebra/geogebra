module sfrules;  % Rules for definite integration.

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


algebraic;

operator defint,choose;

put('intgggg,'simpfn,'simpintgggg);

SHARE MELLINCOEF$

defint_rules:=

{ defint(~x**(~a),~f1,~f2,~x) => intgggg(choose(f1,x),choose(f2,x),a,x),
  defint(~x,~f1,~f2,~x) => intgggg(choose(f1,x),choose(f2,x),1,x),
  defint(~x**(~a),~f1,~x) => intgggg(choose(f1,x),0,a,x),
  defint(~x,~f1,~x) => intgggg(choose(f1,x),0,1,x),
  defint(~f1,~f2,~x) => intgggg(choose(f1,x),choose(f2,x),0,x),
  defint(~f1,~x) => intgggg(choose(f1,x),0,0,x)};


let defint_rules;

choose_data :=

{ choose(1/e**(~x),~var) => f1(1,x),
  choose(sin(~x),~var)   => f1(2,x),
  choose(Heaviside (1-(~x)),~var) => f1(3,x),
  choose(Heaviside ((~p-~x)/~p),~var) => f1(3,x/p),
  choose(Heaviside ((~x)-1),~var) => f1(4,x),

  choose(~f,~var)        => unknown };  % fallthrough case

let choose_data;

fluid '(mellin!-transforms!* mellin!-coefficients!*);

symbolic (mellin!-transforms!* :=mkvect(200))$

symbolic putv(mellin!-transforms!*,0,'(1 . 1)); % undefined case
symbolic putv(mellin!-transforms!*,1,'(() (1 0 0 1) () (nil) 1 x));
symbolic putv(mellin!-transforms!*,2,'
    (() (1 0 0 2) () ((quotient 1 2) nil)
    (sqrt pi) (quotient (expt x 2) 4)));

    % the Heavisides

symbolic putv(mellin!-transforms!*,3,'(() (1 0 1 1) (1) (nil) 1 x));
symbolic putv(mellin!-transforms!*,4,'(() (0 1 1 1) (1) (nil) 1 x));



symbolic (mellin!-coefficients!* :=mkvect(200))$

symbolic procedure simpintgggg (u);

   begin scalar ff1,ff2,alpha,var,chosen_num,coef;

        ff1 := prepsq simp car u;
        if (cadr u) = 0 then ff2 := '(0 0 x) else
                ff2 := prepsq simp cadr u;
        if (ff1 = 'UNKNOWN) then return simp 'unknown;
        if (ff2 = 'UNKNOWN) then return simp 'unknown;
        alpha := caddr u;
        var := cadddr u;

        chosen_num := cadr ff1;
        put('f1,'g,getv(mellin!-transforms!*,chosen_num));
        coef := getv(mellin!-coefficients!*,chosen_num);
        if coef then MELLINCOEF:= coef else MELLINCOEF :=1;

        chosen_num := cadr ff2;
        put('f2,'g,getv(mellin!-transforms!*,chosen_num));
        coef := getv(mellin!-coefficients!*,chosen_num);
        if coef then MELLINCOEF:= coef * MELLINCOEF ;

       return
        simp list('intgg,list('f1,caddr ff1), list('f2,caddr ff2),
                  alpha,var);
   end;

 % some rules which let the results look more convenient ...

algebraic <<

 for all z let sinh(z) = (exp (z) - exp(-z))/2;
 for all z let cosh(z) = (exp (z) + exp(-z))/2;
>>;

endmodule;

end;



