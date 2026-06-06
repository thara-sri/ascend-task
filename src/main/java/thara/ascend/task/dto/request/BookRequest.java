package thara.ascend.task.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter @RequiredArgsConstructor
public class BookRequest {

    /** DTO for Book Request **/

    @NotBlank(message = "title is needed")
    private String title;

    @NotBlank(message = "author is needed")
    private String author;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "date must be in YYYY-MM-DD format (buddhist year)")
    private String publishedDate;

}
