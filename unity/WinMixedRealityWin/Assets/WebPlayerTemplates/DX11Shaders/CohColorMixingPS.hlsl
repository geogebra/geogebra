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

// Formulas for color mixing from https://www.w3.org/TR/compositing-1/#blending

float Lum(float3 color)
{
	return 0.3 * color.r + 0.59 * color.g + 0.11 * color.b;
}

float3 ClipColor(float3 color)
{
	float L = Lum(color);
	float fmin = min(min(color.r, color.g), color.b);
	float fmax = max(max(color.r, color.g), color.b);

	color = lerp(color,
		L + (((color - L) * L) / (L - fmin)),
		float(fmin < 0));

	color = lerp(color,
		L + (((color - L) * (1 - L)) / (fmax - L)),
		float(fmax > 1));

	return color;
}

float3 SetLum(float3 color, float lum)
{
	float d = lum - Lum(color);
	color += d;
	return ClipColor(color);
}

float Sat(float3 color)
{
	return max(max(color.r, color.g), color.b) -
		min(min(color.r, color.g), color.b);
}

float3 SetSatInner(float3 color, float sat)
{
	// Here the |color| values are in ascending order, i.e.
	// color.x <= color.y <= color.z

	if (color.z > color.x)
	{
		color.y = (((color.y - color.x) * sat) / (color.z - color.x));
		color.z = sat;
	}
	else
	{
		color.yz = float2(0.0, 0.0);
	}
	return float3(0.0, color.y, color.z);
}

float3 SetSat(float3 color, float sat)
{
	if (color.r <= color.g)
	{
		if (color.g <= color.b)
		{
			color.rgb = SetSatInner(color.rgb, sat);
		}
		else if (color.r <= color.b)
		{
			color.rbg = SetSatInner(color.rbg, sat);
		}
		else
		{
			color.brg = SetSatInner(color.brg, sat);
		}
	}
	else if (color.r <= color.b)
	{
		color.grb = SetSatInner(color.grb, sat);
	}
	else if (color.g <= color.b)
	{
		color.gbr = SetSatInner(color.gbr, sat);
	}
	else
	{
		color.bgr = SetSatInner(color.bgr, sat);
	}
	return color;
}

float3 ColorMixScreen(float3 backdrop, float3 source)
{
	return backdrop + source - backdrop * source;
}

float3 ColorMixMultiply(float3 backdrop, float3 source)
{
	return backdrop * source;
}

float3 ColorMixHardLight(float3 backdrop, float3 source)
{
	float3 coef = step(0.5, source);
	return lerp(ColorMixScreen(backdrop, 2 * source - 1),
		ColorMixMultiply(backdrop, 2 * source), coef);
}

float3 ColorMixSoftLight(float3 backdrop, float3 source)
{
	// TODO: Check if branching perf is better, diffuseB calc is heavy
	float3 diffuseBCoef = step(0.25, backdrop);
	float3 diffuseB = lerp(sqrt(backdrop),
		((16 * backdrop - 12) * backdrop + 4) * backdrop,
		diffuseBCoef);

	float3 coef = step(0.5, source);
	return lerp(backdrop + (2 * source - 1) * (diffuseB - backdrop),
		backdrop - (1 - 2 * source) * backdrop * (1 - backdrop),
		coef);
}

float3 BlendFunction(float3 backdrop, float3 source, int mode)
{
	// Switch not supported in SM3
	if (mode == 0)
	{
		// Normal mode
		return source;
	}
	else if (mode == 1)
	{
		// Multiply
		return ColorMixMultiply(backdrop, source);
	}
	else if (mode == 2)
	{
		// Screen
		return ColorMixScreen(backdrop, source);
	}
	else if (mode == 3)
	{
		// Overlay
		return ColorMixHardLight(source, backdrop); // Inverted hard-light
	}
	else if (mode == 4)
	{
		// Darken
		return min(source, backdrop);
	}
	else if (mode == 5)
	{
		// Lighten
		return max(source, backdrop);
	}
	else if (mode == 6)
	{
		// Color dodge
		return min(1, backdrop / max(1 - source, 0.0001));
	}
	else if (mode == 7)
	{
		// Color burn
		return 1 - min(1, (1 - backdrop) / max(source, 0.0001));
	}
	else if (mode == 8)
	{
		// Hard light
		return ColorMixHardLight(backdrop, source);
	}
	else if (mode == 9)
	{
		// Soft light
		return ColorMixSoftLight(backdrop, source);
	}
	else if (mode == 10)
	{
		// Difference
		return abs(backdrop - source);
	}
	else if (mode == 11)
	{
		// Exclusion
		return backdrop + source - 2 * backdrop * source;
	}
	else if (mode == 12)
	{
		// Hue
		return SetLum(SetSat(source, Sat(backdrop)), Lum(backdrop));
	}
	else if (mode == 13)
	{
		// Saturation
		return SetLum(SetSat(backdrop, Sat(source)), Lum(backdrop));
	}
	else if (mode == 14)
	{
		// Color
		return SetLum(source, Lum(backdrop));
	}
	else if (mode == 15)
	{
		// Luminosity
		return SetLum(backdrop, Lum(source));
	}
	else
	{
		return 0;
	}
}

float4 ColorMixingPS(PS_INPUT input) : SV_Target
{
	float4 backdrop = SAMPLE2D(txBuffer, input.Additional.zw);
	float4 source = SAMPLE2D(txBuffer1, input.Additional.xy);

	float3 backdropUnprem = backdrop.rgb / max(backdrop.a, 0.0001);
	float3 sourceUnprem = source.rgb / max(source.a, 0.0001);
	float4 result =
		(1 - backdrop.a) * source +
		source.a * backdrop.a * float4(
			saturate(BlendFunction(backdropUnprem, sourceUnprem, int(PrimProps0.x))), 1) +
		(1 - source.a) * backdrop;

	return result;
}

