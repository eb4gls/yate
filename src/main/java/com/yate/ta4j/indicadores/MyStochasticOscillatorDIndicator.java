package com.yate.ta4j.indicadores;


import org.ta4j.core.Decimal;
import org.ta4j.core.Indicator;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.StochasticOscillatorKIndicator;

/**
 * Stochastic oscillator D.
 * <p>
 * Receive {@link StochasticOscillatorKIndicator} and returns its {@link SMAIndicator SMAIndicator(3)}.
 */
public class MyStochasticOscillatorDIndicator extends CachedIndicator<Decimal>
{
	/**
	 * 
	 */
	private static final long serialVersionUID=105080151450509249L;
	private Indicator<Decimal> indicator;

	public MyStochasticOscillatorDIndicator (TimeSeries timeSeries,int timeFrame)
	{
		this (new StochasticOscillatorKIndicator (timeSeries,timeFrame));
	}

	public MyStochasticOscillatorDIndicator (StochasticOscillatorKIndicator k)
	{
		this (new SMAIndicator (k,3));
	}

	public MyStochasticOscillatorDIndicator (Indicator<Decimal> indicator)
	{
		super (indicator);
		this.indicator=indicator;
	}

	@Override
	protected Decimal calculate (int index)
	{
		return indicator.getValue (index);
	}

	@Override
	public String toString ()
	{
		return getClass ().getSimpleName ()+" "+indicator;
	}
}

