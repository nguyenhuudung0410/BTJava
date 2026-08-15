import javax.swing.SwingUtilities;

import javax.swing.UIManager;



/**

 * ENTRY POINT - LỚP CHẠY CHƯƠNG TRÌNH

 */

public class MainApp {


    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        SwingUtilities.invokeLater(() -> {
 
            try {

                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            } catch (Exception e) {

                System.err.println("Không thể thiết lập Look and Feel của hệ thống.");

            }
            DangNhapUI loginForm = new DangNhapUI();

            loginForm.setVisible(true);

        });

    }

}
