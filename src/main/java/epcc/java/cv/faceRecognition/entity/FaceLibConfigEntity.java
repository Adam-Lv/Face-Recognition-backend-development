package epcc.java.cv.faceRecognition.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("facelib")
public class FaceLibConfigEntity {
    private Integer dimensions;
    private Integer maxItemCount;
    private Integer maxLibCount;
    private String rootPath;
}
