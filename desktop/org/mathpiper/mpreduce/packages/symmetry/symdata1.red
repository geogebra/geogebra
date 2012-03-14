module symdata1;  %  Data for symmetry package, part 1.

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


set!*elems!*group('z2,'(id sz2))$
set!*generators('z2,'(sz2))$
set!*relations('z2,'(((sz2 sz2) (id))))$
set!*grouptable('z2,'((grouptable id sz2) (id id sz2) (sz2 sz2 id)))$
set!*inverse('z2,'((id sz2) (id sz2)))$
set!*elemasgen('z2,'(((sz2) (sz2))))$
set!*group('z2,'((id) (sz2)))$
set!*representation('z2,'((id (((1 . 1)))) (sz2 (((1 . 1))))),'complex)$
set!*representation('z2,
  '((id (((1 . 1)))) (sz2 (((-1 . 1))))),'complex)$
set!*representation('z2,
  '(realtype (id (((1 . 1)))) (sz2 (((1 . 1))))),'real)$
set!*representation('z2,
  '(realtype (id (((1 . 1)))) (sz2 (((-1 . 1))))),'real)$
set!*available 'z2$

set!*elems!*group('k4,'(id s1k4 s2k4 rk4))$
set!*generators('k4,'(s1k4 s2k4))$
set!*relations('k4,
               '(((s1k4 s1k4) (id))
                 ((s2k4 s2k4) (id))
                 ((s1k4 s2k4) (s2k4 s1k4))))$
set!*grouptable('k4,
                '((grouptable id s1k4 s2k4 rk4)
                  (id id s1k4 s2k4 rk4)
                  (s1k4 s1k4 id rk4 s2k4)
                  (s2k4 s2k4 rk4 id s1k4)
                  (rk4 rk4 s2k4 s1k4 id)))$
set!*inverse('k4,'((id s1k4 s2k4 rk4) (id s1k4 s2k4 rk4)))$
set!*elemasgen('k4,
  '(((s1k4) (s1k4)) ((s2k4) (s2k4)) ((rk4) (s1k4 s2k4))))$
set!*group('k4,'((id) (s1k4) (s2k4) (rk4)))$
set!*representation('k4,
                    '((id (((1 . 1))))
                      (s1k4 (((1 . 1))))
                      (s2k4 (((1 . 1))))
                      (rk4 (((1 . 1))))),'complex)$
set!*representation('k4,
                    '((id (((1 . 1))))
                      (s1k4 (((-1 . 1))))
                      (s2k4 (((1 . 1))))
                      (rk4 (((-1 . 1))))),'complex)$
set!*representation('k4,
                    '((id (((1 . 1))))
                      (s1k4 (((1 . 1))))
                      (s2k4 (((-1 . 1))))
                      (rk4 (((-1 . 1))))),'complex)$
set!*representation('k4,
                    '((id (((1 . 1))))
                      (s1k4 (((-1 . 1))))
                      (s2k4 (((-1 . 1))))
                      (rk4 (((1 . 1))))),'complex)$
set!*representation('k4,
                    '(realtype
                      (id (((1 . 1))))
                      (s1k4 (((1 . 1))))
                      (s2k4 (((1 . 1))))
                      (rk4 (((1 . 1))))),'real)$
set!*representation('k4,
                    '(realtype
                      (id (((1 . 1))))
                      (s1k4 (((-1 . 1))))
                      (s2k4 (((1 . 1))))
                      (rk4 (((-1 . 1))))),'real)$
set!*representation('k4,
                    '(realtype
                      (id (((1 . 1))))
                      (s1k4 (((1 . 1))))
                      (s2k4 (((-1 . 1))))
                      (rk4 (((-1 . 1))))),'real)$
set!*representation('k4,
                    '(realtype
                      (id (((1 . 1))))
                      (s1k4 (((-1 . 1))))
                      (s2k4 (((-1 . 1))))
                      (rk4 (((1 . 1))))),'real)$
set!*available 'k4$

set!*elems!*group('d3,'(id rd3 rot2d3 sd3 srd3 sr2d3))$
set!*generators('d3,'(rd3 sd3))$
set!*relations('d3,
               '(((sd3 sd3) (id))
                 ((rd3 rd3 rd3) (id))
                 ((sd3 rd3 sd3) (rd3 rd3))))$
set!*grouptable('d3,
                '((grouptable id rd3 rot2d3 sd3 srd3 sr2d3)
                  (id id rd3 rot2d3 sd3 srd3 sr2d3)
                  (rd3 rd3 rot2d3 id sr2d3 sd3 srd3)
                  (rot2d3 rot2d3 id rd3 srd3 sr2d3 sd3)
                  (sd3 sd3 srd3 sr2d3 id rd3 rot2d3)
                  (srd3 srd3 sr2d3 sd3 rot2d3 id rd3)
                  (sr2d3 sr2d3 sd3 srd3 rd3 rot2d3 id)))$
set!*inverse('d3,
   '((id rd3 rot2d3 sd3 srd3 sr2d3) (id rot2d3 rd3 sd3 srd3 sr2d3)))$
set!*elemasgen('d3,
               '(((rd3) (rd3))
                 ((rot2d3) (rd3 rd3))
                 ((sd3) (sd3))
                 ((srd3) (sd3 rd3))
                 ((sr2d3) (sd3 rd3 rd3))))$
set!*group('d3,'((id) (rd3 rot2d3) (sr2d3 sd3 srd3)))$
set!*representation('d3,
                    '((id (((1 . 1))))
                      (rd3 (((1 . 1))))
                      (rot2d3 (((1 . 1))))
                      (sd3 (((1 . 1))))
                      (srd3 (((1 . 1))))
                      (sr2d3 (((1 . 1))))),'complex)$
set!*representation('d3,
                    '((id (((1 . 1))))
                      (rd3 (((1 . 1))))
                      (rot2d3 (((1 . 1))))
                      (sd3 (((-1 . 1))))
                      (srd3 (((-1 . 1))))
                      (sr2d3 (((-1 . 1))))),'complex)$
