package com.movie.movieApp.dto;

import lombok.Builder;

@Builder
public record MailBody(String to, String subject, String text)
{

}
