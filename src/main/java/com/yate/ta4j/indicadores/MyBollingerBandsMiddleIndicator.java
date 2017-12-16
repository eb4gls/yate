package com.yate.ta4j.indicadores;

import org.ta4j.core.Decimal;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.SMAIndicator;


public class MyBollingerBandsMiddleIndicator extends CachedIndicator<Decimal>
{    
	/**
	 * 
	 */
	private static final long serialVersionUID=7423919918756882900L;
	private final SMAIndicator sma;
    
    
    /**
     * Constructor.
     * @param indicator an indicator (usually close price)
     * @param timeFrame the time frame
     * @param k the K multiplier (usually 2.0)
     */
    public MyBollingerBandsMiddleIndicator (Indicator<Decimal> indicator,int timeFrame)
    {
        super (indicator);
        
        this.sma=new SMAIndicator (indicator,timeFrame);
    }

    @Override
    protected Decimal calculate(int index)
    {
        return sma.getValue (index);
    }
}