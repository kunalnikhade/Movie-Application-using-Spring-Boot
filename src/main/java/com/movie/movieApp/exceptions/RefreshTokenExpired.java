package com.movie.movieApp.exceptions;

public class RefreshTokenExpired extends RuntimeException
{
    public RefreshTokenExpired(String message)
    {
        super(message);
    }
}
