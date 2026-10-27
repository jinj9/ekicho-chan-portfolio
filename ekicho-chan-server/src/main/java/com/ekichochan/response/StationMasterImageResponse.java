package com.ekichochan.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author jin
 * 駅データのレスポンスクラス
 * 閲覧処理実行時に使用する
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class StationMasterImageResponse extends StationMasterResponse {
    
    /** スペシャル画像データ */
    private String imageDataSP;
    
    /** スペシャル画像データタイプ */
    private String imageTypeSP;
    
}
