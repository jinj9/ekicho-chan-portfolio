package com.ekichochan.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 駅データのレスポンスクラス
 * えきちょーちゃんガチャで使用する
 * */

@Data
@EqualsAndHashCode(callSuper=false)
public class StationMasterResponse {

    /** id */
    private int id;
    
    /** 駅コード */
    private String stationCd;
    
    /** 駅名 */
    private String stationName;
    
    /** 駅名（英名） */
    private String stationEnglishName;
    
    /** 緯度 */
    private Double latitude;
    
    /** 経度 */
    private Double longitude;
    
    /** 都道府県コード */
    private int prefectureCd;
    
    /** 都道府県名 */
    private String locationName;
    
    /** 出現率コード */
    private String appearanceRateCd;
    
    /** 駅情報テキスト */
    private String stationInformationText;
    
    /** 画像データ */
    private String imageData;
    
    /** 画像データタイプ */
    private String imageType;
    
    /** 獲得済フラグ */
    private Boolean isObtained;
    
}
