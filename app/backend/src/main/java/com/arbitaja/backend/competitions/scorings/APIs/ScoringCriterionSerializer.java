package com.arbitaja.backend.competitions.scorings.APIs;

import com.arbitaja.backend.competitions.scorings.dataobjects.ScoringCriterion;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ScoringCriterionSerializer extends JsonSerializer<ScoringCriterion> {
    @Override
    public void serialize(ScoringCriterion value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeNumber(value.getId());
    }
}
