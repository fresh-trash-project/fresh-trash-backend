package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.entity.TransactionLog;
import freshtrash.freshtrashbackend.repository.TransactionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionLogRepository transactionLogRepository;

    public void saveTransactionLog(Long wasteId, Long sellerId, Long buyerId) {
        transactionLogRepository.save(TransactionLog.builder()
                .wasteId(wasteId)
                .sellerId(sellerId)
                .buyerId(buyerId)
                .build());
    }
}
