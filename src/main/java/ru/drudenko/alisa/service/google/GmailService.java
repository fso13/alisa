package ru.drudenko.alisa.service.google;

import java.io.IOException;

public interface GmailService {
    void getMessages(String token, String refresh) throws IOException;
}