/**
 * 
 */
package com.ekichochan.request;

import lombok.Data;

/**
 * @author jin
 * 位置情報GETのリクエストクラス
 */
@Data
public class LocationGetRequest {
    
    /** ユーザID */
    private String userId;
    
    /** 駅コード */
    private String stationCd;
    
    /** 現在位置情報（緯度） */
    private Double latitude;
    
    /** 現在位置情報（経度） */
    private Double longitude;
    
}
