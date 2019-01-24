package ru.drudenko.alisa.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "tb_client")
public class Client {
    @Id
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @GeneratedValue(generator = "uuid")
    private String id;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "person_id")
    private String personId;

    @Column(name = "is_active")
    private boolean isActive = false;

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
