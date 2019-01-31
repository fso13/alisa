package ru.drudenko.alisa.service.google;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface GoogleTokenExtractor {
    GmailCredentials getToken(String code) throws GeneralSecurityException, IOException;
}
