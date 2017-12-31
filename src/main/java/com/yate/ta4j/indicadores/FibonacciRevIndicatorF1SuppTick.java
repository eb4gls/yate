/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 Marc de Verdelhan & respective authors (see AUTHORS)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.yate.ta4j.indicadores;

import java.util.List;

import org.ta4j.core.Decimal;
import org.ta4j.core.Tick;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.RecursiveCachedIndicator;
import org.ta4j.core.indicators.pivotpoints.PivotPointIndicator;
import org.ta4j.core.indicators.pivotpoints.TimeLevel;

/**
 * Fibonacci Reversal Indicator.
 * <p>
 * @author team172011(Simon-Justus Wimmer), 09.10.2017
 * @see <a href="http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:pivot_points">chart_school: pivotpoints</a>
 */
public class FibonacciRevIndicatorF1SuppTick extends RecursiveCachedIndicator<Decimal>
{

    /**
	 * 
	 */
	private static final long serialVersionUID=-8800339763457882079L;
	private final PivotPointIndicator pivotPointIndicator;
 

    
    /**
     * Standard Fibonacci factors
     */
    public enum FibonacciFactor {
        Factor1(Decimal.valueOf(0.382)),
        Factor2(Decimal.valueOf(0.618)),
        Factor3(Decimal.ONE);

        private final Decimal factor;

        FibonacciFactor(Decimal factor) {
            this.factor = factor;
        }

        public Decimal getFactor(){
            return this.factor;
        }

    }

    public FibonacciRevIndicatorF1SuppTick (TimeSeries series)
    {
        super (series);
    	
    	this.pivotPointIndicator=new PivotPointIndicator (series,TimeLevel.TICKBASED);
    }
    
    @Override
    protected Decimal calculate (int index)
    {
    	final Decimal f1=Decimal.valueOf(0.382);
    	
        List<Integer> ticksOfPreviousPeriod = pivotPointIndicator.getTicksOfPreviousPeriod(index);
        if (ticksOfPreviousPeriod.isEmpty())
            return Decimal.NaN;
        Tick tick = getTimeSeries().getTick(ticksOfPreviousPeriod.get(0));
        Decimal high =  tick.getMaxPrice();
        Decimal low = tick.getMinPrice();
        for(int i: ticksOfPreviousPeriod){
            high = (getTimeSeries().getTick(i).getMaxPrice()).max(high);
            low = (getTimeSeries().getTick(i).getMinPrice()).min(low);
        }

        return pivotPointIndicator.getValue(index).minus(f1.multipliedBy(high.minus(low)));
    }
}
