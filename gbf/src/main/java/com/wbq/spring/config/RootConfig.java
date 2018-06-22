package com.wbq.spring.config;

import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import redis.clients.jedis.JedisPoolConfig;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author 吴璧钦
 * @date 2018/6/22 12:52
 * @Description
 **/
@Configuration
@ComponentScan(value = "com.*", includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION,
        value = {Service.class,Repository.class})})
//事务驱动
@EnableTransactionManagement
/*@MapperScan("com.wbq.spring.dao")*/
public class RootConfig implements TransactionManagementConfigurer {

    private DataSource dataSource;

    @Bean(name = "redisTemplate")
    public RedisTemplate initRedisTemplate(){
        JedisPoolConfig jedisPoolConfig=new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(50);
        jedisPoolConfig.setMaxTotal(100);
        jedisPoolConfig.setMaxWaitMillis(20000);

        JdkSerializationRedisSerializer jdkS= new JdkSerializationRedisSerializer();
        StringRedisSerializer sS=new StringRedisSerializer();

        RedisTemplate redisTemplate=new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setDefaultSerializer(sS);
        redisTemplate.setKeySerializer(sS);
        redisTemplate.setValueSerializer(sS);
        redisTemplate.setHashKeySerializer(sS);
        redisTemplate.setHashValueSerializer(sS);
        return redisTemplate;

    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(){
        JedisConnectionFactory jedisConnectionFactory=new JedisConnectionFactory();
        jedisConnectionFactory.setHostName("localhost");
        jedisConnectionFactory.setPort(6379);
        return jedisConnectionFactory;
    }

    /**
     * 配置数据库
     *
     * @return 数据库连接池
     */
    @Bean(name = "dataSource")
    public DataSource initDataSource() {
        if (dataSource != null) {
            return dataSource;
        }
        Properties properties = new Properties();
        properties.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        properties.setProperty("url",
                "jdbc:mysql://localhost:3306/school?serverTimezone=Hongkong&useUnicode=true&characterEncoding=utf-8");
        properties.setProperty("username", "root");
        properties.setProperty("password", "123456");
        properties.setProperty("maxActive", "200");
        properties.setProperty("maxIdle", "20");
        properties.setProperty("maxWait", "30000");

        try {
            dataSource = BasicDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataSource;
    }

    /**
     * 配置sqlSessionFactory
     *
     * @return sqlSessionFactoryBean
     */
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactoryBean initSqlSessionFactory() {
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(initDataSource());
        //配置文件
        Resource resource = new ClassPathResource("mybatis/mybatis-config.xml");
        sqlSessionFactory.setConfigLocation(resource);
        return sqlSessionFactory;
    }

    /**
     * 自动扫描mapper接口
     *
     * @return mapper扫描器
     */
    @Bean
    public MapperScannerConfigurer initMapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage("com.wbq.spring.dao");
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        mapperScannerConfigurer.setAnnotationClass(Repository.class);
        return mapperScannerConfigurer;
    }

    /**
     * 实现接口方法，注解注册事务，当使用@Transactional 时产生数据库事务
     *
     * @return
     */
    @Override
    @Bean(name = "annotationDrivenTransactionManager")
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(initDataSource());
        return transactionManager;
    }
}
