import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

public class ChamCongLogic {
    private final ChamCongDao ccDao = ChamCongDao.getInstance();
    private final CaLamDao caDao = CaLamDao.getInstance();
    private final PhanCongCaDao pcDao = PhanCongCaDao.getInstance(); 
    
    private static final ChamCongLogic instance = new ChamCongLogic();
    private ChamCongLogic() {}
    public static ChamCongLogic getInstance() { return instance; }

    public void checkIn(String maPhanCong) throws Exception {
        
        PhanCongCaData pc = pcDao.layTheoMa(maPhanCong); 
        if (pc == null) throw new Exception("Dữ liệu phân công không hợp lệ. Nhân viên không thuộc ca này!");

        
        if (ccDao.kiemTraNhanVienChuaCheckOut(pc.getMaNV())) {
            throw new Exception("Từ chối Check-In: Nhân viên này đang có một ca làm việc khác chưa được Check-Out! Vui lòng chốt ca cũ trước.");
        }

        
        CaLamData ca = caDao.layTheoMa(pc.getMaCa()); 
        LoaiCaData lc = caDao.layLoaiCaTheoMa(ca.getMaLoaiCa()); 
        
        if (ca.getNgayLam() != null && lc != null) {
            LocalDateTime thoiDiemBatDauCa = LocalDateTime.of(ca.getNgayLam(), lc.getGioBatDau());
            LocalDateTime bayGio = LocalDateTime.now();

            if (bayGio.isBefore(thoiDiemBatDauCa)) {
                throw new Exception("Từ chối chấm công: Chưa đến giờ làm việc của ca này (Bắt đầu lúc: " + lc.getGioBatDau() + ")!");
            }
        }

        
        ChamCongData ccExist = ccDao.layTheoPhanCong(maPhanCong); 
        if (ccExist != null) throw new Exception("Nhân viên này đã thực hiện Check-In ca này rồi!");

        
        ChamCongData ccMoi = new ChamCongData();
        ccMoi.setMaChamCong("CC_" + System.currentTimeMillis());
        ccMoi.setMaPhanCong(maPhanCong);
        ccMoi.setCheckIn(LocalDateTime.now());

        if (!ccDao.them(ccMoi)) throw new Exception("Lỗi hệ thống: Check-In thất bại!"); 
    }

    public void checkOut(String maPhanCong) throws Exception {
        
        PhanCongCaData pc = pcDao.layTheoMa(maPhanCong);
        if (pc == null) {
            throw new Exception("Dữ liệu phân công không tồn tại!");
        }

        
        ChamCongData cc = ccDao.layTheoPhanCong(maPhanCong);
        if (cc == null) {
            throw new Exception("Nhân viên chưa thực hiện Check-In, không thể Check-Out!");
        }
        if (cc.getCheckOut() != null) {
            throw new Exception("Nhân viên này đã kết thúc (Check-Out) ca làm này rồi!");
        }

        
        LocalDateTime thoiGianCheckOut = LocalDateTime.now();
        cc.setCheckOut(thoiGianCheckOut);

        
        Duration duration = Duration.between(cc.getCheckIn(), thoiGianCheckOut);
        double soGioLam = duration.toMinutes() / 60.0; 
        
        
        cc.setSoGioLam(Math.round(soGioLam * 100.0) / 100.0);

        
        CaLamData ca = caDao.layTheoMa(pc.getMaCa());
        if (ca == null) throw new Exception("Không tìm thấy dữ liệu ca làm!");

        LoaiCaData loaiCa = caDao.layLoaiCaTheoMa(ca.getMaLoaiCa());
        if (loaiCa == null) throw new Exception("Không tìm thấy cấu hình loại ca!");

        NhanVienData nv = NhanVienDao.getInstance().layTheoMa(pc.getMaNV());
        if (nv == null) throw new Exception("Không tìm thấy thông tin nhân viên!");
        if (nv.getLuongGio() == null) {
            throw new Exception("Nhân viên '" + nv.getHoTen() + "' chưa được cài đặt mức lương/giờ!");
        }

        
        BigDecimal luongGio = nv.getLuongGio();
        BigDecimal heSo = loaiCa.getHeSoLuong() != null ? loaiCa.getHeSoLuong() : BigDecimal.ONE;
        
        
        BigDecimal luongCa = luongGio
                .multiply(new BigDecimal(String.valueOf(cc.getSoGioLam())))
                .multiply(heSo)
                .setScale(0, RoundingMode.HALF_UP); 
                
        cc.setLuongCa(luongCa);

        
        if (!ccDao.capNhatCheckOut(cc)) {
            throw new Exception("Lỗi hệ thống: Không thể lưu dữ liệu Check-Out vào cơ sở dữ liệu!");
        }
    }
}
