package com.xuecheng.content.model.dto;

import com.baomidou.mybatisplus.annotation.*;
import com.xuecheng.base.exception.ValidationGroups;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 课程-教师关系表
 * </p>
 *
 * @author itcast
 */
@Data
@ApiModel(value="AddCourseTeacherDto", description="教师Dto")
public class CourseTeacherDto implements Serializable {
    /**
     * 主键
     */
    @ApiModelProperty(value = "教师id", required = true)
    private Long id;

    @ApiModelProperty(value = "课程id", required = true)
    private Long courseId;

    @NotEmpty(message = "教师名称不能为空")
    @ApiModelProperty(value = "教师标识", required = true)
    private String teacherName;

    @NotEmpty(message = "教师职位不能为空")
    @ApiModelProperty(value = "教师职位", required = true)
    private String position;

    @NotEmpty(message = "教师简介不能为空")
    @Size(message = "教师简介内容过少", min = 10)
    @ApiModelProperty(value = "教师简介", required = true)
    private String introduction;

    @ApiModelProperty(value = "教师照片", required = false)
    private String photograph;

    @ApiModelProperty(value = "创建时间", required = false)
    private LocalDateTime createDate;


}
