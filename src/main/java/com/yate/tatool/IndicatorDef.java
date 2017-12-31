package com.yate.tatool;

import org.ta4j.core.indicators.AwesomeOscillatorIndicator;
import org.ta4j.core.indicators.CoppockCurveIndicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.FisherIndicator;
import org.ta4j.core.indicators.HMAIndicator;
import org.ta4j.core.indicators.KAMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.PPOIndicator;
import org.ta4j.core.indicators.ParabolicSarIndicator;
import org.ta4j.core.indicators.RAVIIndicator;
import org.ta4j.core.indicators.ROCIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.StochasticOscillatorKIndicator;
import org.ta4j.core.indicators.StochasticRSIIndicator;
import org.ta4j.core.indicators.WMAIndicator;
import org.ta4j.core.indicators.WilliamsRIndicator;
import org.ta4j.core.indicators.adx.AverageDirectionalMovementIndicator;
import org.ta4j.core.indicators.adx.DirectionalMovementMinusIndicator;
import org.ta4j.core.indicators.adx.DirectionalMovementPlusIndicator;
import org.ta4j.core.indicators.ichimoku.IchimokuSenkouSpanAIndicator;
import org.ta4j.core.indicators.volume.ChaikinMoneyFlowIndicator;
import org.ta4j.core.indicators.volume.NVIIndicator;
import org.ta4j.core.indicators.volume.PVIIndicator;
import org.ta4j.core.indicators.volume.VWAPIndicator;

import com.yate.ta4j.indicadores.DailyTimeIndicator;
import com.yate.ta4j.indicadores.DeMarkIndicatorTick;
import com.yate.ta4j.indicadores.DeMarkRevIndicatorTickResistance;
import com.yate.ta4j.indicadores.DeMarkRevIndicatorTickSupport;
import com.yate.ta4j.indicadores.FibonacciRevIndicatorF1SuppTick;
import com.yate.ta4j.indicadores.MFIIndicator;
import com.yate.ta4j.indicadores.MonthlyTimeIndicator;
import com.yate.ta4j.indicadores.MyBollingerBandsLowerIndicator;
import com.yate.ta4j.indicadores.MyBollingerBandsMiddleIndicator;
import com.yate.ta4j.indicadores.MyBollingerBandsUpperIndicator;
import com.yate.ta4j.indicadores.MyMVWAPIndicator;
import com.yate.ta4j.indicadores.MyStochasticOscillatorDIndicator;
import com.yate.ta4j.indicadores.WeeklyTimeIndicator;
import com.yate.ta4j.indicadores.YearlyTimeIndicator;

public class IndicatorDef
{
	static public final int AWESOME_NUMBER_OF_ARGS=3;
	static public final String AWESOME_SHORT_NAME="aws";
	static public final String AWESOME_NAME="awesome";	
	static public final String AWESOME_DESCRIPTION="Indicador Awesome. Usar -aws o --awesome. argumentos: <timeFrameSma1>,<timeFrameSma2>,<Base de calculo (close,typical,variation,median) \nEjemplo -aws 5,34,close";
	static public final Class<?> AWESOME_CLASS=AwesomeOscillatorIndicator.class;
	
	static public final int MACD_NUMBER_OF_ARGS=3;
	static public final String MACD_SHORT_NAME="macd";
	static public final String MACD_NAME="macd";
	static public final String MACD_DESCRIPTION="Indicador MACD. Usar -macd o --macd. argumentos: <ShortTimeFrame>,<LongTimeFrame>,<Base de calculo (close,typical,variation,median)>";
	static public final Class<?> MACD_CLASS=MACDIndicator.class;
	
	static public final int RSI_NUMBER_OF_ARGS=2;
	static public final String RSI_SHORT_NAME="rsi";
	static public final String RSI_NAME="rsi";
	static public final String RSI_DESCRIPTION="Indicador RSI. Usar -rsi o --rsi. argumentos: <TimeFrame>,<Base de calculo (close,typical,variation,median)>";
	static public final Class<?> RSI_CLASS=RSIIndicator.class;
	
