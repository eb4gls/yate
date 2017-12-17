package com.yate.tatool;

public class IndicatorDef
{
	static public final int AWESOME_NUMBER_OF_ARGS=3;
	static public final String AWESOME_SHORT_NAME="aws";
	static public final String AWESOME_NAME="awesome";	
	static public final String AWESOME_DESCRIPTION="Indicador Awesome. Usar -aws o --awesome. argumentos: <timeFrameSma1>,<timeFrameSma2>,<Base de calculo (close,typical,variation,median) \nEjemplo -aws 5,34,close";
	
	static public final int MACD_NUMBER_OF_ARGS=3;
	static public final String MACD_SHORT_NAME="macd";
	static public final String MACD_NAME="macd";
	static public final String MACD_DESCRIPTION="Indicador MACD. Usar -macd o --macd. argumentos: <ShortTimeFrame>,<LongTimeFrame>,<Base de calculo (close,typical,variation,median)>";
	
	static public final int RSI_NUMBER_OF_ARGS=2;
	static public final String RSI_SHORT_NAME="rsi";
	static public final String RSI_NAME="rsi";
	static public final String RSI_DESCRIPTION="Indicador RSI. Usar -rsi o --rsi. argumentos: <TimeFrame>,<Base de calculo (close,typical,variation,median)>";
	
	static public final int EMA_NUMBER_OF_ARGS=2;
	static public final String EMA_SHORT_NAME="ema";
	static public final String EMA_NAME="exponentialmovingaverage";
	static public final String EMA_DESCRIPTION="Indicador EMA (Exponential Moving Average). Usar -ema o --exponentialmovingaverage. argumentos: <TimeFrame>,<Base de calculo (close,typical,variation,median)>";
	
	static public final int SMA_NUMBER_OF_ARGS=2;
	static public final String SMA_SHORT_NAME="sma";
	static public final String SMA_NAME="simplemovingaverag";
	static public final String SMA_DESCRIPTION="Indicador SMA (Simple Moving Average). Usar -ema o --simplemovingaverage. argumentos: <TimeFrame>,<Base de calculo (close,typical,variation,median)>";
	
	static public final int HMA_NUMBER_OF_ARGS=2;
	static public final String HMA_SHORT_NAME="hma";
	static public final String HMA_NAME="hullmovingaverage";
	static public final String HMA_DESCRIPTION="Indicador HMA (Hull Moving Average). Usar -hma o --hullmovingaverage. argumentos: <TimeFrame>,<Base de calculo (close,typical,variation,median)>";
	
	static public final int PPO_NUMBER_OF_ARGS=3;
	static public final String PPO_SHORT_NAME="ppo";
	static public final String PPO_NAME="ppo";
	static public final String PPO_DESCRIPTION="Indicador PPO. Usar -ppo o --ppo. argumentos: <ShortTimeFrame>,<LongTimeFrame> <Base de calculo (close,typical,variation,median)>";
	
	static public final int ROC_NUMBER_OF_ARGS=2;
	static public final String ROC_SHORT_NAME="roc";
	static public final String ROC_NAME="roc";
	static public final String ROC_DESCRIPTION="Indicador ROC. Usar -roc o --roc. argumentos: <TimeFrame>,<Base de calculo (close,typical,variation,median)>";
	
	static public final int WILL_NUMBER_OF_ARGS=2;
	static public final String WILL_SHORT_NAME="will";
	static public final String WILL_NAME="williansr";
	static public final String WILL_DESCRIPTION="Indicador WilliansR. Usar -will o --williansR. usa la serie original, argumentos: <TimeFrame>,<Base de calculo (series)>";
	
	static public final int FISHER_NUMBER_OF_ARGS=2;
	static public final String FISHER_SHORT_NAME="fshr";
	static public final String FISHER_NAME="fisher";
	static public final String FISHER_DESCRIPTION="Indicador Fisher. Usar -fshr o --fisher. usa generalmente median, argumentos: <TimeFrame>,<Base de calculo (close,typical,variation,median)>";
	
