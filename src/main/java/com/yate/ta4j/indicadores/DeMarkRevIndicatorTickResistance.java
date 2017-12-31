package com.yate.ta4j.indicadores;

import java.util.List;

import org.ta4j.core.Decimal;
import org.ta4j.core.Tick;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.RecursiveCachedIndicator;
import org.ta4j.core.indicators.pivotpoints.DeMarkPivotPointIndicator;
import org.ta4j.core.indicators.pivotpoints.TimeLevel;

public class DeMarkRevIndicatorTickResistance extends RecursiveCachedIndicator<Decimal>
{

	/**
	 * 
	 */
	private static final long serialVersionUID=6172364230121790549L;

	private final DeMarkPivotPointIndicator pivotPointIndicator;

	

	/**
	 * Constructor.
	 * <p>
	 * Calculates the DeMark reversal for the corresponding pivot level
	 * 
	 * @param pivotPointIndicator
	 *            the {@link DeMarkPivotPointIndicator} for this reversal
	 * @param level
	 *            the {@link DeMarkPivotLevel} for this reversal (RESISTANT,
	 *            SUPPORT)
	 */
	public DeMarkRevIndicatorTickResistance (TimeSeries series)
	{
		super (series);
		
		this.pivotPointIndicator=new DeMarkPivotPointIndicator(series,TimeLevel.TICKBASED);
	}

	@Override
	protected Decimal calculate (int index)
	{
		Decimal x=pivotPointIndicator.getValue (index).multipliedBy (Decimal.valueOf (4));
		Decimal result;

		
		result=calculateResistance (x,index);
		
		return result;

	}

	private Decimal calculateResistance (Decimal x,int index)
	{
		List<Integer> ticksOfPreviousPeriod=pivotPointIndicator.getTicksOfPreviousPeriod (index);
		if (ticksOfPreviousPeriod.isEmpty ()){
			return Decimal.NaN;
		}
		Tick tick=getTimeSeries ().getTick (ticksOfPreviousPeriod.get (0));
		Decimal low=tick.getMinPrice ();
		for (int i: ticksOfPreviousPeriod){
			low=getTimeSeries ().getTick (i).getMinPrice ().min (low);
		}

		return x.dividedBy (Decimal.TWO).minus (low);
	}
}