set!*representation('d3,
                    '((id (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (rd3
              (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
              ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (rot2d3
              (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
              ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                      (sd3 (((1 . 1) (nil . 1)) ((nil . 1) (-1 . 1))))
                      (srd3
              (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
              ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (1 . 2))))
                      (sr2d3
              (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (1 . 2))))),'complex)$
set!*representation('d3,
                    '(realtype
                      (id (((1 . 1))))
                      (rd3 (((1 . 1))))
                      (rot2d3 (((1 . 1))))
                      (sd3 (((1 . 1))))
                      (srd3 (((1 . 1))))
                      (sr2d3 (((1 . 1))))),'real)$
set!*representation('d3,
                    '(realtype
                      (id (((1 . 1))))
                      (rd3 (((1 . 1))))
                      (rot2d3 (((1 . 1))))
                      (sd3 (((-1 . 1))))
                      (srd3 (((-1 . 1))))
                      (sr2d3 (((-1 . 1))))),'real)$
set!*representation('d3,
                    '(realtype
                      (id (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (rd3
             (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (rot2d3
             (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
             ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                      (sd3 (((1 . 1) (nil . 1)) ((nil . 1) (-1 . 1))))
                      (srd3
             (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
             ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (1 . 2))))
                      (sr2d3
             (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (1 . 2))))),'real)$
set!*available 'd3$

set!*elems!*group('d4,'(id rd4 rot2d4 rot3d4 sd4 srd4 sr2d4 sr3d4))$
set!*generators('d4,'(rd4 sd4))$
set!*relations('d4,
               '(((sd4 sd4) (id))
                 ((rd4 rd4 rd4 rd4) (id))
                 ((sd4 rd4 sd4) (rd4 rd4 rd4))))$
set!*grouptable('d4,
                '((grouptable id rd4 rot2d4 rot3d4 sd4 srd4 sr2d4 sr3d4)
                  (id id rd4 rot2d4 rot3d4 sd4 srd4 sr2d4 sr3d4)
                  (rd4 rd4 rot2d4 rot3d4 id sr3d4 sd4 srd4 sr2d4)
                  (rot2d4 rot2d4 rot3d4 id rd4 sr2d4 sr3d4 sd4 srd4)
                  (rot3d4 rot3d4 id rd4 rot2d4 srd4 sr2d4 sr3d4 sd4)
                  (sd4 sd4 srd4 sr2d4 sr3d4 id rd4 rot2d4 rot3d4)
                  (srd4 srd4 sr2d4 sr3d4 sd4 rot3d4 id rd4 rot2d4)
                  (sr2d4 sr2d4 sr3d4 sd4 srd4 rot2d4 rot3d4 id rd4)
                  (sr3d4 sr3d4 sd4 srd4 sr2d4 rd4 rot2d4 rot3d4 id)))$
set!*inverse('d4,
             '((id rd4 rot2d4 rot3d4 sd4 srd4 sr2d4 sr3d4)
               (id rot3d4 rot2d4 rd4 sd4 srd4 sr2d4 sr3d4)))$
set!*elemasgen('d4,
               '(((rd4) (rd4))
                 ((rot2d4) (rd4 rd4))
                 ((rot3d4) (rd4 rd4 rd4))
                 ((sd4) (sd4))
                 ((srd4) (sd4 rd4))
                 ((sr2d4) (sd4 rd4 rd4))
                 ((sr3d4) (sd4 rd4 rd4 rd4))))$
set!*group('d4,'((id) (rd4 rot3d4) (rot2d4) (sd4 sr2d4) (sr3d4 srd4)))$
set!*representation('d4,
                    '((id (((1 . 1))))
                      (rd4 (((1 . 1))))
                      (rot2d4 (((1 . 1))))
                      (rot3d4 (((1 . 1))))
                      (sd4 (((1 . 1))))
                      (srd4 (((1 . 1))))
                      (sr2d4 (((1 . 1))))
                      (sr3d4 (((1 . 1))))),'complex)$
set!*representation('d4,
                    '((id (((1 . 1))))
                      (rd4 (((1 . 1))))
                      (rot2d4 (((1 . 1))))
                      (rot3d4 (((1 . 1))))
                      (sd4 (((-1 . 1))))
                      (srd4 (((-1 . 1))))
                      (sr2d4 (((-1 . 1))))
                      (sr3d4 (((-1 . 1))))),'complex)$
set!*representation('d4,
                    '((id (((1 . 1))))
                      (rd4 (((-1 . 1))))
                      (rot2d4 (((1 . 1))))
                      (rot3d4 (((-1 . 1))))
                      (sd4 (((1 . 1))))
                      (srd4 (((-1 . 1))))
                      (sr2d4 (((1 . 1))))
                      (sr3d4 (((-1 . 1))))),'complex)$
set!*representation('d4,
                    '((id (((1 . 1))))
                      (rd4 (((-1 . 1))))
                      (rot2d4 (((1 . 1))))
                      (rot3d4 (((-1 . 1))))
                      (sd4 (((-1 . 1))))
                      (srd4 (((1 . 1))))
                      (sr2d4 (((-1 . 1))))
                      (sr3d4 (((1 . 1))))),'complex)$
set!*representation('d4,
                    '((id (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (rd4 (((nil . 1) (1 . 1)) ((-1 . 1) (nil . 1))))
                 (rot2d4 (((-1 . 1) (nil . 1)) ((nil . 1) (-1 . 1))))
                 (rot3d4 (((nil . 1) (-1 . 1)) ((1 . 1) (nil . 1))))
                      (sd4 (((1 . 1) (nil . 1)) ((nil . 1) (-1 . 1))))
                      (srd4 (((nil . 1) (1 . 1)) ((1 . 1) (nil . 1))))
                      (sr2d4 (((-1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                 (sr3d4 (((nil . 1) (-1 . 1)) ((-1 . 1) (nil . 1))))),
                    'complex)$
set!*representation('d4,
                    '(realtype
                      (id (((1 . 1))))
                      (rd4 (((1 . 1))))
                      (rot2d4 (((1 . 1))))
                      (rot3d4 (((1 . 1))))
                      (sd4 (((1 . 1))))
                      (srd4 (((1 . 1))))
                      (sr2d4 (((1 . 1))))
                      (sr3d4 (((1 . 1))))),'real)$
set!*representation('d4,
                    '(realtype
                      (id (((1 . 1))))
                      (rd4 (((1 . 1))))
                      (rot2d4 (((1 . 1))))
                      (rot3d4 (((1 . 1))))
                      (sd4 (((-1 . 1))))
                      (srd4 (((-1 . 1))))
                      (sr2d4 (((-1 . 1))))
                      (sr3d4 (((-1 . 1))))),'real)$
set!*representation('d4,
                    '(realtype
                      (id (((1 . 1))))
                      (rd4 (((-1 . 1))))
                      (rot2d4 (((1 . 1))))
                      (rot3d4 (((-1 . 1))))
                      (sd4 (((1 . 1))))
                      (srd4 (((-1 . 1))))
                      (sr2d4 (((1 . 1))))
                      (sr3d4 (((-1 . 1))))),'real)$
set!*representation('d4,
                    '(realtype
                      (id (((1 . 1))))
                      (rd4 (((-1 . 1))))
                      (rot2d4 (((1 . 1))))
                      (rot3d4 (((-1 . 1))))
                      (sd4 (((-1 . 1))))
                      (srd4 (((1 . 1))))
                      (sr2d4 (((-1 . 1))))
                      (sr3d4 (((1 . 1))))),'real)$
set!*representation('d4,
                    '(realtype
                      (id (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (rd4 (((nil . 1) (1 . 1)) ((-1 . 1) (nil . 1))))
                  (rot2d4 (((-1 . 1) (nil . 1)) ((nil . 1) (-1 . 1))))
                  (rot3d4 (((nil . 1) (-1 . 1)) ((1 . 1) (nil . 1))))
                      (sd4 (((1 . 1) (nil . 1)) ((nil . 1) (-1 . 1))))
                      (srd4 (((nil . 1) (1 . 1)) ((1 . 1) (nil . 1))))
                  (sr2d4 (((-1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                  (sr3d4 (((nil . 1) (-1 . 1)) ((-1 . 1) (nil . 1))))),
                    'real)$
set!*available 'd4$

set!*elems!*group('d5,
   '(id rd5 r2d5 r3d5 r4d5 sd5 srd5 sr2d5 sr3d5 sr4d5))$
set!*generators('d5,'(rd5 sd5))$
set!*relations('d5,
               '(((sd5 sd5) (id))
                 ((rd5 rd5 rd5 rd5 rd5) (id))
                 ((sd5 rd5 sd5) (rd5 rd5 rd5 rd5))))$
set!*grouptable('d5,
      '((grouptable id rd5 r2d5 r3d5 r4d5 sd5 srd5 sr2d5 sr3d5 sr4d5)
               (id id rd5 r2d5 r3d5 r4d5 sd5 srd5 sr2d5 sr3d5 sr4d5)
               (rd5 rd5 r2d5 r3d5 r4d5 id sr4d5 sd5 srd5 sr2d5 sr3d5)
               (r2d5 r2d5 r3d5 r4d5 id rd5 sr3d5 sr4d5 sd5 srd5 sr2d5)
               (r3d5 r3d5 r4d5 id rd5 r2d5 sr2d5 sr3d5 sr4d5 sd5 srd5)
               (r4d5 r4d5 id rd5 r2d5 r3d5 srd5 sr2d5 sr3d5 sr4d5 sd5)
               (sd5 sd5 srd5 sr2d5 sr3d5 sr4d5 id rd5 r2d5 r3d5 r4d5)
               (srd5 srd5 sr2d5 sr3d5 sr4d5 sd5 r4d5 id rd5 r2d5 r3d5)
               (sr2d5 sr2d5 sr3d5 sr4d5 sd5 srd5 r3d5 r4d5 id rd5 r2d5)
               (sr3d5 sr3d5 sr4d5 sd5 srd5 sr2d5 r2d5 r3d5 r4d5 id rd5)
        (sr4d5 sr4d5 sd5 srd5 sr2d5 sr3d5 rd5 r2d5 r3d5 r4d5 id)))$
set!*inverse('d5,
             '((id rd5 r2d5 r3d5 r4d5 sd5 srd5 sr2d5 sr3d5 sr4d5)
               (id r4d5 r3d5 r2d5 rd5 sd5 srd5 sr2d5 sr3d5 sr4d5)))$
set!*elemasgen('d5,
               '(((rd5) (rd5))
                 ((r2d5) (rd5 rd5))
                 ((r3d5) (rd5 rd5 rd5))
                 ((r4d5) (rd5 rd5 rd5 rd5))
                 ((sd5) (sd5))
                 ((srd5) (sd5 rd5))
                 ((sr2d5) (sd5 rd5 rd5))
                 ((sr3d5) (sd5 rd5 rd5 rd5))
                 ((sr4d5) (sd5 rd5 rd5 rd5 rd5))))$
set!*group('d5,
 '((id) (rd5 r4d5) (r2d5 r3d5) (srd5 sr2d5 sd5 sr4d5 sr3d5)))$
set!*representation('d5,
                    '((id (((1 . 1))))
                      (rd5 (((1 . 1))))
                      (r2d5 (((1 . 1))))
                      (r3d5 (((1 . 1))))
                      (r4d5 (((1 . 1))))
                      (sd5 (((1 . 1))))
                      (srd5 (((1 . 1))))
                      (sr2d5 (((1 . 1))))
                      (sr3d5 (((1 . 1))))
                      (sr4d5 (((1 . 1))))),'complex)$
set!*representation('d5,
                    '((id (((1 . 1))))
                      (rd5 (((1 . 1))))
                      (r2d5 (((1 . 1))))
                      (r3d5 (((1 . 1))))
                      (r4d5 (((1 . 1))))
                      (sd5 (((-1 . 1))))
                      (srd5 (((-1 . 1))))
                      (sr2d5 (((-1 . 1))))
                      (sr3d5 (((-1 . 1))))
                      (sr4d5 (((-1 . 1))))),'complex)$
set!*representation('d5,
                    '((id (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (rd5
                  (((((((cos (quotient (times 2 pi) 5)) . 1) . 1)) . 1)
                  (((((sin (quotient (times 2 pi) 5)) . 1) . -1)) . 1))
                  ((((((sin (quotient (times 2 pi) 5)) . 1) . 1)) . 1)
                  (((((cos (quotient (times 2 pi) 5)) . 1) . 1)) . 1))))
                      (r2d5
                       (((((((sin (quotient (times 2 pi) 5)) . 2) . -1)
                           (((cos (quotient (times 2 pi) 5)) . 2) . 1))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 1)
                    (((cos (quotient (times 2 pi) 5)) . 1) . -2)))
                          . 1))
                        ((((((sin (quotient (times 2 pi) 5)) . 1)
                    (((cos (quotient (times 2 pi) 5)) . 1) . 2)))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 2) . -1)
                           (((cos (quotient (times 2 pi) 5)) . 2) . 1))
                          . 1))))
                      (r3d5
                       (((((((sin (quotient (times 2 pi) 5)) . 2)
                      (((cos (quotient (times 2 pi) 5)) . 1) . -3))
                           (((cos (quotient (times 2 pi) 5)) . 3) . 1))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 3) . 1)
                           (((sin (quotient (times 2 pi) 5)) . 1)
                     (((cos (quotient (times 2 pi) 5)) . 2) . -3)))
                          . 1))
                        ((((((sin (quotient (times 2 pi) 5)) . 3) . -1)
                           (((sin (quotient (times 2 pi) 5)) . 1)
                     (((cos (quotient (times 2 pi) 5)) . 2) . 3)))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 2)
                        (((cos (quotient (times 2 pi) 5)) . 1) . -3))
                     (((cos (quotient (times 2 pi) 5)) . 3) . 1))
                          . 1))))
                      (r4d5
                       (((((((sin (quotient (times 2 pi) 5)) . 4) . 1)
                           (((sin (quotient (times 2 pi) 5)) . 2)
                         (((cos (quotient (times 2 pi) 5)) . 2) . -6))
                        (((cos (quotient (times 2 pi) 5)) . 4) . 1))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 3)
                            (((cos (quotient (times 2 pi) 5)) . 1) . 4))
                           (((sin (quotient (times 2 pi) 5)) . 1)
                          (((cos (quotient (times 2 pi) 5)) . 3) . -4)))
                          . 1))
                        ((((((sin (quotient (times 2 pi) 5)) . 3)
                          (((cos (quotient (times 2 pi) 5)) . 1) . -4))
                           (((sin (quotient (times 2 pi) 5)) . 1)
                          (((cos (quotient (times 2 pi) 5)) . 3) . 4)))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 4) . 1)
                           (((sin (quotient (times 2 pi) 5)) . 2)
                          (((cos (quotient (times 2 pi) 5)) . 2) . -6))
                           (((cos (quotient (times 2 pi) 5)) . 4) . 1))
                          . 1))))
                      (sd5 (((1 . 1) (nil . 1)) ((nil . 1) (-1 . 1))))
                      (srd5
                  (((((((cos (quotient (times 2 pi) 5)) . 1) . 1)) . 1)
                  (((((sin (quotient (times 2 pi) 5)) . 1) . -1)) . 1))
                  ((((((sin (quotient (times 2 pi) 5)) . 1) . -1)) . 1)
               (((((cos (quotient (times 2 pi) 5)) . 1) . -1)) . 1))))
                      (sr2d5
                       (((((((sin (quotient (times 2 pi) 5)) . 2) . -1)
                           (((cos (quotient (times 2 pi) 5)) . 2) . 1))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 1)
                         (((cos (quotient (times 2 pi) 5)) . 1) . -2)))
                          . 1))
                        ((((((sin (quotient (times 2 pi) 5)) . 1)
                        (((cos (quotient (times 2 pi) 5)) . 1) . -2)))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 2) . 1)
                         (((cos (quotient (times 2 pi) 5)) . 2) . -1))
                          . 1))))
                      (sr3d5
                       (((((((sin (quotient (times 2 pi) 5)) . 2)
                          (((cos (quotient (times 2 pi) 5)) . 1) . -3))
                           (((cos (quotient (times 2 pi) 5)) . 3) . 1))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 3) . 1)
                           (((sin (quotient (times 2 pi) 5)) . 1)
                          (((cos (quotient (times 2 pi) 5)) . 2) . -3)))
                          . 1))
                        ((((((sin (quotient (times 2 pi) 5)) . 3) . 1)
                           (((sin (quotient (times 2 pi) 5)) . 1)
                          (((cos (quotient (times 2 pi) 5)) . 2) . -3)))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 2)
                            (((cos (quotient (times 2 pi) 5)) . 1) . 3))
                           (((cos (quotient (times 2 pi) 5)) . 3) . -1))
                          . 1))))
                      (sr4d5
                       (((((((sin (quotient (times 2 pi) 5)) . 4) . 1)
                           (((sin (quotient (times 2 pi) 5)) . 2)
                          (((cos (quotient (times 2 pi) 5)) . 2) . -6))
                           (((cos (quotient (times 2 pi) 5)) . 4) . 1))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 3)
                            (((cos (quotient (times 2 pi) 5)) . 1) . 4))
                           (((sin (quotient (times 2 pi) 5)) . 1)
                          (((cos (quotient (times 2 pi) 5)) . 3) . -4)))
                          . 1))
                        ((((((sin (quotient (times 2 pi) 5)) . 3)
                            (((cos (quotient (times 2 pi) 5)) . 1) . 4))
                           (((sin (quotient (times 2 pi) 5)) . 1)
                          (((cos (quotient (times 2 pi) 5)) . 3) . -4)))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 4) . -1)
                           (((sin (quotient (times 2 pi) 5)) . 2)
                            (((cos (quotient (times 2 pi) 5)) . 2) . 6))
                           (((cos (quotient (times 2 pi) 5)) . 4) . -1))
                          . 1))))),'complex)$
