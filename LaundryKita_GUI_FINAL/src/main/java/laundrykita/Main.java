package laundrykita;

import javax.swing.SwingUtilities;
import laundrykita.gui.UiTheme;
import laundrykita.view.MainFrame;

public class Main {
    public static void main(String[] args) {
        UiTheme.install();
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
