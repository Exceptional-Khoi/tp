package seedu.fitchasers.parser.deleteworkout;

import seedu.fitchasers.exceptions.InvalidArgumentInput;
import seedu.fitchasers.parser.CommandParser;
import seedu.fitchasers.ui.UI;

import java.io.IOException;
import java.time.YearMonth;

//@@author Exceptional-Khoi
/**
 * Parses and validates user input for the /delete_workout command.
 * Handles flags like id/, m/, and ym/ to identify which workout to delete.
 */
public class DeleteParser implements CommandParser<DeleteWorkoutArguments> {

    /**
     * Parses the raw user input and returns a {@code DeleteWorkoutArguments} object.
     *
     * @param raw The raw user input string.
     * @param creationDate The creation date context for parsing year-month values.
     * @return A {@code DeleteWorkoutArguments} object containing parsed arguments.
     * @throws InvalidArgumentInput If the input format is invalid or missing required flags.
     */
    @Override
    public DeleteWorkoutArguments parse(String raw, YearMonth creationDate) throws InvalidArgumentInput {
        UI ui = new UI();
        if (raw == null || raw.isBlank()) {
            throw usage("Workout deletion requires arguments try /delete workout id/<workout_id>.");
        }

        String[] rawArguments = raw.trim().split("\\s+");

        Integer id = null;
        YearMonth ym = null;
        boolean seenM = false;
        boolean seenYM = false;

        for (String argument : rawArguments) {
            if (argument.startsWith("id/")) {
                if (id != null) {
                    throw usage("ID was specified more than once.");
                }
                String idStr = argument.substring(3).trim();
                CommandParser.guard(CommandParser.isInt(idStr), "Invalid workout ID! \nEnsure that " +
                        " you key in a valid number and there is no space between ID/ and your number! E.g. id/1");
                id = Integer.parseInt(idStr);
                CommandParser.guard(id > 0, "ID must be a positive integer, e.g., id/3");
                continue;
            }

            if (argument.startsWith("m/")) {
                if (seenYM) {
                    throw usage("Cannot combine m/<MM> with ym/<MM>/<YY>.");
                }
                seenM = true;
                String mmStr = argument.substring(2).trim();
                CommandParser.guard(CommandParser.isInt(mmStr), "Month after m/ must be an integer 1..12.");
                int mm = Integer.parseInt(mmStr);
                CommandParser.validateMonth(mm);
                ym = YearMonth.of(YearMonth.now().getYear(), mm);
                continue;
            }

            if (argument.startsWith("ym/")) {
                if (seenM) {
                    throw usage("Cannot combine ym/<MM>/<YY> with m/<MM>.");
                }
                seenYM = true;
                String token = argument.substring(3).trim(); // expects "MM/YY"
                try {
                    ym = CommandParser.parseYearMonthTokenStrict(token, creationDate); // see below
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }

            // No other flags are allowed here.
            if (argument.contains("/")) {
                throw usage("Your flag '" + argument + "' is wrong! Do follow the examples below :)");
            } else {
                throw usage("The '" + argument + "' doesn't mean anything :/ \n" +
                        " Ensure you follow the examples below :)");
            }
        }

        if (id == null) {
            throw usage("Missing ID. Example: id/3");
        }
        if (ym == null) {
            ui.showError("Since you didn't input any month or year, I will assume you mean the current month ya! \n " +
                    "If that's not what you want check help to see correct date format input :) ");
            ym = YearMonth.now();
        }

        return new DeleteWorkoutArguments(id, ym);
    }

    /* ---------- helpers ---------- */

    private InvalidArgumentInput usage(String msg) {
        String help = """
                Usage:
                  /delete_workout id/<INDEX> m/<MM>
                  /delete_workout id/<INDEX> ym/<MM>/<YY>
                Examples:
                  /delete_workout id/1 m/10
                  /delete_workout id/2 ym/10/26
                """;
        return new InvalidArgumentInput(msg + "\n" + help);
    }
}