	static public final int EMA_NUMBER_OF_ARGS=2;
	static public final String EMA_SHORT_NAME="ema";
	static public final String EMA_NAME="exponentialmovingaverage";
	static public final String EMA_DESCRIPTION="Indicador EMA (Exponential Moving Average). Usar -ema o --exponentialmovingaverage. argumentos: <TimeFrame>,<Base de calculo (close,typical,variation,median)>";
	static public final Class<?> EMA_CLASS=EMAIndicator.class;
	
	static public final int SMA_NUMBER_OF_ARGS=2;
	static public final String SMA_SHORT_NAME="sma";
	static public final String SMA_NAME="simplemovingaverag";
	static public final String SMA_DESCRIPTION="Indicador SMA (Simple Moving Average). Usar -ema o --simplemovingaverage. argumentos: <TimeFrame>,<Base de calculo (close,typical,variation,median)>";
	static public final Class<?> SMA_CLASS=SMAIndicator.class;
	
	static public final int HMA_NUMBER_OF_ARGS=2;
	static public final String HMA_SHORT_NAME="hma";
	static public final String HMA_NAME="hullmovingaverage";
	static public final String HMA_DESCRIPTION="Indicador HMA (Hull Moving Average). Usar -hma o --hullmovingaverage. argumentos: <TimeFrame>,<Base de calculo (close,typical,variation,median)>";
	static public final Class<?> HMA_CLASS=HMAIndicator.class;
	
	static public final int WMA_NUMBER_OF_ARGS=2;
	static public final String WMA_SHORT_NAME="wma";
	static public final String WMA_NAME="weigtedhmovingaverage";
	static public final String WMA_DESCRIPTION="Indicador WMA (Weighted Moving Average). Usar -wma o --weihhtedmovingaverage. argumentos: <TimeFrame>,<Base de calculo (close,typical,variation,median)>";
	static public final Class<?> WMA_CLASS=WMAIndicator.class;
	
	static public final int PPO_NUMBER_OF_ARGS=3;
	static public final String PPO_SHORT_NAME="ppo";
	static public final String PPO_NAME="ppo";
	static public final String PPO_DESCRIPTION="Indicador PPO. Usar -ppo o --ppo. argumentos: <ShortTimeFrame>,<LongTimeFrame> <Base de calculo (close,typical,variation,median)>";
	static public final Class<?> PPO_CLASS=PPOIndicator.class;
	
	static public final int ROC_NUMBER_OF_ARGS=2;
	static public final String ROC_SHORT_NAME="roc";
	static public final String ROC_NAME="roc";
	static public final String ROC_DESCRIPTION="Indicador ROC. Usar -roc o --roc. argumentos: <TimeFrame>,<Base de calculo (close,typical,variation,median)>";
	static public final Class<?> ROC_CLASS=ROCIndicator.class;
	
	static public final int WILL_NUMBER_OF_ARGS=2;
	static public final String WILL_SHORT_NAME="will";
	static public final String WILL_NAME="williansr";
	static public final String WILL_DESCRIPTION="Indicador WilliansR. Usar -will o --williansR. usa la serie original, argumentos: <TimeFrame>,<Base de calculo (series)>";
	static public final Class<?> WILL_CLASS=WilliamsRIndicator.class;
	
	static public final int FISHER_NUMBER_OF_ARGS=2;
	static public final String FISHER_SHORT_NAME="fshr";
	static public final String FISHER_NAME="fisher";
	static public final String FISHER_DESCRIPTION="Indicador Fisher. Usar -fshr o --fisher. usa generalmente median, argumentos: <TimeFrame>,<Base de calculo (close,typical,variation,median)>";
	static public final Class<?> FISHER_CLASS=FisherIndicator.class;
	
	static public final int RAVI_NUMBER_OF_ARGS=3;
	static public final String RAVI_SHORT_NAME="ravi";
	static public final String RAVI_NAME="ravi";
	static public final String RAVI_DESCRIPTION="Indicador RAVI. Usar -ravi o --ravi. argumentos: <ShortSmaTimeFrame>,<LongSmaTimeFrame> <Base de calculo (close,typical,variation,median)>";
	static public final Class<?> RAVI_CLASS=RAVIIndicator.class;
	
