package com.ekichochan.service;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.ekichochan.common.Enum;
import com.ekichochan.entity.ImageEntity;
import com.ekichochan.entity.ObtainedImageDataEntity;
import com.ekichochan.entity.StationMasterEntity;
import com.ekichochan.exception.BadRequestException;
import com.ekichochan.exception.StationDataNotFoundException;
import com.ekichochan.repositories.ImageRepository;
import com.ekichochan.repositories.ObtainedImageDataRepository;
import com.ekichochan.repositories.StationMasterRepository;
import com.ekichochan.request.LocationGetRequest;
import com.ekichochan.request.StationMasterImageDataRegistRequest;
import com.ekichochan.request.StationMasterRegistRequest;
import com.ekichochan.request.StationMasterSearchRequest;
import com.ekichochan.response.StationMasterImageResponse;
import com.ekichochan.response.StationMasterResponse;
import com.ekichochan.response.StationMasterWatchingResponse;

import io.micrometer.common.util.StringUtils;

@Service
public class StationMasterService {

	private final StationMasterRepository stationMasterRepository;

	private final ImageRepository imageRepository;

	private final ObtainedImageDataRepository obtainedImageDataRepository;

	public StationMasterService(StationMasterRepository stationMasterRepository, ImageRepository imageRepository,
			ObtainedImageDataRepository obtainedImageDataRepository) {
		this.stationMasterRepository = stationMasterRepository;
		this.imageRepository = imageRepository;
		this.obtainedImageDataRepository = obtainedImageDataRepository;
	}

	/**
	 * えきちょーちゃんガチャ実行
	 * 
	 * @param userId ユーザID
	 * @return responseData 駅データ
	 */
	public StationMasterResponse getStationMasterSerch(String userId) {

		final String randomAppearanceRataCd = getAppearanceRateCd();

		List<StationMasterEntity> stationDataList = stationMasterRepository
				.getStationMasterEntityListByAppearanceRataCd(randomAppearanceRataCd);

		// 取得したリストの中から、ランダムでデータを一つ取得する
		final int stationGetNumber = new Random().nextInt(stationDataList.size());
		StationMasterEntity stationData = stationDataList.get(stationGetNumber);

		// 画像データの取得
		// ガチャGETの場合はスペシャル画像フラグがFALSEのデータを取得する
		ImageEntity imageData = imageRepository.getImageData(stationData.getStationCd(), false);

		// 画像データ（バイナリ）をエンコーディングする
		Base64.Encoder encoder = Base64.getEncoder();

		// 取得したエンティティをレスポンスクラスにセットする
		StationMasterResponse responseData = new StationMasterResponse();
		responseData.setId(stationData.getId());
		responseData.setStationCd(stationData.getStationCd());
		responseData.setStationName(stationData.getStationName());
		responseData.setStationEnglishName(stationData.getStationEnglishName());
		responseData.setLatitude(Objects.nonNull(stationData.getLatitude()) ? stationData.getLatitude() : null);
		responseData.setLongitude(Objects.nonNull(stationData.getLongitude()) ? stationData.getLongitude() : null);
		responseData.setLocationName(Enum.PREFECTURE.getByCode(stationData.getPrefectureCd()));
		responseData.setAppearanceRateCd(stationData.getAppearanceRateCd());
		responseData.setStationInformationText(stationData.getStationInformationText());
		responseData.setImageData(encoder.encodeToString(imageData.getImageData()));
		responseData.setIsObtained(addGettingStationData(userId, imageData.getImageId()));

		return responseData;
	}

