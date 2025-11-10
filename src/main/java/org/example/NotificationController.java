package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/subscriptions/{email}")
    public ResponseEntity<?> subscribeEmail(@PathVariable("email") String email) {
        return notificationService.subscribeEmail(email);
    }

    @DeleteMapping("/subscriptions/{email}")
    public ResponseEntity<?> unsubscribeEmail(@PathVariable("email") String email) {
        return notificationService.unsubscribeEmail(email);
    }
}
