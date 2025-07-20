package ru.practicum.ewm.mainservice.advice;

import org.junit.jupiter.api.Test;
import ru.practicum.ewm.mainservice.advice.constant.ValidationConst;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidationConstTest {
    @Test
    void classIsInitializedAndRegexIsValid() throws Exception {
        Pattern p = Pattern.compile(ValidationConst.EMAIL_REGEX);
        assertTrue(p.matcher("user@example.com").matches());

        Class.forName(ValidationConst.class.getName());
    }
}
