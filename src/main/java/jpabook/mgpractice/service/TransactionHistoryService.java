package jpabook.mgpractice.service;

import jpabook.mgpractice.domain.TransactionHistory;
import jpabook.mgpractice.mapper.TransactionHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionHistoryService {

    private final TransactionHistoryMapper transactionHistoryMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertHistory(Long fromAccountId, Long toAccountId, Long amount, String status) {
        TransactionHistory history = TransactionHistory.builder()
                .fromAccountId(fromAccountId)
                .toAccountId(toAccountId)
                .amount(amount)
                .status(status)
                .build();

        transactionHistoryMapper.insertHistory(history);
    }

}
