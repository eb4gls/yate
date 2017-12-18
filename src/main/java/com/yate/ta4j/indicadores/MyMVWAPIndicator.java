package com.yate.ta4j.indicadores;

import org.ta4j.core.Decimal;
import org.ta4j.core.Indicator;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.volume.VWAPIndicator;


/**
 * The Moving volume weighted average price (MVWAP) Indicator.
 * @see http://www.investopedia.com/articles/trading/11/trading-with-vwap-mvwap.asp
 */
public class MyMVWAPIndicator extends CachedIndicator<Decimal>
{
    /**
	 * 
	 */
	private static final long serialVersionUID=4851007190273800289L;
	private final Indicator<Decimal> sma;
    
	public MyMVWAPIndicator (TimeSeries series, int timeFrame)
	{
		this (new VWAPIndicator (series,timeFrame),timeFrame);
	}
	
	
	/**
	 * Constructor.
	 * 
	 * @param vwap
	 *            the vwap
	 * @param timeFrame
	 *            the time frame
	 */
	public MyMVWAPIndicator (VWAPIndicator vwap,int timeFrame)
	{
		super (vwap);
		sma=new SMAIndicator (vwap,timeFrame);
	}

	@Override
	protected Decimal calculate (int index)
	{
		return sma.getValue (index);
	}

}