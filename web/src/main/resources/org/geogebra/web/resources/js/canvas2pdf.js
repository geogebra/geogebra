/*
 *
 *  A canvas to PDF converter. Uses a mock canvas context to build a PDF document.
 *
 *  Licensed under the MIT license:
 *  http://www.opensource.org/licenses/mit-license.php
 *
 *  Author:
 *  Joshua Gould
 *
 *  @license Copyright (c) 2017 Joshua Gould
 */
(function(global) {
	'use strict';
	/**
	 * @name canvas2pdf
	 * @namespace
	 */
	var canvas2pdf = (typeof canvas2pdf !== 'undefined') ? canvas2pdf : {};
	if (typeof module !== 'undefined' && module.exports) {
		module.exports = canvas2pdf; // Node
	} else if (typeof define === 'function' && define.amd) {
		define(function() { // AMD module
			return canvas2pdf;
		});
	} else {
		global.canvas2pdf = canvas2pdf; // browser global
	}

	function hslToHex(h, s, l, a) {
		h = h % 360 + (h < 0) * 360;
		s = isNaN(h) || isNaN(s) ? 0 : s;
		var m2 = l + (l < 0.5 ? l : 1 - l) * s;
		var m1 = 2 * l - m2;
		return rgbToPdf(hsl2rgb(h >= 240 ? h - 240 : h + 120, m1, m2), hsl2rgb(h, m1, m2),
			hsl2rgb(h < 120 ? h + 240 : h - 120, m1, m2), a);
	}

	function hsl2rgb(h, m1, m2) {
		return (h < 60 ? m1 + (m2 - m1) * h / 60 :
			h < 180 ? m2 :
			h < 240 ? m1 + (m2 - m1) * (240 - h) / 60 :
			m1) * 255;
	}

	var reI = '\\s*([+-]?\\d+)\\s*',
		reN = '\\s*([+-]?\\d*\\.?\\d+(?:[eE][+-]?\\d+)?)\\s*',
		reP = '\\s*([+-]?\\d*\\.?\\d+(?:[eE][+-]?\\d+)?)%\\s*',
		reRgbInteger = new RegExp('^rgb\\(' + [reI, reI, reI] + '\\)$'),
		reRgbPercent = new RegExp('^rgb\\(' + [reP, reP, reP] + '\\)$'),
		reRgbaInteger = new RegExp('^rgba\\(' + [reI, reI, reI, reN] + '\\)$'),
		reRgbaPercent = new RegExp('^rgba\\(' + [reP, reP, reP, reN] + '\\)$'),
		reHslPercent = new RegExp('^hsl\\(' + [reN, reP, reP] + '\\)$'),
		reHslaPercent = new RegExp('^hsla\\(' + [reN, reP, reP, reN] + '\\)$');

	var rgbToPdf = function(r, g, b, a) {
		return {
			c: (r / 255) + " " + (g / 255) + " " + (b / 255),
			a: a
		};
	};

	var fixColor = function(value) {
		if (!value) {
			return {
				c: "0 0 0",
				a: 1
			};
		}

		// IE11 doesn't have String.startsWith()
		var startsWith = function(data, input) {
			return data.substring(0, input.length) === input;
		}

		if (!startsWith(value, "rgb") && !startsWith(value, "hsl")) {
			var d = document.createElement("div");
			d.style.color = value;
			document.body.appendChild(d);
			//Color in RGB
			value = window.getComputedStyle(d).color + "";
			document.body.removeChild(d);
		}
		// remove spaces
		// rgb(255, 0, 0) -> rgb(255,0,0)
		value = value.replace(/\s/g, '');

		var m;
		var format = (value + '').trim().toLowerCase();
		if ((m = reRgbInteger.exec(format))) { // rgb(255, 0, 0)
			return rgbToPdf(m[1], m[2], m[3], 1);
		} else if ((m = reRgbPercent.exec(format))) { // // rgb(100%, 0%, 0%)
			return rgbToPdf(m[1] * 255 / 100, m[2] * 255 / 100, m[3] * 255 / 100, 1);
		} else if ((m = reRgbaInteger.exec(format))) { // // rgba(255, 0, 0, 0.5)
			return rgbToPdf(m[1], m[2], m[3], m[4]);
		} else if ((m = reRgbaPercent.exec(format))) { // // rgb(100%, 0%, 0%, .2)
			return rgbToPdf(m[1] * 255 / 100, m[2] * 255 / 100, m[3] * 255 / 100, m[4]);
		} else if ((m = reHslPercent.exec(format))) { // // hsl(120, 50%, 50%)
			return hslToHex(m[1], m[2] / 100, m[3] / 100);
		} else if ((m = reHslaPercent.exec(format))) {
			return hslToHex(m[1], m[2] / 100, m[3] / 100, m[4]); // hsla(120, 50%, 50%, 1)
		} else {
			return {
				c: value,
				a: 1
			};
		}
	};

	/**
	 *
	 * @param stream Stream to write the PDF to.
	 * @param options Options passed to PDFDocument constructor.
	 * @constructor
	 */
	canvas2pdf.PdfContext = function(width, height) {
		var _this = this;
		var doc = new PDFKitMini();
		this.doc = doc;

		// make dummy canvas
		// needed for measureText()
		var canvas = document.createElement("canvas");
		this.context = canvas.getContext("2d");

		doc.pageSetWidth(width / 72);
		doc.pageSetHeight(height / 72);

		doc.addPage();

		var fontValue = '10px Helvetica';
		this.textAlign = 'left';
		this.textBaseline = 'alphabetic';

		var parseFont = function() {
			var regex = /^\s*(?=(?:(?:[-a-z]+\s*){0,2}(italic|oblique))?)(?=(?:(?:[-a-z]+\s*){0,2}(small-caps))?)(?=(?:(?:[-a-z]+\s*){0,2}(bold(?:er)?|lighter|[1-9]00))?)(?:(?:normal|\1|\2|\3)\s*){0,3}((?:xx?-)?(?:small|large)|medium|smaller|larger|[.\d]+(?:%|in|[cem]m|ex|p[ctx]))(?:\s*\/\s*(normal|[.\d]+(?:%|in|[cem]m|ex|p[ctx])))?\s*([-,'"\sa-z]+?)\s*$/i;
			var fontPart = regex.exec(_this.font);

			if (!fontPart) {
				console.log("error parsing font " + _this.font);
				fontPart = regex.exec('10px Helvetica');
			}

			// eg "geogebra-sans-serif, sans-serif";
			if (fontPart[6].indexOf("sans-serif") > -1) {
				fontPart[6] = "Helvetica";
			}

			// eg "geogebra-serif, serif";
			if (fontPart[6].indexOf("serif") > -1) {
				fontPart[6] = "Times-Roman";
			}

			var data = {
				style: fontPart[1] || 'normal',
				size: parseInt(fontPart[4]) || 10,
				family: fontPart[6] || 'Helvetica',
				weight: fontPart[3] || 'normal'
			};
			return data;
		};

		Object.defineProperty(this, 'fillStyle', {
			get: function() {
				return this.fillColor;
			},
			set: function(value) {
				this.fillColor = value;

				if (value instanceof PDFGradientFill) {
					// TODO
					console.log("TODO", _this.doc);
				} else {
					var color = fixColor(value);
					_this.doc.fillColor(color.c, color.a);
				}
			}
		});

		Object.defineProperty(this, 'strokeStyle', {
			get: function() {
				return _this.doc.strokeColor();
			},
			set: function(value) {
				var color = fixColor(value);
				_this.doc.strokeColor(color.c, color.a);
			}
		});

		Object.defineProperty(this, 'lineWidth', {
			get: function() {
				return _this.doc.setLineWidth();
			},
			set: function(value) {
				_this.doc.setLineWidth(value);
			}
		});

		Object.defineProperty(this, 'lineCap', {
			get: function() {
				return "butt";
				//return _this.doc.lineCap();
			},
			set: function(value) {
				_this.doc.lineCap(value);
			}
		});

		Object.defineProperty(this, 'lineJoin', {
			get: function() {
				// TODO:
				return "miter";
				//return _this.doc.lineJoin();
			},
			set: function(value) {
				_this.doc.lineJoin(value);
			}
		});

		Object.defineProperty(this, 'miterLimit', {
			get: function() {
				return 10.0;
			},
			set: function() { /*not implemented*/ }
		});

		Object.defineProperty(this, 'globalAlpha', {
			get: function() {
				return _this.doc.opacity();
			},
			set: function(value) {
				_this.doc.opacity(value);
			}
		});

		Object.defineProperty(this, 'font', {
			get: function() {
				return fontValue;
			},
			set: function(value) {
				// for measureText()
				this.context.font = value;

				fontValue = value;
				var parsedFont = parseFont(value);
				_this.doc.fontSize(parsedFont.size);
				_this.doc.font(parsedFont.family);
				_this.doc.setFontStyle(parsedFont.weight != "normal", parsedFont.style != "normal", false);

				//_this.lineHeight = this.doc.currentLineHeight(false);
			}
		});

		//_this.lineHeight = this.doc.currentLineHeight(false);
		this.font = fontValue;
		this.strokeStyle = 'rgb(0,0,0)';
		this.fillStyle = 'rgb(0,0,0)';
	};

	canvas2pdf.PdfContext.prototype.end = function() {
		this.doc.end();
	};

	canvas2pdf.PdfContext.prototype.addPage = function() {
		this.doc.addPage();
	};

	canvas2pdf.PdfContext.prototype.save = function() {
		this.doc.save();
	};

	canvas2pdf.PdfContext.prototype.restore = function() {
		this.doc.restore();
	};

	canvas2pdf.PdfContext.prototype.transform = function(a, b, c, d, e, f) {
		this.doc.transform(a, b, c, d, e, f);
	};

	canvas2pdf.PdfContext.prototype.scale = function(x, y) {
		this.doc.scale(x, y);
	};

	canvas2pdf.PdfContext.prototype.rotate = function(angle) {
		var degrees = (angle * 180 / Math.PI);
		this.doc.rotate(degrees);
	};

	canvas2pdf.PdfContext.prototype.translate = function(x, y) {
		this.doc.translate(x, y);
	};

	canvas2pdf.PdfContext.prototype.beginPath = function() {
		this.doc.beginPath();
	};

	canvas2pdf.PdfContext.prototype.moveTo = function(x, y) {
		this.doc.moveTo(x, y);
	};

	canvas2pdf.PdfContext.prototype.closePath = function() {
		this.doc.closePath();
	};

	canvas2pdf.PdfContext.prototype.lineTo = function(x, y) {
		this.doc.lineTo(x, y);
	};

	canvas2pdf.PdfContext.prototype.stroke = function() {
		this.doc.stroke();
	};

	canvas2pdf.PdfContext.prototype.fill = function(rule) {
		this.doc.fill(rule);
	};

	canvas2pdf.PdfContext.prototype.rect = function(x, y, width, height) {
		this.doc.rect(x, y, width, height);
	};

	canvas2pdf.PdfContext.prototype.fillRect = function(x, y, width, height) {
		this.doc.beginPath();
		this.doc.rect(x, y, width, height);
		this.doc.fill();
	};

	canvas2pdf.PdfContext.prototype.strokeRect = function(x, y, width, height) {
		this.doc.beginPath();
		this.doc.rect(x, y, width, height);
		this.doc.stroke();
	};

	/**
	 * "Clears" a canvas by just drawing a white rectangle in the current group.
	 */
	canvas2pdf.PdfContext.prototype.clearRect = function(x, y, width, height) {
		var oldFill = this.doc.fillColor();
		this.doc.fillColor('white');
		this.doc.rect(x, y, width, height);
		this.doc.fill();
		this.doc.fillColor(oldFill);
	};

	canvas2pdf.PdfContext.prototype.arc = function(x, y, r, a0, a1, ccw) {
		this.doc.arc(x, y, r, a0, a1, ccw);
	};

	canvas2pdf.PdfContext.prototype.bezierCurveTo = function(cp1x, cp1y, cp2x, cp2y, x, y) {
		this.doc.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y);
	};

	canvas2pdf.PdfContext.prototype.quadraticCurveTo = function(cpx, cpy, x, y) {
		this.doc.quadraticCurveTo(cpx, cpy, x, y);
	};
	canvas2pdf.PdfContext.prototype.createLinearGradient = function(x1, y1, x2, y2) {
		return this.doc.linearGradient(x1, y1, x2, y2);
	};

	canvas2pdf.PdfContext.prototype.createRadialGradient = function(x0, y0, r0, x1, y1, r1) {
		var gradient = this.doc.radialGradient(x0, y0, r0, x1, y1, r1);
		gradient.addColorStop = function(offset, color) {
			var fixedColor = fixColor(color);
			gradient.stop(offset, fixedColor.c, fixedColor.a);
		};
		return gradient;
	};

	canvas2pdf.PdfContext.prototype.fillText = function(text, x, y) {
		if (text && text.trim().length) {
			this.doc.textAdd(x, y, text);
		}
	};

	canvas2pdf.PdfContext.prototype.strokeText = function() {
		console.log('strokeText not implemented, use fillText');
	};

	canvas2pdf.PdfContext.prototype.measureText = function(text) {
		return this.context.measureText(text + "");
	};

	canvas2pdf.PdfContext.prototype.clip = function() {
		this.doc.clip();
	};

	// if you call this, you can't also call getPDFtext()!!!!!!
	canvas2pdf.PdfContext.prototype.getPDFbase64 = function() {
		return this.doc.getBase64Text();
	};

	// if you call this, you can't also call getPDFbase64()!!!!!!
	canvas2pdf.PdfContext.prototype.getPDFtext = function() {
		return this.doc.getText();
	};

	canvas2pdf.PdfContext.prototype.drawImage = function(img, x, y, w, h) {
		var det = this.m00_ * this.m11_ - this.m01_ * this.m10_;
		return this.doc.drawImage(img, x, y, w, h, Math.sqrt(Math.abs(det)));
	};

	canvas2pdf.PdfContext.prototype.setLineDash = function(dashArray) {
		return this.doc.setLineDash(dashArray);
	};

	canvas2pdf.PdfContext.prototype.createPattern = function(image) {
		this.doc.imageTileLoad(image);
	};

	/**
	 * Not yet implemented
	 */
	canvas2pdf.PdfContext.prototype.setTransform = function() {
		console.log('setTransform not implemented');
	};

	canvas2pdf.PdfContext.prototype.drawFocusRing = function() {
		console.log('drawFocusRing not implemented');
	};

	canvas2pdf.PdfContext.prototype.createImageData = function() {
		console.log('drawFocusRing not implemented');
	};

	canvas2pdf.PdfContext.prototype.getImageData = function() {
		console.log('getImageData not implemented');
	};

	canvas2pdf.PdfContext.prototype.putImageData = function() {
		console.log('putImageData not implemented');
	};

	canvas2pdf.PdfContext.prototype.globalCompositeOperation = function() {
		console.log('globalCompositeOperation not implemented');
	};

	canvas2pdf.PdfContext.prototype.arcTo = function() {
		console.log('arcTo not implemented');
	};

	/*
	@license MIT LICENSE
	Copyright (c) 2014 Devon Govett

	Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
	*/

	function PDFKitMini() {
		this.objects = [];
		this.catalog = this.add(new PDFCatalog());
		this.pages = this.add(new PDFPages());
		this.catalog.setPages(this.pages);
		// A4 in inches
		this.pageWidth = 8.27;
		this.pageHeight = 11.69;
		this.textStyle = new PDFTextStyle();
		this.fonts = [];
		this.lineWidth = 1;
		this.lineEndType = 0;
		this.alpha = 1;

		// use pako/fflate to compress streams if available
		// https://github.com/nodeca/pako (MIT)
		// https://github.com/101arrowz/fflate/ (MIT)
		canvas2pdf.useFlateDecode = !!window.pako || !!window.fflate;

		canvas2pdf.deflate = function(stringOrArray) {
			var input = stringOrArray;
			if (window.pako) {
				return window.pako.deflate(input);
			}
			if (window.fflate) {
				if (typeof input == "string") {
					var enc = new TextEncoder();
					input = enc.encode(input);
				}

				return window.fflate.zlibSync(input);
			}
			return input;
		}
	}

	PDFKitMini.prototype.add = function(a) {
		this.objects.push(a);
		a.id = this.objects.length;
		return a
	};

	PDFKitMini.prototype.pageSetWidth = function(w) {
		this.pageWidth = w;
	};

	PDFKitMini.prototype.pageSetHeight = function(h) {
		this.pageHeight = h
	};

	PDFKitMini.prototype.addPage = function() {
		this.currentPage = new PDFPage(this, this.pageWidth, this.pageHeight, this.pages.id);
		this.add(this.currentPage);
		this.pages.addPage(this.currentPage);
		var a = new PDFStream();
		this.add(a);
		this.currentPage.setStream(a);
	};

	PDFKitMini.prototype.font = function(font) {
		this.currentPage.setFontName(font);
	};

	PDFKitMini.prototype.fontSize = function(size) {
		this.currentPage.setFontSize(size);
	};

	PDFKitMini.prototype.lineJoin = function(style) {
		this.currentPage.setLineJoin(style);
	};

	PDFKitMini.prototype.lineCap = function(style) {
		this.currentPage.setLineCap(style);
	};

	PDFKitMini.prototype.setLineWidth = function(width) {
		this.currentPage.setLineWidth(width);
	};

	PDFKitMini.prototype.strokeColor = function(color, alpha) {
		this.currentPage.setStrokeColor(color, alpha);
	};

	PDFKitMini.prototype.fillColor = function(color, alpha) {
		this.currentPage.setFillColor(color, alpha);
	};

	PDFKitMini.prototype.pageSetCurrent = function(page) {
		this.currentPage = page;
	};

	PDFKitMini.prototype.font = function(font) {
		this.textStyle.setFontName(font)
	};

	PDFKitMini.prototype.fontSetSize = function(size) {
		this.textStyle.fontSetSize(size)
	};

	PDFKitMini.prototype.setFontStyle = function(bold, italic) {
		this.textStyle.setFontStyle(bold, italic)
	};

	PDFKitMini.prototype.setFont = function(a) {
		var b = a.getFontName();
		if (null != b) {
			var c = null,
				d;
			for (d = 0; d < this.fonts.length; d++) {
				if (this.fonts[d].fontName == b) {
					c = this.fonts[d];
				}
			}

			if (null == c) {
				c = new PDFFont(b);
				this.add(c);
				this.fonts.push(c);
			}

			a.setFont(c)
		}
	};

	PDFKitMini.prototype.textAdd = function(a, b, c) {
		var state = this.textStyle.clone();
		this.setFont(state)

		this.currentPage.textAdd(a, b, c, state)
	};

	PDFKitMini.prototype.imageLoadFromCanvas = function(a, scale) {
		a = new PDFImage(a);
		a.scale = scale;
		this.add(a);
		if (a.mask) {
			this.add(a.mask);
		}
		this.currentImage = a;
	};

	PDFKitMini.prototype.imageTileLoadFromCanvas = function(a) {
		a = new PDFImageTile(a);
		this.add(a);
		this.currentPage.currentImageTile = a;
	};

	PDFKitMini.prototype.linearGradient = function(x1, y1, x2, y2) {
		var a = new PDFGradientFill(x1, y1, x2, y2, this.currentPage);
		this.add(a);
		this.currentPage.currentImageTile = a;

		this.addPatternToPage(a);

		return a;
	};

	PDFKitMini.prototype.doDrawImage = function(x, y, width, height) {
		this.currentPage.drawImage(x, y, this.currentImage, this.alpha, width, height)
	};

	PDFKitMini.prototype.addPatternToPage = function(img) {
		this.currentPage.addPatternToPage(img)
	};

	PDFKitMini.prototype.moveTo = function(x, y) {
		this.currentPage.moveTo(x, y)
	};

	PDFKitMini.prototype.lineTo = function(x, y) {
		this.currentPage.lineTo(x, y)
	};

	PDFKitMini.prototype.bezierCurveTo = function(cp1x, cp1y, cp2x, cp2y, x, y) {
		this.currentPage.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y)
	};

	PDFKitMini.prototype.translate = function(x, y) {
		this.currentPage.translate(x, y)
	};

	PDFKitMini.prototype.transform = function(m0, m1, m2, m3, m4, m5) {
		this.currentPage.transform(m0, m1, m2, m3, m4, m5)
	};

	//img can be an image element or a canvas
	PDFKitMini.prototype.drawImage = function(img, x, y, w, h, contextScale) {
		var scale = 1;
		if (img.nodeName.toLowerCase() == "img") {
			//convert image to canvas
			var canvas = document.createElement('canvas');
			if (img.src.startsWith && img.src.startsWith("data:image/svg")) {
				// at least 2x more pixels than on screen
				scale = 2 * Math.max(1, contextScale || 1);
			}
			canvas.width = img.width * scale;
			canvas.height = img.height * scale;
			var context = canvas.getContext("2d");
			context.drawImage(img, 0, 0, img.width, img.height, 0, 0, canvas.width, canvas.height);

			img = canvas;
		}
		this.imageLoadFromCanvas(img, scale);
		this.doDrawImage(x, y, img.width, img.height);
	};

	//img can be an image element or a canvas
	PDFKitMini.prototype.imageTileLoad = function(img) {
		if (img.nodeName.toLowerCase() == "img") {
			//convert image to canvas
			var canvas = document.createElement('canvas');
			canvas.width = img.width;
			canvas.height = img.height;
			var context = canvas.getContext("2d");
			context.drawImage(img, 0, 0);

			img = canvas;
		}
		this.imageTileLoadFromCanvas(img);

		this.addPatternToPage(this.currentPage.currentImageTile);
	};

	PDFKitMini.prototype.scale = function(xFactor, yFactor, options) {
		this.currentPage.scale(xFactor, yFactor, options)
	};

	PDFKitMini.prototype.rotate = function(angle, options) {
		this.currentPage.rotate(angle, options)
	};

	PDFKitMini.prototype.beginPath = function() {
		this.currentPage.beginPath()
	};

	PDFKitMini.prototype.arc = function(x, y, r, startAngle, arcAngle, counterclockwise) {
		this.currentPage.arc(x, y, r, startAngle, arcAngle, counterclockwise)
	};

	PDFKitMini.prototype.closePath = function() {
		this.currentPage.closePath()
	};

	PDFKitMini.prototype.stroke = function() {
		this.currentPage.stroke()
	};

	PDFKitMini.prototype.clip = function(rule) {
		this.currentPage.clip(rule)
	};

	PDFKitMini.prototype.opacity = function(alpha) {
		this.currentPage.setAlpha(alpha)
	};

	PDFKitMini.prototype.setLineDash = function(dash) {
		this.currentPage.setLineDash(dash)
	};

	PDFKitMini.prototype.save = function() {
		this.currentPage.saveContext();
	};

	PDFKitMini.prototype.restore = function() {
		this.currentPage.restoreContext();
	};

	PDFKitMini.prototype.fill = function(rule) {
		this.currentPage.fill(rule)
	};

	PDFKitMini.prototype.rect = function(a, b, c, d) {
		this.currentPage.rect(a, b, c, d)
	};

	PDFKitMini.prototype.quadraticCurveTo = function(a, b, c, d) {
		this.currentPage.quadraticCurveTo(a, b, c, d)
	};

	PDFKitMini.prototype.graphicsSetAlpha = function(a) {
		this.alpha = Math.floor(100 * a / 255) / 100;
		this.textStyle.setAlpha(a)
	};

	PDFKitMini.prototype.graphicsSetLineEndType = function(a) {
		this.lineEndType = a
	};

	PDFKitMini.prototype._write = function(a) {
		this.stream += a + "\n";
	};

	PDFKitMini.prototype.getObject = function(a) {
		this.stream = "";

		this._write("%PDF-1.7");
		var _i, d;
		for (_i = 0; _i < this.objects.length; _i++) {
			d = this.objects[_i];
			d.offset = this.stream.length;
			d = d.getObject(this, a);

			this._write(d);
		}

		// adapted from PDFDocument.prototype._finalize

		a = this.stream.length;
		this._write("xref");
		this._write("0 " + (this.objects.length + 1));
		this._write("0000000000 65535 f");
		var _ref = this.objects;
		for (_i = 0; _i < _ref.length; _i++) {
			var offset = this.objects[_i].offset;
			offset = ('0000000000' + offset).slice(-10);
			this._write(offset + " 00000 n");
		}

		this._write("trailer");

		this._write(PDFObject.convert({
			Size: this.objects.length + 1,
			Root: new PDFReference(this.catalog.id + " 0 R"),
		}));

		this._write("startxref");
		this._write(a);
		this._write("%%EOF");
		return this.stream;
	};

	PDFKitMini.prototype.getBase64Text = function() {
		return "data:application/pdf;base64," + btoa(this.getObject(this));
	};

	function PDFCatalog() {}
	PDFCatalog.prototype.setPages = function(a) {
		this.props = {
			"Type": "Catalog",
			"Pages": new PDFReference(a.id + " 0 R")
		};
	};

	PDFCatalog.prototype.getObject = function() {
		return PDFObject.makeObject(this.props, this.id);
	};

	function PDFPages() {
		this.pages = []
	}

	PDFPages.prototype.addPage = function(a) {
		this.pages.push(a);
		a.setPageNumber(this.pages.length);
		return a
	};

	PDFPages.prototype.getObject = function() {
		var refs = "[";
		for (var i = 0 ; i < this.pages.length ; i++) {
			refs += this.pages[i].id;
			refs += " 0 R ";
		}

		refs += "]";

		var props = {
			"Type": "Pages",
			"Count": this.pages.length,
			"Kids": new PDFReference(refs)
		};
		return PDFObject.makeObject(props, this.id);
	};

	function PDFPage(pdf0, width0, height0, pagesID) {
		this.pdf = pdf0;
		this.fonts = [];
		this.images = [];
		this.fillImages = [];
		this.alphas = [];
		this.fillColor = "0 0 0 ";
		this.strokeColor = "0 0 0 ";
		this.lineWidth = 2;
		this.lineCap = 0;
		this.lineJoin = 1;
		this.fontSize = 12;
		this.font = "normal";
		// initial transform, identity matrix
		this.width = 72 * width0;
		this.height = 72 * height0;
		this.pagesID = pagesID;
		this._ctm = [1, 0, 0, 1, 0, 0];
	}

	PDFPage.prototype.setStream = function(a) {
		this.pdfStream = a
		// turn page upside-down
		// to map canvas -> pdf coord system
		this.transform(1, 0, 0, -1, 0, this.height);
	};

	PDFPage.prototype.setPageNumber = function(a) {
		this.pageNumber = a
	};

	// a x-coord
	// b y-coord
	// c text
	// d = PDFTextStyle {color:, font: new PDFFont("Times-Roman"), fontSize: 10}
	PDFPage.prototype.textAdd = function(x, y, text, d) {
		var fontSize = this.fontSize;

		if (this.fonts.indexOf(d.font) == -1) {
			this.fonts.push(d.font);
		}

		text = PDFObject.convert(new String(text));

		this.setAlpha(d.alpha);

		// make sure text isn't upside-down!
		y = this.height - y;
		this.saveContext();
		this.transform(1, 0, 0, -1, 0, this.height);

		this.pdfStream.addText("BT " + this.strokeColor + " rg /F" + d.font.id + " " + fontSize + " Tf " + "1 0 0 1 " + x + " " + y + " cm " + text + "Tj ET ");

		this.restoreContext();
	};

	PDFPage.prototype.setAlpha = function(a) {
		if (a != this.currentAlpha) {
			if (this.alphas.indexOf(a) == -1) {
				this.alphas.push(a);
			}
			var index = this.alphas.indexOf(a);
			this.pdfStream.addText("/Alpha" + index + " gs ");
			this.currentAlpha = a
		}
	};

	PDFPage.prototype.moveTo = function(x, y) {
		// m moveto
		this.pdfStream.addText(x + " " + y + " m ");
	};

	PDFPage.prototype.setLineWidth = function(width) {
		this.lineWidth = width;
	};

	PDFPage.prototype.lineTo = function(x, y) {
		// l lineto
		this.pdfStream.addText(x + " " + y + " l ");
	};

	PDFPage.prototype.bezierCurveTo = function(cp1x, cp1y, cp2x, cp2y, x, y) {
		// c 	curveto.
		this.pdfStream.addText(cp1x + " " + cp1y + " " + cp2x + " " + cp2y + " " + x + " " + y + " c ");
	};

	PDFPage.prototype.transform = function(m11, m12, m21, m22, dx, dy) {
		var m, m0, m1, m2, m3, m4, m5, v, values;
		m = this._ctm;
		m0 = m[0], m1 = m[1], m2 = m[2], m3 = m[3], m4 = m[4], m5 = m[5];
		m[0] = m0 * m11 + m2 * m12;
		m[1] = m1 * m11 + m3 * m12;
		m[2] = m0 * m21 + m2 * m22;
		m[3] = m1 * m21 + m3 * m22;
		m[4] = m0 * dx + m2 * dy + m4;
		m[5] = m1 * dx + m3 * dy + m5;
		values = ((function() {
			var _i, _len, _ref, _results;
			_ref = [m11, m12, m21, m22, dx, dy];
			_results = [];
			for (_i = 0, _len = _ref.length; _i < _len; _i++) {
				v = _ref[_i];
				_results.push(+v.toFixed(5));
			}
			return _results;
		})()).join(' ');
		this.pdfStream.addText("" + values + " cm ");
	};

	PDFPage.prototype.translate = function(x, y) {
		this.transform(1, 0, 0, 1, x, y);
	};

	PDFPage.prototype.rotate = function(angle, options) {
		var cos, rad, sin, x, x1, y, y1, _ref;
		if (options == null) {
			options = {};
		}
		rad = angle * Math.PI / 180;
		cos = Math.cos(rad);
		sin = Math.sin(rad);
		x = y = 0;
		if (options.origin != null) {
			_ref = options.origin, x = _ref[0], y = _ref[1];
			x1 = x * cos - y * sin;
			y1 = x * sin + y * cos;
			x -= x1;
			y -= y1;
		}
		this.transform(cos, sin, -sin, cos, x, y);
	};

	PDFPage.prototype.scale = function(xFactor, yFactor, options) {
		var x, y, _ref;
		if (yFactor == null) {
			yFactor = xFactor;
		}
		if (options == null) {
			options = {};
		}
		// removed, want scale(x,y) to work
		//    if (arguments.length === 2) {
		//      yFactor = xFactor;
		//      options = yFactor;
		//    }
		x = y = 0;
		if (options.origin != null) {
			_ref = options.origin, x = _ref[0], y = _ref[1];
			x -= xFactor * x;
			y -= yFactor * y;
		}
		this.transform(xFactor, 0, 0, yFactor, x, y);
	};

	PDFPage.prototype.stroke = function() {
		this.pdfStream.addTextCheckMerge("S ");
	};

	PDFPage.prototype.clip = function(rule) {
		this.pdfStream.addText(((/even-?odd/.test(rule)) ? "W* " : "W ") + ' n ');
	};


	PDFPage.prototype.setLineDash = function(dash) {
		this.lineDash = dash;
	};

	PDFPage.prototype.saveContext = function() {
		this.pdfStream.addText("q ");
	};

	PDFPage.prototype.restoreContext = function() {
		this.pdfStream.addText("Q ");
	};

	PDFPage.prototype.fill = function(rule) {
		if (this.currentImageTile) {
			this.pdfStream.addText("/Pattern cs ");
			this.pdfStream.addText("/Pattern CS ");
			this.pdfStream.addText("/Paint" + this.currentImageTile.id + " scn ");
			this.pdfStream.addText("/Paint" + this.currentImageTile.id + " SCN ");
			this.currentImageTile = undefined;
		}
		//	f 	fill path.
		// f* 	eofill Even/odd fill path.
		this.pdfStream.addTextCheckMerge((/even-?odd/.test(rule)) ? "f* " : "f ");
		//this.pdfStream.addText("f* ");
	};

	PDFPage.prototype.beginPath = function() {
		// w 	setlinewidth.
		// j 	setlinejoin.
		// J 	setlinecap.
		// RG 	setrgbcolor (stroke).
		// rg 	setrgbcolor (fill).
		// d dash array + offset
		if (this.lineDash) {
			this.pdfStream.addText("[");
			for (var i = 0; i < this.lineDash.length; i++) {
				this.pdfStream.addText(this.lineDash[i]);
				this.pdfStream.addText(" ");
			}
			// offset 0
			this.pdfStream.addText("] 0 d ");
		}
		this.setAlpha(this.alpha);

		this.pdfStream.addText(this.fillColor + " rg ");
		this.pdfStream.addText(this.strokeColor + " RG ");

		this.pdfStream.addText(this.lineCap + " J " + this.lineJoin + " j " + (this.lineWidth | 1) + " w ");
	};

	PDFPage.prototype.setStrokeColor = function(color, alpha) {
		this.strokeColor = color || "0 0 0";
		this.alpha = alpha;
	};

	PDFPage.prototype.setFillColor = function(color, alpha) {
		this.fillColor = color || "0 0 0";
		this.alpha = alpha;
	};

	PDFPage.prototype.closePath = function() {
		this.pdfStream.addText("h ");
	};

	// from PDFKit 0.8
	PDFPage.prototype.quadraticCurveTo = function(cpx, cpy, x, y) {
		return this.pdfStream.addText("" + cpx + " " + cpy + " " + x + " " + y + " v ");
	};

	// implement eg https://stackoverflow.com/questions/33676303/draw-and-arc-in-pdf-generation
	PDFPage.prototype._bezierCurve = function(cx, cy, width, height, startAngle, arcAngle) {
		var pi = Math.PI;

		var a = width;
		var b = height;

		//calculate trigonometric operations so we don't need to repeat the calculus
		var cos1 = Math.cos(startAngle * pi / 180);
		var sin1 = Math.sin(startAngle * pi / 180);
		var cos2 = Math.cos((startAngle + arcAngle) * pi / 180);
		var sin2 = Math.sin((startAngle + arcAngle) * pi / 180);

		//point p1. Start point
		var p1x = cx + a * cos1;
		var p1y = cy - b * sin1;

		//point d1. First derivative at start point.
		var d1x = -a * sin1;
		var d1y = -b * cos1;

		//point p2. End point
		var p2x = cx + a * cos2;
		var p2y = cy - b * sin2;

		//point d2. First derivative at end point
		var d2x = -a * sin2;
		var d2y = -b * cos2;

		//alpha constant
		var aux = Math.tan((arcAngle / 2) * pi / 180);
		var alpha = Math.sin(arcAngle * pi / 180) * (Math.sqrt(4 + 3 * aux * aux) - 1.0) / 3.0;

		//point q1. First control point
		var q1x = p1x + alpha * d1x;
		var q1y = p1y + alpha * d1y;

		//point q2. Second control point.
		var q2x = p2x - alpha * d2x;
		var q2y = p2y - alpha * d2y;

		return [p1x, p1y, q1x, q1y, q2x, q2y, p2x, p2y];
	}

	// https://stackoverflow.com/questions/33676303/draw-and-arc-in-pdf-generation
	PDFPage.prototype.arc = function(x, y, r, startAngle, arcAngle, counterclockwise) {
		if (counterclockwise) {
			console.log("Counterclockwise not supported");
		}
		startAngle *= 180 / Math.PI;
		arcAngle *= 180 / Math.PI;
		var width = r - 1;
		var height = r - 1;

		// 45degrees, ie max 8 arcs
		var maxAnglePerCurve = 45;

		var n = Math.ceil(Math.abs(arcAngle / maxAnglePerCurve));
		var currentStartAngle = startAngle;
		var actualArcAngle = (arcAngle) / n;
		for (var i = 0; i < n; i++) {
			var bezier = this._bezierCurve(x, y, width, height, currentStartAngle, actualArcAngle);
			if (i == 0) {
				this.moveTo(bezier[0], bezier[1]);
			}
			this.bezierCurveTo(bezier[2], bezier[3], bezier[4], bezier[5], bezier[6], bezier[7]);
			currentStartAngle += actualArcAngle;
		}
	}

	PDFPage.prototype.rect = function(x, y, width, height) {
		var x1 = x + width;
		var y1 = y + height;
		this.pdfStream.addText(x + " " + y + " m " + x1 + " " + y + " l " + x1 + " " + y1 + " l " + x + " " + y1 + " l " + x + " " + y + " l ")
	};

	PDFPage.prototype._CAP_STYLES = {
		BUTT: 0,
		ROUND: 1,
		SQUARE: 2
	};

	PDFPage.prototype.setLineCap = function(c) {
		if (typeof c === 'string') {
			c = this._CAP_STYLES[c.toUpperCase()];
		}
		this.lineCap = c;
	};


	PDFPage.prototype._JOIN_STYLES = {
		MITER: 0,
		ROUND: 1,
		BEVEL: 2
	};

	// from PDFKit 0.8
	PDFPage.prototype.setLineJoin = function(j) {
		if (typeof j === 'string') {
			j = this._JOIN_STYLES[j.toUpperCase()];
		}
		this.lineJoin = j;
	};

	PDFPage.prototype.setFontSize = function(size) {
		this.fontSize = size;
	};

	PDFPage.prototype.transform = function(m11, m12, m21, m22, dx, dy) {
		var m, m0, m1, m2, m3, m4, m5, v, values;
		m = this._ctm;
		m0 = m[0], m1 = m[1], m2 = m[2], m3 = m[3], m4 = m[4], m5 = m[5];
		m[0] = m0 * m11 + m2 * m12;
		m[1] = m1 * m11 + m3 * m12;
		m[2] = m0 * m21 + m2 * m22;
		m[3] = m1 * m21 + m3 * m22;
		m[4] = m0 * dx + m2 * dy + m4;
		m[5] = m1 * dx + m3 * dy + m5;
		values = ((function() {
			var _i, _len, _ref, _results;
			_ref = [m11, m12, m21, m22, dx, dy];
			_results = [];
			for (_i = 0, _len = _ref.length; _i < _len; _i++) {
				v = _ref[_i];
				_results.push(+v.toFixed(5));
			}
			return _results;
		})()).join(' ');
		this.pdfStream.addText("" + values + " cm ");
	};

	PDFPage.prototype.drawImage = function(x, y, im, alpha, width, height) {
		var scale = im.scale || 1;
		this.saveContext();
		this.transform(1, 0, 0, -1, 0, 0);
		this.translate(x, -height / scale - y);
		this.scale(width / scale, height / scale);
		this.pdfStream.addText("/Image" + im.id + " Do ");
		this.restoreContext();

		if (this.images.indexOf(im) == -1) {
			this.images.push(im);
		}
	};

	PDFPage.prototype.addPatternToPage = function(im) {
		if (this.fillImages.indexOf(im) == -1) {
			this.fillImages.push(im);
		}
	};

	PDFPage.prototype.getObject = function(a) {
		a.pageSetCurrent(this);

		var props = {
			"Type": "Page"
		};

		props["Parent"] = new PDFReference(this.pagesID + " 0 R");
		props["MediaBox"] = [0, 0, this.width, this.height];
		props["Contents"] = new PDFReference(this.pdfStream.id + " 0 R");

		var fontProps = "<<";

		if (this.fonts.length > 0) {
			for (var c = 0; c < this.fonts.length; c++) {
				var e = this.fonts[c];
				fontProps += "/F" + e.id + " " + e.id + " 0 R"
			}
		}
		fontProps += ">>";

		var alphaProps = {};

		if (this.alphas.length > 0) {
			for (c = 0; c < this.alphas.length; c++) {
				var alpha = this.alphas[c];
				if (isNaN(alpha) || (typeof alpha) === "undefined") {
					alpha = "1";
				}
				if (typeof alpha === "string") {
					alpha = alpha * 1;
				}

				alphaProps["Alpha" + c] = {
					"CA": alpha,
					"ca": alpha
				};
			}
		}

		var imageProps = "<<";

		if (this.images.length > 0) {
			for (var d = 0; d < this.images.length; d++) {
				e = this.images[d];
				imageProps += "/Image" + e.id + " " + e.id + " 0 R"
			}
		}
		imageProps += ">>";

		var patternProps = "<<";

		if (this.fillImages.length > 0) {
			for (d = 0; d < this.fillImages.length; d++) {
				e = this.fillImages[d];
				patternProps += "/Paint" + e.id + " " + e.id + " 0 R"
			}
		}
		patternProps += ">>";

		props["Resources"] = {};

		if (this.fonts.length > 0) {
			props["Resources"]["Font"] = new PDFReference(fontProps);
		}

		if (this.alphas.length > 0) {
			props["Resources"]["ExtGState"] = alphaProps;
		}

		if (this.fillImages.length > 0) {
			props["Resources"]["Pattern"] = new PDFReference(patternProps);
		}

		if (this.images.length > 0) {
			props["Resources"]["XObject"] = new PDFReference(imageProps);
		}

		return PDFObject.makeObject(props, this.id);
	};

	function PDFFont(a) {
		this.fontName = a;
	}

	PDFFont.TIMES = ["Times-Roman", "Times-Italic", "Times-Bold", "Times-BoldItalic"];
	PDFFont.HELVETICA = ["Helvetica", "Helvetica-Oblique", "Helvetica-Bold", "Helvetica-BoldOblique"];

	PDFFont.getPDFName = function(font, bold, italic) {
		var index = (bold ? 2 : 0) + (italic ? 1 : 0);

		if (font == "Helvetica") {
			return PDFFont.HELVETICA[index];
		}

		return PDFFont.TIMES[index];
	};

	PDFFont.prototype.getObject = function() {
		var props = {
			"Subtype": "Type1",
			"Name": "F" + this.id,
			"BaseFont": this.fontName,
			"Encoding": "WinAnsiEncoding",
			"Type": "Font"
		};

		return PDFObject.makeObject(props, this.id);
	};

	function PDFStream() {
		this.stream = ""
	}

	PDFStream.prototype.addText = function(text) {
		this.stream += text;
	};

	PDFStream.prototype.addTextCheckMerge = function(a) {
		//B 	fill and stroke path.
		//B* 	eofill and stroke path.
		//S 	stroke path.
		//f 	fill path.
		//f* 	eofill Even/odd fill path.

		// for IE11
		function endsWith(str, suffix) {
			return str.indexOf(suffix, str.length - suffix.length) !== -1;
		}

		if (a == "f* " && endsWith(this.stream, " S ")) {
			// TODO: not quite right, need stroke /then/ fill
			this.stream = this.stream.substring(0, this.stream.length - 3) + " B* ";
		} else if (a == "S " && endsWith(this.stream, " f* ")) {
			this.stream = this.stream.substring(0, this.stream.length - 4) + " B* ";
		} else if (a == "f " && endsWith(this.stream, " S ")) {
			// TODO: not quite right, need stroke /then/ fill
			this.stream = this.stream.substring(0, this.stream.length - 3) + " B ";
		} else if (a == "S " && endsWith(this.stream, " f ")) {
			this.stream = this.stream.substring(0, this.stream.length - 3) + " B ";
		} else {
			this.stream += a;
		}
	};

	PDFStream.prototype.replaceText = function(a, b) {
		this.stream = this.stream.replace(a, b)
	};

	PDFStream.prototype.getObject = function() {
		var stream = bufferToString(this.stream);
		var props = {
			"Length": stream.length,
		};
		if (canvas2pdf.useFlateDecode) {
			props["Filter"] = "FlateDecode";
		}
		return PDFObject.makeObject(props, this.id, stream);
	};

	// input may be string or Uint8Array
	var bufferToString = function(buffer) {
		if (canvas2pdf.useFlateDecode) {
			buffer = canvas2pdf.deflate(buffer);
		} else if (typeof buffer === "string") {
			return buffer;
		}

		var buffer2 = [];
		for (var i = 0 ; i < buffer.length ; i++) {
			buffer2.push(String.fromCharCode(buffer[i]));
		}
		return buffer2.join("");
	}

	function PDFImage(canvas, alphaBuffer) {
		this.width = canvas.width;
		this.height = canvas.height;
		this.isMask = !!alphaBuffer;
		if (!alphaBuffer) {
			this.loadData(canvas);
		} else {
			this.stream = bufferToString(alphaBuffer);
		}
	}

	PDFImage.prototype.loadData = function(canvas) {
		var ctx = canvas.getContext("2d");
		var buffer = new Uint8Array(this.height * this.width * 3);//[];
		var rawData = ctx.getImageData(0, 0, this.width, this.height);
		var i = 0;
		var alphaBuffer = new Uint8Array(this.height * this.width);
		var alphaI = 0;
		var needsAlpha = false;
		for (var y = 0; y < this.height; y++) {
			for (var x = 0; x < this.width; x++) {
				var red = rawData.data[(x + y * this.width) * 4];
				var green = rawData.data[(x + y * this.width) * 4 + 1];
				var blue = rawData.data[(x + y * this.width) * 4 + 2];
				var alpha = rawData.data[(x + y * this.width) * 4 + 3];
				buffer[i++] = red;
				buffer[i++] = green;
				buffer[i++] = blue;
				alphaBuffer[alphaI++] = alpha;
				if (alpha != 255) {
					needsAlpha = true;
				}
			}
		}
		if (needsAlpha) {
			this.mask = new PDFImage(canvas, alphaBuffer);
		}
		this.stream = bufferToString(buffer);
	}

	PDFImage.prototype.writeImage = function(a) {
		this.stream = a;
	};

	PDFImage.prototype.getObject = function() {
		var props = {
			"Type": "XObject",
			"Width": this.width,
			"Height": this.height,
			"Subtype": "Image",
			"ColorSpace": this.isMask ? "DeviceGray" : "DeviceRGB",
			"BitsPerComponent": 8,
			"Name": "Image" + this.id,
			"Length": this.stream.length
		}
		if (this.mask) {
			props["SMask"] = new PDFReference(this.mask.id + " 0 R");
		}
		if (canvas2pdf.useFlateDecode) {
			props["Filter"] = "FlateDecode";
		}
		return PDFObject.makeObject(props, this.id, this.stream);
	};

	function PDFGradientFill(x1, y1, x2, y2, currentPage) {
		this.page = currentPage;

		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;

		this.cols = ["white", "black"];
	}

	// n = 0 or 1
	PDFGradientFill.prototype.addColorStop = function(n, col) {
		if (n != 0 && n != 1) {
			console.error("only 0 and 1 suppored for addColorStop", n);
		}
		this.cols[Math.round(n)] = col;
	}

	PDFGradientFill.prototype.getObject = function() {
		var col0 = fixColor(this.cols[0]).c.split(" ");
		var col1 = fixColor(this.cols[1]).c.split(" ");

		var props = {
			"Type": "Pattern",
			"PatternType": 2,
			"Shading": {
				"ShadingType": 2,
				"Extend": [true, true],
				"Coords": [this.x1, this.page.height - this.y1, this.x2, this.page.height - this.y2],
				"ColorSpace": "DeviceRGB",
				"Function": {
					"FunctionType": 2,
					"N": 1,
					"Domain": [0, 1],
					"C0": [col0[0]*1, col0[1]*1, col0[2]*1],
					"C1": [col1[0]*1, col1[1]*1, col1[2]*1],
				}
			},
		}
		return PDFObject.makeObject(props, this.id, this.stream);
	};

	function PDFImageTile(canvas) {
		this.width = canvas.width;
		this.height = canvas.height;
		var ctx = canvas.getContext("2d");
		var buffer = [
			// eg"14.000 0.0000 0.0000 -14.000 0.0000 14.000 cm ",
			//this.width+" 0.0000 0.0000 -"+this.height+" 0.0000 "+this.height+" cm ",
			this.width + " 0.0000 0.0000 -" + this.height + " 0.0000 " + this.height + " cm ",
			"BI ",
			"/Width " + this.width + " ",
			"/Height " + this.height + " ",
			"/ColorSpace /DeviceRGB ",
			"/BitsPerComponent 8 ",
			"ID\n"
		];
		var rawData = ctx.getImageData(0, 0, this.width, this.height);
		// reflect vertically so it's drawn right way up!
		for (var y = this.height - 1; y >= 0; y--)
			for (var x = 0; x < this.width; x++) {
				var red = rawData.data[(x + y * this.width) * 4];
				var green = rawData.data[(x + y * this.width) * 4 + 1];
				var blue = rawData.data[(x + y * this.width) * 4 + 2];

				buffer.push(String.fromCharCode(red));
				buffer.push(String.fromCharCode(green));
				buffer.push(String.fromCharCode(blue));
			}
		buffer.push("\nEI\n");

		this.stream = buffer.join("");
	}

	PDFImageTile.prototype.writeImage = function(a) {
		this.stream = a;
	};

	PDFImageTile.prototype.getObject = function() {
		var props = {
			"Type": "Pattern",
			"PatternType": 1,
			"PaintType": 1,
			"TilingType": 1,
			"BBox": [0, 0, this.width, this.height],
			"XStep": this.width,
			"YStep": this.height,
			"Length": this.stream.length,
			"Resources": {
				ProcSet: ["PDF", "ImageC"]
			},
		}
		return PDFObject.makeObject(props, this.id, this.stream);
	};

	function PDFTextStyle() {
		this.fontName = "Helvetica";
		this.fontSize = 12;
		this.italic = false;
		this.bold = false;
		this.font = undefined;
		this.alpha = 1
	}

	PDFTextStyle.prototype.setFontName = function(a) {
		this.fontName = a;
		this.font = undefined;
	};

	PDFTextStyle.prototype.fontSetSize = function(a) {
		this.fontSize = a
	};

	PDFTextStyle.prototype.setFontStyle = function(b, i) {
		this.bold = b;
		this.italic = i;
	};

	PDFTextStyle.prototype.setColor = function(col) {
		this.color = col;
	};

	PDFTextStyle.prototype.setAlpha = function(a) {
		this.alpha = a
	};

	PDFTextStyle.prototype.getFontName = function() {
		return PDFFont.getPDFName(this.fontName, this.bold, this.italic)
	};

	PDFTextStyle.prototype.setFont = function(font) {
		this.font = font;
	};

	PDFTextStyle.prototype.clone = function() {
		var a = new PDFTextStyle();
		a.fontName = this.fontName;
		a.fontSize = this.fontSize;
		a.bold = this.bold;
		a.italic = this.italic;
		a.color = this.color;
		a.alpha = this.alpha;
		a.font = this.font;
		return a
	};

	/*
	PDFObject - converts JavaScript types into their corresponding PDF types.
	By Devon Govett
	 */
	var PDFObject, PDFReference;

	PDFObject = (function() {
		var escapable, escapableRe, pad;

		function PDFObject() {}

		pad = function(str, length) {
			return (Array(length + 1).join('0') + str).slice(-length);
		};

		escapableRe = /[\n\r\t\b\f()\\]/g;

		escapable = {
			'\n': '\\n',
			'\r': '\\r',
			'\t': '\\t',
			'\b': '\\b',
			'\f': '\\f',
			'\\': '\\\\',
			'(': '\\(',
			')': '\\)'
		};

		PDFObject.makeObject = function(props, id, stream) {
			var ret = (id + " 0 obj\n") + PDFObject.convert(props);

			if (stream) {
				ret += "\nstream\n" + stream + "\nendstream\n";
			}

			ret += "endobj\n"

			return ret;
		}

		PDFObject.convert = function(object) {
			var e, i, isUnicode, items, key, out, string, val, _i, _ref;
			if (typeof object === 'string') {
				return '/' + object;
			} else if (object instanceof String) {
				string = object.replace(escapableRe, function(c) {
					return escapable[c];
				});
				isUnicode = false;
				for (i = _i = 0, _ref = string.length; _i < _ref; i = _i += 1) {
					if (string.charCodeAt(i) > 0x7f) {
						isUnicode = true;
						break;
					}
				}
				// just remove Unicode characters for now...
				if (isUnicode) {
					var newString = "";
					for (i = _i = 0, _ref = string.length; _i < _ref; i = _i += 1) {
						if (string.charCodeAt(i) <= 0x7f) {
							newString += string[i];
						} else {
							newString += "?";
						}
					}
					string = newString;
				}
				return '(' + string + ')';
				//} else if (Buffer.isBuffer(object)) {
				//  return '<' + object.toString('hex') + '>';
			} else if (object instanceof PDFReference) {
				return object.toString();
			} else if (object instanceof Date) {
				return '(D:' + pad(object.getUTCFullYear(), 4) + pad(object.getUTCMonth() + 1, 2) + pad(object.getUTCDate(), 2) + pad(object.getUTCHours(), 2) + pad(object.getUTCMinutes(), 2) + pad(object.getUTCSeconds(), 2) + 'Z)';
			} else if (Array.isArray(object)) {
				items = ((function() {
					var _j, _len, _results;
					_results = [];
					for (_j = 0, _len = object.length; _j < _len; _j++) {
						e = object[_j];
						_results.push(PDFObject.convert(e));
					}
					return _results;
				})()).join(' ');
				return '[' + items + ']';
			} else if ({}.toString.call(object) === '[object Object]') {
				out = ['<<'];
				for (key in object) {
					val = object[key];
					out.push('/' + key + ' ' + PDFObject.convert(val));
				}
				out.push('>>');
				return out.join("") + "\n";
			} else {
				return '' + object;
			}
		};

		return PDFObject;
	})();

	PDFReference = (function() {

		function PDFReference(s) {
			this.str = s;
		}

		PDFReference.prototype.toString = function() {
			return this.str
		}

		return PDFReference;
	})();
})(typeof window !== 'undefined' ? window : this);

