/*
 Copyright (c) 2013 Gildas Lormeau. All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice,
 this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in
 the documentation and/or other materials provided with the distribution.

 3. The names of the authors may not be used to endorse or promote products
 derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JCRAFT,
 INC. OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
!function(){"use strict";function t(t){function e(e,n){var r;i.data?e():((r=new XMLHttpRequest).addEventListener("load",function(){i.size||(i.size=Number(r.response.byteLength)),i.data=new Uint8Array(r.response),e()},!1),r.addEventListener("error",n,!1),r.open("GET",t),r.responseType="arraybuffer",r.send())}function n(e,n){var r=new XMLHttpRequest;r.addEventListener("load",function(){i.size=Number(r.response.byteLength),e()},!1),r.addEventListener("error",n,!1),r.open("GET",t),r.responseType="arraybuffer",r.send()}function r(t,n,r,o){e(function(){r(new Uint8Array(i.data.subarray(t,t+n)))},o)}var i=this;i.size=0,i.init=n,i.readUint8Array=r}function e(t){function e(e,n){var r=new XMLHttpRequest;r.addEventListener("load",function(){i.size=Number(r.getResponseHeader("Content-Length")),"bytes"==r.getResponseHeader("Accept-Ranges")?e():n(s)},!1),r.addEventListener("error",n,!1),r.open("HEAD",t),r.send()}function n(e,n,r,i){var o=new XMLHttpRequest;o.open("GET",t),o.responseType="arraybuffer",o.setRequestHeader("Range","bytes="+e+"-"+(e+n-1)),o.addEventListener("load",function(){r(o.response)},!1),o.addEventListener("error",i,!1),o.send()}function r(t,e,r,i){n(t,e,function(t){r(new Uint8Array(t))},i)}var i=this;i.size=0,i.init=e,i.readUint8Array=r}function n(t){function e(e,n){r.size=t.byteLength,e()}function n(e,n,r,i){r(new Uint8Array(t.slice(e,e+n)))}var r=this;r.size=0,r.init=e,r.readUint8Array=n}function r(){function t(t,e){r=new Uint8Array,t()}function e(t,e,n){var i=new Uint8Array(r.length+t.length);i.set(r),i.set(t,r.length),r=i,e()}function n(t){t(r.buffer)}var r,i=this;i.init=t,i.writeUint8Array=e,i.getData=n}function i(t,e){function n(e,n){t.createWriter(function(t){o=t,e()},n)}function r(t,n,r){var i=new Blob([a?t:t.buffer],{type:e});o.onwrite=function(){o.onwrite=null,n()},o.onerror=r,o.write(i)}function i(e){t.file(e)}var o,s=this;s.init=n,s.writeUint8Array=r,s.getData=i}var o,a,s="HTTP Range not supported.",p=zip.Reader,u=zip.Writer;try{a=0===new Blob([new DataView(new ArrayBuffer(0))]).size}catch(t){}t.prototype=new p,t.prototype.constructor=t,e.prototype=new p,e.prototype.constructor=e,n.prototype=new p,n.prototype.constructor=n,r.prototype=new u,r.prototype.constructor=r,i.prototype=new u,i.prototype.constructor=i,zip.FileWriter=i,zip.HttpReader=t,zip.HttpRangeReader=e,zip.ArrayBufferReader=n,zip.ArrayBufferWriter=r,zip.fs&&((o=zip.fs.ZipDirectoryEntry).prototype.addHttpContent=function(n,r,i){return function(t,e,n,r){if(t.directory)return r?new o(t.fs,e,n,t):new zip.fs.ZipFileEntry(t.fs,e,n,t);throw"Parent entry is not a directory."}(this,n,{data:r,Reader:i?e:t})},o.prototype.importHttpContent=function(n,r,i,o){this.importZip(r?new e(n):new t(n),i,o)},zip.fs.FS.prototype.importHttpContent=function(t,e,n,r){this.entries=[],this.root=new o(this),this.root.importHttpContent(t,e,n,r)})}();

