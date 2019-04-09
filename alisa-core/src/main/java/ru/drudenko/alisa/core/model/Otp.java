package ru.drudenko.alisa.core.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.Instant;


@Getter
@Setter
@Entity
@Table(name = "tb_otp")
public class Otp {

    @Id
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @GeneratedValue(generator = "uuid")
    private String id;

    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    private OtpType type;

    @Column(name = "ref")
    private String ref;

    @Column(name = "value")
    private String value;

    private Boolean expired = false;

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
