/*
 * Copyright (C) 2015 Javier Gonzalez (eb4gls@gmail.com)
 * All rights reserved.
 *
 * This file is part EMVLibLite
 * 
 * EMVLibLite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  EMVLibLite is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with EMVLibLite.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.yate.indicadores.exceptions;

public class InternalErrorException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1444302472095530412L;
	private String error;
	private int code;
	
	
	public InternalErrorException (int c,String error)
	{
		this.code=c;
		this.error=error;
	}
	
	public InternalErrorException (String error)
	{
		this.error=error;
		this.code=0;
	}

	public String getError ()
	{
		return error;
	}
	
	public int getCode ()
	{
		return code;
	}
	
	public String toString ()
	{
		if (code==0)
			return "Error interno ("+error+")";
		else
			return "Error interno, codigo "+code+" ("+error+")";
	}

}
