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
const char* CohRenoirShaderPS =
"// Keep in sync with PSTFlags enum in CommandProcessor.cpp                           \n"
"const int PSTF_ColorFromTexture = 0x1;                                               \n"
"const int PSTF_GradientLinear = 0x2;                                                 \n"
"const int PSTF_GradientRadial = 0x4;                                                 \n"
"const int PSTF_Gradient2Point = 0x8;                                                 \n"
"const int PSTF_Gradient3PointSymmetrical = 0x10;                                     \n"
"const int PSTF_GradientFromTexture = 0x20;                                           \n"
"const int PSTF_HasMask = 0x40;                                                       \n"
"                                                                                     \n"
"void main()                                                                          \n"
"{                                                                                    \n"
"	float tVal = 0.f;                                                                 \n"
"	if (bool(ShaderType & PSTF_GradientLinear))                                       \n"
"	{                                                                                 \n"
"		tVal = PSVaryingParam0.x;                                                     \n"
"	}                                                                                 \n"
"	else if (bool(ShaderType & PSTF_GradientRadial))                                  \n"
"	{                                                                                 \n"
"		tVal = length(PSVaryingParam0.xy);                                            \n"
"	}                                                                                 \n"
"                                                                                     \n"
"	vec4 colorTemp;                                                                   \n"
"	if (bool(ShaderType & PSTF_Gradient2Point))                                       \n"
"	{                                                                                 \n"
"		colorTemp = mix(GradientStartColor, GradientEndColor, clamp(tVal, 0.0, 1.0)); \n"
"	}                                                                                 \n"
"	else if (bool(ShaderType & PSTF_Gradient3PointSymmetrical))                       \n"
"	{                                                                                 \n"
"		float oneMinus2t = 1.0 - (2.0 * tVal);                                        \n"
"		colorTemp = clamp(oneMinus2t, 0.0, 1.0) * GradientStartColor;                 \n"
"		colorTemp += (1.0 - min(abs(oneMinus2t), 1.0)) * GradientMidColor;            \n"
"		colorTemp += clamp(-oneMinus2t, 0.0, 1.0) * GradientEndColor;                 \n"
"	}                                                                                 \n"
"	else if (bool(ShaderType & PSTF_GradientFromTexture))                             \n"
"	{                                                                                 \n"
"		vec2 coord = vec2(tVal, GradientYCoord);                                      \n"
"		colorTemp = texture(txBuffer2, coord);                                        \n"
"	}                                                                                 \n"
"	else if (bool(ShaderType & PSTF_ColorFromTexture))                                \n"
"	{                                                                                 \n"
"		colorTemp = texture(txBuffer, PSAdditional.xy);                               \n"
"	}                                                                                 \n"
"                                                                                     \n"
"	if (bool(ShaderType & PSTF_HasMask))                                              \n"
"	{                                                                                 \n"
"		float mask = texture(txBuffer1, PSVaryingParam1.xy).r;                        \n"
"		colorTemp *= mask;                                                            \n"
"	}                                                                                 \n"
"                                                                                     \n"
"	outColor = colorTemp;                                                             \n"
"}                                                                                    \n";
