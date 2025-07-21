package ru.practicum.ewm.mainservice.advice;

import org.junit.jupiter.api.Test;
import ru.practicum.ewm.mainservice.advice.enums.StateActionAdmin;
import ru.practicum.ewm.mainservice.advice.enums.StateActionInitiator;
import ru.practicum.ewm.mainservice.advice.enums.UpdateRequestStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdviceEnumsTest {

    @Test
    void valueOfTestStateActionAdmin() {
        StateActionAdmin s = StateActionAdmin.PUBLISH_EVENT;
        assertEquals(s, StateActionAdmin.valueOf(s.name()));
    }

    @Test
    void valueOfTestStateActionInitiator() {
        StateActionInitiator s = StateActionInitiator.SEND_TO_REVIEW;
        assertEquals(s, StateActionInitiator.valueOf(s.name()));
    }

    @Test
    void valueOfTestUpdateRequestStatus() {
        UpdateRequestStatus s = UpdateRequestStatus.CONFIRMED;
        assertEquals(s, UpdateRequestStatus.valueOf(s.name()));
    }
}
