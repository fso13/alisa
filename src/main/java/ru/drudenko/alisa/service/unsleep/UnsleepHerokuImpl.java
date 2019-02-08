package ru.drudenko.alisa.service.unsleep;

import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UnsleepHerokuImpl implements UnsleepHeroku {
    private static HttpEntity httpEntity;
    private static final String STRING = "{\n" +
            "  \"meta\": {\n" +
            "    \"client_id\": \"string\",\n" +
            "    \"locale\": \"string\",\n" +
            "    \"timezone\": \"string\"\n" +
            "  },\n" +
            "  \"request\": {\n" +
            "    \"command\": \"string\",\n" +
            "    \"markup\": {\n" +
            "      \"dangerous_context\": true\n" +
            "    },\n" +
            "    \"nlu\": {\n" +
            "      \"entities\": [\n" +
            "        {\n" +
            "          \"tokens\": {\n" +
            "            \"end\": 0,\n" +
            "            \"start\": 0\n" +
            "          },\n" +
            "          \"type\": \"string\",\n" +
            "          \"value\": {}\n" +
            "        }\n" +
            "      ],\n" +
            "      \"tokens\": [\n" +
            "        \"string\"\n" +
            "      ]\n" +
            "    },\n" +
            "    \"original_utterance\": \"string\",\n" +
            "    \"type\": \"string\"\n" +
            "  },\n" +
            "  \"session\": {\n" +
            "    \"message_id\": 0,\n" +
            "    \"new\": true,\n" +
            "    \"session_id\": \"string\",\n" +
            "    \"skill_id\": \"string\",\n" +
            "    \"user_id\": \"string\"\n" +
            "  },\n" +
            "  \"version\": \"string\"\n" +
            "}";

    static {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        httpEntity = new HttpEntity<>(STRING, headers);
    }
    private final RestTemplate restTemplate = restTemplate();

    @Override
    @Scheduled(fixedRate=60000)
    @SchedulerLock(name = "UnsleepHerokuImpl.start")
    public void start() {
        System.out.println(restTemplate.exchange("http://alisa-java.herokuapp.com/alisa/command", HttpMethod.POST, httpEntity, Object.class).getStatusCode());
    }

    private static RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(5000);
        httpRequestFactory.setConnectionRequestTimeout(10000);
        return new RestTemplate(httpRequestFactory);
    }
}
