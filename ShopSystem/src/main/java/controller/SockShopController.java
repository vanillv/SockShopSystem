package controller;
import model.SockDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.SockService;

@Controller

@RequestMapping("/api/socks")
public class SockShopController {
    @Autowired
    private SockService sockService;
    Logger log = LoggerFactory.getLogger(SockShopController.class);
    @PostMapping("/income")
    public ResponseEntity<?> registerIncome(@RequestParam String color,
                                            @RequestParam Integer cottonPercentage,
                                            @RequestParam Integer quantity) {
        log.info("controller: registering income: color={}, cottonPercentage={}, quantity={}", color, cottonPercentage, quantity);
        SockDto increasedSock = sockService.registerIncome(color, cottonPercentage, quantity);
        log.debug("controller: income registered successfully: {}", increasedSock);
        return ResponseEntity.ok(increasedSock);
    }

    @PostMapping("/outcome")
    public ResponseEntity<?> registerOutcome(@RequestParam String color,
                                             @RequestParam Integer cottonPercentage,
                                             @RequestParam Integer quantity) {
        log.info("controller: registering outcome: color={}, cottonPercentage={}, quantity={}", color, cottonPercentage, quantity);
        SockDto decreasedSock = sockService.registerOutcome(color, cottonPercentage, quantity);
        log.debug("controller: outcome registered successfully: {}", decreasedSock);
        return ResponseEntity.ok(decreasedSock);
    }

    @GetMapping
    public ResponseEntity<?> getFilteredSockCount(@RequestParam(defaultValue = "all") String color,
                                                  @RequestParam String comparisonOperator,
                                                  @RequestParam Integer cottonPercentage) {
        log.info("controller: retrieving filtered sock count: color={}, operator={}, cottonPercentage={}", color, comparisonOperator, cottonPercentage);
        int count = sockService.getFilteredSockCount(color, comparisonOperator, cottonPercentage);
        log.debug("controller: filtered sock count returned: {}", count);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSock(@PathVariable Long id,
                                        @RequestParam String color,
                                        @RequestParam int cottonPercentage,
                                        @RequestParam int quantity) {
        log.info("controller: updating sock: id={}", id);
        SockDto updatedSock = sockService.updateSock(id, color, cottonPercentage, quantity);
        log.debug("controller: sock updated successfully: {}", updatedSock);
        return ResponseEntity.ok(updatedSock);
    }
    @PostMapping("/batch")
    public ResponseEntity<String> uploadSockBatch(@RequestParam("file") MultipartFile file) {
        log.info("controller: uploading sock batch file: {}", file.getOriginalFilename());
        if (file.isEmpty()) {
            log.warn("controller: uploaded file is empty");
            return ResponseEntity.badRequest().body("File must not be empty");
        }
        sockService.uploadSocksBatch(file);
        log.info("controller: Sock batch upload processed successfully");
        return ResponseEntity.ok("Socks batch uploaded successfully");
    }
}
