package com.yate.tatool;



public enum TimeFormat
{
	
	FORMAT ("Format"),
	EPOCH ("Epoch");
	
				
	private String format;

	private TimeFormat (String format)
	{
		this.format=format;
	}

	public String getTipo ()
	{
		return format;
	}
	

	static public TimeFormat getTimeFormat (String tiempo)
	{
		if (tiempo.equalsIgnoreCase (TimeFormat.FORMAT.getTipo ())){
			return FORMAT;
		}
		else{
			if (tiempo.equalsIgnoreCase (TimeFormat.EPOCH.getTipo ())){
				return EPOCH;
			}
			else{
				return null;
				
			}
		}
	}
}

