-- テーブル作成

-- 獲得済画像データ
DROP TABLE if exists obtained_image_data CASCADE;

CREATE TABLE obtained_image_data (
  id serial NOT NULL
  , user_id character varying(128) NOT NULL
  , image_id character varying(10) NOT NULL
  , getting_date date NOT NULL
  , CONSTRAINT obtained_image_data_PKC PRIMARY KEY (id)
) ;

GRANT ALL PRIVILEGES ON obtained_image_data TO ekicho1;
GRANT USAGE ON SEQUENCE obtained_image_data_id_seq TO ekicho1;

-- ユーザマスタ
DROP TABLE if exists user_master CASCADE;

CREATE TABLE user_master (
  id serial NOT NULL
  , user_id character varying(128) NOT NULL
  , user_name character varying(10)
  , user_password bytea NOT NULL
  , CONSTRAINT user_master_PKC PRIMARY KEY (id)
) ;

GRANT ALL PRIVILEGES ON user_master TO ekicho1;
GRANT USAGE ON SEQUENCE user_master_id_seq TO ekicho1;

-- 画像データ
DROP TABLE if exists image CASCADE;

CREATE TABLE image (
  id serial NOT NULL
  , image_id character varying(10) NOT NULL
  , station_cd character varying(8) NOT NULL
  , image_data bytea NOT NULL
  , image_type character varying(4) NOT NULL
  , is_special_image boolean DEFAULT false NOT NULL
  , CONSTRAINT image_PKC PRIMARY KEY (id)
) ;

GRANT ALL PRIVILEGES ON image TO ekicho1;
GRANT USAGE ON SEQUENCE image_id_seq TO ekicho1;

-- 駅情報マスタ
DROP TABLE if exists station_master CASCADE;

CREATE TABLE station_master (
  id serial NOT NULL
  , station_cd character varying(8) NOT NULL
  , station_name character varying(10) NOT NULL
  , station_english_name character varying(30) NOT NULL
  , latitude decimal(10,7)
  , longitude decimal(10,7)
  , prefecture_cd integer NOT NULL
  , appearance_rate_cd character varying(1) DEFAULT '1' NOT NULL
  , station_information_text character varying(256)
  , CONSTRAINT station_master_PKC PRIMARY KEY (id)
) ;

GRANT ALL PRIVILEGES ON station_master TO ekicho1;
GRANT USAGE ON SEQUENCE station_master_id_seq TO ekicho1;

ALTER TABLE station_master ADD CONSTRAINT station_cd
  UNIQUE (station_cd) ;

COMMENT ON TABLE obtained_image_data IS '獲得済画像データ:獲得した画像データを管理するためのテーブル
ユーザIDごとに管理する。';
COMMENT ON COLUMN obtained_image_data.id IS 'id';
COMMENT ON COLUMN obtained_image_data.user_id IS 'ユーザID';
COMMENT ON COLUMN obtained_image_data.image_id IS '画像ID';
COMMENT ON COLUMN obtained_image_data.getting_date IS '獲得日付';

COMMENT ON TABLE user_master IS 'ユーザマスタ';
COMMENT ON COLUMN user_master.id IS 'id';
COMMENT ON COLUMN user_master.user_id IS 'ユーザID';
COMMENT ON COLUMN user_master.user_name IS 'ユーザ名';
COMMENT ON COLUMN user_master.user_password IS 'パスワード';

COMMENT ON TABLE image IS '画像データ';
COMMENT ON COLUMN image.id IS 'id';
COMMENT ON COLUMN image.image_id IS '画像ID';
COMMENT ON COLUMN image.station_cd IS '駅コード';
COMMENT ON COLUMN image.image_data IS '画像データ(バイナリ)';
COMMENT ON COLUMN image.image_type IS '画像タイプ:jpeg or png';
COMMENT ON COLUMN image.is_special_image IS 'スペシャル画像フラグ:trueはスペシャル画像として扱う';

COMMENT ON TABLE station_master IS '駅情報マスタ:駅情報マスタ';
COMMENT ON COLUMN station_master.id IS 'id';
COMMENT ON COLUMN station_master.station_cd IS '駅コード';
COMMENT ON COLUMN station_master.station_name IS '駅名';
COMMENT ON COLUMN station_master.station_english_name IS '駅名（英名）';
COMMENT ON COLUMN station_master.latitude IS '緯度';
COMMENT ON COLUMN station_master.longitude IS '経度';
COMMENT ON COLUMN station_master.prefecture_cd IS '都道府県コード:都道府県コードはENUMに設定';
COMMENT ON COLUMN station_master.appearance_rate_cd IS '出現率コード';
COMMENT ON COLUMN station_master.station_information_text IS '駅情報テキスト';
