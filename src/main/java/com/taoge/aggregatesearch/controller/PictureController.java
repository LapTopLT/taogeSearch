package com.taoge.aggregatesearch.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taoge.aggregatesearch.common.BaseResponse;
import com.taoge.aggregatesearch.common.ErrorCode;
import com.taoge.aggregatesearch.common.ResultUtils;
import com.taoge.aggregatesearch.exception.ThrowUtils;
import com.taoge.aggregatesearch.model.dto.picture.PictureQueryRequest;
import com.taoge.aggregatesearch.model.entity.Picture;
import com.taoge.aggregatesearch.service.PictureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/picture")
@Slf4j
public class PictureController {

    @Resource
    private PictureService pictureService;
    /**
     *
     * 分页获取列表（封装类）
     * @param pictureQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
            HttpServletRequest request) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        //限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Picture> picturePage = pictureService.searchPicture(pictureQueryRequest.getSearchText(), current, size);
        return ResultUtils.success(picturePage);

    }

}
