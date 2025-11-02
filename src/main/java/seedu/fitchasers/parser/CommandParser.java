package seedu.fitchasers.parser;

import seedu.fitchasers.exceptions.InvalidArgumentInput;
import seedu.fitchasers.storage.FileHandler;

import java.io.IOException;
import java.time.Year;
import java.time.YearMonth;
import java.util.regex.Pattern;

public interface CommandParser<T> {
    Pattern INT = Pattern.compile("^\\d+$");

    T parse(String raw) throws InvalidArgumentInput;
    static void validateMonth(int m) throws InvalidArgumentInput {
        if (m < 1 || m > 12){
            throw new InvalidArgumentInput("Month must be between 1 and 12.");
        }
    }

    static boolean isInt(String s) {
        return s != null && INT.matcher(s).matches();
    }

    static void guard(boolean cond, String msg) throws InvalidArgumentInput {
        if (!cond){
            throw new InvalidArgumentInput(msg);
        }
    }

    /**
     * Strictly "MM/YY" → YearMonth(2000+YY, MM). Adjust if you prefer another rule.
     */
    static YearMonth parseYearMonthTokenStrict(String token) throws InvalidArgumentInput, IOException {
        String[] parts = token.split("/");
        FileHandler fh = new FileHandler();
        if (parts.length != 2) {
            throw new InvalidArgumentInput("Use ym/<MM>/<YY>, e.g., ym/10/26");
        }
        String mmStr = parts[0].trim();
        String yyStr = parts[1].trim();
        if (!isInt(mmStr) || !isInt(yyStr)) {
            throw new InvalidArgumentInput("Use digits only: ym/<MM>/<YY>, e.g., ym/10/26");
        }
        int mm = Integer.parseInt(mmStr);
        int yy = Integer.parseInt(yyStr);
        if (mm < 1 || mm > 12){
            throw new InvalidArgumentInput("Month must be 1..12.");
        }
        if (yy < 0 || yy > 99){
            throw new InvalidArgumentInput("Year must be two digits 00..99.");
        }

        YearMonth yyyy = YearMonth.of(yy+2000, mm); // Example policy: 00..99 -> 2000..2099
        try {
            if (fh.getCreationMonth().isBefore(yyyy) || yyyy.isAfter(YearMonth.of(2100, 12))) {
                throw new InvalidArgumentInput("Year out of supported range.");
            }
        }catch (Exception e){
            throw new IOException("Oh no seems like your Creation Month File is corrupted, Please check it");
        }
        return yyyy;
    }
}
