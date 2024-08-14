package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {
    @Autowired
    private CourseTeacherMapper courseTeacherMapper;

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Override
    public List<CourseTeacherDto> listTeacher(Long courseId) {
        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(new LambdaQueryWrapper<CourseTeacher>()
                .eq(CourseTeacher::getCourseId, courseId));
        List<CourseTeacherDto> courseTeacherDtos = courseTeachers.stream().map(courseTeacher -> {
            CourseTeacherDto courseTeacherDto = new CourseTeacherDto();
            BeanUtils.copyProperties(courseTeacher, courseTeacherDto);
            return courseTeacherDto;
        }).collect(Collectors.toList());
        return courseTeacherDtos;
    }

    private void checkIsItsCompany(Long companyId, Long courseId, String message) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(!companyId.equals(courseBase.getCompanyId())){
            XueChengPlusException.cast(message);
        }
    }

    @Override
    public CourseTeacherDto saveCourseTeacher(Long companyId, CourseTeacherDto courseTeacherDto) {
        checkIsItsCompany(companyId,courseTeacherDto.getCourseId(),"只允许操作自己机构教师的信息");
        if(courseTeacherDto.getId() == null){
            //为添加教师信息请求
            CourseTeacher courseTeacher = new CourseTeacher();
            BeanUtils.copyProperties(courseTeacherDto,courseTeacher);
            courseTeacher.setCreateDate(LocalDateTime.now());
            int insert = courseTeacherMapper.insert(courseTeacher);
            CourseTeacher newCourseTeacher = courseTeacherMapper.selectById(courseTeacher.getId());
            BeanUtils.copyProperties(newCourseTeacher,courseTeacherDto);
        }
        else{
            CourseTeacher courseTeacher = courseTeacherMapper.selectById(courseTeacherDto.getId());
            if(courseTeacher == null){
                XueChengPlusException.cast("教师信息不存在");
            }
            BeanUtils.copyProperties(courseTeacherDto,courseTeacher);
            courseTeacherMapper.updateById(courseTeacher);
            courseTeacher = courseTeacherMapper.selectById(courseTeacherDto.getId());
            BeanUtils.copyProperties(courseTeacher,courseTeacherDto);
        }
        return courseTeacherDto;
    }

    @Override
    public void deleteTeacher(Long companyId, Long courseId, Long id) {
        checkIsItsCompany(companyId,courseId,"只允许操作自己机构教师的信息");
        courseTeacherMapper.deleteById(id);
    }

    @Override
    public void deleteBatchByIds(Long companyId, Long courseId, List<Long> ids){
//        checkIsItsCompany(companyId,courseId,"只允许操作自己机构教师的信息");
        if(ids != null && !ids.isEmpty()) courseTeacherMapper.deleteBatchIds(ids);
    }


}
