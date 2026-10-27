package com.ekichochan.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;

import com.ekichochan.entity.ObtainedImageDataEntity;

/**
 * @author jin
 * 獲得済画像データテーブルのリポジトリインターフェース
 */
public interface ObtainedImageDataRepository extends JpaRepository<ObtainedImageDataEntity, Integer> {

    @Query(value = """
            SELECT o.*
              FROM obtained_image_data o
              JOIN image i ON i.image_id = o.image_id
              JOIN station_master s ON s.station_cd = i.station_cd
             WHERE o.user_id = :userId
               AND (:prefectureCd = 0 OR s.prefecture_cd = :prefectureCd)
               AND (:stationName = '' OR s.station_name ILIKE CONCAT('%', :stationName, '%'))
               AND (:appearanceRateCd = '' OR s.appearance_rate_cd = :appearanceRateCd)
             ORDER BY
               CASE WHEN :sort = 'oldest' THEN o.getting_date END ASC,
               CASE WHEN :sort = 'station' THEN s.station_name END ASC,
               CASE WHEN :sort = 'newest' THEN o.getting_date END DESC,
               o.id DESC
            """,
            countQuery = """
            SELECT COUNT(*)
              FROM obtained_image_data o
              JOIN image i ON i.image_id = o.image_id
              JOIN station_master s ON s.station_cd = i.station_cd
             WHERE o.user_id = :userId
               AND (:prefectureCd = 0 OR s.prefecture_cd = :prefectureCd)
               AND (:stationName = '' OR s.station_name ILIKE CONCAT('%', :stationName, '%'))
               AND (:appearanceRateCd = '' OR s.appearance_rate_cd = :appearanceRateCd)
            """, nativeQuery = true)
    Page<ObtainedImageDataEntity> searchObtainedImages(
            @Param("userId") String userId,
            @Param("prefectureCd") int prefectureCd,
            @Param("stationName") String stationName,
            @Param("appearanceRateCd") String appearanceRateCd,
            @Param("sort") String sort,
            Pageable pageable);
	
	/**
     * ユーザIDを条件に画像データを取得する
     * @param userId ユーザID
     * @return ObtainedImageDataEntity 獲得済画像データのリスト
     * 
     * */
    @Query(value =
    		" SELECT * FROM obtained_image_data "
            + " WHERE user_id = :userId ", nativeQuery = true)
    public List<ObtainedImageDataEntity> getObtainedImageData(@Param("userId") String userId);
    
    /**
     * ユーザIDと駅コードを条件に画像データを取得する
     * @param userId ユーザID
     * @param stationCd 駅コード
     * @return ObtainedImageDataEntity 獲得済画像データ
     * */
    @Query(value =
            " SELECT * FROM obtained_image_data "
            + " WHERE user_id = :userId "
            + " AND image_id = :imageId "
            , nativeQuery = true)
    public ObtainedImageDataEntity getObtainedImageData(
            @Param("userId") String userId, @Param("imageId") String imageId
            );
    
    /**
     * 獲得済画像テーブルに１件追加する
     * @param ObtainedImageDataEntity
     * 
     * */
    public default void addObtaindeImageData(ObtainedImageDataEntity entity) {
        this.save(entity);
    }

    @Modifying
    @Query(value = "DELETE FROM obtained_image_data WHERE user_id = :userId", nativeQuery = true)
    int deleteByUserId(@Param("userId") String userId);

}
