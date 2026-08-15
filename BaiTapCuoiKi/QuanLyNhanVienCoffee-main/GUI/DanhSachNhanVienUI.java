import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * TRUNG TÂM QUẢN TRỊ NHÂN SỰ (HRM DASHBOARD) - MODERN UI
 * Nâng cấp: Thêm Analytics, Mini Charts, Avatar, Empty State, Hover Effects.
 */
public class DanhSachNhanVienUI extends JPanel {

    
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    
    private static final Color BG_MAIN        = new Color(248, 250, 252);
    private static final Color BORDER_COLOR   = new Color(226, 232, 240);
    private static final Color SELECTED_COLOR = new Color(239, 246, 255);
    private static final Color SELECT_BORDER  = new Color(59, 130, 246);
    private static final Color MAU_CHINH      = new Color(59, 130, 246);
    private static final Color TEXT_MAIN      = new Color(30, 41, 59);
    private static final Color TEXT_MUTED     = new Color(100, 116, 139);
    
    private static final Font  FONT_DAM       = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font  FONT_CHINH     = new Font("Segoe UI", Font.PLAIN, 14);
    
    private final int W_CHECK = 35;
    private final int W_MA    = 80;
    private final int W_TEN   = 350; 
    private final int W_SDT   = 110;
    private final int W_CV    = 150;
    private final int W_LUONG = 130;
    private final int W_TT    = 130;
    private final int H_GAP   = 10;

    
    private List<NhanVienData> danhSachGoc         = new ArrayList<>();
    private List<NhanVienData> currentDisplayedList= new ArrayList<>();
    private List<NhanVienData> selectedList        = new ArrayList<>();
    private String currentRoleFilter = "Tất cả";
    private boolean isLoading = false;

    
    private JTextField txtTimKiem;
    private PillMenu tabChucVu;
    private JPanel pnlRowListContainer;
    private JComboBox<String> cbFilterTrangThai;
    private ModernCheckBox cbSelectAll;
    private JButton btnNghiViec;
    
    
    private JLabel lblTyLeActive, lblQuyLuong, lblNhanVienMoi, lblTyLeNghi;
    private JPanel pnlRoleDistribution, pnlTopEarners;

