package thara.ascend.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import thara.ascend.task.model.Book;
import thara.ascend.task.repository.BookRepository;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BookIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     Sets up the database before each test execution.
     Clears all existing data and inserts mock data to ensure a testing environment.
    **/
    @BeforeEach
    public void setupDatabase(TestInfo testInfo) {

        System.out.println("=========================================================");
        System.out.println("RUNNING TEST: " + testInfo.getDisplayName());
        System.out.println();

        bookRepository.deleteAll();
        bookRepository.save(Book.builder()
                .title("Java 101")
                .author("John Doe")
                .publishedDate(LocalDate.of(2020, 1, 1))
                .build());
        bookRepository.save(Book.builder()
                .title("Spring Boot Guide")
                .author("John Doe")
                .publishedDate(LocalDate.of(2021, 5, 15))
                .build());
        bookRepository.save(Book.builder()
                .title("Clean Code")
                .author("Robert C. Martin")
                .publishedDate(LocalDate.of(2008, 8, 1))
                .build());
    }

    /**
     Tests the successful creation of a book.
     Verify the API returns a 201 status and correctly converts the Buddhist date into a valid Christ date.
    **/
    @Test
    public void createBook_Success_WithPublishedValidDate() throws Exception {
        String payload = """
            {
                "title": "Clean Architecture",
                "author": "Robert C. Martin",
                "publishedDate": "2560-10-20"
            }
            """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Clean Architecture"))
                .andExpect(jsonPath("$.author").value("Robert C. Martin"))
                .andExpect(jsonPath("$.publishedDate").value("2017-10-20"));
    }

    /**
     Tests the successful creation of a book when the published date is null.
     Verify the API allows a null value for the published date and still successfully creates the record.
    **/
    @Test
    public void createBook_Success_WithNullPublishedDate() throws Exception {
        String payload = """
            {
                "title": "Refactoring",
                "author": "Martin Fowler"
            }
            """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Refactoring"))
                .andExpect(jsonPath("$.author").value("Martin Fowler"))
                .andExpect(jsonPath("$.publishedDate").isEmpty());
    }

    /**
     Tests the validation for an invalid published date.
     Verify the API deny the request and returns a 400 Status if the provided year is out of the valid range.
    **/
    @Test
    public void createBook_Fail_WithInvalidDate() throws Exception {
        String payload = """
            {
                "title": "Ancient Book",
                "author": "Unknown",
                "publishedDate": "0999-01-01"
            }
            """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    /**
     Tests the validation for missing needed fields.
     Verify the API returns a 400 status if either the title or author fields are null.
    **/
    @Test
    public void createBook_Fail_WithEmptyTitleOrAuthor() throws Exception {
        String payload = """
            {
                "title": "",
                "author": ""
            }
            """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    /**
     Tests retrieving books by a specific author.
     Verify the API returns a 200 status and correctly fetches all books associated with the given author's name.
    **/
    @Test
    public void getBooksByAuthor_Success_FoundBooks() throws Exception {
        mockMvc.perform(get("/api/books")
                        .param("author", "John Doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].author").value("John Doe"))
                .andExpect(jsonPath("$[1].author").value("John Doe"));
    }

    @Test
    public void verify_BookAuthorIndex_Exists() {
        String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS " +
                "WHERE TABLE_SCHEMA = DATABASE() " +
                "AND TABLE_NAME = 'book' " +
                "AND INDEX_NAME = 'idx_book_author'";

        Integer indexCount = jdbcTemplate.queryForObject(sql, Integer.class);
        assertThat(indexCount).isEqualTo(1);
    }


    /**
     Tests retrieving books for an author who does not exist in the database.
     Verifies that the API handles by returning a 200 status with an empty array.
    **/
    @Test
    public void getBooksByAuthor_Success_NoBooksFound() throws Exception {
        mockMvc.perform(get("/api/books")
                        .param("author", "Unknown Author")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


}
