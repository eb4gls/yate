package com.yate.ta4j.indicadores;

import org.ta4j.core.TimeSeries;

public class YearlyTimeIndicator extends TimeIndicator
{
	/**
	 * 
	 */
	private static final long serialVersionUID=5359595781320932659L;

	public YearlyTimeIndicator (TimeSeries series)
	{
		super (series,TimeIndicator.RefTime.YEARLY);
	}
}
