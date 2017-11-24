package com.yate.indicadores;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import com.yate.indicadores.Exceptions.InternalErrorException;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Indicator;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.AwesomeOscillatorIndicator;
import eu.verdelhan.ta4j.indicators.CachedIndicator;
import eu.verdelhan.ta4j.indicators.MACDIndicator;
import eu.verdelhan.ta4j.indicators.RSIIndicator;
import eu.verdelhan.ta4j.indicators.helpers.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.helpers.PriceVariationIndicator;
import eu.verdelhan.ta4j.indicators.helpers.TypicalPriceIndicator;



public class BuildIndicators
{
	private static Logger trace=Logger.getLogger (BuildIndicators.class);
	
	static private final String INTERNAL_RESOURCE="ibex_2017_07_03.csv";
	
	static private final char DEFAULT_SEPARATOR=',';
	
	
	
	static private final int AWESOME_NUMBER_OF_ARGS=3;
	static private final String AWESOME_NAME="awesome";
	static private final String AWESOME_SHORT_NAME="aws";
	static private final int MACD_NUMBER_OF_ARGS=3;
	static private final String MACD_SHORT_NAME="macd";
	static private final String MACD_NAME="macd";
	static private final int RSI_NUMBER_OF_ARGS=2;
	static private final String RSI_SHORT_NAME="rsi";
	static private final String RSI_NAME="rsi";
	
	//Argumentos de la linea de comando
	private String csvInFile=null;
	private String csvOutFile=null;
	private String tickTime=null;
	private final int defaultTickTime=300;//300 segundos
	private int iTickTime=defaultTickTime;
	
	private List<IndicatorArgs> indicators=null;
	
	
	private ClosePriceIndicator closePrice=null;
	private TypicalPriceIndicator typicalPrice=null;
    // Price variation
	private PriceVariationIndicator priceVariation=null;
	
	private TimeSeries series=null;

	private CommandLine cliLine=null;
	
	public static void main (String[] args)
	{
		BuildIndicators buildIndicators=null;
		
		List<String[]> lines=null;
		    
		
	    
	    buildIndicators=new BuildIndicators ();
	    
	    if (!buildIndicators.parseCommonArgs (args)){
	    	System.exit (1);
	    }
	    
	    //Se carga el fichero CSV
	    if (buildIndicators.csvInFile==null){//Recurso por defecto
	    	lines=buildIndicators.loadCSVFromResource ();
	    }
	    else{//Fichero pasado como argumento
	    	lines=buildIndicators.loadCSVFromFile (buildIndicators.csvInFile);
	    }
	    
	    //Se construye la serie temporal de ticks para el intervalo de tiempo especificado, por defecto 300
	    buildIndicators.series=BuildSeries.exec (lines,buildIndicators.iTickTime);
	    
	    // Close price
	    buildIndicators.closePrice=new ClosePriceIndicator (buildIndicators.series);
	 	// Typical price
	    buildIndicators.typicalPrice=new TypicalPriceIndicator (buildIndicators.series);
	 	// Price variation
	    buildIndicators.priceVariation=new PriceVariationIndicator (buildIndicators.series);
	    
	    buildIndicators.parseIndicatorArgs (args);
	    
	    buildIndicators.build (buildIndicators.csvOutFile);
	}
	
	
	private boolean parseCommonArgs (String[] args)
	{
	
		
		HelpFormatter formatter=null;
		CommandLineParser cliParser=null;
		
		Options options=null;
		
		Option helpOpt=null;
		Option fileInOpt=null;
		Option fileOutOpt=null;
		Option tickTimeOpt=null;
		
		Option awesomeIndOpt=null;
		Option macdIndOpt=null;
		Option rsiIndOpt=null;
		
	
		
		//Ayuda
		helpOpt=new Option ("h","Imprime la ayuda");
		//fichero entrada
		fileInOpt=new Option ("fin",true,"Fichero CSV a cargar, si no se indica se cargara el fichero interno");
		fileInOpt.setOptionalArg (true);
		//fichero de salida o stdout
		fileOutOpt=new Option ("fout",true,"Fichero CSV a crear, si no se indica se usa la salida estandar");
		fileOutOpt.setOptionalArg (true);
		//Tiempo del tick
		tickTimeOpt=new Option ("ttime",true,"Tiempo con el que son creados los ticks expreado en segundos, si no se indica se toma por defecto 300 segundos");
		tickTimeOpt.setOptionalArg (true);
		tickTimeOpt.setType (Integer.class);
	
		//Indicadores
		//Awesome, 3 argumentos: p1, p2, [close price, variation price, typical price]
		awesomeIndOpt=new Option (AWESOME_SHORT_NAME,AWESOME_NAME,true,"Indicador Awesome. Usar -aws o --awesome, argumentos <timeFrameSma1> <timeFrameSma1> <Base de calculo (close,typical,variation)>");
		awesomeIndOpt.setOptionalArg (true);
		awesomeIndOpt.setArgs (AWESOME_NUMBER_OF_ARGS);
		awesomeIndOpt.setValueSeparator (',');
		//MACD
		macdIndOpt=new Option (MACD_SHORT_NAME,MACD_NAME,true,"Indicador MACD. Usar -macd o --macd, argumentos <ShortTimeFrame> <LongTimeFrame> <Base de calculo (close,typical,variation)>");
		macdIndOpt.setOptionalArg (true);
		macdIndOpt.setArgs (MACD_NUMBER_OF_ARGS);
		macdIndOpt.setValueSeparator (',');
		//RSI
		rsiIndOpt=new Option (RSI_SHORT_NAME,RSI_NAME,true,"Indicador RSI. Usar -rsi o --rsHelpFormatter formatter=null;i, argumentos <TimeFrame> <Base de calculo (close,typical,variation)>");
		rsiIndOpt.setOptionalArg (true);
		rsiIndOpt.setArgs (RSI_NUMBER_OF_ARGS);
		rsiIndOpt.setValueSeparator (',');

		
	    options=new Options ();

	    options.addOption (helpOpt);
	    
	    options.addOption (fileInOpt);
	    options.addOption (fileOutOpt);
	    
	    options.addOption (tickTimeOpt);
	    
	    options.addOption (awesomeIndOpt);
	    options.addOption (macdIndOpt);
	    options.addOption (rsiIndOpt);
	    
	  
	    cliParser=new GnuParser ();
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
	    	
	    	if (cliLine.hasOption ("ttime")){
	    		tickTime=cliLine.getOptionValue ("ttime");
	   
				try{
					iTickTime=Integer.parseInt (tickTime);
				}
				catch (NumberFormatException e){
					trace.error ("El argumento -ttick se ha suministrado con un valor que no es un entero positivo");
				}
	    	}
	    }	    
	    catch (ParseException e){
            trace.error ("Error analizando linea de comandos",e);
            return false;
        }
	    
