package com.yate.ta4j.indicadores;

import org.ta4j.core.Decimal;
import org.ta4j.core.Tick;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.RecursiveCachedIndicator;

/**
 * Exponential moving average indicator.
 * <p>
 */
public class MFIIndicator extends RecursiveCachedIndicator<Decimal> {

    /**
	 * 
	 */
	private static final long serialVersionUID=1L;

	private final TimeSeries series;

    private final int timeFrame;


    /**
     * Constructor.
     * @param indicator an indicator
     * @param timeFrame the EMA time frame
     */
    public MFIIndicator (TimeSeries series,int timeFrame)
    {
        super (series);
        
        this.series=series;
        this.timeFrame=timeFrame;
    }

    @Override
    /*
    Typical Price = (High + Low + Close)/3
    Raw Money Flow = Typical Price x Volume
    Money Flow Ratio = (14-period Positive Money Flow)/(14-period Negative Money Flow)
    Money Flow Index = 100 - 100/(1 + Money Flow Ratio)
    */
    protected Decimal calculate (int index)
    {
    	final String QUASI_ZERO="0.001";
    	int i;
    	Tick currentTick=null;
    	Tick previousTick=null;
    	Decimal currentTP=null;
    	Decimal previousTP=null;
    	Decimal mf=null;
    	
    	Decimal pmfSum=null;
    	Decimal nmfSum=null;
    	
    	Decimal mr=null;
    	
    	
    	if (index==0){
    		return Decimal.ZERO;
    	}
    	
    	pmfSum=Decimal.ZERO;
    	nmfSum=Decimal.ZERO;
    	
  
    	for (i=index;i>0&&i>index-timeFrame;i--){
    		//Tick i
    		currentTick=series.getTick (i);
    		currentTP=currentTick.getMaxPrice ().plus (currentTick.getMinPrice ()).plus (currentTick.getClosePrice ());

    		//Tick i-1
    		previousTick=series.getTick (i-1);
    		previousTP=previousTick.getMaxPrice ().plus (previousTick.getMinPrice ()).plus (previousTick.getClosePrice ());
    		
    		mf=currentTP.multipliedBy (currentTick.getAmount ());
    		if (currentTP.compareTo (previousTP)>0){
    			pmfSum=pmfSum.plus (mf);//Saldo positivo
    		}
    		else{
    			nmfSum=nmfSum.plus (mf);//Saldo negativo
    		}
    		
    	}
    	
    	if (nmfSum.isZero ()){
    		nmfSum=Decimal.valueOf (QUASI_ZERO);
    	}
    	mr=pmfSum.dividedBy (nmfSum);
    	
    	return Decimal.HUNDRED.minus (Decimal.HUNDRED.dividedBy (Decimal.ONE.plus (mr)));
    }
    
}