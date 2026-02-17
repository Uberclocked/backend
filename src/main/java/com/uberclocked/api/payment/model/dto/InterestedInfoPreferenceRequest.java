package com.uberclocked.api.payment.model.dto;

import java.util.UUID;

public record InterestedInfoPreferenceRequest(UUID postId, UUID interestedUserId) {}