package BaiTap;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Bai8 extends JFrame implements KeyListener {
	JLabel lb1;

	public void GUI() {
		lb1 = new JLabel("", SwingConstants.CENTER);
		lb1.setFont(new Font("Arial", Font.BOLD, 48));

		add(lb1);

		addKeyListener(this);
		setFocusable(true);

		setTitle("Key Event Test");
		setSize(400, 300);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		requestFocusInWindow();
	}

	public void keyTyped(KeyEvent e) {
		lb1.setText(String.valueOf(e.getKeyChar()));
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

	public static void main(String[] args) {
		new Bai8().GUI();
	}
}