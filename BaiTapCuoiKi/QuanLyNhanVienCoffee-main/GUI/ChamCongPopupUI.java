import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

class ChamCongPopupUI extends JDialog {

    private final Color BG_MAIN = new Color(248, 250, 252);
    private final Color PANEL_BG = Color.WHITE;
    private final Color TEXT_MAIN = new Color(30, 41, 59);
    private final Color TEXT_MUTED = new Color(100, 116, 139);

    private CaLamData caLam;
    private LoaiCaData loaiCa;
    private JPanel pnlCardContainer;
    private CardLayout cardLayout;
    private StartShiftPanel startPanel;
    private EndShiftPanel endPanel;
    private JPanel completedPanel;

    public ChamCongPopupUI(Window parent, CaLamData caLam) {
        super(parent, "Quản Lý Chấm Công - Ca: " + caLam.getMaCa(), ModalityType.APPLICATION_MODAL);

        this.caLam = caLam;
        this.loaiCa = CaLamDao.getInstance().layLoaiCaTheoMa(caLam.getMaLoaiCa());

        setSize(950, 650);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(BG_MAIN);
        setLayout(new BorderLayout());

        initUI();
        kiemTraTrangThaiVaHienThi();
    }

    private void initUI() {

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                new EmptyBorder(20, 30, 20, 30)
        ));

        JLabel lblTitle = new JLabel("Điểm Danh / Chấm Công - Ca: " + caLam.getMaCa());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_MAIN);

        JLabel lblSub = new JLabel(
                loaiCa.getTenLoaiCa() + " | Ngày: "
                        + caLam.getNgayLam().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );

        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSub.setForeground(TEXT_MUTED);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 5));
        pnlTitle.setOpaque(false);

        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSub);

        pnlHeader.add(pnlTitle, BorderLayout.WEST);

        add(pnlHeader, BorderLayout.NORTH);

        cardLayout = new CardLayout();

        pnlCardContainer = new JPanel(cardLayout);
        pnlCardContainer.setOpaque(false);
        pnlCardContainer.setBorder(new EmptyBorder(20, 30, 20, 30));

        startPanel = new StartShiftPanel();
        endPanel = new EndShiftPanel();

        initCompletedPanel();

        pnlCardContainer.add(startPanel, "START");
        pnlCardContainer.add(endPanel, "END");
        pnlCardContainer.add(completedPanel, "COMPLETED");

        add(pnlCardContainer, BorderLayout.CENTER);
    }

    private void kiemTraTrangThaiVaHienThi() {

        List<PhanCongCaData> dsPhanCong =
                PhanCongCaDao.getInstance().layDanhSachTheoCa(caLam.getMaCa());

        if (dsPhanCong.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ca này chưa có nhân sự nào được phân công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();
            return;
        }

        int checkedInCount = 0;
        int checkedOutCount = 0;

        for (PhanCongCaData pc : dsPhanCong) {

            ChamCongData cc =
                    ChamCongDao.getInstance().layTheoPhanCong(pc.getMaPhanCong());

            if (cc != null) {

                checkedInCount++;

                if (cc.getCheckOut() != null) {
                    checkedOutCount++;
                }
            }
        }

        if (checkedInCount == 0) {

            startPanel.loadData(dsPhanCong);
            cardLayout.show(pnlCardContainer, "START");

        } else if (checkedInCount > 0 && checkedOutCount < checkedInCount) {

            endPanel.loadData(dsPhanCong);
            cardLayout.show(pnlCardContainer, "END");

        } else {

            cardLayout.show(pnlCardContainer, "COMPLETED");
        }
    }

    class StartShiftPanel extends JPanel {

        private JPanel pnlList;
        private List<EmployeeStartCard> listCards = new ArrayList<>();

        public StartShiftPanel() {

            setLayout(new BorderLayout(0, 20));
            setOpaque(false);

            pnlList = new JPanel(new WrapLayout(FlowLayout.LEFT, 15, 15));
            pnlList.setOpaque(false);

            JScrollPane scroll = new JScrollPane(pnlList);

            scroll.setBorder(null);
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);
            scroll.getVerticalScrollBar().setUnitIncrement(16);

            add(scroll, BorderLayout.CENTER);

            JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
            pnlBottom.setOpaque(false);

            ModernButton btnSelectAll =
                    new ModernButton("Chọn tất cả", new Color(148, 163, 184));

            ModernButton btnDeselect =
                    new ModernButton("Bỏ chọn", new Color(148, 163, 184));

            ModernButton btnStart =
                    new ModernButton("BẮT ĐẦU CA MỚI", new Color(59, 130, 246));

            btnStart.setPreferredSize(new Dimension(220, 45));

            btnSelectAll.addActionListener(
                    e -> listCards.forEach(c -> c.setSelected(true))
            );

            btnDeselect.addActionListener(
                    e -> listCards.forEach(c -> c.setSelected(false))
            );

            btnStart.addActionListener(e -> handleBulkCheckIn());

            pnlBottom.add(btnSelectAll);
            pnlBottom.add(btnDeselect);
            pnlBottom.add(btnStart);

            add(pnlBottom, BorderLayout.SOUTH);
        }

        public void loadData(List<PhanCongCaData> dsPhanCong) {

            pnlList.removeAll();
            listCards.clear();

            for (PhanCongCaData pc : dsPhanCong) {

                NhanVienData nv =
                        NhanVienDao.getInstance().layTheoMa(pc.getMaNV());

                if (nv != null) {

                    EmployeeStartCard card =
                            new EmployeeStartCard(pc, nv);

                    listCards.add(card);
                    pnlList.add(card);
                }
            }

            pnlList.revalidate();
            pnlList.repaint();
        }

        private void handleBulkCheckIn() {

            int count = 0;

            for (EmployeeStartCard card : listCards) {

                if (card.isSelected()) {

                    try {

                        ChamCongLogic.getInstance()
                                .checkIn(card.pc.getMaPhanCong());

                        count++;

                    } catch (Exception ex) {
                    }
                }
            }

            if (count > 0) {

                JOptionPane.showMessageDialog(
                        ChamCongPopupUI.this,
                        "Đã check-in thành công cho " + count + " nhân sự!"
                );

                kiemTraTrangThaiVaHienThi();

            } else {

                JOptionPane.showMessageDialog(
                        ChamCongPopupUI.this,
                        "Vui lòng tick chọn ít nhất 1 nhân viên!",
                        "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        }
    }

    class EmployeeStartCard extends JPanel {

        public PhanCongCaData pc;
        private JCheckBox chkSelect;

        public EmployeeStartCard(PhanCongCaData pc, NhanVienData nv) {

            this.pc = pc;

            setPreferredSize(new Dimension(270, 90));
            setOpaque(false);
            setLayout(new BorderLayout(15, 0));
            setBorder(new EmptyBorder(15, 15, 15, 15));

            JPanel pnlLeft =
                    new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

            pnlLeft.setOpaque(false);

            chkSelect = new JCheckBox();

            chkSelect.setOpaque(false);
            chkSelect.setCursor(new Cursor(Cursor.HAND_CURSOR));
            chkSelect.setSelected(true);

            JPanel pnlAvatar = new JPanel() {

                @Override
                protected void paintComponent(Graphics g) {

                    Graphics2D g2 = (Graphics2D) g.create();

                    g2.setRenderingHint(
                            RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON
                    );

                    g2.setColor(new Color(226, 232, 240));

                    g2.fill(new Ellipse2D.Double(0, 0, 45, 45));

                    g2.setColor(TEXT_MUTED);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 16));

                    String initial =
                            nv.getHoTen()
                                    .substring(nv.getHoTen().lastIndexOf(" ") + 1)
                                    .substring(0, 1)
                                    .toUpperCase();

                    FontMetrics fm = g2.getFontMetrics();

                    g2.drawString(
                            initial,
                            (45 - fm.stringWidth(initial)) / 2,
                            (45 - fm.getHeight()) / 2 + fm.getAscent()
                    );

                    g2.dispose();
                }
            };

            pnlAvatar.setPreferredSize(new Dimension(45, 45));
            pnlAvatar.setOpaque(false);

            pnlLeft.add(chkSelect);
            pnlLeft.add(pnlAvatar);

            JPanel pnlInfo = new JPanel(new GridLayout(3, 1));

            pnlInfo.setOpaque(false);

            JLabel lblName = new JLabel(nv.getHoTen());

            lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));
            lblName.setForeground(TEXT_MAIN);

            JLabel lblRole = new JLabel(
                    "Chức vụ: "
                            + (nv.getChucVu() != null
                            ? nv.getChucVu().name()
                            : "N/A")
            );

            lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblRole.setForeground(TEXT_MUTED);

            JLabel lblWage = new JLabel(
                    "Lương: "
                            + String.format("%,.0f đ/h", nv.getLuongGio())
            );

            lblWage.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblWage.setForeground(new Color(59, 130, 246));

            pnlInfo.add(lblName);
            pnlInfo.add(lblRole);
            pnlInfo.add(lblWage);

            add(pnlLeft, BorderLayout.WEST);
            add(pnlInfo, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                }

                public void mousePressed(MouseEvent e) {
                    chkSelect.setSelected(!chkSelect.isSelected());
                }
            });
        }

        public boolean isSelected() {
            return chkSelect.isSelected();
        }

        public void setSelected(boolean b) {
            chkSelect.setSelected(b);
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(new Color(0, 0, 0, 8));

            g2.fillRoundRect(
                    2,
                    4,
                    getWidth() - 4,
                    getHeight() - 6,
                    15,
                    15
            );

            g2.setColor(PANEL_BG);

            g2.fillRoundRect(
                    0,
                    0,
                    getWidth() - 2,
                    getHeight() - 4,
                    15,
                    15
            );

            if (chkSelect.isSelected()) {

                g2.setColor(new Color(59, 130, 246));

                g2.setStroke(new BasicStroke(2f));

                g2.drawRoundRect(
                        0,
                        0,
                        getWidth() - 2,
                        getHeight() - 4,
                        15,
                        15
                );
            }

            g2.dispose();

            super.paintComponent(g);
        }
    }

    class EndShiftPanel extends JPanel {

        private JPanel pnlList;
        private List<EmployeeEndCard> activeCards = new ArrayList<>();
        private Timer realtimeTimer;

        public EndShiftPanel() {

            setLayout(new BorderLayout(0, 20));
            setOpaque(false);

            pnlList = new JPanel(new WrapLayout(FlowLayout.LEFT, 15, 15));
            pnlList.setOpaque(false);

            JScrollPane scroll = new JScrollPane(pnlList);

            scroll.setBorder(null);
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);

            scroll.getVerticalScrollBar().setUnitIncrement(16);

            add(scroll, BorderLayout.CENTER);

            JPanel pnlBottom = new JPanel(
                    new FlowLayout(FlowLayout.RIGHT, 15, 0)
            );

            pnlBottom.setOpaque(false);

            ModernButton btnRefresh =
                    new ModernButton("Làm Mới", new Color(148, 163, 184));

            ModernButton btnEnd =
                    new ModernButton("KẾT THÚC CA LÀM", new Color(239, 68, 68));

            btnEnd.setPreferredSize(new Dimension(220, 45));

            btnRefresh.addActionListener(
                    e -> loadData(
                            PhanCongCaDao.getInstance()
                                    .layDanhSachTheoCa(caLam.getMaCa())
                    )
            );

            btnEnd.addActionListener(e -> handleBulkCheckOut());

            pnlBottom.add(btnRefresh);
            pnlBottom.add(btnEnd);

            add(pnlBottom, BorderLayout.SOUTH);

            realtimeTimer =
                    new Timer(
                            1000,
                            e -> activeCards.forEach(
                                    EmployeeEndCard::updateRealtimeData
                            )
                    );

            realtimeTimer.start();
        }

        public void loadData(List<PhanCongCaData> dsPhanCong) {

            pnlList.removeAll();
            activeCards.clear();

            for (PhanCongCaData pc : dsPhanCong) {

                ChamCongData cc =
                        ChamCongDao.getInstance()
                                .layTheoPhanCong(pc.getMaPhanCong());

                if (cc != null && cc.getCheckOut() == null) {

                    NhanVienData nv =
                            NhanVienDao.getInstance().layTheoMa(pc.getMaNV());

                    if (nv != null) {

                        EmployeeEndCard card =
                                new EmployeeEndCard(pc, nv, cc);

                        activeCards.add(card);
                        pnlList.add(card);
                    }
                }
            }

            pnlList.revalidate();
            pnlList.repaint();
        }

        private void handleBulkCheckOut() {

            int confirm = JOptionPane.showConfirmDialog(
                    ChamCongPopupUI.this,
                    "Chốt ca và Check-out toàn bộ nhân sự?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {

                int count = 0;

                for (EmployeeEndCard card : activeCards) {

                    try {

                        ChamCongLogic.getInstance()
                                .checkOut(card.pc.getMaPhanCong());

                        count++;

                    } catch (Exception ex) {

                        System.err.println(
                                "Lỗi Check-out: " + ex.getMessage()
                        );
                    }
                }

                if (count > 0) {

                    JOptionPane.showMessageDialog(
                            ChamCongPopupUI.this,
                            "Đã chốt lương cho "
                                    + count
                                    + " nhân sự thành công!"
                    );

                    realtimeTimer.stop();

                    kiemTraTrangThaiVaHienThi();
                }
            }
        }
    }

    class EmployeeEndCard extends JPanel {

        public PhanCongCaData pc;

        private NhanVienData nv;
        private ChamCongData cc;

        private JLabel lblCurrentTime;
        private JLabel lblHours;
        private JLabel lblTempSalary;

        public EmployeeEndCard(
                PhanCongCaData pc,
                NhanVienData nv,
                ChamCongData cc
        ) {

            this.pc = pc;
            this.nv = nv;
            this.cc = cc;

            setPreferredSize(new Dimension(280, 120));

            setOpaque(false);
            setLayout(new BorderLayout());

            setBorder(new EmptyBorder(12, 15, 12, 15));

            JPanel pnlTop = new JPanel(new BorderLayout());

            pnlTop.setOpaque(false);

            JLabel lblName = new JLabel(nv.getHoTen());

            lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));
            lblName.setForeground(TEXT_MAIN);

            JLabel lblBadge = new JLabel("Đang làm");

            lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblBadge.setForeground(new Color(16, 185, 129));

            pnlTop.add(lblName, BorderLayout.WEST);
            pnlTop.add(lblBadge, BorderLayout.EAST);

            JPanel pnlMid = new JPanel(new GridLayout(2, 1));

            pnlMid.setOpaque(false);
            pnlMid.setBorder(new EmptyBorder(10, 0, 10, 0));

            JLabel lblStartTime = new JLabel(
                    "Vào ca: "
                            + cc.getCheckIn().format(
                            DateTimeFormatter.ofPattern("HH:mm:ss")
                    )
            );

            lblStartTime.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblStartTime.setForeground(TEXT_MUTED);

            lblCurrentTime = new JLabel("Hiện tại: --:--:--");

            lblCurrentTime.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblCurrentTime.setForeground(TEXT_MUTED);

            pnlMid.add(lblStartTime);
            pnlMid.add(lblCurrentTime);

            JPanel pnlBot = new JPanel(new BorderLayout());

            pnlBot.setOpaque(false);

            lblHours = new JLabel("0.00 giờ");

            lblHours.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblHours.setForeground(TEXT_MAIN);

            lblTempSalary = new JLabel("0 đ");

            lblTempSalary.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblTempSalary.setForeground(new Color(59, 130, 246));

            pnlBot.add(lblHours, BorderLayout.WEST);
            pnlBot.add(lblTempSalary, BorderLayout.EAST);

            add(pnlTop, BorderLayout.NORTH);
            add(pnlMid, BorderLayout.CENTER);
            add(pnlBot, BorderLayout.SOUTH);

            updateRealtimeData();
        }

        public void updateRealtimeData() {

            LocalDateTime now = LocalDateTime.now();

            lblCurrentTime.setText(
                    "Hiện tại: "
                            + now.format(
                            DateTimeFormatter.ofPattern("HH:mm:ss")
                    )
            );

            Duration duration =
                    Duration.between(cc.getCheckIn(), now);

            double hours =
                    Math.max(0, duration.toMinutes() / 60.0);

            lblHours.setText(
                    String.format("Đã làm: %.2f giờ", hours)
            );

            BigDecimal luongGio =
                    nv.getLuongGio() != null
                            ? nv.getLuongGio()
                            : BigDecimal.ZERO;

            BigDecimal heSo =
                    loaiCa.getHeSoLuong() != null
                            ? loaiCa.getHeSoLuong()
                            : BigDecimal.ONE;

            BigDecimal tempSalary =
                    luongGio
                            .multiply(new BigDecimal(String.valueOf(hours)))
                            .multiply(heSo)
                            .setScale(2, RoundingMode.HALF_UP);

            lblTempSalary.setText(
                    String.format("Tạm tính: %,.0f đ", tempSalary)
            );
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(new Color(0, 0, 0, 8));

            g2.fillRoundRect(
                    2,
                    4,
                    getWidth() - 4,
                    getHeight() - 6,
                    15,
                    15
            );

            g2.setColor(PANEL_BG);

            g2.fillRoundRect(
                    0,
                    0,
                    getWidth() - 2,
                    getHeight() - 4,
                    15,
                    15
            );

            g2.setColor(new Color(16, 185, 129));

            g2.fillRoundRect(
                    0,
                    0,
                    6,
                    getHeight() - 4,
                    15,
                    15
            );

            g2.dispose();

            super.paintComponent(g);
        }
    }

    private void initCompletedPanel() {

        completedPanel = new JPanel(new GridBagLayout());

        completedPanel.setOpaque(false);

        JLabel lblMsg =
                new JLabel("Ca làm này đã được chốt sổ hoàn tất!");

        lblMsg.setFont(new Font("Segoe UI", Font.BOLD, 22));

        lblMsg.setForeground(new Color(16, 185, 129));

        completedPanel.add(lblMsg);
    }

    class ModernButton extends JButton {

        private Color bgColor;

        public ModernButton(String text, Color bg) {

            super(text);

            this.bgColor = bg;

            setForeground(Color.WHITE);

            setFont(new Font("Segoe UI", Font.BOLD, 14));

            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);

            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(
                    getModel().isRollover()
                            ? bgColor.darker()
                            : bgColor
            );

            g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    12,
                    12
            );

            g2.dispose();

            super.paintComponent(g);
        }
    }

    class WrapLayout extends FlowLayout {

        public WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            return layoutSize(target, false);
        }

        private Dimension layoutSize(Container target, boolean preferred) {

            synchronized (target.getTreeLock()) {

                int targetWidth = target.getSize().width;

                if (targetWidth == 0) {
                    targetWidth = Integer.MAX_VALUE;
                }

                int hgap = getHgap();
                int vgap = getVgap();

                Insets insets = target.getInsets();

                int maxWidth =
                        targetWidth
                                - (insets.left + insets.right + hgap * 2);

                Dimension dim = new Dimension(0, 0);

                int rowWidth = 0;
                int rowHeight = 0;

                for (int i = 0; i < target.getComponentCount(); i++) {

                    Component m = target.getComponent(i);

                    if (m.isVisible()) {

                        Dimension d =
                                preferred
                                        ? m.getPreferredSize()
                                        : m.getMinimumSize();

                        if (rowWidth + d.width > maxWidth) {

                            dim.width = Math.max(dim.width, rowWidth);

                            dim.height += rowHeight + vgap;

                            rowWidth = 0;
                            rowHeight = 0;
                        }

                        rowWidth += d.width + hgap;

                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }

                dim.width = Math.max(dim.width, rowWidth);

                dim.height += rowHeight;

                dim.width +=
                        insets.left + insets.right + hgap * 2;

                dim.height +=
                        insets.top + insets.bottom + vgap * 2;

                return dim;
            }
        }
    }
}