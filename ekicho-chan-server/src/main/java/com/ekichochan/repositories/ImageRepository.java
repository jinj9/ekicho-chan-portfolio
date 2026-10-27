package com.ekichochan.repositories;

import java.io.IOException;
import java.util.Objects;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ekichochan.entity.ImageEntity;
import com.ekichochan.exception.BadRequestException;



/**
 * @author jin
 * 画像テーブルのリポジトリインターフェース
 */
public interface ImageRepository extends JpaRepository<ImageEntity, Integer> {
    
    /**
     * 画像idを条件に画像データを取得する
     * @param id
     * @return ImageEntity
     * 
     * */
    @Query(value =
            " SELECT * FROM image "
            + " WHERE image_id = :imageId ", nativeQuery = true)
    public ImageEntity getImageData(@Param("imageId") String imageId);
    
    /**
     * 駅コードとスペシャル画像フラグを条件に画像データを取得する
     * @param stationCd 駅コード
     * @return ImageEntity
     * 
     * */
    @Query(value =
            " SELECT * FROM image "
            + " WHERE station_cd = :stationCd "
            + " AND is_special_image = :isSpecialImage ", nativeQuery = true)
    public ImageEntity getImageData(
                @Param("stationCd") String stationCd, @Param("isSpecialImage") boolean isSpecialImage
            );
    
    /**
     * 画像マスタのデータを１件更新する。
     * @param ImageEntity
     * @return int
     * */
    public default int updateImage(ImageEntity entity, ImageEntity entitySP) 
            throws BadRequestException, IOException {
        try {
            // DBへの登録処理実行　処理が成功した場合はintの1を返す。
            if (Objects.nonNull(this.saveAndFlush(entity)) && Objects.nonNull(this.saveAndFlush(entitySP))) return 1;
        } catch (Exception e) {
            throw new IOException("データの登録に失敗しました。");
        }
        return 0;
    }

}
