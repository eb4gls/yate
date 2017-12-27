package com.yate.ta4j.indicadores;

import java.time.ZonedDateTime;

import org.ta4j.core.Decimal;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.RecursiveCachedIndicator;

/**
 * Exponential moving average indicator.
 * <p>
 */
public class TimeIndicator extends RecursiveCachedIndicator<Decimal>
{
	public enum RefTime
	{
		DAILY (1),
		WEEKLY (2),
		MONTHLY (3),
		YEARLY (4);
		
	
		private int idRefTime;

		private RefTime (int id)
		{
			idRefTime=id;
		}

		public int getIdRefTime ()
		{
			return idRefTime;
		}

		static public RefTime getRefTime (int idRefTime)
		{
			if (idRefTime==RefTime.DAILY.getIdRefTime ()){
				return DAILY;
			}
			else{
				if (idRefTime==RefTime.WEEKLY.getIdRefTime ()){
					return WEEKLY;
				}
				else{
					if (idRefTime==RefTime.MONTHLY.getIdRefTime ()){
						return MONTHLY;
					}
					else{
						if (idRefTime==RefTime.YEARLY.getIdRefTime ()){
							return YEARLY;
						}
						else{
							return null;
						}
					}
				}
			}
		}
	}
	
	
    /**
	 * 
	 */
	private static final long serialVersionUID=1L;

	private final TimeSeries series;

    private final RefTime reference;
    
    static private final Decimal SECONDS_IN_A_DAY=Decimal.valueOf (24*60*60);
    static private final Decimal SECONDS_IN_A_WEEK=Decimal.valueOf (7*24*60*60);
    static private final Decimal SECONDS_IN_A_MONTH=Decimal.valueOf (30*24*60*60);
    static private final Decimal SECONDS_IN_A_YEAR=Decimal.valueOf (365*24*60*60);


    /**
     * Constructor.
     * @param indicator an indicator
     * @param timeFrame the EMA time frame
     */
    public TimeIndicator (TimeSeries series,RefTime reference)
    {
        super (series);
        
        this.series=series;
        this.reference=reference;
    }

    @Override
    protected Decimal calculate (int index)
    {
    	ZonedDateTime tickTime=null;
    	ZonedDateTime refTime=null;
    	
    	Decimal diff=null;
    	
    	Decimal res=null;
    
    	
    	
    	tickTime=series.getTick (index).getEndTime ();
    
    	switch (reference){
		case DAILY:
			refTime=tickTime.withHour (0).withMinute (0).withSecond (0);
			diff=Decimal.valueOf (tickTime.toEpochSecond ()-refTime.toEpochSecond ());
			res=diff.dividedBy (SECONDS_IN_A_DAY);
			break;
		case WEEKLY:
			refTime=tickTime.minusDays (tickTime.getDayOfWeek ().getValue ()-1).withHour (0).withMinute (0).withSecond (0);
			diff=Decimal.valueOf (tickTime.toEpochSecond ()-refTime.toEpochSecond ());
			res=diff.dividedBy (SECONDS_IN_A_WEEK);
			break;
		case MONTHLY:
			refTime=tickTime.withDayOfMonth (1).withHour (0).withMinute (0).withSecond (0);
			diff=Decimal.valueOf (tickTime.toEpochSecond ()-refTime.toEpochSecond ());
			res=diff.dividedBy (SECONDS_IN_A_MONTH);
			break;
		case YEARLY:
			refTime=tickTime.withDayOfYear (1).withHour (0).withMinute (0).withSecond (0);
			diff=Decimal.valueOf (tickTime.toEpochSecond ()-refTime.toEpochSecond ());
			res=diff.dividedBy (SECONDS_IN_A_YEAR);
			break;
		default:
			res=null;
			break;
    	}
    	
    	return res;
    }
}