set!*representation('d5,
                    '((id (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (rd5
                   (((((((cos (quotient (times 4 pi) 5)) . 1) . 1)) . 1)
                   (((((sin (quotient (times 4 pi) 5)) . 1) . -1)) . 1))
                   ((((((sin (quotient (times 4 pi) 5)) . 1) . 1)) . 1)
                (((((cos (quotient (times 4 pi) 5)) . 1) . 1)) . 1))))
                      (r2d5
                       (((((((sin (quotient (times 4 pi) 5)) . 2) . -1)
                           (((cos (quotient (times 4 pi) 5)) . 2) . 1))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 1)
                         (((cos (quotient (times 4 pi) 5)) . 1) . -2)))
                          . 1))
                        ((((((sin (quotient (times 4 pi) 5)) . 1)
                          (((cos (quotient (times 4 pi) 5)) . 1) . 2)))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 2) . -1)
                           (((cos (quotient (times 4 pi) 5)) . 2) . 1))
                          . 1))))
                      (r3d5
                       (((((((sin (quotient (times 4 pi) 5)) . 2)
                         (((cos (quotient (times 4 pi) 5)) . 1) . -3))
                           (((cos (quotient (times 4 pi) 5)) . 3) . 1))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 3) . 1)
                           (((sin (quotient (times 4 pi) 5)) . 1)
                         (((cos (quotient (times 4 pi) 5)) . 2) . -3)))
                          . 1))
                        ((((((sin (quotient (times 4 pi) 5)) . 3) . -1)
                           (((sin (quotient (times 4 pi) 5)) . 1)
                         (((cos (quotient (times 4 pi) 5)) . 2) . 3)))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 2)
                         (((cos (quotient (times 4 pi) 5)) . 1) . -3))
                           (((cos (quotient (times 4 pi) 5)) . 3) . 1))
                          . 1))))
                      (r4d5
                       (((((((sin (quotient (times 4 pi) 5)) . 4) . 1)
                           (((sin (quotient (times 4 pi) 5)) . 2)
                          (((cos (quotient (times 4 pi) 5)) . 2) . -6))
                           (((cos (quotient (times 4 pi) 5)) . 4) . 1))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 3)
                            (((cos (quotient (times 4 pi) 5)) . 1) . 4))
                           (((sin (quotient (times 4 pi) 5)) . 1)
                         (((cos (quotient (times 4 pi) 5)) . 3) . -4)))
                          . 1))
                        ((((((sin (quotient (times 4 pi) 5)) . 3)
                         (((cos (quotient (times 4 pi) 5)) . 1) . -4))
                           (((sin (quotient (times 4 pi) 5)) . 1)
                         (((cos (quotient (times 4 pi) 5)) . 3) . 4)))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 4) . 1)
                           (((sin (quotient (times 4 pi) 5)) . 2)
                         (((cos (quotient (times 4 pi) 5)) . 2) . -6))
                           (((cos (quotient (times 4 pi) 5)) . 4) . 1))
                          . 1))))
                      (sd5 (((1 . 1) (nil . 1)) ((nil . 1) (-1 . 1))))
                      (srd5
                 (((((((cos (quotient (times 4 pi) 5)) . 1) . 1)) . 1)
                 (((((sin (quotient (times 4 pi) 5)) . 1) . -1)) . 1))
                 ((((((sin (quotient (times 4 pi) 5)) . 1) . -1)) . 1)
                 (((((cos (quotient (times 4 pi) 5)) . 1) . -1)) . 1))))
                      (sr2d5
                       (((((((sin (quotient (times 4 pi) 5)) . 2) . -1)
                           (((cos (quotient (times 4 pi) 5)) . 2) . 1))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 1)
                         (((cos (quotient (times 4 pi) 5)) . 1) . -2)))
                          . 1))
                        ((((((sin (quotient (times 4 pi) 5)) . 1)
                         (((cos (quotient (times 4 pi) 5)) . 1) . -2)))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 2) . 1)
                        (((cos (quotient (times 4 pi) 5)) . 2) . -1))
                          . 1))))
                      (sr3d5
                       (((((((sin (quotient (times 4 pi) 5)) . 2)
                         (((cos (quotient (times 4 pi) 5)) . 1) . -3))
                           (((cos (quotient (times 4 pi) 5)) . 3) . 1))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 3) . 1)
                           (((sin (quotient (times 4 pi) 5)) . 1)
                         (((cos (quotient (times 4 pi) 5)) . 2) . -3)))
                       . 1))
                        ((((((sin (quotient (times 4 pi) 5)) . 3) . 1)
                           (((sin (quotient (times 4 pi) 5)) . 1)
                         (((cos (quotient (times 4 pi) 5)) . 2) . -3)))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 2)
                            (((cos (quotient (times 4 pi) 5)) . 1) . 3))
                           (((cos (quotient (times 4 pi) 5)) . 3) . -1))
                          . 1))))
                      (sr4d5
                       (((((((sin (quotient (times 4 pi) 5)) . 4) . 1)
                           (((sin (quotient (times 4 pi) 5)) . 2)
                         (((cos (quotient (times 4 pi) 5)) . 2) . -6))
                           (((cos (quotient (times 4 pi) 5)) . 4) . 1))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 3)
                            (((cos (quotient (times 4 pi) 5)) . 1) . 4))
                           (((sin (quotient (times 4 pi) 5)) . 1)
                        (((cos (quotient (times 4 pi) 5)) . 3) . -4)))
                          . 1))
                        ((((((sin (quotient (times 4 pi) 5)) . 3)
                            (((cos (quotient (times 4 pi) 5)) . 1) . 4))
                           (((sin (quotient (times 4 pi) 5)) . 1)
                         (((cos (quotient (times 4 pi) 5)) . 3) . -4)))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 4) . -1)
                           (((sin (quotient (times 4 pi) 5)) . 2)
                            (((cos (quotient (times 4 pi) 5)) . 2) . 6))
                           (((cos (quotient (times 4 pi) 5)) . 4) . -1))
                          . 1))))),'complex)$
