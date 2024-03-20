package com.baomidou.mybatisplus.test.logicdel;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.injector.methods.LogicDeleteBatchByIds;
import com.baomidou.mybatisplus.test.BaseDbTest;
import org.apache.ibatis.reflection.MetaObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class StudentLogicDelTest extends BaseDbTest<StudentMapper> {

    @Test
    void testLogicDel() {

        doTestAutoCommit(i -> {
            int delete = i.deleteById(1L);
            assertThat(delete).isEqualTo(1);

            delete = i.delete(Wrappers.<Student>lambdaQuery().eq(Student::getId, 2));
            assertThat(delete).isEqualTo(1);
        });

        doTest(i -> {
            Student entity = i.byId(1L);
            assertThat(entity).isNotNull();
            assertThat(Objects.equals(entity.getDeleted(), entity.getId()));

            entity = i.byId(2L);
            assertThat(entity).isNotNull();
            assertThat(Objects.equals(entity.getDeleted(), entity.getId()));
        });

        doTest(mapper -> {
            Student student = new Student();
            student.setName("测试根据实体删除");
            mapper.insert(student);
            assertThat(mapper.deleteById(student)).isEqualTo(1);
        });

        doTest(mapper -> {
            Student student1 = new Student();
            student1.setName("测试根据实体主键批量删除");
            mapper.insert(student1);
            Student student2 = new Student();
            student2.setName("测试根据实体主键批量删除");
            mapper.insert(student2);
            assertThat(mapper.deleteBatchIds(Arrays.asList(student1.getId(), student2.getId()))).isEqualTo(2);
        });

        doTest(mapper -> {
            Student entity1 = new Student();
            entity1.setName("测试根据实体批量删除");
            mapper.insert(entity1);
            Student entity2 = new Student();
            entity2.setName("测试根据实体批量删除");
            mapper.insert(entity2);
            List<Student> entityList = new ArrayList<>();
            entityList.add(entity1);
            entityList.add(entity2);
            assertThat(mapper.deleteBatchIds(entityList)).isEqualTo(2);
            entityList.forEach(entity -> {
                Assertions.assertEquals("haitang", entity.getDeleteBy());
                System.out.println(entity);
            });
        });

        doTest(mapper -> {
            Student entity1 = new Student();
            entity1.setName("测试自定义方法根据实体批量删除");
            mapper.insert(entity1);
            Student entity2 = new Student();
            entity2.setName("测试自定义方法根据实体批量删除");
            mapper.insert(entity2);
            List<Student> entityList = new ArrayList<>();
            entityList.add(entity1);
            entityList.add(entity2);
            assertThat(mapper.testDeleteBatch(entityList)).isEqualTo(2);
            entityList.forEach(entity -> {
                Assertions.assertEquals("haitang", entity.getDeleteBy());
            });
        });

        doTest(mapper -> {
            List<Student> students = mapper.selectAllStudent();
            students.forEach(System.out::println);
        });
    }

    @Override
    protected String tableDataSql() {
        return "insert into student(id,name) values(1,'1'),(2,'2');";
    }
    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists student", "CREATE TABLE IF NOT EXISTS student(" +
            "id BIGINT AUTO_INCREMENT NOT NULL," +
            "name VARCHAR(30) NULL DEFAULT NULL," +
            "delete_by VARCHAR(30) NULL DEFAULT NULL," +
            "deleted BIGINT NOT NULL DEFAULT 0," +
            "PRIMARY KEY (id))");
    }

     @Override
    protected GlobalConfig globalConfig() {
        GlobalConfig globalConfig = super.globalConfig();
        globalConfig.setMetaObjectHandler(new MetaObjectHandler() {

            @Override
            public void insertFill(MetaObject metaObject) {

            }

            @Override
            public void updateFill(MetaObject metaObject) {
                strictUpdateFill(metaObject, "deleteBy", String.class, "haitang");
            }
        });
        globalConfig.setSqlInjector(new DefaultSqlInjector() {
            @Override
            public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
                List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
                methodList.add(new LogicDeleteBatchByIds("testDeleteBatch"));
                return methodList;
            }
        });
        return globalConfig;
    }

}
