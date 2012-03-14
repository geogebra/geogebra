module defintk;

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


% A rule set to test for the validity of the thirty-five cases for
% the validity of the integration of a product of two Meijer
% G-functions.
%
% 'Integrals and Series, Volume 3, More Special Functions',
%  A.P.Prudnikov, Yu.A.Brychkov, O.I.Marichev. Chapter 2.24.1 pages
%  346 & 347

algebraic<<

operator test_cases2,case1,case2,case3,case4,case5,case6,case7,case8,
         case9,case10,case11,case12,case13,case14,case15,case16,case17,
         case18,case19;

test_cases2_rules :=

{test_cases2(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,
        ~rho,~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

   when case1(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
        r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
        test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
        test_15) = 't

   or case2(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,r2,
         phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,test_7,
         test_8,test_9,test_10,test_11,test_12,test_13,test_14,test_15)
        = 't

   or case3(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,r2,
         phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,test_7,
         test_8,test_9,test_10,test_11,test_12,test_13,test_14,test_15)
        = 't

   or case4(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,r2,
         phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,test_7,
         test_8,test_9,test_10,test_11,test_12,test_13,test_14,test_15)
        = 't

   or case5(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,r2,
         phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,test_7,
         test_8,test_9,test_10,test_11,test_12,test_13,test_14,test_15)
        = 't

   or case6(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,r2,
         phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,test_7,
         test_8,test_9,test_10,test_11,test_12,test_13,test_14,test_15)
        = t

   or case7(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,r2,
         phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,test_7,
         test_8,test_9,test_10,test_11,test_12,test_13,test_14,test_15)
        = t

   or case8(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,r2,
         phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,test_7,
         test_8,test_9,test_10,test_11,test_12,test_13,test_14,test_15)
        = t

   or case9(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,r2,
         phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,test_7,
         test_8,test_9,test_10,test_11,test_12,test_13,test_14,test_15)
        = t

   or case10(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
         r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
         test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
         test_15) = t

   or case11(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
         r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
         test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
         test_15) = t

   or case12(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
         r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
         test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
         test_15) = t

   or case13(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
         r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
         test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
         test_15) = t

   or case14(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
         r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
         test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
         test_15) = t

   or case15(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
         r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
         test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
         test_15) = t

   or case16(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
         r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
         test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
         test_15) = t

   or case17(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
         r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
          test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
          test_15) = t

   or case18(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
         r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
         test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
         test_15) = t

   or case19(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
         r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
         test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
         test_15) = t

   or case20(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
        r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
        test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
        test_15) = t

   or case21(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
          r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
          test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
         test_15) = 't

   or case22(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
         r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
         test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
         test_15) = 't

   or case23(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
         r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
         test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
         test_15) = 't

   or case24(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
         r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
         test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
         test_15)
        = 't

   or case25(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
         r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
         test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
         test_15) = 't

   or case26(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
         r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
         test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
         test_15) = 't

   or case27(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
         r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
         test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
         test_15) = 't

   or case28(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
         r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
         test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
         test_15) = 't

   or case29(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
         r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
         test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
         test_15) = t

   or case30(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
         r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
         test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
         test_15) = t

   or case31(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
         r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
          test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
         test_15) = t

   or case32(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
         r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
         test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
         test_15) = t

   or case33(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
         r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
          test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
         test_15) = t

   or case34(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
         r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
         test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
         test_15) = t

   or case35(m,n,p,q,k,l,u,v,delta,epsilon,sigma,omega,rho,eta,mu,r1,
         r2,phi,test_1a,test_1b,test_2,test_3,test_4,test_5,test_6,
         test_7,test_8,test_9,test_10,test_11,test_12,test_13,test_14,
         test_15) = t
};

let test_cases2_rules;

case1_rules :=

{  case1(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when m*n*k*l neq 0
              and delta > 0
              and epsilon > 0
              and test_1a = 't and test_1b = 't and test_2 = 't
              and test_3 = 't and test_10 = 't and test_12 = 't
        and transform_test('test_2,'test3,'test10,'test12,nil,nil,nil,
        nil) = 't

};

let case1_rules;

case2_rules :=

{  case2(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when u = v
              and delta = 0
        and epsilon > 0
        and sigma_tst(sigma) = 't
        and repart rho < 1
        and test_1a = 't and test_1b = 't and test_2 = 't
              and test_3 = 't and test_12 = 't
        and transform_test('test2,'test3,'test12,'sigma_cond,nil,nil,
        nil,nil) = 't
};

let case2_rules;

case3_rules :=

{  case3(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when p = q
        and epsilon = 0
        and delta >0
        and omega_tst(omega) = 't
        and repart eta < 1
        and test_1a = 't and test_1b = 't and test_2 = 't
              and test_3 = 't and test_10 = 't
        and transform_test(test_2,'test3,'test10,'omega_cond,nil,nil,
        nil,nil) = 't
};

let case3_rules;

case4_rules :=

{  case4(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when p = q
        and u = v
        and delta = 0
        and epsilon = 0
        and sigma_tst(sigma) = 't
        and omega_tst(omega) = 't
        and repart eta < 1
        and repart rho < 1
        and sigma^r1 neq omega^r2
        and test_1a = 't and test_1b = 't and test_2 = 't
              and test_3 = 't
        and transform_test('test_2,'test3,'sigma_cond,'omega_cond,nil,
        nil,nil,nil) = 't
};

let case4_rules;


case5_rules :=

{  case5(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when p = q
        and u = v
        and delta = 0
        and epsilon = 0
        and sigma_tst(sigma) = 't
        and omega_tst(omega) = 't
        and repart(eta + rho) < 1
        and sigma^r1 neq omega^r2
        and test_1a = 't and test_1b = 't and test_2 = 't
              and test_3 = 't
        and transform_test('test2,'test3,'sigma_cond,'omega_cond,nil,
        nil,nil,nil) = 't
};

let case5_rules;

case6_rules :=

{  case6(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when p > q
        and k > 0
        and delta > 0
        and epsilon >= 0
        and test_1a = 't and test_1b = 't and test_2 = 't
              and test_3 = 't and test_5 = 't and test_10 = 't
        and test_13 = 't
        and transform_test('test3,'test5,'test10,'test13,nil,nil,nil,
        nil) = 't

};

let case6_rules;

case7_rules :=

{  case7(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when p < q
        and l > 0
        and delta > 0
        and epsilon >= 0
        and test_1a = 't and test_1b = 't and test_2 = 't
              and test_3 = 't and test_4 = 't and test_10 = 't
        and test_13 = 't
        and transform_test('test3,'test4,'test10,'test13,nil,nil,nil,
        nil) = 't

};

let case7_rules;


case8_rules :=

{  case8(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when u > v
        and m > 0
        and delta >= 0
        and epsilon > 0
        and test_1a = 't and test_1b = 't and test_2 = 't
              and test_3 = 't and test_7 = 't and test_11 = 't
        and test_12 = 't
        and transform_test('test3,'test7,'test11,'test12,nil,nil,nil,
        nil) = 't

};

let case8_rules;

case9_rules :=

{  case9(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when u < v
        and n > 0
        and delta >= 0
        and epsilon > 0
        and test_1a = 't and test_1b = 't and test_2 = 't
              and test_3 = 't and test_6 = 't and test_11 = 't
        and test_12 = 't
        and transform_test('test2,'test3,'test6,'test11,'test12,nil,
        nil,nil) = 't

};

let case9_rules;


case10_rules :=

{  case10(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when p > q
        and u = v
        and delta = 0
        and epsilon >= 0
        and sigma_tst(sigma) = 't
        and repart rho < 1
        and test_1a = 't and test_1b = 't and test_2 = 't
              and test_3 = 't and test_5 = 't and test_13 = 't
        and transform_test('test2,'test3,'test5,'test13,'sigma_cond,
        nil,nil,nil) = 't


};

let case10_rules;

case11_rules :=

{  case11(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when p < q
        and u = v
        and delta = 0
        and epsilon >= 0
        and sigma_tst(sigma) = 't
        and repart rho < 1
        and test_1a = 't and test_1b = 't and test_2 = 't
              and test_3 = 't and test_4 = 't and test_13 = 't
        and transform_test('test2,'test3,'test4,'test13,'sigma_cond,
        nil,nil,nil) = 't
};

let case11_rules;

case12_rules :=

{  case12(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when p = q
        and u > v
        and delta >= 0
        and epsilon = 0
        and omega_tst(omega) = 't
        and repart eta < 1
        and test_1a = 't and test_1b = 't and test_2 = 't
              and test_3 = 't and test_7 = 't and test_11 = 't
        and transform_test('test2,'test3,'test7,'test11,'omega_cond,
        nil,nil,nil) = 't
};

let case12_rules;

case13_rules :=

{  case13(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when p = q
        and u < v
        and delta >= 0
        and epsilon = 0
        and omega_tst(omega) = 't
        and repart eta < 1
        and test_1a = 't and test_1b = 't and test_2 = 't
              and test_3 = 't and test_6 = 't and test_11 = 't
        and transform_test('test2,'test3,'test6,'test11,'omega_cond,
        nil,nil,nil) = 't
};

let case13_rules;

case14_rules :=

{  case14(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when p < q
        and u > v
        and delta >= 0
        and epsilon >= 0
        and test_1a = 't and test_1b = 't and test_2 = 't
              and test_3 = 't and test_4 = 't and test_7 = 't
        and test_11 = 't and test_13 = 't
        and transform_test('test2,'test3,'test4,'test7,'test11,'test13,
        nil,nil) = 't
};

let case14_rules;

case15_rules :=

{  case15(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when p > q
        and u < v
        and delta >= 0
        and epsilon >= 0
        and test_1a = 't and test_1b = 't and test_2 = 't
              and test_3 = 't and test_5 = 't and test_6 = 't
        and test_11 = 't and test_13 = 't
        and transform_test('test2,'test3,'test5,'test6,'test11,'test13,
        nil,nil) = 't
};

let case15_rules;

case16_rules :=

{  case16(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when p > q
        and u > v
        and delta >= 0
        and epsilon >= 0
        and test_1a = 't and test_1b = 't and test_2 = 't
              and test_3 = 't and test_5 = 't and test_7 = 't
        and test_8 = 't and test_11 = 't and test_13 = 't
        and test_14 = 't
        and transform_test('test2,'test3,'test5,'test7,'test8,'test11,
        'test13,'test14) = 't
};

let case16_rules;

case17_rules :=

{  case17(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when p < q
        and u < v
        and delta >= 0
        and epsilon >= 0
        and test_1a = 't and test_1b = 't and test_2 = 't
              and test_3 = 't and test_4 = 't and test_6 = 't
        and test_9 = 't and test_11 = 't and test_13 = 't
        and test_14 = 't
        and transform_test('test2,'test3,'test4,'test6,'test9,'test11,
        'test13,'test14) = 't
};

let case17_rules;

case18_rules :=

{  case18(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when l = 0
        and k > 0
        and delta > 0
        and phi > 0
        and test_1a = 't and test_1b = 't and test_2 = 't
              and test_10 = 't
        and transform_test('test2,'test10,nil,nil,nil,nil,nil,nil) = 't
};

let case18_rules;

case19_rules :=

{  case19(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when k = 0
        and l > 0
        and delta > 0
        and phi < 0
        and test_1a = 't and test_1b = 't and test_3 = 't
              and test_10 = 't
        and transform_test('test10,nil,nil,nil,nil,nil,nil,nil) = 't
};

let case19_rules;
>>;

endmodule;
end;



