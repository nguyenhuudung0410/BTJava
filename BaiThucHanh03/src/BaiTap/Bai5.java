package BaiTap;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Bai5 extends JFrame implements ActionListener {
	JLabel lb1, lb2;
	JTextField txt1;
	JButton btn1, btn2, btn3;
	JRadioButton rb1, rb2, rb3, rb4, rb5;
	ButtonGroup group;
	JPanel pn, pn1, pn2, pn3, pnTop;
	JTable table;
	DefaultTableModel tableModel;

	public void GUI() {
		lb1 = new JLabel("Nhap noi dung");
		lb2 = new JLabel("Tim kiem theo");

		txt1 = new JTextField(20);

		btn1 = new JButton("Search");
		btn2 = new JButton("Reset");
		btn3 = new JButton("Exit");

		btn1.addActionListener(this);
		btn2.addActionListener(this);
		btn3.addActionListener(this);

		rb1 = new JRadioButton("Id");
		rb2 = new JRadioButton("Name");
		rb3 = new JRadioButton("Date");
		rb4 = new JRadioButton("Address");
		rb5 = new JRadioButton("Gender", true);

		group = new ButtonGroup();
		group.add(rb1);
		group.add(rb2);
		group.add(rb3);
		group.add(rb4);
		group.add(rb5);

		String[] columns = { "Ma So", "Ho Ten", "Ngay Sinh", "Dia Chi", "Gioi Tinh" };
		tableModel = new DefaultTableModel(columns, 0);
		table = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(table);

		pn1 = new JPanel(new FlowLayout());
		pn1.add(lb1);
		pn1.add(txt1);
		pn1.add(btn1);
		pn1.add(btn2);
		pn1.add(btn3);

		pn2 = new JPanel(new FlowLayout());
		pn2.add(lb2);
		pn2.add(rb1);
		pn2.add(rb2);
		pn2.add(rb3);
		pn2.add(rb4);
		pn2.add(rb5);

		pnTop = new JPanel(new GridLayout(2, 1));
		pnTop.add(pn1);
		pnTop.add(pn2);

		pn3 = new JPanel(new BorderLayout());
		pn3.add(scrollPane, BorderLayout.CENTER);

		pn = new JPanel(new BorderLayout());
		pn.add(pnTop, BorderLayout.NORTH);
		pn.add(pn3, BorderLayout.CENTER);

		add(pn);
		setTitle("Select");
		setSize(700, 450);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		loadData("SELECT * FROM Table2");
		setVisible(true);
	}

	public void loadData(String sql) {
		tableModel.setRowCount(0);
		String url = "jdbc:sqlserver://localhost:1433;databaseName=DATA;encrypt=true;trustServerCertificate=true;";
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			Connection conn = DriverManager.getConnection(url, "sa", "123456");
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Vector<Object> row = new Vector<>();
				row.add(rs.getObject("Id"));
				row.add(rs.getObject("Name"));
				row.add(rs.getObject("Date"));
				row.add(rs.getObject("Address"));
				row.add(rs.getObject("Gender"));
				tableModel.addRow(row);
			}
			conn.close();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Loi: " + ex.getMessage());
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn3) {
			System.exit(0);
		} else if (e.getSource() == btn2) {
			txt1.setText("");
			loadData("SELECT * FROM Table2");
		} else if (e.getSource() == btn1) {
			String key = txt1.getText().trim();
			String field = "";
			if (rb1.isSelected())
				field = "Id";
			else if (rb2.isSelected())
				field = "Name";
			else if (rb3.isSelected())
				field = "Date";
			else if (rb4.isSelected())
				field = "Address";
			else if (rb5.isSelected())
				field = "Gender";

			String sql = "SELECT * FROM Table2 WHERE " + field + " LIKE '%" + key + "%'";
			loadData(sql);
		}
	}

	public static void main(String[] args) {
		new Bai5().GUI();
	}
}