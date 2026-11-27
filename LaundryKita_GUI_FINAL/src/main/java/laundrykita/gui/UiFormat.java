package laundrykita.gui;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class UiFormat {
    private static final Locale ID = Locale.forLanguageTag("id-ID");
    private static final DecimalFormatSymbols SYMBOLS = DecimalFormatSymbols.getInstance(ID);
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("d MMM yyyy", ID);

    static {
        SYMBOLS.setGroupingSeparator('.');
        SYMBOLS.setDecimalSeparator(',');
    }

    private UiFormat() {
    }

    public static String rupiah(BigDecimal amount) {
        if (amount == null) {
            return "-";
        }
        return "Rp " + new DecimalFormat("#,##0", SYMBOLS).format(amount);
    }

    public static String weight(BigDecimal kg) {
        if (kg == null) {
            return "-";
        }
        return new DecimalFormat("#,##0.##", SYMBOLS).format(kg) + " kg";
    }

    public static String date(LocalDate date) {
        return date == null ? "-" : DATE.format(date);
    }

    public static String text(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
