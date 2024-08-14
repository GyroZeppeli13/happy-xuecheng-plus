package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(value = "课程教师编辑接口", tags = "课程教师编辑接口")
@RestController
public class CourseTeacherController {

    @Autowired
    private CourseTeacherService courseTeacherService;

    @ApiOperation("查询教师信息")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacherDto> listTeacher(@PathVariable("courseId") Long courseId){
        List<CourseTeacherDto> courseTeacherDtos = courseTeacherService.listTeacher(courseId);
        return courseTeacherDtos;
    }

    @ApiOperation("教师信息创建或修改")
    @PostMapping("/courseTeacher")
    public CourseTeacherDto saveTeacherInfo( @RequestBody @Validated CourseTeacherDto courseTeacherDto){
        //机构id，由于认证系统没有上线暂时硬编码
        Long companyId = 1232141425L;
        return courseTeacherService.saveCourseTeacher(companyId,courseTeacherDto);
    }

    @ApiOperation("删除教师信息")
    @DeleteMapping("/courseTeacher/course/{courseId}/{id}")
    public void deleteTeacherInfo(@PathVariable("courseId") Long courseId,@PathVariable("id") Long id){
        Long companyId = 1232141425L;
        courseTeacherService.deleteTeacher(companyId,courseId,id);
    }
}
