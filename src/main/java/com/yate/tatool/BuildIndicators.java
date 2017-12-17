package com.yate.tatool;



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
import org.ta4j.core.Decimal;
import org.ta4j.core.Indicator;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.AwesomeOscillatorIndicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.FisherIndicator;
import org.ta4j.core.indicators.HMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.PPOIndicator;
import org.ta4j.core.indicators.RAVIIndicator;
import org.ta4j.core.indicators.ROCIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.StochasticOscillatorKIndicator;
import org.ta4j.core.indicators.StochasticRSIIndicator;
import org.ta4j.core.indicators.WilliamsRIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.MedianPriceIndicator;
import org.ta4j.core.indicators.helpers.PriceVariationIndicator;
import org.ta4j.core.indicators.helpers.TypicalPriceIndicator;
import org.ta4j.core.indicators.volume.NVIIndicator;
import org.ta4j.core.indicators.volume.PVIIndicator;

import com.opencsv.CSVReader;
import com.yate.indicadores.Exceptions.InternalErrorException;
import com.yate.ta4j.indicadores.MFIIndicator;
import com.yate.ta4j.indicadores.MyBollingerBandsLowerIndicator;
import com.yate.ta4j.indicadores.MyBollingerBandsMiddleIndicator;
import com.yate.ta4j.indicadores.MyBollingerBandsUpperIndicator;





public class BuildIndicators
{
	private static Logger trace=Logger.getLogger (BuildIndicators.class);
	
	static private final String INTERNAL_RESOURCE="ibex_2017_07_03.csv";
	
	static private final char DEFAULT_SEPARATOR=',';
	
	
	
	
	
	//Argumentos de la linea de comando
	private String csvInFile=null;
	private String csvOutFile=null;
	private String tickTime=null;
	private final int defaultTickTime=300;//300 segundos
	private int iTickTime=defaultTickTime;
	private boolean verbose=false;
	
	private boolean tickOpenPrice=false;
	private boolean tickClosePrice=false;
	private boolean tickMinPrice=false;
	private boolean tickMaxPrice=false;
	private boolean tickSize=false;
	private boolean tickVolume=false;
	private boolean tradePerSecond=false;
	private boolean volumePerTrade=false;
	private TimeFormat timeFormat=null;
	
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
	    buildIndicators.series=BuildSeries.exec (lines,buildIndicators.iTickTime,buildIndicators.verbose);
	    
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
	
	
	
