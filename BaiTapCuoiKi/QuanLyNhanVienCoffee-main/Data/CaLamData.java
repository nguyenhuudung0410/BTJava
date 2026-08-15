import java.time.LocalDate;

public class CaLamData {
    private String maCa;
    private String maLoaiCa;
    private LocalDate ngayLam;
    private String ghiChu;

    public CaLamData() {}

    public CaLamData(String maCa, String maLoaiCa, LocalDate ngayLam, String ghiChu) {
        this.maCa = maCa;
        this.maLoaiCa = maLoaiCa;
        this.ngayLam = ngayLam;
        this.ghiChu = ghiChu;
    }

    public String getMaCa() { return maCa; }
    public void setMaCa(String maCa) { this.maCa = maCa; }

    public String getMaLoaiCa() { return maLoaiCa; }
    public void setMaLoaiCa(String maLoaiCa) { this.maLoaiCa = maLoaiCa; }

    public LocalDate getNgayLam() { return ngayLam; }
    public void setNgayLam(LocalDate ngayLam) { this.ngayLam = ngayLam; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}