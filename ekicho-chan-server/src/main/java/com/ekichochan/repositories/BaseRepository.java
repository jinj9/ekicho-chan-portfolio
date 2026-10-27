package com.ekichochan.repositories;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.ekichochan.entity.StationMasterEntity;

/**
 * @author jin
 * DBアクセスクラスの基本
 */
public class BaseRepository {
    
    protected final JdbcTemplate jdbcTemplate;
    protected final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public BaseRepository(JdbcTemplate jdbcTemplate,
            NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
    
    /**
     * SQL文を指定して取得する
     * @param sql
     * @param paramMap
     * @param clazz
     */
    public <T> List<T> findBySQL(String sql, Map<String,Object> paramMap, Class<T> clazz){

        MapSqlParameterSource params = new MapSqlParameterSource();

        paramMap.forEach((key, value) -> params.addValue(key, value));
        return this.namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<T>(clazz));
    }
 
    public List<StationMasterEntity> findBySQL() throws DataAccessException {
        // SQL文を作成
        String sql = " SELECT * FROM station_master WHERE 1 = 1 ";
 
        // queryメソッドを実行
        // SQLの実行結果は、自動的にUserクラスにマッピングされて、Listで取得できる。
        RowMapper<StationMasterEntity> rowMapper = new BeanPropertyRowMapper<StationMasterEntity>(StationMasterEntity.class);
        List<StationMasterEntity> stationMasterList = jdbcTemplate.query(sql, rowMapper);
 
        return stationMasterList;
    }
}
