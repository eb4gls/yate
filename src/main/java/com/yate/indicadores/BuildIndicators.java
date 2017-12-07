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
import eu.verdelhan.ta4j.indicators.EMAIndicator;
import eu.verdelhan.ta4j.indicators.FisherIndicator;
import eu.verdelhan.ta4j.indicators.HMAIndicator;
import eu.verdelhan.ta4j.indicators.MACDIndicator;
import eu.verdelhan.ta4j.indicators.PPOIndicator;
import eu.verdelhan.ta4j.indicators.RAVIIndicator;
import eu.verdelhan.ta4j.indicators.ROCIndicator;
import eu.verdelhan.ta4j.indicators.RSIIndicator;
import eu.verdelhan.ta4j.indicators.WilliamsRIndicator;
import eu.verdelhan.ta4j.indicators.helpers.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.helpers.MedianPriceIndicator;
import eu.verdelhan.ta4j.indicators.helpers.PriceVariationIndicator;
import eu.verdelhan.ta4j.indicators.helpers.TypicalPriceIndicator;



public class BuildIndicators
{
	private static Logger trace=Logger.getLogger (BuildIndicators.class);
	
	static private final String INTERNAL_RESOURCE="ibex_2017_07_03.csv";
	
	static private final char DEFAULT_SEPARATOR=',';
	
	
	
	static private final int AWESOME_NUMBER_OF_ARGS=3;
	static private final String AWESOME_SHORT_NAME="aws";
	static private final String AWESOME_NAME="awesome";
	
	
	static private final int MACD_NUMBER_OF_ARGS=3;
	static private final String MACD_SHORT_NAME="macd";
	static private final String MACD_NAME="macd";
	
	static private final int RSI_NUMBER_OF_ARGS=2;
	static private final String RSI_SHORT_NAME="rsi";
	static private final String RSI_NAME="rsi";
	
	static private final int EMA_NUMBER_OF_ARGS=2;
	static private final String EMA_SHORT_NAME="ema";
	static private final String EMA_NAME="ema";
	
	static private final int PPO_NUMBER_OF_ARGS=3;
	static private final String PPO_SHORT_NAME="ppo";
	static private final String PPO_NAME="ppo";
	
	static private final int ROC_NUMBER_OF_ARGS=2;
	static private final String ROC_SHORT_NAME="roc";
	static private final String ROC_NAME="roc";
	
	static private final int WILL_NUMBER_OF_ARGS=2;
	static private final String WILL_SHORT_NAME="will";
	static private final String WILL_NAME="williansr";
	
	static private final int FISHER_NUMBER_OF_ARGS=2;
	static private final String FISHER_SHORT_NAME="fshr";
	static private final String FISHER_NAME="fisher";
	
	static private final int HMA_NUMBER_OF_ARGS=2;
	static private final String HMA_SHORT_NAME="hma";
	static private final String HMA_NAME="hma";
	
