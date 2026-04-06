package com.arbitaja.thesis.contract;

import java.util.List;

interface SignupContractClient {
    List<String> fetchSignupUsernames() throws Exception;
}