	static public final int MFI_NUMBER_OF_ARGS=2;
	static public final String MFI_SHORT_NAME="mfi";
	static public final String MFI_NAME="moneyflowindicator";
	static public final String MFI_DESCRIPTION="Indicador MFI. Usar -mfi o --moneyflowindicator. argumentos: <TimeFrame>,<Base de calculo (series)>";
	static public final Class<?> MFI_CLASS=MFIIndicator.class;
	
	static public final int BOLLINGERMID_NUMBER_OF_ARGS=2;
	static public final String BOLLINGERMID_NAME="bollingermid";
	static public final String BOLLINGERMID_SHORT_NAME="bom";
	static public final String BOLLINGERMID_DESCRIPTION="Indicador Bollinger Middle. Usar -bom o --bollingermid. Argumentos: <TimeFrame (se recomienda 20)> <Base de calculo (close,typical,variation,median)>";
	static public final Class<?> BOLLINGERMID_CLASS=MyBollingerBandsMiddleIndicator.class;
	
	static public final int BOLLINGERUPPER_NUMBER_OF_ARGS=2;
	static public final String BOLLINGERUPPER_NAME="bollingerupper";
	static public final String BOLLINGERUPPER_SHORT_NAME="bou";
	static public final String BOLLINGERUPPER_DESCRIPTION="Indicador Bollinger Lower. Usar -bol o --bollingerlower. Argumentos: <TimeFrame (se recomienda 20)> <Base de calculo (close,typical,variation,median)>";
	static public final Class<?> BOLLINGERUPPER_CLASS=MyBollingerBandsLowerIndicator.class;
	
	static public final int BOLLINGERLOWER_NUMBER_OF_ARGS=2;
	static public final String BOLLINGERLOWER_NAME="bollingerlow";
	static public final String BOLLINGERLOWER_SHORT_NAME="bol";
	static public final String BOLLINGERLOWER_DESCRIPTION="Indicador Bollinger Upper. Usar -bou o --bollingerupper. Argumentos: <TimeFrame (se recomienda 20)> <Base de calculo (close,typical,variation,median)>";
	static public final Class<?> BOLLINGERLOWER_CLASS=MyBollingerBandsUpperIndicator.class;
	
	static public final int STOCHASTIC_K_NUMBER_OF_ARGS=2;
	static public final String STOCHASTIC_K_NAME="stochastick";
	static public final String STOCHASTIC_K_SHORT_NAME="stok";
	static public final String STOCHASTIC_K_DESCRIPTION="Indicador Estocastico K. Usar -stok o --stochastick. Argumentos: <TimeFrame> <Base de calculo (series)>";
	static public final Class<?> STOCHASTICK_K_CLASS=StochasticOscillatorKIndicator.class;
	
	static public final int STOCHASTIC_D_NUMBER_OF_ARGS=2;
	static public final String STOCHASTIC_D_NAME="stochasticd";
	static public final String STOCHASTIC_D_SHORT_NAME="stod";
	static public final String STOCHASTIC_D_DESCRIPTION="Indicador Estocastico D. Usar -stod o --stochasticd. Argumentos: <TimeFrame> <Base de calculo (series)>";
	static public final Class<?> STOCHASTICK_D_CLASS=MyStochasticOscillatorDIndicator.class;
	
	static public final int STOCHASTIC_RSI_NUMBER_OF_ARGS=2;
	static public final String STOCHASTIC_RSI_NAME="stochasticrsi";
	static public final String STOCHASTIC_RSI_SHORT_NAME="stor";
	static public final String STOCHASTIC_RSI_DESCRIPTION="Indicador Estocastico RSI. Usar -stor o --stochasticrsi. Argumentos: <TimeFrame> <Base de calculo (series)>";
	static public final Class<?> STOCHASTICK_RSI_CLASS=StochasticRSIIndicator.class;
	
	static public final int POSITIVE_VOLUME_INDEX_NUMBER_OF_ARGS=1;
	static public final String POSITIVE_VOLUME_INDEX_NAME="positivevolumeindex";
	static public final String POSITIVE_VOLUME_INDEX_SHORT_NAME="pvi";
	static public final String POSITIVE_VOLUME_INDEX_DESCRIPTION="Indicador PVI (Positive Volume Index). Usar -"+POSITIVE_VOLUME_INDEX_SHORT_NAME+" o --"+POSITIVE_VOLUME_INDEX_NAME+". Argumentos: <TimeFrame> <Base de calculo (series)>";
	static public final Class<?> POSITIVE_VOLUME_INDEX_CLASS=PVIIndicator.class;
	
