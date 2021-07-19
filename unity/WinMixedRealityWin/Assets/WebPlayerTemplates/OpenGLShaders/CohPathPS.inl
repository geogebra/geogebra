/*
This file is part of Renoir, a modern graphics library.

Copyright (c) 2012-2016 Coherent Labs AD and/or its licensors. All
rights reserved in all media.

The coded instructions, statements, computer programs, and/or related
material (collectively the "Data") in these files contain confidential
and unpublished information proprietary Coherent Labs and/or its
licensors, which is protected by United States of America federal
copyright law and by international treaties.

This software or source code is supplied under the terms of a license
agreement and nondisclosure agreement with Coherent Labs AD and may
not be copied, disclosed, or exploited except in accordance with the
terms of that agreement. The Data may not be disclosed or distributed to
third parties, in whole or in part, without the prior written consent of
Coherent Labs AD.

COHERENT LABS MAKES NO REPRESENTATION ABOUT THE SUITABILITY OF THIS
SOURCE CODE FOR ANY PURPOSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY, NONINFRINGEMENT, AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER, ITS AFFILIATES,
PARENT COMPANIES, LICENSORS, SUPPLIERS, OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OR PERFORMANCE OF THIS SOFTWARE OR SOURCE CODE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
const char* CohPathPS =
"void main()																			\n"
"{																						\n"
"	if (ShaderType == 14)																\n"
"	{																					\n"
"		// Hairline quads																\n"
"		vec2 px = dFdx(PSExtraParams);													\n"
"		vec2 py = dFdy(PSExtraParams);													\n"
"																						\n"
"		float fx = (2.0 * PSExtraParams.x) * px.x - px.y;								\n"
"		float fy = (2.0 * PSExtraParams.x) * py.x - py.y;								\n"
"																						\n"
"		float edgeAlpha = (PSExtraParams.x * PSExtraParams.x - PSExtraParams.y);		\n"
"		float sd = sqrt((edgeAlpha * edgeAlpha) / (fx * fx + fy * fy));					\n"
"																						\n"
"		float alpha = 1.0 - sd;															\n"
"																						\n"
"		outColor = PrimProps0 * PrimProps1.x * alpha;									\n"
"	}																					\n"
"	else if (ShaderType == 11)															\n"
"	{																					\n"
"		// Hairline lines																\n"
"		outColor = PrimProps0 * min(1.0, (1.0 - abs(PSExtraParams.y)) * PrimProps1.x);	\n"
"	}																					\n"
"	else																				\n"
"	{																					\n"
"		// non-hairline paths															\n"
"		outColor = PSExtraParams.y * PrimProps0;										\n"
"	}																					\n"
"}																						\n";
