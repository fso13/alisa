package ru.drudenko.alisa.service.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.drudenko.alisa.dto.dialog.req.Command;
import ru.drudenko.alisa.model.AlisaClient;
import ru.drudenko.alisa.model.Token;
import ru.drudenko.alisa.repository.AlisaClientRepository;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

@Service
public final class GmailCommandServiceImpl implements GmailCommandService {
    @Value("${app.google.client_id}")
    private String client_id;
    @Value("${app.google.client_secret}")
    private String client_secret;

    private static final List<String> EMAIL = Arrays.asList("получи", "почту");
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

    private final AlisaClientRepository alisaClientRepository;

    public GmailCommandServiceImpl(final AlisaClientRepository alisaClientRepository) throws GeneralSecurityException, IOException {
        this.alisaClientRepository = alisaClientRepository;
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
    public boolean doFilter(final Command command) {
        List<String> tokens = command.getRequest().getNlu().getTokens();
        return tokens.containsAll(EMAIL);
    }

    @Override
    public String getCommands() {
        return "получи почту";
    }

    @Override
    public String getMessage(final Command command) {
        try {
            AlisaClient client = alisaClientRepository.getOne(command.getSession().getUserId());
            Token token = client.getTokens()
                    .stream()
                    .filter(token1 -> token1.getOauthClient().equals("google"))
                    .findFirst().get();
            return getMessages(token.getAccessToken(), token.getRefreshToken());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getMessages(final String token, final String refresh) throws IOException {
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, convertToGoogleCredential(token, refresh, client_secret, client_id)).build();
        String user = "me";
        ListMessagesResponse listResponse = service.users().messages().list(user).execute();
        List<Message> messages = listResponse.getMessages();
        if (messages.isEmpty()) {
            return "Нет новой почты.";
        } else {
            return "Новых сообщений - " + (messages.size() + listResponse.getResultSizeEstimate());
        }
    }
}