	static public final int NEGATIVE_VOLUME_INDEX_NUMBER_OF_ARGS=1;
	static public final String NEGATIVE_VOLUME_INDEX_NAME="negativevolumeindex";
	static public final String NEGATIVE_VOLUME_INDEX_SHORT_NAME="nvi";
	static public final String NEGATIVE_VOLUME_INDEX_DESCRIPTION="Indicador NVI (Negative Volume Index). Usar -"+NEGATIVE_VOLUME_INDEX_SHORT_NAME+" o --"+NEGATIVE_VOLUME_INDEX_NAME+". Argumentos: <TimeFrame> <Base de calculo (series)>";
	static public final Class<?> NEGATIVE_VOLUME_INDEX_CLASS=NVIIndicator.class;
	
	static public final int MVWAP_NUMBER_OF_ARGS=2;
	static public final String MVWAP_NAME="movingvolumeweightedaverageprice";
	static public final String MVWAP_SHORT_NAME="mvwap";
	static public final String MVWAP_DESCRIPTION="Indicador MVWAP (Moving volume weighted average price). Usar -"+MVWAP_SHORT_NAME+" o --"+MVWAP_NAME+". Argumentos: <TimeFrame> <Base de calculo (series)>";
	static public final Class<?> MVWAP_CLASS=MyMVWAPIndicator.class;
	
	static public final int VWAP_NUMBER_OF_ARGS=2;
	static public final String VWAP_NAME="volumeweightedaverageprice";
	static public final String VWAP_SHORT_NAME="vwap";
	static public final String VWAP_DESCRIPTION="Indicador VWAP (volume-weighted average price). Usar -"+MVWAP_SHORT_NAME+" o --"+MVWAP_NAME+". Argumentos: <TimeFrame> <Base de calculo (series)>";
	static public final Class<?> VWAP_CLASS=VWAPIndicator.class;
	
	static public final int DAILYTIME_NUMBER_OF_ARGS=1;
	static public final String DAILYTIME_NAME="dailytime";
	static public final String DAILYTIME_SHORT_NAME="dt";
	static public final String DAILYTIME_DESCRIPTION="Indicador Daily Time. Usar -"+DAILYTIME_SHORT_NAME+" o --"+DAILYTIME_NAME+". Argumentos: <Base de calculo (series)>";
	static public final Class<?> DAILYTIME_CLASS=DailyTimeIndicator.class;
	
	static public final int WEEKLYTIME_NUMBER_OF_ARGS=1;
	static public final String WEEKLYTIME_NAME="weeklytime";
	static public final String WEEKLYTIME_SHORT_NAME="wt";
	static public final String WEEKLYTIME_DESCRIPTION="Indicador Weekly Time. Usar -"+WEEKLYTIME_SHORT_NAME+" o --"+WEEKLYTIME_NAME+". Argumentos: <Base de calculo (series)>";
	static public final Class<?> WEEKLYTIME_CLASS=WeeklyTimeIndicator.class;
	
	static public final int MONTHLYTIME_NUMBER_OF_ARGS=1;
	static public final String MONTHLYTIME_NAME="monthlytime";
	static public final String MONTHLYTIME_SHORT_NAME="mt";
	static public final String MONTHLYTIME_DESCRIPTION="Indicador Monthly Time. Usar -"+MONTHLYTIME_SHORT_NAME+" o --"+MONTHLYTIME_NAME+". Argumentos: <Base de calculo (series)>";
	static public final Class<?> MONTHLYTIME_CLASS=MonthlyTimeIndicator.class;
	
