package com.yate.tatool;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.yate.indicadores.Exceptions.InternalErrorException;

/**
 * Parse una linea correspondiente a una cotizacion (trade) de un fichero CSV.
 * 
 * - Primer campo -> fecha en formato yyyyMMddhhmmss.
 * - Segundo campo -> cotizacion
 * - Tercer campo -> Volumen
 * 
 * @author fjavier
 *
 */
public class TradeLine
{
	static public Date getDateTime (String []line)
	throws InternalErrorException
	{
		final String dateFormat="yyyyMMddHHmmss";
		DateFormat sdf=null;
		
		
		sdf=new SimpleDateFormat (dateFormat);
		
		try{
			return sdf.parse (line[0]);
		}
		catch (ParseException e){
			throw new InternalErrorException ("Error parseando fecha (dateFormat) "+line[0]);
		}
	}
	
	static public long getEpoch (String []line)
	throws InternalErrorException
	{
		final String dateFormat="yyyyMMddHHmmss";
		DateFormat sdf=null;
		
		
		sdf=new SimpleDateFormat (dateFormat);
		
		try{
			return sdf.parse (line[0]).getTime ();
		}
		catch (ParseException e){
			throw new InternalErrorException ("Error parseando fecha (dateFormat) "+line[0]);
		}
	}
	
	static public Double getPrice (String []line)
	throws InternalErrorException
	{
		try{
			return Double.parseDouble (line[1]);
		}
		catch (NumberFormatException e){
			throw new InternalErrorException ("Error parseando cotizacion "+line[1]);
		}
	}
	
	static public Double getVolume (String []line)
	throws InternalErrorException
	{
		try{
			return Double.parseDouble (line[2]);
		}
		catch (NumberFormatException e){
			throw new InternalErrorException ("Error parseando volumen "+line[1]);
		}
	}
}