set!*representation('d5,
                    '(realtype
                      (id (((1 . 1))))
                      (rd5 (((1 . 1))))
                      (r2d5 (((1 . 1))))
                      (r3d5 (((1 . 1))))
                      (r4d5 (((1 . 1))))
                      (sd5 (((1 . 1))))
                      (srd5 (((1 . 1))))
                      (sr2d5 (((1 . 1))))
                      (sr3d5 (((1 . 1))))
                      (sr4d5 (((1 . 1))))),'real)$
set!*representation('d5,
                    '(realtype
                      (id (((1 . 1))))
                      (rd5 (((1 . 1))))
                      (r2d5 (((1 . 1))))
                      (r3d5 (((1 . 1))))
                      (r4d5 (((1 . 1))))
                      (sd5 (((-1 . 1))))
                      (srd5 (((-1 . 1))))
                      (sr2d5 (((-1 . 1))))
                      (sr3d5 (((-1 . 1))))
                      (sr4d5 (((-1 . 1))))),'real)$
set!*representation('d5,
                    '(realtype
                      (id (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (rd5
                 (((((((cos (quotient (times 2 pi) 5)) . 1) . 1)) . 1)
                  (((((sin (quotient (times 2 pi) 5)) . 1) . -1)) . 1))
                   ((((((sin (quotient (times 2 pi) 5)) . 1) . 1)) . 1)
                 (((((cos (quotient (times 2 pi) 5)) . 1) . 1)) . 1))))
                      (r2d5
                       (((((((sin (quotient (times 2 pi) 5)) . 2) . -1)
                           (((cos (quotient (times 2 pi) 5)) . 2) . 1))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 1)
                        (((cos (quotient (times 2 pi) 5)) . 1) . -2)))
                          . 1))
                        ((((((sin (quotient (times 2 pi) 5)) . 1)
                          (((cos (quotient (times 2 pi) 5)) . 1) . 2)))
                        . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 2) . -1)
                           (((cos (quotient (times 2 pi) 5)) . 2) . 1))
                          . 1))))
                      (r3d5
                       (((((((sin (quotient (times 2 pi) 5)) . 2)
                          (((cos (quotient (times 2 pi) 5)) . 1) . -3))
                           (((cos (quotient (times 2 pi) 5)) . 3) . 1))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 3) . 1)
                           (((sin (quotient (times 2 pi) 5)) . 1)
                         (((cos (quotient (times 2 pi) 5)) . 2) . -3)))
                          . 1))
                        ((((((sin (quotient (times 2 pi) 5)) . 3) . -1)
                           (((sin (quotient (times 2 pi) 5)) . 1)
                          (((cos (quotient (times 2 pi) 5)) . 2) . 3)))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 2)
                         (((cos (quotient (times 2 pi) 5)) . 1) . -3))
                         (((cos (quotient (times 2 pi) 5)) . 3) . 1))
                          . 1))))
                      (r4d5
                       (((((((sin (quotient (times 2 pi) 5)) . 4) . 1)
                           (((sin (quotient (times 2 pi) 5)) . 2)
                          (((cos (quotient (times 2 pi) 5)) . 2) . -6))
                           (((cos (quotient (times 2 pi) 5)) . 4) . 1))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 3)
                          (((cos (quotient (times 2 pi) 5)) . 1) . 4))
                          (((sin (quotient (times 2 pi) 5)) . 1)
                         (((cos (quotient (times 2 pi) 5)) . 3) . -4)))
                          . 1))
                        ((((((sin (quotient (times 2 pi) 5)) . 3)
                           (((cos (quotient (times 2 pi) 5)) . 1) . -4))
                          (((sin (quotient (times 2 pi) 5)) . 1)
                          (((cos (quotient (times 2 pi) 5)) . 3) . 4)))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 4) . 1)
                           (((sin (quotient (times 2 pi) 5)) . 2)
                          (((cos (quotient (times 2 pi) 5)) . 2) . -6))
                           (((cos (quotient (times 2 pi) 5)) . 4) . 1))
                          . 1))))
                      (sd5 (((1 . 1) (nil . 1)) ((nil . 1) (-1 . 1))))
                      (srd5
                 (((((((cos (quotient (times 2 pi) 5)) . 1) . 1)) . 1)
                 (((((sin (quotient (times 2 pi) 5)) . 1) . -1)) . 1))
                 ((((((sin (quotient (times 2 pi) 5)) . 1) . -1)) . 1)
                (((((cos (quotient (times 2 pi) 5)) . 1) . -1)) . 1))))
                      (sr2d5
                       (((((((sin (quotient (times 2 pi) 5)) . 2) . -1)
                           (((cos (quotient (times 2 pi) 5)) . 2) . 1))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 1)
                        (((cos (quotient (times 2 pi) 5)) . 1) . -2)))
                          . 1))
                        ((((((sin (quotient (times 2 pi) 5)) . 1)
                       (((cos (quotient (times 2 pi) 5)) . 1) . -2)))
                        . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 2) . 1)
                           (((cos (quotient (times 2 pi) 5)) . 2) . -1))
                          . 1))))
                      (sr3d5
                       (((((((sin (quotient (times 2 pi) 5)) . 2)
                         (((cos (quotient (times 2 pi) 5)) . 1) . -3))
                           (((cos (quotient (times 2 pi) 5)) . 3) . 1))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 3) . 1)
                           (((sin (quotient (times 2 pi) 5)) . 1)
                         (((cos (quotient (times 2 pi) 5)) . 2) . -3)))
                          . 1))
                        ((((((sin (quotient (times 2 pi) 5)) . 3) . 1)
                           (((sin (quotient (times 2 pi) 5)) . 1)
                         (((cos (quotient (times 2 pi) 5)) . 2) . -3)))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 2)
                            (((cos (quotient (times 2 pi) 5)) . 1) . 3))
                           (((cos (quotient (times 2 pi) 5)) . 3) . -1))
                          . 1))))
                      (sr4d5
                       (((((((sin (quotient (times 2 pi) 5)) . 4) . 1)
                           (((sin (quotient (times 2 pi) 5)) . 2)
                         (((cos (quotient (times 2 pi) 5)) . 2) . -6))
                           (((cos (quotient (times 2 pi) 5)) . 4) . 1))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 3)
                            (((cos (quotient (times 2 pi) 5)) . 1) . 4))
                           (((sin (quotient (times 2 pi) 5)) . 1)
                         (((cos (quotient (times 2 pi) 5)) . 3) . -4)))
                          . 1))
                        ((((((sin (quotient (times 2 pi) 5)) . 3)
                            (((cos (quotient (times 2 pi) 5)) . 1) . 4))
                           (((sin (quotient (times 2 pi) 5)) . 1)
                         (((cos (quotient (times 2 pi) 5)) . 3) . -4)))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 4) . -1)
                           (((sin (quotient (times 2 pi) 5)) . 2)
                            (((cos (quotient (times 2 pi) 5)) . 2) . 6))
                           (((cos (quotient (times 2 pi) 5)) . 4) . -1))
                          . 1))))),'real)$