	static public final int YEARLYTIME_NUMBER_OF_ARGS=1;
	static public final String YEARLYTIME_NAME="yearlytime";
	static public final String YEARLYTIME_SHORT_NAME="yt";
	static public final String YEARLYTIME_DESCRIPTION="Indicador Yearly Time. Usar -"+YEARLYTIME_SHORT_NAME+" o --"+YEARLYTIME_NAME+". Argumentos: <Base de calculo (series)>";
	static public final Class<?> YEARLYTIME_CLASS=YearlyTimeIndicator.class;
	
	static public final int COPPOCKCURVE_NUMBER_OF_ARGS=1;
	static public final String COPPOCKCURVE_NAME="coppockcurve";
	static public final String COPPOCKCURVE_SHORT_NAME="cpp";
	static public final String COPPOCKCURVE_DESCRIPTION="Indicador Coppock Curve. Usar -"+COPPOCKCURVE_SHORT_NAME+" o --"+COPPOCKCURVE_NAME+". Argumentos: <Base de calculo (close,typical,variation,median)>";
	static public final Class<?> COPPOCKCURVE_CLASS=CoppockCurveIndicator.class;
	
	static public final int PARABOLICSAR_NUMBER_OF_ARGS=1;
	static public final String PARABOLICSAR_NAME="parabolicsar";
	static public final String PARABOLICSAR_SHORT_NAME="psar";
	static public final String PARABOLICSAR_DESCRIPTION="Indicador Parabolic Sar. Usar -"+PARABOLICSAR_SHORT_NAME+" o --"+PARABOLICSAR_NAME+". Argumentos: <Base de calculo (series)>";
	static public final Class<?> PARABOLICSAR_CLASS=ParabolicSarIndicator.class;
	
	static public final int KAMA_NUMBER_OF_ARGS=4;
	static public final String KAMA_NAME="kama";
	static public final String KAMA_SHORT_NAME="kma";
	static public final String KAMA_DESCRIPTION="Indicador Kama. Usar -"+KAMA_SHORT_NAME+" o --"+KAMA_NAME+". Argumentos: timeFrameEffectiveRatio, timeFrameFast,timeFrameSlow (close,typical,variation,median)> \nEjemplo -kma 10,2,30,close";
	static public final Class<?> KAMA_CLASS=KAMAIndicator.class;
	
	static public final int DEMARKPIVOTTICK_NUMBER_OF_ARGS=1;
	static public final String DEMARKPIVOTTICK_NAME="demarkpivottick";
	static public final String DEMARKPIVOTTICK_SHORT_NAME="dmt";
	static public final String DEMARKPIVOTTICK_DESCRIPTION="Indicador DeMark Pivot Point Tick. Usar -"+DEMARKPIVOTTICK_SHORT_NAME+" o --"+DEMARKPIVOTTICK_NAME+". Argumentos: <Base de calculo (series)>";
	static public final Class<?> DEMARKPIVOTTICK_CLASS=DeMarkIndicatorTick.class;
	
	static public final int DEMARKREVPIVOTTICKREST_NUMBER_OF_ARGS=1;
	static public final String DEMARKREVPIVOTTICKREST_NAME="demarkrevpivottickrest";
	static public final String DEMARKREVPIVOTTICKREST_SHORT_NAME="dmrtr";
	static public final String DEMARKREVPIVOTTICKREST_DESCRIPTION="Indicador DeMark Reversal Pivot Point Tick Resistance. Usar -"+DEMARKREVPIVOTTICKREST_SHORT_NAME+" o --"+DEMARKREVPIVOTTICKREST_NAME+". Argumentos: <Base de calculo (series)>";
	static public final Class<?> DEMARKREVPIVOTTICKREST_CLASS=DeMarkRevIndicatorTickResistance.class;
	
	static public final int DEMARKREVPIVOTTICKSUPP_NUMBER_OF_ARGS=1;
	static public final String DEMARKREVPIVOTTICKSUPP_NAME="demarkrevpivotticksupp";
	static public final String DEMARKREVPIVOTTICKSUPP_SHORT_NAME="dmrts";
	static public final String DEMARKREVPIVOTTICKSUPP_DESCRIPTION="Indicador DeMark Reversal Pivot Point Tick Support. Usar -"+DEMARKREVPIVOTTICKSUPP_SHORT_NAME+" o --"+DEMARKREVPIVOTTICKSUPP_NAME+". Argumentos: <Base de calculo (series)>";
	static public final Class<?> DEMARKREVPIVOTTICKSUPP_CLASS=DeMarkRevIndicatorTickSupport.class;
	
