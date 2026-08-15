import java.time.LocalDateTime;
import java.util.Objects;

public class TaiKhoanData {

    public enum Quyen {
        ADMIN("Quản trị viên"),
        NHAN_VIEN("Nhân viên");

        private final String tenHienThi;
        Quyen(String tenHienThi) { this.tenHienThi = tenHienThi; }
        @Override
        public String toString() { return tenHienThi; }
    }

    private String tenDangNhap;
    private String matKhau;
    private String maNV; 
    private Quyen quyen;
    private LocalDateTime ngayTao;

    public TaiKhoanData() {}

    private TaiKhoanData(ThoXayTaiKhoan builder) {
        this.tenDangNhap = builder.tenDangNhap;
        this.matKhau     = builder.matKhau;
        this.maNV        = builder.maNV;
        this.quyen       = builder.quyen;
        this.ngayTao     = builder.ngayTao;
    }

    // Getters & Setters
    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public Quyen getQuyen() { return quyen; }
    public void setQuyen(Quyen quyen) { this.quyen = quyen; }

    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaiKhoanData)) return false;
        TaiKhoanData that = (TaiKhoanData) o;
        return Objects.equals(tenDangNhap, that.tenDangNhap);
    }

    @Override
    public int hashCode() { return Objects.hash(tenDangNhap); }

    public static class ThoXayTaiKhoan {
        private String tenDangNhap;
        private String matKhau;
        private String maNV;
        private Quyen quyen;
        private LocalDateTime ngayTao;

        public ThoXayTaiKhoan ganTenDangNhap(String t) { this.tenDangNhap = t; return this; }
        public ThoXayTaiKhoan ganMatKhau(String m)    { this.matKhau = m; return this; }
        public ThoXayTaiKhoan ganMaNV(String ma)      { this.maNV = ma; return this; }
        public ThoXayTaiKhoan ganQuyen(Quyen q)       { this.quyen = q; return this; }
        public ThoXayTaiKhoan ganNgayTao(LocalDateTime n) { this.ngayTao = n; return this; }

        public TaiKhoanData taoMoi() { return new TaiKhoanData(this); }
    }
}