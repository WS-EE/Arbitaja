package com.arbitaja.backend.competitions.scorings.APIs;

import com.arbitaja.backend.competitions.scorings.dataobjects.ScoringCriterion;
import com.arbitaja.backend.competitions.scorings.repositories.ScoringCriterionRepository;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ScoringCriterionDeserializer extends JsonDeserializer<ScoringCriterion> {

    @Autowired
    private ScoringCriterionRepository scoringCriterionRepository;

    @Override
    public ScoringCriterion deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Integer id = p.getIntValue();
        return scoringCriterionRepository.findById(id).orElse(null);
    }
}
