package jpabook.mgpractice.mapper;

import jpabook.mgpractice.domain.TransactionHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransactionHistoryMapper {
    void insertHistory(TransactionHistory transactionHistory);
}
