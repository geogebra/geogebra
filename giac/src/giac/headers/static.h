// -*- mode:C++  -*-
/*
 *  Copyright (C) 2010 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

#ifndef _STATIC_H
#define _STATIC_H
#include "first.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

#ifdef STATIC_BUILTIN_LEXER_FUNCTIONS
#define at_GF at_galois_field 
#define at_abscissa at_abscisse
#define at_affix at_affixe
#define at_altitude at_hauteur
#define at_area at_aire
#define at_bar_plot at_diagramme_batons
#define at_barycenter at_barycentre
#define at_bisector at_bissectrice
#define at_center at_centre
#define at_centered_cube at_cube_centre
#define at_centered_tetraedre at_tetraedre_centre
#define at_circle at_cercle
#define at_circumcircle at_circonscrit
#define at_color at_couleur
#define at_common_perpendicular at_perpendiculaire_commune
#define at_conic at_conique
#define at_coordinates at_coordonnees
#define at_cross_ratio at_birapport
#define at_cylinder at_cylindre
#define at_diff at_derive
#define at_distance at_longueur
#define at_distance2 at_longueur2
#define at_division_point at_point_div
#define at_dodecahedron at_dodecaedre
  // #define at_dot_paper at_papier_pointe
#define at_envelope at_enveloppe
#define at_equilateral_triangle at_triangle_equilateral
#define at_exbisector at_exbissectrice
#define at_excircle at_exinscrit
// #define at_grid_paper at_papier_quadrille
#define at_half_cone at_demi_cone
#define at_half_line at_demi_droite
#define at_harmonic_conjugate at_conj_harmonique
#define at_harmonic_division at_div_harmonique
#define at_hexagon at_hexagone
#define at_homothety at_homothetie
#define at_hyperbola at_hyperbole
#define at_icosahedron at_icosaedre
#define at_incircle at_inscrit
#define at_is_collinear at_est_aligne
#define at_is_concyclic at_est_cocyclique
#define at_is_conjugate at_est_conjugue
#define at_is_coplanar at_est_coplanaire
#define at_is_cospheric at_est_cospherique
#define at_is_element at_est_element
#define at_is_equilateral at_est_equilateral
#define at_is_harmonic at_est_harmonique
#define at_is_harmonic_circle_bundle at_est_faisceau_cercle
#define at_is_harmonic_line_bundle at_est_faisceau_droite
#define at_is_isoceles at_est_isocele
#define at_is_orthogonal at_est_orthogonal
#define at_is_parallel at_est_parallele
#define at_is_parallelogram at_est_parallelogramme
#define at_is_perpendicular at_est_perpendiculaire
#define at_is_rectangle at_est_rectangle
#define at_is_rhombus at_est_losange
#define at_is_square at_est_carre
#define at_isobarycenter at_isobarycentre
#define at_isoceles_triangle at_triangle_isocele
#define at_isopolygon at_isopolygone
#define at_legend at_legende
#define at_line at_droite
#define at_line_inter at_inter_droite
#define at_line_segments at_aretes
#define at_locus at_lieu
#define at_median_line at_mediane
#define at_midpoint at_milieu
#define at_octahedron at_octaedre
#define at_op at_feuille
#define at_open_polygon at_polygone_ouvert
#define at_ordinate at_ordonnee
#define at_orthocenter at_orthocentre
#define at_parabola at_parabole
#define at_parallel at_parallele
#define at_parallelepiped at_parallelepipede
#define at_parallelogram at_parallelogramme
#define at_perimeter at_perimetre
#define at_perpen_bisector at_mediatrice
#define at_perpendicular at_perpendiculaire
#define at_plane at_plan
#define at_polar at_polaire
#define at_polar_coordinates at_coordonnees_polaires
#define at_polar_point at_point_polaire
#define at_polygon at_polygone
#define at_polyhedron at_polyedre
#define at_powerpc at_puissance
#define at_prism at_prisme
#define at_pyramid at_pyramide
#define at_quadric at_quadrique
#define at_quadrilateral at_quadrilatere
#define at_radical_axis at_axe_radical
#define at_radius at_rayon
#define at_reciprocation at_polaire_reciproque
#define at_centered_tetrahedron at_tetraedre_centre
#define at_rectangular_coordinates at_coordonnees_rectangulaires
#define at_reduced_conic at_conique_reduite
#define at_reduced_quadric at_quadrique_reduite
#define at_reflection at_symetrie
#define at_rhombus at_losange
#define at_right_triangle at_triangle_rectangle
#define at_similarity at_similitude
#define at_single_inter at_inter_unique
#define at_square at_carre
#define at_tetrahedron at_tetraedre
#define at_vertices at_sommets
#define at_vertices_abc at_sommets_abc
#define at_vertices_abca at_sommets_abca

#include "static_extern.h"

#endif // STATIC_BUILTIN_LEXER_FUNCTIONS

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

#endif // _STATIC_H
