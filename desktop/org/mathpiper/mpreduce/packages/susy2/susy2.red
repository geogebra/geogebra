module susy2;
%version 1.2
%changes and bugs compare to version 1.0
%8.12.1996
%vericication of cp,axp;
%12.05.1997
%lyst in order to consider  n(0)*d(1)^3+m(0)*d(1)^3;
%10.09.1997
%verification of jacob(wx,wx1,wx2) i fjacob
%1.10.1997
%introduction  chiral1 and
%b_chiral,f_chiral,b_antychiral,f_antychiral
%introduction  lyst2
%introduction of matrix(expression,boson or fermion, full or boson sector)
%3.10.1997
%verification of coordinates
%6.10.1997
%verification of wcomb,fcomb;
%19.04.1999
%changes and bugs compare to version 1.1
%new command s_int

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
operator !@f_f,!@g_g,newton,delta,b_part,bf_part,pg,chan,s_part,prykr,prykl;
operator bos,fer,der,del,d,axp,axx,zan,zen,fun,tet,gras,ber,fir,berz,firr,dr,
stp,byk,r_r,!&a,p_p,s_s,waga;
noncom   bos,fer,der,del,d,axp,axx,zan,zen,fun,tet,gras,ber,fir,berz,firr,dr,
stp,byk,r_r;
factor !&a,byk;
factor fer,bos,fun,gras;
%fer,bos,axp superfunctions
%der,del,d,dr operations
%zan,zen needs to divergency
%fun,tet,gras,axx classical part
%stp to adjoint
%ber,fir,berz,firr corresponds to Gato
%byk,r_r,!&a,p_p,s_s,waga for super integration
%*******************************************%
%*** declaration of chirality and **********%
%*** cutoff and drr ************************%
%*******************************************%
chiral:={ abra_kadabra => 1 ,
der(~n)**2 => 0, del(~n)**2 => 0,del(2)*del(1) => - d(1) - del(1)*del(2),
der(2)*der(1) => -d(1) - der(1)*der(2),der(~n)*del(~n) => 0,
del(~n)*der(~n) => 0,der(1)*del(2)=> -d(1) - del(2)*der(1),
der(2)*del(1)=> -d(1) - del(1)*der(2),
%b_chiral,f_chiral,b_antychiral,f_antychiral as lists
b_chiral => {}, f_chiral => {}, b_antychiral => {}, f_antychiral => {},
fer(~f,1,~m) => 0 when not freeof(b_chiral,f),
bos(~f,3,~m) => -bos(f,0,m+1) when not freeof(b_chiral,f),
bos(~f,3,~m,~k) => (-1)**k*bos(f,0,m+1,k) when not freeof(b_chiral,f),

bos(~f,1,~m) => 0 when not freeof(f_chiral,f),
bos(~f,1,~m,~k) => 0 when not freeof(f_chiral,f),
fer(~f,3,~m) => -fer(f,0,m+1) when not freeof(f_chiral,f),

fer(~f,2,~m) => 0 when not freeof(b_antychiral,f),
bos(~f,3,~m) => 0 when not freeof(b_antychiral,f),
bos(~f,3,~m,~k) => 0 when not freeof(b_antychiral,f),

bos(~f,2,~m) => 0 when not freeof(f_antychiral,f),
bos(~f,2,~m,~k) => 0 when not freeof(f_antychiral,f),
fer(~f,3,~m) => 0 when not freeof(f_antychiral,f),

der(1)*fer(~f,1,~m) =>  - fer(f,1,m)*der(1),
der(1)*fer(~f,2,~m) => bos(f,3,m) -  fer(f,2,m)*der(1),
der(1)*fer(~f,3,~m) => - fer(f,3,m)*der(1),
fer(~f,1,~m)*del(1) =>  - del(1)*fer(f,1,m),
fer(~f,2,~m)*del(1) => bos(f,3,m) -  del(1)*fer(f,2,m),
fer(~f,3,~m)*del(1) =>  - del(1)*fer(f,3,m),
der(2)*fer(~f,1,~m) => -bos(f,0,m+1) - bos(f,3,m) - fer(f,1,m)*der(2),
der(2)*fer(~f,2,~m) =>  - fer(f,2,m)*der(2),
der(2)*fer(~f,3,~m) => -bos(f,2,m+1)-fer(f,3,m)*der(2),
fer(~f,1,~m)*del(2) => -bos(f,0,m+1) - bos(f,3,m) - del(2)*fer(f,1,m),
fer(~f,2,~m)*del(2) =>   - del(2)*fer(f,2,m),
fer(~f,3,~m)*del(2) => -bos(f,2,m+1)-del(2)*fer(f,3,m),
der(1)*bos(~f,1,~m) =>  bos(f,1,m)*der(1),
der(1)*bos(~f,2,~m) => fer(f,3,m) + bos(f,2,m)*der(1),
der(1)*bos(~f,3,~m) =>  bos(f,3,m)*der(1),
bos(~f,1,~m)*del(1) =>  del(1)*bos(f,1,m),
bos(~f,2,~m)*del(1) => -fer(f,3,m)+del(1)*bos(f,2,m),
bos(~f,3,~m)*del(1) =>  del(1)*bos(f,3,m),
der(2)*bos(~f,1,~m) => -fer(f,0,m+1) - fer(f,3,m) + bos(f,1,m)*der(2),
der(2)*bos(~f,2,~m) =>  bos(f,2,m)*der(2),
der(2)*bos(~f,3,~m) => -fer(f,2,m+1) + bos(f,3,m)*der(2),
bos(~f,1,~m)*del(2) => fer(f,0,m+1) + fer(f,3,m) + del(2)*bos(f,1,m),
bos(~f,2,~m)*del(2) =>  del(2)*bos(f,2,m),
bos(~f,3,~m)*del(2) => fer(f,2,m+1) + del(2)*bos(f,3,m),
der(1)*bos(~f,1,~m,~l) =>  bos(f,1,m,l)*der(1),
der(1)*bos(~f,2,~m,~l) => l*fer(f,3,m)*bos(f,2,m,l-1)+bos(f,2,m,l)*der(1),
der(1)*bos(~f,3,~m,~l) =>  bos(f,3,m,l)*der(1),
bos(~f,1,~m,~l)*del(1) =>  del(1)*bos(f,1,m,l),
bos(~f,2,~m,~l)*del(1) => -l*fer(f,3,m)*bos(f,2,m,l-1)+del(1)*bos(f,2,m,l),
bos(~f,3,~m,~l)*del(1) =>  del(1)*bos(f,3,m,l),
der(2)*bos(~f,1,~m,~l) => -l*(fer(f,0,m+1)+fer(f,3,m))*bos(f,1,m,l-1) +
                       bos(f,1,m,l)*der(2),
der(2)*bos(~f,2,~m,~l) =>  bos(f,2,m,l)*der(2),
der(2)*bos(~f,3,~m,~l) => -l*fer(f,2,m+1)*bos(f,3,m,l-1) +
                       bos(f,3,m,l)*der(2),
bos(~f,1,~m,~l)*del(2) => l*(fer(f,0,m+1)+fer(f,3,m))*bos(f,1,m,l-1) +
                       del(2)*bos(f,1,m,l),
bos(~f,2,~m,~l)*del(2) => del(2)*bos(f,2,m,l),
bos(~f,3,~m,~l)*del(2) => l*fer(f,2,m+1)*bos(f,3,m,l-1) +
                       del(2)*bos(f,3,m,l)}$

chiral1:={abra_kadabra => 3 ,
%der(3) as commutator,bos(f,3,n) or fer(f,3,4) as commutator
der(~n)**2 => 0 when n neq 3, del(~n)**2 => 0 when n neq 3,
der(3)^2 => d(1)^2,   del(3)^2 => d(1)^2,

der(2)*der(1) => -(d(1)+der(3))/2,der(1)*der(2) => (-d(1)+der(3))/2,
del(2)*del(1) => -(d(1)+del(3))/2,del(1)*del(2) => (-d(1)+del(3))/2,
der(1)*der(3) => d(1)*der(1),  der(2)*der(3) => -d(1)*der(2),
der(3)*der(1) => -d(1)*der(1), der(3)*der(2) =>  d(1)*der(2),
del(1)*del(3) => d(1)*del(1),  del(2)*del(3) => -d(1)*del(2),
del(3)*del(1) => -d(1)*del(1), del(3)*del(2) =>  d(1)*del(2),
der(~n)*del(~n) => if n neq 3 then 0 else if n = 3 then d(1)^2,
del(~n)*der(~n) => if n neq 3 then 0 else if n = 3 then d(1)^2,
der(1)*del(2) => -d(1) -del(2)*der(1),der(2)*del(1) => -d(1) -del(1)*der(2),
der(1)*del(3) => d(1)*del(1), der(2)*del(3) => -d(1)*del(2),
b_chiral => {}, f_chiral => {}, b_antychiral => {}, f_antychiral => {},
fer(~f,1,~m) => 0 when not freeof(b_chiral,f),
bos(~f,3,~m) => -bos(f,0,m+1) when not freeof(b_chiral,f),
bos(~f,3,~m,~k) => (-1)**k*bos(f,0,m+1,k) when not freeof(b_chiral,f),

bos(~f,1,~m) => 0 when not freeof(f_chiral,f),
bos(~f,1,~m,~k) => 0 when not freeof(f_chiral,f),
fer(~f,3,~m) => -fer(f,0,m+1) when not freeof(f_chiral,f),

fer(~f,2,~m) => 0 when not freeof(b_antychiral,f),
bos(~f,3,~m) => bos(f,0,m+1) when not freeof(b_antychiral,f),
bos(~f,3,~m,~k) => bos(f,0,m+1,k) when not freeof(b_antychiral,f),

bos(~f,2,~m) => 0 when not freeof(f_antychiral,f),
bos(~f,2,~m,~k) => 0 when not freeof(f_antychiral,f),
fer(~f,3,~m) => fer(f,0,m+1) when not freeof(f_antychiral,f),

der(1)*fer(~f,1,~m) =>  - fer(f,1,m)*der(1),
der(1)*fer(~f,2,~m) => -bos(f,0,m+1)/2+bos(f,3,m)/2 -  fer(f,2,m)*der(1),
der(1)*fer(~f,3,~m) =>  bos(f,1,m+1)-fer(f,3,m)*der(1),

fer(~f,1,~m)*del(1) =>  - del(1)*fer(f,1,m),
fer(~f,2,~m)*del(1) => -bos(f,0,m+1)/2 + bos(f,3,m)/2 -  del(1)*fer(f,2,m),
fer(~f,3,~m)*del(1) =>  bos(f,1,m+1) - del(1)*fer(f,3,m),

der(2)*fer(~f,1,~m) => -bos(f,0,m+1)/2 - bos(f,3,m)/2 - fer(f,1,m)*der(2),
der(2)*fer(~f,2,~m) =>  - fer(f,2,m)*der(2),
der(2)*fer(~f,3,~m) => -bos(f,2,m+1)-fer(f,3,m)*der(2),

fer(~f,1,~m)*del(2) => -bos(f,0,m+1)/2 - bos(f,3,m)/2 - del(2)*fer(f,1,m),
fer(~f,2,~m)*del(2) =>   - del(2)*fer(f,2,m),
fer(~f,3,~m)*del(2) => -bos(f,2,m+1)-del(2)*fer(f,3,m),

der(3)*fer(~f,0,~m) => fer(f,3,m) + fer(f,0,m)*der(3) - 2*bos(f,1,m)*der(2)
                        + 2*bos(f,2,m)*der(1),
der(3)*fer(~f,1,~m) => -fer(f,1,m+1) - bos(f,0,m+1)*der(1) -bos(f,3,m)*der(1)
                        +fer(f,1,m)*der(3),
der(3)*fer(~f,2,~m) =>  bos(f,0,m+1)*der(2) - bos(f,3,m)*der(2) + fer(f,2,m+1)
                        +fer(f,2,m)*der(3),
der(3)*fer(~f,3,~m) => fer(f,0,m+2) + fer(f,3,m)*der(3)
                        -2*bos(f,1,m+1)*der(2)-2*bos(f,2,m+1)*der(1),

fer(~f,0,~m)*del(3) =>  fer(f,3,m)+ del(3)*fer(f,0,m) + 2*del(2)*bos(f,1,m)
                        - 2*del(1)*bos(f,2,m),
fer(~f,1,~m)*del(3) => -fer(f,1,m+1)+ del(1)*bos(f,0,m+1) +
                del(1)*bos(f,3,m) + del(3)*fer(f,1,m),
fer(~f,2,~m)*del(3) => - del(2)*bos(f,0,m+1) +del(2)*bos(f,3,m) +fer(f,2,m+1)
                        +del(3)*fer(f,2,m),
fer(~f,3,~m)*del(3) => fer(f,0,m+2) + del(3)*fer(f,3,m)
                        + 2*del(2)*bos(f,1,m+1) + 2*del(1)*bos(f,2,m+1),

der(1)*bos(~f,1,~m) =>  bos(f,1,m)*der(1),
der(1)*bos(~f,2,~m) => -fer(f,0,m+1)/2 + fer(f,3,m)/2 + bos(f,2,m)*der(1),
der(1)*bos(~f,3,~m) =>  fer(f,1,m+1) + bos(f,3,m)*der(1),

bos(~f,1,~m)*del(1) =>  del(1)*bos(f,1,m),
bos(~f,2,~m)*del(1) => fer(f,0,m+1)/2 - fer(f,3,m)/2 +del(1)*bos(f,2,m),
bos(~f,3,~m)*del(1) =>  - fer(f,1,m+1) + del(1)*bos(f,3,m),

der(2)*bos(~f,1,~m) => -fer(f,0,m+1)/2 - fer(f,3,m)/2 + bos(f,1,m)*der(2),
der(2)*bos(~f,2,~m) =>  bos(f,2,m)*der(2),
der(2)*bos(~f,3,~m) => -fer(f,2,m+1) + bos(f,3,m)*der(2),

bos(~f,1,~m)*del(2) => fer(f,0,m+1)/2 + fer(f,3,m)/2 + del(2)*bos(f,1,m),
bos(~f,2,~m)*del(2) =>  del(2)*bos(f,2,m),
bos(~f,3,~m)*del(2) => fer(f,2,m+1) + del(2)*bos(f,3,m),

der(3)*bos(~f,0,~m) => bos(f,3,m) + bos(f,0,m)*der(3) + 2*fer(f,1,m)*der(2)
                        -2*fer(f,2,m)*der(1),
der(3)*bos(~f,1,~m) => -bos(f,1,m+1) + fer(f,0,m+1)*der(1) + fer(f,3,m)*der(1)
                        + bos(f,1,m)*der(3),
der(3)*bos(~f,2,~m) =>  - fer(f,0,m+1)*der(2) + fer(f,3,m)*der(2)
                        + bos(f,2,m)*der(3) +bos(f,2,m+1),
der(3)*bos(~f,3,~m) => bos(f,0,m+2) + 2*fer(f,2,m+1)*der(1) +
                2*fer(f,1,m+1)*der(2) + bos(f,3,m)*der(3),

bos(~f,0,~m)*del(3) =>  bos(f,3,m) + del(3)*bos(f,0,m) + 2*del(2)*fer(f,1,m)
                        -  2*del(1)*fer(f,2,m),
bos(~f,1,~m)*del(3) => del(1)*fer(f,0,m+1) + del(1)*fer(f,3,m) -bos(f,1,m+1)
                        + del(3)*bos(f,1,m),
bos(~f,2,~m)*del(3) =>  -del(2)*fer(f,0,m+1) + del(2)*fer(f,3,m)
                        + del(3)*bos(f,2,m) +bos(f,2,m+1) ,
bos(~f,3,~m)*del(3) =>  bos(f,0,m+2) + 2*del(1)*fer(f,2,m+1) +
                2*del(2)*fer(f,1,m+1) + del(3)*bos(f,3,m),

der(1)*bos(~f,1,~m,~l) =>  bos(f,1,m,l)*der(1),
der(1)*bos(~f,2,~m,~l) => l*bos(f,2,m,l-1)*(-fer(f,0,m+1)/2 + fer(f,3,m)/2)
                + bos(f,2,m,l)*der(1),
der(1)*bos(~f,3,~m,~l) => l*bos(f,3,m,l-1)*fer(f,1,m+1) +
                         bos(f,3,m,l)*der(1),

bos(~f,1,~m,~l)*del(1) =>  del(1)*bos(f,1,m,l),
bos(~f,2,~m,~l)*del(1) => - l*bos(f,2,m,l-1)*(-fer(f,0,m+1)/2 + fer(f,3,m)/2)
                        +del(1)*bos(f,2,m,l),
bos(~f,3,~m,~l)*del(1) => - l*bos(f,3,m,l-1)*fer(f,1,m+1) +
                         del(1)*bos(f,3,m,l),

der(2)*bos(~f,1,~m,~l) => - l*bos(f,1,m,l-1)*(fer(f,0,m+1)/2+ fer(f,3,m)/2)
                      + bos(f,1,m,l)*der(2),
der(2)*bos(~f,2,~m,~l) =>  bos(f,2,m,l)*der(2),
der(2)*bos(~f,3,~m,~l) => -l*fer(f,2,m+1)*bos(f,3,m,l-1) +
                       bos(f,3,m,l)*der(2),

bos(~f,1,~m,~l)*del(2) => l*(fer(f,0,m+1)/2+fer(f,3,m)/2)*bos(f,1,m,l-1) +
                       del(2)*bos(f,1,m,l),
bos(~f,2,~m,~l)*del(2) => del(2)*bos(f,2,m,l),
bos(~f,3,~m,~l)*del(2) => l*fer(f,2,m+1)*bos(f,3,m,l-1) +
                       del(2)*bos(f,3,m,l),

der(3)*bos(~f,~k,~m,~l) =>
        der(1)*prykr(bos(f,k,m,l),2)-der(2)*prykr(bos(f,k,m,l),1),
bos(~f,~k,~m,~l)*del(3) =>
        -prykl(bos(f,k,m,l),2)*del(1)+prykl(bos(f,k,m,l),1)*del(2) }$

