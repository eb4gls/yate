package com.yate.indicadores;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.yate.indicadores.Exceptions.InternalErrorException;

import eu.verdelhan.ta4j.BaseTick;
import eu.verdelhan.ta4j.BaseTimeSeries;
import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;

public class BuildSeries
{
	private static Logger trace=Logger.getLogger (BuildSeries.class);
	
	
	public static TimeSeries exec (List<String[]> lines)
	{
		int i;
		List<Tick> ticks=null;
		ZonedDateTime beginTime=null;
		ZonedDateTime endTime=null;
		
		ZonedDateTime tradeTimestamp=null;
		
		
		if ((lines!=null)&&!lines.isEmpty ()){//Si no null y no vacio

			// Getting the first and last trades timestamps
			try{
				beginTime=ZonedDateTime.ofInstant (Instant.ofEpochMilli (TradeLine.getEpoch (lines.get (0))),ZoneId.systemDefault ());
			}
			catch (InternalErrorException e){
				trace.error ("Error en la fecha de la linea 0 "+e);
				
				return null;
			}
			
			try{
				endTime=ZonedDateTime.ofInstant (Instant.ofEpochMilli (TradeLine.getEpoch (lines.get (lines.size ()-1))),ZoneId.systemDefault ());
			}
			catch (InternalErrorException e){
				trace.error ("Error en la fecha de la linea "+(lines.size ()-1)+" "+e);
				
				return null;
			}
			
			if (beginTime.isAfter (endTime)){
				Instant beginInstant=beginTime.toInstant ();
				Instant endInstant=endTime.toInstant ();
				beginTime=ZonedDateTime.ofInstant (endInstant,ZoneId.systemDefault ());
				endTime=ZonedDateTime.ofInstant (beginInstant,ZoneId.systemDefault ());
				// Since the CSV file has the most recent trades at the top of the file, we'll reverse the list to feed the List<Tick> correctly.
				Collections.reverse (lines);
				
				trace.info ("Se ha invertido el orden del fichero cargado, la fecha de la linea 0 era mayor que la de la linea "+(lines.size ()-1));
			}
			// Building the empty ticks (every 300 seconds, yeah welcome in Bitcoin world)
			ticks=buildEmptyTicks (beginTime,endTime,300);
			// Filling the ticks with trades
			i=0;
			for (String[] tradeLine: lines){
				try{
					tradeTimestamp=ZonedDateTime.ofInstant (Instant.ofEpochMilli (TradeLine.getEpoch (tradeLine)),ZoneId.systemDefault ());
				
					for (Tick tick: ticks){
						if (tick.inPeriod (tradeTimestamp)){
							tick.addTrade (TradeLine.getVolume (tradeLine),TradeLine.getPrice (tradeLine));
						}
					}
				}
				catch (InternalErrorException e){
					trace.error ("Error de formato en la linea "+i+" "+e);
					continue;
				}
				
				i++;
			}
			// Removing still empty ticks
			removeEmptyTicks (ticks);
		}
		//else{//Si ticks==null se sale con null, aunque la llamada BaseTimeSeries genera una serie vacia
		//}

		return new BaseTimeSeries ("Trades",ticks);
	}	
	
	
	
	
	
	/**
     * Builds a list of empty ticks.
     * @param beginTime the begin time of the whole period
     * @param endTime the end time of the whole period
     * @param duration the tick duration (in seconds)
     * @return the list of empty ticks
     */
	private static List<Tick> buildEmptyTicks (ZonedDateTime beginTime,ZonedDateTime endTime,int duration)
	{
		List<Tick> emptyTicks=new ArrayList<Tick> ();
		Duration tickDuration=null;
		
		
		tickDuration=Duration.ofSeconds (duration);
		ZonedDateTime tickEndTime=beginTime;
		do{
			tickEndTime=tickEndTime.plus (tickDuration);
			emptyTicks.add (new BaseTick (tickDuration,tickEndTime));
		}while (tickEndTime.isBefore (endTime));

		return emptyTicks;
	}
	
    /**
     * Removes all empty (i.e. with no trade) ticks of the list.
     * @param ticks a list of ticks
     */
	private static void removeEmptyTicks (List<Tick> ticks)
	{
		for (int i=ticks.size ()-1;i>=0;i--){
			if (ticks.get (i).getTrades ()==0){
				ticks.remove (i);
			}
		}
	}

}