	/**
	 * 出現率コードを確率計算する
	 */
	private String getAppearanceRateCd() {
		// 0から999の中からランダムで数値を設定
		final int randomNum = new Random().nextInt(1000);

		LocalDate nowDate = LocalDate.now();
		DayOfWeek dayOfWeek = nowDate.getDayOfWeek();

		// 現在日付が月曜日の場合は、1%の確率で出現率コード"5"を返す
		if (DayOfWeek.MONDAY.equals(dayOfWeek) && randomNum <= 8) {
			return "5";
		}
		if (randomNum <= 599) {
			// 0~599 出現率60%
			return "1";
		} else if (randomNum <= 849) {
			// 600~849 出現率25%
			return "2";
		} else if (randomNum <= 949) {
			// 850~949 出現率10%
			return "3";
		} else if (randomNum <= 998) {
			// 950~998 出現率4.9%
			return "4";
		} else {
			// 出現率0.1%
			return "5"; // randomNum == 999
		}

		// 乱数テスト
//        List<Integer> list = new ArrayList<Integer>();
//        for (int i = 1; ; i++) {
//            int num = new Random().nextInt(1000);
//            list.add(num);
//            if (DayOfWeek.SATURDAY.equals(dayOfWeek) && num <= 3) {
//                System.out.print("リスト：");
//                list.forEach(n -> System.out.print(n + ","));
//                System.out.println();
//                System.out.print("試行回数：");
//                System.out.println(i);
//                return "5";
//            }
//            if (num <= 599) {
//                // 0~599 出現率60%
		//// return "1";
//                continue;
//            } else if (num <= 849) {
//                // 600~849 出現率25%
		//// return "2";
//                continue;
//            } else if (num <= 949) {
//                // 850~949 出現率10%
		//// return "3";
//                continue;
//            } else if (num <= 998) {
//                // 950~998 出現率4.9%
		//// return "4";
//                continue;
//            } else {
//                // 出現率0.1%
//                System.out.print("リスト：");
//                list.forEach(n -> System.out.print(n + ","));
//                System.out.println();
//                System.out.print("試行回数：");
//                System.out.println(i);
//                return "5";    // randomNum == 999
//            }
//            
//        }

	}

	/**
	 * 位置情報GET実行
	 * 
	 * @param LocationGetRequest 位置情報GETのリクエストクラス
	 * @return responseData 駅データ
	 * @throws IOException, BadRequestException
	 */
	public StationMasterResponse getStationMasterSerch(LocationGetRequest request)
			throws IOException, BadRequestException, StationDataNotFoundException {

		// 駅の位置情報を取得するために駅データを1件取得
		StationMasterEntity stationData = stationMasterRepository
				.getStationMasterEntityByStationCd(request.getStationCd());

		// 駅データの緯度または経度がNULLの場合はエラーメッセージを出力する
		if (Objects.isNull(stationData.getLatitude()) || Objects.isNull(stationData.getLongitude())) {
			throw new StationDataNotFoundException(stationData.getStationName() + "駅:データベースに緯度経度情報が登録されていません。");
		}

		// 位置情報GETの判定のために現在位置と駅との距離を取得
		double distance = getDistanceByLatitudeLongitude(request.getLatitude(), request.getLongitude(),
				stationData.getLatitude(), stationData.getLongitude());

		// 位置情報GETの判定 距離が設定値より大きい場合は初期状態のレスポンスクラス（位置情報GET成功フラグがFALSE）を返却する。
		if (Double.compare(distance, Enum.SUCCESSDISTANCE) > 0) {
			throw new BadRequestException("位置情報によるGETができませんでした。" + "\n" + "現在位置と駅の距離：約" + Math.round(distance) + "m");
		}

		// 画像データの取得
		// 位置情報GETの場合はスペシャル画像フラグがTRUEのデータを取得する
		ImageEntity imageData = imageRepository.getImageData(stationData.getStationCd(), true);

		// スペシャル画像が未登録の場合はエラーとする
		if (imageData.getImageData().length == 0) {
			throw new StationDataNotFoundException("データが未登録のためGETできませんでした。");
		}

		// 画像データ（バイナリ）をエンコーディングする
		Base64.Encoder encoder = Base64.getEncoder();

		StationMasterResponse responseData = new StationMasterResponse();

		// 取得したエンティティをレスポンスクラスにセットする
		responseData.setId(stationData.getId());
		responseData.setStationCd(stationData.getStationCd());
		responseData.setStationName(stationData.getStationName());
		responseData.setStationEnglishName(stationData.getStationEnglishName());
		responseData.setLatitude(Objects.nonNull(stationData.getLatitude()) ? stationData.getLatitude() : null);
		responseData.setLongitude(Objects.nonNull(stationData.getLongitude()) ? stationData.getLongitude() : null);
		responseData.setLocationName(Enum.PREFECTURE.getByCode(stationData.getPrefectureCd()));
		responseData.setAppearanceRateCd(stationData.getAppearanceRateCd());
		responseData.setStationInformationText(stationData.getStationInformationText());
		responseData.setImageData(encoder.encodeToString(imageData.getImageData()));
		responseData.setIsObtained(addGettingStationData(request.getUserId(), imageData.getImageId()));

		return responseData;
	}