trad:={abra_kadabra => 2 ,
der(~n)**2 => d(1),del(~n)**2 => d(1),del(2)*del(1) => -del(1)*del(2),
der(2)*der(1) => -der(1)*der(2), der(~n)*del(~n) => d(1),
del(~n)*der(~n) => d(1), der(1)*del(2) => -del(2)*der(1),
der(2)*del(1) => -del(1)*der(2),
der(1)*fer(~f,1,~m) => bos(f,0,m+1) - fer(f,1,m)*der(1),
der(1)*fer(~f,2,~m) => bos(f,3,m) -  fer(f,2,m)*der(1),
der(1)*fer(~f,3,~m) => bos(f,2,m+1) - fer(f,3,m)*der(1),
fer(~f,1,~m)*del(1) => bos(f,0,m+1) - del(1)*fer(f,1,m),
fer(~f,2,~m)*del(1) => bos(f,3,m) -  del(1)*fer(f,2,m),
fer(~f,3,~m)*del(1) => bos(f,2,m+1) - del(1)*fer(f,3,m),
der(2)*fer(~f,1,~m) => -bos(f,3,m) - fer(f,1,m)*der(2),
der(2)*fer(~f,2,~m) => bos(f,0,m+1) - fer(f,2,m)*der(2),
der(2)*fer(~f,3,~m) => -bos(f,1,m+1)-fer(f,3,m)*der(2),
fer(~f,1,~m)*del(2) => -bos(f,3,m) - del(2)*fer(f,1,m),
fer(~f,2,~m)*del(2) =>  bos(f,0,m+1) - del(2)*fer(f,2,m),
fer(~f,3,~m)*del(2) => -bos(f,1,m+1)-del(2)*fer(f,3,m),
der(1)*bos(~f,1,~m) => fer(f,0,m+1) + bos(f,1,m)*der(1),
der(1)*bos(~f,2,~m) => fer(f,3,m)+bos(f,2,m)*der(1),
der(1)*bos(~f,3,~m) => fer(f,2,m+1) + bos(f,3,m)*der(1),
bos(~f,1,~m)*del(1) => -fer(f,0,m+1) + del(1)*bos(f,1,m),
bos(~f,2,~m)*del(1) => -fer(f,3,m)+del(1)*bos(f,2,m),
bos(~f,3,~m)*del(1) => -fer(f,2,m+1) + del(1)*bos(f,3,m),
der(2)*bos(~f,1,~m) => -fer(f,3,m) + bos(f,1,m)*der(2),
der(2)*bos(~f,2,~m) => fer(f,0,m+1) + bos(f,2,m)*der(2),
der(2)*bos(~f,3,~m) => -fer(f,1,m+1) + bos(f,3,m)*der(2),
bos(~f,1,~m)*del(2) => fer(f,3,m) + del(2)*bos(f,1,m),
bos(~f,2,~m)*del(2) => -fer(f,0,m+1) + del(2)*bos(f,2,m),
bos(~f,3,~m)*del(2) => fer(f,1,m+1) + del(2)*bos(f,3,m),
der(1)*bos(~f,1,~m,~l) => l*fer(f,0,m+1)*bos(f,1,m,l-1) +
                       bos(f,1,m,l)*der(1),
der(1)*bos(~f,2,~m,~l) => l*fer(f,3,m)*bos(f,2,m,l-1)+
                       bos(f,2,m,l)*der(1),
der(1)*bos(~f,3,~m,~l) => l*fer(f,2,m+1)*bos(f,3,m,l-1) +
                       bos(f,3,m,l)*der(1),
bos(~f,1,~m,~l)*del(1) => -l*fer(f,0,m+1)*bos(f,1,m,l-1) +
                       del(1)*bos(f,1,m,l),
bos(~f,2,~m,~l)*del(1) => -l*fer(f,3,m)*bos(f,2,m,l-1)+
                       del(1)*bos(f,2,m,l),
bos(~f,3,~m,~l)*del(1) => -l*fer(f,2,m+1)*bos(f,3,m,l-1) +
                       del(1)*bos(f,3,m,l),
der(2)*bos(~f,1,~m,~l) => -l*fer(f,3,m)*bos(f,1,m,l-1) +
                       bos(f,1,m,l)*der(2),
der(2)*bos(~f,2,~m,~l) => l*fer(f,0,m+1)*bos(f,2,m,l-1) +
                       bos(f,2,m,l)*der(2),
der(2)*bos(~f,3,~m,~l) => -l*fer(f,1,m+1)*bos(f,3,m,l-1) +
                       bos(f,3,m,l)*der(2),
bos(~f,1,~m,~l)*del(2) => l*fer(f,3,m)*bos(f,1,m,l-1) +
                       del(2)*bos(f,1,m,l),
bos(~f,2,~m,~l)*del(2) => -l*fer(f,0,m+1)*bos(f,2,m,l-1) +
                       del(2)*bos(f,2,m,l),
bos(~f,3,~m,~l)*del(2) => l*fer(f,1,m+1)*bos(f,3,m,l-1) +
                       del(2)*bos(f,3,m,l)}$

drr:= { d(-1)**(~n) => dr(-n) when n neq 1,d(-1) => dr(-1) }$
nodrr:={ dr(-~n) => d(-1)**n when n neq 1, dr(-1) => d(-1) }$
cutoff:= { dr(~n) => 0 when  n < - cut }$
inverse:={bos(~f,~n,~m) => bos(f,n,m,1) ,
          fun(~f,~n)    => fun(f,n,1)   }$
