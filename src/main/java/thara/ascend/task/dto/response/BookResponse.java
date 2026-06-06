package thara.ascend.task.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter @AllArgsConstructor
public class BookResponse {

    /** DTO for Book Response to prevent use model for Response **/

    private Long id;
    private String title;
    private String author;
    private LocalDate publishedDate;
}
