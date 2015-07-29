package com.s_noda.movieMaker;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Collections;

public class MovieMouseAdapter implements MouseMotionListener, MouseListener {

	public Movie selected_movie;

	public int mode ;
	public int mx, my, lx, ly, rx, ry;

	public boolean update_selected_movie(int x, int y) {
		for (int i = MainFrame.movie.size()-1 ; i>=0 ; i--  ) {
			Movie mov = MainFrame.movie.get(i) ;
			if (x > mov.x && x < mov.x + mov.width && y > mov.y
					&& y < mov.y + mov.height) {
				this.selected_movie = mov;
				this.selected_movie.selected = true;
				//System.out.println( "selected" ) ;
				return true ;
			}
			mov.selected = false ;
		}
		return false ;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		//System.out.println( e.getButton() ) ;
		if (this.selected_movie != null && this.selected_movie.selected ) {
			switch (this.mode) {
			case MouseEvent.BUTTON1:
				//System.out.println( "move" ) ;
				this.selected_movie.x += e.getX() - this.lx ;
				this.selected_movie.y += e.getY() - this.ly ;
				this.selected_movie.update_values() ;
				this.lx = e.getX();
				this.ly = e.getY();
				break;
			case MouseEvent.BUTTON2:
				this.selected_movie.inW += e.getX() - this.mx ;
				this.selected_movie.inH += e.getY() - this.my ;
				this.selected_movie.update_values() ;
				this.mx = e.getX();
				this.my = e.getY();
				break;
			case MouseEvent.BUTTON3:
				this.selected_movie.width += e.getX() - this.rx ;
				this.selected_movie.height += e.getY() - this.ry ;
				this.selected_movie.inW = this.selected_movie.width ;
				this.selected_movie.inH = this.selected_movie.height ;
				this.selected_movie.update_values() ;
				this.rx = e.getX();
				this.ry = e.getY();
				break;
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//System.out.println( "clicked" ) ;
		this.mode = e.getButton() ;
		switch (e.getButton()) {
		case MouseEvent.BUTTON1:
			Console.echo( "MOVE" ) ;
			this.lx = e.getX();
			this.ly = e.getY();
			this.update_selected_movie(this.lx, this.ly);
			MainFrame.self.repaint() ;
			break;
		case MouseEvent.BUTTON2:
			Console.echo( "CLIP" ) ;
			this.mx = e.getX();
			this.my = e.getY();
			if ( this.update_selected_movie(this.mx, this.my) ) {
				this.selected_movie.inX = this.mx - this.selected_movie.x ;
				this.selected_movie.inY = this.my - this.selected_movie.y;
				this.selected_movie.inW = 0 ;
				this.selected_movie.inH = 0 ;
				MainFrame.self.repaint() ;
			}
			break;
		case MouseEvent.BUTTON3:
			Console.echo( "RESIZE" ) ;
			this.rx = e.getX();
			this.ry = e.getY();
			this.update_selected_movie(this.rx, this.ry);
			MainFrame.self.repaint() ;
			break;
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

}
