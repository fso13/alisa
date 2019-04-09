package ru.drudenko.alisa.core.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tb_alisa_client")
public class AlisaClient {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "is_active")
    private boolean isActive = false;

    @OneToMany(mappedBy = "alisaClient", fetch = FetchType.EAGER)
    private Set<Token> tokens = new HashSet<>();

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
