//====JSXGRAPH UTIL CLASS THANKS FOR THE UNZIPPING FUNCTIONALITY======//
		var ggm = {};
		ggm.JXG = {};

		ggm.JXG.Util = {};

		ggm.JXG.Util.Unzip = function(barray) {
			var outputArr = [], output = "", debug = false, gpflags, files = 0, unzipped = [], crc, buf32k = new Array(
					32768), bIdx = 0, modeZIP = false,

			CRC, SIZE,

			bitReverse = [ 0x00, 0x80, 0x40, 0xc0, 0x20, 0xa0, 0x60, 0xe0,
					0x10, 0x90, 0x50, 0xd0, 0x30, 0xb0, 0x70, 0xf0, 0x08, 0x88,
					0x48, 0xc8, 0x28, 0xa8, 0x68, 0xe8, 0x18, 0x98, 0x58, 0xd8,
					0x38, 0xb8, 0x78, 0xf8, 0x04, 0x84, 0x44, 0xc4, 0x24, 0xa4,
					0x64, 0xe4, 0x14, 0x94, 0x54, 0xd4, 0x34, 0xb4, 0x74, 0xf4,
					0x0c, 0x8c, 0x4c, 0xcc, 0x2c, 0xac, 0x6c, 0xec, 0x1c, 0x9c,
					0x5c, 0xdc, 0x3c, 0xbc, 0x7c, 0xfc, 0x02, 0x82, 0x42, 0xc2,
					0x22, 0xa2, 0x62, 0xe2, 0x12, 0x92, 0x52, 0xd2, 0x32, 0xb2,
					0x72, 0xf2, 0x0a, 0x8a, 0x4a, 0xca, 0x2a, 0xaa, 0x6a, 0xea,
					0x1a, 0x9a, 0x5a, 0xda, 0x3a, 0xba, 0x7a, 0xfa, 0x06, 0x86,
					0x46, 0xc6, 0x26, 0xa6, 0x66, 0xe6, 0x16, 0x96, 0x56, 0xd6,
					0x36, 0xb6, 0x76, 0xf6, 0x0e, 0x8e, 0x4e, 0xce, 0x2e, 0xae,
					0x6e, 0xee, 0x1e, 0x9e, 0x5e, 0xde, 0x3e, 0xbe, 0x7e, 0xfe,
					0x01, 0x81, 0x41, 0xc1, 0x21, 0xa1, 0x61, 0xe1, 0x11, 0x91,
					0x51, 0xd1, 0x31, 0xb1, 0x71, 0xf1, 0x09, 0x89, 0x49, 0xc9,
					0x29, 0xa9, 0x69, 0xe9, 0x19, 0x99, 0x59, 0xd9, 0x39, 0xb9,
					0x79, 0xf9, 0x05, 0x85, 0x45, 0xc5, 0x25, 0xa5, 0x65, 0xe5,
					0x15, 0x95, 0x55, 0xd5, 0x35, 0xb5, 0x75, 0xf5, 0x0d, 0x8d,
					0x4d, 0xcd, 0x2d, 0xad, 0x6d, 0xed, 0x1d, 0x9d, 0x5d, 0xdd,
					0x3d, 0xbd, 0x7d, 0xfd, 0x03, 0x83, 0x43, 0xc3, 0x23, 0xa3,
					0x63, 0xe3, 0x13, 0x93, 0x53, 0xd3, 0x33, 0xb3, 0x73, 0xf3,
					0x0b, 0x8b, 0x4b, 0xcb, 0x2b, 0xab, 0x6b, 0xeb, 0x1b, 0x9b,
					0x5b, 0xdb, 0x3b, 0xbb, 0x7b, 0xfb, 0x07, 0x87, 0x47, 0xc7,
					0x27, 0xa7, 0x67, 0xe7, 0x17, 0x97, 0x57, 0xd7, 0x37, 0xb7,
					0x77, 0xf7, 0x0f, 0x8f, 0x4f, 0xcf, 0x2f, 0xaf, 0x6f, 0xef,
					0x1f, 0x9f, 0x5f, 0xdf, 0x3f, 0xbf, 0x7f, 0xff ],

			cplens = [ 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 17, 19, 23, 27, 31,
					35, 43, 51, 59, 67, 83, 99, 115, 131, 163, 195, 227, 258,
					0, 0 ],

			cplext = [ 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3,
					3, 4, 4, 4, 4, 5, 5, 5, 5, 0, 99, 99 ],

			cpdist = [ 0x0001, 0x0002, 0x0003, 0x0004, 0x0005, 0x0007, 0x0009,
					0x000d, 0x0011, 0x0019, 0x0021, 0x0031, 0x0041, 0x0061,
					0x0081, 0x00c1, 0x0101, 0x0181, 0x0201, 0x0301, 0x0401,
					0x0601, 0x0801, 0x0c01, 0x1001, 0x1801, 0x2001, 0x3001,
					0x4001, 0x6001 ],

			cpdext = [ 0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8,
					8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13 ],

			border = [ 16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2,
					14, 1, 15 ],

			bA = barray,

			bytepos = 0, bitpos = 0, bb = 1, bits = 0,

			NAMEMAX = 256,

			nameBuf = [],

			fileout;

			function readByte() {
				bits += 8;
				if (bytepos < bA.length) {
					return bA[bytepos++];
				} else
					return -1;
			}
			;

			function byteAlign() {
				bb = 1;
			}
			;

			function readBit() {
				var carry;
				bits++;
				carry = (bb & 1);
				bb >>= 1;
				if (bb == 0) {
					bb = readByte();
					carry = (bb & 1);
					bb = (bb >> 1) | 0x80;
				}
				return carry;
			}
			;

			function readBits(a) {
				var res = 0, i = a;

				while (i--) {
					res = (res << 1) | readBit();
				}
				if (a) {
					res = bitReverse[res] >> (8 - a);
				}
				return res;
			}
			;

			function flushBuffer() {

				bIdx = 0;
			}
			;
			function addBuffer(a) {
				SIZE++;

				buf32k[bIdx++] = a;
				outputArr.push(String.fromCharCode(a));

				if (bIdx == 0x8000) {

					bIdx = 0;
				}
			}
			;

			function HufNode() {
				this.b0 = 0;
				this.b1 = 0;
				this.jump = null;
				this.jumppos = -1;
			}
			;

			var LITERALS = 288;

			var literalTree = new Array(LITERALS);
			var distanceTree = new Array(32);
			var treepos = 0;
			var Places = null;
			var Places2 = null;

			var impDistanceTree = new Array(64);
			var impLengthTree = new Array(64);

			var len = 0;
			var fpos = new Array(17);
			fpos[0] = 0;
			var flens;
			var fmax;

			function IsPat() {
				while (1) {
					if (fpos[len] >= fmax)
						return -1;
					if (flens[fpos[len]] == len)
						return fpos[len]++;
					fpos[len]++;
				}
			}
			;

			function Rec() {
				var curplace = Places[treepos];
				var tmp;
				if (debug) {
					//console.log("<br>len:" + len + " treepos:" + treepos);
				}
				if (len == 17) { //war 17
					return -1;
				}
				treepos++;
				len++;

				tmp = IsPat();
				if (debug) {
					//console.log("<br>IsPat " + tmp);
				}
				if (tmp >= 0) {
					curplace.b0 = tmp;
					if (debug) {
						//console.log("<br>b0 " + curplace.b0);
					}
				} else {

					curplace.b0 = 0x8000;
					if (debug) {
						//console.log("<br>b0 " + curplace.b0);
					}
					if (Rec())
						return -1;
				}
				tmp = IsPat();
				if (tmp >= 0) {
					curplace.b1 = tmp;
					if (debug) {
						//console.log("<br>b1 " + curplace.b1);
					}
					curplace.jump = null;
				} else {

					curplace.b1 = 0x8000;
					if (debug) {
						//console.log("<br>b1 " + curplace.b1);
					}
					curplace.jump = Places[treepos];
					curplace.jumppos = treepos;
					if (Rec())
						return -1;
				}
				len--;
				return 0;
			}
			;

			function CreateTree(currentTree, numval, lengths, show) {
				var i;

				if (debug) {
					//console.log("currentTree " + currentTree + " numval "
							//+ numval + " lengths " + lengths + " show " + show);
				}
				Places = currentTree;
				treepos = 0;
				flens = lengths;
				fmax = numval;
				for (i = 0; i < 17; i++)
					fpos[i] = 0;
				len = 0;
				if (Rec()) {

					if (debug) {
						//console.log("invalid huffman tree\n");
					}
					return -1;
				}
				if (debug) {
					//console.log('<br>Tree: ' + Places.length);
					/*for ( var a = 0; a < 32; a++) {
						document.write("Places[" + a + "].b0=" + Places[a].b0
								+ "<br>");
						document.write("Places[" + a + "].b1=" + Places[a].b1
								+ "<br>");
					}*/
				}

				return 0;
			}
			;

			function DecodeValue(currentTree) {
				var len, i, xtreepos = 0, X = currentTree[xtreepos], b;

				while (1) {
					b = readBit();
					if (debug) {
						//console.log("b=" + b);
					}
					if (b) {
						if (!(X.b1 & 0x8000)) {
							if (debug) {
								//console.log("ret1");
							}
							return X.b1;
						}
						X = X.jump;
						len = currentTree.length;
						for (i = 0; i < len; i++) {
							if (currentTree[i] === X) {
								xtreepos = i;
								break;
							}
						}
						//xtreepos++;
					} else {
						if (!(X.b0 & 0x8000)) {
							if (debug) {
								//console.log("ret2");
							}
							return X.b0;
						}
						//X++; //??????????????????
						xtreepos++;
						X = currentTree[xtreepos];
					}
				}
				if (debug) {
					//console.log("ret3");
				}
				return -1;
			}
			;

			function DeflateLoop() {
				var last, c, type, i, len;

				do {

					last = readBit();
					type = readBits(2);
					switch (type) {
					case 0:
						if (debug) {
							//console.log("Stored\n");
						}
						break;
					case 1:
						if (debug) {
							//console.log("Fixed Huffman codes\n");
						}
						break;
					case 2:
						if (debug) {
							//console.log("Dynamic Huffman codes\n");
						}
						break;
					case 3:
						if (debug) {
							//console.log("Reserved block type!!\n");
						}
						break;
					default:
						if (debug) {
							//console.log("Unexpected value %d!\n", type);
						}
						break;
					}

					if (type == 0) {
						var blockLen, cSum;

						// Stored 
						byteAlign();
						blockLen = readByte();
						blockLen |= (readByte() << 8);

						cSum = readByte();
						cSum |= (readByte() << 8);

						if (((blockLen ^ ~cSum) & 0xffff)) {
							//document.write("BlockLen checksum mismatch\n");
						}
						while (blockLen--) {
							c = readByte();
							addBuffer(c);
						}
					} else if (type == 1) {
						var j;

						while (1) {

							j = (bitReverse[readBits(7)] >> 1);
							if (j > 23) {
								j = (j << 1) | readBit();

								if (j > 199) {
									j -= 128;
									j = (j << 1) | readBit();
								} else {
									j -= 48;
									if (j > 143) {
										j = j + 136;

									}
								}
							} else {
								j += 256;
							}
							if (j < 256) {
								addBuffer(j);
								//document.write("out:"+String.fromCharCode(j));

							} else if (j == 256) {

								break;
							} else {
								var len, dist;

								j -= 256 + 1;
								len = readBits(cplext[j]) + cplens[j];

								j = bitReverse[readBits(5)] >> 3;
								if (cpdext[j] > 8) {
									dist = readBits(8);
									dist |= (readBits(cpdext[j] - 8) << 8);
								} else {
									dist = readBits(cpdext[j]);
								}
								dist += cpdist[j];

								for (j = 0; j < len; j++) {
									var c = buf32k[(bIdx - dist) & 0x7fff];
									addBuffer(c);
								}
							}
						} // while
					} else if (type == 2) {
						var j, n, literalCodes, distCodes, lenCodes;
						var ll = new Array(288 + 32); // "static" just to preserve stack

						// Dynamic Huffman tables 

						literalCodes = 257 + readBits(5);
						distCodes = 1 + readBits(5);
						lenCodes = 4 + readBits(4);
						//document.write("<br>param: "+literalCodes+" "+distCodes+" "+lenCodes+"<br>");
						for (j = 0; j < 19; j++) {
							ll[j] = 0;
						}

						// Get the decode tree code lengths

						//document.write("<br>");
						for (j = 0; j < lenCodes; j++) {
							ll[border[j]] = readBits(3);
							//document.write(ll[border[j]]+" ");
						}
						//fprintf(errfp, "\n");
						//document.write('<br>ll:'+ll);
						len = distanceTree.length;
						for (i = 0; i < len; i++)
							distanceTree[i] = new HufNode();
						if (CreateTree(distanceTree, 19, ll, 0)) {
							flushBuffer();
							return 1;
						}
						if (debug) {
							//console.log("<br>distanceTree");
							/*for ( var a = 0; a < distanceTree.length; a++) {
								//console.log("<br>" + distanceTree[a].b0 + " "
										+ distanceTree[a].b1 + " "
										+ distanceTree[a].jump + " "
										+ distanceTree[a].jumppos);

							}*/
						}
						//document.write('<BR>tree created');

						//read in literal and distance code lengths
						n = literalCodes + distCodes;
						i = 0;
						var z = -1;
						if (debug) {
							//console.log("<br>n=" + n + " bits: " + bits
							//		+ "<br>");
						}
						while (i < n) {
							z++;
							j = DecodeValue(distanceTree);
							if (debug) {
								//console.log("<br>" + z + " i:" + i
								//		+ " decode: " + j + "    bits " + bits
								//		+ "<br>");
							}
							if (j < 16) { // length of code in bits (0..15)
								ll[i++] = j;
							} else if (j == 16) { // repeat last length 3 to 6 times 
								var l;
								j = 3 + readBits(2);
								if (i + j > n) {
									flushBuffer();
									return 1;
								}
								l = i ? ll[i - 1] : 0;
								while (j--) {
									ll[i++] = l;
								}
							} else {
								if (j == 17) { // 3 to 10 zero length codes
									j = 3 + readBits(3);
								} else { // j == 18: 11 to 138 zero length codes 
									j = 11 + readBits(7);
								}
								if (i + j > n) {
									flushBuffer();
									return 1;
								}
								while (j--) {
									ll[i++] = 0;
								}
							}
						}

						// Can overwrite tree decode tree as it is not used anymore
						len = literalTree.length;
						for (i = 0; i < len; i++)
							literalTree[i] = new HufNode();
						if (CreateTree(literalTree, literalCodes, ll, 0)) {
							flushBuffer();
							return 1;
						}
						len = literalTree.length;
						for (i = 0; i < len; i++)
							distanceTree[i] = new HufNode();
						var ll2 = new Array();
						for (i = literalCodes; i < ll.length; i++) {
							ll2[i - literalCodes] = ll[i];
						}
						if (CreateTree(distanceTree, distCodes, ll2, 0)) {
							flushBuffer();
							return 1;
						}
						if (debug) {
							//console.log("<br>literalTree");
						}
						while (1) {
							j = DecodeValue(literalTree);
							if (j >= 256) { // In C64: if carry set
								var len, dist;
								j -= 256;
								if (j == 0) {
									// EOF
									break;
								}
								j--;
								len = readBits(cplext[j]) + cplens[j];

								j = DecodeValue(distanceTree);
								if (cpdext[j] > 8) {
									dist = readBits(8);
									dist |= (readBits(cpdext[j] - 8) << 8);
								} else {
									dist = readBits(cpdext[j]);
								}
								dist += cpdist[j];
								while (len--) {
									var c = buf32k[(bIdx - dist) & 0x7fff];
									addBuffer(c);
								}
							} else {
								addBuffer(j);
							}
						}
					}
				} while (!last);
				flushBuffer();

				byteAlign();
				return 0;
			}
			;

			ggm.JXG.Util.Unzip.prototype.unzip = function() {
				if (debug) {
					//console.log(bA);
				}
				nextFile();
				return unzipped;
			};

			function nextFile() {
				if (debug) {
					//console.log("NEXTFILE");
				}
				outputArr = [];
				var tmp = [];
				modeZIP = false;
				tmp[0] = readByte();
				tmp[1] = readByte();
				if (debug) {
					//console.log("type: " + tmp[0] + " " + tmp[1]);
				}
				if (tmp[0] == parseInt("78", 16)
						&& tmp[1] == parseInt("da", 16)) { //GZIP
					if (debug) {
						//console.log("GEONExT-GZIP");
					}
					DeflateLoop();
					if (debug) {
						//console.log(outputArr.join(''));
					}
					unzipped[files] = new Array(2);
					unzipped[files][0] = outputArr.join('');
					unzipped[files][1] = "geonext.gxt";
					files++;
				}
				if (tmp[0] == parseInt("1f", 16)
						&& tmp[1] == parseInt("8b", 16)) { //GZIP
					if (debug) {
						//console.log("GZIP");
					}
					//DeflateLoop();
					skipdir();
					if (debug) {
						//console.log(outputArr.join(''));
					}
					unzipped[files] = new Array(2);
					unzipped[files][0] = outputArr.join('');
					unzipped[files][1] = "file";
					files++;
				}
				if (tmp[0] == parseInt("50", 16)
						&& tmp[1] == parseInt("4b", 16)) { //ZIP
					modeZIP = true;
					tmp[2] = readByte();
					tmp[3] = readByte();
					if (tmp[2] == parseInt("3", 16)
							&& tmp[3] == parseInt("4", 16)) {
						//MODE_ZIP
						tmp[0] = readByte();
						tmp[1] = readByte();
						if (debug) {
							//console.log("ZIP-Version: " + tmp[1] + " " + tmp[0]
							//		/ 10 + "." + tmp[0] % 10);
						}

						gpflags = readByte();
						gpflags |= (readByte() << 8);
						if (debug) {
							//console.log("gpflags: " + gpflags);
						}

						var method = readByte();
						method |= (readByte() << 8);
						if (debug) {
							//console.log("method: " + method);
						}

						readByte();
						readByte();
						readByte();
						readByte();

						var crc = readByte();
						crc |= (readByte() << 8);
						crc |= (readByte() << 16);
						crc |= (readByte() << 24);

						var compSize = readByte();
						compSize |= (readByte() << 8);
						compSize |= (readByte() << 16);
						compSize |= (readByte() << 24);

						var size = readByte();
						size |= (readByte() << 8);
						size |= (readByte() << 16);
						size |= (readByte() << 24);

						if (debug) {
							//console.log("local CRC: " + crc + "\nlocal Size: "
							//		+ size + "\nlocal CompSize: " + compSize);
						}
						var filelen = readByte();
						filelen |= (readByte() << 8);

						var extralen = readByte();
						extralen |= (readByte() << 8);

						if (debug) {
							//console.log("filelen " + filelen);
						}
						i = 0;
						nameBuf = [];
						while (filelen--) {
							var c = readByte();
							if (c == "/" | c == ":") {
								i = 0;
							} else if (i < NAMEMAX - 1)
								nameBuf[i++] = String.fromCharCode(c);
						}
						if (debug) {
							//console.log("nameBuf: " + nameBuf);
						}
						//nameBuf[i] = "\0";
						if (!fileout)
							fileout = nameBuf;

						var i = 0;
						while (i < extralen) {
							c = readByte();
							i++;
						}

						CRC = 0xffffffff;
						SIZE = 0;

						if (size = 0 && fileOut.charAt(fileout.length - 1) == "/") {
							//skipdir
							if (debug) {
								//console.log("skipdir");
							}
						}
						if (method == 8) {
							DeflateLoop();
							if (debug) {
								//console.log(outputArr.join(''));
							}
							unzipped[files] = new Array(2);
							unzipped[files][0] = outputArr.join('');
							////console.log(outputArr.join(''));
							unzipped[files][1] = nameBuf.join('');
							files++;
							//return outputArr.join('');
						}
						skipdir();
					}
				}
			}
			;

			function skipdir() {
				var crc, tmp = [], compSize, size, os, i, c;

				if ((gpflags & 8)) {
					tmp[0] = readByte();
					tmp[1] = readByte();
					tmp[2] = readByte();
					tmp[3] = readByte();

					if (tmp[0] == parseInt("50", 16)
							&& tmp[1] == parseInt("4b", 16)
							&& tmp[2] == parseInt("07", 16)
							&& tmp[3] == parseInt("08", 16)) {
						crc = readByte();
						crc |= (readByte() << 8);
						crc |= (readByte() << 16);
						crc |= (readByte() << 24);
					} else {
						crc = tmp[0] | (tmp[1] << 8) | (tmp[2] << 16)
								| (tmp[3] << 24);
					}

					compSize = readByte();
					compSize |= (readByte() << 8);
					compSize |= (readByte() << 16);
					compSize |= (readByte() << 24);

					size = readByte();
					size |= (readByte() << 8);
					size |= (readByte() << 16);
					size |= (readByte() << 24);

					if (debug) {
						//console.log("CRC:");
					}
				}

				if (modeZIP)
					nextFile();

				tmp[0] = readByte();
				if (tmp[0] != 8) {
					if (debug) {
						//console.log("Unknown compression method!");
					}
					return 0;
				}

				gpflags = readByte();
				if (debug) {
					//if ((gpflags & ~(parseInt("1f", 16))))
						//console.log("Unknown flags set!");
				}

				readByte();
				readByte();
				readByte();
				readByte();

				readByte();
				os = readByte();

				if ((gpflags & 4)) {
					tmp[0] = readByte();
					tmp[2] = readByte();
					len = tmp[0] + 256 * tmp[1];
					if (debug) {
						//console.log("Extra field size: " + len);
					}
					for (i = 0; i < len; i++)
						readByte();
				}

				if ((gpflags & 8)) {
					i = 0;
					nameBuf = [];
					while (c = readByte()) {
						if (c == "7" || c == ":")
							i = 0;
						if (i < NAMEMAX - 1)
							nameBuf[i++] = c;
					}
					//nameBuf[i] = "\0";
					if (debug) {
						//console.log("original file name: " + nameBuf);
					}
				}

				if ((gpflags & 16)) {
					while (c = readByte()) {
						//FILE COMMENT
					}
				}

				if ((gpflags & 2)) {
					readByte();
					readByte();
				}

				DeflateLoop();

				crc = readByte();
				crc |= (readByte() << 8);
				crc |= (readByte() << 16);
				crc |= (readByte() << 24);

				size = readByte();
				size |= (readByte() << 8);
				size |= (readByte() << 16);
				size |= (readByte() << 24);

				if (modeZIP)
					nextFile();

			}
			;
		};
		
		ggm.JXG.Util.Base64 = {

			// private property
			_keyStr : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",

			// public method for encoding
			encode : function(input) {
				var output = [], chr1, chr2, chr3, enc1, enc2, enc3, enc4, i = 0;

				//ARinput =ggm.JXG.Util.Base64._utf8_encode(input);//FIXME: maybe this have to be moved outside

				while (i < input.length) {

					chr1 = input.charCodeAt(i++);
					chr2 = input.charCodeAt(i++);
					chr3 = input.charCodeAt(i++);

					enc1 = chr1 >> 2;
					enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
					enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
					enc4 = chr3 & 63;

					if (isNaN(chr2)) {
						enc3 = enc4 = 64;
					} else if (isNaN(chr3)) {
						enc4 = 64;
					}

					output.push([ this._keyStr.charAt(enc1),
							this._keyStr.charAt(enc2),
							this._keyStr.charAt(enc3),
							this._keyStr.charAt(enc4) ].join(''));
				}

				return output.join('');
			}
		};

		ggm.JXG.Util.utf8Decode = function(utftext) {
			var string = [];
			var i = 0;
			var c = 0, c1 = 0, c2 = 0;

			while (i < utftext.length) {
				c = utftext.charCodeAt(i);

				if (c < 128) {
					string.push(String.fromCharCode(c));
					i++;
				} else if ((c > 191) && (c < 224)) {
					c2 = utftext.charCodeAt(i + 1);
					string.push(String
							.fromCharCode(((c & 31) << 6) | (c2 & 63)));
					i += 2;
				} else {
					c2 = utftext.charCodeAt(i + 1);
					c3 = utftext.charCodeAt(i + 2);
					string.push(String.fromCharCode(((c & 15) << 12)
							| ((c2 & 63) << 6) | (c3 & 63)));
					i += 3;
				}
			}
			;
			return string.join('');
		}

		//====JSXGRAPH UTIL CLASS END======================================//