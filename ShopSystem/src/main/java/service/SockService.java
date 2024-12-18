package service;

import entity.SockEntity;
import exception.InsufficientSockStockException;
import model.SockDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;
import repository.SockRepository;
import util.ExcelParser;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public class SockService {
    private final SockRepository sockRepository;

    public SockService(SockRepository sockRepository) {
        this.sockRepository = sockRepository;
    }
    private Logger log = LoggerFactory.getLogger(SockService.class);
    @Transactional
    public SockDto registerIncome(String color, int cottonPercentage, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        Optional<SockEntity> existingSock = sockRepository.findByColorAndCottonPercentage(color, cottonPercentage);
        SockEntity sock;
        if (existingSock.isPresent()) {
            sock = existingSock.get();
            addQuantity(sock, quantity);
        } else {
            sock = new SockEntity(color, cottonPercentage, quantity);
        }
        sock = sockRepository.save(sock);
        return mapToDto(sock);
    }
    @Transactional
    public SockDto registerOutcome(String color, int cottonPercentage, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero");
        Optional<SockEntity> existingSock = sockRepository.findByColorAndCottonPercentage(color, cottonPercentage);
        if (existingSock.isEmpty() || existingSock.get().getQuantity() < quantity) {
            throw new InsufficientSockStockException("Not enough socks available in stock.");
        }
        SockEntity sock = existingSock.get();
        decreaseQuantity(sock, quantity);
        sock = sockRepository.save(sock);
        return mapToDto(sock);
    }
    @Transactional
    public int getFilteredSockCount(String color, String operator, int cottonPercentage) {
        validateOperator(operator);
        List<SockEntity> filteredSocks = sockRepository.findByFilters(color, operator, cottonPercentage);
        return filteredSocks.stream().mapToInt(SockEntity::getQuantity).sum();
    }
    @Transactional
    public SockDto updateSock(Long id, String color, int cottonPercentage, int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");
        if (cottonPercentage < 0 || cottonPercentage > 100) {
            throw new IllegalArgumentException("Cotton percentage must be between 0 and 100");
        }
        SockEntity sock = sockRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sock with id " + id + " not found"));
        sock.setColor(color);
        sock.setCottonPercentage(cottonPercentage);
        sock.setQuantity(quantity);
        sock = sockRepository.save(sock);
        return mapToDto(sock);
    }
    @Transactional
    public void uploadSocksBatch(MultipartFile file) {
        List<SockDto> socks = ExcelParser.parseExcelFile(file);
        for (SockDto sockDto : socks) {
            SockEntity sockEntity = sockRepository.findByColorAndCottonPercentage(sockDto.getColor(), sockDto.getCottonPercentage())
                    .orElse(new SockEntity(sockDto.getColor(), sockDto.getCottonPercentage(), 0));

            sockEntity.setQuantity(sockEntity.getQuantity() + sockDto.getQuantity());
            sockRepository.save(sockEntity);
        }
    }
    private void validateOperator(String operator) {
        if (!operator.equals("equal") && !operator.equals("moreThan") && !operator.equals("lessThan")) {
            throw new IllegalArgumentException("Invalid operator. Must be one of: equal, moreThan, lessThan.");
        }
    }
    private void addQuantity(SockEntity entity, int quantity) {
        entity.setQuantity(entity.getQuantity() + quantity);
    }
    private void decreaseQuantity(SockEntity entity, int quantity) {
        entity.setQuantity(entity.getQuantity() - quantity);
    }
    private SockDto mapToDto(SockEntity sockEntity) {
        return new SockDto(sockEntity.getColor(), sockEntity.getCottonPercentage(), sockEntity.getQuantity());
    }

}
