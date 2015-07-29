package com.s_noda.movieMaker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;

import javax.swing.JTextField;

@SuppressWarnings("serial")
public class Movie extends JFrame implements ActionListener, WindowListener {

	public MainFrame mFrame;
	
	public String filename;
	public String temp_path;

	public BufferedImage thumbnail;
	public String thumbnail_path;

	public double start_time;
	public double end_time;
	public double frame_rate;
	public double total_time ;

	public int x, y;
	public int width, height;

	public int inX, inY;
	public int inW, inH;

	public double show_time;

	public boolean gen_image;

	public boolean selected;

	public ArrayList<JTextField> texts;

	public String[] value_names = new String[] { "x", "y", "w", "h", "start",
			"end", "show", "inX", "inY", "inW", "inH" };

	protected Movie() {
		// for extends
		this.start_time = MainFrame.start_time;
		this.end_time = MainFrame.end_time;
		this.show_time = 0;
		this.total_time = Double.MAX_VALUE ;
		this.x = this.y = 100;
		this.width = 300;
		this.height = 30;
		this.inX = this.inY = 0;
		this.inW = this.width;
		this.inH = this.height;
	}

	public Movie(String filename) throws IOException {
		this.filename = filename;
		File org = new File(this.filename);

		if (!org.exists()) {
			System.out.println("file not found " + this.filename);
			return;
		}

		this.temp_path = ".temp." + org.getName();

		this.thumbnail_path = this.temp_path + "/" + "thumnail.jpg";
		File thumb_file = new File(this.temp_path);
		System.out.println("mkdir : " + this.temp_path);
		if (!thumb_file.exists()) {
			thumb_file.mkdir();
		}

		MainFrame.system("ffmpeg -i " + this.filename
				+ " -ss 1 -vframes 1 -f image2 " + this.thumbnail_path);
		this.thumbnail = ImageIO.read(new File(this.thumbnail_path));

		this.x = this.y = 0;
		this.inX = this.inY = 0;

		this.width = this.thumbnail.getWidth();
		this.height = this.thumbnail.getHeight();
		MainFrame.self.setSize(new Dimension((int) Math
				.max(MainFrame.width,
						MainFrame.width - MainFrame.movie_panel.getWidth()
								+ this.width), (int) Math.max(MainFrame.height,
				MainFrame.height - MainFrame.movie_panel.getHeight()
						+ this.height)));

		// MainFrame.movie_panel.setPreferredSize(new Dimension((int) Math.max(
		// this.width, MainFrame.movie_panel.getWidth()), (int) Math.max(
		// this.height, MainFrame.movie_panel.getHeight())));

		this.inW = this.width;
		this.inH = this.height;

		this.show_time = 0;

		// this.start_time = 0;
		// this.end_time = 60;
		this.movie_status(this.filename);

		this.gen_image = false;
		this.selected = false;

		getContentPane().setLayout(new FlowLayout());

		double[] values = new double[] { x, y, width, height, start_time,
				end_time, show_time, inX, inY, inW, inH };

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
			MainFrame.system("ffmpeg" + " -ss "
					+ ((time - this.show_time + this.start_time) % this.total_time)
					+ " -vframes 1 " + " -i " + this.filename + " -f image2 "
					+ this.thumbnail_path);
			this.thumbnail = ImageIO.read(new File(this.thumbnail_path));
		} else {
			this.thumbnail = null ;
			//this.thumbnail.getGraphics().clearRect(0, 0, this.width,
			//		this.height);
		}
	}

	public void movie_status(String path) {
		// System.out.println( "start" ) ;
		try {
			Process p = MainFrame.run.exec("ffmpeg -i " + path);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));
			String buf;
			while ((buf = in.readLine()) != null) {
				String[] colon_pair = buf.split(",");
				for (String pair : colon_pair) {
					String[] name2value = pair.trim().split(" ");
					if (name2value[0].trim().contentEquals("start:")) {
						this.start_time = Double.parseDouble(name2value[1]
								.trim());
						System.out.println("start time " + this.start_time);
					} else if (name2value[0].trim().contentEquals("Duration:")) {
						String[] time = name2value[1].trim().split(":");
						this.end_time = Double.parseDouble(time[0]) * 3600
								+ Double.parseDouble(time[1]) * 60
								+ Double.parseDouble(time[2]);
						this.total_time = this.end_time ;
						System.out.println("end time " + this.end_time);
					}
				}
				// System.out.println(buf) ;
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// System.out.println( "end" ) ;
	}

	// public void time_update_with_gen_images(double time) throws IOException {
	// if (time > this.show_time
	// && time - this.show_time < this.end_time - this.start_time) {
	// this.thumbnail = ImageIO.read(new File(this.temp_path + "/"
	// + (int) ((time - this.show_time) * MainFrame.frame_rate)
	// + ".jpg"));
	// }
	// }
	// // unsupported
	// public void gen_images() {
	// this.gen_image = true;
	// String buf = this.temp_path + "/" + "buf.avi";
	// MainFrame.system("ffmpeg -i " + this.filename + " -s " + width + "x"
	// + height + " -r " + MainFrame.frame_rate + " -vcodec mjpeg "
	// + buf);
	// MainFrame.system("ffmpeg -i " + buf + " -ss " + this.start_time
	// + " -f image2 " + this.temp_path + "/%d.jpg");
	// }

	public BufferedImage my_resize(BufferedImage i, int w, int h) {
		BufferedImage ret = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		ret.getGraphics().drawImage(
				i.getScaledInstance(w, h, Image.SCALE_SMOOTH), 0, 0, this);
		return ret;
	}

	public void draw(Graphics g) {
		if (this.thumbnail != null) {
			BufferedImage i = (this.thumbnail.getWidth() != this.width || this.thumbnail
					.getHeight() != this.height) ? this.my_resize(
					this.thumbnail, this.width, this.height) : this.thumbnail;
			if (this.inX != 0 || this.inH != 0 || this.width != this.inW
					|| this.height != this.inH) {
				i = i.getSubimage(this.inX, this.inY, this.inW, this.inH);
			}
			this.draw(g, i);
		}
	}

	private void draw(Graphics g, Image image) {
		g.drawImage(image, this.x + this.inX, this.y + this.inY, this.inW,
				this.inH, null);
		if (this.selected) {
			g.setColor(Color.red);
			g.drawRect(this.x, this.y, this.width, this.height);
		}
	}

	public void draw(Graphics g, double time) throws IOException {
		if (time > this.show_time
				&& time - this.show_time < this.end_time - this.start_time) {
			this.draw(g, ImageIO.read(new File(this.temp_path + "/"
					+ (int) ((time - this.show_time) * MainFrame.frame_rate)
					+ ".jpg")));
		}
	}

	public void update_values() {
		double[] values = new double[] { x, y, width, height, start_time,
				end_time, show_time, inX, inY, inW, inH };
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
		this.inX = (int) Double.parseDouble(this.texts.get(7).getText());
		this.inY = (int) Double.parseDouble(this.texts.get(8).getText());
		this.inW = (int) Math.min(
				Double.parseDouble(this.texts.get(9).getText()), this.width
						- this.inX);
		this.inH = (int) Math.min(
				Double.parseDouble(this.texts.get(10).getText()), this.height
						- this.inY);
		try {
			this.time_update(MainFrame.timer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		MainFrame.self.repaint();
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		System.out.println("exit");
		MainFrame.movie.remove(this);
		MainFrame.self.repaint();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}
}
