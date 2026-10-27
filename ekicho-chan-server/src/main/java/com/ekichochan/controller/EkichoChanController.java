package com.ekichochan.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ekichochan.exception.BadRequestException;
import com.ekichochan.request.LocationGetRequest;
import com.ekichochan.request.StationMasterImageDataRegistRequest;
import com.ekichochan.request.StationMasterRegistRequest;
import com.ekichochan.request.StationMasterSearchRequest;
import com.ekichochan.response.StationMasterResponse;
import com.ekichochan.response.StationMasterWatchingResponse;
import com.ekichochan.service.StationMasterService;

/**
 * @author jin
 *
 */
@RestController
@RequestMapping("/api/stationMaster")
public class EkichoChanController {

    private final StationMasterService stationMasterService;

    public EkichoChanController(StationMasterService stationMasterService) {
        this.stationMasterService = stationMasterService;
    }
    
    /** ロガー名「appLogger」を指定していためappLoggerが適用される */
    private final Logger logger = LoggerFactory.getLogger("appLogger");
    
    @GetMapping("/")
    public void test() {
        logger.info("ログテスト");
    }
    
    /**
     * えきちょーちゃんガチャを行う
     * 
     * @param userId ユーザID
     * @return 駅データ
     */
    @GetMapping("/gacha/{userId}")
    public StationMasterResponse getStationMaster(@PathVariable("userId") String userId) {
        return stationMasterService.getStationMasterSerch(userId);
    }

    /**
     * 位置情報GETを行う
     * 
     * @param LocationGetRequest 位置情報GETのリクエストクラス
     * @return 駅データ
     * @throws IOException
     * @throws BadRequestException
     */
    @PostMapping("/locationGet")
    public StationMasterResponse getStationMasterLocation(@RequestBody LocationGetRequest request)
            throws BadRequestException, IOException {
        return stationMasterService.getStationMasterSerch(request);
    }

    /**
     * えきちょーちゃん閲覧ページの検索処理を行う
     * 
     * @param userId ユーザID
     * @return 駅データのリスト
     */
    @GetMapping("/watchingData/{userId}")
    public List<StationMasterWatchingResponse> getWatchingStationData(@PathVariable("userId") String userId) {
        return stationMasterService.getWatchingStationData(userId);
    }

    /**
     * 駅マスタの検索処理を行う
     * 
     * @param
     * @return 駅データのリスト
     */
    @PostMapping("/search")
    public List<StationMasterResponse> getStationMasterList(@RequestBody StationMasterSearchRequest request) {
        return stationMasterService.getStationMasterSearch(request);
    }

    /**
     * 駅マスタのデータを1件取得する
     * 
     * @param id
     * @return 駅データ
     */
    @GetMapping("/search/{id}")
    public StationMasterResponse getStationMaster(@PathVariable("id") int id) {
        return stationMasterService.getStationMasterSearch(id);
    }
    
    /**
     * 駅マスタのデータを取得する
     * 駅リスト（オートコンプリート）に使用する
     * 
     * @param prefectureCd 都道府県コード
     * @return 駅データリスト
     */
    @GetMapping("/store/stationList/{prefectureCd}")
    public List<StationMasterResponse> getStationMasterList(@PathVariable("prefectureCd") String prefectureCd) {
        return stationMasterService.getStationMasterList(prefectureCd);
    }

    /**
     * 駅マスタのデータと紐づく画像データを取得する
     * 
     * @param id
     * @return 駅データ
     * @return 画像データ
     */
    @GetMapping("/search/image/{id}")
    public StationMasterResponse getStationMasterAndImage(@PathVariable("id") int id) {
        return stationMasterService.getStationMasterAndImage(id);
    }

    /**
     * 駅マスタのデータを登録する。
     * 
     * @param StationMasterRegistRequest
     * @return int
     */
    @PostMapping("/regist")
    public int registStationMaster(@RequestBody StationMasterRegistRequest request)
            throws BadRequestException, IOException {
        return stationMasterService.registStationMaster(request);
    }
    
    /**
     * 駅マスタのデータを更新する。
     * @param int id
     * @param StationMasterRegistRequest
     * @return int
     */
    @PutMapping("/update/{id}")
    public int updateStationMaster(@PathVariable("id") int id, @RequestBody StationMasterRegistRequest request)
            throws BadRequestException, IOException {
        return stationMasterService.updateStationMaster(id, request);
    }
    
    /**
     * 駅マスタに紐づく画像データを更新する。
     * 
     * @param StationMasterImageDataRegistRequest
     * @return int
     */
    @PostMapping("/imageUpdate")
    public int updateStationMasterImageData(@RequestBody StationMasterImageDataRegistRequest request)
            throws BadRequestException, IOException {
        return stationMasterService.updateStationMasterImageData(request);
    }
}
