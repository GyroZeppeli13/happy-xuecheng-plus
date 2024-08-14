package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.exception.XuechengPlusWithCodeException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 课程计划service接口实现类
 * @author Mr.M
 * @date 2022/9/9 11:14
 * @version 1.0
 */
@Service
  public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Override
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {
        Long teachplanDtoId = teachplanDto.getId();
        if(teachplanDtoId == null){
            Teachplan teachplan = new Teachplan();
            //取出同父同级别的课程计划数量
            int count = getTeachplanCount(teachplanDto.getCourseId(), teachplanDto.getParentid());
            teachplan.setOrderby(count+1);
            BeanUtils.copyProperties(teachplanDto,teachplan);
            teachplanMapper.insert(teachplan);
        }
        else{
            Teachplan teachplan = teachplanMapper.selectById(teachplanDtoId);
            BeanUtils.copyProperties(teachplanDto,teachplan);
            teachplanMapper.updateById(teachplan);
        }
    }

    /**
     * @description 获取最新的排序号
     * @param courseId  课程id
     * @param parentId  父课程计划id
     * @return int 最新排序号
     * @author Mr.M
     * @date 2022/9/9 13:43
     */
    private int getTeachplanCount(long courseId,long parentId){
//        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Teachplan::getCourseId,courseId);
//        queryWrapper.eq(Teachplan::getParentid,parentId);
//        //将count改为查最大max的orderby，这样性能更好
//        Integer count = teachplanMapper.selectCount(queryWrapper);
        Integer count = teachplanMapper.selectMaxOrdeby(courseId, parentId);
        if(count == null) count = -1;//如果此时没有课程集合则第一个课程集合orderby为0(-1 + 1)
        return count;
    }

    @Transactional
    @Override
    public void deleteTeachplan(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        if(teachplan.getGrade() == 1 && teachplan.getParentid() == 0){
            Integer secondCount = teachplanMapper.getSecondCount(id);
            // 删除第一级别的大章节时要求大章节下边没有小章节时方可删除。
            if(secondCount > 0){
                throw new XuechengPlusWithCodeException(120409L, "课程计划信息还有子级信息，无法操作");
            }
        }
        else if(teachplan.getGrade() == 2){
            // 删除第二级别的小章节的同时需要将teachplan_media表关联的信息也删除。
            teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId, id));
        }
        teachplanMapper.deleteById(id);
    }

    private static final String MOVE_TYPE_UP = "moveup";
    private static final String MOVE_TYPE_DOWN = "movedown";
    @Transactional
    @Override
    public void moveTeachplan(String moveType, Long id) {
        // 1.数据的合法性校验
        if (StringUtils.isBlank(moveType) || id == null) {
            throw new RuntimeException("移动类型为空或课程计划id为空");
        }
        if (!MOVE_TYPE_UP.equals(moveType) && !MOVE_TYPE_DOWN.equals(moveType)) {
            throw new RuntimeException("移动类型不合法");
        }
        // 2.获取同类型的章节list
        Teachplan teachplan = teachplanMapper.selectById(id);
        Integer grade = teachplan.getGrade();
        if (grade == 1) {
            // 大章节
            LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Teachplan::getCourseId, teachplan.getCourseId()).eq(Teachplan::getParentid, 0L).orderByAsc(Teachplan::getOrderby);
            List<Teachplan> teachplans = teachplanMapper.selectList(wrapper);
            // 移动
            move(teachplans, teachplan, moveType);
        }
        else {
            // 小章节
            LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Teachplan::getCourseId, teachplan.getCourseId()).eq(Teachplan::getParentid, teachplan.getParentid()).orderByAsc(Teachplan::getOrderby);
            List<Teachplan> teachplans = teachplanMapper.selectList(wrapper);
            // 移动
            move(teachplans, teachplan, moveType);
        }
    }

    private void move(List<Teachplan> teachplans,Teachplan teachplan, String moveType){
        // 获取当前课程计划的索引
        int index = teachplans.indexOf(teachplan);
        // 获取当前课程计划的排序值
        Integer orderby = teachplan.getOrderby();
        // 判断移动类型
        if (MOVE_TYPE_UP.equals(moveType)) {
            // 上移
            if (index == 0) {
                XueChengPlusException.cast("已经处在最上的位置,无法继续上移");
            }
            // 交换课程计划的排序值
            Teachplan lastTeachplan = teachplans.get(index - 1);
            Integer lastOrderby = lastTeachplan.getOrderby();
            teachplan.setOrderby(lastOrderby);
            lastTeachplan.setOrderby(orderby);
            // 更新
            teachplanMapper.updateById(teachplan);
            teachplanMapper.updateById(lastTeachplan);
        } else {
            // 下移
            int max = teachplans.size() - 1;
            if (index == max) {
                XueChengPlusException.cast("已经处在最下的位置,无法继续下移");
            }
            // 交换课程计划的排序值
            Teachplan nextTeachplan = teachplans.get(index + 1);
            Integer nextOrderby = nextTeachplan.getOrderby();
            teachplan.setOrderby(nextOrderby);
            nextTeachplan.setOrderby(orderby);
            // 更新
            teachplanMapper.updateById(teachplan);
            teachplanMapper.updateById(nextTeachplan);
        }
    }


    @Transactional
    @Override
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        //教学计划id
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if(teachplan==null){
            XueChengPlusException.cast("教学计划不存在");
        }
        Integer grade = teachplan.getGrade();
        if(grade!=2){
            XueChengPlusException.cast("只允许第二级教学计划绑定媒资文件");
        }
        //课程id
        Long courseId = teachplan.getCourseId();

        //先删除原来该教学计划绑定的媒资
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId,teachplanId));

        //再添加教学计划与媒资的绑定关系
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setCourseId(courseId);
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMediaMapper.insert(teachplanMedia);
        return teachplanMedia;
    }

    @Transactional
    @Override
    public void unbindVideo(String teachPlanId, String mediaId) {
        if(StringUtils.isBlank(teachPlanId) || StringUtils.isBlank(mediaId)){
            XueChengPlusException.cast("参数异常");
        }
        int delete = teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId, teachPlanId));
        if(delete <= 0) XueChengPlusException.cast("删除失败");
    }
}