package com.s_noda.movieMaker;

import java.awt.Color;

import javax.swing.JLabel;

@SuppressWarnings("serial")
public class Console extends JLabel{
	
	//public String echo = "" ;
	public static Console self = new Console() ;
	
	public Console(){
		super( "> " ) ;
		this.setBackground( Color.black ) ;
		//this.setSize(, height)
	}
	
	public static void echo( String str ){
		self.setText(str) ;
	}
}
