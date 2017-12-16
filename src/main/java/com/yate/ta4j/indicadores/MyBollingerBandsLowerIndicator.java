package com.yate.ta4j.indicadores;

import org.ta4j.core.Decimal;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;


public class MyBollingerBandsLowerIndicator extends CachedIndicator<Decimal>
{    
    /**
	 * 
	 */
	private static final long serialVersionUID=-8504575379394817861L;

	private final SMAIndicator sma;
    
    private final StandardDeviationIndicator sd;
    
    private final Decimal k;

    
    public MyBollingerBandsLowerIndicator (Indicator<Decimal> indicator,int timeFrame)
    {
    	this (indicator,timeFrame,Decimal.TWO);
    }
    
    /**
     * Constructor.
     * @param indicator an indicator (usually close price)
     * @param timeFrame the time frame
     * @param k the K multiplier (usually 2.0)
     */
    public MyBollingerBandsLowerIndicator (Indicator<Decimal> indicator,int timeFrame,Decimal k)
    {
        super (indicator);
        
        this.sma=new SMAIndicator (indicator,timeFrame);
        this.sd=new StandardDeviationIndicator (indicator,timeFrame);
        
        this.k=k;
    }

    @Override
    protected Decimal calculate(int index)
    {
        return sma.getValue (index).minus (sd.getValue (index).multipliedBy (k));
    }
}