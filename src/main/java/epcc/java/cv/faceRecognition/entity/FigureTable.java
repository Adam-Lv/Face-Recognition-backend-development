package epcc.java.cv.faceRecognition.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("figureTable")
public class FigureTable {
    @TableId(type = IdType.AUTO)
    private int id;

    private String url;

    private String groupName;

    private Date dateName;
}