set!*representation('d5,
                    '(realtype
                      (id (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (rd5
                  (((((((cos (quotient (times 4 pi) 5)) . 1) . 1)) . 1)
                   (((((sin (quotient (times 4 pi) 5)) . 1) . -1)) . 1))
                   ((((((sin (quotient (times 4 pi) 5)) . 1) . 1)) . 1)
                  (((((cos (quotient (times 4 pi) 5)) . 1) . 1)) . 1))))
                   (r2d5
                       (((((((sin (quotient (times 4 pi) 5)) . 2) . -1)
                           (((cos (quotient (times 4 pi) 5)) . 2) . 1))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 1)
                        (((cos (quotient (times 4 pi) 5)) . 1) . -2)))
                        . 1))
                        ((((((sin (quotient (times 4 pi) 5)) . 1)
                        (((cos (quotient (times 4 pi) 5)) . 1) . 2)))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 2) . -1)
                           (((cos (quotient (times 4 pi) 5)) . 2) . 1))
                          . 1))))
                      (r3d5
                       (((((((sin (quotient (times 4 pi) 5)) . 2)
                        (((cos (quotient (times 4 pi) 5)) . 1) . -3))
                         (((cos (quotient (times 4 pi) 5)) . 3) . 1))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 3) . 1)
                           (((sin (quotient (times 4 pi) 5)) . 1)
                        (((cos (quotient (times 4 pi) 5)) . 2) . -3)))
                          . 1))
                        ((((((sin (quotient (times 4 pi) 5)) . 3) . -1)
                           (((sin (quotient (times 4 pi) 5)) . 1)
                         (((cos (quotient (times 4 pi) 5)) . 2) . 3)))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 2)
                          (((cos (quotient (times 4 pi) 5)) . 1) . -3))
                           (((cos (quotient (times 4 pi) 5)) . 3) . 1))
                          . 1))))
                      (r4d5
                       (((((((sin (quotient (times 4 pi) 5)) . 4) . 1)
                         (((sin (quotient (times 4 pi) 5)) . 2)
                          (((cos (quotient (times 4 pi) 5)) . 2) . -6))
                           (((cos (quotient (times 4 pi) 5)) . 4) . 1))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 3)
                           (((cos (quotient (times 4 pi) 5)) . 1) . 4))
                           (((sin (quotient (times 4 pi) 5)) . 1)
                         (((cos (quotient (times 4 pi) 5)) . 3) . -4)))
                          . 1))
                        ((((((sin (quotient (times 4 pi) 5)) . 3)
                           (((cos (quotient (times 4 pi) 5)) . 1) . -4))
                           (((sin (quotient (times 4 pi) 5)) . 1)
                          (((cos (quotient (times 4 pi) 5)) . 3) . 4)))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 4) . 1)
                           (((sin (quotient (times 4 pi) 5)) . 2)
                          (((cos (quotient (times 4 pi) 5)) . 2) . -6))
                           (((cos (quotient (times 4 pi) 5)) . 4) . 1))
                          . 1))))
                      (sd5 (((1 . 1) (nil . 1)) ((nil . 1) (-1 . 1))))
                      (srd5
                  (((((((cos (quotient (times 4 pi) 5)) . 1) . 1)) . 1)
                   (((((sin (quotient (times 4 pi) 5)) . 1) . -1)) . 1))
                   ((((((sin (quotient (times 4 pi) 5)) . 1) . -1)) . 1)
               (((((cos (quotient (times 4 pi) 5)) . 1) . -1)) . 1))))
                      (sr2d5
                       (((((((sin (quotient (times 4 pi) 5)) . 2) . -1)
                           (((cos (quotient (times 4 pi) 5)) . 2) . 1))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 1)
                        (((cos (quotient (times 4 pi) 5)) . 1) . -2)))
                       . 1))
                        ((((((sin (quotient (times 4 pi) 5)) . 1)
                        (((cos (quotient (times 4 pi) 5)) . 1) . -2)))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 2) . 1)
                         (((cos (quotient (times 4 pi) 5)) . 2) . -1))
                         . 1))))
                      (sr3d5
                       (((((((sin (quotient (times 4 pi) 5)) . 2)
                         (((cos (quotient (times 4 pi) 5)) . 1) . -3))
                           (((cos (quotient (times 4 pi) 5)) . 3) . 1))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 3) . 1)
                           (((sin (quotient (times 4 pi) 5)) . 1)
                         (((cos (quotient (times 4 pi) 5)) . 2) . -3)))
                          . 1))
                        ((((((sin (quotient (times 4 pi) 5)) . 3) . 1)
                           (((sin (quotient (times 4 pi) 5)) . 1)
                         (((cos (quotient (times 4 pi) 5)) . 2) . -3)))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 2)
                            (((cos (quotient (times 4 pi) 5)) . 1) . 3))
                           (((cos (quotient (times 4 pi) 5)) . 3) . -1))
                          . 1))))
                      (sr4d5
                       (((((((sin (quotient (times 4 pi) 5)) . 4) . 1)
                           (((sin (quotient (times 4 pi) 5)) . 2)
                          (((cos (quotient (times 4 pi) 5)) . 2) . -6))
                           (((cos (quotient (times 4 pi) 5)) . 4) . 1))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 3)
                           (((cos (quotient (times 4 pi) 5)) . 1) . 4))
                           (((sin (quotient (times 4 pi) 5)) . 1)
                          (((cos (quotient (times 4 pi) 5)) . 3) . -4)))
                          . 1))
                        ((((((sin (quotient (times 4 pi) 5)) . 3)
                            (((cos (quotient (times 4 pi) 5)) . 1) . 4))
                           (((sin (quotient (times 4 pi) 5)) . 1)
                         (((cos (quotient (times 4 pi) 5)) . 3) . -4)))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 4) . -1)
                           (((sin (quotient (times 4 pi) 5)) . 2)
                            (((cos (quotient (times 4 pi) 5)) . 2) . 6))
                           (((cos (quotient (times 4 pi) 5)) . 4) . -1))
                          . 1))))),'real)$
set!*available 'd5$

set!*elems!*group('d6,
                  '(id
                    rd6
                    r2d6
                    r3d6
                    r4d6
                    r5d6
                    sd6
                    srd6
                    sr2d6
                    sr3d6
                    sr4d6
                    sr5d6))$
set!*generators('d6,'(rd6 sd6))$
set!*relations('d6,
               '(((sd6 sd6) (id))
                 ((rd6 rd6 rd6 rd6 rd6 rd6) (id))
                 ((sd6 rd6 sd6) (rd6 rd6 rd6 rd6 rd6))))$
set!*grouptable('d6,
                '((grouptable
                   id
                   rd6
                   r2d6
                   r3d6
                   r4d6
                   r5d6
                   sd6
                   srd6
                   sr2d6
                   sr3d6
                   sr4d6
                   sr5d6)
                  (id
                   id
                   rd6
                   r2d6
                   r3d6
                   r4d6
                   r5d6
                   sd6
                   srd6
                   sr2d6
                   sr3d6
                   sr4d6
                   sr5d6)
                  (rd6
                   rd6
                   r2d6
                   r3d6
                   r4d6
                   r5d6
                   id
                   sr5d6
                   sd6
                   srd6
                   sr2d6
                   sr3d6
                   sr4d6)
                  (r2d6
                   r2d6
                   r3d6
                   r4d6
                   r5d6
                   id
                   rd6
                   sr4d6
                   sr5d6
                   sd6
                   srd6
                   sr2d6
                   sr3d6)
                  (r3d6
                   r3d6
                   r4d6
                   r5d6
                   id
                   rd6
                   r2d6
                   sr3d6
                   sr4d6
                   sr5d6
                   sd6
                   srd6
                   sr2d6)
                  (r4d6
                   r4d6
                   r5d6
                   id
                   rd6
                   r2d6
                   r3d6
                   sr2d6
                   sr3d6
                   sr4d6
                   sr5d6
                   sd6
                   srd6)
                  (r5d6
                   r5d6
                   id
                   rd6
                   r2d6
                   r3d6
                   r4d6
                   srd6
                   sr2d6
                   sr3d6
                   sr4d6
                   sr5d6
                   sd6)
                  (sd6
                   sd6
                   srd6
                   sr2d6
                   sr3d6
                   sr4d6
                   sr5d6
                   id
                   rd6
                   r2d6
                   r3d6
                   r4d6
                   r5d6)
                  (srd6
                   srd6
                   sr2d6
                   sr3d6
                   sr4d6
                   sr5d6
                   sd6
                   r5d6
                   id
                   rd6
                   r2d6
                   r3d6
                   r4d6)
                  (sr2d6
                   sr2d6
                   sr3d6
                   sr4d6
                   sr5d6
                   sd6
                   srd6
                   r4d6
                   r5d6
                   id
                   rd6
                   r2d6
                   r3d6)
                  (sr3d6
                   sr3d6
                   sr4d6
                   sr5d6
                   sd6
                   srd6
                   sr2d6
                   r3d6
                   r4d6
                   r5d6
                   id
                   rd6
                   r2d6)
                  (sr4d6
                   sr4d6
                   sr5d6
                   sd6
                   srd6
                   sr2d6
                   sr3d6
                   r2d6
                   r3d6
                   r4d6
                   r5d6
                   id
                   rd6)
                  (sr5d6
                   sr5d6
                   sd6
                   srd6
                   sr2d6
                   sr3d6
                   sr4d6
                   rd6
                   r2d6
                   r3d6
                   r4d6
                   r5d6
                   id)))$
set!*inverse('d6,
       '((id rd6 r2d6 r3d6 r4d6 r5d6 sd6 srd6 sr2d6 sr3d6 sr4d6 sr5d6)
       (id r5d6 r4d6 r3d6 r2d6 rd6 sd6 srd6 sr2d6 sr3d6 sr4d6 sr5d6)))$
