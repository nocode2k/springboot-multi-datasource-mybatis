package nocode.example.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(value = "nocode.example", annotationClass=SlaveConnection.class, sqlSessionFactoryRef="SlaveSessionFactory")
public class SlaveConnectionConfig {

    @Bean(name = "SlaveDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.slave")
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "SlaveSessionFactory")
    public SqlSessionFactory slaveSessionFactory(@Qualifier("SlaveDataSource") DataSource slaveDataSource) throws Exception {

        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(slaveDataSource);
        sqlSessionFactoryBean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("mybatis-config/slave-config.xml")); //mybatis 설정 xml 파일매핑
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("mapper/slave/*.xml"));
        sqlSessionFactoryBean.setTypeAliasesPackage("nocode.example.domain"); //benas pakage에 dao나 vo 모아둘 때 구분하기 위해 쓰는 것도 좋음
        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "SlaveSessionTemplate")
    public SqlSessionTemplate slaveSessionTemplate(@Qualifier("SlaveSessionFactory") SqlSessionFactory slaveSessionFactory) {
        return new SqlSessionTemplate(slaveSessionFactory);
    }

    @Bean(name = "SlaveTransactionManager")
    public DataSourceTransactionManager PrimaryTransactionManager(@Qualifier("SlaveDataSource") DataSource slaveDataSource) {
        return new DataSourceTransactionManager(slaveDataSource);
    }
}
