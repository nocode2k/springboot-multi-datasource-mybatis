package nocode.example.batch;

import lombok.RequiredArgsConstructor;
import nocode.example.domain.Member;
import nocode.example.mapper.master.MasterMemberMapper;
import nocode.example.mapper.slave.SlaveMemberMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.util.HashMap;
import java.util.Map;

@EnableBatchProcessing
@Configuration
@RequiredArgsConstructor
public class CursorReaderBatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public MyBatisCursorItemReader<Member> reader(@Qualifier("SlaveSessionFactory") SqlSessionFactory slaveSessionFactory) {
        Map<String, Object> param = new HashMap<>();
        param.put("id", 1);
        MyBatisCursorItemReader<Member> reader = new MyBatisCursorItemReader<>();
        reader.setQueryId(SlaveMemberMapper.class.getName() + ".findById");
        reader.setSqlSessionFactory(slaveSessionFactory);
        reader.setParameterValues(param);
        return reader;
    }

    @Bean
    public MyBatisBatchItemWriter<Member> writer(@Qualifier("MasterSessionFactory") SqlSessionFactory masterSessionFactory) {
        MyBatisBatchItemWriter<Member> writer = new MyBatisBatchItemWriter<>();
        writer.setSqlSessionFactory(masterSessionFactory);
        writer.setStatementId(MasterMemberMapper.class.getName() + ".save");
        return writer;
    }

    @Bean
    public FlatFileItemWriter<Member> fileWriter() {
        final FlatFileItemWriter<Member> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource("out/member.txt"));
        writer.setHeaderCallback(headerWriter -> headerWriter.append("ID")
                .append(",")
                .append("NAME"));
        writer.setLineAggregator(item -> item.getId() +
                "," +
                item.getName());
        return writer;
    }

    @Bean
    public Step step1(ItemReader<Member> reader, ItemProcessor<Member, Member> processor, ItemWriter<Member> fileWriter) {
        return stepBuilderFactory.get("step1")
                .<Member, Member>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(fileWriter)
                .build();
    }

    @Bean
    public Job upperCaseLastName(Step step1) {
        return jobBuilderFactory.get("upperCaseLastName")
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .end()
                .build();
    }
}
