package BaiTap;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Bai2 extends JFrame implements ActionListener{
	JLabel lb1, lb2, lb3, lb4;
	JTextField txt1, txt2, txt3;
	JButton btn1, btn2, btn3, btn4, btn5, btn6;
	JPanel pn, pn1, pn2, pn3, pn4;
	
	public void GUI() {
		lb1 = new JLabel("Basic Arithmetic Operations");
		lb2 = new JLabel("Number 1:");
		lb3 = new JLabel("Number 2:");
		lb4 = new JLabel("Result:");
		
		txt1 = new JTextField(7);
		txt2 = new JTextField(7);
		txt3 = new JTextField(7);
		txt3.setEditable(false);
		
		btn1 = new JButton("Addition");
        btn2 = new JButton("Subtraction");
        btn3 = new JButton("Multiplication");
        btn4 = new JButton("Division");
        btn5 = new JButton("Reset");
        btn6 = new JButton("Exit");
        
        btn1.addActionListener(this);
        btn2.addActionListener(this);
        btn3.addActionListener(this);
        btn4.addActionListener(this);
        btn5.addActionListener(this);
        btn6.addActionListener(this);
        
        pn = new JPanel(new GridLayout(4,1));
        pn1 = new JPanel(new FlowLayout());
        pn2 = new JPanel(new GridLayout(3,2));
        pn3 = new JPanel(new FlowLayout());
        pn4 = new JPanel(new FlowLayout());
        
        pn1.add(lb1);
        
        pn2.add(lb2);
        pn2.add(txt1);
        pn2.add(lb3);
        pn2.add(txt2);
        pn2.add(lb4);
        pn2.add(txt3);
        
        pn3.add(btn1);
        pn3.add(btn2);
        pn3.add(btn3);
        pn3.add(btn4);
        
        pn4.add(btn5);
        pn4.add(btn6);
        
        pn.add(pn1);
        pn.add(pn2);
        pn.add(pn3);
        pn.add(pn4);
        
        add(pn);
        setSize(500, 300);
		setTitle("Arithmetic Operations");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		txt1.requestFocus();
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btn5) {
			txt1.setText("");
			txt2.setText("");
			txt3.setText("");
			txt1.requestFocus();
			return;
		}
		if(e.getSource() == btn6) {
			System.exit(0);
			return;
		}
		try {
            double n1 = Double.parseDouble(txt1.getText());
            double n2 = Double.parseDouble(txt2.getText());
            double result = 0;
            if (e.getSource() == btn1) result = n1 + n2;
            if (e.getSource() == btn2) result = n1 - n2;
            if (e.getSource() == btn3) result = n1 * n2;
            if (e.getSource() == btn4) {
                if (n2 == 0) {
                    txt3.setText("Lỗi chia 0");
                    return;
                }
                result = n1 / n2;
            }
            if (result == (int) result)
            	txt3.setText(Integer.toString((int) result));
            else
                txt3.setText(String.format("%.3f", result));
        } catch (Exception ex) {
            txt3.setText("Lỗi nhập liệu!");
        }
	}
	public static void main(String[] args) {
		new Bai2().GUI();;
	}
}
