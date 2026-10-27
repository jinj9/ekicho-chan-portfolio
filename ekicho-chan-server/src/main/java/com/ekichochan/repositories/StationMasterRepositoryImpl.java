package com.ekichochan.repositories;
//package com.example.repositories;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.function.Function;
//
//import org.springframework.data.domain.Example;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
//
//import com.example.entity.StationMasterEntity;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.persistence.Query;
//
///**
// * @author jin
// * 駅マスタリポジトリインターフェースの実装クラス
// *
// */
//public class StationMasterRepositoryImpl implements StationMasterRepository {
//    
//    @PersistenceContext()
//    private EntityManager entityManager;
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public List<StationMasterEntity> getStationMasterEntityListByAppearanceRataCd(String appearanceRateCd) {
//        
//        Query query = entityManager
//                .createNativeQuery("SELECT * FROM station_master WHERE appearance_rate_cd = :appearanceRateCd")
//                .setParameter("appearanceRateCd", appearanceRateCd);
//        List<StationMasterEntity> entityList = query.getResultList();
//        return entityList;
//        
//    }
//    
//    @Override
//    public void flush() {
//        // TODO 自動生成されたメソッド・スタブ
//
//    }
//
//    @Override
//    public <S extends StationMasterEntity> S saveAndFlush(S entity) {
//        // TODO 自動生成されたメソッド・スタブ
//        return null;
//    }
//
//    @Override
//    public <S extends StationMasterEntity> List<S> saveAllAndFlush(Iterable<S> entities) {
//        // TODO 自動生成されたメソッド・スタブ
//        return null;
//    }
//
//    @Override
//    public void deleteAllInBatch(Iterable<StationMasterEntity> entities) {
//        // TODO 自動生成されたメソッド・スタブ
//
//    }
//
//    @Override
//    public void deleteAllByIdInBatch(Iterable<Integer> ids) {
//        // TODO 自動生成されたメソッド・スタブ
//
//    }
//
//    @Override
//    public void deleteAllInBatch() {
//        // TODO 自動生成されたメソッド・スタブ
//
//    }
//
//    @Override
//    public StationMasterEntity getOne(Integer id) {
//        // TODO 自動生成されたメソッド・スタブ
//        return null;
//    }
//
//    @Override
//    public StationMasterEntity getById(Integer id) {
//        // TODO 自動生成されたメソッド・スタブ
//        return null;
//    }
//
//    @Override
//    public StationMasterEntity getReferenceById(Integer id) {
//        // TODO 自動生成されたメソッド・スタブ
//        return null;
//    }
//
//    @Override
//    public <S extends StationMasterEntity> List<S> findAll(Example<S> example) {
//        // TODO 自動生成されたメソッド・スタブ
//        return null;
//    }
//
//    @Override
//    public <S extends StationMasterEntity> List<S> findAll(Example<S> example, Sort sort) {
//        // TODO 自動生成されたメソッド・スタブ
//        return null;
//    }
//
//    @Override
//    public <S extends StationMasterEntity> List<S> saveAll(Iterable<S> entities) {
//        // TODO 自動生成されたメソッド・スタブ
//        return null;
//    }
//
//    @Override
//    public List<StationMasterEntity> findAll() {
//        // TODO 自動生成されたメソッド・スタブ
//        return findAll();
//    }
//
//    @Override
//    public List<StationMasterEntity> findAllById(Iterable<Integer> ids) {
//        // TODO 自動生成されたメソッド・スタブ
//        return null;
//    }
//
//    @Override
//    public <S extends StationMasterEntity> S save(S entity) {
//        // TODO 自動生成されたメソッド・スタブ
//        return null;
//    }
//
//    @Override
//    public Optional<StationMasterEntity> findById(Integer id) {
//        // TODO 自動生成されたメソッド・スタブ
//        return Optional.empty();
//    }
//
//    @Override
//    public boolean existsById(Integer id) {
//        // TODO 自動生成されたメソッド・スタブ
//        return false;
//    }
//
//    @Override
//    public long count() {
//        // TODO 自動生成されたメソッド・スタブ
//        return 0;
//    }
//
//    @Override
//    public void deleteById(Integer id) {
//        // TODO 自動生成されたメソッド・スタブ
//
//    }
//
//    @Override
//    public void delete(StationMasterEntity entity) {
//        // TODO 自動生成されたメソッド・スタブ
//
//    }
//
//    @Override
//    public void deleteAllById(Iterable<? extends Integer> ids) {
//        // TODO 自動生成されたメソッド・スタブ
//
//    }
//
//    @Override
//    public void deleteAll(Iterable<? extends StationMasterEntity> entities) {
//        // TODO 自動生成されたメソッド・スタブ
//
//    }
//
//    @Override
//    public void deleteAll() {
//        // TODO 自動生成されたメソッド・スタブ
//
//    }
//
//    @Override
//    public List<StationMasterEntity> findAll(Sort sort) {
//        // TODO 自動生成されたメソッド・スタブ
//        return null;
//    }
//
//    @Override
//    public Page<StationMasterEntity> findAll(Pageable pageable) {
//        // TODO 自動生成されたメソッド・スタブ
//        return null;
//    }
//
//    @Override
//    public <S extends StationMasterEntity> Optional<S> findOne(Example<S> example) {
//        // TODO 自動生成されたメソッド・スタブ
//        return Optional.empty();
//    }
//
//    @Override
//    public <S extends StationMasterEntity> Page<S> findAll(Example<S> example, Pageable pageable) {
//        // TODO 自動生成されたメソッド・スタブ
//        return null;
//    }
//
//    @Override
//    public <S extends StationMasterEntity> long count(Example<S> example) {
//        // TODO 自動生成されたメソッド・スタブ
//        return 0;
//    }
//
//    @Override
//    public <S extends StationMasterEntity> boolean exists(Example<S> example) {
//        // TODO 自動生成されたメソッド・スタブ
//        return false;
//    }
//
//    @Override
//    public <S extends StationMasterEntity, R> R findBy(Example<S> example,
//            Function<FetchableFluentQuery<S>, R> queryFunction) {
//        // TODO 自動生成されたメソッド・スタブ
//        return null;
//    }
//
//}
