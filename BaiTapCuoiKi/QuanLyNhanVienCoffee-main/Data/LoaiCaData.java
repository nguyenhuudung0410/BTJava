import java.math.BigDecimal;
import java.time.LocalTime;

public class LoaiCaData {
    private String maLoaiCa;
    private String tenLoaiCa;
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;
    private double soGio;
    private BigDecimal heSoLuong;

    // Getters and Setters
    public String getMaLoaiCa() { return maLoaiCa; }
    public void setMaLoaiCa(String maLoaiCa) { this.maLoaiCa = maLoaiCa; }
    public String getTenLoaiCa() { return tenLoaiCa; }
    public void setTenLoaiCa(String tenLoaiCa) { this.tenLoaiCa = tenLoaiCa; }
    public LocalTime getGioBatDau() { return gioBatDau; }
    public void setGioBatDau(LocalTime gioBatDau) { this.gioBatDau = gioBatDau; }
    public LocalTime getGioKetThuc() { return gioKetThuc; }
    public void setGioKetThuc(LocalTime gioKetThuc) { this.gioKetThuc = gioKetThuc; }
    public double getSoGio() { return soGio; }
    public void setSoGio(double soGio) { this.soGio = soGio; }
    public BigDecimal getHeSoLuong() { return heSoLuong; }
    public void setHeSoLuong(BigDecimal heSoLuong) { this.heSoLuong = heSoLuong; }
}