	static private final int RAVI_NUMBER_OF_ARGS=3;
	static private final String RAVI_SHORT_NAME="ravi";
	static private final String RAVI_NAME="ravi";
	
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
	private MedianPriceIndicator medianPrice=null;
	
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
	    	trace.info ("Cargando fichero CSV por defecto (Recurso datos de test)");
	    	lines=buildIndicators.loadCSVFromResource ();
	    }
	    else{//Fichero pasado como argumento
	    	trace.info ("Cargando fichero CSV "+buildIndicators.csvInFile);
	    	lines=buildIndicators.loadCSVFromFile (buildIndicators.csvInFile);
	    }
	    
	    if (lines==null){
	    	System.exit (2);
	    }
	    
	    //Se construye la serie temporal de ticks para el intervalo de tiempo especificado, por defecto 300
		trace.info ("Calculando ticks agrupados cada "+buildIndicators.iTickTime+" segundos");
	    buildIndicators.series=BuildSeries.exec (lines,buildIndicators.iTickTime);
	    
	    trace.info ("Generando serie a partir del precio de cierre");
	    // Close price
	    buildIndicators.closePrice=new ClosePriceIndicator (buildIndicators.series);
	    trace.info ("Generando serie a partir del precio tipico");
	 	// Typical price
	    buildIndicators.typicalPrice=new TypicalPriceIndicator (buildIndicators.series);
	    trace.info ("Generando serie a partir de la variacion del precio");
	 	// Price variation
	    buildIndicators.priceVariation=new PriceVariationIndicator (buildIndicators.series);
	    trace.info ("Generando serie a partir del precio medio");
	    // Median price
	    buildIndicators.medianPrice=new MedianPriceIndicator (buildIndicators.series);
	    
	    if (!buildIndicators.parseIndicatorArgs (args)){
	    	System.exit (3);
	    }
	    
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
		Option emaIndOpt=null;
		Option ppoIndOpt=null;
		Option rocIndOpt=null;
		Option willIndOpt=null;
		Option fisherIndOpt=null;
		Option hmaIndOpt=null;
		Option raviIndOpt=null;
		
		
	
		
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
		awesomeIndOpt=new Option (AWESOME_SHORT_NAME,AWESOME_NAME,true,"Indicador Awesome. Usar -aws o --awesome, argumentos <timeFrameSma1>,<timeFrameSma2>,<Base de calculo (close,typical,variation,median)>"
		+ "\nEjemplo -aws 5,34,close ");
		awesomeIndOpt.setOptionalArg (true);
		awesomeIndOpt.setArgs (AWESOME_NUMBER_OF_ARGS);
		awesomeIndOpt.setValueSeparator (',');
		//MACD
		macdIndOpt=new Option (MACD_SHORT_NAME,MACD_NAME,true,"Indicador MACD. Usar -macd o --macd, argumentos <ShortTimeFrame>,<LongTimeFrame>,<Base de calculo (close,typical,variation,median)>");
		macdIndOpt.setOptionalArg (true);
		macdIndOpt.setArgs (MACD_NUMBER_OF_ARGS);
		macdIndOpt.setValueSeparator (',');
		//RSI
		rsiIndOpt=new Option (RSI_SHORT_NAME,RSI_NAME,true,"Indicador RSI. Usar -rsi o --rsi, argumentos <TimeFrame>,<Base de calculo (close,typical,variation,median)>");
		rsiIndOpt.setOptionalArg (true);
		rsiIndOpt.setArgs (RSI_NUMBER_OF_ARGS);
		rsiIndOpt.setValueSeparator (',');
		//EMA
		emaIndOpt=new Option (EMA_SHORT_NAME,EMA_NAME,true,"Indicador EMA. Usar -ema o --ema, argumentos <TimeFrame>,<Base de calculo (close,typical,variation,median)>");
		emaIndOpt.setOptionalArg (true);
		emaIndOpt.setArgs (EMA_NUMBER_OF_ARGS);
		emaIndOpt.setValueSeparator (',');
		//PPO
		ppoIndOpt=new Option (PPO_SHORT_NAME,PPO_NAME,true,"Indicador PPO. Usar -ppo o --ppo, argumentos <ShortTimeFrame>,<LongTimeFrame> <Base de calculo (close,typical,variation,median)>");
		ppoIndOpt.setOptionalArg (true);
		ppoIndOpt.setArgs (PPO_NUMBER_OF_ARGS);
		ppoIndOpt.setValueSeparator (',');
		//ROC
		rocIndOpt=new Option (ROC_SHORT_NAME,ROC_NAME,true,"Indicador ROC. Usar -roc o --roc, argumentos <TimeFrame>,<Base de calculo (close,typical,variation,median)>");
		rocIndOpt.setOptionalArg (true);
		rocIndOpt.setArgs (ROC_NUMBER_OF_ARGS);
		rocIndOpt.setValueSeparator (',');
		//WILLIANSR
		willIndOpt=new Option (WILL_SHORT_NAME,WILL_NAME,true,"Indicador WilliansR. Usar -will o --williansR, usa la serie original, argumentos <TimeFrame>,<Base de calculo (series)>");
		willIndOpt.setOptionalArg (true);
		willIndOpt.setArgs (WILL_NUMBER_OF_ARGS);
		willIndOpt.setValueSeparator (',');
		//FISHER
		fisherIndOpt=new Option (FISHER_SHORT_NAME,FISHER_NAME,true,"Indicador Fisher. Usar -fshr o --fisher, usa generalmente median, argumentos <TimeFrame>,<Base de calculo (close,typical,variation,median)>");
		fisherIndOpt.setOptionalArg (true);
		fisherIndOpt.setArgs (FISHER_NUMBER_OF_ARGS);
		fisherIndOpt.setValueSeparator (',');
		//HMA
		hmaIndOpt=new Option (HMA_SHORT_NAME,HMA_NAME,true,"Indicador HMA. Usar -hma o --hma, argumentos <TimeFrame>,<Base de calculo (close,typical,variation,median)>");
		hmaIndOpt.setOptionalArg (true);
		hmaIndOpt.setArgs (HMA_NUMBER_OF_ARGS);
		hmaIndOpt.setValueSeparator (',');		
		//RAVI
		raviIndOpt=new Option (RAVI_SHORT_NAME,RAVI_NAME,true,"Indicador RAVI. Usar -ravi o --ravi, argumentos <ShortSmaTimeFrame>,<LongSmaTimeFrame> <Base de calculo (close,typical,variation,median)>");
		raviIndOpt.setOptionalArg (true);
		raviIndOpt.setArgs (RAVI_NUMBER_OF_ARGS);
		raviIndOpt.setValueSeparator (',');

		
		

		
		
	    options=new Options ();

	    options.addOption (helpOpt);
	    
	    options.addOption (fileInOpt);
	    options.addOption (fileOutOpt);
	    
	    options.addOption (tickTimeOpt);
	    
	    options.addOption (awesomeIndOpt);
	    options.addOption (macdIndOpt);
	    options.addOption (rsiIndOpt);
	    options.addOption (emaIndOpt);
	    options.addOption (ppoIndOpt);
	    options.addOption (rocIndOpt);
	    options.addOption (willIndOpt);
	    options.addOption (fisherIndOpt);
	    options.addOption (hmaIndOpt);
	    options.addOption (raviIndOpt);
	    
	    
	  
	    cliParser=new GnuParser ();
	    try{
	    	//parse the command line arguments
	    	cliLine=cliParser.parse (options,args);
	    
	    	if (cliLine.hasOption ('h')) {
	            //automatically generate the help statement
	            formatter=new HelpFormatter ();
	            formatter.printHelp ("Usar indicadores.sh ",options,true);
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
	
	
	private boolean parseIndicatorArgs (String[] args)
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
			//EMA
			if (cliLine.hasOption (EMA_NAME)){
				IndicatorArgs=cliLine.getOptionValues (EMA_NAME);
				buildIndicatorInstance (EMAIndicator.class,EMA_NAME,IndicatorArgs,EMA_NUMBER_OF_ARGS);
			}
			//PPO
			if (cliLine.hasOption (PPO_NAME)){
				IndicatorArgs=cliLine.getOptionValues (PPO_NAME);
				buildIndicatorInstance (PPOIndicator.class,PPO_NAME,IndicatorArgs,PPO_NUMBER_OF_ARGS);
			}
			//ROC
			if (cliLine.hasOption (ROC_NAME)){
				IndicatorArgs=cliLine.getOptionValues (ROC_NAME);
				buildIndicatorInstance (ROCIndicator.class,ROC_NAME,IndicatorArgs,ROC_NUMBER_OF_ARGS);
			}
			//Willians R
			if (cliLine.hasOption (WILL_NAME)){
				IndicatorArgs=cliLine.getOptionValues (WILL_NAME);
				buildIndicatorInstance (WilliamsRIndicator.class,WILL_NAME,IndicatorArgs,WILL_NUMBER_OF_ARGS);
			}
			//Fisher
			if (cliLine.hasOption (FISHER_NAME)){
				IndicatorArgs=cliLine.getOptionValues (FISHER_NAME);
				buildIndicatorInstance (FisherIndicator.class,FISHER_NAME,IndicatorArgs,FISHER_NUMBER_OF_ARGS);
			}
			//HMAIndicator
			if (cliLine.hasOption (HMA_NAME)){
				IndicatorArgs=cliLine.getOptionValues (HMA_NAME);
				buildIndicatorInstance (HMAIndicator.class,HMA_NAME,IndicatorArgs,HMA_NUMBER_OF_ARGS);
			}
			//RAVI
			if (cliLine.hasOption (RAVI_NAME)){
				IndicatorArgs=cliLine.getOptionValues (RAVI_NAME);
				buildIndicatorInstance (RAVIIndicator.class,RAVI_NAME,IndicatorArgs,RAVI_NUMBER_OF_ARGS);
			}
		}
		catch (InternalErrorException e){
			trace.error ("Error instanciando indicadores ",e);
			return false;
		}
		
		return true;
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
    		throw new InternalErrorException ("El indicador "+name+" requiere "+nUnitArgs+" argumentos para cada instancia");
    	}
    	
    	constructorClassArgs=new Class<?>[nUnitArgs];    	
    	constructorArgs=new Object[nUnitArgs];
    	for (i=0;i<IndicatorArgs.length/nUnitArgs;i++){
    		
    		for (j=0;j<nUnitArgs-1;j++){
    			try{
    				constructorClassArgs[j+1]=int.class;
    				//constructorArgs[j+1]=Integer.parseInt (IndicatorArgs[(i*nUnitArgs)+j]);
    				iarg=Integer.parseInt (IndicatorArgs[(i*nUnitArgs)+j]);
    				constructorArgs[j+1]=iarg;
    			}
    			catch (NumberFormatException e){
    				throw new InternalErrorException ("Error el argumento "+IndicatorArgs[(i*nUnitArgs)]+" del indicador "+name+", no es un entero");
    			}
    		}
    		
    		//if ((baseCalc=BaseCalc.getBaseCalc (IndicatorArgs[(i*nUnitArgs)+(nUnitArgs-1)]))==null){
    		if ((baseCalc=BaseCalc.getBaseCalc (IndicatorArgs[(i*nUnitArgs)+j]))==null){
    			throw new InternalErrorException ("La base de calculo indicada para el indicador "+name+", es desconocida");
    		}
    		switch (baseCalc){
			case SERIES:
				constructorArgs[0]=series;
				constructorClassArgs[0]=TimeSeries.class;
				break;
			case CLOSE_PRICE:
				constructorArgs[0]=closePrice;
				constructorClassArgs[0]=Indicator.class;
				break;
			case TYPICAL_PRICE:
				constructorArgs[0]=typicalPrice;
				constructorClassArgs[0]=Indicator.class;
				break;
			case VARIATION_PRICE:
				constructorArgs[0]=priceVariation;
				constructorClassArgs[0]=Indicator.class;
				break;
			case MEDIAN_PRICE:
				constructorArgs[0]=medianPrice;
				constructorClassArgs[0]=Indicator.class;
				break;
			default:
				trace.error ("La base de calculo indicada para el indicador "+name+", es desconocida");
        		return;
    		}
    		
    		
    		//Se busca el constructor usando reflection
    		try{
    			constructor=clazz.getConstructor (constructorClassArgs);
    		}
    		catch (NoSuchMethodException e){
    			throw new InternalErrorException ("Error obteniendo el constructor del indicador "+name+" ("+e+")");
    		}
    		catch (SecurityException e){
    			throw new InternalErrorException ("Error obteniendo el constructor del indicador "+name+" ("+e+")");
    		}
    		
    		//Se invoca
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

	
	
	private void build (String csvOutFile)
	{
		int i;
		StringBuffer strBuff=null;
        PrintStream pos=null;
        
		
		

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
		        strBuff.append (series.getTick (i).getClosePrice ()).append(',');
		        strBuff.append (series.getTick (i).getOpenPrice ()).append(',');
		        strBuff.append (series.getTick (i).getMinPrice ()).append(',');
		        strBuff.append (series.getTick (i).getMaxPrice ()).append(',');
		        strBuff.append (series.getTick (i).getAmount ()).append(',');
		        strBuff.append (series.getTick(i).getEndTime().format (DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss"))).append(',');
		        //.append(typicalPrice.getValue(i)).append(',')
		        //.append(priceVariation.getValue(i)).append(',');
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
