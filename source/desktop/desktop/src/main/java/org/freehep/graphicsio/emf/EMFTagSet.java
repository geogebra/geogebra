// Copyright 2001-2006, FreeHEP.
package org.freehep.graphicsio.emf;

import org.freehep.util.io.TagSet;

/**
 * EMF specific tagset.
 * 
 * @author Mark Donszelmann
 * @version $Id: EMFTagSet.java,v 1.5 2009-08-17 21:44:45 murkle Exp $
 */
public class EMFTagSet extends TagSet {

	public EMFTagSet(int version) {
		if ((version >= 1) /* && (version < 0x4000) FIXME check */) {
			// Set for Windows 3
			addTag(new org.freehep.graphicsio.emf.gdi.PolyBezier()); // 2 02
			addTag(new org.freehep.graphicsio.emf.gdi.EMFPolygon()); // 3 03
			addTag(new org.freehep.graphicsio.emf.gdi.Polyline()); // 4 04
			addTag(new org.freehep.graphicsio.emf.gdi.PolyBezierTo()); // 5 05
			addTag(new org.freehep.graphicsio.emf.gdi.PolylineTo()); // 6 06
			addTag(new org.freehep.graphicsio.emf.gdi.PolyPolyline()); // 7 07
			addTag(new org.freehep.graphicsio.emf.gdi.PolyPolygon()); // 8 08
			addTag(new org.freehep.graphicsio.emf.gdi.SetWindowExtEx()); // 9 09
			addTag(new org.freehep.graphicsio.emf.gdi.SetWindowOrgEx()); // 10
																			// 0a
			addTag(new org.freehep.graphicsio.emf.gdi.SetViewportExtEx()); // 11
																			// 0b
			addTag(new org.freehep.graphicsio.emf.gdi.SetViewportOrgEx()); // 12
																			// 0c
			addTag(new org.freehep.graphicsio.emf.gdi.SetBrushOrgEx()); // 13 0d
			addTag(new org.freehep.graphicsio.emf.gdi.EOF()); // 14 0e
			addTag(new org.freehep.graphicsio.emf.gdi.SetPixelV()); // 15 0f
			addTag(new org.freehep.graphicsio.emf.gdi.SetMapperFlags()); // 16
																			// 10
			addTag(new org.freehep.graphicsio.emf.gdi.SetMapMode()); // 17 11
			addTag(new org.freehep.graphicsio.emf.gdi.SetBkMode()); // 18 12
			addTag(new org.freehep.graphicsio.emf.gdi.SetPolyFillMode()); // 19
																			// 13
			addTag(new org.freehep.graphicsio.emf.gdi.SetROP2()); // 20 14
			addTag(new org.freehep.graphicsio.emf.gdi.SetStretchBltMode()); // 21
																			// 15
			addTag(new org.freehep.graphicsio.emf.gdi.SetTextAlign()); // 22 16
			// addTag(new org.freehep.graphicsio.emf.gdi.SetColorAdjustment());
			// // 23 17
			addTag(new org.freehep.graphicsio.emf.gdi.SetTextColor()); // 24 18
			addTag(new org.freehep.graphicsio.emf.gdi.SetBkColor()); // 25 19
			addTag(new org.freehep.graphicsio.emf.gdi.OffsetClipRgn()); // 26 1a
			addTag(new org.freehep.graphicsio.emf.gdi.MoveToEx()); // 27 1b
			addTag(new org.freehep.graphicsio.emf.gdi.SetMetaRgn()); // 28 1c
			addTag(new org.freehep.graphicsio.emf.gdi.ExcludeClipRect()); // 29
																			// 1d
			addTag(new org.freehep.graphicsio.emf.gdi.IntersectClipRect()); // 30
																			// 1e
			addTag(new org.freehep.graphicsio.emf.gdi.ScaleViewportExtEx()); // 31
																				// 1f
			addTag(new org.freehep.graphicsio.emf.gdi.ScaleWindowExtEx()); // 32
																			// 20
			addTag(new org.freehep.graphicsio.emf.gdi.SaveDC()); // 33 21
			addTag(new org.freehep.graphicsio.emf.gdi.RestoreDC()); // 34 22
			addTag(new org.freehep.graphicsio.emf.gdi.SetWorldTransform()); // 35
																			// 23
			addTag(new org.freehep.graphicsio.emf.gdi.ModifyWorldTransform()); // 36
																				// 24
			addTag(new org.freehep.graphicsio.emf.gdi.SelectObject()); // 37 25
			addTag(new org.freehep.graphicsio.emf.gdi.CreatePen()); // 38 26
			addTag(new org.freehep.graphicsio.emf.gdi.CreateBrushIndirect()); // 39
																				// 27
			addTag(new org.freehep.graphicsio.emf.gdi.DeleteObject()); // 40 28
			addTag(new org.freehep.graphicsio.emf.gdi.AngleArc()); // 41 29
			addTag(new org.freehep.graphicsio.emf.gdi.Ellipse()); // 42 2a
			addTag(new org.freehep.graphicsio.emf.gdi.EMFRectangle()); // 43 2b
			addTag(new org.freehep.graphicsio.emf.gdi.RoundRect()); // 44 2c
			addTag(new org.freehep.graphicsio.emf.gdi.Arc()); // 45 2d
			addTag(new org.freehep.graphicsio.emf.gdi.Chord()); // 46 2e
			addTag(new org.freehep.graphicsio.emf.gdi.Pie()); // 47 2f
			addTag(new org.freehep.graphicsio.emf.gdi.SelectPalette()); // 48 30
			// addTag(new org.freehep.graphicsio.emf.gdi.CreatePalette()); // 49
			// 31
			// addTag(new org.freehep.graphicsio.emf.gdi.SetPaletteEntries());
			// // 50 32
			addTag(new org.freehep.graphicsio.emf.gdi.ResizePalette()); // 51 33
			addTag(new org.freehep.graphicsio.emf.gdi.RealizePalette()); // 52
																			// 34
			addTag(new org.freehep.graphicsio.emf.gdi.ExtFloodFill()); // 53 35
			addTag(new org.freehep.graphicsio.emf.gdi.LineTo()); // 54 36
			addTag(new org.freehep.graphicsio.emf.gdi.ArcTo()); // 55 37
			addTag(new org.freehep.graphicsio.emf.gdi.PolyDraw()); // 56 38
			addTag(new org.freehep.graphicsio.emf.gdi.SetArcDirection()); // 57
																			// 39
			addTag(new org.freehep.graphicsio.emf.gdi.SetMiterLimit()); // 58 3a
			addTag(new org.freehep.graphicsio.emf.gdi.BeginPath()); // 59 3b
			addTag(new org.freehep.graphicsio.emf.gdi.EndPath()); // 60 3c
			addTag(new org.freehep.graphicsio.emf.gdi.CloseFigure()); // 61 3d
			addTag(new org.freehep.graphicsio.emf.gdi.FillPath()); // 62 3e
			addTag(new org.freehep.graphicsio.emf.gdi.StrokeAndFillPath()); // 63
																			// 3f
			addTag(new org.freehep.graphicsio.emf.gdi.StrokePath()); // 64 40
			addTag(new org.freehep.graphicsio.emf.gdi.FlattenPath()); // 65 41
			addTag(new org.freehep.graphicsio.emf.gdi.WidenPath()); // 66 42
			addTag(new org.freehep.graphicsio.emf.gdi.SelectClipPath()); // 67
																			// 43
			addTag(new org.freehep.graphicsio.emf.gdi.AbortPath()); // 68 44
			// this tag does not exist // 69 45
			addTag(new org.freehep.graphicsio.emf.gdi.GDIComment()); // 70 46
			// addTag(new org.freehep.graphicsio.emf.gdi.FillRgn()); // 71 47
			// addTag(new org.freehep.graphicsio.emf.gdi.FrameRgn()); // 72 48
			// addTag(new org.freehep.graphicsio.emf.gdi.InvertRgn()); // 73 49
			// addTag(new org.freehep.graphicsio.emf.gdi.PaintRgn()); // 74 4a
			addTag(new org.freehep.graphicsio.emf.gdi.ExtSelectClipRgn()); // 75
																			// 4b
			addTag(new org.freehep.graphicsio.emf.gdi.BitBlt()); // 76 4c
			// addTag(new org.freehep.graphicsio.emf.gdi.StretchBlt()); // 77 4d
			// addTag(new org.freehep.graphicsio.emf.gdi.MaskBlt()); // 78 4e
			// addTag(new org.freehep.graphicsio.emf.gdi.PlgBlt()); // 79 4f
			// addTag(new org.freehep.graphicsio.emf.gdi.SetDIBitsToDevice());
			// // 80 50
			addTag(new org.freehep.graphicsio.emf.gdi.StretchDIBits()); // 81 51
			addTag(new org.freehep.graphicsio.emf.gdi.ExtCreateFontIndirectW()); // 82
																					// 52
			addTag(new org.freehep.graphicsio.emf.gdi.ExtTextOutA()); // 83 53
			addTag(new org.freehep.graphicsio.emf.gdi.ExtTextOutW()); // 84 54
			addTag(new org.freehep.graphicsio.emf.gdi.PolyBezier16()); // 85 55
			addTag(new org.freehep.graphicsio.emf.gdi.Polygon16()); // 86 56
			addTag(new org.freehep.graphicsio.emf.gdi.Polyline16()); // 87 57
			addTag(new org.freehep.graphicsio.emf.gdi.PolyBezierTo16()); // 88
																			// 58
			addTag(new org.freehep.graphicsio.emf.gdi.PolylineTo16()); // 89 59
			addTag(new org.freehep.graphicsio.emf.gdi.PolyPolyline16()); // 90
																			// 5a
			addTag(new org.freehep.graphicsio.emf.gdi.PolyPolygon16()); // 91 5b
			addTag(new org.freehep.graphicsio.emf.gdi.PolyDraw16()); // 92 5c
			// addTag(new org.freehep.graphicsio.emf.gdi.CreateMonoBrush()); //
			// 93 5d
			// addTag(new
			// org.freehep.graphicsio.emf.gdi.CreateDIBPatternBrushPt()); // 94
			// 5e
			addTag(new org.freehep.graphicsio.emf.gdi.ExtCreatePen()); // 95 5f
			// addTag(new org.freehep.graphicsio.emf.gdi.PolyTextOutA()); // 96
			// 60
			// addTag(new org.freehep.graphicsio.emf.gdi.PolyTextOutW()); // 97
			// 61

			// Set for Windows 4 (NT)
			addTag(new org.freehep.graphicsio.emf.gdi.SetICMMode()); // 98 62
			// addTag(new org.freehep.graphicsio.emf.gdi.CreateColorSpace()); //
			// 99 63
			// addTag(new org.freehep.graphicsio.emf.gdi.SetColorSpace()); //
			// 100 64
			// addTag(new org.freehep.graphicsio.emf.gdi.DeleteColorSpace()); //
			// 101 65
			// addTag(new org.freehep.graphicsio.emf.gdi.GLSRecord()); // 102 66
			// addTag(new org.freehep.graphicsio.emf.gdi.GLSBoundedRecord()); //
			// 103 67
			// addTag(new org.freehep.graphicsio.emf.gdi.PixelFormat()); // 104
			// 68

			// Set for Windows 5 (2000/XP)
			// addTag(new org.freehep.graphicsio.emf.gdi.DrawEscape()); // 105
			// 69
			// addTag(new org.freehep.graphicsio.emf.gdi.ExtEscape()); // 106 6a
			// addTag(new org.freehep.graphicsio.emf.gdi.StartDoc()); // 107 6b
			// addTag(new org.freehep.graphicsio.emf.gdi.SmallTextOut()); // 108
			// 6c
			// addTag(new org.freehep.graphicsio.emf.gdi.ForceUFIMapping()); //
			// 109 6d
			// addTag(new org.freehep.graphicsio.emf.gdi.NamedEscape()); // 110
			// 6e
			// addTag(new org.freehep.graphicsio.emf.gdi.ColorCorrectPalette());
			// // 111 6f
			// addTag(new org.freehep.graphicsio.emf.gdi.SetICMProfileA()); //
			// 112 70
			// addTag(new org.freehep.graphicsio.emf.gdi.SetICMProfileW()); //
			// 113 71
			addTag(new org.freehep.graphicsio.emf.gdi.AlphaBlend()); // 114 72
			// addTag(new org.freehep.graphicsio.emf.gdi.AlphaDIBBlend()); //
			// 115 73
			// addTag(new org.freehep.graphicsio.emf.gdi.TransparentBlt()); //
			// 116 74
			// addTag(new org.freehep.graphicsio.emf.gdi.TransparentDIB()); //
			// 117 75
			addTag(new org.freehep.graphicsio.emf.gdi.GradientFill()); // 118 76
			// addTag(new org.freehep.graphicsio.emf.gdi.SetLinkedUFIs()); //
			// 119 77
			// addTag(new
			// org.freehep.graphicsio.emf.gdi.SetTextJustification()); // 120 78
		}

		if (version >= 0x4000) {
			// From GdiPlusEnums.h of Microsoft Platform SDK 2003 R2
			// base 0x0004000
			addTag(new org.freehep.graphicsio.emf.gdiplus.Header()); // 1
			addTag(new org.freehep.graphicsio.emf.gdiplus.EndOfFile()); // 2

			// addTag(new org.freehep.graphicsio.emf.gdiplus.Comment()); // 3

			// addTag(new org.freehep.graphicsio.emf.gdiplus.GetDC()); // 4

			// addTag(new
			// org.freehep.graphicsio.emf.gdiplus.MultiFormatStart()); // 5
			// addTag(new
			// org.freehep.graphicsio.emf.gdiplus.MultiFormatSection());// 6
			// addTag(new org.freehep.graphicsio.emf.gdiplus.MultiFormatEnd());
			// // 7

			// For all persistent objects

			addTag(new org.freehep.graphicsio.emf.gdiplus.GDIPlusObject()); // 8

			// Drawing Records

			addTag(new org.freehep.graphicsio.emf.gdiplus.Clear()); // 9
			// addTag(new org.freehep.graphicsio.emf.gdiplus.FillRects()); // 10
			// addTag(new org.freehep.graphicsio.emf.gdiplus.DrawRects()); // 11
			// addTag(new org.freehep.graphicsio.emf.gdiplus.FillPolygon()); //
			// 12
			addTag(new org.freehep.graphicsio.emf.gdiplus.DrawLines()); // 13
			addTag(new org.freehep.graphicsio.emf.gdiplus.FillEllipse()); // 14
			addTag(new org.freehep.graphicsio.emf.gdiplus.DrawEllipse()); // 15
			// addTag(new org.freehep.graphicsio.emf.gdiplus.FillPie()); // 16
			// addTag(new org.freehep.graphicsio.emf.gdiplus.DrawPie()); // 17
			// addTag(new org.freehep.graphicsio.emf.gdiplus.DrawArc()); // 18
			// addTag(new org.freehep.graphicsio.emf.gdiplus.FillRegion()); //
			// 19
			addTag(new org.freehep.graphicsio.emf.gdiplus.FillPath()); // 20
			addTag(new org.freehep.graphicsio.emf.gdiplus.DrawPath()); // 21
			// addTag(new org.freehep.graphicsio.emf.gdiplus.FillClosedCurve());
			// // 22
			// addTag(new org.freehep.graphicsio.emf.gdiplus.DrawClosedCurve());
			// // 23
			// addTag(new org.freehep.graphicsio.emf.gdiplus.DrawCurve()); // 24
			// addTag(new org.freehep.graphicsio.emf.gdiplus.DrawBeziers()); //
			// 25
			addTag(new org.freehep.graphicsio.emf.gdiplus.DrawImage()); // 26
			// addTag(new org.freehep.graphicsio.emf.gdiplus.DrawImagePoints());
			// // 27
			// addTag(new org.freehep.graphicsio.emf.gdiplus.DrawString()); //
			// 28

			// Graphics State Records

			// addTag(new
			// org.freehep.graphicsio.emf.gdiplus.SetRenderingOrigin());// 29
			addTag(new org.freehep.graphicsio.emf.gdiplus.SetAntiAliasMode()); // 30
			// addTag(new
			// org.freehep.graphicsio.emf.gdiplus.SetTextRenderingHint());//31
			// addTag(new org.freehep.graphicsio.emf.gdiplus.SetTextContrast());
			// // 32
			// addTag(new
			// org.freehep.graphicsio.emf.gdiplus.SetInterpolationMode());//33
			// addTag(new
			// org.freehep.graphicsio.emf.gdiplus.SetPixelOffsetMode());// 34
			// addTag(new
			// org.freehep.graphicsio.emf.gdiplus.SetCompositingMode());// 35
			// addTag(new
			// org.freehep.graphicsio.emf.gdiplus.SetCompositingQuality());//36
			addTag(new org.freehep.graphicsio.emf.gdiplus.Save()); // 37
			addTag(new org.freehep.graphicsio.emf.gdiplus.Restore()); // 38
			// addTag(new org.freehep.graphicsio.emf.gdiplus.BeginContainer());
			// // 39
			// addTag(new
			// org.freehep.graphicsio.emf.gdiplus.BeginContainerNoParams());//40
			// addTag(new org.freehep.graphicsio.emf.gdiplus.EndContainer()); //
			// 41
			addTag(new org.freehep.graphicsio.emf.gdiplus.SetWorldTransform()); // 42
			// addTag(new
			// org.freehep.graphicsio.emf.gdiplus.ResetWorldTransform());// 43
			addTag(new org.freehep.graphicsio.emf.gdiplus.MultiplyWorldTransform());// 44
			// addTag(new
			// org.freehep.graphicsio.emf.gdiplus.TranslateWorldTransform());//45
			// addTag(new
			// org.freehep.graphicsio.emf.gdiplus.ScaleWorldTransform());// 46
			// addTag(new
			// org.freehep.graphicsio.emf.gdiplus.RotateWorldTransform());//47
			// addTag(new
			// org.freehep.graphicsio.emf.gdiplus.SetPageTransform()); // 48
			addTag(new org.freehep.graphicsio.emf.gdiplus.ResetClip()); // 49
			// addTag(new org.freehep.graphicsio.emf.gdiplus.SetClipRect()); //
			// 50
			addTag(new org.freehep.graphicsio.emf.gdiplus.SetClipPath()); // 51
			// addTag(new org.freehep.graphicsio.emf.gdiplus.SetClipRegion());
			// // 52
			// addTag(new org.freehep.graphicsio.emf.gdiplus.OffsetClip()); //
			// 53

			// addTag(new
			// org.freehep.graphicsio.emf.gdiplus.DrawDriverString()); // 54
		}
	}
}