set!*elemasgen('d6,
               '(((rd6) (rd6))
                 ((r2d6) (rd6 rd6))
                 ((r3d6) (rd6 rd6 rd6))
                 ((r4d6) (rd6 rd6 rd6 rd6))
                 ((r5d6) (rd6 rd6 rd6 rd6 rd6))
                 ((sd6) (sd6))
                 ((srd6) (sd6 rd6))
                 ((sr2d6) (sd6 rd6 rd6))
                 ((sr3d6) (sd6 rd6 rd6 rd6))
                 ((sr4d6) (sd6 rd6 rd6 rd6 rd6))
                 ((sr5d6) (sd6 rd6 rd6 rd6 rd6 rd6))))$
set!*group('d6,
           '((id)
             (rd6 r5d6)
             (r2d6 r4d6)
             (r3d6)
             (sr2d6 sd6 sr4d6)
             (srd6 sr5d6 sr3d6)))$
set!*representation('d6,
                    '((id (((1 . 1))))
                      (rd6 (((1 . 1))))
                      (r2d6 (((1 . 1))))
                      (r3d6 (((1 . 1))))
                      (r4d6 (((1 . 1))))
                      (r5d6 (((1 . 1))))
                      (sd6 (((1 . 1))))
                      (srd6 (((1 . 1))))
                      (sr2d6 (((1 . 1))))
                      (sr3d6 (((1 . 1))))
                      (sr4d6 (((1 . 1))))
                      (sr5d6 (((1 . 1))))),'complex)$
set!*representation('d6,
                    '((id (((1 . 1))))
                      (rd6 (((1 . 1))))
                      (r2d6 (((1 . 1))))
                      (r3d6 (((1 . 1))))
                      (r4d6 (((1 . 1))))
                      (r5d6 (((1 . 1))))
                      (sd6 (((-1 . 1))))
                      (srd6 (((-1 . 1))))
                      (sr2d6 (((-1 . 1))))
                      (sr3d6 (((-1 . 1))))
                      (sr4d6 (((-1 . 1))))
                      (sr5d6 (((-1 . 1))))),'complex)$
set!*representation('d6,
                    '((id (((1 . 1))))
                      (rd6 (((-1 . 1))))
                      (r2d6 (((1 . 1))))
                      (r3d6 (((-1 . 1))))
                      (r4d6 (((1 . 1))))
                      (r5d6 (((-1 . 1))))
                      (sd6 (((1 . 1))))
                      (srd6 (((-1 . 1))))
                      (sr2d6 (((1 . 1))))
                      (sr3d6 (((-1 . 1))))
                      (sr4d6 (((1 . 1))))
                      (sr5d6 (((-1 . 1))))),'complex)$
set!*representation('d6,
                    '((id (((1 . 1))))
                      (rd6 (((-1 . 1))))
                      (r2d6 (((1 . 1))))
                      (r3d6 (((-1 . 1))))
                      (r4d6 (((1 . 1))))
                      (r5d6 (((-1 . 1))))
                      (sd6 (((-1 . 1))))
                      (srd6 (((1 . 1))))
                      (sr2d6 (((-1 . 1))))
                      (sr3d6 (((1 . 1))))
                      (sr4d6 (((-1 . 1))))
                      (sr5d6 (((1 . 1))))),'complex)$
