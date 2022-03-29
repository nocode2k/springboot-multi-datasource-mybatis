package nocode.example.service;

import nocode.example.domain.Member;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class MemberProcess implements ItemProcessor<Member, Member> {

    private ExecutionContext executionContext;

    @Override
    public Member process(Member member) throws Exception {
        incrementRowCount();

        Member transformed = new Member();
        transformed.setId(member.getId());
        transformed.setName(member.getName().toUpperCase());
        return transformed;
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        executionContext = stepExecution.getExecutionContext();
    }

    @BeforeChunk
    public void beforeChunk(ChunkContext chunkContext) {
        incrementChunkCount();
    }

    private void incrementRowCount() {
        executionContext.putInt("row_count",
                executionContext.getInt("row_count", 0) + 1);
    }

    private void incrementChunkCount() {
        executionContext.putInt("chunk_count",
                executionContext.getInt("chunk_count", 0) + 1);
    }
}
