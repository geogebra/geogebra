module defintc;

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


fluid '(mellin!-transforms!* mellin!-coefficients!*);

symbolic (mellin!-transforms!* :=mkvect(200))$

symbolic putv(mellin!-transforms!*,0,'(1 . 1)); % undefined case
symbolic putv(mellin!-transforms!*,1,'(() (1 0 0 1) () (nil) 1 x));

     % trigonometric functions

symbolic putv(mellin!-transforms!*,2,'
    (() (1 0 0 2) () ((quotient 1 2) nil)
    (sqrt pi) (quotient (expt x 2) 4)));

symbolic putv(mellin!-transforms!*,25,'
    (() (1 0 0 2) () ((quotient 1 2) nil)
    (minus (sqrt pi)) (quotient (expt x 2) 4)));

symbolic putv(mellin!-transforms!*,3,'
    (() (1 0 0 2) () (nil (quotient 1 2))
   (sqrt pi) (quotient (expt x 2) 4)));

symbolic putv(mellin!-transforms!*,7,'
    (() (2 0 2 2) (1 1) (nil (quotient 1 2))
    (quotient (sqrt pi) 2) (expt x 2)));

symbolic putv(mellin!-transforms!*,8,'
    (() (0 2 2 2) ((quotient 1 2) 1) (nil nil)
    (quotient (sqrt pi) 2) (expt x 2)));

symbolic putv(mellin!-transforms!*,9,'
    (() (1 2 2 2) ((quotient 1 2) 1) ((quotient 1 2) nil)
    (quotient 1 2) (expt x 2)));

     % hyperbolic functions

symbolic putv(mellin!-transforms!*,10,'
    (() (1 0 1 3) (1) ((quotient 1 2) 1 nil)
    (expt pi (quotient 3 2)) (quotient (expt x 2) 4)));

symbolic putv(mellin!-transforms!*,11,'
    (() (1 0 1 3) ((quotient 1 2)) (nil (quotient 1 2) (quotient 1 2))
    (expt pi (quotient 3 2)) (quotient (expt x 2) 4)));


     % the Heavisides

symbolic putv(mellin!-transforms!*,30,'(() (1 0 1 1) (1) (nil) 1 x));

symbolic putv(mellin!-transforms!*,31,'(() (0 1 1 1) (1) (nil) 1 x));

symbolic putv(mellin!-transforms!*,32,'
    (() (2 0 2 2) (1 1) (nil nil) -1 x));

symbolic putv(mellin!-transforms!*,33,'
    (() (0 2 2 2) (1 1) (nil nil) 1 x));

symbolic putv(mellin!-transforms!*,34,'
    (() (1 2 2 2) (1 1) (1 nil) 1 x));

symbolic putv(mellin!-transforms!*,35,'
    (() (2 1 2 2) (nil 1) (nil nil) 1 x));


     % exponential integral

symbolic putv(mellin!-transforms!*,36,'
    (() (2 0 1 2) (1) (nil nil) -1 x));

     % sin integral

symbolic putv(mellin!-transforms!*,37,'
    (() (1 1 1 3) (1) ((quotient 1 2) nil nil)
    (quotient (sqrt pi) 2) (quotient (expt x 2) 4)));

     % cos integral

symbolic putv(mellin!-transforms!*,38,'
    (() (2 0 1 3) (1) (nil nil (quotient 1 2))
    (quotient (sqrt pi) -2) (quotient (expt x 2) 4)));

     % sinh integral

symbolic putv(mellin!-transforms!*,39,'
    (() (1 1 2 4) (1 nil) ((quotient 1 2) nil nil nil)
    (quotient (expt pi (quotient 3 2)) -2) (quotient (expt x 2) 4)));


     % error functions

symbolic putv(mellin!-transforms!*,41,'
    (() (1 1 1 2) (1) ((quotient 1 2) nil)
    (quotient 1 (sqrt pi)) (expt x 2)));

symbolic putv(mellin!-transforms!*,42,'
    (() (2 0 1 2) (1) (nil (quotient 1 2))
    (quotient 1 (sqrt pi)) (expt x 2)));

    % Fresnel integrals

symbolic putv(mellin!-transforms!*,43,'
    (() (1 1 1 3) (1) ((quotient 3 4) nil (quotient 1 4))
    (quotient 1 2) (quotient (expt x 2) 4)));


symbolic putv(mellin!-transforms!*,44,'
    (() (1 1 1 3) (1) ((quotient 1 4) nil (quotient 3 4))
    (quotient 1 2) (quotient (expt x 2) 4)));

    % gamma function

symbolic putv(mellin!-transforms!*,45,'
    ((n) (1 1 1 2) (1) (n nil) 1 x));

    % Bessel functions

symbolic putv(mellin!-transforms!*,50,'
    ((n) (1 0 0 2) () ((quotient n 2) (minus (quotient n 2)))
    1 (quotient (expt x 2) 4)));

symbolic putv(mellin!-transforms!*,51,'
    ((n) (2 0 1 3) ((quotient (minus (plus n 1)) 2))
((quotient n 2) (minus (quotient n 2)) (quotient (minus (plus n 1)) 2))
    1 (quotient (expt x 2) 4)));

symbolic putv(mellin!-transforms!*,52,'
    ((n) (1 0 1 3) ((plus (quotient 1 2) (quotient n 2)))
    ((quotient n 2) (minus (quotient n 2))
    (plus (quotient 1 2) (quotient n 2)))
    pi (quotient (expt x 2) 4)));

symbolic putv(mellin!-transforms!*,53,'
    ((n) (2 0 0 2) () ((quotient n 2) (minus (quotient n 2)))
    (quotient 1 2) (quotient (expt x 2) 4)));

    % struve functions

symbolic putv(mellin!-transforms!*,54,'
    ((n) (1 1 1 3) ((quotient (plus n 1) 2))
    ((quotient (plus n 1) 2) (minus (quotient n 2)) (quotient n 2))
    1 (quotient (expt x 2) 4)));

symbolic putv(mellin!-transforms!*,55,'
    ((n) (1 1 2 4) ((quotient (plus n 1) 2) nil)
    ((quotient (plus n 1) 2) nil (quotient n 2) (minus (quotient n 2)))
    (times (minus pi) (sec (times (quotient (minus n) 2) pi)))
    (quotient (expt x 2) 4)));

    % legendre polynomials

symbolic putv(mellin!-transforms!*,56,'
    ((n) (2 0 2 2) ((minus n) (plus n 1)) (nil nil)
    1 (quotient (plus x 1) 2)));

symbolic putv(mellin!-transforms!*,57,'
    ((n) (0 2 2 2) (1 1) ((minus n) (plus n 1))
    1 (quotient (plus x 1) 2)));

    % chebyshev polymomials

symbolic putv(mellin!-transforms!*,58,'
    ((n) (2 0 2 2)
    ((difference (quotient 1 2) n) (plus (quotient 1 2) n))
    (nil (quotient 1 2)) (sqrt pi) (quotient (plus x 1) 2)));

symbolic putv(mellin!-transforms!*,59,'
    ((n) (0 2 2 2) (nil (quotient 1 2)) (n (minus n))
    (sqrt pi) (quotient (plus x 1) 2)));

symbolic putv(mellin!-transforms!*,60,'
    ((n) (2 0 2 2)
    ((plus (quotient 3 2) n) (difference (minus (quotient 1 2)) n))
    (nil (quotient 1 2)) (quotient (plus n 1) (times 2 (sqrt pi)))
    (quotient (plus x 1) 2)));

symbolic putv(mellin!-transforms!*,61,'
    ((n) (0 2 2 2) ((quotient 3 2) 2) ((minus n) (plus n 2))
    (quotient (plus n 1) (times 2 (sqrt pi)))
    (quotient (plus x 1) 2)));

    % hermite polynomials

symbolic putv(mellin!-transforms!*,62,'
    ((n) (1 0 1 2) (plus (quotient n 2) 1)
    ((difference (quotient n 2) (quotient n 2))
    (difference (quotient 1 2) (difference (quotient n 2)
    (quotient n 2))))
    (times (expt (minus 1) (quotient n 2)) (sqrt pi) (factorial n))
    (expt x 2)));

    % laguerre polynomials

symbolic putv(mellin!-transforms!*,63,'
    ((n l) (1 0 1 2) ((plus n 1)) (0 (minus l))
    (gamma (plus l n 1)) x));

    % gegenbauer polynomials

symbolic putv(mellin!-transforms!*,64,'
    ((n l) (2 0 2 2) ((plus l n (quotient 1 2))
    (difference (quotient 1 2) (quotient 1 n)))
    (0 (difference (quotient 1 2) l))
   (quotient (times 2 l (gamma (plus l (quotient 1 2)))) (factorial n))
    (quotient (plus x 1) 2)));

symbolic putv(mellin!-transforms!*,65,'
    ((n l) (0 2 2 2) ((plus l (quotient 1 2)) (times 2 l))
    ((minus n) (plus (times 2 l) n))
   (quotient (times 2 l (gamma (plus l (quotient 1 2)))) (factorial n))
    (quotient (plus x 1) 2)));

    % jacobi polynomials

symbolic putv(mellin!-transforms!*,66,'
    ((n r s) (2 0 2 2) ((plus r n 1) (difference (minus s) n))
    (0 (minus s)) (quotient (gamma (plus r n 1)) (factorial n))
    (quotient (plus x 1) 2)));

symbolic putv(mellin!-transforms!*,67,'
    ((n r s) (0 2 2 2) ((plus r 1) (plus r s 1))
    ((minus n) (plus r s n 1))
    (quotient (gamma (plus r n 1)) (factorial n))
    (quotient (plus x 1) 2)));

symbolic (mellin!-coefficients!* :=mkvect(200))$

endmodule;

end;



