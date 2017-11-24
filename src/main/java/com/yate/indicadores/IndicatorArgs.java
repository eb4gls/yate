package com.yate.indicadores;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.indicators.CachedIndicator;

public class IndicatorArgs
{
	private String name=null;
	
	private CachedIndicator<Decimal> indicator=null;
	
	public IndicatorArgs (String name,CachedIndicator<Decimal> indicator)

	{	
		this.indicator=indicator;
		this.name=name;
	}

	public String getName ()
	{
		return name;
	}
	
	public CachedIndicator<Decimal> getIndicator ()
	{
		return indicator;
	}
}
