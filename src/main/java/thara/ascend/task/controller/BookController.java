package thara.ascend.task.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thara.ascend.task.dto.request.BookRequest;
import thara.ascend.task.dto.response.BookResponse;
import thara.ascend.task.service.BookService;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@AllArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest request) {
        BookResponse response = bookService.saveBook(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> getBooksByAuthor(@RequestParam("author") String author) {
        List<BookResponse> responses = bookService.getBooksByAuthor(author);
        return ResponseEntity.ok(responses);
    }

}
