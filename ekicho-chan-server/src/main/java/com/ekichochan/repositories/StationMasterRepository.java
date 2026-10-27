package com.ekichochan.repositories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.JdbcTemplate;

import com.ekichochan.entity.StationMasterEntity;
import com.ekichochan.exception.BadRequestException;
import com.ekichochan.request.StationMasterSearchRequest;

import io.micrometer.common.util.StringUtils;

/**
 * @author jin
 * 駅マスタのリポジトリインターフェース
 *
 */
public interface StationMasterRepository extends JpaRepository<StationMasterEntity, Integer> {

    public static final JdbcTemplate jdbcTemplate = new JdbcTemplate();
    
	/**
     * 駅コードを条件に駅マスタ1件を取得する
     * @param stationCd 駅コード
     * @return StationMasterEntity 駅マスタ
     * 
     * */
    @Query(value = "SELECT * FROM station_master " +
            "WHERE station_cd = :stationCd ", nativeQuery = true)
    public StationMasterEntity
    	getStationMasterEntityByStationCd(@Param("stationCd") String stationCd);
    
    /**
     * 都道府県コードを条件に駅マスタリストを取得する
     * @param prefectureCd 都道府県コード
     * @return List<StationMasterEntity> 駅マスタリスト
     * 
     * */
    @Query(value = "SELECT * FROM station_master " +
            "WHERE prefecture_cd = :prefectureCd ORDER BY id ", nativeQuery = true)
    public List<StationMasterEntity>
        getStationMasterEntityByPrefectureCd(@Param("prefectureCd") int prefectureCd);
	
	/**
     * 出現率コードを条件に駅マスタのリストを取得する
     * @param appearanceCd 出現率コード
     * @return List<StationMasterEntity> 駅マスタのリスト
     * 
     * */
    @Query(value = "SELECT * FROM station_master " +
            "WHERE appearance_rate_cd = :appearanceRateCd ", nativeQuery = true)
    public List<StationMasterEntity> 
    	getStationMasterEntityListByAppearanceRataCd(@Param("appearanceRateCd") String appearanceRateCd);
    
    /**
     * 駅マスタのリストをIDのソート順で取得する。
     * */
    public List<StationMasterEntity> findAllByOrderById();
    
    /**
     * 駅マスタのリストを都道府県コードのソート順で取得する。
     * */
    public List<StationMasterEntity> findAllByOrderByPrefectureCd();
    
    /**
     * 検索リクエストクラスを条件に駅マスタのリストを取得する
     * @param StationMasterSearchRequest 駅マスタ検索のリクエストクラス
     * @return List<StationMasterResponse> 駅マスタのリスト
     * 
     * */
    public default List<StationMasterEntity> 
        getStationMasterEntityList(StationMasterSearchRequest request) {
        
        if (StringUtils.isNotEmpty(request.getStationCd())) {
            // リクエストに駅コードが指定されている場合
            List<StationMasterEntity> stationMasterEntityList = new ArrayList<StationMasterEntity>();
            stationMasterEntityList.add(getStationMasterEntityByStationCd(request.getStationCd()));
            return stationMasterEntityList;
        } else if (Objects.nonNull(request.getPrefectureCd())) {
            // リクエストに都道府県コードが指定されている場合
            return getStationMasterEntityByPrefectureCd(request.getPrefectureCd());
        } else {
            // リクエストに何も指定されていない場合
            return this.findAllByOrderById();
        }
    }
    
    /**
     * 駅マスタにデータを１件登録する。
     * @param StationMasterEntity
     * @return int
     * */
    public default int registStationMaster(StationMasterEntity entity) 
            throws BadRequestException, IOException {
        try {
            // DBへの登録処理実行　処理が成功した場合はintの1を返す。
            if (Objects.nonNull(this.saveAndFlush(entity))) return 1;
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("駅コードが重複しています。");
        }
        return 0;
    }
    
    /**
     * 駅マスタのデータを１件更新する。
     * @param StationMasterEntity
     * @return int
     * */
    public default int updateStationMaster(StationMasterEntity entity) 
            throws BadRequestException, IOException {
        try {
            // DBへの登録処理実行　処理が成功した場合はintの1を返す。
            if (Objects.nonNull(this.saveAndFlush(entity))) return 1;
        } catch (Exception e) {
            throw new IOException("データベースの処理に失敗しました。");
        }
        return 0;
    }
    
    /**
     * BeanPropertyRowMapperでSQLを実行する方法
     * */
//    public default List<StationMasterEntity> getStationMasterEntityList(
//            StationMasterSearchRequest request) {
//
//        String sql = " SELECT * FROM station_master WHERE 1 = 1 ";
//        
//        RowMapper<StationMasterEntity> rowMapper = new BeanPropertyRowMapper<StationMasterEntity>(
//                StationMasterEntity.class);
//        
//        ArrayList<Object> queryList = new ArrayList<Object>();
//        queryList.add(sql);
//        queryList.add(rowMapper);
//
//        if (StringUtils.isNotEmpty(request.getStationCd())) {
//            // リクエストに駅コードが指定されている場合
//            sql += " AND station_cd = ? ";
//            queryList.add(request.getStationCd());
//        } else if (Objects.nonNull(request.getPrefectureCd())) {
//            // リクエストに都道府県コードが指定されている場合
//            sql += " AND prefecture_cd = ? ";
//            queryList.add(request.getPrefectureCd());
//        }
//        
//        List<StationMasterEntity> stationMasterEntityList = jdbcTemplate.query(sql, new DataClassRowMapper<>(StationMasterEntity.class));
//
//        return stationMasterEntityList;
//    }
    
//    /**
//     * ユーザIDを条件に駅マスタのリストを取得する
//     * @param userId ユーザID
//     * @return List<StationMasterEntity> 駅マスタのリスト
//     * 
//     * */
//    @Query(value = 
//    		" SELECT * FROM obtained_image_data AS oid "
//    		+ " LEFT JOIN image AS im ON oid.image_id = im.image_id "
//    		+ " LEFT JOIN station_master AS sm ON im.station_cd = sm.station_cd"
//            + " WHERE oid.user_id = :userId ", nativeQuery = true)
//    public List<StationMasterEntity> getWatchingStationData(@Param("userId") String userId);
    
}
