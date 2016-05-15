package com.s_noda.movieMaker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JTextField;


@SuppressWarnings("serial")
public class StaticMovie extends Movie {

	private BufferedImage thumbnail_buf;
	
	public StaticMovie() throws IOException{
		this("hoge") ;
	}
	
	public StaticMovie(String path) throws IOException {
		super();
		this.thumbnail_path = path ;
		this.thumbnail = ImageIO.read(new File(this.thumbnail_path));
		if ( this.thumbnail == null ) throw new IOException() ;

		this.thumbnail_buf = this.thumbnail;
		this.width = this.thumbnail.getWidth() ;
		this.height = this.thumbnail.getHeight() ;
		MainFrame.self.setSize(new Dimension((int) Math
				.max(MainFrame.width,
						MainFrame.width - MainFrame.movie_panel.getWidth()
								+ this.width), (int) Math.max(MainFrame.height,
				MainFrame.height - MainFrame.movie_panel.getHeight()
						+ this.height)));
		this.inW = this.width;
		this.inH = this.height;
		this.value_names =  new String[] { "x", "y", "w", "h", "start",
				"end", "show"};
		Object[] values = new Object[] { x, y, width, height, start_time,
				end_time, show_time};

		getContentPane().setLayout(new FlowLayout());
		String label = "";
		label += "(";
		for (String n : value_names) {
			label += n + ", ";
		}
		label += ")";
		JButton button = new JButton(label);
		button.addActionListener(this);
		getContentPane().add(button);
		this.texts = new ArrayList<JTextField>();
		for (int i = 0; i < value_names.length; i++) {
			JTextField txt = new JTextField(7);
			txt.setName(value_names[i]);
			txt.setText(values[i] + "");
			this.texts.add(txt);
			getContentPane().add(txt);
		}

		addWindowListener(this);
		setTitle(this.filename);
		// setSize( 300, 600 );
		pack();
		setVisible(true);
	}
	
	public void time_update(double time) throws IOException {
		if (time >= this.show_time
				&& time - this.show_time < this.end_time - this.start_time) {
			this.thumbnail = this.thumbnail_buf;
		} else {
			this.thumbnail = null ;
		}
	}

	@Override
	public void update_values() {
		Object[] values = new Object[] { x, y, width, height, start_time,
				end_time, show_time };
		for (int i = 0; i < value_names.length; i++) {
			this.texts.get(i).setText(values[i] + "");
		}
		actionPerformed(null);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.x = (int) Double.parseDouble(this.texts.get(0).getText());
		this.y = (int) Double.parseDouble(this.texts.get(1).getText());
		this.width = (int) Double.parseDouble(this.texts.get(2).getText());
		this.height = (int) Double.parseDouble(this.texts.get(3).getText());
		this.start_time = Double.parseDouble(this.texts.get(4).getText());
		this.end_time = Double.parseDouble(this.texts.get(5).getText());
		this.show_time = Double.parseDouble(this.texts.get(6).getText());
		this.inW = this.width;
		this.inH = this.height; // not supported yet
		
		MainFrame.self.repaint();
	}
}
