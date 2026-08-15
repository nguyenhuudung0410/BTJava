package BaiTap;
import java.awt.event.*;
import javax.swing.*;

public class Bai9 extends JFrame implements MouseListener {
	public void GUI() {
		addMouseListener(this);

		setTitle("MouseTest");
		setSize(400, 200);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void mouseEntered(MouseEvent e) {
		System.out.println("You enter the window at " + e.getX() + " " + e.getY());
	}

	public void mouseExited(MouseEvent e) {
		System.out.println("You exit the window at " + e.getX() + " " + e.getY());
	}

	public void mousePressed(MouseEvent e) {
		System.out.println("You press the mouse at " + e.getX() + " " + e.getY());
	}

	public void mouseReleased(MouseEvent e) {
		System.out.println("You release the mouse at " + e.getX() + " " + e.getY());
	}

	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e))
			System.out.println("You left click the mouse at " + e.getX() + " " + e.getY());
		else if (SwingUtilities.isRightMouseButton(e))
			System.out.println("You right click the mouse at " + e.getX() + " " + e.getY());
	}

	public static void main(String[] args) {
		new Bai9().GUI();
	}
}