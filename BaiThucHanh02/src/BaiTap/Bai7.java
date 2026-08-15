package BaiTap;
import java.awt.*;
import java.awt.event.*;

public class Bai7 extends Frame implements ItemListener {
	Checkbox cb1, cb2;
	Choice ch1;
	List ls1;
	Label lb1;
	Panel pn, pn1, pn2;

	public void GUI() {
		cb1 = new Checkbox("Male");
		cb2 = new Checkbox("Female");
		cb1.addItemListener(this);
		cb2.addItemListener(this);

		ch1 = new Choice();
		ch1.add("MS DOS");
		ch1.add("Windows");
		ch1.add("Linux");
		ch1.addItemListener(this);

		ls1 = new List(3, false);
		ls1.add("Tiger");
		ls1.add("Lion");
		ls1.add("Elephant");
		ls1.addItemListener(this);

		lb1 = new Label("The event is displayed here", Label.CENTER);

		pn = new Panel(new BorderLayout());
		pn1 = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 20));
		pn2 = new Panel(new BorderLayout());

		pn1.add(cb1);
		pn1.add(cb2);
		pn1.add(ch1);
		pn1.add(ls1);

		pn2.add(lb1, BorderLayout.CENTER);

		pn.add(pn1, BorderLayout.CENTER);
		pn.add(pn2, BorderLayout.SOUTH);

		add(pn);
		setTitle("ItemEventTest");
		setSize(450, 250);
		setLocationRelativeTo(null);
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() != ItemEvent.SELECTED) return;
		Object src = e.getSource();

		if (src == cb1 || src == cb2) {
			lb1.setText("You selected: " + ((Checkbox) src).getLabel());
		} else if (src == ch1) {
			lb1.setText("You chose: " + ch1.getSelectedItem());
		} else if (src == ls1) {
			lb1.setText("You selected from the list: " + ls1.getSelectedItem());
		}
	}

	public static void main(String[] args) {
		new Bai7().GUI();
	}
}