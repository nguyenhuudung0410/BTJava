import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Objects;

public class NhanVienData {

    // ── Enum trạng thái ──────────────────────────────────
    public enum TrangThaiNV {
        DANG_LAM("Đang làm"),
        NGHI_VIEC("Nghỉ việc"),
        NGHI_PHEP("Nghỉ phép");

        private final String tenHienThi;
        TrangThaiNV(String tenHienThi) { this.tenHienThi = tenHienThi; }

        @Override
        public String toString() { return tenHienThi; }
    }

    // ── Enum chức vụ ─────────────────────────────────────
    public enum ChucVuNV {
        QUAN_LY("Quản lý"),
        PHA_CHE("Pha chế"),
        PHUC_VU("Phục vụ"),
        THU_NGAN("Thu ngân"),
        BAO_VE("Bảo vệ");

        private final String tenHienThi;
        ChucVuNV(String tenHienThi) { this.tenHienThi = tenHienThi; }

        @Override
        public String toString() { return tenHienThi; }
    }

    // ── Thông tin định danh ──────────────────────────────
    private String maNV;
    private String hoTen;
    private String cccd;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String diaChi;

    // ── Liên hệ ─────────────────────────────────────────
    private String sdt;
    private String email;

    // ── Công việc ────────────────────────────────────────
    private ChucVuNV chucVu;
    private TrangThaiNV trangThai;
    private LocalDate ngayVaoLam;
    private LocalDate ngayNghiViec;

    // ── Lương ────────────────────────────────────────────
    private BigDecimal luongGio;
    private BigDecimal luongThuong;

    // ── Constructor từ Builder ───────────────────────────
    private NhanVienData(ThoXayNhanVien builder) {
        this.maNV         = builder.maNV;
        this.hoTen        = builder.hoTen;
        this.cccd         = builder.cccd;
        this.ngaySinh     = builder.ngaySinh;
        this.gioiTinh     = builder.gioiTinh;
        this.diaChi       = builder.diaChi;
        this.sdt          = builder.sdt;
        this.email        = builder.email;
        this.chucVu       = builder.chucVu;
        this.trangThai    = builder.trangThai;
        this.ngayVaoLam   = builder.ngayVaoLam;
        this.ngayNghiViec = builder.ngayNghiViec;
        this.luongGio     = builder.luongGio;
        this.luongThuong  = builder.luongThuong;
    }

    public NhanVienData() {}

    // ── Tính lương tổng (derived, không lưu DB) ──────────
    public BigDecimal tinhLuongTong(BigDecimal soGioLam) {
        if (luongGio == null || soGioLam == null) return BigDecimal.ZERO;
        BigDecimal thuong = luongThuong != null ? luongThuong : BigDecimal.ZERO;
        return luongGio.multiply(soGioLam).add(thuong);
    }

    // ── Getters & Setters ────────────────────────────────
    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }

    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getSDT() { return sdt; }
    public void setSDT(String sdt) { this.sdt = sdt; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public ChucVuNV getChucVu() { return chucVu; }
    public void setChucVu(ChucVuNV chucVu) { this.chucVu = chucVu; }

    public TrangThaiNV getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiNV trangThai) { this.trangThai = trangThai; }

    public LocalDate getNgayVaoLam() { return ngayVaoLam; }
    public void setNgayVaoLam(LocalDate ngayVaoLam) { this.ngayVaoLam = ngayVaoLam; }

    public LocalDate getNgayNghiViec() { return ngayNghiViec; }
    public void setNgayNghiViec(LocalDate ngayNghiViec) { this.ngayNghiViec = ngayNghiViec; }

    public BigDecimal getLuongGio() { return luongGio; }
    public void setLuongGio(BigDecimal luongGio) { this.luongGio = luongGio; }

    public BigDecimal getLuongThuong() { return luongThuong; }
    public void setLuongThuong(BigDecimal luongThuong) { this.luongThuong = luongThuong; }

    // ── Utility ──────────────────────────────────────────
    @Override
    public String toString() {
        return String.format("NhanVien{maNV='%s', hoTen='%s', chucVu=%s, trangThai=%s}",
                maNV, hoTen, chucVu, trangThai);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NhanVienData)) return false;
        return Objects.equals(maNV, ((NhanVienData) o).maNV);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNV);
    }

    // ── Builder ──────────────────────────────────────────
    public static class ThoXayNhanVien {
        private String maNV;
        private String hoTen;
        private String cccd;
        private LocalDate ngaySinh;
        private String gioiTinh;
        private String diaChi;
        private String sdt;
        private String email;
        private ChucVuNV chucVu;
        private TrangThaiNV trangThai;
        private LocalDate ngayVaoLam;
        private LocalDate ngayNghiViec;
        private BigDecimal luongGio;
        private BigDecimal luongThuong;

        public ThoXayNhanVien ganMaNV(String maNV)               { this.maNV = maNV; return this; }
        public ThoXayNhanVien ganHoTen(String hoTen)             { this.hoTen = hoTen; return this; }
        public ThoXayNhanVien ganCccd(String cccd)               { this.cccd = cccd; return this; }
        public ThoXayNhanVien ganNgaySinh(LocalDate ngaySinh)    { this.ngaySinh = ngaySinh; return this; }
        public ThoXayNhanVien ganGioiTinh(String gioiTinh)       { this.gioiTinh = gioiTinh; return this; }
        public ThoXayNhanVien ganDiaChi(String diaChi)           { this.diaChi = diaChi; return this; }
        public ThoXayNhanVien ganSDT(String sdt)                 { this.sdt = sdt; return this; }
        public ThoXayNhanVien ganEmail(String email)             { this.email = email; return this; }
        public ThoXayNhanVien ganChucVu(ChucVuNV chucVu)         { this.chucVu = chucVu; return this; }
        public ThoXayNhanVien ganTrangThai(TrangThaiNV trangThai){ this.trangThai = trangThai; return this; }
        public ThoXayNhanVien ganNgayVaoLam(LocalDate d)         { this.ngayVaoLam = d; return this; }
        public ThoXayNhanVien ganNgayNghiViec(LocalDate d)       { this.ngayNghiViec = d; return this; }
        public ThoXayNhanVien ganLuongGio(BigDecimal l)          { this.luongGio = l; return this; }
        public ThoXayNhanVien ganLuongThuong(BigDecimal l)       { this.luongThuong = l; return this; }

        public NhanVienData taoMoi() { return new NhanVienData(this); }
    }
}