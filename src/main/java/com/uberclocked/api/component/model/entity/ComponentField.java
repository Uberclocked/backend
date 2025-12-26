package com.uberclocked.api.component.model.entity;

import com.uberclocked.api.component.model.entity.field.FieldType;
import jakarta.persistence.Embeddable;

@Embeddable
public record ComponentField(FieldType type, boolean required, String defaultValue) {}
