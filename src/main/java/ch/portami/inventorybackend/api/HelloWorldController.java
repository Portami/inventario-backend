package ch.portami.inventorybackend.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @GetMapping("/test")
    public HelloResponse helloWorld() {
        return new HelloResponse("Hello World!");
    }

    static class HelloResponse {
        private String message;

        public HelloResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
