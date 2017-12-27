package com.yate.ta4j.indicadores;

import org.ta4j.core.TimeSeries;

public class DailyTimeIndicator extends TimeIndicator
{

	/**
	 * 
	 */
	private static final long serialVersionUID=-5263356431933536009L;

	public DailyTimeIndicator (TimeSeries series)
	{
		super (series,TimeIndicator.RefTime.DAILY);
	}
}
