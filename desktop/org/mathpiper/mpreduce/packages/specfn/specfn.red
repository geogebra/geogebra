module specfn;  % Special functions package for REDUCE.

% Author:  Chris Cannam, Sept-Nov 1992.
%          Winfried Neun, Nov 1992 ...
%          contribution from various authors ...

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


%  ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||  %
%                                                                %
%     Please report bugs to Winfried Neun,                       %
%                           Konrad-Zuse-Zentrum                  %
%                              fuer Informationstechnik Berlin,  %
%                           Heilbronner Str. 10                  %
%                           10711 Berlin - Wilmersdorf           %
%                           Federal Republic of Germany          %
%     or by email, neun@sc.ZIB-Berlin.de                         %
%                                                                %
%  ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||  %
%                                                                %
%     This package provides algebraic and numeric                %
%     manipulations upon various special functions:              %
%                                                                %
%              -- Bernoulli Numbers                              %
%              -- Gamma Function                                 %
%              -- Pochhammer Notation                            %
%              -- Digamma (Psi) Function and Derivatives         %
%              -- Riemann Zeta Function                          %
%              -- Bessel Functions J, Y, I and K                 %
%              -- Airy Functions                          %
%              -- Hankel Functions H1 and H2                     %
%              -- Kummer Hypergeometric Functions M and U        %
%              -- Struve, Lommel and Whittaker Functions         %
%              -- Integral funtions, Si, Ci, s_i (=si), Ei,...   %
%              -- Simplification of Factorials                   %
%              -- Solid and Spherical Harmonics                    %
%              -- Jacobi Elliptic Functions                        %
%              -- Elliptic Integrals                              %
%                                                                %
%     accessible through the new operators Bernoulli, Gamma,     %
%     Pochhammer, Psi, Polygamma, Zeta, BesselJ, BesselY,        %
%     BesselI, BesselK, Hankel1, Hankel2, KummerM, KummerU,      %
%     AiryAi, AiryBi, AiryAiPrime, AiryBiPrime,                  %
%     Elliptic{sn,cn,dn...}, Elliptic{E,F,K...}
%     Beta, StruveL, StruveH, Lommel1, Lommel2, WhittakerM       %
%     and WhittakerW, with the new switch SaveSFs.               %
%                                                                %
%  ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||  %


create!-package ('(specfn sfconsts sfgen sfbern dilog sfbinom sfpolys
                   sfsums simpfact harmonic jsymbols recsimpl sfellip
                   sfellipi sfint),
                 '(contrib specfn));

exports sq2bf!*, c!:prec!:;

switch savesfs;
on savesfs;


symbolic smacro procedure mksqnew u;
  !*p2f(car fkern(u) .* 1) ./ 1;

symbolic fluid '(bernoulli!-alist new!*bfs bf!*base sf!-alist !*savefs);

symbolic ( bernoulli!-alist := nil );
symbolic ( sf!-alist        := nil );

symbolic ( new!*bfs := fluidp '!:bprec!: );
symbolic ( bf!*base := (if new!*bfs then 2 else 10) );
symbolic ( if not globalp 'log2of10 then
               << global '(log2of10); log2of10 := 3.32193 >> );

symbolic smacro procedure sq2bf!*(x);
   (if fixp x then i2bf!: x
      else ((if car y neq '!:rd!: then retag cdr !*rn2rd y
               else retag cdr y) where y = !*a2f x));

symbolic smacro procedure c!:prec!:;
   (if new!*bfs then lispeval '!:bprec!: else !:prec!:);


% These functions are needed in other modules.

algebraic procedure complex!*on!*switch;
   if not symbolic !*complex then
      if symbolic !*msg then
         << off msg;
            on complex;
            on msg >>
      else on complex
   else t;

algebraic procedure complex!*off!*switch;
   if symbolic !*complex then
      if symbolic !*msg then
         << off msg; off complex; on msg >>
      else off complex
   else t;

algebraic procedure complex!*restore!*switch(fl);
   if not fl then
      if symbolic !*msg then
         << off msg;
            if symbolic !*complex then
               off complex
            else on complex;
            on msg >>
      else if symbolic !*complex then
            off complex
         else on complex;

%algebraic operator besselJ,besselY,besselI,besselK,hankel1,hankel2;
%algebraic (operator kummerM, kummerU, struveh, struvel
%                  ,lommel1, lommel2 ,whittakerm, whittakerw,
%                   Airy_Ai, Airy_Bi,Airy_AiPrime,Airy_biprime);

defautoload_operator(besselj,specbess);
defautoload_operator(bessely,specbess);
defautoload_operator(besseli,specbess);
defautoload_operator(besselk,specbess);
defautoload_operator(hankel1,specbess);
defautoload_operator(hankel2,specbess);
defautoload_operator(kummerM,specbess);
defautoload_operator(kummerU,specbess);
defautoload_operator(struveh,specbess);
defautoload_operator(struvel,specbess);
defautoload_operator(lommel1,specbess);
defautoload_operator(lommel2,specbess);
defautoload_operator(whittakerm,specbess);
defautoload_operator(whittakerw,specbess);
defautoload_operator(Airy_Ai,specbess);
defautoload_operator(Airy_Bi,specbess);
defautoload_operator(Airy_AiPrime,specbess);
defautoload_operator(Airy_biprime,specbess);

defautoload_operator(gamma,sfgamma);
defautoload_operator(igamma,sfgamma);
defautoload_operator(polygamma,sfgamma);
defautoload_operator(psi,sfgamma);
defautoload_operator(ibeta,sfgamma);
defautoload_operator(beta,sfgamma);
defautoload_operator(pochhammer,sfgamma);
defautoload_operator(zeta,sfgamma);

endmodule;

end;




