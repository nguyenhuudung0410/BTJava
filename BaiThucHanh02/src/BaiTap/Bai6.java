package BaiTap;
import java.awt.*;
import java.awt.event.*;

public class Bai6 extends Frame implements ActionListener {
	Button btn1;
	TextField txt1;
	List ls1;
	Label lb1;
	Panel pn, pn1, pn2;

	public void GUI() {
		btn1 = new Button("OK");
		txt1 = new TextField(10);
		
		ls1 = new List(3, false);
		ls1.add("Tiger");
		ls1.add("Lion");
		ls1.add("Elephant");
		ls1.add("Bear");

		lb1 = new Label("The event is displayed here", Label.CENTER);
		lb1.setFont(new Font("Arial", Font.BOLD, 12));

		btn1.addActionListener(this);
		txt1.addActionListener(this);
		ls1.addActionListener(this);

		pn = new Panel(new BorderLayout());
		pn1 = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 20));
		pn2 = new Panel(new FlowLayout());

		pn1.add(btn1);
		pn1.add(txt1);
		pn1.add(ls1);
		
		pn2.add(lb1);

		pn.add(pn1, BorderLayout.CENTER);
		pn.add(pn2, BorderLayout.SOUTH);

		add(pn);
		setTitle("ActionEventTest");
		setSize(400, 250);
		setLocationRelativeTo(null);
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn1) {
			lb1.setText("You clicked OK");
		} else if (e.getSource() == txt1) {
			lb1.setText("You pressed Enter: " + txt1.getText());
		} else if (e.getSource() == ls1) {
			lb1.setText("You double-clicked: " + ls1.getSelectedItem());
		}
	}

	public static void main(String[] args) {
		new Bai6().GUI();
	}
}