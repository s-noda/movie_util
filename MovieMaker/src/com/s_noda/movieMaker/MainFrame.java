package com.s_noda.movieMaker;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import java.awt.Graphics2D;

import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class MainFrame extends JFrame implements ComponentListener,
		ChangeListener, ActionListener, Runnable {

	public static int width = 640, height = 480;
	public static int frame_rate = 25;
	public static Runtime run = Runtime.getRuntime();

	public static double timer = 0;
	public double speed = 1.0;

	public static String temp_path = ".temp.main";
	public static ArrayList<Movie> movie = new ArrayList<Movie>();

	public static MoviePanel movie_panel = new MoviePanel();
	public static JSlider slider = new JSlider(SwingConstants.VERTICAL, 0, 100,
			0);

	public static double start_time = 0, end_time = 300;

	public static MainFrame self = new MainFrame();

	public JButton run_button;
	public JTextField start_time_field, end_time_field, speed_field;

	public BufferedImage buffer;

	public static void system(String command) {
		try {
			Process p = run.exec(command);
			int ret = p.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Movie genMovieObject(String path) {
		Movie ret = null;
		try {
			ret = new StaticMovie(path);
			System.out.println("input type: static movie");
		} catch (IOException e) {
			// e.printStackTrace();
			// System.out.println("not static movie");
		}
		if (ret != null)
			return ret;
		try {
			ret = new Movie(path);
			System.out.println("input type: movie");
			if (MainFrame.movie.size() == 0)
				setTextFieldValues(ret.start_time, ret.end_time, -1);
		} catch (IOException e) {
			// e.printStackTrace();
			// System.out.println("not movie");
		}
		return ret;
	}

	public static void rmTmpDir() {
		File f = new File(temp_path);
		if (f.exists()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++)
				files[i].delete();
			// f.delete();
		}
	}

	public MainFrame() {
		getContentPane().setLayout(new BorderLayout());

		getContentPane().add(movie_panel, BorderLayout.CENTER);
		getContentPane().add(slider, BorderLayout.EAST);
		slider.addChangeListener(this);

		this.run_button = new JButton("(start,end,speed)");
		this.run_button.addActionListener(this);
		// getContentPane().add(this.run_button, BorderLayout.NORTH);

		this.start_time_field = new JTextField(3);
		this.end_time_field = new JTextField(3);
		this.speed_field = new JTextField(3);
		this.start_time_field.setText(start_time + "");
		this.end_time_field.setText(end_time + "");
		this.speed_field.setText(this.speed + "");
		// getContentPane().add(this.start_time_field, BorderLayout.NORTH);
		// getContentPane().add(this.end_time_field, BorderLayout.NORTH);

		JButton credit = new JButton("credit");
		credit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainFrame.movie.add(new CreditMovie());
				MainFrame.self.repaint();
			}
		});

		JPanel p = new JPanel();
		p.setLayout(new FlowLayout());
		p.add(this.run_button);
		p.add(this.start_time_field);
		p.add(this.end_time_field);
		p.add(this.speed_field);
		p.add(credit);
		getContentPane().add(p, BorderLayout.NORTH);

		getContentPane().add(Console.self, BorderLayout.SOUTH);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				//MainFrame.system("rm -rf .temp.*");
				MainFrame.rmTmpDir();
				System.exit(0);
			}
		});

		setTitle("MovieMaker");
		// setSize( width, height );
		pack();
		setVisible(true);

		DropTarget target = new DropTarget(movie_panel, new MovieDropAdapter() {
			@Override
			public void drop(File[] files) {
				for (File f : files) {
					Movie m = genMovieObject(f.getAbsolutePath());
					if (m != null) {
						MainFrame.movie.add(m);
						MainFrame.self.repaint();
					}
				}
			}
		});

		this.addComponentListener(this);

		File temp_file = new File(temp_path);
		if (!temp_file.exists()) {
			temp_file.mkdir();
		}
	}

	public void setTextFieldValues(double st, double ed, double spd) {
		if (st > 0)
			this.start_time_field.setText(String.format("%.1f", st));
		if (ed > 0)
			this.end_time_field.setText(String.format("%.1f", ed));
		if (spd > 0)
			this.speed_field.setText(String.format("%.1f", spd));
		start_time = Double.parseDouble(this.start_time_field.getText());
		end_time = Double.parseDouble(this.end_time_field.getText());
		this.speed = Double.parseDouble(this.speed_field.getText());
	}

	public void updateTextFieldValues() {
		start_time = Double.parseDouble(this.start_time_field.getText());
		end_time = Double.parseDouble(this.end_time_field.getText());
		this.speed = Double.parseDouble(this.speed_field.getText());
	}

	public static void time_update(double time) {
		if (time != MainFrame.timer) {
			MainFrame.timer = time;
			for (Movie mov : MainFrame.movie) {
				try {
					mov.time_update(MainFrame.timer);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		// MainFrame.self.repaint();
	}

	public static void main(String[] args) {
		/*
		 * try { movie.add(new Movie("/home/s_noda/Desktop/test/out.avi")); }
		 * catch (IOException e) { e.printStackTrace(); } main.repaint();
		 */
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
	}

	@Override
	public void componentResized(ComponentEvent e) {
		MainFrame.width = this.getWidth();// e.getComponent().getWidth();
		MainFrame.height = this.getHeight(); // e.getComponent().getHeight();
		Console.self.setSize(MainFrame.width, Console.self.getHeight());
		// MainFrame.movie_panel.setPreferredSize(new Dimension(MainFrame.width,
		// MainFrame.height));
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Long last_changed = (Long) MainFrame.slider
				.getClientProperty("last_changed_time");
		if (!MainFrame.slider.getValueIsAdjusting() || last_changed == null
				|| System.currentTimeMillis() - last_changed > 500) {
			updateTextFieldValues();
			double time = MainFrame.slider.getValue() / 100.0
					* (MainFrame.end_time - MainFrame.start_time);
			time_update(time);
			Console.echo(time + "sec");
			MainFrame.self.repaint();
			MainFrame.slider.putClientProperty("last_changed_time",
					(Long) System.currentTimeMillis());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		updateTextFieldValues();
		new Thread(this).start();
	}

	@Override
	public void run() {
		System.out.println("remove temp files");
		MainFrame.rmTmpDir();
		System.out.println("generate static images");
		System.out.println("painting");
		int index = 0;
		for (Movie mov : MainFrame.movie) {
			mov.selected = false;
		}
		double total = (end_time - start_time) * (frame_rate / this.speed);
		for (double i = start_time; i < end_time; i++) {
			for (double j = 0; j < frame_rate; j += this.speed) {
				index++;
				if (index > total) {
					i = end_time;
					break;
				}
				System.out.print("[" + (index) + "]");
				Console.echo("PAINTING : " + (index) + " / " + total);
				time_update(i + j * 1.0 / frame_rate);
				BufferedImage buf = new BufferedImage(
						MainFrame.movie_panel.getWidth(),
						MainFrame.movie_panel.getHeight(),
						BufferedImage.TYPE_INT_RGB);
				Graphics2D g2 = buf.createGraphics();
				movie_panel.paint(g2);
				repaint();
				g2.dispose();
				try {
					ImageIO.write(buf, "jpeg", new File(temp_path + "/" + index
							+ ".jpeg"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			System.gc();
		}
		System.out.println("");
		System.out.println("generate output movie");
		Console.echo("generate movie ... ");
		MainFrame.system("ffmpeg -i "
				// + " -r " + frame_rate + " "
				+ temp_path + "/%d.jpeg" + " -vcodec mjpeg -sameq "
				+ System.currentTimeMillis() + ".avi");
		System.out.println("done");
		Console.echo("done");
	}
}