set!*representation('d6,
                    '((id (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (rd6
                  (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2) (1 . 2))))
                      (r2d6
              (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                  (r3d6 (((-1 . 1) (nil . 1)) ((nil . 1) (-1 . 1))))
                      (r4d6
              (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
             ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                      (r5d6
               (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
               ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (1 . 2))))
                      (sd6 (((1 . 1) (nil . 1)) ((nil . 1) (-1 . 1))))
                      (srd6
                (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
             ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                      (sr2d6
              (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
              ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (1 . 2))))
                   (sr3d6 (((-1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (sr4d6
             (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
             ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2) (1 . 2))))
                      (sr5d6
              (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
                   ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))),'complex)$
set!*representation('d6,
                    '((id (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (rd6
              (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (r2d6
               (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
              ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                      (r3d6 (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (r4d6
                 (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                 ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (r5d6
               (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
               ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                      (sd6 (((1 . 1) (nil . 1)) ((nil . 1) (-1 . 1))))
                      (srd6
                (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (1 . 2))))
                      (sr2d6
                (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
                ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2) (1 . 2))))
                      (sr3d6 (((1 . 1) (nil . 1)) ((nil . 1) (-1 . 1))))
                      (sr4d6
                (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (1 . 2))))
                      (sr5d6
                (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
                ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                (1 . 2))))),'complex)$
set!*representation('d6,
                    '(realtype
                      (id (((1 . 1))))
                      (rd6 (((1 . 1))))
                      (r2d6 (((1 . 1))))
                      (r3d6 (((1 . 1))))
                      (r4d6 (((1 . 1))))
                      (r5d6 (((1 . 1))))
                      (sd6 (((1 . 1))))
                      (srd6 (((1 . 1))))
                      (sr2d6 (((1 . 1))))
                      (sr3d6 (((1 . 1))))
                      (sr4d6 (((1 . 1))))
                      (sr5d6 (((1 . 1))))),'real)$
set!*representation('d6,
                    '(realtype
                      (id (((1 . 1))))
                      (rd6 (((1 . 1))))
                      (r2d6 (((1 . 1))))
                      (r3d6 (((1 . 1))))
                      (r4d6 (((1 . 1))))
                      (r5d6 (((1 . 1))))
                      (sd6 (((-1 . 1))))
                      (srd6 (((-1 . 1))))
                      (sr2d6 (((-1 . 1))))
                      (sr3d6 (((-1 . 1))))
                      (sr4d6 (((-1 . 1))))
                      (sr5d6 (((-1 . 1))))),'real)$
set!*representation('d6,
                    '(realtype
                      (id (((1 . 1))))
                      (rd6 (((-1 . 1))))
                      (r2d6 (((1 . 1))))
                      (r3d6 (((-1 . 1))))
                      (r4d6 (((1 . 1))))
                      (r5d6 (((-1 . 1))))
                      (sd6 (((1 . 1))))
                      (srd6 (((-1 . 1))))
                      (sr2d6 (((1 . 1))))
                      (sr3d6 (((-1 . 1))))
                      (sr4d6 (((1 . 1))))
                      (sr5d6 (((-1 . 1))))),'real)$
set!*representation('d6,
                    '(realtype
                      (id (((1 . 1))))
                      (rd6 (((-1 . 1))))
                      (r2d6 (((1 . 1))))
                      (r3d6 (((-1 . 1))))
                      (r4d6 (((1 . 1))))
                      (r5d6 (((-1 . 1))))
                      (sd6 (((-1 . 1))))
                      (srd6 (((1 . 1))))
                      (sr2d6 (((-1 . 1))))
                      (sr3d6 (((1 . 1))))
                      (sr4d6 (((-1 . 1))))
                      (sr5d6 (((1 . 1))))),'real)$
set!*representation('d6,
                    '(realtype
                      (id (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (rd6
                (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2) (1 . 2))))
                      (r2d6
               (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (r3d6 (((-1 . 1) (nil . 1)) ((nil . 1) (-1 . 1))))
                      (r4d6
                (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
               ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                      (r5d6
               (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
               ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (1 . 2))))
                      (sd6 (((1 . 1) (nil . 1)) ((nil . 1) (-1 . 1))))
                      (srd6
                (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
              ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                      (sr2d6
               (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
               ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (1 . 2))))
                    (sr3d6 (((-1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (sr4d6
              (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
              ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2) (1 . 2))))
                      (sr5d6
               (((1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))),'real)$
set!*representation('d6,
                    '(realtype
                      (id (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (rd6
               (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (r2d6
             (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
             ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                     (r3d6 (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (r4d6
              (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))
                      (r5d6
             (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
            ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                    (sd6 (((1 . 1) (nil . 1)) ((nil . 1) (-1 . 1))))
                      (srd6
              (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
              ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (1 . 2))))
                      (sr2d6
             (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
             ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2) (1 . 2))))
                   (sr3d6 (((1 . 1) (nil . 1)) ((nil . 1) (-1 . 1))))
                      (sr4d6
               (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
               ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (1 . 2))))
                      (sr5d6
              (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (1 . 2))))),'real)$
set!*available 'd6$

set!*elems!*group('c3,'(id rc3 r2c3))$
set!*generators('c3,'(rc3))$
set!*relations('c3,'(((rc3 rc3 rc3) (id))))$
set!*grouptable('c3,
                '((grouptable id rc3 r2c3)
                  (id id rc3 r2c3)
                  (rc3 rc3 r2c3 id)
                  (r2c3 r2c3 id rc3)))$
set!*inverse('c3,'((id rc3 r2c3) (id r2c3 rc3)))$
set!*elemasgen('c3,'(((rc3) (rc3)) ((r2c3) (rc3 rc3))))$
set!*group('c3,'((id) (rc3) (r2c3)))$
set!*representation('c3,
             '((id (((1 . 1)))) (rc3 (((1 . 1)))) (r2c3 (((1 . 1))))),
                    'complex)$
set!*representation('c3,
                    '((id (((1 . 1))))
                      (rc3
               (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . 1)) . -1)
                          . 2))))
                      (r2c3
              (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . -1)) . -1)
                          . 2))))),'complex)$
set!*representation('c3,
                    '((id (((1 . 1))))
                      (rc3
              (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . -1)) . -1)
                          . 2))))
                      (r2c3
               (((((((expt 3 (quotient 1 2)) . 1) ((i . 1) . 1)) . -1)
                          . 2))))),'complex)$
set!*representation('c3,
                    '(realtype
                      (id (((1 . 1))))
                      (rc3 (((1 . 1))))
                      (r2c3 (((1 . 1))))),'real)$
set!*representation('c3,
                    '(complextype
                      (id (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (rc3
              (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . -1)) . 2))
              ((((((expt 3 (quotient 1 2)) . 1) . 1)) . 2) (-1 . 2))))
                      (r2c3
               (((-1 . 2) (((((expt 3 (quotient 1 2)) . 1) . 1)) . 2))
                        ((((((expt 3 (quotient 1 2)) . 1) . -1)) . 2)
                         (-1 . 2))))),'real)$
set!*available 'c3$

set!*elems!*group('c4,'(id rc4 r2c4 r3c4))$
set!*generators('c4,'(rc4))$
set!*relations('c4,'(((rc4 rc4 rc4 rc4) (id))))$
set!*grouptable('c4,
                '((grouptable id rc4 r2c4 r3c4)
                  (id id rc4 r2c4 r3c4)
                  (rc4 rc4 r2c4 r3c4 id)
                  (r2c4 r2c4 r3c4 id rc4)
                  (r3c4 r3c4 id rc4 r2c4)))$
set!*inverse('c4,'((id rc4 r2c4 r3c4) (id r3c4 r2c4 rc4)))$
set!*elemasgen('c4,
    '(((rc4) (rc4)) ((r2c4) (rc4 rc4)) ((r3c4) (rc4 rc4 rc4))))$
set!*group('c4,'((id) (rc4) (r2c4) (r3c4)))$
set!*representation('c4,
                    '((id (((1 . 1))))
                      (rc4 (((1 . 1))))
                      (r2c4 (((1 . 1))))
                      (r3c4 (((1 . 1))))),'complex)$
set!*representation('c4,
                    '((id (((1 . 1))))
                      (rc4 (((-1 . 1))))
                      (r2c4 (((1 . 1))))
                      (r3c4 (((-1 . 1))))),'complex)$
set!*representation('c4,
                    '((id (((1 . 1))))
                      (rc4 ((((((i . 1) . 1)) . 1))))
                      (r2c4 (((-1 . 1))))
                      (r3c4 ((((((i . 1) . -1)) . 1))))),'complex)$
set!*representation('c4,
                    '((id (((1 . 1))))
                      (rc4 ((((((i . 1) . -1)) . 1))))
                      (r2c4 (((-1 . 1))))
                      (r3c4 ((((((i . 1) . 1)) . 1))))),'complex)$
set!*representation('c4,
                    '(realtype
                      (id (((1 . 1))))
                      (rc4 (((1 . 1))))
                      (r2c4 (((1 . 1))))
                      (r3c4 (((1 . 1))))),'real)$
set!*representation('c4,
                    '(realtype
                      (id (((1 . 1))))
                      (rc4 (((-1 . 1))))
                      (r2c4 (((1 . 1))))
                      (r3c4 (((-1 . 1))))),'real)$
set!*representation('c4,
                    '(complextype
                      (id (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (rc4 (((nil . 1) (-1 . 1)) ((1 . 1) (nil . 1))))
                      (r2c4 (((-1 . 1) (nil . 1)) ((nil . 1) (-1 . 1))))
             (r3c4 (((nil . 1) (1 . 1)) ((-1 . 1) (nil . 1))))),'real)$
set!*available 'c4$

set!*elems!*group('c5,'(id rc5 r2c5 r3c5 r4c5))$
set!*generators('c5,'(rc5))$
set!*relations('c5,'(((rc5 rc5 rc5 rc5 rc5) (id))))$
set!*grouptable('c5,
                '((grouptable id rc5 r2c5 r3c5 r4c5)
                  (id id rc5 r2c5 r3c5 r4c5)
                  (rc5 rc5 r2c5 r3c5 r4c5 id)
                  (r2c5 r2c5 r3c5 r4c5 id rc5)
                  (r3c5 r3c5 r4c5 id rc5 r2c5)
                  (r4c5 r4c5 id rc5 r2c5 r3c5)))$
set!*inverse('c5,'((id rc5 r2c5 r3c5 r4c5) (id r4c5 r3c5 r2c5 rc5)))$
set!*elemasgen('c5,
               '(((rc5) (rc5))
                 ((r2c5) (rc5 rc5))
                 ((r3c5) (rc5 rc5 rc5))
                 ((r4c5) (rc5 rc5 rc5 rc5))))$
set!*group('c5,'((id) (rc5) (r2c5) (r3c5) (r4c5)))$
set!*representation('c5,
                    '((id (((1 . 1))))
                      (rc5 (((1 . 1))))
                      (r2c5 (((1 . 1))))
                      (r3c5 (((1 . 1))))
                      (r4c5 (((1 . 1))))),'complex)$
set!*representation('c5,
                    '((id (((1 . 1))))
                      (rc5
              (((((((sin (quotient (times 2 pi) 5)) . 1) ((i . 1) . 1))
                           (((cos (quotient (times 2 pi) 5)) . 1) . 1))
                          . 1))))
                      (r2c5
                       (((((((sin (quotient (times 2 pi) 5)) . 2) . -1)
                           (((sin (quotient (times 2 pi) 5)) . 1)
                            (((cos (quotient (times 2 pi) 5)) . 1)
                             ((i . 1) . 2)))
                           (((cos (quotient (times 2 pi) 5)) . 2) . 1))
                          . 1))))
                      (r3c5
                       (((((((sin (quotient (times 2 pi) 5)) . 3)
                            ((i . 1) . -1))
                           (((sin (quotient (times 2 pi) 5)) . 2)
                         (((cos (quotient (times 2 pi) 5)) . 1) . -3))
                           (((sin (quotient (times 2 pi) 5)) . 1)
                            (((cos (quotient (times 2 pi) 5)) . 2)
                             ((i . 1) . 3)))
                         (((cos (quotient (times 2 pi) 5)) . 3) . 1))
                          . 1))))
                      (r4c5
                       (((((((sin (quotient (times 2 pi) 5)) . 4) . 1)
                           (((sin (quotient (times 2 pi) 5)) . 3)
                            (((cos (quotient (times 2 pi) 5)) . 1)
                             ((i . 1) . -4)))
                           (((sin (quotient (times 2 pi) 5)) . 2)
                          (((cos (quotient (times 2 pi) 5)) . 2) . -6))
                           (((sin (quotient (times 2 pi) 5)) . 1)
                            (((cos (quotient (times 2 pi) 5)) . 3)
                             ((i . 1) . 4)))
                           (((cos (quotient (times 2 pi) 5)) . 4) . 1))
                          . 1))))),'complex)$
set!*representation('c5,
                    '((id (((1 . 1))))
                      (rc5
             (((((((sin (quotient (times 4 pi) 5)) . 1) ((i . 1) . 1))
                           (((cos (quotient (times 4 pi) 5)) . 1) . 1))
                          . 1))))
                      (r2c5
                       (((((((sin (quotient (times 4 pi) 5)) . 2) . -1)
                           (((sin (quotient (times 4 pi) 5)) . 1)
                            (((cos (quotient (times 4 pi) 5)) . 1)
                             ((i . 1) . 2)))
                           (((cos (quotient (times 4 pi) 5)) . 2) . 1))
                          . 1))))
                      (r3c5
                       (((((((sin (quotient (times 4 pi) 5)) . 3)
                            ((i . 1) . -1))
                           (((sin (quotient (times 4 pi) 5)) . 2)
                          (((cos (quotient (times 4 pi) 5)) . 1) . -3))
                           (((sin (quotient (times 4 pi) 5)) . 1)
                            (((cos (quotient (times 4 pi) 5)) . 2)
                             ((i . 1) . 3)))
                           (((cos (quotient (times 4 pi) 5)) . 3) . 1))
                          . 1))))
                      (r4c5
                       (((((((sin (quotient (times 4 pi) 5)) . 4) . 1)
                           (((sin (quotient (times 4 pi) 5)) . 3)
                            (((cos (quotient (times 4 pi) 5)) . 1)
                             ((i . 1) . -4)))
                           (((sin (quotient (times 4 pi) 5)) . 2)
                          (((cos (quotient (times 4 pi) 5)) . 2) . -6))
                           (((sin (quotient (times 4 pi) 5)) . 1)
                            (((cos (quotient (times 4 pi) 5)) . 3)
                             ((i . 1) . 4)))
                           (((cos (quotient (times 4 pi) 5)) . 4) . 1))
                          . 1))))),'complex)$
set!*representation('c5,
                    '((id (((1 . 1))))
                      (rc5
                       (((((((sin (quotient (times 4 pi) 5)) . 1)
                            ((i . 1) . -1))
                           (((cos (quotient (times 4 pi) 5)) . 1) . 1))
                          . 1))))
                      (r2c5
                       (((((((sin (quotient (times 4 pi) 5)) . 2) . -1)
                           (((sin (quotient (times 4 pi) 5)) . 1)
                            (((cos (quotient (times 4 pi) 5)) . 1)
                             ((i . 1) . -2)))
                           (((cos (quotient (times 4 pi) 5)) . 2) . 1))
                          . 1))))
                      (r3c5
               (((((((sin (quotient (times 4 pi) 5)) . 3) ((i . 1) . 1))
                           (((sin (quotient (times 4 pi) 5)) . 2)
                         (((cos (quotient (times 4 pi) 5)) . 1) . -3))
                           (((sin (quotient (times 4 pi) 5)) . 1)
                            (((cos (quotient (times 4 pi) 5)) . 2)
                             ((i . 1) . -3)))
                           (((cos (quotient (times 4 pi) 5)) . 3) . 1))
                          . 1))))
                      (r4c5
                       (((((((sin (quotient (times 4 pi) 5)) . 4) . 1)
                           (((sin (quotient (times 4 pi) 5)) . 3)
                            (((cos (quotient (times 4 pi) 5)) . 1)
                             ((i . 1) . 4)))
                           (((sin (quotient (times 4 pi) 5)) . 2)
                         (((cos (quotient (times 4 pi) 5)) . 2) . -6))
                           (((sin (quotient (times 4 pi) 5)) . 1)
                            (((cos (quotient (times 4 pi) 5)) . 3)
                             ((i . 1) . -4)))
                           (((cos (quotient (times 4 pi) 5)) . 4) . 1))
                          . 1))))),'complex)$
set!*representation('c5,
                    '((id (((1 . 1))))
                      (rc5
                       (((((((sin (quotient (times 2 pi) 5)) . 1)
                            ((i . 1) . -1))
                           (((cos (quotient (times 2 pi) 5)) . 1) . 1))
                          . 1))))
                      (r2c5
                       (((((((sin (quotient (times 2 pi) 5)) . 2) . -1)
                           (((sin (quotient (times 2 pi) 5)) . 1)
                            (((cos (quotient (times 2 pi) 5)) . 1)
                             ((i . 1) . -2)))
                           (((cos (quotient (times 2 pi) 5)) . 2) . 1))
                          . 1))))
                      (r3c5
             (((((((sin (quotient (times 2 pi) 5)) . 3) ((i . 1) . 1))
                           (((sin (quotient (times 2 pi) 5)) . 2)
                         (((cos (quotient (times 2 pi) 5)) . 1) . -3))
                           (((sin (quotient (times 2 pi) 5)) . 1)
                            (((cos (quotient (times 2 pi) 5)) . 2)
                             ((i . 1) . -3)))
                           (((cos (quotient (times 2 pi) 5)) . 3) . 1))
                          . 1))))
                      (r4c5
                       (((((((sin (quotient (times 2 pi) 5)) . 4) . 1)
                           (((sin (quotient (times 2 pi) 5)) . 3)
                            (((cos (quotient (times 2 pi) 5)) . 1)
                             ((i . 1) . 4)))
                           (((sin (quotient (times 2 pi) 5)) . 2)
                         (((cos (quotient (times 2 pi) 5)) . 2) . -6))
                           (((sin (quotient (times 2 pi) 5)) . 1)
                            (((cos (quotient (times 2 pi) 5)) . 3)
                             ((i . 1) . -4)))
                        (((cos (quotient (times 2 pi) 5)) . 4) . 1))
                          . 1))))),'complex)$
set!*representation('c5,
                    '(realtype
                      (id (((1 . 1))))
                      (rc5 (((1 . 1))))
                      (r2c5 (((1 . 1))))
                      (r3c5 (((1 . 1))))
                      (r4c5 (((1 . 1))))),'real)$
set!*representation('c5,
                    '(complextype
                      (id (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (rc5
                 (((((((cos (quotient (times 2 pi) 5)) . 1) . 1)) . 1)
                 (((((sin (quotient (times 2 pi) 5)) . 1) . -1)) . 1))
                  ((((((sin (quotient (times 2 pi) 5)) . 1) . 1)) . 1)
                (((((cos (quotient (times 2 pi) 5)) . 1) . 1)) . 1))))
                      (r2c5
                       (((((((sin (quotient (times 2 pi) 5)) . 2) . -1)
                           (((cos (quotient (times 2 pi) 5)) . 2) . 1))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 1)
                         (((cos (quotient (times 2 pi) 5)) . 1) . -2)))
                          . 1))
                        ((((((sin (quotient (times 2 pi) 5)) . 1)
                          (((cos (quotient (times 2 pi) 5)) . 1) . 2)))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 2) . -1)
                           (((cos (quotient (times 2 pi) 5)) . 2) . 1))
                          . 1))))
                      (r3c5
                       (((((((sin (quotient (times 2 pi) 5)) . 2)
                         (((cos (quotient (times 2 pi) 5)) . 1) . -3))
                           (((cos (quotient (times 2 pi) 5)) . 3) . 1))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 3) . 1)
                           (((sin (quotient (times 2 pi) 5)) . 1)
                         (((cos (quotient (times 2 pi) 5)) . 2) . -3)))
                          . 1))
                        ((((((sin (quotient (times 2 pi) 5)) . 3) . -1)
                           (((sin (quotient (times 2 pi) 5)) . 1)
                          (((cos (quotient (times 2 pi) 5)) . 2) . 3)))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 2)
                          (((cos (quotient (times 2 pi) 5)) . 1) . -3))
                           (((cos (quotient (times 2 pi) 5)) . 3) . 1))
                          . 1))))
                      (r4c5
                       (((((((sin (quotient (times 2 pi) 5)) . 4) . 1)
                           (((sin (quotient (times 2 pi) 5)) . 2)
                          (((cos (quotient (times 2 pi) 5)) . 2) . -6))
                           (((cos (quotient (times 2 pi) 5)) . 4) . 1))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 3)
                            (((cos (quotient (times 2 pi) 5)) . 1) . 4))
                           (((sin (quotient (times 2 pi) 5)) . 1)
                          (((cos (quotient (times 2 pi) 5)) . 3) . -4)))
                          . 1))
                        ((((((sin (quotient (times 2 pi) 5)) . 3)
                         (((cos (quotient (times 2 pi) 5)) . 1) . -4))
                           (((sin (quotient (times 2 pi) 5)) . 1)
                         (((cos (quotient (times 2 pi) 5)) . 3) . 4)))
                          . 1)
                         (((((sin (quotient (times 2 pi) 5)) . 4) . 1)
                           (((sin (quotient (times 2 pi) 5)) . 2)
                         (((cos (quotient (times 2 pi) 5)) . 2) . -6))
                           (((cos (quotient (times 2 pi) 5)) . 4) . 1))
                          . 1))))),'real)$
