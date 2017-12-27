package com.yate.ta4j.indicadores;

import org.ta4j.core.TimeSeries;

public class MonthlyTimeIndicator extends TimeIndicator
{

	/**
	 * 
	 */
	private static final long serialVersionUID=63040802506480585L;

	public MonthlyTimeIndicator (TimeSeries series)
	{
		super (series,TimeIndicator.RefTime.MONTHLY);
	}
}
