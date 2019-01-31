package ru.drudenko.alisa.service.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Service
public final class GmailServiceImpl implements GmailService {
    @Value("${app.google.client_id}")
    private String client_id;
    @Value("${app.google.client_secret}")
    private String client_secret;

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

    GmailServiceImpl() throws GeneralSecurityException, IOException {
    }


    private static Credential convertToGoogleCredential(String accessToken, String refreshToken, String apiSecret, String apiKey) {
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport).setJsonFactory(jsonFactory).setClientSecrets(apiKey, apiSecret).build();
        credential.setAccessToken(accessToken);
        credential.setRefreshToken(refreshToken);
        try {
            credential.refreshToken();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return credential;
    }

    @Override
    public void getMessages(final String token, final String refresh) throws IOException {
//        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, convertToGoogleCredential(code, "1/bVZQqRLjt0e_wS-4PtRjhiPUMSykoXm6yHLWoAjaTxY", "gykHUJOipdk1a5p-8A-1RYxy", "788764317534-erfn8p6im4v4vo5i2b28c6s4emqejlav.apps.googleusercontent.com"))
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, convertToGoogleCredential(token, refresh, client_secret, client_id))
                .build();

        // Print the labels in the user's account.
        String user = "me";
        ListLabelsResponse listResponse = service.users().labels().list(user).execute();
        List<Label> labels = listResponse.getLabels();
        if (labels.isEmpty()) {
            System.out.println("No labels found.");
        } else {
            System.out.println("Labels:");
            for (Label label : labels) {
                System.out.printf("- %s\n", label.getName());
            }
        }
    }
}
