module symdata2; %  Symmetry data, part 2.

% Author: Karin Gatermann <Gatermann@sc.ZIB-Berlin.de>.

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


set!*elems!*group('c6,'(id rc6 r2c6 r3c6 r4c6 r5c6))$
set!*generators('c6,'(rc6))$
set!*relations('c6,'(((rc6 rc6 rc6 rc6 rc6 rc6) (id))))$
set!*grouptable('c6,
                '((grouptable id rc6 r2c6 r3c6 r4c6 r5c6)
                  (id id rc6 r2c6 r3c6 r4c6 r5c6)
                  (rc6 rc6 r2c6 r3c6 r4c6 r5c6 id)
                  (r2c6 r2c6 r3c6 r4c6 r5c6 id rc6)
                  (r3c6 r3c6 r4c6 r5c6 id rc6 r2c6)
                  (r4c6 r4c6 r5c6 id rc6 r2c6 r3c6)
                  (r5c6 r5c6 id rc6 r2c6 r3c6 r4c6)))$
set!*inverse('c6,
  '((id rc6 r2c6 r3c6 r4c6 r5c6) (id r5c6 r4c6 r3c6 r2c6 rc6)))$
set!*elemasgen('c6,
               '(((rc6) (rc6))
                 ((r2c6) (rc6 rc6))
                 ((r3c6) (rc6 rc6 rc6))
                 ((r4c6) (rc6 rc6 rc6 rc6))
                 ((r5c6) (rc6 rc6 rc6 rc6 rc6))))$
set!*group('c6,'((id) (rc6) (r2c6) (r3c6) (r4c6) (r5c6)))$
set!*representation('c6,
                    '((id (((1 . 1))))
                      (rc6 (((1 . 1))))
                      (r2c6 (((1 . 1))))
                      (r3c6 (((1 . 1))))
                      (r4c6 (((1 . 1))))
                      (r5c6 (((1 . 1))))),'complex)$
set!*representation('c6,
                    '((id (((1 . 1))))
                      (rc6 (((-1 . 1))))
                      (r2c6 (((1 . 1))))
                      (r3c6 (((-1 . 1))))
                      (r4c6 (((1 . 1))))
                      (r5c6 (((-1 . 1))))),'complex)$
set!*representation('c6,
                    '((id (((1 . 1))))
                      (rc6
                (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . 1)) . 1)
                       . 2))))
                      (r2c6
               (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . 1)) . -1)
                          . 2))))
                      (r3c6 (((-1 . 1))))
                      (r4c6
              (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . -1)) . -1)
                          . 2))))
                      (r5c6
               (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . -1)) . 1)
                          . 2))))),'complex)$
set!*representation('c6,
                    '((id (((1 . 1))))
                      (rc6
               (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . 1)) . -1)
                          . 2))))
                      (r2c6
              (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . -1)) . -1)
                          . 2))))
                      (r3c6 (((1 . 1))))
                      (r4c6
               (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . 1)) . -1)
                          . 2))))
                      (r5c6
               (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . -1)) . -1)
                       . 2))))),'complex)$
set!*representation('c6,
                    '((id (((1 . 1))))
                      (rc6
              (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . -1)) . -1)
                          . 2))))
                      (r2c6
               (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . 1)) . -1)
                          . 2))))
                      (r3c6 (((1 . 1))))
                      (r4c6
              (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . -1)) . -1)
                          . 2))))
                      (r5c6
               (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . 1)) . -1)
                        . 2))))),'complex)$
set!*representation('c6,
                    '((id (((1 . 1))))
                      (rc6
               (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . -1)) . 1)
                          . 2))))
                      (r2c6
              (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . -1)) . -1)
                          . 2))))
                      (r3c6 (((-1 . 1))))
                      (r4c6
               (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . 1)) . -1)
                          . 2))))
                      (r5c6
                (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . 1)) . 1)
                          . 2))))),'complex)$
set!*representation('c6,
                    '(realtype
                      (id (((1 . 1))))
                      (rc6 (((1 . 1))))
                      (r2c6 (((1 . 1))))
                      (r3c6 (((1 . 1))))
                      (r4c6 (((1 . 1))))
                      (r5c6 (((1 . 1))))),'real)$
set!*representation('c6,
                    '(realtype
                      (id (((1 . 1))))
                      (rc6 (((-1 . 1))))
                      (r2c6 (((1 . 1))))
                      (r3c6 (((-1 . 1))))
                      (r4c6 (((1 . 1))))
                      (r5c6 (((-1 . 1))))),'real)$