    public DanhSachNhanVienUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        initUI();
        loadData();
        setupListeners();
    }

    private String formatNgay(LocalDate date, String defaultVal) {
        return date != null ? date.format(DATE_FMT) : defaultVal;
    }

    private String removeAccents(String text) {
        if (text == null) return "";
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").toLowerCase().replace("đ", "d");
    }

    
    
    
    private void initUI() {
        
        JPanel pnlNorth = new JPanel(new BorderLayout(20, 15));
        pnlNorth.setOpaque(false);
        
        
        JLabel lblPageTitle = new JLabel("Trung Tâm Quản Trị Nhân Sự");
        lblPageTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblPageTitle.setForeground(TEXT_MAIN);
        pnlNorth.add(lblPageTitle, BorderLayout.NORTH);

        JPanel pnlFilterBar = new JPanel(new BorderLayout(20, 0));
        pnlFilterBar.setOpaque(false);

        
        txtTimKiem = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(148, 163, 184)); 
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString("🔍 Tìm mã, tên, SĐT...", 15, (getHeight() - fm.getHeight()) / 2 + fm.getAscent()); 
                    g2.dispose();
                }
            }
        };
        txtTimKiem.setFont(FONT_CHINH.deriveFont(15f));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(0, 15, 0, 15)));
        txtTimKiem.setPreferredSize(new Dimension(320, 42));
        pnlFilterBar.add(txtTimKiem, BorderLayout.WEST);

        
        List<String> tabs = List.of("Tất cả", "QUAN_LY", "PHA_CHE", "PHUC_VU", "THU_NGAN", "BAO_VE");
        tabChucVu = new PillMenu(tabs, tab -> { currentRoleFilter = tab; filterData(); });

        cbFilterTrangThai = new JComboBox<>(new String[]{"Tất cả trạng thái", "DANG_LAM", "NGHI_PHEP", "NGHI_VIEC"});
        cbFilterTrangThai.setFont(FONT_CHINH);
        cbFilterTrangThai.setPreferredSize(new Dimension(170, 42));
        cbFilterTrangThai.setBackground(Color.WHITE);
        cbFilterTrangThai.addActionListener(e -> filterData());

        JPanel pnlFilterRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlFilterRight.setOpaque(false);
        pnlFilterRight.add(cbFilterTrangThai);
        pnlFilterRight.add(tabChucVu);
        pnlFilterBar.add(pnlFilterRight, BorderLayout.EAST);
        
        pnlNorth.add(pnlFilterBar, BorderLayout.CENTER);

        
        JPanel pnlCenter = new JPanel(new BorderLayout(0, 10));
        pnlCenter.setOpaque(false);
        pnlCenter.add(createFakeHeader(), BorderLayout.NORTH);

        pnlRowListContainer = new JPanel();
        pnlRowListContainer.setLayout(new BoxLayout(pnlRowListContainer, BoxLayout.Y_AXIS));
        pnlRowListContainer.setBackground(BG_MAIN);

        JScrollPane scrollPane = new JScrollPane(pnlRowListContainer);
        scrollPane.getViewport().setBackground(BG_MAIN);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        pnlCenter.add(scrollPane, BorderLayout.CENTER);

        add(pnlNorth,           BorderLayout.NORTH);
        add(pnlCenter,          BorderLayout.CENTER);
        add(createDashboardPanel(), BorderLayout.EAST); 
        add(createActionButtons(),  BorderLayout.SOUTH);
    }

    private JPanel createFakeHeader() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, H_GAP, 12));
        header.setBackground(new Color(241, 245, 249));
        header.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        header.setPreferredSize(new Dimension(0, 45));

        cbSelectAll = new ModernCheckBox();
        cbSelectAll.setPreferredSize(new Dimension(W_CHECK, 20));
        cbSelectAll.addActionListener(e -> handleSelectAll(cbSelectAll.isSelected()));
        
        header.add(cbSelectAll);
        header.add(createHeaderLabel("Mã NV",        W_MA,    SwingConstants.LEFT));
        header.add(createHeaderLabel("Nhân Viên",   W_TEN,   SwingConstants.LEFT));
        header.add(createHeaderLabel("Liên hệ",      W_SDT,   SwingConstants.LEFT));
        header.add(createHeaderLabel("Vai trò",      W_CV,    SwingConstants.LEFT));
        header.add(createHeaderLabel("Lương/Giờ",    W_LUONG, SwingConstants.LEFT));
        header.add(createHeaderLabel("Trạng thái",   W_TT,    SwingConstants.CENTER)); 
        
        return header;
    }

    private JLabel createHeaderLabel(String text, int width, int alignment) {
        JLabel lbl = new JLabel(text, alignment);
        lbl.setFont(FONT_DAM.deriveFont(13f));
        lbl.setForeground(TEXT_MUTED);
        lbl.setPreferredSize(new Dimension(width, 20));
        return lbl;
    }

    
    
    
    private JScrollPane createDashboardPanel() {
        JPanel pnlDash = new JPanel();
        pnlDash.setLayout(new BoxLayout(pnlDash, BoxLayout.Y_AXIS));
        pnlDash.setOpaque(false);
        pnlDash.setBorder(new EmptyBorder(0, 15, 0, 0));
        
        
        JScrollPane scrollDash = new JScrollPane(pnlDash);
        scrollDash.setPreferredSize(new Dimension(320, 0));
        scrollDash.setBorder(null);
        scrollDash.setOpaque(false);
        scrollDash.getViewport().setOpaque(false);
        scrollDash.getVerticalScrollBar().setUnitIncrement(16);

        
        JPanel cardQuyLuong = new GradientCard(new Color(59, 130, 246), new Color(124, 58, 237));
        cardQuyLuong.setLayout(new BoxLayout(cardQuyLuong, BoxLayout.Y_AXIS));
        cardQuyLuong.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel lblT1 = new JLabel("Tổng quỹ lương/giờ");
        lblT1.setForeground(new Color(255, 255, 255, 200));
        lblT1.setFont(FONT_CHINH.deriveFont(13f));
        lblQuyLuong = new JLabel("0 đ/h");
        lblQuyLuong.setForeground(Color.WHITE);
        lblQuyLuong.setFont(new Font("Segoe UI", Font.BOLD, 26));
        cardQuyLuong.add(lblT1); cardQuyLuong.add(Box.createVerticalStrut(5)); cardQuyLuong.add(lblQuyLuong);
        pnlDash.add(cardQuyLuong);
        pnlDash.add(Box.createVerticalStrut(15));

        
        JPanel cardTyLe = createWhiteCard();
        cardTyLe.setLayout(new BorderLayout(0, 10));
        JLabel lblT2 = new JLabel("Nhân sự đang hoạt động");
        lblT2.setFont(FONT_DAM.deriveFont(14f));
        lblT2.setForeground(TEXT_MAIN);
        lblTyLeActive = new JLabel("0% (0 nhân sự)");
        lblTyLeActive.setFont(FONT_DAM.deriveFont(16f));
        lblTyLeActive.setForeground(new Color(16, 185, 129));
        cardTyLe.add(lblT2, BorderLayout.NORTH);
        cardTyLe.add(lblTyLeActive, BorderLayout.CENTER);
        pnlDash.add(cardTyLe);
        pnlDash.add(Box.createVerticalStrut(15));

        
        JPanel pnlMiniGrid = new JPanel(new GridLayout(1, 2, 15, 0));
        pnlMiniGrid.setOpaque(false);
        pnlMiniGrid.setMaximumSize(new Dimension(320, 90));
        
        JPanel cardMoi = createWhiteCard();
        cardMoi.setLayout(new BorderLayout());
        JLabel lblT3 = new JLabel("Mới tháng này"); lblT3.setFont(FONT_CHINH.deriveFont(12f)); lblT3.setForeground(TEXT_MUTED);
        lblNhanVienMoi = new JLabel("+0"); lblNhanVienMoi.setFont(FONT_DAM.deriveFont(22f)); lblNhanVienMoi.setForeground(MAU_CHINH);
        cardMoi.add(lblT3, BorderLayout.NORTH); cardMoi.add(lblNhanVienMoi, BorderLayout.CENTER);
        
        JPanel cardNghi = createWhiteCard();
        cardNghi.setLayout(new BorderLayout());
        JLabel lblT4 = new JLabel("Tỷ lệ nghỉ việc"); lblT4.setFont(FONT_CHINH.deriveFont(12f)); lblT4.setForeground(TEXT_MUTED);
        lblTyLeNghi = new JLabel("0%"); lblTyLeNghi.setFont(FONT_DAM.deriveFont(22f)); lblTyLeNghi.setForeground(new Color(239, 68, 68));
        cardNghi.add(lblT4, BorderLayout.NORTH); cardNghi.add(lblTyLeNghi, BorderLayout.CENTER);
        
        pnlMiniGrid.add(cardMoi); pnlMiniGrid.add(cardNghi);
        pnlDash.add(pnlMiniGrid);
        pnlDash.add(Box.createVerticalStrut(15));

        
        JPanel cardPhanBo = createWhiteCard();
        cardPhanBo.setLayout(new BorderLayout(0, 10));
        JLabel lblT5 = new JLabel("Phân bổ nhân sự");
        lblT5.setFont(FONT_DAM.deriveFont(14f));
        lblT5.setForeground(TEXT_MAIN);
        pnlRoleDistribution = new JPanel();
        pnlRoleDistribution.setLayout(new BoxLayout(pnlRoleDistribution, BoxLayout.Y_AXIS));
        pnlRoleDistribution.setOpaque(false);
        cardPhanBo.add(lblT5, BorderLayout.NORTH);
        cardPhanBo.add(pnlRoleDistribution, BorderLayout.CENTER);
        pnlDash.add(cardPhanBo);
        pnlDash.add(Box.createVerticalStrut(15));

        
        JPanel cardTop = createWhiteCard();
        cardTop.setLayout(new BorderLayout(0, 10));
        JLabel lblT6 = new JLabel("Top thu nhập (Giờ)");
        lblT6.setFont(FONT_DAM.deriveFont(14f));
        lblT6.setForeground(new Color(245, 158, 11));
        pnlTopEarners = new JPanel();
        pnlTopEarners.setLayout(new BoxLayout(pnlTopEarners, BoxLayout.Y_AXIS));
        pnlTopEarners.setOpaque(false);
        cardTop.add(lblT6, BorderLayout.NORTH);
        cardTop.add(pnlTopEarners, BorderLayout.CENTER);
        pnlDash.add(cardTop);

        return scrollDash;
    }

    private JPanel createWhiteCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        return card;
    }

    class GradientCard extends JPanel {
        private Color c1, c2;
        public GradientCard(Color c1, Color c2) { this.c1 = c1; this.c2 = c2; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            
            g2.setColor(new Color(255, 255, 255, 30));
            g2.fillOval(-20, -20, 100, 100);
            g2.dispose();
        }
    }

    
    
    
    private JPanel createActionButtons() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setOpaque(false);
        pnl.setBorder(new EmptyBorder(15, 0, 0, 0));

        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlLeft.setOpaque(false);
        
        JButton btnReload = taoNutNho("Làm mới", new Color(100, 116, 139), false);
        btnReload.addActionListener(e -> handleReload());
        pnlLeft.add(btnReload);

        
        JButton btnPhanCa = taoNutNho("Phân Ca", new Color(139, 92, 246), true);
        btnPhanCa.addActionListener(e -> {
            Window parent = SwingUtilities.getWindowAncestor(this);
            if (parent instanceof TrangChuUI) {
                ((TrangChuUI) parent).chuyenSangTabCaLam();
            }
        });
        pnlLeft.add(btnPhanCa);

        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlRight.setOpaque(false);

        btnNghiViec = taoNut("Báo Nghỉ Việc", new Color(239, 68, 68));
        btnNghiViec.addActionListener(e -> handleNghiViec());
        btnNghiViec.setVisible(false);

        JButton btnThem = taoNut("+ Thêm Nhân Viên Mới", MAU_CHINH);
        btnThem.addActionListener(e -> handleThem());

        pnlRight.add(btnNghiViec);
        pnlRight.add(btnThem);

        pnl.add(pnlLeft,  BorderLayout.WEST);
        pnl.add(pnlRight, BorderLayout.EAST);
        return pnl;
    }

    private JButton taoNut(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? color.darker() : color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_DAM.deriveFont(15f));
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(190, 42));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton taoNutNho(String text, Color color, boolean isOutline) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isOutline) {
                    g2.setColor(getModel().isRollover() ? color : Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(color);
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                } else {
                    g2.setColor(getModel().isRollover() ? color.darker() : color);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(isOutline ? color : Color.WHITE);
        
        
        if (isOutline) {
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { btn.setForeground(Color.WHITE); }
                public void mouseExited(MouseEvent e) { btn.setForeground(color); }
            });
        }
        
        btn.setFont(FONT_DAM.deriveFont(14f));
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(110, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    
    
    
    private void renderList(List<NhanVienData> data) {
        pnlRowListContainer.removeAll();
        if (isLoading) {
            for (int i = 0; i < 5; i++) pnlRowListContainer.add(createSkeletonRow());
        } else if (data.isEmpty()) {
            pnlRowListContainer.add(createEmptyState());
        } else {
            for (NhanVienData nv : data) {
                pnlRowListContainer.add(new EmployeeRowPanel(nv));
                pnlRowListContainer.add(Box.createRigidArea(new Dimension(0, 8)));
            }
        }
        pnlRowListContainer.revalidate();
        pnlRowListContainer.repaint();
    }

    private JPanel createEmptyState() {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setOpaque(false);
        pnl.setPreferredSize(new Dimension(800, 400));
        
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        
        JLabel lblIcon = new JLabel("📭");
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 60));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblTxt = new JLabel("Không tìm thấy nhân viên nào");
        lblTxt.setFont(FONT_DAM.deriveFont(18f));
        lblTxt.setForeground(TEXT_MAIN);
        lblTxt.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSub = new JLabel("Hãy thử thay đổi bộ lọc hoặc thêm nhân sự mới");
        lblSub.setFont(FONT_CHINH);
        lblSub.setForeground(TEXT_MUTED);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        center.add(lblIcon); center.add(Box.createVerticalStrut(10));
        center.add(lblTxt); center.add(Box.createVerticalStrut(5));
        center.add(lblSub);
        
        pnl.add(center);
        return pnl;
    }

    
    class EmployeeRowPanel extends JPanel {
        private NhanVienData nv;
        private boolean isHovered = false;
        private ModernCheckBox cb;

        public EmployeeRowPanel(NhanVienData nv) {
            this.nv = nv;
            boolean isNghiViec = nv.getTrangThai() == NhanVienData.TrangThaiNV.NGHI_VIEC;
            
            setLayout(new BorderLayout());
            setOpaque(false);
            int rowH = 70; 
            setPreferredSize(new Dimension(0, rowH));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, rowH));

            
            JPanel content = new JPanel(new FlowLayout(FlowLayout.LEFT, H_GAP, 18));
            content.setOpaque(false);

            
            cb = new ModernCheckBox();
            cb.setPreferredSize(new Dimension(W_CHECK, 25));
            cb.setSelected(selectedList.contains(nv));
            cb.addActionListener(e -> handleSelection(nv, cb.isSelected(), this));
            content.add(cb);

            
            content.add(createCell(nv.getMaNV() != null ? nv.getMaNV() : "—", W_MA, false, TEXT_MUTED));

            
            JPanel pnlName = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
            pnlName.setOpaque(false);
            pnlName.setPreferredSize(new Dimension(W_TEN, 40));
            
            
            JPanel pnlAvatar = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(224, 231, 255)); 
                    g2.fill(new Ellipse2D.Double(0, 0, 36, 36));
                    
                    
                    Color statusColor = TEXT_MUTED;
                    if(nv.getTrangThai() == NhanVienData.TrangThaiNV.DANG_LAM) statusColor = new Color(16, 185, 129);
                    else if(nv.getTrangThai() == NhanVienData.TrangThaiNV.NGHI_PHEP) statusColor = new Color(245, 158, 11);
                    else if(nv.getTrangThai() == NhanVienData.TrangThaiNV.NGHI_VIEC) statusColor = new Color(239, 68, 68);
                    
                    g2.setColor(Color.WHITE); g2.fillOval(26, 26, 12, 12); 
                    g2.setColor(statusColor); g2.fillOval(28, 28, 8, 8);   
                    
                    
                    g2.setColor(new Color(67, 56, 202));
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                    String name = nv.getHoTen() != null ? nv.getHoTen().trim() : "U";
                    String initial = name.substring(name.lastIndexOf(" ") + 1).substring(0, 1).toUpperCase();
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(initial, (36-fm.stringWidth(initial))/2, (36-fm.getHeight())/2 + fm.getAscent());
                    g2.dispose();
                }
            };
            pnlAvatar.setPreferredSize(new Dimension(40, 40));
            pnlAvatar.setOpaque(false);
            
            JPanel pnlText = new JPanel(new GridLayout(2, 1));
            pnlText.setOpaque(false);
            JLabel lblName = new JLabel(nv.getHoTen() != null ? nv.getHoTen() : "—");
            lblName.setFont(FONT_DAM.deriveFont(15f)); lblName.setForeground(TEXT_MAIN);
            JLabel lblEmail = new JLabel(nv.getNgayVaoLam() != null ? "Vào làm: " + formatNgay(nv.getNgayVaoLam(), "") : "");
            lblEmail.setFont(FONT_CHINH.deriveFont(12f)); lblEmail.setForeground(TEXT_MUTED);
            pnlText.add(lblName); pnlText.add(lblEmail);
            
            pnlName.add(pnlAvatar); pnlName.add(pnlText);
            content.add(pnlName);

            
            content.add(createCell(nv.getSDT() != null ? nv.getSDT() : "—", W_SDT, false, TEXT_MAIN));

            
            String cv = nv.getChucVu() != null ? nv.getChucVu().toString() : "—";
            content.add(createCell(cv, W_CV, true, TEXT_MAIN));

            
            String luong = nv.getLuongGio() != null ? String.format("%,.0f đ/h", nv.getLuongGio().doubleValue()) : "—";
            content.add(createCell(luong, W_LUONG, true, new Color(16, 185, 129)));

            
            content.add(createTrangThaiBadge(nv.getTrangThai()));

            
            if (!isNghiViec) {
                JButton btnEdit = new JButton("Sửa") {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(getModel().isRollover() ? new Color(226, 232, 240) : Color.WHITE);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                        g2.setColor(BORDER_COLOR);
                        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                btnEdit.setFont(FONT_DAM.deriveFont(13f));
                btnEdit.setForeground(TEXT_MAIN);
                btnEdit.setContentAreaFilled(false); btnEdit.setBorderPainted(false); btnEdit.setFocusPainted(false);
                btnEdit.setPreferredSize(new Dimension(80, 32));
                btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btnEdit.addActionListener(e -> handleSua(nv));
                content.add(Box.createHorizontalStrut(20));
                content.add(btnEdit);
            }

            add(content, BorderLayout.CENTER);

            
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
                @Override public void mouseClicked(MouseEvent e) { cb.doClick(); }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            boolean isSel = selectedList.contains(nv);
            
            
            if (isHovered && !isSel) {
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(2, 4, getWidth()-4, getHeight()-6, 15, 15);
            }

            g2.setColor(isSel ? SELECTED_COLOR : Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth()-2, getHeight()-4, 15, 15);
            
            if (isSel) {
                g2.setColor(SELECT_BORDER);
                g2.fillRoundRect(0, 0, 6, getHeight()-4, 15, 15); 
                g2.drawRoundRect(0, 0, getWidth()-3, getHeight()-5, 15, 15);
            } else {
                g2.setColor(isHovered ? new Color(148, 163, 184) : BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-3, getHeight()-5, 15, 15);
            }
            
            if (nv.getTrangThai() == NhanVienData.TrangThaiNV.NGHI_VIEC) {
                g2.setColor(new Color(255, 255, 255, 180));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private JLabel createCell(String text, int width, boolean bold, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(bold ? FONT_DAM.deriveFont(14f) : FONT_CHINH.deriveFont(14f));
        lbl.setForeground(color);
        lbl.setPreferredSize(new Dimension(width, 25));
        return lbl;
    }

    private JPanel createTrangThaiBadge(NhanVienData.TrangThaiNV trangThai) {
        Color color; String text;
        if (trangThai == null) { color = TEXT_MUTED; text = "Không rõ"; }
        else switch (trangThai) {
            case DANG_LAM:  color = new Color(16, 185, 129); text = "Đang làm"; break;
            case NGHI_PHEP: color = new Color(245, 158, 11); text = "Nghỉ phép"; break;
            case NGHI_VIEC: color = new Color(239, 68, 68);  text = "Đã nghỉ"; break;
            default:        color = TEXT_MUTED; text = "Không rõ";
        }
        
        JPanel pill = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        pill.setOpaque(false);
        pill.setPreferredSize(new Dimension(90, 26)); 

        JLabel lbl = new JLabel("● " + text, SwingConstants.CENTER);
        lbl.setFont(FONT_DAM.deriveFont(12f));
        lbl.setForeground(color);
        pill.add(lbl, BorderLayout.CENTER);

        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); 
        container.setOpaque(false);
        container.setPreferredSize(new Dimension(W_TT, 30));
        container.add(pill); 
        return container;
    }

    private JPanel createSkeletonRow() {
        JPanel skeleton = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                g2.dispose();
            }
        };
        skeleton.setOpaque(false);
        skeleton.setPreferredSize(new Dimension(0, 70));
        skeleton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        return skeleton;
    }

    
    
    
    private void loadData() {
        isLoading = true;
        renderList(new ArrayList<>());

        SwingWorker<List<NhanVienData>, Void> worker = new SwingWorker<>() {
            @Override protected List<NhanVienData> doInBackground() {
                return NhanVienLogic.getInstance().layDanhSach(); 
            }
            @Override protected void done() {
                try {
                    danhSachGoc = get();
                    isLoading   = false;
                    selectedList.clear();
                    updateStats();
                    filterData();
                } catch (Exception e) { e.printStackTrace(); isLoading = false; }
            }
        };
        worker.execute();
    }

    private void filterData() {
        if (isLoading) return;
        String key = removeAccents(txtTimKiem.getText().trim());

        currentDisplayedList = danhSachGoc.stream().filter(nv -> {
            boolean matchRole = currentRoleFilter.equals("Tất cả") ||
                    (nv.getChucVu() != null && nv.getChucVu().toString().equals(currentRoleFilter));

            String cboVal = cbFilterTrangThai.getSelectedItem().toString();
            boolean matchTT = cboVal.equals("Tất cả trạng thái") ||
                    (nv.getTrangThai() != null && nv.getTrangThai().toString().equals(cboVal));

            String maNV = removeAccents(nv.getMaNV());
            String hoTen = removeAccents(nv.getHoTen());
            String sdt = nv.getSDT() != null ? nv.getSDT() : "";

            boolean matchKey = key.isEmpty() || maNV.contains(key) || hoTen.contains(key) || sdt.contains(key);
            return matchRole && matchTT && matchKey;
        }).sorted((a, b) -> {
            int sa = a.getTrangThai() == NhanVienData.TrangThaiNV.NGHI_VIEC ? 1 : 0;
            int sb = b.getTrangThai() == NhanVienData.TrangThaiNV.NGHI_VIEC ? 1 : 0;
            return Integer.compare(sa, sb);
        }).collect(Collectors.toList());

        renderList(currentDisplayedList);
        updateSelectionUIState();
    }

    private void updateStats() {
        if (danhSachGoc.isEmpty()) return;

        double total = danhSachGoc.size();
        double active = danhSachGoc.stream().filter(n -> n.getTrangThai() != NhanVienData.TrangThaiNV.NGHI_VIEC).count();
        double nghiViec = total - active;

        
        int percentActive = (int) ((active / total) * 100);
        lblTyLeActive.setText(percentActive + "% (" + (int)active + " nhân sự)");

        
        BigDecimal tongLuong = danhSachGoc.stream()
                .filter(n -> n.getTrangThai() != NhanVienData.TrangThaiNV.NGHI_VIEC && n.getLuongGio() != null)
                .map(NhanVienData::getLuongGio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        lblQuyLuong.setText(String.format("%,.0f đ/h", tongLuong));

        
        long moiThangNay = danhSachGoc.stream().filter(n -> n.getNgayVaoLam() != null && 
            n.getNgayVaoLam().getMonthValue() == LocalDate.now().getMonthValue() && 
            n.getNgayVaoLam().getYear() == LocalDate.now().getYear()).count();
        lblNhanVienMoi.setText("+" + moiThangNay);
        
        int percentNghi = (int) ((nghiViec / total) * 100);
        lblTyLeNghi.setText(percentNghi + "%");

        
        pnlRoleDistribution.removeAll();
        Map<NhanVienData.ChucVuNV, Long> roles = danhSachGoc.stream()
            .filter(n -> n.getChucVu() != null && n.getTrangThai() != NhanVienData.TrangThaiNV.NGHI_VIEC)
            .collect(Collectors.groupingBy(NhanVienData::getChucVu, Collectors.counting()));
        
        for (Map.Entry<NhanVienData.ChucVuNV, Long> entry : roles.entrySet()) {
            double ratio = entry.getValue() / active;
            JPanel barPanel = createMiniBar(entry.getKey().toString(), entry.getValue(), ratio);
            pnlRoleDistribution.add(barPanel);
            pnlRoleDistribution.add(Box.createVerticalStrut(8));
        }

        
        pnlTopEarners.removeAll();
        List<NhanVienData> topEarners = danhSachGoc.stream()
            .filter(n -> n.getLuongGio() != null && n.getTrangThai() != NhanVienData.TrangThaiNV.NGHI_VIEC)
            .sorted(Comparator.comparing(NhanVienData::getLuongGio).reversed())
            .limit(3)
            .collect(Collectors.toList());
            
        int rank = 1;
        for (NhanVienData nv : topEarners) {
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setOpaque(false);
            JLabel lblRank = new JLabel("#" + rank + " " + nv.getHoTen());
            lblRank.setFont(FONT_DAM.deriveFont(13f));
            JLabel lblL = new JLabel(String.format("%,.0f đ", nv.getLuongGio()));
            lblL.setFont(FONT_CHINH.deriveFont(13f)); lblL.setForeground(TEXT_MUTED);
            row.add(lblRank, BorderLayout.WEST); row.add(lblL, BorderLayout.EAST);
            pnlTopEarners.add(row); pnlTopEarners.add(Box.createVerticalStrut(10));
            rank++;
        }

        pnlRoleDistribution.revalidate(); pnlRoleDistribution.repaint();
        pnlTopEarners.revalidate(); pnlTopEarners.repaint();
    }

    private JPanel createMiniBar(String name, long count, double ratio) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setOpaque(false);
        JLabel lbl = new JLabel(name + " (" + count + ")");
        lbl.setFont(FONT_CHINH.deriveFont(12f)); lbl.setForeground(TEXT_MUTED);
        
        JPanel bar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(241, 245, 249));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(99, 102, 241)); 
                g2.fillRoundRect(0, 0, (int)(getWidth() * ratio), getHeight(), 8, 8);
                g2.dispose();
            }
        };
        bar.setPreferredSize(new Dimension(0, 8));
        bar.setOpaque(false);
        
        pnl.add(lbl, BorderLayout.NORTH); pnl.add(Box.createVerticalStrut(3), BorderLayout.CENTER); pnl.add(bar, BorderLayout.SOUTH);
        return pnl;
    }

    
    
    
    private void setupListeners() {
        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterData(); }
            public void removeUpdate(DocumentEvent e) { filterData(); }
            public void changedUpdate(DocumentEvent e) { filterData(); }
        });
    }

    private void handleSelection(NhanVienData nv, boolean sel, JPanel row) {
        if (sel) { if (!selectedList.contains(nv)) selectedList.add(nv); }
        else { selectedList.remove(nv); }
        row.repaint();
        updateSelectionUIState();
    }

    private void handleSelectAll(boolean sel) {
        selectedList.clear();
        if (sel) selectedList.addAll(currentDisplayedList);
        renderList(currentDisplayedList);
        updateSelectionUIState();
    }

    private void updateSelectionUIState() {
        boolean has = !selectedList.isEmpty();
        if (btnNghiViec != null) btnNghiViec.setVisible(has);

        if (currentDisplayedList.isEmpty()) cbSelectAll.setSelected(false);
        else {
            boolean all = currentDisplayedList.stream().allMatch(selectedList::contains);
            cbSelectAll.setSelected(all);
        }
        cbSelectAll.repaint();
    }

    private void handleReload() {
        txtTimKiem.setText("");
        tabChucVu.setActiveTab("Tất cả"); currentRoleFilter = "Tất cả";
        cbFilterTrangThai.setSelectedIndex(0);
        loadData();
    }

    private void handleThem() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        ThemNhanVienDialog dialog = new ThemNhanVienDialog(parent);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Thêm nhân sự mới thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData(); 
        }
    }

    private void handleSua(NhanVienData nv) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        SuaNhanVienDialog dialog = new SuaNhanVienDialog(parent, nv);
        dialog.setVisible(true);
        if (dialog.isSuccess()) loadData();
    }

    private void handleNghiViec() {
        if (selectedList.isEmpty()) return;
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Xác nhận chốt trạng thái NGHỈ VIỆC và TÍNH LƯƠNG cuối cho " + selectedList.size() + " nhân sự?", 
                "Xác nhận", JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            int count = 0;
            for (NhanVienData nv : selectedList) {
                try {
                    
                    nv.setTrangThai(NhanVienData.TrangThaiNV.NGHI_VIEC);
                    nv.setNgayNghiViec(LocalDate.now()); 
                    
                    
                    NhanVienLogic.getInstance().sua(nv); 
                    
                    
                    
                    
                    count++;
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xử lý nhân viên " + nv.getMaNV() + ": " + e.getMessage());
                }
            }
            JOptionPane.showMessageDialog(this, "Đã chốt nghỉ việc cho " + count + " hồ sơ. Vui lòng kiểm tra bảng lương để thanh toán.");
            loadData();
        }
    }

    
    
    
    private class ModernCheckBox extends JCheckBox {
        public ModernCheckBox() { setOpaque(false); setCursor(new Cursor(Cursor.HAND_CURSOR)); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int size = 18, y = (getHeight()-size)/2, x = (getWidth()-size)/2;
            if (isSelected()) {
                g2.setColor(MAU_CHINH); g2.fillRoundRect(x, y, size, size, 5, 5);
                g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(x+4, y+9, x+8, y+13); g2.drawLine(x+8, y+13, x+14, y+5);
            } else {
                g2.setColor(Color.WHITE); g2.fillRoundRect(x, y, size, size, 5, 5);
                g2.setColor(new Color(180, 185, 195)); g2.drawRoundRect(x, y, size, size, 5, 5);
            }
            g2.dispose();
        }
    }

    private class PillMenu extends JPanel {
        private List<JButton> btns = new ArrayList<>();
        public PillMenu(List<String> items, java.util.function.Consumer<String> onSelect) {
            setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0)); setOpaque(false);
            for (String item : items) {
                JButton btn = new JButton(item) {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        if (Boolean.TRUE.equals(getClientProperty("active"))) g2.setColor(MAU_CHINH);
                        else {
                            g2.setColor(Color.WHITE); g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                            g2.setColor(BORDER_COLOR);
                        }
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                        g2.dispose(); super.paintComponent(g);
                    }
                };
                btn.putClientProperty("active", item.equals(items.get(0)));
                btn.setFont(FONT_DAM.deriveFont(13f));
                btn.setForeground(item.equals(items.get(0)) ? Color.WHITE : TEXT_MUTED);
                btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
                btn.setBorder(new EmptyBorder(7, 14, 7, 14)); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.addActionListener(e -> { setActiveTab(item); onSelect.accept(item); });
                btns.add(btn); add(btn);
            }
        }
        public void setActiveTab(String name) {
            for (JButton b : btns) {
                boolean active = b.getText().equals(name);
                b.putClientProperty("active", active);
                b.setForeground(active ? Color.WHITE : TEXT_MUTED); b.repaint();
            }
        }
    }
}
