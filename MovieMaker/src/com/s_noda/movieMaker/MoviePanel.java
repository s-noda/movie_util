package com.s_noda.movieMaker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MoviePanel extends JPanel {

	public BufferedImage buffer ;
	
	public MoviePanel() {
		this.setPreferredSize(new Dimension(MainFrame.width, MainFrame.height));
		MovieMouseAdapter l = new MovieMouseAdapter() ;
		this.addMouseListener(l) ;
		this.addMouseMotionListener(l) ;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
//		if ( this.buffer == null ){
//			this.buffer = 
//		}
		
		g.setColor(Color.black);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		for (Movie mov : MainFrame.movie) {
			mov.draw(g);
		}
	}

}
