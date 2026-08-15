public class PhanCongCaData {
    private String maPhanCong;
    private String maCa;
    private String maNV;

    public PhanCongCaData() {}

    public PhanCongCaData(String maPhanCong, String maCa, String maNV) {
        this.maPhanCong = maPhanCong;
        this.maCa = maCa;
        this.maNV = maNV;
    }

    public String getMaPhanCong() { return maPhanCong; }
    public void setMaPhanCong(String maPhanCong) { this.maPhanCong = maPhanCong; }
    public String getMaCa() { return maCa; }
    public void setMaCa(String maCa) { this.maCa = maCa; }
    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }
}