	/**
	 * 駅マスタ検索処理実行
	 * 
	 * @param StationMasterSearchRequest 駅マスタ検索処理のリクエストクラス
	 * @return responseList 駅データリスト
	 * @throws IOException, BadRequestException
	 */
	public List<StationMasterResponse> getStationMasterSearch(StationMasterSearchRequest request) {

		List<StationMasterEntity> stationDataList = stationMasterRepository.getStationMasterEntityList(request);
		List<StationMasterResponse> responseList = new ArrayList<StationMasterResponse>();

		for (StationMasterEntity stationData : stationDataList) {
			StationMasterResponse responseData = new StationMasterResponse();
			// 取得したエンティティをレスポンスクラスにセットする
			responseData.setId(stationData.getId());
			responseData.setStationCd(stationData.getStationCd());
			responseData.setStationName(stationData.getStationName());
			responseData.setStationEnglishName(stationData.getStationEnglishName());
			responseData.setLatitude(Objects.nonNull(stationData.getLatitude()) ? stationData.getLatitude() : null);
			responseData.setLongitude(Objects.nonNull(stationData.getLongitude()) ? stationData.getLongitude() : null);
			responseData.setLocationName(Enum.PREFECTURE.getByCode(stationData.getPrefectureCd()));
			responseData.setAppearanceRateCd(stationData.getAppearanceRateCd());
			responseData.setStationInformationText(stationData.getStationInformationText());

			responseList.add(responseData);
		}

		return responseList;
	}

	/**
	 * 駅マスタを１件取得する
	 * 
	 * @param id 駅マスタのid
	 * @return responseData 駅データ
	 * @throws IOException, BadRequestException
	 */
	public StationMasterResponse getStationMasterSearch(int id) {

		StationMasterEntity stationData = stationMasterRepository.getReferenceById(id);
		StationMasterResponse responseData = new StationMasterResponse();

		// 取得したエンティティをレスポンスクラスにセットする
		responseData.setId(stationData.getId());
		responseData.setStationCd(stationData.getStationCd());
		responseData.setStationName(stationData.getStationName());
		responseData.setStationEnglishName(stationData.getStationEnglishName());
		responseData.setLatitude(Objects.nonNull(stationData.getLatitude()) ? stationData.getLatitude() : null);
		responseData.setLongitude(Objects.nonNull(stationData.getLongitude()) ? stationData.getLongitude() : null);
		responseData.setLocationName(Enum.PREFECTURE.getByCode(stationData.getPrefectureCd()));
		responseData.setPrefectureCd(stationData.getPrefectureCd());
		responseData.setAppearanceRateCd(stationData.getAppearanceRateCd());
		responseData.setStationInformationText(stationData.getStationInformationText());

		return responseData;
	}

	/**
	 * 駅マスタのデータを取得する 駅リスト（オートコンプリート）に使用する
	 * 
	 * @param prefectureCd
	 * @return 駅データリスト
	 */
	public List<StationMasterResponse> getStationMasterList(String prefectureCd) {

		List<StationMasterEntity> stationDataList = new ArrayList<StationMasterEntity>();
		List<StationMasterResponse> responseList = new ArrayList<StationMasterResponse>();

		final String num = "99";

		if (num.equals(prefectureCd)) {
			stationDataList = stationMasterRepository.findAllByOrderByPrefectureCd();
		} else {
			stationDataList = stationMasterRepository
					.getStationMasterEntityByPrefectureCd(Integer.parseInt(prefectureCd));
		}

		for (StationMasterEntity stationData : stationDataList) {
			StationMasterResponse responseData = new StationMasterResponse();
			// 取得したエンティティをレスポンスクラスにセットする
			responseData.setId(stationData.getId());
			responseData.setStationCd(stationData.getStationCd());
			responseData.setStationName(stationData.getStationName());

			responseList.add(responseData);
		}

		return responseList;
	}