set!*representation('c6,
                    '(complextype
                      (id (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (rc6
                (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
                ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (1 . 2))))
                      (r2c6
               (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
               ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                     (r3c6 (((-1 . 1) (nil . 1)) ((nil . 1) (-1 . 1))))
                      (r4c6
                (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (r5c6
                 (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (1 . 2))))),'real)$
set!*representation('c6,
                    '(complextype
                      (id (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (rc6
               (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
               ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                      (r2c6
               (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (r3c6 (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (r4c6
               (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
               ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                      (r5c6
               (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))),'real)$
set!*available 'c6$

set!*elems!*group('s4,
                  '(id
                    bacd
                    acbd
                    abdc
                    dbca
                    cabd
                    bcad
                    dacb
                    bdca
                    dbac
                    cbda
                    adbc
                    acdb
                    badc
                    cdab
                    dcba
                    cbad
                    adcb
                    bcda
                    bdac
                    cadb
                    dabc
                    cdba
                    dcab))$
set!*generators('s4,'(bacd acbd abdc dbca))$
set!*relations('s4,
               '(((bacd bacd) (id))
                 ((acbd acbd) (id))
                 ((abdc abdc) (id))
                 ((dbca) (bacd acbd abdc acbd bacd))))$
set!*grouptable('s4,
                '((grouptable
                   dcab
                   dcba
                   dbac
                   dbca
                   dabc
                   dacb
                   cdab
                   cdba
                   cbad
                   cbda
                   cabd
                   cadb
                   bdac
                   bdca
                   bcad
                   bcda
                   bacd
                   badc
                   adbc
                   adcb
                   acbd
                   acdb
                   id
                   abdc)
                  (dcab
                   badc
                   abdc
                   cadb
                   acdb
                   cbda
                   bcda
                   bacd
                   id
                   dacb
                   adcb
                   dbca
                   bdca
                   cabd
                   acbd
                   dabc
                   adbc
                   dcba
                   cdba
                   cbad
                   bcad
                   dbac
                   bdac
                   dcab
                   cdab)
                  (dcba
                   bacd
                   id
                   cabd
                   acbd
                   cbad
                   bcad
                   badc
                   abdc
                   dabc
                   adbc
                   dbac
                   bdac
                   cadb
                   acdb
                   dacb
                   adcb
                   dcab
                   cdab
                   cbda
                   bcda
                   dbca
                   bdca
                   dcba
                   cdba)
                  (dbac
                   bcda
                   acdb
                   cbda
                   abdc
                   cadb
                   badc
                   bdca
                   adcb
                   dbca
                   id
                   dacb
                   bacd
                   cdba
                   adbc
                   dcba
                   acbd
                   dabc
                   cabd
                   cdab
                   bdac
                   dcab
                   bcad
                   dbac
                   cbad)
                  (dbca
                   bcad
                   acbd
                   cbad
                   id
                   cabd
                   bacd
                   bdac
                   adbc
                   dbac
                   abdc
                   dabc
                   badc
                   cdab
                   adcb
                   dcab
                   acdb
                   dacb
                   cadb
                   cdba
                   bdca
                   dcba
                   bcda
                   dbca
                   cbda)
                  (dabc
                   bdca
                   adcb
                   cdba
                   adbc
                   cdab
                   bdac
                   bcda
                   acdb
                   dcba
                   acbd
                   dcab
                   bcad
                   cbda
                   abdc
                   dbca
                   id
                   dbac
                   cbad
                   cadb
                   badc
                   dacb
                   bacd
                   dabc
                   cabd)
                  (dacb
                   bdac
                   adbc
                   cdab
                   adcb
                   cdba
                   bdca
                   bcad
                   acbd
                   dcab
                   acdb
                   dcba
                   bcda
                   cbad
                   id
                   dbac
                   abdc
                   dbca
                   cbda
                   cabd
                   bacd
                   dabc
                   badc
                   dacb
                   cadb)
                  (cdab
                   abdc
                   badc
                   acdb
                   cadb
                   bcda
                   cbda
                   id
                   bacd
                   adcb
                   dacb
                   bdca
                   dbca
                   acbd
                   cabd
                   adbc
                   dabc
                   cdba
                   dcba
                   bcad
                   cbad
                   bdac
                   dbac
                   cdab
                   dcab)
                  (cdba
                   id
                   bacd
                   acbd
                   cabd
                   bcad
                   cbad
                   abdc
                   badc
                   adbc
                   dabc
                   bdac
                   dbac
                   acdb
                   cadb
                   adcb
                   dacb
                   cdab
                   dcab
                   bcda
                   cbda
                   bdca
                   dbca
                   cdba
                   dcba)
                  (cbad
                   acdb
                   bcda
                   abdc
                   cbda
                   badc
                   cadb
                   adcb
                   bdca
                   id
                   dbca
                   bacd
                   dacb
                   adbc
                   cdba
                   acbd
                   dcba
                   cabd
                   dabc
                   bdac
                   cdab
                   bcad
                   dcab
                   cbad
                   dbac)
                  (cbda
                   acbd
                   bcad
                   id
                   cbad
                   bacd
                   cabd
                   adbc
                   bdac
                   abdc
                   dbac
                   badc
                   dabc
                   adcb
                   cdab
                   acdb
                   dcab
                   cadb
                   dacb
                   bdca
                   cdba
                   bcda
                   dcba
                   cbda
                   dbca)
                  (cabd
                   adcb
                   bdca
                   adbc
                   cdba
                   bdac
                   cdab
                   acdb
                   bcda
                   acbd
                   dcba
                   bcad
                   dcab
                   abdc
                   cbda
                   id
                   dbca
                   cbad
                   dbac
                   badc
                   cadb
                   bacd
                   dacb
                   cabd
                   dabc)
                  (cadb
                   adbc
                   bdac
                   adcb
                   cdab
                   bdca
                   cdba
                   acbd
                   bcad
                   acdb
                   dcab
                   bcda
                   dcba
                   id
                   cbad
                   abdc
                   dbac
                   cbda
                   dbca
                   bacd
                   cabd
                   badc
                   dabc
                   cadb
                   dacb)
                  (bdac
                   cbda
                   cadb
                   bcda
                   badc
                   acdb
                   abdc
                   dbca
                   dacb
                   bdca
                   bacd
                   adcb
                   id
                   dcba
                   dabc
                   cdba
                   cabd
                   adbc
                   acbd
                   dcab
                   dbac
                   cdab
                   cbad
                   bdac
                   bcad)
                  (bdca
                   cbad
                   cabd
                   bcad
                   bacd
                   acbd
                   id
                   dbac
                   dabc
                   bdac
                   badc
                   adbc
                   abdc
                   dcab
                   dacb
                   cdab
                   cadb
                   adcb
                   acdb
                   dcba
                   dbca
                   cdba
                   cbda
                   bdca
                   bcda)
                  (bcad
                   cadb
                   cbda
                   badc
                   bcda
                   abdc
                   acdb
                   dacb
                   dbca
                   bacd
                   bdca
                   id
                   adcb
                   dabc
                   dcba
                   cabd
                   cdba
                   acbd
                   adbc
                   dbac
                   dcab
                   cbad
                   cdab
                   bcad
                   bdac)
                  (bcda
                   cabd
                   cbad
                   bacd
                   bcad
                   id
                   acbd
                   dabc
                   dbac
                   badc
                   bdac
                   abdc
                   adbc
                   dacb
                   dcab
                   cadb
                   cdab
                   acdb
                   adcb
                   dbca
                   dcba
                   cbda
                   cdba
                   bcda
                   bdca)
                  (bacd
                   cdab
                   cdba
                   bdac
                   bdca
                   adbc
                   adcb
                   dcab
                   dcba
                   bcad
                   bcda
                   acbd
                   acdb
                   dbac
                   dbca
                   cbad
                   cbda
                   id
                   abdc
                   dabc
                   dacb
                   cabd
                   cadb
                   bacd
                   badc)
                  (badc
                   cdba
                   cdab
                   bdca
                   bdac
                   adcb
                   adbc
                   dcba
                   dcab
                   bcda
                   bcad
                   acdb
                   acbd
                   dbca
                   dbac
                   cbda
                   cbad
                   abdc
                   id
                   dacb
                   dabc
                   cadb
                   cabd
                   badc
                   bacd)
                  (adbc
                   dbca
                   dacb
                   dcba
                   dabc
                   dcab
                   dbac
                   cbda
                   cadb
                   cdba
                   cabd
                   cdab
                   cbad
                   bcda
                   badc
                   bdca
                   bacd
                   bdac
                   bcad
                   acdb
                   abdc
                   adcb
                   id
                   adbc
                   acbd)
                  (adcb
                   dbac
                   dabc
                   dcab
                   dacb
                   dcba
                   dbca
                   cbad
                   cabd
                   cdab
                   cadb
                   cdba
                   cbda
                   bcad
                   bacd
                   bdac
                   badc
                   bdca
                   bcda
                   acbd
                   id
                   adbc
                   abdc
                   adcb
                   acdb)
                  (acbd
                   dacb
                   dbca
                   dabc
                   dcba
                   dbac
                   dcab
                   cadb
                   cbda
                   cabd
                   cdba
                   cbad
                   cdab
                   badc
                   bcda
                   bacd
                   bdca
                   bcad
                   bdac
                   abdc
                   acdb
                   id
                   adcb
                   acbd
                   adbc)
                  (acdb
                   dabc
                   dbac
                   dacb
                   dcab
                   dbca
                   dcba
                   cabd
                   cbad
                   cadb
                   cdab
                   cbda
                   cdba
                   bacd
                   bcad
                   badc
                   bdac
                   bcda
                   bdca
                   id
                   acbd
                   abdc
                   adbc
                   acdb
                   adcb)
                  (id
                   dcab
                   dcba
                   dbac
                   dbca
                   dabc
                   dacb
                   cdab
                   cdba
                   cbad
                   cbda
                   cabd
                   cadb
                   bdac
                   bdca
                   bcad
                   bcda
                   bacd
                   badc
                   adbc
                   adcb
                   acbd
                   acdb
                   id
                   abdc)
                  (abdc
                   dcba
                   dcab
                   dbca
                   dbac
                   dacb
                   dabc
                   cdba
                   cdab
                   cbda
                   cbad
                   cadb
                   cabd
                   bdca
                   bdac
                   bcda
                   bcad
                   badc
                   bacd
                   adcb
                   adbc
                   acdb
                   acbd
                   abdc
                   id)))$
set!*inverse('s4,
             '((dcab
                dcba
                dbac
                dbca
                dabc
                dacb
                cdab
                cdba
                cbad
                cbda
                cabd
                cadb
                bdac
                bdca
                bcad
                bcda
                bacd
                badc
                adbc
                adcb
                acbd
                acdb
                id
                abdc)
               (cdba
                dcba
                cbda
                dbca
                bcda
                bdca
                cdab
                dcab
                cbad
                dbac
                bcad
                bdac
                cadb
                dacb
                cabd
                dabc
                bacd
                badc
                acdb
                adcb
                acbd
                adbc
                id
                abdc)))$
set!*elemasgen('s4,
               '(((bacd) (bacd))
                 ((acbd) (acbd))
                 ((abdc) (abdc))
                 ((dbca) (dbca))
                 ((cabd) (bacd acbd))
                 ((bcad) (acbd bacd))
                 ((dacb) (dbca bacd))
                 ((bdca) (bacd dbca))
                 ((dbac) (abdc dbca))
                 ((cbda) (dbca abdc))
                 ((adbc) (acbd abdc))
                 ((acdb) (abdc acbd))
                 ((badc) (bacd abdc))
                 ((cdab) (abdc bacd acbd dbca))
                 ((dcba) (acbd dbca))
                 ((cbad) (bacd acbd bacd))
                 ((adcb) (dbca bacd dbca))
                 ((bcda) (abdc acbd bacd))
                 ((bdac) (acbd bacd abdc))
                 ((cadb) (abdc bacd acbd))
                 ((dabc) (bacd acbd abdc))
                 ((cdba) (bacd acbd dbca))
                 ((dcab) (abdc acbd dbca))))$
set!*group('s4,
           '((dcab dabc cadb bdac bcda cdba)
             (dcba badc cdab)
             (dbac dacb cabd adbc acdb bcad bdca cbda)
             (dbca adcb abdc acbd bacd cbad)
             (id)))$
set!*representation('s4,
                    '((id (((1 . 1))))
                      (bacd (((1 . 1))))
                      (acbd (((1 . 1))))
                      (abdc (((1 . 1))))
                      (dbca (((1 . 1))))
                      (cabd (((1 . 1))))
                      (bcad (((1 . 1))))
                      (dacb (((1 . 1))))
                      (bdca (((1 . 1))))
                      (dbac (((1 . 1))))
                      (cbda (((1 . 1))))
                      (adbc (((1 . 1))))
                      (acdb (((1 . 1))))
                      (badc (((1 . 1))))
                      (cdab (((1 . 1))))
                      (dcba (((1 . 1))))
                      (cbad (((1 . 1))))
                      (adcb (((1 . 1))))
                      (bcda (((1 . 1))))
                      (bdac (((1 . 1))))
                      (cadb (((1 . 1))))
                      (dabc (((1 . 1))))
                      (cdba (((1 . 1))))
                      (dcab (((1 . 1))))),'complex)$
set!*representation('s4,
                    '((id (((1 . 1))))
                      (bacd (((-1 . 1))))
                      (acbd (((-1 . 1))))
                      (abdc (((-1 . 1))))
                      (dbca (((-1 . 1))))
                      (cabd (((1 . 1))))
                      (bcad (((1 . 1))))
                      (dacb (((1 . 1))))
                      (bdca (((1 . 1))))
                      (dbac (((1 . 1))))
                      (cbda (((1 . 1))))
                      (adbc (((1 . 1))))
                      (acdb (((1 . 1))))
                      (badc (((1 . 1))))
                      (cdab (((1 . 1))))
                      (dcba (((1 . 1))))
                      (cbad (((-1 . 1))))
                      (adcb (((-1 . 1))))
                      (bcda (((-1 . 1))))
                      (bdac (((-1 . 1))))
                      (cadb (((-1 . 1))))
                      (dabc (((-1 . 1))))
                      (cdba (((-1 . 1))))
                      (dcab (((-1 . 1))))),'complex)$
set!*representation('s4,
                    '((id (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (bacd
                (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
              ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                      (acbd
               (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (abdc
                (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
              ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                      (dbca
               (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (cabd
              (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
              ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                      (bcad
               (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                    ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (dacb
               (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (bdca
               (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
               ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                      (dbac
               (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
               ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                      (cbda
                (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (adbc
                (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (acdb
                (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
                ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                      (badc (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (cdab (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (dcba (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (cbad (((-1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (adcb (((-1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (bcda (((-1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (bdac
                (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (cadb
                (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
                      ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (dabc (((-1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (cdba
                  (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                      (dcab
                  (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2)
                         (-1 . 2))))),'complex)$
set!*representation('s4,
                    '((id
                       (((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))))
                      (bacd
                       (((nil . 1) (nil . 1) (-1 . 1))
                        ((nil . 1) (1 . 1) (nil . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))))
                      (acbd
                       (((nil . 1) (-1 . 1) (nil . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))))
                      (abdc
                       (((nil . 1) (nil . 1) (1 . 1))
                        ((nil . 1) (1 . 1) (nil . 1))
                        ((1 . 1) (nil . 1) (nil . 1))))
                      (dbca
                       (((nil . 1) (1 . 1) (nil . 1))
                        ((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))))
                      (cabd
                       (((nil . 1) (nil . 1) (-1 . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (1 . 1) (nil . 1))))
                      (bcad
                       (((nil . 1) (-1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))))
                      (dacb
                       (((nil . 1) (1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))))
                      (bdca
                       (((nil . 1) (nil . 1) (-1 . 1))
                        ((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))))
                      (dbac
                       (((nil . 1) (nil . 1) (1 . 1))
                        ((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (1 . 1) (nil . 1))))
                      (cbda
                       (((nil . 1) (1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))
                        ((1 . 1) (nil . 1) (nil . 1))))
                      (adbc
                       (((nil . 1) (-1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))
                        ((1 . 1) (nil . 1) (nil . 1))))
                      (acdb
                       (((nil . 1) (nil . 1) (1 . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))))
                      (badc
                       (((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))))
                      (cdab
                       (((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))))
                      (dcba
                       (((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))))
                      (cbad
                       (((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))
                        ((nil . 1) (1 . 1) (nil . 1))))
                      (adcb
                       (((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))))
                      (bcda
                       (((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))))
                      (bdac
                       (((nil . 1) (-1 . 1) (nil . 1))
                        ((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))))
                      (cadb
                       (((nil . 1) (1 . 1) (nil . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))))
                      (dabc
                       (((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))
                        ((nil . 1) (1 . 1) (nil . 1))))
                      (cdba
                       (((nil . 1) (nil . 1) (-1 . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))
                        ((1 . 1) (nil . 1) (nil . 1))))
                      (dcab
                       (((nil . 1) (nil . 1) (1 . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))))),'complex)$
set!*representation('s4,
                    '((id
                       (((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))))
                      (bacd
                       (((nil . 1) (nil . 1) (1 . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))
                        ((1 . 1) (nil . 1) (nil . 1))))
                      (acbd
                       (((nil . 1) (1 . 1) (nil . 1))
                        ((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))))
                      (abdc
                       (((nil . 1) (nil . 1) (-1 . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))))
                      (dbca
                       (((nil . 1) (-1 . 1) (nil . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))))
                      (cabd
                       (((nil . 1) (nil . 1) (-1 . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (1 . 1) (nil . 1))))
                      (bcad
                       (((nil . 1) (-1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))))
                      (dacb
                       (((nil . 1) (1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))))
                      (bdca
                       (((nil . 1) (nil . 1) (-1 . 1))
                        ((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))))
                      (dbac
                       (((nil . 1) (nil . 1) (1 . 1))
                        ((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (1 . 1) (nil . 1))))
                      (cbda
                       (((nil . 1) (1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))
                        ((1 . 1) (nil . 1) (nil . 1))))
                      (adbc
                       (((nil . 1) (-1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))
                        ((1 . 1) (nil . 1) (nil . 1))))
                      (acdb
                       (((nil . 1) (nil . 1) (1 . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))))
                      (badc
                       (((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))))
                      (cdab
                       (((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))))
                      (dcba
                       (((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))))
                      (cbad
                       (((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))))
                      (adcb
                       (((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))
                        ((nil . 1) (1 . 1) (nil . 1))))
                      (bcda
                       (((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))
                        ((nil . 1) (1 . 1) (nil . 1))))
                      (bdac
                       (((nil . 1) (1 . 1) (nil . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))))
                      (cadb
                       (((nil . 1) (-1 . 1) (nil . 1))
                        ((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))))
                      (dabc
                       (((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))))
                      (cdba
                       (((nil . 1) (nil . 1) (1 . 1))
                        ((nil . 1) (1 . 1) (nil . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))))
                      (dcab
                       (((nil . 1) (nil . 1) (-1 . 1))
                        ((nil . 1) (1 . 1) (nil . 1))
                        ((1 . 1) (nil . 1) (nil . 1))))),'complex)$
set!*representation('s4,
                    '(realtype
                      (id (((1 . 1))))
                      (bacd (((1 . 1))))
                      (acbd (((1 . 1))))
                      (abdc (((1 . 1))))
                      (dbca (((1 . 1))))
                      (cabd (((1 . 1))))
                      (bcad (((1 . 1))))
                      (dacb (((1 . 1))))
                      (bdca (((1 . 1))))
                      (dbac (((1 . 1))))
                      (cbda (((1 . 1))))
                      (adbc (((1 . 1))))
                      (acdb (((1 . 1))))
                      (badc (((1 . 1))))
                      (cdab (((1 . 1))))
                      (dcba (((1 . 1))))
                      (cbad (((1 . 1))))
                      (adcb (((1 . 1))))
                      (bcda (((1 . 1))))
                      (bdac (((1 . 1))))
                      (cadb (((1 . 1))))
                      (dabc (((1 . 1))))
                      (cdba (((1 . 1))))
                      (dcab (((1 . 1))))),'real)$
set!*representation('s4,
                    '(realtype
                      (id (((1 . 1))))
                      (bacd (((-1 . 1))))
                      (acbd (((-1 . 1))))
                      (abdc (((-1 . 1))))
                      (dbca (((-1 . 1))))
                      (cabd (((1 . 1))))
                      (bcad (((1 . 1))))
                      (dacb (((1 . 1))))
                      (bdca (((1 . 1))))
                      (dbac (((1 . 1))))
                      (cbda (((1 . 1))))
                      (adbc (((1 . 1))))
                      (acdb (((1 . 1))))
                      (badc (((1 . 1))))
                      (cdab (((1 . 1))))
                      (dcba (((1 . 1))))
                      (cbad (((-1 . 1))))
                      (adcb (((-1 . 1))))
                      (bcda (((-1 . 1))))
                      (bdac (((-1 . 1))))
                      (cadb (((-1 . 1))))
                      (dabc (((-1 . 1))))
                      (cdba (((-1 . 1))))
                      (dcab (((-1 . 1))))),'real)$
set!*representation('s4,
                    '(realtype
                      (id (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (bacd
                (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
              ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                      (acbd
               (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (abdc
                (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
              ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                      (dbca
               (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (cabd
               (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
               ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                      (bcad
                (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                      ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (dacb
                (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (bdca
               (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
               ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                      (dbac
               (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
               ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                      (cbda
                (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (adbc
                (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (acdb
               (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
               ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                      (badc (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (cdab (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (dcba (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (cbad (((-1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (adcb (((-1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (bcda (((-1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (bdac
                (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (cadb
                (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (dabc (((-1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (cdba
                 (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                      (dcab
                 (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2)
                         (-1 . 2))))),'real)$
set!*representation('s4,
                    '(realtype
                      (id
                       (((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))))
                      (bacd
                       (((nil . 1) (nil . 1) (-1 . 1))
                        ((nil . 1) (1 . 1) (nil . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))))
                      (acbd
                       (((nil . 1) (-1 . 1) (nil . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))))
                      (abdc
                       (((nil . 1) (nil . 1) (1 . 1))
                        ((nil . 1) (1 . 1) (nil . 1))
                        ((1 . 1) (nil . 1) (nil . 1))))
                      (dbca
                       (((nil . 1) (1 . 1) (nil . 1))
                        ((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))))
                      (cabd
                       (((nil . 1) (nil . 1) (-1 . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (1 . 1) (nil . 1))))
                      (bcad
                       (((nil . 1) (-1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))))
                      (dacb
                       (((nil . 1) (1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))))
                      (bdca
                       (((nil . 1) (nil . 1) (-1 . 1))
                        ((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))))
                      (dbac
                       (((nil . 1) (nil . 1) (1 . 1))
                        ((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (1 . 1) (nil . 1))))
                      (cbda
                       (((nil . 1) (1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))
                        ((1 . 1) (nil . 1) (nil . 1))))
                      (adbc
                       (((nil . 1) (-1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))
                        ((1 . 1) (nil . 1) (nil . 1))))
                      (acdb
                       (((nil . 1) (nil . 1) (1 . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))))
                      (badc
                       (((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))))
                      (cdab
                       (((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))))
                      (dcba
                       (((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))))
                      (cbad
                       (((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))
                        ((nil . 1) (1 . 1) (nil . 1))))
                      (adcb
                       (((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))))
                      (bcda
                       (((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))))
                      (bdac
                       (((nil . 1) (-1 . 1) (nil . 1))
                        ((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))))
                      (cadb
                       (((nil . 1) (1 . 1) (nil . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))))
                      (dabc
                       (((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))
                        ((nil . 1) (1 . 1) (nil . 1))))
                      (cdba
                       (((nil . 1) (nil . 1) (-1 . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))
                        ((1 . 1) (nil . 1) (nil . 1))))
                      (dcab
                       (((nil . 1) (nil . 1) (1 . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))))),'real)$
set!*representation('s4,
                    '(realtype
                      (id
                       (((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))))
                      (bacd
                       (((nil . 1) (nil . 1) (1 . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))
                        ((1 . 1) (nil . 1) (nil . 1))))
                      (acbd
                       (((nil . 1) (1 . 1) (nil . 1))
                        ((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))))
                      (abdc
                       (((nil . 1) (nil . 1) (-1 . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))))
                      (dbca
                       (((nil . 1) (-1 . 1) (nil . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))))
                      (cabd
                       (((nil . 1) (nil . 1) (-1 . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (1 . 1) (nil . 1))))
                      (bcad
                       (((nil . 1) (-1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))))
                      (dacb
                       (((nil . 1) (1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))))
                      (bdca
                       (((nil . 1) (nil . 1) (-1 . 1))
                        ((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))))
                      (dbac
                       (((nil . 1) (nil . 1) (1 . 1))
                        ((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (1 . 1) (nil . 1))))
                      (cbda
                       (((nil . 1) (1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))
                        ((1 . 1) (nil . 1) (nil . 1))))
                      (adbc
                       (((nil . 1) (-1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))
                        ((1 . 1) (nil . 1) (nil . 1))))
                      (acdb
                       (((nil . 1) (nil . 1) (1 . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))))
                      (badc
                       (((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))))
                      (cdab
                       (((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))))
                      (dcba
                       (((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))))
                      (cbad
                       (((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))))
                      (adcb
                       (((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))
                        ((nil . 1) (1 . 1) (nil . 1))))
                      (bcda
                       (((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))
                        ((nil . 1) (1 . 1) (nil . 1))))
                      (bdac
                       (((nil . 1) (1 . 1) (nil . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))))
                      (cadb
                       (((nil . 1) (-1 . 1) (nil . 1))
                        ((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))))
                      (dabc
                       (((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))))
                      (cdba
                       (((nil . 1) (nil . 1) (1 . 1))
                        ((nil . 1) (1 . 1) (nil . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))))
                      (dcab
                       (((nil . 1) (nil . 1) (-1 . 1))
                        ((nil . 1) (1 . 1) (nil . 1))
                        ((1 . 1) (nil . 1) (nil . 1))))),'real)$
set!*available 's4$

set!*elems!*group('a4,
                  '(id ta4 t2a4 xa4 ya4 za4 txa4 tya4 tza4 t2xa4 t2ya4
                       t2za4))$
set!*generators('a4,'(ta4 xa4 ya4 za4))$
set!*relations('a4,
               '(((za4) (ta4 xa4 ta4 ta4))
                 ((ya4) (ta4 za4 ta4 ta4))
                 ((xa4) (ta4 ya4 ta4 ta4))
                 ((ta4 ta4 ta4) (id))
                 ((xa4 xa4) (id))
                 ((ya4 ya4) (id))
                 ((za4 za4) (id))
                 ((xa4 ya4) (za4))))$
set!*grouptable('a4,
                '((grouptable
                   id
                   ta4
                   t2a4
                   xa4
                   ya4
                   za4
                   txa4
                   tya4
                   tza4
                   t2xa4
                   t2ya4
                   t2za4)
                  (id id ta4 t2a4 xa4 ya4 za4 txa4 tya4 tza4 t2xa4
                      t2ya4 t2za4)
                  (ta4 ta4 t2a4 id txa4 tya4 tza4 t2xa4 t2ya4 t2za4 xa4
                       ya4 za4)
                  (t2a4
                   t2a4
                   id
                   ta4
                   t2xa4
                   t2ya4
                   t2za4
                   xa4
                   ya4
                   za4
                   txa4
                   tya4
                   tza4)
                  (xa4 xa4 tya4 t2za4 id za4 ya4 tza4 ta4 txa4 t2ya4
                       t2xa4 t2a4)
                  (ya4 ya4 tza4 t2xa4 za4 id xa4 tya4 txa4 ta4 t2a4
                       t2za4 t2ya4)
                  (za4 za4 txa4 t2ya4 ya4 xa4 id ta4 tza4 tya4 t2za4
                       t2a4 t2xa4)
                  (txa4
                   txa4
                   t2ya4
                   za4
                   ta4
                   tza4
                   tya4
                   t2za4
                   t2a4
                   t2xa4
                   ya4
                   xa4
                   id)
                  (tya4
                   tya4
                   t2za4
                   xa4
                   tza4
                   ta4
                   txa4
                   t2ya4
                   t2xa4
                   t2a4
                   id
                   za4
                   ya4)
                  (tza4
                   tza4
                   t2xa4
                   ya4
                   tya4
                   txa4
                   ta4
                   t2a4
                   t2za4
                   t2ya4
                   za4
                   id
                   xa4)
                  (t2xa4
                   t2xa4
                   ya4
                   tza4
                   t2a4
                   t2za4
                   t2ya4
                   za4
                   id
                   xa4
                   tya4
                   txa4
                   ta4)
                  (t2ya4
                   t2ya4
                   za4
                   txa4
                   t2za4
                   t2a4
                   t2xa4
                   ya4
                   xa4
                   id
                   ta4
                   tza4
                   tya4)
                  (t2za4
                   t2za4
                   xa4
                   tya4
                   t2ya4
                   t2xa4
                   t2a4
                   id
                   za4
                   ya4
                   tza4
                   ta4
                   txa4)))$
set!*inverse('a4,
            '((id ta4 t2a4 xa4 ya4 za4 txa4 tya4 tza4 t2xa4 t2ya4 t2za4)
              (id t2a4 ta4 xa4 ya4 za4 t2za4 t2xa4 t2ya4 tya4 tza4 txa4)
            ))$
set!*elemasgen('a4,
               '(((ta4) (ta4))
                 ((t2a4) (ta4 ta4))
                 ((xa4) (xa4))
                 ((ya4) (ya4))
                 ((za4) (za4))
                 ((txa4) (ta4 xa4))
                 ((tya4) (ta4 ya4))
                 ((tza4) (ta4 za4))
                 ((t2xa4) (ta4 ta4 xa4))
                 ((t2ya4) (ta4 ta4 ya4))
                 ((t2za4) (ta4 ta4 za4))))$
set!*group('a4,
           '((id) (txa4 ta4 tza4 tya4) (t2za4 t2a4 t2ya4 t2xa4)
                  (ya4 xa4 za4)))$
set!*representation('a4,
                    '((id (((1 . 1))))
                      (ta4 (((1 . 1))))
                      (t2a4 (((1 . 1))))
                      (xa4 (((1 . 1))))
                      (ya4 (((1 . 1))))
                      (za4 (((1 . 1))))
                      (txa4 (((1 . 1))))
                      (tya4 (((1 . 1))))
                      (tza4 (((1 . 1))))
                      (t2xa4 (((1 . 1))))
                      (t2ya4 (((1 . 1))))
                      (t2za4 (((1 . 1))))),'complex)$
set!*representation('a4,
                    '((id (((1 . 1))))
                      (ta4
                       (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . 1))
                               . -1)
                          . 2))))
                      (t2a4
                       (((((((expt 3 (quotient 1 2)) . 1)((i . 1) . -1))
                               . -1)
                          . 2))))
                      (xa4 (((1 . 1))))
                      (ya4 (((1 . 1))))
                      (za4 (((1 . 1))))
                      (txa4
                       (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . 1))
                               . -1)
                          . 2))))
                      (tya4
                       (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . 1))
                               . -1)
                          . 2))))
                      (tza4
                       (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . 1))
                               . -1)
                          . 2))))
                      (t2xa4
                       (((((((expt 3 (quotient 1 2)) . 1)((i . 1) . -1))
                               . -1)
                          . 2))))
                      (t2ya4
                       (((((((expt 3 (quotient 1 2)) . 1)((i . 1) . -1))
                               . -1)
                          . 2))))
                      (t2za4
                      (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . -1))
                               . -1)
                          . 2))))),'complex)$
set!*representation('a4,
                    '((id (((1 . 1))))
                      (ta4
                       (((((((expt 3 (quotient 1 2)) . 1)((i . 1) . -1))
                               . -1)
                          . 2))))
                      (t2a4
                       (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . 1))
                               . -1)
                          . 2))))
                      (xa4 (((1 . 1))))
                      (ya4 (((1 . 1))))
                      (za4 (((1 . 1))))
                      (txa4
                       (((((((expt 3 (quotient 1 2)) . 1)((i . 1) . -1))
                               . -1)
                          . 2))))
                      (tya4
                       (((((((expt 3 (quotient 1 2)) . 1)((i . 1) . -1))
                               . -1)
                          . 2))))
                      (tza4
                       (((((((expt 3 (quotient 1 2)) . 1)((i . 1) . -1))
                               . -1)
                          . 2))))
                      (t2xa4
                       (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . 1))
                               . -1)
                          . 2))))
                      (t2ya4
                       (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . 1))
                               . -1)
                          . 2))))
                      (t2za4
                       (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . 1))
                               . -1)
                          . 2))))),'complex)$
set!*representation('a4,
                    '((id
                       (((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))))
                      (ta4
                       (((nil . 1) (nil . 1) (-1 . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (1 . 1) (nil . 1))))
                      (t2a4
                       (((nil . 1) (-1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))))
                      (xa4
                       (((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))))
                      (ya4
                       (((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))))
                      (za4
                       (((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))))
                      (txa4
                       (((nil . 1) (nil . 1) (1 . 1))
                        ((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (1 . 1) (nil . 1))))
                      (tya4
                       (((nil . 1) (nil . 1) (1 . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))))
                      (tza4
                       (((nil . 1) (nil . 1) (-1 . 1))
                        ((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))))
                      (t2xa4
                       (((nil . 1) (-1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))
                        ((1 . 1) (nil . 1) (nil . 1))))
                      (t2ya4
                       (((nil . 1) (1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))))
                      (t2za4
                       (((nil . 1) (1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))
                        ((1 . 1) (nil . 1) (nil . 1))))),'complex)$
set!*representation('a4,
                    '(realtype
                      (id (((1 . 1))))
                      (ta4 (((1 . 1))))
                      (t2a4 (((1 . 1))))
                      (xa4 (((1 . 1))))
                      (ya4 (((1 . 1))))
                      (za4 (((1 . 1))))
                      (txa4 (((1 . 1))))
                      (tya4 (((1 . 1))))
                      (tza4 (((1 . 1))))
                      (t2xa4 (((1 . 1))))
                      (t2ya4 (((1 . 1))))
                      (t2za4 (((1 . 1))))),'real)$
set!*representation('a4,
                    '(complextype
                      (id (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (ta4
                       (((-1 . 2)(((((expt 3 (quotient 1 2)) . 1) . 1))
                                     . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (t2a4
                       (((-1 . 2)(((((expt 3 (quotient 1 2)) . 1) . -1))
                                     . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2)
                         (-1 . 2))))
                      (xa4 (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (ya4 (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (za4 (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (txa4
                       (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1))
                                     . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (tya4
                       (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1))
                                     . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (tza4
                       (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1))
                                     . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (t2xa4
                       (((-1 . 2)(((((expt 3 (quotient 1 2)) . 1) . -1))
                                     . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2)
                         (-1 . 2))))
                      (t2ya4
                       (((-1 . 2)(((((expt 3 (quotient 1 2)) . 1) . -1))
                                     . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2)
                         (-1 . 2))))
                      (t2za4
                       (((-1 . 2)(((((expt 3 (quotient 1 2)) . 1) . -1))
                                     . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2)
                         (-1 . 2))))),'real)$
set!*representation('a4,
                    '(realtype
                      (id
                       (((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))))
                      (ta4
                       (((nil . 1) (nil . 1) (-1 . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (1 . 1) (nil . 1))))
                      (t2a4
                       (((nil . 1) (-1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))))
                      (xa4
                       (((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))))
                      (ya4
                       (((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))))
                      (za4
                       (((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))))
                      (txa4
                       (((nil . 1) (nil . 1) (1 . 1))
                        ((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (1 . 1) (nil . 1))))
                      (tya4
                       (((nil . 1) (nil . 1) (1 . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))))
                      (tza4
                       (((nil . 1) (nil . 1) (-1 . 1))
                        ((1 . 1) (nil . 1) (nil . 1))
                        ((nil . 1) (-1 . 1) (nil . 1))))
                      (t2xa4
                       (((nil . 1) (-1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))
                        ((1 . 1) (nil . 1) (nil . 1))))
                      (t2ya4
                       (((nil . 1) (1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (-1 . 1))
                        ((-1 . 1) (nil . 1) (nil . 1))))
                      (t2za4
                       (((nil . 1) (1 . 1) (nil . 1))
                        ((nil . 1) (nil . 1) (1 . 1))
                        ((1 . 1) (nil . 1) (nil . 1))))),'real)$
set!*available 'a4$

endmodule;

end;
