package com.blueharvest.bank.services;

import com.blueharvest.bank.config.exceptions.CreditNotCoveredException;
import com.blueharvest.bank.dto.TransactionDto;
import com.blueharvest.bank.dto.TransactionType;
import com.blueharvest.bank.entities.Customer;
import com.blueharvest.bank.entities.SubAccount;
import com.blueharvest.bank.entities.Transaction;
import com.blueharvest.bank.repositories.CustomerRepository;
import com.blueharvest.bank.repositories.SubAccountRepository;
import com.blueharvest.bank.repositories.TransactionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final SubAccountRepository subAccountRepository;

    public TransactionService(TransactionRepository transactionRepository,CustomerRepository customerRepository ,SubAccountRepository subAccountRepository) {
        this.transactionRepository = transactionRepository;
        this.customerRepository= customerRepository;
        this.subAccountRepository=subAccountRepository;
    }

    @Transactional
    public TransactionDto addTransaction(TransactionDto transactionDto) {
        ModelMapper modelMapper= new ModelMapper();
        Transaction transaction= modelMapper.map(transactionDto,Transaction.class);
        Optional.ofNullable(transaction)
                .filter(transaction1 -> transaction1.getFrom().getBalance()>transaction1.getAmount())
                .orElseThrow(CreditNotCoveredException::new);
        double parentBalance=calculateBalanceBasedOnTransactionType(transaction.getTransactionType(), transaction.getFrom().getBalance(), transaction.getAmount());
        Customer parent= customerRepository.updateAmountById(transaction.getFrom().getId(),parentBalance);
        transaction.setFrom(parent);

        double childBalance= calculateBalanceBasedOnTransactionType(transaction.getTransactionType(), transaction.getTo().getBalance(),transaction.getAmount());

        SubAccount child=subAccountRepository.updateBalanceById(transaction.getTo().getId(),childBalance);
        transaction.setTo(child);

        Transaction savedTransaction=transactionRepository.save(transaction);
        return modelMapper.map(savedTransaction,TransactionDto.class);
    }

    public List<TransactionDto> retrieveTransactions(long fromId){

        return transactionRepository.findAllByFrom(fromId).stream()
                .map(transaction -> new ModelMapper().map(transaction,TransactionDto.class))
                .collect(Collectors.toList());
    }

    private double calculateBalanceBasedOnTransactionType(TransactionType transactionType, double balance , double amount) {
        return Optional.ofNullable(transactionType)
                .filter(transactionType1 -> transactionType1.equals(TransactionType.DEPOSIT)).map(transactionType1 -> balance+amount)
                .orElseGet(()-> balance-amount);
    }
}