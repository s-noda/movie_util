package com.s_noda.movieMaker;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JTextField;


@SuppressWarnings("serial")
public class CreditMovie extends Movie {

	public String credit = "hoge";
	public int color = 0xffffff ;
	public String[] value_names ;

	public CreditMovie() {
		super();
		this.value_names =  new String[] { "x", "y", "w", "h", "start",
				"end", "show", "credit", "color" };
		Object[] values = new Object[] { x, y, width, height, start_time,
				end_time, show_time, credit, color };

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
	}

	@Override
	public void draw(Graphics g, double time) throws IOException {
		//System.out.println("draw") ;
		if (time >= this.show_time
				&& time - this.show_time <= this.end_time - this.start_time) {
			//System.out.println("done") ;
			g.setColor( ( color >= 0 && color <= 0xffffff ) ? new Color( color ) : Color.white );
			g.setFont(new Font(Font.SERIF, Font.PLAIN, (int)this.height));
			g.drawString(this.credit, this.x, this.y + g.getFontMetrics().getHeight());
			if (this.selected) {
				g.setColor(Color.red);
				g.drawRect(this.x, this.y, this.width, this.height);
			}
		}
	}
	
	@Override
	public void draw(Graphics g){
		try {
			draw(g, MainFrame.timer) ;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update_values() {
		Object[] values = new Object[] { x, y, width, height, start_time,
				end_time, show_time, credit, color };
		for (int i = 0; i < value_names.length; i++) {
			this.texts.get(i).setText(values[i] + "");
		}
		this.texts.get(8).setText( Integer.toHexString(this.color) ) ;
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
		
		this.credit = this.texts.get(7).getText() ;
		this.color = Integer.decode( "0x" + this.texts.get(8).getText() ) ;
		
		MainFrame.self.repaint();
	}
}
