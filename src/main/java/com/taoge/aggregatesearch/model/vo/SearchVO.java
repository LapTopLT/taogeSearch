package com.taoge.aggregatesearch.model.vo;/*
 *Author:Litao
 *Created in:
 */

import com.google.gson.Gson;
import com.taoge.aggregatesearch.model.entity.Picture;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 聚合搜索
 */

@Data
public class SearchVO implements Serializable {

    private final static Gson GSON = new Gson();

    private List<UserVO> userList;

    private List<PostVO> postList;

    private List<Picture> pictureList;

    private List<Object> dataList;

    private static final long serialVersionUID = 1L;

}
