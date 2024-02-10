package io.mosip.iiitb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@NoArgsConstructor
@Data
@Entity
@Table(name = "uin_hash_salt")
public class UinHashSaltEntity {
    @Id
    private int id;

    @Column(name = "salt")
    private String salt;

    @Column(name = "cr_by")
    private String createdBy;


    @Column(name = "cr_dtimes")
    private LocalDateTime createdDTimes;

    @Column(name = "upd_by")
    private String updatedBy;


    @Column(name = "upd_dtimes")
    private LocalDateTime updatedDTimes;
}
