package com.example.guiproject3;
public interface LoginPage
{
    void handleLogin();
    void handleSignup();
    void handleForgotPassword();
    boolean saveUser(String username, String email, String password);
    String retrievePassword(String Useremail);

}