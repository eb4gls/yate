package com.yate.ta4j.indicadores;

import org.ta4j.core.Decimal;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;


public class MyBollingerBandsUpperIndicator extends CachedIndicator<Decimal>
{    
	/**
	 * 
	 */
	private static final long serialVersionUID=-6946688196758148207L;

	private final SMAIndicator sma;
    
    private final StandardDeviationIndicator sd;
    
    private final Decimal k;

    
    public MyBollingerBandsUpperIndicator (Indicator<Decimal> indicator,int timeFrame)
    {
    	this (indicator,timeFrame,Decimal.TWO);
    }
    
    /**
     * Constructor.
     * @param indicator an indicator (usually close price)
     * @param timeFrame the time frame
     * @param k the K multiplier (usually 2.0)
     */
    public MyBollingerBandsUpperIndicator (Indicator<Decimal> indicator,int timeFrame,Decimal k)
    {
        super (indicator);
        
        this.sma=new SMAIndicator (indicator,timeFrame);
        this.sd=new StandardDeviationIndicator (indicator,timeFrame);
        
        this.k=k;
    }

    @Override
    protected Decimal calculate (int index)
    {
        return sma.getValue (index).plus (sd.getValue (index).multipliedBy (k));
    }
}