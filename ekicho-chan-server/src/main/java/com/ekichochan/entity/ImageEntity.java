package com.ekichochan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/** 
 * 画像テーブルのエンティティクラス
 *  */

@Entity
@Table(name = "image")
@Data
public class ImageEntity {
	
	/** id */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    
    /** 画像ID */
    @Column(name="image_id")
    private String imageId;
    
    /** 駅コード */
    @Column(name="station_cd")
    private String stationCd;
    
    /** 画像データ（バイナリ） */
    @Column(name="image_data")
    private byte[] imageData;
    
    /** 画像タイプ */
    @Column(name="image_type")
    private String imageType;
    
    /** スペシャル画像フラグ */
    @Column(name="is_special_image")
    private Boolean isSpecialImage;
	
}
