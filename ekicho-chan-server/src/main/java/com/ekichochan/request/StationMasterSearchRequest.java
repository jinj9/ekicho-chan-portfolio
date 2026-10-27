/**
 * 
 */
package com.ekichochan.request;

import lombok.Data;

/**
 * @author jin
 * 
 * 駅マスタ検索のリクエストクラス
 */
@Data
public class StationMasterSearchRequest {
    
    /** ユーザID */
    private String userId;
    
    /** 都道府県コード */
    private Integer prefectureCd;
    
    /** 駅コード */
    private String stationCd;

}
