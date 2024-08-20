package com.xuecheng.checkcode.service.impl;

import com.xuecheng.checkcode.model.CheckCodeParamsDto;
import com.xuecheng.checkcode.model.CheckCodeResultDto;
import com.xuecheng.checkcode.service.AbstractCheckCodeService;
import com.xuecheng.checkcode.service.CheckCodeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @description 数字验证码生成器
 * @date 2022/9/29 16:16
 */
@Service("NumCheckCodeService")
public class NumCheckCodeServiceImpl extends AbstractCheckCodeService implements CheckCodeService {

    @Resource(name = "NumberLetterCheckCodeGenerator")
    @Override
    public void setCheckCodeGenerator(CheckCodeGenerator checkCodeGenerator) {
        this.checkCodeGenerator = checkCodeGenerator;
    }

    @Resource(name = "UUIDKeyGenerator")
    @Override
    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    @Resource(name = "RedisCheckCodeStore")
    @Override
    public void setCheckCodeStore(CheckCodeStore checkCodeStore) {
        this.checkCodeStore = checkCodeStore;
    }

    @Override
    public CheckCodeResultDto generate(CheckCodeParamsDto checkCodeParamsDto) {
        GenerateResult generate = generate(checkCodeParamsDto,
                4, "checkcode:",300);
        String key = generate.getKey();
        String code = generate.getCode();
        CheckCodeResultDto checkCodeResultDto = new CheckCodeResultDto();
        checkCodeResultDto.setAliasing(code);
        checkCodeResultDto.setKey(key);
        return checkCodeResultDto;
    }
}

