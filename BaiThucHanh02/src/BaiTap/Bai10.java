package BaiTap;
import java.awt.event.*;
import javax.swing.*;

public class Bai10 extends JFrame {
	public void GUI() {
		JMenuBar mb = new JMenuBar();

		JMenu m1 = new JMenu("File");
		m1.add(new JMenuItem("New..."));
		m1.add(new JMenuItem("Open..."));
		m1.add(new JMenuItem("Save"));
		m1.addSeparator();
		m1.add(new JMenuItem("Quit"));

		JMenu m2 = new JMenu("Edit");
		m2.add(new JMenuItem("Copy"));
		m2.add(new JMenuItem("Cut"));
		m2.add(new JMenuItem("Paste"));
		m2.addSeparator();

		JMenu sub = new JMenu("Option");
		sub.add(new JMenuItem("First"));
		sub.add(new JMenuItem("Second"));
		sub.add(new JMenuItem("Third"));
		
		m2.add(sub);
		m2.add(new JMenuItem("Protected"));

		mb.add(m1);
		mb.add(m2);

		setJMenuBar(mb);
		setTitle("Menu Demo");
		setSize(400, 200);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public static void main(String[] args) {
		new Bai10().GUI();
	}
}