	    return true;
	}
	
	
	private void parseIndicatorArgs (String[] args)
	{
		String []IndicatorArgs=null;
		
		
	   
		//Indicadores
		indicators=new ArrayList<IndicatorArgs> ();

		try{
			//AWESOME
			if (cliLine.hasOption (AWESOME_NAME)){
				IndicatorArgs=cliLine.getOptionValues (AWESOME_NAME);
				buildIndicatorInstance (AwesomeOscillatorIndicator.class,AWESOME_NAME,IndicatorArgs,AWESOME_NUMBER_OF_ARGS);
			}
			//RSI
			if (cliLine.hasOption (RSI_NAME)){
				IndicatorArgs=cliLine.getOptionValues (RSI_NAME);
				buildIndicatorInstance (RSIIndicator.class,RSI_NAME,IndicatorArgs,RSI_NUMBER_OF_ARGS);
			}
			//MACD
			if (cliLine.hasOption (MACD_NAME)){
				IndicatorArgs=cliLine.getOptionValues (MACD_NAME);
				buildIndicatorInstance (MACDIndicator.class,MACD_NAME,IndicatorArgs,MACD_NUMBER_OF_ARGS);
			}
		}
		catch (InternalErrorException e){
			trace.error ("Error instanciando indicadores ",e);
		}
	}

	
	
	
	//private <I extends CachedIndicator<Decimal>> boolean buildIndicatorInstance (Class<I> clazz,String name,String []IndicatorArgs,int nUnitArgs)
	@SuppressWarnings("unchecked")
	private void buildIndicatorInstance (Class<?> clazz,String name,String []IndicatorArgs,int nUnitArgs)
	throws InternalErrorException
	{
		int i=0;
		int j=0;
		
		int iarg;
	
		
    	BaseCalc baseCalc=null;
    	
    	Class<?> []constructorClassArgs=null;
    	Object constructorArgs[]=null;
    	Constructor<?> constructor=null;
    	
    	CachedIndicator<Decimal> indicator=null;
    	
    	if ((IndicatorArgs.length%nUnitArgs)!=0){
    		trace.error ("El indicador "+name+" requiere "+nUnitArgs+" argumentos para cada instancia");
    		return;
    	}
    	
    	//Se busca el constructor usando reflection
    	constructorClassArgs=new Class<?>[nUnitArgs];
    	constructorClassArgs[0]=Indicator.class;
    	for (i=0;i<nUnitArgs-1;i++){
    		constructorClassArgs[i+1]=int.class;
    	}
    	try{
			constructor=clazz.getConstructor (constructorClassArgs);
		}
		catch (NoSuchMethodException e){
			trace.error ("Error obteniendo el constructor del indicador "+name,e);
			return;
		}
		catch (SecurityException e){
			trace.error ("Error obteniendo el constructor del indicador "+name,e);
			return;
		}
    	
    	constructorArgs=new Object[nUnitArgs];
    	for (i=0;i<IndicatorArgs.length/nUnitArgs;i++){
    		
    		for (j=0;j<nUnitArgs-1;j++){
    			try{
    				//constructorArgs[j+1]=Integer.parseInt (IndicatorArgs[(i*nUnitArgs)+j]);
    				iarg=Integer.parseInt (IndicatorArgs[(i*nUnitArgs)+j]);
    				constructorArgs[j+1]=iarg;
    			}
    			catch (NumberFormatException e){
    				trace.error ("Error el argumento "+IndicatorArgs[(i*nUnitArgs)]+" del indicador "+name+", no es un entero");
    				return;
    			}
    		}
    		
    		//if ((baseCalc=BaseCalc.getBaseCalc (IndicatorArgs[(i*nUnitArgs)+(nUnitArgs-1)]))==null){
    		if ((baseCalc=BaseCalc.getBaseCalc (IndicatorArgs[(i*nUnitArgs)+j]))==null){
    			trace.error ("La base de calculo indicada para el indicador "+name+", es desconocida");
        		return;
    		}
    		switch (baseCalc){
			case CLOSE_PRICE:
				constructorArgs[0]=closePrice;
				break;
			case SERIES:
				constructorArgs[0]=series;
				break;
			case TYPICAL_PRICE:
				constructorArgs[0]=typicalPrice;
				break;
			case VARIATION_PRICE:
				constructorArgs[0]=priceVariation;
				break;
			default:
				trace.error ("La base de calculo indicada para el indicador "+name+", es desconocida");
        		return;
    		}
    	
    		try{
				indicator=(CachedIndicator<Decimal>)constructor.newInstance (constructorArgs);
			}
			catch (InstantiationException e){
				throw new InternalErrorException ("Error instanciando el constructor del indicador "+name+" (e)");
			}
			catch (IllegalAccessException e){
				throw new InternalErrorException ("Error instanciando el constructor del indicador "+name+" (e)");
			}
			catch (IllegalArgumentException e){
				throw new InternalErrorException ("Error instanciando el constructor del indicador "+name+" (e)");
			}
			catch (InvocationTargetException e){
				throw new InternalErrorException ("Error instanciando el constructor del indicador "+name+" (e)");
			}
    		indicators.add (new IndicatorArgs (name,indicator));
    	}
	}
	
	/*public static Child create(Integer i, String s) throws Exception
	{
	  Constructor c = Class.forName(childClass).getConstructor(new Object[]{Integer.class, String.class});
	  c.setAccessible(true);
	  Child instance = (Child) c.newInstance(new Object[]{i , s}) ; 
	  return instance;
	}*/
	
	
	private void build (String csvOutFile)
	{
		int i;
		StringBuffer strBuff=null;
        PrintStream pos=null;
        
		
		/*
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
		
		//awesome=new AwesomeOscillatorIndicator (); 
*/

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
		        
		        strBuff.append (series.getTick(i).getEndTime().format (DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss"))).append(',')
		        .append(closePrice.getValue(i)).append(',')
		        .append(typicalPrice.getValue(i)).append(',')
		        .append(priceVariation.getValue(i)).append(',');
		    	for (IndicatorArgs item:indicators){
		    		strBuff.append (item.getIndicator ().getValue (i)).append(',');
				}
		    	strBuff.deleteCharAt (strBuff.length ()-1);
		        
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

	
/*	
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
        
        AwesomeOscillatorIndicator awesome=null;
	    
        
        PrintStream pos=null;
        
        
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
		
		//awesome=new AwesomeOscillatorIndicator (); 


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
		        
		        strBuff.append (series.getTick(i).getEndTime().format (DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss"))).append(',')
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
	*/
	
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
