package com.ekichochan.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author jin
 * 駅マスタ画像データ登録リクエストクラス
 */
@Data
public class StationMasterImageDataRegistRequest {

    /** 駅コード */
    @NotBlank
    @Pattern(regexp = "^[0-9]{1,8}$")
    private String stationCd;
    
    /** 画像データ */
    @NotNull
    @Size(max = 6990508)
    private String imageData;
    
    /** 画像タイプ */
    @NotNull
    @Pattern(regexp = "(?i)^(|png|jpg|jpeg)$")
    private String imageType;
    
    /** 画像データ（スペシャル） */
    @NotNull
    @Size(max = 6990508)
    private String imageDataSP;
    
    /** 画像タイプ（スペシャル） */
    @NotNull
    @Pattern(regexp = "(?i)^(|png|jpg|jpeg)$")
    private String imageTypeSP;
}
