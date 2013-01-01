/*
	Copyright (c) 2004 Pablo Bleyer Kocik.

	Redistribution and use in source and binary forms, with or without
	modification, are permitted provided that the following conditions are met:

	1. Redistributions of source code must retain the above copyright notice, this
	list of conditions and the following disclaimer.

	2. Redistributions in binary form must reproduce the above copyright notice,
	this list of conditions and the following disclaimer in the documentation
	and/or other materials provided with the distribution.

	3. The name of the author may not be used to endorse or promote products
	derived from this software without specific prior written permission.

	THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR IMPLIED
	WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
	MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
	EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
	SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
	PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
	BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
	IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
	ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
	POSSIBILITY OF SUCH DAMAGE.
*/

package jd2xx;

import java.io.IOException;
import java.io.OutputStream;

public class JD2XXOutputStream extends OutputStream {

	public JD2XX jd2xx = null;

	public JD2XXOutputStream() {
	}

	public JD2XXOutputStream(JD2XX j) {
		jd2xx = j;
	}

	public JD2XXOutputStream(int dn) throws IOException {
		jd2xx = new JD2XX(dn);
	}

	public JD2XXOutputStream(String dn, int f) throws IOException {
		jd2xx = new JD2XX(dn, f);
	}

	public JD2XXOutputStream(int n, int f) throws IOException {
		jd2xx = new JD2XX(n, f);
	}

	public void close() throws IOException {
		// jd2xx.close();
		jd2xx = null;
	}

	public void write(int b) throws IOException {
		byte[] c = new byte[1];
		c[0] = (byte)b;
		while (jd2xx.write(c) != 0) ;
	}

	public void write(byte[] b) throws IOException {
		jd2xx.write(b);
	}
}
