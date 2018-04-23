using System;
using System.Linq;
using Coherent.UIGT;

[CoherentType]
public class MyMathGT
{
	[CoherentProperty]
	public double Sum(double[] numbers)
	{
		return numbers.Sum();
	}

	[CoherentProperty]
	public double Average(double[] numbers)
	{
		return numbers.Average();
	}
}
