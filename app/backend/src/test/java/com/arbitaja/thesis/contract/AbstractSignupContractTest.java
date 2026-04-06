package com.arbitaja.thesis.contract;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class AbstractSignupContractTest {

    protected abstract SignupContractClient client();

    @Test
    void signupListContractReturnsExpectedUsernames() throws Exception {
        List<String> usernames = client().fetchSignupUsernames();
        assertEquals(List.of("alice", "bob"), usernames);
    }
}