	private Option addIndicatorToArgs (String shortName,String name,String desc,int argc)
	{
		Option opt=null;
		
		opt=new Option (shortName,name,true,desc);
		opt.setOptionalArg (true);
		opt.setArgs (argc);
		opt.setValueSeparator (',');
		
		return opt;
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
		Option verboseOpt=null;
		
		Option tickOpenPriceOpt=null;
		Option tickClosePriceOpt=null;
		Option tickMinPriceOpt=null;
		Option tickMaxPriceOpt=null;
		Option tickSizeOpt=null;
		Option tradesPerSecondOpt=null;
		Option tickVolumeOpt=null;
		Option VolumePerTradeOpt=null;
		Option timeFormatOpt=null;
		
		Option awesomeIndOpt=null;
		Option macdIndOpt=null;
		Option rsiIndOpt=null;
		Option emaIndOpt=null;
		Option smaIndOpt=null;
		Option ppoIndOpt=null;
		Option rocIndOpt=null;
		Option willIndOpt=null;
		Option fisherIndOpt=null;
		Option hmaIndOpt=null;
		Option raviIndOpt=null;
		Option mfiIndOpt=null;
		Option bollMidIndOpt=null;
		Option bollUpperIndOpt=null;
		Option bollLowerIndOpt=null;
		Option stochsaticKIndOpt=null;
		Option stochsaticRSIIndOpt=null;
		Option pviIndOpt=null;
		Option nviIndOpt=null;
		
		
	
		
		//Ayuda
		helpOpt=new Option ("h","Imprime la ayuda");
		//fichero entrada
		fileInOpt=new Option ("fin",true,"Fichero CSV a cargar, si no se indica se cargara el fichero interno");
		fileInOpt.setOptionalArg (true);
		//fichero de salida o stdout
		fileOutOpt=new Option ("fout",true,"Fichero CSV a crear, si no se indica se usa la salida estandar");
		fileOutOpt.setOptionalArg (true);
		//Verbose
		verboseOpt=new Option ("v","verbose",false,"Muestra el progreso en la creacion de los ticks");
		//Tiempo del tick
		tickTimeOpt=new Option ("ttime",true,"Tiempo con el que son creados los ticks expreado en segundos, si no se indica se toma por defecto 300 segundos");
		tickTimeOpt.setOptionalArg (true);
		tickTimeOpt.setType (Integer.class);
		
		tickOpenPriceOpt=new Option ("tcp","tickcloseprice",false,"Precio de cierre del Tick");
		tickClosePriceOpt=new Option ("top","tickopenprice",false,"Precio de apertura del Tick");
		tickMinPriceOpt=new Option ("tmp","tickminprice",false,"Precio minimo del Tick");
		tickMaxPriceOpt=new Option ("txp","tickmaxprice",false,"Precio maximo del Tick");
		
		tickSizeOpt=new Option ("ts","ticksize",false,"Vuelca el numero de trades que contiene cada tick");
		tradesPerSecondOpt=new Option ("tps","tradepersecond",false,"Vuelca el numero de trades por segundo en el Tick calculado");
		tickVolumeOpt=new Option ("tv","tickvolume",false,"Vuelca el volumen acumulado para el tick");
		VolumePerTradeOpt=new Option ("vpt","volumepertrade",false,"Vuelca el volumen medio por trade en el tick calculado");
		timeFormatOpt=new Option ("tf","timeformat",true,"Vuelca el tiempo en el formato indicado (Format o Epoch)");
		timeFormatOpt.setArgs (1);
		timeFormatOpt.setValueSeparator (',');
	
		//Indicadores
		//Awesome, 3 argumentos: p1, p2, [close price, variation price, typical price]
		awesomeIndOpt=addIndicatorToArgs (IndicatorDef.AWESOME_SHORT_NAME,IndicatorDef.AWESOME_NAME,IndicatorDef.AWESOME_DESCRIPTION,IndicatorDef.AWESOME_NUMBER_OF_ARGS);
		//MACD
		macdIndOpt=addIndicatorToArgs (IndicatorDef.MACD_SHORT_NAME,IndicatorDef.MACD_NAME,IndicatorDef.MACD_DESCRIPTION,IndicatorDef.MACD_NUMBER_OF_ARGS);
		//RSI
		rsiIndOpt=addIndicatorToArgs (IndicatorDef.RSI_SHORT_NAME,IndicatorDef.RSI_NAME,IndicatorDef.RSI_DESCRIPTION,IndicatorDef.EMA_NUMBER_OF_ARGS);
		//EMA - Exponentical Moving Average
		emaIndOpt=addIndicatorToArgs (IndicatorDef.EMA_SHORT_NAME,IndicatorDef.EMA_NAME,IndicatorDef.EMA_DESCRIPTION,IndicatorDef.EMA_NUMBER_OF_ARGS);
		//SMA - Simple Moving Average
		smaIndOpt=addIndicatorToArgs (IndicatorDef.SMA_SHORT_NAME,IndicatorDef.SMA_NAME,IndicatorDef.SMA_DESCRIPTION,IndicatorDef.SMA_NUMBER_OF_ARGS);
		//HMA
		hmaIndOpt=addIndicatorToArgs (IndicatorDef.HMA_SHORT_NAME,IndicatorDef.HMA_NAME,IndicatorDef.HMA_DESCRIPTION,IndicatorDef.HMA_NUMBER_OF_ARGS);
		//PPO
		ppoIndOpt=addIndicatorToArgs (IndicatorDef.PPO_SHORT_NAME,IndicatorDef.PPO_NAME,IndicatorDef.PPO_DESCRIPTION,IndicatorDef.PPO_NUMBER_OF_ARGS);
		//ROC
		rocIndOpt=addIndicatorToArgs (IndicatorDef.ROC_SHORT_NAME,IndicatorDef.ROC_NAME,IndicatorDef.ROC_DESCRIPTION,IndicatorDef.ROC_NUMBER_OF_ARGS);
		//WILLIANSR
		willIndOpt=addIndicatorToArgs (IndicatorDef.WILL_SHORT_NAME,IndicatorDef.WILL_NAME,IndicatorDef.WILL_DESCRIPTION,IndicatorDef.WILL_NUMBER_OF_ARGS);
		//FISHER
		fisherIndOpt=addIndicatorToArgs (IndicatorDef.FISHER_SHORT_NAME,IndicatorDef.FISHER_NAME,IndicatorDef.FISHER_DESCRIPTION,IndicatorDef.FISHER_NUMBER_OF_ARGS);
		//RAVI
		raviIndOpt=addIndicatorToArgs (IndicatorDef.RAVI_SHORT_NAME,IndicatorDef.RAVI_NAME,IndicatorDef.RAVI_DESCRIPTION,IndicatorDef.RAVI_NUMBER_OF_ARGS);
		//MFI
		mfiIndOpt=addIndicatorToArgs (IndicatorDef.MFI_SHORT_NAME,IndicatorDef.MFI_NAME,IndicatorDef.MFI_DESCRIPTION,IndicatorDef.MFI_NUMBER_OF_ARGS);
		//BollingerMiddle
		bollMidIndOpt=addIndicatorToArgs (IndicatorDef.BOLLINGERMID_SHORT_NAME,IndicatorDef.BOLLINGERMID_NAME,IndicatorDef.BOLLINGERMID_DESCRIPTION,IndicatorDef.BOLLINGERMID_NUMBER_OF_ARGS);
		//BollingerUpper
		bollUpperIndOpt=addIndicatorToArgs (IndicatorDef.BOLLINGERLOWER_SHORT_NAME,IndicatorDef.BOLLINGERLOWER_NAME,IndicatorDef.BOLLINGERLOWER_DESCRIPTION,IndicatorDef.BOLLINGERLOWER_NUMBER_OF_ARGS);
		//BollingerLower
		bollLowerIndOpt=addIndicatorToArgs (IndicatorDef.BOLLINGERUPPER_SHORT_NAME,IndicatorDef.BOLLINGERUPPER_NAME,IndicatorDef.BOLLINGERUPPER_DESCRIPTION,IndicatorDef.BOLLINGERUPPER_NUMBER_OF_ARGS);
		//StochasticK
		stochsaticKIndOpt=addIndicatorToArgs (IndicatorDef.STOCHASTIC_K_SHORT_NAME,IndicatorDef.STOCHASTIC_K_NAME,IndicatorDef.STOCHASTIC_K_DESCRIPTION,IndicatorDef.STOCHASTIC_K_NUMBER_OF_ARGS);
		//StochasticRSI
		stochsaticRSIIndOpt=addIndicatorToArgs (IndicatorDef.STOCHASTIC_RSI_SHORT_NAME,IndicatorDef.STOCHASTIC_RSI_NAME,IndicatorDef.STOCHASTIC_RSI_DESCRIPTION,IndicatorDef.STOCHASTIC_RSI_NUMBER_OF_ARGS);
		//PVI - Positive Value Index
		pviIndOpt=addIndicatorToArgs (IndicatorDef.POSITIVE_VOLUME_INDEX_SHORT_NAME,IndicatorDef.POSITIVE_VOLUME_INDEX_NAME,IndicatorDef.POSITIVE_VOLUME_INDEX_DESCRIPTION,IndicatorDef.POSITIVE_VOLUME_INDEX_NUMBER_OF_ARGS);
		//NVI - Negative Value Index
		nviIndOpt=addIndicatorToArgs (IndicatorDef.NEGATIVE_VOLUME_INDEX_SHORT_NAME,IndicatorDef.NEGATIVE_VOLUME_INDEX_NAME,IndicatorDef.NEGATIVE_VOLUME_INDEX_DESCRIPTION,IndicatorDef.NEGATIVE_VOLUME_INDEX_NUMBER_OF_ARGS);
		
	    options=new Options ();

	    options.addOption (helpOpt);
	    
	    options.addOption (fileInOpt);
	    options.addOption (fileOutOpt);
	    options.addOption (tickTimeOpt);
	    options.addOption (verboseOpt);
	    
	    options.addOption (tickOpenPriceOpt);
	    options.addOption (tickClosePriceOpt);
	    options.addOption (tickMinPriceOpt);
	    options.addOption (tickMaxPriceOpt);
	    options.addOption (tickSizeOpt);
	    options.addOption (tradesPerSecondOpt);
	    options.addOption (tickVolumeOpt);
	    options.addOption (VolumePerTradeOpt);
	    options.addOption (timeFormatOpt);
	    
	    options.addOption (awesomeIndOpt);
	    options.addOption (macdIndOpt);
	    options.addOption (rsiIndOpt);
	    options.addOption (emaIndOpt);
	    options.addOption (smaIndOpt);
	    options.addOption (ppoIndOpt);
	    options.addOption (rocIndOpt);
	    options.addOption (willIndOpt);
	    options.addOption (fisherIndOpt);
	    options.addOption (hmaIndOpt);
	    options.addOption (raviIndOpt);
	    options.addOption (mfiIndOpt);
	    options.addOption (bollMidIndOpt);
	    options.addOption (bollLowerIndOpt);
	    options.addOption (bollUpperIndOpt);
	    options.addOption (stochsaticKIndOpt);
	    options.addOption (stochsaticRSIIndOpt);
	    options.addOption (pviIndOpt);
	    options.addOption (nviIndOpt);
	    
	  
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
	    	
	    	if (cliLine.hasOption ('v')){
	    		verbose=true;
	    	}
	    	
	    	//Precio de apertura del tick
			if (cliLine.hasOption ("top")){
				tickOpenPrice=true;
			}
			//Precio de cierre del tick
			if (cliLine.hasOption ("tcp")){
				tickClosePrice=true;
			}
			//Precio minimo del tick
			if (cliLine.hasOption ("tmp")){
				tickMinPrice=true;
			}
			//Precio maximo del tick
			if (cliLine.hasOption ("txp")){
				tickMaxPrice=true;
			}
			
			//Tamaño del tick en trades
			if (cliLine.hasOption ("ts")){
				tickSize=true;
			}
			//Trades por segundo
			if (cliLine.hasOption ("tps")){
				tradePerSecond=true;
			}
			
			//Volumen acumulado para el tick
			if (cliLine.hasOption ("tv")){
				tickVolume=true;
			}
			//Volumen por trade, volumen medio en el tick
			if (cliLine.hasOption ("vpt")){
				volumePerTrade=true;
			}
			
			//Formato del tiempo
			if (cliLine.hasOption ("tf")){
				timeFormat=TimeFormat.getTimeFormat (cliLine.getOptionValues ("tf")[0]);
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
			if (cliLine.hasOption (IndicatorDef.AWESOME_NAME)){
				IndicatorArgs=cliLine.getOptionValues (IndicatorDef.AWESOME_NAME);
				buildIndicatorInstance (AwesomeOscillatorIndicator.class,IndicatorDef.AWESOME_NAME,IndicatorArgs,IndicatorDef.AWESOME_NUMBER_OF_ARGS);
			}
			//RSI
			if (cliLine.hasOption (IndicatorDef.RSI_NAME)){
				IndicatorArgs=cliLine.getOptionValues (IndicatorDef.RSI_NAME);
				buildIndicatorInstance (RSIIndicator.class,IndicatorDef.RSI_NAME,IndicatorArgs,IndicatorDef.RSI_NUMBER_OF_ARGS);
			}
			//MACD
			if (cliLine.hasOption (IndicatorDef.MACD_NAME)){
				IndicatorArgs=cliLine.getOptionValues (IndicatorDef.MACD_NAME);
				buildIndicatorInstance (MACDIndicator.class,IndicatorDef.MACD_NAME,IndicatorArgs,IndicatorDef.MACD_NUMBER_OF_ARGS);
			}
			//EMA - Exponentical Moving Average
			if (cliLine.hasOption (IndicatorDef.EMA_NAME)){
				IndicatorArgs=cliLine.getOptionValues (IndicatorDef.EMA_NAME);
				buildIndicatorInstance (EMAIndicator.class,IndicatorDef.EMA_NAME,IndicatorArgs,IndicatorDef.EMA_NUMBER_OF_ARGS);
			}
			//SMA - Simple Moving Average
			if (cliLine.hasOption (IndicatorDef.SMA_NAME)){
				IndicatorArgs=cliLine.getOptionValues (IndicatorDef.SMA_NAME);
				buildIndicatorInstance (SMAIndicator.class,IndicatorDef.SMA_NAME,IndicatorArgs,IndicatorDef.SMA_NUMBER_OF_ARGS);
			}
			//PPO
			if (cliLine.hasOption (IndicatorDef.PPO_NAME)){
				IndicatorArgs=cliLine.getOptionValues (IndicatorDef.PPO_NAME);
				buildIndicatorInstance (PPOIndicator.class,IndicatorDef.PPO_NAME,IndicatorArgs,IndicatorDef.PPO_NUMBER_OF_ARGS);
			}
			//ROC
			if (cliLine.hasOption (IndicatorDef.ROC_NAME)){
				IndicatorArgs=cliLine.getOptionValues (IndicatorDef.ROC_NAME);
				buildIndicatorInstance (ROCIndicator.class,IndicatorDef.ROC_NAME,IndicatorArgs,IndicatorDef.ROC_NUMBER_OF_ARGS);
			}
			//Willians R
			if (cliLine.hasOption (IndicatorDef.WILL_NAME)){
				IndicatorArgs=cliLine.getOptionValues (IndicatorDef.WILL_NAME);
				buildIndicatorInstance (WilliamsRIndicator.class,IndicatorDef.WILL_NAME,IndicatorArgs,IndicatorDef.WILL_NUMBER_OF_ARGS);
			}
			//Fisher
			if (cliLine.hasOption (IndicatorDef.FISHER_NAME)){
				IndicatorArgs=cliLine.getOptionValues (IndicatorDef.FISHER_NAME);
				buildIndicatorInstance (FisherIndicator.class,IndicatorDef.FISHER_NAME,IndicatorArgs,IndicatorDef.FISHER_NUMBER_OF_ARGS);
			}
			//HMAIndicator
			if (cliLine.hasOption (IndicatorDef.HMA_NAME)){
				IndicatorArgs=cliLine.getOptionValues (IndicatorDef.HMA_NAME);
				buildIndicatorInstance (HMAIndicator.class,IndicatorDef.HMA_NAME,IndicatorArgs,IndicatorDef.HMA_NUMBER_OF_ARGS);
			}
			//RAVI
			if (cliLine.hasOption (IndicatorDef.RAVI_NAME)){
				IndicatorArgs=cliLine.getOptionValues (IndicatorDef.RAVI_NAME);
				buildIndicatorInstance (RAVIIndicator.class,IndicatorDef.RAVI_NAME,IndicatorArgs,IndicatorDef.RAVI_NUMBER_OF_ARGS);
			}
			//MFI - Money Flow Index
			if (cliLine.hasOption (IndicatorDef.MFI_NAME)){
				IndicatorArgs=cliLine.getOptionValues (IndicatorDef.MFI_NAME);
				buildIndicatorInstance (MFIIndicator.class,IndicatorDef.MFI_NAME,IndicatorArgs,IndicatorDef.MFI_NUMBER_OF_ARGS);
			}
			//BollingerMid
			if (cliLine.hasOption (IndicatorDef.BOLLINGERMID_NAME)){
				IndicatorArgs=cliLine.getOptionValues (IndicatorDef.BOLLINGERMID_NAME);
				buildIndicatorInstance (MyBollingerBandsMiddleIndicator.class,IndicatorDef.BOLLINGERMID_NAME,IndicatorArgs,IndicatorDef.BOLLINGERMID_NUMBER_OF_ARGS);
			}
			//BollingerLower
			if (cliLine.hasOption (IndicatorDef.BOLLINGERLOWER_NAME)){
				IndicatorArgs=cliLine.getOptionValues (IndicatorDef.BOLLINGERLOWER_NAME);
				buildIndicatorInstance (MyBollingerBandsLowerIndicator.class,IndicatorDef.BOLLINGERLOWER_NAME,IndicatorArgs,IndicatorDef.BOLLINGERLOWER_NUMBER_OF_ARGS);
			}
			//BollingerUpper
			if (cliLine.hasOption (IndicatorDef.BOLLINGERUPPER_NAME)){
				IndicatorArgs=cliLine.getOptionValues (IndicatorDef.BOLLINGERUPPER_NAME);
				buildIndicatorInstance (MyBollingerBandsUpperIndicator.class,IndicatorDef.BOLLINGERUPPER_NAME,IndicatorArgs,IndicatorDef.BOLLINGERUPPER_NUMBER_OF_ARGS);
			}
			//Stochastic K - Estocastico
			if (cliLine.hasOption (IndicatorDef.STOCHASTIC_K_NAME)){
				IndicatorArgs=cliLine.getOptionValues (IndicatorDef.STOCHASTIC_K_NAME);
				buildIndicatorInstance (StochasticOscillatorKIndicator.class,IndicatorDef.STOCHASTIC_K_NAME,IndicatorArgs,IndicatorDef.STOCHASTIC_K_NUMBER_OF_ARGS);
			}
			//Stochastic RSI
			if (cliLine.hasOption (IndicatorDef.STOCHASTIC_RSI_NAME)){
				IndicatorArgs=cliLine.getOptionValues (IndicatorDef.STOCHASTIC_RSI_NAME);
				buildIndicatorInstance (StochasticRSIIndicator.class,IndicatorDef.STOCHASTIC_RSI_NAME,IndicatorArgs,IndicatorDef.STOCHASTIC_RSI_NUMBER_OF_ARGS);
			}			
			//PVI - Positive Volume Index
			if (cliLine.hasOption (IndicatorDef.POSITIVE_VOLUME_INDEX_NAME)){
				IndicatorArgs=cliLine.getOptionValues (IndicatorDef.POSITIVE_VOLUME_INDEX_NAME);
				buildIndicatorInstance (PVIIndicator.class,IndicatorDef.POSITIVE_VOLUME_INDEX_NAME,IndicatorArgs,IndicatorDef.POSITIVE_VOLUME_INDEX_NUMBER_OF_ARGS);
			}
			//NVI - Negative Volume Index
			if (cliLine.hasOption (IndicatorDef.NEGATIVE_VOLUME_INDEX_NAME)){
				IndicatorArgs=cliLine.getOptionValues (IndicatorDef.NEGATIVE_VOLUME_INDEX_NAME);
				buildIndicatorInstance (NVIIndicator.class,IndicatorDef.NEGATIVE_VOLUME_INDEX_NAME,IndicatorArgs,IndicatorDef.NEGATIVE_VOLUME_INDEX_NUMBER_OF_ARGS);
			}

		}
		catch (InternalErrorException e){
			trace.error ("Error instanciando indicadores ",e);
			return false;
		}
		
		return true;
	}

	
	
	
	//private <I extends CachedIndicator<Decimal>> boolean buildIndicatorInstance (Class<I> clazz,String name,String []IndicatorArgs,int nUnitArgs)
	/**
	 * Instancia indicadores que proveen un constructor de la forma: indicador del tipo cachedIndicator, y una serie de argumentos int.
	 * 
	 * @param clazz
	 * @param name
	 * @param IndicatorArgs
	 * @param nUnitArgs
	 * @throws InternalErrorException
	 */
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
		        if (tickOpenPrice){
		        	strBuff.append (series.getTick (i).getOpenPrice ()).append(',');	
		        }
		    	if (tickClosePrice){
		    		strBuff.append (series.getTick (i).getClosePrice ()).append(',');	
		    	}
		    	if (tickMinPrice){
		    		strBuff.append (series.getTick (i).getMinPrice ()).append(',');	
		    	}
		    	if (tickMaxPrice){
		    		strBuff.append (series.getTick (i).getMaxPrice ()).append(',');	
		    	}
		        if (tickSize){
		        	strBuff.append (series.getTick (i).getTrades ()).append(',');
		        }
		        if (tradePerSecond){
		        	strBuff.append ((series.getTick (i).getTrades ()/iTickTime)).append(',');
		        }
		        if (tickVolume){
		        	strBuff.append (series.getTick (i).getAmount ()).append(',');
		        }
		    	if (volumePerTrade){
		    		strBuff.append (series.getTick (i).getAmount ().dividedBy (Decimal.valueOf (series.getTick (i).getTrades ()))).append(',');
		    	}
		        if (timeFormat!=null){
		        	switch (timeFormat){
					case EPOCH:
						strBuff.append (series.getTick(i).getEndTime().toEpochSecond ()).append(',');
						break;
					case FORMAT:
						strBuff.append (series.getTick(i).getEndTime().format (DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss"))).append(',');
						break;
					default:
						break;
		        	
		        	}
		        }
		        //Indicadores configurados
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
