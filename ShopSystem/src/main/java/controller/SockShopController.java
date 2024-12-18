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

import java.util.List;

@Controller

@RequestMapping("/api/socks")
public class SockShopController {
    @Autowired
    private SockService sockService;
    Logger log = LoggerFactory.getLogger(SockShopController.class);
    @PostMapping("/income")
    public ResponseEntity<SockDto> registerIncome(@RequestParam String color,
                                                  @RequestParam Integer cottonPercentage,
                                                  @RequestParam Integer quantity) {
        SockDto increasedSock = sockService.registerIncome(color, cottonPercentage, quantity);
        return ResponseEntity.ok(increasedSock);
    }
    @PostMapping("/outcome")
    public ResponseEntity<SockDto> registerOutcome(@RequestParam String color,
                                                   @RequestParam Integer cottonPercentage,
                                                   Integer quantity) {
        SockDto decreasedSock = sockService.registerOutcome(color, cottonPercentage, quantity);
        return ResponseEntity.ok(decreasedSock);
    }
    @GetMapping
    public ResponseEntity<Integer> getFilteredSockCount(@RequestParam(defaultValue = "all") String color,
                                                        @RequestParam String comparisonOperator,
                                                        @RequestParam Integer cottonPercentage) {
        int count = sockService.getFilteredSockCount(color, comparisonOperator, cottonPercentage);
        return ResponseEntity.ok(count);
    }
    @PutMapping("/{id}")
    public ResponseEntity<SockDto> updateSock(
            @PathVariable Long id,
            @RequestParam String color,
            @RequestParam int cottonPercentage,
            @RequestParam int quantity) {
        SockDto updatedSock = sockService.updateSock(id, color, cottonPercentage, quantity);
        return ResponseEntity.ok(updatedSock);
    }
    @PostMapping("/batch")
    public ResponseEntity<String> uploadSockBatch(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok("Batch upload processed successfully.");
    }
    @PostMapping("/batch")
    public ResponseEntity<String> uploadSocksBatch(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File must not be empty");
        }
        try {
            sockService.uploadSocksBatch(file);
            return ResponseEntity.ok("Socks batch uploaded successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body("An error occurred while processing the file");
        }
    }
}
