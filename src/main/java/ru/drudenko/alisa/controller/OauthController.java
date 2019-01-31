package ru.drudenko.alisa.controller;

import com.google.api.client.auth.oauth.OAuthParameters;
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
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ru.drudenko.alisa.dto.oauth.Token;
import ru.drudenko.alisa.model.Client;
import ru.drudenko.alisa.model.Otp;
import ru.drudenko.alisa.repository.ClientRepository;
import ru.drudenko.alisa.repository.OtpRepository;

import java.io.IOException;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping(value = "/oauth", produces = MediaType.ALL_VALUE)
public class OauthController {

    @Value("${app.yandex.client_id}")
    private String client_id;

    @Value("${app.yandex.client_secret}")
    private String client_secret;

    private final RestTemplate yandexRestTemplate;
    private final OtpRepository otpRepository;
    private final ClientRepository clientRepository;
    private final VelocityEngine velocityEngine;

    @Autowired
    public OauthController(RestTemplate yandexRestTemplate,
                           OtpRepository otpRepository,
                           ClientRepository clientRepository,
                           VelocityEngine velocityEngine) {

        this.yandexRestTemplate = yandexRestTemplate;
        this.otpRepository = otpRepository;
        this.clientRepository = clientRepository;
        this.velocityEngine = velocityEngine;
    }

    @GetMapping(value = "/yandex", produces = {"text/html;charset=UTF-8"})
    ResponseEntity yandex(@RequestParam(name = "state") String state, @RequestParam(name = "code") String code) {

        String token = getToken(code);

        Otp otp = otpRepository.findByValueAndExpiredAndPersonIdIsNull(state.trim(), false).orElseThrow(RuntimeException::new);
        Client client = clientRepository.findByClientId(otp.getClientId()).orElseThrow(RuntimeException::new);
        client.setPersonId(token);

        Otp newOtp = new Otp();
        newOtp.setClientId(otp.getClientId());
        newOtp.setPersonId(token);
        String otpByClient = String.valueOf(100000 + (long) (Math.random() * (999999 - 100000)));
        newOtp.setValue(otpByClient);
        otpRepository.save(newOtp);

        Template t = velocityEngine.getTemplate("src/main/resources/templates/code.html", "utf-8");
        VelocityContext context = new VelocityContext();
        context.put("otpByClient", otpByClient);
        StringWriter writer = new StringWriter();
        t.merge(context, writer);

        return ResponseEntity
                .ok()
                .body(writer.toString());
    }

    @GetMapping(value = "/google", produces = {"text/html;charset=UTF-8"})
    ResponseEntity google(@RequestParam(name = "state") String state, @RequestParam(name = "code") String code) {

        System.out.println(code);
        return ResponseEntity
                .ok().build();
    }

    private String getToken(String code) {

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("code", Long.valueOf(code));
        map.add("client_id", client_id);
        map.add("client_secret", client_secret);

        HttpHeaders headers = new HttpHeaders();
//        headers.setBasicAuth(client_id, client_secret);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
        ResponseEntity<Token> responseEntity = yandexRestTemplate.
                exchange("https://oauth.yandex.ru/token",
                        HttpMethod.POST,
                        request,
                        ParameterizedTypeReference.forType(Token.class));

        return responseEntity.getBody().getAccess_token();
    }

    private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";


    public static void main(String[] args) throws GeneralSecurityException, IOException {
        // Build a new authorized API client service.


        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        OAuthParameters oAuthParameters = new OAuthParameters();
        String token = "ya29.GluiBipkMmiRhj7BpgzPDpi3tnDVxJtXVROeYfYLgMR4BWatSES_y6OvCSwrgrMekYEjw7srSCt1_9Bc5CTo8g1q44NdUtUpMHpXaGF1YhT_2BjRg_XCkmdEJqOj";
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, convertToGoogleCredential(token, "1/bVZQqRLjt0e_wS-4PtRjhiPUMSykoXm6yHLWoAjaTxY", "gykHUJOipdk1a5p-8A-1RYxy", "788764317534-erfn8p6im4v4vo5i2b28c6s4emqejlav.apps.googleusercontent.com"))
                .setApplicationName(APPLICATION_NAME)
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
