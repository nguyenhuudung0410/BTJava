package BaiTap;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class Bai4 extends JFrame implements ActionListener {
	JLabel lb1, lb2, lb3, lb4, lb5;
	JTextField txt1, txt2, txt3, txt4, txt5;
	JButton btn1, btn2, btn3;
	JPanel pn, pn1, pn2;

	public void GUI() {
		lb1 = new JLabel("Enter a String");
		lb2 = new JLabel("To UpperCase");
		lb3 = new JLabel("To LowerCase");
		lb4 = new JLabel("To LowerUpper");
		lb5 = new JLabel("Number of word");

		txt1 = new JTextField(20);
		txt2 = new JTextField(20); txt2.setEditable(false);
		txt3 = new JTextField(20); txt3.setEditable(false);
		txt4 = new JTextField(20); txt4.setEditable(false);
		txt5 = new JTextField(20); txt5.setEditable(false);

		btn1 = new JButton("OK");
		btn2 = new JButton("Reset");
		btn3 = new JButton("Exit");

		btn1.addActionListener(this);
		btn2.addActionListener(this);
		btn3.addActionListener(this);

		pn = new JPanel(new GridLayout(2, 1));
		pn1 = new JPanel(new GridLayout(5, 2, 10, 10));
		pn1.setBorder(new EmptyBorder(15, 20, 10, 20));
		pn2 = new JPanel(new GridLayout(1, 3, 40, 0));
		pn2.setBorder(new EmptyBorder(10, 20, 20, 20));

		pn1.add(lb1); pn1.add(txt1);
		pn1.add(lb2); pn1.add(txt2);
		pn1.add(lb3); pn1.add(txt3);
		pn1.add(lb4); pn1.add(txt4);
		pn1.add(lb5); pn1.add(txt5);

		pn2.add(btn1);
		pn2.add(btn2);
		pn2.add(btn3);

		pn.add(pn1);
		pn.add(pn2);

		add(pn);
		setTitle("Xu li chuoi ky tu");
		setSize(450, 350);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		txt1.requestFocus();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn3) {
			System.exit(0);
			return;
		}
		if (e.getSource() == btn2) {
			txt1.setText(""); txt2.setText(""); txt3.setText("");
			txt4.setText(""); txt5.setText("");
			txt1.requestFocus();
			return;
		}
		if (e.getSource() == btn1) {
			String s = txt1.getText();
			txt2.setText(s.toUpperCase());
			txt3.setText(s.toLowerCase());

			StringBuilder s1 = new StringBuilder();
			for (char c : s.toCharArray())
				s1.append(Character.isLowerCase(c) ? Character.toUpperCase(c) : Character.toLowerCase(c));
			txt4.setText(s1.toString());

			String t = s.trim();
			txt5.setText(String.valueOf(t.isEmpty() ? 0 : t.split("\\s+").length));
		}
	}

	public static void main(String[] args) {
		new Bai4().GUI();
	}
}