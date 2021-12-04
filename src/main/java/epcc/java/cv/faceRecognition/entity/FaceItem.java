package epcc.java.cv.faceRecognition.entity;

import com.github.jelmerk.knn.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class FaceItem implements Item<Integer, float[]>{

    //indexId是标识在人脸库中每一个人脸的id，不是这个人脸属于哪个图片的id。实测indexId不能重复，若插入重复id则会覆盖掉原来的数据
    private int indexId;
    private float[] vector;
    //用于标识这个人脸属于哪个图片
    @Getter
    @Setter
    private int figureId;

    @Override
    public Integer id() {
        return indexId;
    }

    @Override
    public float[] vector() {
        return vector;
    }

    @Override
    public int dimensions() {
        return vector.length;
    }
}