set!*representation('c5,
                    '(complextype
                      (id (((1 . 1) (nil . 1)) ((nil . 1) (1 . 1))))
                      (rc5
                  (((((((cos (quotient (times 4 pi) 5)) . 1) . 1)) . 1)
                  (((((sin (quotient (times 4 pi) 5)) . 1) . -1)) . 1))
                   ((((((sin (quotient (times 4 pi) 5)) . 1) . 1)) . 1)
                 (((((cos (quotient (times 4 pi) 5)) . 1) . 1)) . 1))))
                      (r2c5
                       (((((((sin (quotient (times 4 pi) 5)) . 2) . -1)
                           (((cos (quotient (times 4 pi) 5)) . 2) . 1))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 1)
                          (((cos (quotient (times 4 pi) 5)) . 1) . -2)))
                          . 1))
                        ((((((sin (quotient (times 4 pi) 5)) . 1)
                          (((cos (quotient (times 4 pi) 5)) . 1) . 2)))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 2) . -1)
                           (((cos (quotient (times 4 pi) 5)) . 2) . 1))
                          . 1))))
                      (r3c5
                       (((((((sin (quotient (times 4 pi) 5)) . 2)
                          (((cos (quotient (times 4 pi) 5)) . 1) . -3))
                           (((cos (quotient (times 4 pi) 5)) . 3) . 1))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 3) . 1)
                           (((sin (quotient (times 4 pi) 5)) . 1)
                         (((cos (quotient (times 4 pi) 5)) . 2) . -3)))
                          . 1))
                        ((((((sin (quotient (times 4 pi) 5)) . 3) . -1)
                           (((sin (quotient (times 4 pi) 5)) . 1)
                         (((cos (quotient (times 4 pi) 5)) . 2) . 3)))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 2)
                         (((cos (quotient (times 4 pi) 5)) . 1) . -3))
                           (((cos (quotient (times 4 pi) 5)) . 3) . 1))
                          . 1))))
                      (r4c5
                       (((((((sin (quotient (times 4 pi) 5)) . 4) . 1)
                           (((sin (quotient (times 4 pi) 5)) . 2)
                          (((cos (quotient (times 4 pi) 5)) . 2) . -6))
                           (((cos (quotient (times 4 pi) 5)) . 4) . 1))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 3)
                            (((cos (quotient (times 4 pi) 5)) . 1) . 4))
                           (((sin (quotient (times 4 pi) 5)) . 1)
                        (((cos (quotient (times 4 pi) 5)) . 3) . -4)))
                          . 1))
                        ((((((sin (quotient (times 4 pi) 5)) . 3)
                          (((cos (quotient (times 4 pi) 5)) . 1) . -4))
                           (((sin (quotient (times 4 pi) 5)) . 1)
                          (((cos (quotient (times 4 pi) 5)) . 3) . 4)))
                          . 1)
                         (((((sin (quotient (times 4 pi) 5)) . 4) . 1)
                           (((sin (quotient (times 4 pi) 5)) . 2)
                          (((cos (quotient (times 4 pi) 5)) . 2) . -6))
                           (((cos (quotient (times 4 pi) 5)) . 4) . 1))
                          . 1))))),'real)$
set!*available 'c5$

endmodule;

end;
