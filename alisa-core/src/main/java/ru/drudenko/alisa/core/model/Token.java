package ru.drudenko.alisa.core.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "tb_token")
public class Token {
    @Id
    private String id;

    @Column(name = "accessToken")
    private String accessToken;

    @Column(name = "refreshToken")
    private String refreshToken;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private AlisaClient alisaClient;

    @Column(name = "oauth_client")
    private String oauthClient;

    @Column(name = "time_create")
    private Instant createTime;

    @Column(name = "time_update")
    private Instant updateTime;

    @PrePersist
    private void save() {
        this.createTime = Instant.now();
        this.updateTime = Instant.now();
    }

    @PreUpdate
    private void update() {
        this.updateTime = Instant.now();
    }
}