	/**
	 * 駅マスタと紐づく画像データ取得する
	 * 
	 * @param id 駅マスタのid
	 * @return responseData 駅データ
	 * @throws IOException, BadRequestException
	 */
	public StationMasterResponse getStationMasterAndImage(int id) {

		// 画像データ（バイナリ）をエンコーディングする
		Base64.Encoder encoder = Base64.getEncoder();

		StationMasterEntity stationData = stationMasterRepository.getReferenceById(id);

		// 画像データの取得
		ImageEntity imageData = imageRepository.getImageData(stationData.getStationCd(), false);
		ImageEntity imageDataSP = imageRepository.getImageData(stationData.getStationCd(), true);

		StationMasterImageResponse responseData = new StationMasterImageResponse();

		// 取得したエンティティをレスポンスクラスにセットする
		responseData.setId(stationData.getId());
		responseData.setStationCd(stationData.getStationCd());
		responseData.setStationName(stationData.getStationName());
		responseData.setImageData(Objects.nonNull(imageData) ? encoder.encodeToString(imageData.getImageData()) : "");
		responseData.setImageType(Objects.nonNull(imageData) ? imageData.getImageType() : "");
		responseData
				.setImageDataSP(Objects.nonNull(imageDataSP) ? encoder.encodeToString(imageDataSP.getImageData()) : "");
		responseData.setImageTypeSP(Objects.nonNull(imageDataSP) ? imageDataSP.getImageType() : "");

		return responseData;
	}

	/**
	 * リクエストの緯度経度と駅データの緯度経度を使って、距離(メートル)を計算する。
	 */
	private Double getDistanceByLatitudeLongitude(Double latitude1, Double longitude1, Double latitude2,
			Double longitude2) {

		// 緯度経度をラジアンに変換
		double rlat1 = Math.toRadians(latitude1);
		double rlng1 = Math.toRadians(longitude1);
		double rlat2 = Math.toRadians(latitude2);
		double rlng2 = Math.toRadians(longitude2);

		// 2点の中心角(ラジアン)を求める
		double a = Math.sin(rlat1) * Math.sin(rlat2) + Math.cos(rlat1) * Math.cos(rlat2) * Math.cos(rlng1 - rlng2);
		double rr = Math.acos(a);

		// 地球赤道半径(メートル)
		final double earth_radius = 6378140;

		// 2点間の距離(メートル)
		double distance = earth_radius * rr;

		return distance;
	}

	/**
	 * ガチャで獲得した画像データを獲得済テーブルにインサートする 画像データが既に存在する場合は獲得済フラグTRUEを返却する
	 * 画像データが存在しない場合は、テーブルにインサートしてFALSEを返却する
	 * 
	 * @param userID
	 * @param imageId
	 * @return boolean
	 */
	private Boolean addGettingStationData(String userId, String imageId) {

		// 既にテーブルに同一データが存在するか確認する
		ObtainedImageDataEntity obtainedImageData = obtainedImageDataRepository.getObtainedImageData(userId, imageId);
		// 同一データが存在する場合は獲得済フラグtrueを返却する
		if (Objects.nonNull(obtainedImageData)) {
			return true;
		}

		// 同一データが存在しない場合はエンティティにセットする
		ObtainedImageDataEntity entity = new ObtainedImageDataEntity();
		entity.setUserId(userId);
		entity.setImageId(imageId);
		entity.setGettingDate(LocalDate.now()); // 現在日付をセットする

		// エンティティをインサートする
		obtainedImageDataRepository.addObtaindeImageData(entity);

		return false;
	}

