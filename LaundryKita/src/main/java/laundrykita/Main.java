package laundrykita;

import javax.swing.SwingUtilities;
import laundrykita.gui.MainFrame;
import laundrykita.gui.UiTheme;

public class Main {
    public static void main(String[] args) {
        UiTheme.install();
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
