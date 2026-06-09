package thara.ascend.task.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "book", indexes = {
        @Index(name = "idx_book_author", columnList = "author")
})
/**
 Database index created for the author column.
 This index optimizes the GET /books?author={authorName} query performance.
 It's preventing full table scans and ensuring fast data retrieval even with a large number of book records.
**/
@Getter @NoArgsConstructor @AllArgsConstructor @Builder
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


