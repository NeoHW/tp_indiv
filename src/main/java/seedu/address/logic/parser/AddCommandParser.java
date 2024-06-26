package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FAMILY_CONDITION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FOOD_PREFERENCE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_HOBBY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PREFERRED_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.patient.FamilyCondition;
import seedu.address.model.patient.FoodPreference;
import seedu.address.model.patient.Hobby;
import seedu.address.model.patient.Name;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientHospitalId;
import seedu.address.model.patient.PreferredName;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {
    private static final Logger logger = LogsCenter.getLogger(AddCommandParser.class);

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        logger.info("Received arguments: " + args + " for AddCommand; Attempting to parse..");

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_PID, PREFIX_NAME, PREFIX_PREFERRED_NAME, PREFIX_FOOD_PREFERENCE,
                    PREFIX_FAMILY_CONDITION, PREFIX_HOBBY, PREFIX_TAG);

        if (!arePrefixesPresent(argMultimap, PREFIX_PID, PREFIX_NAME, PREFIX_PREFERRED_NAME, PREFIX_FOOD_PREFERENCE,
            PREFIX_FAMILY_CONDITION, PREFIX_HOBBY) || !argMultimap.getPreamble().isEmpty()) {
            logger.log(Level.WARNING, "Required prefix(s) not found in AddCommand");
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
        logger.info("All prefixes required are present.");

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_PID, PREFIX_NAME, PREFIX_PREFERRED_NAME);

        PatientHospitalId patientHospitalId = ParserUtil.parsePatientHospitalId(argMultimap.getValue(PREFIX_PID).get());
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        PreferredName preferredName = ParserUtil.parsePreferredName(argMultimap.getValue(PREFIX_PREFERRED_NAME).get());
        Set<FoodPreference> foodPreferenceList = ParserUtil.parseFoodPreferences(argMultimap
            .getAllValues(PREFIX_FOOD_PREFERENCE));
        Set<FamilyCondition> familyConditionList = ParserUtil.parseFamilyConditions(argMultimap
            .getAllValues(PREFIX_FAMILY_CONDITION));
        Set<Hobby> hobbyList = ParserUtil.parseHobbies(argMultimap.getAllValues(PREFIX_HOBBY));
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

        Patient patient = new Patient(patientHospitalId, name, preferredName, foodPreferenceList, familyConditionList,
            hobbyList, tagList);

        logger.info("All arguments received are valid");
        return new AddCommand(patient);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
