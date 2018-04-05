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

#include "CohPlatform.ihlsl"
#include "CohStandardCommon.ihlsl"
#include "CohCommonPS.ihlsl"

// Keep in sync w/ SDFGenerator
#define DISTANCE_FIELD_MULTIPLIER 7.96875f
#define DISTANCE_FIELD_MULTIPLIER_DIV2 3.984375f
#define DISTANCE_FIELD_THRESHOLD 0.50196078431f

#define SHOW_DF 0

float GetLuminance(float3 color)
{
	return 0.2126f * color.r + 0.7152f * color.g + 0.0722f * color.b;
}

float4 TextPS(PS_INPUT input) : SV_Target
{
	float dfValue = SAMPLE2D(txBuffer, input.Additional.xy) COH_A8_SAMPLE_MASK;
#if SHOW_DF
	return float4(dfValue.xxx, 1);
#endif
	if (ShaderType == 1)
	{
		// Values should be in [-4, 4]
		dfValue = (dfValue * DISTANCE_FIELD_MULTIPLIER) - DISTANCE_FIELD_MULTIPLIER_DIV2;

		dfValue = smoothstep(-DISTANCE_FIELD_THRESHOLD / PrimProps0.x, DISTANCE_FIELD_THRESHOLD / PrimProps0.x, dfValue);
	}
	else if (ShaderType == 2)
	{
		const float scale = sqrt(PrimProps0.y * 0.5);

		const float bias = 0.5 * scale - 0.9;
		const float outlineWidth = PrimProps0.z / PrimProps0.y * 0.5 * scale;
		dfValue *= scale;

		float4 c = lerp(PrimProps1, input.Color, saturate(dfValue - (bias + outlineWidth)));

		c *= saturate(dfValue - max(0, bias - outlineWidth));
		return c;
	}

	const float lum = GetLuminance(input.Color.xyz);
	// dfValue is in the [0,1] range here so warning X3571 can be safely ignored
	return input.Color * pow(dfValue, 1.45f - lum);
}
