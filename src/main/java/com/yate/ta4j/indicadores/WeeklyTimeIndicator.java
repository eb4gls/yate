package com.yate.ta4j.indicadores;

import org.ta4j.core.TimeSeries;

public class WeeklyTimeIndicator extends TimeIndicator
{
	/**
	 * 
	 */
	private static final long serialVersionUID=-5953068249544669029L;

	public WeeklyTimeIndicator (TimeSeries series)
	{
		super (series,TimeIndicator.RefTime.WEEKLY);
	}
}
