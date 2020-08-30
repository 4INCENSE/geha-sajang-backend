package com.incense.gehasajang.domain.house;

import com.incense.gehasajang.domain.host.Host;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
public class HostHouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "host_house_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "host_id")
    private Host host;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "house_id")
    private House house;

    @Builder
    public HostHouse(Host host, House house) {
        this.host = host;
        this.house = house;
    }
}
