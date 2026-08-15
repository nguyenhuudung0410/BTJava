package BaiTap;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Bai1 extends JFrame implements ActionListener{
	JTextField txta, txtb, txtkq;
	JButton btnTinh, btnReset, btnThoat;
	JLabel lb1, lb2, lb3, lb4;
	JPanel pn, pn1, pn2, pn3;
	
	public void GUI() {
		lb1 = new JLabel("GIAI PHUONG TRINH BAC NHAT");
		lb2 = new JLabel("Nhap a:");
		lb3 = new JLabel("Nhap b:");
		lb4 = new JLabel("Ket qua:");
		
		txta = new JTextField(7);
		txtb = new JTextField(7);
		txtkq = new JTextField();
		txtkq.setEditable(false);
		
		btnTinh = new JButton("Tinh");
		btnReset = new JButton("Reset");
		btnThoat = new JButton("Thoat");
		
		btnTinh.addActionListener(this);
		btnReset.addActionListener(this);
		btnThoat.addActionListener(this);
		
		pn = new JPanel(new GridLayout(3,1));
		pn1 = new JPanel(new FlowLayout());
		pn2 = new JPanel(new GridLayout(3,2));
		pn3 = new JPanel(new GridLayout(1,3));
		
		pn1.add(lb1);
		
		pn2.add(lb2);
		pn2.add(txta);
		pn2.add(lb3);
		pn2.add(txtb);
		pn2.add(lb4);
		pn2.add(txtkq);
		
		pn3.add(btnTinh);
		pn3.add(btnReset);
		pn3.add(btnThoat);
		
		pn.add(pn1);
		pn.add(pn2);
		pn.add(pn3);
		
		add(pn);
		setSize(400, 300);
		setTitle("Giai phuong trinh bac nhat");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		txta.requestFocus();
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnTinh) {
			try {
				double a = Double.parseDouble(txta.getText());
				double b = Double.parseDouble(txtb.getText());
				if(a != 0) {
					double result = -b / a;
					if (result == (int) result)
					    txtkq.setText(Integer.toString((int) result));
					else
					    txtkq.setText(String.format("%.3f", result));
				}
				else {
					if(b == 0) {
						txtkq.setText("Phuong trinh vo so nghiem!");
					}
					else {
						txtkq.setText("Phuong trinh vo nghiem!");
					}
				}
			} catch (NumberFormatException ex) {
				txtkq.setText("Nhap so thuc hop le cho a va b");
			}
		}
		else if(e.getSource() == btnReset) {
			txta.setText("");
			txtb.setText("");
			txtkq.setText("");
			txta.requestFocus();
			return;
		}
		else if(e.getSource() == btnThoat) {
			System.exit(0);
			return;
		}
	}
	public static void main(String[] args) {
		new Bai1().GUI();
	}
}
