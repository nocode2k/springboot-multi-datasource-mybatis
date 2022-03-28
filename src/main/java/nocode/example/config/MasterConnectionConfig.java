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
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@MapperScan(value = "nocode.example", annotationClass=MasterConnection.class, sqlSessionFactoryRef="MasterSessionFactory")
public class MasterConnectionConfig {


    @Primary
    @Bean(name = "MasterDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "MasterSessionFactory")
    public SqlSessionFactory masterSessionFactory(@Qualifier("MasterDataSource") DataSource masterDataSource) throws Exception {

        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(masterDataSource);
        sqlSessionFactoryBean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("mybatis-config/master-config.xml")); //mybatis 설정 xml 파일매핑
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("mapper/master/*.xml"));
        sqlSessionFactoryBean.setTypeAliasesPackage("nocode.example.domain"); //benas pakage에 dao나 vo 모아둘 때 구분하기 위해 쓰는 것도 좋음
        return sqlSessionFactoryBean.getObject();
    }

    @Primary
    @Bean(name = "MasterSessionTemplate")
    public SqlSessionTemplate masterSessionTemplate(@Qualifier("MasterSessionFactory") SqlSessionFactory masterSessionFactory) {
        return new SqlSessionTemplate(masterSessionFactory);
    }

    @Primary
    @Bean(name = "MasterTransactionManager")
    public DataSourceTransactionManager PrimaryTransactionManager(@Qualifier("MasterDataSource") DataSource masterDataSource) {
        return new DataSourceTransactionManager(masterDataSource);
    }
}
