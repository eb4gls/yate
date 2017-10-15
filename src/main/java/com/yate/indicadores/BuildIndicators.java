package com.yate.indicadores;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import com.opencsv.CSVReader;

import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.EMAIndicator;
import eu.verdelhan.ta4j.indicators.PPOIndicator;
import eu.verdelhan.ta4j.indicators.ROCIndicator;
import eu.verdelhan.ta4j.indicators.RSIIndicator;
import eu.verdelhan.ta4j.indicators.SMAIndicator;
import eu.verdelhan.ta4j.indicators.WilliamsRIndicator;
import eu.verdelhan.ta4j.indicators.helpers.AverageTrueRangeIndicator;
import eu.verdelhan.ta4j.indicators.helpers.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.helpers.PriceVariationIndicator;
import eu.verdelhan.ta4j.indicators.helpers.TypicalPriceIndicator;
import eu.verdelhan.ta4j.indicators.statistics.StandardDeviationIndicator;



public class BuildIndicators
{
	private static Logger trace=Logger.getLogger (BuildIndicators.class);
	
	static private final String INTERNAL_RESOURCE="ibex_2017_07_03.csv";
	
	static private final char DEFAULT_SEPARATOR=',';

	public static void main(String[] args)
	{
		BuildIndicators buildIndicators=null;
		
		HelpFormatter formatter=null;
		CommandLineParser cliParser=null;
		CommandLine cliLine=null;
		Options options=null;
		Option helpOpt=null;
		Option fileInOpt=null;
		Option fileOutOpt=null;
		
		String csvInFile=null;
		String csvOutFile=null;
		
		
		helpOpt=new Option ("h","Imprime la ayuda");
		fileInOpt=new Option ("fin",true,"Fichero CSV a cargar, si no se indica se cargara el fichero interno");
		fileOutOpt=new Option ("fout",true,"Fichero CSV a crear, si no se indica se usa la salida estandar");
		
	    options=new Options ();

	    options.addOption (helpOpt);
	    options.addOption (fileInOpt);
	    options.addOption (fileOutOpt);
	    

	    List<String[]> lines=null;
	    
	    TimeSeries series=null;
	    
	    
	    
	    cliParser=new GnuParser();
	    try{
	    	//parse the command line arguments
	    	cliLine=cliParser.parse (options,args);
	    
	    	if (cliLine.hasOption ('h')) {
	            //automatically generate the help statement
	            formatter=new HelpFormatter ();
	            formatter.printHelp ("Usar TestPrePerso",options,true);
	            System.exit (0);
	        }
	    	if (cliLine.hasOption ("fin")){
	    		csvInFile=cliLine.getOptionValue ("fin");
	    	}
	    	if (cliLine.hasOption ("fout")){
	    		csvOutFile=cliLine.getOptionValue ("fout");
	    	}
	    }	    
	    catch (ParseException e){
            trace.error ("Error analizando linea de comandos",e);
            System.exit (-1);
        }

	    
	    buildIndicators=new BuildIndicators ();
	    
	    //Se carga el fichero CSV
	    if (csvInFile==null){//Recurso por defecto
	    	lines=buildIndicators.loadCSVFromResource ();
	    }
	    else{//Fichero pasado como argumento
	    	lines=buildIndicators.loadCSVFromFile (csvInFile);
	    }
	    
	    //Se construye la serie temporal de ticks para el intervalo de tiempo especificado, por defecto 300
	    series=BuildSeries.exec (lines);
	    
	   
	    buildIndicators.build (series,csvOutFile);
	}
	
	
	
