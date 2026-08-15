import java.util.List;

public class TaiKhoanLogic {

    private final TaiKhoanDao dao = TaiKhoanDao.getInstance();
    private final NhanVienDao nvDao = NhanVienDao.getInstance();

    private static final TaiKhoanLogic instance = new TaiKhoanLogic();
    private TaiKhoanLogic() {}
    public static TaiKhoanLogic getInstance() { return instance; }

    public List<TaiKhoanData> layDanhSach() {
        return dao.layDanhSach();
    }

    public TaiKhoanData dangNhap(String ten, String mk) throws Exception {
        if (ten == null || ten.trim().isEmpty()) throw new Exception("Tên đăng nhập không được để trống!");
        if (mk == null || mk.trim().isEmpty()) throw new Exception("Mật khẩu không được để trống!");

        TaiKhoanData tk = dao.layTheoTen(ten);
        if (tk == null || !tk.getMatKhau().equals(mk)) {
            throw new Exception("Tên đăng nhập hoặc mật khẩu không chính xác!");
        }
        
        NhanVienData nv = nvDao.layTheoMa(tk.getMaNV());
        if (nv != null && nv.getTrangThai() == NhanVienData.TrangThaiNV.NGHI_VIEC) {
            throw new Exception("Tài khoản này thuộc về nhân viên đã nghỉ việc!");
        }

        return tk;
    }

    public void themTaiKhoan(TaiKhoanData tk) throws Exception {
        kiemTraHopLe(tk);
        
        if (dao.layTheoTen(tk.getTenDangNhap()) != null) {
            throw new Exception("Tên đăng nhập này đã được sử dụng!");
        }
        
        if (nvDao.layTheoMa(tk.getMaNV()) == null) {
            throw new Exception("Mã nhân viên '" + tk.getMaNV() + "' không tồn tại!");
        }

        boolean ok = dao.them(tk);
        if (!ok) throw new Exception("Lỗi hệ thống: Không thể tạo tài khoản!");
    }

    public void doiMatKhau(String ten, String mkCu, String mkMoi) throws Exception {
    	TaiKhoanData tk = dao.layTheoTen(ten);
    	if (tk == null || !tk.getMatKhau().equals(mkCu)) {
    	    throw new Exception("Mật khẩu cũ không chính xác!");
    	}
        
        if (mkMoi == null || mkMoi.length() < 6) {
            throw new Exception("Mật khẩu mới phải có ít nhất 6 ký tự!");
        }
        if (mkCu.equals(mkMoi)) {
            throw new Exception("Mật khẩu mới không được trùng với mật khẩu cũ!");
        }

        boolean ok = dao.doiMatKhau(ten, mkMoi);
        if (!ok) throw new Exception("Lỗi hệ thống: Đổi mật khẩu thất bại!");
    }

    public void xoaTaiKhoan(String ten) throws Exception {
        if (dao.layTheoTen(ten) == null) {
            throw new Exception("Tài khoản không tồn tại!");
        }
        boolean ok = dao.xoa(ten);
        if (!ok) throw new Exception("Lỗi hệ thống: Xóa tài khoản thất bại!");
    }

    private void kiemTraHopLe(TaiKhoanData tk) throws Exception {
        if (tk.getTenDangNhap() == null || tk.getTenDangNhap().trim().length() < 4) {
            throw new Exception("Tên đăng nhập phải có ít nhất 4 ký tự!");
        }
        if (tk.getMatKhau() == null || tk.getMatKhau().length() < 6) {
            throw new Exception("Mật khẩu phải có ít nhất 6 ký tự!");
        }
        if (tk.getMaNV() == null || tk.getMaNV().trim().isEmpty()) {
            throw new Exception("Phải gán tài khoản cho một mã nhân viên!");
        }
        if (tk.getQuyen() == null) {
            throw new Exception("Vui lòng chọn quyền cho tài khoản!");
        }
    }
}