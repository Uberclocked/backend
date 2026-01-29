package com.uberclocked.api.market.model.dto;

import org.springframework.web.multipart.MultipartFile;

public record PostDataDto(
  String title,
  String description,
  Double price,
  String category
){}