	/**
	 * えきちょーちゃん閲覧
	 * 
	 * @param userId ユーザID
	 * @return List<StationMasterResponse> 駅データのリスト
	 */
	public List<StationMasterWatchingResponse> getWatchingStationData(String userId) {

		// 獲得済画像データリストの取得
		List<ObtainedImageDataEntity> obtainedImageDataList = obtainedImageDataRepository.getObtainedImageData(userId);

		// 画像データ（バイナリ）をエンコーディングする
		Base64.Encoder encoder = Base64.getEncoder();

		List<StationMasterWatchingResponse> responseList = new ArrayList<StationMasterWatchingResponse>();

		// 獲得済画像データリストから画像データと駅データを取得する
		for (ObtainedImageDataEntity obtainedImageData : obtainedImageDataList) {
			// 画像データの取得
			ImageEntity imageData = imageRepository.getImageData(obtainedImageData.getImageId());
			if (Objects.isNull(imageData)) {
				continue;
			}
			// 駅データの取得
			StationMasterEntity stationData = stationMasterRepository
					.getStationMasterEntityByStationCd(imageData.getStationCd());

			// レスポンスデータをセット
			StationMasterWatchingResponse responseData = new StationMasterWatchingResponse();
			responseData.setId(stationData.getId());
			responseData.setStationCd(stationData.getStationCd());
			responseData.setStationName(stationData.getStationName());
			responseData.setStationEnglishName(stationData.getStationEnglishName());
			responseData.setLatitude(Objects.nonNull(stationData.getLatitude()) ? stationData.getLatitude() : null);
			responseData.setLongitude(Objects.nonNull(stationData.getLongitude()) ? stationData.getLongitude() : null);
			responseData.setLocationName(Enum.PREFECTURE.getByCode(stationData.getPrefectureCd()));
			responseData.setAppearanceRateCd(stationData.getAppearanceRateCd());
			responseData.setStationInformationText(stationData.getStationInformationText());
			responseData.setImageData(encoder.encodeToString(imageData.getImageData()));
			responseData.setGettingDate(localDateToString(obtainedImageData.getGettingDate()));

			// レスポンスリストに追加
			responseList.add(responseData);
		}

		return responseList;
	}

	public com.ekichochan.response.ObtainedStationPageResponse getWatchingStationDataPage(String userId, int page,
			int size, int prefectureCd, String stationName, String appearanceRateCd, String sort) {
		int safePage = Math.max(page, 0);
		int safeSize = Math.min(Math.max(size, 1), 48);
		String safeSort = switch (sort) {
		case "oldest", "station" -> sort;
		default -> "newest";
		};
		Page<ObtainedImageDataEntity> result = obtainedImageDataRepository.searchObtainedImages(userId, prefectureCd,
				stationName.strip(), appearanceRateCd, safeSort, PageRequest.of(safePage, safeSize));
		List<StationMasterWatchingResponse> content = toWatchingResponses(result.getContent());
		return new com.ekichochan.response.ObtainedStationPageResponse(content, result.getNumber(), result.getSize(),
				result.getTotalElements(), result.getTotalPages());
	}

	private List<StationMasterWatchingResponse> toWatchingResponses(
			List<ObtainedImageDataEntity> obtainedImageDataList) {
		Base64.Encoder encoder = Base64.getEncoder();
		List<StationMasterWatchingResponse> responseList = new ArrayList<>();
		for (ObtainedImageDataEntity obtainedImageData : obtainedImageDataList) {
			ImageEntity imageData = imageRepository.getImageData(obtainedImageData.getImageId());
			if (Objects.isNull(imageData))
				continue;
			StationMasterEntity stationData = stationMasterRepository
					.getStationMasterEntityByStationCd(imageData.getStationCd());
			if (Objects.isNull(stationData))
				continue;
			StationMasterWatchingResponse responseData = new StationMasterWatchingResponse();
			responseData.setId(stationData.getId());
			responseData.setStationCd(stationData.getStationCd());
			responseData.setStationName(stationData.getStationName());
			responseData.setStationEnglishName(stationData.getStationEnglishName());
			responseData.setLatitude(stationData.getLatitude());
			responseData.setLongitude(stationData.getLongitude());
			responseData.setLocationName(Enum.PREFECTURE.getByCode(stationData.getPrefectureCd()));
			responseData.setAppearanceRateCd(stationData.getAppearanceRateCd());
			responseData.setStationInformationText(stationData.getStationInformationText());
			responseData.setImageData(encoder.encodeToString(imageData.getImageData()));
			responseData.setGettingDate(localDateToString(obtainedImageData.getGettingDate()));
			responseList.add(responseData);
		}
		return responseList;
	}

