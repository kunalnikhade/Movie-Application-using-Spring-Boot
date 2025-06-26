package com.movie.movieApp.exceptions;

public class ResourceNotFoundException extends RuntimeException
{
    public ResourceNotFoundException(final String resourceName, final String fieldName, final Integer fieldValue)
    {
        super(String.format("%s not found with %s : %s", resourceName, fieldName, fieldValue));
    }
}
