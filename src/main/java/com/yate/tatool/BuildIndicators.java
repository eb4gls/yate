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
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.MedianPriceIndicator;
import org.ta4j.core.indicators.helpers.PriceVariationIndicator;
import org.ta4j.core.indicators.helpers.TypicalPriceIndicator;

import com.opencsv.CSVReader;
import com.yate.indicadores.Exceptions.InternalErrorException;





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
	private boolean header=false;
	
	private boolean tickOpenPrice=false;
	private boolean tickClosePrice=false;
	private boolean tickMinPrice=false;
	private boolean tickMaxPrice=false;
	private boolean tickSize=false;
	private boolean tickVolume=false;
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
		Option headerOpt=null;
		
		Option tickOpenPriceOpt=null;
		Option tickClosePriceOpt=null;
		Option tickMinPriceOpt=null;
		Option tickMaxPriceOpt=null;
		Option tickSizeOpt=null;
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
		Option stochsaticDIndOpt=null;
		Option stochsaticRSIIndOpt=null;
		Option pviIndOpt=null;
		Option nviIndOpt=null;
		Option mvwapIndOpt=null;
		Option vwapIndOpt=null;
		Option dailyTimeIndOpt=null;
		Option weeklyTimeIndOpt=null;
		Option monthlyTimeIndOpt=null;
		Option yearlyTimeIndOpt=null;
		
	
		
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
		//Header
		headerOpt=new Option ("hd","header",false,"Incluye una cabecera con el nombre de cada campo");
		//Tiempo del tick
		tickTimeOpt=new Option ("ttime",true,"Tiempo con el que son creados los ticks expreado en segundos, si no se indica se toma por defecto 300 segundos");
		tickTimeOpt.setOptionalArg (true);
		tickTimeOpt.setType (Integer.class);
	
		tickOpenPriceOpt=new Option (ElementFieldDef.TICKCLOSEPRICE_SHORT_NAME,ElementFieldDef.TICKCLOSEPRICE_NAME,false,ElementFieldDef.TICKCLOSEPRICE_DESCRIPTION);
		tickClosePriceOpt=new Option (ElementFieldDef.TICKOPENPRICE_SHORT_NAME,ElementFieldDef.TICKOPENPRICE_NAME,false,ElementFieldDef.TICKOPENPRICE_DESCRIPTION);
		tickMinPriceOpt=new Option (ElementFieldDef.TICKMINPRICE_SHORT_NAME,ElementFieldDef.TICKMINPRICE_NAME,false,ElementFieldDef.TICKMINPRICE_DESCRIPTION);
		tickMaxPriceOpt=new Option (ElementFieldDef.TICKMAXPRICE_SHORT_NAME,ElementFieldDef.TICKMAXPRICE_NAME,false,ElementFieldDef.TICKMAXPRICE_DESCRIPTION);
		
		tickSizeOpt=new Option (ElementFieldDef.TICKSIZE_SHORT_NAME,ElementFieldDef.TICKSIZE_NAME,false,ElementFieldDef.TICKSIZE_DESCRIPTION);
		tickVolumeOpt=new Option (ElementFieldDef.TICKVOLUME_SHORT_NAME,ElementFieldDef.TICKVOLUME_NAME,false,ElementFieldDef.TICKVOLUME_DESCRIPTION);
		VolumePerTradeOpt=new Option (ElementFieldDef.VOLUMEPERTRADE_SHORT_NAME,ElementFieldDef.VOLUMEPERTRADE_NAME,false,ElementFieldDef.VOLUMEPERTRADE_DESCRIPTION);
		timeFormatOpt=new Option (ElementFieldDef.TIME_SHORT_NAME,ElementFieldDef.TIME_NAME,true,ElementFieldDef.TIME_DESCRIPTION);
		timeFormatOpt.setArgs (ElementFieldDef.TIME_NUMBER_OF_ARGS);
		//timeFormatOpt.setValueSeparator (',');
	
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
		//Stochastic K
		stochsaticKIndOpt=addIndicatorToArgs (IndicatorDef.STOCHASTIC_K_SHORT_NAME,IndicatorDef.STOCHASTIC_K_NAME,IndicatorDef.STOCHASTIC_K_DESCRIPTION,IndicatorDef.STOCHASTIC_K_NUMBER_OF_ARGS);
		//Stochastic D
		stochsaticDIndOpt=addIndicatorToArgs (IndicatorDef.STOCHASTIC_D_SHORT_NAME,IndicatorDef.STOCHASTIC_D_NAME,IndicatorDef.STOCHASTIC_D_DESCRIPTION,IndicatorDef.STOCHASTIC_D_NUMBER_OF_ARGS);
		//StochasticRSI
		stochsaticRSIIndOpt=addIndicatorToArgs (IndicatorDef.STOCHASTIC_RSI_SHORT_NAME,IndicatorDef.STOCHASTIC_RSI_NAME,IndicatorDef.STOCHASTIC_RSI_DESCRIPTION,IndicatorDef.STOCHASTIC_RSI_NUMBER_OF_ARGS);
		//PVI - Positive Value Index
		pviIndOpt=addIndicatorToArgs (IndicatorDef.POSITIVE_VOLUME_INDEX_SHORT_NAME,IndicatorDef.POSITIVE_VOLUME_INDEX_NAME,IndicatorDef.POSITIVE_VOLUME_INDEX_DESCRIPTION,IndicatorDef.POSITIVE_VOLUME_INDEX_NUMBER_OF_ARGS);
		//NVI - Negative Value Index
		nviIndOpt=addIndicatorToArgs (IndicatorDef.NEGATIVE_VOLUME_INDEX_SHORT_NAME,IndicatorDef.NEGATIVE_VOLUME_INDEX_NAME,IndicatorDef.NEGATIVE_VOLUME_INDEX_DESCRIPTION,IndicatorDef.NEGATIVE_VOLUME_INDEX_NUMBER_OF_ARGS);
		//MVWAP Moving Volume Weighted Average Price
		mvwapIndOpt=addIndicatorToArgs (IndicatorDef.MVWAP_SHORT_NAME,IndicatorDef.MVWAP_NAME,IndicatorDef.MVWAP_DESCRIPTION,IndicatorDef.MVWAP_NUMBER_OF_ARGS);
		//VWAP Volume Weighted Average Price
		vwapIndOpt=addIndicatorToArgs (IndicatorDef.VWAP_SHORT_NAME,IndicatorDef.VWAP_NAME,IndicatorDef.VWAP_DESCRIPTION,IndicatorDef.VWAP_NUMBER_OF_ARGS);				
		//Daily Time
		dailyTimeIndOpt=addIndicatorToArgs (IndicatorDef.DAILYTIME_SHORT_NAME,IndicatorDef.DAILYTIME_NAME,IndicatorDef.DAILYTIME_DESCRIPTION,IndicatorDef.DAILYTIME_NUMBER_OF_ARGS);
		//Weekly Time
		weeklyTimeIndOpt=addIndicatorToArgs (IndicatorDef.WEEKLYTIME_SHORT_NAME,IndicatorDef.WEEKLYTIME_NAME,IndicatorDef.WEEKLYTIME_DESCRIPTION,IndicatorDef.WEEKLYTIME_NUMBER_OF_ARGS);
		//Monthly Time
		monthlyTimeIndOpt=addIndicatorToArgs (IndicatorDef.MONTHLYTIME_SHORT_NAME,IndicatorDef.MONTHLYTIME_NAME,IndicatorDef.MONTHLYTIME_DESCRIPTION,IndicatorDef.MONTHLYTIME_NUMBER_OF_ARGS);
		//Daily Time
		yearlyTimeIndOpt=addIndicatorToArgs (IndicatorDef.YEARLYTIME_SHORT_NAME,IndicatorDef.YEARLYTIME_NAME,IndicatorDef.YEARLYTIME_DESCRIPTION,IndicatorDef.YEARLYTIME_NUMBER_OF_ARGS);


		
	    options=new Options ();

	    options.addOption (helpOpt);
	    
	    options.addOption (fileInOpt);
	    options.addOption (fileOutOpt);
	    options.addOption (tickTimeOpt);
	    options.addOption (verboseOpt);
	    options.addOption (headerOpt);
	    
	    options.addOption (tickOpenPriceOpt);
	    options.addOption (tickClosePriceOpt);
	    options.addOption (tickMinPriceOpt);
	    options.addOption (tickMaxPriceOpt);
	    options.addOption (tickSizeOpt);
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
	    options.addOption (stochsaticDIndOpt);
	    options.addOption (stochsaticRSIIndOpt);
	    options.addOption (pviIndOpt);
	    options.addOption (nviIndOpt);
	    options.addOption (mvwapIndOpt);
	    options.addOption (vwapIndOpt);
	    options.addOption (dailyTimeIndOpt);
	    options.addOption (weeklyTimeIndOpt);
	    options.addOption (monthlyTimeIndOpt);
	    options.addOption (yearlyTimeIndOpt);
	    
	    
	  
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
	    	
	    	if (cliLine.hasOption ("hd")){
	    		header=true;
	    	}
	    	
	    	//Precio de apertura del tick
			if (cliLine.hasOption (ElementFieldDef.TICKOPENPRICE_SHORT_NAME)){
				tickOpenPrice=true;
			}
			//Precio de cierre del tick
			if (cliLine.hasOption (ElementFieldDef.TICKCLOSEPRICE_SHORT_NAME)){
				tickClosePrice=true;
			}
			//Precio minimo del tick
			if (cliLine.hasOption (ElementFieldDef.TICKMINPRICE_SHORT_NAME)){
				tickMinPrice=true;
			}
			//Precio maximo del tick
			if (cliLine.hasOption (ElementFieldDef.TICKMAXPRICE_SHORT_NAME)){
				tickMaxPrice=true;
			}
			
			//Tamaño del tick en trades
			if (cliLine.hasOption (ElementFieldDef.TICKSIZE_SHORT_NAME)){
				tickSize=true;
			}
			
			//Volumen acumulado para el tick
			if (cliLine.hasOption (ElementFieldDef.TICKVOLUME_SHORT_NAME)){
				tickVolume=true;
			}
			//Volumen por trade, volumen medio en el tick
			if (cliLine.hasOption (ElementFieldDef.VOLUMEPERTRADE_SHORT_NAME)){
				volumePerTrade=true;
			}
			
			//Formato del tiempo
			if (cliLine.hasOption (ElementFieldDef.TIME_SHORT_NAME)){
				timeFormat=TimeFormat.getTimeFormat (cliLine.getOptionValues (ElementFieldDef.TIME_SHORT_NAME)[0]);
			}
	    }	    
	    catch (ParseException e){
            trace.error ("Error analizando linea de comandos",e);
            return false;
        }
	    
	    return true;
	}
	
	
	private void addIndicatorToExecution (String name,int argc,Class<?> clazz)
	throws InternalErrorException
	{
		String []IndicatorArgs=null;
		
		if (cliLine.hasOption (name)){
			IndicatorArgs=cliLine.getOptionValues (name);
			if ((argc!=0)&&(IndicatorArgs==null)){
				throw new InternalErrorException ("El indicador "+name+" necesita argumentos que no se han suministrado");
			}
			buildIndicatorInstance (clazz,name,IndicatorArgs,argc);
		}
	}
	
	
	private boolean parseIndicatorArgs (String[] args)
	{
		//Indicadores
		indicators=new ArrayList<IndicatorArgs> ();

		try{
			//AWESOME
			addIndicatorToExecution (IndicatorDef.AWESOME_NAME,IndicatorDef.AWESOME_NUMBER_OF_ARGS,IndicatorDef.AWESOME_CLASS);
			//MACD
			addIndicatorToExecution (IndicatorDef.MACD_NAME,IndicatorDef.MACD_NUMBER_OF_ARGS,IndicatorDef.MACD_CLASS);
			//RSI
			addIndicatorToExecution (IndicatorDef.RSI_NAME,IndicatorDef.RSI_NUMBER_OF_ARGS,IndicatorDef.RSI_CLASS);
			//EMA - Exponentical Moving Average
			addIndicatorToExecution (IndicatorDef.EMA_NAME,IndicatorDef.EMA_NUMBER_OF_ARGS,IndicatorDef.EMA_CLASS);
			//SMA - Simple Moving Average
			addIndicatorToExecution (IndicatorDef.SMA_NAME,IndicatorDef.SMA_NUMBER_OF_ARGS,IndicatorDef.SMA_CLASS);
			//HMA - Hull Moving Average
			addIndicatorToExecution (IndicatorDef.HMA_NAME,IndicatorDef.HMA_NUMBER_OF_ARGS,IndicatorDef.HMA_CLASS);
			//PPO
			addIndicatorToExecution (IndicatorDef.PPO_NAME,IndicatorDef.PPO_NUMBER_OF_ARGS,IndicatorDef.PPO_CLASS);
			//ROC
			addIndicatorToExecution (IndicatorDef.ROC_NAME,IndicatorDef.ROC_NUMBER_OF_ARGS,IndicatorDef.ROC_CLASS);
			//Willians R
			addIndicatorToExecution (IndicatorDef.WILL_NAME,IndicatorDef.WILL_NUMBER_OF_ARGS,IndicatorDef.WILL_CLASS);
			//Fisher
			addIndicatorToExecution (IndicatorDef.FISHER_NAME,IndicatorDef.FISHER_NUMBER_OF_ARGS,IndicatorDef.FISHER_CLASS);
			//RAVI
			addIndicatorToExecution (IndicatorDef.RAVI_NAME,IndicatorDef.RAVI_NUMBER_OF_ARGS,IndicatorDef.RAVI_CLASS);
			//MFI - Money Flow Index
			addIndicatorToExecution (IndicatorDef.MFI_NAME,IndicatorDef.MFI_NUMBER_OF_ARGS,IndicatorDef.MFI_CLASS);
			//BollingerMid
			addIndicatorToExecution (IndicatorDef.BOLLINGERMID_NAME,IndicatorDef.BOLLINGERMID_NUMBER_OF_ARGS,IndicatorDef.BOLLINGERMID_CLASS);
			//BollingerLower
			addIndicatorToExecution (IndicatorDef.BOLLINGERLOWER_NAME,IndicatorDef.BOLLINGERLOWER_NUMBER_OF_ARGS,IndicatorDef.BOLLINGERLOWER_CLASS);
			//BollingerUpper
			addIndicatorToExecution (IndicatorDef.BOLLINGERUPPER_NAME,IndicatorDef.BOLLINGERUPPER_NUMBER_OF_ARGS,IndicatorDef.BOLLINGERUPPER_CLASS);
			//Stochastic K 
			addIndicatorToExecution (IndicatorDef.STOCHASTIC_K_NAME,IndicatorDef.STOCHASTIC_K_NUMBER_OF_ARGS,IndicatorDef.STOCHASTICK_K_CLASS);
			//Stochastic D 
			addIndicatorToExecution (IndicatorDef.STOCHASTIC_D_NAME,IndicatorDef.STOCHASTIC_D_NUMBER_OF_ARGS,IndicatorDef.STOCHASTICK_D_CLASS);
			//Stochastic RSI
			addIndicatorToExecution (IndicatorDef.STOCHASTIC_RSI_NAME,IndicatorDef.STOCHASTIC_RSI_NUMBER_OF_ARGS,IndicatorDef.STOCHASTICK_RSI_CLASS);
			//PVI - Positive Volume Index
			addIndicatorToExecution (IndicatorDef.POSITIVE_VOLUME_INDEX_NAME,IndicatorDef.POSITIVE_VOLUME_INDEX_NUMBER_OF_ARGS,IndicatorDef.POSITIVE_VOLUME_INDEX_CLASS);
			//NVI - Negative Volume Index
			addIndicatorToExecution (IndicatorDef.NEGATIVE_VOLUME_INDEX_NAME,IndicatorDef.NEGATIVE_VOLUME_INDEX_NUMBER_OF_ARGS,IndicatorDef.NEGATIVE_VOLUME_INDEX_CLASS);
			//MVWAP Moving Volume Weighted Average Price
			addIndicatorToExecution (IndicatorDef.MVWAP_NAME,IndicatorDef.MVWAP_NUMBER_OF_ARGS,IndicatorDef.MVWAP_CLASS);
			//VWAP Volume Weighted Average Price
			addIndicatorToExecution (IndicatorDef.VWAP_NAME,IndicatorDef.VWAP_NUMBER_OF_ARGS,IndicatorDef.VWAP_CLASS);
			//Daily Time
			addIndicatorToExecution (IndicatorDef.DAILYTIME_NAME,IndicatorDef.DAILYTIME_NUMBER_OF_ARGS,IndicatorDef.DAILYTIME_CLASS);			
			//Weekly Time
			addIndicatorToExecution (IndicatorDef.WEEKLYTIME_NAME,IndicatorDef.WEEKLYTIME_NUMBER_OF_ARGS,IndicatorDef.WEEKLYTIME_CLASS);
			//Monthly Time
			addIndicatorToExecution (IndicatorDef.MONTHLYTIME_NAME,IndicatorDef.MONTHLYTIME_NUMBER_OF_ARGS,IndicatorDef.MONTHLYTIME_CLASS);
			//Yearly Time
			addIndicatorToExecution (IndicatorDef.YEARLYTIME_NAME,IndicatorDef.YEARLYTIME_NUMBER_OF_ARGS,IndicatorDef.YEARLYTIME_CLASS);


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
		int j;
		String aux=null;
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
			
			//cabecera
			if (header){
				strBuff=new StringBuffer ();
				 
			    if (tickClosePrice){
		    		strBuff.append (ElementFieldDef.TICKCLOSEPRICE_NAME+",");	
		    	}
		        if (tickOpenPrice){
		        	strBuff.append (ElementFieldDef.TICKOPENPRICE_NAME+",");	
		        }
		    	if (tickMinPrice){
		    		strBuff.append (ElementFieldDef.TICKMINPRICE_NAME+",");	
		    	}
		    	if (tickMaxPrice){
		    		strBuff.append (ElementFieldDef.TICKMAXPRICE_NAME+",");	
		    	}
		        if (tickSize){
		        	strBuff.append (ElementFieldDef.TICKSIZE_NAME+",");
		        }
		        if (tickVolume){
		        	strBuff.append (ElementFieldDef.TICKVOLUME_NAME+",");
		        }
		    	if (volumePerTrade){
		    		strBuff.append (ElementFieldDef.VOLUMEPERTRADE_NAME+",");
		    	}
		    	if (timeFormat!=null){
		    		strBuff.append (ElementFieldDef.TIME_NAME+",");
		    	}
		    	
		    	//Indicadores configurados
		    	for (IndicatorArgs item:indicators){
		    		strBuff.append (item.getName ()).append(',');
				}
		    	
		    	strBuff.deleteCharAt (strBuff.length ()-1);
		    	
		    	pos.println (strBuff);
			}
			
			trace.info ("Procesando los indicadores sobre "+series.getTickCount ()+" Ticks de "+iTickTime+" segundos");
	   
			aux=" ";
			System.out.print (aux);
			
	        for (i=0;i<series.getTickCount ();i++){
		        strBuff=new StringBuffer ();
		      
		        if (tickClosePrice){
		    		strBuff.append (series.getTick (i).getClosePrice ()).append(',');	
		    	}
		        if (tickOpenPrice){
		        	strBuff.append (series.getTick (i).getOpenPrice ()).append(',');	
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
		        
		        
		    	if (verbose){
					if ((i%20)==0){
						for (j=0;j<aux.length ();j++){
							System.out.print ("\b");
						}
						aux=String.format ("Procesando %d de %d Ticks",i,series.getTickCount ());
						System.out.print (aux);
					}
				}
	        }
	    	for (j=0;verbose&&aux!=null&&j<aux.length ();j++){
				System.out.print ("\b");
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
