package com.ekichochan.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/** 
 * 獲得済画像テーブルのエンティティクラス
 *  */

@Entity
@Table(name = "obtained_image_data")
@Data
public class ObtainedImageDataEntity {
	
	/** id */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    
    /** ユーザID */
    @Column(name="user_id")
    private String userId;
    
    /** 画像ID */
    @Column(name="image_id")
    private String imageId;
    
    /** 獲得日付 */
    @Column(name="getting_date")
    private LocalDate gettingDate;
	
}
