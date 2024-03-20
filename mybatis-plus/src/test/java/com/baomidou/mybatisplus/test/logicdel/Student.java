package com.baomidou.mybatisplus.test.logicdel;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * @author haitang
 * @since 2024-03-20
 */

@Data
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    @TableField(fill = FieldFill.UPDATE)
    private String deleteBy;

    @TableLogic(mode = LogicMode.ID)
    private Long deleted;
}
