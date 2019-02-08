package ru.drudenko.alisa.service.unsleep;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UnsleepHerokuImpl implements UnsleepHeroku {

    private final RestTemplate restTemplate = restTemplate();

    @Scheduled(cron = "0 */5 * * * *")
    @Override
    public void start() {
        restTemplate.getForObject("https://alisa-java.herokuapp.com/", Object.class);
    }

    private static RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(5000);
        httpRequestFactory.setConnectionRequestTimeout(10000);
        return new RestTemplate(httpRequestFactory);
    }
}
