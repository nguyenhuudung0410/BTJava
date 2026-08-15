package BaiTap;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Bai3 extends JFrame implements ActionListener {
	JButton btn1, btn2, btn3, btn4;
	JPanel pn, pn1, pn2;

	public void GUI() {
		btn1 = new JButton("RED");
		btn2 = new JButton("GREEN");
		btn3 = new JButton("BLUE");
		btn4 = new JButton("Thoat");

		btn1.addActionListener(this);
		btn2.addActionListener(this);
		btn3.addActionListener(this);
		btn4.addActionListener(this);

		pn = new JPanel(new BorderLayout());
		pn1 = new JPanel(new FlowLayout());
		pn2 = new JPanel();

		pn1.add(btn1);
		pn1.add(btn2);
		pn1.add(btn3);
		pn1.add(btn4);

		pn.add(pn2, BorderLayout.CENTER);
		pn.add(pn1, BorderLayout.SOUTH);

		add(pn);
		setSize(400, 300);
		setTitle("Doi mau nen");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn4) {
			System.exit(0);
			return;
		}

		if (e.getSource() == btn1) {
			pn1.setBackground(Color.RED);
			pn2.setBackground(Color.RED);
		} else if (e.getSource() == btn2) {
			pn1.setBackground(Color.GREEN);
			pn2.setBackground(Color.GREEN);
		} else if (e.getSource() == btn3) {
			pn1.setBackground(Color.BLUE);
			pn2.setBackground(Color.BLUE);
		}
	}

	public static void main(String[] args) {
		new Bai3().GUI();
	}
}