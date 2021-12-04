package epcc.java.cv.faceRecognition.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

//@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
public class FaceVectorJsonEntity {
    @JsonProperty("head_count_num")
    private int headCountNum;
    @JsonProperty("head_boxes")
    private List<float[]> headBoxes;
    @JsonProperty("landmarks")
    private float[] landmarks;
    @JsonProperty("features")
    private Features features;

    @Data
    public static class Features {
        @JsonProperty("face_recognize_result")
        private List<float[]> faceRecognizeResult;
    }
}
