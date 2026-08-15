import java.util.List;

public class PhanCongCaLogic {
    private final PhanCongCaDao pcDao = PhanCongCaDao.getInstance();
    private final CaLamDao caDao = CaLamDao.getInstance();
    private final ChamCongDao ccDao = ChamCongDao.getInstance();
    
    private static final PhanCongCaLogic instance = new PhanCongCaLogic();
    private PhanCongCaLogic() {}
    public static PhanCongCaLogic getInstance() { return instance; }

    public void phanCongNhanVien(String maCa, String maNV) throws Exception {
        CaLamData caLam = caDao.layTheoMa(maCa);
        if (caLam == null) throw new Exception("Ca làm không tồn tại!");
        
        
        if (NhanVienDao.getInstance().layTheoMa(maNV) == null) {
            throw new Exception("Nhân viên không tồn tại trong hệ thống!");
        }

        
        List<PhanCongCaData> dspc = pcDao.layDanhSachTheoCa(maCa);
        for (PhanCongCaData pc : dspc) {
            if (pc.getMaNV().equals(maNV)) {
                throw new Exception("Nhân viên này đã được phân công vào ca này rồi!");
            }
        }

        
        LoaiCaData loaiCaMoi = caDao.layLoaiCaTheoMa(caLam.getMaLoaiCa());
        boolean isOverlap = pcDao.kiemTraTrungGioLam(
                maNV, 
                caLam.getNgayLam(), 
                loaiCaMoi.getGioBatDau(), 
                loaiCaMoi.getGioKetThuc()
        );
        if (isOverlap) {
            throw new Exception("Không thể phân công! Nhân viên này đã bị kẹt một ca khác trùng giờ trong ngày " + caLam.getNgayLam() + ".");
        }

        
        int randomNum = (int)(Math.random() * 9000) + 1000; 
        String maPhanCong = "PC" + randomNum + "_" + maNV;
        PhanCongCaData pc = new PhanCongCaData(maPhanCong, maCa, maNV);
        
        if (!pcDao.them(pc)) {
            throw new Exception("Lỗi hệ thống: Phân công nhân viên thất bại!");
        }
    }
    
    public void xoaPhanCong(String maPhanCong) throws Exception {
        if (pcDao.layTheoMa(maPhanCong) == null) {
            throw new Exception("Dữ liệu phân công không tồn tại!");
        }

        
        if (ccDao.layTheoPhanCong(maPhanCong) != null) {
            throw new Exception("Không thể gỡ phân công! Nhân viên đã có dữ liệu chấm công thực tế trong ca này.");
        }

        if (!pcDao.xoa(maPhanCong)) {
            throw new Exception("Lỗi hệ thống: Gỡ phân công thất bại!");
        }
    }
}
