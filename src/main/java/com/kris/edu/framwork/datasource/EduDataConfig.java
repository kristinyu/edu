package com.kris.edu.framwork.datasource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.kris.edu.app.*.dao",sqlSessionFactoryRef = "krisSqlSessionFactory")
public class EduDataConfig {

    @Bean(name = "krisDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource krisDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "krisSqlSessionFactory")
    @Primary
    public SqlSessionFactory krisSqlSessionFactory(@Qualifier("krisDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mybatis/*/*Mapper.xml"));
        return bean.getObject();
    }

    @Bean(name = "krisTransactionManager")
    public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("krisDataSource")DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "krisSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate krisSqlSessionTemplate(@Qualifier("krisSqlSessionFactory")SqlSessionFactory sqlSessionFactory ){
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
