module definte;

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


algebraic <<

laplace2_rules :=

{ laplace2(1/~x,~f1,~x) => int(1/x*f1*e^(-s*x),x,0,infinity),
  laplace2(1/~x^(~a),~f1,~x) => int(1/x^a*f1*e^(-s*x),x,0,infinity),
  laplace2(1/sqrt(~x),~f1,~x)=> int(1/sqrt(x)*f1*e^(-s*x),x,0,infinity),
  laplace2(1/(sqrt(~x)*~x),~f1,~x) =>
                 int(1/(sqrt(x)*x)*f1*e^(-s*x),x,0,infinity),
  laplace2(1/(sqrt(~x)*~x^~a),~f1,~x) =>
                 int(1/(sqrt(x)*x^a)*f1*e^(-s*x),x,0,infinity),
  laplace2(~x^~a,~f1,~x) => int(x^a*f1*e^(-s*x),x,0,infinity),
  laplace2(~x,~f1,~x) => int(x*f1*e^(-s*x),x,0,infinity),
  laplace2(sqrt(~x),~f1,~x) => int(sqrt(x)*f1*e^(-s*x),x,0,infinity),
  laplace2(sqrt(~x)*~x,~f1,~x)=>int(sqrt(x)*x*f1*e^(-s*x),x,0,infinity),
  laplace2(sqrt(~x)*~x^~a,~f1,~x) =>
                    int(sqrt(x)*x^a*f1*e^(-s*x),x,0,infinity),
  laplace2(~b,~f1,~x) => int(b*f1*e^(-s*x),x,0,infinity),
  laplace2(~f1,~x) => int(f1*e^(-s*x),x,0,infinity)

};

let laplace2_rules;

hankel2_rules :=

{ hankel2(1/~x,~f1,~x) =>
                int(1/x*f1*besselj(n,2*(s*x)^(1/2)),x,0,infinity),
  hankel2(1/~x^(~a),~f1,~x) =>
                int(1/x^a*f1*besselj(n,2*(s*x)^(1/2)),x,0,infinity),
  hankel2(1/sqrt(~x),~f1,~x) =>
                int(1/sqrt(x)*f1*besselj(n,2*(s*x)^(1/2)),x,0,infinity),
  hankel2(1/(sqrt(~x)*~x),~f1,~x) =>
            int(1/(sqrt(x)*x)*f1*besselj(n,2*(s*x)^(1/2)),x,0,infinity),
  hankel2(1/(sqrt(~x)*~x^~a),~f1,~x) =>
          int(1/(sqrt(x)*x^a)*f1*besselj(n,2*(s*x)^(1/2)),x,0,infinity),
  hankel2(~x^~a,~f1,~x) =>
          int(x^a*f1*besselj(n,2*(s*x)^(1/2)),x,0,infinity),
  hankel2(~x,~f1,~x) => int(x*f1*besselj(n,2*(s*x)^(1/2)),x,0,infinity),
  hankel2(sqrt(~x),~f1,~x) =>
          int(sqrt(x)*f1*besselj(n,2*(s*x)^(1/2)),x,0,infinity),
  hankel2(sqrt(~x)*~x,~f1,~x) =>
          int(sqrt(x)*x,f1,besselj(n,2*(s*x)^(1/2)),x,0,infinity),
  hankel2(sqrt(~x)*~x^~a,~f1,~x) =>
          int(sqrt(x)*x^a*f1*besselj(n,2*(s*x)^(1/2)),x,0,infinity),
  hankel2(~b,~f1,~x) => int(b*f1*besselj(n,2*(s*x)^(1/2)),x,0,infinity),
  hankel2(~f1,~x) => int(f1*besselj(n,2*(s*x)^(1/2)),x,0,infinity)
};

let hankel2_rules;

Y_transform2_rules :=

