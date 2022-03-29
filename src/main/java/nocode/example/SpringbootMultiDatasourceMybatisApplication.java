package nocode.example;

import lombok.RequiredArgsConstructor;
import nocode.example.service.MemberService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class SpringbootMultiDatasourceMybatisApplication implements CommandLineRunner {

	private final MemberService memberService;

	public static void main(String[] args) {
		SpringApplication.run(SpringbootMultiDatasourceMybatisApplication.class, args);
	}

	@Override
	public void run(String... args) {
		//memberService.saveMember();
		memberService.viewMember();
	}
}
