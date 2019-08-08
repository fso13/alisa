package ru.drudenko.alisa.google.impl;

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
import ru.drudenko.alisa.api.auth.AlisaClientService;
import ru.drudenko.alisa.api.auth.TokenRequestDto;
import ru.drudenko.alisa.api.auth.TokenResponseDto;
import ru.drudenko.alisa.api.dialog.dto.req.Command;
import ru.drudenko.alisa.google.GmailCommandService;
import ru.drudenko.alisa.google.configuration.GoogleSettings;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public final class GmailCommandServiceImpl implements GmailCommandService {
    private static final List<String> EMAIL = Arrays.asList("получи", "почту");
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    private final GoogleSettings googleSettings;
    private final AlisaClientService alisaClientService;

    public GmailCommandServiceImpl(final GoogleSettings googleSettings,
                                   final AlisaClientService alisaClientService) throws GeneralSecurityException, IOException {
        this.googleSettings = googleSettings;
        this.alisaClientService = alisaClientService;
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
            TokenRequestDto tokenRequestDto = TokenRequestDto.builder().oauthClient(command.getSession().getUserId()).userId("google").build();
            TokenResponseDto token = alisaClientService.getTokenByUserIdAndOauthClient(tokenRequestDto);
            return getMessages(token.getAccessToken(), token.getRefreshToken());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getMessages(final String token, final String refresh) throws IOException {
        Credential credential = convertToGoogleCredential(token, refresh, googleSettings.getClient_secret(), googleSettings.getClient_id());
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).build();
        String user = "me";
        ListMessagesResponse listResponse = service.users().messages().list(user).execute();
        List<Message> messages = listResponse.getMessages();
        if (messages.isEmpty()) {
            return "Нет новой почты.";
        } else {
            return "Новых сообщений - " + (messages.size() + listResponse.getResultSizeEstimate());
        }
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

}
