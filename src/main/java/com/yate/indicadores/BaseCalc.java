package com.yate.indicadores;

public enum BaseCalc
{
	SERIES ("Series",1),
	CLOSE_PRICE ("Close",2),
	TYPICAL_PRICE ("Typical",3),
	VARIATION_PRICE ("Variation",4),
	MEDIAN_PRICE ("Median",5);
	
				
	private String baseCalc;
	private int idBase;

	private BaseCalc (String base,int id)
	{
		baseCalc=base;
		idBase=id;
	}

	public String getBaseCalcName ()
	{
		return baseCalc;
	}
	
	public int getIdBaseCalc ()
	{
		return idBase;
	}

	static public BaseCalc getBaseCalc (String base)
	{
		if (base.equalsIgnoreCase (BaseCalc.SERIES.getBaseCalcName ())){
			return SERIES;
		}
		else{
			if (base.equalsIgnoreCase (BaseCalc.CLOSE_PRICE.getBaseCalcName ())){
				return CLOSE_PRICE;
			}
			else{
				if (base.equalsIgnoreCase (BaseCalc.TYPICAL_PRICE.getBaseCalcName ())){
					return TYPICAL_PRICE;
				}	
				else{
					if (base.equalsIgnoreCase (BaseCalc.VARIATION_PRICE.getBaseCalcName ())){
						return VARIATION_PRICE;
					}
					else{
						if (base.equalsIgnoreCase (BaseCalc.MEDIAN_PRICE.getBaseCalcName ())){
							return MEDIAN_PRICE;
						}
						else{
							return null;
						}
					}
				}
			}
		}
	}
}
