package it.aulab.aulabchronicles.services;

public interface EmailService {
    void sendSimpleEmail(String to, String subject, String text);
    
}
