package com.ekichochan.service;

import com.ekichochan.exception.BadRequestException;
import com.ekichochan.request.StationMasterImageDataRegistRequest;
import com.ekichochan.request.StationMasterRegistRequest;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.springframework.stereotype.Component;

@Component
public class AdminRequestValidator {
    private static final int MAX_IMAGE_BYTES = 5 * 1024 * 1024;
    private static final int MAX_DIMENSION = 4096;
    private static final long MAX_PIXELS = 16_000_000L;

    public void validateStation(StationMasterRegistRequest request) throws BadRequestException {
        validateCoordinate(request.getLatitude(), -90, 90, "緯度");
        validateCoordinate(request.getLongitude(), -180, 180, "経度");
    }

    public void validateImages(StationMasterImageDataRegistRequest request) throws BadRequestException {
        validateImage(request.getImageData(), request.getImageType(), "通常画像");
        validateImage(request.getImageDataSP(), request.getImageTypeSP(), "スペシャル画像");
    }

    private void validateCoordinate(String value, double min, double max, String label)
            throws BadRequestException {
        if (value == null || value.isBlank()) return;
        try {
            double number = Double.parseDouble(value);
            int decimals = value.contains(".") ? value.length() - value.indexOf('.') - 1 : 0;
            if (!Double.isFinite(number) || number < min || number > max || decimals > 7) {
                throw new BadRequestException(label + "の値が範囲外です。");
            }
        } catch (NumberFormatException exception) {
            throw new BadRequestException(label + "は数値で入力してください。");
        }
    }

    private void validateImage(String encoded, String type, String label) throws BadRequestException {
        if (encoded.isBlank() && type.isBlank()) return;
        if (encoded.isBlank() || type.isBlank()) {
            throw new BadRequestException(label + "のデータと形式を両方指定してください。");
        }
        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(encoded);
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException(label + "のデータ形式が正しくありません。");
        }
        if (bytes.length > MAX_IMAGE_BYTES) {
            throw new BadRequestException(label + "は5MB以下にしてください。");
        }
        String normalizedType = type.toLowerCase();
        boolean png = bytes.length >= 8 && bytes[0] == (byte) 0x89 && bytes[1] == 0x50
                && bytes[2] == 0x4e && bytes[3] == 0x47;
        boolean jpeg = bytes.length >= 3 && bytes[0] == (byte) 0xff
                && bytes[1] == (byte) 0xd8 && bytes[2] == (byte) 0xff;
        if ((normalizedType.equals("png") && !png)
                || ((normalizedType.equals("jpg") || normalizedType.equals("jpeg")) && !jpeg)) {
            throw new BadRequestException(label + "の拡張子と実際の画像形式が一致しません。");
        }
        try (ImageInputStream stream = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
            if (!readers.hasNext()) throw new BadRequestException(label + "を画像として読み取れません。");
            ImageReader reader = readers.next();
            try {
                reader.setInput(stream);
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                if (width > MAX_DIMENSION || height > MAX_DIMENSION || (long) width * height > MAX_PIXELS) {
                    throw new BadRequestException(label + "の縦横サイズが大きすぎます。");
                }
            } finally {
                reader.dispose();
            }
        } catch (BadRequestException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BadRequestException(label + "を画像として読み取れません。");
        }
    }
}
