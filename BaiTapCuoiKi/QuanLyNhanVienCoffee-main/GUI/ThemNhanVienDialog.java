import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ThemNhanVienDialog extends JDialog {

    private boolean isSuccess = false;
    private NhanVienData nhanVienMoi;

    private JPanel pnlCard;
    private FormGroup txtMaNV, txtHoTen, txtSDT, txtEmail, txtLuongGio;
    private ComboGroup cbChucVu, cbGioiTinh;
    private ReactButton btnTao, btnHuy;

    private final Color bgDark       = new Color(15, 23, 42);
    private final Color cardDark     = new Color(30, 41, 59);
    private final Color borderNormal = new Color(51, 65, 85);
    private final Color borderFocus  = new Color(59, 130, 246);
    private final Color textPrimary  = Color.WHITE;
    private final Color textSecondary= new Color(203, 213, 225);
    
    // Đã tăng font base lên 14
    private final Font  FONT_DAM     = new Font("Segoe UI", Font.BOLD, 14);
    private final Font  FONT_CHINH   = new Font("Segoe UI", Font.PLAIN, 14);

    private float opacity = 0f;
    private float scale   = 0.93f;
    private BufferedImage blurredBackground;

    public ThemNhanVienDialog(Window parent) {
        super(parent, "Tạo Nhân Viên Mới", ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        if (parent != null) setBounds(parent.getBounds());
        else { setSize(1366, 768); setLocationRelativeTo(null); }

        taoHieuUngKinhMo(parent);
        initUI();
        setupAutoData(); // Gọi hàm sinh mã tự động
        setupValidation();
        setupKeyBindings();

        Timer timer = new Timer(10, e -> {
            opacity += 0.07f;
            scale   += 0.007f;
            if (opacity >= 1f) {
                opacity = 1f;
                scale   = 1f;
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });
        timer.start();
    }

    private void taoHieuUngKinhMo(Window parent) {
        if (parent == null || !parent.isShowing()) return;
        try {
            Robot robot = new Robot();
            BufferedImage screen = robot.createScreenCapture(parent.getBounds());
            float w = 1f / 25f;
            float[] data = new float[25];
            for (int i = 0; i < 25; i++) data[i] = w;
            blurredBackground = new ConvolveOp(new Kernel(5, 5, data),
                    ConvolveOp.EDGE_NO_OP, null).filter(screen, null);
        } catch (Exception ignored) {}
    }

    private void initUI() {
        JPanel rootPanel = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                if (blurredBackground != null)
                    g2.drawImage(blurredBackground, 0, 0, getWidth(), getHeight(), null);
                g2.setColor(new Color(15, 23, 42, 200));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        rootPanel.setOpaque(false);
        setContentPane(rootPanel);

        pnlCard = new JPanel(new BorderLayout(0, 18)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                g2.translate(w/2.0, h/2.0); g2.scale(scale, scale); g2.translate(-w/2.0, -h/2.0);
                g2.setColor(new Color(0, 0, 0, 80));
                g2.fillRoundRect(5, 10, w-10, h-10, 20, 20);
                g2.setColor(cardDark);
                g2.fillRoundRect(0, 0, w, h-5, 16, 16);
                g2.setColor(new Color(255, 255, 255, 25));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w-1, h-6, 16, 16);
                g2.dispose();
            }
        };
        pnlCard.setOpaque(false);
        pnlCard.setPreferredSize(new Dimension(780, 580));
        pnlCard.setBorder(new EmptyBorder(30, 35, 30, 35));

        JLabel lblTitle = new JLabel("Tạo nhân viên mới");
        lblTitle.setFont(FONT_DAM.deriveFont(Font.BOLD, 22f));
        lblTitle.setForeground(textPrimary);
        lblTitle.setBorder(new EmptyBorder(0, 0, 5, 0));

        JPanel pnlForm = new JPanel(new GridLayout(4, 2, 25, 18));
        pnlForm.setOpaque(false);

        txtMaNV   = new FormGroup("Mã Nhân Viên", false);
        cbChucVu  = new ComboGroup("Chức Vụ", buildChucVuItems());
        txtHoTen  = new FormGroup("Họ và Tên", true);
        cbGioiTinh= new ComboGroup("Giới Tính", new String[]{"Nam", "Nữ"});
        txtSDT    = new FormGroup("Số Điện Thoại", true);
        txtEmail  = new FormGroup("Email", true);
        txtLuongGio   = new FormGroup("Lương / Giờ (VNĐ)", true);
        
        // Tạo panel trống để thay thế ô lương thưởng đã xóa
        JPanel dummyPanel = new JPanel();
        dummyPanel.setOpaque(false);

        pnlForm.add(txtMaNV);     pnlForm.add(cbChucVu);
        pnlForm.add(txtHoTen);    pnlForm.add(cbGioiTinh);
        pnlForm.add(txtSDT);      pnlForm.add(txtEmail);
        pnlForm.add(txtLuongGio); pnlForm.add(dummyPanel);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlBtns.setOpaque(false);
        pnlBtns.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnHuy = new ReactButton("Hủy bỏ", new Color(71, 85, 105));
        btnHuy.addActionListener(e -> closeWithAnimation());

        btnTao = new ReactButton("+ Tạo nhân viên", new Color(37, 99, 235));
        btnTao.addActionListener(e -> handleTao());

        pnlBtns.add(btnHuy);
        pnlBtns.add(btnTao);

        pnlCard.add(lblTitle,  BorderLayout.NORTH);
        pnlCard.add(pnlForm,   BorderLayout.CENTER);
        pnlCard.add(pnlBtns,   BorderLayout.SOUTH);

        rootPanel.add(pnlCard);
    }

    private String[] buildChucVuItems() {
        NhanVienData.ChucVuNV[] values = NhanVienData.ChucVuNV.values();
        String[] items = new String[values.length];
        for (int i = 0; i < values.length; i++) items[i] = values[i].toString();
        return items;
    }

    // TẠO MÃ NHÂN VIÊN TỰ ĐỘNG TĂNG (NV000, NV001,...)
    private void setupAutoData() {
        try {
            List<NhanVienData> list = NhanVienLogic.getInstance().layDanhSach();
            int maxId = -1;
            for (NhanVienData nv : list) {
                if (nv.getMaNV() != null && nv.getMaNV().toUpperCase().startsWith("NV")) {
                    try {
                        int num = Integer.parseInt(nv.getMaNV().substring(2));
                        if (num > maxId) maxId = num;
                    } catch (Exception ignored) {}
                }
            }
            int nextId = maxId < 0 ? 0 : maxId + 1;
            txtMaNV.setText(String.format("NV%03d", nextId));
        } catch (Exception e) {
            txtMaNV.setText("NV000"); // Mặc định nếu chưa có DL
        }
    }

    private void setupValidation() {
        txtSDT.getField().getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { checkSDT(); }
            public void removeUpdate(DocumentEvent e) { checkSDT(); }
            public void changedUpdate(DocumentEvent e) { checkSDT(); }
            private void checkSDT() {
                String v = txtSDT.getText();
                if (!v.isEmpty() && !v.matches("^0\\d{9}$"))
                    txtSDT.setError("10 số, bắt đầu bằng 0");
                else txtSDT.clearError();
            }
        });

        txtEmail.getField().getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { checkEmail(); }
            public void removeUpdate(DocumentEvent e) { checkEmail(); }
            public void changedUpdate(DocumentEvent e) { checkEmail(); }
            private void checkEmail() {
                String v = txtEmail.getText();
                if (!v.isEmpty() && !v.matches("^[\\w.+\\-]+@[\\w\\-]+\\.[a-z]{2,}$"))
                    txtEmail.setError("Email không hợp lệ");
                else txtEmail.clearError();
            }
        });

        setupLuongFormat(txtLuongGio);
    }

    private void setupLuongFormat(FormGroup fg) {
        fg.getField().getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { check(); }
            public void removeUpdate(DocumentEvent e) { check(); }
            public void changedUpdate(DocumentEvent e) { check(); }
            private void check() {
                String v = fg.getText().replace(",", "");
                if (!v.isEmpty() && !v.matches("\\d+")) fg.setError("Chỉ nhập số!");
                else fg.clearError();
            }
        });
        fg.getField().addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                try {
                    String raw = fg.getText().replaceAll("[^\\d]", "");
                    if (!raw.isEmpty()) {
                        fg.setText(new DecimalFormat("#,###").format(Long.parseLong(raw)));
                        fg.clearError();
                    }
                } catch (Exception ex) {
                    if (!fg.getText().isEmpty()) fg.setError("Định dạng lỗi");
                }
            }
        });
    }

    private void setupKeyBindings() {
        pnlCard.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
        pnlCard.getActionMap().put("close", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { closeWithAnimation(); }
        });
        pnlCard.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "save");
        pnlCard.getActionMap().put("save", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { handleTao(); }
        });
    }

    private void handleTao() {
        txtHoTen.clearError(); txtSDT.clearError();
        txtLuongGio.clearError(); txtEmail.clearError();

        boolean valid = true;

        if (txtHoTen.getText().trim().isEmpty()) {
            txtHoTen.setError("Bắt buộc nhập họ tên"); valid = false;
        }
        if (!txtSDT.getText().matches("^0\\d{9}$")) {
            txtSDT.setError("Sai định dạng SĐT"); valid = false;
        }

        BigDecimal luongGio = BigDecimal.ZERO;
        try {
            String raw = txtLuongGio.getText().replaceAll("[^\\d]", "");
            if (raw.isEmpty() || Long.parseLong(raw) <= 0) throw new Exception();
            luongGio = new BigDecimal(raw);
        } catch (Exception e) {
            txtLuongGio.setError("Phải lớn hơn 0"); valid = false;
        }

        if (!valid) return;

        NhanVienData.ChucVuNV chucVu = null;
        String chucVuStr = cbChucVu.getSelectedItem().toString();
        for (NhanVienData.ChucVuNV cv : NhanVienData.ChucVuNV.values()) {
            if (cv.toString().equals(chucVuStr)) { chucVu = cv; break; }
        }

        nhanVienMoi = new NhanVienData.ThoXayNhanVien()
                .ganMaNV      (txtMaNV.getText().trim())
                .ganHoTen     (txtHoTen.getText().trim())
                .ganSDT       (txtSDT.getText().trim())
                .ganEmail     (txtEmail.getText().trim())
                .ganGioiTinh  (cbGioiTinh.getSelectedItem().toString())
                .ganChucVu    (chucVu)
                .ganTrangThai (NhanVienData.TrangThaiNV.DANG_LAM)
                .ganLuongGio  (luongGio)
                .ganLuongThuong(BigDecimal.ZERO) // Lương thưởng mặc định là 0
                .ganNgayVaoLam(LocalDate.now())
                .taoMoi();
        try {
            NhanVienLogic.getInstance().them(nhanVienMoi);
            isSuccess = true;
            closeWithAnimation();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void closeWithAnimation() {
        Timer t = new Timer(10, e -> {
            opacity -= 0.09f;
            scale   -= 0.012f;
            if (opacity <= 0) { dispose(); ((Timer) e.getSource()).stop(); }
            repaint();
        });
        t.start();
    }

    public boolean isSuccess() { return isSuccess; }
    public NhanVienData getNhanVienMoi() { return nhanVienMoi; }

    private class FormGroup extends JPanel {
        private JTextField txt;
        private JLabel lblError;
        private boolean isFocus = false;

        public FormGroup(String labelTitle, boolean editable) {
            setLayout(new BorderLayout(0, 6));
            setOpaque(false);

            JLabel lbl = new JLabel(labelTitle);
            lbl.setFont(FONT_DAM.deriveFont(Font.BOLD, 15f));
            lbl.setForeground(textSecondary);

            txt = new JTextField() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(isEditable() ? bgDark : new Color(15, 23, 42, 120));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    if (!lblError.getText().isBlank()) {
                        g2.setColor(new Color(255, 99, 103));
                        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                    } else if (isFocus) {
                        g2.setColor(new Color(59, 130, 246, 50));
                        g2.setStroke(new BasicStroke(3f));
                        g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 8, 8);
                        g2.setColor(borderFocus);
                        g2.setStroke(new BasicStroke(1f));
                        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                    } else {
                        g2.setColor(borderNormal);
                        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                    }
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            txt.setPreferredSize(new Dimension(0, 46));
            txt.setOpaque(false);
            txt.setBorder(new EmptyBorder(0, 14, 0, 14));
            txt.setFont(FONT_CHINH.deriveFont(15f));
            txt.setCaretColor(textPrimary);
            txt.setEditable(editable);
            txt.setForeground(editable ? textPrimary : new Color(170, 180, 195));
            if (!editable) txt.setFocusable(false);

            txt.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) { isFocus = true; repaint(); }
                public void focusLost(FocusEvent e)   { isFocus = false; repaint(); }
            });

            lblError = new JLabel(" ");
            lblError.setFont(FONT_CHINH.deriveFont(Font.BOLD, 12f));
            lblError.setForeground(new Color(255, 99, 103));

            add(lbl,      BorderLayout.NORTH);
            add(txt,      BorderLayout.CENTER);
            add(lblError, BorderLayout.SOUTH);
        }
        public void setText(String t) { txt.setText(t); }
        public String getText() { return txt.getText(); }
        public void setError(String msg) { lblError.setText(msg); txt.repaint(); }
        public void clearError() { lblError.setText(" "); txt.repaint(); }
        public JTextField getField() { return txt; }
    }

    private class ComboGroup extends JPanel {
        private JComboBox<String> cb;

        public ComboGroup(String labelTitle, String[] items) {
            setLayout(new BorderLayout(0, 6));
            setOpaque(false);

            JLabel lbl = new JLabel(labelTitle);
            lbl.setFont(FONT_DAM.deriveFont(Font.BOLD, 15f));
            lbl.setForeground(textSecondary);

            cb = new JComboBox<>(items) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(bgDark);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(borderNormal);
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            cb.setPreferredSize(new Dimension(0, 46));
            cb.setOpaque(false);
            cb.setForeground(textPrimary);
            cb.setFont(FONT_CHINH.deriveFont(15f));
            cb.setBorder(new EmptyBorder(0, 14, 0, 14));
            cb.setFocusable(false);

            cb.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
                @Override public void paintCurrentValueBackground(Graphics g, Rectangle b, boolean f) {}
                @Override protected JButton createArrowButton() {
                    JButton btn = new JButton("▼");
                    btn.setFont(new Font("Arial", Font.PLAIN, 11));
                    btn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
                    btn.setContentAreaFilled(false);
                    btn.setForeground(textSecondary);
                    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    return btn;
                }
            });

            cb.setRenderer(new DefaultListCellRenderer() {
                @Override public Component getListCellRendererComponent(
                        JList<?> list, Object val, int idx, boolean isSel, boolean focus) {
                    JLabel l = (JLabel) super.getListCellRendererComponent(list, val, idx, isSel, focus);
                    l.setBorder(new EmptyBorder(10, 12, 10, 12));
                    l.setFont(FONT_CHINH.deriveFont(15f));
                    l.setOpaque(true);
                    if (idx == -1) { l.setOpaque(false); l.setForeground(textPrimary); }
                    else if (isSel) { l.setBackground(borderFocus); l.setForeground(Color.WHITE); }
                    else { l.setBackground(cardDark); l.setForeground(textPrimary); }
                    return l;
                }
            });

            JLabel dummy = new JLabel(" ");
            dummy.setFont(FONT_CHINH.deriveFont(11f));

            add(lbl,   BorderLayout.NORTH);
            add(cb,    BorderLayout.CENTER);
            add(dummy, BorderLayout.SOUTH);
        }
        public Object getSelectedItem() { return cb.getSelectedItem(); }
    }

    private class ReactButton extends JButton {
        private float scaleBtn = 1f;
        private final Font FONT_DAM_LOCAL = new Font("Segoe UI", Font.BOLD, 14);

        public ReactButton(String text, Color baseColor) {
            super(text);
            setBackground(baseColor);
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
            setPreferredSize(new Dimension(160, 46));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e)  { animate(0.95f); }
                public void mouseReleased(MouseEvent e) { animate(1.0f);  }
            });
        }
        private void animate(float target) {
            final float[] cur = {scaleBtn};
            Timer t = new Timer(10, e -> {
                cur[0] += (target - cur[0]) * 0.4f;
                scaleBtn = cur[0];
                if (Math.abs(target - cur[0]) < 0.01f) {
                    scaleBtn = target;
                    ((Timer) e.getSource()).stop();
                }
                repaint();
            });
            t.start();
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.translate(w/2.0, h/2.0); g2.scale(scaleBtn, scaleBtn); g2.translate(-w/2.0, -h/2.0);
            Color bg = getModel().isRollover() ? getBackground().brighter() : getBackground();
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, w, h, 10, 10);
            g2.setColor(Color.WHITE);
            g2.setFont(FONT_DAM_LOCAL);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(getText(), (w-fm.stringWidth(getText()))/2,
                    (h+fm.getAscent()-fm.getDescent())/2);
            g2.dispose();
        }
    }
}