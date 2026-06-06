package thara.ascend.task.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "book", indexes = {
        @Index(name = "idx_book_author", columnList = "author")
})
@Getter @NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "title is needed")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "author is needed")
    @Column(nullable = false)
    private String author;

    @Column(name = "publishedDate")
    private LocalDate publishedDate;

}


