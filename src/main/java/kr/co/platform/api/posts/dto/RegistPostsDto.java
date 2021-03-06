package kr.co.platform.api.posts.dto;

import kr.co.platform.api.posts.domain.entity.Posts;
import kr.co.platform.util.auth.AuthUtil;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RegistPostsDto {

	@NotBlank(message = "'title' is a required input value")
	private String title;

	@NotBlank(message = "'content' is a required input value")
	private String content;

	public Posts toEntity() {
		Posts build = Posts.builder()
				.author(AuthUtil.getNickname())
				.title(title)
				.content(content)
				.build();
		return build;
	}

}