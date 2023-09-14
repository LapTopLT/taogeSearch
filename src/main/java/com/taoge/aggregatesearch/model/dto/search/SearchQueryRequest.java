package com.taoge.aggregatesearch.model.dto.search;/*
 *Author:Litao
 *Created in:
 */

import com.taoge.aggregatesearch.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class SearchQueryRequest extends PageRequest implements Serializable {
    private String searchText;

    /**
     * 类型
     */
    private String type;

    private static final long serialVersionUID = 1L;
}