	static public final int RAVI_NUMBER_OF_ARGS=3;
	static public final String RAVI_SHORT_NAME="ravi";
	static public final String RAVI_NAME="ravi";
	static public final String RAVI_DESCRIPTION="Indicador RAVI. Usar -ravi o --ravi. argumentos: <ShortSmaTimeFrame>,<LongSmaTimeFrame> <Base de calculo (close,typical,variation,median)>";
	
	static public final int MFI_NUMBER_OF_ARGS=2;
	static public final String MFI_SHORT_NAME="mfi";
	static public final String MFI_NAME="moneyflowindicator";
	static public final String MFI_DESCRIPTION="Indicador MFI. Usar -mfi o --moneyflowindicator. argumentos: <TimeFrame>,<Base de calculo (series)>";
	
	static public final int BOLLINGERMID_NUMBER_OF_ARGS=2;
	static public final String BOLLINGERMID_NAME="bollingermid";
	static public final String BOLLINGERMID_SHORT_NAME="bom";
	static public final String BOLLINGERMID_DESCRIPTION="Indicador Bollinger Middle. Usar -bom o --bollingermid. Argumentos: <TimeFrame (se recomienda 20)> <Base de calculo (close,typical,variation,median)>";
	
	static public final int BOLLINGERUPPER_NUMBER_OF_ARGS=2;
	static public final String BOLLINGERUPPER_NAME="bollingerupper";
	static public final String BOLLINGERUPPER_SHORT_NAME="bou";
	static public final String BOLLINGERUPPER_DESCRIPTION="Indicador Bollinger Lower. Usar -bol o --bollingerlower. Argumentos: <TimeFrame (se recomienda 20)> <Base de calculo (close,typical,variation,median)>";
	
	static public final int BOLLINGERLOWER_NUMBER_OF_ARGS=2;
	static public final String BOLLINGERLOWER_NAME="bollingerlow";
	static public final String BOLLINGERLOWER_SHORT_NAME="bol";
	static public final String BOLLINGERLOWER_DESCRIPTION="Indicador Bollinger Upper. Usar -bou o --bollingerupper. Argumentos: <TimeFrame (se recomienda 20)> <Base de calculo (close,typical,variation,median)>";
	
	static public final int STOCHASTIC_K_NUMBER_OF_ARGS=2;
	static public final String STOCHASTIC_K_NAME="stochastick";
	static public final String STOCHASTIC_K_SHORT_NAME="stok";
	static public final String STOCHASTIC_K_DESCRIPTION="Indicador Estocastico K (Estocastico). Usar -stok o --stochastick. Argumentos: <TimeFrame> <Base de calculo (series)>";
	
	static public final int STOCHASTIC_RSI_NUMBER_OF_ARGS=2;
	static public final String STOCHASTIC_RSI_NAME="stochasticrsi";
	static public final String STOCHASTIC_RSI_SHORT_NAME="stor";
	static public final String STOCHASTIC_RSI_DESCRIPTION="Indicador Estocastico K (Estocastico). Usar -stok o --stochastick. Argumentos: <TimeFrame> <Base de calculo (series)>";
	
	static public final int POSITIVE_VOLUME_INDEX_NUMBER_OF_ARGS=1;
	static public final String POSITIVE_VOLUME_INDEX_NAME="positivevolumeindex";
	static public final String POSITIVE_VOLUME_INDEX_SHORT_NAME="pvi";
	static public final String POSITIVE_VOLUME_INDEX_DESCRIPTION="Indicador PVI (Positive Volume Index). Usar -"+POSITIVE_VOLUME_INDEX_SHORT_NAME+" o --"+POSITIVE_VOLUME_INDEX_NAME+". Argumentos: <TimeFrame> <Base de calculo (series)>";
	
	static public final int NEGATIVE_VOLUME_INDEX_NUMBER_OF_ARGS=1;
	static public final String NEGATIVE_VOLUME_INDEX_NAME="negativevolumeindex";
	static public final String NEGATIVE_VOLUME_INDEX_SHORT_NAME="nvi";
	static public final String NEGATIVE_VOLUME_INDEX_DESCRIPTION="Indicador NVI (Negative Volume Index). Usar -"+NEGATIVE_VOLUME_INDEX_SHORT_NAME+" o --"+NEGATIVE_VOLUME_INDEX_NAME+". Argumentos: <TimeFrame> <Base de calculo (series)>";
}