	/**
	 * 駅マスタにデータを新規登録する。
	 * 
	 * @param StationMasterRegistRequest
	 * @return int
	 */
	public int registStationMaster(StationMasterRegistRequest request) throws BadRequestException, IOException {

		Double latitude = null;
		Double longitude = null;

		if (StringUtils.isNotEmpty(request.getLatitude())) {
			// 緯度の型変換
			latitude = Double.parseDouble(request.getLatitude());
		}
		if (StringUtils.isNotEmpty(request.getLongitude())) {
			// 経度の型変換
			longitude = Double.parseDouble(request.getLongitude());
		}

		StationMasterEntity entity = new StationMasterEntity();
		entity.setStationCd(request.getStationCd());
		entity.setStationName(request.getStationName());
		entity.setStationEnglishName(request.getStationEnglishName());
		entity.setLatitude(latitude);
		entity.setLongitude(longitude);
		entity.setPrefectureCd(request.getPrefectureCd());
		entity.setAppearanceRateCd(request.getAppearanceRateCd());
		entity.setStationInformationText(request.getStationInformationText());

		return stationMasterRepository.registStationMaster(entity);
	}

	/**
	 * 駅マスタにデータを更新する。
	 * 
	 * @param int                        id
	 * @param StationMasterRegistRequest
	 * @return int
	 */
	public int updateStationMaster(int id, StationMasterRegistRequest request) throws BadRequestException, IOException {

		Double latitude = null;
		Double longitude = null;

		if (StringUtils.isNotEmpty(request.getLatitude())) {
			// 緯度の型変換
			latitude = Double.parseDouble(request.getLatitude());
		}
		if (StringUtils.isNotEmpty(request.getLongitude())) {
			// 経度の型変換
			longitude = Double.parseDouble(request.getLongitude());
		}

		StationMasterEntity entity = new StationMasterEntity();
		entity.setId(id);
		entity.setStationCd(request.getStationCd());
		entity.setStationName(request.getStationName());
		entity.setStationEnglishName(request.getStationEnglishName());
		entity.setLatitude(latitude);
		entity.setLongitude(longitude);
		entity.setPrefectureCd(request.getPrefectureCd());
		entity.setAppearanceRateCd(request.getAppearanceRateCd());
		entity.setStationInformationText(request.getStationInformationText());

		return stationMasterRepository.updateStationMaster(entity);
	}

	/**
	 * 駅マスタに紐づく画像データを更新する。
	 * 
	 * @param StationMasterImageDataRegistRequest
	 * @return int
	 */
	public int updateStationMasterImageData(StationMasterImageDataRegistRequest request)
			throws BadRequestException, IOException {

		ImageEntity entity = new ImageEntity();
		ImageEntity entitySP = new ImageEntity();

		if (Objects.nonNull(imageRepository.getImageData(request.getStationCd(), false))) {
			entity = imageRepository.getImageData(request.getStationCd(), false);
		}
		if (Objects.nonNull(imageRepository.getImageData(request.getStationCd(), true))) {
			entitySP = imageRepository.getImageData(request.getStationCd(), true);
		}

		byte[] imageDataByte = Base64.getDecoder().decode(request.getImageData());
		byte[] imageDataSPByte = Base64.getDecoder().decode(request.getImageDataSP());

		entity.setImageId(createImageId(request.getStationCd(), false));
		entity.setStationCd(request.getStationCd());
		entity.setImageData(imageDataByte);
		entity.setImageType(request.getImageType());
		entity.setIsSpecialImage(false);

		entitySP.setImageId(createImageId(request.getStationCd(), true));
		entitySP.setStationCd(request.getStationCd());
		entitySP.setImageData(imageDataSPByte);
		entitySP.setImageType(request.getImageTypeSP());
		entitySP.setIsSpecialImage(true);

		return imageRepository.updateImage(entity, entitySP);
	}

	/**
	 * 画像IDの生成（１０文字の文字列を生成する）
	 */
	private String createImageId(String stationCd, boolean isSpecial) {
		StringBuilder sb = new StringBuilder();
		if (isSpecial) {
			sb.append(stationCd + "SP");
		} else {
			sb.append(stationCd);
		}
		int length = sb.length();
		for (; length < 10;) {
			sb.insert(0, "0");
			length++;
		}
		return sb.toString();
	}

	/**
	 * LocalDateをStringに変換する YYYY-MM-DD → YYYY年MM月DD日
	 */
	private String localDateToString(LocalDate localDate) {
		String formatDate = localDate.toString();
		formatDate = formatDate.replaceFirst("-", "年");
		formatDate = formatDate.replace("-", "月");
		formatDate += "日";

		return formatDate;
	}

}
