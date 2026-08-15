import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ChamCongData {
    private String maChamCong;
    private String maPhanCong;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private double soGioLam;
    private BigDecimal luongCa;

    // Getters and Setters
    public String getMaChamCong() { return maChamCong; }
    public void setMaChamCong(String maChamCong) { this.maChamCong = maChamCong; }
    public String getMaPhanCong() { return maPhanCong; }
    public void setMaPhanCong(String maPhanCong) { this.maPhanCong = maPhanCong; }
    public LocalDateTime getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDateTime checkIn) { this.checkIn = checkIn; }
    public LocalDateTime getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDateTime checkOut) { this.checkOut = checkOut; }
    public double getSoGioLam() { return soGioLam; }
    public void setSoGioLam(double soGioLam) { this.soGioLam = soGioLam; }
    public BigDecimal getLuongCa() { return luongCa; }
    public void setLuongCa(BigDecimal luongCa) { this.luongCa = luongCa; }
}