	static public final int FIBONACIREVF1ST_NUMBER_OF_ARGS=1;
	static public final String FIBONACIREVF1ST_NAME="fibonaccirevf1supptick";
	static public final String FIBONACIREVF1ST_SHORT_NAME="frf1st";
	static public final String FIBONACIREVF1ST_DESCRIPTION="Indicador Fibonacci Reversal Factor 1 Tick Support. Usar -"+DEMARKREVPIVOTTICKSUPP_SHORT_NAME+" o --"+DEMARKREVPIVOTTICKSUPP_NAME+". Argumentos: <Base de calculo (series)>";
	static public final Class<?> FIBONACIREVF1ST_CLASS=FibonacciRevIndicatorF1SuppTick.class;
	
	static public final int AVGDIRMOV_NUMBER_OF_ARGS=2;
	static public final String AVGDIRMOV_NAME="avgdirmov";
	static public final String AVGDIRMOV_SHORT_NAME="adm";
	static public final String AVGDIRMOV_DESCRIPTION="Indicador Average Directional Movement Indicator. Usar -"+AVGDIRMOV_SHORT_NAME+" o --"+AVGDIRMOV_NAME+". Argumentos: timeframe,<Base de calculo (series)>";
	static public final Class<?> AVGDIRMOV_CLASS=AverageDirectionalMovementIndicator.class;
	
	static public final int DIRMOVMINUS_NUMBER_OF_ARGS=2;
	static public final String DIRMOVMINUS_NAME="dirmovminus";
	static public final String DIRMOVMINUS_SHORT_NAME="dmm";
	static public final String DIRMOVMINUS_DESCRIPTION="Indicador Directional Movement Minus Indicator. Usar -"+DIRMOVMINUS_SHORT_NAME+" o --"+DIRMOVMINUS_NAME+". Argumentos: timeframe,<Base de calculo (series)>";
	static public final Class<?> DIRMOVMINUS_CLASS=DirectionalMovementMinusIndicator.class;
	
	static public final int DIRMOVPLUS_NUMBER_OF_ARGS=2;
	static public final String DIRMOVPLUS_NAME="dirmovplus";
	static public final String DIRMOVPLUS_SHORT_NAME="dmp";
	static public final String DIRMOVPLUS_DESCRIPTION="Indicador Directional Movement Plus Indicator. Usar -"+DIRMOVPLUS_SHORT_NAME+" o --"+DIRMOVPLUS_NAME+". Argumentos: timeframe,<Base de calculo (series)>";
	static public final Class<?> DIRMOVPLUS_CLASS=DirectionalMovementPlusIndicator.class;
	
	static public final int ICHIMOKUSA_NUMBER_OF_ARGS=3;
	static public final String ICHIMOKUSA_NAME="ichimokusenkoua";
	static public final String ICHIMOKUSA_SHORT_NAME="isa";
	static public final String ICHIMOKUSA_DESCRIPTION="Indicador Ichimoku Senkou Indicator. Usar -"+ICHIMOKUSA_SHORT_NAME+" o --"+ICHIMOKUSA_NAME+". Argumentos: timeFrameConversionLine,timeFrameBaseLine,<Base de calculo (series)>\n Ejmplo -isa 9,26,series";
	static public final Class<?> ICHIMOKUSA_CLASS=IchimokuSenkouSpanAIndicator.class;

	static public final int CHAIKINMFI_NUMBER_OF_ARGS=2;
	static public final String CHAIKINMFI_NAME="chaikinmoneyflow";
	static public final String CHAIKINMFI_SHORT_NAME="cmf";
	static public final String CHAIKINMFI_DESCRIPTION="Indicador Chaikin Money Flow Indicator. Usar -"+CHAIKINMFI_SHORT_NAME+" o --"+CHAIKINMFI_NAME+". Argumentos: timeFrame,<Base de calculo (series)>\n Ejmplo -cmf 14,series";
	static public final Class<?> CHAIKINMFI_CLASS=ChaikinMoneyFlowIndicator.class;
}
