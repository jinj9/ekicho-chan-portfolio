package com.ekichochan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/** 
 * 駅情報マスタのエンティティクラス
 *  */

@Entity
@Table(name = "station_master")
@Data
public class StationMasterEntity {

    /** id */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;
    
    /** 駅コード */
    @Column(name="station_cd")
    private String stationCd;
    
    /** 駅名 */
    @Column(name="station_name")
    private String stationName;
    
    /** 駅名（英名） */
    @Column(name="station_english_name")
    private String stationEnglishName;
    
    /** 緯度 */
    @Column(name="latitude")
    private Double latitude;
    
    /** 経度 */
    @Column(name="longitude")
    private Double longitude;
    
    /** 都道府県コード */
    @Column(name="prefecture_cd")
    private int prefectureCd;
    
    /** 出現率コード */
    @Column(name="appearance_rate_cd")
    private String appearanceRateCd;
    
    /** 駅情報テキスト */
    @Column(name="station_information_text")
    private String stationInformationText;
    
}
