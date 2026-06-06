package thara.ascend.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import thara.ascend.task.model.Book;
import thara.ascend.task.repository.BookRepository;

import java.time.LocalDate;

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
    public void getBooksByAuthor_Success_NoBooksFound() throws Exception {
        mockMvc.perform(get("/api/books")
                        .param("author", "Unknown Author")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


}
