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
const char* CohShadeGeometry =
"void ShadeGeometry(inout vec4 outColor, inout float alpha)                                                                  \n"
"{                                                                                                                           \n"
"	// Rect/stroke rect                                                                                                      \n"
"	if (ShaderType == 0)                                                                                                     \n"
"	{                                                                                                                        \n"
"		alpha = min(1.0, PSAdditional.z * PSAdditional.w);                                                                   \n"
"	}                                                                                                                        \n"
"	// Circle / Rounded rect                                                                                                 \n"
"	else if (ShaderType == 1)                                                                                                \n"
"	{                                                                                                                        \n"
"		vec2 posPixels = PSScreenNormalPosition.xy;                                                                          \n"
"		float distance2edge = length(posPixels - PSAdditional.xy) - PSAdditional.z;                                          \n"
"		alpha = clamp(0.5 - distance2edge, 0.0, 1.0);                                                                        \n"
"	}                                                                                                                        \n"
"	// Stroke Circle / Rounded rect                                                                                          \n"
"	else if (ShaderType == 2)                                                                                                \n"
"	{                                                                                                                        \n"
"		vec2 posPixels = PSScreenNormalPosition.xy;                                                                          \n"
"		float de = length(posPixels - PSAdditional.xy);                                                                      \n"
"		float distance2OuterEdge = de - (PSAdditional.z + PSAdditional.w / 2.0);                                             \n"
"		float distance2InnerEdge = de - (PSAdditional.z - PSAdditional.w / 2.0);                                             \n"
"		alpha = clamp(0.5 - distance2OuterEdge, 0.0, 1.0);                                                                   \n"
"		alpha *= 1.0 - clamp(0.5 - distance2InnerEdge, 0.0, 1.0);                                                            \n"
"	}                                                                                                                        \n"
"	// Image                                                                                                                 \n"
"	else if (ShaderType == 3)                                                                                                \n"
"	{                                                                                                                        \n"
"		outColor = texture(txBuffer, PSAdditional.xy);                                                                       \n"
"		alpha = PSColor.a * clamp(PSAdditional.z, 0.0, 1.0);                                                                 \n"
"	}                                                                                                                        \n"
"                                                                                                                            \n"
"	// Ellipse                                                                                                               \n"
"	else if (ShaderType == 4)                                                                                                \n"
"	{                                                                                                                        \n"
"		vec2 offset = (PSScreenNormalPosition.xy - PSAdditional.xy) / (PSAdditional.zw);                                     \n"
"		float test = dot(offset, offset) - 1.0;                                                                              \n"
"		vec2 dudx = dFdx(offset);                                                                                            \n"
"		vec2 dudy = dFdy(offset);                                                                                            \n"
"		vec2 gradient = vec2(2.0 * offset.x * dudx.x + 2.0 * offset.y * dudx.y,                                              \n"
"			2.0 * offset.x * dudy.x + 2.0 * offset.y * dudy.y);                                                              \n"
"		float grad_dot = max(dot(gradient, gradient), 1.0e-4);                                                               \n"
"		float invlen = inversesqrt(grad_dot);                                                                                \n"
"                                                                                                                            \n"
"		alpha = clamp(0.5 - test * invlen, 0.0, 1.0);                                                                        \n"
"	}                                                                                                                        \n"
"                                                                                                                            \n"
"	// Stroke Ellipse                                                                                                        \n"
"	else if (ShaderType == 5)                                                                                                \n"
"	{                                                                                                                        \n"
"		vec2 offset = (PSScreenNormalPosition.xy - PSAdditional.xy) / (PSAdditional.zw + (PrimProps0.x / 2.0));              \n"
"		float test = dot(offset, offset) - 1.0;                                                                              \n"
"		vec2 dudx = dFdx(offset);                                                                                            \n"
"		vec2 dudy = dFdy(offset);                                                                                            \n"
"		vec2 gradient = vec2(2.0 * offset.x * dudx.x + 2.0 * offset.y * dudx.y,                                              \n"
"			2.0 * offset.x * dudy.x + 2.0 * offset.y * dudy.y);                                                              \n"
"		float grad_dot = max(dot(gradient, gradient), 1.0e-4);                                                               \n"
"		float invlen = inversesqrt(grad_dot);                                                                                \n"
"                                                                                                                            \n"
"		alpha = clamp(0.5 - test * invlen, 0.0, 1.0);                                                                        \n"
"                                                                                                                            \n"
"		offset = (PSScreenNormalPosition.xy - PSAdditional.xy) / ((PSAdditional.zw - (PrimProps0.x / 2.0)));                 \n"
"		test = dot(offset, offset) - 1.0;                                                                                    \n"
"		dudx = dFdx(offset);                                                                                                 \n"
"		dudy = dFdy(offset);                                                                                                 \n"
"		gradient = vec2(2.0 * offset.x * dudx.x + 2.0 * offset.y * dudx.y,                                                   \n"
"			2.0 * offset.x * dudy.x + 2.0 * offset.y * dudy.y);                                                              \n"
"		grad_dot = max(dot(gradient, gradient), 1.0e-4);                                                                     \n"
"		invlen = inversesqrt(grad_dot);                                                                                      \n"
"                                                                                                                            \n"
"		alpha *= clamp(0.5 + test * invlen, 0.0, 1.0);                                                                       \n"
"	}                                                                                                                        \n"
"                                                                                                                            \n"
"	// Blur                                                                                                                  \n"
"	else if (ShaderType == 6)                                                                                                \n"
"	{                                                                                                                        \n"
"		outColor = vec4(0.0, 0.0, 0.0, 0.0);                                                                                 \n"
"		for (int i = 0; i < int(PrimProps0.x); ++i) {                                                                        \n"
"			float coeff = Coefficients[i >> 2][i & 3];                                                                       \n"
"			vec2 offset;                                                                                                     \n"
"			offset.x = PixelOffsets[(i * 2) >> 2][(i * 2) & 3];                                                              \n"
"			offset.y = PixelOffsets[(i * 2 + 1) >> 2][(i * 2 + 1) & 3];                                                      \n"
"			outColor += coeff * (texture(txBuffer, PSAdditional.xy + offset) + texture(txBuffer, PSAdditional.xy - offset)); \n"
"		}                                                                                                                    \n"
"		alpha = PSColor.a;                                                                                                   \n"
"	}                                                                                                                        \n"
"                                                                                                                            \n"
"	// Image with color matrix                                                                                               \n"
"	else if (ShaderType == 7)                                                                                                \n"
"	{                                                                                                                        \n"
"		vec4 baseColor = texture(txBuffer, PSAdditional.xy);                                                                 \n"
"		float nonZeroAlpha = max(baseColor.a, 0.00001);                                                                      \n"
"		baseColor = vec4(baseColor.rgb / nonZeroAlpha, nonZeroAlpha);                                                        \n"
"		// TODO: Rename the members of the constant buffer so they are not weird for non-blurs                               \n"
"		outColor.r = dot(baseColor, Coefficients[0]);                                                                        \n"
"		outColor.g = dot(baseColor, Coefficients[1]);                                                                        \n"
"		outColor.b = dot(baseColor, Coefficients[2]);                                                                        \n"
"		outColor.a = dot(baseColor, PixelOffsets[0]);                                                                        \n"
"		outColor += PixelOffsets[1];                                                                                         \n"
"                                                                                                                            \n"
"		alpha = outColor.a * PSColor.a;                                                                                      \n"
"		outColor.a = 1.0;                                                                                                    \n"
"	}                                                                                                                        \n"
"	// YUV2RGB (9) YUVA2RGB (12)                                                                                             \n"
"	else if (ShaderType == 9 || ShaderType == 12)                                                                            \n"
"	{                                                                                                                        \n"
"		vec3 YCbCr;                                                                                                          \n"
"		YCbCr.x = texture(txBuffer, PSAdditional.xy).r;                                                                      \n"
"		YCbCr.y = texture(txBuffer1, PSAdditional.xy).r;                                                                     \n"
"		YCbCr.z = texture(txBuffer2, PSAdditional.xy).r;                                                                     \n"
"                                                                                                                            \n"
"		YCbCr -= vec3(0.0625, 0.5, 0.5);                                                                                     \n"
"		mat3 yuv2rgb = mat3(vec3(1.164, 1.164, 1.164), vec3(0, -0.391, 2.018), vec3(1.596, -0.813, 0));                      \n"
"		vec3 rgb = yuv2rgb * YCbCr;                                                                                          \n"
"                                                                                                                            \n"
"		alpha = PSColor.a;                                                                                                   \n"
"		outColor = vec4(rgb, 1.0);                                                                                           \n"
"                                                                                                                            \n"
"		if (ShaderType == 12)                                                                                                \n"
"		{                                                                                                                    \n"
"			float a = texture(txBuffer3, PSAdditional.xy).r;                                                                 \n"
"			alpha *= a;                                                                                                      \n"
"		}                                                                                                                    \n"
"	}                                                                                                                        \n"
"	// Hairline                                                                                                              \n"
"	else if(ShaderType == 11)                                                                                                \n"
"	{                                                                                                                        \n"
"		vec3 posPixels = vec3(PSScreenNormalPosition.xy, 1.0);                                                               \n"
"		float distance2line = abs(dot(PSAdditional.xyz, posPixels));                                                         \n"
"		alpha = 1.0 - clamp(distance2line, 0.0, 1.0);                                                                        \n"
"	}                                                                                                                        \n"
"}                                                                                                                           \n";