{ Y_transform2(1/~x,~f1,~x) =>
                      int(1/x*f1*bessely(n,2*(s*x)^(1/2)),x,0,infinity),
  Y_transform2(1/~x^(~a),~f1,~x) =>
                    int(1/x^a*f1*bessely(n,2*(s*x)^(1/2)),x,0,infinity),
  Y_transform2(1/sqrt(~x),~f1,~x) =>
                int(1/sqrt(x)*f1*bessely(n,2*(s*x)^(1/2)),x,0,infinity),
  Y_transform2(1/(sqrt(~x)*~x),~f1,~x) =>
            int(1/(sqrt(x)*x)*f1*bessely(n,2*(s*x)^(1/2)),x,0,infinity),
  Y_transform2(1/(sqrt(~x)*~x^~a),~f1,~x) =>
          int(1/(sqrt(x)*x^a)*f1*bessely(n,2*(s*x)^(1/2)),x,0,infinity),
  Y_transform2(~x^~a,~f1,~x) =>
                int(x^a*f1*bessely(n,2*(s*x)^(1/2)),x,0,infinity),
  Y_transform2(~x,~f1,~x) =>
                int(x*f1*bessely(n,2*(s*x)^(1/2)),x,0,infinity),
  Y_transform2(sqrt(~x),~f1,~x) =>
                int(sqrt(x)*f1*bessely(n,2*(s*x)^(1/2)),x,0,infinity),
  Y_transform2(sqrt(~x)*~x,~f1,~x) =>
                int(sqrt(x)*x*f1*bessely(n,2*(s*x)^(1/2)),x,0,infinity),
  Y_transform2(sqrt(~x)*~x^~a,~f1,~x) =>
              int(sqrt(x)*x^a*f1*bessely(n,2*(s*x)^(1/2)),x,0,infinity),
  Y_transform2(~b,~f1,~x) =>
              int(b*f1*bessely(n,2*(s*x)^(1/2)),x,0,infinity),
  Y_transform2(~f1,~x) => int(f1*bessely(n,2*(s*x)^(1/2)),x,0,infinity)
};

let Y_transform2_rules;

K_transform2_rules :=

{ K_transform2(1/~x,~f1,~x) =>
                      int(1/x*f1*besselK(n,2*(s*x)^(1/2)),x,0,infinity),
  K_transform2(1/~x^(~a),~f1,~x) =>
                    int(1/x^a*f1*besselK(n,2*(s*x)^(1/2)),x,0,infinity),
  K_transform2(1/sqrt(~x),~f1,~x) =>
                int(1/sqrt(x)*f1*besselK(n,2*(s*x)^(1/2)),x,0,infinity),
  K_transform2(1/(sqrt(~x)*~x),~f1,~x) =>
            int(1/(sqrt(x)*x)*f1*besselK(n,2*(s*x)^(1/2)),x,0,infinity),
  K_transform2(1/(sqrt(~x)*~x^~a),~f1,~x) =>
          int(1/(sqrt(x)*x^a)*f1*besselK(n,2*(s*x)^(1/2)),x,0,infinity),
  K_transform2(~x^~a,~f1,~x) =>
          int(x^a*f1*besselK(n,2*(s*x)^(1/2)),x,0,infinity),
  K_transform2(~x,~f1,~x) =>
          int(x*f1*besselK(n,2*(s*x)^(1/2)),x,0,infinity),
  K_transform2(sqrt(~x),~f1,~x) =>
          int(sqrt(x)*f1*besselK(n,2*(s*x)^(1/2)),x,0,infinity),
  K_transform2(sqrt(~x)*~x,~f1,~x) =>
          int(sqrt(x)*x*f1*besselK(n,2*(s*x)^(1/2)),x,0,infinity),
  K_transform2(sqrt(~x)*~x^~a,~f1,~x) =>
          int(sqrt(x)*x^a*f1*besselK(n,2*(s*x)^(1/2)),x,0,infinity),
  K_transform2(~b,~f1,~x) =>
          int(b*f1*besselK(n,2*(s*x)^(1/2)),x,0,infinity),
  K_transform2(~f1,~x) => int(f1*besselK(n,2*(s*x)^(1/2)),x,0,infinity)
};

let K_transform2_rules;

struveh2_rules :=

{ struveh2(1/~x,~f1,~x) =>
                 int(1/x*f1*struveh(n,2*(s*x)^(1/2)),x,0,infinity),
  struveh2(1/~x^(~a),~f1,~x) =>
                 int(1/x^a*f1*struveh(n,2*(s*x)^(1/2)),x,0,infinity),
  struveh2(1/sqrt(~x),~f1,~x) =>
                int(1/sqrt(x)*f1*struveh(n,2*(s*x)^(1/2)),x,0,infinity),
  struveh2(1/(sqrt(~x)*~x),~f1,~x) =>
            int(1/(sqrt(x)*x)*f1*struveh(n,2*(s*x)^(1/2)),x,0,infinity),
  struveh2(1/(sqrt(~x)*~x^~a),~f1,~x) =>
          int(1/(sqrt(x)*x^a)*f1*struveh(n,2*(s*x)^(1/2)),x,0,infinity),
  struveh2(~x^~a,~f1,~x) =>
          int(x^a*f1*struveh(n,2*(s*x)^(1/2)),x,0,infinity),
  struveh2(~x,~f1,~x) =>
          int(x*f1*struveh(n,2*(s*x)^(1/2)),x,0,infinity),
  struveh2(sqrt(~x),~f1,~x) =>
          int(sqrt(x)*f1*struveh(n,2*(s*x)^(1/2)),x,0,infinity),
  struveh2(sqrt(~x)*~x,~f1,~x) =>
          int(sqrt(x)*x*f1*struveh(n,2*(s*x)^(1/2)),x,0,infinity),
  struveh2(sqrt(~x)*~x^~a,~f1,~x) =>
          int(sqrt(x)*x^a*f1*struveh(n,2*(s*x)^(1/2)),x,0,infinity),
  struveh2(~b,~f1,~x) =>
          int(b*f1*struveh(n,2*(s*x)^(1/2)),x,0,infinity),
  struveh2(~f1,~x) => int(f1*struveh(n,2*(s*x)^(1/2)),x,0,infinity)
};

