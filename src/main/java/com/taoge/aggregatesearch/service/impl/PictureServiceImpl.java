package com.taoge.aggregatesearch.service.impl;/*
 *Author:Litao
 *Created in:
 */

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taoge.aggregatesearch.common.ErrorCode;
import com.taoge.aggregatesearch.exception.BusinessException;
import com.taoge.aggregatesearch.model.entity.Picture;
import com.taoge.aggregatesearch.service.PictureService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PictureServiceImpl implements PictureService {

    @Override
    public Page<Picture> searchPicture(String searchText, long pageNum, long pageSize) {
        long curr = pageSize * (pageNum - 1);
        String url = String.format("https://cn.bing.com/images/search?q=%s&form=HDRSC2&first=%d",searchText,curr);
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据获取异常");
        }
        Elements elements = doc.select(".iuscp.isv");
        List<Picture> pictures = new ArrayList<>();
        for (Element element : elements) {
            String m = element.select(".iusc").get(0).attr("m");
            String title = element.select(".inflnk").get(0).attr("aria-label");
            Map<String,Object> map = JSONUtil.toBean(m,Map.class);
            String murl = (String) map.get("murl");
            String purl = (String) map.get("purl");
            Picture pic = new Picture();
            pic.setTitle(title);
            pic.setUrl(murl);
            pic.setPurl(purl);
            pictures.add(pic);
            if(pictures.size() >= pageSize){
                break;
            }
        }
        Page<Picture> picturePage = new Page<>(pageNum,pageSize);
        picturePage.setRecords(pictures);
        return picturePage;
    }
}
