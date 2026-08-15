// Input: jdbc:sqlserver://localhost:1433;databaseName=DATA;encrypt=true;trustServerCertificate=true;
package BaiTap;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Bai3 extends JFrame implements ActionListener {
	JLabel lb1, lb2;
	JTextField txt1, txt2;
	JButton btnSubmit, btnReset, btnCancel;
	JPanel pn, pnTop, pnGrid, pnCenter, pnBottom;
	JTable table;
	DefaultTableModel tableModel;

	public void GUI() {
		lb1 = new JLabel("Input Information:", SwingConstants.RIGHT);
		lb2 = new JLabel("SQL:", SwingConstants.RIGHT);

		txt1 = new JTextField("", 35);
		txt2 = new JTextField("", 35);

		btnSubmit = new JButton("Submit");
		btnReset = new JButton("Reset");
		btnCancel = new JButton("Cancel");

		btnSubmit.addActionListener(this);
		btnReset.addActionListener(this);
		btnCancel.addActionListener(this);

		String[] columns = { "Id", "Name", "Address", "Total" };
		tableModel = new DefaultTableModel(columns, 0);
		table = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(table);

		pnGrid = new JPanel(new GridLayout(2, 2, 5, 5));
		pnGrid.add(lb1);
		pnGrid.add(txt1);
		pnGrid.add(lb2);
		pnGrid.add(txt2);

		pnTop = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pnTop.add(pnGrid);
		pnTop.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

		pnCenter = new JPanel(new BorderLayout());
		pnCenter.add(scrollPane, BorderLayout.CENTER);
		pnCenter.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

		pnBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
		pnBottom.add(btnSubmit);
		pnBottom.add(btnReset);
		pnBottom.add(btnCancel);

		pn = new JPanel(new BorderLayout());
		pn.add(pnTop, BorderLayout.NORTH);
		pn.add(pnCenter, BorderLayout.CENTER);
		pn.add(pnBottom, BorderLayout.SOUTH);

		add(pn);
		setSize(650, 400);
		setTitle("Database Programming - Type 4");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnCancel) {
			System.exit(0);
		} else if (e.getSource() == btnReset) {
			txt1.setText("");
			txt2.setText("");
			tableModel.setRowCount(0);
		} else if (e.getSource() == btnSubmit) {
			String url = txt1.getText().trim();
			String sql = txt2.getText().trim();

			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				Connection conn = DriverManager.getConnection(url, "sa", "123456");
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);

				tableModel.setRowCount(0);
				while (rs.next()) {
					Vector<Object> row = new Vector<>();
					row.add(rs.getObject(1));
					row.add(rs.getObject(2));
					row.add(rs.getObject(3));
					row.add(rs.getObject(4));
					tableModel.addRow(row);
				}
				conn.close();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
			}
		}
	}

	public static void main(String[] args) {
		new Bai3().GUI();
	}
}