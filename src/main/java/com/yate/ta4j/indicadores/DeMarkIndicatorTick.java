package com.yate.ta4j.indicadores;

import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.pivotpoints.DeMarkPivotPointIndicator;
import org.ta4j.core.indicators.pivotpoints.TimeLevel;

public class DeMarkIndicatorTick extends DeMarkPivotPointIndicator
{

	/**
	 * 
	 */
	private static final long serialVersionUID=6172364230121790549L;

	public DeMarkIndicatorTick (TimeSeries series)
	{
		super (series,TimeLevel.TICKBASED);

	}
}
