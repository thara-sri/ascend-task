package thara.ascend.task.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thara.ascend.task.dto.request.BookRequest;
import thara.ascend.task.dto.response.BookResponse;
import thara.ascend.task.model.Book;
import thara.ascend.task.repository.BookRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /** method for mapping book and book response **/
    private BookResponse mapToResponse(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getPublishedDate());
    }

    @Transactional
    public BookResponse saveBook(BookRequest request) {

        LocalDate date = null;

        if (request.getPublishedDate() != null && !request.getPublishedDate().trim().isEmpty()) {
            String[] dateParts = request.getPublishedDate().split("-");
            int buddhistYear = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]);
            int day = Integer.parseInt(dateParts[2]);

            int currentYear = LocalDate.now().getYear() + 543;
            if (buddhistYear < 1000 || buddhistYear > currentYear) {
                throw new IllegalArgumentException("published date must be valid.");
            }

            int christYear = buddhistYear - 543;
            date = LocalDate.of(christYear, month, day);
        }

        Book savedBook = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .publishedDate(date)
                .build();

        bookRepository.save(savedBook);

        return mapToResponse(savedBook);
    }

    public List<BookResponse> getBooksByAuthor(String author) {
        List<Book> books = bookRepository.findByAuthor(author);
        return books.stream()
                .map(this::mapToResponse)
                .toList();
    }

}