let struveh2_rules;

fourier_sin2_rules :=

{ fourier_sin2(1/~x,~f1,~x) => int(1/x*f1*sin(s*x),x,0,infinity),
  fourier_sin2(1/~x^(~a),~f1,~x) => int(1/x^a*f1*sin(s*x),x,0,infinity),
  fourier_sin2(1/sqrt(~x),~f1,~x) =>
                                int(1/sqrt(x)*f1*sin(s*x),x,0,infinity),
  fourier_sin2(1/(sqrt(~x)*~x),~f1,~x) =>
                 int(1/(sqrt(x)*x)*f1*sin(s*x),x,0,infinity),
  fourier_sin2(1/(sqrt(~x)*~x^~a),~f1,~x) =>
                 int(1/(sqrt(x)*x^a)*f1*sin(s*x),x,0,infinity),
  fourier_sin2(~x^~a,~f1,~x) => int(x^a*f1*sin(s*x),x,0,infinity),
  fourier_sin2(~x,~f1,~x) => int(x*f1*sin(s*x),x,0,infinity),
  fourier_sin2(sqrt(~x),~f1,~x)=> int(sqrt(x)*f1*sin(s*x),x,0,infinity),
  fourier_sin2(sqrt(~x)*~x,~f1,~x) =>
                 int(sqrt(x)*x*f1*sin(s*x),x,0,infinity),
  fourier_sin2(sqrt(~x)*~x^~a,~f1,~x) =>
                 int(sqrt(x)*x^a*f1*sin(s*x),x,0,infinity),
  fourier_sin2(~b,~f1,~x) => int(b*f1*sin(s*x),x,0,infinity),
  fourier_sin2(~f1,~x) => int(f1*sin(s*x),x,0,infinity)
};

let fourier_sin2_rules;

fourier_cos2_rules :=

{ fourier_cos2(1/~x,~f1,~x) => int(1/x*f1*cos(s*x),x,0,infinity),
  fourier_cos2(1/~x^(~a),~f1,~x) => int(1/x^a*f1*cos(s*x),x,0,infinity),
  fourier_cos2(1/sqrt(~x),~f1,~x) =>
                int(1/sqrt(x)*f1*cos(s*x),x,0,infinity),
  fourier_cos2(1/(sqrt(~x)*~x),~f1,~x) =>
                int(1/(sqrt(x)*x)*f1*cos(s*x),x,0,infinity),
  fourier_cos2(1/(sqrt(~x)*~x^~a),~f1,~x) =>
                int(1/(sqrt(x)*x^a)*f1*cos(s*x),x,0,infinity),
  fourier_cos2(~x^~a,~f1,~x) => int(x^a*f1*cos(s*x),x,0,infinity),
  fourier_cos2(~x,~f1,~x) => int(x*f1*cos(s*x),x,0,infinity),
  fourier_cos2(sqrt(~x),~f1,~x)=> int(sqrt(x)*f1*cos(s*x),x,0,infinity),
  fourier_cos2(sqrt(~x)*~x,~f1,~x) =>
                       int(sqrt(x)*x*f1*cos(s*x),x,0,infinity),
  fourier_cos2(sqrt(~x)*~x^~a,~f1,~x) =>
                       int(sqrt(x)*x^a*f1*cos(s*x),x,0,infinity),
  fourier_cos2(~b,~f1,~x) => int(b*f1*cos(s*x),x,0,infinity),
  fourier_cos2(~f1,~x) => int(f1*cos(s*x),x,0,infinity)
};

let fourier_cos2_rules;

>>;

endmodule;
end;



