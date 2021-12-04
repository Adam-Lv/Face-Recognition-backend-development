package epcc.java.cv.faceRecognition.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import epcc.java.cv.faceRecognition.entity.FaceVectorJsonEntity;
import epcc.java.cv.faceRecognition.service.UploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.List;

@Service
public class UploadServiceImpl implements UploadService {
    @Value("${faceServer.url}")
    private String faceServerUrl;

    /**
     * 通过multipart file方式利用post方法传送图片文件到目标服务器，然后解析回传的json字符串，获取vector
     */
    @Override
    public List<float[]> getVectorByFigure(File figure) throws JsonProcessingException {
        //生成请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        //生成请求体
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        FileSystemResource img = new FileSystemResource(figure);
        body.add("img", img); //body中键值对为 <img, 文件流>
        //装配请求头和请求体
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        //发送http请求
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(faceServerUrl, requestEntity, String.class);
        //获取返回体
        String responseBody = response.getBody();
        //返回的json如果用python的数据结构表示，vector在这里：
        //responseBody["features"]["face_recognize_result"][0]
        //下面是处理json
        ObjectMapper mapper = new ObjectMapper();
        FaceVectorJsonEntity faceVectorJsonEntity = mapper.readValue(responseBody, FaceVectorJsonEntity.class);
        return faceVectorJsonEntity.getFeatures().getFaceRecognizeResult();
    }

    @Override
    public void UploadFigures() {
        //第一部分是将图片存在本地存储中，这一部分需要前端返回值，包括上传的方法等，暂时无法实现。
        //同时将图片的详情信息添加到MySQL数据库中，并根据insert指令中设置的自增id获取每张图片
        //插入到数据库中后对应的id（这个id是图片的唯一标识）。

        //第二部分是将每张图片和对应的figureId（图片的唯一标识）group，date，通过 getVectorByFigure 方法获得这张图片的所有
        //人脸的vector，然后插入到对应的路径下的人脸库中。
    }
}
