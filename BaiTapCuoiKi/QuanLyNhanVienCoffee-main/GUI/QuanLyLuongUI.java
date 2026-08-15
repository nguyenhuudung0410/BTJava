import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class QuanLyLuongUI extends JPanel {

    private JTable tblLuong;
    private DefaultTableModel model;
    private JComboBox<String> cbThang, cbNam;
    private JTextField txtTimKiem;
    private List<NhanVienData> danhSachGoc;

    
    private Map<String, String> mapGioLam = new HashMap<>();
    private Map<String, String> mapThuong = new HashMap<>();

    private final Font FONT_DAM = new Font("Segoe UI", Font.BOLD, 15);
    private final Font FONT_CHINH = new Font("Segoe UI", Font.PLAIN, 15);
    
    
    private final Color BG_COLOR = new Color(248, 250, 252);       
    private final Color CARD_BG = Color.WHITE;                     
    private final Color PRIMARY_COLOR = new Color(59, 130, 246);   
    private final Color TEXT_MAIN = new Color(30, 41, 59);         
    private final Color TEXT_MUTED = new Color(100, 116, 139);     

    public QuanLyLuongUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        initTopPanel();
        initTable();
        initBottomPanel();

        loadData();
        setupListeners();
    }

    
    
    
    private String taoKey(String maNV) {
        int thang = cbThang.getSelectedIndex() + 1;
        String nam = cbNam.getSelectedItem().toString();
        return maNV + "_" + thang + "_" + nam;
    }

    
    
    
    private void initTopPanel() {
        JPanel pnlTop = new JPanel(new BorderLayout(0, 15));
        pnlTop.setOpaque(false);
        pnlTop.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lblTitle = new JLabel("Báº¢NG TÃNH LÆ¯Æ NG NHÃ‚N VIÃŠN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(TEXT_MAIN);
        pnlTop.add(lblTitle, BorderLayout.WEST);

        
        txtTimKiem = new JTextField() {
            private boolean hasFocus = false;
            {
                addFocusListener(new java.awt.event.FocusAdapter() {
                    @Override public void focusGained(java.awt.event.FocusEvent e) { hasFocus = true; repaint(); }
                    @Override public void focusLost(java.awt.event.FocusEvent e) { hasFocus = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
                if (!hasFocus && getText().isEmpty()) {
                    Graphics2D g3 = (Graphics2D) g.create();
                    g3.setFont(FONT_CHINH);
                    g3.setColor(new Color(148, 163, 184));
                    g3.drawString("ðŸ”  TÃ¬m mÃ£ hoáº·c tÃªn nhÃ¢n viÃªn...", 16, (getHeight() + g3.getFontMetrics().getAscent() - g3.getFontMetrics().getDescent()) / 2);
                    g3.dispose();
                }
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hasFocus ? PRIMARY_COLOR : new Color(226, 232, 240));
                g2.setStroke(new BasicStroke(hasFocus ? 2f : 1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };
        txtTimKiem.setOpaque(false);
        txtTimKiem.setFont(FONT_CHINH);
        txtTimKiem.setBorder(new EmptyBorder(0, 16, 0, 16));
        txtTimKiem.setForeground(TEXT_MAIN);
        txtTimKiem.setCaretColor(PRIMARY_COLOR);

        JPanel pnlCenter = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pnlCenter.setOpaque(false);
        txtTimKiem.setPreferredSize(new Dimension(350, 45));
        pnlCenter.add(txtTimKiem);
        pnlTop.add(pnlCenter, BorderLayout.CENTER);

        
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlRight.setOpaque(false);

        LocalDate now = LocalDate.now();
        cbThang = createModernComboBox(new String[]{"ThÃ¡ng 1", "ThÃ¡ng 2", "ThÃ¡ng 3", "ThÃ¡ng 4", "ThÃ¡ng 5", "ThÃ¡ng 6", "ThÃ¡ng 7", "ThÃ¡ng 8", "ThÃ¡ng 9", "ThÃ¡ng 10", "ThÃ¡ng 11", "ThÃ¡ng 12"});
        cbThang.setSelectedIndex(now.getMonthValue() - 1);
        cbThang.setPreferredSize(new Dimension(130, 45));

        cbNam = createModernComboBox(new String[]{"2024", "2025", "2026", "2027", "2028"});
        cbNam.setSelectedItem(String.valueOf(now.getYear()));
        cbNam.setPreferredSize(new Dimension(100, 45));

        ActionListener changeListener = e -> loadData();
        cbThang.addActionListener(changeListener);
        cbNam.addActionListener(changeListener);

        pnlRight.add(cbThang);
        pnlRight.add(cbNam);
        pnlTop.add(pnlRight, BorderLayout.EAST);

        add(pnlTop, BorderLayout.NORTH);
    }

    private JComboBox<String> createModernComboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(FONT_CHINH);
        cb.setBackground(Color.WHITE);
        cb.setForeground(TEXT_MAIN);
        cb.setFocusable(false);
        cb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return cb;
    }

    
    
    
    private void initTable() {
        String[] columns = {"MÃ£ NV", "Há» TÃªn", "LÆ°Æ¡ng/Giá»", "Tá»•ng giá» lÃ m", "ThÆ°á»Ÿng", "Tá»”NG LÆ¯Æ NG"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) {
                return col == 3 || col == 4; 
            }
        };

        tblLuong = new JTable(model);
        tblLuong.setRowHeight(85); 
        tblLuong.setFont(FONT_CHINH);
        tblLuong.setShowGrid(false); 
        tblLuong.setIntercellSpacing(new Dimension(0, 0)); 
        tblLuong.setBackground(BG_COLOR); 
        tblLuong.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblLuong.setSelectionBackground(BG_COLOR); 
        tblLuong.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        
        CardCellRenderer cardRenderer = new CardCellRenderer();
        for (int i = 0; i < tblLuong.getColumnCount(); i++) {
            tblLuong.getColumnModel().getColumn(i).setCellRenderer(cardRenderer);
        }

        
        JTextField editorField = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(CARD_BG);
                g2.fillRect(0, 10, getWidth(), getHeight() - 20);
                
                g2.setColor(new Color(239, 246, 255));
                g2.fillRoundRect(5, (getHeight() - 40) / 2, getWidth() - 10, 40, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        editorField.setOpaque(false);
        editorField.setBorder(new EmptyBorder(0, 15, 0, 15));
        editorField.setFont(new Font("Segoe UI", Font.BOLD, 15));
        editorField.setForeground(PRIMARY_COLOR);
        editorField.setCaretColor(PRIMARY_COLOR);
        editorField.setHorizontalAlignment(JTextField.CENTER);
        
        editorField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) { SwingUtilities.invokeLater(editorField::selectAll); }
        });

        DefaultCellEditor cellEditor = new DefaultCellEditor(editorField);
        cellEditor.setClickCountToStart(1); 
        tblLuong.getColumnModel().getColumn(3).setCellEditor(cellEditor);
        tblLuong.getColumnModel().getColumn(4).setCellEditor(cellEditor);

        
        JTableHeader header = tblLuong.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 50));
        header.setBackground(BG_COLOR);
        header.setForeground(TEXT_MUTED);
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = new JLabel(value.toString());
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                lbl.setForeground(TEXT_MUTED);
                lbl.setBorder(new EmptyBorder(0, column == 0 ? 30 : 10, 0, 0));
                if (column == 5) { lbl.setHorizontalAlignment(SwingConstants.RIGHT); lbl.setBorder(new EmptyBorder(0, 0, 0, 30)); }
                if (column == 3 || column == 4) lbl.setHorizontalAlignment(SwingConstants.CENTER);
                return lbl;
            }
        });

        
        
        
        model.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();
                if (row >= 0 && row < model.getRowCount() && (col == 3 || col == 4)) {
                    String maNV = model.getValueAt(row, 0).toString();
                    String key = taoKey(maNV);
                    mapGioLam.put(key, model.getValueAt(row, 3).toString());
                    mapThuong.put(key, model.getValueAt(row, 4).toString());
                    tinhLuongTaiDong(row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblLuong);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(BG_COLOR);
        scrollPane.getViewport().setBackground(BG_COLOR);
        add(scrollPane, BorderLayout.CENTER);
    }

    
    
    
    class CardCellRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private int column, totalCols, rowIndex;
        private Object cellValue, nameValue;
        private boolean isSelected;

        public CardCellRenderer() { setOpaque(false); }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            this.column = column;
            this.totalCols = table.getColumnCount();
            this.cellValue = value;
            this.nameValue = table.getValueAt(row, 1);
            this.isSelected = isSelected;
            this.rowIndex = row; 
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int padY = 12; 
            int h = getHeight() - padY * 2;
            int arc = 20; 

            
            g2.setColor(new Color(148, 163, 184, 40)); 
            if (column == 0) {
                g2.fillRoundRect(12, padY + 4, getWidth(), h, arc, arc);
            } else if (column == totalCols - 1) {
                g2.fillRoundRect(0, padY + 4, getWidth() - 10 + 2, h, arc, arc);
            } else {
                g2.fillRect(0, padY + 4, getWidth(), h);
            }

            
            Color bgColor = (rowIndex % 2 == 0) ? Color.WHITE : new Color(250, 252, 255);
            if (isSelected) bgColor = new Color(239, 246, 255); 
            
            g2.setColor(bgColor);
            if (column == 0) {
                g2.fillRoundRect(10, padY, getWidth(), h, arc, arc);
                g2.fillRect(getWidth() - arc, padY, arc, h); 
                
                
                java.awt.GradientPaint gpStrip = new java.awt.GradientPaint(
                        10, padY, new Color(59, 130, 246),       
                        10, padY + h, new Color(139, 92, 246));  
                g2.setPaint(gpStrip);
                g2.fillRoundRect(10, padY, 6, h, 8, 8); 
                g2.fillRect(13, padY, 3, h); 

            } else if (column == totalCols - 1) {
                g2.fillRoundRect(0, padY, getWidth() - 10, h, arc, arc);
                g2.fillRect(0, padY, arc, h); 
            } else {
                g2.fillRect(0, padY, getWidth(), h);
            }

            
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            String text = cellValue != null ? cellValue.toString() : "";
            FontMetrics fm = g2.getFontMetrics();
            int textY = padY + (h - fm.getHeight()) / 2 + fm.getAscent();

            if (column == 0) {
                
                java.awt.GradientPaint gpAva = new java.awt.GradientPaint(
                        25, padY + (h - 45) / 2f, new Color(99, 102, 241), 
                        70, padY + (h - 45) / 2f + 45, new Color(236, 72, 153)); 
                g2.setPaint(gpAva);
                g2.fillOval(25, padY + (h - 45) / 2, 45, 45);
                
                
                g2.setColor(Color.WHITE); 
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                String name = nameValue != null ? nameValue.toString().trim() : "U";
                String initial = name.substring(name.lastIndexOf(" ") + 1).substring(0, 1).toUpperCase();
                FontMetrics fmA = g2.getFontMetrics();
                g2.drawString(initial, 25 + (45 - fmA.stringWidth(initial)) / 2, padY + (h - 45) / 2 + (45 - fmA.getHeight()) / 2 + fmA.getAscent());

                
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2.setColor(new Color(100, 116, 139));
                g2.drawString(text, 85, textY);
            } 
            else if (column == 1) {
                g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                g2.setColor(new Color(30, 41, 59));
                g2.drawString(text, 10, textY);
            }
            else if (column == 3 || column == 4) {
                
                g2.setColor(new Color(243, 244, 246)); 
                g2.fillRoundRect(5, padY + (h - 40) / 2, getWidth() - 10, 40, 12, 12);
                
                g2.setColor(new Color(37, 99, 235)); 
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                int txtX = (getWidth() - g2.getFontMetrics().stringWidth(text)) / 2;
                g2.drawString(text, txtX, textY);
            }
            else if (column == totalCols - 1) {
                
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                g2.setColor(new Color(16, 185, 129)); 
                int txtX = getWidth() - g2.getFontMetrics().stringWidth(text) - 30;
                g2.drawString(text, txtX, textY);
            }
            else {
                g2.setColor(new Color(30, 41, 59));
                g2.drawString(text, 10, textY);
            }
            
            g2.dispose();
        }
    }

    
    
    
    private void initBottomPanel() {
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlBottom.setOpaque(false);
        pnlBottom.setBorder(new EmptyBorder(15, 0, 0, 0));

        ModernButton btnLuu = new ModernButton("LÆ°u báº£ng lÆ°Æ¡ng", PRIMARY_COLOR);
        btnLuu.addActionListener(e -> handleLuu());
        pnlBottom.add(btnLuu);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    
    
    
    private class ModernButton extends JButton {
        private Color bgColor;
        public ModernButton(String text, Color bg) {
            super(text);
            this.bgColor = bg;
            setPreferredSize(new Dimension(190, 45));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();
        }
    }

    
    
    
    private void handleLuu() {
        if (tblLuong.isEditing()) {
            tblLuong.getCellEditor().stopCellEditing();
        }
        int count = 0;
        for (NhanVienData nv : danhSachGoc) {
            String key = taoKey(nv.getMaNV());
            if (mapThuong.containsKey(key) || mapGioLam.containsKey(key)) {
                try {
                    double thuong = parseTien(mapThuong.getOrDefault(key, "0"));
                    nv.setLuongThuong(new BigDecimal(thuong));
                    NhanVienDao.getInstance().sua(nv);
                    count++;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        JOptionPane.showMessageDialog(this, "ÄÃ£ cáº­p nháº­t báº£ng lÆ°Æ¡ng cho " + count + " nhÃ¢n viÃªn.", "ThÃ nh cÃ´ng", JOptionPane.INFORMATION_MESSAGE);
        loadData();
    }

    private void loadData() {
        danhSachGoc = NhanVienLogic.getInstance().layDanhSach();
        filterData();
    }

    private void filterData() {
        String raw = txtTimKiem.getText();
        String keyword = removeAccents(raw.trim());
        model.setRowCount(0);

        for (NhanVienData nv : danhSachGoc) {
            if (nv.getTrangThai() == NhanVienData.TrangThaiNV.NGHI_VIEC) continue;

            String maNV = removeAccents(nv.getMaNV());
            String hoTen = removeAccents(nv.getHoTen());

            if (keyword.isEmpty() || maNV.contains(keyword) || hoTen.contains(keyword)) {
                String keyMap = taoKey(nv.getMaNV());
                String gioLam = mapGioLam.getOrDefault(keyMap, "0");
                String thuong = mapThuong.getOrDefault(keyMap, "0");
                
                double luongGioVal = nv.getLuongGio() != null ? nv.getLuongGio().doubleValue() : 0;
                double gioLamVal = parseGio(gioLam);
                double thuongVal = parseTien(thuong);
                double tong = (luongGioVal * gioLamVal) + thuongVal;

                String displayGio = gioLamVal == 0 ? "0" : String.format("%.1f", gioLamVal).replace(".0", "");
                String displayThuong = thuongVal == 0 ? "0" : String.format("%,.0f", thuongVal);

                model.addRow(new Object[]{
                        nv.getMaNV(),
                        nv.getHoTen(),
                        String.format("%,.0f Ä‘", luongGioVal),
                        displayGio,
                        displayThuong,
                        String.format("%,.0f Ä‘", tong)
                });
            }
        }
    }

    private void tinhLuongTaiDong(int row) {
        SwingUtilities.invokeLater(() -> {
            try {
                double luongGio = parseTien(model.getValueAt(row, 2));
                double gioLam = parseGio(model.getValueAt(row, 3));
                double thuong = parseTien(model.getValueAt(row, 4));
                double tong = (luongGio * gioLam) + thuong;
                model.setValueAt(String.format("%,.0f Ä‘", tong), row, 5);
            } catch (Exception ignored) {}
        });
    }

    private void setupListeners() {
        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterData(); }
            public void removeUpdate(DocumentEvent e) { filterData(); }
            public void changedUpdate(DocumentEvent e) { filterData(); }
        });
    }

    private double parseTien(Object obj) {
        if (obj == null) return 0;
        String s = obj.toString().replaceAll("[^\\d]", "");
        if (s.isEmpty()) return 0;
        return Double.parseDouble(s);
    }

    private double parseGio(Object obj) {
        if (obj == null) return 0;
        String s = obj.toString().replace(",", ".");
        s = s.replaceAll("[^\\d.]", "");
        if (s.isEmpty() || s.equals(".")) return 0;
        try { return Double.parseDouble(s); } catch (Exception e) { return 0; }
    }

    private String removeAccents(String text) {
        if (text == null) return "";
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").toLowerCase().replace("Ä‘", "d");
    }
}
