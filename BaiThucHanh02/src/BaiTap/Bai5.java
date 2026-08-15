package BaiTap;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class Bai5 extends JFrame implements ActionListener {
	JTextField txt1;
	JButton btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
	JButton btnAdd, btnSub, btnMul, btnDiv, btnEq, btnC, btnDot;
	JPanel pn, pn1, pn2;

	public void GUI() {
		txt1 = new JTextField();
		txt1.setHorizontalAlignment(JTextField.RIGHT);
		txt1.setFont(new Font("Arial", Font.BOLD, 22));
		txt1.setBorder(BorderFactory.createLoweredBevelBorder());

		btn0 = new JButton("0"); btn1 = new JButton("1"); btn2 = new JButton("2");
		btn3 = new JButton("3"); btn4 = new JButton("4"); btn5 = new JButton("5");
		btn6 = new JButton("6"); btn7 = new JButton("7"); btn8 = new JButton("8");
		btn9 = new JButton("9");
		btnAdd = new JButton("+"); btnSub = new JButton("-");
		btnMul = new JButton("*"); btnDiv = new JButton("/");
		btnEq = new JButton("="); btnC = new JButton("C"); btnDot = new JButton(".");

		Font f = new Font("Arial", Font.BOLD, 14);
		JButton[] btns = {btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, 
						  btnAdd, btnSub, btnMul, btnDiv, btnEq, btnC, btnDot};
		for (JButton b : btns) {
			b.addActionListener(this);
			b.setFont(f);
		}

		pn = new JPanel(new BorderLayout(2, 2));
		pn1 = new JPanel(new GridLayout(1, 1));
		pn2 = new JPanel(new GridLayout(4, 4, 2, 2));

		pn1.setBorder(new EmptyBorder(5, 5, 5, 5));
		pn1.add(txt1);

		pn2.add(btn7); pn2.add(btn8); pn2.add(btn9); pn2.add(btnDiv);
		pn2.add(btn4); pn2.add(btn5); pn2.add(btn6); pn2.add(btnMul);
		pn2.add(btn1); pn2.add(btn2); pn2.add(btn3); pn2.add(btnSub);
		pn2.add(btn0); pn2.add(btnDot); pn2.add(btnC); pn2.add(btnAdd);

		pn.add(pn1, BorderLayout.NORTH);
		pn.add(pn2, BorderLayout.CENTER);
		pn.add(btnEq, BorderLayout.EAST);
		btnEq.setPreferredSize(new Dimension(50, 0));

		add(pn);
		setTitle("Calculator");
		setSize(320, 240);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		if (s.equals("C")) {
			txt1.setText("");
		} else if (s.equals("=")) {
			try {
				double res = evaluate(txt1.getText());
				if (res == (long) res) txt1.setText(String.valueOf((long) res));
				else txt1.setText(String.valueOf(res));
			} catch (Exception ex) {
				txt1.setText("Error");
			}
		} else {
			txt1.setText(txt1.getText() + s);
		}
	}

	private double evaluate(String expr) {
		int i = expr.length() - 1;
		int depth = 0;
		while (i >= 0) {
			char c = expr.charAt(i);
			if (c == ')') depth++;
			else if (c == '(') depth--;
			if (depth == 0 && (c == '+' || c == '-') && i > 0) {
				double left = evaluate(expr.substring(0, i));
				double right = evaluate(expr.substring(i + 1));
				return c == '+' ? left + right : left - right;
			}
			i--;
		}
		i = expr.length() - 1;
		depth = 0;
		while (i >= 0) {
			char c = expr.charAt(i);
			if (c == ')') depth++;
			else if (c == '(') depth--;
			if (depth == 0 && (c == '*' || c == '/') && i > 0) {
				double left = evaluate(expr.substring(0, i));
				double right = evaluate(expr.substring(i + 1));
				return c == '*' ? left * right : left / right;
			}
			i--;
		}
		if (expr.startsWith("(") && expr.endsWith(")"))
			return evaluate(expr.substring(1, expr.length() - 1));
		return Double.parseDouble(expr);
	}

	public static void main(String[] args) {
		new Bai5().GUI();
	}
}