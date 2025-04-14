package com.scm.movieApp.exceptions;

public class RefreshTokenExpired extends RuntimeException
{
    public RefreshTokenExpired(String message)
    {
        super(message);
    }
}
