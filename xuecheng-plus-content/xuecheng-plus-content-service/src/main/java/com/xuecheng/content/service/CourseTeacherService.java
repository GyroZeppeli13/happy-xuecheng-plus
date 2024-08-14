package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface CourseTeacherService {

    /**
     * 查询教师信息
     * @param courseId
     * @return
     */
    public List<CourseTeacherDto> listTeacher(@PathVariable("courseId") Long courseId);

    /**
     * 添加或修改教师信息
     * @param companyId
     * @param courseTeacherDto
     * @return
     */
    public CourseTeacherDto saveCourseTeacher(Long companyId,CourseTeacherDto courseTeacherDto);

    /**
     * 删除教师信息
     * @param companyId
     * @param courseId
     * @param id
     */
    public void deleteTeacher(Long companyId, Long courseId,Long id);

    /**
     * 根据id集合批量删除教师信息
     * @param companyId
     * @param courseId
     * @param ids
     */
    public void deleteBatchByIds(Long companyId, Long courseId, List<Long> ids);
}