	private void build (TimeSeries series,String csvOutFile)
	{
		int i;
		StringBuffer strBuff=null;
		
		ClosePriceIndicator closePrice=null;
	    TypicalPriceIndicator typicalPrice = null;
        // Price variation
        PriceVariationIndicator priceVariation = null;
        // Simple moving averages
        SMAIndicator shortSma = null;
        SMAIndicator longSma = null;
        // Exponential moving averages
        EMAIndicator shortEma = null;
        EMAIndicator longEma = null;
        // Percentage price oscillator
        PPOIndicator ppo = null;
        // Rate of change
        ROCIndicator roc = null;
        // Relative strength index
        RSIIndicator rsi = null;
        // Williams %R
        WilliamsRIndicator williamsR = null;
        // Average true range
        AverageTrueRangeIndicator atr = null;
        // Standard deviation
        StandardDeviationIndicator sd = null;
	    
        
        PrintStream pos=null;
        
        
        /**
         * Creating indicators
         */
		// Close price
		closePrice=new ClosePriceIndicator (series);
		// Typical price
		typicalPrice=new TypicalPriceIndicator (series);
		// Price variation
		priceVariation=new PriceVariationIndicator (series);
		// Simple moving averages
		shortSma=new SMAIndicator (closePrice,8);
		longSma=new SMAIndicator (closePrice,20);
		// Exponential moving averages
		shortEma=new EMAIndicator (closePrice,8);
		longEma=new EMAIndicator (closePrice,20);
		// Percentage price oscillator
		ppo=new PPOIndicator (closePrice,12,26);
		// Rate of change
		roc=new ROCIndicator (closePrice,100);
		// Relative strength index
		rsi=new RSIIndicator (closePrice,14);
		// Williams %R
		williamsR=new WilliamsRIndicator (series,20);
		// Average true range
		atr=new AverageTrueRangeIndicator (series,20);
		// Standard deviation
		sd=new StandardDeviationIndicator (closePrice,14);


		try{
			if (csvOutFile==null){
				pos=System.out;
			}
			else{
				try{
					pos=new PrintStream (csvOutFile);
				}
				catch (FileNotFoundException e){
					trace.error ("No se pudo crear el fichero "+csvOutFile,e);
					return;
				}
			}
	        
	   
	        for (i=0;i<series.getTickCount();i++){
		        strBuff=new StringBuffer ();
		        
		        strBuff.append (series.getTick(i).getEndTime()).append(',')
		        .append(closePrice.getValue(i)).append(',')
		        .append(typicalPrice.getValue(i)).append(',')
		        .append(priceVariation.getValue(i)).append(',')
		        .append(shortSma.getValue(i)).append(',')
		        .append(longSma.getValue(i)).append(',')
		        .append(shortEma.getValue(i)).append(',')
		        .append(longEma.getValue(i)).append(',')
		        .append(ppo.getValue(i)).append(',')
		        .append(roc.getValue(i)).append(',')
		        .append(rsi.getValue(i)).append(',')
		        .append(williamsR.getValue(i)).append(',')
		        .append(atr.getValue(i)).append(',')
		        .append(sd.getValue(i));;
		        
		        pos.println (strBuff);
	        }
		}
		finally{
			pos.flush ();
			if ((csvOutFile!=null)&&(pos!=null)){
				pos.close ();
				pos=null;
			}
		}
	}
	
	
	/**
	 * Genera un lista de lineas a partir de un fichero CSV. La carga se realiza a partir del path suministrado para el fichero
	 * CSV.
	 * 
	 * @param file Path del fichero CSV.
	 * @return Lista de Arrays de String, dimensionado al numero de campos de la linea.
	 */
	private List<String[]> loadCSVFromFile (String file)
	{
		InputStream is=null;
	
		try{
			is=new FileInputStream (file);
		}
		catch (FileNotFoundException e){

			trace.error ("Error,no se ha encontrado el fichero CSV "+file,e);
			return null;
		}

		return loadCSV (is);
	}
	
	/**
	 * Genera un lista de lineas a partir de un fichero CSV. La carga se realiza a partir de un fichero CSV contenido como recurso interno.
	 * 
	 * @return Lista de Arrays de String, dimensionado al numero de campos de la linea.
	 */
	private List<String[]> loadCSVFromResource ()
	{
		InputStream is=null;
		
		is=BuildIndicators.class.getClassLoader ().getResourceAsStream (INTERNAL_RESOURCE);
		
		return loadCSV (is);
	}
	
	/**
	 * Genera un lista de lineas a partir de un fichero CSV, suministrado con un InputStream.
	 * 
	 * @param is InputStream con el fichero CSV.
	 * @return
	 */
	private List<String[]> loadCSV (InputStream is)
	{
        CSVReader csvReader=null;
        List<String[]> lines=null;
        
        
        
		try{
			csvReader=new CSVReader (new InputStreamReader (is),DEFAULT_SEPARATOR);
			lines=csvReader.readAll ();
			//lines.remove(0);// Removing header line
		}
		catch (IOException e){
			trace.error ("No se pudo cargar las cotizaciones del recurso interno "+INTERNAL_RESOURCE,e);
			return null;
		}
		finally{
			if (csvReader!=null){
				try{
					csvReader.close ();
				}
				catch (IOException e){
					trace.error ("No se pudo liberar CSVReader",e);
				}
			}
			
			if (is!=null){
				try{
					is.close ();
				}
				catch (IOException e){
					trace.error ("No se pudo liberar InputStream",e);
				}
				is=null;
			}
		}
		
		return lines;
	}
}
