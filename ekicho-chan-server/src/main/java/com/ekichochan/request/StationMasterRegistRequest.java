package com.ekichochan.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

/**
 * @author jin
 * 駅マスタ登録リクエストクラス
 */
@Data
public class StationMasterRegistRequest {
    
    /** 駅コード */
    @NotBlank
    @Pattern(regexp = "^[0-9]{1,8}$")
    private String stationCd;
    
    /** 駅名 */
    @NotBlank
    @Size(max = 100)
    private String stationName;
    
    /** 駅名（英名） */
    @NotBlank
    @Size(max = 100)
    private String stationEnglishName;
    
    /** 緯度 */
    @Size(max = 16)
    private String latitude;
    
    /** 経度 */
    @Size(max = 16)
    private String longitude;
    
    /** 都道府県コード */
    @Min(1)
    @Max(47)
    private int prefectureCd;
    
    /** 出現率コード */
    @NotNull
    @Pattern(regexp = "^[1-5]{1}$")
    private String appearanceRateCd;
    
    /** 駅情報テキスト */
    @Size(max = 1000)
    private String stationInformationText;
    
}