%*******************************************%
%*** module  ordering **********************%
%*******************************************%
%ordering of bos with 4 and 3 indices and (fer,axp,ber,fir,zen,zan)
let {
bos(~f,~n,~m,~k)*bos(~g,~x,~z,~v) => bos(g,x,z,v)*bos(f,n,m,k) when
       ordp(f,g) and f neq g or f equal g and n<x or
       f equal g and n equal x and m<z,
bos(~f,~n,~m)*bos(~g,~x,~z) => bos(g,x,z)*bos(f,n,m) when
       ordp(f,g) and f neq g or f equal g and n<x or
       f equal g and n equal x and m<z,
bos(~f,~n,~m,~k)*bos(~g,~x,~z) => bos(g,x,z)*bos(f,n,m,k) when
       ordp(f,g) and f neq g or f equal g and n<x or
       f equal g and n equal x and m<z,
bos(~g,~x,~z)*bos(~f,~n,~m,~k) =>bos(f,n,m,k)*bos(g,x,z) when
       ordp(g,f) and f neq g or f equal g and n>x or
       f equal g and n equal x and m>z,
bos(~f,~n,~m,~k)*bos(~f,~n,~m,~l) => bos(f,n,m,k+l),
bos(~f,~n,~m,~k)**2 => bos(f,n,m,2k),
bos(~f,~n,~m,0) => 1, bos(0,~f,~n,~m) => 0, bos(0,~f,~n) => 0,
bos(~f,~n,~m,~k)*bos(~f,~n,~m) => bos(f,n,m,k+1),
bos(~f,~n,~m)*bos(~f,~n,~m,~k) => bos(f,n,m,k+1),

ber(~f,~n,~m)*bos(~g,~k,~x,~l) => bos(g,k,x,l)*ber(f,n,m),
fir(~f,~n,~m)*bos(~g,~k,~x,~l) => bos(g,k,x,l)*fir(f,n,m),
ber(~f,~n,~m)*bos(~g,~k,~l) => bos(g,k,l)*ber(f,n,m),
ber(~f,~n,~m)*fer(~g,~k,~l) => fer(g,k,l)*ber(f,n,m),
fir(~f,~n,~m)*bos(~g,~k,~l) => bos(g,k,l)*fir(f,n,m),
fir(~f,~n,~m)*fer(~g,~k,~l) => -fer(g,k,l)*fir(f,n,m),
%ordering of fer,
fer(0,~n,~m) => 0,
bos(~f,~n,~m,~y)*fer(~g,~x,~h) => fer(g,x,h)*bos(f,n,m,y),
bos(~f,~n,~m)*fer(~g,~x,~h)    => fer(g,x,h)*bos(f,n,m),
fer(~f,~n,~m)**2 => 0,
fer(~f,~n,~m)*fer(~g,~k,~l) => - fer(g,k,l)*fer(f,n,m) when
        ordp(f,g) and f neq g or f equal g and n<k or
        f equal g and n equal k and m<l,
%ordering classical,
fun(~f,~n,~m)*fun(~g,~k,~l) => fun(g,k,l)*fun(f,n,m) when
        ordp(f,g) and f neq g or f equal g and n<k or
        f equal g and n equal k and m<l,
fun(~f,~n,~m)*fun(~g,~x) => fun(g,x)*fun(f,n,m) when
        ordp(f,g) and f neq g or f equal g and n<x ,
fun(~g,~x)*fun(~f,~n,~m) => fun(f,n,m)*fun(g,x) when
       ordp(g,f) and f neq g or f equal g and n>x,
fun(~f,~n)*fun(~g,~m) => fun(g,m)*fun(f,n) when
        ordp(f,g) and f neq g or f equal g and n<m,
fun(~f,~n,~m,~k,~l)*fun(~s,~x) => fun(s,x)*fun(f,n,m,k,l),
fun(~f,~n,~m,~k,~l)*fun(~s,~x,~z) => fun(s,x,z)*fun(f,n,m,k,l),
fun(~f,~n,~m,~k,~l)*gras(~s,~x) => gras(s,x)*fun(f,n,m,k,l),
fun(~f,~n,~m,~k,~l)*tet(~s) => tet(s)*fun(f,n,m,k,l),
fun(~f,~n,~m,~k,~l)*fun(~s,~x,~z) => fun(s,x,z)*fun(f,n,m,k,l),
fun(~f,~n,~m)*gras(~g,~x) => gras(g,x)*fun(f,n,m),
fun(~f,~n)*gras(~g,~x) => gras(g,x)*fun(f,n),
gras(~f,~n)*gras(~g,~m) =>-gras(g,m)*gras(f,n) when
        ordp(f,g) and f neq g or f equal g and n<m,
ber(~f,~n)*fun(~g,~m) => fun(g,m)*fun(f,n),
ber(~f,~n)*gras(~g,~m) => gras(g,m)*fun(f,n),
fir(~f,~n)*fun(~g,~m) => fun(g,m)*fir(f,n),
fir(~f,~n)*gras(~g,~m) => - fir(g,m)*gras(f,n),
gras(~f,~n)^2 => 0,
fun(~f,~n,0) => 1,fun(0,~n,~m) => 0, fun(0,~n) => 0, gras(0,~n) => 0,
fun(~f,~n,~m)*fun(~f,~n,~k) => fun(f,n,m+k),
fun(~f,~n,~m)**2 => fun(f,n,2m),
fun(~f,~n,~m)*tet(~k) => tet(k)*fun(f,n,m),
fun(~f,~n)*tet(~k) => tet(k)*fun(f,n),
gras(~f,~n)*tet(~k) => - tet(k)*gras(f,n),
axx(~f)*fun(~g,~n) => fun(g,n)*axx(f),
axx(~f)*gras(~g,~n) => gras(g,n)*axx(f),
axx(~f)*fun(~g,~n,~m) => fun(g,n,m)*axx(f),
axx(~f)*fun(~g,~n,~m,~k,~l) => fun(g,n,m,~k,~l)*axx(f),
fun(~f,~n,~g,~m,~k) => (for s:=0:k sum
(-1)**s*newton(k,s)*fun(f,n,k-s)*fun(g,m,s)/(2**s))
when numberp(k) and k >=0,
%ordering other,
bos(~g,~x,~h)*zan(~f,~n,~m)    => zan(f,n,m)*bos(g,x,h),
bos(~g,~x,~h,~l)*zan(~f,~n,~m) => zan(f,n,m)*bos(g,x,h,l),
fer(~g,~x,~h)*zan(~f,~n,~m)    => zan(f,n,m)*fer(g,x,h),
bos(~g,~x,~h)*zen(~f,~n,~m)    => zen(f,n,m)*bos(g,x,h),
bos(~g,~x,~h,~l)*zen(~f,~n,~m) => zen(f,n,m)*bos(g,x,h,l),
fer(~g,~x,~h)*zen(~f,~n,~m)    => - zen(f,n,m)*fer(g,x,h),
axp(~g)*zan(~f,~n,~m)          => zan(f,n,m)*axp(g),
axp(~g)*zen(~f,~n,~m)          => zen(f,n,m)*axp(g),
axp(~g)*bos(~f,~n,~m) => bos(f,n,m)*axp(g),
axp(~g)*bos(~f,~n,~m,~l) => bos(f,n,m,l)*axp(g),
axp(~g)*fer(~f,~n,~m) => fer(f,n,m)*axp(g),
axp(~f)*axp(~g) => axp(f+g), axp(~f)**(~n) => axp(n*f)}$
%other;
let {          dr(~n)*dr(~m) => dr(n+m),tet(~n)^2 => 0,
tet(~n)*tet(~m) => -tet(m)*tet(n) when n<m,
!@g_g(~f,~m,~n)**2 => 0 when m=0 or m=3,
dr(~n)*d(1) => dr(n+1),   d(1)*dr(~n) => dr(n+1),dr(~n)*d(2) => dr(n+1),
d(2)*dr(~n) => dr(n+1),der(~m)*dr(~n) => dr(n)*der(m),
dr(~n)*del(~m) => del(m)*dr(n), dr(~n)**2 => dr(2n),axp(0)=> 1,
axx(0)=> 1,dr(0)=> 1, der(0) => 1,
der(~n)*d(~m) => d(m)*der(n) when m neq t,
d(1)*del(~n) => del(n)*d(1),del(~n)*d(2) => d(2)*del(n),
d(-1)*del(~n) => del(n)*d(-1),del(~n)*d(-2) => d(-2)*del(n),
d(-3)*del(~n) => del(n)*d(-3),del(~n)*d(-4) => d(-4)*del(n),

d(1)*d(-1)=> 1,d(-1)*d(1)=>1,d(1)*d(-2)=> 1,d(-2)*d(1)=> 1,
d(1)*d(-3)=> 1,d(-3)*d(1)=>1,d(1)*d(-4)=> 1,d(-4)*d(1)=> 1,
d(3)*d(-1)=> 1,d(-1)*d(3)=>1,d(3)*d(-2)=> 1,d(-2)*d(3)=> 1,
d(3)*d(-4)=> 1,d(-4)*d(3)=>1,d(2)*d(-1)=> 1,d(-1)*d(2)=> 1,
d(2)*d(-2)=> 1,d(-2)*d(2)=>1,d(2)*d(-3)=> 1,d(-3)*d(2)=> 1,
d(2)*d(-4)=> 1,d(-4)*d(2)=>1,d(3)*d(-3)=>1,
d(1)*d(3)=> d(3)*d(1), d(-3)*d(3)=>1,
d(t)*d(1)=> d(1)*d(t),d(t)*d(2)=>d(2)*d(t),
d(t)*der(~n)=>der(n)*d(t),d(t)*del(~n)=>del(n)*d(t),
d(t)*d(-1)=>d(-1)*d(t),d(t)*d(-2)=> d(-2)*d(t),!@x_y^2 =>1,
d(t)*d(-3)=>d(-3)*d(t),d(t)*d(-4)=> d(-4)*d(t),abs(!#ll) =>1,
delta(~f,~g)    => if f equal g then 1 else 0,
bf_part(~wx,~n) => part(fpart(wx),n+1),
b_part(~wx,~n)  => part(bpart(wx),n+1),
pg(~n,~x)       => sub(d(1)=0,d(1)**n*x),
chan(~x)        => sub(d(2)=d(1),sub(d(1)=d(2),x)),
s_part(~x,~n)   => coeffn(sub(der(1)=!@k,der(2)=(!@k)^2,der(3)=(!@k)^3,
                    del(1)=!@k,del(2)=(!@k)^2,der(3)=(!@k)^3,x),!@k,n),
newton(~n,~m)   => factorial(n)/(factorial(m)*factorial(n-m)),
prykr(~f,~g)    => if g = 1 then der(1)*f else if g = 2 then der(2)*f,
prykl(~f,~g)    => if g = 1 then f*del(1) else if g = 2 then f*del(2)}$
%adjoint
let {   bos(~f,~n,~m)*stp(~x)     => stp(x)*bos(f,n,m),
   bos(~f,~n,~m,~l)*stp(~x)       => stp(x)*bos(f,n,m,l),
   axp(~f)*stp(~x)                => stp(x)*axp(f),
d(~n)*stp(~x)  => -stp(x)*d(n) ,
der(~k)*stp(1) => stp(2)*der(k) when k neq 3,
der(~k)*stp(2)  => - stp(1)*der(k) when k neq 3,
del(~k)*stp(1) => - del(k) when k neq 3,
del(~k)*stp(2)  => - stp(1)*del(k) when k neq 3,
der(3)*stp(~x) => stp(x)*der(3),del(3)*stp(~x) => stp(x)*der(3),
del(~x)*stp(10)           => stp(20)*del(x) when x neq 3,
del(~x)*stp(20)           => -stp(10)*del(x) when x neq 3,
fer(~f,~n,~m)*stp(1)      => stp(10)*fer(f,n,m),
fer(~f,~n,~m)*stp(10)     => -stp(20)*fer(f,n,m),
fer(~f,~n,~m)*stp(20)     => stp(10)*fer(f,n,m),
fer(~f,~n,~m)*stp(2)      => stp(10)*fer(f,n,m)}$
%***********************************%
%*** Local action ******************%
%***********************************%
tryk:={ d(~f)*fer(~g,~n,~m)    => delta(f,g)*zen(g,n,m) + fer(g,n,m)*d(f),
        d(~f)*bos(~g,~n,~m)    => delta(f,g)*zan(g,n,m) + bos(g,n,m)*d(f),
        d(~f)*bos(~g,~n,~m,~l) => l*delta(f,g)*zan(g,n,m)*bos(g,n,m,l-1)+
                                  bos(g,n,m,l)*d(f),
        d(~f)*axp(~g)         => axp(g)*(d(f)*g-g*d(f))+axp(g)*d(f) }$
tryk1:={zan(~f,0,~m) => (-1)**m*d(1)**m,
       zan(~f,3,~m) =>
               if abra_kadabra = 2 then (-1)**m*der(1)*der(2)*d(1)**m
         else  if abra_kadabra = 1 then (-1)**(m+1)*der(2)*der(1)*d(1)**m
         else  if abra_kadabra = 3 then (-1)**m*der(3)*d(1)**m,
       zan(~f,1,~m) => (-1)**m*der(1)*d(1)**m,
       zan(~f,2,~m) => (-1)**m*der(2)*d(1)**m,
       zen(~f,1,~m) => (-1)**(m+1)*der(1)*d(1)**m,
       zen(~f,2,~m) => (-1)**(m+1)*der(2)*d(1)**m,
       zen(~f,0,~m) => (-1)**m*d(1)**m,
       zen(~f,3,~m) =>
               if abra_kadabra = 2 then (-1)**m*der(1)*der(2)*d(1)**m
         else  if abra_kadabra = 1 then (-1)**(m+1)*der(2)*der(1)*d(1)**m
         else  if abra_kadabra = 3 then (-1)**m*der(3)*d(1)**m}$
tryk2:={zan(~f,0,~m) => bos(f,0,0)*(-1)**m*d(1)**m,
        zan(~f,3,~m) => bos(f,0,0)*(
               if abra_kadabra = 2 then (-1)**m*der(1)*der(2)*d(1)**m
         else  if abra_kadabra = 1 then (-1)**(m+1)*der(2)*der(1)*d(1)**m
         else  if abra_kadabra = 3 then (-1)**m*der(3)*d(1)**m),
        zan(~f,1,~m) => fer(f,0,0)*(-1)**m*der(1)*d(1)**m,
        zan(~f,2,~m) => fer(f,0,0)*(-1)**m*der(2)*d(1)**m,
        zen(~f,1,~m) => bos(f,0,0)*(-1)**(m+1)*der(1)*d(1)**m,
        zen(~f,2,~m) => bos(f,0,0)*(-1)**(m+1)*der(2)*d(1)**m,
        zen(~f,0,~m) => fer(f,0,0)*(-1)**m*d(1)**m,
        zen(~f,3,~m) => fer(f,0,0)*(
               if abra_kadabra = 2 then (-1)**m*der(1)*der(2)*d(1)**m
         else  if abra_kadabra = 1 then (-1)**(m+1)*der(2)*der(1)*d(1)**m
         else  if abra_kadabra = 3 then (-1)**m*der(3)*d(1)**m)}$
tryk3:={fer(~f,~n,~m)    =>1,        bos(~f,~n,~m)    =>1,
        bos(~f,~n,~m,~l) =>1,        axp(~f)          => 1 }$
tryk4:={fer(~f,~n,~m)    => 1,        bos(~f,~n,~m)    => 1,
        axp(~f)          => 1,        bos(~f,~n,~m,~l) => 1,
        der(~n)  => 1, d(~n)  => 1,  del(~n)  => 1}$
%only for  trad
tryk5:={bos(~f,~m,~n) => if m=0 then
       fun(mkid(f,0),n)+tet(1)*gras(mkid(f,mkid(f,1)),n)+
       tet(2)*gras(mkid(f,mkid(f,2)),n)+tet(2)*tet(1)*fun(mkid(f,1),n)
       else if m=1  then
       fun(mkid(f,0),n) - tet(2)*gras(mkid(f,mkid(f,2)),n) +
       tet(1)*gras(mkid(f,mkid(f,1)),n+1) - tet(2)*tet(1)*fun(mkid(f,1),n+1)
       else if m=2 then
       fun(mkid(f,1),n) + tet(1)*gras(mkid(f,mkid(f,2)),n) +
       tet(2)*gras(mkid(f,mkid(f,1)),n+1) + tet(2)*tet(1)*fun(mkid(f,0),n+1)
       else if m=3 then
       tet(1)*gras(mkid(f,mkid(f,2)),n+1) + fun(mkid(f,1),n) -
       tet(2)*tet(1)*fun(mkid(f,0),n+2) - tet(2)*gras(mkid(f,mkid(f,1)),n+1)
       else rederr " wrong values of arguments",
fer(~f,~m,~n) => if m=0 then
       gras(mkid(f,mkid(f,1)),n)+tet(1)*fun(mkid(f,0),n)+
        tet(2)*fun(mkid(f,1),n)+tet(2)*tet(1)*gras(mkid(f,mkid(f,2)),n)
       else if m=1 then
        gras(mkid(f,mkid(f,1)),n)-tet(2)*fun(mkid(f,1),n)+
        tet(1)*fun(mkid(f,0),n+1)-tet(2)*tet(1)*gras(mkid(f,mkid(f,2)),n+1)
       else if m=2 then
        gras(mkid(f,mkid(f,2)),n)+tet(1)*fun(mkid(f,1),n)+
        tet(2)*fun(mkid(f,0),n+1)+tet(2)*tet(1)*gras(mkid(f,mkid(f,1)),n+1)
       else if m=3  then
       tet(1)*fun(mkid(f,1),n+1) + gras(mkid(f,mkid(f,2)),n) -
       tet(2)*tet(1)*gras(mkid(f,mkid(f,1)),n+2) - tet(2)*fun(mkid(f,0),n+1)
        else rederr "wrong values of arguments" ,
bos(~f,~m,~n,~l) => if m equal 0 then
       fun(mkid(f,0),n,l) + l*fun(mkid(f,0),n,l-1)*(tet(1)*gras(mkid(f,1),n)+
       tet(2)*gras(mkid(g,2),n) +tet(2)*tet(1)*(fun(mkid(f,1),n,1)+
       (l-1)*fun(mkid(f,0),n,-1)*gras(mkid(g,1),n)*gras(mkid(g,2),n)))
       else if m=1 then
       fun(mkid(f,0),n,l)+l*fun(mkid(f,0),n,l-1)*(tet(1)*gras(mkid(g,1),n+1)-
       tet(2)*gras(mkid(g,2),n)+tet(2)*tet(1)*(fun(mkid(f,1),n+1,1)-
       (l-1)*fun(mkid(f,0),n,-1)*gras(mkid(g,1),n+1)*gras(mkid(g,2),n)))
       else if m=2 then
       fun(mkid(f,1),n,l)+l*fun(mkid(f,1),n,l-1)*(tet(1)*gras(mkid(g,2),n)+
       tet(2)*gras(mkid(g,1),n+1)+tet(2)*tet(1)*(fun(mkid(f,0),n+1,1) -
       (l-1)*fun(mkid(f,1),n,-1)*gras(mkid(g,1),n+1)*gras(mkid(g,2),n)))
       else if m=3 then
       fun(mkid(f,1),n,l)+l*fun(mkid(f,1),n,l-1)*(tet(1)*gras(mkid(g,2),n+1)-
       tet(2)*gras(mkid(g,1),n+1) + tet(2)*tet(1)*(-fun(mkid(f,0),n+1,1)+
       (l-1)*fun(mkid(f,1),n,-1)*gras(mkid(g,1),n+1)*gras(mkid(g,2),n+1) ) )
       else rederr "wrong values of arguments" ,
axp(~f) => axx(bf_part(f,0))*(1+ tet(1)*bf_part(f,1)+
      tet(2)*bf_part(f,2) + tet(2)*tet(1)*(bf_part(f,3)+
        2*bf_part(f,1)*bf_part(f,2))) }$
tryk6:={ gras(~f,~n) =>0 }$
tryk7:={  !@f_f(~f,0,~n)    => bos(f,0,n),
!@f_f(~f,1,~n)    =>    if abra_kadabra = 2 then bos(f,1,n) else
                        if not freeof(f_chiral,f) then 0 else bos(f,1,n),
!@f_f(~f,2,~n)    =>    if abra_kadabra = 2 then bos(f,2,n) else
                        if not freeof(f_antychiral,f) then 0 else  bos(f,2,n),
!@f_f(~f,3,~n)    =>    if abra_kadabra = 2 then bos(f,3,n) else
if not freeof(b_chiral,f) then - bos(f,0,n+1) else
if abra_kadabra = 1 and not freeof(b_antychiral,f) then 0 else
if abra_kadabra = 3 and not freeof(b_antychiral,f) then bos(f,0,n+1) else
bos(f,3,n),
!@g_g(~f,0,~n)    => fer(f,0,n),
!@g_g(~f,1,~n)    =>    if abra_kadabra = 2 then fer(f,1,n) else
                        if not freeof(b_chiral,f) then 0 else fer(f,1,n),
!@g_g(~f,2,~n)    =>    if abra_kadabra = 2 then fer(f,2,n) else
                        if not freeof(b_antychiral,f) then 0 else fer(f,2,n),
!@g_g(~f,3,~n)    =>    if abra_kadabra = 2 then fer(f,3,n) else
if not freeof(f_chiral,f) then -fer(f,0,n+1) else
if abra_kadabra = 1 and not freeof(f_antychiral,f) then 0 else
if abra_kadabra = 3 and not freeof(f_antychiral,f) then fer(f,0,n+1) else
fer(f,3,n)}$
tryk8:={ bos(~f,~n,~m)     => berz(f,n,m)+eps*ber(f,n,m),
         fer(~f,~n,~m)     => firr(f,n,m)+eps*fir(f,n,m),
         bos(~f,~n,~m,~l)  => berz(f,n,m,l)+l*eps*berz(f,n,m,l-1)*ber(f,n,m)}$
tryk9:={ berz(~f,~n,~m)    => bos(f,n,m), firr(~f,~n,~m)    => fer(f,n,m),
         berz(~f,~n,~m,~l) => bos(f,n,m,l)}$
tryk10:= { fir(~f,~n,~m)    => pg(m,pr(n,bos(f))),
          ber(~f,~n,~m)    => pg(m,pr(n,bos(f)))}$
tryk11:= {  !#a(~n) => !#aa(n),  !#b(~n) => !#bb(n),  !#c(~n) => !#cc(n) }$
tryk12:= { !#aa(~n) =>  !#b(n), !#bb(~n) =>  !#c(n), !#cc(~n) =>  !#a(n) }$
tryk13:= { !#aa(~n) =>  !#c(n), !#bb(~n) =>  !#a(n), !#cc(~n) =>  !#b(n) }$
tryk14:={ bos(~f,~n,~m,t,t) => pg(m,pr(n,bos(f,t))),
        fer(~f,~n,~m,t) => pg(m,pr(n,bos(f,t))) }$
tryk15:={ bos(~f,~n,~m,~l) => if n equal 0 or n equal 3 then berz(f,n,m,l)
               else if n equal 1 then (-1)**l*berz(f,2,m,l)
               else if n equal 2 then  berz(f,1,m,l),
       bos(~f,~n,~m)  => if n equal 0 or n equal 3 then berz(f,n,m)
               else if n equal 1 then -berz(f,2,m)
               else if n equal 2 then   berz(f,1,m),
       fer(~f,~n,~m)  => if n equal 0 or n equal 3 then firr(f,n,m)
               else if n equal 1 then -firr(f,2,m)
                else if n equal 2 then  firr(f,1,m) }$
%only for chiral
tryk16:={
bos(~f,0,~n) => if not freeof(b_chiral,f)  then
                fun(mkid(f,0),n)+tet(2)*gras(mkid(f,mkid(f,2)),n)-
                tet(2)*tet(1)*fun(mkid(f,0),n+1)/2
        else    if not freeof(b_antychiral,f) then
                fun(mkid(f,0),n)+tet(1)*gras(mkid(f,mkid(f,1)),n) +
                tet(2)*tet(1)*fun(mkid(f,0),n+1)/2
        else    fun(mkid(f,0),n)+tet(1)*gras(mkid(f,mkid(f,1)),n)+
       tet(2)*gras(mkid(f,mkid(f,2)),n)+tet(2)*tet(1)*fun(mkid(f,1),n),
bos(~f,1,~n)   =>  if not freeof(f_chiral,f) then 0 else
                   if not freeof(f_antychiral,f) then
       fun(mkid(f,0),n) - tet(2)*gras(mkid(f,mkid(f,1)),n+1) -
       tet(2)*tet(1)*fun(mkid(f,0),n+1)/2
else  fun(mkid(f,0),n) - tet(2)*gras(mkid(f,mkid(f,2)),n) -
       tet(2)*gras(mkid(f,mkid(f,1)),n+1)/2 -
       tet(2)*tet(1)*fun(mkid(f,0),n+1)/2,

bos(~f,2,~n) => if not freeof(f_chiral,f) then
                fun(mkid(f,0),n) - tet(2)*gras(mkid(f,mkid(f,1)),n+1) -
                tet(2)*tet(1)*fun(mkid(f,0),n+1)/2
        else if not freeof(f_antychiral,f) then 0
        else     fun(mkid(f,1),n) + tet(1)*gras(mkid(f,mkid(f,2)),n) -
       tet(1)*gras(mkid(f,mkid(f,1)),n+1) + tet(2)*tet(1)*fun(mkid(f,1),n+1)/2,

bos(~f,3,~n) => if abra_kadabra = 1 then
   if not freeof(b_chiral,f) then - bos(f,0,n+1) else
   if not freeof(b_antychiral,f) then 0  else
        fun(mkid(f,1),n) - fun(mkid(f,0),n+1)/2 -
        tet(2)*gras(mkid(f,mkid(f,2)),n+1) -
        tet(2)*tet(1)*fun(mkid(f,1),n+1)/2 +
        tet(2)*tet(1)*fun(mkid(f,0),n+2)/4
else if abra_kadabra = 3 then
   if not freeof(b_chiral,f) then - bos(f,0,n+1) else
   if not freeof(b_antychiral,f) then bos(f,0,n+1) else
   2*fun(mkid(f,1),n) - tet(2)*gras(mkid(f,mkid(f,2)),n+1) +
   tet(1)*gras(mkid(f,mkid(f,1)),n+1)+tet(2)*tet(1)*fun(mkid(f,0),n+2)/2,

bos(~f,0,~n,~k) =>
if not freeof(b_chiral,f)  then
   fun(mkid(f,0),n,k)+k*tet(2)*fun(mkid(f,0),n,k-1)*
   (gras(mkid(f,mkid(f,2)),n) -tet(1)*fun(mkid(f,0),n+1,1)/2)
else    if not freeof(b_antychiral,f) then
   fun(mkid(f,0),n,k)+k*tet(1)*fun(mkid(f,0),n,k-1)*
   (gras(mkid(f,mkid(f,1)),n) - tet(2)*fun(mkid(f,0),n+1,1)/2)
else  fun(mkid(f,0),n,k)+
   k*tet(1)*gras(mkid(f,mkid(f,1)),n)*fun(mkid(f,0),n,k-1)+
   k*tet(2)*gras(mkid(f,mkid(f,2)),n)*fun(mkid(f,0),n,k-1)+
+tet(2)*tet(1)*(k*fun(mkid(f,1),n,1)*fun(mkid(f,0),n,k-1)+
k*(k-1)*gras(mkid(f,mkid(f,1)),n)*gras(mkid(f,mkid(f,2)),n)*
fun(mkid(f,0),n,k-2)),

bos(~f,1,~n,~k)   =>
 if not freeof(f_chiral,f) then 0 else
 if not freeof(f_antychiral,f) then
fun(mkid(f,0),n,k) - k*fun(mkid(f,0),n,k-1)*tet(2)*(
gras(mkid(f,mkid(f,1)),n+1) + tet(1)*fun(mkid(f,0),n+1,1)/2)
 else  fun(mkid(f,0),n,k) -k*fun(mkid(f,0),n,k-1)*tet(2)*
    (gras(mkid(f,mkid(f,2)),n) + gras(mkid(f,mkid(f,1)),n+1)/2 +
       tet(1)*fun(mkid(f,0),n+1,1)/2),

bos(~f,2,~n,~k) => if not freeof(f_chiral,f) then
    fun(mkid(f,0),n,k) - k*tet(2)*fun(mkid(f,0),n,k-1)*
     (gras(mkid(f,mkid(f,1)),n+1) + tet(1)*fun(mkid(f,0),n+1)/2)
else if not freeof(f_antychiral,f) then 0
else   fun(mkid(f,1),n,k) + k*tet(1)*fun(mkid(f,1),n,k-1)*
    (gras(mkid(f,mkid(f,2)),n) - gras(mkid(f,mkid(f,1)),n+1) -
    tet(2)*fun(mkid(f,1),n+1,1)/2),

bos(~f,3,~n,~k) => if abra_kadabra = 1 then
     if not freeof(b_chiral,f) then (-1)**k*bos(f,0,n+1,k)
else if not freeof(b_antychiral,f) then 0
else  fun(mkid(f,1),n,mkid(f,0),n+1,k) - k*fun(mkid(f,1),n,mkid(f,0),n+1,k-1)*
        (tet(2)*gras(mkid(f,mkid(f,2)),n+1) +
        tet(2)*tet(1)*fun(mkid(f,1),n+1,1)/2 -
        tet(2)*tet(1)*fun(mkid(f,0),n+2,1)/4)
else if abra_kadabra = 3 then
     if not freeof(b_chiral,f) then (-1)**k*bos(f,0,n+1,k)
else if not freeof(b_antychiral,f) then bos(f,0,n+1,k)
else   2**k*fun(mkid(f,1),n,k) + k*2**(k-1)*fun(mkid(f,1),n,k-1)*
  (- tet(2)*gras(mkid(f,mkid(f,2)),n+1) +
   tet(1)*gras(mkid(f,mkid(f,1)),n+1)+tet(2)*tet(1)*fun(mkid(f,0),n+2,1)/2)
 -k*(k-1)*2**(k-2)*tet(2)*tet(1)*fun(mkid(f,1),n,k-2)*
        gras(mkid(f,mkid(f,1)),n+1)*gras(mkid(f,mkid(f,2)),n+1),

fer(~f,0,~n) => if not freeof(f_chiral,f) then
                gras(mkid(f,mkid(f,1)),n)+tet(2)*fun(mkid(f,1),n) -
                tet(2)*tet(1)*gras(mkid(f,mkid(f,1)),n+1)/2
        else if not freeof(f_antychiral,f) then
                gras(mkid(f,mkid(f,1)),n)+tet(1)*fun(mkid(f,0),n)+
                tet(2)*tet(1)*gras(mkid(f,mkid(f,1)),n+1)/2
        else    gras(mkid(f,mkid(f,1)),n)+tet(1)*fun(mkid(f,0),n)+
        tet(2)*fun(mkid(f,1),n)+tet(2)*tet(1)*gras(mkid(f,mkid(f,2)),n),

fer(~f,1,~n) => if not freeof(b_chiral,f) then 0
          else  if not freeof(b_antychiral,f) then
                gras(mkid(f,mkid(f,1)),n) -
                tet(2)*fun(mkid(f,0),n+1) -
                tet(2)*tet(1)*gras(mkid(f,mkid(f,1)),n+1)/2
          else  gras(mkid(f,mkid(f,1)),n) - tet(2)*fun(mkid(f,1),n)-
                tet(2)*fun(mkid(f,0),n+1)/2 -
                tet(2)*tet(1)*gras(mkid(f,mkid(f,1)),n+1)/2,

fer(~f,2,~n) => if not freeof(b_chiral,f) then
                gras(mkid(f,mkid(f,2)),n) -tet(1)*fun(mkid(f,0),n+1)+
                tet(2)*tet(1)*gras(mkid(f,mkid(f,2)),n+1)/2
        else    if not freeof(b_antychiral,f) then 0
        else    gras(mkid(f,mkid(f,2)),n)+tet(1)*fun(mkid(f,1),n) -
                tet(1)*fun(mkid(f,0),n+1)/2 +
                tet(2)*tet(1)*gras(mkid(f,mkid(f,2)),n+1)/2,

fer(~f,3,~n) => if abra_kadabra = 1 then
        if not freeof(f_chiral,f) then  - fer(f,0,n+1)
        else    if not freeof(f_antychiral,f) then 0
        else   gras(mkid(f,mkid(f,2)),n) - gras(mkid(f,mkid(f,1)),n+1)/2 -
               tet(2)*fun(mkid(f,1),n+1) -
        tet(2)*tet(1)*gras(mkid(f,mkid(f,1)),n+2)/4 -
        tet(2)*tet(1)*gras(mkid(f,mkid(f,2)),n+1)/2
else if abra_kadabar = 3 then
        if not freeof(f_chiral,f) then - fer(f,0,n+1)
  else  if not freeof(f_antychiral,f) then fer(f,0,n+1)
else    2*gras(mkif(f,mkid(f,2)),n) - tet(2)*fun(mkid(f,1),n+1) +
 tet(1)*fun(mkid(f,0),n+1) +tet(2)*tet(1)*gras(mkid(f,mkid(f,1)),n+2)/2,

axp(~f) => axx(bf_part(f,0))*(1+ tet(1)*bf_part(f,1)+
      tet(2)*bf_part(f,2) + tet(2)*tet(1)*(bf_part(f,3)+
        2*bf_part(f,1)*bf_part(f,2))) }$
%***********************************%
%*** module - operators  ***********%
%***********************************%
%differentations
let {   d(1)*fer(~f,~n,~m) => fer(f,n,m+1)+fer(f,n,m)*d(1),
d(1)*bos(~f,~n,~m)         => bos(f,n,m+1)+bos(f,n,m)*d(1),
fer(~f,~n,~m)*d(2)         => -fer(f,n,m+1)+d(2)*fer(f,n,m),
bos(~f,~n,~m)*d(2)         => -bos(f,n,m+1)+d(2)*bos(f,n,m),
d(1)*bos(~f,~n,~m,~l)      => l*bos(f,n,m+1,1)*bos(f,n,m,l-1)+bos(f,n,m,l)*d(1),
bos(~f,~n,~m,~l)*d(2)      => -l*bos(f,n,m+1,1)*bos(f,n,m,l-1)+d(2)*bos(f,n,m,l),

der(~k)*fer(~f,0,~m) => bos(f,k,m)-fer(f,0,m)*der(k) when numberp k and k < 3,
der(~k)*bos(~f,0,~m) => fer(f,k,m)+bos(f,0,m)*der(k) when numberp k and k < 3,
fer(~f,0,~m)*del(~k) => bos(f,k,m)-del(k)*fer(f,0,m) when numberp k and k < 3,
bos(~f,0,~m)*del(~k) => -fer(f,k,m)+del(k)*bos(f,0,m) when numberp k and k < 3,
der(~k)*bos(~f,0,~m,~l) => l*fer(f,k,m)*bos(f,0,m,l-1)+bos(f,0,m,l)*der(k)
                when numberp k and k < 3,
bos(~f,0,~m,~l)*del(~k) => -l*fer(f,k,m)*bos(f,0,m,l-1)+del(k)*bos(f,0,m,l)
                when numberp k and k < 3,
d(1)*axp(~g) => pg(1,g)*axp(g)+axp(g)*d(1),
der(1)*axp(~g) => pr(1,g)*axp(g)+axp(g)*der(1),
der(2)*axp(~g) => pr(2,g)*axp(g)+axp(g)*der(2),
axp(~g)*d(2) => -pg(1,g)*axp(g)+d(2)*axp(g),
axp(~g)*del(1) => -pr(1,g)*axp(g)+del(1)*axp(g),
axp(~g)*del(2) => -pr(2,g)*axp(g)+del(2)*axp(g),
d(1)*fun(~f,~m) => fun(f,m+1)+fun(f,m)*d(1),
fun(~f,~m)*d(2) => -fun(f,m+1)+d(2)*fun(f,m),
d(1)*fun(~f,~n,~m) => m*fun(f,n+1,1)*fun(f,n,m-1)+fun(f,n,m)*d(1),
fun(~f,~n,~m)*d(2) => -m*fun(f,n+1,1)*fun(f,n,m-1)+d(2)*fun(f,n,m),
gras(~f,~m)*d(2) => -gras(f,m+1)+d(2)*gras(f,m),
d(1)*gras(~f,~m) => gras(f,m+1)+gras(f,m)*d(1),
d(1)*axx(~f) => pg(1,f)*axx(f)+axx(f)*d(1),
axx(~f)*d(2) => -pg(1,f)*axx(f)+d(2)*axx(f)}$
%integrations;
let { d(-1)*fer(~f,~n,~m) => if numberp(ww) then
for k:=0:ww-1 sum (-1)**k*fer(f,n,m+k)*d(-1)**(k+1) else
rederr "introduce the precision e.g. give the value of ww > 0",
fer(~f,~n,~m)*d(-2) => if numberp(ww) then
for k:=0:ww-1 sum d(-2)**(k+1)*fer(f,n,m+k) else
rederr "introduce the precision e.g. give the value of ww > 0",
d(-1)*bos(~f,~n,~m) => if numberp(ww) then
for k:=0:ww-1 sum (-1)**k*bos(f,n,m+k)*d(-1)**(k+1) else
rederr "introduce the precision e.g. give the value of ww > 0",
bos(~f,~n,~m)*d(-2) => if numberp(ww) then
for k:=0:ww-1 sum d(-2)**(k+1)*bos(f,n,m+k) else
rederr "introduce the precision e.g. give the value of ww > 0",
d(-1)*bos(~f,~n,~m,~l) => if numberp(ww) then
for k:=0:ww-1 sum (-1)**k*pg(k,bos(f,n,m,l))*d(-1)**(k+1) else
rederr "introduce the precision e.g. give the value of ww > 0",
bos(~f,~n,~m,~l)*d(-2) => if numberp(ww) then
for k:=0:ww-1 sum d(-2)**(k+1)*pg(k,bos(f,n,m,l)) else
rederr "introduce the precision e.g. give the value of ww > 0",
d(-1)*axp(~f) => if numberp(ww) then
for k:=0:ww-1 sum (-1)**k*pg(k,axp(f))*d(-1)**(k+1) else
rederr "introduce the precision e.g. give the value of ww > 0",
axp(~f)*d(-2) => if numberp(ww) then
for k:=0:ww-1 sum d(-2)**(k+1)*pg(k,axp(f)) else
rederr "introduce the precision e.g. give the value of ww > 0",
%acceleration;
dr(~x)*bos(~f,~n,~m) => if numberp(ww) then
for s:=0:ww sum (-1)**s*newton(-x+s-1,-x-1)*bos(f,n,m+s)*dr(x-s)
else rederr "introduce the precision e.g. give the value of ww > 0",
dr(~x)*fer(~f,~n,~m) => if numberp(ww) then
for s:=0:ww sum (-1)**s*newton(-x+s-1,-x-1)*fer(f,n,m+s)*dr(x-s)
else rederr "introduce the precision e.g. give the value of ww > 0",
dr(~x)*bos(~f,~n,~m,~l) => if numberp(ww) then
for s:=0:ww sum (-1)**s*newton(-x+s-1,-x-1)*pg(s,bos(f,n,m,l))*dr(x-s)
else rederr "introduce the precision e.g. give the value of ww > 0",
dr(~x)*fun(~f,~n) => if numberp(ww) then
for s:=0:ww sum (-1)**s*newton(-x+s-1,-x-1)*fun(f,n+s)*dr(x-s)
else rederr "introduce the precision e.g. give the value of ww > 0",
dr(~x)*gras(~f,~n) => if numberp(ww) then
for s:=0:ww sum (-1)**s*newton(-x+s-1,-x-1)*gras(f,n+s)*dr(x-s)
else rederr "introduce the precision e.g. give the value of ww > 0",
dr(~x)*fun(~f,~n,~l) => if numberp(ww) then
for s:=0:ww sum (-1)**s*newton(-x+s-1,-x-1)*pg(s,fun(f,n,l))*dr(x-s)
else rederr "introduce the precision e.g. give the value of ww > 0",
%classical
d(-1)*fun(~f,~n,~m) => if numberp(ww) then
for k:=0:ww-1 sum (-1)**k*pg(k,fun(f,n,m))*d(-1)**(k+1) else
rederr "introduce the precision e.g. give the value of ww > 0",
fun(~f,~n,~m)*d(-2) => if numberp(ww) then
for k:=0:ww-1 sum d(-2)**(k+1)*pg(k,fun(f,n,m)) else
rederr "introduce the precision e.g. give the value of ww > 0",
d(-1)*fun(~f,~n) => if numberp(ww) then
for k:=0:ww-1 sum (-1)**k*fun(f,n+k)*d(-1)**(k+1) else
rederr "introduce the precision e.g. give the value of ww > 0",
fun(~f,~n)*d(-2) => if numberp(ww) then
for k:=0:ww-1 sum d(-2)**(k+1)*fun(f,n+k) else
rederr "introduce the precision e.g. give the value of ww > 0",
d(-1)*gras(~f,~n) => if numberp(ww) then
for k:=0:ww-1 sum (-1)**k*gras(f,n+k)*d(-1)**(k+1) else
rederr "introduce the precision e.g. give the value of ww > 0",
gras(~f,~n)*d(-2) => if numberp(ww) then
for k:=0:ww-1 sum d(-2)**(k+1)*gras(f,n+k) else
rederr "introduce the precision e.g. give the value of ww > 0",
d(-1)*axx(~f) => if numberp(ww) then
for k:=0:ww-1 sum (-1)**k*pg(k,axx(f))*d(-1)**(k+1) else
rederr "introduce the precision e.g. give the value of ww > 0",
axx(~f)*d(-2) => if numberp(ww) then
for k:=0:ww-1 sum d(-2)**(k+1)*pg(k,axx(f)) else
rederr "introduce the precision e.g. give the value of ww > 0" }$
%other time
let
{ d(t)*axp(~f) => axp(f)*d(t)*f,
d(t)*bos(~f,~n,~m) => bos(f,n,m,t,t) + bos(f,n,m)*d(t),
d(t)*fer(~f,~n,~m) => fer(f,n,m,t) +fer(f,n,m)*d(t),
d(t)*bos(~f,~n,~m,~l) => l*bos(f,n,m,l-1)*bos(f,n,m,t,t)+bos(f,n,m,l)*d(t) }$
%******************************************%
%*** module - actions  ********************%
%******************************************%
procedure rzut(rr,n);
       begin scalar ola,ewa;
       ola:=chan(rr);  ewa:=sub(d(-1)=0,d(-2)=0,d(-3)=0,d(-4)=0,ola);
       if n = 0 then return ewa;
       if n = 1 then  ewa:=ewa-sub(der(1)=0,der(2)=0,der(3)=0,d(1)=0,ewa);
       if n = 2 then  ewa:=ewa-sd_part(ewa,0,0)-sd_part(ewa,1,0)*der(1)-
       sd_part(ewa,2,0)*der(2)-sd_part(ewa,0,1)*d(1);
return ewa    end$
procedure sd_part(wr,n,m);
       begin scalar ewa,ola;
       ewa:=sub(d(1)=!@kk,d(2)=!@kk,d(-2)=!@ss,d(-1)=!@ss,
                 d(-3)=!@ss*d(-33),d(-4)=!@ss*d(-44),wr);
       ola:=if m greaterp 0 then coeffn(ewa,!@kk,m) else if m equal 0 then
       sub(!@ss=0,!@kk=0,ewa) else coeffn(ewa,!@ss,-m);
return s_part(sub(!@ss=1,!@kk=1,d(-33)=d(-3),d(-44)=d(-4),ola),n)  end$
procedure d_part(ww,n);
       begin scalar ewa,ola;
       ewa:=sub(d(1)=!@kk,d(2)=!@kk,d(-1)=!@ss,d(-2)=!@ss,
               d(-3)=!@ss*d(-33),d(-4)=d(-44)*!@ss,ww);
       ola:=if n greaterp 0 then coeffn(ewa,!@kk,n) else if n=0 then
       sub(!@kk=0,!@ss=0,ewa) else coeffn(ewa,!@ss,-n);
return sub(d(-33)=d(-3),d(-44)=d(-4),ola)   end$
procedure pr(n,ww);
       begin scalar ewa;
       if n=0 then ewa:=ww;            if n=1 then ewa:=der(1)*ww;
       if n=2 then ewa:=der(2)*ww;
       if n=3 then if abra_kadabra = 3 then ewa:=der(3)*ww else
        ewa:=der(1)*pr(2,ww);
return sub(der(1)=0,der(2)=0,der(3)=0,ewa)   end$
%*********************************%
%*** module adjoint     **********%
%*********************************%
% stp(1),stp(10),stp(20) if does not appeare der or apeapare der(1)*der(2);
% stp(2) if appeare der;
!@rak:={ stp(1)=1, stp(10)=1, stp(20)=1, stp(2)= - 1 }$
procedure cp(xwx);
       begin scalar kap,kap1,ess,k,l;
        if xwx equal 0 then return 0;kap:=length(xwx);
        if numberp(kap) then return cp1(xwx);
        kap1:=first kap;
        matrix !@z_z(kap1,kap1);matrix !@s_s(kap1,kap1);
        for k:=1:kap1 do for l:=1:kap1 do <<
        ess:=sub(!@krr=1,!@krr*xwx(k,l));!@z_z(k,l):=cp1(ess);  clear ess; >>;
        clear !@krr;!@s_s:=tp(!@z_z);clear !@z_z;  return !@s_s  end$
procedure cp1(yyz);
       begin scalar ewa,ola,xx,yyy;
if yyz equal 0 then return 0;  yyy:=if length(yyz) equal 1 and arglength(yyz)
equal -1 then !@*yyz else yyz; factor d,der,del;       ewa:=lyst(yyy);
ola:=for each xx in ewa collect
    begin scalar mew,wem,em1,em2,em,em3,licz,mian;
licz:=num(xx);mian:=den(xx); if numberp(licz) then return xx;
mew:=licz*stp(1);wem:=sub(!@rak,mew);em:=if part(wem,0) equal minus
then -1 else 1;
em1:= cp2(em*wem);
em2:=part(reverse(em1),0):=*;
        return em2*em/mian  end;   remfac d,der,del;
return sub(!@=1,part(ola,0):=+)  end$
procedure cp2(zz);
        begin scalar ewa,ola,ela,el1;
        if arglength(@*zz) equal 2 then return {zz};
        ewa:=(zz where tryk4);
        ola:=zz/ewa;
        ela:=if arglength(!@*ola) equal 2 then {ola} else part(ola,0):=list;
        el1:=append({ewa},ela);
        return el1  end$
%************************************%
%***  module O(2) invariance ********%
%************************************%
procedure odwa(wx);
       begin scalar ewa,ola;
       let tryk15; ewa:=sub(der(1)=-der(20),der(2)=der(10),
            del(1)=-del(20),del(2)=del(10),wx); clearrules tryk15;
       let tryk9;  ola:=ewa; clearrules tryk9;
return sub(der(10)=der(1),der(20)=der(2),del(10)=del(1),del(20)=del(2),ola) end$
%************************************%
%*** module - coefficients **********%
%************************************%
procedure lyst(wx);
       begin scalar ewa,ola,kap,kap1,adam;
       if wx=0 then return {0};
        factor d,der,del;kap:=length(wx);kap1:=arglength(wx);
       if kap equal 1 and kap1 equal -1 then return {wx};
        if kap1>kap then return {wx};  on div; ewa:=wx;
        if part(ewa,0) = plus then adam:=part(ewa,0):=list else
        adam:={ewa};off div; remfac d,der,del;return adam;   end$
procedure lyst1(wy);
       begin scalar ewa,ola;   ewa:=lyst(wy);
       ola:=(ewa where tryk3);return ola  end$
procedure lyst2(wy);
       begin scalar ewa,ola;   ewa:=lyst(wy);
       ola:=(ewa where tryk4);return ola  end$
%************************************%
%*** module - gradients *************%
%************************************%
procedure war(wa,f);
       begin scalar ewa,ola,adam,mew;
       let tryk;       ewa:=d(f)*wa-wa*d(f);   clearrules tryk;
       ola:=(ewa where tryk1);
       ewa:=sub(d(1)=0,der(1)=0,der(2)=0,der(3)=0,ola);
       if ewa=0 then return 0; adam:=lyst(ewa); mew:=(adam where tryk3);
        return if mew equal 0 then {} else mew  end$
procedure dyw(wa,f);
       begin scalar ewa,ola;
         ewa:=(d(f)*wa-wa*d(f) where tryk);
        ola:=(ewa where tryk2);
       ewa:=sub(d(1)=0,der(1)=0,der(2)=0,der(3)=0,ola);
       if ewa=0 then return 0;
return lyst(ewa) end$

procedure gra(wa,f);
        begin scalar ewa,ola;
        ewa:=(d(f)*wa-wa*d(f) where tryk); ola:=(ewa where tryk1);
       return sub(d(1)=0,der(1)=0,der(2)=0,der(3)=0,ola) end$
%***************************************%
%*** module - coordinates **************%
%***************************************%
procedure fpart(wx);
       begin scalar ewa,ola,adam;
       ewa:=if abra_kadabra = 2 then (wx where tryk5) else (wx where tryk16);
       ola:=sub(tet(1)=!#qw,tet(2)=!#qq,ewa);
       adam:= {coeffn(coeffn(ola,!#qw,0),!#qq,0),
               coeffn(coeffn(ola,!#qw,1),!#qq,0),
               coeffn(coeffn(ola,!#qw,0),!#qq,1),
               coeffn(coeffn(ola,!#qw,1),!#qq,1)};   return adam end$
procedure bpart(wx);
        begin scalar ewa,ola,adam;
        ewa:=if abra_kadabra = 2 then (wx where  tryk5) else (wx where tryk16);
        let tryk6; ola:=sub(tet(1)=!#qw,tet(2)=!#qq,ewa);clearrules tryk6;
       adam:= {coeffn(coeffn(ola,!#qw,0),!#qq,0),
               coeffn(coeffn(ola,!#qw,1),!#qq,0),
               coeffn(coeffn(ola,!#qw,0),!#qq,1),
               coeffn(coeffn(ola,!#qw,1),!#qq,1)};      return adam end$
%******************************************%
%*** module combinations ******************%
%******************************************%
procedure koza(wx,wi,wn);
        begin scalar ew1,ew2,am;
        ew3:=part(wx,3);ew1:=part(wx,1);
        am:=
if ew3 eq f and wi = 0 or ew3 eq f and wi = 3 then
                !@x_y*!@g_g(ew1,wi,wn) else
if ew3 eq f and wi = 1 or ew3 eq f and wi = 2 then
                !@x_y**2*!@f_f(ew1,wi,wn) else
if ew3 eq b and wi = 0 or ew3 eq b and wi = 3 then
                !@x_y**2*!@f_f(ew1,wi,wn) else
if ew3 eq b and wi = 1 or ew3 eq b and wi = 2 then
                !@x_y*!@g_g(ew1,wi,wn);  return am end$
procedure w_comb(as,m,a,bb);
       begin scalar kap,ewa,ola,wic,wid,wod,wod1,wx,k,s,!*precise;
        kap:=length(as);

       if m = 0 then return 0;if m = 0.5 then return w_comb1(as,a,bb);
       (!#l)^(m+1):=0;(!#l)^(m+1/2):=0;
       ewa:=for s:=0:floor(m) sum  for k:=1:kap sum
               (!#l)^(part(as,k,2)+s)*(koza(part(as,k),0,s)*2+
               (!#l)*koza(part(as,k),3,s)+
               (!#l)^(1/2)*koza(part(as,k),1,s)+
               (!#l)^(1/2)*koza(part(as,k),2,s));
       ola:=ewa;wic:=ewa;
       for k:=0:floor(m) do << ola:=ewa*ola;
       ewa:=for s:=0:m-k+1 sum  for r:=1:kap sum
                         (!#l)^(part(as,r,2)+s)*(koza(part(as,r),0,s)*2+
                         (!#l)*koza(part(as,r),3,s)+
                         (!#l)^(1/2)*koza(part(as,r),1,s)+
                         (!#l)^(1/2)*koza(part(as,r),2,s)); wic:=wic+ola;>>;
       wid:=sub((!#l)=(!#ll)^2,wic); wod:=coeffn(wid,(!#ll),2m);
       wod:=if bb eq b then  sub(!@x_y=0,wod) else
            if bb eq f then  coeffn(wod,!@x_y,1);
        wod1:=lyst((wod where tryk7));  clear (!#l)^(m+1),(!#l)^(m+1/2);
       kap:=length(wod1);ewa:=0; for k:=1:kap do <<adam:=
       if wod1 = 0 then 0 else  part(wod1,k);
       ola:=if adam = 0 then 0 else
       if part(adam,0)=minus then -adam else adam;
       wx:=if ola = 0 then 1 else
       if part(ola,0)=times then
                if fixp(part(ola,1)) then part(ola,1) else 1 else 1;
       ewa:=ewa+mkid(a,k)*ola/wx;>>;
return ewa end$

procedure w_comb1(as,a,bb);
begin scalar ew,kap1,ew1,kap;
kap:=length(as);
ew:=for n:=1:kap join
if part(as,n,2) neq  1/2 then  {} else
if part(as,n,3) eq f and bb eq f then {fer(part(as,n,1),0,0)} else
if part(as,n,3) eq f and bb eq b then {} else
if part(as,n,3) eq b and bb eq f then {} else
if part(as,n,3) eq b and bb eq b then {bos(part(as,n,1),0,0)};
kap1:=length(ew);
ew1:=if kap1 = 0 then 0 else
for n:=1:kap1 sum mkid(a,n)*part(ew,n);
return ew1 end$

procedure fcomb(as,n,b,bb);
       begin scalar ewa,ola,ala,k,kap,wx,wy,kap1,wod,wod1,ema,wz,wz1;
       operator b;ewa:=w_comb(as,n,a,bb);kap:=length(as);ala:={};
       wz:=ewa;wz1:=ewa;ema:={};
       for k:=1:kap do << wz:=sub(part(as,k,1)=0,wz);
       wx:=wz1-wz; wz1:=wz;ema:=append(ema,{{wx}});>>;
       for k:=1:kap do << wx:=dyw(part(ema,k,1),part(as,k,1));
       wy:=if wx=0 then {} else wx;
       ala:=append(ala,wy);>>; kap1:=length(ala); ewa:=0;
       for k:=1:kap1 do << wod:=part(ala,k);
       wod1:=(wod where tryk3);ewa:=ewa+b(k)*wod/wod1;>>; return ewa end$

procedure pse_ele(n,ww,ss);
       begin scalar ewa,ola,kap,k,maj,maj1,ela;
       ewa:=0;
        operator ss;
        for k:=1:n do << ewa:=ewa+
(w_comb(ww,k,mkid(mkid(a,k),a),b)+
 w_comb(ww,k-1/2,mkid(mkid(a,k),b),f)*der(1) +
 w_comb(ww,k-1/2,mkid(mkid(a,k),c),f)*der(2)+
 w_comb(ww,k-1,mkid(mkid(a,k),d),b)*(if abra_kadabra = 3 then
der(3) else  der(1)*der(2)))*d(1)**(n-k);>>;
        remfac fer,bos; kap:=length(ewa); ola:=0;
        for k:=1:kap do << maj:=if ewa = 0 then 0 else
        if kap equal 1 then ewa else part(ewa,k);
        maj1:=if maj = 0 then 1 else (maj where tryk4);
        ola:=ola+ss(k+1)*maj/maj1;>>;
        ela:=ss(0)*d(1)**n+ss(1)*(if abra_kadabra = 3 then der(3) else
        der(1)*der(2))*d(1)**(n-1)+ola;factor fer,bos;
        if abra_kadabra = 3 then ela:=(ela where {der(1)*der(2) => der(3)});
       return ela end$
%********************************************%
%*** module jacobi  *************************%
%********************************************%
%wim as {bos(f)=>expression};
procedure n_gat(pp,wim);
        begin scalar kap,niech,zyje;
       kap:=length(wim);
       niech:=gato(pp); let wim;zyje:=(niech where tryk10);
        clearrules wim;
       return zyje end$
procedure gato(p);
       begin scalar as,ess,mess;
       if numberp(length(p)) then return gat1(p);
       as:=first length(p);       matrix !#zz(as,as);
       for k:=1:as do   for l:=1:as do
        << ess:=(!#zab*p(k,l) where tryk8);
   mess:=(ess/!#zab where tryk9);!#zz(k,l):=sub(eps=0,df(mess,eps));>>;
       return !#zz   end$
procedure gat1(p);
       begin scalar ess,zz,mess;
        ess:=(p  where tryk8);  mess:=(ess where tryk9);
       zz:=sub(eps=0,df(mess,eps)); return zz end$
% p is a hamiltonian operator in d,der;
% w is a list of functions with the ordering such way that
%first corresponds to the (1,1) element of p corresponds to {f,f};
% m is a list of components of the test vecor functions;
procedure fjacob(p,w);
        begin scalar as,as1,es1,wod0,wod1,wod2,wod3,wodx;
       if numberp(length(p)) then return jacob1(p,w);
       as:=first length(p); matrix !#ala(as,as),!#ela(as,as);
       !#ala:=gato(p);   operator !#a,!#b,!#c;
       as1:=for k:=1:as collect for l:=1:as sum
               sub(d(1)=0,der(1)=0,der(2)=0,p(k,l)*bos(!#b(l),0,0));
       for k:=1:as do << bos(part(w,k)):=part(as1,k);>>;
       !#ela:=(!#ala where tryk10);
       wod:=for k:=1:as sum for l:=1:as sum
                bos(!#c(k),0,0)*!#ela(k,l)*bos(!#a(l),0,0);
       wod1:=sub(d(1)=0,der(1)=0,der(2)=0,wod);
%permutation;
wodx:=(wod1 where tryk11); wod2:=(wodx where tryk12);
wod3:=(wodx where tryk13); return wod1+wod2+wod3 end$
procedure jacob(p,w,m);
        begin scalar woda;
        woda:=for wx:=1:3 sum
        begin scalar trys,as1,as,wod;
      operator !#a,!#b,!#c,!@a,!@b,!@c;
        as:=first length(p);
       trys:=for k:=1:as join {
       !@a(k)=if k equal first(m) then !#a(k) else 0,
       !@b(k)=if k equal second(m) then !#b(k) else 0,
       !@c(k)=if k equal third(m) then !#c(k) else 0};
       let trys; matrix !@ala(as,as),!@ela(as,as);
        !@ala:=gato(p);
       as1:=for k:=1:as collect for l:=1:as sum
               sub(d(1)=0,der(1)=0,der(2)=0,p(k,l)*bos(
        if wx = 1 then !@b(l) else
        if wx = 2 then !@c(l) else
        if wx = 3 then !@a(l),0,0));
       for k:=1:as do <<  bos(part(w,k)):=part(as1,k);>>;
       !@ela:=(!@ala where tryk10);
       wod:=for k:=1:as sum for l:=1:as sum
bos(if wx = 1 then !@c(k) else
    if wx = 2 then !@a(k) else
    if wx = 3 then !@b(k),0,0)*!@ela(k,l)*
bos(if wx = 1 then !@a(l) else
    if wx = 2 then !@b(l) else
    if wx = 3 then !@c(l),0,0);
        for k:=1:as do clear  !@a(k),!@b(k),!@c(k),bos(part(w,k));
        return sub(d(1)=0,der(1)=0,der(2)=0,wod)  end;  return woda end$
procedure jacob1(p,w);
        begin scalar ala,ela,wod,wod1,wod2,wodx,ewa;
        ala:=gat1(p);bos(w):=sub(d(1)=0,der(1)=0,der(2)=0,p*bos(!#b,0,0));
let tryk10;ela:=ala;clearrules tryk10;wod:=bos(!#c,0,0)*ela*bos(!#a,0,0);
       wod1:=sub(d(1)=0,der(1)=0,der(2)=0,wod);
%permutation;
        wodx:=sub(!#a=!#aa,!#b=!#bb,!#c=!#cc,wod1);
        wod2:=sub(!#aa=!#b,!#bb=!#c,!#cc=!#a,wodx);
        wod3:=sub(!#aa=!#c,!#bb=!#a,!#cc=!#b,wodx);
        clear bos(w);
        return wod1+wod2+wod3 end$
%************************************************
%********* module macierz ***********************
%************************************************
%wx pse_ele
%xx f (fermion)  or b (boson)
%yy bosonic part or fermionic part
procedure macierz(wx,xx,yy);
        begin scalar ewa,ola,ew1,ew2;
        matrix !@z_z_x(4,4);   ewa:=if xx eq f then
 sub(der(1)=0,der(2)=0,der(3)=0,d(1)=0,wx*fer(!#z_z,0,0)) else
 if xx eq b then sub(der(1)=0,der(2)=0,der(3)=0,d(1)=0,wx*bos(!#z_z,0,0)) else
        rederr "wrong value of second  argument which should be b or f";
 ola:=if yy eq b then bpart(ewa) else if yy eq f then fpart(ewa) else
        rederr "wrong value of third  argument which should be b or f";
        ew1:=(ola where
        {fun(!#z_z0,~n) => ber(1,n),fun(!#z_z1,~n) => ber(2,n),
gras(!#z_z!#z_z1,~n) => fir(1,n),gras(!#z_z!#z_z2,~n) => fir(2,n)});
        ew2:=(ew1 where {
        ber(1,~n) => !#s_s*d(1)^n, ber(2,~n) => !#s_s^4*d(1)^n,
        fir(1,~n) => !#s_s^2*d(1)^n,fir(2,~n) => !#s_s^3*d(1)^n});
for k:=1:4 do for l:=1:4 do !@z_z_x(k,l):=chan(coeffn(part(ew2,k),!#s_s,l));
        return !@z_z_x end$

%********************************************%
%*** module dot_ham *************************%
%********************************************%
procedure dot_ham(ww,mm);
       begin scalar ewa,ola,ala,as;
       as:=length(ww); ewa:=d(t)*mm-mm*d(t);
        for k:=1:as do bos(part(part(ww,k),1),t):=part(part(ww,k),2);
       ola:=(ewa where tryk14);
       for k:=1:as do clear bos(part(part(ww,k),1),t);
return ola end$
%module supersymmetric integration

%############################################################################
%#########    F U N C T I O N A L    S U S Y   I N T E G R A T I O N   ######
%#########           O N L Y    F O R   T R A D                        ######
%############################################################################
%  s_int(number,expression,variable)
%  numbers corespond to integration over: 0 => d(1),
%  1=> der(1), 2=> der(2), 3 => der(1)*der(2)
%  variable {f,g,...} the names of the superfunctions
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%% G L O B A L   A C T I O N %%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
let {
waga(~k,~s,~m) =>
2*m+delta(k,s)+2*delta(3,s)+delta(3-k,s)*(if m>0 then 1 else 0),
        s_s(~f,~n) =>1, s_s(1,~f,~n)   => 1,
der(1)*del(-1)=>1,      der(2)*del(-2) => 1,       der(3)*del(-3) =>1,
del(0)=>d(-3),          der(1)*del(-3) => 1,       der(2)*del(-3) =>del(-3),
der(1)*der(2)*del(-3)=>1
}$
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%&&&&&&&&&&&&&&&&&
%%%% L O C A L    A C T I O N %%%%%%%%%%%%%%%%&&&&&&&&&&&&&&&&&
%##############################################################
%%%%%%%%%%%%%%%%%%%%%%%         n_dyw    %%%%%%%%%%%%%%%%%%%%%%
%##############################################################
%scaling
dryk:=  {d(~f,~n)*fer(~g,~k,~m) => fer(g,k,m)*d(f,n+delta(f,g)),
         d(~f,~n)*bos(~g,~k,~m) => bos(g,k,m)*d(f,n+delta(f,g)),
         d(~f,~n)*zan(~g,~k,~m) => zan(g,k,m)*d(f,n+1),
         d(~f,~n)*zen(~g,~k,~m) => zen(g,k,m)*d(f,n+1)
}$
%wariation
wariat_0:={
        zen(~f,~k,~n) => (-1)^n*fer(f,k,0)*d(1)^n,
        zan(~f,~k,~n) => (-1)^n*bos(f,k,0)*d(1)^n
}$
wariat_1:={
        zen(~f,~k,~n) => if k = 3 or k = 1 then
(-1)^(n+1)*bos(f,k-1,0)*der(1)*d(1)^n else (-1)^n*fer(f,k,0)*d(1)^n ,

        zan(~f,~k,~n) => if k = 3 or k = 1 then
(-1)^n*fer(f,k-1,0)*der(1)*d(1)^n else (-1)^n*bos(f,k,0)*d(1)^n
}$
wariat_2:={
        zen(~f,~k,~n) => if k = 3 or k = 2 then
(-1)^(k-1+n)*bos(f,k-2,0)*der(2)*d(1)^n else (-1)^n*fer(f,k,0)*d(1)^n ,

        zan(~f,~k,~n) => if k = 3 or k = 2 then
(-1)^(k+n)*fer(f,k-2,0)*der(2)*d(1)^n else (-1)^n*bos(f,k,0)*d(1)^n
}$
wariat_3:={
zen(~f,0,~n) =>  if n > 1 then  (-1)^(n-1)*(-fer(f,0,0)*d(1)^n
                + n*d(1)*fer(f,0,0)*d(1)^(n-1)) else fer(f,0,n) ,
zen(~f,1,~n) =>
        (-1)^n*(fer(f,1,0)*d(1)^n + n*d(1)*bos(f,0,0)*der(1)*d(1)^(n-1)),
zen(~f,2,~n) =>
        (-1)^n*(fer(f,2,0)*d(1)^n + n*d(1)*bos(f,0,0)*der(2)*d(1)^(n-1)),
zen(~f,3,~n)  =>
        -zen(f,0,n)*der(1)*der(2) + zan(f,1,n)*der(2) - zan(f,2,n)*der(1),

zan(~f,0,~n) =>  if n>1 then (-1)^(n-1)*(-bos(f,0,0)*d(1)^n
                + n*d(1)*bos(f,0,0)*d(1)^(n-1)) else bos(f,0,n),
zan(~f,1,~n) =>
        (-1)^n*(bos(f,1,0)*d(1)^n - n*d(1)*fer(f,0,0)*der(1)*d(1)^(n-1)),
zan(~f,2,~n) =>
        (-1)^n*(bos(f,2,0)*d(1)^n - n*d(1)*fer(f,0,0)*der(2)*d(1)^(n-1)),
zan(~f,3,~n)  =>
        -zan(f,0,n)*der(1)*der(2) - zen(f,1,n)*der(2)+zen(f,2,n)*der(1)
}$
%###########################################################
%%%%%%%%%%%%%%%%%%%%%%          maxi     %%%%%%%%%%%%%%%%%%%
%###########################################################
szukaj0:={
byk(~n,~g)*fer(~f,~k,~m) => if n<=2*m then fer(f,k,m)*byk(2*m,f)
                                        else fer(f,k,m)*byk(n,g),
byk(~n,~g)*bos(~f,~k,~m) => if n<=2*m then bos(f,k,m)*byk(2*m,f)
                                        else bos(f,k,m)*byk(n,g)
}$
szukaj1:={
byk(~k,~g)*fer(~f,~n,~m) => if k <= waga(1,n,m) then
        fer(f,n,m)*byk(waga(1,n,m),f) else fer(f,n,m)*byk(k,g),

byk(~k,~g)*bos(~f,~n,~m) =>if k <= waga(1,n,m) then
        bos(f,n,m)*byk(waga(1,n,m),f) else bos(f,n,m)*byk(k,g)
}$
szukaj2:={
byk(~k,~g)*fer(~f,~n,~m) => if k <= waga(2,n,m) then
        fer(f,n,m)*byk(waga(2,n,m),f) else fer(f,n,m)*byk(k,g),

byk(~k,~g)*bos(~f,~n,~m) => if k<= waga(2,n,m) then
        bos(f,n,m)*byk(waga(2,n,m),f) else bos(f,n,m)*byk(k,g)
}$
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
szukaj3:={
byk(~k,~g)*fer(~f,0,~m) => if m < 2 then fer(f,0,m)*byk(k,g) else
<< if k <= 2*m then fer(f,0,m)*byk(2*m,f) else  fer(f,0,m)*byk(k,g) >>,

byk(~k,~g)*fer(~f,1,~m) => if m < 1 then fer(f,1,m)*byk(k,g) else
<< if k <= 2*m+1 then fer(f,1,m)*byk(2*m+1,f) else fer(f,1,m)*byk(k,g) >>,

byk(~k,~g)*fer(~f,2,~m) => if m < 1 then fer(f,2,m)*byk(k,g) else
<< if k <= 2*m+1 then fer(f,2,m)*byk(2*m+1,f) else fer(f,2,m)*byk(k,g) >> ,

byk(~k,~g)*fer(~f,3,~m) => if k <= 2*m+2 then fer(f,3,m)*byk(2*m+2,f)
                else fer(f,3,m)*byk(k,g),

byk(~k,~g)*bos(~f,0,~m) => if m < 2 then bos(f,0,m)*byk(k,g) else
<< if k <= 2*m then bos(f,0,m)*byk(2*m,f) else   bos(f,0,m)*byk(k,g) >> ,

byk(~k,~g)*bos(~f,1,~m) => if m < 1 then bos(f,1,m)*byk(k,g) else
<< if k <= 2*m+1 then bos(f,1,m)*byk(2*m+1,f) else bos(f,1,m)*byk(k,g) >>,

byk(~k,~g)*bos(~f,2,~m) => if m < 1 then bos(f,2,m)*byk(k,g) else
<< if k <= 2*m+1 then bos(f,2,m)*byk(2*m+1,f) else bos(f,2,m)*byk(k,g) >>,

byk(~k,~g)*bos(~f,3,~m) => if k<=2*m+2 then bos(f,3,m)*byk(2*m+2,f)
                else bos(f,3,m)*byk(k,g)
}$
%###########################################################################
poszukaj0:={
fer(~f,~s,~m)*r_r(~k,~g) => if k = 2*m and g equal f then
                        r_r(2*m,f)*zen(f,s,m) else r_r(k,g)*fer(f,s,m),
bos(~f,~s,~m)*r_r(~k,~g) => if k = 2*m and g equal f then
                        r_r(2*m,f)*zan(f,s,m) else r_r(k,g)*bos(f,s,m)
}$
poszukaj1:={
fer(~f,~s,~m)*r_r(~k,~g) => if k = waga(1,s,m) and g equal f
        then r_r(k,f)*zen(f,s,m) else r_r(k,g)*fer(f,s,m) ,

bos(~f,~s,~m)*r_r(~k,~g) => if k = waga(1,s,m) and g equal f
        then r_r(k,f)*zan(f,s,m) else r_r(k,g)*bos(f,s,m)
}$
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
poszukaj2:={
fer(~f,~s,~m)*r_r(~k,~g) => if k = waga(2,s,m) and g equal f
        then r_r(k,f)*zen(f,s,m) else r_r(k,g)*fer(f,s,m),

bos(~f,~s,~m)*r_r(~k,~g) => if k = waga(2,s,m) and g equal f
        then r_r(k,f)*zan(f,s,m) else r_r(k,g)*bos(f,s,m)
}$
poszukaj3:={
fer(~f,0,~m)*r_r(~k,~g) => if m < 2 then  r_r(k,g)*fer(f,0,m) else
        << if k = 2*m and f equal g then r_r(1,2*m,f)*zen(f,0,m)
                else r_r(k,g)*fer(f,0,m) >>,
fer(~f,1,~m)*r_r(~k,~g) => if m < 1 then r_r(k,g)*fer(f,1,m) else
        <<if k = 2*m+1 and f equal g then r_r(1,2*m+1,f)*zen(f,1,m) else
                        r_r(k,g)*fer(f,1,m)>>,
fer(~f,2,~m)*r_r(~k,~g) => if m < 1 then r_r(k,g)*fer(f,2,m) else
        << if k = 2*m+1 and f e        qual g then r_r(1,2*m+1,f)*zen(f,2,m) else
        r_r(k,g)*fer(f,2,m) >> ,
fer(~f,3,~m)*r_r(~k,~g) => if k = 2*m+2 and f equal g then
        r_r(1,2*m+2,f)*zen(f,3,m) else r_r(k,g)*fer(f,3,m),


bos(~f,0,~m)*r_r(~k,~g) => if m < 2 then  r_r(k,g)*bos(f,0,m) else
        << if k = 2*m and f equal g then r_r(1,2*m,f)*zan(f,0,m)
                else r_r(k,g)*bos(f,0,m) >>,
bos(~f,1,~m)*r_r(~k,~g) => if m < 1 then r_r(k,g)*bos(f,1,m) else
        << if k = 2*m+1 and f equal g then r_r(1,2*m+1,f)*zan(f,1,m)
                else r_r(k,g)*bos(f,1,m) >>,
bos(~f,2,~m)*r_r(~k,~g) => if m < 1 then r_r(k,g)*bos(f,2,m) else
        << if k = 2*m+1 and f e        qual g then r_r(1,2*m+1,f)*zan(f,2,m)
                else r_r(k,g)*bos(f,2,m) >> ,
bos(~f,3,~m)*r_r(~k,~g) => if k = 2*m+2 and f equal g then
        r_r(1,2*m+2,f)*zan(f,3,m) else r_r(k,g)*bos(f,3,m)

}$
%#######################################################################
%%%%%%%%%%%%%%%%%%%%% I N T E G R A T I O N  %%%%%%%%%%%%%%%%%%%%%%%%%%%
%#######################################################################
calkuj0:={
zen(~f,~n,~m) => fer(f,n,m-1),zan(~f,~n,~m) => bos(f,n,m-1)
}$
pocalkuj0:={
zen(~f,~n,~m) => -fer(f,n,m-1)*d(1),
zan(~f,~n,~m) => -bos(f,n,m-1)*d(1)
}$
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
calkuj1:={
zen(~f,~k,~n) => if k = 3 or k = 1 then bos(f,k-1,n) else
        if k = 0 and n > 0 or k = 2 and n > 0 then bos(f,k+1,n-1),
zan(~f,~k,~n) => if k = 3 or k = 1 then fer(f,k-1,n) else
        if k = 0 and n > 0 or k = 2 and n > 0 then fer(f,k+1,n-1)
}$
pocalkuj1:={
zen(~f,~k,~n) =>  if k = 3 or k = 1 then -bos(f,k-1,n)*der(1) else
    if k = 0 and n > 0 or k = 2 and n > 0 then -bos(f,k+1,n-1)*der(1),
zan(~f,~k,~n) =>  if k = 3 or k = 1 then fer(f,k-1,n)*der(1) else
    if k = 0 and n > 0 or k = 2 and n > 0 then fer(f,k+1,n-1)*der(1)
}$
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
calkuj2:={
zen(~f,~k,~n) =>   if k = 3 or k = 2 then (-1)^k*bos(f,k-2,n) else
    if k = 0 and n > 0 or k = 1 and n > 0 then  (-1)^k*bos(f,k+2,n-1) ,
zan(~f,~k,~n) =>   if k = 3 or k = 2 then (-1)^k*fer(f,k-2,n) else
    if k = 0 and n > 0 or k = 1 then (-1)^k*fer(f,k+2,n-1)
}$
pocalkuj2:={
zen(~f,~k,~n) => if k = 3 or k = 2 then    -(-1)^k*bos(f,k-2,n)*der(2) else
if k = 0 and n > 0 or k = 1 and n > 0 then -(-1)^k*bos(f,k+2,n-1)*der(2) ,
zan(~f,~k,~n) =>   if k = 3 or k = 2 then  (-1)^k*fer(f,k-2,n)*der(2) else
if k = 0 and n > 0 or k = 1 and n > 0 then (-1)^k*fer(f,k+2,n-1)*der(2)
}$
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
calkuj3:={
zen(~f,0,~n) => if n < 2 then fer(f,0,n) else -fer(f,3,n-2),
zen(~f,1,~n) => if n < 1 then fer(f,1,0) else  fer(f,2,n-1),
zen(~f,2,~n) => if n < 1 then fer(f,2,0) else -fer(f,1,n-1),
zen(~f,3,~n) => fer(f,0,n),

zan(~f,0,~n) => if n < 2 then fer(f,0,n) else -bos(f,3,n-2),
zan(~f,1,~n) => if n < 1 then fer(f,1,0) else  bos(f,2,n-1),
zan(~f,2,~n) => if n < 1 then fer(f,2,0) else -bos(f,1,n-1),
zan(~f,3,~n) => bos(f,0,n)
}$
pocalkuj3:={
zen(~f,0,~n) => -bos(f,2,n-1)*der(2)-bos(f,1,n-1)*der(1)+
                        fer(f,3,n-2)*der(1)*der(2) ,
zen(~f,1,~n) =>  bos(f,3,n-1)*der(2)-bos(f,0,n)*der(1)-
                        fer(f,2,n-1)*der(1)*der(2) ,
zen(~f,2,~n) => -bos(f,0,n)*der(2)-bos(f,3,n-1)*der(1)+
                        fer(f,1,n-1)*der(1)*der(2),
zen(~f,3,~n) =>   -fer(f,0,n)*der(1)*der(2)+bos(f,1,n)*der(2)-bos(f,2,n)*der(1),

zan(~f,0,~n) => bos(f,3,n-2)*der(1)*der(2)+fer(f,2,n-1)*der(2)+
                        fer(f,1,n-1)*der(1),
zan(~f,1,~n) => -fer(f,3,n-1)*der(2)+fer(f,0,n)*der(1)-
                        bos(f,2,n-1)*der(1)*der(2) ,
zan(~f,2,~n) => fer(f,3,n-1)*der(1)+fer(f,0,n)*der(2)+
                        bos(f,1,n-1)*der(1)*der(2) ,
zan(~f,3,~n) => -bos(f,0,n)*der(1)*der(2)-fer(f,1,n)*der(2)+fer(f,2,n)*der(1)
}$
%$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
%$$$$$                                          $$$$$$
%$$$$$           P R O C E D U R E S               $$$$$$
%$$$$$                                          $$$$$$
%$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
%%%%%%%%%%%%%%%%%           one maximum   %%%%%%%%%%%%%%%%%%%%%
procedure maxi(wrt,wx);
        begin scalar kr,kr1,ew1,ew2,ew3,ew4,ew5;
if wx equal 0 then return {0,0};
                kr:=num wx;                kr1:=den wx;
ew1:=(byk(0,0)*kr where (help!* := mkid(szukaj,wrt)));
        ew2:=(ew1 where {byk(~n,~f) => (!l_a_!@m)^n*p_p(n,f)});
                ew2:=sub(p_p=r_r,lcof(ew2,!l_a_!@m));
                        ew2:=(ew2 where (help!* := mkid(poszukaj,wrt)));
                                ew3:=sub(r_r=s_s,ew2);
ew1:=if part(ew3,0) equal minus then -1 else 1;
        ew4:=sub(x_x=0,if length(ew1*ew3) < arglength(ew1*ew3)
        then ew1*ew3 else part(ew1*ew3+x_x,1));
                  ew5:=kr-sub(zen=fer,zan=bos,ew1*ew4);
                        return {ew1*ew4/kr1,ew5/kr1} end$
%#################################################################
%%%%%%%%%%%%%%%%%     dywergent terms  %%%%%%%%%%%%%%%%%%%%%%%%%%%
%#################################################################
procedure n_dyw(wrt,wx,wz);
        begin scalar eks0,eks,eks1,eks2,osa1,osa2,osa3,osa4;
kap:=length wz;
        eks:=num wx;             eks0:=den wx;
                eks1:=if part(eks,0) equal minus then -1 else 1;
                        eks2:=eks1*eks;osa4:=0;
for k:=1:kap do <<
osa1:=eks2-sub(part(wz,k)=0,eks2);
        eks2:=eks2-osa1;
                osa2:=sub(d(part(wz,k))=0,(d(part(wz,k))*osa1 where tryk));
%scaling
osa3:=(d(part(wz,k),0)*osa2 where dryk);
        osa3:=(osa3 where {d(~f,~n) => 1/n when n>0});
%end scaling
osa3:=sub(der(1)=0,d(1)=0,der(3)=0,der(2)=0,
                           (osa3 where (help!* := mkid(wariat_,wrt))));
                        osa4:=osa4+osa3;
>>;
        return {wx-eks1*osa4/eks0,eks1*osa4/eks0} end$
%&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
%***************                        *******************************
%***************      MAIN PROCEDURE    *******************************
%***************                        *******************************
%&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
procedure s_int(wrt,wx,wz);
        begin scalar kak,kak1,kak2,pak,pak1,pak2,kap,pak3,pak4;
if wx equal 0 then return 0;
if abra_kadabra equal 1 and wrt > 0 or abra_kadabra equal 3
and wrt > 0 then
rederr " **** I T is Impossible to define in a proper
manner this integral => use trad representation for computation only";
        kak:=n_dyw(wrt,wx,wz);
                                kak1:=first kak;
                                        kak2:=second kak;
%if kak2 neq 0 then return del(-wrt)*wx;
pak:=hom(kak1);
        pak1:=first first pak;            pak2:=second pak;
                kap:=length pak2;
pak3:=if pak2 equal 0 then 0 else for k:=1:kap sum cal(wrt,part(pak2,k));
        pak4:= pak1*pak3+del(-wrt)*kak2 ;
                        return pak4   end$
procedure cal(wrt,wx);
        begin scalar wem,wem1,wem2,wem3,wem4,wem5,wem6,z_z_z;
if wx equal 0 then return 0;
        wem:=maxi(wrt,wx);      wem1:=first(wem);        wem2:=second(wem);
                                z_z_z:=0;
while wem1 neq 0 do <<
wem3:=sub(zen=fer,zan=bos,wem1);
    wem4:=(wem1 where (help!* := mkid(calkuj,wrt)));
           wem5:=sub(der(3)=0,der(1)=0,d(1)=0,der(2)=0,(-wem1 where
                (help!* := mkid(pocalkuj,wrt))));
if wem4 = 0 then
z_z_z:=z_z_z+del(-wrt)*wem3 else   <<
        xxx:=(-!l_a_!@m*wem4+wem3+wem5 where {wem3=>koz});
                wem6:=(rhs first solve(xxx,koz));clear xxx;
                        z_z_z:=z_z_z+coeffn(wem6,!l_a_!@m,1);
wem6:=sub(!l_a_!@m=0,wem6);            wem2:=wem2+wem6  >>;
wem6:=maxi(wrt,wem2);
        wem1:=first wem6;         wem2:=second wem6;
>>;
        return z_z_z                    end$

procedure hom(wx);
        begin scalar zet1,zet2,iks,iks1,iks2;
if wx equal 0 then return {{0},0};
        iks:=num wx;iks2:=den wx;
                iks1:=if part(iks,0) equal minus then -1 else 1;
                        iks:=iks1*iks;
zet1:=(iks where {
fer(~f,~k,~n) => !&a(f)*!&a(!@)^(2n+(if k = 1 or k = 2 then 1 else
                                if k = 3 then 2 else 0))*zen(f,k,n),
bos(~f,~k,~n) => !&a(f)*!&a(!@)^(2n+(if k = 1 or k = 2 then 1 else
                                if k = 3 then 2 else 0))*zan(f,k,n)});
        zet2:=part(zet1+x_x,0):=list;
                 zet1:=reverse rest reverse zet2;
return {{iks1/iks2},sub(zen=fer,zan=bos,(zet1 where
                                {!&a(~f) =>1,!&a(!@) => 1}))}
end$

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%% D E C L A R A T I O N %%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
let trad;


endmodule;

end;

