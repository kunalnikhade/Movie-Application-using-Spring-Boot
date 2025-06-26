package com.movie.movieApp.exceptions;

public class UsernameNotFoundException extends RuntimeException
{

  public UsernameNotFoundException(final String resourceName)
  {
    super(String.format("%s not found with %s : %s", resourceName));
  }
}
