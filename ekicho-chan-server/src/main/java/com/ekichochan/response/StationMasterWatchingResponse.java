package com.ekichochan.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author jin
 * 駅データのレスポンスクラス
 * 閲覧処理実行時に使用する
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class StationMasterWatchingResponse extends StationMasterResponse {
    
    /** 獲得日付 */
    private String gettingDate;
    
}
