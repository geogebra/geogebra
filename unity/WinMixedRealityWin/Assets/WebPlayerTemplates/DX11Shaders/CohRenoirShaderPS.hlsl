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

#define PS_INPUT_ADDITIONAL_INTERP_MODIFIER centroid

#include "CohPlatform.ihlsl"
#include "CohStandardCommon.ihlsl"
#include "CohCommonPS.ihlsl"

#define txMask txBuffer1
#define txGradient txBuffer2

// Keep in sync with PSTFlags enum in CommandProcessor.cpp
static const int PSTF_ColorFromTexture = 0x1;
static const int PSTF_GradientLinear = 0x2;
static const int PSTF_GradientRadial = 0x4;
static const int PSTF_Gradient2Point = 0x8;
static const int PSTF_Gradient3PointSymmetrical = 0x10;
static const int PSTF_GradientFromTexture = 0x20;
static const int PSTF_HasMask = 0x40;

#if defined(__DX9__)
	#define IS_SET(value, flag) (value % (flag * 2) >= flag)
#else
	#define IS_SET(value, flag) value & flag
#endif


float4 RenoirShaderPS(PS_INPUT input) : SV_Target
{
	float tVal = 0.f;
	if (IS_SET(ShaderType, PSTF_GradientLinear))
	{
		tVal = input.VaryingParam0.x;
	}
	else if (IS_SET(ShaderType, PSTF_GradientRadial))
	{
		tVal = length(input.VaryingParam0.xy);
	}

	float4 colorTemp;
	if (IS_SET(ShaderType, PSTF_Gradient2Point))
	{
		colorTemp = lerp(GradientStartColor, GradientEndColor, saturate(tVal));
	}
	else if (IS_SET(ShaderType, PSTF_Gradient3PointSymmetrical))
	{
		float oneMinus2t = 1.0 - (2.0 * tVal);
		colorTemp = clamp(oneMinus2t, 0.0, 1.0) * GradientStartColor;
		colorTemp += (1.0 - min(abs(oneMinus2t), 1.0)) * GradientMidColor;
		colorTemp += clamp(-oneMinus2t, 0.0, 1.0) * GradientEndColor;
	}
	else if (IS_SET(ShaderType, PSTF_GradientFromTexture))
	{
		float2 coord = float2(tVal, GradientYCoord);
		colorTemp = SAMPLE2D(txGradient, coord);
	}
	else if (IS_SET(ShaderType, PSTF_ColorFromTexture))
	{
		colorTemp = SAMPLE2D(txBuffer, input.Additional.xy);
	}

	// Warning X4000 for usage of potentially uninitialized variable can be
	// safely ignored, as there are no ShaderTypes that don't enter any of the
	// branches above.
	if (IS_SET(ShaderType, PSTF_HasMask))
	{
		float mask = SAMPLE2D(txMask, input.VaryingParam1.xy).r;
		colorTemp *= mask;
	}

	return colorTemp;
}

#undef IS_SET
