package com.icia.delivery.dto.rider;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "rider_account")
@NoArgsConstructor
@AllArgsConstructor
public class RiderAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rider_aid")
    private Long riderAid;        // 계좌 PK 아이디

    @Column(name = "rider_No")
    private Long riderNo;         // 라이더(PK) 번호 - FK

    @Column(name = "rider_bank_name")
    private String riderBankName;      // 은행명

    @Column(name = "rider_account_number")
    private String riderAccountNumber; // 계좌번호

    @Column(name = "rider_account_holder")
    private String riderAccountHolder; // 예금주명

    // Entity -> DTO 변환
    public static RiderAccountEntity toEntity(RiderAccountDTO dto){
        RiderAccountEntity entity = new RiderAccountEntity();

        entity.setRiderAid(dto.getRiderAid());
        entity.setRiderNo(dto.getRiderNo());
        entity.setRiderBankName(dto.getRiderBankName());
        entity.setRiderAccountNumber(dto.getRiderAccountNumber());
        entity.setRiderAccountHolder(dto.getRiderAccountHolder());

        return entity;
    }
}
